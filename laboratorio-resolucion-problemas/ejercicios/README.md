# Recorrido de consignas

| Etapa | Consigna | Secciones de la guía |
|---|---|---|
| 01 | [Descubrimiento y requisitos](01-descubrimiento.md) | 1–3, 5, 15, 17 |
| 02 | [Dominio y API](02-dominio.md) | 4, 11, 16 |
| 03 | [Concurrencia e idempotencia](03-concurrencia.md) | 6 |
| 04 | [Procesos asíncronos y resiliencia](04-asincronia.md) | 7–8 |
| 05 | [Lecturas y reportes](05-reportes.md) | 9 |
| 06 | [Lotes, auditoría y conciliación](06-lotes.md) | 10–11 |
| 07 | [Outbox y consumidores](07-outbox.md) | 12 |
| 08 | [Saga y compensaciones](08-saga.md) | 12 |
| 09 | [Legado e importaciones](09-legado.md) | 13 |
| 10 | [Incidentes e integración final](10-incidentes.md) | 14–17 |

Leé el [caso](../caso/distribuidora.md) antes de comenzar.
Las funcionalidades indicadas aún no están implementadas.

## Forma de trabajo

Cada etapa entrega análisis breve, código cuando corresponda, migraciones, pruebas,
solicitudes Postman y evidencia en `ejercicios/respuestas/NN.md`.
Primero reproducí el problema; después implementá y compará resultados.
No agregues una tecnología sin justificar qué problema resuelve.

Convenciones: Java 21, UUID, fechas UTC con Instant, BigDecimal para dinero, API bajo
`/api`, DTO separados de JPA y ProblemDetail con código de negocio y correlationId.
Usá 201 para creación, 202 para procesamiento pendiente, 400 para entrada inválida,
404 para ausencia y 409 para conflicto. Documentá cualquier excepción al contrato.

Pruebas unitarias para invariantes y pruebas PostgreSQL/Testcontainers para concurrencia,
restricciones y transacciones. Para carreras usá una barrera de inicio, no Postman secuencial.
Inyectá Clock y simuladores configurables; evitá esperas reales largas.
No generes datos de prueba automáticamente fuera de un perfil lab.

Los fallos distribuidos no se resuelven con una sola anotación @Transactional.
Un evento en memoria post-commit no es entrega durable. Los ejercicios 04 y 07 hacen
explícita esa diferencia. Guardá decisiones en `decisiones/ADR-NN.md`, creados al resolverlas.
