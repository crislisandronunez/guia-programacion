# Glosario de Testing en Java y Spring Boot

Términos, anotaciones y herramientas para entender y escribir pruebas: JUnit 5, Mockito, AssertJ, Spring Boot Test, MockMvc, `@DataJpaTest`, Testcontainers y más.

## Índice
1. [Conceptos generales](#1-conceptos-generales)
2. [Dependencias y estructura](#2-dependencias-y-estructura)
3. [JUnit 5: anotaciones](#3-junit-5-anotaciones)
4. [Assertions: JUnit, AssertJ y Hamcrest](#4-assertions-junit-assertj-y-hamcrest)
5. [Tests parametrizados, dinámicos y repetidos](#5-tests-parametrizados-dinámicos-y-repetidos)
6. [Mockito: mocks, stubs y spies](#6-mockito-mocks-stubs-y-spies)
7. [Mockito: verificación y captura](#7-mockito-verificación-y-captura)
8. [Tests unitarios de capas](#8-tests-unitarios-de-capas)
9. [Spring Boot Test: contexto e integración](#9-spring-boot-test-contexto-e-integración)
10. [Slices de test](#10-slices-de-test)
11. [Testing web con MockMvc](#11-testing-web-con-mockmvc)
12. [Testing con TestRestTemplate y WebTestClient](#12-testing-con-testresttemplate-y-webtestclient)
13. [Testing de la capa de datos](#13-testing-de-la-capa-de-datos)
14. [Testcontainers](#14-testcontainers)
15. [Testing de seguridad](#15-testing-de-seguridad)
16. [Mock de servicios externos](#16-mock-de-servicios-externos)
17. [Testing de excepciones, tiempo y asincronía](#17-testing-de-excepciones-tiempo-y-asincronía)
18. [Datos de prueba y buenas prácticas](#18-datos-de-prueba-y-buenas-prácticas)
19. [Cobertura y ejecución con Maven](#19-cobertura-y-ejecución-con-maven)
20. [Errores frecuentes](#20-errores-frecuentes)
21. [Ejemplo completo por capas](#21-ejemplo-completo-por-capas)

---

## 1. Conceptos generales

| Término | Significado |
|---|---|
| **Test (prueba)** | Código que verifica automáticamente que otro código se comporta como se espera. |
| **SUT** | *System Under Test*: la clase/método que se está probando. |
| **Test unitario** | Prueba una unidad aislada (una clase) sin BD, red ni Spring. Rápido. |
| **Test de integración** | Prueba varias piezas funcionando juntas (controlador + servicio + BD). Más lento. |
| **Test end-to-end (E2E)** | Prueba el sistema completo desde fuera (navegador, API real). |
| **Test de humo (smoke)** | Prueba mínima de que la app arranca y lo esencial funciona. |
| **Test de regresión** | Evita que un error ya corregido vuelva a aparecer. |
| **Test de aceptación** | Verifica un requisito de negocio. |
| **Test de contrato** | Verifica el acuerdo entre dos servicios (Spring Cloud Contract, Pact). |
| **Test de carga/rendimiento** | Mide comportamiento bajo estrés (JMeter, Gatling). |
| **Pirámide de tests** | Muchos unitarios (base), algunos de integración, pocos E2E (cima). |
| **TDD** | Test-Driven Development: escribir el test **antes** que el código (rojo → verde → refactor). |
| **BDD** | Behavior-Driven Development: tests en lenguaje de comportamiento (`Given / When / Then`, Cucumber). |
| **Given / When / Then** | Dado un contexto, cuando ocurre una acción, entonces se espera un resultado. |
| **AAA (Arrange / Act / Assert)** | Preparar, ejecutar, verificar. Estructura estándar de un test. |
| **Fixture** | Estado/datos preparados antes de cada test. |
| **Test double** | Objeto que sustituye a una dependencia real en un test (término general). |
| **Dummy** | Objeto que se pasa pero nunca se usa (relleno de parámetros). |
| **Stub** | Devuelve respuestas predefinidas. |
| **Mock** | Objeto simulado que además **verifica** cómo fue usado (llamadas, argumentos). |
| **Spy** | Objeto **real** parcialmente simulado: ejecuta el código real salvo lo que se sobrescribe. |
| **Fake** | Implementación simplificada pero funcional (BD en memoria H2). |
| **Aislamiento** | Cada test no depende de otro ni del orden de ejecución. |
| **Idempotencia** | Ejecutar el test varias veces da el mismo resultado. |
| **Flaky test** | Test inestable: a veces pasa y a veces falla sin cambios de código. |
| **Cobertura (coverage)** | Porcentaje de código ejecutado por los tests (líneas, ramas). |
| **Test oracle** | La fuente que dice cuál es el resultado esperado. |
| **Assertion (aserción)** | Comprobación dentro del test (`assertEquals`). |
| **Happy path** | Caso de éxito normal. |
| **Edge case / caso borde** | Valores límite (0, `null`, vacío, máximo). |
| **Caso negativo** | Entrada inválida o error esperado. |
| **CI** | Integración Continua: ejecuta los tests en cada cambio (GitHub Actions, Jenkins). |
| **Mutation testing** | Introduce errores al código para comprobar que los tests los detectan (PIT). |

### Pirámide de tests
```
          /\
         /E2E\          pocos, lentos, frágiles
        /------\
       /Integra-\       algunos
      /  ción    \
     /------------\
    /  Unitarios   \    muchos, rápidos, baratos
   /________________\
```

### Estructura AAA
```java
@Test
void sumar_dosNumeros_devuelveLaSuma() {
    // Arrange (preparar)
    Calculadora calc = new Calculadora();

    // Act (ejecutar)
    int resultado = calc.sumar(2, 3);

    // Assert (verificar)
    assertEquals(5, resultado);
}
```

### Cuándo usar cada tipo de test double
| Necesito... | Uso |
|---|---|
| Que una dependencia devuelva un valor fijo | Stub (`when(...).thenReturn(...)`) |
| Verificar que se llamó a un método | Mock (`verify(...)`) |
| Usar el objeto real pero cambiar un método | Spy |
| Una BD ligera pero real | Fake (H2) o Testcontainers |

## 2. Dependencias y estructura

| Término | Significado |
|---|---|
| `spring-boot-starter-test` | Starter que incluye todo lo siguiente. |
| **JUnit 5 (Jupiter)** | Framework de pruebas (`org.junit.jupiter`). |
| **Mockito** | Librería para crear mocks/spies/stubs. |
| **AssertJ** | Aserciones fluidas y legibles (`assertThat(...)`). |
| **Hamcrest** | Aserciones con *matchers* (`assertThat(x, is(5))`). |
| **JSONassert** | Comparar JSON. |
| **JsonPath** | Consultar JSON (`$.nombre`). |
| **Awaitility** | Esperar condiciones asíncronas (no incluida por defecto). |
| **Spring Test / MockMvc** | Soporte de Spring para tests. |
| `spring-security-test` | Utilidades para probar seguridad. |
| `spring-boot-testcontainers` | Integración con Testcontainers. |
| `h2` | BD en memoria para tests. |
| `rest-assured` | Cliente fluido para probar APIs REST (alternativa). |
| `wiremock` / `mockwebserver` | Simular APIs HTTP externas. |
| `<scope>test</scope>` | La dependencia solo existe al ejecutar tests. |
| `src/test/java` | Código de tests. |
| `src/test/resources` | Recursos de test (`application-test.yml`, SQL, JSON). |
| `*Test` / `*Tests` | Clases ejecutadas por **Surefire** (tests unitarios). |
| `*IT` / `*IntegrationTest` | Convención para tests de integración (Failsafe). |
| **Surefire** | Plugin de Maven que ejecuta los tests unitarios (`mvn test`). |
| **Failsafe** | Plugin que ejecuta los tests de integración (`mvn verify`). |

### Ejemplo: dependencias
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-test</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.awaitility</groupId>
    <artifactId>awaitility</artifactId>
    <scope>test</scope>
</dependency>
```

### Estructura de carpetas (espejo del código)
```
src/main/java/com/empresa/app/service/PedidoService.java
src/test/java/com/empresa/app/service/PedidoServiceTest.java     ← mismo paquete
src/test/resources/
    application-test.yml
    datos-prueba.sql
    respuestas/usuario.json
```

### Convención de nombres de tests
```java
void metodo_condicion_resultadoEsperado()     // buscar_conIdInexistente_lanzaExcepcion
void deberiaHacerAlgo_cuandoCondicion()       // deberiaLanzarError_cuandoStockEsInsuficiente
@DisplayName("Crear pedido sin items lanza excepción")   // nombre legible en el reporte
```

## 3. JUnit 5: anotaciones

| Anotación | Significado |
|---|---|
| `@Test` | Marca un método como test (`org.junit.jupiter.api.Test`; **no** usar el de JUnit 4). |
| `@DisplayName("...")` | Nombre legible del test o clase. |
| `@BeforeEach` | Se ejecuta **antes de cada** test (preparar fixture). |
| `@AfterEach` | Después de cada test (limpiar). |
| `@BeforeAll` | **Una vez** antes de todos los tests de la clase (método `static`). |
| `@AfterAll` | Una vez al terminar la clase (`static`). |
| `@Disabled("motivo")` | Desactiva un test/clase. |
| `@Nested` | Clase interna para agrupar tests por escenario. |
| `@Tag("lento")` | Etiqueta para filtrar tests (`mvn test -Dgroups=lento`). |
| `@Timeout(5)` | Falla si tarda más del tiempo indicado. |
| `@RepeatedTest(n)` | Repite el test n veces. |
| `@ParameterizedTest` | Test con varios conjuntos de datos. |
| `@ValueSource` / `@CsvSource` / `@MethodSource` / `@EnumSource` / `@NullSource` / `@EmptySource` | Fuentes de datos de un test parametrizado. |
| `@TestFactory` | Método que genera tests dinámicos. |
| `@TestInstance(Lifecycle.PER_CLASS)` | Una instancia de clase para todos los tests (permite `@BeforeAll` no estático). |
| `@TestMethodOrder` / `@Order` | Controla el orden (evitar depender de él). |
| `@EnabledOnOs` / `@DisabledOnOs` | Según sistema operativo. |
| `@EnabledIf` / `@EnabledIfEnvironmentVariable` / `@EnabledIfSystemProperty` | Condicional. |
| `@EnabledOnJre` / `@EnabledForJreRange` | Según versión de Java. |
| `@TempDir` | Carpeta temporal para el test. |
| `@ExtendWith(...)` | Registra una **extensión** (Mockito, Spring...). |
| `@RegisterExtension` | Registra una extensión programáticamente. |
| `TestInfo` / `TestReporter` | Parámetros inyectables con info del test. |
| `@ResourceLock` / `@Execution(CONCURRENT)` | Control de concurrencia. |
| **Ciclo de vida** | `@BeforeAll` → (`@BeforeEach` → `@Test` → `@AfterEach`) × N → `@AfterAll`. |
| **Una instancia por test** | JUnit 5 crea una **nueva instancia** de la clase para cada test (por defecto). |

### JUnit 4 vs JUnit 5
| JUnit 4 | JUnit 5 |
|---|---|
| `org.junit.Test` | `org.junit.jupiter.api.Test` |
| `@Before` / `@After` | `@BeforeEach` / `@AfterEach` |
| `@BeforeClass` / `@AfterClass` | `@BeforeAll` / `@AfterAll` |
| `@Ignore` | `@Disabled` |
| `@RunWith(SpringRunner.class)` | `@ExtendWith(SpringExtension.class)` (ya incluido en las anotaciones de Boot) |
| `@Rule` / `@ClassRule` | `@ExtendWith` / `@RegisterExtension` |
| `@Category` | `@Tag` |
| `@Test(expected=...)` | `assertThrows` |
| `@Test(timeout=...)` | `@Timeout` |

### Ejemplo: ciclo de vida
```java
class CicloVidaTest {

    @BeforeAll
    static void antesDeTodo() { System.out.println("1 vez al inicio (static)"); }

    @BeforeEach
    void antesDeCada() { System.out.println("antes de cada test"); }

    @Test void testA() { System.out.println("testA"); }
    @Test void testB() { System.out.println("testB"); }

    @AfterEach
    void despuesDeCada() { System.out.println("después de cada test"); }

    @AfterAll
    static void alFinal() { System.out.println("1 vez al final"); }
}
```

### Ejemplo: `@Nested`, `@DisplayName`, `@Disabled`, `@Tag`, `@TempDir`
```java
@DisplayName("PedidoService")
class PedidoServiceTest {

    @Nested
    @DisplayName("al crear un pedido")
    class Crear {
        @Test @DisplayName("con datos válidos devuelve el pedido")
        void valido() { ... }

        @Test @DisplayName("sin items lanza excepción")
        void sinItems() { ... }
    }

    @Nested
    @DisplayName("al cancelar un pedido")
    class Cancelar {
        @Test void yaEnviado_noSePuede() { ... }
    }

    @Test
    @Disabled("Pendiente de corregir bug #123")
    void pendiente() { ... }

    @Test @Tag("lento")
    void procesoLargo() { ... }

    @Test
    void escribeArchivo(@TempDir Path carpeta) throws IOException {
        Path f = carpeta.resolve("datos.txt");
        Files.writeString(f, "hola");
        assertEquals("hola", Files.readString(f));    // la carpeta se borra sola
    }

    @Test
    @Timeout(value = 500, unit = TimeUnit.MILLISECONDS)
    void debeSerRapido() { ... }

    @Test
    @EnabledOnOs(OS.LINUX)
    void soloEnLinux() { ... }

    @Test
    @EnabledIfEnvironmentVariable(named = "ENTORNO", matches = "ci")
    void soloEnCI() { ... }
}
```

### Ejemplo: `TestInfo`
```java
@Test
void conInfo(TestInfo info) {
    System.out.println("Ejecutando: " + info.getDisplayName());
}
```

## 4. Assertions: JUnit, AssertJ y Hamcrest

### JUnit (`org.junit.jupiter.api.Assertions`)
| Método | Verifica |
|---|---|
| `assertEquals(esperado, real)` | Igualdad (**el esperado va primero**). |
| `assertNotEquals(a, b)` | Desigualdad. |
| `assertTrue(cond)` / `assertFalse(cond)` | Condición. |
| `assertNull(x)` / `assertNotNull(x)` | Nulidad. |
| `assertSame(a, b)` / `assertNotSame(a, b)` | Misma/distinta referencia (`==`). |
| `assertArrayEquals(a, b)` | Arreglos iguales. |
| `assertIterableEquals(a, b)` | Iterables iguales. |
| `assertLinesMatch` | Listas de líneas (admite regex). |
| `assertThrows(Excep.class, () -> ...)` | Que se lance una excepción (devuelve la excepción). |
| `assertDoesNotThrow(() -> ...)` | Que **no** se lance. |
| `assertTimeout(Duration, () -> ...)` | Que termine a tiempo. |
| `assertAll(...)` | Agrupa varias aserciones (ejecuta **todas** y reporta todos los fallos). |
| `assertInstanceOf(Clase.class, obj)` | Tipo del objeto. |
| `fail("mensaje")` | Falla incondicionalmente. |
| `assertEquals(esperado, real, delta)` | Doubles con tolerancia. |
| `assertEquals(esperado, real, "mensaje")` | Con mensaje de error. |

### AssertJ (`org.assertj.core.api.Assertions.*`) — **recomendado**
| Método | Verifica |
|---|---|
| `assertThat(x).isEqualTo(y)` | Igualdad. |
| `.isNotNull()` / `.isNull()` | Nulidad. |
| `.isTrue()` / `.isFalse()` | Booleanos. |
| `.isEmpty()` / `.isNotEmpty()` / `.isBlank()` | Vacío (colección, String). |
| `.hasSize(n)` | Tamaño. |
| `.contains(a, b)` / `.containsExactly(a, b)` / `.containsExactlyInAnyOrder(...)` | Elementos (con/sin orden). |
| `.containsOnly(...)` / `.doesNotContain(...)` | Solo estos / no contiene. |
| `.startsWith` / `.endsWith` / `.containsIgnoringCase` / `.matches(regex)` | Strings. |
| `.isGreaterThan` / `.isLessThan` / `.isBetween` / `.isPositive` / `.isZero` | Números. |
| `.isCloseTo(x, within(0.01))` | Decimales con tolerancia. |
| `.isInstanceOf(Clase.class)` | Tipo. |
| `.extracting("campo")` / `.extracting(Clase::getX)` | Extrae propiedades de una lista/objeto. |
| `.filteredOn(...)` | Filtra antes de verificar. |
| `.allMatch(...)` / `.anyMatch(...)` / `.noneMatch(...)` | Condición sobre elementos. |
| `.usingRecursiveComparison()` | Compara objetos campo por campo (sin `equals`). |
| `.ignoringFields("id")` | Excluye campos en la comparación recursiva. |
| `.hasFieldOrPropertyWithValue("nombre", "Ana")` | Propiedad concreta. |
| `assertThatThrownBy(() -> ...)` | Verifica una excepción: `.isInstanceOf(...)`, `.hasMessage(...)`, `.hasMessageContaining(...)`, `.hasCauseInstanceOf(...)`. |
| `assertThatExceptionOfType(X.class).isThrownBy(...)` | Otra forma. |
| `assertThatNoException().isThrownBy(...)` | No lanza. |
| `assertThat(optional).isPresent()` / `.isEmpty()` / `.contains(x)` | `Optional`. |
| `assertThat(map).containsEntry("k", "v")` / `.containsKey` | `Map`. |
| `.as("descripción")` | Mensaje descriptivo en el fallo. |
| `SoftAssertions` | Acumula fallos y reporta todos al final. |
| `.satisfies(...)` | Verificar con lambdas. |
| `.isBefore` / `.isAfter` / `.isToday` | Fechas. |
| `assertThat(file).exists()` / `.hasContent(...)` | Archivos. |

### Hamcrest (`org.hamcrest.*`)
| Matcher | Significado |
|---|---|
| `is(x)` / `equalTo(x)` | Igual. |
| `not(...)` | Negación. |
| `nullValue()` / `notNullValue()` | Nulo / no nulo. |
| `hasSize(n)` / `empty()` / `hasItem(x)` / `hasItems(...)` / `contains(...)` | Colecciones. |
| `containsString` / `startsWith` / `endsWith` | Strings. |
| `greaterThan` / `lessThan` / `closeTo` | Números. |
| `allOf(...)` / `anyOf(...)` | Combinar con AND / OR. |
| `hasProperty("x", is(...))` | Propiedades. |

> Hamcrest se ve mucho en tests de `MockMvc` (`jsonPath("$.nombre", is("Ana"))`). AssertJ es más legible para el resto.

### Ejemplos JUnit
```java
assertEquals(5, calc.sumar(2, 3));
assertEquals("Ana", usuario.getNombre(), "El nombre debería ser Ana");
assertEquals(0.3, 0.1 + 0.2, 0.0001);                         // con tolerancia
assertTrue(lista.isEmpty());
assertNull(repo.findByEmail("x").orElse(null));
assertArrayEquals(new int[]{1,2,3}, resultado);

// Excepciones
IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
        () -> service.crear(null));
assertEquals("Los datos son obligatorios", ex.getMessage());

assertDoesNotThrow(() -> service.validar(datos));

// Varias aserciones a la vez: reporta TODOS los fallos
assertAll("usuario",
    () -> assertEquals("Ana", u.getNombre()),
    () -> assertEquals("ana@mail.com", u.getEmail()),
    () -> assertTrue(u.isActivo())
);
```

### Ejemplos AssertJ
```java
import static org.assertj.core.api.Assertions.*;

// Básicas
assertThat(resultado).isEqualTo(5);
assertThat(nombre).isNotBlank().startsWith("A").hasSize(3);
assertThat(precio).isPositive().isBetween(BigDecimal.ONE, BigDecimal.TEN);

// Colecciones
assertThat(usuarios).hasSize(3)
                    .extracting(Usuario::getNombre)
                    .containsExactlyInAnyOrder("Ana", "Luis", "Eva");

assertThat(usuarios).filteredOn(u -> u.getEdad() > 18).hasSize(2);
assertThat(usuarios).allMatch(Usuario::isActivo);
assertThat(usuarios).extracting("nombre", "email")
                    .contains(tuple("Ana", "ana@mail.com"));

// Objetos: comparar campo por campo ignorando el id
assertThat(guardado).usingRecursiveComparison()
                    .ignoringFields("id", "creadoEn")
                    .isEqualTo(esperado);

assertThat(usuario).hasFieldOrPropertyWithValue("nombre", "Ana")
                   .hasFieldOrPropertyWithValue("activo", true);

// Excepciones
assertThatThrownBy(() -> service.buscar(99L))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessageContaining("99");

assertThatExceptionOfType(IllegalStateException.class)
        .isThrownBy(() -> pedido.cancelar())
        .withMessage("Ya fue enviado");

// Optional y Map
assertThat(repo.findByEmail("ana@mail.com")).isPresent()
                                            .get().extracting(Usuario::getNombre).isEqualTo("Ana");
assertThat(mapa).containsEntry("a", 1).doesNotContainKey("z");

// Soft assertions: no se detiene en el primer fallo
SoftAssertions.assertSoftly(soft -> {
    soft.assertThat(u.getNombre()).isEqualTo("Ana");
    soft.assertThat(u.getEdad()).isGreaterThan(17);
    soft.assertThat(u.getEmail()).contains("@");
});

// Descripción
assertThat(total).as("Total del pedido %s", pedido.getId()).isEqualByComparingTo("100.00");
```
> Para `BigDecimal` usa `isEqualByComparingTo("100.00")` (ignora la escala: `100.0` == `100.00`). `isEqualTo` compara escala también.

### Ejemplos Hamcrest
```java
assertThat(resultado, is(5));
assertThat(lista, hasSize(3));
assertThat(lista, hasItem("Ana"));
assertThat(nombre, allOf(startsWith("A"), containsString("n")));
```

## 5. Tests parametrizados, dinámicos y repetidos

| Término | Significado |
|---|---|
| `@ParameterizedTest` | Ejecuta el mismo test con distintos datos. |
| `@ValueSource(ints = {...})` | Valores simples (`strings`, `ints`, `longs`, `doubles`...). |
| `@CsvSource({"a,1", "b,2"})` | Varios argumentos por fila, separados por coma. |
| `@CsvFileSource(resources = "/datos.csv")` | Datos desde un archivo CSV. |
| `@MethodSource("nombreMetodo")` | Datos desde un método estático que devuelve `Stream<Arguments>`. |
| `@EnumSource(Estado.class)` | Cada valor de un enum. |
| `@NullSource` / `@EmptySource` / `@NullAndEmptySource` | Casos `null` y vacío. |
| `@ArgumentsSource` | Proveedor propio. |
| `Arguments.of(...)` | Conjunto de argumentos. |
| `name = "{index}: {0}"` | Plantilla del nombre del caso. |
| `@RepeatedTest(5)` | Repite el test. |
| `@TestFactory` + `DynamicTest` | Tests generados en ejecución. |

### Ejemplos
```java
@ParameterizedTest
@ValueSource(strings = {"ana@mail.com", "luis@empresa.org", "a.b@c.es"})
void emailsValidos(String email) {
    assertTrue(validador.esEmailValido(email));
}

@ParameterizedTest
@NullAndEmptySource
@ValueSource(strings = {"  ", "sin-arroba", "@sinusuario.com"})
void emailsInvalidos(String email) {
    assertFalse(validador.esEmailValido(email));
}

@ParameterizedTest(name = "{0} + {1} = {2}")
@CsvSource({
    "1, 2, 3",
    "0, 0, 0",
    "-1, 1, 0",
    "100, 200, 300"
})
void sumar(int a, int b, int esperado) {
    assertEquals(esperado, calc.sumar(a, b));
}

@ParameterizedTest
@MethodSource("casosDeDescuento")
void descuento(double precio, String cupon, double esperado) {
    assertEquals(esperado, service.aplicar(precio, cupon), 0.001);
}

static Stream<Arguments> casosDeDescuento() {
    return Stream.of(
        Arguments.of(100.0, "DESC10", 90.0),
        Arguments.of(100.0, "DESC50", 50.0),
        Arguments.of(100.0, "INVALIDO", 100.0)
    );
}

@ParameterizedTest
@EnumSource(value = Estado.class, names = {"ACTIVO", "PENDIENTE"})
void estadosEditables(Estado estado) {
    assertTrue(estado.esEditable());
}

@RepeatedTest(value = 3, name = "ejecución {currentRepetition} de {totalRepetitions}")
void repetido() { ... }

@TestFactory
Collection<DynamicTest> dinamicos() {
    return List.of(2, 4, 6).stream()
        .map(n -> DynamicTest.dynamicTest("par " + n, () -> assertEquals(0, n % 2)))
        .toList();
}
```

## 6. Mockito: mocks, stubs y spies

| Término | Significado |
|---|---|
| **Mockito** | Librería para crear dobles de prueba. |
| `@ExtendWith(MockitoExtension.class)` | Activa Mockito en JUnit 5 (inicializa `@Mock`, `@InjectMocks`). |
| `@Mock` | Crea un mock de la dependencia. |
| `@InjectMocks` | Crea el SUT e **inyecta** los mocks en él (constructor, setter o campo). |
| `@Spy` | Espía un objeto real. |
| `@Captor` | Crea un `ArgumentCaptor`. |
| `@MockitoSettings(strictness = ...)` | Nivel de estrictitud. |
| `Mockito.mock(Clase.class)` | Crear un mock manualmente. |
| `Mockito.spy(obj)` | Crear un spy. |
| `when(mock.metodo(...)).thenReturn(x)` | Stub: qué devolver. |
| `thenThrow(new X())` | Lanzar excepción. |
| `thenAnswer(inv -> ...)` | Respuesta calculada (accede a los argumentos). |
| `thenCallRealMethod()` | Ejecutar el método real. |
| `doReturn(x).when(spy).metodo()` | Stub seguro para spies. |
| `doThrow(...).when(mock).metodoVoid()` | Excepción en métodos `void`. |
| `doNothing().when(mock).metodoVoid()` | No hacer nada (es lo normal en mocks). |
| `doAnswer(...)` | Respuesta para métodos `void`. |
| **Encadenar respuestas** | `.thenReturn(a).thenReturn(b)`: primera llamada a, segunda b. |
| **Argument matchers** | `any()`, `any(Clase.class)`, `anyString()`, `anyLong()`, `eq(x)`, `isNull()`, `notNull()`, `argThat(...)`, `contains()`, `startsWith()`. |
| **Regla de matchers** | Si usas un matcher en un argumento, **todos** deben ser matchers (`eq(5)`, no `5` mezclado). |
| `mockStatic(Clase.class)` | Mock de métodos estáticos (`MockedStatic`). |
| `mockConstruction(Clase.class)` | Mock de constructores. |
| `RETURNS_DEEP_STUBS` | Mocks encadenados (`a.b().c()`). |
| `Strictness.STRICT_STUBS` | Falla si hay stubs sin usar (`UnnecessaryStubbingException`). |
| `lenient()` | Permite un stub sin usar. |
| **Valores por defecto de un mock** | `null` para objetos, `0` para números, `false`, listas vacías, `Optional.empty()`. |
| `@MockBean` / `@MockitoBean` | Mock **dentro del contexto Spring** (ver sección 9). |

### Ejemplo básico: mock + InjectMocks (test unitario puro)
```java
@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock private PedidoRepository repo;
    @Mock private EmailService email;
    @InjectMocks private PedidoService service;       // se crea con los mocks anteriores

    @Test
    void buscar_existente_devuelvePedido() {
        // Arrange: stub
        Pedido pedido = new Pedido(1L, "Ana");
        when(repo.findById(1L)).thenReturn(Optional.of(pedido));

        // Act
        Pedido resultado = service.buscar(1L);

        // Assert
        assertThat(resultado.getCliente()).isEqualTo("Ana");
        verify(repo).findById(1L);                    // verificar interacción
    }

    @Test
    void buscar_inexistente_lanzaExcepcion() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscar(99L))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
```

### Ejemplo: variedad de stubbing
```java
// Valor fijo
when(repo.count()).thenReturn(5L);

// Con matchers
when(repo.findByEmail(anyString())).thenReturn(Optional.empty());
when(repo.findById(eq(1L))).thenReturn(Optional.of(pedido));
when(service.calcular(anyDouble(), eq("VIP"))).thenReturn(10.0);       // mezcla: TODOS matchers
when(repo.save(any(Pedido.class))).thenAnswer(inv -> inv.getArgument(0));   // devuelve lo recibido

// Simular que save asigna un ID
when(repo.save(any(Pedido.class))).thenAnswer(inv -> {
    Pedido p = inv.getArgument(0);
    p.setId(10L);
    return p;
});

// Excepciones
when(repo.findById(5L)).thenThrow(new RuntimeException("BD caída"));
doThrow(new IllegalStateException()).when(email).enviar(any());       // método void

// Respuestas consecutivas
when(reloj.ahora()).thenReturn(t1).thenReturn(t2).thenThrow(new RuntimeException());

// Un método void: por defecto no hace nada
doNothing().when(email).enviar("a@b.com");

// Verificar con argThat
when(repo.save(argThat(p -> p.getTotal() > 100))).thenReturn(pedido);
```

### Ejemplo: `@Spy`
```java
@Spy
private List<String> lista = new ArrayList<>();      // objeto real

@Test
void spy() {
    lista.add("a");                                  // ejecuta código real
    doReturn(100).when(lista).size();                // sobrescribe solo size()
    assertThat(lista.size()).isEqualTo(100);
    assertThat(lista.get(0)).isEqualTo("a");         // el resto sigue real
}
// Con spies usa doReturn(...).when(spy) y no when(spy.metodo()) (esto ejecutaría el método real)
```

### Ejemplo: mock de métodos estáticos
```java
@Test
void conStatic() {
    try (MockedStatic<LocalDate> mocked = mockStatic(LocalDate.class, CALLS_REAL_METHODS)) {
        mocked.when(LocalDate::now).thenReturn(LocalDate.of(2026, 1, 1));
        assertThat(servicio.esAnioNuevo()).isTrue();
    }                                                // se restaura al cerrar el try
}
```
> Mejor alternativa: **inyectar un `Clock`** y usar `Clock.fixed(...)` en el test (ver sección 17).

### Mocks inicializados manualmente (sin extensión)
```java
PedidoRepository repo = Mockito.mock(PedidoRepository.class);
PedidoService service = new PedidoService(repo);     // constructor: simple y sin magia
```

### `@InjectMocks`: cómo inyecta
Orden de intento: **constructor** (el más grande) → **setter/propiedad** → **campo**. Si el constructor recibe un tipo para el que no hay mock, pasa `null`.

## 7. Mockito: verificación y captura

| Método | Significado |
|---|---|
| `verify(mock).metodo(args)` | Se llamó **exactamente 1 vez** con esos argumentos. |
| `verify(mock, times(n))` | Exactamente n veces. |
| `verify(mock, never())` | Nunca. |
| `verify(mock, atLeastOnce())` / `atLeast(n)` / `atMost(n)` | Mínimo/máximo. |
| `verify(mock, timeout(ms))` | Se llamó dentro de un tiempo (asincronía). |
| `verifyNoInteractions(mock)` | No se usó el mock en absoluto. |
| `verifyNoMoreInteractions(mock)` | No hubo llamadas **adicionales** a las verificadas. |
| `verifyZeroInteractions` | Obsoleto (usar `verifyNoInteractions`). |
| `InOrder` | Verifica el **orden** de las llamadas. |
| `ArgumentCaptor<T>` | Captura el argumento con el que se llamó para inspeccionarlo. |
| `captor.capture()` | Se usa dentro de `verify(...)`. |
| `captor.getValue()` / `getAllValues()` | Lee lo capturado. |
| `ArgumentMatchers` | `any`, `eq`, etc. también sirven en `verify`. |
| `MockitoAnnotations.openMocks(this)` | Inicializa `@Mock` sin extensión. |
| `Mockito.reset(mock)` | Limpia stubs/llamadas. |
| `Mockito.clearInvocations` | Limpia solo el historial. |
| `mockingDetails(x).getInvocations()` | Inspeccionar llamadas. |

### Ejemplos
```java
@Test
void crear_guardaYEnviaEmail() {
    when(repo.save(any(Pedido.class))).thenAnswer(inv -> inv.getArgument(0));

    service.crear(new PedidoRequest("Ana", "ana@mail.com"));

    verify(repo).save(any(Pedido.class));                       // 1 vez
    verify(email, times(1)).enviar(eq("ana@mail.com"), anyString());
    verify(auditoria, never()).registrar(any());
    verifyNoMoreInteractions(email);                            // no hubo otras llamadas
}

// Capturar argumento: verificar QUÉ se guardó
@Captor private ArgumentCaptor<Pedido> pedidoCaptor;

@Test
void crear_guardaPedidoConEstadoPendiente() {
    service.crear(request);

    verify(repo).save(pedidoCaptor.capture());
    Pedido guardado = pedidoCaptor.getValue();
    assertThat(guardado.getEstado()).isEqualTo(Estado.PENDIENTE);
    assertThat(guardado.getCliente()).isEqualTo("Ana");
}

// Orden de llamadas
InOrder orden = inOrder(repo, email);
orden.verify(repo).save(any());
orden.verify(email).enviar(any(), any());

// Nunca se debe tocar el repositorio si la validación falla
@Test
void crear_sinDatos_noGuarda() {
    assertThrows(IllegalArgumentException.class, () -> service.crear(null));
    verifyNoInteractions(repo);
}

// Verificar asíncrono
verify(email, timeout(1000)).enviar(any(), any());
```

### Qué verificar (y qué no)
| ✓ Verifica | ✗ Evita |
|---|---|
| Efectos importantes (se guardó, se envió, se publicó el evento) | Verificar cada llamada interna (tests frágiles ligados a la implementación) |
| Que **no** se llamó a algo cuando no debía | `verifyNoMoreInteractions` en todas partes |
| Argumentos clave (con captor) | Mockear tipos de valor (`String`, `List`, DTOs simples): usa objetos reales |

> Regla: **no mockees lo que no te pertenece** (clases de librerías complejas); envuélvelas en tu propia clase y mockea el envoltorio.

## 8. Tests unitarios de capas

Sin levantar Spring: rápidos (milisegundos).

| Capa | Qué se prueba | Qué se mockea |
|---|---|---|
| **Service** | Lógica de negocio. | Repositorios, clientes externos, otros services. |
| **Controller** | (Como unitario) Delegación y respuestas. Más habitual probarlo con MockMvc (sección 11). | Service. |
| **Mapper/Util** | Conversiones, cálculos. | Nada (clases puras). |
| **Validator** | Reglas de validación. | Dependencias externas. |
| **Repository** | **No** se mockea: se prueba con BD real/H2 (`@DataJpaTest`). | — |

### Ejemplo: test unitario de Service completo
```java
@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock private UsuarioRepository repo;
    @Mock private PasswordEncoder encoder;
    @Mock private EmailService email;
    @InjectMocks private UsuarioService service;

    @Test
    @DisplayName("registrar: guarda con la contraseña cifrada y envía bienvenida")
    void registrar_ok() {
        // Arrange
        RegistroRequest req = new RegistroRequest("ana", "ana@mail.com", "Clave123");
        when(repo.existsByEmail("ana@mail.com")).thenReturn(false);
        when(encoder.encode("Clave123")).thenReturn("HASH");
        when(repo.save(any(Usuario.class))).thenAnswer(inv -> {
            Usuario u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });

        // Act
        UsuarioResponse resp = service.registrar(req);

        // Assert
        assertThat(resp.id()).isEqualTo(1L);
        assertThat(resp.email()).isEqualTo("ana@mail.com");

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(repo).save(captor.capture());
        assertThat(captor.getValue().getPassword()).isEqualTo("HASH");   // no texto plano

        verify(email).enviarBienvenida("ana@mail.com");
    }

    @Test
    @DisplayName("registrar: email duplicado lanza conflicto y no guarda")
    void registrar_emailDuplicado() {
        when(repo.existsByEmail("ana@mail.com")).thenReturn(true);

        assertThatThrownBy(() -> service.registrar(new RegistroRequest("ana", "ana@mail.com", "x")))
                .isInstanceOf(EmailDuplicadoException.class);

        verify(repo, never()).save(any());
        verifyNoInteractions(email);
    }
}
```

### Ejemplo: test de clase pura (sin mocks)
```java
class CalculadoraDescuentoTest {

    private final CalculadoraDescuento calc = new CalculadoraDescuento();

    @ParameterizedTest
    @CsvSource({"100,0,100", "100,10,90", "200,50,100"})
    void aplicarDescuento(double precio, int porcentaje, double esperado) {
        assertThat(calc.aplicar(precio, porcentaje)).isEqualTo(esperado);
    }

    @Test
    void porcentajeNegativo_lanzaExcepcion() {
        assertThatThrownBy(() -> calc.aplicar(100, -5))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
```

### Ejemplo: test de un Mapper
```java
class UsuarioMapperTest {
    private final UsuarioMapper mapper = new UsuarioMapper();

    @Test
    void toResponse_copiaCampos() {
        Usuario u = new Usuario(1L, "Ana", "ana@mail.com");
        UsuarioResponse r = mapper.toResponse(u);
        assertThat(r).usingRecursiveComparison()
                     .isEqualTo(new UsuarioResponse(1L, "Ana", "ana@mail.com"));
    }
}
```

## 9. Spring Boot Test: contexto e integración

| Término | Significado |
|---|---|
| `@SpringBootTest` | Levanta el **contexto completo** de Spring (como la app real). Test de integración. |
| `webEnvironment` | Tipo de entorno web del test. |
| `MOCK` | (Defecto) Entorno web **simulado**, sin servidor real. Se usa con `MockMvc`. |
| `RANDOM_PORT` | Servidor **real** en puerto aleatorio. Se usa con `TestRestTemplate`/`WebTestClient`. |
| `DEFINED_PORT` | Servidor real en el puerto configurado. |
| `NONE` | Sin entorno web. |
| `@LocalServerPort` | Inyecta el puerto aleatorio. |
| `classes = App.class` | Indica la clase de configuración (si no se encuentra sola). |
| `properties = {"a=b"}` | Propiedades solo para ese test. |
| `args` | Argumentos de línea de comandos. |
| `@AutoConfigureMockMvc` | Configura `MockMvc` dentro de `@SpringBootTest`. |
| `@AutoConfigureTestDatabase` | Controla el reemplazo de la BD por una embebida. |
| `@ActiveProfiles("test")` | Activa el perfil `test` (carga `application-test.yml`). |
| `@TestPropertySource` | Propiedades/archivo para el test. |
| `@DynamicPropertySource` | Propiedades calculadas en ejecución (URL de un contenedor). |
| `@TestConfiguration` | Configuración **solo para tests** (beans de prueba). |
| `@Import(Clase.class)` | Importa beans/configuración al contexto del test. |
| `@MockBean` | Reemplaza/añade un **mock** en el contexto de Spring (deprecado en Boot 3.4 → `@MockitoBean`). |
| `@SpyBean` | Espía un bean existente (→ `@MockitoSpyBean`). |
| `@MockitoBean` / `@MockitoSpyBean` | Reemplazos modernos (Spring Framework 6.2 / Boot 3.4+). |
| `@DirtiesContext` | Marca el contexto como "sucio": se recrea tras el test (lento; usar solo si hace falta). |
| `@Transactional` (en test) | Cada test se ejecuta en una transacción que hace **rollback** al terminar. |
| `@Rollback` / `@Commit` | Controla si se revierte o se confirma. |
| `@Sql` | Ejecuta scripts SQL antes/después del test. |
| `@SqlGroup` / `@SqlConfig` | Varios scripts / configuración. |
| `@Autowired` | Inyecta beans reales en el test. |
| `@TestExecutionListeners` | Personaliza los listeners de test. |
| `@Timed` / `@Repeat` | Utilidades de Spring Test. |
| `@EnabledIf` / `@DisabledIf` (Spring) | Condicionales con SpEL. |
| `@RecordApplicationEvents` / `ApplicationEvents` | Capturar eventos publicados. |
| `OutputCaptureExtension` / `CapturedOutput` | Capturar la salida de consola/logs. |
| **Caché de contexto** | Spring reutiliza el contexto entre clases de test con la **misma configuración**; cada configuración distinta (distintos `@MockBean`, propiedades) crea uno nuevo y ralentiza. |
| `ApplicationContextRunner` | Probar auto-configuraciones/condiciones sin levantar toda la app. |
| `TestEntityManager` | EntityManager para tests JPA. |
| `ReflectionTestUtils` | Acceder a campos privados (`setField`, `getField`, `invokeMethod`). |
| `ResourceUtils` / `ClassPathResource` | Leer archivos de `src/test/resources`. |

### Ejemplo: test de integración completo
```java
@SpringBootTest
@ActiveProfiles("test")
class PedidoServiceIntegrationTest {

    @Autowired private PedidoService service;       // bean REAL
    @Autowired private PedidoRepository repo;       // BD real/H2

    @MockBean private EmailService email;           // solo el servicio externo se simula

    @Test
    @Transactional                                  // rollback automático al terminar
    void crear_persisteYEnvia() {
        PedidoResponse r = service.crear(new PedidoRequest("Ana", "ana@mail.com"));

        assertThat(repo.findById(r.id())).isPresent();
        verify(email).enviar(eq("ana@mail.com"), any());
    }
}
```

### Ejemplo: servidor real con puerto aleatorio
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApiIntegrationTest {

    @LocalServerPort private int puerto;
    @Autowired private TestRestTemplate rest;

    @Test
    void health() {
        ResponseEntity<String> resp =
                rest.getForEntity("http://localhost:" + puerto + "/actuator/health", String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
```

### Ejemplo: propiedades y configuración de test
```java
@SpringBootTest(properties = {"app.feature.nueva=true", "spring.main.lazy-initialization=true"})
class ConPropiedadesTest { ... }

@TestPropertySource(locations = "classpath:test.properties", properties = "app.x=1")
class OtroTest { ... }

@SpringBootTest
@Import(TestConfig.class)
class ConConfigTest { ... }

@TestConfiguration
public class TestConfig {
    @Bean
    @Primary                                         // reemplaza al bean real en tests
    public Clock clockFijo() {
        return Clock.fixed(Instant.parse("2026-01-01T10:00:00Z"), ZoneOffset.UTC);
    }
}
```

### Ejemplo: `@Sql`
```java
@SpringBootTest
class ConDatosTest {

    @Test
    @Sql(scripts = "/datos/usuarios.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM usuarios", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void test() { ... }
}
```

### Ejemplo: `ReflectionTestUtils`
```java
ReflectionTestUtils.setField(service, "limite", 5);                       // campo privado
Object valor = ReflectionTestUtils.getField(service, "cache");
ReflectionTestUtils.invokeMethod(service, "metodoPrivado", "arg");
```

### Ejemplo: capturar logs y eventos
```java
@ExtendWith(OutputCaptureExtension.class)
class LogTest {
    @Test
    void escribeLog(CapturedOutput output) {
        service.procesar();
        assertThat(output).contains("Procesando pedido");
    }
}

@SpringBootTest
@RecordApplicationEvents
class EventosTest {
    @Autowired ApplicationEvents events;
    @Autowired PedidoService service;

    @Test
    void publicaEvento() {
        service.crear(request);
        assertThat(events.stream(PedidoCreadoEvent.class)).hasSize(1);
    }
}
```

### Ejemplo: `@MockBean` (clásico) vs `@MockitoBean` (nuevo)
```java
// Spring Boot ≤ 3.3
@MockBean private EmailService email;
@SpyBean  private AuditoriaService auditoria;

// Spring Boot 3.4+ (Spring Framework 6.2)
@MockitoBean    private EmailService email;
@MockitoSpyBean private AuditoriaService auditoria;
```

### `@Mock` vs `@MockBean`
| | `@Mock` (Mockito) | `@MockBean` / `@MockitoBean` (Spring) |
|---|---|---|
| Requiere Spring | No | Sí |
| Dónde vive | Solo en el test | Dentro del contexto de Spring (reemplaza el bean real) |
| Velocidad | Muy rápido | Más lento (contexto) |
| Se inyecta con | `@InjectMocks` | `@Autowired` / otros beans lo usan |
| Usar en | Tests unitarios | Slices e integración |

### `@SpringBootTest` vs slices
| | `@SpringBootTest` | Slices (`@WebMvcTest`, `@DataJpaTest`...) |
|---|---|---|
| Contexto | Completo | Solo la capa necesaria |
| Velocidad | Lento | Más rápido |
| Cuándo | Flujos completos, integración real | Probar una capa aislada |

> **Rendimiento:** muchos `@MockBean` distintos o `@DirtiesContext` impiden reutilizar el contexto cacheado → tests lentos. Agrupa configuraciones comunes en una clase base.

## 10. Slices de test

Cargan **solo la parte** de la aplicación necesaria.

| Anotación | Carga | No carga | Auto-configura |
|---|---|---|---|
| `@WebMvcTest` | Controllers, `@ControllerAdvice`, filtros, `WebMvcConfigurer`, Jackson | `@Service`, `@Repository`, `@Component` normales | `MockMvc` |
| `@WebFluxTest` | Controladores reactivos | Services, repos | `WebTestClient` |
| `@DataJpaTest` | Entities, repositorios, `EntityManager` | Controllers, services | H2 embebida, `TestEntityManager`, rollback |
| `@DataJdbcTest` | Repositorios Spring Data JDBC | — | BD embebida |
| `@JdbcTest` | `JdbcTemplate`, `DataSource` | — | BD embebida |
| `@DataMongoTest` / `@DataRedisTest` | Repositorios Mongo/Redis | — | Embebido/contenedor |
| `@JsonTest` | Jackson / `ObjectMapper` | — | `JacksonTester` |
| `@RestClientTest` | Clientes `RestClient`/`RestTemplate` | — | `MockRestServiceServer` |
| `@JooqTest` | jOOQ | — | |
| `@GraphQlTest` | Controladores GraphQL | — | |
| `@AutoConfigureWebTestClient` | — | — | `WebTestClient` en `@SpringBootTest` |
| `@ImportAutoConfiguration` | Auto-configs concretas | — | |
| `@Import` | Beans específicos que el slice no carga | — | |

### Dentro de `@WebMvcTest` hay que **proveer** (mock) lo que el controlador necesita
```java
@WebMvcTest(UsuarioController.class)        // solo ese controlador
class UsuarioControllerTest {
    @Autowired private MockMvc mockMvc;
    @MockBean private UsuarioService service;   // el service NO se carga: se mockea
    // ...
}
```

### Ejemplo: `@JsonTest`
```java
@JsonTest
class UsuarioJsonTest {

    @Autowired private JacksonTester<UsuarioResponse> json;

    @Test
    void serializa() throws Exception {
        UsuarioResponse r = new UsuarioResponse(1L, "Ana", "ana@mail.com");

        assertThat(json.write(r)).hasJsonPathStringValue("$.nombre");
        assertThat(json.write(r)).extractingJsonPathStringValue("$.nombre").isEqualTo("Ana");
        assertThat(json.write(r)).isEqualToJson("""
            {"id":1,"nombre":"Ana","email":"ana@mail.com"}
            """);
    }

    @Test
    void deserializa() throws Exception {
        String contenido = "{\"id\":1,\"nombre\":\"Ana\",\"email\":\"ana@mail.com\"}";
        assertThat(json.parse(contenido)).isEqualTo(new UsuarioResponse(1L, "Ana", "ana@mail.com"));
        assertThat(json.parseObject(contenido).nombre()).isEqualTo("Ana");
    }
}
```

### Ejemplo: `@RestClientTest` (probar un cliente HTTP)
```java
@RestClientTest(PaisClient.class)
class PaisClientTest {

    @Autowired private PaisClient client;
    @Autowired private MockRestServiceServer server;

    @Test
    void obtenerPais() {
        server.expect(requestTo("/paises/AR"))
              .andExpect(method(HttpMethod.GET))
              .andRespond(withSuccess("{\"codigo\":\"AR\",\"nombre\":\"Argentina\"}",
                                      MediaType.APPLICATION_JSON));

        Pais p = client.obtener("AR");

        assertThat(p.nombre()).isEqualTo("Argentina");
        server.verify();
    }

    @Test
    void error500() {
        server.expect(requestTo("/paises/XX")).andRespond(withServerError());
        assertThatThrownBy(() -> client.obtener("XX")).isInstanceOf(HttpServerErrorException.class);
    }
}
```

### Ejemplo: `ApplicationContextRunner` (probar auto-configuración)
```java
class MiAutoConfigTest {
    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(SaludoAutoConfiguration.class));

    @Test
    void creaBeanPorDefecto() {
        runner.run(ctx -> assertThat(ctx).hasSingleBean(SaludoService.class));
    }

    @Test
    void respetaPropiedad() {
        runner.withPropertyValues("saludo.mensaje=Hola").run(ctx ->
            assertThat(ctx.getBean(SaludoService.class).getMensaje()).isEqualTo("Hola"));
    }

    @Test
    void noSeCreaSiYaExiste() {
        runner.withBean(SaludoService.class, () -> new SaludoService("custom"))
              .run(ctx -> assertThat(ctx).hasSingleBean(SaludoService.class));
    }
}
```

## 11. Testing web con MockMvc

Simula peticiones HTTP **sin levantar servidor** (rápido).

| Término | Significado |
|---|---|
| `MockMvc` | Cliente simulado de Spring MVC. |
| `@WebMvcTest(Controller.class)` | Slice web (controlador + MVC). |
| `@AutoConfigureMockMvc` | `MockMvc` en `@SpringBootTest`. |
| `mockMvc.perform(request)` | Ejecuta una petición simulada. |
| `MockMvcRequestBuilders` | `get()`, `post()`, `put()`, `patch()`, `delete()`, `multipart()`, `options()`. |
| `.param("k","v")` | Query/form param. |
| `.header("X","v")` | Cabecera. |
| `.contentType(MediaType.APPLICATION_JSON)` | Content-Type. |
| `.accept(MediaType.APPLICATION_JSON)` | Accept. |
| `.content(json)` | Cuerpo. |
| `.cookie(...)` / `.sessionAttr(...)` / `.requestAttr(...)` | Cookies, sesión. |
| `.with(...)` | Post-procesadores (csrf, user, jwt, httpBasic). |
| `.andExpect(...)` | Verifica la respuesta. |
| `.andDo(print())` | Imprime la petición/respuesta (depuración). |
| `.andReturn()` | Devuelve el `MvcResult`. |
| `MockMvcResultMatchers` | `status()`, `content()`, `jsonPath()`, `header()`, `view()`, `model()`, `redirectedUrl()`, `cookie()`, `forwardedUrl()`. |
| `status().isOk()` / `isCreated()` / `isNoContent()` / `isBadRequest()` / `isUnauthorized()` / `isForbidden()` / `isNotFound()` / `is(409)` | Estado HTTP. |
| `jsonPath("$.campo")` | Consulta en el JSON de respuesta. |
| `jsonPath("$.lista", hasSize(3))` | Con matcher Hamcrest. |
| `jsonPath("$.lista[0].nombre")` | Índices. |
| `jsonPath("$[?(@.edad>18)]")` | Filtros. |
| `.exists()` / `.doesNotExist()` / `.isArray()` / `.isEmpty()` / `.value(x)` | Verificaciones de jsonPath. |
| `content().json(...)` | Compara JSON (no estricto por defecto). |
| `content().string(...)` / `contentType(...)` | Texto / tipo. |
| `header().string("Location", ...)` | Cabeceras. |
| `view().name("usuarios")` | Nombre de vista (MVC con Thymeleaf). |
| `model().attributeExists("x")` / `attribute("x", y)` / `attributeHasErrors` | Modelo MVC. |
| `redirectedUrl("/login")` | Redirección. |
| `MockMvcBuilders.standaloneSetup(controller)` | `MockMvc` sin contexto Spring (unitario puro). |
| `MockMvcBuilders.webAppContextSetup(ctx)` | Con contexto web. |
| `ObjectMapper` | Para convertir objetos a JSON en el `.content(...)`. |
| `MockMultipartFile` | Archivo simulado para pruebas de subida. |
| `MvcResult` | Resultado accesible por código. |
| `async` | `asyncStarted()` + `asyncDispatch(...)` para respuestas asíncronas. |

### Ejemplo: `@WebMvcTest` completo (CRUD)
```java
@WebMvcTest(UsuarioController.class)
class UsuarioControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private UsuarioService service;

    @Test
    void listar_devuelve200YLista() throws Exception {
        when(service.listar()).thenReturn(List.of(
                new UsuarioResponse(1L, "Ana", "ana@mail.com"),
                new UsuarioResponse(2L, "Luis", "luis@mail.com")));

        mockMvc.perform(get("/api/usuarios").accept(MediaType.APPLICATION_JSON))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$", hasSize(2)))
               .andExpect(jsonPath("$[0].nombre").value("Ana"))
               .andExpect(jsonPath("$[1].email", is("luis@mail.com")));
    }

    @Test
    void buscar_existente() throws Exception {
        when(service.buscar(1L)).thenReturn(new UsuarioResponse(1L, "Ana", "ana@mail.com"));

        mockMvc.perform(get("/api/usuarios/{id}", 1L))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(1))
               .andExpect(jsonPath("$.nombre").value("Ana"));
    }

    @Test
    void buscar_inexistente_devuelve404() throws Exception {
        when(service.buscar(99L)).thenThrow(new UsuarioNoEncontradoException(99L));

        mockMvc.perform(get("/api/usuarios/99"))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.mensaje").value(containsString("99")));
    }

    @Test
    void crear_devuelve201() throws Exception {
        UsuarioRequest req = new UsuarioRequest("Ana", "ana@mail.com");
        when(service.crear(any(UsuarioRequest.class)))
                .thenReturn(new UsuarioResponse(1L, "Ana", "ana@mail.com"));

        mockMvc.perform(post("/api/usuarios")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
               .andExpect(status().isCreated())
               .andExpect(header().string("Location", containsString("/api/usuarios/1")))
               .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void crear_sinNombre_devuelve400ConErrores() throws Exception {
        mockMvc.perform(post("/api/usuarios")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"email\":\"ana@mail.com\"}"))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.nombre").exists());
        verifyNoInteractions(service);                          // nunca llegó al service
    }

    @Test
    void crear_jsonMalFormado_devuelve400() throws Exception {
        mockMvc.perform(post("/api/usuarios")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{no es json"))
               .andExpect(status().isBadRequest());
    }

    @Test
    void actualizar() throws Exception {
        mockMvc.perform(put("/api/usuarios/{id}", 1L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(new UsuarioRequest("Ana M", "ana@mail.com"))))
               .andExpect(status().isOk());
        verify(service).actualizar(eq(1L), any());
    }

    @Test
    void eliminar_devuelve204() throws Exception {
        mockMvc.perform(delete("/api/usuarios/1"))
               .andExpect(status().isNoContent());
        verify(service).eliminar(1L);
    }

    @Test
    void conParametrosYCabeceras() throws Exception {
        mockMvc.perform(get("/api/usuarios")
                    .param("page", "0").param("size", "5").param("sort", "nombre,asc")
                    .header("X-Request-Id", "abc"))
               .andExpect(status().isOk());
    }

    @Test
    void parametroConTipoIncorrecto_400() throws Exception {
        mockMvc.perform(get("/api/usuarios/abc"))               // id debería ser número
               .andExpect(status().isBadRequest());
    }

    @Test
    void metodoNoPermitido_405() throws Exception {
        mockMvc.perform(patch("/api/usuarios"))
               .andExpect(status().isMethodNotAllowed());
    }
}
```

### Ejemplo: paginación en el controlador
```java
@Test
void listarPaginado() throws Exception {
    Page<UsuarioResponse> pagina = new PageImpl<>(List.of(new UsuarioResponse(1L, "Ana", "a@a.com")),
                                                  PageRequest.of(0, 5), 1);
    when(service.listar(any(Pageable.class))).thenReturn(pagina);

    mockMvc.perform(get("/api/usuarios?page=0&size=5"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.content", hasSize(1)))
           .andExpect(jsonPath("$.totalElements").value(1));

    ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
    verify(service).listar(captor.capture());
    assertThat(captor.getValue().getPageSize()).isEqualTo(5);
}
```

### Ejemplo: subida de archivo
```java
@Test
void subirArchivo() throws Exception {
    MockMultipartFile archivo = new MockMultipartFile(
            "archivo", "foto.png", MediaType.IMAGE_PNG_VALUE, "contenido".getBytes());

    mockMvc.perform(multipart("/api/archivos/subir").file(archivo))
           .andExpect(status().isOk());
}
```

### Ejemplo: controlador MVC con vistas (Thymeleaf)
```java
@Test
void listarVista() throws Exception {
    when(service.listar()).thenReturn(List.of());

    mockMvc.perform(get("/usuarios"))
           .andExpect(status().isOk())
           .andExpect(view().name("usuarios"))
           .andExpect(model().attributeExists("usuarios"));
}

@Test
void guardarConErrores_vuelveAlFormulario() throws Exception {
    mockMvc.perform(post("/usuarios/guardar").param("nombre", "").with(csrf()))
           .andExpect(status().isOk())
           .andExpect(view().name("form-usuario"))
           .andExpect(model().attributeHasFieldErrors("usuario", "nombre"));
}

@Test
void guardarOk_redirige() throws Exception {
    mockMvc.perform(post("/usuarios/guardar").param("nombre", "Ana").with(csrf()))
           .andExpect(status().is3xxRedirection())
           .andExpect(redirectedUrl("/usuarios"));
}
```

### Ejemplo: leer el resultado por código
```java
MvcResult resultado = mockMvc.perform(get("/api/usuarios/1")).andReturn();
String json = resultado.getResponse().getContentAsString();
UsuarioResponse r = objectMapper.readValue(json, UsuarioResponse.class);
assertThat(r.nombre()).isEqualTo("Ana");
```

### Ejemplo: `standaloneSetup` (sin Spring, unitario puro)
```java
class UsuarioControllerStandaloneTest {
    private MockMvc mockMvc;
    private final UsuarioService service = mock(UsuarioService.class);

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new UsuarioController(service))
                                 .setControllerAdvice(new GlobalExceptionHandler())
                                 .build();
    }
    // tests...
}
```

### Ejemplo: sintaxis de JsonPath
| Expresión | Significado |
|---|---|
| `$` | Raíz. |
| `$.nombre` | Campo `nombre`. |
| `$.direccion.ciudad` | Anidado. |
| `$[0]` | Primer elemento del arreglo raíz. |
| `$.items[0].precio` | Elemento de un arreglo. |
| `$.items[*].nombre` | Todos los nombres. |
| `$.items.length()` | Cantidad. |
| `$[?(@.edad > 18)]` | Filtro. |
| `$..id` | Búsqueda recursiva de `id`. |

### Qué verificar en un test de controlador
| Aspecto | Ejemplo |
|---|---|
| Código HTTP | `status().isCreated()` |
| Cuerpo JSON | `jsonPath("$.nombre").value("Ana")` |
| Cabeceras | `header().string("Location", ...)` |
| Delegación al service | `verify(service).crear(any())` |
| Validación | 400 y `verifyNoInteractions(service)` |
| Errores de negocio | 404 / 409 / 422 vía `@ControllerAdvice` |
| Seguridad | 401 sin usuario, 403 con rol incorrecto |

## 12. Testing con TestRestTemplate y WebTestClient

Con **servidor real** (`RANDOM_PORT`): atraviesa filtros, serialización, seguridad y red reales.

| Término | Significado |
|---|---|
| `TestRestTemplate` | Cliente HTTP para tests de integración (no lanza excepciones en 4xx/5xx; devuelve la respuesta). |
| `WebTestClient` | Cliente fluido (reactivo y MVC) con aserciones integradas. |
| `.withBasicAuth(u, p)` | Autenticación Basic en `TestRestTemplate`. |
| `getForEntity` / `postForEntity` / `exchange` / `getForObject` | Métodos de `TestRestTemplate`. |
| `HttpEntity` / `HttpHeaders` | Cuerpo + cabeceras. |
| `@AutoConfigureWebTestClient` | Configura `WebTestClient`. |
| `expectStatus()` / `expectBody()` / `expectHeader()` | Aserciones de `WebTestClient`. |
| `jsonPath(...)` | Verificar JSON. |
| `RestAssured` | Alternativa muy legible (`given().when().then()`). |

### Ejemplo: `TestRestTemplate`
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class UsuarioApiIT {

    @Autowired private TestRestTemplate rest;
    @Autowired private UsuarioRepository repo;

    @BeforeEach void limpiar() { repo.deleteAll(); }

    @Test
    void crearYConsultar() {
        // crear
        ResponseEntity<UsuarioResponse> creado = rest.postForEntity(
                "/api/usuarios", new UsuarioRequest("Ana", "ana@mail.com"), UsuarioResponse.class);

        assertThat(creado.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Long id = creado.getBody().id();

        // consultar
        ResponseEntity<UsuarioResponse> obtenido =
                rest.getForEntity("/api/usuarios/{id}", UsuarioResponse.class, id);
        assertThat(obtenido.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(obtenido.getBody().nombre()).isEqualTo("Ana");
    }

    @Test
    void listaGenerica() {
        ResponseEntity<List<UsuarioResponse>> resp = rest.exchange(
                "/api/usuarios", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<UsuarioResponse>>() {});
        assertThat(resp.getBody()).isEmpty();
    }

    @Test
    void conAutenticacion() {
        ResponseEntity<String> resp = rest.withBasicAuth("admin", "1234")
                                          .getForEntity("/api/admin/usuarios", String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void sinTokenDevuelve401() {
        ResponseEntity<String> resp = rest.getForEntity("/api/pedidos", String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void conTokenJwt() {
        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth(obtenerToken());
        ResponseEntity<String> resp = rest.exchange("/api/pedidos", HttpMethod.GET,
                new HttpEntity<>(h), String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
```

### Ejemplo: `WebTestClient`
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UsuarioWebTestClientTest {

    @Autowired private WebTestClient client;

    @Test
    void crear() {
        client.post().uri("/api/usuarios")
              .contentType(MediaType.APPLICATION_JSON)
              .bodyValue(new UsuarioRequest("Ana", "ana@mail.com"))
              .exchange()
              .expectStatus().isCreated()
              .expectHeader().exists("Location")
              .expectBody()
              .jsonPath("$.nombre").isEqualTo("Ana")
              .jsonPath("$.id").isNotEmpty();
    }

    @Test
    void listar() {
        client.get().uri("/api/usuarios")
              .exchange()
              .expectStatus().isOk()
              .expectBodyList(UsuarioResponse.class).hasSize(0);
    }
}
```

### Ejemplo: RestAssured
```java
@SpringBootTest(webEnvironment = RANDOM_PORT)
class UsuarioRestAssuredTest {
    @LocalServerPort int puerto;

    @BeforeEach void setUp() { RestAssured.port = puerto; }

    @Test
    void crear() {
        given().contentType(ContentType.JSON)
               .body("{\"nombre\":\"Ana\",\"email\":\"ana@mail.com\"}")
        .when().post("/api/usuarios")
        .then().statusCode(201)
               .body("nombre", equalTo("Ana"));
    }
}
```

### MockMvc vs TestRestTemplate/WebTestClient
| | `MockMvc` | `TestRestTemplate` / `WebTestClient` |
|---|---|---|
| Servidor real | No (simulado) | Sí (puerto real) |
| Velocidad | Rápido | Más lento |
| Cubre red/serialización real | Parcial | Sí |
| Con `@WebMvcTest` (slice) | Sí | No (necesita `@SpringBootTest`) |
| Transacciones del test | `@Transactional` hace rollback | **No**: el servidor corre en otro hilo; hay que limpiar los datos manualmente |

> **Importante:** con servidor real, `@Transactional` en el test **no** revierte lo que hace el servidor (otro hilo). Limpia en `@BeforeEach/@AfterEach`.

## 13. Testing de la capa de datos

| Término | Significado |
|---|---|
| `@DataJpaTest` | Slice JPA: carga solo entidades y repositorios; usa BD embebida; cada test en una **transacción con rollback**. |
| `TestEntityManager` | `EntityManager` simplificado: `persist`, `persistAndFlush`, `flush`, `clear`, `find`, `refresh`. |
| `@AutoConfigureTestDatabase(replace = NONE)` | Usa **tu BD configurada** (p. ej. Testcontainers) en vez de H2. |
| `replace = ANY` | (Defecto) Reemplaza cualquier `DataSource` por una BD embebida. |
| `@Import` | Añade beans que el slice no carga (ej. `@Import(UsuarioService.class)`). |
| `flush()` | Fuerza el envío de SQL a la BD (necesario para detectar errores de constraints). |
| `clear()` | Limpia el contexto de persistencia para forzar lecturas reales de BD. |
| `@Sql` | Carga datos desde SQL. |
| `@Rollback(false)` / `@Commit` | Confirmar los cambios del test. |
| `@Transactional` | Rollback automático al final del test. |
| `@DirtiesContext` | Recrea el contexto (evitar). |
| `spring.jpa.hibernate.ddl-auto=create-drop` | Esquema generado a partir de las entidades en test. |
| `@JdbcTest` | Probar `JdbcTemplate`. |
| `@DataJdbcTest` | Probar Spring Data JDBC. |
| `@DataMongoTest` | Probar Mongo. |

### Ejemplo: `@DataJpaTest` de un repositorio
```java
@DataJpaTest
class UsuarioRepositoryTest {

    @Autowired private UsuarioRepository repo;
    @Autowired private TestEntityManager em;

    @BeforeEach
    void datos() {
        em.persist(new Usuario("Ana", "ana@mail.com", Estado.ACTIVO, 25));
        em.persist(new Usuario("Luis", "luis@mail.com", Estado.INACTIVO, 40));
        em.persist(new Usuario("Eva", "eva@mail.com", Estado.ACTIVO, 17));
        em.flush();
        em.clear();
    }

    @Test
    void findByEmail_devuelveUsuario() {
        assertThat(repo.findByEmail("ana@mail.com"))
                .isPresent()
                .get().extracting(Usuario::getNombre).isEqualTo("Ana");
    }

    @Test
    void findByEmail_inexistente_devuelveVacio() {
        assertThat(repo.findByEmail("nadie@mail.com")).isEmpty();
    }

    @Test
    void findByEstado_filtra() {
        assertThat(repo.findByEstado(Estado.ACTIVO))
                .hasSize(2)
                .extracting(Usuario::getNombre)
                .containsExactlyInAnyOrder("Ana", "Eva");
    }

    @Test
    void findByEdadBetween() {
        assertThat(repo.findByEdadBetween(18, 30)).extracting(Usuario::getNombre).containsExactly("Ana");
    }

    @Test
    void existsByEmail() {
        assertThat(repo.existsByEmail("ana@mail.com")).isTrue();
        assertThat(repo.existsByEmail("x@x.com")).isFalse();
    }

    @Test
    void countByEstado() {
        assertThat(repo.countByEstado(Estado.ACTIVO)).isEqualTo(2);
    }

    @Test
    void paginacion() {
        Page<Usuario> pag = repo.findAll(PageRequest.of(0, 2, Sort.by("nombre")));
        assertThat(pag.getContent()).hasSize(2);
        assertThat(pag.getTotalElements()).isEqualTo(3);
        assertThat(pag.getTotalPages()).isEqualTo(2);
    }

    @Test
    void save_asignaId() {
        Usuario guardado = repo.save(new Usuario("Nuevo", "n@mail.com", Estado.ACTIVO, 30));
        assertThat(guardado.getId()).isNotNull();
    }

    @Test
    void emailUnico_lanzaExcepcion() {
        assertThatThrownBy(() -> repo.saveAndFlush(new Usuario("Otra", "ana@mail.com", Estado.ACTIVO, 20)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @Sql("/datos/usuarios-extra.sql")
    void conSql() { ... }
}
```

### Ejemplo: probar `@Query` con `@Modifying`
```java
@Test
void desactivarInactivos_actualizaFilas() {
    int filas = repo.desactivarInactivos(Estado.INACTIVO, LocalDate.now().plusDays(1));
    assertThat(filas).isGreaterThan(0);

    em.clear();   // importante: relee desde la BD y no desde el contexto de persistencia
    assertThat(repo.findAll()).allMatch(u -> u.getEstado() == Estado.INACTIVO);
}
```

### Ejemplo: relaciones y N+1
```java
@Test
void buscarConPedidos_traeLosPedidosEnUnaConsulta() {
    Cliente c = em.persist(new Cliente("Ana"));
    em.persist(new Pedido(c));
    em.persist(new Pedido(c));
    em.flush(); em.clear();

    Cliente cargado = repo.buscarConPedidos(c.getId()).orElseThrow();

    assertThat(Hibernate.isInitialized(cargado.getPedidos())).isTrue();   // fetch join funcionó
    assertThat(cargado.getPedidos()).hasSize(2);
}
```

### Ejemplo: `@DataJpaTest` con la BD real (no H2)
```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class RepoContraBdRealTest { ... }
```

### Ejemplo: `application-test.yml`
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1
    driver-class-name: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
logging:
  level:
    org.hibernate.SQL: DEBUG
```

### Ejemplo: script SQL de datos
```sql
-- src/test/resources/datos/usuarios-extra.sql
INSERT INTO usuarios (nombre, email, estado) VALUES ('Zoe', 'zoe@mail.com', 'ACTIVO');
```

### Cuándo `flush()` y `clear()`
| Situación | Qué hacer |
|---|---|
| Probar constraints (UNIQUE, NOT NULL) | `saveAndFlush` / `em.flush()` para que Hibernate lance el SQL |
| Verificar que se lee de la BD y no de la caché de primer nivel | `em.flush()` + `em.clear()` antes de consultar |
| Probar lazy loading | `clear()` y luego acceder a la relación |
| Después de `@Modifying` | `em.clear()` (la consulta no actualiza el contexto) |

> **H2 vs BD real:** H2 puede comportarse distinto (funciones SQL, tipos, `JSON`, índices). Para consultas nativas o específicas del motor usa **Testcontainers** con la BD real.

## 14. Testcontainers

Levanta **servicios reales en contenedores Docker** (BD, Kafka, Redis...) durante los tests. Requiere Docker.

| Término | Significado |
|---|---|
| **Testcontainers** | Librería que gestiona contenedores Docker para tests. |
| `@Testcontainers` | Extensión JUnit 5 que gestiona el ciclo de vida de los contenedores. |
| `@Container` | Marca un contenedor; si es `static`, se comparte entre tests de la clase; si es de instancia, uno por test. |
| `PostgreSQLContainer` / `MySQLContainer` / `MongoDBContainer` / `GenericContainer` / `KafkaContainer` / `RedisContainer` | Contenedores de módulos. |
| `@DynamicPropertySource` | Inyecta URL/credenciales del contenedor en las propiedades de Spring. |
| `@ServiceConnection` | (Boot 3.1+) Configura la conexión **automáticamente**, sin `@DynamicPropertySource`. |
| `@TestConfiguration(proxyBeanMethods = false)` | Para declarar contenedores como beans. |
| `SpringApplication.from(...).with(...)` | Arrancar la app en desarrollo con contenedores. |
| **Singleton container pattern** | Arrancar el contenedor una sola vez para toda la suite (más rápido). |
| `.withReuse(true)` | Reutiliza el contenedor entre ejecuciones. |
| `.withInitScript("init.sql")` | Script inicial. |
| `.withDatabaseName/.withUsername/.withPassword` | Configuración de BD. |
| `jdbc:tc:postgresql:16:///test` | URL especial de Testcontainers (sin código). |
| **Ryuk** | Contenedor que limpia los recursos al terminar. |
| `DockerImageName.parse("postgres:16")` | Imagen a usar. |
| `Wait.forHttp` / `Wait.forLogMessage` | Estrategias de espera de arranque. |

### Ejemplo con `@ServiceConnection` (recomendado, Boot 3.1+)
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-testcontainers</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>postgresql</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>
```
```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class UsuarioRepositoryContainerTest {

    @Container
    @ServiceConnection                                   // configura datasource.url/user/password solo
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired private UsuarioRepository repo;

    @Test
    void guardaYBusca() {
        repo.save(new Usuario("Ana", "ana@mail.com"));
        assertThat(repo.findByEmail("ana@mail.com")).isPresent();
    }
}
```

### Ejemplo con `@DynamicPropertySource` (Boot < 3.1)
```java
@SpringBootTest
@Testcontainers
class IntegracionTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("test").withUsername("test").withPassword("test");

    @DynamicPropertySource
    static void propiedades(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }
}
```

### Ejemplo: contenedor compartido por toda la suite (más rápido)
```java
@TestConfiguration(proxyBeanMethods = false)
public class ContenedoresConfig {
    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgres() {
        return new PostgreSQLContainer<>("postgres:16-alpine").withReuse(true);
    }
}

@SpringBootTest
@Import(ContenedoresConfig.class)
class MiTest { ... }
```

### Ejemplo: otros servicios
```java
@Container @ServiceConnection
static MongoDBContainer mongo = new MongoDBContainer("mongo:7");

@Container @ServiceConnection(name = "redis")
static GenericContainer<?> redis = new GenericContainer<>("redis:7").withExposedPorts(6379);

@Container @ServiceConnection
static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.6.0"));

// Contenedor genérico con espera
@Container
static GenericContainer<?> api = new GenericContainer<>("mi-api:1.0")
        .withExposedPorts(8080)
        .waitingFor(Wait.forHttp("/actuator/health").forStatusCode(200));
```

### Ejemplo: sin código, solo URL
```properties
spring.datasource.url=jdbc:tc:postgresql:16-alpine:///testdb
spring.datasource.driver-class-name=org.testcontainers.jdbc.ContainerDatabaseDriver
```

### Ventajas y requisitos
| | |
|---|---|
| ✓ | Pruebas contra el **mismo motor** que producción (sin sorpresas de H2). |
| ✓ | Aislamiento y reproducibilidad (cada ejecución parte limpia). |
| ✗ | Requiere **Docker** en la máquina y en el CI. |
| ✗ | Más lento que H2 (mitigar con contenedor `static` / reuse). |

## 15. Testing de seguridad

Requiere `spring-security-test`.

| Término | Significado |
|---|---|
| `@WithMockUser` | Simula un usuario autenticado (`username`, `roles`, `authorities`). |
| `@WithAnonymousUser` | Usuario anónimo. |
| `@WithUserDetails("ana")` | Usa un usuario real de tu `UserDetailsService`. |
| `@WithSecurityContext` | Contexto de seguridad personalizado. |
| `with(user("ana").roles("ADMIN"))` | Usuario simulado solo en esa petición. |
| `with(csrf())` | Añade token CSRF válido (obligatorio en POST/PUT/DELETE si CSRF está activo). |
| `with(httpBasic("u","p"))` | Autenticación Basic. |
| `with(jwt())` | JWT simulado (resource server). |
| `with(oauth2Login())` | Login OAuth2 simulado. |
| `with(anonymous())` | Petición anónima. |
| `@Import(SecurityConfig.class)` | `@WebMvcTest` **no** carga tu `SecurityConfig` por defecto: hay que importarla. |
| `SecurityMockMvcConfigurers.springSecurity()` | Aplica Security a un `MockMvc` construido a mano. |
| `status().isUnauthorized()` / `isForbidden()` | 401 / 403. |
| `redirectedUrlPattern("**/login")` | Redirección al login (apps con formulario). |

### Ejemplo: tests de reglas de acceso
```java
@WebMvcTest(ProductoController.class)
@Import(SecurityConfig.class)                          // usa TUS reglas de seguridad
class ProductoControllerSecurityTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private ProductoService service;
    @MockBean private JwtService jwtService;                   // dependencias de tus filtros
    @MockBean private UserDetailsService userDetailsService;

    @Test
    void sinAutenticar_rutaProtegida_401() throws Exception {
        mockMvc.perform(get("/api/pedidos"))
               .andExpect(status().isUnauthorized());
    }

    @Test
    void rutaPublica_sinAutenticar_200() throws Exception {
        mockMvc.perform(get("/api/productos"))
               .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void usuario_noPuedeBorrar_403() throws Exception {
        mockMvc.perform(delete("/api/productos/1").with(csrf()))
               .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void admin_puedeBorrar_204() throws Exception {
        mockMvc.perform(delete("/api/productos/1").with(csrf()))
               .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void postSinCsrf_403() throws Exception {
        mockMvc.perform(post("/api/productos").contentType(MediaType.APPLICATION_JSON).content("{}"))
               .andExpect(status().isForbidden());           // falta el token CSRF
    }

    @Test
    void conUsuarioEnLaPeticion() throws Exception {
        mockMvc.perform(get("/api/admin/usuarios").with(user("root").roles("ADMIN")))
               .andExpect(status().isOk());
    }

    @Test
    void conJwtSimulado() throws Exception {
        mockMvc.perform(get("/api/me")
                    .with(jwt().jwt(j -> j.subject("ana").claim("email", "ana@mail.com"))
                               .authorities(new SimpleGrantedAuthority("ROLE_USER"))))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.usuario").value("ana"));
    }

    @Test
    void conHttpBasic() throws Exception {
        mockMvc.perform(get("/api/usuarios").with(httpBasic("ana", "1234")))
               .andExpect(status().isOk());
    }
}
```

### Ejemplo: test parametrizado de roles
```java
@ParameterizedTest
@CsvSource({
    "USER,  403",
    "MANAGER, 403",
    "ADMIN, 204"
})
void borrar_segunRol(String rol, int estadoEsperado) throws Exception {
    mockMvc.perform(delete("/api/productos/1").with(csrf()).with(user("x").roles(rol)))
           .andExpect(status().is(estadoEsperado));
}
```

### Ejemplo: métodos protegidos con `@PreAuthorize`
```java
@SpringBootTest
class PedidoServiceSecurityTest {

    @Autowired private PedidoService service;

    @Test
    @WithMockUser(roles = "USER")
    void usuarioNoPuedeEliminarTodos() {
        assertThatThrownBy(() -> service.eliminarTodos()).isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminPuedeEliminarTodos() {
        assertThatNoException().isThrownBy(() -> service.eliminarTodos());
    }

    @Test
    @WithMockUser(username = "ana")
    void soloVeSusPedidos() {
        assertThat(service.listar()).allMatch(p -> p.getUsuario().equals("ana"));
    }
}
```

### Ejemplo: login real y uso del token (integración)
```java
@SpringBootTest
@AutoConfigureMockMvc
class AuthIT {
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper mapper;

    @Test
    void flujoCompleto() throws Exception {
        String respuesta = mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"username\":\"ana\",\"password\":\"Clave123!\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String token = mapper.readTree(respuesta).get("token").asText();

        mockMvc.perform(get("/api/pedidos").header("Authorization", "Bearer " + token))
               .andExpect(status().isOk());
    }

    @Test
    void passwordIncorrecta_401() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"username\":\"ana\",\"password\":\"mala\"}"))
               .andExpect(status().isUnauthorized());
    }
}
```

### Ejemplo: `@WithUserDetails`
```java
@Test
@WithUserDetails(value = "ana", userDetailsServiceBeanName = "customUserDetailsService")
void conUsuarioReal() throws Exception {
    mockMvc.perform(get("/api/perfil")).andExpect(status().isOk());
}
```

## 16. Mock de servicios externos

| Herramienta | Uso |
|---|---|
| `MockRestServiceServer` | Simula respuestas de `RestTemplate`/`RestClient` (sin red). |
| `@RestClientTest` | Slice para clientes HTTP. |
| **WireMock** | Servidor HTTP falso real: defines endpoints y respuestas. Muy completo (delays, errores, verificación). |
| `MockWebServer` (OkHttp) | Servidor HTTP falso ligero, ideal para `WebClient`. |
| `@MockBean` del cliente | Si solo quieres evitar la llamada: mockeas tu `PaisClient`. |
| **Contract testing (Pact / Spring Cloud Contract)** | Verificar el contrato con otro servicio. |
| `@SpringBootTest(properties = "api.url=http://localhost:${wiremock.server.port}")` | Apuntar el cliente al servidor falso. |
| `@EnableWireMock` / `@AutoConfigureWireMock` | Integración Spring de WireMock. |
| `GreenMail` | Servidor de correo falso. |
| `Awaitility` | Esperar resultados asíncronos. |

### Ejemplo: WireMock
```java
@SpringBootTest
@AutoConfigureWireMock(port = 0)                         // puerto aleatorio
@TestPropertySource(properties = "api.paises.url=http://localhost:${wiremock.server.port}")
class PaisServiceWireMockTest {

    @Autowired private PaisService service;

    @Test
    void obtenerPais() {
        stubFor(get(urlEqualTo("/paises/AR"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"codigo\":\"AR\",\"nombre\":\"Argentina\"}")));

        Pais p = service.obtener("AR");

        assertThat(p.nombre()).isEqualTo("Argentina");
        verify(getRequestedFor(urlEqualTo("/paises/AR")));
    }

    @Test
    void servidorCaido_reintentaOLanza() {
        stubFor(get(urlEqualTo("/paises/XX")).willReturn(aResponse().withStatus(503)));
        assertThatThrownBy(() -> service.obtener("XX")).isInstanceOf(ServicioNoDisponibleException.class);
    }

    @Test
    void timeout() {
        stubFor(get(urlEqualTo("/paises/LENTO")).willReturn(aResponse().withFixedDelay(5000).withStatus(200)));
        assertThatThrownBy(() -> service.obtener("LENTO")).isInstanceOf(ResourceAccessException.class);
    }
}
```

### Ejemplo: `MockWebServer`
```java
class ClienteWebClientTest {
    private MockWebServer server;
    private PaisClient client;

    @BeforeEach
    void setUp() throws IOException {
        server = new MockWebServer();
        server.start();
        client = new PaisClient(WebClient.create(server.url("/").toString()));
    }

    @AfterEach void tearDown() throws IOException { server.shutdown(); }

    @Test
    void obtener() throws Exception {
        server.enqueue(new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody("{\"codigo\":\"AR\",\"nombre\":\"Argentina\"}"));

        Pais p = client.obtener("AR").block();

        assertThat(p.nombre()).isEqualTo("Argentina");
        RecordedRequest req = server.takeRequest();
        assertThat(req.getPath()).isEqualTo("/paises/AR");
    }
}
```

### Ejemplo: simular un envío de correo
```java
@RegisterExtension
static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP);

@Test
void enviaCorreo() {
    emailService.enviar("ana@mail.com", "Hola");
    MimeMessage[] mensajes = greenMail.getReceivedMessages();
    assertThat(mensajes).hasSize(1);
    assertThat(mensajes[0].getSubject()).isEqualTo("Hola");
}
```

## 17. Testing de excepciones, tiempo y asincronía

| Tema | Herramienta |
|---|---|
| Excepciones | `assertThrows`, `assertThatThrownBy`, `@ExceptionHandler` probado con `MockMvc`. |
| **Tiempo** | Inyectar un `Clock` y usar `Clock.fixed(...)` en el test (evita `LocalDate.now()` impredecible). |
| `Clock.fixed(Instant, ZoneId)` | Reloj congelado. |
| `Clock.offset(clock, Duration)` | Reloj desplazado. |
| `MutableClock` | Reloj que se puede avanzar (implementación propia o de librerías). |
| **Asincronía** | `Awaitility` (`await().atMost(...).untilAsserted(...)`), `CompletableFuture.get(timeout)`, `verify(..., timeout(ms))`. |
| `@Async` en tests | Usar `@EnableAsync` con un executor síncrono en test, o esperar con Awaitility. |
| `SyncTaskExecutor` | Executor que ejecuta en el mismo hilo (hace determinista `@Async`). |
| `CountDownLatch` | Esperar a que ocurra algo en otro hilo. |
| **Scheduled** | No esperar el cron: invocar el método directamente o inyectar el scheduler. |
| `@RecordApplicationEvents` | Capturar eventos. |
| `TestPropertySource` | Cambiar `app.cron=-` para desactivar tareas. |
| **Aleatoriedad** | Inyectar `Random`/`Supplier<UUID>` para controlarlo. |

### Ejemplo: tiempo controlado con `Clock`
```java
// Código de producción: recibir un Clock (no llamar a LocalDate.now() directamente)
@Service
public class SuscripcionService {
    private final Clock clock;
    public SuscripcionService(Clock clock) { this.clock = clock; }

    public boolean estaVencida(Suscripcion s) {
        return s.getFin().isBefore(LocalDate.now(clock));
    }
}
@Bean public Clock clock() { return Clock.systemDefaultZone(); }     // configuración real

// Test
class SuscripcionServiceTest {
    @Test
    void vencida() {
        Clock fijo = Clock.fixed(Instant.parse("2026-06-15T10:00:00Z"), ZoneOffset.UTC);
        SuscripcionService service = new SuscripcionService(fijo);

        assertThat(service.estaVencida(new Suscripcion(LocalDate.of(2026, 6, 14)))).isTrue();
        assertThat(service.estaVencida(new Suscripcion(LocalDate.of(2026, 6, 16)))).isFalse();
    }
}
```

### Ejemplo: Awaitility (asincronía)
```java
@Test
void procesaEnSegundoPlano() {
    service.encolar(pedido);                      // procesa en otro hilo

    await().atMost(5, TimeUnit.SECONDS)
           .pollInterval(100, TimeUnit.MILLISECONDS)
           .untilAsserted(() ->
                assertThat(repo.findById(pedido.getId()).orElseThrow().getEstado())
                        .isEqualTo(Estado.PROCESADO));
}

@Test
void completableFuture() throws Exception {
    CompletableFuture<String> futuro = service.calcularAsync();
    assertThat(futuro.get(2, TimeUnit.SECONDS)).isEqualTo("listo");
}
```

### Ejemplo: `@Async` determinista en tests
```java
@TestConfiguration
public class AsyncTestConfig {
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() { return new SyncTaskExecutor(); }     // mismo hilo
}
```

### Ejemplo: probar `@Scheduled` sin esperar
```java
@Test
void tareaDeLimpieza() {
    repo.save(pedidoAntiguo());
    tareas.limpiezaDiaria();                  // se invoca directamente el método
    assertThat(repo.findAll()).isEmpty();
}
```

### Ejemplo: eventos de Spring
```java
@Test
void publicaEventoAlCrear() {
    service.crear(request);
    verify(publisher).publishEvent(any(PedidoCreadoEvent.class));            // con @Mock ApplicationEventPublisher
}
```

### Ejemplo: manejo global de excepciones
```java
@WebMvcTest(UsuarioController.class)
class ErroresTest {
    @Autowired MockMvc mockMvc;
    @MockBean UsuarioService service;

    @Test
    void errorInesperado_devuelve500ConFormatoPropio() throws Exception {
        when(service.listar()).thenThrow(new RuntimeException("boom"));

        mockMvc.perform(get("/api/usuarios"))
               .andExpect(status().isInternalServerError())
               .andExpect(jsonPath("$.mensaje").value("Error interno"));
    }
}
```

## 18. Datos de prueba y buenas prácticas

### Cómo construir datos de prueba
| Técnica | Significado |
|---|---|
| **Object Mother** | Clase con métodos estáticos que crean objetos válidos (`UsuarioMother.valido()`). |
| **Test Data Builder** | Builder con valores por defecto, modificando solo lo relevante. |
| **Lombok `@Builder`** | Builders sin código. |
| **Fixtures SQL / JSON** | Archivos en `src/test/resources`. |
| **Faker** (`net.datafaker`) | Datos aleatorios realistas (nombres, emails). |
| **Instancio / EasyRandom** | Genera objetos aleatorios automáticamente. |
| **`@BeforeEach`** | Preparar datos comunes. |
| **Datos únicos por test** | Usar IDs/emails distintos para no colisionar. |

### Ejemplo: Object Mother + Builder
```java
public class UsuarioMother {
    public static Usuario valido() {
        return Usuario.builder().nombre("Ana").email("ana@mail.com")
                      .estado(Estado.ACTIVO).edad(25).build();
    }
    public static Usuario inactivo() {
        return valido().toBuilder().estado(Estado.INACTIVO).build();
    }
    public static UsuarioRequest requestValido() {
        return new UsuarioRequest("Ana", "ana@mail.com");
    }
}

// Test Data Builder propio
public class PedidoBuilder {
    private Long id = 1L;
    private String cliente = "Ana";
    private BigDecimal total = BigDecimal.TEN;
    private Estado estado = Estado.PENDIENTE;

    public static PedidoBuilder unPedido() { return new PedidoBuilder(); }
    public PedidoBuilder conTotal(BigDecimal t) { this.total = t; return this; }
    public PedidoBuilder enviado() { this.estado = Estado.ENVIADO; return this; }
    public Pedido build() { return new Pedido(id, cliente, total, estado); }
}
// Uso: unPedido().enviado().conTotal(new BigDecimal("500")).build();
```

### Ejemplo: Faker y JSON de prueba
```java
Faker faker = new Faker(new Locale("es"));
String nombre = faker.name().fullName();
String email  = faker.internet().emailAddress();

// Leer un JSON desde src/test/resources/respuestas/usuario.json
String json = new String(getClass().getResourceAsStream("/respuestas/usuario.json").readAllBytes());
// o con Spring:
String json2 = new ClassPathResource("respuestas/usuario.json").getContentAsString(StandardCharsets.UTF_8);
```

### Buenas prácticas (resumen)
| Práctica | Detalle |
|---|---|
| **F.I.R.S.T.** | **F**ast (rápidos), **I**ndependent (independientes), **R**epeatable (repetibles), **S**elf-validating (se validan solos), **T**imely (oportunos). |
| Un concepto por test | Evita tests con 10 aserciones sin relación. |
| Nombres descriptivos | `@DisplayName` o `metodo_condicion_resultado`. |
| Estructura AAA | Separar preparar / ejecutar / verificar. |
| Sin lógica en tests | Sin `if`, `for`: son los tests los que deben ser obvios. |
| No depender del orden | Cada test prepara y limpia su estado. |
| No probar el framework | No pruebes que `repo.save()` de Spring Data funciona; prueba **tu** lógica y **tus** consultas. |
| Probar comportamiento, no implementación | Evita ligarte a detalles internos (qué método privado se llamó). |
| Mockear solo lo necesario | Mucho mock = test frágil y poco valor. |
| Preferir inyección por constructor | Facilita instanciar con mocks sin Spring. |
| Casos borde y negativos | `null`, vacío, límites, errores, permisos. |
| Mantener los tests rápidos | Unitarios sin Spring; slices antes que `@SpringBootTest`. |
| Tests determinísticos | Controlar tiempo (`Clock`), aleatoriedad y orden. |
| Tests como documentación | Un buen nombre explica la regla de negocio. |
| No ignorar tests rotos | Arréglalos o elimínalos (`@Disabled` con motivo y ticket). |
| Ejecutar en CI | Cada commit/PR. |
| Cobertura como guía, no meta | 100% no garantiza calidad; importa **qué** se verifica. |
| Un test que falla debe decir por qué | Mensajes claros / AssertJ `as(...)`. |

### Qué probar en cada capa (guía)
| Capa | Tipo de test | Verificar |
|---|---|---|
| Lógica de negocio | Unitario (JUnit + Mockito) | Reglas, cálculos, excepciones, interacciones clave. |
| Controllers | `@WebMvcTest` | Rutas, validación, códigos HTTP, JSON, seguridad. |
| Repositorios | `@DataJpaTest` (+ Testcontainers) | Consultas derivadas, `@Query`, constraints, relaciones. |
| Clientes HTTP | `@RestClientTest` / WireMock | Request correcta, manejo de errores/timeouts. |
| Seguridad | `@WebMvcTest` + `@Import(SecurityConfig)` | 401/403 por rol y ruta. |
| Flujo completo | `@SpringBootTest` + Testcontainers | Camino feliz E2E y errores críticos. |
| Serialización | `@JsonTest` | Formato JSON de entrada/salida. |

## 19. Cobertura y ejecución con Maven

| Término | Significado |
|---|---|
| **JaCoCo** | Herramienta de cobertura de código para Java (plugin de Maven). |
| **Cobertura de líneas** | % de líneas ejecutadas por los tests. |
| **Cobertura de ramas (branch)** | % de caminos de `if/else/switch` ejecutados. |
| **Cobertura de instrucciones / métodos / clases** | Otras métricas de JaCoCo. |
| `target/site/jacoco/index.html` | Reporte HTML. |
| `jacoco:check` | Falla el build si no se alcanza un mínimo. |
| **SonarQube / SonarCloud** | Plataformas que analizan calidad y cobertura. |
| **PIT (Pitest)** | Mutation testing. |
| `mvn test` | Ejecuta tests unitarios (Surefire: `*Test`, `*Tests`, `Test*`). |
| `mvn verify` | Ejecuta también tests de integración (Failsafe: `*IT`, `*ITCase`) y checks. |
| `mvn -Dtest=NombreTest test` | Ejecuta una clase. |
| `mvn -Dtest=NombreTest#metodo test` | Ejecuta un solo método. |
| `mvn -Dtest='*Service*' test` | Por patrón. |
| `mvn -Dgroups=rapido test` | Por `@Tag`. |
| `mvn -DexcludedGroups=lento test` | Excluir tags. |
| `mvn -DskipTests package` | Compila pero no ejecuta tests. |
| `mvn -Dmaven.test.skip=true package` | Ni compila ni ejecuta tests. |
| `mvn -Dsurefire.failIfNoSpecifiedTests=false` | No falla si no encuentra el test indicado. |
| `target/surefire-reports/` | Reportes de resultados. |
| `-DfailIfNoTests=false` | No falla sin tests. |

### Ejemplo: JaCoCo en `pom.xml`
```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.12</version>
    <executions>
        <execution>
            <goals><goal>prepare-agent</goal></goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals><goal>report</goal></goals>
        </execution>
        <execution>
            <id>check</id>
            <goals><goal>check</goal></goals>
            <configuration>
                <rules>
                    <rule>
                        <element>BUNDLE</element>
                        <limits>
                            <limit>
                                <counter>LINE</counter>
                                <value>COVEREDRATIO</value>
                                <minimum>0.70</minimum>
                            </limit>
                        </limits>
                    </rule>
                </rules>
            </configuration>
        </execution>
    </executions>
    <configuration>
        <excludes>
            <exclude>**/config/**</exclude>
            <exclude>**/*Application.class</exclude>
            <exclude>**/dto/**</exclude>
        </excludes>
    </configuration>
</plugin>
```

### Ejemplo: separar unitarios e integración con Failsafe
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-failsafe-plugin</artifactId>
    <executions>
        <execution>
            <goals>
                <goal>integration-test</goal>
                <goal>verify</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```
```bash
mvn test                      # solo *Test (rápidos)
mvn verify                    # *Test + *IT + checks de cobertura
mvn test jacoco:report        # genera el reporte
open target/site/jacoco/index.html
```

### Comandos útiles
```bash
mvn test -Dtest=UsuarioServiceTest
mvn test -Dtest=UsuarioServiceTest#registrar_ok
mvn test -Dtest='Usuario*Test'
mvn test -Dgroups="rapido"
mvn clean verify -DskipITs                 # sin integración
mvn -q test                                # salida reducida
mvn test -Dspring.profiles.active=test
```

### GitHub Actions (CI básico)
```yaml
name: CI
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest        # incluye Docker → Testcontainers funciona
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with: { distribution: temurin, java-version: 21, cache: maven }
      - run: mvn -B verify
```

## 20. Errores frecuentes

| Problema / Error | Causa | Solución |
|---|---|---|
| `No tests found` / el test no se ejecuta | El nombre no coincide con el patrón de Surefire (`*Test`), o usa `@Test` de JUnit 4 con motor JUnit 5. | Nombrar `XxxTest`; importar `org.junit.jupiter.api.Test`. |
| Test con `@Test` de **JUnit 4** | Import equivocado (`org.junit.Test`). | Usar `org.junit.jupiter.api.Test`. |
| `NullPointerException` en un `@Mock`/`@InjectMocks` | Falta `@ExtendWith(MockitoExtension.class)`. | Añadirlo (o `MockitoAnnotations.openMocks(this)`). |
| `UnnecessaryStubbingException` | Hay un `when(...)` que el test no usa (strict stubs). | Quitar el stub sobrante; o `lenient()`. |
| `InvalidUseOfMatchersException` | Mezclaste matchers y valores literales. | Usar **todos** matchers (`eq(5)`, `anyString()`). |
| `WrongTypeOfReturnValue` | `thenReturn` con tipo incorrecto. | Ajustar el tipo devuelto. |
| El mock devuelve `null` | No se hizo el stub para esos argumentos exactos. | Revisar argumentos / usar matchers. |
| `when(spy.metodo())` ejecuta el método real | Con spies, `when()` llama al método de verdad. | Usar `doReturn(x).when(spy).metodo()`. |
| `MockitoException: Cannot mock/spy class ... final` | Clase `final`. | Mockito 5 ya mockea finales; si no, usar inline mock maker. |
| `NoSuchBeanDefinitionException` en `@WebMvcTest` | El controlador depende de un `@Service` que el slice **no** carga. | `@MockBean` del service. |
| `@WebMvcTest` devuelve 401/403 en todo | Spring Security está activo. | `@Import(SecurityConfig.class)` + `@WithMockUser`, o `@AutoConfigureMockMvc(addFilters = false)` (solo si no pruebas seguridad). |
| 403 en POST/PUT/DELETE con usuario válido | Falta CSRF. | `.with(csrf())`. |
| 404 en `MockMvc` | Ruta incorrecta o el controlador no está en el slice. | Revisar `@WebMvcTest(Controller.class)` y la URL. |
| 415 Unsupported Media Type | Falta `contentType`. | `.contentType(MediaType.APPLICATION_JSON)`. |
| 400 por JSON | JSON mal formado o campos obligatorios. | Revisar el `content(...)`; imprimir con `andDo(print())`. |
| `Unable to find a @SpringBootConfiguration` | El test está fuera del árbol de paquetes de `@SpringBootApplication`. | Mismo paquete/subpaquete o `@SpringBootTest(classes = App.class)`. |
| `Failed to load ApplicationContext` | Algún bean falla al crearse. | Leer el último **Caused by**; revisar propiedades de `application-test`. |
| Tests lentos | Muchos `@SpringBootTest`, `@MockBean` distintos, `@DirtiesContext`. | Usar slices/unitarios; unificar configuración; reutilizar contexto. |
| Datos que "se filtran" entre tests | Sin rollback / servidor real con otro hilo. | `@Transactional` (si mismo hilo) o limpiar en `@BeforeEach/@AfterEach`. |
| `LazyInitializationException` en el test | Acceso a relación lazy fuera de transacción. | `@Transactional` en el test, `JOIN FETCH` o DTO. |
| Test de repositorio pasa pero falla en producción | H2 ≠ motor real. | Testcontainers con la misma BD. |
| Constraint no se dispara en `@DataJpaTest` | Hibernate no hizo flush. | `saveAndFlush` / `em.flush()`. |
| `@DataJpaTest` lee de caché y no de BD | Contexto de persistencia de primer nivel. | `em.flush(); em.clear();`. |
| `@Modifying` no refleja cambios al leer | El contexto no se actualiza. | `em.clear()`, o `@Modifying(clearAutomatically = true)`. |
| `Could not find a valid Docker environment` | Docker no está corriendo. | Iniciar Docker; en CI usar un runner con Docker. |
| Test flaky con hilos | Dependencia del tiempo/ordenamiento. | Awaitility, `SyncTaskExecutor`, `Clock` fijo. |
| Falla solo en CI | Zona horaria, locale, orden de ejecución, puertos ocupados. | Fijar `TimeZone`/`Locale`; usar `RANDOM_PORT`; evitar dependencia del orden. |
| `assertEquals` con argumentos invertidos | Mensajes de fallo confusos. | Siempre `assertEquals(esperado, real)`. |
| `BigDecimal` no es igual (`100.0` vs `100.00`) | `equals` considera la escala. | `isEqualByComparingTo(...)`. |
| Comparar listas de objetos falla | Sin `equals/hashCode`. | `usingRecursiveComparison()`. |
| `@BeforeAll` no compila | Debe ser `static`. | Hacerlo `static` o `@TestInstance(PER_CLASS)`. |
| `@MockBean` deprecado | Boot 3.4+. | Migrar a `@MockitoBean`/`@MockitoSpyBean`. |
| `ClassNotFoundException: org.junit.runner...` | Mezcla de JUnit 4 y 5. | Eliminar dependencias de JUnit 4 (o añadir `junit-vintage-engine`). |
| `@Value`/propiedades `null` en test unitario | Sin Spring no se inyectan. | `ReflectionTestUtils.setField(...)` o pasar por constructor. |

### Checklist antes de dar un test por bueno
1. ¿Falla si rompo el código que prueba? (haz la prueba: rómpelo a propósito).
2. ¿Tiene un nombre que explica qué comprueba?
3. ¿Es independiente (corre solo y en cualquier orden)?
4. ¿Verifica comportamiento observable, no detalles internos?
5. ¿Es rápido y determinista?

## 21. Ejemplo completo por capas

Un mismo caso (**crear un usuario**) probado en cada nivel de la pirámide.

```java
// ============ 1. UNITARIO: Service (Mockito, sin Spring) ============
@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock UsuarioRepository repo;
    @Mock PasswordEncoder encoder;
    @InjectMocks UsuarioService service;

    @Test
    @DisplayName("crear: cifra la contraseña y guarda")
    void crear_ok() {
        when(repo.existsByEmail("ana@mail.com")).thenReturn(false);
        when(encoder.encode("Clave123")).thenReturn("HASH");
        when(repo.save(any(Usuario.class))).thenAnswer(i -> { Usuario u = i.getArgument(0); u.setId(1L); return u; });

        UsuarioResponse r = service.crear(new UsuarioRequest("Ana", "ana@mail.com", "Clave123"));

        assertThat(r.id()).isEqualTo(1L);
        ArgumentCaptor<Usuario> c = ArgumentCaptor.forClass(Usuario.class);
        verify(repo).save(c.capture());
        assertThat(c.getValue().getPassword()).isEqualTo("HASH");
    }

    @Test
    @DisplayName("crear: email repetido lanza excepción y no guarda")
    void crear_duplicado() {
        when(repo.existsByEmail(any())).thenReturn(true);
        assertThatThrownBy(() -> service.crear(new UsuarioRequest("Ana", "ana@mail.com", "x")))
                .isInstanceOf(EmailDuplicadoException.class);
        verify(repo, never()).save(any());
    }
}

// ============ 2. SLICE WEB: Controller (@WebMvcTest) ============
@WebMvcTest(UsuarioController.class)
@Import(SecurityConfig.class)
class UsuarioControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper mapper;
    @MockBean UsuarioService service;
    @MockBean JwtService jwtService;
    @MockBean UserDetailsService uds;

    @Test
    @WithMockUser(roles = "ADMIN")
    void crear_201() throws Exception {
        when(service.crear(any())).thenReturn(new UsuarioResponse(1L, "Ana", "ana@mail.com"));

        mockMvc.perform(post("/api/usuarios").with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(new UsuarioRequest("Ana", "ana@mail.com", "Clave123"))))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void crear_sinNombre_400() throws Exception {
        mockMvc.perform(post("/api/usuarios").with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"email\":\"ana@mail.com\"}"))
               .andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }

    @Test
    void crear_sinLogin_401() throws Exception {
        mockMvc.perform(post("/api/usuarios").with(csrf())
                    .contentType(MediaType.APPLICATION_JSON).content("{}"))
               .andExpect(status().isUnauthorized());
    }
}

// ============ 3. SLICE DATOS: Repository (@DataJpaTest + Testcontainers) ============
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class UsuarioRepositoryTest {

    @Container @ServiceConnection
    static PostgreSQLContainer<?> pg = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired UsuarioRepository repo;
    @Autowired TestEntityManager em;

    @Test
    void existsByEmail() {
        em.persistAndFlush(new Usuario("Ana", "ana@mail.com", "HASH"));
        assertThat(repo.existsByEmail("ana@mail.com")).isTrue();
        assertThat(repo.existsByEmail("otro@mail.com")).isFalse();
    }

    @Test
    void emailUnico() {
        repo.saveAndFlush(new Usuario("Ana", "ana@mail.com", "HASH"));
        assertThatThrownBy(() -> repo.saveAndFlush(new Usuario("Otra", "ana@mail.com", "HASH")))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}

// ============ 4. INTEGRACIÓN: flujo completo (@SpringBootTest + servidor real) ============
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test")
class UsuarioFlujoIT {

    @Container @ServiceConnection
    static PostgreSQLContainer<?> pg = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired TestRestTemplate rest;
    @Autowired UsuarioRepository repo;
    @MockBean EmailService email;                // lo externo se simula

    @BeforeEach void limpiar() { repo.deleteAll(); }

    @Test
    void registrarLoginYConsultar() {
        // 1. registrar
        var reg = rest.postForEntity("/api/auth/register",
                new RegistroRequest("ana", "ana@mail.com", "Clave123!"), Void.class);
        assertThat(reg.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // 2. login
        var login = rest.postForEntity("/api/auth/login",
                new LoginRequest("ana", "Clave123!"), TokenResponse.class);
        assertThat(login.getStatusCode()).isEqualTo(HttpStatus.OK);

        // 3. usar el token
        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth(login.getBody().token());
        var yo = rest.exchange("/api/me", HttpMethod.GET, new HttpEntity<>(h), String.class);
        assertThat(yo.getStatusCode()).isEqualTo(HttpStatus.OK);

        // 4. sin token
        assertThat(rest.getForEntity("/api/me", String.class).getStatusCode())
                .isEqualTo(HttpStatus.UNAUTHORIZED);

        verify(email).enviarBienvenida("ana@mail.com");
    }
}
```

### Resumen: qué herramienta para qué
| Necesito... | Uso |
|---|---|
| Probar lógica pura | JUnit + AssertJ |
| Aislar dependencias | Mockito (`@Mock`, `@InjectMocks`) |
| Verificar llamadas | `verify`, `ArgumentCaptor` |
| Varios casos de datos | `@ParameterizedTest` |
| Probar un controlador | `@WebMvcTest` + `MockMvc` |
| Probar una consulta JPA | `@DataJpaTest` (+ Testcontainers) |
| Probar el flujo completo | `@SpringBootTest` + `TestRestTemplate`/`WebTestClient` |
| BD real en tests | Testcontainers + `@ServiceConnection` |
| Simular API externa | WireMock / `MockRestServiceServer` / `MockWebServer` |
| Probar seguridad | `@WithMockUser`, `with(jwt())`, `with(csrf())` |
| Probar JSON | `@JsonTest` + `JacksonTester` |
| Controlar el tiempo | `Clock.fixed` |
| Esperar asincronía | Awaitility |
| Medir cobertura | JaCoCo |

### Glosario de siglas
| Sigla | Significado |
|---|---|
| **SUT** | System Under Test |
| **AAA** | Arrange – Act – Assert |
| **TDD / BDD** | Test/Behavior-Driven Development |
| **FIRST** | Fast, Independent, Repeatable, Self-validating, Timely |
| **CI / CD** | Integración / Entrega continua |
| **E2E** | End-to-End |
| **IT** | Integration Test |
| **UT** | Unit Test |
| **DTO** | Data Transfer Object |
| **JDBC** | Java Database Connectivity |
| **H2** | Base de datos Java embebida (para tests) |
| **SUT / CUT** | System / Class Under Test |
| **JaCoCo** | Java Code Coverage |
| **PIT** | Pitest (mutation testing) |
| **WireMock** | Servidor HTTP falso |
| **CSRF** | Cross-Site Request Forgery |
| **JWT** | JSON Web Token |

---

> **Tip de lectura:** al abrir una clase de test, mira primero las **anotaciones de la clase** — `@ExtendWith(MockitoExtension.class)` = test unitario; `@WebMvcTest` = solo la capa web; `@DataJpaTest` = solo JPA; `@SpringBootTest` = aplicación completa. Eso te dice qué parte del sistema se está probando y qué está simulado (`@Mock`, `@MockBean`). Luego busca el patrón **Arrange / Act / Assert** en cada método.

