package ro.ionutzbaur.thermostat.model;

import java.util.List;

public record UserDTO(String name, String email, String username, List<HomeDTO> homes) {
}
