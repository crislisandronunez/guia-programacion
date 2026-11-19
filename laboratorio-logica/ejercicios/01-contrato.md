# 01 — Contrato y traza

## Lectura

Secciones 1–5, 11 y 13 de la [guía 02](../../guias/02-Glosario-Logica-de-Programacion-y-Resolucion-de-Problemas.md), más las [reglas del caso](../caso/centro-de-envios.md).

## Consigna

Definí el contrato del método `static int contarTurnos(int[] cargas)`: devuelve la cantidad de turnos registrados. El resultado depende de la longitud del arreglo, pero **antes** deben comprobarse todas las cargas. Implementalo sin modificar la entrada.

Antes de programar, escribí en `ejercicios/respuestas/01.md` la entrada admitida, la salida, las precondiciones, la postcondición y el comportamiento ante `null`, vacío y cargas fuera de rango. Agregá pseudocódigo y una traza manual de `{0, 12, 45}`.

## Criterios de aceptación

| Entrada | Resultado |
|---|---|
| `{}` | `0` |
| `{0}` | `1` |
| `{0, 12, 45}` | `3` |
| `null` | `IllegalArgumentException` |
| `{12, -1}` o `{12, 51}` | `IllegalArgumentException` |

Las pruebas verifican también que el arreglo conserve sus valores. Una implementación que devuelve directamente `cargas.length` sin validar los elementos no cumple el contrato.

## Entrega

Método, pruebas JUnit y `ejercicios/respuestas/01.md`. Explicá por qué una entrada vacía y una entrada ausente son distintas.

Siguiente: [Condiciones y rangos](02-condiciones.md).

