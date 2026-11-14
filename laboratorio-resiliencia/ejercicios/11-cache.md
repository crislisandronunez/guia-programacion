# 11 — Caché caída y estampidas

## Lectura

Secciones 13 de la [guía](../../guias/Glosario-Reciliencia-Programacion.md).

## Consigna

Activá Compose cache e incorporá Redis y pruebas de integración. Definí TTL,
jitter controlable, caché negativa y límite de datos obsoletos del caso.
Contá accesos a la fuente y simulá vencimiento concurrente de una clave popular.

Implementá agrupación de cargas por clave o una solución soportada por el proveedor
de caché. Verificá el alcance real de sync=true si lo usás; no asumas bloqueo entre nodos.
Ante caída de Redis, permití leer la fuente solo dentro de su propio bulkhead y presupuesto.

## Experimentos y aceptación

- Las consultas concurrentes no producen una estampida ilimitada dentro de la instancia.
- Un SKU inexistente usa caché negativa corta; crear ese SKU invalida el negativo.
- TTL aleatorio queda dentro de límites comprobables.
- Redis caído no permite una avalancha sin límites sobre PostgreSQL/proveedor.
- Fallos de lectura, escritura e invalidación tienen políticas distintas y pruebas.
- Corrupción de serialización se hace visible; no se oculta como fallo de red exitosamente recuperado.
- La limpieza afecta solo claves lab:resiliencia: de la prueba.
- Documentás qué ocurre con dos instancias y por qué fail-open de caché no aplica a controles críticos.

## Entrega

Código, pruebas y evidencia en `ejercicios/respuestas/11.md`.
Agregá solicitudes Postman cuando corresponda y anotá límites de lo demostrado.

## Para explicar

¿Cuándo degradar hacia la fuente puede causar una caída más grande que rechazar la petición?

Siguiente: [Mensajería, DLQ y backpressure](12-mensajeria.md).

