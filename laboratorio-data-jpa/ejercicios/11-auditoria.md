# 11 — Auditoría, soft delete y excepciones

## Lectura
Secciones 3, 17 y 18 de la [guía](../../guias/Glosario-Spring-Data-JPA.md).

## Consigna
Activá auditoría Spring Data con fechas y AuditorAware.
Para pruebas usá un actor controlado; para HTTP identificá explícitamente el actor
como demo o incorporá autenticación. No presentes un header libre como identidad verificada.

Implementá desactivación/borrado lógico de productos mediante servicio transaccional.
Las búsquedas de catálogo excluyen eliminados; pedidos históricos conservan acceso a
su producto. Definí que el SKU no se reutiliza, aunque el producto esté eliminado.
Compará esta política explícita con filtros globales de Hibernate y su impacto en relaciones.

Implementá un handler de errores: validación 400, ausencia 404, unicidad/FK/versión 409.
No traduzcas todo fallo SQL a conflicto: una base inaccesible es otro problema.
Los callbacks deben ser cortos, sin llamadas externas.

## Aceptación
- createdAt no cambia al editar y modifiedAt sí.
- Auditoría técnica y un historial de cambios, si lo agregás, se distinguen.
- Producto eliminado desaparece del catálogo, pero un pedido viejo sigue consultable.
- UNIQUE sigue impidiendo SKU repetido.
- Bulk updates de la etapa 08 tienen política explícita para auditoría.
- Conflictos de persistencia detectados al flush/commit llegan a la respuesta adecuada.
- Mensajes no filtran SQL, credenciales ni trazas internas.

## Pregunta
¿Por qué soft delete no es automáticamente la mejor opción para todas las tablas?

Siguiente: [12](12-integracion.md).
