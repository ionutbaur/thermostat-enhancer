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
import ro.ionutzbaur.thermostat.model.*;
import ro.ionutzbaur.thermostat.model.enums.Brand;
import ro.ionutzbaur.thermostat.model.enums.DegreesScale;
import ro.ionutzbaur.thermostat.service.ThermostatService;
import ro.ionutzbaur.thermostat.util.HelperConstants;

import java.util.List;

@Tag(name = "Thermostat basics", description = "Basic operations for thermostat management")
@Path("base")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ThermostatResource {

    private final ThermostatService thermostatService;

    public ThermostatResource(ThermostatService thermostatService) {
        this.thermostatService = thermostatService;
    }

    @Operation(summary = "Authentication", description = "Authenticate an user in the chosen brand's system")
    @Parameter(in = ParameterIn.HEADER, name = BrandFilter.BRAND_ID_HEADER,
            schema = @Schema(implementation = Brand.class), description = HelperConstants.BRAND_HEADER_DESCRIPTION)
    @PUT
    @Path("auth")
    @RunOnVirtualThread
    public Boolean auth(AuthRequest authRequest) {
        return thermostatService.authenticate(authRequest.username(), authRequest.password());
    }

    @Operation(summary = "User info", description = "Get user information from the chosen brand's system")
    @Parameter(in = ParameterIn.HEADER, name = BrandFilter.BRAND_ID_HEADER,
            schema = @Schema(implementation = Brand.class), description = HelperConstants.BRAND_HEADER_DESCRIPTION)
    @GET
    @Path("user-info")
    @RunOnVirtualThread
    public UserDTO getUserInfo() {
        return thermostatService.getUserInfo();
    }

    @Operation(summary = "All rooms info", description = "Get information of all rooms from the chosen brand's system")
    @Parameter(in = ParameterIn.HEADER, name = BrandFilter.BRAND_ID_HEADER,
            schema = @Schema(implementation = Brand.class), description = HelperConstants.BRAND_HEADER_DESCRIPTION)
    @GET
    @Path("{homeId}/rooms")
    @RunOnVirtualThread
    public List<RoomDTO> getAllRoomsInfo(@PathParam("homeId") String homeId,
                                         @QueryParam("degreesScale")
                                         @DefaultValue("CELSIUS") DegreesScale scale) {
        return thermostatService.getAllRoomsInfo(homeId, scale);
    }

    @Operation(summary = "Room info", description = "Get information of a specific room from the chosen brand's system")
    @Parameter(in = ParameterIn.HEADER, name = BrandFilter.BRAND_ID_HEADER,
            schema = @Schema(implementation = Brand.class), description = HelperConstants.BRAND_HEADER_DESCRIPTION)
    @GET
    @Path("{homeId}/rooms/{roomId}")
    @RunOnVirtualThread
    public RoomDTO getRoomInfo(@PathParam("homeId") String homeId,
                               @PathParam("roomId") String roomId,
                               @QueryParam("degreesScale")
                               @DefaultValue("CELSIUS") DegreesScale scale) {
        return thermostatService.getRoomInfo(homeId, roomId, scale);
    }

    @Operation(summary = "Modify temperature", description = "Set a new temperature in a room belonging to a specific brand")
    @Parameter(in = ParameterIn.HEADER, name = BrandFilter.BRAND_ID_HEADER,
            schema = @Schema(implementation = Brand.class), description = HelperConstants.BRAND_HEADER_DESCRIPTION)
    @PUT
    @Path("temperature")
    @RunOnVirtualThread
    public TemperatureDTO modifyTemperature(TemperatureRequest temperatureRequest) {
        return thermostatService.setTemperature(temperatureRequest);
    }

}
