# 04 — Reservas y resultados inciertos

## Lectura

Secciones 2, 5, 9, 16 de la [guía](../../guias/Glosario-Reciliencia-Programacion.md).

## Consigna

Implementá el contrato de reservas y el registro idempotente del caso en memoria.
El proveedor simulado acepta una clave y conserva el resultado para repeticiones.
Probá dos requests simultáneos con la misma clave y la reutilización con otro payload.

Simulá que el proveedor descuenta stock y luego pierde la respuesta. Guardá INCIERTA,
exponé consulta y reconciliá por la misma clave. No lances la operación en un proveedor
alternativo para intentar resolver el timeout.

## Experimentos y aceptación

- Dos requests con igual clave/payload producen una sola reserva remota.
- Misma clave con otro payload devuelve 409.
- La respuesta perdida no genera otra reserva ni una confirmación inventada.
- INCIERTA devuelve 202 y permite observar el estado por Location.
- La reconciliación recupera el resultado del proveedor.
- Documentás la pérdida del registro local al reiniciar y la vigencia de deduplicación remota.
- No garantizás exactly-once mediante una caché en memoria.

## Entrega

Código, pruebas y evidencia en `ejercicios/respuestas/04.md`.
Agregá solicitudes Postman cuando corresponda y anotá límites de lo demostrado.

## Para explicar

¿Qué sucede si el proveedor no ofrece idempotencia ni consulta de resultados?

Siguiente: [Circuit Breaker y recuperación](05-circuit-breaker.md).

