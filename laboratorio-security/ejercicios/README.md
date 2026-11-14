# Consignas de Spring Security

| Etapa | Consigna | Secciones de la guía |
|---|---|---|
| 01 | [Seguridad por defecto y filtros](01-seguridad-inicial.md) | 1–3, 14 |
| 02 | [Cadena propia y reglas por ruta](02-reglas-http.md) | 4–5, 20 |
| 03 | [Usuarios y contraseñas](03-usuarios-passwords.md) | 6–8 |
| 04 | [SecurityContext y respuestas de error](04-contexto-errores.md) | 8, 11, 20, 23 |
| 05 | [Roles, permisos y propiedad de recursos](05-metodos-propiedad.md) | 9–10, 25 |
| 06 | [Formulario, sesión y logout](06-sesiones-login.md) | 12–13 |
| 07 | [CSRF y CORS](07-csrf-cors.md) | 17–18 |
| 08 | [API Bearer y validación JWT](08-cadenas-jwt.md) | 3–4, 12, 15, 24 |
| 09 | [Authorities y ciclo de vida del token](09-claims-ciclo-token.md) | 9, 15, 24–25 |
| 10 | [OAuth2 y OpenID Connect](10-oauth2-oidc.md) | 16 |
| 11 | [Cabeceras, cookies y HTTPS](11-cabeceras-tls.md) | 19, 25 |
| 12 | [Bloqueo, remember-me y auditoría](12-cuentas-auditoria.md) | 21, 25 |
| 13 | [Pruebas y diagnóstico de seguridad](13-testing-diagnostico.md) | 22–23 |
| 14 | [Integración y revisión final](14-integracion.md) | 1–25, especialmente 24–25 |

## Método

Leé el [caso](../caso/portal-solicitudes.md). La base arranca y comprueba dos comportamientos
predeterminados; las soluciones de los ejercicios quedan a cargo del estudiante.

1. Leé las secciones indicadas y anticipá la decisión de seguridad.
2. Implementá la funcionalidad y probá tanto acceso permitido como denegado.
3. Verificá que un rechazo no produjo cambios ni reveló datos.
4. Agregá solicitudes Postman y guardá respuestas en ejercicios/respuestas/NN.md.
5. Ejecutá mvn verify y documentá qué verifican realmente los tests.

## Convenciones

- Java 21, Boot 3.5.16 y dependencias Security administradas por el parent.
- Paquete raíz com.estudio.security; DTO separados, UUID, Instant y Clock inyectable.
- Datos en memoria y ficticios; ninguna solución necesita enviar correo ni usar cuentas reales.
- Reglas HTTP del caso: 201 crear, 200 consultar/operar, 400 validar, 401 autenticar,
  403 permiso ausente, 404 recurso no visible y 409 estado incompatible.
- Un error CSRF puede ocurrir antes de autenticar y dar 403: no lo confundas con permisos.
- Pruebas de propiedad sobre datos de dos usuarios, además del administrador.
- Concurrencia mediante barreras y tareas con timeouts, no Postman secuencial.
- Configuraciones deliberadamente defectuosas en tests aislados.
- Hashes, tokens, cookies de sesión y claves no aparecen en evidencias ni colecciones.
- El test inicial de API anónima continúa como regresión; ampliá la cobertura al implementar.
- La seguridad por método se verifica sobre un bean administrado y su proxy.

## Evolución de mecanismos

Las etapas 02–07 usan sesión, formulario y Basic de estudio con CSRF conservado.
En 08 la API pasa exclusivamente a Bearer stateless; web conserva sesión y CSRF.
Actualizá las pruebas de autenticación de API al cambiar el mecanismo y mantené
pruebas aisladas para el aprendizaje de Basic. No acumules configuraciones incompatibles.

La cadena propia debe contemplar Actuator explícitamente: al declararla, la seguridad
automática de Boot cede. Exponé solo health básico en el recorrido normal.

## Cobertura y fuentes

El recorrido trabaja los conceptos principales de las 25 secciones; OIDC completo,
persistencia, MFA y autorización avanzada tienen extensiones identificadas.
JWT se implementa con Resource Server. El filtro JWT artesanal de la guía se analiza
para entender sus responsabilidades, no se copia como solución requerida.

Contrastá el ejercicio 07 con la [referencia CSRF](https://docs.spring.io/spring-security/reference/6.5/servlet/exploits/csrf.html)
y 08–09 con [Resource Server JWT](https://docs.spring.io/spring-security/reference/6.5/servlet/oauth2/resource-server/jwt.html).

