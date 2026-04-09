package ro.ionutzbaur.thermostat.factory;

import ro.ionutzbaur.thermostat.exception.ThermostatException;
import ro.ionutzbaur.thermostat.model.auth.AuthConfirmRequest;
import ro.ionutzbaur.thermostat.model.auth.AuthCredentials;
import ro.ionutzbaur.thermostat.model.auth.DeviceCodeCredentials;
import ro.ionutzbaur.thermostat.model.auth.UsernamePasswordCredentials;

public class AuthCredentialsFactory {

    private AuthCredentialsFactory() {
        // statics only
    }

    public static AuthCredentials createCredentials(AuthConfirmRequest request) {
        if (request.deviceCode() != null) {
            return new DeviceCodeCredentials(request.deviceCode());
        } else if (request.username() != null && request.password() != null) {
            return new UsernamePasswordCredentials(request.username(), request.password());
        } else {
            throw new ThermostatException("Invalid authentication confirm request: " +
                    "must provide either deviceCode or username and password");
        }
    }
}
