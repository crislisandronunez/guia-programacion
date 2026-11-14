# 02 — Timeouts y cancelación

## Lectura

Secciones 4 de la [guía](../../guias/Glosario-Reciliencia-Programacion.md).

## Consigna

Implementá el cliente HTTP con tiempos finitos de conexión y respuesta/lectura.
Verificá qué significa cada timeout para el cliente elegido, incluyendo espera de
conexión del pool. Añadí un límite total de operación que contemple las etapas restantes.

Compará con TimeLimiter sobre un tipo asíncrono soportado por Resilience4j, usando
ejecutor y scheduler administrados y acotados. No anotes un método síncrono esperando
que TimeLimiter interrumpa automáticamente un socket.

## Experimentos y aceptación

- Una respuesta lenta termina con el error contractual dentro del margen documentado.
- Conexión rechazada y timeout de conexión se identifican como casos distintos.
- Para simular conexión bloqueada usás un proxy local controlado o documentás ese experimento pendiente; no una IP pública aleatoria.
- Después del timeout medís si la tarea subyacente sigue activa.
- cancelRunningFuture no se presenta como garantía de deshacer un efecto remoto.
- Se liberan recursos; no usás el common pool para I/O bloqueante.
- La prueba comprueba comportamiento de la llamada real, no solo valores en YAML.

## Entrega

Código, pruebas y evidencia en `ejercicios/respuestas/02.md`.
Agregá solicitudes Postman cuando corresponda y anotá límites de lo demostrado.

## Para explicar

¿Puede el servidor terminar una reserva después de que tu cliente haya dejado de esperar?

Siguiente: [Retry, backoff y jitter](03-reintentos.md).

