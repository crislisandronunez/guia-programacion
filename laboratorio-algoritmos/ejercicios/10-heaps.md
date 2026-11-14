# 10 — Top K y planificación por prioridad

## Lectura

Secciones 10 de la [guía](../../guias/Glosario-Tipos-de-Algoritmos-Programadores.md).

## Consigna

Implementá Top K ventas por importe descendente e ID ascendente usando heap acotado.
Compará con ordenar todo. Para k=0 devolvé vacío; k negativo es inválido; k mayor que
la entrada devuelve todos. Definí que los IDs de venta deben ser únicos.

Construí un planificador didáctico con prioridad y orden de llegada para empates.
Compará prioridad estricta con una política explícita que evite inanición.

## Experimentos y aceptación

- Top K coincide con el oráculo por ordenación, incluso con importes empatados.
- El heap retiene como máximo k elementos.
- La salida se ordena explícitamente; iterar PriorityQueue no garantiza orden.
- Distinguís offer/poll de contains/remove(Object) al analizar costos.
- No mutás una prioridad dentro de la cola sin restaurar el orden.
- La política de envejecimiento no usa un comparador dependiente del tiempo sin reordenar.
- Documentás memoria O(k) y costo O(n log k) más orden final.

## Entrega

Implementación, pruebas, análisis de tiempo/memoria y respuestas en
`ejercicios/respuestas/10.md`. Incluí precondiciones, casos límite e invariantes.

## Para explicar

¿Por qué una PriorityQueue con capacidad inicial k no impone un máximo de k elementos?

Siguiente: [LRU, LFU y vencimiento](11-cache.md).

