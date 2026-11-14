# 12 — Tiempo, eventos y asincronía

## Lectura

Secciones 17–18 de la [guía](../../guias/Glosario-Testing.md).

## Consigna

Implementá cancelación con Clock y probá justo antes, exactamente a las 24 horas y
después. Agregá una notificación local asíncrona tras crear el pedido: probá por separado
publicación del evento y procesamiento con un ejecutor real, usando Awaitility o
futuros con límite. No afirmes durabilidad de ese evento en memoria.

Probá dos clientes compitiendo por el último libro con conexiones/transacciones
separadas y barrera. Probá también dos cancelaciones simultáneas.

## Experimentos y aceptación

- La frontera exacta de 24 horas rechaza la cancelación.
- Solo uno de los pedidos competidores reserva el último libro.
- Dos cancelaciones reponen stock una sola vez.
- No se comparte EntityManager entre hilos.
- Esperas tienen timeout y observan condición o excepción, no sleeps largos.
- SyncTaskExecutor puede verificar lógica, pero no demuestra concurrencia real.
- Fallo del notificador no borra un pedido confirmado.
- Se cierran ejecutores y se limpian datos incluso si falla una aserción.

## Entrega

Pruebas y código mínimo del comportamiento, más evidencia y respuestas en
`ejercicios/respuestas/12.md`. Indicá qué falla si rompés la regla que estás verificando.

## Para explicar

¿Qué carreras quedan ocultas cuando todas las tareas del test se ejecutan en el mismo hilo?

Siguiente: [Maven, cobertura y calidad de pruebas](13-ejecucion-cobertura.md).

