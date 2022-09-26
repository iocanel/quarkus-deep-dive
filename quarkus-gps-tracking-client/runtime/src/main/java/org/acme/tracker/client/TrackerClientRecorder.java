package org.acme.tracker.client;

import org.acme.tracker.runtime.TrackerConfig;

import io.quarkus.runtime.RuntimeValue;
import io.quarkus.runtime.annotations.Recorder;

@Recorder
public class TrackerClientRecorder {

    public RuntimeValue<TrackerClient> create(TrackerConfig config) {
        TrackerClient client = new TrackerClient(config);
        return new RuntimeValue<>(client);
    }

}
