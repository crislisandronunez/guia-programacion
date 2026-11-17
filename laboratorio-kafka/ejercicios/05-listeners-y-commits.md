# 05 — Listeners, commits y rebalanceos

## Objetivo

Leé las secciones 10–12, 21–22 y 33 de la [guía](../../guias/Glosario-Kafka.md).
Decidí cuándo un listener puede dar por terminado un registro.

## Consigna

Implementá dos `@KafkaListener` sobre `pedidos-confirmados`: uno del grupo
`auditoria-lab` y otro de `estadisticas-lab`. Por ahora pueden guardar resultados
en memoria **solo para observar**; aclaralo en logs y en el informe, porque
esa memoria se pierde al reiniciar. Cada listener registra `eventoId`, grupo,
partición y offset. Verificá el `ack-mode: record` de la configuración base.

Probá dos instancias de la aplicación con puertos HTTP diferentes. Usá una
configuración que no pierda los datos observables al reiniciar la instancia,
o complementá la observación con los offsets del grupo. Cambiá la concurrencia
del listener y examiná la asignación de las tres particiones. Provocá un fallo
controlado **antes** de terminar el método y observá reentrega/reintentos.

## Experimentos y aceptación

- Ambos grupos reciben cada evento aunque sus miembros compartan el clúster.
- Dentro de un grupo, cada partición tiene un único miembro asignado a la vez
  bajo el modelo de grupos estándar del laboratorio.
- Con más miembros que particiones, identificás miembros sin trabajo útil.
- Un error no se captura para retornar normalmente si el efecto no terminó.
- Explicás por qué un commit posterior al efecto admite duplicación ante caída.

## Para explicar

¿Qué cambiaría al usar `MANUAL_IMMEDIATE`? ¿Un commit manual vuelve atómica la
escritura de una base de datos y el offset Kafka?

Siguiente: [06 — Contratos](06-contratos.md).
