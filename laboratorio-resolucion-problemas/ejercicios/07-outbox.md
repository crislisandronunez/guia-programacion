# 07 — Outbox y entrega durable

## Objetivo
Leé la sección 12 de la [guía](../../guias/Resolucion-de-Problemas.md).

## Consigna
Activá RabbitMQ con el perfil mensajeria del Compose. Agregá Spring AMQP y configuración
para localhost:5673 con las credenciales locales del README. Configurá exchanges, colas
durables y mensajes persistentes, con eventId, tipo, versión, agregado y correlationId.

En la misma transacción que aprueba el pedido, insertá PedidoAprobado en outbox.
Un relay reclama un lote con lease, publica fuera de una transacción larga y marca enviado
tras confirmación del broker. Configurá detección de mensajes no enrutados.
Si la confirmación es incierta, mantené el evento para reintento con el mismo ID.

El consumidor crea una orden de preparación: deduplicación eventId y efecto de negocio
se guardan juntos en PostgreSQL. Confirmá el mensaje después del commit.
Limitá reintentos y enviá fallos permanentes a una cola de errores inspeccionable.

## Experimentos y aceptación
- Rollback del pedido implica ausencia del evento en outbox.
- Broker apagado: el pedido persiste y el evento espera.
- Caída tras publicar antes de marcar enviado: reentrega sin segunda orden.
- Caída del consumidor después del commit antes del ACK: efecto único.
- Routing key sin destino no se marca como entrega exitosa.
- Dos relays recuperan leases abandonados y no pierden registros.
- Reprocesar cola de errores mantiene eventId y la deduplicación.

Un publisher confirm no demuestra que el consumidor terminó el trabajo:
[documentación de RabbitMQ](https://www.rabbitmq.com/docs/confirms).
No afirmes entrega exactamente una vez; probá efectos idempotentes.

Siguiente: [08](08-saga.md).
