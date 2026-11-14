# 04 — SecurityContext y respuestas de error

## Lectura

Secciones 8, 11, 20, 23 de la [guía](../../guias/Glorasio-Spring-Security.md).

## Consigna

Implementá /api/me mediante Principal o @AuthenticationPrincipal. Inspeccioná Authentication
y distinguí anónimo, principal y authorities. Configurá un AuthenticationEntryPoint para
401 JSON en API y AccessDeniedHandler para 403 con codigo y mensaje genéricos.
Preservá los desafíos WWW-Authenticate que correspondan al mecanismo.

Compará errores de filtros con errores del controller. En una prueba de ejecutor
reutilizado observá el contexto de seguridad y compará propagación explícita con
DelegatingSecurityContextExecutor; cerrá el ejecutor.

## Experimentos y aceptación

- API anónima obtiene 401; usuario autenticado sin permiso obtiene 403.
- Un POST sin CSRF puede producir 403 antes de comprobar credenciales: identificás la causa.
- Un ControllerAdvice no captura automáticamente todos los fallos anteriores al DispatcherServlet.
- Una petición posterior no hereda identidad de la anterior.
- La tarea delegada usa el contexto previsto y el hilo reutilizado no filtra esa identidad.
- No establecés autenticación desde un header de nombre de usuario ni desde JSON no verificado.

## Entrega

Código y pruebas cuando corresponda, solicitudes Postman y evidencia en
`ejercicios/respuestas/04.md`. En las tareas de diseño, entregá decisiones y diagramas;
no presentes una simulación como integración real.

## Para explicar

¿Por qué guardar Authentication en SecurityContextHolder no equivale siempre a persistir una sesión?

Siguiente: [Roles, permisos y propiedad de recursos](05-metodos-propiedad.md).

