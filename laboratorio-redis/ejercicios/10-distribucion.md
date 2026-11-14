# 10 — Replicación, Sentinel, Cluster y desafío final

## Objetivo
Leé las secciones 15 y 20 de la [guía](../../guias/Glosario-Spring-Redis.md).
Diferenciá disponibilidad, replicación y partición de datos.

## Parte A: Sentinel
Creá un Compose separado con un primary, una réplica y tres Sentinels, quorum 2.
Incluí configuración de replicación y Sentinel. Usá nombre de proyecto y volúmenes
propios. Para evitar direcciones anunciadas inaccesibles desde Windows, ejecutá la
aplicación de prueba dentro de esa red Docker y documentá los nombres resolubles allí.

Configurá un perfil Spring Sentinel, escribí por el primary, detén ese servicio y
observá promoción y reconexión. Verificá que no haya dos instancias aceptadas por el
cliente como primary estable. Documentá posibles pérdidas de escrituras por replicación
asíncrona; no presentes el failover como garantía de pérdida cero.

## Parte B: Cluster
En otro Compose, creá seis nodos (tres primarios y tres réplicas), activá cluster y
prepará un script de inicialización repetible que distribuya los slots.
El cliente de prueba también corre en esa red. Usá base lógica 0 y perfil Spring Cluster.

Probá operaciones multiclave con prefijos distintos para reproducir CROSSSLOT; luego
usá hash tags como `lab:redis:{usuario-1}:limite` y
`lab:redis:{usuario-1}:codigo`. Verificá CLUSTER KEYSLOT y slots cubiertos.
Documentá cómo una hash tag demasiado amplia concentra carga en un único shard.
La configuración standalone inicial no demuestra estas propiedades.

## Criterios de aceptación
- Los entornos se levantan y detienen con comandos documentados sin tocar otros laboratorios.
- Sentinel promueve una réplica y el cliente recupera acceso con timeout acotado.
- Cluster distribuye claves y el cliente maneja redirecciones; la operación multiclave
  funciona solo al satisfacer los requisitos de ubicación.
- Hay pruebas o evidencia reproducible de la caída y de CROSSSLOT.
- Explicás por qué Sentinel no hace sharding y Cluster no elimina hot keys.

## Desafío integrador
Volvé al entorno standalone y ejecutá un recorrido reproducible: catálogo con hit/miss,
invalidación, sesión entre dos instancias, límite concurrente, lock vencido, evento
reentregado y recuperación tras reinicio de Redis. Guardá los resultados y limitaciones
en `ejercicios/respuestas/10-informe.md`. Ejecutá mvn verify con Docker activo y verificá
que las pruebas no estén omitidas.

Para cada sección de la guía indicá si la practicaste, la simulaste o la estudiaste
conceptualmente. Redis JSON/Search/Bloom, Valkey, Dragonfly, Jedis, Redisson y programación
reactiva quedan como ampliaciones opcionales: no son requisitos de este recorrido.
