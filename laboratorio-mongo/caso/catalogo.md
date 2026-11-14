# Caso: catálogo de una tienda con sucursales

Datos ficticios:
- Cliente Ana, ana@example.test; Bruno, bruno@example.test.
- Producto CUAD-01: cuaderno, precio ARS 1.000,00, stock 10, etiquetas papel/escolar.
- Producto LIB-01: libro, precio ARS 2.500,00, stock 1, etiquetas lectura/escolar.
- Sucursal Centro: GeoJSON Point [-58.3816, -34.6037].
- Sucursal Norte: GeoJSON Point [-58.4473, -34.5627].

Producto tiene atributos variables por categoría y variantes con color y stock.
Cliente tiene direcciones acotadas; pedidos conservan una copia de dirección de entrega
y líneas con precio histórico. La colección pedidos crece; no se embebe toda la historia
en Cliente. Cada pedido admite como máximo 100 líneas.

Importes BigDecimal almacenados como Decimal128; cantidades enteras positivas.
SKU y email normalizado son únicos mediante índices.
Confirmar un pedido descuenta stock sin permitir negativos.
BORRADOR → CONFIRMADO → CANCELADO. Cancelar repone una vez.
Los archivos adjuntos se guardan en GridFS y se consultan por identificador.

No hay cobros, envíos reales ni datos personales reales.
