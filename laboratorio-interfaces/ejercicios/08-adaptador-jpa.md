# 08 — Persistencia JPA sin contaminar el núcleo

## Lectura

Secciones 6, 8, 10 de la [guía](../../guias/Glosario-Interface-Contratos.md).

## Consigna

Levantá PostgreSQL del Compose y agregá JPA, driver PostgreSQL, Flyway y Testcontainers.
Creá entidades JPA separadas, Spring Data repository técnico y mappers dentro de
infraestructura. El adaptador implementa el puerto existente sin cambiar el caso de uso.

Persistí presupuesto y líneas en una transacción del adaptador. Configurá ddl-auto:
validate y open-in-view:false. Asegurá inserción, no upsert silencioso por save/merge.
Traducí el conflicto de ID al error propio incluso si ocurre al flush o al commit.
Usá precisión suficiente, Instant preservado y orden SQL compatible con el contrato.

## Experimentos y aceptación

- La misma suite de 06 pasa contra memoria y PostgreSQL real.
- Guardar y recuperar preserva totales y precios históricos.
- ID duplicado da conflicto, sin sobrescribir datos.
- Un fallo al guardar líneas no deja presupuesto parcial.
- Paginación y desempate UUID coinciden con memoria; no asumís que todos los comparadores ordenan UUID igual.
- Reiniciar la aplicación conserva datos de PostgreSQL.
- Entidades y excepciones JPA no salen del adaptador.
- Las pruebas verifican datos tras flush/clear o una lectura en otra transacción.

## Entrega

Código, pruebas y respuestas en `ejercicios/respuestas/08.md`.
Agregá documentación del contrato y solicitudes Postman cuando corresponda.

## Para explicar

¿Por qué una interfaz que extiende JpaRepository sigue siendo infraestructura aunque sea una interface?

Siguiente: [Límites arquitectónicos y contratos pequeños](09-limites-evolucion.md).

