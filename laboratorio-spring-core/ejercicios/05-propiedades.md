# 05 — Propiedades tipadas y precedencia

## Lectura

Secciones 10, 21, 28 de la [guía](../../guias/Glosario-Spring-Core-Boot-Core.md).

## Consigna

Creá AvisosProperties bajo lab.avisos con nombre no vacío, timeout Duration positivo,
máximo de texto entre 1 y 500 y canal predeterminado. Registralo explícitamente o con
@ConfigurationPropertiesScan, usando validación y valores por defecto documentados.
Compará un valor simple con @Value y una consulta mediante Environment.

Usá YAML base, un archivo externo temporal, variable de entorno y argumento de arranque
para cambiar el mismo nombre. Registrá cada escenario y el valor efectivo. Probá
spring.config.import con recurso opcional ausente y obligatorio ausente.

## Experimentos y aceptación

- Propiedades válidas enlazan tipos; timeout ilegible y límites inválidos impiden arrancar.
- La validación anidada funciona si elegís objetos anidados.
- Un argumento de línea de comandos prevalece sobre la variable y el YAML en el escenario probado.
- El import obligatorio ausente falla; optional permite continuar.
- No confundís @PropertySource con carga automática de YAML ni con configuración temprana de Boot.
- No exponés Environment completo por HTTP para comprobar los valores.

## Entrega

Código y pruebas del ejercicio, evidencia y respuestas en `ejercicios/respuestas/05.md`.
Agregá solicitudes a Postman cuando haya comportamiento HTTP.

## Para explicar

¿Por qué una propiedad puede estar bien escrita en src y aun así no ser el valor efectivo?

Siguiente: [Perfiles y condiciones](06-perfiles-condiciones.md).

