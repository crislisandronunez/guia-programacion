# 03 — Tipos, embebidos y columnas especiales

## Lectura
Secciones 3 y 5 de la [guía](../../guias/Glosario-Spring-Data-JPA.md).

## Consigna
Agregá Direccion @Embeddable dos veces a Cliente, con @AttributeOverrides para envío
y facturación. Incorporá un AttributeConverter para un consentimiento S/N que valide
valores desconocidos y defina qué significa null.

Producto tendrá etiquetas @ElementCollection y atributos adicionales JSONB mediante
un mapping de Hibernate compatible con la versión del proyecto. Conservá precio,
stock y SKU como columnas consultables, no ocultos dentro del JSON.

Probá Instant para auditoría técnica y LocalDate para una fecha sin hora.
Agregá una descripción larga: compará TEXT con @Lob en PostgreSQL y elegí explícitamente
el mapping que realmente produce la representación deseada.

## Aceptación
- Ambas direcciones se almacenan sin pisar columnas.
- El converter hace round trip y rechaza datos fuera del contrato.
- Etiquetas sobreviven a flush/clear; duplicados se manejan según la colección elegida.
- JSON se recupera con tipos y campos esperados; una consulta nativa puede filtrar un atributo.
- Descripción larga y fechas se recuperan sin truncamiento o cambio de significado.
- Una migración agrega cada columna con política para filas existentes.

## Pregunta
¿Qué parte del mapping es JPA portable y cuál depende de Hibernate/PostgreSQL?
¿Por qué @Lob String no debe asumirse equivalente a TEXT en cualquier motor?

Siguiente: [04](04-relaciones.md).
