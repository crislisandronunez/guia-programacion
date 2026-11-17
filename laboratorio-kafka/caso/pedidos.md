# Caso de estudio: pedidos confirmados

Una tienda recibe pedidos. Al confirmar `P-42`, el servicio Pedidos guarda su
estado y emite el hecho `PedidoConfirmado` con un `eventoId` estable. Dos procesos
necesitan ese hecho: Auditoría registra qué ocurrió y Estadísticas actualiza una
proyección de conteos. Cada uno avanza con su propio grupo de consumidores.

```text
Pedidos -> pedidos-confirmados (clave: pedidoId)
                      |-> grupo auditoria-lab    -> auditoría
                      |-> grupo estadisticas-lab -> proyección
```

## Contrato inicial

```json
{
  "tipo": "PedidoConfirmado",
  "version": 1,
  "eventoId": "E-91",
  "pedidoId": "P-42",
  "ocurridoEn": "2026-11-17T12:00:00Z"
}
```

- El tópico principal es `pedidos-confirmados`, con tres particiones.
- La clave Kafka es `pedidoId`: los hechos del mismo pedido necesitan orden.
- `eventoId` identifica el hecho lógico y sirve para deduplicar efectos.
- `ocurridoEn` expresa tiempo de negocio, que puede diferir del timestamp de Kafka.
- El tópico `pedidos-confirmados-dlt` recibe fallos que la política decida apartar.

## Reglas del negocio

1. La respuesta «pedido confirmado» solo puede darse cuando el estado del pedido
   y la fila outbox quedaron confirmados en una **misma transacción SQL**. Antes
   de la etapa de outbox, las publicaciones son experimentos de mensajería y
   no deben prometer esa garantía.
2. Auditoría y Estadísticas pueden avanzar a velocidades distintas. Un fallo de
   Estadísticas no debe hacer retroceder el grupo de Auditoría.
3. Reenviar el mismo `eventoId` no duplica un efecto SQL. La marca de deduplicación
   y el efecto se guardan juntos.
4. Un payload inválido no se descarta sin evidencia: conserva clave, bytes,
   partición, offset y causa para investigar.
5. La política de retención debe cubrir la ventana de recuperación acordada.
   Kafka no promete conservar registros después de esa ventana.

## Fallos para provocar

| Momento de caída | Pregunta a responder |
|---|---|
| Después del commit SQL de Pedidos y antes de publicar | ¿Dónde queda `E-91` pendiente? |
| Después de publicar y antes de marcar outbox enviada | ¿Puede aparecer dos veces? |
| Después de guardar Auditoría y antes de confirmar offset | ¿Cómo se evita duplicar Auditoría? |
| Mientras Estadísticas no puede conectarse | ¿Qué pasa con el otro grupo? |
| Con JSON inválido o versión desconocida | ¿Dónde se investiga y quién decide el replay? |

## Entrega final

Mostrá una traza de `E-91` desde la transacción de Pedidos hasta ambos efectos.
Para cada paso distinguí **confirmación SQL, confirmación de publicación Kafka,
offset confirmado y efecto de negocio**. Una captura de un log «mensaje recibido»
no prueba los cuatro hechos.
