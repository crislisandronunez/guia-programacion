# Laboratorio de interfaces, contratos y capas

Java 21, Maven y Spring Boot 3.5.16.
Basado en [Interfaces, contratos y ubicación por capas](../guias/Glosario-Interface-Contratos.md).

## Qué incluye

- Aplicación mínima con Web, Validation y Actuator.
- Prueba HTTP de arranque y colección inicial de Postman.
- Caso ficticio de una imprenta y diez ejercicios progresivos.
- PostgreSQL en Compose para el segundo adaptador de persistencia, desde la etapa 08.

Las interfaces de negocio, políticas, casos de uso, adaptadores y pruebas de contrato
son entregables tuyos. No están implementados. La base solo permite verificar el arranque.
Primero trabajás con Java puro y memoria; Spring se usa después para conectar las piezas.

## Abrir y ejecutar

Agregá este pom.xml en IntelliJ con **Add as Maven Project**, o abrilo por separado.
Seleccioná JDK 21 para Project SDK y para el importador y runner de Maven.
Podés usar Maven de IntelliJ; no se incluye Maven Wrapper.

Ejecutá com.estudio.contratos.LaboratorioContratosApplication.
Con Java 21 y Maven en PATH, desde esta carpeta:

```shell
mvn spring-boot:run
```

Importá postman/laboratorio-interfaces.postman_collection.json.
GET http://localhost:8089/actuator/health debe devolver 200 y {"status":"UP"}.
SERVER_PORT permite cambiar el puerto; actualizá baseUrl en Postman.
Las rutas /api todavía no existen. El arranque inicial no requiere Docker.

## Verificar

```shell
mvn verify
java -jar target/laboratorio-interfaces-0.0.1-SNAPSHOT.jar
```

El test inicial usa un puerto aleatorio y no valida todavía contratos de negocio.
También podés ejecutar verify desde Maven en IntelliJ.

## PostgreSQL, desde la etapa 08

Iniciá Docker Desktop y ejecutá desde esta carpeta:

```shell
docker compose up -d --wait
```

| Campo para DBeaver | Valor |
|---|---|
| Host | localhost |
| Puerto | 5438 |
| Base | contratos_lab |
| Usuario | contratos_lab |
| Contraseña de práctica | contratos_lab_local |

Las dependencias JPA, migraciones, configuración y adaptador se agregan al resolver
la etapa 08. Levantar el contenedor no conecta automáticamente la aplicación.
La base aún no tiene tablas de negocio.

Para detener conservando los datos:

```shell
docker compose down
```

## Recorrido

Empezá por el [caso](caso/imprenta.md) y el [índice](ejercicios/README.md).
Vas a construir políticas de dominio, puertos de entrada y salida, adaptadores en
memoria y JPA, y una suite común que compruebe su comportamiento.

Los paquetes ayudan a expresar límites; no los imponen por sí solos. Los contratos
también deben especificar errores, ausencia, precisión, efectos y concurrencia.

Referencias para contrastar la guía:
[interfaces en Java 21](https://docs.oracle.com/javase/specs/jls/se21/html/jls-9.html)
y [artículo original de arquitectura hexagonal](https://alistair.cockburn.us/hexagonal-architecture).

