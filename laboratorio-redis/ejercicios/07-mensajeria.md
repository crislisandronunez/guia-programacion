# 07 — Pub/Sub y Streams

## Objetivo
Leé la sección 12 de la [guía](../../guias/Glosario-Spring-Redis.md).

## Consigna A
Creá POST `/api/notificaciones` para publicar en `lab:redis:notificaciones` y un
listener que registre eventos con ID y texto ficticio. Probá un suscriptor conectado
y otro desconectado: Pub/Sub no guarda mensajes para recuperarlos después.
Referencia: [semántica de Pub/Sub](https://redis.io/docs/latest/develop/pubsub/).

## Consigna B
Creá POST `/api/eventos` que agregue eventos a un Stream. Configurá un consumer group
desde 0 para incluir eventos preexistentes, dos consumidores con nombres únicos,
XACK después del procesamiento y recuperación de pendientes con XAUTOCLAIM.

El efecto didáctico será aumentar un contador Redis. Usá Lua para registrar el eventId
procesado e incrementar en una sola operación; conservá deduplicación durante todo el
ejercicio. Esto protege ese efecto Redis, no transacciones externas arbitrarias.
Un fallo después del efecto pero antes del ACK debe permitir reentrega sin sumar otra vez.

## Experimentos y aceptación
- Un mensaje Pub/Sub emitido sin suscriptores no aparece al reconectar.
- Un evento del Stream permanece aunque no haya consumidores.
- Dos consumidores del grupo distribuyen el trabajo, sin asumir orden global de ejecución.
- Detené uno antes del ACK: XPENDING muestra el pendiente y el otro lo reclama.
- Tras reentrega, el contador tiene un solo incremento por eventId.
- Un evento inválido pasa a una dead-letter stream tras tres intentos y luego se confirma.
  Evitá duplicar la entrada de dead-letter ante una caída.
- Definí retención: no hagas trimming de eventos todavía pendientes necesarios para recuperar.

## Para explicar
¿Por qué un ACK no prueba entrega exactamente una vez? ¿Cómo cambia la garantía si
el efecto es enviar un email o actualizar una base SQL?

Siguiente: [08](08-persistencia.md).
