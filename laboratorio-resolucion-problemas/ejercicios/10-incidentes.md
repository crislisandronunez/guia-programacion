# 10 — Incidentes y demostración final

## Objetivo
Leé las secciones 14–17 de la [guía](../../guias/Resolucion-de-Problemas.md).

## Consigna
Agregá correlationId validado o generado al entrar, respuesta HTTP y logs estructurados.
Propagalo por jobs y eventos; limpiá MDC al terminar. No registres secretos ni payloads
sensibles. Exponé métricas de negocio y tiempos con acceso administrativo/local.

Prepará escenarios de fallo exclusivos de lab:
1. Consulta lenta por índice faltante o N+1.
2. ERP caído con jobs acumulados.
3. RabbitMQ caído con outbox pendiente.
4. Mensaje duplicado después del commit.
5. Saga con transporte incierto.
6. Importación inválida y lote interrumpido.

Para cada incidente, registrá impacto, hipótesis, evidencia, mitigación, causa y prevención.
Compará las hipótesis con logs, métricas y consultas SQL antes de reiniciar servicios.
Escribí un post-mortem sin culpa con acciones concretas y pruebas de regresión.

## Desafío integrador
Creá dos pedidos concurrentes por la última unidad y repetí uno por mala conexión.
El resultado debe tener un único pedido por clave, a lo sumo una reserva de la unidad,
evaluación de crédito trazable y ninguna preparación antes de completar sus requisitos.
Interrumpí la entrega del evento, recuperala y verificá una sola orden de preparación.
Despachá, ejecutá el cierre dos veces y conciliá el reporte.

Medí el objetivo local del caso con carga reproducible y reportá p50, p95, errores,
tamaño de muestra, dataset y entorno. Un resultado que no alcanza el objetivo se analiza;
no se cambia la métrica para declarar éxito.

## Aceptación final
- mvn verify con Docker activo; pruebas de integración efectivamente ejecutadas.
- Postman permite repetir el recorrido con referencias nuevas.
- DBeaver permite reconstruir pedido, stock, auditoría, outbox y saga.
- Hay un runbook de arranque, diagnóstico y recuperación.
- Matriz final de las 17 secciones: implementado, simulado o conceptual.
- ADR actualizado: qué mantener simple y qué justificaría otra arquitectura.
- Informe en ejercicios/respuestas/10-informe.md con limitaciones conocidas.

¿La solución redujo el problema original o únicamente agregó componentes?
