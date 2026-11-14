# Tienda ficticia: Librería Sur

La tienda vende productos físicos y digitales. Un cliente tiene email único,
estado y direcciones de facturación/envío. Un pedido contiene líneas con cantidad
y precio aplicado, y pertenece a un cliente.

## Datos para preparar en perfil lab

- Ana: ana@example.test, ACTIVO, ciudad La Plata.
- Bruno: bruno@example.test, ACTIVO, ciudad Córdoba.
- Carla: carla@example.test, INACTIVO, ciudad La Plata.
- Cuaderno: SKU CUAD-01, ARS 1.000,00, stock 10.
- Libro: SKU LIB-01, ARS 2.500,00, stock 1.
- Guía digital: SKU DIG-01, ARS 500,00, sin logística física.

## Reglas

- Importes BigDecimal con dos decimales, todos en ARS; cero es válido para precio.
- Cantidades de pedido positivas, stock físico nunca negativo.
- Precio y stock provienen del servidor; el pedido conserva el precio de compra.
- No eliminar un cliente o producto referenciado por pedidos históricos.
- SKU y email son únicos. Email se normaliza con trim y minúsculas antes de guardar.
- BORRADOR → CONFIRMADO → CANCELADO. Cancelar devuelve stock una sola vez.
- Solo BORRADOR permite editar líneas. Confirmar descuenta stock físico.
- Cliente inactivo no confirma pedidos.

Este es un dominio didáctico. Las restricciones, consultas y garantías se implementan
progresivamente; no hay cobros ni envío de mensajes reales.
