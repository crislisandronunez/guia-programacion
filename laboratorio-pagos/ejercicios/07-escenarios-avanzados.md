# Ejercicio 07: marketplace, suscripciones y múltiples pasarelas

## Objetivo y lectura

Completá la etapa 06. Leé las secciones 11, 12 y 16 de
[Mundo de pagos](../../guias/Mundo-Pagos.md). Resolvé estas tres partes en orden,
reutilizando idempotencia, estados inciertos y contabilidad.

## Parte A: split y fondos retenidos

Creá `POST /api/ventas-marketplace` con referencia de pedido, vendedor, importe y moneda.
La venta se vincula con un pago; no crea un segundo cobro. En este escenario ficticio,
la plataforma cobra un 10 %, calculado con dos decimales y HALF_EVEN.
El vendedor recibe el resto por diferencia, para conservar los centavos.

Una captura de ARS 10.000 genera 1.000 para la plataforma y 9.000 retenidos para el
vendedor, con contrapartida PSP. Adaptá la contabilización de la etapa 05 para distribuir
el mismo ingreso: no registres primero un ingreso completo y luego otro split duplicado.

Implementá `POST /api/ventas-marketplace/{id}/liberar`: al confirmar entrega, los fondos
pasan de retenidos a disponibles. `POST /api/retiros` reserva saldo disponible y simula
un payout. Confirmación, rechazo y timeout siguen el patrón de operaciones anteriores.
Es una retención interna didáctica, sin afirmar que implementa un servicio legal de escrow.

Para esta parte admitimos devolución total antes de liberar: revierte ambas participaciones
una vez. Devoluciones parciales o posteriores a la liberación quedan fuera de alcance y
deben rechazarse explícitamente, sin generar un reembolso que deje el split inconsistente.

### Aceptación A

- La suma de participaciones coincide exactamente con el cobro, incluso para ARS 0,05.
- No se puede retirar dinero retenido ni más saldo del disponible.
- Liberar dos veces no vuelve a acreditar.
- Timeout de payout mantiene la reserva; confirmar debita una vez y rechazar la libera.
- Devolución total antes de liberar revierte ingreso y participaciones sin borrar asientos.

## Parte B: suscripciones y reintentos

Creá `POST /api/suscripciones`, `GET /api/suscripciones/{id}` y
`POST /api/suscripciones/{id}/cancelar`. Guardá importe, moneda, próximo vencimiento,
estado y un identificador ficticio de método reutilizable; no reutilices tokens de un solo uso.
Definimos períodos de 30 días exactos para simplificar el calendario del laboratorio.

El job genera una obligación única por suscripción y período. Cada intento tiene su propia
referencia estable; repetir el mismo intento conserva su clave. No abras otro intento
mientras el anterior sea incierto. El simulador distingue aprobación, SOFT_DECLINE y
HARD_DECLINE. Un rechazo suave permite dos nuevos intentos: a las 24 y 72 horas del
primero. Un rechazo definitivo detiene reintentos y marca REQUIRES_PAYMENT_METHOD.
Agotados los intentos suaves, marcá PAST_DUE; no acumules nuevos períodos pendientes
en este ejercicio. Cancelar impide nuevos intentos, pero conserva y resuelve los ya enviados.

### Aceptación B

- Dos ejecuciones concurrentes del job crean una obligación por período.
- Avanzar `Clock` permite probar vencimientos y reintentos sin esperar tiempo real.
- Un período aprobado se cobra una vez; el siguiente tiene su propia obligación.
- Un rechazo definitivo no se reintenta y un timeout no se trata como rechazo suave.
- Cancelar conserva el historial y no crea cobros futuros.

## Parte C: dos pasarelas

Agregá dos adaptadores simulados al mismo puerto y seleccioná proveedor mediante una
estrategia por moneda. Guardá proveedor e identificador remoto por intento.
Permití usar el segundo proveedor únicamente cuando el primero confirme que no aceptó
la operación y la política permita intentarlo; no ante un timeout o un 5xx ambiguo.
Un rechazo de negocio no dispara cambio automático en este laboratorio.

### Aceptación C

- Ambos adaptadores superan las mismas pruebas de contrato.
- Fallo confirmado antes de aceptación permite un intento secundario registrado.
- Timeout después de aceptación no invoca al secundario.
- Webhook y sonda identifican proveedor y operación sin confundir IDs remotos iguales.
- Las claves de idempotencia de dos proveedores no se consideran una garantía compartida.

## Entregables y preguntas

Agregá a Postman carpetas para marketplace, suscripciones y proveedores. Incluí migraciones,
pruebas de las tres partes y evidencia de saldos en DBeaver.
¿Dónde termina una obligación y empieza un intento? ¿Qué fondos puede retirar el vendedor?
¿Qué evidencia concreta habilita un cambio de pasarela?

Siguiente: [08 — Seguridad e integración](08-seguridad-e-integracion.md).
