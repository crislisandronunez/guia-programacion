# 10 — Integración y defensa del diseño

## Lectura

Secciones 1–12 de la [guía](../../guias/Glosario-Interface-Contratos.md).

## Consigna

Completá el caso y Postman. Prepará un mapa contrato → consumidor → implementación →
capa → prueba, distinguiendo políticas, puertos de entrada y de salida.
Dibujá por separado flujo de llamadas en ejecución y dependencias de código.

Elegí adaptadores mediante configuración explícita: memoria para una ejecución local,
JPA para persistencia y catálogo local/HTTP según el escenario. Asegurá un candidato
por puerto para cada combinación válida. Documentá arranque y dependencias por perfil.
Compará el ejemplo de cobro de la guía: una transacción SQL no deshace un efecto HTTP.

## Experimentos y aceptación

- Flujo HTTP y lote usan las mismas reglas y pasan las pruebas.
- Cambiar memoria por JPA no modifica firmas de puertos ni fórmulas.
- Suite común y pruebas específicas del adaptador pasan.
- mvn verify y JAR funcionan con la configuración elegida.
- Las pruebas PostgreSQL se ejecutan con Docker; si se omiten, no se reportan como validación real.
- La documentación distingue memoria efímera de persistencia durable.
- Ningún DTO técnico se filtra al núcleo.
- Señalás extensiones pendientes: módulos Maven independientes, otros proveedores y transacciones entre varios recursos.

## Entrega

Código, pruebas y respuestas en `ejercicios/respuestas/10.md`.
Agregá documentación del contrato y solicitudes Postman cuando corresponda.

## Para explicar

¿Qué cambiarías si el negocio exigiera notificar externamente de forma durable después de guardar?

Volvé al [índice](README.md) para revisar cobertura.

