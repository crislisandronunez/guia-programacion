# 06 — JSON, DTO y actualización parcial

## Lectura

Secciones 5–6, 11 de la [guía](../../guias/Glosario-Springboot-web.md).

## Consigna

Implementá PATCH según el contrato propio del caso: campos ausentes conservan valor,
null explícito falla, desconocidos fallan. Elegí un mecanismo que permita distinguir
ausente de null; un record con dos Strings por sí solo no expresa esa distinción.

Probá JSON de entrada y salida con ObjectMapper/@JsonTest y MockMvc.
Usá Instant ISO-8601, enums documentados y DTO separados del almacenamiento.

## Experimentos y aceptación

- PATCH de un campo no borra el otro.
- PATCH vacío no cambia fecha ni versión.
- Null explícito e intento de fijar id/version/estado se rechazan.
- PUT sigue requiriendo todos sus campos.
- Las fechas tienen zona/offset inequívoco.
- No hay ciclos ni exposición de metadata interna por serializar el modelo completo.
- Accept incompatible y Content-Type incorrecto se prueban por separado.

## Entrega

Código, pruebas, solicitudes Postman y respuestas en
`ejercicios/respuestas/06.md`. Para vistas, agregá evidencia del comportamiento en navegador.

## Para explicar

¿Por qué omitir un campo y enviarlo con null pueden representar intenciones distintas?

Siguiente: [ETag y operaciones condicionales](07-condicionales.md).

