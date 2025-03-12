package ro.ionutzbaur.thermostat.datasource.tado.entity.auth;

import jakarta.ws.rs.core.MultivaluedMap;

public final class RefreshTokenParams extends AbstractAuthParams {

    private static final String REFRESH_TOKEN_PARAM = "refresh_token";

    private final String refreshToken;

    public RefreshTokenParams(String clientId,
                              String clientSecret,
                              String grantType,
                              String scope,
                              String refreshToken) {
        super(clientId, clientSecret, grantType, scope);
        this.refreshToken = refreshToken;
    }

    @Override
    protected void addAuthSpecificParams(MultivaluedMap<String, String> params) {
        params.add(REFRESH_TOKEN_PARAM, refreshToken);
    }
}
