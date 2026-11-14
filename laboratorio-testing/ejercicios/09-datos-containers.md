# 09 — JPA, PostgreSQL y Testcontainers

## Lectura

Secciones 13–14 de la [guía](../../guias/Glosario-Testing.md).

## Consigna

Agregá JPA, PostgreSQL, Flyway, spring-boot-testcontainers y módulos de Testcontainers
compatibles con Boot. Implementá entidades, repositorios y migraciones del caso.
Usá @ServiceConnection o @DynamicPropertySource con puerto dinámico y vida útil
del contenedor alineada con el contexto; esta última no es exclusiva de Boot antiguo.

Probá consultas, referencia única por propietario, stock no negativo y persistencia
de líneas con @DataJpaTest. Evitá reemplazo accidental por H2. Hibernate valida el
esquema creado por las mismas migraciones de la aplicación.

## Experimentos y aceptación

- Lecturas después de flush/clear verifican datos de la base.
- Duplicado y cantidad/stock inválidos fallan en las restricciones previstas.
- El mismo texto de referencia para otro propietario no es duplicado.
- Listados tienen orden y filtros correctos.
- Una excepción al guardar líneas revierte la operación completa.
- Probás una lectura posterior al commit desde otra transacción.
- Los tests no usan ni borran la base Compose del usuario.
- Si Docker no está, se informa fallo/omisión explícita, no validación contra PostgreSQL.

## Entrega

Pruebas y código mínimo del comportamiento, más evidencia y respuestas en
`ejercicios/respuestas/09.md`. Indicá qué falla si rompés la regla que estás verificando.

## Para explicar

¿Qué errores de PostgreSQL podría ocultar una suite que solo usa H2?

Siguiente: [Proveedor simulado y límites del mock](10-cliente-http.md).

