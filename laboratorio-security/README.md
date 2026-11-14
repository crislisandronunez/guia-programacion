# Laboratorio Spring Security

Java 21, Maven, Spring Boot 3.5.16 y la versión de Spring Security gestionada por Boot.
Basado en [Glosario Spring Security](../guias/Glorasio-Spring-Security.md).

## Qué está preparado

- Aplicación mínima con Web, Security, Validation y Actuator.
- Dos pruebas iniciales con la cadena real de filtros y una colección Postman.
- Caso ficticio y catorce consignas relacionadas con las veinticinco secciones de la guía.

No hay SecurityFilterChain propia, usuarios propios, controladores, servicio JWT ni
soluciones de negocio. Los vas a implementar vos. La base usa la auto-configuración
de seguridad para que puedas observarla antes de reemplazarla.

El recorrido principal no necesita Docker ni base de datos. Los usuarios y solicitudes
se implementarán en memoria; reiniciar perderá los cambios. La integración OIDC real
se propone como extensión con un proveedor local, sin requerir cuentas externas.

## Abrir y ejecutar

Agregá este pom.xml con **Add as Maven Project** en IntelliJ, o abrilo por separado.
Seleccioná JDK 21 en Project SDK y en el importador y runner de Maven.
Podés usar Maven incluido en IntelliJ; no se incluye Maven Wrapper.

Ejecutá com.estudio.security.LaboratorioSecurityApplication.
Con Java 21 y Maven en PATH, desde esta carpeta:

```shell
mvn spring-boot:run
```

La aplicación escucha en http://localhost:8087. Podés cambiarlo con SERVER_PORT.
Importá postman/laboratorio-security.postman_collection.json y ajustá baseUrl si corresponde.

- GET /actuator/health sin credenciales devuelve 200 y {"status":"UP"}.
- GET /api/solicitudes sin credenciales, con Accept: application/json, devuelve 401.
- Con Accept: text/html, una ruta protegida puede redirigir al formulario de login.

El 401 inicial ocurre antes de llegar a un controlador: /api/solicitudes todavía no
está implementado. No demuestra que ya exista una API de solicitudes.
Boot permite consultar health básico con su configuración Actuator predeterminada;
no asumas que absolutamente todas las rutas están cerradas.

El usuario inicial es user y la contraseña temporal aparece en la consola de arranque.
No se fija ni se guarda en la colección. Se reemplazan en el ejercicio 03.
No copies esa contraseña a respuestas ni capturas del laboratorio.

## Verificar

```shell
mvn verify
java -jar target/laboratorio-security-0.0.1-SNAPSHOT.jar
```

También podés ejecutar verify desde Maven en IntelliJ.
Los tests iniciales cargan el contexto y atraviesan filtros mediante MockMvc.
No necesitan Docker; no verifican todavía autenticación válida ni reglas de negocio.

## Recorrido

Leé el [caso](caso/portal-solicitudes.md) y el [índice de ejercicios](ejercicios/README.md).
Primero construís una web con sesión y CSRF; luego separás /api/** con Bearer JWT.
En el resultado final, la cadena web conserva sus protecciones y la API acepta
únicamente credenciales Bearer explícitas, según la consigna.

Las claves y certificados locales futuros van en local/ (ignorado por Git).
Los tokens de prueba se generan al ejecutar tests. No hay claves privadas compartidas.

## Referencias compatibles

- [Spring Security 6.5](https://docs.spring.io/spring-security/reference/6.5/index.html).
- [Protección CSRF](https://docs.spring.io/spring-security/reference/6.5/servlet/exploits/csrf.html).
- [Resource Server JWT](https://docs.spring.io/spring-security/reference/6.5/servlet/oauth2/resource-server/jwt.html).
- [Seguridad de endpoints Actuator en Boot 3.5](https://docs.spring.io/spring-boot/3.5/reference/actuator/endpoints.html).

Usá la guía como punto de partida y comprobá sus simplificaciones con experimentos:
stateless por sí solo no elimina CSRF, un JWT firmado no oculta su contenido y
@WithMockUser no comprueba una contraseña.

