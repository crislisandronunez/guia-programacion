# Ejercicio 06: reembolsos, disputas y conciliación

## Objetivo y lectura

Partí de la etapa 05 y leé las secciones 10 y 14 de
[Mundo de pagos](../../guias/Mundo-Pagos.md). Separá la devolución solicitada por el
comercio, la disputa iniciada externamente y las diferencias detectadas en reportes.

## Consigna A: reembolsos

Creá `POST /api/pagos/{id}/reembolsos` con monto, motivo e `Idempotency-Key`, y
`GET /api/pagos/{id}/reembolsos`. Persistí cada reembolso y su referencia remota.
Estados de la operación: PENDING, PENDING_CONFIRMATION, SUCCEEDED y REJECTED.

Reservá capacidad reembolsable al aceptar la solicitud: la suma de reembolsos confirmados
y pendientes no puede superar lo capturado. La reserva y la validación deben ser atómicas.
Un rechazo definitivo libera la reserva; un timeout la conserva hasta resolver el resultado.

El pago pasa a PARTIALLY_REFUNDED o REFUNDED solo al confirmar la devolución. Creá los
apuntes compensatorios por el importe confirmado en la misma transacción local.
Ampliá el simulador, la sonda y los webhooks para reembolsos.

## Consigna B: disputas

Modelá la disputa como recurso separado del estado del pago: OPEN, WON o LOST.
El simulador emite un evento de apertura que retiene el importe disputado y una resolución
que lo libera o confirma la pérdida. Cada efecto debe ser idempotente y dejar apuntes.
Para acotar el ejercicio admitimos una disputa por pago, sin comisión adicional; su monto
no supera lo capturado menos devoluciones y reservas. Mientras esté abierta, bloqueá
nuevos reembolsos. Si el evento es incompatible con esos límites, enviá el caso a revisión.

## Consigna C: conciliación

Implementá `POST /api/conciliaciones` para importar un CSV del simulador y
`GET /api/conciliaciones/{id}` para consultar incidencias. Definí estas columnas:

```text
referencia,moneda,bruto,comision,impuestos,neto,estado,fechaLiquidacion
```

Persistí el lote y una huella del archivo: importar el mismo reporte no duplica incidencias.
Compará por referencia, moneda, estado e importes, independientemente del orden de filas.
Usá un reporte ficticio completo para una fecha de liquidación explícita; no confundas
fecha del cobro con fecha de liquidación. Solo buscá faltantes entre pagos esperados en
esa liquidación. Este ejercicio compara sistema y PSP; agregar extracto bancario sería
una extensión de conciliación de tres fuentes.

Registrá diferencias sin corregir automáticamente saldos basándote solo en el CSV.
Una fila inválida o referencia duplicada debe quedar informada, sin sobrescribir datos.

## Experimentos y aceptación

| Caso | Resultado esperado |
|---|---|
| Captura 10.000; devoluciones 3.000 y 7.000 | Parcial y luego total |
| Reembolso adicional de 0,01 | Rechazo, sin llamada remota |
| Dos reembolsos concurrentes de 7.000 sobre captura 10.000 | Solo uno aceptado |
| Timeout de un reembolso y repetición | Misma operación y reserva, sin duplicado |
| Disputa abierta y ganada | Retención y liberación únicas |
| Disputa perdida con evento repetido | Una pérdida contabilizada |
| Reporte coincide con sistema | Cero incidencias |
| Falta local, falta remota, estado distinto, neto distinto | Cuatro incidencias identificables |
| Archivo repetido | Mismo resultado sin nuevas incidencias |

Prepará archivos CSV de ejemplo válidos e inválidos en `datos/conciliacion`, solicitudes
Postman y pruebas de importación. En DBeaver relacioná el pago, sus devoluciones,
las reservas y los apuntes contables.

## Para explicar

¿Por qué una devolución pendiente consume capacidad reembolsable?
¿Por qué un cobro que no aparece en el reporte de hoy podría liquidarse mañana?

Siguiente: [07 — Escenarios avanzados](07-escenarios-avanzados.md).
