# 03 — Índices, unicidad y expiración

## Lectura
Secciones 6 de la [guía](../../guias/Glosario-Spring-MongoDB.md).

## Consigna
Creá un inicializador versionado de índices, repetible, con auto-index-creation:false.
Índices: SKU único, email normalizado único, compuesto para pedidos por cliente/fecha,
multikey para etiquetas y texto sobre nombre/descripcion. Prepará datos duplicados en
una colección aislada y resolvelos antes de intentar crear UNIQUE.

Creá sesiones ficticias con expiresAt BSON Date y un índice TTL expireAfterSeconds:0.
La API verifica expiresAt al leer: no depende de que el borrado físico ya ocurrió.

## Experimentos y aceptación
- getIndexes muestra nombres, campos, orden y opciones.
- Dos inserciones concurrentes del mismo SKU dejan un documento.
- Ausencia y null en campos únicos tienen una política validada; estudiá índice parcial.
- explain("executionStats") compara una consulta antes/después del índice.
- La sesión vencida se rechaza aunque su documento todavía esté presente.
- Observá eliminación TTL eventual con espera acotada; no exijas el segundo exacto.
- Anotá qué índices se usan y el costo que agregan a escrituras.

## Para explicar
¿Por qué @Indexed en una clase no prueba que el índice exista en el servidor?

Siguiente: [04](04-consultas.md).
