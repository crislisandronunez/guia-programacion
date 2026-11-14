# Laboratorio Spring Boot Web

Java 21, Maven, Spring Boot 3.5.16 y Spring MVC.
Basado en [Glosario Spring Boot Web](../guias/Glosario-Springboot-web.md).

## Qué incluye

- Base Web, Validation y Actuator con test de arranque HTTP.
- Colección inicial Postman, caso ficticio y catorce ejercicios.
- Recorrido de API REST, HTML, archivos, integración HTTP y documentación.

Los controllers, servicios, DTO, repositorios en memoria, plantillas, filtros y pruebas
de los ejercicios los implementás vos. Solo está preparada la base de arranque.
No requiere Docker ni base de datos. Los artículos en memoria se pierden al reiniciar.

## Abrir y ejecutar

Agregá este pom.xml con **Add as Maven Project** en IntelliJ o abrilo por separado.
Seleccioná JDK 21 para Project SDK, importador y runner de Maven.
Podés usar Maven incluido en IntelliJ; no se incluye Maven Wrapper.

Ejecutá com.estudio.web.LaboratorioWebApplication.
Con Java 21 y Maven en PATH, desde esta carpeta:

```shell
mvn spring-boot:run
```

Importá postman/laboratorio-web.postman_collection.json.
GET http://localhost:8092/actuator/health responde 200 y {"status":"UP"}.
Podés cambiarlo con SERVER_PORT y actualizar baseUrl en Postman.
Todavía no existen rutas /api, páginas HTML ni Swagger UI.

## Verificar

```shell
mvn verify
java -jar target/laboratorio-web-0.0.1-SNAPSHOT.jar
```

El test inicial consulta health con servidor real y puerto aleatorio.
No demuestra todavía que el catálogo o los ejercicios estén implementados.

## Recorrido

Leé el [caso](caso/catalogo.md) y el [índice](ejercicios/README.md).
Thymeleaf, Spring Security, el stub HTTP y springdoc se agregan en sus etapas.
Seguimos MVC; usar un cliente asíncrono no exige migrar toda la aplicación a WebFlux.

Los archivos de práctica futuros van en local/uploads, fuera de recursos estáticos
e ignorados por Git. En tests usá @TempDir. Reiniciar conserva los archivos en disco,
pero no su metadata en memoria: documentá esa limitación y limpiá solo datos propios.

## Referencias

- [Spring MVC 6.2](https://docs.spring.io/spring-framework/reference/6.2/web/webmvc.html).
- [ProblemDetail y errores](https://docs.spring.io/spring-framework/reference/6.2/web/webmvc/mvc-ann-rest-exceptions.html).
- [Multipart](https://docs.spring.io/spring-framework/reference/6.2/web/webmvc/mvc-controller/ann-methods/multipart-forms.html).
- [springdoc y compatibilidad de versiones](https://springdoc.org/).

Las consignas precisan las simplificaciones de la guía: los filtros rodean al servlet,
la idempotencia describe efectos y no respuestas idénticas, y el nombre original de
un archivo no es una ruta confiable.

