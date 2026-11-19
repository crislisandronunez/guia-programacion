# 07 — Mayor sin valor inventado

## Lectura

Secciones 5, 9 y 13 de la [guía 02](../../guias/02-Glosario-Logica-de-Programacion-y-Resolucion-de-Problemas.md).

## Consigna

Implementá `static OptionalInt mayorCarga(int[] cargas)`. Si hay turnos, devuelve la carga mayor. Si el arreglo está vacío, devuelve `OptionalInt.empty()`. Rechazá `null` o cualquier carga fuera de `0..50`.

El valor `0` es una carga válida, por lo que no puede usarse como señal de ausencia. Mostrá la evolución del mejor candidato para `{12, 0, 45, 20}` y justificá su estado inicial.

## Criterios de aceptación

| Cargas | Resultado |
|---|---|
| `{}` | Vacío |
| `{0}` | Presente con valor `0` |
| `{12, 0, 45, 20}` | Presente con valor `45` |
| `{50, 50}` | Presente con valor `50` |

`null`, `{-1}` y `{10, 51}` producen `IllegalArgumentException`. Las pruebas distinguen claramente entre `OptionalInt.empty()` y `OptionalInt.of(0)`.

## Entrega

Método, pruebas JUnit y `ejercicios/respuestas/07.md`. Explicá el significado del candidato después de procesar los primeros `i` turnos.

Siguiente: [Resumen de un día](08-resumen.md).

