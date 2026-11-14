# 05 — Ordenamiento y Comparator

## Lectura

Secciones 5 de la [guía](../../guias/Glosario-Tipos-de-Algoritmos-Programadores.md).

## Consigna

Implementá el orden de despacho del caso con Comparator. Escribí MergeSort estable
como ejercicio y comparalo con la ordenación JDK. Usá elementos etiquetados para verificar
estabilidad, distinguiéndola del desempate explícito por ID.
QuickSort manual queda como extensión con selección de pivote y casos adversos.

## Experimentos y aceptación

- El resultado respeta todos los criterios y conserva el multiconjunto de entrada.
- Se cumple antisimetría y transitividad para ejemplos generados.
- No comparás long restando ni convirtiendo la diferencia a int.
- La variante estable conserva orden relativo de empates.
- Definís si ordenás una copia o modificás la entrada.
- Medís ordenado, inverso, aleatorio y muchos empates.
- No generalizás un algoritmo interno del JDK a toda implementación de List.

## Entrega

Implementación, pruebas, análisis de tiempo/memoria y respuestas en
`ejercicios/respuestas/05.md`. Incluí precondiciones, casos límite e invariantes.

## Para explicar

¿Puede un comparador incorrecto romper un algoritmo de ordenamiento correcto?

Siguiente: [Dos punteros y ventanas](06-punteros-ventanas.md).

