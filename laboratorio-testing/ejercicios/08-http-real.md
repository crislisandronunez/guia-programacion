# 08 — Integración HTTP y estado real

## Lectura

Secciones 9, 12 de la [guía](../../guias/Glosario-Testing.md).

## Consigna

Agregá una prueba de creación y consulta con @SpringBootTest(RANDOM_PORT) y
TestRestTemplate, inicialmente con almacenamiento en memoria. Evitá reemplazar
controller, servicio y repositorio en esa prueba. Una tarifa fija de práctica es el
límite explícito del escenario.

Compará un cliente HTTP real con MockMvc. WebTestClient/RestAssured quedan como
alternativas de cliente: si las explorás, registrá dependencias necesarias y el modo
de conexión sin convertir el servidor MVC a WebFlux por accidente.

## Experimentos y aceptación

- POST seguido de GET recupera datos creados por el flujo real.
- El test usa el puerto asignado y no depende de 8091 libre.
- Cada escenario prepara y limpia su estado.
- @Transactional en el test no se presenta como rollback de requests en otro hilo.
- Se comprueban errores y ausencia de efectos, además del caso feliz.
- Explicás por qué esto no equivale a un E2E con todos los servicios externos reales.

## Entrega

Pruebas y código mínimo del comportamiento, más evidencia y respuestas en
`ejercicios/respuestas/08.md`. Indicá qué falla si rompés la regla que estás verificando.

## Para explicar

¿Por qué una prueba HTTP puede dejar datos aunque el método de test tenga rollback?

Siguiente: [JPA, PostgreSQL y Testcontainers](09-datos-containers.md).

