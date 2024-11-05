package ro.ionutzbaur.thermostat.rest;

import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import ro.ionutzbaur.thermostat.interceptor.filter.BrandFilter;
import ro.ionutzbaur.thermostat.model.RoutineDTO;
import ro.ionutzbaur.thermostat.model.RoutineRequest;
import ro.ionutzbaur.thermostat.model.enums.Brand;
import ro.ionutzbaur.thermostat.service.RoutineService;
import ro.ionutzbaur.thermostat.util.HelperConstants;

import java.util.List;

@Tag(name = "Thermostat routines", description = "Create different routines for the thermostat")
@Path("routines")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoutineResource {

    private final RoutineService routineService;

    public RoutineResource(RoutineService routineService) {
        this.routineService = routineService;
    }

    @Operation(summary = "List of routines", description = "Get all current routines")
    @GET
    public List<RoutineDTO> getAllRoutines() {
        return routineService.getAllRoutines();
    }

    @Operation(summary = "Create routine", description = "Create a new room temperature routine for specific brand")
    @Parameter(in = ParameterIn.HEADER, name = BrandFilter.BRAND_ID_HEADER,
            schema = @Schema(implementation = Brand.class), description = HelperConstants.BRAND_HEADER_DESCRIPTION)
    @POST
    @RunOnVirtualThread
    public RoutineDTO createRoomTemperatureRoutine(RoutineRequest routineRequest) {
        return routineService.createRoomTemperatureRoutine(routineRequest);
    }

    @Operation(summary = "Remove all routines", description = "Remove all room temperature routines")
    @DELETE
    public void removeAllRoutines() {
        routineService.removeAllRoutines();
    }

    @Operation(summary = "Remove routine", description = "Remove a room temperature routine by id")
    @DELETE
    @Path("{routineId}")
    public void removeRoutine(@PathParam("routineId") String routineId) {
        routineService.removeRoutine(routineId);
    }
}
