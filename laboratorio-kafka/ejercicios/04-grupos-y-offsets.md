# 04 — Grupos, offsets y replay

## Objetivo

Leé las secciones 9–12, 21 y 28 de la [guía](../../guias/Glosario-Kafka.md).
Observá posición leída, offset confirmado y posición de inicio.

## Consigna

Escribí un consumidor Java con `enable.auto.commit=false` que procese un lote
y haga `commitSync()` **después** del trabajo. Publicá seis eventos del caso.
Consumilos con el grupo `auditoria-lab`; inspeccioná su avance con
`kafka-consumer-groups.sh --describe`.

Reiniciá el mismo grupo con `auto.offset.reset=earliest` y registrá qué relee.
Después ejecutá un grupo nuevo `diagnostico-04` y compará. Para un replay
controlado del grupo original, detené todos sus miembros y usá primero
`--reset-offsets ... --to-earliest --dry-run`. Solo en este clúster local, ejecutá
`--execute` después de anotar el efecto esperado. No borres tópicos para
«reiniciar» el ejercicio.

## Experimentos y aceptación

- Distinguís el offset leído por `poll` del confirmado para reanudar.
- `earliest` no provoca replay completo cuando el grupo tiene offset válido.
- Un grupo nuevo puede leer historial todavía retenido.
- El `dry-run` muestra el movimiento antes de aplicarlo.
- Anotás qué efecto de negocio podría duplicarse al releer y cómo lo evitarías.

## Para explicar

¿Por qué el valor numérico del offset no puede usarse como ID global? ¿Qué
ocurriría si la retención ya hubiese eliminado los primeros eventos?

Siguiente: [05 — Listeners y commits](05-listeners-y-commits.md).
