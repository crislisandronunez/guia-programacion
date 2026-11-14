# Consignas de testing

| Etapa | Consigna | Secciones de la guía |
|---|---|---|
| 01 | [Estrategia, oráculos y primer ciclo TDD](01-estrategia.md) | 1–2, 21 |
| 02 | [JUnit, ciclo de vida y aserciones](02-junit-assertions.md) | 3–4 |
| 03 | [Datos, parametrización y fixtures](03-parametrizados.md) | 5, 18 |
| 04 | [Dobles, stubbing y spies](04-mockito.md) | 6, 8 |
| 05 | [Captura, interacciones y pruebas de capas](05-verificaciones.md) | 7–8 |
| 06 | [Contexto, JSON y configuración](06-contexto-slices.md) | 9–10 |
| 07 | [Contrato web con MockMvc](07-mockmvc.md) | 10–11 |
| 08 | [Integración HTTP y estado real](08-http-real.md) | 9, 12 |
| 09 | [JPA, PostgreSQL y Testcontainers](09-datos-containers.md) | 13–14 |
| 10 | [Proveedor simulado y límites del mock](10-cliente-http.md) | 10, 16 |
| 11 | [Pruebas de autenticación y permisos](11-seguridad.md) | 15 |
| 12 | [Tiempo, eventos y asincronía](12-tiempo-concurrencia.md) | 17–18 |
| 13 | [Maven, cobertura y calidad de pruebas](13-ejecucion-cobertura.md) | 19 |
| 14 | [Diagnóstico, regresión y recorrido final](14-regresion-integracion.md) | 18, 20–21 |

## Forma de trabajo

Leé el [caso](../caso/tienda.md). El laboratorio de testing también deja las clases
productivas para vos: cada etapa introduce una porción pequeña para poder probarla.
No hace falta implementar toda la tienda antes de escribir la primera prueba.

1. Definí comportamiento y esperado con datos concretos.
2. Escribí la prueba y observá el fallo relevante.
3. Implementá lo necesario y verificá.
4. Refactorizá sin cambiar el comportamiento.
5. Guardá evidencia y límites en ejercicios/respuestas/NN.md.

## Convenciones

- Java 21, Boot 3.5.16 y JUnit Jupiter; paquete com.estudio.testing.
- Modelos y servicios por constructor; tests unitarios sin Spring.
- BigDecimal para importes, Instant y Clock para tiempo.
- Pruebas con nombres de comportamiento, AAA y fixtures independientes.
- Preferí resultados observables; verificá interacciones solo cuando sean parte del contrato.
- Oráculos simples e independientes; no copiar fórmulas de producción en el esperado.
- No ocultar fallos con @Disabled, lenient global, retry hasta pasar o filtros desactivados.
- Datos ficticios, puertos dinámicos y recursos temporales.
- Tests de concurrencia con barreras, conexiones separadas y timeouts.
- La estructura del test puede tener helpers y tablas; evitá que se convierta en otra
  implementación compleja del sistema que necesitás verificar.

## Dependencias y ejecución progresiva

El starter-test ya trae JUnit, Mockito y AssertJ. Inspeccioná el árbol efectivo antes
de agregar versiones duplicadas o asumir que falta una herramienta.
JPA/PostgreSQL/Flyway/Testcontainers se incorporan en 09; stub HTTP en 10;
Security en 11; Failsafe/JaCoCo en 13. Fijá versiones cuando no estén gestionadas.

Hasta 13 usá nombres *Test para que Surefire descubra las pruebas, incluidas las
integraciones que vayas agregando. Desde 13, renombrá integraciones a *IT y configurá
Failsafe. La anotación no determina qué plugin descubre la clase.

Testcontainers ejecuta un PostgreSQL real en un contenedor; no lo confundas con un mock.
Postman complementa la suite automatizada y no demuestra carreras por sí solo.

## Alcance

Las 21 secciones de la guía están relacionadas con etapas. Pact/Spring Cloud Contract,
Cucumber, pruebas de carga y navegador quedan como extensiones identificadas.
WebTestClient, RestAssured y PIT se exploran como alternativas sin exigir todos los
frameworks para demostrar el mismo comportamiento.

Referencias: [testing Boot 3.5](https://docs.spring.io/spring-boot/3.5/reference/testing/spring-boot-applications.html)
y [descubrimiento de Failsafe](https://maven.apache.org/surefire/maven-failsafe-plugin/examples/inclusion-exclusion.html).

