# 12 — Token Bucket y ventanas de tasa

## Lectura

Secciones 12 de la [guía](../../guias/Glosario-Tipos-de-Algoritmos-Programadores.md).

## Consigna

Implementá Token Bucket con fuente de tiempo monótono inyectable, capacidad, tasa y
consumo atómico. Compará Fixed Window y Sliding Window Log exacta para un cliente.
Definí intervalo (t-W,t], qué eventos se contabilizan y cuándo se elimina estado inactivo.

La Sliding Window Counter ponderada queda como comparación aproximada; no la presentes
como equivalente exacta al registro de timestamps.

## Experimentos y aceptación

- Ráfaga inicial consume capacidad; recarga nunca la supera.
- Tiempo detenido y fracciones de período tienen comportamiento definido.
- Avance de reloj largo no desborda cálculos.
- Requests concurrentes no consumen más tokens disponibles.
- Fixed Window muestra la ráfaga en el borde; Sliding Log aplica su ventana exacta.
- Tiempo y memoria se analizan incluyendo limpieza y cardinalidad de clientes.
- Dos instancias locales no se presentan como un limitador global.

## Entrega

Implementación, pruebas, análisis de tiempo/memoria y respuestas en
`ejercicios/respuestas/12.md`. Incluí precondiciones, casos límite e invariantes.

## Para explicar

¿Qué propiedad necesitás coordinar para imponer una cuota entre varios procesos?

Siguiente: [Backoff y balanceo](13-jitter-balanceo.md).

