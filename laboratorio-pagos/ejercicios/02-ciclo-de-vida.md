# Ejercicio 02: ciclo de vida de un pago

## Objetivo y lectura

Partí del ejercicio 01. Leé las secciones 4 y 5 de
[Mundo de pagos](../../guias/Mundo-Pagos.md). Modelá la diferencia entre crear una
intención, reservar un importe y capturarlo.

## Consigna

1. Agregá Flyway y su módulo PostgreSQL al POM. Creá la primera migración en
   `src/main/resources/db/migration`; mantené `ddl-auto: validate`.
2. Creá `Pago` con UUID, referencia de pedido, importe, moneda, estado, monto autorizado,
   monto capturado, fechas y versión para control de concurrencia.
3. Implementá una máquina de estados con esta tabla. Centralizá las transiciones en el
   dominio, no en asignaciones sueltas desde los controllers.

| Operación | Estado inicial | Estado final |
|---|---|---|
| Crear | No existe | CREATED |
| Autorizar con aprobación | CREATED | AUTHORIZED |
| Autorizar con rechazo | CREATED | REJECTED |
| Capturar | AUTHORIZED | CAPTURED |
| Anular autorización | AUTHORIZED | VOIDED |
| Cobro directo aprobado | CREATED | CAPTURED |
| Cobro directo rechazado | CREATED | REJECTED |

4. Creá un puerto `PasarelaPago` y un adaptador simulado con aprobación y rechazo
   deterministas. Usá tokens ficticios `tok_lab_aprobado` y `tok_lab_rechazado`.
5. Implementá los endpoints siguientes. Captura y anulación reciben sus decisiones desde
   el simulador. Una captura parcial es única y final: el resto de la autorización se libera
   según el contrato ficticio. No admitimos múltiples capturas por pago.

| Método y ruta | Entrada principal |
|---|---|
| POST `/api/pagos` | referenciaPedido, monto, moneda |
| GET `/api/pagos/{id}` | Sin body |
| POST `/api/pagos/{id}/autorizar` | paymentMethodToken |
| POST `/api/pagos/{id}/capturar` | monto |
| POST `/api/pagos/{id}/anular` | Sin body |
| POST `/api/pagos/{id}/cobrar` | paymentMethodToken |

Ejemplo de creación:

```json
{"referenciaPedido":"pedido-001","monto":"10000.00","moneda":"ARS"}
```

Un rechazo de negocio devuelve el resultado de la operación con estado `REJECTED`;
no lo conviertas en un HTTP 500. Una transición ilegal devuelve 409.

## Experimento

Creá un pago de ARS 10.000, autorizalo y capturá ARS 8.000. Consultalo desde Postman
y DBeaver. En otro pago, autorizá y anulá. Compará ambos registros y explicá por qué
la anulación no es un reembolso.

## Criterios de aceptación

- Reiniciar la aplicación conserva los pagos; arrancar con una base vacía aplica la migración.
- Cero, importes negativos, precisión extra y monedas no admitidas devuelven 400.
- Capturar sin autorización, capturar más de lo autorizado o volver a capturar devuelve 409.
- Anular un pago capturado devuelve 409. Un ID inexistente devuelve 404.
- Un pago rechazado no puede autorizarse otra vez; una nueva intención es otro recurso.
- Las pruebas cubren cada transición permitida y al menos una prohibida desde cada estado.
- Dos capturas concurrentes no dejan dos capturas locales: verificá el control de versión.
  La garantía de no duplicar efectos externos se incorporará en la etapa 03.

## Para explicar

¿CAPTURED significa que el dinero ya está depositado en el banco del comercio?
¿Dónde pondrías las reglas para que se cumplan también si una operación nace de un job?

Siguiente: [03 — Idempotencia](03-idempotencia.md).
