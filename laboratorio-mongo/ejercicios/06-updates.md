# 06 — Actualizaciones parciales y atomicidad

## Lectura
Secciones 11 de la [guía](../../guias/Glosario-Spring-MongoDB.md).

## Consigna
Agregá PATCH /api/productos/{id} con campos permitidos y diferencia explícita entre
omitido y null. Usá $set, $unset, $inc y $addToSet en operaciones específicas.
El stock no se edita mediante un reemplazo completo de documento.

Implementá una reserva de una unidad usando findAndModify con criterio stock >= cantidad,
incremento negativo y retorno del documento actualizado.
Compará con read-modify-save en una prueba deliberadamente concurrente.
Para operaciones de array practicá operadores posicionales y arrayFilters.

## Experimentos y aceptación
- Actualizar nombre no elimina otros campos ni cambia stock.
- Veinte intentos por una única unidad permiten uno y dejan stock cero.
- Cantidad cero o negativa se rechaza antes de ejecutar el update.
- Distinguí matchedCount de modifiedCount y retorno null de findAndModify.
- Upsert usa $setOnInsert y un identificador único; no crea duplicados al competir.
- Una operación de un documento es atómica; dos llamadas sucesivas no lo son en conjunto.

## Para explicar
¿Cómo impedirías que repetir una reserva por un timeout descuente otra unidad? Diseñá la referencia idempotente.

Siguiente: [07](07-agregaciones.md).
