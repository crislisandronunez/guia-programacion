# 05 — Roles, permisos y propiedad de recursos

## Lectura

Secciones 9–10, 25 de la [guía](../../guias/Glorasio-Spring-Security.md).

## Consigna

Activá @EnableMethodSecurity y protegé operaciones del servicio con @PreAuthorize
y reglas de propiedad. Definí una forma consistente de resolver el dueño sin confiar
en IDs enviados como identidad. Usá el contrato 404 para recurso ajeno y 403 para
ausencia de permiso global.

Compará hasRole("ADMIN") con hasAuthority("ROLE_ADMIN"). Probá el servicio a través
del proxy de Spring, además de HTTP. Reproducí self-invocation en una variante aislada
y corregí la separación de responsabilidades.

## Experimentos y aceptación

- Ana no consulta ni cancela recursos de Bruno, aun con UUID válido conocido.
- Los listados y sus totales no filtran datos ajenos.
- Admin consulta todas y aprueba, pero no cancela solicitudes ajenas.
- Dos aprobaciones concurrentes producen una transición y un conflicto.
- Una llamada al servicio desde otro bean mantiene la autorización.
- La prueba distingue proxy de objeto construido con new.
- @PostAuthorize no se usa para autorizar una escritura que ya ocurrió.

## Entrega

Código y pruebas cuando corresponda, solicitudes Postman y evidencia en
`ejercicios/respuestas/05.md`. En las tareas de diseño, entregá decisiones y diagramas;
no presentes una simulación como integración real.

## Para explicar

¿Por qué ocultar un botón o usar UUID no reemplaza comprobar permisos en el servidor?

Siguiente: [Formulario, sesión y logout](06-sesiones-login.md).

