# 09 — Versiones y transacciones multi-documento

## Lectura
Secciones 15 de la [guía](../../guias/Glosario-Spring-MongoDB.md).

## Consigna
Registrá MongoTransactionManager y confirmá un pedido modificando varios productos
y el pedido en una única transacción. Todas las operaciones deben usar la sesión
transaccional; prepará colecciones e índices antes del flujo.

Agregá @Version a Producto/Pedido y verificá el conflicto entre dos copias.
Para updates de bajo nivel incorporá explícitamente versión esperada e incremento;
no supongas que un filtro de versión surge solo de cualquier llamada al driver.
Usá escrituras acknowledged.

Un reintento por error transitorio debe rehacer la unidad transaccional con límite.
Distinguí resultado de commit desconocido de rechazo definitivo: consultá la referencia
de operación antes de emitir otro efecto. No hagas llamadas externas dentro del bloque.

## Experimentos y aceptación
- Fallo en segundo producto revierte el primero y deja el pedido sin confirmar.
- Dos pedidos por última unidad producen un ganador.
- Dos copias con la misma versión: una actualización pierde el conflicto.
- Cancelar concurrentemente repone una sola vez.
- La prueba usa replica set, no standalone.
- Repetir clave de operación no vuelve a descontar stock.
- Documentá qué garantías no ofrece el replica set local de un nodo.

## Para explicar
¿Qué requerimiento realmente necesita una transacción, y cuál se resuelve con un documento bien modelado?

Siguiente: [10](10-geo.md).
