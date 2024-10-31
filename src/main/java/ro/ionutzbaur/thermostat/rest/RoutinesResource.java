package ro.ionutzbaur.thermostat.rest;

import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import ro.ionutzbaur.thermostat.model.enums.DegreesScale;
import ro.ionutzbaur.thermostat.poller.TemperaturePoller;

@Tag(name = "Thermostat routines", description = "Create different routines for the thermostat")
@Path("routines")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoutinesResource {

    private final TemperaturePoller temperaturePoller;

    public RoutinesResource(TemperaturePoller temperaturePoller) {
        this.temperaturePoller = temperaturePoller;
    }

    @POST
    @Path("{homeId}/rooms/{roomId}/temperature/{temperature}")
    @RunOnVirtualThread
    public void poll(@PathParam("homeId") String homeId,
                     @PathParam("roomId") String roomId,
                     @PathParam("temperature") double temperature,
                     @QueryParam("degreesScale")
                     @DefaultValue("CELSIUS") DegreesScale scale) {
        temperaturePoller.startPolling(homeId, roomId, temperature, scale);
    }

    @POST
    @Path("stop-polling")
    public void poll() {
        temperaturePoller.stopPolling();
    }
}
