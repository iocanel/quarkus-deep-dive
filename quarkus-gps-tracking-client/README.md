# Quarkus - Gps Tracking Client

## Steps

### Generate the extension skeleton

```sh
quarkus create extension gps-tracking-client

```

### Create the gRPC module

``` sh
cd quarkus-gpc-tracking-client
quarkus create app -x grpc grpc
```

### Grab the gRPC proto file

``` sh
mkdir -p grpc/src/main/proto
curl https://raw.githubusercontent.com/iocanel/quarkus-deep-dive/main/gps-tracking-grpc-service/src/main/proto/tracker.proto -o grpc/src/main/proto/tracker.proto
```


### Add the module to the project

```sh
sed -i '/<module>runtime/a <module>grpc</module>' pom.xml
```

### Add the grpc dependency to runtime

### Add the grpc-deployment dependency to deployment

### Create the Config

```java
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
```

### Create the Client

```java
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
```

### Create the Recorder

```java
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

```

### Create the synthetic beans on compile time

``` java
package io.quarkiverse.gps.tracking.client.deployment;

import static io.quarkus.deployment.annotations.ExecutionTime.RUNTIME_INIT;

import javax.enterprise.context.ApplicationScoped;

import org.acme.tracker.client.TrackerClient;
import org.acme.tracker.client.TrackerClientRecorder;
import org.acme.tracker.runtime.TrackerConfig;

import io.quarkus.arc.deployment.SyntheticBeanBuildItem;
import io.quarkus.deployment.Capabilities;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.ExtensionSslNativeSupportBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.ServiceStartBuildItem;
import io.quarkus.deployment.builditem.ShutdownContextBuildItem;

class GpsTrackingClientProcessor {

    static final String FEATURE = "gps-tracking-client";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    @Record(RUNTIME_INIT)
    ServiceStartBuildItem registerSyntheticBeans(
            TrackerConfig runtimeConfig,
            ShutdownContextBuildItem shutdownContextBuildItem,
            TrackerClientRecorder recorder,
            Capabilities capabilities,
            BuildProducer<SyntheticBeanBuildItem> syntheticBeans,
            BuildProducer<ExtensionSslNativeSupportBuildItem> sslNativeSupport) {

        sslNativeSupport.produce(new ExtensionSslNativeSupportBuildItem(FEATURE));
        syntheticBeans.produce(
                SyntheticBeanBuildItem.configure(TrackerClient.class)
                        .scope(ApplicationScoped.class)
                        .setRuntimeInit()
                        .runtimeValue(recorder.create(runtimeConfig))
                        .done());

        return new ServiceStartBuildItem(FEATURE);
    }
}
```

### Create the DevService

``` java
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

```
