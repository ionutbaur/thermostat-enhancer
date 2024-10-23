package ro.ionutzbaur.thermostat.rest;

import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import ro.ionutzbaur.thermostat.model.AuthRequest;
import ro.ionutzbaur.thermostat.model.RoomDTO;
import ro.ionutzbaur.thermostat.model.UserDTO;
import ro.ionutzbaur.thermostat.service.ThermostatService;

import java.util.List;

@Path("base")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ThermostatResource {

    private final ThermostatService thermostatService;

    public ThermostatResource(ThermostatService thermostatService) {
        this.thermostatService = thermostatService;
    }

    @PUT
    @Path("auth")
    @RunOnVirtualThread
    public Boolean auth(AuthRequest authRequest) {
        return thermostatService.authenticate(authRequest.username(), authRequest.password());
    }

    @GET
    @Path("user-info")
    @RunOnVirtualThread
    public UserDTO getUserInfo() {
        return thermostatService.getUserInfo();
    }

    @GET
    @Path("{homeId}/rooms-info")
    public List<RoomDTO> getRoomsInfo(@PathParam("homeId") Long homeId) {
        return thermostatService.getRoomsInfo(homeId);
    }
}
