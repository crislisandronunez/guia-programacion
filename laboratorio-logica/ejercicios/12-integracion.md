# 12 — Integración del caso

## Lectura

Repaso de las secciones 1–20 de la [guía 02](../../guias/02-Glosario-Logica-de-Programacion-y-Resolucion-de-Problemas.md) y de los contratos del laboratorio.

## Consigna

El centro necesita un informe diario. Definí:

```java
record InformeDia(
    ResumenTurnos resumen,
    int cantidadCriticos,
    int primerCritico,
    boolean sinCriticos
) {}
```

Implementá `static InformeDia evaluarDia(int[] cargas, int umbralCritico)`. El umbral pertenece a `1..50`. Un turno es crítico si su carga es **mayor o igual** al umbral. `cantidadCriticos` cuenta todos; `primerCritico` guarda la primera posición o `-1`; `sinCriticos` es verdadero exactamente cuando `cantidadCriticos == 0`. `resumen` cumple el contrato de la etapa 08.

Podés reutilizar los métodos anteriores o diseñar un recorrido integrado. Cualquiera de las dos opciones debe validar la entrada completa, conservar los mismos resultados y no modificar el arreglo. Antes de programar, escribí contrato, pseudocódigo, una traza e invariantes para los estados que uses.

## Criterios de aceptación

| Cargas | Umbral | Resumen `(cantidad, total, mayor)` | Críticos | Primero | Sin críticos |
|---|---:|---|---:|---:|---|
| `{}` | `40` | `(0, 0, ausente)` | 0 | -1 | `true` |
| `{0}` | `40` | `(1, 0, 0)` | 0 | -1 | `true` |
| `{10, 41, 45}` | `40` | `(3, 96, 45)` | 2 | 1 | `false` |
| `{50, 50}` | `50` | `(2, 100, 50)` | 2 | 0 | `false` |

Se rechazan `null`, cargas inválidas y umbrales `0` o `51`. `{41, 51}` con umbral `40` debe rechazarse por completo. Las pruebas comprueban que `sinCriticos`, `cantidadCriticos` y `primerCritico` nunca se contradicen.

## Entrega

Record, método, pruebas JUnit y `ejercicios/respuestas/12.md`. Documentá las decisiones del contrato, una traza del caso `{10, 41, 45}`, las invariantes y el costo de la solución. El laboratorio queda completo cuando todas las pruebas pasan y podés justificar los resultados para vacío, límites e inválidos.

