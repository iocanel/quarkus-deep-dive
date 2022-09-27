package io.quarkiverse.gps.tracking.client.it;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.inject.Inject;

import org.acme.tracker.client.TrackerClient;
import org.amce.tracker.Tracker.Position;
import org.junit.jupiter.api.Test;

import com.google.protobuf.Empty;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

@QuarkusTest
public class GpsTrackingClientResourceTest {

    @Inject
    TrackerClient trackerClient;

    @Test
    public void testService() {
        Multi<Position> positions = trackerClient.gps().track(Empty.getDefaultInstance());
        assertNotNull(positions);
        Uni<Position> first = positions.collect().first();
        Position p = first.await().indefinitely();
        assertNotNull(p.getLatitude());
        assertNotNull(p.getLongitude());
    }
}
