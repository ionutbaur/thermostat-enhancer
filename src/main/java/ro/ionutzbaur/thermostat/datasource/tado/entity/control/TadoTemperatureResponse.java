package ro.ionutzbaur.thermostat.datasource.tado.entity.control;

import ro.ionutzbaur.thermostat.datasource.tado.entity.control.enums.SettingType;

public class TadoTemperatureResponse {

    private SettingType type;
    private Setting setting;
    private Termination termination;

    public SettingType getType() {
        return type;
    }

    public Setting getSetting() {
        return setting;
    }

    public Termination getTermination() {
        return termination;
    }
}
