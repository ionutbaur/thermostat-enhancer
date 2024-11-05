package ro.ionutzbaur.thermostat.model;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import ro.ionutzbaur.thermostat.model.enums.DegreesScale;

public record TemperatureRequest(String homeId,
                                 String roomId,
                                 Double degrees,
                                 @Schema(defaultValue = "CELSIUS")
                                 DegreesScale scale) {

    @Override
    public DegreesScale scale() {
        return scale == null ? DegreesScale.CELSIUS : scale;
    }
}
