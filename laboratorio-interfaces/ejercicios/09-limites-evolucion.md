# 09 — Límites arquitectónicos y contratos pequeños

## Lectura

Secciones 3, 7–10 de la [guía](../../guias/Glosario-Interface-Contratos.md).

## Consigna

Agregá pruebas de arquitectura, por ejemplo con ArchUnit y versión explícita compatible.
Comprobá dominio sin dependencias de aplicación/infraestructura/frameworks, aplicación
sin infraestructura y puertos sin tipos técnicos. La configuración de composición
puede conocer los adaptadores para construirlos.

Revisá una interfaz hipotética que mezcle guardar, exportar PDF y notificar. Separá
capacidades por consumidor; no obligues a implementaciones a lanzar
UnsupportedOperationException para operaciones esenciales del contrato.
Elegí un colaborador interno que permanezca como clase concreta y justificá por qué.

## Experimentos y aceptación

- Las reglas pasan con el diseño actual y detectan una dependencia prohibida en un experimento aislado.
- Un paquete llamado domain no se considera suficiente evidencia de aislamiento.
- Cada puerto tiene un consumidor y una necesidad explícita.
- No hay parejas Service/ServiceImpl por costumbre.
- Analizás el impacto de agregar un método abstracto y de agregar un default con comportamiento.
- No usás un método default para ocultar un incumplimiento o devolver null silenciosamente.
- Documentás una evolución compatible y otra que requiere cambiar consumidores/adaptadores.

## Entrega

Código, pruebas y respuestas en `ejercicios/respuestas/09.md`.
Agregá documentación del contrato y solicitudes Postman cuando corresponda.

## Para explicar

¿Por qué un puerto de salida puede aportar valor aunque hoy tenga una única implementación?

Siguiente: [Integración y defensa del diseño](10-integracion.md).

