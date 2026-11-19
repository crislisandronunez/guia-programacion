# 06 — Todos cumplen y validación

## Lectura

Secciones 6, 8, 9 y 13 de la [guía 02](../../guias/02-Glosario-Logica-de-Programacion-y-Resolucion-de-Problemas.md).

## Consigna

Implementá `static boolean todosDentroDe(int[] cargas, int cupo)`. El resultado es verdadero si **cada** carga es menor o igual a `cupo`. `cupo` pertenece a `0..50`. El arreglo vacío devuelve `true`, porque no contiene un contraejemplo. Las reglas de validez del caso siguen aplicándose.

Después de validar el arreglo completo, el recorrido lógico puede terminar al encontrar la primera carga mayor que `cupo`. Compará el valor inicial de este patrón con el de una búsqueda de existencia.

## Criterios de aceptación

| Cargas | Cupo | Resultado |
|---|---:|---|
| `{}` | `20` | `true` |
| `{0, 20}` | `20` | `true` |
| `{0, 21}` | `20` | `false` |
| `{21, 0}` | `20` | `false` |

`{21, 51}` con cupo `20` debe dar `IllegalArgumentException`, no `false`. También se rechazan `null` y cupos fuera de rango. Probá `cupo = 0` y `cupo = 50`.

## Entrega

Método, pruebas JUnit y `ejercicios/respuestas/06.md`. Explicá la diferencia entre «todos cumplen» y «existe al menos uno», incluyendo el resultado para una secuencia vacía.

Siguiente: [Mayor sin valor inventado](07-mayor.md).

