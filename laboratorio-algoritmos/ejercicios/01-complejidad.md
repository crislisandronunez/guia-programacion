# 01 — Complejidad y medición

## Lectura

Secciones 1, 16 de la [guía](../../guias/Glosario-Tipos-de-Algoritmos-Programadores.md).

## Consigna

Implementá búsqueda por recorrido y un cruce ingenuo sobre colecciones pequeñas.
Contá comparaciones y operaciones relevantes con n y m independientes. Después generá
datos con semilla fija para tamaños crecientes. Separá preparación de datos de ejecución.
No arranques Spring para medir un bucle.

## Experimentos y aceptación

- Documentás mejor, peor y caso esperado donde correspondan, más memoria auxiliar.
- El cruce general se expresa con n y m, no siempre n².
- Duplicar entradas muestra el crecimiento esperado del contador en el escenario elegido.
- No usás un umbral rígido de milisegundos como prueba de Big-O.
- Datasets cuadráticos tienen tamaño y tiempo máximos.
- Explicás por qué O(1) no significa una operación exacta ni latencia constante garantizada.

## Entrega

Implementación, pruebas, análisis de tiempo/memoria y respuestas en
`ejercicios/respuestas/01.md`. Incluí precondiciones, casos límite e invariantes.

## Para explicar

¿Cuándo construir un índice puede costar más que recorrer una lista una sola vez?

Siguiente: [Colecciones y contratos de igualdad](02-estructuras.md).

