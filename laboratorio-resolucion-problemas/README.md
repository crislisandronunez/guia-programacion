# Laboratorio de resolución de problemas empresariales

Java 21, Maven, Spring Boot 3.5.16 y PostgreSQL 17.
Basado en [Resolución de problemas](../guias/Resolucion-de-Problemas.md).

El caso es una distribuidora que recibe pedidos por planillas. Vas a investigar el dolor,
definir reglas, construir una API y demostrar que la solución mejora el proceso.
No es un laboratorio de acertijos algorítmicos.

## Qué está listo

- Proyecto Maven independiente con Web, Validation, JPA y Actuator.
- PostgreSQL en Docker; RabbitMQ opcional para la etapa de outbox.
- Colección inicial de Postman y prueba de integración con Testcontainers.
- Diez consignas completas, un caso ficticio y CSV de práctica.

La base arranca, pero todavía no contiene entidades ni endpoints de negocio.
Las consignas son tareas por resolver, no soluciones implementadas.
No depende de los laboratorios de pagos o Redis.

## IntelliJ y Maven

Hacé clic derecho en este `pom.xml` y elegí **Add as Maven Project**, o abrilo como
proyecto. Seleccioná JDK 21 para Project SDK y para el runner/importador de Maven.
Podés usar el Maven incluido en IntelliJ. No se incluye Maven Wrapper.

## Arranque

Iniciá Docker Desktop y ejecutá desde esta carpeta:

```shell
docker compose up -d --wait
```

Ejecutá `com.estudio.problemas.LaboratorioProblemasApplication` desde IntelliJ.
Con Java 21 y Maven en PATH, la alternativa es `mvn spring-boot:run`.
La aplicación escucha en `http://localhost:8083`.
Importá `postman/laboratorio-problemas.postman_collection.json` y comprobá que
`GET /actuator/health` responde HTTP 200 con `{"status":"UP"}`.

## DBeaver

| Campo | Valor |
|---|---|
| Tipo | PostgreSQL |
| Host | localhost |
| Puerto | 5435 |
| Base | problemas_lab |
| Usuario | problemas_lab |
| Contraseña local | problemas_lab_local |

No habrá tablas de negocio hasta resolver la etapa 02. Hibernate valida el esquema;
las migraciones se incorporan como parte del ejercicio.
Podés sobrescribir DB_URL, DB_USERNAME, DB_PASSWORD y SERVER_PORT desde IntelliJ.

## Mensajería, desde la etapa 07

```shell
docker compose --profile mensajeria up -d --wait
```

RabbitMQ: localhost:5673. Panel local: http://localhost:15673.
Usuario y contraseña: problemas_lab / problemas_lab_local.
La dependencia Spring AMQP y su configuración son parte de esa etapa; el arranque inicial
no necesita RabbitMQ. Son credenciales ficticias para uso local.

Para detener todos los servicios conservando sus volúmenes:

```shell
docker compose --profile mensajeria down
```

## Verificación

Ejecutá `mvn verify` desde la carpeta o el objetivo verify desde IntelliJ.
La prueba usa PostgreSQL temporal y requiere Docker activo. Sin Docker se omite;
un build correcto en ese caso solo verifica compilación y empaquetado.

## Comenzar

Leé el [caso de la distribuidora](caso/distribuidora.md) y el
[índice de ejercicios](ejercicios/README.md). La primera etapa produce un análisis;
recién después incorporás código de negocio.
