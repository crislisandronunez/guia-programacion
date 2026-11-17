package com.estudio.kafka;

import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers(disabledWithoutDocker = true)
class LaboratorioKafkaApplicationTest {

    @Container
    static final KafkaContainer KAFKA = new KafkaContainer("apache/kafka:4.4.0");

    @DynamicPropertySource
    static void configurarKafka(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", KAFKA::getBootstrapServers);
    }

    @Autowired
    private TestRestTemplate cliente;

    @Autowired
    private KafkaTemplate<String, String> kafka;

    @Test
    void aplicacionYBrokerEstanDisponibles() {
        var respuesta = cliente.getForEntity("/actuator/health", String.class);
        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).contains("\"status\":\"UP\"");
    }

    @Test
    void registroConservaClaveYValor() throws Exception {
        String eventoId = UUID.randomUUID().toString();
        String clave = "P-" + UUID.randomUUID();
        String valor = "{\"eventoId\":\"" + eventoId + "\",\"pedidoId\":\"" + clave + "\"}";

        Properties opciones = new Properties();
        opciones.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA.getBootstrapServers());
        opciones.put(ConsumerConfig.GROUP_ID_CONFIG, "inspector-" + UUID.randomUUID());
        opciones.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        opciones.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        opciones.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        try (KafkaConsumer<String, String> inspector = new KafkaConsumer<>(opciones)) {
            inspector.subscribe(List.of("pedidos-confirmados"));
            var metadata = kafka.send("pedidos-confirmados", clave, valor)
                    .get(10, TimeUnit.SECONDS).getRecordMetadata();
            var recibido = KafkaTestUtils.getSingleRecord(
                    inspector, "pedidos-confirmados", Duration.ofSeconds(10));

            assertThat(recibido.key()).isEqualTo(clave);
            assertThat(recibido.value()).isEqualTo(valor);
            assertThat(recibido.partition()).isEqualTo(metadata.partition());
            assertThat(recibido.offset()).isEqualTo(metadata.offset());
        }
    }
}
