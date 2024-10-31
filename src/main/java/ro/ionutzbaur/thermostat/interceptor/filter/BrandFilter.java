package ro.ionutzbaur.thermostat.interceptor.filter;

import io.quarkus.runtime.util.StringUtil;
import io.quarkus.vertx.web.RouteFilter;
import io.smallrye.common.vertx.ContextLocals;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.ionutzbaur.thermostat.model.enums.Brand;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * An HTTP filter designed to intercept each request in order to resolve the brand supplied on header
 */
public class BrandFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrandFilter.class);

    public static final String BRAND_ID_HEADER = "X-Brand-Id";
    public static final String BRAND_KEY = "brand";

    @RouteFilter
    public void filter(RoutingContext routingContext) {
        String brand = routingContext.request().getHeader(BRAND_ID_HEADER);
        resolveBrand(brand);

        routingContext.next();
    }

    private void resolveBrand(String brand) {
        if (StringUtil.isNullOrEmpty(brand)) {
            LOGGER.debug("No brand found on header. Using default brand: {}", Brand.defaultBrand());
            ContextLocals.put(BRAND_KEY, Brand.defaultBrand());
        } else {
            LOGGER.debug("Found brand on header: {}", brand);
            Brand resolvedBrand = validateBrand(brand);
            ContextLocals.put(BRAND_KEY, resolvedBrand);
        }
    }

    private Brand validateBrand(String brand) {
        return Stream.of(Brand.values())
                .filter(brandEnum -> brandEnum.name().equalsIgnoreCase(brand))
                .findAny()
                .orElseThrow(() -> new RuntimeException("Invalid brand header supplied: " + brand + ". " +
                        "Must be one of: " + Arrays.toString(Brand.values())));
    }
}
