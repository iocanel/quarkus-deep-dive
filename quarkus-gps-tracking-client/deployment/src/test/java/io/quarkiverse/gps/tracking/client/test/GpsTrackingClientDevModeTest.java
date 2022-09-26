package io.quarkiverse.gps.tracking.client.test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import javax.inject.Inject;

import com.google.protobuf.Empty;

import org.acme.tracker.client.TrackerClient;
import org.amce.tracker.Tracker.Position;
import org.eclipse.microprofile.config.ConfigProvider;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.test.QuarkusDevModeTest;

public class GpsTrackingClientDevModeTest {

    @Inject
    public TrackerClient client;

    // Start hot reload (DevMode) test with your extension loaded
    @RegisterExtension
    static final QuarkusDevModeTest devModeTest = new QuarkusDevModeTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class));

    @Test
    public void writeYourOwnDevModeTest() {
        // Write your dev mode tests here - see the testing extension guide https://quarkus.io/guides/writing-extensions#testing-hot-reload for more information
        Assertions.assertTrue(true, "Add dev mode assertions to " + getClass().getName());
    }

        @Test
    public void writeYourOwnUnitTest() throws Exception {
        Optional<String> url = ConfigProvider.getConfig().getOptionalValue("quarkus.tracker.url", String.class);
        assertTrue(url.isPresent());
        Position pos = client.gps().track(Empty.getDefaultInstance()).collect().first().await().indefinitely();
        assertNotNull(pos.getLatitude());
        assertNotNull(pos.getLongitude());
    }

}
