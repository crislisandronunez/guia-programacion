# 02 — Embebidos y referencias

## Lectura
Secciones 5 de la [guía](../../guias/Glosario-Spring-MongoDB.md).

## Consigna
Modelá Cliente con hasta cinco direcciones embebidas y Pedido con líneas embebidas
(máximo 100) y clienteId como referencia manual. Copiá nombre/precio de productos y
dirección elegida al crear el pedido para preservar historia.

Compará, en una consulta aparte, la referencia manual con @DocumentReference.
No esperes cascadas ni integridad referencial automática. Definí una política de
desactivación de clientes referenciados en lugar de borrar su historia.

## Experimentos y aceptación
- Cambiar precio o dirección actuales no altera el pedido histórico.
- Una referencia a un cliente inexistente se rechaza en el caso de uso.
- Probá qué ocurre si otro proceso borra el cliente y documentá el riesgo de carrera.
- La consulta de detalle devuelve DTO sin referencias circulares.
- Medí accesos adicionales al resolver referencias.
- No hay arrays de pedidos ilimitados dentro de un cliente.

## Para explicar
¿Qué se consulta junto y qué crece sin límite? Usá esas respuestas para justificar el modelo.

Siguiente: [03](03-indices.md).
