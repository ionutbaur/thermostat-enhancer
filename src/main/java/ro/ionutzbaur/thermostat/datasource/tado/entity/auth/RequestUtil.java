package ro.ionutzbaur.thermostat.datasource.tado.entity.auth;

import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.MultivaluedMap;

public class RequestUtil {

    private RequestUtil() {
        // Utility class
    }

    public static Form asXWwwFormUrlEncoded(MultivaluedMap<String, String> params) {
        return new Form(params);
    }
}
