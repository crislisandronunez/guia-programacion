# 09 — Lazy, N+1 y planes de carga

## Lectura
Sección 16 de la [guía](../../guias/Glosario-Spring-Data-JPA.md).

## Consigna
Prepará al menos 20 pedidos con varios clientes y líneas. Reproducí N+1 al convertir
pedidos a DTO accediendo a sus relaciones. Medí estadísticas con contexto limpio,
sin compartir contadores entre pruebas paralelas.

Resolvé un detalle con JOIN FETCH o EntityGraph, y un listado paginado con proyección
o dos consultas (página de IDs y carga de datos). Conservá el orden de los IDs de página.
No hagas fetch join de colección junto a paginación sin analizar la estrategia.
El perfil lab falla explícitamente ante paginación en memoria sobre collection fetch.

## Aceptación
- Mostrás SQL y número de consultas antes/después sobre el mismo dataset.
- El costo de consultas del listado no crece una por fila.
- La paginación devuelve cantidad correcta y total coherente.
- open-in-view permanece false; DTO se construye dentro del límite apropiado.
- LazyInitializationException se reproduce en un test aislado y se resuelve con un plan
  de carga, no convirtiendo todas las relaciones a EAGER.
- Varias colecciones no generan producto cartesiano descontrolado ni duplican resultados.
- EXPLAIN ANALYZE en entorno de prueba permite justificar un índice.

## Pregunta
¿Por qué EAGER no garantiza un único SELECT? ¿Cómo afecta el contexto de primer nivel
a una medición de N+1?

Siguiente: [10](10-bloqueos.md).
