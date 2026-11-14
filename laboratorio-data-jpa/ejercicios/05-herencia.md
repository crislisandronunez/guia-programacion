# 05 — Herencia y relaciones con atributos

## Lectura
Secciones 4, 6 y 7 de la [guía](../../guias/Glosario-Spring-Data-JPA.md).

## Consigna
Introducí una @MappedSuperclass para campos técnicos compartidos.
Convertí Producto en una jerarquía SINGLE_TABLE con discriminador: ProductoFisico
y ProductoDigital. Definí peso/envío para físico y referencia de descarga ficticia
para digital. Migrá productos existentes como físicos; asegurá restricciones válidas
según discriminador y ajustá DTO.

Modelá Producto–Categoria con una tabla intermedia para practicar ManyToMany sin REMOVE
en cascada. Para Producto–Proveedor, usá entidad OfertaProveedor porque la relación
tiene costo y plazo. Elegí PK técnica con UNIQUE(producto, proveedor); compará con
@EmbeddedId/@MapsId mediante una prueba aislada.

## Aceptación
- Consulta polimórfica obtiene ambos subtipos.
- Un producto digital no exige stock físico ni peso.
- Restricciones del esquema reflejan qué campos pueden ser nulos por subtipo.
- Quitar una categoría de un producto no elimina la categoría.
- No hay dos ofertas para la misma pareja.
- equals/hashCode no cambia de forma que rompa un Set después de persistir.
- Compará SQL y trade-offs de SINGLE_TABLE, JOINED y TABLE_PER_CLASS; implementá solo
  la estrategia elegida en el modelo principal.

## Pregunta
¿Por qué @MappedSuperclass no crea por sí sola una jerarquía consultable polimórficamente?

Siguiente: [06](06-consultas.md).
