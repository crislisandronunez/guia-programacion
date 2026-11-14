# 01 — Fallos y presupuesto de tiempo

## Lectura

Secciones 1–3 de la [guía](../../guias/Glosario-Reciliencia-Programacion.md).

## Consigna

Leé el caso y dibujá dependencias críticas y secundarias. Implementá el catálogo local
y el contrato de consulta de precio sin protecciones todavía, solo en un experimento
acotado. Creá un servidor HTTP de pruebas separado con WireMock, MockWebServer u otro
stub equivalente; fijá la versión compatible en el POM. Un mock Java no prueba sockets.

Programá secuencias reproducibles de 200, 503, 400, demora y cierre de conexión.
Registrá intentos, latencia, trabajo concurrente y resultados. Fijá límite de carga,
duración máxima y criterio de detención antes de provocar saturación.

## Experimentos y aceptación

- El caso sano funciona y el catálogo no depende del proveedor.
- El simulador registra cuántas llamadas recibe y permite reiniciar su estado entre tests.
- El experimento de lentitud termina con un timeout del test, sin colgar el build.
- Distinguís fallo transitorio, permanente y resultado desconocido.
- La tabla de presupuesto incluye colas, conexión, ejecución y esperas.
- Documentás carga y entorno de toda medición; no presentás el objetivo como resultado.

## Entrega

Código, pruebas y evidencia en `ejercicios/respuestas/01.md`.
Agregá solicitudes Postman cuando corresponda y anotá límites de lo demostrado.

## Para explicar

¿Por qué agregar hilos no resuelve una dependencia cuya capacidad ya está saturada?

Siguiente: [Timeouts y cancelación](02-timeouts.md).

