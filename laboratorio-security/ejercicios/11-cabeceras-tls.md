# 11 — Cabeceras, cookies y HTTPS

## Lectura

Secciones 19, 25 de la [guía](../../guias/Glorasio-Spring-Security.md).

## Consigna

Inventariá las cabeceras por defecto y agregá una CSP acorde con el contenido realmente
servido. Compará X-Content-Type-Options, protección contra framing y Cache-Control.
No desactives todas las cabeceras para solucionar una página de prueba.

Creá un perfil HTTPS local con certificado de desarrollo en local/, fuera de Git.
Documentá generación, confianza local y contraseña mediante entorno. Comprobá cookies
HttpOnly, Secure y SameSite bajo HTTPS. Si simulás un proxy, delimitá qué cabeceras
reenviadas puede confiar la aplicación.

## Experimentos y aceptación

- Recursos públicos conservan las cabeceras de seguridad.
- Comprobás HSTS bajo HTTPS y explicás por qué no lo esperás igual en HTTP local.
- Cookie Secure no se usa como excusa para quitar protección al perfil HTTPS.
- Logout y renovación de CSRF siguen funcionando bajo TLS.
- Una cabecera X-Forwarded-Proto enviada por un cliente no se considera confiable sin política de proxy.
- La CSP se ajusta a recursos reales y no presupone eliminar todo XSS.

## Entrega

Código y pruebas cuando corresponda, solicitudes Postman y evidencia en
`ejercicios/respuestas/11.md`. En las tareas de diseño, entregá decisiones y diagramas;
no presentes una simulación como integración real.

## Para explicar

¿Qué protección da HttpOnly y qué riesgos permanecen si hay JavaScript malicioso en el mismo origen?

Siguiente: [Bloqueo, remember-me y auditoría](12-cuentas-auditoria.md).

