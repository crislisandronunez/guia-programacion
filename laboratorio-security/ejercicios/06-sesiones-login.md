# 06 — Formulario, sesión y logout

## Lectura

Secciones 12–13 de la [guía](../../guias/Glorasio-Spring-Security.md).

## Consigna

Agregá GET /web/me y usá el formulario generado de Spring para observar el login.
Mantené CSRF activo, sesión IF_REQUIRED y protección contra session fixation.
Probá persistencia de identidad mediante cookie entre peticiones y el logout por POST.
Compará SessionCreationPolicy sin asumir que NEVER e IF_REQUIRED son equivalentes.

En una variante de prueba configurá máximo de una sesión, con la infraestructura de
registro y eventos que requiera tu configuración. Elegí rechazar el nuevo login o
invalidar el anterior y documentá esa elección.

## Experimentos y aceptación

- Login correcto establece la identidad; incorrecto no permite /web/me.
- Si existía sesión antes del login, el identificador cambia al autenticar.
- POST /logout con CSRF invalida acceso mediante la sesión anterior.
- Un GET no ejecuta una mutación de dominio ni un logout silencioso.
- El límite de sesiones se verifica con dos clientes independientes.
- Distinguís expiración de sesión, borrar cookie en cliente e invalidación en servidor.

## Entrega

Código y pruebas cuando corresponda, solicitudes Postman y evidencia en
`ejercicios/respuestas/06.md`. En las tareas de diseño, entregá decisiones y diagramas;
no presentes una simulación como integración real.

## Para explicar

¿Qué tendría que hacer un login manual además de llamar a AuthenticationManager?

Siguiente: [CSRF y CORS](07-csrf-cors.md).

