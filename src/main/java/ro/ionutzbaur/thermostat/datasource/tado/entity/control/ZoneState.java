package ro.ionutzbaur.thermostat.datasource.tado.entity.control;

public class ZoneState {

    private Long zoneId;
    private Setting setting;

    public Long getZoneId() {
        return zoneId;
    }

    public void setZoneId(Long zoneId) {
        this.zoneId = zoneId;
    }

    public Setting getSetting() {
        return setting;
    }
}
