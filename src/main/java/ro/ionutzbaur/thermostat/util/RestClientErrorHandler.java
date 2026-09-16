package ro.ionutzbaur.thermostat.util;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.lang.reflect.Method;

public class RestClientErrorHandler {

    /**
     * Converts a Response object to a WebApplicationException with a detailed error message.
     * <p>
     * Should be used in conjunction with a {@link io.quarkus.rest.client.reactive.ClientExceptionMapper}
     * in a REST client interface to handle error responses from external API calls.
     *
     * @param response the Response object containing the error details
     * @param method   the Method that was invoked when the error occurred
     * @return a WebApplicationException with a detailed error message and the original response
     */
    public static WebApplicationException toException(Response response, Method method) {
        int status = response.getStatus();
        String body = response.readEntity(String.class);
        String errorMessage = "Error response from external API call. " +
                "HTTP " + status + " on " + method.getDeclaringClass().getSimpleName() + "::" + method.getName() + ": " + body;
        return new WebApplicationException(
                errorMessage,
                Response.serverError()
                        .entity(errorMessage)
                        .type(MediaType.TEXT_PLAIN_TYPE)
                        .build()
        );
    }
}
