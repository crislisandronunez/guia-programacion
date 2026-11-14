# 01 — Seguridad por defecto y filtros

## Lectura

Secciones 1–3, 14 de la [guía](../../guias/Glorasio-Spring-Security.md).

## Consigna

Ejecutá la base sin modificarla. Compará health, una ruta protegida y /login con Accept
JSON y HTML. En Postman desactivá temporalmente seguir redirecciones para observar el
302 original. Probá Basic con el usuario temporal de la consola, sin guardar su clave.

Dibujá el recorrido DelegatingFilterProxy → FilterChainProxy → SecurityFilterChain →
DispatcherServlet. Activá DEBUG solo en una ejecución local y anotá los filtros presentes.
No copies credenciales ni cabeceras Authorization a la evidencia.

## Experimentos y aceptación

- Health público devuelve 200; la API anónima con Accept JSON devuelve 401.
- Distinguís un 302 de login de un 200 obtenido después de seguir una redirección.
- Basic incorrecto falla; Basic válido supera autenticación, pero no crea un controller inexistente.
- Identificás la cadena real; no asumís que el esquema resumido de la guía contiene todos los filtros.
- Explicás que Base64 no cifra credenciales y que Basic necesita TLS fuera de esta práctica local.

## Entrega

Código y pruebas cuando corresponda, solicitudes Postman y evidencia en
`ejercicios/respuestas/01.md`. En las tareas de diseño, entregá decisiones y diagramas;
no presentes una simulación como integración real.

## Para explicar

¿Por qué recibir 401 en una ruta inexistente no demuestra que el endpoint esté implementado?

Siguiente: [Cadena propia y reglas por ruta](02-reglas-http.md).

