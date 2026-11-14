# 08 — Persistencia, memoria y rendimiento

## Objetivo
Leé las secciones 5, 13 y 18 de la [guía](../../guias/Glosario-Spring-Redis.md).

## Consigna
Usá el Redis del laboratorio para comprobar AOF: escribí datos, reiniciá el contenedor y
compará claves y TTL. Después creá una configuración Compose experimental independiente,
con otro nombre de proyecto y volumen, para comparar AOF, RDB y ninguna persistencia.

En ese entorno descartable fijá maxmemory y probá noeviction frente a allkeys-lru.
No mezcles sesiones, streams o locks importantes con la instancia que llena memoria.
Cargá valores de tamaño acotado hasta observar rechazo o evicciones.

## Experimentos y aceptación
- Un reinicio conservando volumen recupera datos según la configuración elegida.
- Una clave expirada durante la parada no revive como válida.
- Diferenciá reinicio limpio de caída abrupta: AOF everysec no promete pérdida cero.
- Con noeviction observás errores al agotar memoria; con allkeys-lru aumentan evicted_keys.
- Registrá INFO memory, INFO stats, MEMORY USAGE y SLOWLOG GET antes y después.
- Construí un valor grande de prueba y compará lectura completa con paginación.
- Medí lotes con pipeline frente a comandos individuales usando calentamiento y varias
  repeticiones. Reportá mediciones observadas, sin exigir una mejora porcentual fija.
- Calculá hit ratio con deltas y manejá denominador cero.

## Entrega y preguntas
Incluí los archivos del entorno experimental, comandos y tabla de resultados.
¿Cómo distinguís expiración de eviction? ¿Por qué persistencia no es backup?
Explicá qué datos del laboratorio son reconstruibles y cuáles requerirían otra
estrategia de recuperación. No publiques endpoints administrativos para cambiar CONFIG.

Siguiente: [09](09-seguridad-y-testing.md).
