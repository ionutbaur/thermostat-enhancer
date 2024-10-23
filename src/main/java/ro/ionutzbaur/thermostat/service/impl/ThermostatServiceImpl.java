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
import ro.ionutzbaur.thermostat.model.HomeDTO;
import ro.ionutzbaur.thermostat.model.RoomDTO;
import ro.ionutzbaur.thermostat.model.TemperatureDTO;
import ro.ionutzbaur.thermostat.model.UserDTO;
import ro.ionutzbaur.thermostat.model.enums.DegreesScale;
import ro.ionutzbaur.thermostat.service.ThermostatService;

import java.util.*;

@ApplicationScoped
public class ThermostatServiceImpl implements ThermostatService {

    private final TadoAuthService tadoAuthService;
    private final TadoControllerService tadoControllerService;
    private final CachedTadoControllerService cachedTadoControllerService;

    private OAuth2Token oAuth2Token;

    public ThermostatServiceImpl(@RestClient TadoAuthService tadoAuthService,
                                 @RestClient TadoControllerService tadoControllerService,
                                 CachedTadoControllerService cachedTadoControllerService) {
        this.tadoAuthService = tadoAuthService;
        this.tadoControllerService = tadoControllerService;
        this.cachedTadoControllerService = cachedTadoControllerService;
    }

    @Override
    public boolean authenticate(String username, String password) {
        AuthorizationParams authorizationParams = new AuthorizationParams("tado-web-app",
                "wZaRN7rpjn3FoNyF5IFuxg9uMzYJcvOoQ8QWiIqS3hfk6gLhVlG57j5YNoZL2Rtc",
                "password",
                "home.user",
                username,
                password);
        oAuth2Token = tadoAuthService.authenticate(authorizationParams.asXWwwFormUrlEncoded())
                .await()
                .indefinitely();

        return oAuth2Token != null;
    }

    private void refreshToken() {
        if (oAuth2Token == null) {
            throw new IllegalStateException("User is not authenticated!");
        }

        RefreshTokenParams refreshTokenParams = new RefreshTokenParams("tado-web-app",
                "wZaRN7rpjn3FoNyF5IFuxg9uMzYJcvOoQ8QWiIqS3hfk6gLhVlG57j5YNoZL2Rtc",
                "refresh_token",
                "home.user",
                oAuth2Token.getRefreshToken());
        oAuth2Token = tadoAuthService.authenticate(refreshTokenParams.asXWwwFormUrlEncoded())
                .await()
                .indefinitely();
    }

    @Override
    public UserDTO getUserInfo() {
        refreshToken();

        Me me = cachedTadoControllerService.getInfoAboutMe("Bearer " + oAuth2Token.getAccessToken())
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
    public List<RoomDTO> getRoomsInfo(Long homeId) {
        refreshToken();

        List<Zone> zones = cachedTadoControllerService.getZones("Bearer " + oAuth2Token.getAccessToken(), homeId)
                .await()
                .indefinitely();
        List<RoomDTO> rooms = new ArrayList<>();

        List<Uni<ZoneState>> zoneStateUnis = new ArrayList<>();
        Map<Long, Zone> map = new HashMap<>();
        zones.forEach(zone -> {
            Uni<ZoneState> enhancedZoneStateUni = tadoControllerService.getZoneState("Bearer " + oAuth2Token.getAccessToken(), homeId, zone.getId())
                    .invoke(zoneState -> zoneState.setZoneId(zone.getId()));
            zoneStateUnis.add(enhancedZoneStateUni);
            map.computeIfAbsent(zone.getId(), k -> zone);
        });

        Uni<List<ZoneState>> uni = Uni.combine()
                .all()
                .unis(zoneStateUnis)
                .with(zoneStates -> ((List<ZoneState>) zoneStates)
                        .stream()
                        .filter(Objects::nonNull)
                        .toList());

        List<ZoneState> zoneStates = uni.await().indefinitely();
        zoneStates.forEach(zoneState -> {
            Zone zone = map.get(zoneState.getZoneId());
            RoomDTO roomDTO = new RoomDTO(zone.getName(), zone.getId());

            Temperature temperature = zoneState.getSetting().temperature();
            roomDTO.setTemperature(new TemperatureDTO(temperature.celsius(), DegreesScale.CELSIUS));

            rooms.add(roomDTO);
        });


        return rooms;
    }

    @Override
    public void setTemperature(double degrees, DegreesScale scale) {
        /*TemperatureResponse temperatureResponse = tadoControllerService.modifyTemperature(
                        "Bearer " + bearerToken.getAccessToken(),
                        me.getHomes().getFirst().getId(),
                        5L,
                        new TemperatureControl(new Setting(SettingType.HEATING, Power.ON,
                                setTemperatureByScale(degrees, scale)), new Termination(SettingType.MANUAL)))
                .await()
                .indefinitely();*/
    }

    private Temperature setTemperatureByScale(double degrees, DegreesScale scale) {
        return switch (scale) {
            case CELSIUS -> new Temperature(degrees, null);
            case FAHRENHEIT -> new Temperature(null, degrees);
        };
    }
}
