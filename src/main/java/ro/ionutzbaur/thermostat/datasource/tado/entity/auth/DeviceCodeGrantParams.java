package ro.ionutzbaur.thermostat.datasource.tado.entity.auth;

import jakarta.ws.rs.core.MultivaluedMap;

public final class DeviceCodeGrantParams extends AbstractAuthParams {

    private static final String GRANT_TYPE_PARAM = "grant_type";
    private static final String DEVICE_CODE_PARAM = "device_code";

    private final String grantType;
    private final String deviceCode;

    public DeviceCodeGrantParams(String clientId, String grantType, String deviceCode) {
        super(clientId);
        this.grantType = grantType;
        this.deviceCode = deviceCode;
    }

    @Override
    protected void addAuthSpecificParams(MultivaluedMap<String, String> params) {
        params.add(GRANT_TYPE_PARAM, grantType);
        params.add(DEVICE_CODE_PARAM, deviceCode);
    }
}
