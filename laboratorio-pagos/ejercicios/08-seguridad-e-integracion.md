# Ejercicio 08: seguridad y desafío integrador

## Objetivo y lectura

Partí de la etapa 07 y revisá las secciones 15 y 17 de
[Mundo de pagos](../../guias/Mundo-Pagos.md). Convertí los controles relevantes en
evidencia verificable. El resultado sigue siendo un laboratorio local.

## Consigna A: identidad y datos

1. Incorporá Spring Security. Para el entorno local, configurá identidades de cliente y
   operador con credenciales externas al código. Documentá cómo cargarlas en IntelliJ y
   Postman. Si usás HTTP Basic, limitá esta configuración al perfil local; fuera de él
   no deben quedar habilitadas credenciales de práctica ni acceso HTTP sin TLS.
2. Asociá pagos y billeteras a un titular. Derivalo de la identidad autenticada: un cliente
   no puede elegir otro titular en el body, consultar sus movimientos ni gastar sus fondos.
3. Restringí conciliación y revisión manual al operador. El webhook usa su firma propia,
   no las credenciales del cliente. No desactives autenticación para toda `/api`.
4. Ajustá el ámbito de idempotencia por identidad/comercio; una clave ajena no expone
   respuestas. Protegé también `GET /api/operaciones/{id}`.
5. Aceptá solo tokens ficticios o referencias de método de pago. Rechazá campos de tarjeta
   como PAN y CVV. No persistir ni registrar esos cuerpos, tokens, secretos o Authorization.

## Consigna B: controles de operación

Implementá un límite didáctico: cinco intentos de pago por usuario en una ventana de
diez minutos. El sexto devuelve 429 sin llamar a la pasarela. Consultas, webhooks y
repeticiones idempotentes no consumen nuevos intentos. Usá `Clock` y un contador seguro
ante concurrencia; documentá si su alcance es una sola instancia o varias.

Propagá `correlationId` y generá logs con operación, pago, proveedor y transición, sin
datos sensibles. Agregá indicadores consultables por el operador: pagos inciertos,
eventos pendientes e incidencias de conciliación.

Agregá al simulador un desafío de autenticación ficticio: `REQUIRES_ACTION` y una referencia
de desafío. Mientras no se confirme, no hay captura. En perfil `lab`, un mecanismo de
simulación permite aprobar o rechazar el desafío y emitir el evento correspondiente.
No implementa 3DS real ni garantiza un cambio de responsabilidad ante fraude.

## Desafío integrador

Prepará datos y una secuencia reproducible con estos casos independientes:

1. Cobro de ARS 10.000 con respuesta perdida. Repetí la solicitud, recibí el webhook
   duplicado y ejecutá la sonda: debe existir una captura y una transacción contable.
2. Dos transferencias concurrentes que juntas superen el saldo: solo una prospera.
3. Reembolso de 3.000 sobre aquel cobro, confirmación repetida y devolución posterior
   de 7.000: neto devuelto 10.000, sin posibilidad de devolver más.
4. Venta marketplace de 10.000: retención 9.000, liberación y payout con timeout resuelto
   por consulta. El retiro tiene un solo efecto.
5. Suscripción con rechazo suave y aprobación posterior: una obligación pagada, dos
   intentos identificables y un solo cobro efectivo.
6. CSV con diferencias conocidas: el reporte enumera cada diferencia sin alterar saldos.
7. Reiniciá la aplicación en medio de una operación incierta y comprobá recuperación.

## Criterios de aceptación

| Comprobación | Resultado esperado |
|---|---|
| Request sin credenciales a recurso protegido | 401 |
| Cliente intenta conciliación o recurso ajeno | 403 o 404 consistente, sin datos filtrados |
| Webhook falsificado | 401, sin efecto |
| Sexto intento en ventana | 429, sin llamada remota |
| Challenge pendiente | Sin captura ni ingreso contable |
| Perfil distinto de lab | Controles de simulación inaccesibles |
| Revisión de logs y BD | Sin datos de tarjeta, secretos ni tokens registrados |
| Cada transacción contable | Suma cero por moneda |
| Reconstrucción de saldos | Coincide con saldos materializados |
| Reinicio y reentregas | Sin efectos económicos duplicados |

Ejecutá `mvn verify` con Docker activo y comprobá que las pruebas de integración no
figuren omitidas. La colección Postman debe conservar variables locales para credenciales
y secretos, y permitir repetir el recorrido con referencias nuevas sin borrar datos previos.

## Entrega final

Creá `ejercicios/respuestas/08-informe.md` con instrucciones de reproducción, resultados,
consultas SQL de verificación y limitaciones conocidas. Para cada punto del checklist de
la guía indicá dónde se implementó, cómo se probó o por qué quedó fuera del alcance.

Explicá finalmente: ¿qué diferencia hay entre entrega repetida de un mensaje y efecto
económico duplicado? ¿Qué operaciones necesitan revisión humana? ¿Qué habría que
rediseñar antes de integrar un proveedor real?
