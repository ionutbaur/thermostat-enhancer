package ro.ionutzbaur.thermostat.datasource.tado.entity.auth;

import jakarta.ws.rs.core.MultivaluedMap;

public final class AuthorizationParams extends AbstractAuthParams {

    private static final String USERNAME_PARAM = "username";
    private static final String PASSWORD_PARAM = "password";

    private final String username;
    private final String password;

    public AuthorizationParams(String clientId,
                               String clientSecret,
                               String grantType,
                               String scope,
                               String username,
                               String password) {
        super(clientId, clientSecret, grantType, scope);
        this.username = username;
        this.password = password;
    }

    @Override
    protected void addAuthSpecificParams(MultivaluedMap<String, String> params) {
        params.add(USERNAME_PARAM, username);
        params.add(PASSWORD_PARAM, password);
    }
}
