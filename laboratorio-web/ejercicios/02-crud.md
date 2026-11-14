# 02 — CRUD, capas y mapeos

## Lectura

Secciones 4, 6–7, 19 de la [guía](../../guias/Glosario-Springboot-web.md).

## Consigna

Implementá modelo, repositorio en memoria, servicio y DTO del catálogo.
Agregá POST, GET individual, PUT, DELETE y publicación según el caso.
Usá ResponseEntity para Location, códigos y cuerpo. La unicidad de slug debe ser
atómica; no basta con contains seguido de put sin protección.

Mantené reglas en servicios/modelos y conversiones HTTP en controllers.
No crees entidades JPA ni una base para practicar estos endpoints.

## Experimentos y aceptación

- Crear devuelve 201 y Location recuperable con GET.
- PUT reemplaza los campos editables y no crea un ID ausente.
- DELETE responde 204 sin cuerpo, aun al repetirlo.
- Dos creaciones con igual slug producen un éxito y un conflicto.
- Publicar dos veces no duplica un efecto.
- Métodos no admitidos responden 405 con Allow correspondiente.
- Distinguís idempotencia de igualdad de códigos de respuesta.

## Entrega

Código, pruebas, solicitudes Postman y respuestas en
`ejercicios/respuestas/02.md`. Para vistas, agregá evidencia del comportamiento en navegador.

## Para explicar

¿Por qué un GET que modifica estado rompe expectativas del protocolo y de sus clientes?

Siguiente: [Parámetros, filtros y paginación](03-entradas-listados.md).

