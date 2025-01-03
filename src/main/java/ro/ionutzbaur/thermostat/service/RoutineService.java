package ro.ionutzbaur.thermostat.service;

import io.quarkus.runtime.util.StringUtil;
import io.smallrye.mutiny.subscription.Cancellable;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.ionutzbaur.thermostat.exception.RoutineException;
import ro.ionutzbaur.thermostat.model.RoutineDTO;
import ro.ionutzbaur.thermostat.model.RoutineRequest;
import ro.ionutzbaur.thermostat.model.TemperatureDTO;
import ro.ionutzbaur.thermostat.model.TemperatureRequest;
import ro.ionutzbaur.thermostat.model.enums.DegreesScale;
import ro.ionutzbaur.thermostat.util.RequestHelper;
import ro.ionutzbaur.thermostat.util.ThermostatUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static ro.ionutzbaur.thermostat.util.ThermostatUtils.safeDouble;

// TODO: make it scalable to support multiple users - users should be able to manage routines only for their homes
@Singleton
public class RoutineService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoutineService.class);

    private final List<RoutineDTO> routines;
    private final ConcurrentHashMap<String, Cancellable> pollSubscriptionMap;
    private final ConcurrentHashMap<String, TemperatureDTO> routineTemperatureMap;

    private final ThermostatService thermostatService;

    public RoutineService(ThermostatService thermostatService) {
        this.thermostatService = thermostatService;
        this.routines = new ArrayList<>();
        this.pollSubscriptionMap = new ConcurrentHashMap<>();
        this.routineTemperatureMap = new ConcurrentHashMap<>();
    }

    public List<RoutineDTO> getAllRoutines() {
        return routines;
    }

    public RoutineDTO createRoomTemperatureRoutine(RoutineRequest routineRequest) {
        validateRoutineRequest(routineRequest);
        if (routineExists(routineRequest)) {
            throw new RoutineException("Routine already exists! " + routineRequest);
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
            LOGGER.warn("No active poll subscription to stop for routineId: {}", routineId);
        } else {
            LOGGER.info("Stopping poll subscription for routineId: {}", routineId);
            pollSubscription.cancel();
            pollSubscriptionMap.remove(routineId);
            routines.removeIf(routine -> routine.id().equals(routineId));
        }
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
        return ThermostatUtils.startPolling(() -> thermostatService.getRoomInfo(homeId, roomId, scale),
                room -> {
                    TemperatureDTO roomTemperature = room.temperature();
                    if (roomTemperature == null) {
                        return; // should never happen
                    }

                    if (safeDouble(roomTemperature.degrees()) < safeDouble(cutOffTemperature)) {
                        if (roomTemperature.isTurnedOn()) { // reset and ignore if the room is heated
                            routineTemperatureMap.remove(routineId);
                        } else {
                            turnOffHeating(routineId, homeId, roomId);
                        }
                    }
                });
    }

    private void turnOffHeating(String routineId,
                                String homeId,
                                String roomId) {
        TemperatureDTO setTemperature = routineTemperatureMap.get(routineId);
        if (setTemperature == null) { // check if the temperature was not already set
            // TODO: maybe force open window detection instead of setting the temperature to null
            TemperatureRequest temperatureRequest = new TemperatureRequest(homeId, roomId, null, null);
            routineTemperatureMap.computeIfAbsent(routineId, key -> thermostatService.setTemperature(temperatureRequest)); //turn off heating
        } else {
            LOGGER.debug("Heating has already been turned off for routineId: {}", routineId);
        }
    }

}
