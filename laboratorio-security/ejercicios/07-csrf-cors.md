# 07 — CSRF y CORS

## Lectura

Secciones 17–18 de la [guía](../../guias/Glorasio-Spring-Security.md).

## Consigna

Probá creación y cancelación con sesión: token CSRF válido, ausente e inválido.
Para Postman, incorporá al resolver el ejercicio un mecanismo acotado que entregue
el CsrfToken actual; mantené su cookie/sesión y renovalo después de login y logout.
No desactives la protección para lograr que pase el POST.

Configurá CORS para el origen local http://localhost:5173, métodos y headers concretos.
Probá preflight antes de autenticación y el request real. Recordá que localhost y
127.0.0.1 son orígenes distintos.

## Experimentos y aceptación

- POST sin CSRF o con token inválido falla sin cambiar datos.
- Con sesión y token correctos se ejecuta la operación autorizada.
- Un preflight permitido funciona sin cookie; la petición real sigue exigiendo identidad.
- Un origen no permitido no recibe autorización CORS.
- No combinás allowCredentials=true con origen comodín.
- Probás también en navegador: Postman no aplica la política CORS del navegador.
- Explicás por qué stateless, Basic o un JWT en cookie no bastan para descartar CSRF.

## Entrega

Código y pruebas cuando corresponda, solicitudes Postman y evidencia en
`ejercicios/respuestas/07.md`. En las tareas de diseño, entregá decisiones y diagramas;
no presentes una simulación como integración real.

## Para explicar

¿Por qué CORS no impide que un cliente fuera del navegador invoque tu API?

Siguiente: [API Bearer y validación JWT](08-cadenas-jwt.md).

