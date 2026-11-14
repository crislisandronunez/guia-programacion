# 08 — Compensaciones y estado incierto

## Objetivo
Profundizá en sagas de la sección 12 de la [guía](../../guias/Resolucion-de-Problemas.md).

## Consigna
Extendé el pedido aprobado con un flujo: reserva de stock → contratación de transporte
simulado → habilitación para preparar. Persistí saga, paso actual, intentos y resultado.
Mientras la saga esté pendiente, no permitas preparar ni despachar.
Adaptá el consumidor de la etapa 07: crear una orden pendiente no habilita su ejecución.

El transporte admite referencia idempotente, consulta por referencia y cancelación.
Simulá éxito, rechazo definitivo y aceptación con respuesta perdida.
Ante rechazo definitivo, cancelá el pedido y liberá reserva una sola vez.
Ante timeout, consultá antes de repetir o compensar. Una reserva de transporte desconocida
no se considera inexistente. Agotado el presupuesto, enviá a revisión manual.

Si el usuario cancela durante el flujo, registrá la intención y resolvé primero el estado
remoto; luego compensá transporte y stock según corresponda.
Una compensación fallida queda pendiente con reintentos, no mágicamente completada.

## Aceptación
- Camino feliz habilita preparación una vez.
- Rechazo libera stock sin contratación.
- Respuesta perdida se resuelve sin segundo transporte.
- Reinicio continúa desde el paso persistido.
- Cancelación concurrente con confirmación termina coherentemente o en revisión explícita.
- Repetir un paso o compensación no duplica efectos.
- El historial explica qué falta compensar y por qué.

## Pregunta
¿Qué diferencia hay entre rollback local y compensación? ¿Qué paso podría ser irreversible
si el camión ya salió?

Siguiente: [09](09-legado.md).
