# 01 — Documentos, BSON e identificadores

## Lectura
Secciones 1–4 de la [guía](../../guias/Glosario-Spring-MongoDB.md).

## Consigna
Modelá Producto con @Document, ID ObjectId, SKU, nombre, stock, etiquetas y atributos.
Representá dinero con BigDecimal y @Field(targetType = DECIMAL128). Usá DTO para JSON.
Creá MongoRepository y POST/GET /api/productos. Validá SKU, importes y cantidades.

Compará String @Id que puede convertirse a ObjectId con un ID String explícito mediante
@MongoId(FieldType.STRING) en una colección de prueba. No mezcles tipos de _id por accidente.

## Experimentos y aceptación
- En mongosh, $type confirma ObjectId y decimal en los campos esperados.
- Un precio 0.10 conserva su valor tras escribir y leer.
- ID mal formado devuelve 400; ID válido ausente devuelve 404.
- El documento conserva arrays y objetos anidados.
- Cambiar un objeto Java sin guardarlo no modifica Mongo: no hay dirty checking de JPA.
- Compará insert y save sobre un ID existente; no uses reemplazo como parche parcial.

## Para explicar
¿Qué conceptos de JPA no se trasladan directamente a MongoRepository?

Siguiente: [02](02-modelado.md).
