# 06 — Consultas derivadas, JPQL, SQL y DTO

## Lectura
Secciones 9, 10 y 12 de la [guía](../../guias/Glosario-Spring-Data-JPA.md).

## Consigna
Escribí query methods para email normalizado, productos por rango de precio y pedidos
por cliente/estado. Definí semántica inclusiva de los límites y rangos inválidos.
Implementá resumen de pedidos con JPQL agregado y DTO mediante constructor.
Compará con una proyección de interfaz cerrada y con SQL nativo parametrizado.

Agregá GET /api/reportes/ventas por rango de fechas. En este caso sumá solo pedidos
CONFIRMADOS, excluyendo CANCELADOS, y usá el precio de línea histórico.
Normalizá resultados vacíos a cero donde el contrato lo requiera.

## Aceptación
- Datos con varios pedidos y líneas no generan sumas duplicadas por joins.
- Un cliente sin pedidos se incluye o excluye según contrato explícito.
- JPQL usa entidades/atributos; SQL usa tablas/columnas reales.
- Ninguna consulta concatena valores de usuario en SQL.
- Proyecciones devuelven únicamente los campos necesarios.
- Los resultados de JPQL y SQL coinciden para el mismo dataset.
- Métodos que prometen un único resultado tienen restricción que lo respalda.

## Pregunta
¿Cuándo preferís @Query a un nombre de método largo? ¿Qué costo de mantenimiento
introduce una consulta nativa?

Siguiente: [07](07-filtros.md).
