# Laboratorio Spring Core y Spring Boot Core

Java 21, Maven y Spring Boot 3.5.16, en línea con los otros laboratorios.
Basado en [Glosario Spring Core y Spring Boot Core](../guias/Glosario-Spring-Core-Boot-Core.md).

## Qué incluye

- Aplicación mínima con Web, Validation y Actuator.
- Prueba de arranque HTTP y colección inicial de Postman.
- Un caso ficticio y dieciséis etapas vinculadas con las treinta secciones de la guía.

Las clases de negocio, configuraciones didácticas, aspectos, listeners y pruebas de
los ejercicios son entregables tuyos. La base solo verifica el arranque.
No requiere Docker, base de datos, DBeaver ni servicios externos.
Los datos se guardarán en memoria y se perderán al reiniciar.

## Abrir en IntelliJ

Agregá este `pom.xml` con **Add as Maven Project**, o abrilo como proyecto independiente.
Seleccioná JDK 21 para Project SDK y para el importador y runner de Maven.
Podés usar Maven incluido en IntelliJ; no se incluye Maven Wrapper.

## Ejecutar y comprobar

Ejecutá `com.estudio.core.LaboratorioCoreApplication` desde IntelliJ.
Con Java 21 y Maven en PATH, desde esta carpeta:

```shell
mvn spring-boot:run
```

Importá `postman/laboratorio-spring-core.postman_collection.json`.
`GET http://localhost:8086/actuator/health` debe devolver HTTP 200 y `{"status":"UP"}`.
La ruta `/` y las rutas `/api` todavía no tienen controladores.
Podés cambiar el puerto con la variable `SERVER_PORT`; actualizá también `baseUrl` en Postman.
Detené la aplicación con Ctrl+C o el botón Stop de IntelliJ.

## Verificación y empaquetado

```shell
mvn verify
java -jar target/laboratorio-spring-core-0.0.1-SNAPSHOT.jar
```

También podés ejecutar `verify` desde la ventana Maven de IntelliJ.
El test inicial usa un puerto aleatorio y no necesita Docker. Solo comprueba la base,
no demuestra que los ejercicios estén resueltos.

## Recorrido

Leé el [caso de la central de avisos](caso/central-avisos.md) y el
[índice de ejercicios](ejercicios/README.md). Primero vas a trabajar con objetos y
contextos pequeños; después integrarás la aplicación Boot y una API mínima.
Los perfiles y las dependencias de AOP, caché y DevTools se agregan en sus etapas.

La guía orienta el estudio; los experimentos deben comprobar las garantías reales.
En particular, usar singleton no vuelve seguro un objeto compartido y publicar un
evento en memoria no garantiza entrega durable.

## Referencias

- [Documentación Spring Boot 3.5](https://docs.spring.io/spring-boot/3.5/reference/).
- [Contenedor Spring Framework 6.2](https://docs.spring.io/spring-framework/reference/6.2/core/beans.html).
- [Configuración externa y precedencia](https://docs.spring.io/spring-boot/3.5/reference/features/external-config.html).
- [Auto-configuración propia](https://docs.spring.io/spring-boot/3.5/reference/features/developing-auto-configuration.html).

Usá documentación compatible con Boot 3.5 y Framework 6.2 al comparar ejemplos.
