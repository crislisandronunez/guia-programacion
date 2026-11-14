# 12 — Testing, diagnóstico y recorrido final

## Lectura
Secciones 19–20 de la [guía](../../guias/Glosario-Spring-MongoDB.md).

## Consigna
Prepará @DataMongoTest con MongoDBContainer y configuración dinámica. Testcontainers
debe iniciar replica set. Aislá colecciones/datos por prueba y no uses el volumen local.
No supongas rollback automático de todos los tests de Mongo; definí limpieza explícita.

Agregá pruebas de índices reales, errores de duplicado, update concurrente, rollback
multi-documento, agregación Decimal128, geodatos y GridFS.
Diagnosticá timeout, índice faltante, BSON incompatible y documento demasiado grande.
Exponé ProblemDetail con códigos, sin consultas o trazas internas.

Recorrido: crear catálogo y cliente, armar pedido, confirmar dos pedidos competidores,
consultar ventas, buscar sucursal cercana, adjuntar archivo y cancelar una vez.

## Experimentos y aceptación
- mvn verify con Docker activo y pruebas efectivamente ejecutadas.
- La colección Postman permite repetir el recorrido con referencias nuevas.
- mongosh verifica índices, tipos BSON, stock y documentos históricos.
- Hay evidencia de explain para consultas importantes.
- Inicialización e índices se repiten sin destruir datos.
- Informe ejercicios/respuestas/12-informe.md con límites, mediciones y cobertura.
- Comparación con JPA: referencias, dirty checking, esquema, atomicidad y consultas.

## Para explicar
Sharding, replica sets multinodo y API reactiva quedan conceptuales/extensiones. ¿Qué cambios justificarían incorporarlos?

Volvé al [índice](README.md) y completá tu matriz de cobertura.
