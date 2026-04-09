package ro.ionutzbaur.thermostat.model.auth;

/**
 * Request body for the authentication confirm step.
 * Populate only the fields relevant to the brand being used
 * (e.g. {@code deviceCode} for device-auth brands, {@code username}/{@code password} for credential-based brands).
 */
public record AuthConfirmRequest(String deviceCode, String username, String password) {
}
