package ro.ionutzbaur.thermostat.datasource.tado.entity.auth;

import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;

public class AuthorizationParams {

    private final String clientId;
    private final String clientSecret;
    private final String grantType;
    private final String scope;
    private final String username;
    private final String password;

    public AuthorizationParams(String clientId,
                               String clientSecret,
                               String grantType,
                               String scope,
                               String username,
                               String password) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.grantType = grantType;
        this.scope = scope;
        this.username = username;
        this.password = password;
    }

    public Form asXWwwFormUrlEncoded() {
        final MultivaluedMap<String, String> params = new MultivaluedHashMap<>();
        params.add("client_id", clientId);
        params.add("grant_type", grantType);
        params.add("scope", scope);
        params.add("username", username);
        params.add("password", password);
        params.add("client_secret", clientSecret);

        return RequestUtil.asXWwwFormUrlEncoded(params);
    }
}
