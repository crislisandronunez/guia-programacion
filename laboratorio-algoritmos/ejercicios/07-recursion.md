# 07 — Recursión, división y backtracking

## Lectura

Secciones 7 de la [guía](../../guias/Glosario-Tipos-de-Algoritmos-Programadores.md).

## Consigna

Creá un árbol de combos y calculá su costo recursivamente y con pila explícita.
Definí que un componente compartido se cobra por cada aparición; un ciclo en la rama
es error. Compará conjunto global de visitados con conjunto de la ruta actual.

Resolvés además una asignación pequeña de turnos con backtracking: cada tarea ocupa
un turno compatible y no puede superponerse con otra. Definí máximo de tareas y nodos
explorados. Incluí una reducción divide-and-conquer sobre una lista como comparación.

## Experimentos y aceptación

- Hay caso base y cada paso progresa.
- Un nodo compartido no se confunde con un ciclo.
- La variante iterativa procesa una cadena profunda sin depender de la pila Java.
- El backtracking devuelve solución válida o ausencia demostrada en búsqueda completa.
- Agotar el presupuesto se reporta como búsqueda incompleta, no como imposible.
- La reducción respeta el contrato de combinación y detecta overflow.

## Entrega

Implementación, pruebas, análisis de tiempo/memoria y respuestas en
`ejercicios/respuestas/07.md`. Incluí precondiciones, casos límite e invariantes.

## Para explicar

¿Por qué un problema con muchas combinaciones necesita límite aunque cada paso sea simple?

Siguiente: [Greedy, memoización y programación dinámica](08-greedy-dp.md).

