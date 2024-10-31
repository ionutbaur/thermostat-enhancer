package ro.ionutzbaur.thermostat.interceptor.qualifier;

import jakarta.inject.Qualifier;
import ro.ionutzbaur.thermostat.model.enums.Brand;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * <p>
 * This annotation should be used on the bean implementation of a brand-based service.
 * </p>
 * <br>
 * Usage:
 * <pre>
 * {@code
 * @BrandService(Brand.TADO)
 * public class MyCustomerService implements CustomerService {
 * }
 * }
 * </pre>
 */

@Qualifier
@Retention(RUNTIME)
@Target({METHOD, FIELD, PARAMETER, TYPE})
public @interface BrandService {

    Brand value();
}
