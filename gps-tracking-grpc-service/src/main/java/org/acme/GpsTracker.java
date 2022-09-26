package org.acme;


import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Multi;
import org.amce.tracker.GpsService;
import org.amce.tracker.Tracker;

import javax.enterprise.context.ApplicationScoped;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@GrpcService
public class GpsTracker implements GpsService {

    @Override
    public Multi<Tracker.Position> track(com.google.protobuf.Empty request) {
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
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Stream<String> readLines(InputStream stream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return reader.lines();
    }
}
