package com.example.webapp.config;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.statsd.StatsdMeterRegistry;
import io.micrometer.statsd.StatsdConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

    @Bean
    public MeterRegistry meterRegistry() {
        StatsdConfig statsdConfig = new StatsdConfig() {
            @Override
            public String get(String key) {
                return null;
            }

            @Override
            public String prefix() {
                return "metrics";
            }

            @Override
            public String host() {
                return "localhost";
            }

            @Override
            public int port() {
                return 8125;
            }
        };

        return new StatsdMeterRegistry(statsdConfig, Clock.SYSTEM);
    }
}
