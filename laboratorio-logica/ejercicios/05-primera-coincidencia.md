# 05 — Primera coincidencia

## Lectura

Secciones 8, 9, 11, 12 y 17 de la [guía 02](../../guias/02-Glosario-Logica-de-Programacion-y-Resolucion-de-Problemas.md).

## Consigna

Implementá `static int primerTurnoDesde(int[] cargas, int minimo)`. Devuelve la **primera posición** cuya carga es mayor o igual a `minimo`, o `-1` si no existe. `minimo` pertenece a `0..50`. Validá el arreglo completo antes de buscar para que una carga inválida al final no quede oculta por una coincidencia al principio.

Usá un recorrido por índices. Escribí pseudocódigo y una traza para `{10, 41, 45}` con `minimo = 40`. La traza debe mostrar en qué momento termina la búsqueda.

## Criterios de aceptación

| Cargas | Mínimo | Resultado |
|---|---:|---:|
| `{10, 41, 45}` | `40` | `1` |
| `{41, 10, 41}` | `40` | `0` |
| `{10, 20}` | `40` | `-1` |
| `{}` | `0` | `-1` |

Se rechazan `null`, cargas fuera de `0..50` y mínimos fuera de ese rango. En particular, `{41, 51}` con mínimo `40` debe producir error, no posición `0`.

## Entrega

Método, pruebas JUnit y `ejercicios/respuestas/05.md`. Explicá por qué el retorno temprano es correcto solo después de validar todo el arreglo y por qué la posición encontrada es la primera.

Siguiente: [Todos cumplen y validación](06-todos-cumplen.md).

