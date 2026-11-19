# Laboratorio de lógica de programación

Java 21, Maven y JUnit 5. Basado en la [guía 02 de lógica de programación](../guias/02-Glosario-Logica-de-Programacion-y-Resolucion-de-Problemas.md).

## Qué incluye

- Un proyecto Maven independiente, preparado para escribir clases Java y pruebas JUnit.
- Un [caso ficticio](caso/centro-de-envios.md) que mantiene el mismo contexto en todo el recorrido.
- Doce [consignas progresivas](ejercicios/README.md), vinculadas con secciones concretas de la guía.
- Contratos, ejemplos y criterios de aceptación; las soluciones y pruebas de negocio son tuyas.

El proyecto inicial **no contiene algoritmos resueltos**. Los ejercicios comienzan con especificación, decisiones y trazas; después incorporan bucles, patrones, corrección y composición. No se necesita Spring Boot, Docker, base de datos ni Postman: este laboratorio trata de lógica en Java puro.

## Abrir y ejecutar

Abrí este `pom.xml` como proyecto Maven en IntelliJ o agregalo con **Add as Maven Project**. Seleccioná JDK 21 para el proyecto, el importador y el runner de Maven. Podés usar Maven incluido en IntelliJ; no se incluye Maven Wrapper.

La clase `com.estudio.logica.LaboratorioLogica` permite comprobar que el proyecto arranca. Desde IntelliJ ejecutá su método `main`. Con Java 21 y Maven disponibles en la terminal:

```shell
mvn verify
```

`mvn verify` compila la base. Al comienzo no hay pruebas de los ejercicios: las agregarás en `src/test/java` mientras resolvés cada etapa. Que el proyecto inicial compile no demuestra que las consignas estén resueltas.

## Recorrido

Leé primero el [caso](caso/centro-de-envios.md) y después seguí el [índice de ejercicios](ejercicios/README.md). Cada etapa indica qué parte de la guía repasar, qué comportamiento implementar, con qué casos comprobarlo y qué razonamiento documentar.

Los ejercicios proponen firmas de métodos para que los contratos sean precisos. Podés organizar las clases por etapa o por responsabilidad; conservá las firmas públicas indicadas cuando una etapa posterior dependa de ellas. Las respuestas escritas se guardan en `ejercicios/respuestas/NN.md`, que vas creando al avanzar.

