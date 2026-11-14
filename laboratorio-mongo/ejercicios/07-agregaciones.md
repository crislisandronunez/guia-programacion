# 07 — Pipelines y reportes

## Lectura
Secciones 12–13 de la [guía](../../guias/Glosario-Spring-MongoDB.md).

## Consigna
Implementá GET /api/reportes/ventas por período sobre pedidos CONFIRMADOS.
Pipeline con filtro, unwind de líneas, agrupación por SKU, suma de cantidad e importe
Decimal128, orden y límite. Pedidos cancelados quedan excluidos.
Compará @Aggregation y builder Aggregation de MongoTemplate.

Agregá $lookup para un dato actual del catálogo sin reemplazar el precio histórico.
Controlá documentos sin coincidencia y el efecto de unwind sobre filas vacías.
Usá proyecciones DTO y rangos temporales [desde, hasta).

## Experimentos y aceptación
- Un fixture pequeño permite calcular a mano totales y compararlos.
- No se duplican ventas al unir varias categorías o referencias.
- Los importes siguen siendo decimales en Mongo y Java.
- Filtro previo usa índices cuando aplica; filtros sobre datos calculados van después
  de la etapa que los produce, no necesariamente al inicio.
- Resultado vacío tiene un contrato definido.
- explain y medición muestran documentos examinados y devueltos.

## Para explicar
¿Por qué desnormalizar puede evitar lookup, pero obliga a decidir qué datos son históricos?

Siguiente: [08](08-auditoria.md).
