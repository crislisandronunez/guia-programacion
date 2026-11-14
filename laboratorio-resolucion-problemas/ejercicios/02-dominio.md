# 02 — Modelar dominio y contratos

## Objetivo
Leé las secciones 4, 11 y 16 de la [guía](../../guias/Resolucion-de-Problemas.md).

## Consigna
Agregá Flyway y su soporte PostgreSQL. Creá migraciones para clientes, productos,
pedidos, líneas y reservas. Mantené ddl-auto: validate.
Separá módulos en paquetes: ventas, inventario, credito y operaciones.

Pedido es raíz del agregado: nadie modifica sus líneas saltando sus reglas.
Usá objetos de valor para importe ARS y cantidad. Calculá totales con precios del servidor,
no con totales enviados por el cliente; conservá el precio aplicado por línea.
Rechazá IDs repetidos de producto en la solicitud o normalizalos con una regla explícita.

Definí estas transiciones:
- Creación → PENDIENTE_EVALUACION.
- Evaluación → APROBADO o RECHAZADO.
- APROBADO → EN_PREPARACION → DESPACHADO.
- PENDIENTE_EVALUACION, APROBADO o EN_PREPARACION → CANCELADO.

Implementá POST /api/pedidos, GET /api/pedidos/{id} y POST para evaluar, preparar,
despachar y cancelar. En esta etapa, crédito usa un simulador local inmediato.
La aprobación reserva stock y la cancelación lo libera; al despachar se consume
la reserva. Definí existencia física y cantidad reservada por separado.

## Aceptación
- Cliente inactivo o con deuda no obtiene aprobación.
- Cantidad cero, negativa o precio manipulado no produce un pedido válido incorrecto.
- La falta de stock impide aprobar todas las líneas; no quedan reservas parciales.
- Despachado no vuelve a preparación ni puede cancelarse.
- Reiniciar conserva datos; una base vacía se inicializa con migraciones.
- Cada transición permitida y prohibida tiene una prueba.

## Entrega
Diagrama de estados, esquema, DTO, solicitudes Postman y consultas DBeaver para reconstruir
pedido y reservas. Usá los datos del caso mediante inicializador exclusivo de lab.

Siguiente: [03](03-concurrencia.md).
