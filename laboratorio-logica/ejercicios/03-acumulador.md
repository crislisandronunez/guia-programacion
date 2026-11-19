# 03 — Bucle y acumulador

## Lectura

Secciones 5, 8 y 9 de la [guía 02](../../guias/02-Glosario-Logica-de-Programacion-y-Resolucion-de-Problemas.md).

## Consigna

Implementá `static long totalPaquetes(int[] cargas)`. Validá el arreglo y todas sus cargas según el caso. Sumá una vez cada carga y devolvé el total como `long`. Usá un bucle explícito y un acumulador cuyo valor inicial tenga una justificación.

Describí el estado antes y después de cada iteración para `{0, 12, 45}`. En pseudocódigo, indicá dónde ocurre la validación y dónde se actualiza el total.

## Criterios de aceptación

| Entrada | Resultado |
|---|---:|
| `{}` | `0` |
| `{0}` | `0` |
| `{12, 45}` | `57` |
| `{50, 50, 50}` | `150` |
| `null`, `{2, -1}` o `{2, 51}` | `IllegalArgumentException` |

Las pruebas comprueban que el arreglo no cambió. El método no debe devolver el total parcial de `{2, 51}`.

## Entrega

Método, pruebas JUnit y `ejercicios/respuestas/03.md`. Explicá qué representa el acumulador después de procesar los primeros `i` turnos.

Siguiente: [Contador con umbral](04-contador.md).

