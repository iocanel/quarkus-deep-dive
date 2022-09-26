package org.acme.tracker.runtime;

import java.net.URL;

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

@ConfigRoot(name = "tracker", phase = ConfigPhase.RUN_TIME)
public class TrackerConfig {

    /**
     * The tracker service url.
     */
    @ConfigItem
    public URL url;
}
