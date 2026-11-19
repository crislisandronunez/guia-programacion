# 11 — Corrección y costo

## Lectura

Secciones 12 y 15 de la [guía 02](../../guias/02-Glosario-Logica-de-Programacion-y-Resolucion-de-Problemas.md).

## Consigna

El centro quiere saber si dos turnos distintos recibieron la **misma** cantidad de paquetes. Implementá dos soluciones con el mismo contrato:

- `static boolean hayCargaRepetidaPorPares(int[] cargas)`: comparar pares de posiciones distintas mediante recorridos anidados.
- `static boolean hayCargaRepetida(int[] cargas)`: recorrer una vez usando memoria auxiliar para recordar cargas observadas. El dominio `0..50` permite una estructura pequeña indexada por carga.

Ambas devuelven `false` para un arreglo vacío o con un solo turno. Ambas validan **todo** el arreglo antes de un posible retorno temprano. No modifiques la entrada.

Compará las soluciones por número de comparaciones y memoria adicional para una entrada de `n` turnos. Diferenciá el peor caso del caso en el que aparece una repetición al principio. No uses un límite de milisegundos como criterio de corrección.

## Criterios de aceptación

| Cargas | Resultado de ambos métodos |
|---|---|
| `{}` | `false` |
| `{0}` | `false` |
| `{0, 1, 0}` | `true` |
| `{50, 49, 48}` | `false` |
| `{50, 50}` | `true` |

Las pruebas comparan ambos métodos con los mismos datos y comprueban `null`, valores inválidos y ausencia de cambios en el arreglo. `{0, 0, 51}` se rechaza aunque la repetición aparezca antes.

## Entrega

Dos métodos, pruebas JUnit y `ejercicios/respuestas/11.md` con la comparación de costo. Explicá por qué dos implementaciones con resultados iguales pueden tener costos distintos.

Siguiente: [Integración del caso](12-integracion.md).

