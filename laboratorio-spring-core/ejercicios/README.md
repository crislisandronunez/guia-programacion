# Consignas de Spring Core y Spring Boot Core

| Etapa | Consigna | Secciones de la guía |
|---|---|---|
| 01 | [Objetos, IoC y contenedor](01-contenedor.md) | 1–3, 17–19 |
| 02 | [Estereotipos e inyección](02-inyeccion.md) | 3–5, 16 |
| 03 | [Configuración Java y creación explícita](03-configuracion-java.md) | 2–3, 8 |
| 04 | [Scopes y ciclo de vida](04-scopes-ciclo.md) | 2, 6–7 |
| 05 | [Propiedades tipadas y precedencia](05-propiedades.md) | 10, 21, 28 |
| 06 | [Perfiles y condiciones](06-perfiles-condiciones.md) | 9, 20–22 |
| 07 | [API mínima, recursos, conversión e idiomas](07-validacion-recursos.md) | 13, 15, 29 |
| 08 | [Eventos de aplicación](08-eventos.md) | 11 |
| 09 | [AOP y límites de los proxies](09-aop.md) | 12, 16 |
| 10 | [Ejecución asíncrona y tareas programadas](10-asincronia.md) | 14 |
| 11 | [Caché local y consistencia](11-cache.md) | 14 |
| 12 | [Starters y auto-configuración](12-autoconfiguracion.md) | 18, 20 |
| 13 | [Logging, Actuator y disponibilidad](13-observabilidad.md) | 23–24, 28 |
| 14 | [Hooks de arranque y cierre](14-arranque-apagado.md) | 7, 19, 27 |
| 15 | [DevTools, empaquetado y diagnóstico](15-diagnostico.md) | 16, 25–26, 28, 30 |
| 16 | [Integración y defensa del laboratorio](16-integracion.md) | 1–30, especialmente 29–30 |

## Forma de trabajo

Leé el [caso](../caso/central-avisos.md). Cada etapa construye sobre la anterior,
salvo experimentos que requieren contextos aislados. Las consignas están completas;
sus soluciones, respuestas y pruebas de negocio quedan a cargo del estudiante.

1. Leé las secciones y anticipá el resultado antes de ejecutar.
2. Implementá la solución y un experimento que demuestre su comportamiento.
3. Probá también ausencia, errores y límites de las anotaciones.
4. Guardá tus respuestas en `ejercicios/respuestas/NN.md`; esa carpeta es un entregable futuro.
5. Extendé Postman cuando aparezcan endpoints y ejecutá `mvn verify`.

## Convenciones

- Java 21 y Boot 3.5.16. Clases propias bajo com.estudio.core, salvo la auto-configuración de la etapa 12.
- Inyección por constructor, contratos pequeños y paquetes por función.
- UUID, Instant y Clock inyectable. Estado en memoria; no agregar JPA ni una base para resolver este recorrido.
- API bajo /api, DTO separados y errores con codigo y mensaje, sin trazas internas.
- JUnit para reglas puras; AnnotationConfigApplicationContext para Core;
  ApplicationContextRunner para condiciones y SpringBootTest para integración.
- Clases y beans intencionalmente defectuosos en configuraciones de prueba aisladas,
  preferentemente anidadas y registradas explícitamente. No ensuciar el scan de la aplicación.
- Concurrencia con ejecutores, barreras y timeouts; limpiar contextos y recursos.
- No confundir usar una anotación con demostrar su comportamiento mediante proxy.
- Los perfiles, mensajes, recursos y dependencias adicionales se crean en su etapa.

## Alcance y referencias

El recorrido cubre los conceptos principales de las treinta secciones. Las alternativas
avanzadas se identifican como exploraciones en la etapa 16; no se exige implementar
cada anotación del glosario. Web sirve para observar el contenedor; seguridad, JPA y
mensajería durable pertenecen a otros recorridos.

Contrastá precedencia con la [referencia de configuración de Boot 3.5](https://docs.spring.io/spring-boot/3.5/reference/features/external-config.html)
y el registro de auto-configuraciones con su [documentación oficial](https://docs.spring.io/spring-boot/3.5/reference/features/developing-auto-configuration.html).

