# Laboratorio de algoritmos y estructuras de datos

Java 21, Maven y Spring Boot 3.5.16.
Basado en [Tipos de algoritmos para programadores](../guias/Glosario-Tipos-de-Algoritmos-Programadores.md).

## Qué incluye

- Base ejecutable con Web, Validation y Actuator.
- Prueba de arranque y colección inicial de Postman.
- Caso ficticio, datasets pequeños y dieciséis consignas.
- Ejercicios de corrección, complejidad, casos límite y comparación de alternativas.

Los algoritmos, modelos, lectores de datos y tests de los ejercicios no están resueltos.
Vos los implementás. Primero probás Java puro con JUnit; después conectás una API mínima.
No requiere Docker, base de datos ni proveedores externos.

## Abrir y ejecutar

Agregá este pom.xml con **Add as Maven Project** en IntelliJ o abrilo por separado.
Elegí JDK 21 para Project SDK, importador y runner de Maven.
Podés usar Maven incluido en IntelliJ; no hay Maven Wrapper.

Ejecutá com.estudio.algoritmos.LaboratorioAlgoritmosApplication.
Con Java 21 y Maven en PATH, desde esta carpeta:

```shell
mvn spring-boot:run
```

Importá postman/laboratorio-algoritmos.postman_collection.json.
GET http://localhost:8090/actuator/health responde 200 y {"status":"UP"}.
SERVER_PORT permite cambiar el puerto; actualizá baseUrl en Postman.
Todavía no existen endpoints /api.

## Verificar y empaquetar

```shell
mvn verify
java -jar target/laboratorio-algoritmos-0.0.1-SNAPSHOT.jar
```

El test inicial comprueba arranque HTTP en puerto aleatorio. No verifica ningún algoritmo.
Podés ejecutar verify también desde Maven en IntelliJ.

## Recorrido

Leé el [caso](caso/distribuidora.md), los [datos](datos/README.md) y el
[índice de ejercicios](ejercicios/README.md).
Los datasets pequeños sirven para razonar a mano; los grandes los vas a generar con semilla fija.
No se incluyen resultados resueltos ni implementaciones modelo.

Las mediciones de rendimiento van separadas de los tests de corrección.
Un algoritmo no aprueba por ser rápido si devuelve resultados incorrectos.
Para benchmarks rigurosos incorporá JMH como extensión, sin volverlo requisito del arranque.

## Referencias

- [Colecciones Java 21](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/package-summary.html).
- [PriorityQueue: contrato y costos](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/PriorityQueue.html).
- [Backoff y variantes de jitter](https://aws.amazon.com/blogs/architecture/exponential-backoff-and-jitter/).
- [API Java Mac para HMAC](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/javax/crypto/Mac.html).

Las simplificaciones de la guía se contrastan con pruebas: Big-O no promete milisegundos,
hash suele analizarse en promedio y un checksum válido no demuestra autenticidad.

