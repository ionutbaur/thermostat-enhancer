package ro.ionutzbaur.thermostat.poller;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.subscription.Cancellable;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.ionutzbaur.thermostat.model.enums.DegreesScale;
import ro.ionutzbaur.thermostat.service.ThermostatService;

import java.time.Duration;

@Singleton
public class TemperaturePoller {

    private static final Logger LOGGER = LoggerFactory.getLogger(TemperaturePoller.class);

    private Cancellable pollSubscription;

    private final ThermostatService thermostatService;

    public TemperaturePoller(ThermostatService thermostatService) {
        this.thermostatService = thermostatService;
    }

    public void startPolling(String homeId, String roomId, double temperature, DegreesScale scale) {
        if (pollSubscription == null) {
            pollSubscription = Multi.createBy()
                    .repeating()
                    .supplier(() -> thermostatService.getRoomInfo(homeId, roomId, scale))
                    .withDelay(Duration.ofSeconds(5))
                    .indefinitely()
                    //.runSubscriptionOn(Infrastructure.getDefaultWorkerPool())
                    .subscribe()
                    .with(room -> {
                        if (room.temperature() != null) {
                            LOGGER.info("Room temperature is: {}", room.temperature().degrees());
                            if (room.temperature().degrees() < temperature) {
                                LOGGER.info("Executing action because room temp is lower than: {}", temperature);
                            }
                        }
                    }, failure -> LOGGER.error("Polling failed: {}", failure.getMessage()));
        } else {
            pollSubscription.cancel();
        }
    }

    public void stopPolling() {
        if (pollSubscription == null) {
            LOGGER.info("No active poll subscription to stop");
        } else {
            pollSubscription.cancel();
            pollSubscription = null;
        }
    }

}
