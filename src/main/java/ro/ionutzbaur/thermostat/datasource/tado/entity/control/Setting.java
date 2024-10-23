package ro.ionutzbaur.thermostat.datasource.tado.entity.control;

import ro.ionutzbaur.thermostat.datasource.tado.entity.control.enums.Power;
import ro.ionutzbaur.thermostat.datasource.tado.entity.control.enums.SettingType;

public record Setting(SettingType type, Power power, Temperature temperature) {
}
