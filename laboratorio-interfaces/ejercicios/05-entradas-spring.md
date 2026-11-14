# 05 — HTTP, lote y composición con Spring

## Lectura

Secciones 5, 10–11 de la [guía](../../guias/Glosario-Interface-Contratos.md).

## Consigna

Conectá las piezas con @Configuration y @Bean en infrastructure/config.
Para este laboratorio mantené dominio y aplicación libres de Spring: también los
servicios de aplicación se registran desde la configuración. Esto es una convención
del ejercicio, no la única variante posible de arquitectura hexagonal.

Implementá controllers y DTO HTTP con los códigos del caso. Agregá un segundo adaptador
de entrada por lote, invocado desde tests o un runner habilitado solo por perfil lab.
Ambos deben llamar al mismo puerto de entrada y no duplicar el cálculo.

## Experimentos y aceptación

- POST crea con 201 y Location; GET recupera y pagina según el contrato.
- HTTP inválido produce 400; ausencia 404; conflicto 409; indisponibilidad 503.
- El lote y HTTP obtienen los mismos importes para entradas equivalentes.
- No exigís igualdad de UUID o timestamps entre ejecuciones distintas.
- El controller depende del puerto de entrada, no del adaptador de almacenamiento.
- No hay carga automática de datos fuera del perfil explícito que elijas.
- Los tests del núcleo siguen funcionando sin levantar el contexto.

## Entrega

Código, pruebas y respuestas en `ejercicios/respuestas/05.md`.
Agregá documentación del contrato y solicitudes Postman cuando corresponda.

## Para explicar

¿Dónde se conoce finalmente qué implementación concreta satisface cada interfaz?

Siguiente: [Sustitución y suite común de contratos](06-pruebas-contrato.md).

