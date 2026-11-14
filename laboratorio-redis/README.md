# Laboratorio Redis

Proyecto Maven independiente con Java 21, Spring Boot 3.5.16, Spring Data Redis
(Lettuce) y Redis 7.4. Basado en [Glosario Spring Redis](../guias/Glosario-Spring-Redis.md).

## Qué está preparado

Aplicación arrancable, Compose con persistencia AOF, colección inicial de Postman y
pruebas de conexión, lectura y expiración con Testcontainers. Las diez consignas están
escritas; los endpoints de negocio se implementan al resolverlas. No hay dependencia
del laboratorio de pagos ni hace falta PostgreSQL para este recorrido.

## Arranque

1. Abrí `pom.xml` en IntelliJ como proyecto Maven o agregalo desde la ventana Maven.
2. Seleccioná JDK 21 para el proyecto y para el runner/importador Maven.
3. Iniciá Docker Desktop. Desde esta carpeta ejecutá:

```shell
docker compose up -d --wait
docker compose exec redis redis-cli ping
```

El resultado debe ser `PONG`. Redis escucha en `localhost:6380`; dentro de Docker,
en `6379`. El volumen conserva los datos. Este Compose es local y no tiene contraseña;
el puerto solo está publicado en loopback. En la etapa 09 configurarás ACL.

4. Ejecutá `com.estudio.redis.LaboratorioRedisApplication` desde IntelliJ.
   Con Java y Maven en PATH, también podés usar `mvn spring-boot:run`.
5. Importá `postman/laboratorio-redis.postman_collection.json`.
   La solicitud `GET http://localhost:8081/actuator/health` debe responder
   `{"status":"UP"}`. Con Redis caído puede responder 503.

Para una segunda instancia, duplicá la configuración de ejecución y definí
`SERVER_PORT=8082`. Ambas usan el mismo Redis.
Las variables disponibles son SERVER_PORT, REDIS_HOST, REDIS_PORT, REDIS_USERNAME
y REDIS_PASSWORD.

## Inspeccionar Redis

Abrí la consola incluida en el contenedor:

```shell
docker compose exec redis redis-cli
```

Dentro de ella usá `SCAN 0 MATCH lab:redis:* COUNT 100`, `TYPE clave`, `TTL clave`
y el comando de lectura apropiado al tipo. Continuá SCAN con el cursor devuelto hasta
que sea cero. Para este laboratorio usaremos redis-cli; DBeaver no es un requisito.
RedisInsight queda como interfaz gráfica opcional.

Detené Redis conservando los datos con `docker compose down`.
No uses `down -v` si querés conservar el volumen.

## Verificación

Ejecutá `mvn verify` desde esta carpeta o desde la ventana Maven.
Testcontainers levanta un Redis temporal independiente y requiere Docker activo.
Sin Docker, las dos pruebas se omiten: compilar correctamente no verifica la integración.
La prueba de expiración espera hasta un límite; cambiar un Clock Java no adelanta el
reloj del servidor Redis.

## Recorrido completo

Consultá el [índice de consignas](ejercicios/README.md).
Los ejercicios agregan gradualmente dependencias como Spring Session; la base no las
activa antes de necesitarlas. El catálogo usará inicialmente una fuente ficticia en
memoria, reiniciada al arrancar; no la confundas con una base durable.

Documentación de contraste para revisar las simplificaciones de la guía:
[transacciones Redis](https://redis.io/docs/latest/develop/using-commands/transactions/),
[Pub/Sub](https://redis.io/docs/latest/develop/pubsub/) y
[Spring Data Redis](https://docs.spring.io/spring-data/redis/reference/).
