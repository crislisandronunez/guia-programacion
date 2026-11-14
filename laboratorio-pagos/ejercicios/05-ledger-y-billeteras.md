# Ejercicio 05: libro mayor y transferencias

## Objetivo y lectura

Completá la etapa 04 y leé las secciones 8 y 9 de
[Mundo de pagos](../../guias/Mundo-Pagos.md). Cada movimiento debe dejar un registro
auditable y conservar el valor total.

## Consigna A: contabilidad

Creá cuentas, transacciones contables y apuntes inmutables. Cada transacción tiene una
referencia de negocio única, una moneda y al menos dos apuntes. En este modelo didáctico
usaremos importes firmados: positivo aumenta la posición de una cuenta y negativo la
disminuye. La suma de los apuntes de cada transacción debe ser cero por moneda.

Los apuntes no reutilizan la restricción de monto no negativo de `Dinero`: representá
importe absoluto más dirección, o un tipo específico de importe firmado.
Una corrección se registra como otra transacción compensatoria que referencia la original;
no se editan ni borran apuntes anteriores.

Registrá un cobro capturado con un apunte positivo en la cuenta del comercio y uno
negativo en una cuenta de contrapartida PSP. Autorizaciones y rechazos no crean ingresos.
El estado CAPTURED y sus apuntes deben confirmarse atómicamente en la misma transacción
local, incluso si la confirmación llega por sonda o webhook.

## Consigna B: billeteras

Implementá:

| Método y ruta | Función |
|---|---|
| POST `/api/billeteras` | Crear billetera con titular y moneda, saldo inicial cero |
| GET `/api/billeteras/{id}` | Consultar saldo |
| GET `/api/billeteras/{id}/movimientos` | Consultar movimientos paginados |
| POST `/api/transferencias` | Enviar monto entre origen y destino |

La transferencia recibe `origenId`, `destinoId`, `monto` y `moneda`, además de
`Idempotency-Key`. Rechazá mismo origen y destino, monedas distintas, importes no positivos
y saldo insuficiente. Bloqueá las cuentas en un orden estable (UUID ordenados) para
reducir deadlocks; débito, crédito, saldos y apuntes pertenecen a una sola transacción.

Prepará un inicializador exclusivo del perfil `lab` con dos billeteras: Ana con ARS 1.000
y Bruno con ARS 500. La carga inicial también genera apuntes contra una cuenta de fondeo
ficticio; su referencia es única para que reiniciar no vuelva a cargar saldo.

## Experimentos y aceptación

- Transferí ARS 200 de Ana a Bruno: saldos 800 y 700, total conservado en 1.500.
- Repetí la clave: mismos resultados, sin nuevos apuntes.
- Con saldo de Ana en 1.000, ejecutá simultáneamente dos transferencias de 800 con claves
  distintas: solo una puede prosperar. La otra devuelve 409.
- Lanzá transferencias opuestas concurrentes con saldo suficiente: verificá terminación,
  conservación de fondos y ausencia de escrituras parciales.
- Inyectá un fallo entre débito y crédito: rollback completo.
- Entregá dos veces una confirmación de captura: una sola transacción contable.
- Consultá en DBeaver los apuntes agrupados por transacción y moneda: toda suma es cero.
- Reconstruí los saldos desde apuntes y comparalos con cualquier saldo materializado.

Las carreras y el rollback se prueban con PostgreSQL real en Testcontainers.
Si agregás reintentos por deadlock, limitá los intentos y conservá la misma referencia.

## Para explicar

¿Por qué no alcanza con comprobar el saldo antes de abrir la transacción?
¿Qué evidencia permite demostrar que una devolución ocurrió sin borrar el cobro original?

Siguiente: [06 — Reembolsos y conciliación](06-reembolsos-y-conciliacion.md).
