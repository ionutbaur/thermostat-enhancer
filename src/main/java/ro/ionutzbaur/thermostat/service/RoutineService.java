package ro.ionutzbaur.thermostat.service;

import io.quarkus.runtime.util.StringUtil;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.subscription.Cancellable;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.ionutzbaur.thermostat.model.RoutineDTO;
import ro.ionutzbaur.thermostat.model.RoutineRequest;
import ro.ionutzbaur.thermostat.model.TemperatureRequest;
import ro.ionutzbaur.thermostat.model.enums.DegreesScale;
import ro.ionutzbaur.thermostat.util.RequestHelper;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static ro.ionutzbaur.thermostat.util.ThermostatUtils.safeDouble;

@Singleton
public class RoutineService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoutineService.class);

    private final List<RoutineDTO> routines;
    private final ConcurrentHashMap<String, Cancellable> pollSubscriptionMap;

    private final ThermostatService thermostatService;

    public RoutineService(ThermostatService thermostatService) {
        this.thermostatService = thermostatService;
        this.routines = new ArrayList<>();
        this.pollSubscriptionMap = new ConcurrentHashMap<>();
    }

    public List<RoutineDTO> getAllRoutines() {
        return routines;
    }

    public RoutineDTO createRoomTemperatureRoutine(RoutineRequest routineRequest) {
        validateRoutineRequest(routineRequest);
        if (routineExists(routineRequest)) {
            throw new RuntimeException("Routine already exists!");
        }

        final String homeId = routineRequest.getHomeId();
        final String roomId = routineRequest.getRoomId();

        final String routineId = RequestHelper.getBrand().name().toLowerCase() + "-" + UUID.randomUUID();
        pollSubscriptionMap.computeIfAbsent(routineId,
                key -> roomTemperaturePoller(key, homeId, roomId, routineRequest.getTemperature(), routineRequest.getScale()));
        RoutineDTO routineDTO = new RoutineDTO(routineId, routineRequest.getDescription(),
                routineRequest.getRoutineType(), homeId, roomId, RequestHelper.getBrand());
        routines.add(routineDTO);

        return routineDTO;
    }

    public void removeAllRoutines() {
        new ArrayList<>(routines)
                .forEach(routine -> removeRoutine(routine.id()));
    }

    public void removeRoutine(String routineId) {
        Cancellable pollSubscription = pollSubscriptionMap.get(routineId);
        if (pollSubscription == null) {
            LOGGER.info("No active poll subscription to stop for routineId: {}", routineId);
        } else {
            LOGGER.info("Stopping poll subscription for routineId: {}", routineId);
            pollSubscription.cancel();
            pollSubscriptionMap.remove(routineId);
            routines.removeIf(routine -> routine.id().equals(routineId));
        }
    }

    private <T> Cancellable startPolling(Supplier<T> serviceSupplier, Consumer<T> itemConsumer) {
        return Multi.createBy()
                .repeating()
                .supplier(serviceSupplier)
                .withDelay(Duration.ofSeconds(5))
                .indefinitely()
                .subscribe()
                .with(itemConsumer, failure -> LOGGER.error("Polling failed!", failure));
    }

    private void validateRoutineRequest(RoutineRequest routineRequest) {
        if (routineRequest == null) {
            throw new IllegalArgumentException("Routine request cannot be null!");
        }

        if (StringUtil.isNullOrEmpty(routineRequest.getDescription())) {
            throw new IllegalArgumentException("Please provide a description for the routine!");
        }

        if (routineRequest.getRoutineType() == null) {
            throw new IllegalArgumentException("Routine type cannot be null!");
        }

        if (StringUtil.isNullOrEmpty(routineRequest.getHomeId())) {
            throw new IllegalArgumentException("Please provide the homeId!");
        }

        if (StringUtil.isNullOrEmpty(routineRequest.getRoomId())) {
            throw new IllegalArgumentException("Please provide the roomId!");
        }

        if (routineRequest.getTemperature() < 0) {
            throw new IllegalArgumentException("Temperature cannot be negative!");
        }
    }

    private boolean routineExists(RoutineRequest routineRequest) {
        return routines.stream()
                .anyMatch(routine -> routine.routineType() == routineRequest.getRoutineType()
                        && routine.homeId().equals(routineRequest.getHomeId())
                        && routine.roomId().equals(routineRequest.getRoomId())
                        && routine.brand() == RequestHelper.getBrand());
    }

    private Cancellable roomTemperaturePoller(String routineId,
                                              String homeId,
                                              String roomId,
                                              Double cutOffTemperature,
                                              DegreesScale scale) {
        return startPolling(() -> thermostatService.getRoomInfo(homeId, roomId, scale),
                room -> {
                    LOGGER.info("Room temperature is null?: {}", room.temperature() == null);
                    if (room.temperature() != null && room.temperature().isTurnedOn()) {
                        LOGGER.info("Room temperature is: {}", room.temperature().degrees());
                        if (room.temperature().degrees() < safeDouble(cutOffTemperature) && !isRoutineActive(routineId)) {
                            LOGGER.info("Executing action because room temp is lower than: {} and routine {} is not active", cutOffTemperature, routineId);
                            TemperatureRequest temperatureRequest = new TemperatureRequest(homeId, roomId, null, null);

                            thermostatService.setTemperature(temperatureRequest);
                        }
                    }
                });
    }

    private boolean isRoutineActive(String routineId) {
        return pollSubscriptionMap.containsKey(routineId);
    }
}
