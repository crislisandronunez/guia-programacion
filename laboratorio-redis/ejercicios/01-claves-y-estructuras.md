# 01 — Claves, estructuras y TTL

## Objetivo
Leé las secciones 1, 4 y 5 de la [guía](../../guias/Glosario-Spring-Redis.md).
Aprendé a elegir estructuras desde redis-cli antes de escribir Java.

## Consigna
Creá claves bajo `lab:redis:01:` para estos casos:
- String: nombre de producto y contador de visitas con INCR.
- Hash: producto con nombre, precio como texto decimal y stock entero.
- List: cola FIFO de tres tareas con RPUSH y LPOP.
- Set: favoritos sin duplicados.
- Sorted Set: ranking de productos con puntuación.
- Bitmap: asistencia de siete días.
- HyperLogLog: visitantes únicos aproximados.
- Geo: dos locales con coordenadas conocidas y consulta por distancia.

Guardá comandos y resultados en tu respuesta. Usá TYPE y el comando de lectura de cada
tipo. Agregá una clave con TTL de cinco segundos, otra sin vencimiento y una inexistente.
Recorré el namespace usando SCAN hasta finalizar; no supongas que COUNT es un límite exacto.

## Experimentos y aceptación
- Diez INCR producen diez incrementos; agregar dos veces un favorito deja un miembro.
- Las tareas salen en el orden de entrada y el ranking devuelve los mayores scores.
- TTL distingue clave temporal, persistente (-1) e inexistente (-2).
- La clave temporal desaparece; actualizar con SET sin conservar TTL permite observar
  cómo cambia su expiración.
- GET sobre un Hash produce WRONGTYPE: explicá cómo diagnosticarlo.
- PFCOUNT se interpreta como aproximación, no como prueba de identidad exacta.
- Borrá únicamente las claves que creaste.

## Para explicar
¿Cuándo usarías Hash en lugar de JSON dentro de String? ¿Por qué un contador se guarda
como entero y un precio monetario no se incrementa con punto flotante?

Siguiente: [02](02-templates-y-repositorios.md).
