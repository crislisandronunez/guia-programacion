# Laboratorio Kafka

Proyecto Maven independiente con Java 21, Spring Boot 3.5.16, Spring Kafka y un
broker Apache Kafka 4.4.0 en KRaft. Las pruebas usan Testcontainers 2.0.5.
Basado en la [guía de Kafka](../guias/Glosario-Kafka.md).

## Qué está preparado

La aplicación base arranca, declara dos tópicos de tres particiones
(`pedidos-confirmados` y `pedidos-confirmados-dlt`), comprueba la conexión al broker
en `/actuator/health` y trae una colección Postman inicial. Dos pruebas con
Testcontainers comprueban salud y publicación/lectura reales. Las doce consignas
describen el trabajo de negocio; productores, listeners y endpoints se implementan
al resolverlas. El proyecto no depende de otro laboratorio.

## IntelliJ y Maven

Abrí este `pom.xml` como proyecto Maven y seleccioná JDK 21 para el proyecto, el
importador y el runner de Maven. Podés usar el Maven incluido en IntelliJ; no se
incluye Maven Wrapper. La configuración de código sigue Spring Boot 3.5 y Spring
Kafka 3.x. Si actualizás a Boot 4, revisá los cambios de Jackson y de Spring Kafka.

## Arranque

1. Iniciá Docker Desktop. Desde esta carpeta ejecutá:

```shell
docker compose up -d --wait
docker compose exec kafka /opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --list
```

El Compose usa la imagen oficial de Kafka con configuración local de un nodo.
Publica `9092` solo en loopback. El healthcheck verifica que el broker responde
por su API. Este nodo **no da alta disponibilidad** y se ejecuta sin autenticación:
usalo solo en tu máquina de estudio.

2. Ejecutá `com.estudio.kafka.LaboratorioKafkaApplication` desde IntelliJ.
   Si Java y Maven están en PATH, también podés usar `mvn spring-boot:run`.
3. Importá `postman/laboratorio-kafka.postman_collection.json` y ejecutá
   `GET http://localhost:8093/actuator/health`. Con Kafka disponible debe dar
   HTTP 200 y `{"status":"UP"}`; con el broker inaccesible, la comprobación
   específica de Kafka debe dar `DOWN` y la respuesta global será 503.

El puerto HTTP se cambia con `SERVER_PORT`; la dirección del broker con
`KAFKA_BOOTSTRAP_SERVERS`. La aplicación corre en el host, por eso usa
`localhost:9092`. Si la ejecutás dentro de otro contenedor, revisá las direcciones
anunciadas por Kafka: sustituir solo `bootstrap-servers` puede no bastar.

## Inspeccionar Kafka

```shell
docker compose exec kafka /opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --describe --topic pedidos-confirmados
docker compose exec kafka /opt/kafka/bin/kafka-consumer-groups.sh --bootstrap-server localhost:9092 --list
```

La [primera consigna](ejercicios/01-topicos-y-consola.md) usa productores y
consumidores de consola. Un grupo aparece después de consumir y confirmar offsets;
un tópico existente por sí solo no crea un grupo. Las herramientas Kafka se
ejecutan **dentro** del contenedor para evitar depender de scripts locales.

## PostgreSQL en etapas posteriores

Las etapas 08 y 09 agregan JDBC, deduplicación y outbox. Cuando llegues ahí,
levantá PostgreSQL sin cambiar de proyecto:

```shell
docker compose -f compose.yaml -f compose.postgres.yaml up -d --wait
```

PostgreSQL local escucha en `127.0.0.1:5440`, con base y usuario `kafka_lab` y
contraseña de desarrollo `kafka_lab_local`. El volumen `postgres_data` conserva
sus datos. **La aplicación base todavía no abre una conexión SQL**: añadí las
dependencias y propiedades en la etapa correspondiente. Las pruebas de esa etapa
deben usar contenedores propios, no el volumen del Compose.

## Pruebas y datos

Ejecutá `mvn verify` o la fase Verify desde IntelliJ. Las dos pruebas usan un
broker efímero de Testcontainers, no el Compose local. Con Docker ausente se omiten;
un resultado verde en ese caso **no verifica Kafka**. La prueba de publicación
comprueba clave, valor, partición y offset del registro recibido.

El Compose básico no monta un volumen para Kafka: `docker compose stop` conserva
el contenedor y sus datos para el próximo `start`; `docker compose down` lo elimina
y perdés el historial local. Eso permite repetir los experimentos desde cero.
El volumen de PostgreSQL es independiente. Si levantaste ambos servicios con
dos archivos, usá los mismos dos archivos para detenerlos:

```shell
docker compose -f compose.yaml -f compose.postgres.yaml down
```

No agregues `-v` si querés conservar PostgreSQL. No reinicies offsets o borres
tópicos de un clúster compartido para resolver ejercicios de este laboratorio.

## Recorrido

Leé el [caso de pedidos](caso/pedidos.md) y el [índice de consignas](ejercicios/README.md).
La base solo prepara infraestructura y pruebas de conexión; cada etapa agrega
código, pruebas y solicitudes Postman. La entrega de un registro, el avance del
grupo y el efecto en PostgreSQL se comprueban por separado.

Referencias técnicas: [Docker oficial de Kafka](https://kafka.apache.org/44/getting-started/docker/),
[Spring Boot con Kafka](https://docs.spring.io/spring-boot/3.5/reference/messaging/kafka.html)
y [Testcontainers Kafka](https://java.testcontainers.org/modules/kafka/).
