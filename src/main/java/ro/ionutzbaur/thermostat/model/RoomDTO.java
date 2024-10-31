package ro.ionutzbaur.thermostat.model;

public record RoomDTO(String name, Long id, TemperatureDTO temperature) {
}
