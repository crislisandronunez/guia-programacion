package com.estudio.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicsConfig {

    @Bean
    NewTopic pedidosConfirmados() {
        return TopicBuilder.name("pedidos-confirmados")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    NewTopic pedidosConfirmadosDlt() {
        return TopicBuilder.name("pedidos-confirmados-dlt")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
