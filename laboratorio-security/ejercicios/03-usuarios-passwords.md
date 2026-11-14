# 03 — Usuarios y contraseñas

## Lectura

Secciones 6–8 de la [guía](../../guias/Glorasio-Spring-Security.md).

## Consigna

Registrá ana, bruno y admin en memoria con las autoridades del caso y hashes mediante
PasswordEncoder. Compará BCryptPasswordEncoder y DelegatingPasswordEncoder y explicá
el prefijo que identifica el algoritmo. Definí una política local de longitud compatible
con el encoder, incluyendo su límite en bytes para entradas UTF-8.

Implementá o adaptá UserDetailsService y observá DaoAuthenticationProvider y
AuthenticationManager. Probá usuario inexistente, contraseña errónea, cuenta deshabilitada
y bloqueada. Un provider personalizado queda como experimento aislado.

## Experimentos y aceptación

- Dos hashes del mismo texto pueden ser distintos, pero matches valida ambos.
- No comparás contraseñas mediante igualdad de hashes ni usás {noop}.
- Ana obtiene solo sus autoridades; no reaparece el usuario generado de Boot.
- Credenciales inválidas no distinguen públicamente usuario inexistente de clave incorrecta.
- DTO, logs y /api/me nunca incluyen password ni su hash.
- Las pruebas con Basic real pasan por el encoder; @WithMockUser no cuenta como prueba de contraseña.

## Entrega

Código y pruebas cuando corresponda, solicitudes Postman y evidencia en
`ejercicios/respuestas/03.md`. En las tareas de diseño, entregá decisiones y diagramas;
no presentes una simulación como integración real.

## Para explicar

¿Qué dato hay que conservar para poder migrar gradualmente a otro algoritmo de hash?

Siguiente: [SecurityContext y respuestas de error](04-contexto-errores.md).

