# 03 — Parámetros, filtros y paginación

## Lectura

Secciones 5, 7 de la [guía](../../guias/Glosario-Springboot-web.md).

## Consigna

Implementá listado con filtros y orden permitidos. Usá @PathVariable, @RequestParam
y conversión tipada. Agregá una preferencia de idioma por cookie para la futura vista,
sin usarla como identidad. Observá Accept-Language y una cabecera de correlación.

Definí DTO propio de página para evitar agregar Spring Data solo para paginar memoria.
El filtrado ocurre antes de contar y paginar; la selección de orden usa lista permitida.

## Experimentos y aceptación

- Defaults, primera página, página vacía y límite 50 tienen resultados definidos.
- pagina negativa, tamanio inválido, categoría desconocida y UUID mal formado dan 400.
- El total cuenta resultados filtrados, no todo el repositorio.
- El desempate evita resultados aleatorios entre consultas iguales.
- Query excesiva y orden no permitido se rechazan.
- Una cookie ausente tiene valor por defecto y no provoca sesión innecesaria.

## Entrega

Código, pruebas, solicitudes Postman y respuestas en
`ejercicios/respuestas/03.md`. Para vistas, agregá evidencia del comportamiento en navegador.

## Para explicar

¿Qué inconsistencias aparecen si paginás primero y filtrás después?

Siguiente: [Binding y validación](04-validacion.md).

