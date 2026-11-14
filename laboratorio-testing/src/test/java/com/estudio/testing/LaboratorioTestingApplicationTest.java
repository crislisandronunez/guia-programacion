package com.estudio.testing;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LaboratorioTestingApplicationTest {

    @Autowired
    private TestRestTemplate cliente;

    @Test
    void aplicacionEstaDisponible() {
        var respuesta = cliente.getForEntity("/actuator/health", String.class);
        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isNotNull();
        assertThat(respuesta.getBody()).contains("\"status\":\"UP\"");
    }
}
