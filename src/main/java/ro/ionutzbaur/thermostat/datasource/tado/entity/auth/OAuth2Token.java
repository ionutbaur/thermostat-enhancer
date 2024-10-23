package ro.ionutzbaur.thermostat.datasource.tado.entity.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OAuth2Token {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("expires_in")
    private Long expiresInMinutes;

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public Long getExpiresInMinutes() {
        return expiresInMinutes;
    }
}
