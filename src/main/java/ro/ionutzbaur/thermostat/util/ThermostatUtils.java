package ro.ionutzbaur.thermostat.util;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.subscription.Cancellable;
import org.eclipse.microprofile.config.ConfigProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.ionutzbaur.thermostat.exception.ThermostatException;

import java.time.Duration;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ThermostatUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThermostatUtils.class);

    private static final Duration POLLING_DELAY = Duration.ofSeconds(
            ConfigProvider.getConfig()
                    .getOptionalValue("thermostat.polling.delay.seconds", Long.class)
                    .orElse(5L));
    private static final Duration POLLING_EXPIRE_DURATION = Duration.ofMinutes(
            ConfigProvider.getConfig()
                    .getOptionalValue("thermostat.polling.expire.minutes", Long.class)
                    .orElse(30L));

    private ThermostatUtils() {
        // Utility class
    }

    /**
     * Safely converts a {@link String} to a primitive long, throwing a {@link ThermostatException} if the input is not a valid long.
     *
     * @param str               The {@link String} value
     * @param exceptionSupplier A supplier for the exception to be thrown
     * @return The primitive long value
     */
    public static long toLong(String str, Supplier<ThermostatException> exceptionSupplier) {
        try {
            return Long.parseLong(str);
        } catch (NumberFormatException e) {
            throw exceptionSupplier.get();
        }
    }

    /**
     * Safely converts a {@link Double} to a primitive double, returning 0 if the input is null.
     *
     * @param value The {@link Double} value
     * @return The primitive double value
     */
    public static double safeDouble(Double value) {
        return value == null ? 0d : value;
    }

    /**
     * Starts a polling mechanism that will call the supplied service based on the configured polling delay and
     * expiration duration. The intervals are determined by the {@link #POLLING_DELAY} and
     * {@link #POLLING_EXPIRE_DURATION} configurations. The polling will continue indefinitely unless the returned
     * {@link Cancellable} is cancelled, or the polling fails too many times in the configured period and expires.
     *
     * @param serviceSupplier The supplier of the service to be called
     * @param itemConsumer    The consumer of the service result
     * @param <T>             The type of the service result
     * @return A {@link Cancellable} that can be used to stop the polling
     */
    public static <T> Cancellable startPolling(Supplier<T> serviceSupplier, Consumer<T> itemConsumer) {
        return startPolling(serviceSupplier, itemConsumer, POLLING_DELAY, POLLING_DELAY, POLLING_EXPIRE_DURATION);
    }

    /**
     * Starts a polling mechanism that will call the supplied service at a specified interval and consumes the result.
     * The polling will continue indefinitely unless the returned {@link Cancellable} is cancelled,
     * or the polling fails too many times in the given period of time and expires.
     *
     * @param serviceSupplier The supplier of the service to be called
     * @param itemConsumer    The consumer of the service result
     * @param delay           The delay between polling calls
     * @param backOff         The back-off duration between retries
     * @param expireIn        The duration in which the polling will expire
     * @param <T>             The type of the service result
     * @return A {@link Cancellable} that can be used to stop the polling
     */
    public static <T> Cancellable startPolling(Supplier<T> serviceSupplier,
                                               Consumer<T> itemConsumer,
                                               Duration delay,
                                               Duration backOff,
                                               Duration expireIn) {
        return Multi.createBy()
                .repeating()
                .supplier(serviceSupplier)
                .withDelay(delay)
                .indefinitely()
                .onFailure(failure -> {
                    LOGGER.error("Polling failed when calling service supplier!", failure);
                    return true;
                })
                .retry()
                .withBackOff(backOff)
                .expireIn(expireIn.toMillis())
                .onFailure()
                .invoke(() -> LOGGER.error("Polling expired. Too many failures and retries in {} minutes.", expireIn.toMinutes()))
                .subscribe()
                .with(itemConsumer, failure -> LOGGER.error("Polling failed when consuming the item returned by service!", failure));
    }
}
