package ro.ionutzbaur.service.impl;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import ro.ionutzbaur.thermostat.datasource.tado.entity.control.TemperatureResponse;
import ro.ionutzbaur.thermostat.service.ThermostatService;

@QuarkusTest
class ThermostatServiceImplTest {

    @Inject
    ThermostatService thermostatService;

    @Test
    void setTemperature() {
        /*TemperatureResponse temperatureResponse = thermostatService.setTemperature(22.d, null);
        System.out.println(temperatureResponse);*/
    }
}