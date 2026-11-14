# 05 — Errores consistentes con ProblemDetail

## Lectura

Secciones 9 de la [guía](../../guias/Glosario-Springboot-web.md).

## Consigna

Implementá @RestControllerAdvice y, si corresponde, ResponseEntityExceptionHandler.
Mapeá validación, conversión, JSON ilegible, ausencia, conflicto y fallos remotos.
Usá application/problem+json y codigo/correlationId/errores como extensiones.

Provocá un fallo inesperado controlado en tests y registrá su causa internamente.
No devuelvas mensajes crudos de excepción, rutas locales ni stacktraces al consumidor.

## Experimentos y aceptación

- El status del cuerpo coincide con el status HTTP.
- Una sintaxis JSON inválida se distingue de campos inválidos.
- 404 de recurso y 404 de ruta tienen un contrato comprensible.
- 405 conserva Allow y 415/406 conservan su semántica.
- No transformás todo Throwable en 400.
- Un error de filtro anterior al controller no se supone capturado automáticamente por el advice.
- Cada caso incluye una prueba que verifica ausencia de cambios.

## Entrega

Código, pruebas, solicitudes Postman y respuestas en
`ejercicios/respuestas/05.md`. Para vistas, agregá evidencia del comportamiento en navegador.

## Para explicar

¿Por qué responder HTTP 200 con un campo error complica a clientes, métricas y caches?

Siguiente: [JSON, DTO y actualización parcial](06-json-patch.md).

