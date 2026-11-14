# 09 — Configuración y observabilidad

## Lectura

Secciones 10–11, 16 de la [guía](../../guias/Glosario-Reciliencia-Programacion.md).

## Consigna

Creá un perfil lab que exponga localmente health y metrics, con endpoints adicionales
solo si los necesitás y justificás. Inspeccioná métricas realmente registradas por
Resilience4j y agregá métricas de operaciones finales, intentos y respuestas degradadas.

Externalizá nombres y parámetros de políticas. Prepará tabla de excepciones:
rechazo de negocio, fallo técnico remoto, timeout, circuito abierto y rechazo local.
Validá configuración efectiva consultando registros de Resilience4j en tests.

## Experimentos y aceptación

- Un éxito tras tres intentos cuenta una operación y tres intentos.
- Una respuesta degradada se observa por separado de un éxito fresco.
- Medís latencia incluyendo esperas y rechazos, no solo llamadas exitosas.
- Métricas usan etiquetas acotadas, sin UUID, SKU arbitrario ni clave idempotente.
- Perfiles de prueba reducen tiempos sin cambiar la semántica que se verifica.
- Logs correlacionan operación e intentos sin almacenar cuerpos sensibles.
- Fuera de lab continúa expuesto únicamente health.

## Entrega

Código, pruebas y evidencia en `ejercicios/respuestas/09.md`.
Agregá solicitudes Postman cuando corresponda y anotá límites de lo demostrado.

## Para explicar

¿Cómo podrías tener pocos errores HTTP y aun así estar degradando casi todas las respuestas?

Siguiente: [Transacciones, conflictos y pool](10-base-datos.md).

