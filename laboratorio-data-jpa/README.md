# Laboratorio Spring Data JPA

Java 21, Maven, Spring Boot 3.5.16, Hibernate y PostgreSQL 17.
Basado en [Glosario Spring Data JPA](../guias/Glosario-Spring-Data-JPA.md).

## Qué incluye

- Aplicación Spring Boot con JPA, Web, Validation, Actuator y driver PostgreSQL.
- Compose independiente, colección inicial de Postman y prueba de integración.
- Doce consignas completas con casos de aceptación.
- Perfil lab para ver SQL y estadísticas de Hibernate.

Las entidades, consultas y endpoints de negocio son ejercicios por implementar.
La base actual solo permite comprobar el arranque y la conexión.
No depende de otros laboratorios.

## Abrir como proyecto Maven

En IntelliJ hacé clic derecho sobre este `pom.xml` → **Add as Maven Project**.
También podés abrir el POM como proyecto independiente.
Seleccioná JDK 21 para Project SDK, Maven importer y Maven runner.
Usá el Maven incluido en IntelliJ; este proyecto no incluye Maven Wrapper.

## Ejecutar

Iniciá Docker Desktop. Desde esta carpeta:

```shell
docker compose up -d --wait
```

Ejecutá `com.estudio.jpa.LaboratorioJpaApplication` desde IntelliJ.
Para ver SQL, agregá `SPRING_PROFILES_ACTIVE=lab` a su configuración de ejecución.
Con Java 21 y Maven en PATH también podés usar `mvn spring-boot:run`.

Importá `postman/laboratorio-data-jpa.postman_collection.json`.
GET `http://localhost:8084/actuator/health` debe responder HTTP 200 y
`{"status":"UP"}`. Un arranque correcto no implica que los ejercicios estén resueltos.

## Conectar DBeaver

| Campo | Valor |
|---|---|
| Tipo | PostgreSQL |
| Host | localhost |
| Puerto | 5436 |
| Base | jpa_lab |
| Usuario | jpa_lab |
| Contraseña local | jpa_lab_local |

Todavía no hay tablas de negocio. Flyway y la primera migración son parte del ejercicio 01.
La configuración usa `ddl-auto: validate` y `open-in-view: false`.
Variables opcionales: DB_URL, DB_USERNAME, DB_PASSWORD y SERVER_PORT.

Para detener PostgreSQL conservando el volumen:

```shell
docker compose down
```

## Pruebas

Ejecutá `mvn verify` o el objetivo verify desde Maven en IntelliJ.
La prueba inicial arranca la aplicación con PostgreSQL temporal de Testcontainers.
Sin Docker se omite; eso no valida el funcionamiento contra una base real.

Al resolver los ejercicios agregá pruebas @DataJpaTest con PostgreSQL y sin reemplazo
por H2. Usá flush y clear cuando necesites comprobar SQL real, y transacciones separadas
para observar commits y carreras. Los tests con rollback automático no prueban por sí
solos que otro cliente verá los datos.

## Recorrido

Leé el [caso de la tienda](caso/tienda.md) y el [índice de ejercicios](ejercicios/README.md).
Los temas de la guía se distribuyen en doce etapas; las anotaciones avanzadas de Hibernate
se identifican como exploración adicional para no confundirlas con JPA portable.
