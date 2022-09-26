package org.acme;

import com.google.protobuf.Empty;
import io.smallrye.mutiny.Multi;
import org.amce.tracker.Tracker;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jboss.resteasy.reactive.RestSseElementType;
import org.jboss.resteasy.reactive.RestStreamElementType;

@Path("/gps")
public class GpsTracerResource {

    @GET
    @Path("track")
    @RestStreamElementType(MediaType.TEXT_PLAIN)
    public Multi<String> getPositions() {
         try {
            AtomicInteger index = new AtomicInteger();

            List<Tracker.Position> positions = readLines(Thread.currentThread().getContextClassLoader().getResourceAsStream("/tracking-data"))
                    .map(l -> Tracker.Position.newBuilder()
                            .setLatitude(Double.parseDouble(l.substring(0, l.indexOf(" "))))
                            .setLongitude(Double.parseDouble(l.substring(l.indexOf(" ") + 1)))
                            .build()).collect(Collectors.toList());

            return Multi.createFrom().ticks().every(Duration.ofSeconds(1)).map(t -> {
                int i = index.getAndAccumulate(positions.size(), (prev, limit) -> prev >= limit - 1 ? 0 : prev + 1);
                return positions.get(i);
            }).map(p-> p.getLatitude() + " - " + p.getLongitude() + "\n");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Stream<String> readLines(InputStream stream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return reader.lines();
    }
}
