# Laboratorio de resiliencia y tolerancia a fallos

Java 21, Maven, Spring Boot 3.5.16 y Resilience4j 2.3.0.
Basado en [Glosario de resiliencia](../guias/Glosario-Reciliencia-Programacion.md).

## Qué incluye

- Base Spring Boot con Web, Validation, Actuator, AOP y Resilience4j para Boot 3.
- Prueba de arranque y colección inicial de Postman.
- Caso ficticio y catorce etapas con experimentos, límites y criterios de aceptación.
- Compose con PostgreSQL, Redis y RabbitMQ activados por perfiles para etapas posteriores.

No hay clientes HTTP de negocio, simuladores, servicios, políticas de reintento,
circuitos configurados ni soluciones de los ejercicios. Vos los vas a construir.
Usamos la integración directa resilience4j-spring-boot3; no se agrega Spring Cloud
CircuitBreaker ni un segundo mecanismo que duplique las mismas protecciones.

## Abrir y ejecutar la base

Agregá este pom.xml con **Add as Maven Project** en IntelliJ o abrilo por separado.
Seleccioná JDK 21 para Project SDK, importador y runner de Maven.
Podés usar Maven incluido en IntelliJ; no se incluye Maven Wrapper.

Ejecutá com.estudio.resiliencia.LaboratorioResilienciaApplication.
Con Java 21 y Maven en PATH, desde esta carpeta:

```shell
mvn spring-boot:run
```

Importá postman/laboratorio-resiliencia.postman_collection.json.
GET http://localhost:8088/actuator/health debe responder 200 y {"status":"UP"}.
Podés cambiar el puerto con SERVER_PORT y actualizar baseUrl en Postman.
La base no requiere Docker. Todavía no existen rutas /api ni /lab.

## Verificar y empaquetar

```shell
mvn verify
java -jar target/laboratorio-resiliencia-0.0.1-SNAPSHOT.jar
```

También podés ejecutar verify desde Maven en IntelliJ. El test inicial usa HTTP real
en un puerto aleatorio. Solo verifica el arranque; no demuestra tolerancia a fallos.

## Infraestructura para etapas avanzadas

Iniciá Docker Desktop cuando llegues a la etapa correspondiente. Desde esta carpeta:

```shell
docker compose --profile datos up -d --wait
docker compose --profile cache up -d --wait
docker compose --profile mensajeria up -d --wait
```

Cada comando activa el servicio indicado; no necesitás ejecutar los tres al comenzar.
Las dependencias Java, conexiones y pruebas de integración se incorporan en las
etapas 10–12. Los perfiles de Compose no activan perfiles de Spring automáticamente.

| Servicio | Conexión local | Datos de práctica |
|---|---|---|
| PostgreSQL, etapa 10 | localhost:5437 | Base/usuario resiliencia_lab; clave resiliencia_lab_local |
| Redis, etapa 11 | localhost:6381 | Sin contraseña; prefijo lab:resiliencia: |
| RabbitMQ, etapa 12 | localhost:5674 | Usuario resiliencia_lab; clave resiliencia_lab_local |
| Panel RabbitMQ | http://localhost:15674 | Mismas credenciales |

Podés conectar DBeaver al PostgreSQL de esta tabla. No habrá tablas hasta que hagas
las migraciones. Todos los servicios se publican solo en loopback y usan datos ficticios.

Para detenerlos conservando los volúmenes:

```shell
docker compose --profile datos --profile cache --profile mensajeria down
```

No se incluyen clusters: un contenedor PostgreSQL o RabbitMQ no demuestra alta disponibilidad.

## Recorrido y referencias

Leé el [caso](caso/tienda.md) y el [índice de ejercicios](ejercicios/README.md).
La guía se usa como material de estudio, no como una configuración universal para
producción. Vas a contrastar sus ejemplos con mediciones y contratos de negocio.

- [Integración Resilience4j con Spring Boot](https://resilience4j.readme.io/docs/getting-started-3).
- [Circuit Breaker](https://resilience4j.readme.io/docs/circuitbreaker).
- [TimeLimiter](https://resilience4j.readme.io/docs/timeout).
- [Versión 2.3.0 de referencia](https://github.com/resilience4j/resilience4j/releases/tag/v2.3.0).
- [HikariCP](https://github.com/brettwooldridge/HikariCP#configuration-knobs-baby).
- [Confirms y acknowledgements de RabbitMQ](https://www.rabbitmq.com/docs/confirms).

