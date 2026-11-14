# 06 — Locks y estampida de caché

## Objetivo
Leé los apartados de locks y cache stampede en las secciones 3 y 14 de la
[guía](../../guias/Glosario-Spring-Redis.md).

## Consigna
Creá un lock por producto con SET NX PX, token UUID por adquisición y TTL acotado.
Liberalo con Lua comparando token antes de DEL. Nunca liberes el lock de otro dueño.
Los solicitantes que no adquieren esperan con backoff y límite; luego releen la caché.
El dueño también relee la caché después de adquirir antes de recalcular.

Aplicá el mecanismo al catálogo. Para probar dos instancias con una fuente en memoria,
usá datos iniciales inmutables idénticos y sumá sus contadores; esa fuente no permite
demostrar consistencia de escrituras entre instancias.

## Experimentos y aceptación
- Veinte lecturas concurrentes de una clave fría producen un único cálculo cuando el
  cálculo termina dentro del lease y Redis permanece disponible.
- Una excepción libera el lock solo si todavía pertenece al solicitante.
- Si A supera su lease, B adquiere; al terminar A, no borra el lock de B.
- Demostrá que el lease vencido permite dos trabajos simultáneos. No afirmes exclusión
  absoluta ni uses este lock como única protección de saldo o stock crítico.
- Para una escritura protegida, diseñá un fencing token creciente y un recurso que
  rechace tokens antiguos; explicá qué limita el diseño si Redis pierde estado.
- Agregá TTL con jitter para repartir vencimientos de distintas claves.
- Redis caído no genera espera infinita ni una avalancha ilimitada de tareas.

## Para explicar
Compará el diseño manual con RLock y watchdog de Redisson. La renovación de un lock
no elimina todos los escenarios de pausa, partición o failover. Indicá qué garantiza
tu prueba y bajo qué supuestos.

Siguiente: [07](07-mensajeria.md).
