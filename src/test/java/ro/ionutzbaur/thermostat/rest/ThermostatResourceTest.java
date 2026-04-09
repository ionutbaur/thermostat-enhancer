package ro.ionutzbaur.thermostat.rest;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.core.Form;
import org.apache.http.HttpStatus;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import ro.ionutzbaur.thermostat.datasource.tado.entity.auth.DeviceAuthorizationResponse;
import ro.ionutzbaur.thermostat.datasource.tado.service.TadoAuthService;
import ro.ionutzbaur.thermostat.interceptor.filter.BrandFilter;
import ro.ionutzbaur.thermostat.model.auth.AuthInitiateResponse;
import ro.ionutzbaur.thermostat.model.auth.AuthInitiateType;
import ro.ionutzbaur.thermostat.model.enums.Brand;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * This will test that for a given brand provided on header,
 * the correct implementation of {@link ro.ionutzbaur.thermostat.service.ThermostatService} is called
 */
@QuarkusTest
class ThermostatResourceTest {

    @InjectMock
    @RestClient
    TadoAuthService tadoAuthService;

    private static final String AUTH_PATH = "base/auth";
    private static final String USER_INFO_PATH = "base/user-info";

    @Test
    void initiateAuthentication_TadoShouldReturn_DEVICE_AUTH() {
        when(tadoAuthService.initiateDeviceAuth(any(Form.class)))
                .thenReturn(Uni.createFrom().item(new DeviceAuthorizationResponse()));

        AuthInitiateResponse authInitiateResponse = initiateAuthentication(Brand.TADO);
        assertEquals(AuthInitiateType.DEVICE_AUTH, authInitiateResponse.getType());
    }

    @Test
    void initiateAuthentication_ZigbeeShouldReturn_CREDENTIALS_REQUIRED() {
        AuthInitiateResponse authInitiateResponse = initiateAuthentication(Brand.ZIGBEE);
        assertEquals(AuthInitiateType.CREDENTIALS_REQUIRED, authInitiateResponse.getType());
    }

    private AuthInitiateResponse initiateAuthentication(Brand brand) {
        return given()
                .when()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header(BrandFilter.BRAND_ID_HEADER, brand)
                .post(AUTH_PATH)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .as(AuthInitiateResponse.class);
    }

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