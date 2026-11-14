# 04 — Procesos lentos y proveedores inestables

## Objetivo
Leé las secciones 7 y 8 de la [guía](../../guias/Resolucion-de-Problemas.md).

## Consigna
Hacé asíncrona la evaluación de pedidos: POST devuelve 202, ID y Location de consulta.
Persistí trabajos con estado, intentos, siguiente ejecución y lease de procesamiento.
Un worker reclama trabajo en transacciones cortas y recupera leases vencidos tras caídas.

Creá un simulador HTTP de crédito con respuestas: aprobado, deuda, 503, timeout y recuperación.
Configurá timeout de conexión/lectura y presupuesto total. Agregá reintentos acotados
con backoff y jitter solo para fallos transitorios, y un circuit breaker.
Elegí una biblioteca compatible al implementarlo y documentá su configuración.

No aprobar por fallback cuando el crédito es desconocido. Mientras haya incertidumbre,
el pedido permanece pendiente. Capturá versión antes de consultar y revalidá estado al
aplicar el resultado: una respuesta tardía no debe revivir un pedido cancelado.

## Experimentos y aceptación
- Una consulta lenta no retiene la conexión HTTP original durante todo el proceso.
- Deuda o cliente inactivo no se reintentan como errores técnicos.
- Circuito abierto evita llamadas y permite sondas de recuperación.
- Reiniciar tras reclamar un trabajo permite retomarlo al vencer el lease.
- Dos workers no aplican dos aprobaciones.
- Saturación del executor/cola tiene límite y respuesta observable, no memoria ilimitada.
- Compará @Async post-commit en memoria con el trabajo persistido; demostración de pérdida
  al reiniciar en la ventana correspondiente.

## Pregunta
¿Por qué reintentar una consulta no tiene el mismo riesgo que reintentar una contratación
de transporte con respuesta perdida?

Siguiente: [05](05-reportes.md).
