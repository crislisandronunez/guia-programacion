# 12 — Pruebas e integración final

## Objetivo

Leé las secciones 24–26, 36 y 41–43 de la [guía](../../guias/Glosario-Kafka.md).
Demostrá el recorrido completo con evidencia de estados y fallos.

## Consigna

Ampliá las pruebas iniciales de Testcontainers con un `PostgreSQLContainer`.
Probá el flujo Pedido → outbox → Kafka → Auditoría/Estadísticas. Cada prueba
usa identificadores únicos y evita depender del orden de otros tests. Esperá
condiciones con un plazo explícito; no sincronices con `Thread.sleep`.

Prepará una colección Postman para crear/confirmar pedido, consultar estado
de outbox, auditoría y proyección. No expongas datos internos sensibles ni
endpoints de administración Kafka. Conservá respuestas de ejemplo y comandos
de consola que muestren grupo, lag, tópico, partición y offset.

## Experimentos y aceptación

- `mvn verify` con Docker activo ejecuta las pruebas de integración; informás
  cantidad ejecutada y resultado, no solo «build success».
- Pedido confirmado y outbox persisten juntos ante un fallo de publicación.
- Auditoría y Estadísticas avanzan en grupos independientes.
- Publicación duplicada y caída antes de commit de offset dejan un único
  efecto por `eventoId` en cada destino SQL.
- JSON inválido llega a DLT con información suficiente; falla la prueba si
  el registro desaparece sin rastro.
- Un replay de un grupo no duplica efectos ni mueve offsets del otro.
- Documentás retención, límite de particiones y diferencia entre garantía
  Kafka y garantía del negocio.
- `ejercicios/respuestas/12.md` incluye diagrama final, comandos, resultados,
  fallos inyectados, métricas y pendientes reales.

## Para explicar

¿Qué evidencia concreta te permite afirmar «el pedido está confirmado»?
¿Qué evidencia adicional permite afirmar «Auditoría y Estadísticas terminaron»?

Volvé al [índice](README.md) y cerrá tu matriz de cobertura.
