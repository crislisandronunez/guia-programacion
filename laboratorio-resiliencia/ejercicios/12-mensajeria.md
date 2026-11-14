# 12 — Mensajería, DLQ y backpressure

## Lectura

Secciones 14 de la [guía](../../guias/Glosario-Reciliencia-Programacion.md).

## Consigna

Activá Compose mensajeria y agregá Spring AMQP y Testcontainers. Implementá una cola
de notificaciones ficticias de reserva, DLQ y política de intentos acotados con espera.
Errores de formato van a DLQ; un fallo transitorio tiene hasta tres intentos totales.
Definí estrategia de retry y dead lettering compatible con el tipo de cola elegido.

Agregá eventId y deduplicación durable en PostgreSQL. Usá outbox para que la reserva y
su intención de notificación se guarden atómicamente. El publicador marca entrega solo
tras el resultado apropiado de confirmación/ruteo; reenvíos siguen siendo posibles.

## Experimentos y aceptación

- Mensaje válido procesa una vez su efecto local aun si se entrega duplicado.
- Poison pill no bloquea mensajes sanos ni entra en requeue infinito.
- Tras agotar intentos, el mensaje queda inspeccionable en DLQ con causa e identificador.
- Prefetch, concurrencia y buffers están acotados.
- Caída después del efecto y antes del ack no duplica el efecto durable al redeliver.
- Distinguís publisher confirm, mensaje sin ruta y consumer acknowledgement.
- Reiniciar broker/consumidor conserva lo prometido por la configuración probada.
- Redrive de DLQ es una operación explícita, auditable y mantiene la clave de deduplicación.

## Entrega

Código, pruebas y evidencia en `ejercicios/respuestas/12.md`.
Agregá solicitudes Postman cuando corresponda y anotá límites de lo demostrado.

## Para explicar

¿Por qué un publisher confirm no demuestra que el consumidor terminó el trabajo?

Siguiente: [Probes, apagado y fallos controlados](13-apagado-caos.md).

