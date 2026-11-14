package com.estudio.mongo;

import java.util.UUID;

import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers(disabledWithoutDocker = true)
class LaboratorioMongoApplicationTest {

    @Container
    static final MongoDBContainer MONGO = new MongoDBContainer("mongo:8.0");

    @DynamicPropertySource
    static void configurarMongo(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> MONGO.getReplicaSetUrl("mongo_test"));
    }

    @Autowired
    private TestRestTemplate cliente;

    @Autowired
    private MongoTemplate mongo;

    @Test
    void aplicacionYMongoEstanDisponibles() {
        var respuesta = cliente.getForEntity("/actuator/health", String.class);
        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).contains("\"status\":\"UP\"");
    }

    @Test
    void documentoConservaCamposAnidados() {
        String id = UUID.randomUUID().toString();
        var coleccion = mongo.getCollection("verificacion");
        try {
            coleccion.insertOne(new Document("_id", id)
                    .append("direccion", new Document("ciudad", "La Plata")));
            var guardado = coleccion.find(new Document("_id", id)).first();
            assertThat(guardado).isNotNull();
            assertThat(guardado.get("direccion", Document.class).getString("ciudad"))
                    .isEqualTo("La Plata");
        } finally {
            coleccion.deleteOne(new Document("_id", id));
        }
    }
}
