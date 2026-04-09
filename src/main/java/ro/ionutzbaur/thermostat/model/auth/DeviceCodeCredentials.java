package ro.ionutzbaur.thermostat.model.auth;

public record DeviceCodeCredentials(String deviceCode) implements AuthCredentials {
}
