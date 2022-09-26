package io.quarkiverse.gps.tracking.client.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class GpsTrackingClientResourceTest {

    @Test
    public void testHelloEndpoint() {
        given()
                .when().get("/gps-tracking-client")
                .then()
                .statusCode(200)
                .body(is("Hello gps-tracking-client"));
    }
}
