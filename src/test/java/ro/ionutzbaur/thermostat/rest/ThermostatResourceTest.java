package ro.ionutzbaur.thermostat.rest;

import io.quarkus.test.junit.QuarkusTest;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import ro.ionutzbaur.thermostat.interceptor.filter.BrandFilter;
import ro.ionutzbaur.thermostat.model.enums.Brand;

import static io.restassured.RestAssured.given;

/**
 * This will test that for a given brand provided on header,
 * the correct implementation of {@link ro.ionutzbaur.thermostat.service.ThermostatService} is called
 */
@QuarkusTest
class ThermostatResourceTest {

    private static final String USER_INFO_PATH = "base/user-info";

    @Test
    void getUserInfo_TadoHeader() {
        given()
                .when()
                .header(BrandFilter.BRAND_ID_HEADER, Brand.TADO)
                .get(USER_INFO_PATH)
                .then()
                .body(CoreMatchers.containsString("ro.ionutzbaur.thermostat.exception.TadoException: User is not authenticated!"));
    }

    @Test
    void getUserInfo_ZigbeeHeader() {
        given()
                .when()
                .header(BrandFilter.BRAND_ID_HEADER, Brand.ZIGBEE)
                .get(USER_INFO_PATH)
                .then()
                .body(CoreMatchers.containsString("ro.ionutzbaur.thermostat.exception.ZigbeeException: Not yet implemented for Zigbee"));
    }

    @Test
    void getUserInfo_noHeader_shouldDefaultToTado() {
        given()
                .when()
                .get(USER_INFO_PATH)
                .then()
                .body(CoreMatchers.containsString("ro.ionutzbaur.thermostat.exception.TadoException: User is not authenticated!"));
    }
}