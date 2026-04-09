package ro.ionutzbaur.thermostat.model.auth;

public record UsernamePasswordCredentials(String username, String password) implements AuthCredentials {
}
