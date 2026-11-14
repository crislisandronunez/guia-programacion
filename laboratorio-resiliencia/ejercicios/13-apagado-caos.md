# 13 — Probes, apagado y fallos controlados

## Lectura

Secciones 15 de la [guía](../../guias/Glosario-Reciliencia-Programacion.md).

## Consigna

Habilitá liveness/readiness y definí qué dependencias impiden atender trabajo esencial.
No agregues indiscriminadamente circuitos de proveedores secundarios a liveness.
Separá salud del proceso, disponibilidad para recibir tráfico y operación degradada.

Prepará un experimento de caída y recuperación con stub HTTP; para infraestructura,
usá detención controlada de los contenedores propios o Toxiproxy en tests.
Comprobá apagado normal con una petición activa, ejecutores y consumidor con mensaje en vuelo.

## Experimentos y aceptación

- Proveedor de precios caído no fuerza reinicios si el proceso sigue sano.
- Readiness cambia según capacidad real de atender el contrato crítico.
- Al quitar el fallo, el sistema recupera servicio sin tormenta de reintentos.
- Apagado normal deja terminar o interrumpe dentro del tiempo máximo documentado.
- Trabajo aceptado durable queda recuperable; tareas solo en memoria no se presentan como durables.
- Un kill forzado no se supone equivalente a graceful shutdown.
- El experimento tiene hipótesis, carga máxima, criterio de aborto y restauración.

## Entrega

Código, pruebas y evidencia en `ejercicios/respuestas/13.md`.
Agregá solicitudes Postman cuando corresponda y anotá límites de lo demostrado.

## Para explicar

¿Cómo un health que incluye todos los servicios externos podría provocar una caída en cascada?

Siguiente: [Integración y defensa de decisiones](14-integracion.md).

