package org.cloudfoundry.samples.music.config;

import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

@Configuration
@Profile({"cloud","local"})
public class RSocketPrometheusConfig implements EnvironmentAware {

    private static final Logger logger = LoggerFactory.getLogger(RSocketPrometheusConfig.class);

    @Value("${vcap.application.name:Spring Music}")
    private String appName;

    private String instanceId;

    @Override
    public void setEnvironment(Environment environment) {
        this.instanceId = environment.getProperty("vcap.application.instance_id:8675309jenny");
    }


    @Bean
    MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return registry -> registry.config().commonTags("app-name", appName)
                .commonTags("instance-id", instanceId);
    }
}
