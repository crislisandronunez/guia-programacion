# 11 — Cliente HTTP y respuestas asíncronas

## Lectura

Secciones 6, 14 de la [guía](../../guias/Glosario-Springboot-web.md).

## Consigna

Implementá recomendaciones con RestClient y servidor stub local independiente.
Configurá URL por propiedad, timeouts finitos y traducción de DTO remoto.
No permitas que el request elija cualquier URL. Probá éxito, error, demora y JSON inválido.

Para observar MVC asíncrono, creá un reporte local con Callable o DeferredResult,
ejecutor acotado, timeout y limpieza. Como extensión, emití un número finito de eventos
con SseEmitter; no agregues un flujo infinito sin control.

## Experimentos y aceptación

- El cliente envía ruta/parámetros esperados y no filtra DTO del proveedor.
- 503,504 y 502 corresponden a los escenarios del caso.
- La prueba de demora usa conexión HTTP real local.
- Asincronía MVC no se presenta como I/O completamente no bloqueante.
- Se verifica asyncDispatch en MockMvc y un escenario con servidor real.
- Timeout/desconexión no dejan tareas o emitters sin liberar.
- Una respuesta ya iniciada no intenta cambiar después a otro status JSON.

## Entrega

Código, pruebas, solicitudes Postman y respuestas en
`ejercicios/respuestas/11.md`. Para vistas, agregá evidencia del comportamiento en navegador.

## Para explicar

¿Por qué devolver CompletableFuture no garantiza que la operación interna deje de bloquear?

Siguiente: [Configuración y seguridad web](12-config-seguridad.md).

