package org.acme.tracker.client;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import org.acme.tracker.runtime.TrackerConfig;

import io.quarkus.arc.DefaultBean;

@ApplicationScoped
public class TrackerClientProducer {

    private TrackerClient client;

    @Produces
    @DefaultBean
    @Singleton
    public TrackerClient create(TrackerConfig config) {
        this.client = new TrackerClient(config);
        return this.client;
    }

    @PreDestroy
    public void destroy() {
        if (client != null) {
            try {
                client.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
