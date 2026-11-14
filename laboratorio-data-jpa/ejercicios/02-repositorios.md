# 02 — Repositorios y contexto de persistencia

## Lectura
Secciones 8 y 15 de la [guía](../../guias/Glosario-Spring-Data-JPA.md).

## Consigna
Implementá POST, GET por ID, PUT y DELETE /api/clientes y /api/productos con DTO y servicios.
Actualizá una entidad cargada en una transacción mediante dirty checking.
Para eliminar, respetá futuras referencias históricas.

Prepará pruebas de estados transient, managed, detached y removed usando EntityManager.
Compará persist, merge, find, getReference, flush, detach, clear y refresh.
Implementá un fragmento de repositorio personalizado para una consulta simple.

## Experimentos y aceptación
- Dos find del mismo ID en el mismo contexto devuelven la misma instancia gestionada.
- Cambiar una entidad managed confirma un UPDATE sin save explícito.
- Cambiar una detached no persiste hasta realizar una operación apropiada.
- merge devuelve la instancia gestionada; el argumento original sigue detached.
- flush envía SQL pero un rollback posterior revierte la operación.
- getReference puede diferir el acceso; probar ID inexistente no equivale a findById.
- PUT a un ID inexistente devuelve 404, no crea una fila accidentalmente.
- Los tests verifican datos desde un contexto nuevo.

Contrastá cómo Spring Data determina si una entidad es nueva:
[referencia](https://docs.spring.io/spring-data/jpa/reference/jpa/entity-persistence.html).
No reduzcas save a “siempre INSERT” ni a “siempre UPDATE”.

Siguiente: [03](03-tipos.md).
