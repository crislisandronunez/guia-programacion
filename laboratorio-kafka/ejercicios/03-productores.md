# 03 — Productores Java y Spring

## Objetivo

Leé las secciones 7, 13, 20 y 30–32 de la [guía](../../guias/Glosario-Kafka.md).
Separá la llamada a `send()` del resultado confirmado por Kafka.

## Consigna

Implementá primero un productor pequeño con `kafka-clients` que envíe un
`PedidoConfirmado` con clave `pedidoId` y registre tópico, partición y offset
cuando la operación termine. En la aplicación Spring, creá un servicio con
`KafkaTemplate<String, String>` y un endpoint `POST /api/pedidos/{id}/eventos`
para publicar **solo eventos del caso**. El request debe validar los datos; el
servidor genera `eventoId` y `ocurridoEn`. No permitas al cliente elegir tópico.

Hacé que la respuesta HTTP distinga al menos «aceptado por la aplicación» de
«confirmado por Kafka». Si respondés antes de la confirmación, documentá cómo
se consultará un fallo posterior. Si esperás, poné un plazo y devolvé un error
claro cuando venza sin afirmar que el registro nunca llegó.

## Experimentos y aceptación

- Con Kafka activo, el futuro termina con metadatos y un consumidor de consola
  encuentra el mismo `eventoId` y la misma clave.
- Con Kafka detenido, el endpoint no responde éxito de publicación; registrás
  la excepción y el tiempo que tardó.
- Un timeout se trata como resultado incierto, no como demostración de ausencia.
- Comparás `acks=all` e idempotencia del productor con la deduplicación de
  efectos de negocio, que se implementará más adelante.
- Añadís pruebas del contrato del endpoint y de publicación con broker de prueba.

## Para explicar

Si el productor recibe una excepción tras enviar, ¿cuándo sería seguro repetir
el intento y qué datos necesitás conservar?

Siguiente: [04 — Grupos y offsets](04-grupos-y-offsets.md).
