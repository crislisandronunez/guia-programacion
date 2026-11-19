# 02 — Condiciones y rangos

## Lectura

Secciones 6, 7 y 13 de la [guía 02](../../guias/02-Glosario-Logica-de-Programacion-y-Resolucion-de-Problemas.md).

## Consigna

Implementá `static String clasificarCarga(int carga)` para una carga individual de `0..50`:

| Carga | Clasificación |
|---:|---|
| `0` | `SIN_PAQUETES` |
| `1..20` | `BAJA` |
| `21..40` | `MEDIA` |
| `41..50` | `ALTA` |

Los valores fuera de rango se rechazan con `IllegalArgumentException`. Antes del código, representá los intervalos como una tabla sin huecos ni superposiciones. Mostrá en una traza qué ramas se evalúan para `0`, `20`, `21`, `40`, `41` y `50`.

Implementá también `static boolean requiereRefuerzo(int carga)`, verdadero exactamente para cargas `41..50`. La función debe rechazar valores fuera de rango.

## Criterios de aceptación

Las pruebas cubren todos los límites de la tabla y sus vecinos: `-1`, `0`, `1`, `20`, `21`, `40`, `41`, `50` y `51`. Para cada carga válida, `requiereRefuerzo` coincide con la clasificación `ALTA`. Una carga `51` no debe clasificarse como `ALTA`.

## Entrega

Métodos, pruebas JUnit y `ejercicios/respuestas/02.md`. Explicá por qué evaluar primero `carga >= 21` haría inaccesible la rama `ALTA` si el método termina al encontrar la primera coincidencia.

Siguiente: [Bucle y acumulador](03-acumulador.md).

