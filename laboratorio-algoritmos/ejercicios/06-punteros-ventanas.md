# 06 — Dos punteros y ventanas

## Lectura

Secciones 6 de la [guía](../../guias/Glosario-Tipos-de-Algoritmos-Programadores.md).

## Consigna

Implementá cruce por dos punteros de secuencias ordenadas y búsqueda de un par de
importes que sume un objetivo usando posiciones distintas. Definí cómo resolver empates.
Después implementá la ventana temporal del caso con límites exactos y suma incremental.
Si aceptás entrada desordenada, ordenala y contabilizá ese costo.

## Experimentos y aceptación

- Dos punteros no salta grupos duplicados ni usa dos veces la misma posición.
- La ventana usa (t-10min,t], sin truncar Duration a minutos enteros.
- Eventos con el mismo timestamp se evalúan agrupados.
- Umbral exacto no dispara alerta; superarlo sí.
- Una implementación ingenua pequeña sirve de oráculo independiente.
- Probás datos vacíos, salto temporal grande y acumulación con overflow.
- Explicás por qué una ventana variable de suma basada en positividad no sirve igual con negativos.

## Entrega

Implementación, pruebas, análisis de tiempo/memoria y respuestas en
`ejercicios/respuestas/06.md`. Incluí precondiciones, casos límite e invariantes.

## Para explicar

¿Qué cambia si los eventos llegan fuera de orden en un flujo en vivo?

Siguiente: [Recursión, división y backtracking](07-recursion.md).

