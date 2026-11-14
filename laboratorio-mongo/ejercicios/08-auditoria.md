# 08 — Auditoría, conversores y evolución

## Lectura
Secciones 14, 18 de la [guía](../../guias/Glosario-Spring-MongoDB.md).

## Consigna
Activá @EnableMongoAuditing, fechas y AuditorAware con identidad demo explícita en lab.
Añadí un converter de un objeto de valor y probá escritura/lectura de su BSON.
Compará alias de tipo y _class; no suprimas metadatos si el modelo necesita polimorfismo.

Introducí schemaVersion y una evolución de campo: lectura compatible, backfill por lotes
y retiro posterior del formato antiguo. Cada lote puede reanudarse.
Agregá un validador $jsonSchema para campos esenciales tras preparar datos existentes.

## Experimentos y aceptación
- createdAt permanece y modifiedAt cambia al guardar.
- Probá también updates directos: no asumas que todos disparan callbacks de auditoría;
  actualizá metadatos explícitamente cuando corresponda.
- El converter conserva valor y rechaza representaciones inválidas.
- Documentos antiguos siguen legibles durante la transición.
- Repetir el backfill no aplica dos veces una transformación.
- Auditoría no contiene credenciales ni payloads sensibles.

## Para explicar
¿Quién decide la compatibilidad cuando conviven dos versiones de la aplicación?

Siguiente: [09](09-transacciones.md).
