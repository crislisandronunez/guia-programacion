# 08 — Greedy, memoización y programación dinámica

## Lectura

Secciones 8 de la [guía](../../guias/Glosario-Tipos-de-Algoritmos-Programadores.md).

## Consigna

Implementá asignación FIFO de saldo a facturas como regla de negocio.
Luego compará cambio mínimo greedy con DP para monedas [1,3,4] y monto 6; construí
el contraejemplo a mano. Implementá mochila 0/1 con pesos y capacidad enteros,
reconstruyendo los elementos elegidos y definiendo desempates.

## Experimentos y aceptación

- FIFO conserva dinero: aplicado más remanente coincide con la entrada.
- El contraejemplo distingue una heurística de una solución óptima.
- Mochila no reutiliza un ítem y nunca supera capacidad.
- DP y fuerza bruta coinciden en instancias pequeñas.
- Imposibilidad se distingue de solución de valor cero.
- Documentás O(n*C), memoria y carácter pseudopolinómico respecto de C.
- Rechazás capacidades que exceden el presupuesto de memoria antes de reservar la tabla.

## Entrega

Implementación, pruebas, análisis de tiempo/memoria y respuestas en
`ejercicios/respuestas/08.md`. Incluí precondiciones, casos límite e invariantes.

## Para explicar

¿Por qué no existe una complejidad única para todos los algoritmos greedy?

Siguiente: [BFS, DFS, Dijkstra y dependencias](09-grafos.md).

