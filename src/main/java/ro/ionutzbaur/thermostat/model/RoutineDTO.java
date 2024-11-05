package ro.ionutzbaur.thermostat.model;

import ro.ionutzbaur.thermostat.model.enums.Brand;
import ro.ionutzbaur.thermostat.model.enums.RoutineType;

public record RoutineDTO(String id,
                         String description,
                         RoutineType routineType,
                         String homeId,
                         String roomId,
                         Brand brand) {

}
