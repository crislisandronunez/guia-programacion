# 11 — Pruebas de autenticación y permisos

## Lectura

Secciones 15 de la [guía](../../guias/Glosario-Testing.md).

## Consigna

Agregá Security y spring-security-test. Definí usuarios locales ana y bruno con
ROLE_CLIENTE y admin con ROLE_ADMIN. La lectura requiere autenticación; crear y
cancelar requieren CLIENTE; admin puede leer todos. Conservá CSRF y usá Basic como
mecanismo local de práctica con respuesta 401 JSON para la API.

Importá la cadena real en los slices y compará @WithMockUser, user(), csrf() y Basic
con credenciales reales. Mantené health público para el smoke test.

## Experimentos y aceptación

- Sin identidad: 401; sin permiso global: 403.
- Ana no ve ni cancela datos de Bruno; detalle ajeno responde 404.
- Los listados filtran por identidad y admin puede leerlos todos.
- POST con usuario válido pero sin CSRF da 403 sin cambios.
- Una prueba Basic real valida contraseña y UserDetailsService.
- @WithMockUser no se presenta como validación del mecanismo de login.
- Las pruebas de seguridad mantienen filtros habilitados.
- Si protegés métodos, la prueba invoca el bean a través de su proxy.

## Entrega

Pruebas y código mínimo del comportamiento, más evidencia y respuestas en
`ejercicios/respuestas/11.md`. Indicá qué falla si rompés la regla que estás verificando.

## Para explicar

¿Cómo distinguís un 403 por CSRF de uno causado por falta de rol?

Siguiente: [Tiempo, eventos y asincronía](12-tiempo-concurrencia.md).

