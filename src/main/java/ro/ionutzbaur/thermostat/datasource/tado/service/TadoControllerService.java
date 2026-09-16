package ro.ionutzbaur.thermostat.datasource.tado.service;

import io.quarkus.rest.client.reactive.ClientExceptionMapper;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import ro.ionutzbaur.thermostat.datasource.tado.entity.auth.Me;
import ro.ionutzbaur.thermostat.datasource.tado.entity.control.TadoTemperatureResponse;
import ro.ionutzbaur.thermostat.datasource.tado.entity.control.TemperatureControl;
import ro.ionutzbaur.thermostat.datasource.tado.entity.control.Zone;
import ro.ionutzbaur.thermostat.datasource.tado.entity.control.ZoneState;
import ro.ionutzbaur.thermostat.util.RestClientErrorHandler;

import java.lang.reflect.Method;
import java.util.List;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@RegisterRestClient(configKey = "tado-controller-service")
@Path("api/v2")
@Consumes(APPLICATION_JSON)
public interface TadoControllerService {

    @GET
    @Path("me")
    Uni<Me> getInfoAboutMe(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorization);

    @GET
    @Path("homes/{homeId}/zones")
    Uni<List<Zone>> getZones(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorization,
                             @PathParam("homeId") Long homeId);

    @GET
    @Path("homes/{homeId}/zones/{zoneId}/state")
    Uni<ZoneState> getZoneState(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorization,
                                @PathParam("homeId") Long homeId,
                                @PathParam("zoneId") Long zoneId);

    @PUT
    @Path("homes/{homeId}/zones/{zoneId}/overlay")
    Uni<TadoTemperatureResponse> modifyTemperature(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorization,
                                                   @PathParam("homeId") Long homeId,
                                                   @PathParam("zoneId") Long zoneId,
                                                   TemperatureControl temperatureControl);

    @ClientExceptionMapper
    static WebApplicationException toException(Response response, Method method) {
        return RestClientErrorHandler.toException(response, method);
    }
}
