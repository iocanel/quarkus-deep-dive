package org.acme.tracker.client;

import org.acme.tracker.runtime.TrackerConfig;
import org.amce.tracker.MutinyGpsServiceGrpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class TrackerClient implements AutoCloseable {
    private final TrackerConfig config;
    private final ManagedChannel channel;

    public TrackerClient(TrackerConfig config) {
        this.config = config;
        this.channel = ManagedChannelBuilder.forAddress(config.url.getHost(), config.url.getPort())
                .usePlaintext()
                .build();
    }

    public MutinyGpsServiceGrpc.MutinyGpsServiceStub gps() {
        return MutinyGpsServiceGrpc.newMutinyStub(channel);
    }

    @Override
    public void close() throws Exception {
        channel.shutdown();
    }
}
