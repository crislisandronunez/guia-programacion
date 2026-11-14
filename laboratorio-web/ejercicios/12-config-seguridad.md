# 12 — Configuración y seguridad web

## Lectura

Secciones 3, 15–16 de la [guía](../../guias/Glosario-Springboot-web.md).

## Consigna

Creá propiedades tipadas para proveedor, uploads y límites, con validación.
Agregá un perfil local con context-path /portal y comprobá URLs, Location, CSS y formulario.
No agregues @EnableWebMvc para personalizaciones que admite WebMvcConfigurer.

Incorporá Security y sus tests con las identidades/permisos del caso. Usá formLogin
y Basic de práctica, CSRF activo y respuestas JSON para errores API.
Configurá una URL de login accesible y logout por POST; mantené health público.

## Experimentos y aceptación

- Propiedades inválidas impiden el arranque con error útil.
- La API conserva /api/v1 bajo el context-path sin duplicarlo en Location.
- Usuario anónimo obtiene 401 JSON en API; lector no puede escribir.
- Un POST sin CSRF falla aunque tenga identidad válida.
- La web permite login y logout con cookie de sesión.
- No se usa una cookie de preferencia como identidad.
- Los tests anteriores se actualizan para atravesar la seguridad real.
- JWT completo queda como extensión o continuación en laboratorio-security, sin filtro artesanal.

## Entrega

Código, pruebas, solicitudes Postman y respuestas en
`ejercicios/respuestas/12.md`. Para vistas, agregá evidencia del comportamiento en navegador.

## Para explicar

¿Por qué desactivar CSRF globalmente para hacer funcionar Postman cambia la protección del formulario?

Siguiente: [OpenAPI y documentación del contrato](13-openapi.md).

