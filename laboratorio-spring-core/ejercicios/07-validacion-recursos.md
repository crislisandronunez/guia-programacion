# 07 — API mínima, recursos, conversión e idiomas

## Lectura

Secciones 13, 15, 29 de la [guía](../../guias/Glosario-Spring-Core-Boot-Core.md).

## Consigna

Implementá el caso: modelo, repositorio en memoria, servicio y las tres rutas indicadas.
Usá DTO, validación Jakarta y errores con codigo y mensaje. Respetá el máximo de texto
configurado y la transición atómica de PENDIENTE a ENVIADO ante llamadas concurrentes.

Cargá el catálogo desde un recurso classpath UTF-8 que vos crearás. Leelo como stream
para que funcione dentro del JAR. Incorporá Converter para un valor propio y mensajes
es/en con MessageSource. Compará una expresión SpEL fija con un placeholder de propiedad;
no evalúes expresiones recibidas del cliente.

## Experimentos y aceptación

- Crear, consultar y enviar cumplen el contrato del caso y sus códigos HTTP.
- Campos vacíos, email inválido y texto excesivo fallan sin guardar el aviso.
- Curso desconocido y UUID inexistente responden 404.
- Dos envíos simultáneos producen un único envío; un fallo simulado conserva PENDIENTE.
- El recurso se lee desde el JAR, y su ausencia produce un error comprensible.
- Probás mensaje español, inglés y fallback; un valor no convertible responde 400.

## Entrega

Código y pruebas del ejercicio, evidencia y respuestas en `ejercicios/respuestas/07.md`.
Agregá solicitudes a Postman cuando haya comportamiento HTTP.

## Para explicar

¿Qué diferencia hay entre convertir un dato, validarlo y aplicar una regla de negocio?

Siguiente: [Eventos de aplicación](08-eventos.md).

