# 06 — Contrato, JSON y evolución

## Objetivo

Leé las secciones 5, 17 y 31 de la [guía](../../guias/Glosario-Kafka.md).
Tratá el evento como contrato entre servicios, no como serialización de una
entidad JPA interna.

## Consigna

Definí el contrato `PedidoConfirmado` versión 1 del [caso](../caso/pedidos.md).
Validá `tipo`, `version`, `eventoId`, `pedidoId` y `ocurridoEn` en el consumidor.
Rechazá una clave Kafka que no coincida con `pedidoId`. Prepará tres mensajes:
uno válido, uno con JSON mal formado y otro con versión desconocida.

Agregá una versión 2 con el campo opcional `canal`. Demostrá con pruebas si el
consumidor anterior puede leerla. Si no puede, diseñá el orden de despliegue y
la política de compatibilidad. Compará JSON como `String` con serializador
tipado de Spring Kafka; si experimentás con el tipado, documentá cabeceras de
tipo, paquete confiable y qué pasa ante fallo de deserialización.

## Experimentos y aceptación

- El mismo `eventoId` mantiene el mismo significado al pasar por JSON, Kafka y SQL.
- Un mensaje inválido no parece «procesado con éxito» por capturar y ocultar el error.
- La prueba de versión 2 incluye consumidor viejo y nuevo, no solo dos DTOs.
- Documentás qué campos y cabeceras se guardan para investigar un mensaje fallido.
- No publicás secretos ni una entidad persistente completa como payload.

## Para explicar

¿Por qué cambiar la clave del evento puede afectar el orden aunque el JSON siga
siendo compatible? ¿Cuándo justificarías usar un registro de esquemas?

Siguiente: [07 — Errores y DLT](07-errores-y-dlt.md).
