# Laboratorio MongoDB

Java 21, Maven, Spring Boot 3.5.16, Spring Data MongoDB síncrono y MongoDB 8.0.
Basado en [Glosario MongoDB](../guias/Glosario-Spring-MongoDB.md).

## Qué está listo

Aplicación base, Compose con replica set local de un nodo, inicialización repetible,
colección Postman, dos pruebas con Testcontainers y doce consignas completas.
Los documentos y endpoints de negocio son tareas por implementar.
No depende de PostgreSQL, JPA ni de otro laboratorio.

## IntelliJ y Maven

Clic derecho en este `pom.xml` → **Add as Maven Project**.
Seleccioná JDK 21 para proyecto, importador y runner Maven.
Podés usar el Maven incluido en IntelliJ; no se incluye Maven Wrapper.

## Arranque en dos pasos

Iniciá Docker Desktop. Desde esta carpeta ejecutá:

```shell
docker compose up -d --wait
docker compose exec mongo mongosh --quiet --port 27018 --file /scripts/init-replica.js
```

La segunda orden debe imprimir `Replica set rs0 listo`; podés repetirla sin borrar datos.
El healthcheck del contenedor solo comprueba PING, no que la elección del primary terminó.

Ejecutá `com.estudio.mongo.LaboratorioMongoApplication` en IntelliJ.
Con Java y Maven en PATH, también sirve `mvn spring-boot:run`.
Importá la colección de `postman/`: GET `http://localhost:8085/actuator/health`
debe devolver HTTP 200 y `{"status":"UP"}`.

MongoDB se publica solo en localhost:27018 y no tiene autenticación en este entorno
local. El replica set de un nodo permite practicar transacciones, pero no alta
disponibilidad. No uses esta configuración para exponer MongoDB a una red externa.

## Explorar documentos

Usá la consola incluida:

```shell
docker compose exec mongo mongosh --port 27018 mongo_lab
```

Dentro de mongosh:

```javascript
db.getName()
show collections
db.runCommand({ hello: 1 }).isWritablePrimary
```

Las colecciones de negocio aparecen al implementar los ejercicios.
MongoDB Compass es opcional. Si ya lo tenés, usá:

```text
mongodb://localhost:27018/mongo_lab?replicaSet=rs0&directConnection=true
```

DBeaver no es necesario. La conexión directa y localhost son decisiones de este
laboratorio en un único equipo; al cambiar topología deben revisarse las direcciones
anunciadas del replica set.

## Configuración y apagado

Variables: MONGODB_URI y SERVER_PORT. El perfil lab habilita logs de MongoTemplate:
`SPRING_PROFILES_ACTIVE=lab`. Solo datos ficticios en esos logs.
La creación automática de índices está desactivada: se gestionan explícitamente
en los ejercicios.

```shell
docker compose down
```

Conserva el volumen. Al volver a levantarlo, ejecutá otra vez la comprobación de
inicialización. No uses down -v si querés conservar datos.

## Pruebas y recorrido

`mvn verify` compila y ejecuta pruebas. Testcontainers crea su propio replica set
temporal, sin usar el volumen local. Sin Docker las pruebas se omiten: un build exitoso
no demuestra integración en ese caso.

Comenzá por el [caso](caso/catalogo.md) y el [índice de ejercicios](ejercicios/README.md).
