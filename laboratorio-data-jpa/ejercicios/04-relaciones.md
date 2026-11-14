# 04 — Relaciones, lado dueño y cascadas

## Lectura
Sección 6 de la [guía](../../guias/Glosario-Spring-Data-JPA.md).

## Consigna
Modelá Cliente 1:N Pedido, Pedido 1:N LineaPedido y LineaPedido N:1 Producto.
Usá precio aplicado por línea, cantidad y estado de pedido.
Agregá PerfilCliente 1:1 Cliente con FK única.
Indicá dueño, mappedBy, FK, fetch y cascadas en un diagrama.

Pedido agrega y quita líneas con métodos que mantienen ambos lados. Permití
cascade PERSIST y orphanRemoval donde el ciclo de vida lo justifica.
Nunca propagues REMOVE desde una línea a un producto compartido.
Definí relaciones to-one lazy cuando corresponda y verificá el SQL real.

## Experimentos y aceptación
- Crear un pedido con líneas guarda FKs correctas.
- Modificar solo el lado inverso demuestra por qué falta sincronizar el dueño.
- Quitar una línea de un BORRADOR elimina ese hijo, pero conserva el producto.
- Borrar un cliente con pedidos se rechaza sin borrar su historial.
- El pedido captura el precio actual; cambiar Producto no modifica una compra anterior.
- DTO de detalle funciona con open-in-view:false y no entra en recursión JSON.
- FKs y UNIQUE del 1:1 impiden inconsistencias incluso por SQL directo.

## Entrega
POST /api/pedidos, GET /api/pedidos/{id} y edición de líneas de borrador.
La confirmación transaccional se resuelve en la etapa 08.

Siguiente: [05](05-herencia.md).
