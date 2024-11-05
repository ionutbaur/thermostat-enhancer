package ro.ionutzbaur.thermostat.service;

import ro.ionutzbaur.thermostat.model.RoomDTO;
import ro.ionutzbaur.thermostat.model.TemperatureDTO;
import ro.ionutzbaur.thermostat.model.TemperatureRequest;
import ro.ionutzbaur.thermostat.model.UserDTO;
import ro.ionutzbaur.thermostat.model.enums.DegreesScale;

import java.util.List;

public interface ThermostatService {

    boolean authenticate(String username, String password);

    UserDTO getUserInfo();

    List<RoomDTO> getAllRoomsInfo(String homeId, DegreesScale scale);

    RoomDTO getRoomInfo(String homeId, String roomId, DegreesScale scale);

    TemperatureDTO setTemperature(TemperatureRequest temperatureRequest);
}
