# 06 — Sustitución y suite común de contratos

## Lectura

Secciones 1, 8, 12 de la [guía](../../guias/Glosario-Interface-Contratos.md).

## Consigna

Creá una suite reutilizable de pruebas de contrato para el repositorio de presupuestos,
con una fábrica de adaptador y preparación/limpieza por prueba. Ejecutala primero
contra memoria y reservá la misma suite para JPA en 08.

Creá una implementación deliberadamente defectuosa solo como fixture aislado:
por ejemplo, sobrescribe IDs o devuelve referencias mutables. Demostrá que la suite
detecta el defecto mediante un experimento documentado, sin dejar tests fallando en el build.
Distinguí mock, stub y fake según su uso.

## Experimentos y aceptación

- La suite cubre ausencia, duplicado, conservación, inmutabilidad observable y paginación.
- Los tests no verifican nombres privados de clases ni cantidad de llamadas JPA.
- Una implementación que fortalece precondiciones rompe la sustitución y se detecta.
- Los errores cumplen el mismo contrato independientemente del adaptador.
- Documentás garantías lógicas compartidas y garantías operativas distintas.
- Probás las implementaciones; un mock configurado para devolver lo esperado no demuestra conformidad.

## Entrega

Código, pruebas y respuestas en `ejercicios/respuestas/06.md`.
Agregá documentación del contrato y solicitudes Postman cuando corresponda.

## Para explicar

¿Qué significa que dos implementaciones sean intercambiables para su consumidor?

Siguiente: [Traducción de un proveedor HTTP](07-adaptador-http.md).

