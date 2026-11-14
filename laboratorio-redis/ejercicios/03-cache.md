# 03 — Caché de catálogo

## Objetivo
Leé las secciones 10, 14 y 20 de la [guía](../../guias/Glosario-Spring-Redis.md).

## Consigna
Creá una fuente ficticia de productos en memoria, separada de Redis, con contador de
lecturas y datos iniciales repetibles. Documentá que reiniciar la aplicación reinicia
esa fuente. Implementá GET, PUT y DELETE `/api/catalogo/{id}`.

Activá @EnableCaching y usá @Cacheable, @CachePut y @CacheEvict desde servicios invocados
por el proxy de Spring. Configurá JSON, prefijos y TTL: productos 30 segundos, listados
10 segundos. Las actualizaciones invalidan listados y actualizan o invalidan el detalle.
Rechazá valores inválidos antes de modificar la fuente.

## Experimentos y aceptación
- Dos lecturas secuenciales antes del TTL consultan una vez la fuente.
- Tras vencer el TTL, la próxima lectura vuelve a consultar la fuente.
- Actualizar y borrar no dejan respuestas antiguas en las consultas secuenciales.
- Mostrá un caso de self-invocation que elude la caché y refactorizalo.
- Un ID inexistente devuelve 404. Agregá después caché negativa explícita de cinco
  segundos; crear ese ID debe invalidarla.
- Si Redis cae, una lectura puede obtener datos de la fuente dentro de un tiempo acotado.
  Registrá el fallo; no presentes errores de deserialización como éxitos silenciosos.
- Simulá lectura concurrente con actualización: identificá la ventana de dato obsoleto
  y documentá el límite de consistencia. El TTL acota el daño, no crea una transacción
  entre fuente y caché.

## Entrega
Pruebas del contador de lecturas y solicitudes Postman. Medí hits/misses de esta caché
sin confundirlos con todos los accesos de Redis. El stampede se trabaja en la etapa 06.

Siguiente: [04](04-sesiones.md).
