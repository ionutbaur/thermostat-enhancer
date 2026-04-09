package ro.ionutzbaur.thermostat.datasource.tado.entity.auth;

import jakarta.ws.rs.core.MultivaluedMap;

public final class DeviceInitiateParams extends AbstractAuthParams {

    private static final String SCOPE_PARAM = "scope";

    private final String scope;

    public DeviceInitiateParams(String clientId, String scope) {
        super(clientId);
        this.scope = scope;
    }

    @Override
    protected void addAuthSpecificParams(MultivaluedMap<String, String> params) {
        params.add(SCOPE_PARAM, scope);
    }
}
