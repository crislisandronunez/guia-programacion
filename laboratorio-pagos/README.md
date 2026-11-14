# Laboratorio de pagos

Proyecto Maven independiente para estudiar [Mundo de pagos](../guias/Mundo-Pagos.md)
con Java 21, Spring Boot 3.5.16 y PostgreSQL 17.

## Qué está preparado

- Aplicación Spring Boot con Web, Validation, JPA y Actuator.
- PostgreSQL en Docker con almacenamiento persistente.
- Colección de Postman con una comprobación de salud.
- Prueba de integración con una base PostgreSQL temporal mediante Testcontainers.
- Ocho consignas progresivas con experimentos y criterios de aceptación.

Esta es la base del laboratorio: todavía no hay entidades, tablas de negocio ni endpoints
de pagos. Los construiremos en los ejercicios, junto con una pasarela simulada.
Usaremos operaciones ficticias y credenciales locales de práctica.

## 1. Abrir en IntelliJ

Abrí este `pom.xml` como proyecto Maven, o agregalo al proyecto actual desde la ventana
Maven con **Add Maven Projects**. No hace falta un POM padre.

En Project Structure seleccioná **JDK 21** como Project SDK. En la configuración de Maven,
seleccioná también JDK 21 para el importador y el runner. Podés usar el Maven incluido
en IntelliJ. Esperá a que se descarguen las dependencias.

## 2. Levantar PostgreSQL

Abrí Docker Desktop y esperá a que el motor esté disponible. Desde esta carpeta:

```shell
docker compose up -d --wait
docker compose ps
```

La primera ejecución descarga la imagen. La base escucha en `localhost:5434`;
dentro del contenedor utiliza `5432`.

Para detenerla conservando los datos:

```shell
docker compose down
```

El volumen `pagos_data` conserva la información entre reinicios.

## 3. Ejecutar la aplicación

Ejecutá `com.estudio.pagos.LaboratorioPagosApplication` desde IntelliJ.
Como alternativa, si Java 21 y Maven están en el PATH:

```shell
mvn spring-boot:run
```

La aplicación escucha en `http://localhost:8080` y se conecta a PostgreSQL.
`GET http://localhost:8080/actuator/health` debe devolver HTTP 200 y:

```json
{"status":"UP"}
```

Si PostgreSQL no está disponible, revisá Docker y el puerto antes de arrancar la aplicación.
Podés sobrescribir `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` y `SERVER_PORT` mediante variables
de entorno de la configuración de ejecución de IntelliJ.

## 4. Conectar DBeaver

Creá una conexión de tipo **PostgreSQL** con estos datos:

| Campo | Valor |
|---|---|
| Host | localhost |
| Puerto | 5434 |
| Base de datos | pagos_lab |
| Usuario | pagos_lab |
| Contraseña | pagos_lab_local |

Permití descargar el driver si DBeaver lo solicita y usá **Test Connection**.
Podés verificar la conexión ejecutando:

```sql
SELECT current_database(), current_user;
```

Todavía no habrá tablas de negocio. Agregaremos migraciones al introducir persistencia;
Hibernate está configurado con `ddl-auto: validate` para validar el esquema.

## 5. Importar Postman

Importá `postman/laboratorio-pagos.postman_collection.json` y ejecutá la solicitud inicial.
La variable de colección `baseUrl` apunta a `http://localhost:8080`.
Las dos comprobaciones deben pasar. Ampliaremos la colección al implementar cada endpoint.

## 6. Verificar el proyecto

Con Java 21 y Maven configurados:

```shell
mvn verify
```

También podés ejecutar el objetivo `verify` desde la ventana Maven de IntelliJ.
La prueba de integración crea una base temporal independiente de `pagos_lab` y verifica
el endpoint de salud con una conexión real. Requiere Docker activo y poder descargar
`postgres:17`. Si Docker no está disponible, la prueba se omite: un build correcto en ese
caso solo confirma la compilación y el empaquetado, no el funcionamiento con PostgreSQL.

## Recorrido

| Etapa | Tema | Secciones de la guía |
|---|---|---|
| 1 | [Dinero, moneda y desglose financiero](ejercicios/01-dinero.md) | 1–3 |
| 2 | [Ciclo de vida del pago](ejercicios/02-ciclo-de-vida.md) | 4–5 |
| 3 | [Idempotencia y concurrencia](ejercicios/03-idempotencia.md) | 7 |
| 4 | [Webhooks y resultados inciertos](ejercicios/04-webhooks-y-timeouts.md) | 6, 13 |
| 5 | [Libro mayor y billeteras](ejercicios/05-ledger-y-billeteras.md) | 8–9 |
| 6 | [Reembolsos y conciliación](ejercicios/06-reembolsos-y-conciliacion.md) | 10, 14 |
| 7 | [Marketplace, suscripciones y pasarelas](ejercicios/07-escenarios-avanzados.md) | 11–12, 16 |
| 8 | [Seguridad y desafío integrador](ejercicios/08-seguridad-e-integracion.md) | 15, 17 |

Todas las consignas están disponibles; sus funcionalidades son tareas por implementar.
Seguí el orden y consultá las [reglas comunes](ejercicios/README.md).
Los paquetes de dominio se agregarán cuando los necesitemos. El siguiente paso es el
[ejercicio 01](ejercicios/01-dinero.md).

La guía es material de estudio, no una especificación completa de una pasarela real.
En particular, un timeout no prueba que un cobro falló: antes de intentar otro cobro
o cambiar de proveedor, resolveremos el resultado incierto del primer intento.

Referencia del framework: [documentación de Spring Boot 3.5](https://docs.spring.io/spring-boot/3.5/).
