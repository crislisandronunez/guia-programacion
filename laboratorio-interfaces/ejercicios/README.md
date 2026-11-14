# Consignas de interfaces y contratos

| Etapa | Consigna | Secciones de la guía |
|---|---|---|
| 01 | [Firma y comportamiento del contrato](01-contrato.md) | 1, 8, 12 |
| 02 | [Políticas puras de dominio](02-politicas.md) | 2, 4, 7 |
| 03 | [Puertos de salida y adaptadores en memoria](03-puertos-salida.md) | 3, 6–8 |
| 04 | [Puertos de entrada y orquestación](04-casos-uso.md) | 2, 5, 11 |
| 05 | [HTTP, lote y composición con Spring](05-entradas-spring.md) | 5, 10–11 |
| 06 | [Sustitución y suite común de contratos](06-pruebas-contrato.md) | 1, 8, 12 |
| 07 | [Traducción de un proveedor HTTP](07-adaptador-http.md) | 6, 8, 11 |
| 08 | [Persistencia JPA sin contaminar el núcleo](08-adaptador-jpa.md) | 6, 8, 10 |
| 09 | [Límites arquitectónicos y contratos pequeños](09-limites-evolucion.md) | 3, 7–10 |
| 10 | [Integración y defensa del diseño](10-integracion.md) | 1–12 |

## Método

Leé el [caso de la imprenta](../caso/imprenta.md). Las consignas están completas;
interfaces, clases, mappers y tests de negocio son entregables del estudiante.

1. Escribí qué promete el contrato, además de su firma.
2. Implementá el consumidor y una variante simple.
3. Probá casos válidos, errores y límites.
4. Incorporá otro adaptador sin modificar el núcleo.
5. Registrá respuestas y decisiones en ejercicios/respuestas/NN.md.

## Convenciones del laboratorio

- Java 21 y Boot 3.5.16; paquete raíz com.estudio.contratos.
- domain: modelos, reglas puras y puertos de dominio elegidos.
- application: puertos de entrada, servicios y puertos auxiliares elegidos.
- infrastructure: adaptadores de entrada/salida, DTO técnicos y configuración.
- Dominio y aplicación sin Spring en este recorrido; composición con @Bean afuera.
- No crear carpetas con clases vacías para anticipar todas las etapas.
- Records o modelos propios para comandos/resultados; colecciones con semántica de copia explícita.
- Clock inyectable, importes BigDecimal y UUID.
- Nulabilidad, ausencia, errores, precisión y efectos documentados en Javadoc.
- Tests de dominio y aplicación sin contexto; integración para cableado, HTTP y JPA.
- Concurrencia con barreras y timeouts; no pruebas basadas solo en ejecución secuencial.
- Los ejemplos rotos son experimentos aislados, no código que deje de compilar el proyecto.

## Dependencias por etapa

La base incluye Web, Validation, Actuator y pruebas Spring Boot.
La herramienta de stub HTTP se agrega en 07; JPA, PostgreSQL, Flyway y Testcontainers
en 08; pruebas de arquitectura en 09. Docker se necesita desde 08.
Conservá un modo de trabajo en memoria al incorporar la persistencia.

## Qué demostrar

El recorrido cubre las doce secciones de la guía. La forma de organizar capas elegida
es una convención didáctica. La ubicación de un puerto se justifica por su consumidor
y lenguaje; no por contar cuántas implementaciones tiene.

Una interface Java no garantiza por sí sola sustitución, seguridad ni atomicidad.
Las reglas de contrato y las pruebas deben mostrarlo. La suite compartida comprueba
garantías comunes; persistencia tras reinicio requiere una prueba específica de JPA.

Para repasar sintaxis, consultá la [especificación de interfaces Java 21](https://docs.oracle.com/javase/specs/jls/se21/html/jls-9.html).
Para el propósito de puertos y adaptadores, el [artículo original](https://alistair.cockburn.us/hexagonal-architecture).

