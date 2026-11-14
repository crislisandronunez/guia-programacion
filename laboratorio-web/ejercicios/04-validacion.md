# 04 — Binding y validación

## Lectura

Secciones 5, 8 de la [guía](../../guias/Glosario-Springboot-web.md).

## Consigna

Aplicá Jakarta Validation a DTO, colecciones y metadata del caso.
Creá una validación de slug si la combinación de reglas lo requiere.
Separá error de binding, Bean Validation y conflicto de negocio.
Probá también invocar el servicio directamente para no depender solo de @Valid.

Explorá @Validated y validación de parámetros de método con la configuración compatible
de Spring MVC 6.2, evitando duplicar mecanismos sin necesidad.

## Experimentos y aceptación

- Nulos, vacíos, espacios y fronteras de longitud se prueban explícitamente.
- Etiquetas inválidas se identifican por campo/índice y no se guardan.
- Normalización y deduplicación tienen orden documentado.
- Slug duplicado es 409, no un error genérico de formato.
- Un request inválido no modifica el repositorio.
- No confiás en campos calculados enviados por el cliente.

## Entrega

Código, pruebas, solicitudes Postman y respuestas en
`ejercicios/respuestas/04.md`. Para vistas, agregá evidencia del comportamiento en navegador.

## Para explicar

¿Qué diferencia hay entre un dato con formato válido y una operación permitida por el negocio?

Siguiente: [Errores consistentes con ProblemDetail](05-errores.md).

