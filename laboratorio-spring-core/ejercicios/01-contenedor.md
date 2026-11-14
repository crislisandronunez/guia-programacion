# 01 — Objetos, IoC y contenedor

## Lectura

Secciones 1–3, 17–19 de la [guía](../../guias/Glosario-Spring-Core-Boot-Core.md).

## Consigna

Modelá la interfaz CanalAviso y un canal de consola simulado. Creá un servicio que
reciba el canal por constructor. Primero conectá los objetos con new en un test unitario.
Después registralos en una configuración Java y abrí un AnnotationConfigApplicationContext
en otro test. Cerrá siempre ese contexto. Todavía no crees endpoints.

Inspeccioná nombres, tipos y definiciones de beans desde el test, sin convertir el
servicio en un buscador de dependencias mediante getBean. Explicá qué aportan los
starters, el parent Maven y SpringApplication en la base existente.

## Experimentos y aceptación

- El servicio funciona con un doble de prueba sin arrancar Spring.
- El contexto resuelve el mismo contrato y dos búsquedas del singleton devuelven la misma instancia.
- Dos contextos independientes no comparten ese singleton.
- Un objeto creado con new no recibe automáticamente procesamiento de anotaciones.
- Identificás las tres responsabilidades principales de @SpringBootApplication.

## Entrega

Código y pruebas del ejercicio, evidencia y respuestas en `ejercicios/respuestas/01.md`.
Agregá solicitudes a Postman cuando haya comportamiento HTTP.

## Para explicar

¿Qué parte de tu servicio depende realmente de Spring y cuál es Java puro?

Siguiente: [Estereotipos e inyección](02-inyeccion.md).

