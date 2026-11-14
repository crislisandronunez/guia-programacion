# 12 — Bloqueo, remember-me y auditoría

## Lectura

Secciones 21, 25 de la [guía](../../guias/Glorasio-Spring-Security.md).

## Consigna

Implementá en memoria un bloqueo temporal tras intentos fallidos, con umbral y duración
configurables y Clock inyectable. Limitá el crecimiento de registros y usá actualización
atómica. Evitá revelar si una cuenta existe. Registrá eventos de éxito/fallo sin claves
ni tokens, y explicá el riesgo de que un tercero provoque bloqueos de otra persona.

En un perfil de práctica de sesión, explorá remember-me. Definí clave externa, duración
y revocación, y exigí autenticación completa para una acción sensible. No lo habilites
en la cadena Bearer.

## Experimentos y aceptación

- Alcanzar el umbral bloquea; avanzar el reloj permite recuperar acceso sin esperar minutos.
- Intentos concurrentes no pierden incrementos.
- Un login correcto aplica la política documentada sobre el contador.
- Usuarios inexistentes reciben mensajes genéricos y no provocan crecimiento ilimitado.
- Remember-me se distingue de autenticación completa y no evita el control sensible.
- Logout limpia las credenciales remember-me según la estrategia elegida.
- Documentás límites de memoria local y de confiar en una IP detrás de proxies.

## Entrega

Código y pruebas cuando corresponda, solicitudes Postman y evidencia en
`ejercicios/respuestas/12.md`. En las tareas de diseño, entregá decisiones y diagramas;
no presentes una simulación como integración real.

## Para explicar

¿Cómo reducirías fuerza bruta sin permitir un bloqueo indefinido provocado por otro usuario?

Siguiente: [Pruebas y diagnóstico de seguridad](13-testing-diagnostico.md).

