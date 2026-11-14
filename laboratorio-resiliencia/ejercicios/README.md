# Consignas de resiliencia

| Etapa | Consigna | Secciones de la guía |
|---|---|---|
| 01 | [Fallos y presupuesto de tiempo](01-fallos-presupuestos.md) | 1–3 |
| 02 | [Timeouts y cancelación](02-timeouts.md) | 4 |
| 03 | [Retry, backoff y jitter](03-reintentos.md) | 5, 11 |
| 04 | [Reservas y resultados inciertos](04-idempotencia.md) | 2, 5, 9, 16 |
| 05 | [Circuit Breaker y recuperación](05-circuit-breaker.md) | 6, 11 |
| 06 | [Aislamiento y colas acotadas](06-bulkhead.md) | 7 |
| 07 | [Rate limiting y rechazo temprano](07-limites-carga.md) | 8 |
| 08 | [Fallback y orden de protecciones](08-fallback-composicion.md) | 9–11 |
| 09 | [Configuración y observabilidad](09-metricas.md) | 10–11, 16 |
| 10 | [Transacciones, conflictos y pool](10-base-datos.md) | 12 |
| 11 | [Caché caída y estampidas](11-cache.md) | 13 |
| 12 | [Mensajería, DLQ y backpressure](12-mensajeria.md) | 14 |
| 13 | [Probes, apagado y fallos controlados](13-apagado-caos.md) | 15 |
| 14 | [Integración y defensa de decisiones](14-integracion.md) | 1–16 |

## Forma de trabajo

Leé el [caso](../caso/tienda.md). Las consignas están completas; las clases de negocio,
simuladores, políticas y pruebas de los ejercicios son trabajo del estudiante.
La base solo comprueba arranque con las dependencias disponibles.

1. Planteá una hipótesis y el resultado esperado antes de provocar el fallo.
2. Implementá el escenario y una política a la vez.
3. Medí intentos remotos, trabajo activo, latencia y resultado de negocio.
4. Verificá recuperación y limpieza, además del rechazo.
5. Guardá evidencia y respuestas en ejercicios/respuestas/NN.md, creada al resolver.

## Convenciones

- Java 21, Boot 3.5.16, Resilience4j 2.3.0; paquete com.estudio.resiliencia.
- API /api, UUID, Instant, Clock inyectable, BigDecimal y DTO validados.
- Errores con codigo, mensaje y correlationId; conservar causa técnica en logs sin secretos.
- Tests de red con servidor stub independiente y puertos aleatorios.
- JUnit y contadores para políticas; barreras y timeouts para concurrencia.
- No depender de Thread.sleep largos ni de random no controlado. Clock no cambia
  automáticamente el reloj interno de todas las bibliotecas: elegí mecanismos de prueba compatibles.
- Limpiar ejecutores, conexiones y escenarios; no alterar otros laboratorios.
- En cada prueba registrar número total de intentos, incluidos el original y los reintentos.
- Evitar self-invocation para anotaciones basadas en proxy.
- POST no se reintenta sin un contrato idempotente comprobado.

## Infraestructura y progresión

Etapas 01–09: aplicación y proveedor de pruebas local; sin Docker obligatorio.
Etapa 10: PostgreSQL; etapa 11: Redis además de la fuente; etapa 12: RabbitMQ y PostgreSQL.
Las conexiones se configuran al resolver cada etapa, con perfiles de Spring si querés
conservar un arranque básico sin infraestructura. No confundas esos perfiles con Compose.

Usá Testcontainers para comprobar infraestructura en tests aislados. Especificá
dependencias y versiones compatibles al incorporarlas. Un build que omita las pruebas
por falta de Docker no valida recuperación, transacciones ni entrega de mensajes.

## Lectura crítica de la guía

- Timeout significa que dejaste de esperar, no que el efecto remoto se deshizo.
- Cambiar de proveedor tras una reserva incierta puede duplicar el efecto.
- Circuit Breaker observa resultados; Bulkhead limita concurrencia; no son equivalentes.
- La composición y el lugar del fallback cambian los errores que ven las capas externas.
- Fail-open hacia una fuente requiere capacidad protegida.
- ENCOLADO exige registro real y una garantía explícita de recuperación.
- Un tamaño de pool o timeout de ejemplo necesita validación con tu carga.

Las 16 secciones tienen etapas asociadas. La parte de mensajería se practica con
RabbitMQ; Kafka, failover entre nodos y limitación distribuida quedan como extensiones,
sin presentarlas como implementadas.

Referencias: [integración Boot](https://resilience4j.readme.io/docs/getting-started-3),
[circuitos](https://resilience4j.readme.io/docs/circuitbreaker) y
[confirmaciones de RabbitMQ](https://www.rabbitmq.com/docs/confirms).

