# 07 — Reintentos y DLT

## Objetivo

Leé las secciones 15, 24 y 34 de la [guía](../../guias/Glosario-Kafka.md).
Separá fallos transitorios de contratos que nunca se arreglarán repitiendo.

## Consigna

Configurá `DefaultErrorHandler` con un límite de reintentos y
`DeadLetterPublishingRecoverer` para `pedidos-confirmados-dlt`. La DLT base tiene
las mismas tres particiones que el tópico original. Usá una excepción propia
para un contrato inválido y marcala como **no reintentable**. Para un fallo
transitorio simulado, permití reintentos con espera y contá los intentos.

Repetí el experimento con un deserializador tipado y
`ErrorHandlingDeserializer`, **en una configuración de prueba separada**. El
recorrido principal usa `StringDeserializer`, así que el JSON inválido falla
dentro del listener; no mezcles los dos mecanismos sin explicar dónde se
produce la excepción.

## Experimentos y aceptación

- Un error transitorio que se recupera dentro del límite no termina en DLT.
- Un error permanente termina en DLT sin reintentos inútiles.
- Un fallo de publicación en DLT no se reporta como recuperación exitosa.
- La DLT conserva el `eventoId` o los bytes originales, clave, origen, offset
  y causa suficiente para investigar.
- Agregás alerta o informe para DLT creciente; definís quién autoriza el replay.
- Una prueba demuestra qué hace el grupo al recibir un mensaje defectuoso.

## Para explicar

¿Por qué mover un evento a DLT puede cambiar los efectos del orden dentro de
su partición? ¿Qué harías antes de reinsertarlo?

Siguiente: [08 — Idempotencia SQL](08-idempotencia-sql.md).
