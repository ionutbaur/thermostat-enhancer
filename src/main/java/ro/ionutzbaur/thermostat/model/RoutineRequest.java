package ro.ionutzbaur.thermostat.model;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import ro.ionutzbaur.thermostat.model.enums.DegreesScale;
import ro.ionutzbaur.thermostat.model.enums.RoutineType;

public class RoutineRequest {

    private String description;
    private RoutineType routineType;
    private String homeId;
    private String roomId;
    private Double temperature;
    @Schema(defaultValue = "CELSIUS")
    private DegreesScale scale;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RoutineType getRoutineType() {
        return routineType;
    }

    public void setRoutineType(RoutineType routineType) {
        this.routineType = routineType;
    }

    public String getHomeId() {
        return homeId;
    }

    public void setHomeId(String homeId) {
        this.homeId = homeId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public DegreesScale getScale() {
        return scale == null ? DegreesScale.CELSIUS : scale;
    }

    public void setScale(DegreesScale scale) {
        this.scale = scale;
    }

    @Override
    public String toString() {
        return "RoutineRequest{" +
                "description='" + description + '\'' +
                ", routineType=" + routineType +
                ", homeId='" + homeId + '\'' +
                ", roomId='" + roomId + '\'' +
                ", temperature=" + temperature +
                ", scale=" + scale +
                '}';
    }
}
