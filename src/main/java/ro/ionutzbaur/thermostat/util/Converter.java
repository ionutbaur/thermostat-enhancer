package ro.ionutzbaur.thermostat.util;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class Converter {

    private Converter() {
        // Utility class
    }

    public static long toLong(String str, Supplier<RuntimeException> exceptionSupplier) {
        try {
            return Long.parseLong(str);
        } catch (NumberFormatException e) {
            //throw new RuntimeException("Tado zoneId must be a number! Provided: " + roomId, e);
            throw exceptionSupplier.get();
        }
    }
}
