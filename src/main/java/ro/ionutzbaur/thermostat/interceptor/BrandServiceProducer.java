package ro.ionutzbaur.thermostat.interceptor;

import io.smallrye.common.vertx.ContextLocals;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.util.AnnotationLiteral;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.ionutzbaur.thermostat.interceptor.filter.BrandFilter;
import ro.ionutzbaur.thermostat.interceptor.qualifier.BrandService;
import ro.ionutzbaur.thermostat.model.enums.Brand;

import java.util.Set;

/**
 * A CDI bean producer.
 * This class should be extended by the desired service producer(s).
 */
public class BrandServiceProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrandServiceProducer.class);

    private final BeanManager beanManager;

    public BrandServiceProducer(BeanManager beanManager) {
        this.beanManager = beanManager;
    }

    /**
     * Returns a CDI bean based on a {@link ContextLocals#get(String, Object)} variable.
     * In this implementation the {@value BrandFilter#BRAND_KEY} key is used.
     *
     * @param beanType The bean class to be produced.
     * @return An implementation instance of the interface.
     * @param <T> The interface representing the bean.
     */
    @SuppressWarnings("unchecked")
    protected <T> T produceBrandService(final Class<T> beanType) {
        LOGGER.debug("Producing tenant service for class: {}", beanType.getName());

        final Brand brandKey = ContextLocals.get(BrandFilter.BRAND_KEY, Brand.defaultBrand());
        Set<Bean<?>> beans = beanManager.getBeans(beanType, new AnnotationLiteral<Any>() {
        });

        for (Bean<?> bean : beans) {
            BrandService brandService = bean.getBeanClass().getAnnotation(BrandService.class);
            if (brandService != null && brandService.value() == brandKey) {
                LOGGER.debug("Produced brand service instance: {}", bean);
                return (T) beanManager.getReference(bean, beanType, beanManager.createCreationalContext(bean));
            }
        }

        throw new IllegalStateException("No suitable bean found for brand: " + brandKey);
    }
}
