package ro.ionutzbaur.thermostat.util;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.subscription.Cancellable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.ionutzbaur.thermostat.exception.ThermostatException;

import java.time.Duration;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ThermostatUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThermostatUtils.class);

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
     * Starts a polling mechanism that will call the supplied service every 5 seconds and consumes the result.
     * The polling will continue indefinitely unless the returned {@link Cancellable} is cancelled.
     *
     * @param serviceSupplier The supplier of the service to be called
     * @param itemConsumer    The consumer of the service result
     * @param <T>             The type of the service result
     * @return A {@link Cancellable} that can be used to stop the polling
     */
    public static <T> Cancellable startPolling(Supplier<T> serviceSupplier, Consumer<T> itemConsumer) {
        return Multi.createBy()
                .repeating()
                .supplier(serviceSupplier)
                .withDelay(Duration.ofSeconds(5))
                .indefinitely()
                .subscribe()
                .with(itemConsumer, failure -> LOGGER.error("Polling failed!", failure));
    }
}
