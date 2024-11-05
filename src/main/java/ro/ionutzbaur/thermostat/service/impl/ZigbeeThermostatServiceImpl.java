package ro.ionutzbaur.thermostat.service.impl;

import jakarta.enterprise.context.ApplicationScoped;
import ro.ionutzbaur.thermostat.interceptor.qualifier.BrandService;
import ro.ionutzbaur.thermostat.model.RoomDTO;
import ro.ionutzbaur.thermostat.model.TemperatureDTO;
import ro.ionutzbaur.thermostat.model.TemperatureRequest;
import ro.ionutzbaur.thermostat.model.UserDTO;
import ro.ionutzbaur.thermostat.model.enums.Brand;
import ro.ionutzbaur.thermostat.model.enums.DegreesScale;
import ro.ionutzbaur.thermostat.service.ThermostatService;

import java.util.List;

@ApplicationScoped
@BrandService(Brand.ZIGBEE)
public class ZigbeeThermostatServiceImpl implements ThermostatService {

    @Override
    public boolean authenticate(String username, String password) {
        throw new RuntimeException("Not yet implemented for Zigbee");
    }

    @Override
    public UserDTO getUserInfo() {
        throw new RuntimeException("Not yet implemented for Zigbee");
    }

    @Override
    public List<RoomDTO> getAllRoomsInfo(String homeId, DegreesScale scale) {
        throw new RuntimeException("Not yet implemented for Zigbee");
    }

    @Override
    public RoomDTO getRoomInfo(String homeId, String roomId, DegreesScale scale) {
        throw new RuntimeException("Not yet implemented for Zigbee");
    }

    @Override
    public TemperatureDTO setTemperature(TemperatureRequest temperatureRequest) {
        throw new RuntimeException("Not yet implemented for Zigbee");
    }
}
