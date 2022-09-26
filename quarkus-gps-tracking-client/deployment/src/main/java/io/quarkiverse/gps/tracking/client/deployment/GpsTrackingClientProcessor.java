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
