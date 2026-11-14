# 10 — Ejecución asíncrona y tareas programadas

## Lectura

Secciones 14 de la [guía](../../guias/Glosario-Spring-Core-Boot-Core.md).

## Consigna

Agregá @EnableAsync y un ejecutor con nombre, pool y cola acotados. Generá un reporte
de cantidades mediante CompletableFuture desde un bean invocado externamente.
Usá una instantánea consistente de los avisos y documentá el comportamiento ante
rechazo de tareas. No modifiques el contrato síncrono del envío.

Agregá una tarea de resumen con @EnableScheduling, activada por propiedad y desactivada
por defecto. Externalizá el intervalo y compará fixedDelay, fixedRate y cron con zona
horaria. Separá la lógica de la tarea de su disparador.

## Experimentos y aceptación

- Probás que el trabajo usa el ejecutor indicado y que el futuro propaga resultados y errores.
- La prueba usa latch/barrera y timeout acotado, no esperas largas arbitrarias.
- Una cola saturada tiene un resultado observable y documentado.
- Self-invocation no activa @Async.
- La tarea no corre cuando está desactivada; su lógica se prueba sin esperar el cron.
- Al cerrar el contexto, no quedan ejecutores propios sin cerrar.
- Un test demuestra cómo se informa el error de un método @Async void.

## Entrega

Código y pruebas del ejercicio, evidencia y respuestas en `ejercicios/respuestas/10.md`.
Agregá solicitudes a Postman cuando haya comportamiento HTTP.

## Para explicar

¿Por qué @Scheduled se ejecutaría dos veces si levantás dos instancias de la aplicación?

Siguiente: [Caché local y consistencia](11-cache.md).

