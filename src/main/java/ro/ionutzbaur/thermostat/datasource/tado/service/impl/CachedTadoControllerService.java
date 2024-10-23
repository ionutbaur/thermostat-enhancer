package ro.ionutzbaur.thermostat.datasource.tado.service.impl;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import ro.ionutzbaur.thermostat.datasource.tado.entity.auth.Me;
import ro.ionutzbaur.thermostat.datasource.tado.entity.control.Zone;
import ro.ionutzbaur.thermostat.datasource.tado.service.TadoControllerService;

import io.quarkus.cache.CacheResult;

import java.util.List;

@ApplicationScoped
public class CachedTadoControllerService {

    private final TadoControllerService tadoControllerService;

    public CachedTadoControllerService(@RestClient TadoControllerService tadoControllerService) {
        this.tadoControllerService = tadoControllerService;
    }

    @CacheResult(cacheName = "tadoControllerService-me")
    public Uni<Me> getInfoAboutMe(String authorization) {
        return tadoControllerService.getInfoAboutMe(authorization);
    }

    @CacheResult(cacheName = "tadoControllerService-zones")
    public Uni<List<Zone>> getZones(String authorization, Long homeId) {
        return tadoControllerService.getZones(authorization, homeId);
    }
}
