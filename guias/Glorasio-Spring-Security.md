# Glosario Spring Security

Términos para entender autenticación, autorización, JWT, OAuth2, filtros y configuración de seguridad en proyectos
Spring Boot. Ejemplos escritos para **Spring Security 6 / Spring Boot 3** (Jakarta, sin `WebSecurityConfigurerAdapter`).

## Índice

1. [Conceptos generales](#1-conceptos-generales)
2. [Dependencias y comportamiento por defecto](#2-dependencias-y-comportamiento-por-defecto)
3. [Arquitectura: la cadena de filtros](#3-arquitectura-la-cadena-de-filtros)
4. [Configuración: SecurityFilterChain y HttpSecurity](#4-configuración-securityfilterchain-y-httpsecurity)
5. [Reglas de autorización (requestMatchers)](#5-reglas-de-autorización-requestmatchers)
6. [Usuarios y UserDetails](#6-usuarios-y-userdetails)
7. [Contraseñas y PasswordEncoder](#7-contraseñas-y-passwordencoder)
8. [Proceso de autenticación](#8-proceso-de-autenticación)
9. [Roles, permisos y autoridades](#9-roles-permisos-y-autoridades)
10. [Seguridad a nivel de método](#10-seguridad-a-nivel-de-método)
11. [SecurityContext: el usuario actual](#11-securitycontext-el-usuario-actual)
12. [Sesiones vs stateless](#12-sesiones-vs-stateless)
13. [Login por formulario y logout](#13-login-por-formulario-y-logout)
14. [HTTP Basic](#14-http-basic)
15. [JWT (JSON Web Token)](#15-jwt-json-web-token)
16. [OAuth2 y OpenID Connect](#16-oauth2-y-openid-connect)
17. [CSRF](#17-csrf)
18. [CORS con Security](#18-cors-con-security)
19. [Cabeceras de seguridad y HTTPS](#19-cabeceras-de-seguridad-y-https)
20. [Manejo de errores de seguridad](#20-manejo-de-errores-de-seguridad)
21. [Remember-me, bloqueo de cuentas y otras funciones](#21-remember-me-bloqueo-de-cuentas-y-otras-funciones)
22. [Testing de seguridad](#22-testing-de-seguridad)
23. [Excepciones y errores comunes](#23-excepciones-y-errores-comunes)
24. [Ejemplo completo: API con JWT](#24-ejemplo-completo-api-con-jwt)
25. [Buenas prácticas y vulnerabilidades](#25-buenas-prácticas-y-vulnerabilidades)

---

## 1. Conceptos generales

| Término                            | Significado                                                                                     |
|------------------------------------|-------------------------------------------------------------------------------------------------|
| **Spring Security**                | Framework de Spring para autenticación, autorización y protección contra ataques comunes.       |
| **Autenticación (Authentication)** | Verificar **quién eres** (usuario + contraseña, token, huella...).                              |
| **Autorización (Authorization)**   | Verificar **qué puedes hacer** (roles, permisos). Siempre ocurre después de autenticar.         |
| **Principal**                      | El usuario autenticado (su identidad).                                                          |
| **Credentials**                    | Lo que prueba la identidad (contraseña, token).                                                 |
| **Authority (autoridad)**          | Un permiso concreto (`READ_USERS`, `ROLE_ADMIN`).                                               |
| **Rol (Role)**                     | Agrupación de permisos; en Spring es una autoridad con prefijo `ROLE_`.                         |
| **Granted Authority**              | Representación Java de una autoridad (`GrantedAuthority`).                                      |
| **Credenciales inválidas**         | Error 401 (no autenticado).                                                                     |
| **Acceso denegado**                | Error 403 (autenticado, pero sin permiso).                                                      |
| **401 Unauthorized**               | Falta autenticación o es inválida.                                                              |
| **403 Forbidden**                  | Sabemos quién eres, pero no tienes permiso.                                                     |
| **Stateful**                       | El servidor recuerda al usuario mediante una sesión.                                            |
| **Stateless**                      | El servidor no guarda estado; cada petición trae su prueba (token).                             |
| **Sesión (HttpSession)**           | Datos del usuario guardados en el servidor, identificados por la cookie `JSESSIONID`.           |
| **Cookie**                         | Dato guardado en el navegador y enviado en cada petición.                                       |
| **Token**                          | Cadena que representa una identidad/permiso (ej. JWT).                                          |
| **Bearer token**                   | Token enviado en la cabecera `Authorization: Bearer <token>`.                                   |
| **Hash**                           | Resultado irreversible de una función matemática sobre un texto (así se guardan contraseñas).   |
| **Salt**                           | Dato aleatorio añadido antes de hashear, para que dos contraseñas iguales den hashes distintos. |
| **Cifrado (encryption)**           | Transformación **reversible** con clave (distinto de hash).                                     |
| **Firma digital**                  | Garantiza que un dato no fue alterado y quién lo emitió.                                        |
| **SSO**                            | Single Sign-On: iniciar sesión una vez y acceder a varias aplicaciones.                         |
| **MFA / 2FA**                      | Autenticación multifactor / de dos factores.                                                    |
| **RBAC**                           | Role-Based Access Control: permisos según rol.                                                  |
| **ABAC**                           | Attribute-Based Access Control: permisos según atributos (dueño del recurso, hora...).          |
| **ACL**                            | Access Control List: permisos por objeto individual.                                            |
| **IdP**                            | Identity Provider: sistema que autentica (Keycloak, Auth0, Google, Okta).                       |
| **CSRF**                           | Ataque donde otro sitio hace peticiones usando tu sesión.                                       |
| **XSS**                            | Inyección de scripts en una página.                                                             |
| **CORS**                           | Mecanismo del navegador para permitir peticiones entre orígenes distintos.                      |
| **HTTPS / TLS**                    | HTTP cifrado.                                                                                   |
| **Brute force**                    | Ataque probando contraseñas repetidamente.                                                      |
| **Session fixation**               | Ataque que fija el ID de sesión de la víctima.                                                  |
| **Clickjacking**                   | Engañar al usuario para que haga clic en una página oculta (iframe).                            |
| **Least privilege**                | Principio: dar solo los permisos mínimos necesarios.                                            |

### Autenticación vs autorización

```
Petición → ¿Quién eres? (Autenticación) → ¿Puedes hacer esto? (Autorización) → Recurso
              401 si falla                     403 si falla
```

## 2. Dependencias y comportamiento por defecto

| Término                                            | Significado                                                                                             |
|----------------------------------------------------|---------------------------------------------------------------------------------------------------------|
| `spring-boot-starter-security`                     | Starter principal de Spring Security.                                                                   |
| `spring-security-test`                             | Utilidades para testear seguridad.                                                                      |
| `spring-boot-starter-oauth2-resource-server`       | Valida tokens JWT/opaque en una API.                                                                    |
| `spring-boot-starter-oauth2-client`                | Permite "Login con Google/GitHub", etc.                                                                 |
| `spring-security-oauth2-authorization-server`      | Servidor de autorización propio.                                                                        |
| `jjwt-api / jjwt-impl / jjwt-jackson`              | Librería JJWT para crear/validar JWT.                                                                   |
| `nimbus-jose-jwt`                                  | Librería JWT usada internamente por Spring.                                                             |
| `thymeleaf-extras-springsecurity6`                 | Tags de seguridad en plantillas Thymeleaf.                                                              |
| **Comportamiento por defecto**                     | Al añadir el starter, **todos** los endpoints quedan protegidos, con login por formulario y HTTP Basic. |
| `user`                                             | Usuario por defecto.                                                                                    |
| **Password generada**                              | Se imprime en consola al arrancar: `Using generated security password: ...`                             |
| `spring.security.user.name` / `password` / `roles` | Configurar el usuario por defecto en `application.properties`.                                          |

### Ejemplo: dependencias

```xml

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
<groupId>org.springframework.security</groupId>
<artifactId>spring-security-test</artifactId>
<scope>test</scope>
</dependency>

        <!-- JWT con JJWT -->
<dependency>
<groupId>io.jsonwebtoken</groupId>
<artifactId>jjwt-api</artifactId>
<version>0.12.5</version>
</dependency>
<dependency>
<groupId>io.jsonwebtoken</groupId>
<artifactId>jjwt-impl</artifactId>
<version>0.12.5</version>
<scope>runtime</scope>
</dependency>
<dependency>
<groupId>io.jsonwebtoken</groupId>
<artifactId>jjwt-jackson</artifactId>
<version>0.12.5</version>
<scope>runtime</scope>
</dependency>
```

### Ejemplo: usuario por defecto

```properties
spring.security.user.name=admin
spring.security.user.password=1234
spring.security.user.roles=ADMIN
```

## 3. Arquitectura: la cadena de filtros

Spring Security funciona con **filtros de servlet** que se ejecutan **antes** de llegar al controlador.

| Término                                          | Significado                                                                     |
|--------------------------------------------------|---------------------------------------------------------------------------------|
| **Filter**                                       | Componente que intercepta la petición HTTP.                                     |
| `DelegatingFilterProxy`                          | Puente entre el contenedor de servlets y Spring.                                |
| `FilterChainProxy`                               | Filtro principal de Spring Security; contiene las cadenas de seguridad.         |
| `SecurityFilterChain`                            | Una cadena de filtros aplicada a ciertas URLs. Puede haber varias.              |
| `SecurityContextHolderFilter`                    | Carga/guarda el `SecurityContext` de la petición.                               |
| `UsernamePasswordAuthenticationFilter`           | Procesa el login por formulario (`POST /login`).                                |
| `BasicAuthenticationFilter`                      | Procesa HTTP Basic.                                                             |
| `BearerTokenAuthenticationFilter`                | Procesa tokens Bearer (OAuth2 resource server).                                 |
| `OncePerRequestFilter`                           | Clase base para filtros propios que corren una vez por petición.                |
| `CsrfFilter`                                     | Valida el token CSRF.                                                           |
| `CorsFilter`                                     | Aplica las reglas CORS.                                                         |
| `LogoutFilter`                                   | Procesa el cierre de sesión.                                                    |
| `ExceptionTranslationFilter`                     | Convierte excepciones de seguridad en respuestas 401/403.                       |
| `AuthorizationFilter`                            | Último filtro: decide si se permite el acceso (aplica `authorizeHttpRequests`). |
| `AnonymousAuthenticationFilter`                  | Asigna usuario "anónimo" si no hay autenticación.                               |
| `SessionManagementFilter`                        | Gestiona sesiones.                                                              |
| `RequestCacheAwareFilter`                        | Recuerda la URL pedida antes del login para redirigir luego.                    |
| `FilterChain.doFilter(req, res)`                 | Pasa la petición al siguiente filtro.                                           |
| `addFilterBefore / addFilterAfter / addFilterAt` | Colocan un filtro propio en la cadena.                                          |
| `@Order`                                         | Orden entre varias `SecurityFilterChain`.                                       |

### Flujo de una petición

```
Petición HTTP
   ↓
[CorsFilter] → [CsrfFilter] → [LogoutFilter]
   → [JwtFilter (propio)] / [UsernamePasswordAuthenticationFilter] / [BasicAuthenticationFilter]
   → [AnonymousAuthenticationFilter]
   → [ExceptionTranslationFilter]
   → [AuthorizationFilter]      ← ¿tiene permiso?
   ↓
DispatcherServlet → Controller
```

> Tip de depuración: `logging.level.org.springframework.security=DEBUG` muestra qué filtros se ejecutan y por qué se
> rechaza una petición.

## 4. Configuración: SecurityFilterChain y HttpSecurity

| Término                     | Significado                                                                                                        |
|-----------------------------|--------------------------------------------------------------------------------------------------------------------|
| `@EnableWebSecurity`        | Activa la seguridad web.                                                                                           |
| `@Configuration`            | Clase de configuración.                                                                                            |
| `@Bean SecurityFilterChain` | **Forma actual** de configurar la seguridad (reemplaza a `WebSecurityConfigurerAdapter`, eliminado en Security 6). |
| `HttpSecurity`              | Constructor de la configuración de la cadena.                                                                      |
| `http.build()`              | Construye la cadena.                                                                                               |
| `authorizeHttpRequests`     | Reglas de acceso por URL (reemplaza a `authorizeRequests`).                                                        |
| `csrf`                      | Configura/desactiva CSRF.                                                                                          |
| `cors`                      | Activa CORS.                                                                                                       |
| `sessionManagement`         | Política de sesiones.                                                                                              |
| `formLogin`                 | Login por formulario.                                                                                              |
| `httpBasic`                 | Autenticación HTTP Basic.                                                                                          |
| `logout`                    | Configuración del cierre de sesión.                                                                                |
| `oauth2Login`               | Login con proveedores externos.                                                                                    |
| `oauth2ResourceServer`      | API que valida tokens.                                                                                             |
| `exceptionHandling`         | Manejo personalizado de 401/403.                                                                                   |
| `headers`                   | Cabeceras de seguridad.                                                                                            |
| `rememberMe`                | "Recordarme".                                                                                                      |
| `requiresChannel`           | Forzar HTTPS.                                                                                                      |
| `securityMatcher`           | Limita la cadena a ciertas rutas (para tener varias cadenas).                                                      |
| `Customizer.withDefaults()` | Usa la configuración por defecto de esa sección.                                                                   |
| `WebSecurityCustomizer`     | Ignorar rutas completamente (`web.ignoring()`); poco recomendado.                                                  |
| **Lambda DSL**              | Estilo actual: `http.csrf(csrf -> csrf.disable())`. El estilo encadenado con `.and()` está obsoleto.               |

### Ejemplo: configuración mínima

```java

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/", "/public/**").permitAll()
            .requestMatchers("/admin/**").hasRole("ADMIN")
            .anyRequest().authenticated())
        .formLogin(Customizer.withDefaults())
        .logout(Customizer.withDefaults());
    return http.build();
  }
}
```

### Ejemplo: varias cadenas de seguridad (API + web)

```java

@Bean
@Order(1)
public SecurityFilterChain apiChain(HttpSecurity http) throws Exception {
  http.securityMatcher("/api/**")                     // solo aplica a /api/**
      .csrf(csrf -> csrf.disable())
      .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .authorizeHttpRequests(a -> a.anyRequest().authenticated())
      .httpBasic(Customizer.withDefaults());
  return http.build();
}

@Bean
@Order(2)
public SecurityFilterChain webChain(HttpSecurity http) throws Exception {
  http.authorizeHttpRequests(a -> a.anyRequest().authenticated())
      .formLogin(Customizer.withDefaults());          // resto de la app
  return http.build();
}
```

### Ejemplo: ignorar recursos estáticos

```java

@Bean
public WebSecurityCustomizer webSecurityCustomizer() {
  return web -> web.ignoring().requestMatchers("/css/**", "/js/**", "/images/**");
}
// Alternativa más segura: permitirlas en authorizeHttpRequests con permitAll()
```

### Migración de código antiguo (Security 5 → 6)

| Antiguo                                   | Actual                                              |
|-------------------------------------------|-----------------------------------------------------|
| `extends WebSecurityConfigurerAdapter`    | `@Bean SecurityFilterChain`                         |
| `configure(HttpSecurity http)`            | método `@Bean` que recibe `HttpSecurity`            |
| `authorizeRequests()`                     | `authorizeHttpRequests()`                           |
| `antMatchers()` / `mvcMatchers()`         | `requestMatchers()`                                 |
| `.and()` encadenado                       | Lambda DSL                                          |
| `configure(AuthenticationManagerBuilder)` | Bean `UserDetailsService` / `AuthenticationManager` |
| `javax.servlet`                           | `jakarta.servlet`                                   |
| `@EnableGlobalMethodSecurity`             | `@EnableMethodSecurity`                             |
| `access("hasRole(...)")`                  | `access(AuthorizationManager)` / `hasRole(...)`     |

## 5. Reglas de autorización (requestMatchers)

| Método                                  | Significado                                                                                                              |
|-----------------------------------------|--------------------------------------------------------------------------------------------------------------------------|
| `requestMatchers("/ruta/**")`           | Aplica la regla a esas rutas (`*` un nivel, `**` todos los niveles).                                                     |
| `requestMatchers(HttpMethod.GET, "/x")` | Solo para ese verbo HTTP.                                                                                                |
| `anyRequest()`                          | Cualquier otra petición no cubierta antes.                                                                               |
| `permitAll()`                           | Cualquiera, sin autenticación.                                                                                           |
| `denyAll()`                             | Nadie.                                                                                                                   |
| `authenticated()`                       | Cualquier usuario autenticado.                                                                                           |
| `anonymous()`                           | Solo usuarios **no** autenticados.                                                                                       |
| `fullyAuthenticated()`                  | Autenticado y no por "remember-me".                                                                                      |
| `rememberMe()`                          | Autenticado por remember-me.                                                                                             |
| `hasRole("ADMIN")`                      | Tiene el rol (Spring añade `ROLE_` solo).                                                                                |
| `hasAnyRole("ADMIN","USER")`            | Tiene alguno de los roles.                                                                                               |
| `hasAuthority("READ")`                  | Tiene la autoridad exacta.                                                                                               |
| `hasAnyAuthority(...)`                  | Tiene alguna de las autoridades.                                                                                         |
| `access(...)`                           | Regla personalizada (`AuthorizationManager`).                                                                            |
| `hasIpAddress`                          | Restringe por IP (con `access`).                                                                                         |
| **Orden de las reglas**                 | Se evalúan **de arriba abajo**; gana la primera que coincide. Las específicas van **primero** y `anyRequest()` al final. |

### Ejemplo: reglas típicas de una API

```java
.authorizeHttpRequests(auth ->auth
    // público
    .

requestMatchers("/api/auth/**").

permitAll()
    .

requestMatchers("/swagger-ui/**","/v3/api-docs/**").

permitAll()
    .

requestMatchers(HttpMethod.GET, "/api/productos/**").

permitAll()
    .

requestMatchers("/actuator/health").

permitAll()

// por rol
    .

requestMatchers(HttpMethod.POST,   "/api/productos/**").

hasRole("ADMIN")
    .

requestMatchers(HttpMethod.DELETE, "/api/productos/**").

hasRole("ADMIN")
    .

requestMatchers("/api/pedidos/**").

hasAnyRole("USER","ADMIN")

// por autoridad
    .

requestMatchers("/api/reportes/**").

hasAuthority("REPORT_READ")

// cualquier otro: autenticado
    .

anyRequest().

authenticated())
```

### Ejemplo: regla personalizada con `access`

```java
.requestMatchers("/api/usuarios/{id}").

access((authentication, context) ->{
String idRuta = context.getVariables().get("id");
boolean esDueño = authentication.get().getName().equals(idRuta);
boolean esAdmin = authentication.get().getAuthorities().stream()
    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    return new

AuthorizationDecision(esDueño ||esAdmin);
})
```

### Patrones de ruta

| Patrón             | Coincide con                                                   |
|--------------------|----------------------------------------------------------------|
| `/api/usuarios`    | Solo esa ruta exacta.                                          |
| `/api/usuarios/*`  | `/api/usuarios/5` (un solo nivel).                             |
| `/api/usuarios/**` | `/api/usuarios`, `/api/usuarios/5`, `/api/usuarios/5/pedidos`. |
| `/api/*/detalle`   | `/api/x/detalle`.                                              |
| `/archivos/*.pdf`  | PDFs en esa carpeta.                                           |

> **Error común:** poner `anyRequest().authenticated()` **antes** de otras reglas: las siguientes nunca se evalúan.

## 6. Usuarios y UserDetails

| Término                           | Significado                                                                                                     |
|-----------------------------------|-----------------------------------------------------------------------------------------------------------------|
| `UserDetails`                     | Interfaz que representa al usuario para Spring Security (username, password, authorities, estado de la cuenta). |
| `User` (de Spring)                | Implementación lista de `UserDetails`.                                                                          |
| `UserDetailsService`              | Interfaz con `loadUserByUsername(String)`: carga el usuario (desde BD u otro origen).                           |
| `UsernameNotFoundException`       | Se lanza si el usuario no existe.                                                                               |
| `InMemoryUserDetailsManager`      | Usuarios en memoria (pruebas/demos).                                                                            |
| `JdbcUserDetailsManager`          | Usuarios en BD con el esquema estándar de Spring.                                                               |
| `UserDetailsManager`              | Extiende `UserDetailsService` con crear/modificar/borrar usuarios.                                              |
| `UserDetailsPasswordService`      | Permite actualizar el hash si cambia el algoritmo.                                                              |
| `isEnabled()`                     | Cuenta habilitada.                                                                                              |
| `isAccountNonLocked()`            | Cuenta no bloqueada.                                                                                            |
| `isAccountNonExpired()`           | Cuenta no expirada.                                                                                             |
| `isCredentialsNonExpired()`       | Credenciales no expiradas.                                                                                      |
| `getAuthorities()`                | Roles/permisos del usuario.                                                                                     |
| `getUsername()` / `getPassword()` | Identificador y contraseña (hash).                                                                              |
| `CredentialsContainer`            | `eraseCredentials()` borra la contraseña tras autenticar.                                                       |
| **Entidad de usuario propia**     | Tu clase `Usuario` de BD, normalmente separada del `UserDetails`.                                               |

### Ejemplo: usuarios en memoria

```java

@Bean
public UserDetailsService users(PasswordEncoder encoder) {
  UserDetails user = User.withUsername("ana")
      .password(encoder.encode("1234"))
      .roles("USER")
      .build();
  UserDetails admin = User.withUsername("root")
      .password(encoder.encode("admin"))
      .roles("USER", "ADMIN")
      .build();
  return new InMemoryUserDetailsManager(user, admin);
}
```

### Ejemplo: entidad de usuario + roles

```java

@Entity
@Table(name = "usuarios")
public class Usuario {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false)
  private String username;

  @Column(nullable = false)
  private String password;            // hash BCrypt, nunca texto plano

  private boolean enabled = true;
  private boolean bloqueado = false;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "usuario_rol",
      joinColumns = @JoinColumn(name = "usuario_id"),
      inverseJoinColumns = @JoinColumn(name = "rol_id"))
  private Set<Rol> roles = new HashSet<>();
  // getters y setters
}

@Entity
public class Rol {
  @Id
  @GeneratedValue
  private Long id;
  @Column(unique = true)
  private String nombre;     // "ROLE_ADMIN", "ROLE_USER"
}

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
  Optional<Usuario> findByUsername(String username);
}
```

### Ejemplo: `UserDetailsService` propio (el más común)

```java

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final UsuarioRepository repo;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Usuario u = repo.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("No existe: " + username));

    List<GrantedAuthority> authorities = u.getRoles().stream()
        .map(r -> new SimpleGrantedAuthority(r.getNombre()))   // ya con "ROLE_"
        .collect(Collectors.toList());

    return new User(u.getUsername(), u.getPassword(),
        u.isEnabled(), true, true, !u.isBloqueado(), authorities);
  }
}
```

### Ejemplo: implementar `UserDetails` directamente en la entidad

```java
public class UsuarioPrincipal implements UserDetails {
  private final Usuario usuario;

  public UsuarioPrincipal(Usuario usuario) {
    this.usuario = usuario;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return usuario.getRoles().stream()
        .map(r -> new SimpleGrantedAuthority(r.getNombre())).toList();
  }

  @Override
  public String getPassword() {
    return usuario.getPassword();
  }

  @Override
  public String getUsername() {
    return usuario.getUsername();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return !usuario.isBloqueado();
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return usuario.isEnabled();
  }

  public Long getId() {
    return usuario.getId();
  }     // datos extra accesibles luego
}
```

### Ejemplo: `JdbcUserDetailsManager`

```java

@Bean
public UserDetailsService jdbcUsers(DataSource dataSource) {
  return new JdbcUserDetailsManager(dataSource);   // tablas "users" y "authorities"
}
```

## 7. Contraseñas y PasswordEncoder

| Término                                                      | Significado                                                                                             |
|--------------------------------------------------------------|---------------------------------------------------------------------------------------------------------|
| `PasswordEncoder`                                            | Interfaz para codificar y verificar contraseñas.                                                        |
| `encode(raw)`                                                | Genera el hash de una contraseña.                                                                       |
| `matches(raw, hash)`                                         | Verifica si la contraseña coincide con el hash.                                                         |
| `upgradeEncoding(hash)`                                      | Indica si conviene re-hashear con un algoritmo mejor.                                                   |
| `BCryptPasswordEncoder`                                      | **El más usado.** Algoritmo BCrypt con salt automático y costo configurable.                            |
| `Argon2PasswordEncoder`                                      | Algoritmo Argon2 (muy seguro, consume memoria).                                                         |
| `SCryptPasswordEncoder`                                      | Algoritmo SCrypt.                                                                                       |
| `Pbkdf2PasswordEncoder`                                      | Algoritmo PBKDF2.                                                                                       |
| `DelegatingPasswordEncoder`                                  | Soporta varios algoritmos usando un prefijo (`{bcrypt}...`). Es el que crea `PasswordEncoderFactories`. |
| `PasswordEncoderFactories.createDelegatingPasswordEncoder()` | Crea el delegating encoder por defecto.                                                                 |
| `NoOpPasswordEncoder`                                        | Sin cifrado (**obsoleto, solo para pruebas; nunca en producción**).                                     |
| `{bcrypt}`, `{noop}`, `{pbkdf2}`                             | Prefijos que indican el algoritmo en el hash.                                                           |
| `strength` (costo)                                           | Dificultad de BCrypt (por defecto 10; cada +1 duplica el tiempo).                                       |
| `$2a$10$...`                                                 | Formato de un hash BCrypt (`2a` = versión, `10` = costo).                                               |
| `There is no PasswordEncoder mapped for the id "null"`       | Error: la contraseña guardada no tiene prefijo `{id}` y no hay encoder configurado.                     |
| **Hash irreversible**                                        | No existe forma de "descifrar" un hash; solo se compara.                                                |

### Ejemplo

```java

@Bean
public PasswordEncoder passwordEncoder() {
  return new BCryptPasswordEncoder();          // costo 10 por defecto
  // return new BCryptPasswordEncoder(12);     // más seguro y más lento
  // return PasswordEncoderFactories.createDelegatingPasswordEncoder();
}

// Uso al registrar un usuario
@Service
@RequiredArgsConstructor
public class RegistroService {
  private final UsuarioRepository repo;
  private final PasswordEncoder encoder;

  public Usuario registrar(RegistroRequest req) {
    Usuario u = new Usuario();
    u.setUsername(req.username());
    u.setPassword(encoder.encode(req.password()));   // guarda el HASH
    return repo.save(u);
  }
}

// Verificar manualmente
boolean ok = encoder.matches("miClave", usuario.getPassword());
```

### Ejemplo: cómo se ve un hash

```
Contraseña:  1234
BCrypt:      $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
             └┬┘└┬┘└───────────┬──────────┘└───────────┬───────────────┘
           versión costo      salt (22 chars)         hash
```

> Cada vez que se hashea la misma contraseña el resultado es **distinto** (por el salt); por eso hay que usar`matches()`
> y nunca comparar con `equals()`.

## 8. Proceso de autenticación

| Término                               | Significado                                                                                                                   |
|---------------------------------------|-------------------------------------------------------------------------------------------------------------------------------|
| `Authentication`                      | Interfaz que representa la petición de autenticación y, luego, al usuario ya autenticado.                                     |
| `UsernamePasswordAuthenticationToken` | Implementación para usuario/contraseña. Con 2 argumentos = **sin autenticar**; con 3 (incluye authorities) = **autenticado**. |
| `AuthenticationManager`               | Punto de entrada: `authenticate(Authentication)`.                                                                             |
| `ProviderManager`                     | Implementación por defecto: delega en varios `AuthenticationProvider`.                                                        |
| `AuthenticationProvider`              | Valida un tipo de credenciales.                                                                                               |
| `DaoAuthenticationProvider`           | Valida contra un `UserDetailsService` + `PasswordEncoder`.                                                                    |
| `AuthenticationConfiguration`         | Permite obtener el `AuthenticationManager` global.                                                                            |
| `AuthenticationException`             | Raíz de errores de autenticación.                                                                                             |
| `BadCredentialsException`             | Usuario o contraseña incorrectos.                                                                                             |
| `UsernameNotFoundException`           | Usuario no existe.                                                                                                            |
| `DisabledException`                   | Cuenta deshabilitada.                                                                                                         |
| `LockedException`                     | Cuenta bloqueada.                                                                                                             |
| `AccountExpiredException`             | Cuenta expirada.                                                                                                              |
| `CredentialsExpiredException`         | Contraseña expirada.                                                                                                          |
| `InsufficientAuthenticationException` | Autenticación insuficiente (falta credencial).                                                                                |
| `AuthenticationEntryPoint`            | Define qué responder cuando un usuario **no autenticado** pide algo protegido.                                                |
| `AuthenticationSuccessHandler`        | Se ejecuta tras login correcto.                                                                                               |
| `AuthenticationFailureHandler`        | Se ejecuta tras login fallido.                                                                                                |
| `AuthenticationEventPublisher`        | Publica eventos de login.                                                                                                     |
| `hideUserNotFoundExceptions`          | Por seguridad, convierte "usuario no existe" en "credenciales inválidas".                                                     |

### Flujo de autenticación

```
Credenciales (usuario, clave)
   ↓
UsernamePasswordAuthenticationToken (sin autenticar)
   ↓
AuthenticationManager.authenticate(...)
   ↓
ProviderManager → DaoAuthenticationProvider
   ↓                    ↓
 UserDetailsService   PasswordEncoder.matches(...)
 (carga el usuario)   (compara hash)
   ↓
Authentication AUTENTICADO (con authorities)
   ↓
SecurityContextHolder.getContext().setAuthentication(...)
```

### Ejemplo: exponer el `AuthenticationManager`

```java

@Bean
public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
    throws Exception {
  return config.getAuthenticationManager();
}
```

### Ejemplo: `AuthenticationProvider` personalizado

```java

@Bean
public AuthenticationProvider authenticationProvider(UserDetailsService uds,
                                                     PasswordEncoder encoder) {
  DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
  provider.setUserDetailsService(uds);
  provider.setPasswordEncoder(encoder);
  return provider;
}

// Provider totalmente propio (ej. API key, LDAP, etc.)
@Component
public class ApiKeyAuthenticationProvider implements AuthenticationProvider {
  @Override
  public Authentication authenticate(Authentication auth) throws AuthenticationException {
    String key = (String) auth.getCredentials();
    if (!"clave-secreta".equals(key)) {
      throw new BadCredentialsException("API key inválida");
    }
    return new UsernamePasswordAuthenticationToken("servicio", null,
        List.of(new SimpleGrantedAuthority("ROLE_SERVICE")));
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
  }
}
```

### Ejemplo: autenticar manualmente (login en un controlador de API)

```java

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthenticationManager authManager;
  private final JwtService jwtService;

  @PostMapping("/login")
  public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest req) {
    Authentication auth = authManager.authenticate(
        new UsernamePasswordAuthenticationToken(req.username(), req.password()));
    // si las credenciales son incorrectas lanza BadCredentialsException (→ 401)
    String token = jwtService.generarToken((UserDetails) auth.getPrincipal());
    return ResponseEntity.ok(new TokenResponse(token));
  }
}

public record LoginRequest(@NotBlank String username, @NotBlank String password) {
}

public record TokenResponse(String token) {
}
```

### Ejemplo: handlers de éxito y fallo

```java
.formLogin(form ->form
    .

successHandler((req, res, auth) ->res.

sendRedirect("/dashboard"))
    .

failureHandler((req, res, ex) ->res.

sendRedirect("/login?error=true")))
```

## 9. Roles, permisos y autoridades

| Término                      | Significado                                                               |
|------------------------------|---------------------------------------------------------------------------|
| `GrantedAuthority`           | Interfaz de un permiso/rol (`getAuthority()` devuelve el texto).          |
| `SimpleGrantedAuthority`     | Implementación simple con un `String`.                                    |
| **Prefijo `ROLE_`**          | Convención: un rol es una authority que empieza con `ROLE_`.              |
| `hasRole("ADMIN")`           | Busca la authority `ROLE_ADMIN` (añade el prefijo solo).                  |
| `hasAuthority("ROLE_ADMIN")` | Busca el texto exacto. Equivale al anterior.                              |
| `hasAuthority("READ_USERS")` | Permiso fino, sin prefijo.                                                |
| `GrantedAuthorityDefaults`   | Bean para cambiar o quitar el prefijo `ROLE_`.                            |
| `RoleHierarchy`              | Jerarquía: ADMIN incluye los permisos de USER.                            |
| `RoleHierarchyImpl`          | Implementación de la jerarquía.                                           |
| **Roles vs permisos**        | Rol = "quién eres" (ADMIN); permiso = "qué puedes hacer" (`USER_DELETE`). |

### Ejemplo: authorities en `UserDetails`

```java
// Con roles (Spring añade ROLE_ solo)
User.withUsername("ana").

password(hash).

roles("USER","ADMIN").

build();
// Equivale a authorities: ROLE_USER, ROLE_ADMIN

// Con authorities explícitas (roles + permisos mezclados)
User.

withUsername("ana").

password(hash)
    .

authorities("ROLE_ADMIN","USER_READ","USER_WRITE").

build();

// Programáticamente
List<GrantedAuthority> auths = List.of(
    new SimpleGrantedAuthority("ROLE_ADMIN"),
    new SimpleGrantedAuthority("USER_READ"));
```

### Ejemplo: jerarquía de roles

```java

@Bean
public RoleHierarchy roleHierarchy() {
  return RoleHierarchyImpl.fromHierarchy("""
      ROLE_ADMIN > ROLE_MANAGER
      ROLE_MANAGER > ROLE_USER
      """);
}
// Un ADMIN puede entrar donde se pide MANAGER o USER
```

### Ejemplo: quitar el prefijo `ROLE_`

```java

@Bean
static GrantedAuthorityDefaults grantedAuthorityDefaults() {
  return new GrantedAuthorityDefaults("");   // ahora hasRole("ADMIN") busca "ADMIN"
}
```

### Roles en BD: diseño típico

```
usuarios         roles            usuario_rol
id | username    id | nombre      usuario_id | rol_id
1  | ana         1  | ROLE_ADMIN  1          | 1
2  | luis        2  | ROLE_USER   1          | 2
                                  2          | 2
```

Con permisos finos: `roles` ←→ `permisos` (otra tabla N:N) y se cargan ambos como authorities.

## 10. Seguridad a nivel de método

Se protege cada **método** (normalmente en el Service o Controller) además de las URLs.

| Término                                      | Significado                                                                                                                                                |
|----------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `@EnableMethodSecurity`                      | Activa las anotaciones de seguridad en métodos (reemplaza a `@EnableGlobalMethodSecurity`). `@PreAuthorize` y `@PostAuthorize` vienen activos por defecto. |
| `@PreAuthorize("expr")`                      | Valida **antes** de ejecutar el método. **El más usado.**                                                                                                  |
| `@PostAuthorize("expr")`                     | Valida **después**, pudiendo mirar el valor devuelto (`returnObject`).                                                                                     |
| `@PreFilter`                                 | Filtra la colección de **entrada**.                                                                                                                        |
| `@PostFilter`                                | Filtra la colección **devuelta** (`filterObject`).                                                                                                         |
| `@Secured("ROLE_ADMIN")`                     | Simple, solo roles (requiere `securedEnabled = true`).                                                                                                     |
| `@RolesAllowed("ADMIN")`                     | Estándar JSR-250 (requiere `jsr250Enabled = true`).                                                                                                        |
| `@AuthorizeReturnObject`                     | Aplica seguridad a los objetos devueltos (Security 6.3+).                                                                                                  |
| **SpEL**                                     | Expression Language de Spring usado en las anotaciones.                                                                                                    |
| `hasRole('ADMIN')`                           | Tiene el rol.                                                                                                                                              |
| `hasAnyRole('A','B')`                        | Alguno de los roles.                                                                                                                                       |
| `hasAuthority('X')` / `hasAnyAuthority(...)` | Permisos.                                                                                                                                                  |
| `isAuthenticated()`                          | Está autenticado.                                                                                                                                          |
| `isAnonymous()`                              | Es anónimo.                                                                                                                                                |
| `permitAll()` / `denyAll()`                  | Todos / nadie.                                                                                                                                             |
| `principal`                                  | El usuario actual (`UserDetails`).                                                                                                                         |
| `authentication`                             | El objeto `Authentication`.                                                                                                                                |
| `#parametro`                                 | Accede a un parámetro del método.                                                                                                                          |
| `@bean.metodo(...)`                          | Llama a un bean propio para lógica personalizada.                                                                                                          |
| `returnObject`                               | Valor devuelto (en `@PostAuthorize`).                                                                                                                      |
| `filterObject`                               | Elemento individual (en `@PostFilter`/`@PreFilter`).                                                                                                       |
| `hasPermission(obj, 'perm')`                 | Delega en un `PermissionEvaluator`.                                                                                                                        |
| `PermissionEvaluator`                        | Interfaz para permisos por objeto (ACL/propietario).                                                                                                       |
| `MethodSecurityExpressionHandler`            | Configura el manejo de expresiones.                                                                                                                        |
| `@AuthenticationPrincipal`                   | Inyecta el usuario actual como parámetro (ver sección 11).                                                                                                 |

### Ejemplos

```java

@Configuration
@EnableMethodSecurity               // prePostEnabled = true por defecto
// @EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class MethodSecurityConfig {
}

@Service
public class PedidoService {

  // Solo ADMIN
  @PreAuthorize("hasRole('ADMIN')")
  public void eliminarTodos() { ...}

  // ADMIN o MANAGER
  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  public Reporte generarReporte() { ...}

  // Por permiso fino
  @PreAuthorize("hasAuthority('PEDIDO_WRITE')")
  public Pedido crear(PedidoRequest req) { ...}

  // Combinar condiciones
  @PreAuthorize("hasRole('ADMIN') or hasAuthority('PEDIDO_DELETE')")
  public void borrar(Long id) { ...}

  // Solo el propio usuario (compara parámetro con el principal)
  @PreAuthorize("#username == authentication.name")
  public Perfil verPerfil(String username) { ...}

  // El dueño o un admin
  @PreAuthorize("#pedido.usuarioId == principal.id or hasRole('ADMIN')")
  public void actualizar(Pedido pedido) { ...}

  // Después: solo ve el pedido si es suyo
  @PostAuthorize("returnObject.usuario == authentication.name or hasRole('ADMIN')")
  public Pedido buscar(Long id) { ...}

  // Filtra la lista devuelta: cada uno ve solo lo suyo
  @PostFilter("filterObject.usuario == authentication.name")
  public List<Pedido> listar() { ...}

  // Llamar a un bean propio para lógica compleja
  @PreAuthorize("@seguridad.puedeEditar(#id, authentication)")
  public void editar(Long id) { ...}
}

@Component("seguridad")
public class SeguridadService {
  public boolean puedeEditar(Long pedidoId, Authentication auth) {
    // lógica propia: consultar BD, revisar propietario, etc.
    return true;
  }
}
```

### Ejemplo: `@Secured` y `@RolesAllowed`

```java

@Secured("ROLE_ADMIN")
public void soloAdmin() { ...}

@Secured({"ROLE_ADMIN", "ROLE_MANAGER"})
public void adminOManager() { ...}

@RolesAllowed("ADMIN")                 // sin prefijo ROLE_
public void tambienAdmin() { ...}
```

### Ejemplo: anotación propia (meta-anotación)

```java

@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole('ADMIN')")
public @interface SoloAdmin {
}

@SoloAdmin
public void borrarTodo() { ...}
```

### Ejemplo: `PermissionEvaluator`

```java

@Component
public class MiPermissionEvaluator implements PermissionEvaluator {
  @Override
  public boolean hasPermission(Authentication auth, Object target, Object permission) {
    if (target instanceof Documento doc) {
      return doc.getPropietario().equals(auth.getName());
    }
    return false;
  }

  @Override
  public boolean hasPermission(Authentication auth, Serializable id, String type, Object perm) {
    return false;
  }
}
// Uso:  @PreAuthorize("hasPermission(#doc, 'WRITE')")
```

### URL vs método

|                 | URL (`authorizeHttpRequests`) | Método (`@PreAuthorize`)                         |
|-----------------|-------------------------------|--------------------------------------------------|
| Dónde se define | Configuración central         | En cada método                                   |
| Granularidad    | Ruta + verbo                  | Lógica fina (propietario, parámetros, resultado) |
| Recomendación   | Reglas generales              | Reglas de negocio específicas. **Usar ambas.**   |

> Si lanzan `AccessDeniedException` en un método protegido, se traduce en un **403**.

## 11. SecurityContext: el usuario actual

| Término                                                                   | Significado                                                                                                 |
|---------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------|
| `SecurityContext`                                                         | Contiene el `Authentication` de la petición actual.                                                         |
| `SecurityContextHolder`                                                   | Acceso estático al contexto actual (`getContext()`).                                                        |
| `SecurityContextHolder.clearContext()`                                    | Limpia el contexto (cerrar sesión programático).                                                            |
| `SecurityContextHolderStrategy`                                           | Cómo se guarda el contexto: `MODE_THREADLOCAL` (por defecto), `MODE_INHERITABLETHREADLOCAL`, `MODE_GLOBAL`. |
| **ThreadLocal**                                                           | El contexto vive en el hilo de la petición; hilos nuevos no lo ven.                                         |
| `SecurityContextRepository`                                               | Dónde se persiste el contexto entre peticiones (sesión, nada, etc.).                                        |
| `HttpSessionSecurityContextRepository`                                    | Lo guarda en la sesión HTTP.                                                                                |
| `RequestAttributeSecurityContextRepository`                               | Solo durante la petición (stateless).                                                                       |
| `@AuthenticationPrincipal`                                                | Inyecta el principal en un parámetro del controlador.                                                       |
| `@CurrentSecurityContext`                                                 | Inyecta el `SecurityContext` o parte de él.                                                                 |
| `Principal`                                                               | Interfaz estándar de Java (`getName()`).                                                                    |
| `Authentication`                                                          | Más completo que `Principal`: authorities, detalles, etc.                                                   |
| `getPrincipal()`                                                          | El usuario (normalmente `UserDetails`).                                                                     |
| `getName()`                                                               | Nombre del usuario.                                                                                         |
| `getAuthorities()`                                                        | Roles/permisos.                                                                                             |
| `isAuthenticated()`                                                       | Si está autenticado.                                                                                        |
| `getDetails()`                                                            | IP, ID de sesión, etc.                                                                                      |
| `DelegatingSecurityContextExecutor` / `DelegatingSecurityContextRunnable` | Propagan el contexto a otros hilos.                                                                         |
| `AnonymousAuthenticationToken`                                            | Usuario anónimo (`anonymousUser`).                                                                          |

### Ejemplos: obtener el usuario actual

```java
// 1. En un controlador (lo más simple)
@GetMapping("/api/yo")
public String yo(Principal principal) {
  return principal.getName();
}

@GetMapping("/api/yo2")
public Object yo2(Authentication authentication) {
  return authentication.getAuthorities();       // [ROLE_USER, ...]
}

// 2. @AuthenticationPrincipal: el UserDetails completo
@GetMapping("/api/perfil")
public PerfilResponse perfil(@AuthenticationPrincipal UserDetails user) {
  return new PerfilResponse(user.getUsername());
}

// Con tu propia clase principal (acceso a datos extra como el id)
@GetMapping("/api/mis-pedidos")
public List<Pedido> misPedidos(@AuthenticationPrincipal UsuarioPrincipal user) {
  return service.pedidosDe(user.getId());
}

// Solo una propiedad del principal con SpEL
@GetMapping("/api/nombre")
public String nombre(@AuthenticationPrincipal(expression = "username") String username) {
  return username;
}

// 3. En cualquier clase (Service, Util)
Authentication auth = SecurityContextHolder.getContext().getAuthentication();
String username = auth.getName();
boolean esAdmin = auth.getAuthorities().stream()
    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
```

### Ejemplo: clase utilitaria

```java
public final class SecurityUtils {
  private SecurityUtils() {
  }

  public static Optional<String> usuarioActual() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
      return Optional.empty();
    }
    return Optional.of(auth.getName());
  }

  public static boolean tieneRol(String rol) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    return auth != null && auth.getAuthorities().stream()
        .anyMatch(a -> a.getAuthority().equals("ROLE_" + rol));
  }
}
```

### Ejemplo: autenticar programáticamente (establecer el usuario)

```java
UserDetails user = userDetailsService.loadUserByUsername("ana");
UsernamePasswordAuthenticationToken auth =
    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
SecurityContext context = SecurityContextHolder.createEmptyContext();
context.

setAuthentication(auth);
SecurityContextHolder.

setContext(context);
```

### Ejemplo: usar el contexto en otro hilo (`@Async`)

```java
// Por defecto el contexto NO pasa a otros hilos. Opciones:
SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);

// o envolver el ejecutor:
@Bean
public Executor executor() {
  return new DelegatingSecurityContextExecutor(Executors.newFixedThreadPool(5));
}
```

### Ejemplo: en Thymeleaf

```html
<!-- thymeleaf-extras-springsecurity6 -->
<div sec:authorize="isAuthenticated()">
    Hola, <span sec:authentication="name">usuario</span>
</div>
<a sec:authorize="hasRole('ADMIN')" th:href="@{/admin}">Panel admin</a>
<div sec:authorize="isAnonymous()"><a th:href="@{/login}">Iniciar sesión</a></div>
```

## 12. Sesiones vs stateless

| Término                                   | Significado                                                                                                          |
|-------------------------------------------|----------------------------------------------------------------------------------------------------------------------|
| `SessionCreationPolicy`                   | Política de creación de sesiones.                                                                                    |
| `ALWAYS`                                  | Siempre crea sesión.                                                                                                 |
| `IF_REQUIRED`                             | (Por defecto) Crea una solo si es necesaria.                                                                         |
| `NEVER`                                   | No crea, pero usa una existente.                                                                                     |
| `STATELESS`                               | **Nunca** crea ni usa sesión. Es lo que se usa con JWT.                                                              |
| `JSESSIONID`                              | Cookie con el ID de sesión del contenedor.                                                                           |
| `maximumSessions(n)`                      | Límite de sesiones simultáneas por usuario.                                                                          |
| `maxSessionsPreventsLogin(true)`          | Si llega al máximo, **rechaza** el nuevo login (en vez de expulsar el anterior).                                     |
| `sessionFixation()`                       | Protección contra session fixation: `changeSessionId()` (por defecto), `newSession()`, `migrateSession()`, `none()`. |
| `invalidSessionUrl`                       | Dónde redirigir si la sesión es inválida.                                                                            |
| `SessionRegistry`                         | Registro de sesiones activas (ver quién está conectado).                                                             |
| `HttpSessionEventPublisher`               | Necesario para que el control de sesiones concurrentes funcione.                                                     |
| `SessionInformation`                      | Datos de una sesión (último acceso, expirada).                                                                       |
| **Spring Session**                        | Guarda las sesiones en Redis/JDBC para compartirlas entre servidores.                                                |
| `server.servlet.session.timeout`          | Tiempo de vida de la sesión (ej. `30m`).                                                                             |
| `server.servlet.session.cookie.http-only` | Cookie inaccesible desde JavaScript.                                                                                 |
| `server.servlet.session.cookie.secure`    | Cookie solo por HTTPS.                                                                                               |
| `server.servlet.session.cookie.same-site` | `strict`, `lax` o `none`.                                                                                            |

### Ejemplo: API stateless (JWT)

```java
http.sessionManagement(s ->s.

sessionCreationPolicy(SessionCreationPolicy.STATELESS));
```

### Ejemplo: sesiones con límite

```java
http.sessionManagement(s ->s
    .

sessionFixation(f ->f.

changeSessionId())
    .

maximumSessions(1)                         // un solo dispositivo por usuario
    .

maxSessionsPreventsLogin(false)            // el nuevo login expulsa al anterior
    .

expiredUrl("/login?expired"));

@Bean
public HttpSessionEventPublisher httpSessionEventPublisher() {
  return new HttpSessionEventPublisher();
}
```

```properties
server.servlet.session.timeout=30m
server.servlet.session.cookie.http-only=true
server.servlet.session.cookie.secure=true
server.servlet.session.cookie.same-site=strict
```

### Sesión vs Token

|                           | Sesión (stateful)                  | JWT (stateless)                    |
|---------------------------|------------------------------------|------------------------------------|
| Dónde se guarda el estado | Servidor                           | Cliente (en el token)              |
| Escalabilidad             | Necesita sesión compartida (Redis) | Muy fácil                          |
| Revocar acceso            | Inmediato (borrar sesión)          | Difícil (hasta que expire)         |
| Ideal para                | Apps web clásicas con vistas       | APIs REST, móviles, microservicios |
| CSRF                      | Hay que protegerse                 | No aplica (si no usa cookies)      |

## 13. Login por formulario y logout

| Término                                   | Significado                                                 |
|-------------------------------------------|-------------------------------------------------------------|
| `formLogin`                               | Login con formulario HTML.                                  |
| `loginPage("/login")`                     | Página de login propia.                                     |
| `loginProcessingUrl("/login")`            | URL a la que se envía el formulario (`POST`).               |
| `usernameParameter` / `passwordParameter` | Nombre de los campos (por defecto `username` y `password`). |
| `defaultSuccessUrl("/home", true)`        | A dónde ir tras login (`true` = siempre).                   |
| `failureUrl("/login?error")`              | A dónde ir si falla.                                        |
| `successHandler` / `failureHandler`       | Lógica personalizada.                                       |
| `permitAll()`                             | La página de login debe ser pública.                        |
| `logout`                                  | Configuración del cierre de sesión.                         |
| `logoutUrl("/logout")`                    | URL de logout (por defecto `POST /logout`).                 |
| `logoutSuccessUrl("/login?logout")`       | Destino tras salir.                                         |
| `invalidateHttpSession(true)`             | Invalida la sesión.                                         |
| `deleteCookies("JSESSIONID")`             | Borra cookies.                                              |
| `clearAuthentication(true)`               | Limpia el contexto.                                         |
| `LogoutHandler`                           | Acciones al salir.                                          |
| `LogoutSuccessHandler`                    | Respuesta tras salir (ej. JSON en APIs).                    |
| `SavedRequest` / `RequestCache`           | Recuerda la URL que el usuario quería antes de ir a login.  |

### Ejemplo: configuración

```java
http
    .authorizeHttpRequests(a ->a
    .

requestMatchers("/login","/registro","/css/**").

permitAll()
        .

anyRequest().

authenticated())
    .

formLogin(form ->form
    .

loginPage("/login")
        .

loginProcessingUrl("/login")
        .

usernameParameter("email")
        .

passwordParameter("clave")
        .

defaultSuccessUrl("/dashboard",true)
        .

failureUrl("/login?error")
        .

permitAll())
    .

logout(logout ->logout
    .

logoutUrl("/logout")
        .

logoutSuccessUrl("/login?logout")
        .

invalidateHttpSession(true)
        .

deleteCookies("JSESSIONID")
        .

permitAll());
```

### Ejemplo: plantilla de login (Thymeleaf)

```html

<form th:action="@{/login}" method="post">
    <div th:if="${param.error}">Usuario o contraseña incorrectos</div>
    <div th:if="${param.logout}">Sesión cerrada</div>
    <input type="text" name="email" placeholder="Correo" required>
    <input type="password" name="clave" placeholder="Contraseña" required>
    <!-- Thymeleaf agrega el token CSRF automáticamente con th:action -->
    <button type="submit">Entrar</button>
</form>
```

```java

@Controller
public class LoginController {
  @GetMapping("/login")
  public String login() {
    return "login";
  }      // templates/login.html
}
```

### Ejemplo: logout con respuesta JSON (API)

```java
.logout(logout ->logout
    .

logoutUrl("/api/auth/logout")
    .

logoutSuccessHandler((req, res, auth) ->res.

setStatus(HttpServletResponse.SC_OK)))
```

## 14. HTTP Basic

| Término                                | Significado                                                                       |
|----------------------------------------|-----------------------------------------------------------------------------------|
| **HTTP Basic**                         | El cliente envía `usuario:contraseña` en **cada** petición, codificado en Base64. |
| `Authorization: Basic dXNlcjpwYXNz`    | Cabecera Basic (`base64("user:pass")`).                                           |
| `httpBasic(Customizer.withDefaults())` | Activa Basic.                                                                     |
| `BasicAuthenticationEntryPoint`        | Responde 401 con `WWW-Authenticate: Basic`.                                       |
| `WWW-Authenticate`                     | Cabecera con la que el servidor pide credenciales.                                |
| **Base64 ≠ cifrado**                   | Se decodifica trivialmente: **usar siempre con HTTPS**.                           |

### Ejemplo

```java
http.httpBasic(Customizer.withDefaults());
```

```bash
curl -u ana:1234 http://localhost:8080/api/usuarios
# equivale a:
curl -H "Authorization: Basic YW5hOjEyMzQ=" http://localhost:8080/api/usuarios
```

Útil para: pruebas rápidas, comunicación entre servicios internos, herramientas. No recomendable para usuarios finales.

## 15. JWT (JSON Web Token)

| Término                          | Significado                                                                           |
|----------------------------------|---------------------------------------------------------------------------------------|
| **JWT**                          | Token autocontenido y firmado con la identidad y datos (*claims*) del usuario.        |
| **Estructura**                   | `header.payload.signature` (tres partes en Base64URL separadas por puntos).           |
| **Header**                       | Algoritmo y tipo: `{"alg":"HS256","typ":"JWT"}`.                                      |
| **Payload (claims)**             | Datos del token: usuario, roles, expiración. **No cifrado**: cualquiera puede leerlo. |
| **Signature**                    | Firma que garantiza que no fue alterado.                                              |
| `sub` (subject)                  | A quién pertenece el token (usuario).                                                 |
| `iss` (issuer)                   | Quién lo emitió.                                                                      |
| `aud` (audience)                 | Para quién es.                                                                        |
| `exp` (expiration)               | Fecha de expiración (segundos desde 1970).                                            |
| `iat` (issued at)                | Fecha de emisión.                                                                     |
| `nbf` (not before)               | No válido antes de esta fecha.                                                        |
| `jti`                            | ID único del token (útil para listas negras).                                         |
| **Claim personalizado**          | Datos propios: `roles`, `userId`, `email`.                                            |
| **HS256**                        | Firma simétrica (HMAC + SHA-256): misma clave firma y valida.                         |
| **RS256**                        | Firma asimétrica (RSA): clave **privada** firma, **pública** valida.                  |
| **Secret / clave de firma**      | Debe ser larga (≥ 256 bits para HS256) y mantenerse en secreto.                       |
| **Access token**                 | Token de vida corta (5–30 min) para acceder a la API.                                 |
| **Refresh token**                | Token de vida larga para obtener nuevos access tokens.                                |
| **Rotación de refresh token**    | Emitir un refresh nuevo cada vez que se usa el anterior.                              |
| **Blacklist / revocación**       | Lista de tokens invalidados antes de expirar (JWT no se puede "borrar").              |
| `ExpiredJwtException`            | El token expiró.                                                                      |
| `MalformedJwtException`          | Token mal formado.                                                                    |
| `SignatureException`             | Firma inválida (manipulado o clave incorrecta).                                       |
| `UnsupportedJwtException`        | Tipo de JWT no soportado.                                                             |
| `JwtException`                   | Raíz de errores de JJWT.                                                              |
| `JwtDecoder` / `JwtEncoder`      | Interfaces de Spring (resource server) para validar/crear JWT.                        |
| `NimbusJwtDecoder`               | Implementación de `JwtDecoder`.                                                       |
| `JwtAuthenticationConverter`     | Convierte claims en authorities.                                                      |
| `JwtGrantedAuthoritiesConverter` | Mapea un claim (ej. `roles`) a authorities con prefijo.                               |
| `jwt.io`                         | Sitio para inspeccionar un JWT.                                                       |
| **Token opaco**                  | Token sin información, que el servidor valida consultando (introspección).            |

### Anatomía de un JWT

```
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbmEiLCJyb2xlcyI6WyJVU0VSIl0sImV4cCI6MTc2MDAwMDAwMH0.Vxk9...
└────── header ─────┘└────────────────── payload ──────────────────────────┘└─ firma ─┘
```

Payload decodificado:

```json
{
  "sub": "ana",
  "roles": [
    "ROLE_USER"
  ],
  "iat": 1759990000,
  "exp": 1760000000
}
```

> **Nunca guardes datos sensibles** (contraseñas, tarjetas) en el payload: solo está codificado, no cifrado.

### Flujo con JWT

```
1. POST /api/auth/login  {usuario, clave}
2. Servidor valida y responde  {"token": "eyJ..."}
3. Cliente guarda el token
4. GET /api/pedidos   Authorization: Bearer eyJ...
5. JwtFilter valida firma + expiración → establece el usuario en el SecurityContext
6. El controlador se ejecuta
```

### Ejemplo: propiedades

```properties
app.jwt.secret=${JWT_SECRET}        # mínimo 32 caracteres; NO subirlo al repositorio
app.jwt.expiration-ms=900000        # 15 minutos
app.jwt.refresh-expiration-ms=604800000   # 7 días
```

### Ejemplo: `JwtService` (JJWT 0.12.x)

```java

@Service
public class JwtService {

  @Value("${app.jwt.secret}")
  private String secret;

  @Value("${app.jwt.expiration-ms}")
  private long expirationMs;

  private SecretKey getKey() {
    return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
  }

  public String generarToken(UserDetails user) {
    return Jwts.builder()
        .subject(user.getUsername())
        .claim("roles", user.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority).toList())
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + expirationMs))
        .signWith(getKey())
        .compact();
  }

  public Claims extraerClaims(String token) {
    return Jwts.parser()
        .verifyWith(getKey())
        .build()
        .parseSignedClaims(token)       // valida firma y expiración
        .getPayload();
  }

  public String extraerUsername(String token) {
    return extraerClaims(token).getSubject();
  }

  public boolean esValido(String token, UserDetails user) {
    try {
      Claims c = extraerClaims(token);
      return c.getSubject().equals(user.getUsername())
          && c.getExpiration().after(new Date());
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }
}
```

### Ejemplo: filtro JWT

```java

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final UserDetailsService userDetailsService;

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain chain) throws ServletException, IOException {

    String header = request.getHeader(HttpHeaders.AUTHORIZATION);

    // Sin token: continuar sin autenticar (las reglas decidirán si se permite)
    if (header == null || !header.startsWith("Bearer ")) {
      chain.doFilter(request, response);
      return;
    }

    String token = header.substring(7);
    try {
      String username = jwtService.extraerUsername(token);

      if (username != null
          && SecurityContextHolder.getContext().getAuthentication() == null) {

        UserDetails user = userDetailsService.loadUserByUsername(username);

        if (jwtService.esValido(token, user)) {
          UsernamePasswordAuthenticationToken auth =
              new UsernamePasswordAuthenticationToken(
                  user, null, user.getAuthorities());
          auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(auth);
        }
      }
    } catch (JwtException e) {
      // Token inválido/expirado: no autenticar; Security responderá 401
    }
    chain.doFilter(request, response);       // SIEMPRE continuar la cadena
  }
}
```

### Ejemplo: registrar el filtro en la cadena

```java
http
    .csrf(csrf ->csrf.

disable())
    .

sessionManagement(s ->s.

sessionCreationPolicy(SessionCreationPolicy.STATELESS))
    .

addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter .class);
```

### Ejemplo: refresh token

```java

@PostMapping("/api/auth/refresh")
public ResponseEntity<TokenResponse> refresh(@RequestBody RefreshRequest req) {
  String username = jwtService.extraerUsername(req.refreshToken());   // valida el refresh
  UserDetails user = userDetailsService.loadUserByUsername(username);
  return ResponseEntity.ok(new TokenResponse(jwtService.generarToken(user)));
}
```

### Ejemplo: JWT con el soporte nativo de Spring (Resource Server)

```xml

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
</dependency>
```

```java

@Bean
public SecurityFilterChain chain(HttpSecurity http) throws Exception {
  http.authorizeHttpRequests(a -> a.anyRequest().authenticated())
      .oauth2ResourceServer(o -> o.jwt(j -> j.jwtAuthenticationConverter(converter())));
  return http.build();
}

@Bean
public JwtDecoder jwtDecoder() {
  SecretKey key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
  return NimbusJwtDecoder.withSecretKey(key).build();
}

// Convertir claim "roles" en authorities
private JwtAuthenticationConverter converter() {
  JwtGrantedAuthoritiesConverter gac = new JwtGrantedAuthoritiesConverter();
  gac.setAuthoritiesClaimName("roles");
  gac.setAuthorityPrefix("");                  // los roles ya traen ROLE_
  JwtAuthenticationConverter conv = new JwtAuthenticationConverter();
  conv.setJwtGrantedAuthoritiesConverter(gac);
  return conv;
}
```

```properties
# Si el token lo emite un servidor externo (Keycloak, Auth0...)
spring.security.oauth2.resourceserver.jwt.issuer-uri=https://mi-keycloak/realms/mi-realm
```

### Dónde guardar el token en el cliente

| Lugar                                 | Ventaja             | Riesgo                                     |
|---------------------------------------|---------------------|--------------------------------------------|
| `localStorage`                        | Simple              | Vulnerable a XSS (cualquier script lo lee) |
| Cookie `HttpOnly + Secure + SameSite` | JS no puede leerla  | Requiere protección CSRF                   |
| Memoria (variable JS)                 | Más segura ante XSS | Se pierde al recargar                      |

## 16. OAuth2 y OpenID Connect

| Término                               | Significado                                                                                                |
|---------------------------------------|------------------------------------------------------------------------------------------------------------|
| **OAuth 2.0**                         | Protocolo de **autorización delegada**: una app accede a recursos de un usuario sin conocer su contraseña. |
| **OpenID Connect (OIDC)**             | Capa de **autenticación** sobre OAuth2 (añade el `id_token` con la identidad).                             |
| **Resource Owner**                    | El usuario dueño de los datos.                                                                             |
| **Client**                            | La aplicación que quiere acceder (tu app).                                                                 |
| **Authorization Server**              | Quien autentica y emite tokens (Keycloak, Auth0, Google, Okta).                                            |
| **Resource Server**                   | La API que valida tokens y protege recursos.                                                               |
| **Scope**                             | Permiso solicitado (`openid`, `profile`, `email`, `read:orders`).                                          |
| **Authorization Code**                | Flujo estándar para apps web (el más seguro).                                                              |
| **Authorization Code + PKCE**         | Variante para SPAs y apps móviles (sin secreto de cliente).                                                |
| **PKCE**                              | Proof Key for Code Exchange: protege el flujo en clientes públicos.                                        |
| **Client Credentials**                | Flujo máquina-a-máquina (sin usuario).                                                                     |
| **Password grant**                    | Usuario/clave directo al cliente. **Obsoleto, no usar.**                                                   |
| **Implicit**                          | **Obsoleto**, reemplazado por Code + PKCE.                                                                 |
| **Refresh Token**                     | Obtiene nuevos access tokens.                                                                              |
| **Access Token**                      | Autoriza llamadas a la API.                                                                                |
| **ID Token**                          | JWT de OIDC con la identidad del usuario.                                                                  |
| `client-id` / `client-secret`         | Credenciales de tu app registrada en el proveedor.                                                         |
| `redirect-uri`                        | URL donde el proveedor devuelve al usuario tras el login.                                                  |
| `issuer-uri`                          | URL base del proveedor (para descubrir su configuración).                                                  |
| `.well-known/openid-configuration`    | Documento de descubrimiento del proveedor.                                                                 |
| **JWKS** (`jwks-uri`)                 | Endpoint con las claves públicas para validar firmas.                                                      |
| **Introspección**                     | El resource server pregunta al auth server si un token opaco es válido.                                    |
| `oauth2Login`                         | Habilita "Iniciar sesión con X" en tu app.                                                                 |
| `oauth2Client`                        | Tu app consume APIs de terceros con token.                                                                 |
| `oauth2ResourceServer`                | Tu API valida tokens.                                                                                      |
| `ClientRegistration`                  | Configuración de un proveedor (Google, GitHub...).                                                         |
| `ClientRegistrationRepository`        | Almacén de proveedores.                                                                                    |
| `OAuth2User` / `OidcUser`             | Usuario autenticado vía OAuth2 / OIDC.                                                                     |
| `OAuth2AuthorizedClientService`       | Guarda los tokens de acceso del cliente.                                                                   |
| `@RegisteredOAuth2AuthorizedClient`   | Inyecta el cliente autorizado con su token.                                                                |
| `OAuth2UserService`                   | Personaliza cómo se carga el usuario (guardarlo en BD, asignar roles).                                     |
| **Keycloak**                          | Servidor de identidad open source muy usado.                                                               |
| **Auth0 / Okta / Azure AD / Cognito** | Proveedores de identidad en la nube.                                                                       |

### Los 3 roles en una arquitectura típica

```
Usuario → [Tu app / Frontend (Client)] → login → [Keycloak/Google (Authorization Server)]
                       │                                      │ emite access token
                       └── Authorization: Bearer ... ──▶ [Tu API (Resource Server)]
```

### Ejemplo: Login con Google/GitHub

```xml

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-client</artifactId>
</dependency>
```

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: ${GOOGLE_CLIENT_ID}
            client-secret: ${GOOGLE_CLIENT_SECRET}
            scope: openid, profile, email
          github:
            client-id: ${GITHUB_CLIENT_ID}
            client-secret: ${GITHUB_CLIENT_SECRET}
```

```java
http.authorizeHttpRequests(a ->a.

requestMatchers("/","/login/**").

permitAll()
                                 .

anyRequest().

authenticated())
    .

oauth2Login(oauth ->oauth
    .

loginPage("/login")
        .

defaultSuccessUrl("/home",true));

@GetMapping("/home")
public String home(@AuthenticationPrincipal OidcUser user, Model model) {
  model.addAttribute("nombre", user.getFullName());
  model.addAttribute("email", user.getEmail());
  return "home";
}
```

### Ejemplo: API protegida por Keycloak (Resource Server)

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://localhost:8180/realms/mi-realm
```

```java
http.authorizeHttpRequests(a ->a
    .

requestMatchers("/api/admin/**").

hasRole("ADMIN")
        .

anyRequest().

authenticated())
    .

oauth2ResourceServer(o ->o.

jwt(Customizer.withDefaults()));

// Los roles de Keycloak vienen en realm_access.roles → hay que mapearlos
@Bean
public JwtAuthenticationConverter keycloakConverter() {
  JwtAuthenticationConverter conv = new JwtAuthenticationConverter();
  conv.setJwtGrantedAuthoritiesConverter(jwt -> {
    Map<String, Object> realm = jwt.getClaim("realm_access");
    List<String> roles = realm == null ? List.of() : (List<String>) realm.get("roles");
    return roles.stream()
        .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
        .collect(Collectors.toList());
  });
  return conv;
}
```

### Ejemplo: usar `@AuthenticationPrincipal Jwt`

```java

@GetMapping("/api/me")
public Map<String, Object> me(@AuthenticationPrincipal Jwt jwt) {
  return Map.of("usuario", jwt.getSubject(),
      "email", jwt.getClaimAsString("email"));
}
```

### Ejemplo: llamar a una API externa con client credentials

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          mi-servicio:
            provider: keycloak
            client-id: backend
            client-secret: ${SECRET}
            authorization-grant-type: client_credentials
        provider:
          keycloak:
            token-uri: http://localhost:8180/realms/mi-realm/protocol/openid-connect/token
```

## 17. CSRF

| Término                                                   | Significado                                                                                                        |
|-----------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------|
| **CSRF** (Cross-Site Request Forgery)                     | Un sitio malicioso hace que tu navegador envíe una petición a otro sitio donde ya tienes sesión (con tus cookies). |
| **Token CSRF**                                            | Valor secreto que el servidor exige en peticiones que modifican datos (`POST`, `PUT`, `DELETE`, `PATCH`).          |
| `CsrfFilter`                                              | Filtro que valida el token.                                                                                        |
| `CsrfToken`                                               | Objeto con el token (`getToken()`, `getHeaderName()`, `getParameterName()`).                                       |
| `CsrfTokenRepository`                                     | Dónde guardar el token (sesión o cookie).                                                                          |
| `HttpSessionCsrfTokenRepository`                          | En la sesión (por defecto).                                                                                        |
| `CookieCsrfTokenRepository`                               | En una cookie (`XSRF-TOKEN`); típico para SPAs.                                                                    |
| `withHttpOnlyFalse()`                                     | Permite que JavaScript lea la cookie para reenviarla.                                                              |
| `_csrf`                                                   | Nombre del parámetro del token.                                                                                    |
| `X-CSRF-TOKEN` / `X-XSRF-TOKEN`                           | Cabeceras donde el cliente envía el token.                                                                         |
| `ignoringRequestMatchers`                                 | Excluir rutas de la validación CSRF.                                                                               |
| `csrf.disable()`                                          | Desactiva la protección.                                                                                           |
| `SameSite` (cookie)                                       | Atributo que evita enviar la cookie en peticiones cross-site.                                                      |
| **Métodos seguros**                                       | `GET`, `HEAD`, `OPTIONS`, `TRACE` no requieren token.                                                              |
| `InvalidCsrfTokenException` / `MissingCsrfTokenException` | Errores: token inválido / ausente (→ 403).                                                                         |

### ¿Cuándo activar o desactivar?

| Escenario                                              | CSRF                                                            |
|--------------------------------------------------------|-----------------------------------------------------------------|
| App web con sesión + formularios (cookies)             | **Activado** (por defecto)                                      |
| API REST stateless con JWT en cabecera `Authorization` | Se suele **desactivar** (no hay cookie automática que explotar) |
| SPA con JWT guardado en cookie                         | **Activado**                                                    |

### Ejemplos

```java
// Desactivar (API stateless con Bearer token)
http.csrf(csrf ->csrf.

disable());

// Activar con cookie legible por JS (SPA Angular/React)
    http.

csrf(csrf ->csrf
    .

csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()));

// Excluir solo algunas rutas
    http.

csrf(csrf ->csrf.

ignoringRequestMatchers("/api/webhooks/**","/h2-console/**"));
```

```html
<!-- Thymeleaf lo agrega solo con th:action. En HTML plano: -->
<form method="post" action="/transferir">
    <input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}"/>
</form>
```

```javascript
// SPA: enviar el token en cada petición que modifica datos
fetch("/api/pedidos", {
    method: "POST",
    headers: {"X-XSRF-TOKEN": getCookie("XSRF-TOKEN"), "Content-Type": "application/json"},
    credentials: "include",
    body: JSON.stringify(pedido)
});
```

## 18. CORS con Security

| Término                            | Significado                                                                                                      |
|------------------------------------|------------------------------------------------------------------------------------------------------------------|
| **CORS**                           | El navegador bloquea peticiones desde otro origen (dominio/puerto/protocolo) salvo que el servidor las autorice. |
| **Origen**                         | Combinación `protocolo + dominio + puerto` (`http://localhost:3000`).                                            |
| **Preflight**                      | Petición `OPTIONS` automática que el navegador envía antes de la real.                                           |
| `Access-Control-Allow-Origin`      | Orígenes permitidos.                                                                                             |
| `Access-Control-Allow-Methods`     | Métodos permitidos.                                                                                              |
| `Access-Control-Allow-Headers`     | Cabeceras permitidas.                                                                                            |
| `Access-Control-Allow-Credentials` | Permite cookies/credenciales (no se puede usar con `*`).                                                         |
| `Access-Control-Max-Age`           | Cuánto cachea el navegador el preflight.                                                                         |
| `CorsConfiguration`                | Reglas CORS.                                                                                                     |
| `CorsConfigurationSource`          | Bean que las provee.                                                                                             |
| `UrlBasedCorsConfigurationSource`  | Asocia reglas a rutas.                                                                                           |
| `http.cors(...)`                   | Activa CORS **dentro** de Security (necesario para que preflight no sea bloqueado con 401/403).                  |
| `@CrossOrigin`                     | Alternativa en el controlador (solo funciona si `http.cors()` está activo).                                      |

> **CORS no es seguridad del servidor**: lo aplica el navegador. Curl o Postman lo ignoran.

### Ejemplo

```java
http.cors(Customizer.withDefaults());       // usa el bean corsConfigurationSource

@Bean
public CorsConfigurationSource corsConfigurationSource() {
  CorsConfiguration config = new CorsConfiguration();
  config.setAllowedOrigins(List.of("http://localhost:3000", "https://miapp.com"));
  config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
  config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
  config.setExposedHeaders(List.of("Location"));
  config.setAllowCredentials(true);
  config.setMaxAge(3600L);

  UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
  source.registerCorsConfiguration("/api/**", config);
  return source;
}
```

Permitir los preflight sin autenticar (si hace falta):

```java
.requestMatchers(HttpMethod.OPTIONS, "/**").

permitAll()
```

## 19. Cabeceras de seguridad y HTTPS

| Término                            | Significado                                                                           |
|------------------------------------|---------------------------------------------------------------------------------------|
| `headers`                          | Configura cabeceras de seguridad (Spring añade varias por defecto).                   |
| `X-Content-Type-Options: nosniff`  | Evita que el navegador "adivine" el tipo de contenido.                                |
| `X-Frame-Options`                  | Controla si la página puede mostrarse en un `<iframe>` (anti clickjacking).           |
| `frameOptions().sameOrigin()`      | Permite iframes del mismo origen (necesario para la consola H2).                      |
| `Content-Security-Policy (CSP)`    | Define qué fuentes de scripts/estilos/imágenes son válidas (anti XSS).                |
| `Strict-Transport-Security (HSTS)` | Obliga al navegador a usar siempre HTTPS.                                             |
| `Referrer-Policy`                  | Controla qué información de origen se envía.                                          |
| `Permissions-Policy`               | Restringe funciones del navegador (cámara, geolocalización).                          |
| `Cache-Control`                    | Spring desactiva la caché en respuestas protegidas.                                   |
| `requiresChannel`                  | Obliga a usar HTTPS (`requiresSecure()`).                                             |
| `server.ssl.*`                     | Configura SSL/HTTPS en Spring Boot.                                                   |
| `server.ssl.key-store`             | Archivo con el certificado (`.p12`/`.jks`).                                           |
| `server.ssl.key-store-password`    | Contraseña del keystore.                                                              |
| **Keystore**                       | Archivo que guarda claves y certificados.                                             |
| **Certificado autofirmado**        | Para desarrollo (el navegador avisará).                                               |
| **TLS terminado en proxy**         | Un proxy (Nginx) maneja HTTPS; configura `server.forward-headers-strategy=framework`. |
| `server.forward-headers-strategy`  | Respetar `X-Forwarded-*` detrás de un proxy.                                          |

### Ejemplos

```java
http.headers(h ->h
    .

frameOptions(f ->f.

sameOrigin())                       // H2 console
    .

contentSecurityPolicy(csp ->csp.

policyDirectives(
        "default-src 'self'; script-src 'self'; img-src 'self' data:"))
    .

httpStrictTransportSecurity(hsts ->hsts
    .

includeSubDomains(true).

maxAgeInSeconds(31536000))
    .

referrerPolicy(r ->r.

policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.SAME_ORIGIN)));

// Forzar HTTPS
    http.

requiresChannel(c ->c.

anyRequest().

requiresSecure());
```

```properties
server.port=8443
server.ssl.enabled=true
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=${KEYSTORE_PASSWORD}
server.ssl.key-store-type=PKCS12
server.ssl.key-alias=miapp
```

```bash
# Generar un certificado autofirmado para desarrollo
keytool -genkeypair -alias miapp -keyalg RSA -keysize 2048 \
        -storetype PKCS12 -keystore keystore.p12 -validity 365
```

## 20. Manejo de errores de seguridad

| Término                                    | Significado                                                                                          |
|--------------------------------------------|------------------------------------------------------------------------------------------------------|
| `AuthenticationEntryPoint`                 | Se ejecuta cuando alguien **no autenticado** accede a algo protegido → normalmente responde **401**. |
| `AccessDeniedHandler`                      | Se ejecuta cuando alguien **autenticado sin permiso** accede → **403**.                              |
| `ExceptionTranslationFilter`               | Decide a cuál de los dos llamar.                                                                     |
| `AccessDeniedException`                    | Excepción de autorización.                                                                           |
| `AuthorizationDeniedException`             | Versión nueva (Security 6.3+), subclase de `AccessDeniedException`.                                  |
| `exceptionHandling`                        | Configura ambos handlers.                                                                            |
| `HttpStatusEntryPoint`                     | Responde un código HTTP fijo.                                                                        |
| `Http403ForbiddenEntryPoint`               | Responde 403 (en vez de redirigir a login).                                                          |
| `LoginUrlAuthenticationEntryPoint`         | Redirige a la página de login (apps con formulario).                                                 |
| `@ExceptionHandler` en `@ControllerAdvice` | **No** captura errores de filtros, solo de controladores (y de `@PreAuthorize`).                     |
| `HandlerExceptionResolver`                 | Mecanismo para delegar errores de filtros en tu `@ControllerAdvice`.                                 |

> **Importante:** las excepciones lanzadas en los **filtros** (como el filtro JWT) ocurren antes de Spring MVC, así que
`@ControllerAdvice` no las ve. Para responder JSON hay que usar `AuthenticationEntryPoint` / `AccessDeniedHandler`.

### Ejemplo: respuestas JSON para 401 y 403

```java

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
  private final ObjectMapper mapper = new ObjectMapper();

  @Override
  public void commence(HttpServletRequest req, HttpServletResponse res,
                       AuthenticationException ex) throws IOException {
    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);                 // 401
    res.setContentType(MediaType.APPLICATION_JSON_VALUE);
    mapper.writeValue(res.getOutputStream(), Map.of(
        "status", 401,
        "error", "No autenticado",
        "mensaje", "Token ausente, inválido o expirado",
        "ruta", req.getRequestURI()));
  }
}

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
  private final ObjectMapper mapper = new ObjectMapper();

  @Override
  public void handle(HttpServletRequest req, HttpServletResponse res,
                     AccessDeniedException ex) throws IOException {
    res.setStatus(HttpServletResponse.SC_FORBIDDEN);                    // 403
    res.setContentType(MediaType.APPLICATION_JSON_VALUE);
    mapper.writeValue(res.getOutputStream(), Map.of(
        "status", 403,
        "error", "Acceso denegado",
        "mensaje", "No tienes permiso para este recurso",
        "ruta", req.getRequestURI()));
  }
}

// Registro
http.

exceptionHandling(e ->e
    .

authenticationEntryPoint(entryPoint)
        .

accessDeniedHandler(accessDeniedHandler));
```

### Ejemplo: manejar errores de `@PreAuthorize` en `@RestControllerAdvice`

```java

@RestControllerAdvice
public class SecurityExceptionHandler {

  @ExceptionHandler(AccessDeniedException.class)       // lanzada por @PreAuthorize
  public ResponseEntity<ApiError> denegado(AccessDeniedException ex) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(new ApiError(403, "No tienes permiso"));
  }

  @ExceptionHandler(BadCredentialsException.class)     // lanzada en el login del controller
  public ResponseEntity<ApiError> credenciales(BadCredentialsException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(new ApiError(401, "Usuario o contraseña incorrectos"));
  }

  @ExceptionHandler({DisabledException.class, LockedException.class})
  public ResponseEntity<ApiError> cuenta(AuthenticationException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(new ApiError(401, "Cuenta deshabilitada o bloqueada"));
  }
}
```

> Si en tu `@RestControllerAdvice` hay un `@ExceptionHandler(Exception.class)` genérico, puede **tragarse** los
`AccessDeniedException` y devolver 500 en lugar de 403. Declara el handler específico.

### Ejemplo: redirigir a login en apps web y 401 en `/api`

```java
// En la cadena de la web: formLogin ya redirige a /login.
// En la cadena de la API: devolver 401 en vez de redirigir
http.securityMatcher("/api/**")
    .

exceptionHandling(e ->e.

authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));
```

## 21. Remember-me, bloqueo de cuentas y otras funciones

| Término                                    | Significado                                                                   |
|--------------------------------------------|-------------------------------------------------------------------------------|
| **Remember-me**                            | Mantener la sesión tras cerrar el navegador mediante una cookie persistente.  |
| `rememberMe`                               | Configuración en `HttpSecurity`.                                              |
| `tokenValiditySeconds`                     | Duración de la cookie.                                                        |
| `key`                                      | Clave secreta para firmar la cookie.                                          |
| `remember-me` (parámetro)                  | Checkbox del formulario.                                                      |
| `PersistentTokenRepository`                | Guarda tokens persistentes en BD (más seguro).                                |
| `JdbcTokenRepositoryImpl`                  | Implementación en BD.                                                         |
| **Bloqueo de cuentas**                     | Bloquear tras N intentos fallidos (anti fuerza bruta).                        |
| `AuthenticationFailureBadCredentialsEvent` | Evento de login fallido.                                                      |
| `AuthenticationSuccessEvent`               | Evento de login correcto.                                                     |
| `@EventListener`                           | Escucha eventos de seguridad (para auditoría o bloqueo).                      |
| `AbstractAuthenticationEvent`              | Base de eventos de autenticación.                                             |
| **Rate limiting**                          | Limitar peticiones por IP/usuario (Bucket4j, Resilience4j).                   |
| **Impersonación (`SwitchUserFilter`)**     | Un admin actúa temporalmente como otro usuario.                               |
| **Anonymous**                              | Usuario no autenticado; `anonymousUser` con rol `ROLE_ANONYMOUS`.             |
| **Run-as**                                 | Ejecutar con otra identidad temporalmente.                                    |
| **ACL (spring-security-acl)**              | Permisos por objeto individual (tabla de permisos).                           |
| **Auditoría**                              | Registrar quién hizo qué (ver `AuditorAware` en el glosario JPA).             |
| **MFA (2FA/TOTP)**                         | Segundo factor (código del móvil). Spring Security 6.4+ incluye soporte base. |
| **One-Time Token (OTT) login**             | Login con enlace mágico de un solo uso (Security 6.4+).                       |
| **Passkeys / WebAuthn**                    | Autenticación sin contraseña con biometría/llaves (Security 6.4+).            |

### Ejemplo: remember-me

```java
http.rememberMe(r ->r
    .

key("clave-secreta-larga")
    .

tokenValiditySeconds(14*24*60*60)        // 14 días
    .

userDetailsService(userDetailsService));
```

```html
<input type="checkbox" name="remember-me"> Recordarme
```

### Ejemplo: bloqueo por intentos fallidos

```java

@Component
@RequiredArgsConstructor
public class LoginAttemptListener {

  private final LoginAttemptService attempts;

  @EventListener
  public void onFailure(AuthenticationFailureBadCredentialsEvent event) {
    attempts.registrarFallo(event.getAuthentication().getName());
  }

  @EventListener
  public void onSuccess(AuthenticationSuccessEvent event) {
    attempts.reiniciar(event.getAuthentication().getName());
  }
}

@Service
public class LoginAttemptService {
  private static final int MAX_INTENTOS = 5;
  private final UsuarioRepository repo;

  // ...
  public void registrarFallo(String username) {
    repo.findByUsername(username).ifPresent(u -> {
      u.setIntentosFallidos(u.getIntentosFallidos() + 1);
      if (u.getIntentosFallidos() >= MAX_INTENTOS) {
        u.setBloqueado(true);           // luego isAccountNonLocked() = false → LockedException
      }
      repo.save(u);
    });
  }

  public void reiniciar(String username) {
    repo.findByUsername(username).ifPresent(u -> {
      u.setIntentosFallidos(0);
      repo.save(u);
    });
  }
}
```

### Ejemplo: rate limiting simple con filtro (Bucket4j)

```java

@Component
public class RateLimitFilter extends OncePerRequestFilter {
  private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

  @Override
  protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res,
                                  FilterChain chain) throws ServletException, IOException {
    Bucket bucket = buckets.computeIfAbsent(req.getRemoteAddr(),
        ip -> Bucket.builder()
            .addLimit(Bandwidth.simple(100, Duration.ofMinutes(1)))
            .build());
    if (bucket.tryConsume(1)) {
      chain.doFilter(req, res);
    } else {
      res.setStatus(429);                                  // Too Many Requests
    }
  }
}
```

## 22. Testing de seguridad

Requiere `spring-security-test`.

| Término                                         | Significado                                                                                    |
|-------------------------------------------------|------------------------------------------------------------------------------------------------|
| `@WithMockUser`                                 | Simula un usuario autenticado (nombre, roles, authorities).                                    |
| `@WithAnonymousUser`                            | Simula un usuario anónimo.                                                                     |
| `@WithUserDetails("ana")`                       | Carga el usuario real desde tu `UserDetailsService`.                                           |
| `@WithSecurityContext`                          | Crea un contexto personalizado.                                                                |
| `SecurityMockMvcRequestPostProcessors`          | Métodos estáticos para `MockMvc`: `user()`, `csrf()`, `jwt()`, `httpBasic()`, `oauth2Login()`. |
| `with(csrf())`                                  | Añade un token CSRF válido (necesario en POST si CSRF está activo).                            |
| `with(user("ana").roles("ADMIN"))`              | Usuario simulado en una petición concreta.                                                     |
| `with(jwt())`                                   | JWT simulado (resource server).                                                                |
| `springSecurity()`                              | Configurador para `MockMvc` manual.                                                            |
| `@AutoConfigureMockMvc`                         | Configura MockMvc con seguridad en `@SpringBootTest`.                                          |
| `@WebMvcTest` + `@Import(SecurityConfig.class)` | Test de controlador incluyendo tu configuración de seguridad.                                  |
| `status().isUnauthorized()`                     | Espera 401.                                                                                    |
| `status().isForbidden()`                        | Espera 403.                                                                                    |

### Ejemplos

```java

@WebMvcTest(ProductoController.class)
@Import(SecurityConfig.class)           // para que use tus reglas reales
class ProductoControllerSecurityTest {

  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private ProductoService service;
  @MockBean
  private JwtService jwtService;                  // dependencias de tu filtro
  @MockBean
  private UserDetailsService userDetailsService;

  @Test
  void sinAutenticar_devuelve401() throws Exception {
    mockMvc.perform(get("/api/pedidos"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockUser(username = "ana", roles = "USER")
  void usuario_puedeListar() throws Exception {
    mockMvc.perform(get("/api/pedidos"))
        .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(roles = "USER")
  void usuario_noPuedeBorrar_devuelve403() throws Exception {
    mockMvc.perform(delete("/api/productos/1").with(csrf()))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void admin_puedeBorrar() throws Exception {
    mockMvc.perform(delete("/api/productos/1").with(csrf()))
        .andExpect(status().isNoContent());
  }

  @Test
  void conUsuarioEnLaPeticion() throws Exception {
    mockMvc.perform(get("/api/admin/usuarios")
            .with(user("root").roles("ADMIN")))
        .andExpect(status().isOk());
  }

  @Test
  void conHttpBasic() throws Exception {
    mockMvc.perform(get("/api/usuarios").with(httpBasic("ana", "1234")))
        .andExpect(status().isOk());
  }

  @Test
  void conJwtSimulado() throws Exception {
    mockMvc.perform(get("/api/me")
            .with(jwt().jwt(j -> j.subject("ana").claim("email", "ana@mail.com"))
                .authorities(new SimpleGrantedAuthority("ROLE_USER"))))
        .andExpect(status().isOk());
  }
}
```

### Ejemplo: test de método protegido

```java

@SpringBootTest
class PedidoServiceSecurityTest {

  @Autowired
  private PedidoService service;

  @Test
  @WithMockUser(roles = "USER")
  void usuarioNoPuedeEliminarTodos() {
    assertThrows(AccessDeniedException.class, () -> service.eliminarTodos());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void adminPuedeEliminarTodos() {
    assertDoesNotThrow(() -> service.eliminarTodos());
  }
}
```

### Ejemplo: test de login real con JWT

```java

@SpringBootTest
@AutoConfigureMockMvc
class AuthIntegrationTest {
  @Autowired
  private MockMvc mockMvc;

  @Test
  void loginDevuelveToken() throws Exception {
    mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\":\"ana\",\"password\":\"1234\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").isNotEmpty());
  }

  @Test
  void credencialesIncorrectas_401() throws Exception {
    mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\":\"ana\",\"password\":\"mala\"}"))
        .andExpect(status().isUnauthorized());
  }
}
```

## 23. Excepciones y errores comunes

| Problema / Excepción                                   | Causa habitual                                                                                                | Solución                                                                                   |
|--------------------------------------------------------|---------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------|
| **Todos los endpoints piden login**                    | Se añadió el starter sin configurar.                                                                          | Crear `SecurityFilterChain` con tus reglas.                                                |
| **403 en POST/PUT/DELETE con sesión**                  | CSRF activo y falta el token.                                                                                 | Enviar token CSRF o desactivarlo en APIs stateless.                                        |
| **401 en la petición de preflight (OPTIONS)**          | CORS no integrado en Security.                                                                                | `http.cors(...)` + `CorsConfigurationSource`.                                              |
| **403 en vez de 401 sin token**                        | No hay `AuthenticationEntryPoint` y con `httpBasic`/`formLogin` ausentes se usa `Http403ForbiddenEntryPoint`. | Configurar `exceptionHandling().authenticationEntryPoint(...)`.                            |
| `BadCredentialsException`                              | Usuario/clave incorrectos.                                                                                    | Capturar y responder 401.                                                                  |
| `UsernameNotFoundException`                            | Usuario no existe.                                                                                            | Se oculta como `BadCredentialsException` por defecto.                                      |
| `DisabledException` / `LockedException`                | Cuenta deshabilitada/bloqueada.                                                                               | Revisar `enabled`/`accountNonLocked`.                                                      |
| `AccessDeniedException`                                | Autenticado sin permiso.                                                                                      | 403; revisar roles.                                                                        |
| `There is no PasswordEncoder mapped for the id "null"` | Contraseña sin prefijo `{bcrypt}` y sin bean `PasswordEncoder`.                                               | Declarar bean `BCryptPasswordEncoder`.                                                     |
| `Encoded password does not look like BCrypt`           | La contraseña en BD está en texto plano o con otro formato.                                                   | Guardar siempre `encoder.encode(...)`.                                                     |
| **`hasRole("ADMIN")` no funciona**                     | Authority guardada como `ADMIN` en vez de `ROLE_ADMIN`.                                                       | Guardar con `ROLE_` o usar `hasAuthority("ADMIN")`.                                        |
| **Reglas ignoradas**                                   | `anyRequest()` puesto antes que otras reglas.                                                                 | Ordenar de lo específico a lo general.                                                     |
| **Rutas no coinciden**                                 | Falta `/**`, o el `context-path`, o la barra final.                                                           | Revisar patrón.                                                                            |
| `ExpiredJwtException`                                  | Token expirado.                                                                                               | Usar refresh token / volver a hacer login.                                                 |
| `SignatureException`                                   | Firma inválida o clave distinta.                                                                              | Verificar el secret; no cambiar la clave entre emisión y validación.                       |
| `WeakKeyException`                                     | Secret demasiado corto para HS256.                                                                            | Usar ≥ 32 bytes.                                                                           |
| `MalformedJwtException`                                | Token mal formado / falta `Bearer `.                                                                          | Revisar cabecera `Authorization`.                                                          |
| **`@PreAuthorize` no se ejecuta**                      | Falta `@EnableMethodSecurity`; o llamada interna (self-invocation); o método no público.                      | Activar anotación; llamar desde otro bean.                                                 |
| **`SecurityContext` vacío en `@Async`**                | El contexto es `ThreadLocal`.                                                                                 | `DelegatingSecurityContextExecutor` o `MODE_INHERITABLETHREADLOCAL`.                       |
| **Usuario `anonymousUser`**                            | No se autenticó la petición.                                                                                  | Revisar el filtro JWT / orden de filtros.                                                  |
| **Consola H2 no carga (403/pantalla en blanco)**       | CSRF y `X-Frame-Options` la bloquean.                                                                         | `csrf.ignoringRequestMatchers("/h2-console/**")` y `frameOptions().sameOrigin()`.          |
| **Login por formulario en bucle de redirecciones**     | `/login` no está en `permitAll()`.                                                                            | Permitir login.                                                                            |
| **Swagger devuelve 401/403**                           | Rutas no permitidas.                                                                                          | `permitAll()` para `/swagger-ui/**` y `/v3/api-docs/**`.                                   |
| **Filtro JWT se ejecuta dos veces**                    | Declarado como `@Component` y también `addFilterBefore` (se registra como filtro de servlet además).          | Registrar `FilterRegistrationBean` con `setEnabled(false)` o no anotarlo con `@Component`. |
| **`@ControllerAdvice` no captura errores del filtro**  | Los filtros corren antes de MVC.                                                                              | Usar `AuthenticationEntryPoint`/`AccessDeniedHandler` o `HandlerExceptionResolver`.        |
| `StackOverflowError` al serializar `UserDetails`       | Relaciones bidireccionales en la entidad.                                                                     | Usar DTOs.                                                                                 |
| `LazyInitializationException` en `loadUserByUsername`  | Roles `LAZY` fuera de transacción.                                                                            | `@Transactional`, `EAGER` o `JOIN FETCH`.                                                  |
| `NoSuchBeanDefinitionException: AuthenticationManager` | No se expuso el bean.                                                                                         | Declarar bean con `AuthenticationConfiguration`.                                           |
| `Cannot invoke ... getAuthentication() is null`        | No hay autenticación en el contexto.                                                                          | Verificar que la ruta esté protegida o comprobar `null`.                                   |

### Ejemplo: configuración para consola H2

```java
http.csrf(csrf ->csrf.

ignoringRequestMatchers("/h2-console/**"))
    .

headers(h ->h.

frameOptions(f ->f.

sameOrigin()))
    .

authorizeHttpRequests(a ->a.

requestMatchers("/h2-console/**").

permitAll() /* solo en dev */);
```

### Ejemplo: evitar que el filtro JWT se registre dos veces

```java

@Bean
public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilterRegistration(JwtAuthenticationFilter f) {
  FilterRegistrationBean<JwtAuthenticationFilter> reg = new FilterRegistrationBean<>(f);
  reg.setEnabled(false);          // solo lo gestiona Spring Security
  return reg;
}
```

### Ejemplo: depurar

```properties
logging.level.org.springframework.security=DEBUG
logging.level.org.springframework.security.web.FilterChainProxy=TRACE
```

## 24. Ejemplo completo: API con JWT

Estructura:

```
com.empresa.app
├── config/
│   └── SecurityConfig.java
├── security/
│   ├── JwtService.java
│   ├── JwtAuthenticationFilter.java
│   ├── JwtAuthenticationEntryPoint.java
│   ├── CustomAccessDeniedHandler.java
│   └── CustomUserDetailsService.java
├── controller/
│   └── AuthController.java
├── model/        (Usuario, Rol)
└── repository/   (UsuarioRepository)
```

```java
// ---------- SecurityConfig ----------
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtFilter;
  private final JwtAuthenticationEntryPoint entryPoint;
  private final CustomAccessDeniedHandler accessDeniedHandler;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())                                   // API stateless
        .cors(Customizer.withDefaults())
        .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .exceptionHandling(e -> e
            .authenticationEntryPoint(entryPoint)                       // 401 JSON
            .accessDeniedHandler(accessDeniedHandler))                  // 403 JSON
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/auth/**").permitAll()
            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/productos/**").permitAll()
            .requestMatchers("/api/admin/**").hasRole("ADMIN")
            .anyRequest().authenticated())
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg)
      throws Exception {
    return cfg.getAuthenticationManager();     // usa tu UserDetailsService + PasswordEncoder
  }
}

// ---------- AuthController ----------
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthenticationManager authManager;
  private final JwtService jwtService;
  private final UsuarioRepository repo;
  private final PasswordEncoder encoder;

  @PostMapping("/register")
  @ResponseStatus(HttpStatus.CREATED)
  public void register(@Valid @RequestBody RegisterRequest req) {
    if (repo.existsByUsername(req.username())) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Usuario ya existe");
    }
    Usuario u = new Usuario();
    u.setUsername(req.username());
    u.setPassword(encoder.encode(req.password()));      // hash, nunca texto plano
    // asignar rol por defecto ROLE_USER
    repo.save(u);
  }

  @PostMapping("/login")
  public TokenResponse login(@Valid @RequestBody LoginRequest req) {
    Authentication auth = authManager.authenticate(
        new UsernamePasswordAuthenticationToken(req.username(), req.password()));
    return new TokenResponse(jwtService.generarToken((UserDetails) auth.getPrincipal()));
  }
}

// ---------- Endpoint protegido ----------
@RestController
@RequestMapping("/api")
public class PedidoController {

  @GetMapping("/pedidos")                          // cualquier autenticado
  public List<Pedido> misPedidos(@AuthenticationPrincipal UserDetails user) { ...}

  @DeleteMapping("/admin/pedidos/{id}")            // solo ADMIN (regla de URL)
  public void borrar(@PathVariable Long id) { ...}

  @PreAuthorize("hasAuthority('PEDIDO_WRITE')")    // regla fina por método
  @PostMapping("/pedidos")
  public Pedido crear(@RequestBody PedidoRequest req) { ...}
}
```

### Flujo completo de uso

```bash
# 1) Registrar
curl -X POST localhost:8080/api/auth/register -H "Content-Type: application/json" \
     -d '{"username":"ana","password":"Clave123!"}'

# 2) Login → devuelve el token
curl -X POST localhost:8080/api/auth/login -H "Content-Type: application/json" \
     -d '{"username":"ana","password":"Clave123!"}'
# {"token":"eyJhbGciOiJIUzI1NiJ9..."}

# 3) Usar el token
curl localhost:8080/api/pedidos -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."

# Sin token → 401  |  Token de USER en /api/admin/** → 403
```

### Resumen de qué pasa en cada petición

```
Request ─► JwtAuthenticationFilter
              ├─ sin header Bearer ──────────► sigue (anónimo) ─► AuthorizationFilter ─► 401 si la ruta no es pública
              └─ con token válido ─► SecurityContext(user, roles) ─► AuthorizationFilter
                                                                        ├─ rol insuficiente ─► 403
                                                                        └─ OK ─► Controller (@PreAuthorize puede volver a validar)
```

## 25. Buenas prácticas y vulnerabilidades

### Buenas prácticas

| Práctica                                                                                | Por qué                                               |
|-----------------------------------------------------------------------------------------|-------------------------------------------------------|
| Nunca guardar contraseñas en texto plano; usar **BCrypt/Argon2**                        | Si se filtra la BD, no se exponen las claves.         |
| Secretos (JWT secret, claves de BD) en **variables de entorno** o un gestor de secretos | No subirlos a Git.                                    |
| Access tokens de **vida corta** + refresh tokens                                        | Reduce el daño si roban un token.                     |
| **HTTPS** siempre en producción                                                         | Sin él, tokens y contraseñas viajan expuestos.        |
| Mínimo privilegio: permisos justos                                                      | Limita el impacto de una cuenta comprometida.         |
| Reglas específicas primero, `anyRequest().authenticated()` al final                     | Por defecto todo cerrado.                             |
| Validar **autorización en el servidor** (no confiar en el frontend)                     | Ocultar un botón no es seguridad.                     |
| Verificar **propiedad del recurso** (que el pedido sea del usuario)                     | Evita IDOR (acceso a datos de otros cambiando el ID). |
| Mensajes de error genéricos en login ("credenciales inválidas")                         | No revelar si el usuario existe.                      |
| Limitar intentos de login (rate limiting / bloqueo)                                     | Frena la fuerza bruta.                                |
| Validar y sanear toda entrada (`@Valid`)                                                | Previene inyecciones.                                 |
| No devolver entidades con `password` (usar DTOs / `@JsonIgnore`)                        | Evita filtrar hashes.                                 |
| No registrar contraseñas ni tokens en los logs                                          | Los logs suelen ser menos protegidos.                 |
| Mantener dependencias actualizadas (`mvn versions`, Dependabot)                         | Corrige vulnerabilidades conocidas (CVE).             |
| Desactivar consola H2, Swagger y Actuator abiertos en producción                        | Superficies de ataque.                                |
| Usar `@PreAuthorize` además de reglas de URL                                            | Defensa en profundidad.                               |

### Vulnerabilidades comunes (OWASP) y cómo se mitigan

| Vulnerabilidad                    | Descripción                                                            | Mitigación                                                   |
|-----------------------------------|------------------------------------------------------------------------|--------------------------------------------------------------|
| **Broken Access Control**         | Usuarios acceden a lo que no deben.                                    | `authorizeHttpRequests`, `@PreAuthorize`, verificar dueño.   |
| **IDOR**                          | Cambiar `/pedidos/5` por `/pedidos/6` y ver datos ajenos.              | Comprobar que el recurso pertenece al usuario.               |
| **Inyección SQL**                 | Entrada maliciosa en consultas.                                        | Parámetros (`:param`, Spring Data), nunca concatenar.        |
| **XSS**                           | Script malicioso se ejecuta en el navegador.                           | Escapar salida (Thymeleaf lo hace), CSP, cookies `HttpOnly`. |
| **CSRF**                          | Peticiones falsificadas desde otro sitio.                              | Token CSRF, `SameSite`.                                      |
| **Credenciales débiles**          | Contraseñas fáciles o por defecto.                                     | Política de contraseñas, BCrypt, MFA.                        |
| **Fuerza bruta**                  | Probar muchas contraseñas.                                             | Bloqueo, rate limit, CAPTCHA.                                |
| **Exposición de datos sensibles** | Filtrar hashes, tokens, datos personales.                              | DTOs, HTTPS, no loguear secretos.                            |
| **Mass assignment**               | El cliente envía campos que no debería poder cambiar (`rol`, `admin`). | DTOs de entrada, no bindear entidades directamente.          |
| **Token robado**                  | JWT interceptado o filtrado.                                           | Vida corta, HTTPS, `HttpOnly`, revocación.                   |
| **Algoritmo `none` en JWT**       | Aceptar tokens sin firma.                                              | Usar librerías actuales y forzar el algoritmo esperado.      |
| **Session fixation**              | Reutilizar ID de sesión de la víctima.                                 | `changeSessionId()` (Spring lo hace por defecto).            |
| **Clickjacking**                  | Página embebida en iframe oculto.                                      | `X-Frame-Options`/CSP `frame-ancestors`.                     |
| **Configuración insegura**        | Endpoints de Actuator/H2/Swagger abiertos.                             | Cerrarlos o protegerlos en producción.                       |

### Ejemplo: evitar IDOR

```java

@GetMapping("/api/pedidos/{id}")
public PedidoResponse ver(@PathVariable Long id, @AuthenticationPrincipal UsuarioPrincipal user) {
  Pedido p = repo.findById(id).orElseThrow(() -> new EntityNotFoundException());
  if (!p.getUsuarioId().equals(user.getId()) && !user.esAdmin()) {
    throw new AccessDeniedException("No es tu pedido");        // 403
  }
  return mapper.toResponse(p);
}
// Mejor aún: consultar directamente solo lo del usuario
// repo.findByIdAndUsuarioId(id, user.getId())
```

### Ejemplo: evitar mass assignment

```java
// ✗ Malo: recibe la entidad completa (el cliente podría enviar "rol":"ADMIN")
@PostMapping
public Usuario crear(@RequestBody Usuario u) { ...}

// ✓ Bueno: DTO con solo los campos permitidos
public record RegistroRequest(@NotBlank String username, @Size(min = 8) String password) {
}

@PostMapping
public void registrar(@Valid @RequestBody RegistroRequest req) { ...}
```

---

## Tabla rápida de decisiones

| Necesito...                        | Uso                                                       |
|------------------------------------|-----------------------------------------------------------|
| Proteger una web con formularios   | `formLogin` + sesión + CSRF activado                      |
| Proteger una API REST propia       | JWT + `STATELESS` + CSRF desactivado                      |
| "Iniciar sesión con Google/GitHub" | `oauth2Login`                                             |
| Validar tokens de Keycloak/Auth0   | `oauth2ResourceServer` con `issuer-uri`                   |
| Comunicación servicio a servicio   | OAuth2 `client_credentials` (o Basic sobre HTTPS interno) |
| Regla por URL                      | `authorizeHttpRequests`                                   |
| Regla por método / propietario     | `@PreAuthorize` (con `@EnableMethodSecurity`)             |
| Usuario actual en un controlador   | `@AuthenticationPrincipal` o `Principal`                  |
| Usuario actual en un service       | `SecurityContextHolder`                                   |
| Guardar contraseña                 | `BCryptPasswordEncoder.encode()`                          |
| Responder 401/403 en JSON          | `AuthenticationEntryPoint` + `AccessDeniedHandler`        |
| Probar seguridad                   | `@WithMockUser`, `with(jwt())`, `with(csrf())`            |

## Glosario de siglas

| Sigla                 | Significado                                                        |
|-----------------------|--------------------------------------------------------------------|
| **AuthN / AuthZ**     | Authentication / Authorization.                                    |
| **JWT**               | JSON Web Token.                                                    |
| **JWS / JWE**         | JSON Web Signature (firmado) / JSON Web Encryption (cifrado).      |
| **JWK / JWKS**        | JSON Web Key / conjunto de claves públicas.                        |
| **OAuth2**            | Framework de autorización delegada.                                |
| **OIDC**              | OpenID Connect.                                                    |
| **PKCE**              | Proof Key for Code Exchange.                                       |
| **SSO**               | Single Sign-On.                                                    |
| **MFA / 2FA**         | Autenticación multifactor / de dos factores.                       |
| **TOTP**              | Contraseña de un solo uso basada en tiempo (Google Authenticator). |
| **RBAC / ABAC**       | Control de acceso por roles / por atributos.                       |
| **SAML**              | Estándar XML para SSO empresarial.                                 |
| **LDAP**              | Protocolo de directorio de usuarios (Active Directory).            |
| **CSRF / XSS / CORS** | Ver secciones 17, 18 y 25.                                         |
| **HSTS**              | HTTP Strict Transport Security.                                    |
| **CSP**               | Content Security Policy.                                           |
| **TLS / SSL**         | Cifrado de transporte.                                             |
| **HMAC**              | Código de autenticación de mensajes con hash y clave.              |
| **BCrypt / Argon2**   | Algoritmos de hashing para contraseñas.                            |
| **IDOR**              | Insecure Direct Object Reference.                                  |
| **OWASP**             | Organización que publica el Top 10 de riesgos de seguridad web.    |
| **CVE**               | Identificador público de una vulnerabilidad conocida.              |
| **IdP**               | Identity Provider.                                                 |
| **SpEL**              | Spring Expression Language.                                        |
| **DSL**               | Domain-Specific Language (la sintaxis fluida de `HttpSecurity`).   |

---

> **Tip de lectura:** ante un proyecto con Spring Security, empieza por la clase `@EnableWebSecurity` → mira el
`SecurityFilterChain` (qué rutas son públicas, qué rol pide cada una, si usa sesión o JWT, qué filtros agrega) → luego
> el `UserDetailsService` (de dónde salen los usuarios) → el `PasswordEncoder` → y por último los `@PreAuthorize` en los
> services/controllers.

