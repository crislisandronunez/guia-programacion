# 04 — Contador con umbral

## Lectura

Secciones 3, 7, 8 y 9 de la [guía 02](../../guias/02-Glosario-Logica-de-Programacion-y-Resolucion-de-Problemas.md).

## Consigna

Implementá `static int contarTurnosDesde(int[] cargas, int minimo)`. Devuelve cuántos turnos tienen una carga **mayor o igual** a `minimo`. `minimo` debe estar entre `0` y `50`, inclusive. Validá también el arreglo completo y no lo modifiques.

Compará este patrón con `totalPaquetes`: ambos recorren los datos, pero uno aumenta de a uno y el otro suma el valor observado. Mostrá una traza para `{0, 20, 21, 50}` con `minimo = 21`.

## Criterios de aceptación

| Cargas | Mínimo | Resultado |
|---|---:|---:|
| `{0, 20, 21, 50}` | `21` | `2` |
| `{0, 20, 21, 50}` | `0` | `4` |
| `{0, 20, 21, 50}` | `50` | `1` |
| `{}` | `21` | `0` |

`minimo = -1`, `minimo = 51`, `null` y arreglos con cargas inválidas se rechazan. Probá los valores justo debajo, en y encima del umbral.

## Entrega

Método, pruebas JUnit y `ejercicios/respuestas/04.md`. Explicá por qué `>` cambiaría el resultado para una carga igual al mínimo.

Siguiente: [Primera coincidencia](05-primera-coincidencia.md).

