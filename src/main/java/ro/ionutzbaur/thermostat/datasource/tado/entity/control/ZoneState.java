package ro.ionutzbaur.thermostat.datasource.tado.entity.control;

public class ZoneState {

    private Long zoneId;
    private SensorDataPoints sensorDataPoints;

    public Long getZoneId() {
        return zoneId;
    }

    public void setZoneId(Long zoneId) {
        this.zoneId = zoneId;
    }

    public SensorDataPoints getSensorDataPoints() {
        return sensorDataPoints;
    }
}
