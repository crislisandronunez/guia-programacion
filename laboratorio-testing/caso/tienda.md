# Caso: pedidos de la tienda Horizonte

La tienda ficticia vende productos y consulta una tarifa de envío a un proveedor.
El objetivo es construir comportamiento pequeño y verificable por capas.

## Reglas para implementar

- CUAD-01: precio ARS 1000.00, stock 10; LIB-01: ARS 2500.00, stock 1.
- Peso por unidad: CUAD-01, 200 gramos; LIB-01, 500 gramos.
- Destino obligatorio, de 1 a 40 caracteres; peso total enviado al cotizador en gramos.
- Usuario inicial de práctica: ana; otro usuario: bruno; administrador: admin.
- Pedido con UUID, propietario, referenciaCliente, líneas, importes, creadoEn y estado.
- Referencia no vacía de hasta 40 caracteres, única por propietario.
- De una a veinte líneas, SKU sin repetir y cantidad entre 1 y 100.
- Precio y stock se obtienen del catálogo; el cliente no fija precios.
- Subtotal: suma de precio por cantidad con BigDecimal.
- Descuento: 10% si subtotal >= ARS 10000.00; cero en otro caso.
- Redondeo HALF_EVEN a dos decimales sobre el descuento total.
- Tarifa de envío no negativa; total = subtotal - descuento + envío.
- Fechas Instant con Clock inyectable. El pedido comienza CREADO.
- Se puede cancelar mientras ahora sea estrictamente anterior a creadoEn + 24 horas.
- Cancelar pasa a CANCELADO y repone stock una sola vez; repetir es conflicto.
- Stock nunca negativo. Si falta una unidad de cualquier producto, no se guarda ni descuenta parcialmente.
- El pedido conserva precios históricos aunque cambie el catálogo.

## Escenarios de importe

| Subtotal | Descuento | Envío | Total |
|---|---|---|---|
| 9000.00 | 0.00 | 500.00 | 9500.00 |
| 10000.00 | 1000.00 | 500.00 | 9500.00 |
| 12500.00 | 1250.00 | 0.00 | 11250.00 |

Probá también fronteras directamente sobre la calculadora aunque los precios iniciales
no produzcan todos los valores decimales. Las tablas son oráculos de requisitos,
no resultados calculados mediante la misma implementación bajo prueba.

## Contrato HTTP a construir

| Operación | Respuesta correcta |
|---|---|
| POST /api/pedidos | 201, Location y pedido creado |
| GET /api/pedidos/{id} | 200 con pedido |
| GET /api/pedidos?pagina=0&tamanio=10 | 200, página de pedidos y total |
| POST /api/pedidos/{id}/cancelacion | 200 con CANCELADO |

400 para entrada inválida; 404 para producto/pedido ausente o pedido no visible;
409 para stock insuficiente, referencia duplicada o cancelación inválida;
503 para proveedor no disponible; 502 para respuesta de proveedor mal formada.
Listado ordenado por creadoEn e ID canónico ascendente, tamaño 1 a 50.

Antes de seguridad, una identidad de práctica inyectada devuelve ana.
Desde la etapa 11 el propietario se obtiene de la identidad autenticada; nunca del JSON.
Ana y Bruno leen, listan y cancelan solo lo propio. Admin puede leer todos, pero no
cancelar pedidos ajenos. La API devuelve 401 sin identidad y 403 sin permiso global.
La invisibilidad de un pedido ajeno se trata como 404.

## Proveedor de envío y efectos

El adaptador consulta una tarifa con destino y peso positivo; esa consulta no crea
envíos. El stub remoto usará tarifa_centavos y moneda. Solo ARS y tarifa no negativa
son válidos. Configurá timeouts finitos. La consulta ocurre antes de la transacción
que guarda pedido y descuenta stock; esa transacción vuelve a validar el stock.

En la etapa de asincronía agregás una notificación local de pedido creado.
Es un evento en memoria, no una cola durable. Su fallo no deshace el pedido confirmado.
No hay correo, cobros ni datos reales.

## Límites y evolución

Al principio usás repositorios de práctica en memoria. En la etapa 09 incorporás JPA
y PostgreSQL para demostrar restricciones, rollback, consultas y concurrencia.
No hace falta implementar todo al comenzar: cada etapa define la porción a construir.

