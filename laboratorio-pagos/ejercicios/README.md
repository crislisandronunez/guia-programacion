# Consignas del laboratorio

Construimos una tienda ficticia y luego la ampliamos con billeteras y vendedores.
Los ocho ejercicios corresponden al recorrido del [README principal](../README.md).
Cada uno parte del anterior. El código de negocio y las solicitudes de Postman indicadas
son entregables del estudiante: todavía no están implementados.

## Forma de trabajar

1. Leé las secciones indicadas de la guía y explicá el problema con tus palabras.
2. Implementá las reglas de dominio y sus pruebas antes de conectar HTTP y persistencia.
3. Agregá una migración por cambio de esquema; no edites migraciones ya aplicadas.
4. Incorporá a la colección Postman las solicitudes del ejercicio, con sus comprobaciones.
5. Inspeccioná los registros en DBeaver y ejecutá `mvn verify` con Docker activo.
6. Guardá tus respuestas y evidencias en `ejercicios/respuestas/NN.md`, sin secretos.

## Convenciones del proyecto

- Java 21, una aplicación Spring Boot y PostgreSQL. No hace falta crear microservicios.
- Paquetes por función: `dinero`, `pagos`, `pasarela`, `billeteras`, `contabilidad`, etc.
- API bajo `/api`; UUID para identificadores y fechas UTC con `Instant`.
- ARS y USD con dos decimales. Sin conversión de moneda entre operaciones.
- Importes JSON como cadenas, por ejemplo `"10000.00"`, convertidos a `BigDecimal`.
- Una operación de pago exige importe mayor que cero; `Dinero` permite cero.
- DTO separados de entidades JPA. Nunca devolver entidades directamente desde controllers.
- HTTP 201 para creación, 200 para consulta u operación finalizada, 202 para procesamiento
  pendiente, 400 para entradas inválidas, 404 para recursos inexistentes y 409 para conflictos.
- Errores consistentes con `codigo`, `mensaje` y `correlationId`; no incluir trazas internas.
- Los códigos y estados de este laboratorio son un contrato didáctico propio.

## Pruebas y entregables

Cada ejercicio entrega código, migraciones cuando correspondan, pruebas y solicitudes
Postman. Las pruebas unitarias cubren reglas; las de integración usan PostgreSQL temporal
con Testcontainers. Las carreras se prueban con tareas concurrentes y una barrera de inicio;
el Collection Runner secuencial de Postman no demuestra seguridad ante concurrencia.

Inyectá `Clock` cuando aparezcan vencimientos o reintentos. Así podés avanzar el tiempo
en pruebas sin esperar minutos. Para simular fallos, usá escenarios deterministas.

La pasarela será ficticia. Las firmas, los tokens de prueba y los escenarios de rechazo
no representan contratos oficiales de proveedores. Los controles de la etapa 8 son
prácticas de ingeniería, no una certificación para operar dinero real.

## Progreso

- [ ] [01 — Dinero](01-dinero.md)
- [ ] [02 — Ciclo de vida](02-ciclo-de-vida.md)
- [ ] [03 — Idempotencia](03-idempotencia.md)
- [ ] [04 — Webhooks y timeouts](04-webhooks-y-timeouts.md)
- [ ] [05 — Ledger y billeteras](05-ledger-y-billeteras.md)
- [ ] [06 — Reembolsos y conciliación](06-reembolsos-y-conciliacion.md)
- [ ] [07 — Escenarios avanzados](07-escenarios-avanzados.md)
- [ ] [08 — Seguridad e integración](08-seguridad-e-integracion.md)
