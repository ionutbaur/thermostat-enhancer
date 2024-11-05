package ro.ionutzbaur.thermostat.util;

import java.util.function.Supplier;

public class ThermostatUtils {

    private ThermostatUtils() {
        // Utility class
    }

    public static long toLong(String str, Supplier<RuntimeException> exceptionSupplier) {
        try {
            return Long.parseLong(str);
        } catch (NumberFormatException e) {
            throw exceptionSupplier.get();
        }
    }

    public static double safeDouble(Double value) {
        return value == null ? 0d : value;
    }
}
