# 02 — Templates, serialización y repositorios

## Objetivo
Leé las secciones 2, 3 y 6–9 de la [guía](../../guias/Glosario-Spring-Redis.md).
Relacioná comandos Redis con Spring Data.

## Consigna
1. Creá ProductoDto con ID, nombre y precio BigDecimal. Implementá PUT y GET
   `/api/productos-temporales/{id}` mediante un RedisTemplate tipado con claves String
   y valores JSON. Cada escritura fija TTL de 60 segundos.
2. Usá StringRedisTemplate para POST `/api/productos-temporales/{id}/visitas` y un
   contador separado; no serialices el número con un formato incompatible con INCR.
3. Creá una entidad distinta OfertaTemporal con @RedisHash, @Id, @Indexed en categoría
   y @TimeToLive. Exponé creación y consulta por categoría mediante repositorio.
4. Separá namespaces de templates y repositorios. Rechazá precio negativo, nombre vacío
   e IDs inválidos. Usá JSON tipado sin habilitar deserialización polimórfica irrestricta.

## Experimentos y aceptación
- El JSON es legible en CLI y se recupera con el tipo esperado.
- Un recurso ausente o vencido devuelve 404.
- El TTL se renueva al escribir, según el contrato definido.
- La búsqueda por categoría devuelve ofertas activas. Investigá índices auxiliares,
  shadow copies y eventos de expiración; verificá qué ocurre si la app está apagada
  durante el vencimiento y documentá una estrategia de limpieza.
- Introducí un JSON incompatible en una clave propia: diagnosticá la excepción y resolvé
  el cambio de esquema con un namespace v2, sin borrar claves ajenas.
- Pruebas reales de round trip, expiración e incremento concurrente.

## Para explicar
¿Qué aporta Lettuce y qué agrega Spring Data? Compará Jedis, Redisson y el cliente reactivo
por sus modelos de uso. No hace falta incorporar los tres al proyecto.

Siguiente: [03](03-cache.md).
