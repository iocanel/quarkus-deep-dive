package io.quarkiverse.gps.tracking.client.deployment;

import java.util.Map;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import io.quarkus.deployment.IsNormal;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.DevServicesResultBuildItem;
import io.quarkus.deployment.builditem.DevServicesResultBuildItem.RunningDevService;
import io.quarkus.deployment.builditem.LaunchModeBuildItem;
import io.quarkus.deployment.dev.devservices.GlobalDevServicesConfig;

public class DevServicesGpsTrackingProcessor {

    @BuildStep(onlyIfNot = IsNormal.class, onlyIf = GlobalDevServicesConfig.Enabled.class)
    public DevServicesResultBuildItem createContainer(LaunchModeBuildItem launchMode) {
        DockerImageName dockerImageName = DockerImageName.parse("iocanel/gps-tracking-grpc-service:0.0.3");
        GpsTrackingContainer container = new GpsTrackingContainer(dockerImageName).withNetwork(Network.SHARED)
                .waitingFor(Wait.forLogMessage(".*gRPC Server started.*", 1));
        container.start();
        Map<String, String> props = Map.of("quarkus.tracker.url", "https://" + container.getHost() + ":" + container.getPort());
        return new RunningDevService(GpsTrackingClientProcessor.FEATURE, container.getContainerId(), container::close, props)
                .toBuildItem();
    }

    private static class GpsTrackingContainer extends GenericContainer<GpsTrackingContainer> {
        public GpsTrackingContainer(DockerImageName image) {
            super(image);
        }

        @Override
        protected void configure() {
            withNetwork(Network.SHARED);
            addExposedPorts(9000);
        }

        public Integer getPort() {
            return this.getMappedPort(9000);
        }
    }
}
