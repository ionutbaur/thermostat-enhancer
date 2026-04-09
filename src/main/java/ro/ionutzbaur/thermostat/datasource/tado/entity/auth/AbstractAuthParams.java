package ro.ionutzbaur.thermostat.datasource.tado.entity.auth;

import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;

public abstract sealed class AbstractAuthParams permits DeviceInitiateParams, DeviceCodeGrantParams, RefreshTokenParams {

    private static final String CLIENT_ID_PARAM = "client_id";

    private final String clientId;

    protected AbstractAuthParams(String clientId) {
        this.clientId = clientId;
    }

    public Form asXWwwFormUrlEncoded() {
        MultivaluedMap<String, String> params = new MultivaluedHashMap<>();
        params.add(CLIENT_ID_PARAM, clientId);

        addAuthSpecificParams(params);

        return RequestUtil.asXWwwFormUrlEncoded(params);
    }

    protected abstract void addAuthSpecificParams(MultivaluedMap<String, String> params);
}
