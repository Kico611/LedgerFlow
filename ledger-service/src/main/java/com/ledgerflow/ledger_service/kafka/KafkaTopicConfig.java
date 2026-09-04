package com.ledgerflow.ledger_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic ledgerEventsTopic() {
        return TopicBuilder
                .name("ledgerflow.ledger-events")
                .partitions(3)
                .replicas(1)
                .build();
    }
}