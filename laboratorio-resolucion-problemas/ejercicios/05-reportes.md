# 05 — Lecturas, caché y reportes

## Objetivo
Leé la sección 9 de la [guía](../../guias/Resolucion-de-Problemas.md).

## Consigna
Generá 10.000 pedidos ficticios desde un comando exclusivo de lab.
Implementá GET /api/pedidos con filtros por cliente, estado y fechas, orden estable
(fecha e ID), paginación y tamaño máximo 100. Usá proyecciones y evitá N+1.

Creá POST /api/reportes que encole una exportación y GET /api/reportes/{id} para estado
y descarga. Procesá por páginas/keyset sin cargar el conjunto completo.
Definí si el reporte refleja un corte estable o datos cambiantes; no lo dejes implícito.

Medí antes y después con EXPLAIN ANALYZE sobre datos de prueba. Agregá índices según
consultas observadas. Incorporá caché con TTL para un resumen no crítico y una proyección
de lectura actualizable de forma idempotente (CQRS sencillo dentro del monolito).
No uses esa proyección eventual para decidir stock.

## Aceptación
- Límite de página validado y orden determinista.
- Número de consultas no crece una por cada fila devuelta.
- Reporte grande usa memoria acotada, conserva filtros y puede reanudarse o reiniciarse
  sin presentar un archivo parcial como completo.
- Documentás atraso permitido y fecha de actualización del resumen.
- Comparás latencia, consultas y memoria con el mismo dataset.
- Invalidación/reconstrucción de caché y proyección tiene pruebas.

## Pregunta
¿Qué problema justifica separar el modelo de lectura? ¿Necesitás otra base para hacerlo?

Siguiente: [06](06-lotes.md).
