package io.quarkiverse.gps.tracking.client.it;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.inject.Inject;

import org.acme.tracker.client.TrackerClient;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class GpsTrackingClientInjectionTest {

    @ConfigProperty(name = "quarkus.tracker.url")
    String trackerURL;

    @Inject
    TrackerClient trackerClient;

    @Test
    void testInjection() {
        assertNotNull(trackerURL);
        assertNotNull(trackerClient);
    }
}
