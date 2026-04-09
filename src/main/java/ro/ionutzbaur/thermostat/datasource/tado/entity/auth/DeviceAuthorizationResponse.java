package ro.ionutzbaur.thermostat.datasource.tado.entity.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceAuthorizationResponse {

    @JsonProperty("device_code")
    private String deviceCode;

    @JsonProperty("user_code")
    private String userCode;

    @JsonProperty("verification_uri")
    private String verificationUri;

    @JsonProperty("verification_uri_complete")
    private String verificationUriComplete;

    @JsonProperty("expires_in")
    private Long expiresIn;

    @JsonProperty("interval")
    private Long interval;

    public String getDeviceCode() {
        return deviceCode;
    }

    public String getUserCode() {
        return userCode;
    }

    public String getVerificationUri() {
        return verificationUri;
    }

    public String getVerificationUriComplete() {
        return verificationUriComplete;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public Long getInterval() {
        return interval;
    }
}
