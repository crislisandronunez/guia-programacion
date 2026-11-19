# Glosario Spring Data Redis

Términos, comandos, clases y patrones para entender Redis y su integración con Spring Boot: caché, sesiones, estructuras de datos, `RedisTemplate`, repositorios, pub/sub, locks, rate limiting y más.

## Índice
1. [Conceptos generales de Redis](#1-conceptos-generales-de-redis)
2. [Dependencias y configuración](#2-dependencias-y-configuración)
3. [Clientes: Lettuce, Jedis y Redisson](#3-clientes-lettuce-jedis-y-redisson)
4. [Estructuras de datos y comandos](#4-estructuras-de-datos-y-comandos)
5. [Claves, expiración (TTL) y persistencia](#5-claves-expiración-ttl-y-persistencia)
6. [RedisTemplate y StringRedisTemplate](#6-redistemplate-y-stringredistemplate)
7. [Serializadores](#7-serializadores)
8. [Operaciones por estructura (opsFor...)](#8-operaciones-por-estructura-opsfor)
9. [Repositorios Redis (@RedisHash)](#9-repositorios-redis-redishash)
10. [Caché con Spring Cache](#10-caché-con-spring-cache)
11. [Sesiones con Spring Session](#11-sesiones-con-spring-session)
12. [Pub/Sub y Streams](#12-pubsub-y-streams)
13. [Transacciones, pipelines y scripts Lua](#13-transacciones-pipelines-y-scripts-lua)
14. [Patrones comunes](#14-patrones-comunes)
15. [Alta disponibilidad: Sentinel, Cluster y réplicas](#15-alta-disponibilidad-sentinel-cluster-y-réplicas)
16. [Seguridad y producción](#16-seguridad-y-producción)
17. [Testing con Redis](#17-testing-con-redis)
18. [Monitoreo y herramientas](#18-monitoreo-y-herramientas)
19. [Excepciones y errores frecuentes](#19-excepciones-y-errores-frecuentes)
20. [Ejemplo completo](#20-ejemplo-completo)

---

## 1. Conceptos generales de Redis

| Término | Significado |
|---|---|
| **Redis** | *REmote DIctionary Server*: base de datos **en memoria** clave-valor, ultrarrápida, con estructuras de datos avanzadas. |
| **In-memory** | Los datos viven en la **RAM**; acceso en microsegundos. |
| **Clave-valor (key-value)** | Cada dato se guarda y se busca por una **clave** única. |
| **NoSQL** | Base de datos no relacional. |
| **Single-threaded** | Redis ejecuta los comandos en **un solo hilo** (cada comando es atómico). |
| **Atomicidad** | Cada comando se ejecuta completo sin interrupciones de otros clientes. |
| **Caché** | Almacén temporal rápido para evitar consultas costosas a la BD. |
| **TTL** | *Time To Live*: tiempo de vida de una clave; luego se borra sola. |
| **Hit / Miss** | Acierto (el dato estaba en caché) / fallo (no estaba). |
| **Hit ratio** | % de aciertos de caché. |
| **Eviction (desalojo)** | Redis elimina claves cuando llena la memoria (según una política). |
| **Persistencia** | Guardar los datos en disco para sobrevivir a reinicios (RDB / AOF). |
| **RDB** | Snapshots periódicos del dataset en disco. |
| **AOF** | *Append Only File*: registra cada operación de escritura. |
| **Pub/Sub** | Mensajería de publicación/suscripción. |
| **Stream** | Estructura tipo log de eventos con grupos de consumidores. |
| **Replicación** | Copia de los datos de un nodo principal (master) a réplicas. |
| **Master / Primary / Réplica (replica)** | Nodo de escritura / nodos de copia (lectura). |
| **Sentinel** | Servicio que monitorea y hace *failover* automático. |
| **Cluster** | Redis distribuido con **sharding** (los datos se reparten entre nodos). |
| **Hash slot** | Cluster divide el espacio de claves en **16384 slots**. |
| **Sharding** | Repartir datos entre varios nodos. |
| **Failover** | Promover una réplica a master cuando el master cae. |
| **Cache-aside** | Patrón: buscar en caché; si no está, ir a la BD y guardar en caché. |
| **Write-through / Write-behind** | Escribir en caché y BD a la vez / en caché y luego a la BD. |
| **Cache stampede (thundering herd)** | Muchas peticiones simultáneas recalculan el mismo dato al expirar la caché. |
| **Cache penetration** | Peticiones por claves que no existen y siempre llegan a la BD. |
| **Cache avalanche** | Muchas claves expiran a la vez y la BD se satura. |
| **Hot key** | Clave con una cantidad desproporcionada de accesos. |
| **Big key** | Valor muy grande que ralentiza Redis. |
| **Namespace / prefijo** | Convención `app:entidad:id` para organizar claves. |
| **Base de datos lógica** | Redis tiene 16 DBs numeradas (0–15) en modo standalone (en cluster solo la 0). |
| **Puerto por defecto** | `6379`. |
| **RESP** | Protocolo de comunicación de Redis. |
| **Redis Stack** | Redis con módulos extra (JSON, Search, TimeSeries, Bloom). |
| **Valkey** | Fork open source de Redis (compatible). |
| **Dragonfly / KeyDB** | Alternativas compatibles con el protocolo Redis. |

### Cuándo usar Redis
| Caso de uso | Estructura típica |
|---|---|
| Caché de consultas | `String` / `Hash` con TTL |
| Sesiones de usuario | `Hash` (Spring Session) |
| Contadores / rate limiting | `String` con `INCR` |
| Ranking / leaderboard | `Sorted Set` |
| Colas simples | `List` |
| Mensajería / eventos | `Pub/Sub` / `Stream` |
| Locks distribuidos | `String` con `SET NX PX` |
| Tokens (JWT blacklist, OTP) | `String` con TTL |
| Geolocalización | `Geo` |
| Conteo único aproximado | `HyperLogLog` |
| Banderas por usuario | `Bitmap` |

> Redis **no sustituye** a tu BD principal: es un complemento rápido y volátil (aunque puede persistir).

## 2. Dependencias y configuración

| Término | Significado |
|---|---|
| `spring-boot-starter-data-redis` | Starter de Spring Data Redis (cliente **Lettuce** por defecto). |
| `spring-boot-starter-cache` | Soporte de `@Cacheable`. |
| `spring-session-data-redis` | Sesiones HTTP en Redis. |
| `commons-pool2` | Necesario para el **pool de conexiones** de Lettuce. |
| `jedis` | Cliente alternativo (hay que excluir Lettuce). |
| `redisson-spring-boot-starter` | Cliente con locks, colecciones distribuidas, etc. |
| `spring-boot-starter-data-redis-reactive` | Redis reactivo (`ReactiveRedisTemplate`). |
| `spring.data.redis.host` | Host (por defecto `localhost`). |
| `spring.data.redis.port` | Puerto (`6379`). |
| `spring.data.redis.password` | Contraseña. |
| `spring.data.redis.username` | Usuario (ACL, Redis 6+). |
| `spring.data.redis.database` | Número de DB lógica. |
| `spring.data.redis.timeout` | Timeout de comandos. |
| `spring.data.redis.connect-timeout` | Timeout de conexión. |
| `spring.data.redis.ssl.enabled` | Usar TLS. |
| `spring.data.redis.url` | URL completa: `redis://user:pass@host:6379/0`. |
| `spring.data.redis.client-type` | `lettuce` o `jedis`. |
| `spring.data.redis.lettuce.pool.*` | Pool de Lettuce (`max-active`, `max-idle`, `min-idle`, `max-wait`). |
| `spring.data.redis.sentinel.*` | Configuración Sentinel (`master`, `nodes`, `password`). |
| `spring.data.redis.cluster.*` | Configuración Cluster (`nodes`, `max-redirects`). |
| `spring.data.redis.repositories.enabled` | Activa/desactiva repositorios Redis. |
| `spring.cache.type=redis` | Usa Redis como proveedor de caché. |
| `spring.cache.redis.time-to-live` | TTL por defecto de la caché. |
| `spring.cache.redis.key-prefix` | Prefijo de claves. |
| `spring.cache.redis.use-key-prefix` | Usar prefijo. |
| `spring.cache.redis.cache-null-values` | Cachear valores `null`. |
| `spring.session.store-type` / `spring.session.timeout` | Sesión en Redis. |
| `RedisConnectionFactory` | Fábrica de conexiones (`LettuceConnectionFactory`, `JedisConnectionFactory`). |
| `RedisStandaloneConfiguration` | Config. de un solo nodo. |
| `RedisSentinelConfiguration` / `RedisClusterConfiguration` | Config. HA / cluster. |
| `RedisPassword` | Contraseña tipada. |
| `LettuceClientConfiguration` | Config. avanzada del cliente (timeouts, SSL, pool). |
| **Nota de versión** | En Spring Boot < 3.2 las propiedades eran `spring.redis.*`; desde 3.x son `spring.data.redis.*`. |

### Ejemplo: dependencias
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>
<!-- Necesario para usar pool con Lettuce -->
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-pool2</artifactId>
</dependency>
<!-- Sesiones -->
<dependency>
    <groupId>org.springframework.session</groupId>
    <artifactId>spring-session-data-redis</artifactId>
</dependency>
```

### Ejemplo: `application.yml`
```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      password: ${REDIS_PASSWORD:}
      database: 0
      timeout: 2s
      connect-timeout: 2s
      lettuce:
        pool:
          max-active: 16
          max-idle: 8
          min-idle: 2
          max-wait: 1s
  cache:
    type: redis
    redis:
      time-to-live: 10m
      key-prefix: "miapp::"
      use-key-prefix: true
      cache-null-values: false
```

### Ejemplo: levantar Redis con Docker
```bash
docker run -d --name redis -p 6379:6379 redis:7-alpine
docker run -d --name redis -p 6379:6379 redis:7-alpine --requirepass miClave

# Cliente de línea de comandos
docker exec -it redis redis-cli
docker exec -it redis redis-cli -a miClave ping      # → PONG

# Redis Stack (con interfaz web en :8001)
docker run -d --name redis-stack -p 6379:6379 -p 8001:8001 redis/redis-stack
```
```yaml
# compose.yaml
services:
  redis:
    image: redis:7-alpine
    command: ["redis-server", "--requirepass", "${REDIS_PASSWORD}", "--appendonly", "yes"]
    ports: ["6379:6379"]
    volumes: ["redis-data:/data"]
volumes:
  redis-data:
```

### Ejemplo: configuración por código
```java
@Configuration
public class RedisConfig {

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration cfg = new RedisStandaloneConfiguration("localhost", 6379);
        cfg.setPassword(RedisPassword.of("miClave"));
        cfg.setDatabase(0);

        LettuceClientConfiguration client = LettuceClientConfiguration.builder()
                .commandTimeout(Duration.ofSeconds(2))
                .useSsl()                                  // TLS (opcional)
                .build();
        return new LettuceConnectionFactory(cfg, client);
    }
}
```

## 3. Clientes: Lettuce, Jedis y Redisson

| Cliente | Características |
|---|---|
| **Lettuce** | **(Por defecto en Spring Boot).** Basado en Netty, **thread-safe**: una conexión compartida sirve a muchos hilos; soporta modo reactivo y asíncrono; no necesita pool salvo en transacciones/bloqueantes. |
| **Jedis** | Cliente clásico y simple, **no thread-safe** (requiere pool de conexiones). Solo síncrono. |
| **Redisson** | Cliente de alto nivel: **locks distribuidos**, semáforos, colas, mapas, rate limiter, `RLock`, `RMap`, etc. |
| **Spring Data Redis** | Capa de abstracción de Spring sobre Lettuce/Jedis (`RedisTemplate`, repositorios, caché). |
| `ReactiveRedisTemplate` | Versión reactiva (WebFlux). |
| `RedisConnection` | Conexión de bajo nivel (comandos crudos). |
| `RedisCallback` / `SessionCallback` | Ejecutar varias operaciones en la misma conexión/sesión. |
| **Pool de conexiones** | Conjunto de conexiones reutilizables (necesita `commons-pool2`). |
| `ClientResources` | Recursos compartidos de Lettuce (hilos de Netty). |

### Ejemplo: cambiar a Jedis
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
    <exclusions>
        <exclusion>
            <groupId>io.lettuce</groupId>
            <artifactId>lettuce-core</artifactId>
        </exclusion>
    </exclusions>
</dependency>
<dependency>
    <groupId>redis.clients</groupId>
    <artifactId>jedis</artifactId>
</dependency>
```
```yaml
spring:
  data:
    redis:
      client-type: jedis
      jedis:
        pool:
          max-active: 16
```

### Ejemplo: Redisson
```xml
<dependency>
    <groupId>org.redisson</groupId>
    <artifactId>redisson-spring-boot-starter</artifactId>
    <version>3.37.0</version>
</dependency>
```
```java
@Autowired RedissonClient redisson;

RLock lock = redisson.getLock("bloqueo:pedido:" + id);
if (lock.tryLock(5, 30, TimeUnit.SECONDS)) {      // espera 5 s, expira a los 30 s
    try { /* sección crítica */ } finally { lock.unlock(); }
}
RMap<String, String> mapa = redisson.getMap("config");
RRateLimiter limiter = redisson.getRateLimiter("api");
```

### Comparativa
| | Lettuce | Jedis | Redisson |
|---|---|---|---|
| Thread-safe | **Sí** | No (pool) | Sí |
| Reactivo/async | Sí | No | Sí |
| Estructuras distribuidas (locks, colas) | No | No | **Sí** |
| Integración Spring Boot | Por defecto | Opcional | Starter propio |
| Recomendado | **General** | Casos simples/legado | Locks y estructuras avanzadas |

## 4. Estructuras de datos y comandos

Con `redis-cli` (o `docker exec -it redis redis-cli`) puedes probar cada comando.

### Tipos de datos
| Tipo | Descripción | Casos de uso |
|---|---|---|
| **String** | Texto o bytes (hasta 512 MB). También números (`INCR`). | Caché, contadores, tokens, JSON serializado. |
| **Hash** | Mapa campo → valor dentro de una clave. | Objetos (usuario con campos), sesiones. |
| **List** | Lista ordenada por inserción (lista enlazada). | Colas, historial, últimos N. |
| **Set** | Conjunto **sin duplicados** y sin orden. | Tags, seguidores, únicos. |
| **Sorted Set (ZSet)** | Conjunto con **puntuación (score)**, ordenado. | Rankings, planificación por tiempo, rate limit. |
| **Bitmap** | Operaciones de bits sobre un String. | Asistencia diaria, flags. |
| **HyperLogLog** | Conteo aproximado de elementos únicos (memoria fija ~12 KB). | Visitantes únicos. |
| **Geo** | Coordenadas y distancias. | "Cerca de mí". |
| **Stream** | Log de eventos con consumer groups. | Event sourcing, colas fiables. |
| **Bloom filter / JSON / Search** | Módulos de Redis Stack. | Existencia probable, documentos, búsqueda. |

### Comandos generales
| Comando | Significado |
|---|---|
| `PING` | Comprueba la conexión (`PONG`). |
| `SET clave valor` | Guarda un valor. |
| `GET clave` | Lee un valor. |
| `DEL clave` | Borra una clave. |
| `UNLINK clave` | Borra en segundo plano (no bloquea). |
| `EXISTS clave` | ¿Existe? |
| `KEYS patrón` | Lista claves por patrón. **Evitar en producción** (bloquea). |
| `SCAN cursor MATCH patrón COUNT n` | Recorre claves **de forma incremental** (usar en lugar de `KEYS`). |
| `TYPE clave` | Tipo de dato. |
| `RENAME a b` | Renombrar. |
| `EXPIRE clave seg` / `PEXPIRE clave ms` | Fija TTL. |
| `TTL clave` / `PTTL clave` | Tiempo restante (`-1` sin TTL, `-2` no existe). |
| `PERSIST clave` | Quita el TTL. |
| `FLUSHDB` / `FLUSHALL` | Borra la DB actual / **todas** las DBs. **Peligroso.** |
| `SELECT n` | Cambia de DB lógica. |
| `DBSIZE` | Cantidad de claves. |
| `INFO` | Estadísticas del servidor (`INFO memory`, `INFO stats`). |
| `MONITOR` | Muestra todos los comandos en vivo (solo depuración). |
| `CONFIG GET/SET` | Ver/cambiar configuración. |
| `MEMORY USAGE clave` | Memoria que ocupa una clave. |
| `OBJECT ENCODING clave` | Codificación interna. |
| `DUMP` / `RESTORE` | Serializar/restaurar una clave. |

### Comandos por estructura

**String**
| Comando | Significado |
|---|---|
| `SET k v [EX seg] [PX ms] [NX\|XX]` | `NX` solo si no existe; `XX` solo si existe; `EX/PX` TTL. |
| `SETEX k seg v` | Guarda con TTL. |
| `SETNX k v` | Solo si no existe. |
| `GETSET` / `GETDEL` / `GETEX` | Leer y modificar a la vez. |
| `MSET` / `MGET` | Varias claves de una vez. |
| `INCR` / `DECR` / `INCRBY` / `DECRBY` / `INCRBYFLOAT` | Contadores atómicos. |
| `APPEND` / `STRLEN` / `GETRANGE` / `SETRANGE` | Operaciones de texto. |

**Hash**
| Comando | Significado |
|---|---|
| `HSET k campo valor [campo valor...]` | Asigna campos. |
| `HGET k campo` / `HMGET k c1 c2` | Lee uno/varios campos. |
| `HGETALL k` | Todos los campos y valores. |
| `HDEL k campo` | Borra un campo. |
| `HEXISTS k campo` | ¿Existe el campo? |
| `HKEYS` / `HVALS` / `HLEN` | Campos / valores / cantidad. |
| `HINCRBY k campo n` | Incrementa un campo. |
| `HSETNX` | Solo si el campo no existe. |
| `HSCAN` | Recorre un hash grande. |

**List**
| Comando | Significado |
|---|---|
| `LPUSH k v` / `RPUSH k v` | Agregar por la izquierda / derecha. |
| `LPOP k` / `RPOP k` | Quitar de la izquierda / derecha. |
| `BLPOP k timeout` / `BRPOP` | Quitar **bloqueando** hasta que haya elementos (colas de trabajo). |
| `LRANGE k inicio fin` | Rango (`0 -1` = todo). |
| `LLEN` / `LINDEX` / `LSET` / `LREM` / `LTRIM` | Longitud, índice, modificar, borrar, recortar. |
| `LMOVE` / `BLMOVE` | Mover entre listas (colas fiables). |

**Set**
| Comando | Significado |
|---|---|
| `SADD k miembro` / `SREM` | Agregar / quitar. |
| `SMEMBERS k` | Todos los miembros. |
| `SISMEMBER k m` | ¿Pertenece? |
| `SCARD k` | Cantidad. |
| `SPOP` / `SRANDMEMBER` | Quitar/obtener aleatorio. |
| `SUNION` / `SINTER` / `SDIFF` | Unión / intersección / diferencia. |
| `SSCAN` | Recorrido incremental. |

**Sorted Set**
| Comando | Significado |
|---|---|
| `ZADD k score miembro` | Agrega/actualiza con puntuación. |
| `ZRANGE k 0 -1 [WITHSCORES]` | Por posición (ascendente). |
| `ZREVRANGE k 0 9 WITHSCORES` | Top 10 (descendente). |
| `ZRANGEBYSCORE k min max` | Por rango de score. |
| `ZINCRBY k n miembro` | Suma al score. |
| `ZRANK` / `ZREVRANK` | Posición. |
| `ZSCORE k m` | Score de un miembro. |
| `ZREM` / `ZREMRANGEBYSCORE` / `ZCARD` / `ZCOUNT` | Borrar, contar. |

**Otros**
| Comando | Significado |
|---|---|
| `PUBLISH canal msg` / `SUBSCRIBE canal` / `PSUBSCRIBE patrón*` | Pub/Sub. |
| `XADD` / `XREAD` / `XGROUP` / `XREADGROUP` / `XACK` | Streams. |
| `GEOADD` / `GEODIST` / `GEOSEARCH` | Geolocalización. |
| `PFADD` / `PFCOUNT` | HyperLogLog. |
| `SETBIT` / `GETBIT` / `BITCOUNT` | Bitmaps. |
| `MULTI` / `EXEC` / `DISCARD` / `WATCH` | Transacciones. |
| `EVAL` / `EVALSHA` | Scripts Lua. |

### Ejemplos en `redis-cli`
```bash
# STRING
SET usuario:1:nombre "Ana"
GET usuario:1:nombre                 # "Ana"
SET sesion:abc "datos" EX 3600       # expira en 1 hora
SET lock:pedido:5 "yo" NX PX 30000   # lock: solo si no existe, 30 s
INCR visitas                         # 1
INCRBY visitas 10                    # 11
TTL sesion:abc                       # 3598

# HASH (un objeto)
HSET usuario:1 nombre "Ana" email "ana@mail.com" edad 25
HGET usuario:1 nombre
HGETALL usuario:1
HINCRBY usuario:1 edad 1

# LIST (cola)
RPUSH cola:tareas "t1" "t2" "t3"
LPOP cola:tareas                     # "t1"
LRANGE cola:tareas 0 -1
BLPOP cola:tareas 5                  # espera hasta 5 s

# SET
SADD tags:post:1 "java" "spring" "redis"
SMEMBERS tags:post:1
SISMEMBER tags:post:1 "java"         # 1
SINTER tags:post:1 tags:post:2       # tags en común

# SORTED SET (ranking)
ZADD ranking 100 "ana" 250 "luis" 180 "eva"
ZREVRANGE ranking 0 2 WITHSCORES     # top 3
ZINCRBY ranking 50 "ana"
ZREVRANK ranking "ana"               # posición (0 = primero)

# Recorrer claves sin bloquear
SCAN 0 MATCH usuario:* COUNT 100
```

### Convención de nombres de claves
```
app:entidad:id:campo

usuario:1                  → hash del usuario 1
usuario:1:sesiones         → set de sesiones del usuario 1
pedido:100:items           → lista
cache:productos::5         → caché de Spring (nombre::clave)
lock:pedido:100            → lock
rate:ip:10.0.0.1:2026-11-05T19:01   → rate limit por ventana
```
> Usa `:` como separador; incluye un prefijo de aplicación si compartes el Redis con otras apps.

## 5. Claves, expiración (TTL) y persistencia

| Término | Significado |
|---|---|
| **TTL** | Tiempo de vida de una clave en segundos. |
| `-1` / `-2` | `TTL` devuelve `-1` si no tiene expiración; `-2` si no existe. |
| **Expiración pasiva** | Se borra al intentar acceder a una clave expirada. |
| **Expiración activa** | Redis muestrea y borra periódicamente claves expiradas. |
| `maxmemory` | Memoria máxima que puede usar Redis. |
| `maxmemory-policy` | Qué hacer al llenar la memoria. |
| `noeviction` | No borra; rechaza escrituras con error `OOM`. (Defecto) |
| `allkeys-lru` | Borra las menos recientemente usadas (**típico para caché**). |
| `allkeys-lfu` | Borra las menos frecuentemente usadas. |
| `volatile-lru` / `volatile-lfu` / `volatile-ttl` | Igual, pero **solo** entre claves con TTL. |
| `allkeys-random` / `volatile-random` | Aleatorio. |
| **LRU / LFU** | *Least Recently / Frequently Used*. |
| `save 900 1` | Regla RDB: snapshot si hay ≥1 cambio en 900 s. |
| `appendonly yes` | Activa AOF. |
| `appendfsync always/everysec/no` | Frecuencia de sincronización de AOF. |
| `dump.rdb` / `appendonly.aof` | Archivos de persistencia. |
| `BGSAVE` / `SAVE` | Genera snapshot RDB (en segundo plano / bloqueante). |
| `BGREWRITEAOF` | Compacta el AOF. |
| **Solo caché (sin persistencia)** | `save ""` y `appendonly no`. |
| `lazyfree-lazy-eviction` | Borrado diferido (rendimiento). |
| **Keyspace notifications** | Eventos cuando una clave expira/cambia (`notify-keyspace-events`). |

### Estrategias de persistencia
| | RDB | AOF | Ambos |
|---|---|---|---|
| Qué guarda | Snapshots | Cada escritura | Los dos |
| Pérdida posible | Datos desde el último snapshot | ~1 segundo (`everysec`) | Mínima |
| Tamaño/arranque | Compacto, arranque rápido | Mayor, más lento | — |
| Uso | Caché con respaldo | Datos que importan | Producción general |

### Ejemplo: configuración (`redis.conf`)
```conf
maxmemory 512mb
maxmemory-policy allkeys-lru        # ideal para caché
appendonly yes
appendfsync everysec
save 900 1
save 300 10
requirepass ${REDIS_PASSWORD}
```

### Ejemplo: TTL desde Spring
```java
redisTemplate.opsForValue().set("token:abc", "datos", Duration.ofMinutes(30));  // con TTL
redisTemplate.expire("clave", Duration.ofHours(1));                              // fijar TTL
Long segundos = redisTemplate.getExpire("clave", TimeUnit.SECONDS);              // consultar
redisTemplate.persist("clave");                                                  // quitar TTL
redisTemplate.expireAt("clave", Instant.parse("2026-12-31T23:59:59Z"));          // fecha exacta
```
> **Buena práctica:** da un TTL a **todas** las claves de caché y añade un pequeño **jitter** aleatorio para evitar avalanchas.
```java
Duration ttl = Duration.ofMinutes(10).plusSeconds(ThreadLocalRandom.current().nextInt(0, 60));
```

## 6. RedisTemplate y StringRedisTemplate

| Término | Significado |
|---|---|
| `RedisTemplate<K, V>` | Clase central: operaciones tipadas sobre Redis (serializa claves y valores). |
| `StringRedisTemplate` | `RedisTemplate<String, String>` ya configurado con serializador de String. |
| `RedisOperations` | Interfaz de `RedisTemplate`. |
| `opsForValue()` | Operaciones sobre **String**. |
| `opsForHash()` | Sobre **Hash**. |
| `opsForList()` | Sobre **List**. |
| `opsForSet()` | Sobre **Set**. |
| `opsForZSet()` | Sobre **Sorted Set**. |
| `opsForGeo()` | Geoespacial. |
| `opsForHyperLogLog()` | HyperLogLog. |
| `opsForStream()` | Streams. |
| `boundValueOps(key)` / `boundHashOps(key)` / ... | Operaciones **ligadas a una clave** (no repites la clave). |
| `hasKey` / `delete` / `expire` / `keys` / `scan` / `type` / `rename` | Operaciones de claves. |
| `execute(RedisCallback)` | Acceso de bajo nivel a la conexión. |
| `execute(SessionCallback)` | Varias operaciones en la misma sesión (transacciones). |
| `executePipelined(...)` | Enviar muchos comandos en lote. |
| `convertAndSend(canal, msg)` | Publicar en un canal (Pub/Sub). |
| `setKeySerializer` / `setValueSerializer` / `setHashKeySerializer` / `setHashValueSerializer` | Serializadores. |
| `setEnableTransactionSupport(true)` | Integra con transacciones de Spring. |
| `afterPropertiesSet()` | Inicializa el template al crearlo manualmente. |
| **Bean por defecto** | Boot crea `RedisTemplate<Object,Object>` y `StringRedisTemplate`; el primero usa serialización **JDK** (poco legible). |

### Ejemplo: configurar `RedisTemplate` con JSON (recomendado)
```java
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        StringRedisSerializer keySerializer = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer valueSerializer = new GenericJackson2JsonRedisSerializer();

        template.setKeySerializer(keySerializer);
        template.setHashKeySerializer(keySerializer);
        template.setValueSerializer(valueSerializer);
        template.setHashValueSerializer(valueSerializer);
        template.afterPropertiesSet();
        return template;
    }
}
```

### Ejemplo: uso básico
```java
@Service
@RequiredArgsConstructor
public class RedisDemo {

    private final StringRedisTemplate stringTemplate;               // claves y valores String
    private final RedisTemplate<String, Object> redisTemplate;      // valores como JSON

    public void ejemplos() {
        // String simple
        stringTemplate.opsForValue().set("saludo", "hola");
        String s = stringTemplate.opsForValue().get("saludo");

        // Objeto como JSON, con TTL
        redisTemplate.opsForValue().set("usuario:1", new UsuarioDto(1L, "Ana"), Duration.ofMinutes(10));
        UsuarioDto u = (UsuarioDto) redisTemplate.opsForValue().get("usuario:1");

        // Existencia, borrado
        Boolean existe = redisTemplate.hasKey("usuario:1");
        redisTemplate.delete("usuario:1");
        redisTemplate.delete(List.of("a", "b", "c"));               // varias claves

        // Contador atómico
        Long total = stringTemplate.opsForValue().increment("visitas");

        // Solo si no existe (útil para locks)
        Boolean creado = stringTemplate.opsForValue().setIfAbsent("lock:x", "yo", Duration.ofSeconds(30));

        // Recorrer claves de forma segura
        try (Cursor<String> cursor = stringTemplate.scan(
                ScanOptions.scanOptions().match("usuario:*").count(100).build())) {
            cursor.forEachRemaining(System.out::println);
        }
    }
}
```

### Ejemplo: `boundOps` (clave fija)
```java
BoundValueOperations<String, String> contador = stringTemplate.boundValueOps("visitas");
contador.increment();
contador.expire(Duration.ofDays(1));

BoundHashOperations<String, String, String> user = stringTemplate.boundHashOps("usuario:1");
user.put("nombre", "Ana");
user.put("email", "ana@mail.com");
```

### `RedisTemplate` vs `StringRedisTemplate`
| | `StringRedisTemplate` | `RedisTemplate<K,V>` |
|---|---|---|
| Tipos | `String`/`String` | Genéricos |
| Serializador | String (legible) | El que configures |
| Cuándo | Datos de texto, contadores, tokens, JSON como String | Objetos Java directamente |
| Interoperabilidad con otras apps/`redis-cli` | **Alta** (legible) | Depende del serializador |

## 7. Serializadores

Redis solo almacena **bytes**. El serializador convierte tus objetos Java ↔ bytes.

| Serializador | Descripción |
|---|---|
| `StringRedisSerializer` | Texto UTF-8. **Ideal para claves.** |
| `JdkSerializationRedisSerializer` | Serialización Java nativa (por defecto en `RedisTemplate`). Ilegible, requiere `Serializable`, acopla a la clase y es frágil ante cambios. **Evitar.** |
| `GenericJackson2JsonRedisSerializer` | JSON con **información de tipo** (`@class`). Valores legibles; sirve para cualquier objeto. |
| `Jackson2JsonRedisSerializer<T>` | JSON para **un tipo concreto** (sin `@class`). |
| `GenericToStringSerializer<T>` | Convierte números/objetos con `toString`. |
| `OxmSerializer` | XML. |
| `ByteArrayRedisSerializer` / `RedisSerializer.byteArray()` | Bytes crudos. |
| `RedisSerializer.string()` / `.json()` / `.java()` | Atajos estáticos. |
| `RedisSerializationContext` | Equivalente para el template **reactivo** y la caché. |
| `SerializationPair` | Par serializador/deserializador. |
| `@class` | Campo con el nombre de la clase que añade `GenericJackson2JsonRedisSerializer`. |
| `ObjectMapper` personalizado | Para fechas Java Time (`JavaTimeModule`), ignorar campos desconocidos, etc. |
| `SerializationException` | Error al serializar/deserializar (clase cambió, tipo incorrecto). |

### Ejemplo: `ObjectMapper` propio con soporte de fechas
```java
@Bean
public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
    ObjectMapper mapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())                       // LocalDate, LocalDateTime...
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .activateDefaultTyping(
                    BasicPolymorphicTypeValidator.builder().allowIfBaseType(Object.class).build(),
                    ObjectMapper.DefaultTyping.NON_FINAL,
                    JsonTypeInfo.As.PROPERTY)                      // guarda @class
            .build();

    GenericJackson2JsonRedisSerializer json = new GenericJackson2JsonRedisSerializer(mapper);

    RedisTemplate<String, Object> t = new RedisTemplate<>();
    t.setConnectionFactory(factory);
    t.setKeySerializer(RedisSerializer.string());
    t.setHashKeySerializer(RedisSerializer.string());
    t.setValueSerializer(json);
    t.setHashValueSerializer(json);
    return t;
}
```

### Ejemplo: serializador para un tipo concreto
```java
@Bean
public RedisTemplate<String, UsuarioDto> usuarioTemplate(RedisConnectionFactory factory) {
    RedisTemplate<String, UsuarioDto> t = new RedisTemplate<>();
    t.setConnectionFactory(factory);
    t.setKeySerializer(new StringRedisSerializer());
    t.setValueSerializer(new Jackson2JsonRedisSerializer<>(UsuarioDto.class));   // sin @class
    return t;
}
```

### Cómo se ve cada serializador en `redis-cli`
```
JDK:      "\xac\xed\x00\x05sr\x00\x1acom.empresa.UsuarioDto..."      ← ilegible
Jackson:  "{\"@class\":\"com.empresa.UsuarioDto\",\"id\":1,\"nombre\":\"Ana\"}"
String:   "hola"
```

### Comparativa
| | JDK | Generic Jackson | Jackson tipado | String |
|---|---|---|---|---|
| Legible en `redis-cli` | No | Sí | Sí | Sí |
| Requiere `Serializable` | Sí | No | No | — |
| Guarda tipo de clase | Sí | Sí (`@class`) | No | — |
| Cambiar la clase rompe lectura | **Mucho** | Algo | Poco | — |
| Interoperable con otros lenguajes | No | Parcial | **Sí** | Sí |
| Recomendado | **No** | Sí (cuando hay polimorfismo) | **Sí** (un solo tipo) | Claves y texto |

> **Error clásico:** `ClassCastException` o `SerializationException` tras renombrar/mover una clase si usabas `@class` o JDK. Para datos de larga vida, usa JSON **sin** tipo (`Jackson2JsonRedisSerializer`) o guarda JSON como `String`.

## 8. Operaciones por estructura (opsFor...)

### `ValueOperations` (String)
| Método | Comando Redis |
|---|---|
| `set(k, v)` / `set(k, v, Duration)` | `SET` / `SETEX` |
| `get(k)` | `GET` |
| `getAndSet(k, v)` / `getAndDelete(k)` / `getAndExpire(...)` | `GETSET` / `GETDEL` / `GETEX` |
| `setIfAbsent(k, v, Duration)` | `SET NX` |
| `setIfPresent(k, v, Duration)` | `SET XX` |
| `multiSet(map)` / `multiGet(keys)` | `MSET` / `MGET` |
| `increment(k)` / `increment(k, n)` / `decrement(k)` | `INCR` / `INCRBY` / `DECR` |
| `append(k, str)` / `size(k)` | `APPEND` / `STRLEN` |
| `setBit` / `getBit` | Bitmaps |

### `HashOperations`
| Método | Comando |
|---|---|
| `put(k, campo, valor)` / `putAll(k, map)` | `HSET` |
| `get(k, campo)` / `multiGet(k, campos)` | `HGET` / `HMGET` |
| `entries(k)` | `HGETALL` |
| `keys(k)` / `values(k)` / `size(k)` | `HKEYS` / `HVALS` / `HLEN` |
| `delete(k, campos...)` | `HDEL` |
| `hasKey(k, campo)` | `HEXISTS` |
| `increment(k, campo, n)` | `HINCRBY` |
| `putIfAbsent` | `HSETNX` |
| `scan(k, opciones)` | `HSCAN` |

### `ListOperations`
| Método | Comando |
|---|---|
| `leftPush` / `rightPush` / `leftPushAll` | `LPUSH` / `RPUSH` |
| `leftPop` / `rightPop` (con `Duration` = bloqueante) | `LPOP` / `RPOP` / `BLPOP` |
| `range(k, ini, fin)` | `LRANGE` |
| `size` / `index` / `set` / `remove` / `trim` | `LLEN` / `LINDEX` / `LSET` / `LREM` / `LTRIM` |
| `move` | `LMOVE` |

### `SetOperations`
| Método | Comando |
|---|---|
| `add` / `remove` | `SADD` / `SREM` |
| `members` / `size` / `isMember` | `SMEMBERS` / `SCARD` / `SISMEMBER` |
| `union` / `intersect` / `difference` (y `...AndStore`) | `SUNION` / `SINTER` / `SDIFF` |
| `pop` / `randomMember` | `SPOP` / `SRANDMEMBER` |

### `ZSetOperations`
| Método | Comando |
|---|---|
| `add(k, v, score)` | `ZADD` |
| `incrementScore(k, v, delta)` | `ZINCRBY` |
| `range(k, ini, fin)` / `reverseRange` | `ZRANGE` / `ZREVRANGE` |
| `rangeWithScores` / `reverseRangeWithScores` | con `WITHSCORES` → `TypedTuple` |
| `rangeByScore` / `reverseRangeByScore` | `ZRANGEBYSCORE` |
| `rank` / `reverseRank` / `score` | `ZRANK` / `ZREVRANK` / `ZSCORE` |
| `remove` / `removeRangeByScore` / `size` / `count` | `ZREM` / `ZREMRANGEBYSCORE` / `ZCARD` / `ZCOUNT` |

### Ejemplos
```java
@Service
@RequiredArgsConstructor
public class EjemplosEstructuras {

    private final RedisTemplate<String, Object> redis;
    private final StringRedisTemplate str;

    // ---------- HASH: un objeto por campos ----------
    public void hash() {
        HashOperations<String, String, Object> ops = redis.opsForHash();
        ops.put("usuario:1", "nombre", "Ana");
        ops.put("usuario:1", "edad", 25);
        ops.putAll("usuario:2", Map.of("nombre", "Luis", "edad", 40));

        Object nombre = ops.get("usuario:1", "nombre");
        Map<String, Object> todo = ops.entries("usuario:1");
        ops.increment("usuario:1", "visitas", 1);                // contador por campo
        ops.delete("usuario:1", "edad");
        redis.expire("usuario:1", Duration.ofHours(1));          // el TTL es de toda la clave
    }

    // ---------- LIST: cola de tareas ----------
    public void cola() {
        ListOperations<String, Object> ops = redis.opsForList();
        ops.rightPush("cola:tareas", "tarea1");                  // encolar
        ops.rightPush("cola:tareas", "tarea2");
        Object siguiente = ops.leftPop("cola:tareas");           // desencolar (FIFO)
        Object bloqueante = ops.leftPop("cola:tareas", Duration.ofSeconds(5));  // espera hasta 5 s
        List<Object> todas = ops.range("cola:tareas", 0, -1);
        ops.trim("historial:usuario:1", 0, 99);                  // conservar solo las últimas 100
    }

    // ---------- SET: únicos ----------
    public void conjuntos() {
        SetOperations<String, Object> ops = redis.opsForSet();
        ops.add("seguidores:ana", "luis", "eva", "luis");        // "luis" no se duplica
        Boolean sigue = ops.isMember("seguidores:ana", "luis");
        Set<Object> comunes = ops.intersect("seguidores:ana", "seguidores:pedro");
        Long cantidad = ops.size("seguidores:ana");
    }

    // ---------- SORTED SET: ranking ----------
    public void ranking() {
        ZSetOperations<String, Object> ops = redis.opsForZSet();
        ops.add("ranking", "ana", 100);
        ops.add("ranking", "luis", 250);
        ops.incrementScore("ranking", "ana", 50);

        Set<ZSetOperations.TypedTuple<Object>> top3 =
                ops.reverseRangeWithScores("ranking", 0, 2);     // top 3 con puntaje
        top3.forEach(t -> System.out.println(t.getValue() + " → " + t.getScore()));

        Long posicion = ops.reverseRank("ranking", "ana");       // 0 = primero
        Double puntos = ops.score("ranking", "ana");
    }

    // ---------- GEO ----------
    public void geo() {
        GeoOperations<String, Object> ops = redis.opsForGeo();
        ops.add("tiendas", new Point(-58.38, -34.60), "centro");
        ops.add("tiendas", new Point(-58.44, -34.57), "palermo");
        Distance d = ops.distance("tiendas", "centro", "palermo", Metrics.KILOMETERS);
        GeoResults<RedisGeoCommands.GeoLocation<Object>> cerca = ops.radius("tiendas",
                new Circle(new Point(-58.40, -34.59), new Distance(5, Metrics.KILOMETERS)));
    }

    // ---------- HYPERLOGLOG: únicos aproximados ----------
    public void unicos() {
        HyperLogLogOperations<String, String> ops = str.opsForHyperLogLog();
        ops.add("visitantes:hoy", "ip1", "ip2", "ip1");
        Long aprox = ops.size("visitantes:hoy");                 // 2
    }

    // ---------- BITMAP: asistencia diaria ----------
    public void bitmap() {
        str.opsForValue().setBit("asistencia:2026-11-05", 1001, true);       // usuario 1001 asistió
        Boolean asistio = str.opsForValue().getBit("asistencia:2026-11-05", 1001);
    }
}
```

### Ejemplo: guardar un objeto: ¿Hash o String JSON?
| | String (JSON) | Hash |
|---|---|---|
| Lectura | Se lee **todo** el objeto | Puedes leer/modificar **un campo** |
| Estructura anidada | Soporta cualquier estructura | Solo campos planos |
| Memoria | Similar | Eficiente en objetos pequeños |
| TTL | Por objeto | Por objeto (no por campo) |
| Simplicidad | **Alta** | Media |
| Cuándo | Cachear DTOs completos | Objetos con campos que se actualizan por separado (contadores, estado) |

## 9. Repositorios Redis (@RedisHash)

Spring Data permite guardar entidades en Redis como **Hashes** con un repositorio estilo JPA.

| Término | Significado |
|---|---|
| `@RedisHash("nombre")` | Marca una clase como entidad; se guarda como **Hash** con clave `nombre:id`. |
| `timeToLive` | Atributo de `@RedisHash` (segundos de vida). |
| `@TimeToLive` | Campo/método que define el TTL de **esa** instancia. |
| `@Id` | Identificador (`org.springframework.data.annotation.Id`); si es `null` se genera un UUID. |
| `@Indexed` | Crea un **índice secundario** (un Set auxiliar) para buscar por ese campo. |
| `@Reference` | Referencia a otra entidad Redis. |
| `@EnableRedisRepositories` | Activa los repositorios (Boot lo hace solo). |
| `CrudRepository<T, ID>` | Repositorio CRUD básico (`save`, `findById`, `findAll`, `deleteById`, `count`...). |
| **Query methods** | `findByEmail(...)` funciona **solo** con campos `@Indexed` y operadores simples (igualdad). |
| `KeyValueAdapter` | Adaptador interno para Redis. |
| `@RedisHash` + `@Indexed` | Consultas por igualdad. |
| `keyspace` | Nombre del conjunto (prefijo) de claves de la entidad. |
| `@EnableRedisRepositories(enableKeyspaceEvents = ...)` | Limpieza de índices cuando expiran las claves. |
| **Phantom key** | Copia de la clave con TTL extra que Spring usa para limpiar índices al expirar. |
| **Limitaciones** | Sin joins, sin `LIKE`, sin ordenar/paginar con criterios complejos; no es un reemplazo de JPA. |
| `MappingRedisConverter` | Convierte entidad ↔ hash. |
| `RedisCustomConversions` | Conversiones personalizadas. |

### Ejemplo: entidad y repositorio
```java
@RedisHash(value = "sesion", timeToLive = 1800)          // 30 minutos
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class SesionUsuario implements Serializable {

    @Id
    private String id;                                    // clave final: sesion:<id>

    @Indexed
    private String username;                              // permite findByUsername

    @Indexed
    private String ip;

    private LocalDateTime inicio;

    @TimeToLive                                           // TTL específico (sobrescribe el de la clase)
    private Long ttlSegundos;
}

public interface SesionRepository extends CrudRepository<SesionUsuario, String> {
    List<SesionUsuario> findByUsername(String username);  // usa el índice
    List<SesionUsuario> findByIp(String ip);
    long countByUsername(String username);
}
```
```java
@Service
@RequiredArgsConstructor
public class SesionService {
    private final SesionRepository repo;

    public SesionUsuario crear(String username, String ip) {
        SesionUsuario s = new SesionUsuario(null, username, ip, LocalDateTime.now(), 3600L);
        return repo.save(s);                              // id (UUID) generado
    }

    public Optional<SesionUsuario> buscar(String id) { return repo.findById(id); }
    public List<SesionUsuario> deUsuario(String user) { return repo.findByUsername(user); }
    public void cerrar(String id) { repo.deleteById(id); }
}
```
```bash
# Cómo se ve en Redis
KEYS sesion*
# "sesion:3f2a..."                      → Hash con los campos
# "sesion"                              → Set con los ids de todas las sesiones
# "sesion:username:ana"                 → Set índice (ids de las sesiones de ana)
HGETALL sesion:3f2a...
# 1) "_class" 2) "com.empresa.SesionUsuario" 3) "username" 4) "ana" ...
```

### Habilitar limpieza de índices al expirar
```java
@SpringBootApplication
@EnableRedisRepositories(enableKeyspaceEvents = RedisKeyValueAdapter.EnableKeyspaceEvents.ON_STARTUP)
public class App { }
```
```bash
# En el servidor (si no se permite CONFIG, usar notify-keyspace-events en redis.conf)
CONFIG SET notify-keyspace-events Ex
```

### ¿Repositorio, template o caché?
| Necesito... | Uso |
|---|---|
| Guardar entidades simples con TTL y buscar por 1-2 campos | `@RedisHash` + `CrudRepository` |
| Control total (estructuras, TTL, comandos) | `RedisTemplate` |
| Cachear el resultado de un método | `@Cacheable` |
| Consultas complejas, joins, paginación | Base relacional (o Redis Stack con Search) |

## 10. Caché con Spring Cache

La forma **más común** de usar Redis en Spring: cachear el resultado de métodos.

| Término | Significado |
|---|---|
| `@EnableCaching` | Activa la caché (en una clase `@Configuration` o la principal). |
| `@Cacheable` | Si ya hay valor en caché para esa clave, **no ejecuta** el método y lo devuelve; si no, ejecuta y guarda. |
| `@CachePut` | **Siempre ejecuta** el método y actualiza la caché. |
| `@CacheEvict` | Elimina una entrada (o `allEntries = true` todas). |
| `@Caching` | Combina varias anotaciones de caché. |
| `@CacheConfig(cacheNames = "x")` | Valores comunes a nivel de clase. |
| `cacheNames` / `value` | Nombre(s) de la caché (region lógica). |
| `key` | Expresión SpEL de la clave (`#id`, `#p.id`, `#root.methodName`). |
| `keyGenerator` | Generador de claves personalizado. |
| `condition` | Solo cachea si la condición (SpEL) es verdadera (evaluada **antes**). |
| `unless` | No cachea si es verdadera (evaluada **después**, accede a `#result`). |
| `sync = true` | Solo un hilo calcula el valor; los demás esperan (anti *stampede*, en la misma JVM). |
| `beforeInvocation` | En `@CacheEvict`: borra antes de ejecutar el método. |
| `allEntries` | Vacía toda la caché. |
| `CacheManager` | Gestor de cachés. |
| `RedisCacheManager` | `CacheManager` con Redis. |
| `RedisCacheConfiguration` | Config. de una caché: TTL, serializadores, prefijo, nulos. |
| `RedisCacheManagerBuilderCustomizer` | Personaliza cachés concretas desde Boot. |
| `CacheErrorHandler` | Qué hacer si Redis falla (ignorar y seguir con la BD). |
| `CachingConfigurer` | Configuración avanzada (error handler, key generator). |
| `computePrefixWith` | Cómo se arma el prefijo (`nombre::clave`). |
| `entryTtl` | TTL por caché. |
| `disableCachingNullValues()` | No guarda `null`. |
| `serializeValuesWith` | Serializador de valores. |
| **Formato de clave** | `nombreCache::clave` (por defecto). |
| **Self-invocation** | Llamar a un método cacheable **desde la misma clase** no pasa por el proxy: no cachea. |
| **Métodos privados** | `@Cacheable` no funciona en ellos. |

### Ejemplo: configuración con TTL distintos por caché
```java
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public RedisCacheConfiguration cacheDefaults() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .disableCachingNullValues()
                .prefixCacheNameWith("miapp::")
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer()));
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer cachesPersonalizadas(RedisCacheConfiguration defaults) {
        return builder -> builder
                .withCacheConfiguration("productos", defaults.entryTtl(Duration.ofHours(1)))
                .withCacheConfiguration("usuarios",  defaults.entryTtl(Duration.ofMinutes(5)))
                .withCacheConfiguration("config",    defaults.entryTtl(Duration.ofDays(1)));
    }

    // Si Redis cae, la app sigue funcionando consultando la BD
    @Bean
    public CacheErrorHandler cacheErrorHandler() {
        return new CacheErrorHandler() {
            private final Logger log = LoggerFactory.getLogger("cache");
            public void handleCacheGetError(RuntimeException e, Cache c, Object k)   { log.warn("GET falló: {}", e.getMessage()); }
            public void handleCachePutError(RuntimeException e, Cache c, Object k, Object v) { log.warn("PUT falló: {}", e.getMessage()); }
            public void handleCacheEvictError(RuntimeException e, Cache c, Object k) { log.warn("EVICT falló: {}", e.getMessage()); }
            public void handleCacheClearError(RuntimeException e, Cache c)           { log.warn("CLEAR falló: {}", e.getMessage()); }
        };
    }
}
```

### Ejemplo: uso en un Service
```java
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "productos")
public class ProductoService {

    private final ProductoRepository repo;

    // 1ª vez: va a la BD y guarda en Redis (clave "productos::5"). Siguientes: lee de Redis.
    @Cacheable(key = "#id")
    public ProductoDto buscar(Long id) {
        return repo.findById(id).map(mapper::toDto).orElseThrow();
    }

    // Clave compuesta y condiciones
    @Cacheable(key = "#categoria + ':' + #pagina", condition = "#pagina < 5", unless = "#result.isEmpty()")
    public List<ProductoDto> listar(String categoria, int pagina) { ... }

    // sync = true: si 100 hilos piden lo mismo a la vez, solo uno ejecuta el método
    @Cacheable(key = "'top'", sync = true)
    public List<ProductoDto> masVendidos() { ... }

    // Siempre ejecuta y deja la caché actualizada
    @CachePut(key = "#result.id")
    public ProductoDto actualizar(Long id, ProductoRequest req) { ... }

    // Elimina la entrada
    @CacheEvict(key = "#id")
    public void eliminar(Long id) { repo.deleteById(id); }

    // Vacía toda la caché "productos"
    @CacheEvict(allEntries = true)
    public void limpiarTodo() { }

    // Varias operaciones a la vez
    @Caching(
        put   = @CachePut(key = "#result.id"),
        evict = @CacheEvict(cacheNames = "listados", allEntries = true)
    )
    public ProductoDto crear(ProductoRequest req) { ... }
}
```

### Referencia de SpEL para `key`/`condition`/`unless`
| Expresión | Significado |
|---|---|
| `#id` / `#p0` / `#a0` | Parámetro por nombre / posición. |
| `#usuario.id` | Propiedad de un parámetro. |
| `#result` | Valor devuelto (solo en `unless` y `@CachePut`/`@CacheEvict(beforeInvocation=false)`). |
| `#root.methodName` | Nombre del método. |
| `#root.target` / `#root.args` | Objeto y argumentos. |
| `'texto'` | Literal. |
| `#a + ':' + #b` | Concatenación. |
| `T(java.util.Objects).hash(#a, #b)` | Llamada estática. |

### Cómo se ve en Redis
```bash
KEYS *
# "miapp::productos::5"
GET "miapp::productos::5"
# "{\"@class\":\"com.empresa.ProductoDto\",\"id\":5,\"nombre\":\"Teclado\"}"
TTL "miapp::productos::5"      # 3541
```

### Errores comunes de caché
| Problema | Causa | Solución |
|---|---|---|
| No cachea | Sin `@EnableCaching`; llamada interna (self-invocation); método privado | Activar; llamar desde otro bean; método público |
| `SerializationException` / `ClassCastException` | La clase no es `Serializable` (con JDK) o cambió; falta `@class` | JSON serializer y/o DTOs estables |
| Fechas fallan al serializar | Falta `JavaTimeModule` | `ObjectMapper` con el módulo |
| Datos viejos | TTL muy largo; no se invalida al actualizar | `@CacheEvict`/`@CachePut` en las escrituras; TTL razonable |
| La app cae si Redis cae | Sin `CacheErrorHandler` | Implementarlo |
| Cachear entidades JPA | Proxies lazy, ciclos | Cachear **DTOs** |
| Se cachea `null` | `cache-null-values: true` por defecto en Boot | `disableCachingNullValues()` o `unless="#result == null"` |

### ¿Qué cachear?
| ✓ Buen candidato | ✗ Mal candidato |
|---|---|
| Datos que se leen mucho y cambian poco | Datos que cambian en cada petición |
| Resultados costosos (consultas pesadas, APIs externas) | Datos por usuario con baja reutilización |
| Catálogos, configuraciones, parámetros | Información sensible sin control de acceso |
| Respuestas de servicios externos lentos | Resultados enormes (big keys) |

## 11. Sesiones con Spring Session

Guarda las sesiones HTTP en Redis para **compartirlas entre varias instancias** (balanceo de carga) y sobrevivir a reinicios.

| Término | Significado |
|---|---|
| **Spring Session** | Reemplaza la `HttpSession` del contenedor por una almacenada en un repositorio externo. |
| `spring-session-data-redis` | Dependencia para usar Redis. |
| `@EnableRedisHttpSession` | Activa sesiones en Redis (Boot lo hace con la dependencia). |
| `maxInactiveIntervalInSeconds` | Tiempo de inactividad antes de expirar. |
| `redisNamespace` | Prefijo de claves (por defecto `spring:session`). |
| `spring.session.timeout` | Duración de la sesión. |
| `spring.session.redis.namespace` | Namespace. |
| `spring.session.redis.flush-mode` | `on-save` (defecto) o `immediate`. |
| `spring.session.redis.save-mode` | `on-set-attribute`, `on-get-attribute`, `always`. |
| `spring.session.redis.repository-type` | `default` o `indexed`. |
| `SessionRepository` | Interfaz del repositorio de sesiones. |
| `RedisIndexedSessionRepository` | Con índices (permite buscar sesiones por usuario → `FindByIndexNameSessionRepository`). |
| `FindByIndexNameSessionRepository` | Buscar todas las sesiones de un usuario (cerrar sesiones remotas). |
| `SessionRegistry` | Registro de sesiones (límite de sesiones concurrentes). |
| `CookieSerializer` | Personaliza la cookie (`SESSION`, `SameSite`, dominio). |
| `HeaderHttpSessionIdResolver` | Usa una cabecera (`X-Auth-Token`) en vez de cookie (APIs). |
| `SESSION` | Nombre de la cookie de Spring Session (en vez de `JSESSIONID`). |
| **Claves en Redis** | `spring:session:sessions:<id>` (Hash), `spring:session:sessions:expires:<id>`, `spring:session:expirations:<tiempo>`. |
| `@EnableSpringHttpSession` | Para usar otro repositorio. |

### Ejemplo
```yaml
spring:
  session:
    timeout: 30m
    redis:
      namespace: miapp:session
      flush-mode: on-save
      save-mode: on-set-attribute
```
```java
@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 1800, redisNamespace = "miapp:session")
public class SessionConfig {

    // Cookie personalizada
    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer s = new DefaultCookieSerializer();
        s.setCookieName("MISESION");
        s.setUseHttpOnlyCookie(true);
        s.setUseSecureCookie(true);
        s.setSameSite("Strict");
        return s;
    }

    // Para APIs: sesión por cabecera en vez de cookie
    // @Bean public HttpSessionIdResolver resolver() { return HeaderHttpSessionIdResolver.xAuthToken(); }
}
```
```java
// El código no cambia: sigues usando HttpSession
@GetMapping("/carrito/agregar")
public String agregar(HttpSession session) {
    List<String> carrito = (List<String>) session.getAttribute("carrito");
    if (carrito == null) carrito = new ArrayList<>();
    carrito.add("producto");
    session.setAttribute("carrito", carrito);       // se guarda en Redis (el objeto debe ser serializable)
    return "ok";
}
```
```java
// Cerrar todas las sesiones de un usuario (requiere repositorio indexado)
@Autowired FindByIndexNameSessionRepository<? extends Session> sessions;

public void cerrarSesionesDe(String username) {
    sessions.findByPrincipalName(username).keySet().forEach(sessions::deleteById);
}
```
```bash
KEYS spring:session:*
HGETALL spring:session:sessions:7f9c-...
```
> Con Spring Security, el `SecurityContext` se guarda en la sesión de Redis: el usuario queda autenticado en **cualquier** instancia de la aplicación.

### Sesión en Redis vs JWT
| | Sesión en Redis | JWT |
|---|---|---|
| Estado en servidor | Sí (Redis) | No |
| Revocar al instante | **Sí** (borrar la clave) | Difícil |
| Escalado horizontal | Sí (Redis compartido) | Sí |
| Dependencia | Redis | Ninguna |

## 12. Pub/Sub y Streams

### Pub/Sub
Mensajería **fire-and-forget**: los suscriptores conectados reciben el mensaje; si no están, **lo pierden** (no hay persistencia).

| Término | Significado |
|---|---|
| **Canal (channel)** | Nombre del "tema" donde se publica. |
| **Publisher** | Quien publica (`convertAndSend`). |
| **Subscriber** | Quien escucha. |
| `ChannelTopic` | Canal exacto. |
| `PatternTopic` | Patrón (`eventos:*`). |
| `RedisMessageListenerContainer` | Contenedor que gestiona las suscripciones. |
| `MessageListener` | Interfaz con `onMessage(Message, byte[] pattern)`. |
| `MessageListenerAdapter` | Adapta un POJO como listener. |
| `RedisPubSubCommands` | Comandos de bajo nivel. |
| **At-most-once** | Entrega como máximo una vez (puede perderse). |

```java
// Configuración
@Configuration
public class PubSubConfig {

    @Bean
    public RedisMessageListenerContainer listenerContainer(RedisConnectionFactory factory,
                                                           NotificacionListener listener) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(factory);
        container.addMessageListener(listener, new ChannelTopic("notificaciones"));
        container.addMessageListener(listener, new PatternTopic("eventos:*"));
        return container;
    }
}

// Suscriptor
@Component
public class NotificacionListener implements MessageListener {
    @Override
    public void onMessage(Message message, byte[] pattern) {
        String canal = new String(message.getChannel());
        String cuerpo = new String(message.getBody());
        System.out.println("Recibido en " + canal + ": " + cuerpo);
    }
}

// Publicador
@Service
@RequiredArgsConstructor
public class Notificador {
    private final StringRedisTemplate redis;

    public void publicar(String mensaje) {
        redis.convertAndSend("notificaciones", mensaje);
    }
}
```
```bash
SUBSCRIBE notificaciones
PUBLISH notificaciones "hola a todos"       # → devuelve cuántos suscriptores lo recibieron
PSUBSCRIBE eventos:*
```

### Streams
Log de eventos **persistente** con confirmación (`ACK`), reintentos y **grupos de consumidores** (cada mensaje lo procesa uno del grupo). Más fiable que Pub/Sub.

| Término | Significado |
|---|---|
| **Stream** | Secuencia inmutable de entradas con ID (`<ms>-<seq>`). |
| **Entry / Record** | Un mensaje: ID + pares campo-valor. |
| `XADD stream * campo valor` | Agrega un mensaje (`*` = ID automático). |
| `XREAD` | Lee mensajes. |
| `XRANGE` / `XREVRANGE` | Rango por IDs. |
| **Consumer group** | Grupo de consumidores que se reparten los mensajes. |
| `XGROUP CREATE` | Crea un grupo. |
| `XREADGROUP GROUP g consumidor` | Lee como miembro del grupo. |
| `XACK` | Confirma que un mensaje fue procesado. |
| `XPENDING` | Mensajes entregados pero sin confirmar. |
| `XCLAIM` / `XAUTOCLAIM` | Reasigna mensajes pendientes de un consumidor caído. |
| `XTRIM` / `MAXLEN` | Limita el tamaño del stream. |
| `$` / `>` | Solo nuevos / solo no entregados al grupo. |
| `StreamMessageListenerContainer` | Contenedor de listeners de streams en Spring. |
| `StreamListener<K, MapRecord>` | Listener de mensajes. |
| `MapRecord` / `ObjectRecord` | Registro como mapa / como objeto. |
| `Consumer.from(grupo, nombre)` | Identidad del consumidor. |
| `StreamOffset.create(stream, ReadOffset.lastConsumed())` | Desde dónde leer. |
| `autoAcknowledge` | Confirmar automáticamente. |
| **At-least-once** | Garantía de entrega al menos una vez (puede haber duplicados → procesar de forma **idempotente**). |

```java
// Publicar
@Service
@RequiredArgsConstructor
public class PedidoEventos {
    private final StringRedisTemplate redis;

    public RecordId publicar(Long pedidoId, String estado) {
        MapRecord<String, String, String> record = StreamRecords.newRecord()
                .in("stream:pedidos")
                .ofMap(Map.of("pedidoId", pedidoId.toString(), "estado", estado));
        return redis.opsForStream().add(record);
    }
}

// Crear grupo (una vez)
redis.opsForStream().createGroup("stream:pedidos", ReadOffset.from("0"), "grupo-facturacion");

// Consumidor
@Configuration
public class StreamConfig {

    @Bean
    public StreamMessageListenerContainer<String, MapRecord<String, String, String>> container(
            RedisConnectionFactory factory, PedidoStreamListener listener) {

        var options = StreamMessageListenerContainer.StreamMessageListenerContainerOptions.builder()
                .pollTimeout(Duration.ofSeconds(1))
                .build();

        var container = StreamMessageListenerContainer.create(factory, options);
        container.receive(
                Consumer.from("grupo-facturacion", "worker-1"),
                StreamOffset.create("stream:pedidos", ReadOffset.lastConsumed()),
                listener);
        container.start();
        return container;
    }
}

@Component
@RequiredArgsConstructor
public class PedidoStreamListener implements StreamListener<String, MapRecord<String, String, String>> {
    private final StringRedisTemplate redis;

    @Override
    public void onMessage(MapRecord<String, String, String> message) {
        Map<String, String> datos = message.getValue();
        // ... procesar de forma idempotente ...
        redis.opsForStream().acknowledge("grupo-facturacion", message);      // XACK
    }
}
```
```bash
XADD stream:pedidos * pedidoId 100 estado CREADO
XGROUP CREATE stream:pedidos grupo-facturacion 0
XREADGROUP GROUP grupo-facturacion worker-1 COUNT 10 STREAMS stream:pedidos >
XACK stream:pedidos grupo-facturacion 1730851200000-0
XPENDING stream:pedidos grupo-facturacion
XLEN stream:pedidos
```

### Pub/Sub vs List vs Stream
| | Pub/Sub | List (cola) | Stream |
|---|---|---|---|
| Persistencia | **No** | Sí | Sí |
| Varios consumidores reciben el mismo mensaje | **Sí** (broadcast) | No | Sí (grupos distintos) |
| Reparto de trabajo entre workers | No | Sí | **Sí** (consumer group) |
| Confirmación (ACK) / reintentos | No | Manual | **Sí** |
| Historial / releer | No | No | **Sí** |
| Cuándo | Notificaciones en vivo, invalidar cachés | Colas simples | Eventos fiables, procesamiento distribuido |

> Para mensajería seria considera Kafka/RabbitMQ; los Streams de Redis son una opción ligera cuando ya usas Redis.

## 13. Transacciones, pipelines y scripts Lua

| Término | Significado |
|---|---|
| `MULTI` / `EXEC` | Agrupa comandos; se ejecutan **en secuencia y sin interrupciones**. |
| `DISCARD` | Cancela la transacción. |
| `WATCH clave` | Bloqueo **optimista**: si la clave cambia antes de `EXEC`, la transacción falla. |
| **Sin rollback** | Redis **no revierte** comandos si uno falla en ejecución (solo si hay error de sintaxis al encolar). |
| `SessionCallback` | Ejecutar `multi/exec` en la misma conexión desde Spring. |
| `setEnableTransactionSupport(true)` | Integra con `@Transactional` (las lecturas dentro devuelven `null`/colas). |
| **Pipeline** | Enviar muchos comandos **sin esperar** respuesta de cada uno (reduce la latencia de red). |
| `executePipelined` | Pipeline en Spring. |
| `RTT` | Round-Trip Time: latencia de ida y vuelta (lo que reduce el pipeline). |
| **Lua script** | Script que Redis ejecuta de forma **atómica** en el servidor. |
| `EVAL` / `EVALSHA` | Ejecutar un script / por su hash. |
| `RedisScript<T>` | Script tipado en Spring. |
| `DefaultRedisScript` | Implementación. |
| `KEYS[]` / `ARGV[]` | Arreglos de claves y argumentos dentro de Lua. |
| `redis.call()` | Llama un comando desde Lua. |
| **Functions** | Redis 7: reemplazo moderno de los scripts (`FUNCTION LOAD`). |
| **Atomicidad por script** | Mejor que `WATCH/MULTI` para "leer-modificar-escribir". |

### Ejemplo: pipeline (muchos comandos, 1 viaje)
```java
List<Object> resultados = redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
    StringRedisConnection conn = (StringRedisConnection) connection;
    for (int i = 0; i < 1000; i++) {
        conn.set("clave:" + i, "valor" + i);
        conn.expire("clave:" + i, 3600);
    }
    return null;                      // en pipeline, el return se ignora
});
```

### Ejemplo: transacción con `WATCH`
```java
List<Object> resultado = redisTemplate.execute(new SessionCallback<>() {
    @Override
    public List<Object> execute(RedisOperations ops) {
        ops.watch("saldo:ana");                                   // vigilar
        Integer saldo = (Integer) ops.opsForValue().get("saldo:ana");
        if (saldo == null || saldo < 100) { ops.unwatch(); return null; }

        ops.multi();                                              // inicia
        ops.opsForValue().decrement("saldo:ana", 100);
        ops.opsForValue().increment("saldo:luis", 100);
        return ops.exec();                                        // null si "saldo:ana" cambió
    }
});
```

### Ejemplo: script Lua (liberar un lock de forma segura)
```java
// Solo borra el lock si todavía es MÍO (compara el valor) → atómico
private static final RedisScript<Long> UNLOCK = new DefaultRedisScript<>("""
        if redis.call('get', KEYS[1]) == ARGV[1] then
            return redis.call('del', KEYS[1])
        else
            return 0
        end
        """, Long.class);

public boolean liberar(String clave, String token) {
    Long r = stringRedisTemplate.execute(UNLOCK, List.of(clave), token);
    return r != null && r > 0;
}
```

### Ejemplo: script Lua para rate limiting (incrementa y fija TTL de forma atómica)
```java
private static final RedisScript<Long> RATE = new DefaultRedisScript<>("""
        local actual = redis.call('INCR', KEYS[1])
        if actual == 1 then
            redis.call('EXPIRE', KEYS[1], ARGV[1])
        end
        return actual
        """, Long.class);

public boolean permitido(String ip, int limite, int ventanaSeg) {
    Long n = stringRedisTemplate.execute(RATE, List.of("rate:" + ip), String.valueOf(ventanaSeg));
    return n != null && n <= limite;
}
```
> Los scripts Lua guardados en `src/main/resources/scripts/*.lua` se cargan con `new DefaultRedisScript<>(); setLocation(new ClassPathResource("scripts/unlock.lua"))`.

## 14. Patrones comunes

### 1. Cache-aside manual
```java
public ProductoDto buscar(Long id) {
    String key = "producto:" + id;
    ProductoDto cacheado = (ProductoDto) redis.opsForValue().get(key);
    if (cacheado != null) return cacheado;                        // HIT

    ProductoDto dto = repo.findById(id).map(mapper::toDto).orElseThrow();   // MISS → BD
    redis.opsForValue().set(key, dto, Duration.ofMinutes(10));
    return dto;
}
public void actualizar(Long id, ...) { repo.save(...); redis.delete("producto:" + id); }   // invalidar
```

### 2. Lock distribuido (simple, con `SET NX PX`)
```java
@Service
@RequiredArgsConstructor
public class LockService {
    private final StringRedisTemplate redis;

    public String adquirir(String recurso, Duration ttl) {
        String token = UUID.randomUUID().toString();
        Boolean ok = redis.opsForValue().setIfAbsent("lock:" + recurso, token, ttl);
        return Boolean.TRUE.equals(ok) ? token : null;
    }

    public void liberar(String recurso, String token) {
        // usar el script Lua de la sección 13 para borrar solo si el token coincide
    }
}

// Uso
String token = lockService.adquirir("pedido:" + id, Duration.ofSeconds(30));
if (token == null) throw new ConflictoException("Ya se está procesando");
try { procesar(id); } finally { lockService.liberar("pedido:" + id, token); }
```
> Para producción considera **Redisson** (`RLock`) o **Spring Integration `RedisLockRegistry`**: gestionan renovación (watchdog), reentrada y espera.

### 3. Rate limiting (ventana fija con INCR)
```java
public boolean permitir(String clienteId) {
    String key = "rate:" + clienteId + ":" + (System.currentTimeMillis() / 60_000);   // ventana de 1 min
    Long n = redis.opsForValue().increment(key);
    if (n != null && n == 1) redis.expire(key, Duration.ofMinutes(1));
    return n != null && n <= 100;                                  // máx. 100 por minuto
}

// Como filtro
@Component
public class RateLimitFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        if (!limiter.permitir(req.getRemoteAddr())) {
            res.setStatus(429);                                    // Too Many Requests
            res.setHeader("Retry-After", "60");
            return;
        }
        chain.doFilter(req, res);
    }
}
```

### 4. Ranking / leaderboard (Sorted Set)
```java
public void sumarPuntos(String jugador, double puntos) {
    redis.opsForZSet().incrementScore("ranking:global", jugador, puntos);
}
public List<String> top10() {
    return redis.opsForZSet().reverseRange("ranking:global", 0, 9)
            .stream().map(Object::toString).toList();
}
public Long miPosicion(String jugador) {
    Long r = redis.opsForZSet().reverseRank("ranking:global", jugador);
    return r == null ? null : r + 1;
}
```

### 5. Blacklist de JWT / logout
```java
public void invalidar(String jti, Instant expira) {
    Duration ttl = Duration.between(Instant.now(), expira);
    if (!ttl.isNegative()) stringTemplate.opsForValue().set("jwt:blacklist:" + jti, "1", ttl);   // vive lo que le queda al token
}
public boolean estaInvalidado(String jti) {
    return Boolean.TRUE.equals(stringTemplate.hasKey("jwt:blacklist:" + jti));
}
```

### 6. Códigos temporales (OTP, recuperar contraseña)
```java
public String generarOtp(String email) {
    String codigo = String.format("%06d", ThreadLocalRandom.current().nextInt(1_000_000));
    stringTemplate.opsForValue().set("otp:" + email, codigo, Duration.ofMinutes(5));
    return codigo;
}
public boolean validarOtp(String email, String ingresado) {
    String guardado = stringTemplate.opsForValue().get("otp:" + email);
    if (guardado != null && guardado.equals(ingresado)) {
        stringTemplate.delete("otp:" + email);                    // un solo uso
        return true;
    }
    return false;
}
```

### 7. Idempotencia de peticiones
```java
public boolean primeraVez(String idempotencyKey) {
    return Boolean.TRUE.equals(
        stringTemplate.opsForValue().setIfAbsent("idem:" + idempotencyKey, "1", Duration.ofHours(24)));
}
// Si devuelve false → la petición ya fue procesada: no repetir (pagos, creación de pedidos)
```

### 8. Cola de trabajo con List
```java
// Productor
redis.opsForList().rightPush("cola:emails", json);

// Consumidor (hilo que bloquea esperando)
@Scheduled(fixedDelay = 100)
public void consumir() {
    Object tarea = redis.opsForList().leftPop("cola:emails", Duration.ofSeconds(2));   // BLPOP
    if (tarea != null) procesar(tarea);
}
```

### 9. Contador de visitas únicas (HyperLogLog)
```java
stringTemplate.opsForHyperLogLog().add("visitas:" + LocalDate.now(), usuarioId);
Long unicas = stringTemplate.opsForHyperLogLog().size("visitas:" + LocalDate.now());
```

### 10. Mitigar problemas de caché
| Problema | Solución |
|---|---|
| **Stampede** (muchos recalculan a la vez) | `@Cacheable(sync = true)`; lock por clave; refrescar antes de expirar; TTL con *jitter*. |
| **Penetration** (claves inexistentes) | Cachear `null` con TTL corto; **Bloom filter**; validar el input. |
| **Avalanche** (todo expira junto) | TTL con jitter; escalonar; réplicas/Cluster. |
| **Hot key** | Copia local (Caffeine) delante de Redis (caché de 2 niveles); replicar la clave con sufijos. |
| **Big key** | Dividir en varias claves/hashes; comprimir; paginar. |
| **Datos obsoletos** | Invalidar al escribir (`@CacheEvict`), TTL corto, eventos (Pub/Sub) para invalidar. |

### Ejemplo: caché de 2 niveles (local + Redis)
```java
// Nivel 1: Caffeine (en memoria de la JVM, microsegundos). Nivel 2: Redis (compartido).
@Cacheable(cacheNames = "catalogo", cacheManager = "caffeineManager")   // L1
public Catalogo local() { return redisLookup(); }

@Cacheable(cacheNames = "catalogo", cacheManager = "redisCacheManager") // L2
public Catalogo redisLookup() { return repo.cargar(); }
```

## 15. Alta disponibilidad: Sentinel, Cluster y réplicas

| Modo | Descripción |
|---|---|
| **Standalone** | Un solo nodo. Simple; punto único de fallo. |
| **Master–Réplica** | Un master (escritura) y N réplicas (copia/lectura). Sin failover automático. |
| **Sentinel** | Procesos que monitorean el master y **promueven una réplica** automáticamente si cae. El cliente pregunta a Sentinel quién es el master. |
| **Cluster** | **Sharding** automático en 16384 slots + réplicas por shard. Escala memoria y escritura. |
| `quorum` | Cantidad de Sentinels que deben coincidir en que el master cayó. |
| `ReadFrom` | Estrategia de lectura de Lettuce: `MASTER`, `REPLICA`, `REPLICA_PREFERRED`, `ANY`. |
| **Hash tag** | `{usuario:1}:perfil` y `{usuario:1}:pedidos`: lo que va entre llaves decide el slot → ambas claves en el mismo nodo. |
| `MOVED` / `ASK` | Redirecciones del cluster (el cliente las gestiona). |
| **Multi-key en Cluster** | Operaciones con varias claves solo si están en el **mismo slot** (usar hash tags). |
| `CROSSSLOT` | Error por usar claves de distintos slots en un comando. |
| `max-redirects` | Máximo de redirecciones. |
| **Redis Enterprise / ElastiCache / Memorystore / Azure Cache** | Servicios gestionados en la nube. |
| **Replicación asíncrona** | Una réplica puede ir unos milisegundos atrás: lecturas pueden ser ligeramente obsoletas. |

### Ejemplo: Sentinel
```yaml
spring:
  data:
    redis:
      sentinel:
        master: mymaster
        nodes: sentinel1:26379,sentinel2:26379,sentinel3:26379
        password: ${SENTINEL_PASSWORD:}
      password: ${REDIS_PASSWORD}
```

### Ejemplo: Cluster
```yaml
spring:
  data:
    redis:
      cluster:
        nodes: node1:6379,node2:6379,node3:6379,node4:6379,node5:6379,node6:6379
        max-redirects: 3
      password: ${REDIS_PASSWORD}
```

### Ejemplo: leer desde réplicas
```java
@Bean
public LettuceConnectionFactory factory() {
    LettuceClientConfiguration cfg = LettuceClientConfiguration.builder()
            .readFrom(ReadFrom.REPLICA_PREFERRED)         // lecturas en réplicas si hay
            .build();
    RedisStaticMasterReplicaConfiguration nodes =
            new RedisStaticMasterReplicaConfiguration("master", 6379);
    nodes.addNode("replica1", 6379);
    return new LettuceConnectionFactory(nodes, cfg);
}
```

### Ejemplo: hash tags en Cluster
```java
// Mismas llaves {…} → mismo slot → se pueden usar en una transacción/script/multi-key
redis.opsForValue().set("{usuario:1}:perfil", perfil);
redis.opsForValue().set("{usuario:1}:config", config);
redis.opsForValue().multiGet(List.of("{usuario:1}:perfil", "{usuario:1}:config"));   // OK
```

### Elegir modo
| Necesito... | Modo |
|---|---|
| Desarrollo / caché sencilla | Standalone |
| Alta disponibilidad, datos caben en 1 nodo | **Sentinel** |
| Más memoria/escritura de la que cabe en 1 nodo | **Cluster** |
| Lo más sencillo en producción | Servicio gestionado (ElastiCache, etc.) |

## 16. Seguridad y producción

| Tema | Detalle |
|---|---|
| **No exponer Redis a Internet** | Redis sin protección es una puerta abierta (se usa para ataques). Mantenerlo en red privada / VPC. |
| `requirepass` | Contraseña global (`AUTH password`). |
| **ACL (Redis 6+)** | Usuarios con permisos por comando/clave (`ACL SETUSER app on >clave ~app:* +@read +@write`). |
| `bind 127.0.0.1` | Restringe interfaces de escucha. |
| `protected-mode yes` | Bloquea acceso externo si no hay contraseña. |
| **TLS** | `tls-port`, certificados; en Spring `spring.data.redis.ssl.enabled=true`. |
| **Renombrar/deshabilitar comandos peligrosos** | `FLUSHALL`, `FLUSHDB`, `CONFIG`, `KEYS`, `DEBUG`, `SHUTDOWN` (`rename-command ... ""` o ACL). |
| **No guardar datos sensibles sin cifrar** | Contraseñas, tarjetas, tokens de larga vida. |
| **Prefijos por aplicación/entorno** | Evita colisiones y borrados accidentales. |
| **Memoria** | Definir `maxmemory` y política de desalojo. |
| **Persistencia** | Decidir si es solo caché o datos importantes (RDB/AOF). |
| **Backups** | Copiar RDB/AOF; probar la restauración. |
| **Monitoreo** | Memoria, hit ratio, latencia, clientes, evicciones. |
| **Timeouts y pool** | Evitar que una caída de Redis bloquee la app (timeouts cortos, circuit breaker, `CacheErrorHandler`). |
| **Fallback** | Si Redis falla, la app debe poder seguir (degradada) usando la BD. |
| **No usar `KEYS`/`FLUSHALL` en producción** | Usar `SCAN` y borrado por prefijo controlado. |
| **Evitar big keys y hot keys** | Fragmentar. |
| **Versiones** | Mantener Redis actualizado (parches de seguridad). |

### Ejemplo: ACL
```bash
ACL SETUSER miapp on >ClaveFuerte123 ~miapp:* +@read +@write +@string +@hash +@list +@set +@sortedset +expire +ttl -@dangerous
ACL LIST
```
```yaml
spring:
  data:
    redis:
      username: miapp
      password: ClaveFuerte123
      ssl:
        enabled: true
```

### Ejemplo: borrar claves por prefijo con `SCAN` (no `KEYS`)
```java
public void borrarPorPrefijo(String prefijo) {
    ScanOptions opts = ScanOptions.scanOptions().match(prefijo + "*").count(500).build();
    try (Cursor<String> cursor = stringTemplate.scan(opts)) {
        List<String> lote = new ArrayList<>();
        while (cursor.hasNext()) {
            lote.add(cursor.next());
            if (lote.size() >= 500) { stringTemplate.unlink(lote); lote.clear(); }
        }
        if (!lote.isEmpty()) stringTemplate.unlink(lote);       // UNLINK no bloquea
    }
}
```

### Ejemplo: circuit breaker para no depender de Redis
```java
@CircuitBreaker(name = "redis", fallbackMethod = "desdeBd")
public ProductoDto buscar(Long id) { /* usar Redis */ }

public ProductoDto desdeBd(Long id, Throwable t) { return repo.findById(id)...; }
```

## 17. Testing con Redis

| Opción | Descripción |
|---|---|
| **Testcontainers** | **Recomendado.** Redis real en Docker (`GenericContainer("redis:7")`). Mismo comportamiento que producción. |
| `@ServiceConnection` | (Boot 3.1+) Conecta automáticamente al contenedor (`name = "redis"`). |
| `@DataRedisTest` | Slice para repositorios y `RedisTemplate`. |
| **Embedded Redis** | Librerías que arrancan un Redis embebido (menos fiables/mantenidas). |
| **Mock de `RedisTemplate`** | Para tests unitarios del service (Mockito): `when(redis.opsForValue()).thenReturn(valueOps)`. |
| `@MockBean StringRedisTemplate` | Mock en el contexto. |
| **`FLUSHDB` en `@BeforeEach`** | Limpiar entre tests. |
| `@DirtiesContext` | Evitar si es posible. |
| `Awaitility` | Esperar expiraciones o mensajes asíncronos. |
| `spring.cache.type=none` | Desactivar la caché en tests donde estorba. |
| `@AutoConfigureCache` | Configura una caché en memoria para slices. |

### Ejemplo: test con Testcontainers
```java
@SpringBootTest
@Testcontainers
class ProductoCacheIT {

    @Container
    @ServiceConnection(name = "redis")
    static GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine").withExposedPorts(6379);

    @Autowired ProductoService service;
    @Autowired CacheManager cacheManager;
    @Autowired StringRedisTemplate redisTemplate;
    @MockBean ProductoRepository repo;

    @BeforeEach
    void limpiar() {
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushDb();
    }

    @Test
    void segundaLlamadaUsaCache() {
        when(repo.findById(1L)).thenReturn(Optional.of(new Producto(1L, "Teclado")));

        service.buscar(1L);      // MISS → BD
        service.buscar(1L);      // HIT  → Redis

        verify(repo, times(1)).findById(1L);                       // BD consultada una sola vez
        assertThat(cacheManager.getCache("productos").get(1L)).isNotNull();
    }

    @Test
    void evictBorraLaEntrada() {
        when(repo.findById(1L)).thenReturn(Optional.of(new Producto(1L, "Teclado")));
        service.buscar(1L);

        service.eliminar(1L);

        assertThat(cacheManager.getCache("productos").get(1L)).isNull();
    }
}
```

### Ejemplo: `@DataRedisTest` (repositorios `@RedisHash`)
```java
@DataRedisTest
@Testcontainers
class SesionRepositoryTest {

    @Container @ServiceConnection(name = "redis")
    static GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine").withExposedPorts(6379);

    @Autowired SesionRepository repo;

    @Test
    void guardaYBuscaPorIndice() {
        repo.save(new SesionUsuario(null, "ana", "10.0.0.1", LocalDateTime.now(), 60L));
        assertThat(repo.findByUsername("ana")).hasSize(1);
        assertThat(repo.findByUsername("otro")).isEmpty();
    }
}
```

### Ejemplo: test unitario con mock de `StringRedisTemplate`
```java
@ExtendWith(MockitoExtension.class)
class OtpServiceTest {

    @Mock StringRedisTemplate redis;
    @Mock ValueOperations<String, String> ops;
    @InjectMocks OtpService service;

    @Test
    void validarOtp_correcto_borraElCodigo() {
        when(redis.opsForValue()).thenReturn(ops);
        when(ops.get("otp:ana@mail.com")).thenReturn("123456");

        assertThat(service.validarOtp("ana@mail.com", "123456")).isTrue();
        verify(redis).delete("otp:ana@mail.com");
    }
}
```

### Ejemplo: probar expiración
```java
@Test
void claveExpira() {
    redisTemplate.opsForValue().set("temp", "x", Duration.ofSeconds(1));
    await().atMost(3, TimeUnit.SECONDS)
           .untilAsserted(() -> assertThat(redisTemplate.hasKey("temp")).isFalse());
}
```

## 18. Monitoreo y herramientas

| Herramienta / Comando | Para qué sirve |
|---|---|
| `redis-cli` | Cliente de consola. |
| `redis-cli --scan --pattern 'usuario:*'` | Listar claves de forma segura. |
| `redis-cli --bigkeys` | Encontrar claves enormes. |
| `redis-cli --latency` | Medir latencia. |
| `redis-cli monitor` | Ver comandos en vivo. |
| `INFO` (`memory`, `stats`, `clients`, `replication`, `keyspace`) | Métricas del servidor. |
| `SLOWLOG GET` | Comandos lentos. |
| `CLIENT LIST` | Clientes conectados. |
| `MEMORY STATS` / `MEMORY USAGE` | Uso de memoria. |
| `LATENCY DOCTOR` | Diagnóstico de latencia. |
| **RedisInsight** | Interfaz gráfica oficial (explorar claves, perfilar, ver memoria). |
| **Spring Boot Actuator** | `/actuator/health` incluye el estado de Redis; `/actuator/metrics` y `/actuator/caches`. |
| **Micrometer / Prometheus** | Métricas de la caché (`cache.gets`, `cache.puts`, hit/miss). |
| `redis_exporter` | Exporta métricas de Redis a Prometheus/Grafana. |
| `keyspace_hits` / `keyspace_misses` | Hit ratio global (`INFO stats`). |
| `evicted_keys` | Claves desalojadas por falta de memoria. |
| `used_memory` / `used_memory_peak` / `mem_fragmentation_ratio` | Memoria. |
| `connected_clients` / `blocked_clients` | Clientes. |
| `instantaneous_ops_per_sec` | Operaciones por segundo. |
| `rejected_connections` | Conexiones rechazadas (`maxclients`). |
| `latest_fork_usec` | Costo del fork de persistencia. |

### Ejemplo: Actuator
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health, metrics, caches, prometheus
  endpoint:
    health:
      show-details: always
```
```bash
curl localhost:8080/actuator/health
# {"status":"UP","components":{"redis":{"status":"UP","details":{"version":"7.2.4"}}}}

curl localhost:8080/actuator/caches
curl localhost:8080/actuator/metrics/cache.gets?tag=result:hit
curl -X DELETE localhost:8080/actuator/caches/productos          # vaciar una caché
```

### Ejemplo: hit ratio desde `redis-cli`
```bash
INFO stats | grep keyspace
# keyspace_hits:15230
# keyspace_misses:1820
# hit ratio = hits / (hits + misses) = 89 %
```
```bash
INFO memory | grep -E "used_memory_human|maxmemory_human|mem_fragmentation_ratio"
INFO keyspace                       # db0:keys=1520,expires=1480,avg_ttl=543210
SLOWLOG GET 10
```

## 19. Excepciones y errores frecuentes

| Excepción / Error | Causa | Solución |
|---|---|---|
| `RedisConnectionFailureException` / `Unable to connect to Redis` | Redis apagado, host/puerto/red incorrectos, firewall. | Verificar `docker ps`, host, puerto, red; `redis-cli ping`. |
| `RedisCommandTimeoutException` | Comando lento o red lenta; Redis saturado. | Subir `timeout`, revisar `SLOWLOG`, evitar `KEYS`/big keys. |
| `NOAUTH Authentication required` | Falta contraseña. | `spring.data.redis.password`. |
| `WRONGPASS invalid username-password pair` | Credenciales incorrectas. | Revisar usuario/clave (ACL). |
| `NOPERM this user has no permissions to run the command` | ACL restrictiva. | Ajustar permisos del usuario. |
| `ERR max number of clients reached` | Demasiadas conexiones. | Pool correcto, `maxclients`, cerrar conexiones. |
| `OOM command not allowed when used memory > 'maxmemory'` | Memoria llena con política `noeviction`. | Aumentar `maxmemory`, `allkeys-lru`, poner TTL. |
| `MISCONF Redis is configured to save RDB snapshots, but it is currently not able to persist` | Fallo al escribir snapshot (disco/permisos/espacio). | Revisar disco/permisos; `stop-writes-on-bgsave-error`. |
| `READONLY You can't write against a read only replica` | Escribiendo en una réplica (tras failover o mal configurado). | Apuntar al master/Sentinel. |
| `LOADING Redis is loading the dataset in memory` | Redis arrancando. | Esperar/reintentar. |
| `CROSSSLOT Keys in request don't hash to the same slot` | Multi-key en Cluster con claves de distintos slots. | Hash tags `{...}`. |
| `MOVED` / `CLUSTERDOWN` | Cluster reconfigurándose o caído. | Revisar salud del cluster. |
| `WRONGTYPE Operation against a key holding the wrong kind of value` | Comando de un tipo sobre una clave de otro (ej. `GET` en un Hash). | Revisar el tipo (`TYPE clave`) y prefijos. |
| `ERR value is not an integer or out of range` | `INCR` sobre un valor no numérico (o serializado con JDK/JSON). | Usar `StringRedisTemplate` para contadores. |
| `SerializationException: Could not read JSON` / `Cannot deserialize` | La clase cambió o serializadores inconsistentes. | Alinear serializadores; borrar claves viejas; versionar claves (`v2:`). |
| `ClassCastException` al leer | `GenericJackson2JsonRedisSerializer` devolvió `LinkedHashMap` (sin `@class`). | Activar default typing o usar `Jackson2JsonRedisSerializer<T>`. |
| `java.io.NotSerializableException` | Serializador JDK y la clase no implementa `Serializable`. | Cambiar a serializador JSON. |
| Claves ilegibles (`\xac\xed\x00\x05t\x00...`) | Serializador JDK por defecto en `RedisTemplate`. | Configurar `StringRedisSerializer` para claves. |
| `@Cacheable` no cachea | Sin `@EnableCaching`; self-invocation; método privado/no público. | Revisar sección 10. |
| `InvalidDataAccessApiUsageException` | Uso incorrecto (`null` como clave, comandos en transacción). | Revisar argumentos. |
| `LettucePool`/`Could not get a resource from the pool` | Pool agotado. | Aumentar `max-active`, liberar conexiones, revisar bloqueos (`BLPOP`). |
| `NoSuchBeanDefinitionException: RedisTemplate<String,Object>` | Solo existe `RedisTemplate<Object,Object>` por defecto. | Declarar el bean con los genéricos que usas. |
| `Consumer group ... already exists` (`BUSYGROUP`) | Crear dos veces el mismo grupo. | Capturar la excepción o comprobar si existe. |
| `@RedisHash` con `findByCampo` no devuelve nada | El campo no tiene `@Indexed`. | Añadir `@Indexed`. |
| Datos que "no se borran" del índice al expirar | Keyspace events desactivados. | `enableKeyspaceEvents` + `notify-keyspace-events Ex`. |
| Sesiones no compartidas entre instancias | Cookie `JSESSIONID` en vez de `SESSION`; Spring Session no activo. | Verificar dependencia y config; cookies/dominio. |
| App lenta con Redis | `KEYS *`, big keys, una conexión por operación, N+1 de comandos. | `SCAN`, pipeline, `MGET`, reutilizar conexiones. |
| Datos desaparecen tras reiniciar | Sin persistencia (o `docker run` sin volumen). | RDB/AOF y volumen persistente. |

### Checklist de depuración
1. `redis-cli -h host -p puerto -a clave ping` → ¿responde `PONG`?
2. `redis-cli KEYS 'prefijo*'` (en desarrollo) / `SCAN` → ¿están las claves esperadas y con qué formato?
3. `TYPE clave` y `TTL clave` → ¿tipo y expiración correctos?
4. `GET clave` → ¿el valor es legible? (si es ilegible, revisa los serializadores).
5. `INFO memory`, `INFO stats` → ¿memoria llena, evicciones, hit ratio bajo?
6. `SLOWLOG GET` → ¿hay comandos lentos?
7. Logs: `logging.level.org.springframework.data.redis=DEBUG`, `io.lettuce.core=DEBUG`.

## 20. Ejemplo completo

Caché de productos con Redis + ranking + rate limiting + lock, listo para usar.

```yaml
# application.yml
spring:
  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: 6379
      password: ${REDIS_PASSWORD:}
      timeout: 2s
      lettuce:
        pool:
          max-active: 16
          max-idle: 8
          min-idle: 2
  cache:
    type: redis
```

```java
// ---------- Configuración: Template JSON + caché con TTL ----------
@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory f) {
        GenericJackson2JsonRedisSerializer json = new GenericJackson2JsonRedisSerializer();
        RedisTemplate<String, Object> t = new RedisTemplate<>();
        t.setConnectionFactory(f);
        t.setKeySerializer(RedisSerializer.string());
        t.setHashKeySerializer(RedisSerializer.string());
        t.setValueSerializer(json);
        t.setHashValueSerializer(json);
        return t;
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer cachesCustomizer() {
        RedisCacheConfiguration base = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .disableCachingNullValues()
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer()));
        return b -> b.withCacheConfiguration("productos", base.entryTtl(Duration.ofHours(1)))
                     .withCacheConfiguration("listados", base.entryTtl(Duration.ofMinutes(2)));
    }

    @Bean
    public CacheErrorHandler cacheErrorHandler() {            // Redis caído ≠ app caída
        return new SimpleCacheErrorHandler() {
            @Override public void handleCacheGetError(RuntimeException e, Cache c, Object k) { /* log y seguir */ }
            @Override public void handleCachePutError(RuntimeException e, Cache c, Object k, Object v) { /* log */ }
        };
    }
}

// ---------- Service con caché ----------
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "productos")
public class ProductoService {

    private final ProductoRepository repo;
    private final ProductoMapper mapper;
    private final RedisTemplate<String, Object> redis;

    @Cacheable(key = "#id", unless = "#result == null")
    public ProductoDto buscar(Long id) {
        return repo.findById(id).map(mapper::toDto)
                   .orElseThrow(() -> new ProductoNoEncontradoException(id));
    }

    @Caching(put   = @CachePut(key = "#result.id"),
             evict = @CacheEvict(cacheNames = "listados", allEntries = true))
    public ProductoDto actualizar(Long id, ProductoRequest req) {
        Producto p = repo.findById(id).orElseThrow();
        mapper.actualizar(p, req);
        return mapper.toDto(repo.save(p));
    }

    @CacheEvict(key = "#id")
    public void eliminar(Long id) { repo.deleteById(id); }

    // Ranking de más vistos (Sorted Set)
    public void registrarVista(Long id) {
        redis.opsForZSet().incrementScore("ranking:vistas", String.valueOf(id), 1);
    }

    public List<ProductoDto> masVistos(int n) {
        Set<Object> ids = redis.opsForZSet().reverseRange("ranking:vistas", 0, n - 1);
        return ids == null ? List.of()
                : ids.stream().map(i -> buscar(Long.valueOf(i.toString()))).toList();
    }
}

// ---------- Rate limiting como filtro ----------
@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final StringRedisTemplate redis;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        String key = "rate:" + req.getRemoteAddr() + ":" + (System.currentTimeMillis() / 60_000);
        Long n = redis.opsForValue().increment(key);
        if (n != null && n == 1) redis.expire(key, Duration.ofMinutes(1));

        if (n != null && n > 100) {
            res.setStatus(429);
            res.setHeader("Retry-After", "60");
            return;
        }
        chain.doFilter(req, res);
    }
}

// ---------- Controller ----------
@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService service;

    @GetMapping("/{id}")
    public ProductoDto buscar(@PathVariable Long id) {
        service.registrarVista(id);                      // ranking
        return service.buscar(id);                       // 1ª vez BD, luego Redis
    }

    @GetMapping("/mas-vistos")
    public List<ProductoDto> top(@RequestParam(defaultValue = "10") int n) {
        return service.masVistos(n);
    }

    @PutMapping("/{id}")
    public ProductoDto actualizar(@PathVariable Long id, @Valid @RequestBody ProductoRequest r) {
        return service.actualizar(id, r);                // actualiza BD y caché
    }
}
```
```bash
# Probar
curl localhost:8080/api/productos/5          # MISS (consulta la BD)
curl localhost:8080/api/productos/5          # HIT  (viene de Redis)
redis-cli KEYS '*'
# 1) "productos::5"
# 2) "ranking:vistas"
redis-cli TTL "productos::5"
redis-cli ZREVRANGE ranking:vistas 0 9 WITHSCORES
```

---

## Tabla rápida: ¿qué herramienta de Spring uso?

| Necesito... | Uso |
|---|---|
| Cachear resultados de métodos | `@Cacheable` / `@CacheEvict` / `@CachePut` |
| Control total de claves y estructuras | `RedisTemplate` / `StringRedisTemplate` |
| Guardar entidades simples con TTL | `@RedisHash` + `CrudRepository` |
| Compartir sesiones entre instancias | Spring Session + Redis |
| Notificaciones en tiempo real | Pub/Sub |
| Eventos fiables con ACK | Streams |
| Colas simples | `List` (`BLPOP`) |
| Contadores / rate limit | `increment` + `expire` (o Lua) |
| Ranking | `ZSet` |
| Lock distribuido | `setIfAbsent` + Lua, o Redisson |
| Varios comandos en 1 viaje | Pipeline |
| Operación atómica compleja | Script Lua |
| Alta disponibilidad | Sentinel / Cluster / servicio gestionado |

## Cheat-sheet: Spring ↔ comando Redis

| Spring (`redisTemplate...`) | Redis |
|---|---|
| `opsForValue().set(k, v, ttl)` | `SET k v EX` |
| `opsForValue().get(k)` | `GET k` |
| `opsForValue().increment(k)` | `INCR k` |
| `opsForValue().setIfAbsent(k, v, ttl)` | `SET k v NX EX` |
| `opsForHash().put(k, f, v)` | `HSET k f v` |
| `opsForHash().entries(k)` | `HGETALL k` |
| `opsForList().rightPush(k, v)` | `RPUSH k v` |
| `opsForList().leftPop(k, timeout)` | `BLPOP k t` |
| `opsForSet().add(k, v)` | `SADD k v` |
| `opsForZSet().add(k, v, score)` | `ZADD k score v` |
| `opsForZSet().reverseRangeWithScores(k, 0, 9)` | `ZREVRANGE k 0 9 WITHSCORES` |
| `delete(k)` / `unlink(k)` | `DEL k` / `UNLINK k` |
| `expire(k, ttl)` | `EXPIRE k` |
| `getExpire(k)` | `TTL k` |
| `hasKey(k)` | `EXISTS k` |
| `scan(opts)` | `SCAN` |
| `convertAndSend(canal, msg)` | `PUBLISH` |
| `opsForStream().add(record)` | `XADD` |
| `executePipelined(...)` | pipeline |
| `execute(script, keys, args)` | `EVAL` |

## Glosario de siglas

| Sigla | Significado |
|---|---|
| **TTL** | Time To Live |
| **LRU / LFU** | Least Recently / Least Frequently Used |
| **RDB / AOF** | Redis Database snapshot / Append Only File |
| **RESP** | REdis Serialization Protocol |
| **ACL** | Access Control List |
| **TLS** | Transport Layer Security |
| **HA** | High Availability |
| **RTT** | Round-Trip Time |
| **OOM** | Out Of Memory |
| **FIFO / LIFO** | First/Last In, First Out |
| **NX / XX** | *Not eXists* / *eXists* (condiciones de `SET`) |
| **PX / EX** | Expiración en milisegundos / segundos |
| **JSON / DTO** | JavaScript Object Notation / Data Transfer Object |
| **SpEL** | Spring Expression Language |
| **ORM** | Object-Relational Mapping |
| **CQRS** | Command Query Responsibility Segregation |
| **OTP** | One-Time Password |
| **JWT / JTI** | JSON Web Token / identificador del token |

---

> **Tip de lectura:** ante un proyecto con Redis, mira primero **`application.yml`** (host, modo: standalone/sentinel/cluster, `spring.cache.type`), luego busca `@EnableCaching` y las anotaciones `@Cacheable` (qué se cachea y con qué TTL), la clase `RedisConfig` (serializadores: define cómo se **ven** los datos en `redis-cli`) y los usos de `RedisTemplate`/`StringRedisTemplate` (qué estructuras y prefijos de clave usa la aplicación). Con `redis-cli KEYS '*'` (o `SCAN`) en desarrollo ves de inmediato qué guarda realmente.

