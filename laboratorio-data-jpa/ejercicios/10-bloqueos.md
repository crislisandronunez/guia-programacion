# 10 — Bloqueos y concurrencia

## Lectura
Bloqueos de las secciones 10 y 14 de la [guía](../../guias/Glosario-Spring-Data-JPA.md).

## Consigna
Agregá @Version a Producto y Pedido con migración para registros existentes.
Demostrá actualización perdida sin protección en una variante de prueba y verificá
que el bloqueo optimista detecta el conflicto.

Para confirmar pedidos, compará estrategia optimista con PESSIMISTIC_WRITE y con un
UPDATE condicional de stock. Elegí una para el flujo principal.
Si usás UPDATE condicional, integrá explícitamente versionado y limpieza de contexto.
Ordená bloqueos por ID y definí timeout finito. No llames proveedores bajo el lock.

## Experimentos y aceptación
- Dos transacciones leen la misma versión; tras el primer commit, la otra falla al escribir.
- Dos pedidos compiten por la última unidad: uno solo confirma.
- Dos cancelaciones simultáneas reponen una vez.
- Pedido con varios productos mantiene atomicidad.
- Una prueba usa dos conexiones/transacciones y barrera; no comparte EntityManager entre hilos.
- Conflicto devuelve 409 con error claro.
- Reintentos, si existen, son acotados y recargan estado dentro de una transacción nueva.
- El timeout de bloqueo no deja cambios parciales ni espera infinita.

## Pregunta
¿Qué protege @Version si el cliente ni siquiera envía la versión que vio al editar?
Definí un contrato de edición con versión esperada para detectar también ese caso.

Siguiente: [11](11-auditoria.md).
