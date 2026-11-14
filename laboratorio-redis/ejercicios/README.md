# Consignas del laboratorio Redis

## Recorrido y cobertura

| Etapa | Consigna | Secciones de la guía |
|---|---|---|
| 01 | [Claves, tipos y TTL](01-claves-y-estructuras.md) | 1, 4, 5 |
| 02 | [Templates, JSON y repositorios](02-templates-y-repositorios.md) | 2, 3, 6–9 |
| 03 | [Caché de catálogo](03-cache.md) | 10, 14, 20 |
| 04 | [Sesiones compartidas](04-sesiones.md) | 11 |
| 05 | [Atomicidad y rate limiting](05-atomicidad.md) | 13, 14 |
| 06 | [Locks y estampida de caché](06-locks.md) | 3, 14 |
| 07 | [Pub/Sub y Streams](07-mensajeria.md) | 12 |
| 08 | [Persistencia, memoria y rendimiento](08-persistencia.md) | 5, 13, 18 |
| 09 | [Seguridad, errores y pruebas](09-seguridad-y-testing.md) | 16–19 |
| 10 | [Replicación, Sentinel, Cluster e integración](10-distribucion.md) | 15, 20 |

Todas son consignas por resolver; la base contiene únicamente infraestructura y pruebas
de conexión. Las primeras nueve se apoyan en el Redis standalone. La topología distribuida
se construye en la última etapa. Módulos JSON/Search/Bloom, clientes alternativos y Redis
reactivo se estudian como extensiones, sin afirmar que estén implementados aquí.

## Reglas comunes

- Java 21, API bajo `/api`, DTO validados y claves bajo `lab:redis:`.
- Dividí prefijos por ejercicio y versión, por ejemplo `lab:redis:v1:producto:1`.
- HTTP 400 para datos inválidos, 404 para ausencia, 409 para conflicto, 429 para límite
  y 503 cuando una dependencia imprescindible no esté disponible.
- No expongas un endpoint que ejecute comandos Redis arbitrarios.
- Cada etapa entrega código, pruebas, solicitudes Postman y respuestas en
  `ejercicios/respuestas/NN.md`. Las respuestas son un entregable futuro.
- Probá concurrencia con ejecutores y una barrera, no con un runner secuencial.
- Usá Testcontainers y claves únicas por prueba. Limpieza solo de claves propias;
  no uses FLUSHALL ni FLUSHDB sobre servicios compartidos.
- Documentá qué se pierde cuando Redis cae: una caché puede degradar a la fuente;
  una sesión, reserva o límite requiere una decisión diferente.
- No copies ejemplos de la guía sin verificar sus garantías: pipeline no implica
  atomicidad, MULTI no ofrece rollback de errores de ejecución, y un lease vencido
  no impide que un cliente antiguo continúe trabajando.

Comenzá con [01 — Claves y estructuras](01-claves-y-estructuras.md).
