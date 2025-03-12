package ro.ionutzbaur.thermostat.datasource.tado.entity.auth;

import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;

public abstract sealed class AbstractAuthParams permits AuthorizationParams, RefreshTokenParams {

    private static final String CLIENT_ID_PARAM = "client_id";
    private static final String CLIENT_SECRET_PARAM = "client_secret";
    private static final String GRANT_TYPE_PARAM = "grant_type";
    private static final String SCOPE_PARAM = "scope";

    private final String clientId;
    private final String clientSecret;
    private final String grantType;
    private final String scope;

    protected AbstractAuthParams(String clientId, String clientSecret, String grantType, String scope) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.grantType = grantType;
        this.scope = scope;
    }

    public Form asXWwwFormUrlEncoded() {
        MultivaluedMap<String, String> params = new MultivaluedHashMap<>();
        params.add(CLIENT_ID_PARAM, clientId);
        params.add(CLIENT_SECRET_PARAM, clientSecret);
        params.add(GRANT_TYPE_PARAM, grantType);
        params.add(SCOPE_PARAM, scope);

        addAuthSpecificParams(params);

        return RequestUtil.asXWwwFormUrlEncoded(params);
    }

    protected abstract void addAuthSpecificParams(MultivaluedMap<String, String> params);
}
