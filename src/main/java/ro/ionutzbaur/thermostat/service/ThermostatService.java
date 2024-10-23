package ro.ionutzbaur.thermostat.service;

import ro.ionutzbaur.thermostat.model.RoomDTO;
import ro.ionutzbaur.thermostat.model.UserDTO;
import ro.ionutzbaur.thermostat.model.enums.DegreesScale;

import java.util.List;

public interface ThermostatService {

    boolean authenticate(String username, String password);

    UserDTO getUserInfo();

    List<RoomDTO> getRoomsInfo(Long homeId);

    void setTemperature(double degrees, DegreesScale scale);
}