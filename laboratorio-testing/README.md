# Laboratorio de testing en Java y Spring Boot

Java 21, Maven y Spring Boot 3.5.16.
Basado en [Glosario de Testing](../guias/Glosario-Testing.md).

## Qué incluye

- Base con Web, Validation, Actuator y spring-boot-starter-test.
- Un smoke test de arranque HTTP y colección inicial Postman.
- Caso de tienda, fixtures JSON y catorce etapas con criterios de aceptación.
- PostgreSQL local en Compose para cuando incorpores persistencia.

Vos implementás las clases de negocio y sus pruebas. No están resueltos los tests
de dominio, Mockito, controllers, repositorios, seguridad ni asincronía.
El único test preparado verifica que la base arranque.

## Abrir y ejecutar

Agregá este pom.xml con **Add as Maven Project** en IntelliJ o abrilo por separado.
Seleccioná JDK 21 para Project SDK, importador y runner de Maven.
Podés usar Maven de IntelliJ; no se incluye Maven Wrapper.

Ejecutá com.estudio.testing.LaboratorioTestingApplication.
Con Java 21 y Maven en PATH, desde esta carpeta:

```shell
mvn spring-boot:run
```

Importá postman/laboratorio-testing.postman_collection.json.
GET http://localhost:8091/actuator/health devuelve 200 y {"status":"UP"}.
Podés cambiar el puerto con SERVER_PORT y actualizar baseUrl.
Todavía no existen rutas /api. El arranque inicial no necesita Docker.

## Ejecutar pruebas

```shell
mvn test
mvn verify
java -jar target/laboratorio-testing-0.0.1-SNAPSHOT.jar
```

En la base, tanto test como verify ejecutan el smoke test mediante Surefire.
Aunque su nombre termina en Test, es una prueba de integración con servidor real.
Failsafe y cobertura todavía no están configurados: los incorporarás en la etapa 13.
Después de esa etapa, verify también ejecutará las clases *IT.
No confundas nombre de archivo, herramienta y alcance de una prueba.

## PostgreSQL desde la etapa 09

Iniciá Docker Desktop. Para desarrollo manual:

```shell
docker compose up -d --wait
```

| Campo de conexión | Valor |
|---|---|
| Host | localhost |
| Puerto | 5439 |
| Base y usuario | testing_lab |
| Contraseña de práctica | testing_lab_local |

Podés inspeccionar esa base con DBeaver. Las tablas se crean con las migraciones que
vos escribas. La aplicación no se conecta automáticamente por levantar Compose:
dependencias y configuración se agregan en la etapa de datos.

Los tests usarán PostgreSQL temporal de Testcontainers con puerto dinámico, separado
de testing_lab. No necesitan el contenedor Compose, pero sí Docker disponible.

Para detener el servicio manual conservando datos:

```shell
docker compose down
```

## Recorrido y fuentes

Empezá por el [caso](caso/tienda.md) y el [índice de ejercicios](ejercicios/README.md).
Los fixtures están en src/test/resources/fixtures y se describen en su README.

- [Testing con Spring Boot 3.5](https://docs.spring.io/spring-boot/3.5/reference/testing/spring-boot-applications.html).
- [Patrones de descubrimiento de Failsafe](https://maven.apache.org/surefire/maven-failsafe-plugin/examples/inclusion-exclusion.html).
- [MockitoBean en Spring Framework 6.2](https://docs.spring.io/spring-framework/reference/6.2/testing/annotations/integration-spring/annotation-mockitobean.html).

El objetivo es demostrar comportamiento y detectar regresiones, no acumular mocks
ni alcanzar un porcentaje de cobertura sin comprobar resultados.

