//package com.example.webapp.config;
//
//
//import com.timgroup.statsd.NoOpStatsDClient;
//import com.timgroup.statsd.NonBlockingStatsDClient;
//import com.timgroup.statsd.StatsDClient;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class MetricsClient {
//    @Value("true")
//    private boolean publishMetrics;
//    @Value("localhost")
//    private String metricsServerHost;
//    @Value("8125")
//    private int metricsServerPort;
//    @Bean
//    public StatsDClient statsDClient() {
//        if (publishMetrics) {
//            return new NonBlockingStatsDClient("prefix6225", metricsServerHost, metricsServerPort);
//        }
//        return new NoOpStatsDClient();
//    }
//}
