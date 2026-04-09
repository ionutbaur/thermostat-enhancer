package ro.ionutzbaur.thermostat.model.auth;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Generic response for the authentication initiation step.
 * The {@code type} field tells the client what credentials to provide in the confirm step.
 * <ul>
 *   <li>{@link AuthInitiateType#DEVICE_AUTH} – client must redirect the user to {@code verificationUri}
 *       and then confirm with the {@code deviceCode}.</li>
 *   <li>{@link AuthInitiateType#CREDENTIALS_REQUIRED} – client must provide username/password in the confirm step.</li>
 * </ul>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthInitiateResponse {

    private final AuthInitiateType type;
    private final String verificationUri;
    private final String verificationUriComplete;
    private final String userCode;
    private final String deviceCode;
    private final Long expiresIn;

    private AuthInitiateResponse(AuthInitiateType type,
                                  String verificationUri,
                                  String verificationUriComplete,
                                  String userCode,
                                  String deviceCode,
                                  Long expiresIn) {
        this.type = type;
        this.verificationUri = verificationUri;
        this.verificationUriComplete = verificationUriComplete;
        this.userCode = userCode;
        this.deviceCode = deviceCode;
        this.expiresIn = expiresIn;
    }

    public static AuthInitiateResponse deviceAuth(String verificationUri,
                                                   String verificationUriComplete,
                                                   String userCode,
                                                   String deviceCode,
                                                   Long expiresIn) {
        return new AuthInitiateResponse(AuthInitiateType.DEVICE_AUTH,
                verificationUri, verificationUriComplete, userCode, deviceCode, expiresIn);
    }

    public static AuthInitiateResponse credentialsRequired() {
        return new AuthInitiateResponse(AuthInitiateType.CREDENTIALS_REQUIRED,
                null, null, null, null, null);
    }

    public AuthInitiateType getType() {
        return type;
    }

    public String getVerificationUri() {
        return verificationUri;
    }

    public String getVerificationUriComplete() {
        return verificationUriComplete;
    }

    public String getUserCode() {
        return userCode;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }
}
