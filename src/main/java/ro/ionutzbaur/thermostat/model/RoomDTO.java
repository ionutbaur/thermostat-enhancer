package ro.ionutzbaur.thermostat.model;

public class RoomDTO {

    private final String name;
    private final Long id;
    private TemperatureDTO temperature;

    public RoomDTO(String name, Long id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public TemperatureDTO getTemperature() {
        return temperature;
    }

    public void setTemperature(TemperatureDTO temperature) {
        this.temperature = temperature;
    }
}
