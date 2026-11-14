# 08 — API Bearer y validación JWT

## Lectura

Secciones 3–4, 12, 15, 24 de la [guía](../../guias/Glorasio-Spring-Security.md).

## Consigna

Agregá spring-boot-starter-oauth2-resource-server sin versión independiente.
Creá una cadena @Order para /api/** con autenticación Bearer y STATELESS.
Conservá una cadena de respaldo para web, login, logout, público y health.
Basic deja de autenticar la API. Desactivá CSRF solo en la cadena API después de
verificar que no acepta sesión, Basic ni tokens enviados automáticamente en cookies.

Usá JwtDecoder y soporte nativo Resource Server. Generá un par RSA efímero en tests,
firmá tokens de prueba con encoder de biblioteca y configurá validación de firma,
algoritmo permitido, issuer, audience, expiración y not-before. Exigí exp y sub no vacío
como contrato propio: no presupongas que todas las claims son obligatorias por defecto.
No implementes un filtro JWT criptográfico propio ni un /login emisor improvisado.

## Experimentos y aceptación

- JWT válido permite el acceso; expirado, prematuro, modificado o firmado por otra clave da 401.
- Issuer, audience o algoritmo incorrectos, exp ausente y sub vacío se rechazan.
- Los límites temporales contemplan el clock skew elegido sin esperas largas.
- Sesión web y Basic no autentican /api; Bearer no crea sesión web.
- Una ruta fuera de /api conserva la cadena de respaldo.
- Probás tokens firmados reales: with(jwt()) no valida firma ni JwtDecoder.
- El recurso valida tokens; emitirlos para usuarios es responsabilidad separada.

## Entrega

Código y pruebas cuando corresponda, solicitudes Postman y evidencia en
`ejercicios/respuestas/08.md`. En las tareas de diseño, entregá decisiones y diagramas;
no presentes una simulación como integración real.

## Para explicar

¿Qué protege la firma y qué datos siguen siendo legibles en un JWT firmado?

Siguiente: [Authorities y ciclo de vida del token](09-claims-ciclo-token.md).

