# 08 — Idempotencia y efectos SQL

## Objetivo

Leé las secciones 13–14, 23 y 35 de la [guía](../../guias/Glosario-Kafka.md).
Hacé que una reentrega Kafka no duplique un efecto persistido.

## Preparación

Agregá `spring-boot-starter-jdbc`, el controlador PostgreSQL y
`org.testcontainers:testcontainers-postgresql` (este último con alcance `test`,
según Testcontainers 2 del proyecto) al `pom.xml`.
Levantá el Compose con `compose.postgres.yaml` como indica el [README](../README.md).
Configurá `spring.datasource.url` para `jdbc:postgresql://localhost:5440/kafka_lab`
y credenciales de desarrollo mediante variables o perfil local. Para las pruebas,
usá un `PostgreSQLContainer`, independiente del volumen del Compose.

## Consigna

Creá una tabla de auditoría cuyo `evento_id` sea único y una tabla de proyección
de estadísticas. El listener de Auditoría inserta el evento **una sola vez**;
el de Estadísticas actualiza su proyección una sola vez. Una marca de procesado
y el cambio de la proyección deben confirmarse en una misma transacción SQL.
Usá `INSERT ... ON CONFLICT DO NOTHING`, una restricción única o un mecanismo
equivalente y comprobá el resultado de la inserción.

Si recibís el mismo `eventoId` con otro `pedidoId` o contenido incompatible,
registrá una anomalía: no ocultes el cambio bajo «ya procesado».

## Experimentos y aceptación

- Publicar dos veces `E-91` deja **una** fila de auditoría y **un** incremento
  de Estadísticas.
- Una caída provocada después del commit SQL y antes del commit de offset produce
  reentrega, pero no duplica el efecto.
- Una caída **antes** del commit SQL deja el efecto ausente y permite reintento.
- La marca y el efecto no se guardan en transacciones separadas.
- El test verifica la base real de prueba y el offset del grupo; no se conforma
  con el número de invocaciones de un mock.

## Para explicar

¿Qué garantía aporta `enable.idempotence` al productor? ¿Por qué no resuelve la
duplicación del efecto en PostgreSQL?

Siguiente: [09 — Outbox](09-outbox.md).
