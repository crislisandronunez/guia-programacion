package com.estudio.redis;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers(disabledWithoutDocker = true)
class LaboratorioRedisApplicationTest {

    @Container
    static final GenericContainer<?> REDIS =
            new GenericContainer<>("redis:7.4-alpine").withExposedPorts(6379);

    @DynamicPropertySource
    static void configurarRedis(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", REDIS::getHost);
        registry.add("spring.data.redis.port", () -> REDIS.getMappedPort(6379));
        registry.add("spring.data.redis.username", () -> "");
        registry.add("spring.data.redis.password", () -> "");
    }

    @Autowired
    private StringRedisTemplate redis;

    @Autowired
    private TestRestTemplate cliente;

    @Test
    void aplicacionYRedisEstanDisponibles() {
        var respuesta = cliente.getForEntity("/actuator/health", String.class);
        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).contains("\"status\":\"UP\"");
    }

    @Test
    void escribeLeeYExpiraUnaClave() {
        String clave = "lab:redis:test:" + UUID.randomUUID();
        try {
            redis.opsForValue().set(clave, "hola", Duration.ofSeconds(2));
            assertThat(redis.opsForValue().get(clave)).isEqualTo("hola");
            assertThat(redis.getExpire(clave, TimeUnit.MILLISECONDS)).isPositive();
            await().atMost(Duration.ofSeconds(6))
                    .untilAsserted(() -> assertThat(redis.hasKey(clave)).isFalse());
        } finally {
            redis.delete(clave);
        }
    }
}
