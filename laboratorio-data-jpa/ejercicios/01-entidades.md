# 01 — Del objeto a la tabla

## Lectura
Secciones 1–4 y 19 de la [guía](../../guias/Glosario-Spring-Data-JPA.md).

## Consigna
Agregá flyway-core y flyway-database-postgresql al POM. Creá V1 en
src/main/resources/db/migration con tablas clientes y productos, secuencias,
PK, UNIQUE, NOT NULL y CHECK para precios/stock no negativos.

Mapeá Cliente y Producto con @Entity, @Table y @Column. Usá Long generado por SEQUENCE;
alineá allocationSize y el incremento de la secuencia.
Cliente lleva nombre, email normalizado, estado como EnumType.STRING y LocalDate de alta.
Producto lleva SKU, nombre, precio NUMERIC(12,2) y stock entero.
Definí el máximo de importe admitido acorde con precisión/escala.

Agregá repositorios mínimos y una prueba PostgreSQL que persista y recupere ambas entidades.
La API CRUD completa se incorpora en la etapa siguiente.

## Experimentos y aceptación
- Base vacía arranca aplicando Flyway; un segundo arranque conserva datos.
- DBeaver muestra tipos, índices y restricciones esperados.
- Email o SKU duplicados fallan también si se intenta escribir sin pasar por el controller.
- Cantidades negativas fallan por restricción SQL.
- El precio vuelve con precisión decimal intacta tras flush/clear.
- Cambiar el mapping a una columna inexistente hace fallar validate en un test aislado.
- Documentá diferencia entre JPA, Hibernate, JDBC y Spring Data.

## Pregunta
¿Por qué @Column(nullable=false) no reemplaza validar un DTO? ¿Por qué los IDs de
secuencia pueden tener huecos y no representan cantidad de filas?

Siguiente: [02](02-repositorios.md).
