# 09 — Authorities y ciclo de vida del token

## Lectura

Secciones 9, 15, 24–25 de la [guía](../../guias/Glorasio-Spring-Security.md).

## Consigna

Definí el contrato de claims para identidades del caso y un JwtAuthenticationConverter.
Compará SCOPE_ de scopes OAuth2 con ROLE_ y permisos propios. Aceptá autoridades
solo de claims validadas del issuer esperado y hacé explícita cualquier traducción.

Prepará una matriz de token válido con permiso suficiente/insuficiente. Diseñá un
documento sobre duración, cambio de roles, revocación, rotación de claves y refresh.
Implementá en tests una rotación local controlada con claves de distinto kid y una
política explícita de retiro; no conviertas este laboratorio en un authorization server.

## Experimentos y aceptación

- Un token de Bruno nunca se transforma en Ana por un parámetro del request.
- Token válido sin permiso devuelve 403; token inválido devuelve 401.
- Un claim arbitrario admin=true no eleva privilegios.
- Tokens de prueba para Ana, Bruno y admin cumplen la misma matriz de propiedad.
- La rotación admite las claves previstas y rechaza una clave retirada según la política local.
- Documentás que logout web no invalida un JWT ya emitido.
- Refresh, su rotación y detección de reutilización se describen como responsabilidad del emisor, no como función ya implementada.

## Entrega

Código y pruebas cuando corresponda, solicitudes Postman y evidencia en
`ejercicios/respuestas/09.md`. En las tareas de diseño, entregá decisiones y diagramas;
no presentes una simulación como integración real.

## Para explicar

¿Por qué cambiar un rol en el servidor no cambia automáticamente un access token autocontenido vigente?

Siguiente: [OAuth2 y OpenID Connect](10-oauth2-oidc.md).

