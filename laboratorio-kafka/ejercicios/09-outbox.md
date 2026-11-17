# 09 — Outbox y publicación confiable

## Objetivo

Leé las secciones 23, 32, 35 y 41 de la [guía](../../guias/Glosario-Kafka.md).
Persistí un hecho pendiente junto al cambio de estado que lo originó.

## Consigna

Implementá `POST /api/pedidos/{id}/confirmar`. En **una transacción SQL** debe
pasar el pedido a `CONFIRMADO` y crear una fila outbox con `eventoId`, tipo,
versión, clave `pedidoId`, payload, fecha y estado pendiente. Una solicitud
repetida no crea un hecho lógico nuevo para la misma confirmación.

Un publicador separado lee filas pendientes y llama a `KafkaTemplate.send()`.
Solo marca una fila como enviada **después** de recibir confirmación Kafka.
Si funciona en más de una instancia, coordiná la toma de filas, por ejemplo
con bloqueo de filas y `SKIP LOCKED`, sin mantener una transacción SQL abierta
durante una espera larga de red. Registrá intentos y último error para
diagnóstico. Documentá cómo recuperás filas atascadas.

## Experimentos y aceptación

- Al confirmar el pedido con Kafka detenido, el pedido y la outbox quedan
  confirmados en SQL; no se pierde `E-91`.
- Al volver Kafka, el publicador entrega la fila y la marca enviada.
- Simulá caída después del envío y antes de marcar outbox: puede haber dos
  registros Kafka con `E-91`, pero la etapa 08 deja un solo efecto por grupo.
- Un fallo SQL antes del commit no deja ni pedido confirmado ni outbox aislada.
- La respuesta HTTP dice «pedido confirmado» según el commit SQL, no según
  finalización de Auditoría o Estadísticas.

## Para explicar

¿Por qué `@Transactional` alrededor de `save()` y `kafka.send()` no es una
transacción atómica entre PostgreSQL y Kafka? ¿Qué cambia si el publicador de
outbox usa CDC en lugar de consultar la tabla?

Siguiente: [10 — Transacciones Kafka](10-transacciones-kafka.md).
