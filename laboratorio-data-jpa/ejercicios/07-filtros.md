# 07 — Paginación y consultas dinámicas

## Lectura
Secciones 11 y 13 de la [guía](../../guias/Glosario-Spring-Data-JPA.md).

## Consigna
Implementá GET /api/productos con filtros opcionales por nombre, precio, categoría
y subtipo. Usá JpaSpecificationExecutor con especificaciones combinables.
Limitá size a 100 y ordená por un campo permitido más ID como desempate.

Compará Page y Slice: inspeccioná cuándo aparece un count.
Agregá una consulta equivalente simple con Query By Example y una con Criteria API
en un fragmento personalizado. Documentá límites de QBE para rangos y condiciones complejas.

## Experimentos y aceptación
- Cada filtro y combinaciones producen resultados esperados.
- Rango invertido, página negativa, size inválido y sort no permitido devuelven 400.
- Empates tienen orden determinista; páginas sobre un dataset estable no repiten filas.
- Joins con categorías no duplican productos ni inflan el count.
- La respuesta devuelve DTO y metadatos definidos por tu API.
- Con inserciones concurrentes, explicá por qué offset puede desplazar elementos;
  implementá como extensión un cursor por precio/ID.
- Medí la consulta de datos y la de conteo por separado.

## Pregunta
¿Necesita el usuario un total exacto o solamente saber si hay más resultados?

Siguiente: [08](08-transacciones.md).
