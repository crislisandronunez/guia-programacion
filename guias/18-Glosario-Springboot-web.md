# Glosario Spring Boot Web

Términos del módulo web de Spring Boot (`spring-boot-starter-web`, Spring MVC) para entender controladores, peticiones HTTP, respuestas y configuración.

## Índice
1. [Conceptos generales](#1-conceptos-generales)
2. [Dependencias y starters](#2-dependencias-y-starters)
3. [Arranque de la aplicación](#3-arranque-de-la-aplicación)
4. [Controladores y mapeo de rutas](#4-controladores-y-mapeo-de-rutas)
5. [Recibir datos de la petición](#5-recibir-datos-de-la-petición)
6. [Respuestas](#6-respuestas)
7. [HTTP: métodos y códigos de estado](#7-http-métodos-y-códigos-de-estado)
8. [Validación](#8-validación)
9. [Manejo de excepciones](#9-manejo-de-excepciones)
10. [Filtros, interceptores y CORS](#10-filtros-interceptores-y-cors)
11. [JSON y serialización (Jackson)](#11-json-y-serialización-jackson)
12. [Vistas y plantillas (MVC clásico)](#12-vistas-y-plantillas-mvc-clásico)
13. [Archivos y recursos estáticos](#13-archivos-y-recursos-estáticos)
14. [Clientes HTTP](#14-clientes-http)
15. [Configuración (`application.properties`)](#15-configuración-applicationproperties)
16. [Seguridad web (Spring Security)](#16-seguridad-web-spring-security)
17. [Documentación de API (Swagger/OpenAPI)](#17-documentación-de-api-swaggeropenapi)
18. [Testing web](#18-testing-web)
19. [Arquitectura en capas y clases típicas](#19-arquitectura-en-capas-y-clases-típicas)
20. [Ejemplo completo comentado](#20-ejemplo-completo-comentado)

---

## 1. Conceptos generales

| Término | Significado |
|---|---|
| **Spring MVC** | Framework web de Spring basado en Modelo-Vista-Controlador. |
| **Spring Web** | Módulo que permite crear aplicaciones web y APIs REST. |
| **API REST** | API web que usa HTTP y recursos identificados por URL (`/api/usuarios/1`). |
| **Endpoint** | Ruta + método HTTP que expone una funcionalidad (`GET /api/usuarios`). |
| **Recurso** | Entidad accesible por URL (usuario, producto, pedido). |
| **Request (petición)** | Mensaje que el cliente envía al servidor. |
| **Response (respuesta)** | Mensaje que el servidor devuelve. |
| **Cliente** | Quien consume la API (navegador, app móvil, Postman, otro servicio). |
| **Servidor embebido** | Servidor (Tomcat por defecto) incluido dentro del `.jar`; no hace falta instalarlo. |
| **Tomcat / Jetty / Undertow** | Servidores web/contenedores de servlets compatibles. |
| **Servlet** | Clase Java que atiende peticiones HTTP; Spring usa uno central. |
| **DispatcherServlet** | *Front controller* de Spring MVC: recibe todas las peticiones y las deriva al controlador correcto. |
| **HandlerMapping** | Decide qué método de controlador atiende cada URL. |
| **HandlerAdapter** | Ejecuta el método del controlador elegido. |
| **ViewResolver** | Resuelve el nombre de una vista a un archivo (HTML/JSP). |
| **HttpMessageConverter** | Convierte el cuerpo HTTP a objetos Java y viceversa (ej. JSON ↔ objeto). |
| **Stateless** | El servidor no guarda estado de sesión entre peticiones (típico de REST). |
| **Idempotente** | Repetir la operación produce el mismo resultado (`GET`, `PUT`, `DELETE`). |
| **Payload / Body** | Contenido (cuerpo) de la petición o respuesta. |
| **Header (cabecera)** | Metadatos HTTP (`Content-Type`, `Authorization`). |
| **Query string** | Parámetros tras `?` en la URL: `?page=1&size=10`. |
| **Path** | Ruta de la URL: `/api/usuarios/5`. |
| **Content negotiation** | Elegir formato de respuesta (JSON/XML) según `Accept`. |
| **MIME type / Media type** | Tipo de contenido: `application/json`, `text/html`. |
| **Contexto (context-path)** | Prefijo común de la aplicación (`/miapp`). |
| **Puerto** | Puerto de escucha; por defecto `8080`. |
| **Sesión (HttpSession)** | Datos del usuario guardados en el servidor entre peticiones. |
| **Cookie** | Dato pequeño guardado en el navegador. |
| **Mapping** | Asociar una URL/verbo HTTP a un método. |
| **Binding** | Convertir datos de la petición en objetos Java. |

### Flujo de una petición
```
Cliente → DispatcherServlet → HandlerMapping → Filtros/Interceptores
        → Controller → Service → Repository → BD
        ← Respuesta (JSON/HTML) ← HttpMessageConverter ←
```

### Ejemplo: petición y respuesta HTTP cruda
```http
POST /api/v1/usuarios HTTP/1.1
Host: localhost:8080
Content-Type: application/json
Accept: application/json
Authorization: Bearer eyJhbGciOi...

{"nombre": "Ana", "email": "ana@mail.com", "edad": 25}
```
```http
HTTP/1.1 201 Created
Content-Type: application/json
Location: /api/v1/usuarios/7

{"id": 7, "nombre": "Ana", "email": "ana@mail.com"}
```

## 2. Dependencias y starters

| Término | Significado |
|---|---|
| **Starter** | Dependencia "paquete" que agrupa librerías relacionadas. |
| `spring-boot-starter-web` | Spring MVC + Tomcat + Jackson para APIs y web. |
| `spring-boot-starter-webflux` | Web reactiva (Netty, `Mono`/`Flux`). |
| `spring-boot-starter-thymeleaf` | Motor de plantillas Thymeleaf. |
| `spring-boot-starter-validation` | Validación de datos (Bean Validation). |
| `spring-boot-starter-security` | Spring Security. |
| `spring-boot-starter-data-jpa` | JPA/Hibernate para acceso a datos. |
| `spring-boot-starter-test` | JUnit, Mockito, MockMvc, etc. |
| `spring-boot-starter-actuator` | Endpoints de monitoreo (`/actuator/health`). |
| `spring-boot-starter-parent` | Pom padre con versiones gestionadas. |
| `spring-boot-devtools` | Reinicio automático en desarrollo. |
| `springdoc-openapi` | Genera Swagger UI/OpenAPI. |

### Ejemplo: `pom.xml`
```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.3.0</version>
</parent>

<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

## 3. Arranque de la aplicación

| Término | Significado |
|---|---|
| `@SpringBootApplication` | Combina `@Configuration`, `@EnableAutoConfiguration` y `@ComponentScan`. |
| `SpringApplication.run(...)` | Arranca la aplicación y el servidor embebido. |
| **Auto-configuración** | Spring Boot configura beans automáticamente según las dependencias. |
| `@ComponentScan` | Busca componentes (`@Controller`, `@Service`...) en los paquetes. |
| `@Configuration` | Clase de configuración con beans. |
| `@Bean` | Método que crea un bean. |
| `SpringBootServletInitializer` | Permite desplegar como `.war` en un Tomcat externo. |
| `CommandLineRunner` / `ApplicationRunner` | Código que corre al iniciar la aplicación. |
| `@EnableWebMvc` | Desactiva la config. automática de MVC y toma control total (usar con cuidado). |
| `WebMvcConfigurer` | Interfaz para personalizar MVC (CORS, interceptores, recursos). |
| `@Profile` | Beans/config activos solo en cierto perfil (`dev`, `prod`). |
| `Environment` | Acceso a propiedades y perfiles. |

```java
@SpringBootApplication
public class MiApp {
    public static void main(String[] args) {
        SpringApplication.run(MiApp.class, args);
    }
}
```

### Ejemplo: `CommandLineRunner`, `@Bean` y `WebMvcConfigurer`
```java
@Configuration
public class AppConfig implements WebMvcConfigurer {

    @Bean
    public CommandLineRunner init() {
        return args -> System.out.println("Aplicación iniciada");
    }

    @Bean
    public RestClient restClient() {
        return RestClient.create();
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
    }
}
```

## 4. Controladores y mapeo de rutas

| Anotación / Clase | Significado |
|---|---|
| `@Controller` | Controlador MVC; sus métodos suelen devolver el nombre de una vista. |
| `@RestController` | `@Controller` + `@ResponseBody`: devuelve datos (JSON) directo. |
| `@RequestMapping` | Mapea una URL (y opcionalmente verbo) a clase o método. |
| `@GetMapping` | Atiende `GET` (leer). |
| `@PostMapping` | Atiende `POST` (crear). |
| `@PutMapping` | Atiende `PUT` (reemplazar/actualizar). |
| `@PatchMapping` | Atiende `PATCH` (actualización parcial). |
| `@DeleteMapping` | Atiende `DELETE` (eliminar). |
| `value` / `path` | URL del mapeo: `@GetMapping("/lista")`. |
| `method` | Verbo HTTP en `@RequestMapping(method = RequestMethod.GET)`. |
| `consumes` | Tipo de contenido que acepta el endpoint. |
| `produces` | Tipo de contenido que devuelve el endpoint. |
| `params` / `headers` | Condiciones extra sobre parámetros o cabeceras. |
| **Ruta con variable** | `/usuarios/{id}` — `{id}` es un valor dinámico. |
| **Ruta con regex** | `/usuarios/{id:[0-9]+}`. |
| **Versionado de API** | `/api/v1/...` para mantener compatibilidad. |

```java
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @GetMapping                       // GET /api/usuarios
    public List<Usuario> listar() { ... }

    @GetMapping("/{id}")              // GET /api/usuarios/5
    public Usuario buscar(@PathVariable Long id) { ... }

    @GetMapping("/{id:[0-9]+}/detalle") // solo números
    public Usuario detalle(@PathVariable Long id) { ... }

    @PostMapping(consumes = "application/json",
                 produces = "application/json")   // POST /api/usuarios
    public Usuario crear(@RequestBody Usuario u) { ... }

    @GetMapping(params = "activo=true")  // GET /api/usuarios?activo=true
    public List<Usuario> soloActivos() { ... }

    @RequestMapping(value = "/legacy", method = RequestMethod.GET) // forma larga
    public String legacy() { return "ok"; }
}
```

## 5. Recibir datos de la petición

| Anotación / Clase | Significado | Ejemplo |
|---|---|---|
| `@PathVariable` | Valor del path. | `GET /usuarios/5` → `@PathVariable Long id` (id = 5) |
| `@RequestParam` | Parámetro del query string o formulario. | `GET /buscar?nombre=Ana` → `@RequestParam String nombre` |
| `@RequestParam(required=false)` | Parámetro opcional (llega `null` si falta). | `@RequestParam(required=false) String ciudad` → `/buscar` y `/buscar?ciudad=Lima` funcionan |
| `@RequestParam(defaultValue="10")` | Valor por defecto si no viene. | `@RequestParam(defaultValue="10") int size` → sin `?size=` vale 10 |
| `@RequestBody` | Cuerpo (JSON) convertido a objeto. | `POST` con `{"nombre":"Ana"}` → `@RequestBody UsuarioRequest req` |
| `@RequestHeader` | Valor de una cabecera. | `@RequestHeader("Authorization") String token` → `"Bearer abc123"` |
| `@CookieValue` | Valor de una cookie. | `@CookieValue(name="sesion", required=false) String sesion` |
| `@ModelAttribute` | Enlaza datos de formulario a un objeto / agrega datos al modelo. | `@ModelAttribute UsuarioForm form` ← campos `nombre=Ana&edad=20` del `<form>` |
| `@RequestPart` | Parte de una petición `multipart` (archivo + datos). | `@RequestPart("archivo") MultipartFile archivo` |
| `@MatrixVariable` | Variables en el path (`;a=1;b=2`), poco usado. | `GET /autos;color=rojo` → `@MatrixVariable String color` |
| `@SessionAttribute` / `@SessionAttributes` | Datos de sesión. | `@SessionAttribute("carrito") Carrito carrito` |
| `@RequestAttribute` | Atributo de la petición (puesto por un filtro/interceptor). | `@RequestAttribute("usuarioId") Long usuarioId` |
| `@Valid` / `@Validated` | Activa validación del objeto recibido. | `crear(@Valid @RequestBody UsuarioRequest req)` → si falla, devuelve 400 |
| `HttpServletRequest` | Objeto con toda la información de la petición. | `req.getMethod()`, `req.getRequestURI()`, `req.getRemoteAddr()`, `req.getHeader("User-Agent")` |
| `HttpServletResponse` | Objeto para manipular la respuesta directamente. | `res.setStatus(200); res.getWriter().write("ok");` |
| `HttpSession` | Sesión del usuario. | `session.setAttribute("user", u);` / `session.getAttribute("user")` |
| `Principal` / `Authentication` | Usuario autenticado actual. | `String nombre = principal.getName();` / `auth.getAuthorities()` |
| `Pageable` | Parámetros de paginación (`page`, `size`, `sort`). | `GET /usuarios?page=0&size=5&sort=nombre,asc` → `listar(Pageable pageable)` |
| `Model` / `ModelMap` / `ModelAndView` | Contenedor de datos para la vista. | `model.addAttribute("usuarios", lista); return "usuarios";` |
| `BindingResult` | Resultado de validación/binding (va justo después del `@Valid`). | `registro(@Valid @ModelAttribute Usuario u, BindingResult br)` → `br.hasErrors()` |
| `MultipartFile` | Archivo subido. | `@RequestParam("archivo") MultipartFile f` → `f.getOriginalFilename()`, `f.getSize()`, `f.transferTo(...)` |
| `Locale` | Idioma del cliente (cabecera `Accept-Language`). | `saludar(Locale locale)` → `locale.getLanguage()` = `"es"` |

```java
@GetMapping("/{id}")
public Usuario buscar(@PathVariable Long id,
                      @RequestParam(defaultValue = "false") boolean detalle) { ... }

@PostMapping
public Usuario crear(@Valid @RequestBody UsuarioRequest request) { ... }
```

### Más ejemplos de entrada de datos
```java
// Cabecera y cookie
@GetMapping("/perfil")
public String perfil(@RequestHeader("Authorization") String token,
                     @CookieValue(name = "sesion", required = false) String sesion) { ... }

// Varios parámetros opcionales con Optional
@GetMapping("/buscar")   // /buscar?nombre=Ana&ciudad=Lima
public List<Usuario> buscar(@RequestParam Optional<String> nombre,
                            @RequestParam(required = false) String ciudad) { ... }

// Todos los parámetros en un Map
@GetMapping("/filtrar")
public List<Usuario> filtrar(@RequestParam Map<String, String> filtros) { ... }

// Formulario HTML (application/x-www-form-urlencoded) → objeto
@PostMapping("/registro")
public String registro(@ModelAttribute UsuarioForm form, BindingResult errores) { ... }

// Paginación: GET /usuarios?page=0&size=5&sort=nombre,asc
@GetMapping("/paginado")
public Page<Usuario> paginado(Pageable pageable) { ... }

// Usuario autenticado
@GetMapping("/yo")
public String yo(Principal principal) { return principal.getName(); }

// Acceso directo a request/response
@GetMapping("/raw")
public void raw(HttpServletRequest req, HttpServletResponse res) throws IOException {
    res.getWriter().write("IP: " + req.getRemoteAddr());
}

// Subida de archivo + datos JSON
@PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public void avatar(@RequestPart("archivo") MultipartFile archivo,
                   @RequestPart("datos") UsuarioRequest datos) { ... }
```

## 6. Respuestas

| Término | Significado |
|---|---|
| `@ResponseBody` | El retorno se escribe en el cuerpo de la respuesta (como JSON). |
| `ResponseEntity<T>` | Respuesta completa: cuerpo + código de estado + cabeceras. |
| `ResponseEntity.ok(x)` | 200 con cuerpo. |
| `ResponseEntity.created(uri)` | 201 con cabecera `Location`. |
| `ResponseEntity.noContent().build()` | 204 sin cuerpo. |
| `ResponseEntity.notFound().build()` | 404. |
| `ResponseEntity.badRequest()` | 400. |
| `ResponseEntity.status(HttpStatus.X)` | Estado personalizado. |
| `@ResponseStatus` | Fija el código HTTP de un método o excepción. |
| `HttpStatus` | Enum con códigos HTTP (`OK`, `NOT_FOUND`, `CREATED`...). |
| `HttpHeaders` | Manejo de cabeceras. |
| `MediaType` | Constantes de tipos de contenido (`APPLICATION_JSON`). |
| `HttpEntity<T>` | Cuerpo + cabeceras (base de `ResponseEntity`). |
| `ModelAndView` | Vista + datos (MVC clásico). |
| `RedirectView` / `redirect:/ruta` | Redirigir a otra URL. |
| `forward:/ruta` | Reenviar internamente a otra URL. |
| `RedirectAttributes` | Pasar datos (flash) en una redirección. |
| `StreamingResponseBody` | Respuesta en streaming (archivos grandes). |
| `SseEmitter` | Server-Sent Events: el servidor envía eventos continuos. |
| `DeferredResult` / `Callable` / `CompletableFuture` | Respuestas asíncronas. |
| `Resource` / `ByteArrayResource` | Devolver archivos para descargar. |
| `Content-Disposition` | Cabecera para forzar la descarga de un archivo. |
| `Page<T>` | Resultado paginado (contenido + total + páginas). |
| `Slice<T>` | Paginación sin conteo total. |
| `ProblemDetail` | Formato estándar de errores (RFC 7807), Spring 6+. |

```java
@PostMapping
public ResponseEntity<Usuario> crear(@RequestBody Usuario u) {
    Usuario guardado = service.guardar(u);
    URI uri = URI.create("/api/usuarios/" + guardado.getId());
    return ResponseEntity.created(uri).body(guardado);
}
```

### Más ejemplos de respuestas
```java
// 200 o 404 según exista (Optional)
@GetMapping("/{id}")
public ResponseEntity<Usuario> buscar(@PathVariable Long id) {
    return service.buscar(id)                 // Optional<Usuario>
                  .map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
}

// 204 sin cuerpo
@DeleteMapping("/{id}")
public ResponseEntity<Void> eliminar(@PathVariable Long id) {
    service.eliminar(id);
    return ResponseEntity.noContent().build();
}

// Cabeceras personalizadas
@GetMapping("/total")
public ResponseEntity<List<Usuario>> total() {
    List<Usuario> lista = service.listar();
    return ResponseEntity.ok()
            .header("X-Total-Count", String.valueOf(lista.size()))
            .body(lista);
}

// Descarga de archivo
@GetMapping("/reporte")
public ResponseEntity<Resource> descargar() {
    Resource archivo = new FileSystemResource("reportes/informe.pdf");
    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=informe.pdf")
            .contentType(MediaType.APPLICATION_PDF)
            .body(archivo);
}

// Redirección (MVC)
@GetMapping("/viejo")
public String redirigir() { return "redirect:/nuevo"; }

// Código HTTP fijo con anotación
@PostMapping("/rapido")
@ResponseStatus(HttpStatus.CREATED)
public Usuario rapido(@RequestBody Usuario u) { return service.guardar(u); }

// Server-Sent Events
@GetMapping(value = "/eventos", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public SseEmitter eventos() {
    SseEmitter emitter = new SseEmitter();
    // emitter.send("mensaje");
    return emitter;
}

// Respuesta asíncrona
@GetMapping("/async")
public CompletableFuture<String> asincrono() {
    return CompletableFuture.supplyAsync(() -> "listo");
}

// Error estándar RFC 7807
@GetMapping("/problema")
public ProblemDetail problema() {
    ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Dato inválido");
    pd.setTitle("Error de validación");
    return pd;
}
```

## 7. HTTP: métodos y códigos de estado

### Métodos
| Verbo | Uso | CRUD | Idempotente |
|---|---|---|---|
| `GET` | Obtener datos | Read | Sí |
| `POST` | Crear recurso | Create | No |
| `PUT` | Reemplazar recurso completo | Update | Sí |
| `PATCH` | Modificar parcialmente | Update | No necesariamente |
| `DELETE` | Eliminar | Delete | Sí |
| `HEAD` | Solo cabeceras | — | Sí |
| `OPTIONS` | Métodos permitidos (CORS preflight) | — | Sí |

### Códigos de estado
| Código | `HttpStatus` | Significado |
|---|---|---|
| 200 | `OK` | Correcto. |
| 201 | `CREATED` | Recurso creado. |
| 202 | `ACCEPTED` | Aceptado para procesar (asíncrono). |
| 204 | `NO_CONTENT` | Correcto, sin cuerpo. |
| 301 / 302 | `MOVED_PERMANENTLY` / `FOUND` | Redirección. |
| 304 | `NOT_MODIFIED` | Usar caché. |
| 400 | `BAD_REQUEST` | Petición inválida. |
| 401 | `UNAUTHORIZED` | No autenticado. |
| 403 | `FORBIDDEN` | Autenticado sin permiso. |
| 404 | `NOT_FOUND` | No existe. |
| 405 | `METHOD_NOT_ALLOWED` | Verbo no permitido en esa ruta. |
| 409 | `CONFLICT` | Conflicto (ej. duplicado). |
| 415 | `UNSUPPORTED_MEDIA_TYPE` | Tipo de contenido no soportado. |
| 422 | `UNPROCESSABLE_ENTITY` | Datos válidos sintácticamente pero con errores de negocio. |
| 429 | `TOO_MANY_REQUESTS` | Demasiadas peticiones. |
| 500 | `INTERNAL_SERVER_ERROR` | Error interno del servidor. |
| 502 / 503 / 504 | `BAD_GATEWAY` / `SERVICE_UNAVAILABLE` / `GATEWAY_TIMEOUT` | Fallos de servicio/proxy. |

> Familias: **1xx** informativo · **2xx** éxito · **3xx** redirección · **4xx** error del cliente · **5xx** error del servidor.

### Cabeceras comunes
| Cabecera | Significado |
|---|---|
| `Content-Type` | Formato del cuerpo enviado. |
| `Accept` | Formato que el cliente quiere recibir. |
| `Authorization` | Credenciales (`Bearer <token>`, `Basic ...`). |
| `Location` | URL del recurso creado o destino de redirección. |
| `Cache-Control` | Reglas de caché. |
| `Cookie` / `Set-Cookie` | Envío / creación de cookies. |
| `Origin` | Origen de la petición (CORS). |
| `User-Agent` | Identifica al cliente. |
| `X-Forwarded-For` | IP original detrás de un proxy. |

### Ejemplo: probar con `curl`
```bash
curl http://localhost:8080/api/usuarios                       # GET
curl http://localhost:8080/api/usuarios/5                     # GET por id
curl -X POST http://localhost:8080/api/usuarios \
     -H "Content-Type: application/json" \
     -d '{"nombre":"Ana","email":"ana@mail.com"}'              # POST
curl -X PUT http://localhost:8080/api/usuarios/5 \
     -H "Content-Type: application/json" \
     -d '{"nombre":"Ana María"}'                               # PUT
curl -X DELETE http://localhost:8080/api/usuarios/5           # DELETE
curl -H "Authorization: Bearer TOKEN" http://localhost:8080/api/yo
curl -i http://localhost:8080/api/usuarios                    # -i muestra cabeceras
```

### Ejemplo: devolver códigos distintos según el caso
```java
@PostMapping
public ResponseEntity<?> crear(@RequestBody Usuario u) {
    if (service.existeEmail(u.getEmail())) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Email ya registrado"); // 409
    }
    return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(u));          // 201
}
```

## 8. Validación

Requiere `spring-boot-starter-validation`.

| Anotación | Significado |
|---|---|
| `@Valid` | Valida el objeto (y anidados con `@Valid`). |
| `@Validated` | Validación de Spring; permite grupos y validar parámetros de métodos. |
| `@NotNull` | No puede ser `null`. |
| `@NotEmpty` | No `null` ni vacío (String, colección). |
| `@NotBlank` | No `null`, ni vacío, ni solo espacios. |
| `@Size(min, max)` | Longitud/tamaño. |
| `@Min` / `@Max` | Valor mínimo / máximo. |
| `@Positive` / `@PositiveOrZero` | Número positivo / ≥ 0. |
| `@Negative` | Número negativo. |
| `@Email` | Formato de correo. |
| `@Pattern(regexp)` | Debe cumplir una expresión regular. |
| `@Past` / `@Future` | Fecha pasada / futura. |
| `@PastOrPresent` / `@FutureOrPresent` | Pasada o presente / futura o presente. |
| `@AssertTrue` / `@AssertFalse` | Debe ser verdadero / falso. |
| `@Digits` | Cantidad de enteros y decimales. |
| `@DecimalMin` / `@DecimalMax` | Límites decimales. |
| `@Constraint` | Crea una validación personalizada. |
| `ConstraintValidator` | Lógica de una validación personalizada. |
| `MethodArgumentNotValidException` | Se lanza cuando falla `@Valid` en `@RequestBody`. |
| `ConstraintViolationException` | Falla de validación en parámetros o entidades. |
| `BindException` | Falla de binding en formularios. |
| `BindingResult` / `FieldError` | Detalle de errores por campo. |

```java
public record UsuarioRequest(
    @NotBlank String nombre,
    @Email String email,
    @Min(18) int edad) {}
```

### Ejemplos: más validaciones
```java
// Clase con varias anotaciones y objeto anidado
public class PedidoRequest {
    @NotNull(message = "El cliente es obligatorio")
    private Long clienteId;

    @NotEmpty(message = "Debe tener al menos un ítem")
    private List<@Valid ItemRequest> items;    // valida cada ítem

    @Future
    private LocalDate fechaEntrega;

    @Pattern(regexp = "^[0-9]{9}$", message = "Teléfono de 9 dígitos")
    private String telefono;

    @Digits(integer = 6, fraction = 2)
    private BigDecimal total;
}

// Validar parámetros simples (requiere @Validated en la clase)
@Validated
@RestController
public class ProductoController {
    @GetMapping("/productos/{id}")
    public Producto get(@PathVariable @Min(1) Long id,
                        @RequestParam @Size(max = 20) String filtro) { ... }
}

// Revisar errores manualmente con BindingResult
@PostMapping("/registro")
public ResponseEntity<?> registro(@Valid @RequestBody UsuarioRequest req, BindingResult br) {
    if (br.hasErrors()) {
        Map<String, String> errores = new HashMap<>();
        br.getFieldErrors().forEach(e -> errores.put(e.getField(), e.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errores);
    }
    return ResponseEntity.ok(service.crear(req));
}

// Validación personalizada
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DniValidator.class)
public @interface Dni {
    String message() default "DNI inválido";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

public class DniValidator implements ConstraintValidator<Dni, String> {
    @Override
    public boolean isValid(String valor, ConstraintValidatorContext ctx) {
        return valor != null && valor.matches("\\d{8}");
    }
}
```

## 9. Manejo de excepciones

| Término | Significado |
|---|---|
| `@ExceptionHandler` | Método que maneja una excepción concreta. |
| `@ControllerAdvice` | Clase con manejo global de excepciones/config para todos los controladores. |
| `@RestControllerAdvice` | `@ControllerAdvice` + `@ResponseBody` (respuestas JSON). |
| `@ResponseStatus` | Asocia un código HTTP a una excepción. |
| `ResponseStatusException` | Lanza error HTTP directo: `throw new ResponseStatusException(HttpStatus.NOT_FOUND, "...")`. |
| `ResponseEntityExceptionHandler` | Clase base con manejo de excepciones estándar de Spring MVC. |
| `HandlerExceptionResolver` | Mecanismo interno de resolución de excepciones. |
| `ErrorController` | Personaliza la página/endpoint `/error`. |
| `BasicErrorController` | Controlador de errores por defecto de Boot. |
| `ErrorAttributes` | Datos del error (timestamp, status, message, path). |
| `NoHandlerFoundException` | No existe controlador para la URL. |
| `HttpRequestMethodNotSupportedException` | Verbo no soportado (405). |
| `HttpMediaTypeNotSupportedException` | Content-Type no soportado (415). |
| `MissingServletRequestParameterException` | Falta un `@RequestParam` obligatorio. |
| `MethodArgumentTypeMismatchException` | Tipo de parámetro incorrecto (ej. texto en vez de número). |
| `HttpMessageNotReadableException` | JSON mal formado o ilegible. |
| `EntityNotFoundException` / `NoSuchElementException` | Recurso no encontrado. |
| `AccessDeniedException` | Acceso denegado (Security). |
| **Excepción de negocio** | Excepción propia (`UsuarioNoEncontradoException`). |
| **ErrorResponse / ApiError** | Clase propia con el formato de error devuelto al cliente. |

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsuarioNoEncontradoException.class)
    public ResponseEntity<ApiError> noEncontrado(UsuarioNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body(new ApiError(404, ex.getMessage()));
    }
}
```

### Ejemplos: más manejo de excepciones
```java
// Excepción propia con código HTTP
@ResponseStatus(HttpStatus.NOT_FOUND)
public class UsuarioNoEncontradoException extends RuntimeException {
    public UsuarioNoEncontradoException(Long id) {
        super("Usuario " + id + " no encontrado");
    }
}

// Lanzar un error HTTP directo, sin crear excepción propia
public Usuario buscar(Long id) {
    return repo.findById(id).orElseThrow(() ->
        new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no existe"));
}

// Clase de respuesta de error
public record ApiError(int status, String mensaje, LocalDateTime fecha) {}

// Handler global completo
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Errores de @Valid → 400 con detalle por campo
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> validacion(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
          .forEach(e -> errores.put(e.getField(), e.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errores);
    }

    // JSON mal formado → 400
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> jsonInvalido(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest()
               .body(new ApiError(400, "JSON inválido", LocalDateTime.now()));
    }

    // Parámetro con tipo incorrecto → 400
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> tipo(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity.badRequest()
               .body(new ApiError(400, "Parámetro inválido: " + ex.getName(), LocalDateTime.now()));
    }

    // Cualquier otra excepción → 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> general(Exception ex) {
        return ResponseEntity.internalServerError()
               .body(new ApiError(500, "Error interno", LocalDateTime.now()));
    }
}
```

## 10. Filtros, interceptores y CORS

| Término | Significado |
|---|---|
| **Filter** (`jakarta.servlet.Filter`) | Intercepta peticiones **antes** de llegar a Spring MVC (nivel servlet). |
| `OncePerRequestFilter` | Filtro de Spring que se ejecuta una sola vez por petición. |
| `FilterChain` | Cadena de filtros; `chain.doFilter(req, res)` continúa. |
| `FilterRegistrationBean` | Registra y ordena un filtro. |
| `@Order` | Orden de ejecución. |
| **HandlerInterceptor** | Intercepta **antes/después** del controlador (nivel Spring MVC). |
| `preHandle` | Antes del controlador; devuelve `false` para cortar. |
| `postHandle` | Después del controlador, antes de renderizar la vista. |
| `afterCompletion` | Al finalizar toda la petición. |
| `InterceptorRegistry` | Donde se registran interceptores en `WebMvcConfigurer`. |
| **AOP / `@Aspect`** | Lógica transversal (logs, auditoría) alrededor de métodos. |
| **CORS** | Cross-Origin Resource Sharing: permite que un front en otro dominio llame a la API. |
| `@CrossOrigin` | Habilita CORS en un controlador/método. |
| `CorsRegistry` | Configura CORS globalmente en `WebMvcConfigurer`. |
| `CorsConfigurationSource` | Configuración CORS (con Spring Security). |
| **Preflight** | Petición `OPTIONS` previa que hace el navegador. |
| **CSRF** | Ataque de falsificación de petición; Spring Security lo protege (se suele desactivar en APIs stateless con JWT). |
| `RequestContextHolder` | Acceso a la petición actual desde cualquier lugar. |
| `MDC` | Datos de contexto para logs (ej. id de correlación). |

```java
@Override
public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/api/**")
            .allowedOrigins("http://localhost:3000")
            .allowedMethods("GET", "POST", "PUT", "DELETE");
}
```

### Ejemplo: Filter
```java
@Component
@Order(1)
public class LogFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res,
                                    FilterChain chain) throws ServletException, IOException {
        long inicio = System.currentTimeMillis();
        chain.doFilter(req, res);                       // continúa la cadena
        System.out.println(req.getMethod() + " " + req.getRequestURI()
            + " -> " + res.getStatus() + " (" + (System.currentTimeMillis() - inicio) + " ms)");
    }
}
```

### Ejemplo: HandlerInterceptor
```java
@Component
public class AuthInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler)
            throws Exception {
        if (req.getHeader("X-API-KEY") == null) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;                               // corta la petición
        }
        return true;                                    // sigue al controlador
    }
}

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired private AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/publico/**");
    }
}
```

### Ejemplo: `@CrossOrigin` y CORS con Security
```java
@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class ProductoController { ... }

@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of("http://localhost:3000"));
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    config.setAllowedHeaders(List.of("*"));
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
}
```

## 11. JSON y serialización (Jackson)

| Término | Significado |
|---|---|
| **Jackson** | Librería que convierte objetos Java ↔ JSON. |
| `ObjectMapper` | Clase principal de Jackson (`writeValueAsString`, `readValue`). |
| **Serializar** | Objeto Java → JSON. |
| **Deserializar** | JSON → objeto Java. |
| `@JsonProperty("nombre")` | Nombre del campo en el JSON. |
| `@JsonIgnore` | Excluye un campo del JSON. |
| `@JsonIgnoreProperties` | Excluye varios campos / ignora desconocidos. |
| `@JsonInclude(Include.NON_NULL)` | Omite campos nulos. |
| `@JsonFormat` | Formato de fechas/números (`pattern = "dd/MM/yyyy"`). |
| `@JsonAlias` | Nombres alternativos al leer. |
| `@JsonCreator` | Constructor/método para deserializar. |
| `@JsonManagedReference` / `@JsonBackReference` | Evitan bucles infinitos en relaciones bidireccionales. |
| `@JsonIdentityInfo` | Otra forma de resolver referencias circulares. |
| `@JsonView` | Muestra distintos campos según la vista. |
| `@JsonTypeInfo` / `@JsonSubTypes` | Polimorfismo en JSON. |
| `@JsonSerialize` / `@JsonDeserialize` | Serializador/deserializador personalizado. |
| `@JsonNaming` | Estrategia de nombres (`snake_case`). |
| `Jackson2ObjectMapperBuilder` | Constructor de `ObjectMapper` en Spring. |
| `spring.jackson.*` | Propiedades de configuración de Jackson. |
| `JsonNode` | Representa un JSON como árbol. |
| `TypeReference<T>` | Deserializar tipos genéricos (`List<Usuario>`). |
| `JsonProcessingException` | Error al procesar JSON. |
| **Referencia circular** | A referencia a B y B a A → recursión infinita al serializar. |
| **Lazy loading** | Carga diferida de relaciones JPA; puede causar `LazyInitializationException` al serializar. |
| **Jackson JSR-310** | Soporte para `LocalDate`, `LocalDateTime`, etc. |

### Ejemplos: anotaciones de Jackson
```java
@JsonInclude(JsonInclude.Include.NON_NULL)          // omite nulos
@JsonIgnoreProperties(ignoreUnknown = true)         // ignora campos desconocidos al leer
public class UsuarioDto {

    private Long id;

    @JsonProperty("nombre_completo")                // JSON: "nombre_completo"
    private String nombre;

    @JsonIgnore                                     // nunca sale en el JSON
    private String password;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate fechaNacimiento;

    @JsonAlias({"mail", "correo"})                  // acepta varios nombres al leer
    private String email;
}

// Evitar bucle infinito en relaciones bidireccionales
public class Pedido {
    @JsonManagedReference
    private List<Item> items;
}
public class Item {
    @JsonBackReference
    private Pedido pedido;
}
```
```java
// Usar ObjectMapper manualmente
ObjectMapper mapper = new ObjectMapper();
mapper.registerModule(new JavaTimeModule());

String json = mapper.writeValueAsString(usuario);                       // objeto → JSON
Usuario u = mapper.readValue(json, Usuario.class);                      // JSON → objeto
List<Usuario> lista = mapper.readValue(json, new TypeReference<>() {}); // JSON → lista
JsonNode nodo = mapper.readTree(json);
String nombre = nodo.get("nombre").asText();
```
```properties
# Configuración global de Jackson
spring.jackson.property-naming-strategy=SNAKE_CASE
spring.jackson.default-property-inclusion=non_null
spring.jackson.serialization.write-dates-as-timestamps=false
```

## 12. Vistas y plantillas (MVC clásico)

| Término | Significado |
|---|---|
| **Vista (View)** | Página que se muestra al usuario (HTML). |
| **Modelo (Model)** | Datos que el controlador pasa a la vista. |
| **Thymeleaf** | Motor de plantillas HTML (el más usado con Boot). |
| `th:text` | Muestra texto. |
| `th:each` | Recorre una colección. |
| `th:if` / `th:unless` | Condicionales. |
| `th:href` / `th:src` | Enlaces/recursos con `@{...}`. |
| `th:object` / `th:field` | Enlace de formularios con objeto. |
| `th:action` | URL de envío de un formulario. |
| `th:fragment` / `th:replace` / `th:insert` | Reutilizar fragmentos (header, footer). |
| `${...}` | Variable. |
| `*{...}` | Expresión sobre el objeto seleccionado. |
| `@{...}` | URL. |
| `#{...}` | Mensaje de internacionalización. |
| `src/main/resources/templates` | Carpeta de plantillas. |
| **JSP** | JavaServer Pages (antiguo). |
| **FreeMarker / Mustache** | Otros motores de plantillas. |
| **Layout** | Plantilla base compartida. |
| **i18n** | Internacionalización (`messages.properties`). |
| `MessageSource` | Resuelve mensajes por idioma. |
| `LocaleResolver` | Determina el idioma del usuario. |
| **Flash attribute** | Dato que sobrevive una sola redirección. |
| **PRG** | Patrón Post/Redirect/Get: evita reenvío de formularios. |

```java
@Controller
public class HomeController {
    @GetMapping("/")
    public String inicio(Model model) {
        model.addAttribute("titulo", "Hola");
        return "inicio";   // → templates/inicio.html
    }
}
```

### Ejemplo: plantilla Thymeleaf (`templates/usuarios.html`)
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title th:text="${titulo}">Título</title>
    <link rel="stylesheet" th:href="@{/css/estilos.css}">
</head>
<body>
    <div th:replace="~{fragmentos :: header}"></div>

    <!-- Lista -->
    <table>
        <tr th:each="u : ${usuarios}">
            <td th:text="${u.id}">1</td>
            <td th:text="${u.nombre}">Nombre</td>
            <td th:if="${u.activo}">Activo</td>
            <td th:unless="${u.activo}">Inactivo</td>
            <td><a th:href="@{/usuarios/{id}(id=${u.id})}">Ver</a></td>
        </tr>
    </table>

    <!-- Formulario -->
    <form th:action="@{/usuarios/guardar}" th:object="${usuario}" method="post">
        <input type="text" th:field="*{nombre}">
        <span th:if="${#fields.hasErrors('nombre')}" th:errors="*{nombre}">Error</span>
        <button type="submit">Guardar</button>
    </form>

    <p th:text="#{saludo.bienvenida}">Bienvenido</p>
</body>
</html>
```

### Ejemplo: controlador MVC con formulario (patrón PRG)
```java
@Controller
@RequestMapping("/usuarios")
public class UsuarioWebController {

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", service.listar());
        return "usuarios";                          // templates/usuarios.html
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "form-usuario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Usuario usuario,
                          BindingResult errores,
                          RedirectAttributes flash) {
        if (errores.hasErrors()) {
            return "form-usuario";                  // vuelve al formulario
        }
        service.guardar(usuario);
        flash.addFlashAttribute("mensaje", "Guardado correctamente");
        return "redirect:/usuarios";                // evita reenvío (PRG)
    }

    @GetMapping("/vista")
    public ModelAndView vista() {
        ModelAndView mv = new ModelAndView("usuarios");
        mv.addObject("usuarios", service.listar());
        return mv;
    }
}
```
```properties
# messages.properties (i18n)
saludo.bienvenida=Bienvenido
```

## 13. Archivos y recursos estáticos

| Término | Significado |
|---|---|
| `src/main/resources/static` | Archivos estáticos (CSS, JS, imágenes) servidos directo. |
| `src/main/resources/public` | Otra carpeta de recursos públicos. |
| `ResourceHandlerRegistry` | Configura rutas de recursos estáticos. |
| `MultipartFile` | Archivo recibido en una petición. |
| `multipart/form-data` | Tipo de contenido para subir archivos. |
| `getOriginalFilename()` | Nombre original del archivo. |
| `getBytes()` / `getInputStream()` | Contenido del archivo. |
| `transferTo(File)` | Guarda el archivo en disco. |
| `spring.servlet.multipart.max-file-size` | Tamaño máximo por archivo. |
| `spring.servlet.multipart.max-request-size` | Tamaño máximo de la petición. |
| `MaxUploadSizeExceededException` | Archivo excede el tamaño permitido. |
| `Resource` | Abstracción de un archivo/recurso (`ClassPathResource`, `FileSystemResource`, `UrlResource`). |
| `favicon.ico` | Ícono de la pestaña del navegador. |
| **WebJars** | Librerías JS/CSS empaquetadas como dependencias Maven. |

### Ejemplo: subir y guardar un archivo
```java
@RestController
@RequestMapping("/api/archivos")
public class ArchivoController {

    private final Path carpeta = Paths.get("uploads");

    @PostMapping("/subir")
    public ResponseEntity<String> subir(@RequestParam("archivo") MultipartFile archivo)
            throws IOException {
        if (archivo.isEmpty()) {
            return ResponseEntity.badRequest().body("Archivo vacío");
        }
        Files.createDirectories(carpeta);
        String nombre = UUID.randomUUID() + "_" + archivo.getOriginalFilename();
        archivo.transferTo(carpeta.resolve(nombre));
        return ResponseEntity.ok("Guardado: " + nombre);
    }

    @GetMapping("/{nombre}")
    public ResponseEntity<Resource> bajar(@PathVariable String nombre) throws IOException {
        Resource recurso = new UrlResource(carpeta.resolve(nombre).toUri());
        if (!recurso.exists()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombre + "\"")
                .body(recurso);
    }
}
```
```bash
curl -F "archivo=@foto.png" http://localhost:8080/api/archivos/subir
```
```properties
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```
```java
// Servir carpeta externa como recursos estáticos
@Override
public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/imagenes/**")
            .addResourceLocations("file:/var/app/imagenes/");
}
```

## 14. Clientes HTTP

Para que tu aplicación **consuma** otras APIs.

| Término | Significado |
|---|---|
| `RestTemplate` | Cliente HTTP síncrono clásico (en modo mantenimiento). |
| `RestClient` | Cliente síncrono moderno (Spring 6.1+). |
| `WebClient` | Cliente reactivo/asíncrono (WebFlux). |
| `HttpClient` (Java 11+) | Cliente nativo del JDK. |
| `OpenFeign` / `@FeignClient` | Cliente declarativo: defines una interfaz y Spring la implementa. |
| `exchange(...)` | Petición genérica con control total. |
| `getForObject / getForEntity` | GET directo. |
| `postForObject / postForEntity` | POST directo. |
| `retrieve()` | Ejecuta la petición (WebClient/RestClient). |
| `bodyToMono / bodyToFlux` | Convierte la respuesta a `Mono`/`Flux`. |
| `Mono<T>` | 0 o 1 elemento asíncrono (reactivo). |
| `Flux<T>` | 0..N elementos asíncronos (reactivo). |
| `HttpClientErrorException` | Respuesta 4xx. |
| `HttpServerErrorException` | Respuesta 5xx. |
| `ResourceAccessException` | Error de conexión/timeout. |
| **Timeout** | Tiempo máximo de espera de conexión/lectura. |
| **Retry** | Reintentar tras fallo. |
| **Circuit Breaker** | Corta llamadas a un servicio que falla (Resilience4j). |

### Ejemplos: consumir otra API
```java
// RestTemplate (clásico)
RestTemplate rest = new RestTemplate();
Usuario u = rest.getForObject("https://api.ejemplo.com/usuarios/1", Usuario.class);
ResponseEntity<Usuario> resp = rest.postForEntity(url, nuevo, Usuario.class);
ResponseEntity<List<Usuario>> lista = rest.exchange(
        url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Usuario>>() {});

// RestClient (moderno, Spring 6.1+)
RestClient client = RestClient.builder().baseUrl("https://api.ejemplo.com").build();
Usuario u2 = client.get()
        .uri("/usuarios/{id}", 1)
        .header("Authorization", "Bearer TOKEN")
        .retrieve()
        .body(Usuario.class);

Usuario creado = client.post()
        .uri("/usuarios")
        .contentType(MediaType.APPLICATION_JSON)
        .body(nuevo)
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
            throw new RuntimeException("Error del cliente: " + res.getStatusCode());
        })
        .body(Usuario.class);

// WebClient (reactivo)
WebClient web = WebClient.create("https://api.ejemplo.com");
Mono<Usuario> mono = web.get().uri("/usuarios/1").retrieve().bodyToMono(Usuario.class);
Flux<Usuario> flux = web.get().uri("/usuarios").retrieve().bodyToFlux(Usuario.class);
Usuario bloqueante = mono.block();   // espera el resultado (evitar en código reactivo)

// Manejo de errores de RestTemplate
try {
    rest.getForObject(url, String.class);
} catch (HttpClientErrorException e) {       // 4xx
    System.out.println(e.getStatusCode() + " " + e.getResponseBodyAsString());
} catch (ResourceAccessException e) {        // sin conexión / timeout
    System.out.println("Sin conexión");
}

// Cliente nativo de Java 11+
HttpClient http = HttpClient.newHttpClient();
HttpRequest request = HttpRequest.newBuilder(URI.create(url)).GET().build();
HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
```
```java
// Feign: solo defines la interfaz (requiere @EnableFeignClients)
@FeignClient(name = "usuarios-api", url = "https://api.ejemplo.com")
public interface UsuarioClient {
    @GetMapping("/usuarios/{id}")
    Usuario buscar(@PathVariable("id") Long id);

    @PostMapping("/usuarios")
    Usuario crear(@RequestBody Usuario u);
}
```

## 15. Configuración (`application.properties`)

Archivo: `src/main/resources/application.properties` o `application.yml`.

| Propiedad | Significado |
|---|---|
| `server.port` | Puerto del servidor (por defecto 8080). |
| `server.servlet.context-path` | Prefijo global de la aplicación. |
| `server.error.include-message` | Incluir mensaje en errores. |
| `server.error.include-stacktrace` | Incluir stack trace en errores. |
| `server.compression.enabled` | Comprime respuestas (gzip). |
| `server.ssl.*` | Configuración HTTPS. |
| `server.tomcat.*` | Ajustes de Tomcat. |
| `spring.application.name` | Nombre de la aplicación. |
| `spring.profiles.active` | Perfil activo (`dev`, `prod`). |
| `spring.mvc.*` | Ajustes de Spring MVC. |
| `spring.mvc.throw-exception-if-no-handler-found` | Lanza excepción si no hay controlador. |
| `spring.web.resources.static-locations` | Ubicación de recursos estáticos. |
| `spring.servlet.multipart.*` | Subida de archivos. |
| `spring.jackson.*` | Configuración de JSON. |
| `spring.jackson.date-format` | Formato global de fechas. |
| `spring.jackson.default-property-inclusion` | Ej. `non_null`. |
| `spring.thymeleaf.*` | Configuración de Thymeleaf (`cache`, `prefix`). |
| `spring.datasource.url/username/password` | Conexión a base de datos. |
| `spring.jpa.hibernate.ddl-auto` | Cómo gestiona el esquema (`none`, `update`, `create`, `validate`). |
| `spring.jpa.show-sql` | Muestra SQL en consola. |
| `logging.level.<paquete>` | Nivel de log (`DEBUG`, `INFO`, `WARN`, `ERROR`). |
| `management.endpoints.web.exposure.include` | Endpoints de Actuator expuestos. |
| `${propiedad}` | Referencia a una propiedad. |
| `@Value("${x}")` | Inyecta una propiedad. |
| `@ConfigurationProperties` | Mapea un grupo de propiedades a una clase. |
| `application-dev.properties` | Config específica del perfil `dev`. |
| **Variables de entorno** | Pueden sobreescribir propiedades (`SERVER_PORT=9090`). |

```properties
server.port=8081
spring.application.name=mi-api
spring.jackson.default-property-inclusion=non_null
logging.level.org.springframework.web=DEBUG
```

### Ejemplo: `application.yml` equivalente, perfiles y `@ConfigurationProperties`
```yaml
server:
  port: 8081
  servlet:
    context-path: /miapp
spring:
  application:
    name: mi-api
  datasource:
    url: jdbc:mysql://localhost:3306/midb
    username: root
    password: ${DB_PASSWORD}      # variable de entorno
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
app:
  jwt:
    secret: miClaveSecreta
    expiracion: 3600
```
```java
// Leer una propiedad suelta
@Value("${app.jwt.expiracion}")
private long expiracion;

// Mapear un grupo de propiedades
@ConfigurationProperties(prefix = "app.jwt")
@Component
public class JwtProperties {
    private String secret;
    private long expiracion;
    // getters y setters
}
```
```bash
# Elegir perfil al ejecutar
java -jar app.jar --spring.profiles.active=prod
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## 16. Seguridad web (Spring Security)

| Término | Significado |
|---|---|
| **Autenticación** | Verificar quién eres (login). |
| **Autorización** | Verificar qué puedes hacer (roles/permisos). |
| `SecurityFilterChain` | Bean que define las reglas de seguridad HTTP. |
| `HttpSecurity` | Constructor de reglas (`authorizeHttpRequests`, `csrf`, `cors`...). |
| `@EnableWebSecurity` | Activa la seguridad web. |
| `@EnableMethodSecurity` | Habilita seguridad a nivel de método. |
| `@PreAuthorize("hasRole('ADMIN')")` | Valida antes de ejecutar el método. |
| `@Secured` / `@RolesAllowed` | Restringen por rol. |
| `requestMatchers(...)` | Rutas a las que aplica una regla. |
| `permitAll()` / `authenticated()` / `hasRole()` / `hasAuthority()` | Reglas de acceso. |
| `UserDetails` | Representa al usuario (nombre, password, roles). |
| `UserDetailsService` | Carga usuarios (por ej. desde BD). |
| `PasswordEncoder` / `BCryptPasswordEncoder` | Cifra/compara contraseñas. |
| `AuthenticationManager` | Coordina la autenticación. |
| `AuthenticationProvider` | Valida credenciales. |
| `SecurityContext` / `SecurityContextHolder` | Almacena el usuario autenticado actual. |
| `GrantedAuthority` | Permiso o rol. |
| **Role vs Authority** | Role lleva prefijo `ROLE_`; Authority es un permiso fino. |
| **JWT** | JSON Web Token: token firmado para autenticación stateless. |
| **Bearer token** | Token enviado en `Authorization: Bearer ...`. |
| **OAuth2 / OpenID Connect** | Estándares de autorización y login delegado. |
| **Session-based** | Autenticación guardada en sesión del servidor. |
| `SessionCreationPolicy.STATELESS` | No crear sesiones (APIs con JWT). |
| `csrf().disable()` | Desactiva CSRF. |
| **Basic Auth** | Usuario:clave codificado en base64 en cada petición. |
| **HTTPS / TLS** | Cifrado de la comunicación. |

### Ejemplo: configuración de seguridad (API stateless con JWT)
```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtFilter jwtFilter) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
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
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }
}
```

### Ejemplo: `UserDetailsService`, login y protección por método
```java
@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) {
        Usuario u = repo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("No existe: " + username));
        return User.withUsername(u.getUsername())
                   .password(u.getPassword())        // ya cifrada con BCrypt
                   .roles(u.getRol())                // genera ROLE_ADMIN, etc.
                   .build();
    }
}

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest req) {
        Authentication auth = authManager.authenticate(
            new UsernamePasswordAuthenticationToken(req.username(), req.password()));
        String token = jwtService.generar(auth.getName());
        return ResponseEntity.ok(new TokenResponse(token));
    }
}

@GetMapping("/api/admin/usuarios")
@PreAuthorize("hasRole('ADMIN')")
public List<Usuario> soloAdmin() { ... }

// Obtener el usuario actual
Authentication auth = SecurityContextHolder.getContext().getAuthentication();
String username = auth.getName();
```

### Ejemplo: filtro JWT
```java
@Component
public class JwtFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res,
                                    FilterChain chain) throws ServletException, IOException {
        String header = req.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String username = jwtService.extraerUsuario(header.substring(7));
            UserDetails user = userDetailsService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        chain.doFilter(req, res);
    }
}
```

## 17. Documentación de API (Swagger/OpenAPI)

| Término | Significado |
|---|---|
| **OpenAPI** | Estándar para describir APIs REST. |
| **Swagger UI** | Interfaz web para probar la API (`/swagger-ui.html`). |
| `springdoc-openapi` | Librería que genera la documentación automáticamente. |
| `@Operation` | Describe un endpoint. |
| `@Tag` | Agrupa endpoints. |
| `@Parameter` | Describe un parámetro. |
| `@ApiResponse` / `@ApiResponses` | Respuestas posibles. |
| `@Schema` | Describe un modelo/campo. |
| `/v3/api-docs` | JSON con la especificación. |

### Ejemplo: documentar con OpenAPI
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.5.0</version>
</dependency>
```
```java
@Bean
public OpenAPI apiInfo() {
    return new OpenAPI().info(new Info().title("Mi API").version("1.0")
                                        .description("API de usuarios"));
}

@Tag(name = "Usuarios", description = "Gestión de usuarios")
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Operation(summary = "Buscar usuario por id")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Encontrado"),
        @ApiResponse(responseCode = "404", description = "No existe")
    })
    @GetMapping("/{id}")
    public Usuario buscar(@Parameter(description = "Id del usuario") @PathVariable Long id) { ... }
}

@Schema(description = "Datos para crear un usuario")
public record UsuarioRequest(
    @Schema(example = "Ana") String nombre,
    @Schema(example = "ana@mail.com") String email) {}
```
Luego abre: `http://localhost:8080/swagger-ui.html`

## 18. Testing web

| Término | Significado |
|---|---|
| `@WebMvcTest` | Test solo de la capa web (controladores). |
| `MockMvc` | Simula peticiones HTTP sin levantar servidor. |
| `perform(get("/x"))` | Ejecuta una petición simulada. |
| `andExpect(status().isOk())` | Verifica el resultado. |
| `jsonPath("$.nombre")` | Consulta campos del JSON de respuesta. |
| `@MockBean` / `@MockitoBean` | Reemplaza un bean por un mock en el contexto Spring. |
| `@SpringBootTest` | Levanta el contexto completo. |
| `webEnvironment = RANDOM_PORT` | Arranca servidor real en puerto aleatorio. |
| `TestRestTemplate` | Cliente para tests de integración. |
| `WebTestClient` | Cliente de test (reactivo y MVC). |
| `@AutoConfigureMockMvc` | Configura MockMvc en `@SpringBootTest`. |
| `@WithMockUser` | Simula usuario autenticado. |
| `@DataJpaTest` | Test solo de capa JPA. |
| `ObjectMapper` | Convertir objetos a JSON en tests. |
| **Postman / Insomnia / curl** | Herramientas para probar endpoints manualmente. |

### Ejemplo: test del controlador con `MockMvc`
```java
@WebMvcTest(UsuarioController.class)
class UsuarioControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private UsuarioService service;       // @MockitoBean en Spring Boot 3.4+

    @Test
    void buscar_devuelve200() throws Exception {
        when(service.buscar(1L)).thenReturn(new UsuarioResponse(1L, "Ana"));

        mockMvc.perform(get("/api/usuarios/1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.nombre").value("Ana"));
    }

    @Test
    void crear_devuelve201() throws Exception {
        UsuarioRequest req = new UsuarioRequest("Ana", "ana@mail.com", 25);
        when(service.crear(any())).thenReturn(new UsuarioResponse(1L, "Ana"));

        mockMvc.perform(post("/api/usuarios")
                   .contentType(MediaType.APPLICATION_JSON)
                   .content(objectMapper.writeValueAsString(req)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void crear_sinNombre_devuelve400() throws Exception {
        mockMvc.perform(post("/api/usuarios")
                   .contentType(MediaType.APPLICATION_JSON)
                   .content("{\"email\":\"x@mail.com\"}"))
               .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")                  // simula usuario autenticado
    void eliminar_devuelve204() throws Exception {
        mockMvc.perform(delete("/api/usuarios/1"))
               .andExpect(status().isNoContent());
    }
}
```

### Ejemplo: test de integración con servidor real
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UsuarioIntegrationTest {

    @Autowired private TestRestTemplate rest;

    @Test
    void listar() {
        ResponseEntity<String> resp = rest.getForEntity("/api/usuarios", String.class);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }
}
```

## 19. Arquitectura en capas y clases típicas

```
Cliente → Controller → Service → Repository → Base de datos
            (web)      (negocio)   (datos)
```

| Capa / Clase | Rol |
|---|---|
| **Controller** | Recibe peticiones, valida entrada, delega y devuelve respuesta. Sin lógica de negocio. |
| **Service** (`@Service`) | Lógica de negocio y transacciones. |
| **Repository** (`@Repository`) | Acceso a datos. |
| **Entity** | Clase mapeada a tabla. |
| **DTO** | Objeto de transferencia entre capas/cliente. |
| **Request DTO** | Datos que entran (`CrearUsuarioRequest`). |
| **Response DTO** | Datos que salen (`UsuarioResponse`). |
| **Mapper** | Convierte Entity ↔ DTO (MapStruct, ModelMapper). |
| **Exception / ExceptionHandler** | Errores de negocio y su manejo global. |
| **Config** | Clases `@Configuration` (CORS, seguridad, beans). |
| **Security** | Filtros JWT, `UserDetailsService`, etc. |
| **Util / Helper** | Métodos auxiliares. |
| **Constants** | Constantes compartidas. |
| **Enum** | Estados/tipos fijos. |
| `...Impl` | Implementación de una interfaz (`UsuarioServiceImpl`). |
| **HATEOAS** | Incluir enlaces relacionados en la respuesta (`EntityModel`, `Link`). |
| **Paginación** | `Pageable`, `Page`, `PageRequest.of(0, 10)`. |
| **Ordenamiento** | `Sort.by("nombre").ascending()`. |
| **Inyección por constructor** | Forma recomendada de inyectar dependencias (con `final`). |

### Estructura típica de carpetas
```
src/main/java/com/empresa/app/
├── controller/     ← @RestController
├── service/        ← lógica de negocio (+ impl/)
├── repository/     ← interfaces JPA
├── model/ (entity) ← entidades
├── dto/            ← request / response
├── mapper/
├── exception/      ← excepciones + GlobalExceptionHandler
├── config/         ← configuración, CORS, seguridad
└── MiApp.java      ← @SpringBootApplication
src/main/resources/
├── application.properties
├── static/         ← css, js, imágenes
└── templates/      ← vistas Thymeleaf
```

### Ejemplo: las demás capas
```java
// Entity: tabla en BD
@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String email;
    private String nombre;
    // getters y setters
}

// DTOs
public record UsuarioRequest(@NotBlank String nombre, @Email String email) {}
public record UsuarioResponse(Long id, String nombre, String email) {}

// Repository
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
    Page<Usuario> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);
}

// Mapper manual (alternativa: MapStruct)
@Component
public class UsuarioMapper {
    public Usuario toEntity(UsuarioRequest r) {
        Usuario u = new Usuario();
        u.setNombre(r.nombre());
        u.setEmail(r.email());
        return u;
    }
    public UsuarioResponse toResponse(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getNombre(), u.getEmail());
    }
}

// Service (interfaz + implementación)
public interface UsuarioService {
    UsuarioResponse crear(UsuarioRequest request);
    UsuarioResponse buscar(Long id);
}

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository repo;
    private final UsuarioMapper mapper;

    @Override
    @Transactional
    public UsuarioResponse crear(UsuarioRequest request) {
        if (repo.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email duplicado");
        }
        return mapper.toResponse(repo.save(mapper.toEntity(request)));
    }

    @Override
    public UsuarioResponse buscar(Long id) {
        return repo.findById(id)
                   .map(mapper::toResponse)
                   .orElseThrow(() -> new UsuarioNoEncontradoException(id));
    }
}

// Paginación y orden
Pageable p = PageRequest.of(0, 10, Sort.by("nombre").ascending());
Page<Usuario> pagina = repo.findAll(p);
pagina.getContent();        // elementos de la página
pagina.getTotalElements();  // total
pagina.getTotalPages();     // nº de páginas
```

## 20. Ejemplo completo comentado

```java
@RestController                       // devuelve JSON
@RequestMapping("/api/v1/usuarios")   // prefijo de todas las rutas
@RequiredArgsConstructor              // Lombok: constructor con campos final
public class UsuarioController {

    private final UsuarioService service;  // inyección por constructor

    @GetMapping                           // GET /api/v1/usuarios?page=0&size=10
    public Page<UsuarioResponse> listar(Pageable pageable) {
        return service.listar(pageable);
    }

    @GetMapping("/{id}")                  // GET /api/v1/usuarios/5
    public UsuarioResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping                          // POST /api/v1/usuarios
    public ResponseEntity<UsuarioResponse> crear(
            @Valid @RequestBody UsuarioRequest request) {   // valida el JSON
        UsuarioResponse creado = service.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);  // 201
    }

    @PutMapping("/{id}")                  // PUT /api/v1/usuarios/5
    public UsuarioResponse actualizar(@PathVariable Long id,
                                      @Valid @RequestBody UsuarioRequest request) {
        return service.actualizar(id, request);
    }

    @DeleteMapping("/{id}")               // DELETE /api/v1/usuarios/5
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}
```

---

> **Tip de lectura:** ante un método de controlador, mira en este orden: **verbo + ruta** (`@GetMapping`) → **entradas** (`@PathVariable`, `@RequestBody`) → **tipo de retorno** (`ResponseEntity`, DTO) → **qué servicio llama**.

