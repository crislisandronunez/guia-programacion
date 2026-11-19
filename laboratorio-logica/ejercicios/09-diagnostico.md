# 09 — Diagnóstico de fallos

## Lectura

Secciones 11, 13 y 14 de la [guía 02](../../guias/02-Glosario-Logica-de-Programacion-y-Resolucion-de-Problemas.md).

## Consigna

El siguiente código **deliberadamente defectuoso** intenta contar turnos cuya carga llega a un mínimo. Está solo en este enunciado; no forma parte del proyecto compilable.

```java
static int contarDesdeConErrores(int[] cargas, int minimo) {
    int cantidad = 1;
    for (int i = 0; i <= cargas.length; i++) {
        if (cargas[i] > minimo) {
            cantidad++;
        }
    }
    return cantidad;
}
```

Identificá un contraejemplo pequeño para cada defecto que encuentres. Registrá entrada, salida esperada, salida real o excepción, primera divergencia de la traza y causa. Luego implementá una versión corregida que cumpla **el contrato de la etapa 04**, incluyendo toda su validación. Conservá tu método de la etapa 04 si ya es correcto; esta etapa se centra en diagnosticar y explicar.

## Criterios de aceptación

- `{}` con mínimo `0` devuelve `0` sin acceder a una posición inexistente.
- `{20}` con mínimo `20` devuelve `1`.
- `{19}` con mínimo `20` devuelve `0`.
- `null`, cargas fuera de rango y mínimo fuera de `0..50` se rechazan como indica el contrato.
- Las pruebas de regresión conservan los contraejemplos que revelaron cada error.

## Entrega

Pruebas de regresión y `ejercicios/respuestas/09.md` con las trazas. Explicá la diferencia entre corregir un síntoma y corregir la primera decisión equivocada.

Siguiente: [Invariante y terminación](10-invariante.md).

