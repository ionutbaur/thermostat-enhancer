package ro.ionutzbaur.thermostat.datasource.tado.service;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Form;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import ro.ionutzbaur.thermostat.datasource.tado.entity.auth.OAuth2Token;

import static jakarta.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@RegisterRestClient(configKey = "tado-auth-service")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_FORM_URLENCODED)
public interface TadoAuthService {

    @POST
    @Path("/oauth/token")
    Uni<OAuth2Token> authenticate(Form xWwwFormUrlEncoded);
}
