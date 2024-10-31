package ro.ionutzbaur.thermostat.service;

import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.spi.BeanManager;
import ro.ionutzbaur.thermostat.interceptor.BrandServiceProducer;

public class ServiceProducer extends BrandServiceProducer {

    public ServiceProducer(BeanManager beanManager) {
        super(beanManager);
    }

    @RequestScoped
    public ThermostatService produceThermostatService() {
        return produceBrandService(ThermostatService.class);
    }
}
