# Caso ficticio: Distribuidora Horizonte

Todos los datos siguientes son supuestos didácticos, no mediciones de una empresa real.

Horizonte recibe 300 pedidos diarios. Seis vendedores usan una planilla compartida;
administración revisa deuda en un ERP, depósito descuenta stock y un operador contrata
el envío. Las notificaciones se copian manualmente por correo.

## Voces de los interesados

- Ventas: «Con mala conexión enviamos otra vez y a veces aparecen dos pedidos».
- Depósito: «Dos vendedores prometen la última caja; uno termina cancelando».
- Administración: «Si el ERP no responde, algunos aprueban igual para no frenar la venta».
- Operaciones: «El reporte mensual tarda minutos y la pantalla parece colgada».
- Gerencia: «Queremos una app, pero primero necesitamos reducir reclamos».
- Soporte: «No sabemos quién canceló el pedido ni por qué».

## Datos iniciales

Cliente C-001: activo, sin deuda vencida. C-002: activo, deuda vencida de ARS 1,00.
C-003: inactivo. Producto P-001: diez unidades, precio ARS 1.000,00.
Producto P-002: una unidad, precio ARS 2.500,00.

Políticas acordadas para el ejercicio:
- Toda deuda vencida mayor a cero bloquea nuevos pedidos.
- Un cliente inactivo no puede comprar.
- El precio se toma del catálogo del servidor y se conserva en cada línea.
- Las cantidades son enteros positivos; la moneda del laboratorio es ARS.
- No se permite stock disponible negativo.
- Cancelar antes del despacho libera la reserva una sola vez.
- Un pedido despachado no se cancela mediante esta operación.
- La indisponibilidad del ERP deja el pedido pendiente de evaluación, nunca aprobado.
- La confirmación de transporte puede ser incierta; no se repite sin consultar referencia.

## Indicadores propuestos

Medí duplicados por referencia de operación, rechazos por stock, tiempo de aceptación,
trabajos pendientes y antigüedad del evento más viejo sin entregar.
Como hipótesis local, evaluaremos p95 de aceptación inferior a 500 ms para diez clientes
concurrentes durante 60 segundos, con proveedores simulados y datos precargados.
Registrá hardware, muestra y resultados: no presentes ese objetivo como SLA de producción.

## Alcance

Un monolito modular, PostgreSQL, simuladores locales y RabbitMQ cuando el ejercicio lo
requiera. No se enviarán emails reales, no se cobrarán pagos ni se conectará un ERP real.
Stock y pedidos viven en PostgreSQL; el simulador ERP es autoridad de deuda en este caso.
