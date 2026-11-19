# 08 — Resumen de un día

## Lectura

Secciones 4, 9, 10, 12 y 18 de la [guía 02](../../guias/02-Glosario-Logica-de-Programacion-y-Resolucion-de-Problemas.md).

## Consigna

Definí `record ResumenTurnos(int cantidad, long total, OptionalInt mayor)` e implementá `static ResumenTurnos resumir(int[] cargas)`. El resumen combina tres patrones: contador de turnos, acumulador de paquetes y mejor candidato. Validá la entrada completa; el método no debe modificarla.

Podés reutilizar métodos anteriores o recorrer una sola vez. En ambos casos, documentá qué representa cada campo después de procesar un prefijo de turnos. Escribí pseudocódigo y una traza para `{0, 12, 45}` antes de implementar.

## Criterios de aceptación

| Cargas | Cantidad | Total | Mayor |
|---|---:|---:|---|
| `{}` | 0 | 0 | Vacío |
| `{0}` | 1 | 0 | 0 |
| `{0, 12, 45}` | 3 | 57 | 45 |
| `{50, 1, 50}` | 3 | 101 | 50 |

`null`, `{12, -1}` y `{12, 51}` se rechazan. Ninguna entrada inválida produce un `ResumenTurnos` parcial.

## Entrega

Record, método, pruebas JUnit y `ejercicios/respuestas/08.md`. Explicá por qué `{}` y `{0}` tienen el mismo total pero distinto número de turnos y distinto mayor.

Siguiente: [Diagnóstico de fallos](09-diagnostico.md).

