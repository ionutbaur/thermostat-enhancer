package ro.ionutzbaur.thermostat.util;

import io.smallrye.common.vertx.ContextLocals;
import ro.ionutzbaur.thermostat.interceptor.filter.BrandFilter;
import ro.ionutzbaur.thermostat.model.enums.Brand;

public class RequestHelper {

    private RequestHelper() {
        // helper utility class
    }

    public static Brand getBrand() {
        return ContextLocals.get(BrandFilter.BRAND_KEY, Brand.defaultBrand());
    }
}
