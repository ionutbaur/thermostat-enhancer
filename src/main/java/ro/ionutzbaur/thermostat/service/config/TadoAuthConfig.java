package ro.ionutzbaur.thermostat.service.config;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "tado.auth")
public interface TadoAuthConfig {

    String clientId();

    String deviceCodeGrantType();

    String refreshTokenGrantType();

    String scope();
}