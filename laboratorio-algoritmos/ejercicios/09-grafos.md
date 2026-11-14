# 09 — BFS, DFS, Dijkstra y dependencias

## Lectura

Secciones 9 de la [guía](../../guias/Glosario-Tipos-de-Algoritmos-Programadores.md).

## Consigna

Construí listas de adyacencia para los datasets. Implementá BFS para menos saltos,
DFS con detección de ciclos dirigidos, Dijkstra para costo mínimo y Kahn para dependencias.
Incluí nodos aislados y reconstrucción del camino. Ordená vecinos cuando necesites
recorridos reproducibles, contabilizando ese trabajo.

## Experimentos y aceptación

- BFS y Dijkstra pueden elegir rutas diferentes correctamente.
- Dijkstra admite cero, rechaza negativos y evita overflow al sumar distancias.
- Camino ausente y origen igual a destino tienen contratos explícitos.
- DFS diferencia nodo terminado de nodo activo en la rama.
- Kahn detecta ciclo si no procesa todos los nodos.
- No exigís un único orden topológico si varias respuestas son válidas.
- Explicás O(V+E) de recorridos y el costo de heap de tu variante de Dijkstra.

## Entrega

Implementación, pruebas, análisis de tiempo/memoria y respuestas en
`ejercicios/respuestas/09.md`. Incluí precondiciones, casos límite e invariantes.

## Para explicar

¿Por qué elegir la arista más barata en cada paso no sustituye a Dijkstra?

Siguiente: [Top K y planificación por prioridad](10-heaps.md).

