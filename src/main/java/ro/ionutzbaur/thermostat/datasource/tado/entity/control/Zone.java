package ro.ionutzbaur.thermostat.datasource.tado.entity.control;

import ro.ionutzbaur.thermostat.datasource.tado.entity.control.enums.SettingType;

public class Zone {

    private Long id;
    private String name;
    private SettingType type;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public SettingType getType() {
        return type;
    }
}
