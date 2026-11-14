# 05 — Atomicidad, Lua y rate limiting

## Objetivo
Leé las secciones 13 y 14 de la [guía](../../guias/Glosario-Spring-Redis.md).

## Consigna
Implementá POST `/api/demo/solicitudes` con un límite didáctico por cliente: cinco
solicitudes por ventana fija de diez segundos. En perfil lab, usá un ID validado en
header; documentá que un header manipulable no sirve como identidad de producción.

Primero observá la carrera entre INCR y EXPIRE; luego reemplazala por un script Lua
que compruebe argumentos antes de escribir, incremente y fije el TTL atómicamente.
Devolvé 429 y Retry-After calculado desde el vencimiento para las solicitudes excedidas.
Definí fail-closed con 503 si Redis no está disponible.

Además, implementá un consumo de código ficticio de un solo uso con comparación y
borrado atómicos. Un código incorrecto no debe consumir el correcto.

## Experimentos y aceptación
- Veinte solicitudes concurrentes en la misma ventana permiten cinco.
- Todas las claves del limitador tienen TTL; al vencer se habilita otra ventana.
- Dos validaciones simultáneas del código correcto: una sola tiene éxito.
- En redis-cli ejecutá MULTI/EXEC y provocá un error de ejecución: observá qué comandos
  sí modifican datos. No asumas rollback.
- Repetí una actualización condicional con WATCH y dos conexiones; detectá el conflicto
  y limitá los reintentos.
- Compará 100 escrituras normales con pipeline: mismos datos; el pipeline reduce viajes
  de red, pero no aporta aislamiento.

## Para explicar
¿Por qué atomizar INCR no atomiza INCR seguido de EXPIRE? ¿Qué sucede si Lua escribe y
luego falla? Validá antes de modificar: los scripts tampoco ofrecen rollback general.
Consultá la [referencia de transacciones](https://redis.io/docs/latest/develop/using-commands/transactions/).

Siguiente: [06](06-locks.md).
