package ro.ionutzbaur.thermostat.service.impl;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import ro.ionutzbaur.thermostat.datasource.tado.entity.auth.AuthorizationParams;
import ro.ionutzbaur.thermostat.datasource.tado.entity.auth.Me;
import ro.ionutzbaur.thermostat.datasource.tado.entity.auth.OAuth2Token;
import ro.ionutzbaur.thermostat.datasource.tado.entity.auth.RefreshTokenParams;
import ro.ionutzbaur.thermostat.datasource.tado.entity.control.*;
import ro.ionutzbaur.thermostat.datasource.tado.entity.control.enums.Power;
import ro.ionutzbaur.thermostat.datasource.tado.entity.control.enums.SettingType;
import ro.ionutzbaur.thermostat.datasource.tado.service.TadoAuthService;
import ro.ionutzbaur.thermostat.datasource.tado.service.TadoControllerService;
import ro.ionutzbaur.thermostat.datasource.tado.service.impl.CachedTadoControllerService;
import ro.ionutzbaur.thermostat.exception.TadoException;
import ro.ionutzbaur.thermostat.interceptor.qualifier.BrandService;
import ro.ionutzbaur.thermostat.model.*;
import ro.ionutzbaur.thermostat.model.enums.Brand;
import ro.ionutzbaur.thermostat.model.enums.DegreesScale;
import ro.ionutzbaur.thermostat.service.ThermostatService;
import ro.ionutzbaur.thermostat.service.config.TadoAuthConfig;
import ro.ionutzbaur.thermostat.util.ThermostatUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@ApplicationScoped
@BrandService(Brand.TADO)
public class TadoThermostatServiceImpl implements ThermostatService {

    private static final double MIN_DEGREES_CELSIUS = 5d;
    private static final double MIN_DEGREES_FAHRENHEIT = 41d;

    private final TadoAuthConfig tadoAuthConfig;
    private final TadoAuthService tadoAuthService;
    private final TadoControllerService tadoControllerService;
    private final CachedTadoControllerService cachedTadoControllerService;

    private OAuth2Token oAuth2Token;

    public TadoThermostatServiceImpl(TadoAuthConfig tadoAuthConfig,
                                     @RestClient TadoAuthService tadoAuthService,
                                     @RestClient TadoControllerService tadoControllerService,
                                     CachedTadoControllerService cachedTadoControllerService) {
        this.tadoAuthConfig = tadoAuthConfig;
        this.tadoAuthService = tadoAuthService;
        this.tadoControllerService = tadoControllerService;
        this.cachedTadoControllerService = cachedTadoControllerService;
    }

    @Override
    public boolean authenticate(String username, String password) {
        AuthorizationParams authorizationParams = new AuthorizationParams(tadoAuthConfig.clientId(), tadoAuthConfig.clientSecret(),
                tadoAuthConfig.authorizationGrantType(), tadoAuthConfig.scope(), username, password);
        oAuth2Token = tadoAuthService.authorize(authorizationParams.asXWwwFormUrlEncoded())
                .await()
                .indefinitely();

        return oAuth2Token != null;
    }

    @Override
    public UserDTO getUserInfo() {
        refreshTokenIfNeeded();

        Me me = cachedTadoControllerService.getInfoAboutMe(getAuthorizationHeader())
                .await()
                .indefinitely();

        List<HomeDTO> homes = me.getHomes()
                .stream()
                .map(home -> new HomeDTO(home.getId(), home.getName()))
                .toList();

        return new UserDTO(me.getName(), me.getEmail(), me.getUsername(), homes);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<RoomDTO> getAllRoomsInfo(String homeId, DegreesScale scale) {
        long tadoHomeId = toTadoHomeId(homeId);
        refreshTokenIfNeeded();

        List<Zone> zones = cachedTadoControllerService.getZones(getAuthorizationHeader(), tadoHomeId)
                .await()
                .indefinitely();

        List<Uni<ZoneState>> zoneStateUnis = new ArrayList<>();
        Map<Long, Zone> map = new HashMap<>();
        zones.forEach(zone -> {
            Uni<ZoneState> enhancedZoneStateUni = tadoControllerService.getZoneState(getAuthorizationHeader(), tadoHomeId, zone.getId())
                    .invoke(zoneState -> zoneState.setZoneId(zone.getId()));
            zoneStateUnis.add(enhancedZoneStateUni);
            map.computeIfAbsent(zone.getId(), k -> zone);
        });

        List<ZoneState> zoneStates = Uni.combine()
                .all()
                .unis(zoneStateUnis)
                .with(zoneStateList -> ((List<ZoneState>) zoneStateList)
                        .stream()
                        .filter(Objects::nonNull)
                        .toList())
                .await()
                .indefinitely();

        List<RoomDTO> rooms = new ArrayList<>();
        zoneStates.forEach(zoneState -> {
            Zone zone = map.get(zoneState.getZoneId());
            RoomDTO roomDTO = getRoomDTO(zoneState, zone, scale);

            rooms.add(roomDTO);
        });

        return rooms;
    }

    @Override
    public RoomDTO getRoomInfo(String homeId, String roomId, DegreesScale scale) {
        long tadoHomeId = toTadoHomeId(homeId);
        long tadoZoneId = toTadoZoneId(roomId);
        refreshTokenIfNeeded();

        Uni<List<Zone>> zonesUni = cachedTadoControllerService.getZones(getAuthorizationHeader(), tadoHomeId);
        Uni<ZoneState> zoneStateUni = tadoControllerService.getZoneState(getAuthorizationHeader(), tadoHomeId, tadoZoneId);

        AtomicReference<ZoneState> zoneStateAtomicRef = new AtomicReference<>();
        Zone zone = Uni.combine()
                .all()
                .unis(zonesUni, zoneStateUni)
                .asTuple()
                .map(tuple -> {
                    List<Zone> zones = tuple.getItem1();
                    zoneStateAtomicRef.set(tuple.getItem2());

                    return zones.stream()
                            .filter(z -> z.getId() == tadoZoneId)
                            .findFirst()
                            .orElseThrow(() -> new TadoException("Zone not found for zoneId " + tadoZoneId));
                })
                .await()
                .indefinitely();

        return getRoomDTO(zoneStateAtomicRef.get(), zone, scale);
    }

    @Override
    public TemperatureDTO setTemperature(TemperatureRequest temperatureRequest) {
        validateTemperatureRequest(temperatureRequest);

        long tadoHomeId = toTadoHomeId(temperatureRequest.homeId());
        long tadoZoneId = toTadoZoneId(temperatureRequest.roomId());

        Double degrees = temperatureRequest.degrees();
        Setting setting;
        boolean isTurnedOn;
        if (degrees == null) {
            setting = new Setting(SettingType.HEATING, Power.OFF, null);
            isTurnedOn = false;
        } else {
            setting = new Setting(SettingType.HEATING, Power.ON, buildTemperatureByScale(degrees, temperatureRequest.scale()));
            isTurnedOn = true;
        }

        refreshTokenIfNeeded();

        TemperatureControl temperatureControl = new TemperatureControl(setting, new Termination(SettingType.MANUAL));
        tadoControllerService.modifyTemperature(getAuthorizationHeader(), tadoHomeId, tadoZoneId, temperatureControl)
                .await()
                .indefinitely();

        return new TemperatureDTO(degrees, temperatureRequest.scale(), isTurnedOn);
    }

    private void refreshTokenIfNeeded() {
        if (oAuth2Token == null) {
            throw new TadoException("User is not authenticated!");
        }

        // refresh token if expired or if it's about to expire in less than 30 seconds
        if (oAuth2Token.getExpirationTime().minusSeconds(30).isBefore(LocalDateTime.now())) {
            RefreshTokenParams refreshTokenParams = new RefreshTokenParams(tadoAuthConfig.clientId(), tadoAuthConfig.clientSecret(),
                    tadoAuthConfig.refreshTokenGrantType(), tadoAuthConfig.scope(), oAuth2Token.getRefreshToken());
            oAuth2Token = tadoAuthService.authorize(refreshTokenParams.asXWwwFormUrlEncoded())
                    .await()
                    .indefinitely();
        }
    }

    private String getAuthorizationHeader() {
        return "Bearer " + oAuth2Token.getAccessToken();
    }

    private long toTadoHomeId(String homeId) {
        return ThermostatUtils.toLong(homeId, () -> new TadoException("HomeId must be a number! Provided: " + homeId));
    }

    private long toTadoZoneId(String roomId) {
        return ThermostatUtils.toLong(roomId, () -> new TadoException("HomeId must be a number! Provided: " + roomId));
    }

    private RoomDTO getRoomDTO(ZoneState zoneState, Zone zone, DegreesScale scale) {
        InsideTemperature insideTemperature = Optional.ofNullable(zoneState.getSensorDataPoints())
                .orElseThrow(() -> new TadoException("Temperature sensor info not available for zone " + zone.getName()))
                .getInsideTemperature();
        Temperature setTemperature = Optional.ofNullable(zoneState.getSetting())
                .map(Setting::temperature)
                .orElse(null);

        TemperatureDTO temperatureDTO = toTemperatureDTO(insideTemperature, setTemperature, scale);

        return new RoomDTO(zone.getName(), zone.getId(), temperatureDTO);
    }

    private void validateTemperatureRequest(TemperatureRequest temperatureRequest) {
        if (temperatureRequest == null) {
            throw new IllegalArgumentException("Temperature request must not be null!");
        }

        if (temperatureRequest.homeId() == null) {
            throw new IllegalArgumentException("HomeId must not be null!");
        }

        if (temperatureRequest.roomId() == null) {
            throw new IllegalArgumentException("RoomId must not be null!");
        }

        if (temperatureRequest.degrees() != null) {
            if (temperatureRequest.scale() == DegreesScale.CELSIUS && temperatureRequest.degrees() < MIN_DEGREES_CELSIUS) {
                throw new IllegalArgumentException("New Tado temperature must be at least " + MIN_DEGREES_CELSIUS + " degrees celsius!");
            }

            if (temperatureRequest.scale() == DegreesScale.FAHRENHEIT && temperatureRequest.degrees() < MIN_DEGREES_FAHRENHEIT) {
                throw new IllegalArgumentException("New Tado temperature must be at least " + MIN_DEGREES_FAHRENHEIT + " degrees fahrenheit!");
            }
        }
    }

    private TemperatureDTO toTemperatureDTO(InsideTemperature insideTemperature,
                                            Temperature setTemperature,
                                            DegreesScale scale) {
        if (insideTemperature == null) {
            return new TemperatureDTO(null, null, false);
        }

        Double degrees;
        if (scale == DegreesScale.CELSIUS) {
            degrees = insideTemperature.getCelsius();
        } else { // FAHRENHEIT
            degrees = insideTemperature.getFahrenheit();
        }

        return new TemperatureDTO(degrees, scale, setTemperature != null);
    }

    private Temperature buildTemperatureByScale(double degrees, DegreesScale scale) {
        return switch (scale) {
            case CELSIUS -> new Temperature(degrees, null);
            case FAHRENHEIT -> new Temperature(null, degrees);
        };
    }
}
