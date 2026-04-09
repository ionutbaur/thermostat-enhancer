package ro.ionutzbaur.thermostat.model.auth;

/**
 * Sealed interface representing credentials supplied during the authentication confirm step.
 * Each brand's service implementation accepts the credential type it supports.
 */
public sealed interface AuthCredentials permits DeviceCodeCredentials, UsernamePasswordCredentials {
}
