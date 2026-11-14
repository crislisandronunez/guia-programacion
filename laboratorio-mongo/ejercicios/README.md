# Consignas de MongoDB

| Etapa | Consigna | Secciones de la guía |
|---|---|---|
| 01 | [Documentos, BSON e identificadores](01-documentos.md) | 1–4 |
| 02 | [Embebidos y referencias](02-modelado.md) | 5 |
| 03 | [Índices, unicidad y expiración](03-indices.md) | 6 |
| 04 | [Repositorios, operadores y proyecciones](04-consultas.md) | 7–9, 13 |
| 05 | [MongoTemplate y filtros paginados](05-template.md) | 10, 13 |
| 06 | [Actualizaciones parciales y atomicidad](06-updates.md) | 11 |
| 07 | [Pipelines y reportes](07-agregaciones.md) | 12–13 |
| 08 | [Auditoría, conversores y evolución](08-auditoria.md) | 14, 18 |
| 09 | [Versiones y transacciones multi-documento](09-transacciones.md) | 15 |
| 10 | [Consultas geoespaciales](10-geo.md) | 16 |
| 11 | [Archivos con GridFS](11-gridfs.md) | 17 |
| 12 | [Testing, diagnóstico y recorrido final](12-integracion.md) | 19–20 |

## Forma de trabajo

Leé el [caso](../caso/catalogo.md). Cada etapa entrega código, tests, solicitudes Postman,
comandos mongosh y respuestas en ejercicios/respuestas/NN.md.
La base incluye solo infraestructura y verificación; los ejercicios no están resueltos.

Java 21 y API /api. DTO separados de documentos, cantidades positivas, importes Decimal128,
fechas Instant y límites de tamaño/paginación explícitos.
HTTP 201 creación, 400 entrada inválida, 404 ausencia, 409 conflicto y 503 indisponibilidad.
Los tests de carreras usan barreras y tareas concurrentes, no Postman secuencial.

Índices y validadores son cambios versionados y repetibles. No borres colecciones para
arreglar un cambio de esquema. Usá exclusivamente datos ficticios.
Las pruebas deben verificar BSON persistido, no solamente objetos Java.

Un TTL elimina de forma eventual; el vencimiento funcional se valida en la aplicación.
Referencia: [índices TTL](https://www.mongodb.com/docs/manual/core/index-ttl/).
Las anotaciones de auditoría y versión no sustituyen comprobar el comportamiento de
updates directos, operaciones masivas y driver nativo.
