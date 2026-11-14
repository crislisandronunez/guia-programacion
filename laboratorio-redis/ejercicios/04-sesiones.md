# 04 — Sesiones compartidas

## Objetivo
Leé la sección 11 de la [guía](../../guias/Glosario-Spring-Redis.md).

## Consigna
Agregá Spring Session Data Redis. Configurá namespace `lab:redis:sesiones` y tiempo de
inactividad de 120 segundos. Creá POST `/api/sesion/carrito`, GET en la misma ruta y
DELETE `/api/sesion`. Guardá un carrito pequeño con IDs y cantidades, no objetos JPA.
La API crea sesiones de práctica; no representa autenticación real.

Levantá dos instancias en 8081 y 8082, con la misma configuración de sesión. Usá el cookie
jar de Postman para enviar la misma cookie SESSION a ambas.

## Experimentos y aceptación
- Guardar un carrito en A permite leerlo desde B.
- Reiniciar A conserva la sesión si Redis sigue disponible.
- Invalidar la sesión la elimina para ambas instancias.
- Tras inactividad suficiente, la aplicación trata la sesión como vencida; no uses
  únicamente la desaparición física inmediata de todas sus claves como criterio.
- Dos clientes con cookies distintas no comparten carrito.
- Redis caído produce una respuesta controlada, sin inventar una sesión válida local.
- La cookie es HttpOnly y tiene política SameSite explícita. Documentá Secure para HTTPS;
  en este laboratorio HTTP local no puede exigirse sin configurar TLS.

## Para explicar
¿Qué cambia frente a guardar sesiones en memoria de cada instancia? ¿Por qué compartir
sesión no evita actualizaciones perdidas de un carrito editado simultáneamente?

Siguiente: [05](05-atomicidad.md).
