# Ejercicio 04: pagos asíncronos y resultados inciertos

## Objetivo y lectura

Partí de la etapa 03. Leé las secciones 6 y 13 de
[Mundo de pagos](../../guias/Mundo-Pagos.md). Un error de red no determina el resultado
económico de una operación.

## Consigna A: simulador y recuperación

Ampliá el simulador con escenarios configurables solo en perfil `lab`:

- Aprobación inmediata y rechazo definitivo.
- Operación aceptada pero respuesta perdida por timeout.
- Fallo confirmado antes de aceptar la operación.
- Resultado pendiente que luego cambia a aprobado.
- Evento duplicado o entregado fuera de orden.

Persistí los registros remotos ficticios en tablas separadas de los pagos locales.
El simulador debe consultar únicamente sus registros, deduplicar por referencia y
conservar su resultado al reiniciar la aplicación.

Agregá `PENDING_CONFIRMATION` y guardá qué operación está pendiente (autorización,
captura, cobro o anulación), su estado previo y su referencia remota. Bloqueá nuevas
operaciones incompatibles mientras el resultado sea incierto.

Implementá una sonda que consulte por referencia y complete la operación idempotentemente.
Usá `Clock`, reintentos espaciados y un límite de intentos. Al agotarlo, dejá la operación
pendiente con marca de revisión manual. Un resultado "no encontrado" por sí solo no
demuestra rechazo. Recuperá también operaciones que quedaron en curso tras una caída.

## Consigna B: webhook seguro

Creá `POST /api/webhooks/pasarela-lab`. Para este protocolo ficticio definimos:

```json
{"eventId":"evt-001","operationId":"UUID","paymentId":"UUID","status":"CAPTURED","monto":"10000.00","moneda":"ARS"}
```

El emisor manda `X-Lab-Timestamp` (segundos Unix) y `X-Lab-Signature` (HMAC-SHA256
hexadecimal de `timestamp + "." + bodyCrudo`, codificado en UTF-8). Configurá el secreto
por variable de entorno, sin incluirlo en Git ni logs. Admití un desfase máximo de
cinco minutos y compará firmas en tiempo constante.

Verificá firma antes de procesar JSON; después validá esquema, referencia, importe,
moneda y transición. Persistí una bandeja de entrada con `eventId` único. La recepción
durable permite responder 202 y procesar luego; un duplicado válido ya aceptado devuelve
200. Un mismo ID con contenido distinto devuelve 409. Firma inválida o vencida: 401.

## Experimentos y aceptación

- Timeout después de cobrar: HTTP 202 y estado incierto; la sonda confirma una sola captura.
- Caída después del efecto remoto y antes de guardar la respuesta: recuperación sin duplicar.
- Webhook y sonda simultáneos: una transición y un efecto local.
- Evento repetido diez veces: una sola aplicación.
- Body modificado o timestamp vencido: 401 y ningún cambio de negocio.
- Evento AUTHORIZED posterior a CAPTURED: no hace retroceder el pago; queda auditado.
- Evento con monto o referencia inconsistentes: se rechaza o queda en revisión, sin acreditar.
- Reinicio con eventos pendientes: el procesamiento continúa.

Agregá a Postman solicitudes válidas e inválidas. Prepará un script auxiliar o un pre-request
script para firmar exactamente el body enviado; el secreto queda en una variable local.
Las pruebas automatizadas deben reproducir estos fallos sin depender de esperas reales.

## Para explicar

¿Por qué firmar un objeto JSON reserializado puede dar una firma distinta?
¿Qué pasaría si reenviaras el cobro a otra pasarela durante el timeout?

Siguiente: [05 — Ledger y billeteras](05-ledger-y-billeteras.md).
