# 10 — Transacciones, conflictos y pool

## Lectura

Secciones 12 de la [guía](../../guias/Glosario-Reciliencia-Programacion.md).

## Consigna

Activá el perfil Compose datos. Agregá JPA, PostgreSQL, Flyway y pruebas Testcontainers;
si comparás Spring Retry, agregá su dependencia y @EnableRetry solo para este ejercicio.
Creá migraciones para reservas, unicidad de clave y stock con @Version. Hibernate valida
el esquema y open-in-view queda desactivado.

Persistí la intención antes de llamar al proveedor y cerrá la transacción antes del I/O.
Un retry de conflicto local debe crear una transacción nueva y releer el estado.
Separá el orquestador de reintentos y el servicio transaccional para demostrar ese límite.
No vuelvas a enviar la reserva remota al repetir una actualización local.

## Experimentos y aceptación

- Dos clientes compiten por la última unidad y solo uno confirma el descuento local.
- Cada intento fallido hace rollback; el siguiente observa datos nuevos.
- Una clave idempotente persiste tras reiniciar la aplicación.
- Una intención durable permite reconciliar tras caída entre efecto remoto y actualización local.
- Saturar Hikari con conexiones retenidas falla dentro de connectionTimeout.
- Distinguís espera por conexión, timeout de consulta, de lock y de red.
- La prueba usa PostgreSQL real, dos conexiones y barrera, no H2 ni una sola transacción con rollback.
- Un contenedor único no se presenta como prueba de failover; diseñá ese experimento como extensión.

## Entrega

Código, pruebas y evidencia en `ejercicios/respuestas/10.md`.
Agregá solicitudes Postman cuando corresponda y anotá límites de lo demostrado.

## Para explicar

¿Por qué reintentar dentro de una transacción marcada rollback-only no recupera el trabajo?

Siguiente: [Caché caída y estampidas](11-cache.md).

