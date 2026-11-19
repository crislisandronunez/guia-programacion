# 10 — Invariante y terminación

## Lectura

Secciones 8, 11 y 12 de la [guía 02](../../guias/02-Glosario-Logica-de-Programacion-y-Resolucion-de-Problemas.md).

## Consigna

Implementá `static int turnosHastaObjetivo(int[] cargas, long objetivo)`. Devuelve la **menor cantidad de turnos iniciales** cuya suma alcanza o supera `objetivo`. Si ni todos los turnos lo alcanzan, devuelve `-1`. `objetivo` debe ser mayor que `0`; las cargas siguen las reglas comunes.

Validá el arreglo completo antes de calcular el prefijo para que un dato inválido posterior no quede oculto por un retorno temprano. Escribí una invariante para el total acumulado y otra para los prefijos que ya se descartaron. Justificá por qué el bucle termina.

## Criterios de aceptación

| Cargas | Objetivo | Resultado |
|---|---:|---:|
| `{10, 0, 20}` | `1` | `1` |
| `{10, 0, 20}` | `10` | `1` |
| `{10, 0, 20}` | `11` | `3` |
| `{10, 0, 20}` | `30` | `3` |
| `{10, 0, 20}` | `31` | `-1` |
| `{}` | `1` | `-1` |

Se rechazan `objetivo = 0`, objetivos negativos, `null` y cargas inválidas. `{50, 51}` con objetivo `50` debe rechazarse, no devolver `1`.

## Entrega

Método, pruebas JUnit y `ejercicios/respuestas/10.md`. Explicá por qué el resultado para objetivo `11` es `3` aunque el segundo turno agregue cero paquetes.

Siguiente: [Corrección y costo](11-costo.md).

