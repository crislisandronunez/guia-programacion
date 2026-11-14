# Ejercicio 01: representar dinero

## Objetivo

Modelar importes y calcular el neto de una venta sin perder centavos.
Primero trabajamos con Java y pruebas unitarias; este ejercicio no necesita Docker.

## Antes de programar

Leé las secciones 1, 2 y 3 de [Mundo de pagos](../../guias/Mundo-Pagos.md).
Respondé con tus palabras:

1. ¿Qué diferencia hay entre el comercio, la pasarela y el banco emisor?
2. ¿Qué diferencia hay entre autorizar, capturar y liquidar un pago?
3. ¿Por qué un importe necesita una moneda?
4. ¿Por qué `new BigDecimal("10.0").equals(new BigDecimal("10.00"))` da falso?

## Consigna

Creá el paquete `com.estudio.pagos.dinero` y un `record Dinero` con
`BigDecimal monto` y `Currency moneda`.

Para este primer ejercicio definimos estas reglas del laboratorio:

- Solo se admiten ARS y USD, ambas con dos decimales.
- Monto y moneda son obligatorios; el monto no puede ser negativo. Cero es válido.
- Normalizá la escala a dos decimales. Rechazá importes que requieran redondeo:
  `10.001` no es válido, mientras que `10.000` puede representarse como `10.00`.
- Implementá `sumar(Dinero otro)` y `restar(Dinero otro)` devolviendo un nuevo valor.
- No permitas mezclar monedas ni obtener un resultado negativo.
- Implementá `aCentavos()` con conversión exacta a `long`; detectá desbordamientos.

Luego creá un `record DesglosePago` con bruto, comisión, impuestos y neto.
Todos deben usar la misma moneda y cumplir `bruto - comisión - impuestos = neto`.
No implementes todavía un endpoint o una entidad JPA.

## Caso de estudio

Una venta ficticia de ARS 10.000,00 tiene una comisión de ARS 350,00 y unos impuestos
de ARS 150,00. El neto debe ser ARS 9.500,00. Son valores didácticos, no tasas reales.

## Criterios de aceptación

Escribí pruebas JUnit en `src/test/java/com/estudio/pagos/dinero` para comprobar:

| Entrada u operación | Resultado esperado |
|---|---|
| ARS 0.10 + ARS 0.20 | ARS 0.30 exactos |
| ARS 10.0 y ARS 10.00 | Mismo valor normalizado |
| ARS 15.50 a centavos | 1550 |
| ARS 10.001 | Rechazo por precisión |
| Monto o moneda nulos | Rechazo |
| Moneda EUR | Rechazo por moneda no admitida en este ejercicio |
| ARS -1.00 | Rechazo |
| ARS 10.00 + USD 10.00 | Rechazo por monedas distintas |
| ARS 5.00 - ARS 10.00 | Rechazo por resultado negativo |
| Importe cuyos centavos no caben en long | Rechazo por desbordamiento |
| Desglose del caso de estudio | Válido |
| Mismo desglose con neto ARS 9.499,99 | Rechazo |
| Desglose con distintas monedas | Rechazo |

## Pistas

Revisá `setScale`, `RoundingMode.UNNECESSARY`, `compareTo`, `movePointRight`
y `longValueExact`. Construí los importes de prueba desde cadenas.

## Para conversar al terminar

- ¿Por qué elegimos rechazar precisión extra en lugar de redondear silenciosamente?
- ¿Dónde definirías el redondeo si calculáramos una comisión porcentual?
- ¿Qué habría que cambiar para admitir monedas con otra cantidad de decimales?
- ¿Por qué la regla de montos no negativos necesitará revisarse al diseñar el libro mayor?

El ejercicio está terminado cuando las pruebas pasan y podés explicar esas decisiones.
