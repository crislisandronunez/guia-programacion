# 11 — Caché local y consistencia

## Lectura

Secciones 14 de la [guía](../../guias/Glosario-Spring-Core-Boot-Core.md).

## Consigna

Agregá spring-boot-starter-cache y habilitá caché explícitamente. Usá un administrador
local en memoria para consultas del catálogo, con contador de lecturas de la fuente.
Compará @Cacheable, @CachePut y @CacheEvict mediante un servicio llamado desde otro bean.
Definí claves e invalidación para detalle y listado.

La caché inicial es didáctica: no presupongas TTL, persistencia ni coordinación entre
instancias. Si querés vencimiento, investigá un proveedor compatible como extensión.

## Experimentos y aceptación

- Dos consultas iguales leen una vez la fuente.
- Una actualización o baja invalida las entradas afectadas.
- Una validación fallida no altera la fuente ni deja un valor inválido cacheado.
- El comportamiento para ausentes está definido y probado.
- Self-invocation muestra el límite del proxy.
- Reiniciar elimina la caché, y dos contextos con caches locales no comparten entradas.

## Entrega

Código y pruebas del ejercicio, evidencia y respuestas en `ejercicios/respuestas/11.md`.
Agregá solicitudes a Postman cuando haya comportamiento HTTP.

## Para explicar

¿Qué ventana de inconsistencia puede aparecer entre actualizar la fuente e invalidar la caché?

Siguiente: [Starters y auto-configuración](12-autoconfiguracion.md).

