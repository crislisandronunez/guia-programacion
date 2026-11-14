# Consignas de Data JPA

| Etapa | Consigna | Secciones de la guía |
|---|---|---|
| 01 | [Entidades y esquema](01-entidades.md) | 1–4, 19 |
| 02 | [Repositorios y ciclo de vida](02-repositorios.md) | 8, 15 |
| 03 | [Tipos y objetos de valor](03-tipos.md) | 3, 5 |
| 04 | [Relaciones y cascadas](04-relaciones.md) | 6 |
| 05 | [Herencia y entidades asociativas](05-herencia.md) | 4, 6–7 |
| 06 | [Consultas y proyecciones](06-consultas.md) | 9–10, 12 |
| 07 | [Paginación y filtros dinámicos](07-filtros.md) | 11, 13 |
| 08 | [Transacciones y operaciones masivas](08-transacciones.md) | 14–15 |
| 09 | [Lazy, fetch y N+1](09-rendimiento.md) | 16 |
| 10 | [Concurrencia y bloqueos](10-bloqueos.md) | 10, 14 |
| 11 | [Auditoría, borrado lógico y errores](11-auditoria.md) | 3, 17–18 |
| 12 | [Migraciones, testing e integración](12-integracion.md) | 19–21 |

## Método

Leé el [caso](../caso/tienda.md). Cada etapa entrega código, migraciones cuando correspondan,
pruebas reales, solicitudes Postman y respuestas en `ejercicios/respuestas/NN.md`.
Las funcionalidades están por resolver; las consignas sí están completas.

Antes de programar, anticipá qué SQL esperás. Después comparalo con logs y DBeaver.
No devuelvas entidades JPA desde los controllers: usá DTO y límites transaccionales
en servicios. API bajo /api; 201 al crear, 400 al validar, 404 si no existe y 409 ante
conflictos de estado, unicidad o versión. No expongas SQL o trazas internas al cliente.

No agregues @Data de Lombok a entidades sin analizar equals/hashCode/toString, proxies
y relaciones. Usá constructor protegido sin argumentos, y records para DTO.
Los tests de consultas deben hacer flush/clear para no validar solamente objetos en memoria.
Las pruebas de concurrencia necesitan dos transacciones y una barrera de inicio.

Mantendremos PostgreSQL en desarrollo y tests. Flyway controla DDL, Hibernate lo valida.
No uses ddl-auto:create para arreglar una migración defectuosa sobre datos existentes.

El recorrido cubre los conceptos principales de las 21 secciones.
@Formula, @Subselect, segundo nivel de caché, QueryDSL y estrategias alternativas de
herencia quedan como comparaciones/extensiones; no se presentan como ya implementadas.
