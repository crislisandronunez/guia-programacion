# 02 — Cadena propia y reglas por ruta

## Lectura

Secciones 4–5, 20 de la [guía](../../guias/Glorasio-Spring-Security.md).

## Consigna

Creá SecurityFilterChain con Lambda DSL. Permití /public/info y health básico; definí
reglas específicas por ruta y método antes del cierre con denyAll para rutas no previstas.
Mantené CSRF y habilitá Basic y formulario como mecanismos iniciales.

Implementá el modelo, repositorio en memoria y endpoints mínimos del caso. Dejales
sus reglas de propiedad desde el inicio, aunque en la etapa 05 las refuerces en servicios.
Para pruebas iniciales usá usuarios simulados; las credenciales propias llegan en 03.
No agregues un endpoint de registro que permita elegir autoridades.

## Experimentos y aceptación

- Una ruta pública conserva los filtros y cabeceras; no la excluís con web.ignoring.
- Verificás GET y POST por separado, incluyendo CSRF en operaciones de escritura.
- La primera regla coincidente decide; una regla general no tapa la de aprobación.
- Health sigue público después de reemplazar la auto-configuración.
- Usuario sin permiso no ejecuta la operación; una ruta no prevista queda denegada.
- Los DTO no aceptan propietario, estado inicial arbitrario ni roles; el servidor decide esos valores.

## Entrega

Código y pruebas cuando corresponda, solicitudes Postman y evidencia en
`ejercicios/respuestas/02.md`. En las tareas de diseño, entregá decisiones y diagramas;
no presentes una simulación como integración real.

## Para explicar

¿Qué diferencia hay entre securityMatcher, que selecciona una cadena, y requestMatchers dentro de ella?

Siguiente: [Usuarios y contraseñas](03-usuarios-passwords.md).

