package com.ledgerflow.transaction_service.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic transferEventsTopic() {
        return TopicBuilder
                .name("ledgerflow.transfer-events")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic ledgerEventsDltTopic() {
        return TopicBuilder
                .name("ledgerflow.ledger-events.DLT")
                .partitions(3)
                .replicas(1)
                .build();
    }
}