# 03 — Evitar duplicados y sobreventa

## Objetivo
Leé la sección 6 de la [guía](../../guias/Resolucion-de-Problemas.md).

## Consigna
Exigí Idempotency-Key en creación y operaciones mutantes de pedidos. Persistí ámbito,
clave, huella normalizada, estado y respuesta con restricción UNIQUE.
Misma clave y mismo contenido reproducen la respuesta; contenido distinto devuelve 409.
Una operación todavía en curso devuelve 202 con una consulta de seguimiento.

Protegé reservas mediante bloqueo de filas ordenadas por ID o actualización condicional
atómica. Reservar todos los productos y aprobar el pedido ocurre en una transacción.
Una consulta previa de existencia no sustituye restricciones de base.
No mantengas locks mientras llamás a proveedores externos.

## Experimentos y aceptación
- Veinte creaciones simultáneas con una clave producen un pedido.
- Misma clave con otra cantidad devuelve 409.
- Dos pedidos por la última unidad: solo uno se aprueba.
- Un pedido de dos productos sin stock en el segundo no reserva el primero.
- Cancelación repetida libera una vez; cancelar contra despachar concurrentemente
  termina en un único estado válido y con stock consistente.
- Repetir después de reiniciar mantiene la garantía.
- Mostrá el fallo de una implementación ingenua y la prueba que lo detecta.

## Para explicar
¿Por qué @Version y una clave idempotente resuelven problemas diferentes?
¿Cómo recuperás una colisión UNIQUE sin seguir usando una transacción marcada rollback-only?

Siguiente: [04](04-asincronia.md).
