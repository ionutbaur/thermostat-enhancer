package ro.ionutzbaur.thermostat.model.enums;

public enum Brand {

    TADO,
    ZIGBEE;

    public static Brand defaultBrand() {
        return TADO;
    }
}
