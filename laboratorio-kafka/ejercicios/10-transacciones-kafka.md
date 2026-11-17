# 10 — Transacciones dentro de Kafka

## Objetivo

Leé las secciones 13–14, 35 y 40 de la [guía](../../guias/Glosario-Kafka.md).
Identificá el alcance exacto de una transacción Kafka.

## Consigna

En un flujo **separado del outbox**, consumí `pedidos-confirmados` y generá
dos registros derivados en tópicos de salida creados para esta etapa. Usá el
grupo `transformacion-lab`. Configurá productor transaccional
con `transaction-id-prefix` único por instancia. Probá una ejecución que confirma
ambas salidas y otra que lanza una excepción entre las dos publicaciones.
Leé las salidas con `isolation.level=read_committed` y compará con lo que
observa un consumidor configurado de otro modo.

Si coordinás offsets de entrada con registros de salida dentro de Kafka, usá
el soporte transaccional del contenedor Spring y documentá la configuración.
No mezcles en la misma explicación una transacción de Kafka con la transacción
SQL de la etapa 09.

## Experimentos y aceptación

- El lector `read_committed` no ve los registros de la transacción abortada.
- La ejecución confirmada publica los dos registros derivados.
- Distinguís idempotencia del productor, transacción Kafka y deduplicación SQL.
- Dos instancias no comparten el mismo prefijo de `transactional.id`.
- Una prueba nombra explícitamente lo que **sí** y lo que **no** cubre el
  procesamiento «exactly once» del flujo.

## Para explicar

¿Un commit Kafka exitoso puede demostrar que la fila de PostgreSQL se confirmó?
¿Qué pasaría si la base se confirmó y después falló el commit Kafka?

Siguiente: [11 — Operación](11-operacion.md).
