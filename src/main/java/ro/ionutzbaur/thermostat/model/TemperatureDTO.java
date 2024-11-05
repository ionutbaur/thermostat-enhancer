package ro.ionutzbaur.thermostat.model;

import ro.ionutzbaur.thermostat.model.enums.DegreesScale;

public record TemperatureDTO(Double degrees, DegreesScale scale, boolean isTurnedOn) {
}
