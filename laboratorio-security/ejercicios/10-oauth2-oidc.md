# 10 — OAuth2 y OpenID Connect

## Lectura

Secciones 16 de la [guía](../../guias/Glorasio-Spring-Security.md).

## Consigna

Dibujá resource owner, cliente, authorization server y resource server.
Diferenciá OAuth2 de OIDC, access token de ID token y scopes de roles de aplicación.
Compará Authorization Code con PKCE y client_credentials.

Agregá pruebas de autorización con oauth2Login() y oidcLogin() en configuración aislada,
y describí exactamente qué simulan. Como extensión, conectá un IdP local con usuarios
ficticios y el starter oauth2-client en un perfil explícito. Documentá su arranque,
redirect URI exacta, issuer y mapeo estable (issuer, subject) hacia identidad local.
El recorrido principal debe seguir ejecutándose sin ese proveedor.

## Experimentos y aceptación

- No usás un ID token como credencial Bearer de la API.
- Una identidad OIDC no obtiene ADMIN por un email parecido al del administrador.
- Explicás state, nonce y PKCE y qué parte valida el cliente.
- Un cliente máquina no se trata como alumno sin una política explícita.
- Distinguís pruebas simuladas de un flujo completo con redirección y canje real.
- Si realizás la extensión, probás callback inválido, cierre de sesión local y proveedor caído.
- Ningún secreto del cliente queda en Git ni en la colección exportada.

## Entrega

Código y pruebas cuando corresponda, solicitudes Postman y evidencia en
`ejercicios/respuestas/10.md`. En las tareas de diseño, entregá decisiones y diagramas;
no presentes una simulación como integración real.

## Para explicar

¿Por qué hacer logout en tu aplicación no necesariamente cierra la sesión del proveedor?

Siguiente: [Cabeceras, cookies y HTTPS](11-cabeceras-tls.md).

