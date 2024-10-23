package ro.ionutzbaur.thermostat.datasource.tado.entity.auth;

import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;

public class RefreshTokenParams {

    private final String clientId;
    private final String clientSecret;
    private final String grantType;
    private final String scope;
    private final String refreshToken;

    public RefreshTokenParams(String clientId,
                              String clientSecret,
                              String grantType,
                              String scope,
                              String refreshToken) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.grantType = grantType;
        this.scope = scope;
        this.refreshToken = refreshToken;
    }

    public Form asXWwwFormUrlEncoded() {
        MultivaluedMap<String, String> params = new MultivaluedHashMap();
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("grant_type", grantType);
        params.add("scope", scope);
        params.add("refresh_token", refreshToken);

        return RequestUtil.asXWwwFormUrlEncoded(params);
    }
}
