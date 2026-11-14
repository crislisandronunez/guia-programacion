# 03 — Retry, backoff y jitter

## Lectura

Secciones 5, 11 de la [guía](../../guias/Glosario-Reciliencia-Programacion.md).

## Consigna

Agregá Retry a la lectura de cotizaciones mediante bean invocado desde otro bean.
Configurá tres intentos totales, clasificación explícita de errores, backoff exponencial
y jitter acotado. Elegí una configuración o IntervalFunction soportada por 2.3.0 y
verificá los intervalos reales; no presupongas que combinar dos flags produce el jitter esperado.

Reintentá fallos técnicos elegidos, no validación ni ausencia de producto.
Tratamiento de 429 y Retry-After depende del contrato del proveedor y del presupuesto.
Evitá sumar retries en cliente HTTP, servicio y gateway.

## Experimentos y aceptación

- Secuencia 503,503,200 produce tres llamadas y un resultado exitoso.
- Un 400 o SKU ausente produce una sola llamada.
- Fallo persistente termina al agotar intentos o presupuesto, lo que ocurra primero.
- Tres intentos generan como máximo dos esperas.
- Jitter se prueba por límites o fuente controlada, no comparando tiempos exactos aleatorios.
- Mostrás que self-invocation evita el aspecto.
- Una operación rechazada por saturación local no entra en un bucle automático de reintentos.

## Entrega

Código, pruebas y evidencia en `ejercicios/respuestas/03.md`.
Agregá solicitudes Postman cuando corresponda y anotá límites de lo demostrado.

## Para explicar

¿Cuántas llamadas remotas podrías generar si tres capas hicieran tres intentos cada una?

Siguiente: [Reservas y resultados inciertos](04-idempotencia.md).

