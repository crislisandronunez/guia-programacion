# 13 — Pruebas y diagnóstico de seguridad

## Lectura

Secciones 22–23 de la [guía](../../guias/Glorasio-Spring-Security.md).

## Consigna

Completá una matriz de anónimo, Ana, Bruno, admin y cuenta bloqueada para cada operación.
Separá pruebas de reglas con @WithMockUser/user()/jwt() de pruebas de autenticación
con contraseña real y Bearer firmado. Conservá filtros en MockMvc.

Reproducí en tests aislados prefijo ROLE_ duplicado, regla general antes de la específica,
cadena sin cobertura, CSRF faltante y self-invocation. Si exploraste OncePerRequestFilter,
comprobá orden y doble registro servlet/Security con un contador, sin usarlo para
reemplazar el soporte JWT.

## Experimentos y aceptación

- Ningún test de seguridad desactiva filtros para hacer pasar el caso.
- Los casos denegados verifican también que no se modificó el repositorio.
- No confundís 403 por CSRF con 403 por permisos: cada test aísla su causa.
- Probás IDOR en detalle, listado y cancelación.
- Los tests de decoder rechazan firmas reales inválidas.
- Las configuraciones rotas no entran en el scan normal.
- Las evidencias muestran causa y corrección sin incluir credenciales.

## Entrega

Código y pruebas cuando corresponda, solicitudes Postman y evidencia en
`ejercicios/respuestas/13.md`. En las tareas de diseño, entregá decisiones y diagramas;
no presentes una simulación como integración real.

## Para explicar

¿Qué parte del flujo omite with(jwt()) y por qué aun así resulta útil?

Siguiente: [Integración y revisión final](14-integracion.md).

