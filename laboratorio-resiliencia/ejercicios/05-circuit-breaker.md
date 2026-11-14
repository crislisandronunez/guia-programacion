# 05 — Circuit Breaker y recuperación

## Lectura

Secciones 6, 11 de la [guía](../../guias/Glosario-Reciliencia-Programacion.md).

## Consigna

Configurá un circuito por dependencia, con ventana por cantidad, mínimo de muestras,
umbrales de fallo y lentitud, tiempo abierto y cupo de prueba. Para un escenario inicial
usá ventana 4, mínimo 4 y umbral de fallos 50%; separá las pruebas de lentitud.

Observá eventos y llamadas del simulador. Clasificá las excepciones que realmente
emite tu adaptador, no solo las causas internas de red. Diferenciá ignorar una
excepción de no incluirla entre las registradas como fallo.

## Experimentos y aceptación

- Antes del mínimo de muestras no abre por porcentaje de fallos.
- Con cuatro muestras y dos fallos abre en el escenario acordado.
- OPEN rechaza sin incrementar las llamadas al proveedor.
- HALF_OPEN limita las pruebas y vuelve a CLOSED u OPEN según sus resultados.
- Identificás si la transición requiere una nueva llamada o el monitoreo automático configurado.
- Llamadas lentas exitosas pueden contar para el umbral de lentitud.
- Un error de negocio ignorado no degrada la salud medida del proveedor.

## Entrega

Código, pruebas y evidencia en `ejercicios/respuestas/05.md`.
Agregá solicitudes Postman cuando corresponda y anotá límites de lo demostrado.

## Para explicar

¿Por qué la ventana del circuito no limita por sí sola la concurrencia?

Siguiente: [Aislamiento y colas acotadas](06-bulkhead.md).

