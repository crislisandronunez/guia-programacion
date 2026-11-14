# 06 — Aislamiento y colas acotadas

## Lectura

Secciones 7 de la [guía](../../guias/Glosario-Reciliencia-Programacion.md).

## Consigna

Limitá consultas al proveedor mediante bulkhead de semáforo. Empezá con dos permisos
y espera cero. Retené dos llamadas del simulador con barreras y observá la tercera.
Compará con thread-pool bulkhead y cola acotada para generación de reportes.

Separá recursos del catálogo, proveedor y reportes. Mantené límite total de espera
incluyendo cola; liberar un permiso demasiado pronto puede ocultar trabajo que sigue
ejecutándose tras un timeout.

## Experimentos y aceptación

- La concurrencia observada no supera el cupo en el escenario de semáforo.
- Una llamada adicional se rechaza rápidamente y no llega al proveedor.
- El catálogo responde mientras las llamadas remotas están retenidas.
- Pool y cola llenos no crean hilos o memoria sin límite.
- Probás liberación de permisos tras éxito, excepción y cancelación.
- No devolvés ENCOLADO si no guardaste realmente un trabajo con contrato de seguimiento.
- Documentás que los límites son por instancia, incluso con virtual threads.

## Entrega

Código, pruebas y evidencia en `ejercicios/respuestas/06.md`.
Agregá solicitudes Postman cuando corresponda y anotá límites de lo demostrado.

## Para explicar

¿Cuándo un semáforo limita concurrencia pero sigue ocupando hilos de Tomcat?

Siguiente: [Rate limiting y rechazo temprano](07-limites-carga.md).

