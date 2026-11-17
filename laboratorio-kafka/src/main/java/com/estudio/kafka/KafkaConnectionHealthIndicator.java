package com.estudio.kafka;

import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Component;

@Component("kafkaHealthIndicator")
public class KafkaConnectionHealthIndicator implements HealthIndicator {

    private final KafkaAdmin kafkaAdmin;

    public KafkaConnectionHealthIndicator(KafkaAdmin kafkaAdmin) {
        this.kafkaAdmin = kafkaAdmin;
    }

    @Override
    public Health health() {
        try (AdminClient client = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            if (client.describeCluster().nodes().get(3, TimeUnit.SECONDS).isEmpty()) {
                return Health.down().build();
            }
            return Health.up().build();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            return Health.down(ex).build();
        } catch (Exception ex) {
            return Health.down(ex).build();
        }
    }
}
