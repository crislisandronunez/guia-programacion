# Glosario y Guía de Interfaces, Contratos y Ubicación por Capas (Dominio vs Aplicación vs Infraestructura)

Este documento explica de forma **100% práctica y con ejemplos en Java y Spring Boot** qué es realmente un **Contrato (`interface`)**, **cuándo** debes crear una interfaz (y cuándo no hace falta), y la duda más importante de la Arquitectura Limpia y Hexagonal: **¿Dónde va la interfaz y dónde va la clase que la implementa (`implements`) según sea una regla de Dominio, un Caso de Uso de Aplicación o un Adaptador de Infraestructura?**

---

## Índice
1. [¿Qué es un Contrato (`interface`) en Ingeniería de Software?](#1-qué-es-un-contrato-interface-en-ingeniería-de-software)
2. [Mapa Maestro: Los 3 Tipos de Contratos y en Qué Capa Vive Cada Uno](#2-mapa-maestro-los-3-tipos-de-contratos-y-en-qué-capa-vive-cada-uno)
3. [La Regla de la Dependencia (Por qué la Interfaz y su Implementación a veces se separan de capa)](#3-la-regla-de-la-dependencia-por-qué-la-interfaz-y-su-implementación-a-veces-se-separan-de-capa)
4. [CASO 1: Contrato e Implementación AMBOS en la Capa de DOMINIO (Políticas y Reglas de Negocio Puras)](#4-caso-1-contrato-e-implementación-ambos-en-la-capa-de-dominio-políticas-y-reglas-de-negocio-puras)
5. [CASO 2: Contrato e Implementación en la Capa de APLICACIÓN (Puertos de Entrada / Casos de Uso)](#5-caso-2-contrato-e-implementación-en-la-capa-de-aplicación-puertos-de-entrada--casos-de-uso)
6. [CASO 3: Contrato en DOMINIO/APLICACIÓN e Implementación (`Adapter`) en INFRAESTRUCTURA (Puertos de Salida)](#6-caso-3-contrato-en-dominioaplicación-e-implementación-adapter-en-infraestructura-puertos-de-salida)
7. [Árbol de Decisión Rápido: "¿En qué carpeta pongo mi `interface` y mi `class`?"](#7-árbol-de-decisión-rápido-en-qué-carpeta-pongo-mi-interface-y-mi-class)
8. [Reglas de Oro para Diseñar Contratos Limpios (Sin Fugas de Infraestructura)](#8-reglas-de-oro-para-diseñar-contratos-limpios-sin-fugas-de-infraestructura)
9. [El Antipatión `*Service` + `*ServiceImpl` (Cuándo NO crear una Interfaz)](#9-el-antipatrón-service--serviceimpl-cuándo-no-crear-una-interfaz)
10. [Estructura Completa de Carpetas de un Proyecto Profesional en Spring Boot](#10-estructura-completa-de-carpetas-de-un-proyecto-profesional-en-spring-boot)
11. [Ejemplo Completo de Punta a Punta: Los 3 Casos Funcionando Juntos en un Cobro](#11-ejemplo-completo-de-punta-a-punta-los-3-casos-funcionando-juntos-en-un-cobro)
12. [Glosario de Términos de Contratos y Arquitectura Hexagonal](#12-glosario-de-términos-de-contratos-y-arquitectura-hexagonal)

---

## 1. ¿Qué es un Contrato (`interface`) en Ingeniería de Software?

En Java, una **`interface`** no es simplemente "una lista de métodos vacíos". Es un **Contrato Formal** entre dos partes:
1. **El Cliente (*Caller*):** Quien necesita que se haga un trabajo, sin importarle cómo se hace por dentro.
2. **El Proveedor (*Implementor*):** La clase que escribe el `implements` y se compromete a cumplir ese trabajo.

Todo contrato consta de **4 elementos**:

| Elemento del Contrato | Qué define | Ejemplo en Java |
|---|---|---|
| **1. Firma Sintáctica (*Signature*)** | Nombre del método, qué parámetros recibe y qué tipo devuelve. | `ReciboPago cobrar(OrdenCobro orden);` |
| **2. Precondiciones (*Preconditions*)** | Qué obliga el contrato a que sea verdad **antes** de llamarlo. | `orden` no puede ser `null` y `orden.monto() > 0`. |
| **3. Postcondiciones (*Postconditions*)** | Qué promete devolver el contrato **después** de ejecutarse. | Devuelve un `ReciboPago` nunca nulo con un código de transacción único. |
| **4. Semántica de Errores (Excepciones del Contrato)** | Qué excepciones de negocio puede lanzar si algo falla. | Lanza `FondosInsuficientesException` si la tarjeta no tiene cupo. |

### Los 2 Únicos Motivos Válidos para Crear una `interface`:
1. **Polimorfismo de Negocio (Principio Abierto/Cerrado - OCP):** Existen **múltiples formas de calcular o ejecutar una regla** (ej. Comisión Minorista, Comisión Mayorista, Comisión VIP) y queremos cambiarlas o agregar nuevas sin usar `if/else`.
2. **Cruzar una Frontera de Arquitectura (Principio de Inversión de Dependencias - DIP):** Nuestro código de negocio necesita **hablar con el mundo exterior** (guardar en PostgreSQL, llamar a la API de Mercado Pago, publicar en Kafka, mandar un email) sin acoplarse a la librería técnica.

---

## 2. Mapa Maestro: Los 3 Tipos de Contratos y en Qué Capa Vive Cada Uno

Esta tabla responde directamente a tu pregunta. En una arquitectura profesional existen **3 lugares** donde se ubican las interfaces y sus implementaciones:

```text
┌────────────────────────────────────────────────────────────────────────────────────────┐
│ CAPA DE INFRAESTRUCTURA (Detalles técnicos: Web, Base de Datos SQL/NoSQL, APIs, Colas) │
│   • [RestController] ──(llama a)──> Interfaz Input Port (de Aplicación)                │
│   • [JpaAdapter / StripeAdapter] ──(implementa)──> Interfaz Output Port                │
└───────────────────┬────────────────────────────────────────────────┬───────────────────┘
                    │                                                │ (Inversión de
                    ▼                                                ▼  Dependencia DIP)
┌────────────────────────────────────────────────────────────────────────────────────────┐
│ CAPA DE APLICACIÓN (Orquestación de Casos de Uso y Transacciones)                      │
│   • Interfaz Input Port (Caso de Uso) + Su Implementación (@Service UseCaseImpl)       │
│   • (Opcional) Interfaces de Output Ports de infraestructura técnica                   │
└───────────────────────────────────────────┬────────────────────────────────────────────┘
                                            │
                                            ▼
┌────────────────────────────────────────────────────────────────────────────────────────┐
│ CAPA DE DOMINIO (El Corazón del Negocio: 100% Java puro, CERO bases de datos ni HTTP)  │
│   • Entidades de Dominio y Value Objects                                               │
│   • Interfaces de Reglas/Políticas de Negocio + SUS IMPLEMENTACIONES PURAS DE NEGOCIO  │
│   • Interfaces de Repositorios/Puertos de Dominio (sin su implementación SQL)          │
└────────────────────────────────────────────────────────────────────────────────────────┘
```

| Escenario | ¿Dónde va la `interface` (Contrato)? | ¿Dónde va la `class` (`implements`)? | ¿Por qué se hace así? | Ejemplo Real |
|---|---|---|---|---|
| **CASO 1: Regla o Política de Negocio Pura (*Domain Policy / Strategy*)** | En **DOMINIO** (`domain/policy`) | En **DOMINIO** (`domain/policy`) | Porque tanto el contrato como la fórmula matemática son **100% reglas de la empresa** y no tocan bases de datos ni APIs externas. | `PoliticaDescuento` y sus clases `DescuentoVip`, `DescuentoMayorista`. |
| **CASO 2: Caso de Uso / Puerto de Entrada (*Input / Driving Port*)** | En **APLICACIÓN** (`application/port/in`) | En **APLICACIÓN** (`application/service`) | Porque define **qué acciones ofrece el sistema** hacia los controladores (`@RestController`, Kafka listener, CLI) y cómo se orquesta el flujo. | `TransferirDineroUseCase` (interfaz) y `TransferirDineroService` (implementación). |
| **CASO 3: Puerto de Salida hacia el Exterior (*Output / Driven Port*)** | En **DOMINIO** (o **APLICACIÓN** `port/out`) | En **INFRAESTRUCTURA** (`infrastructure/adapter/out`) | Porque el negocio define **qué necesita** (guardar un pedido o cobrar), pero el **cómo técnico** (PostgreSQL, Mongo, Stripe, S3) es un detalle externo que vive en Infraestructura. | `PasarelaPagoPort` (en Dominio/App) y `MercadoPagoAdapter` (en Infraestructura). |

---

## 3. La Regla de la Dependencia (Por qué la Interfaz y su Implementación a veces se separan de capa)

En Arquitectura Limpia y Hexagonal existe **una sola ley inquebrantable**:
> **Las flechas de dependencia (`import`) SIEMPRE apuntan hacia adentro (hacia el Dominio).**
> - **Infraestructura** conoce a **Aplicación** y a **Dominio**.
> - **Aplicación** conoce solo a **Dominio**.
> - **Dominio** NO conoce a nadie (en los archivos `.java` del paquete `domain` están prohibidos los `import` de `org.springframework.web.*`, `jakarta.persistence.*`, `com.stripe.*`, etc.).

**Entonces, ¿cómo hace el Dominio o la Aplicación para guardar en la Base de Datos o llamar a Stripe si tiene prohibido importar las clases de Infraestructura?**
- **Ahí entra el Caso 3 (Inversión de Dependencias - DIP):**
  1. Ponemos la **`interface` (`PedidoRepositoryPort` o `PasarelaPagoPort`) adentro de Dominio/Aplicación**.
  2. El servicio de Aplicación llama a esa interfaz (no importa nada de afuera).
  3. La clase **`PedidoJpaAdapter` en la capa de Infraestructura** hace `implements PedidoRepositoryPort` (la flecha apunta desde Infraestructura hacia adentro).

---

## 4. CASO 1: Contrato e Implementación AMBOS en la Capa de DOMINIO (Políticas y Regocio Puro)

### ¿Cuándo van TANTO la interfaz COMO sus implementaciones dentro de la capa de Dominio?
Cuando estás separando **reglas de negocio puras, cálculos matemáticos, algoritmos de decisión o validaciones del negocio** que pueden variar según el tipo de cliente, país o producto (**Patrón Strategy / Policy de DDD**), y **NO necesitan hacer consultas SQL ni llamadas HTTP** para ejecutarse (operan en memoria con los objetos de dominio que reciben por parámetro).

### Ejemplos típicos del Caso 1 (Todo vive en `domain`):
- Cálculo de comisiones, impuestos o intereses según el tipo de cuenta.
- Reglas de elegibilidad crediticia (*Scoring* sobre datos ya cargados del cliente).
- Políticas de penalidad por cancelación de reserva (según falten 24hs o 7 días).
- Estrategias de reparto de descuento en un carrito de compras.

### Código del Caso 1 (Tanto la `interface` como el `implements` están en `domain`):

```java
// =========================================================================
// CAPA DE DOMINIO: Paquete com.empresa.pagos.domain.policy
// Observa que NO hay ni un solo import de base de datos, HTTP ni librerías externas.
// =========================================================================
package com.empresa.pagos.domain.policy;

import com.empresa.pagos.domain.model.Comercio;
import com.empresa.pagos.domain.model.CategoriaComercio;
import java.math.BigDecimal;
import java.math.RoundingMode;

// 1. EL CONTRATO DE DOMINIO (Policy Interface): Vive en la capa de Dominio
public interface PoliticaComisionVenta {
    boolean aplicaPara(CategoriaComercio categoria);
    BigDecimal calcularComision(BigDecimal montoBruto, Comercio comercio);
}

// 2. IMPLEMENTACIÓN DE DOMINIO #1: También vive en la capa de Dominio
// ¿Por qué vive en Dominio y NO en Infraestructura? Porque el "1% con tope de $500"
// es una REGLA DE NEGOCIO PURA de la empresa, no es un detalle técnico de servidores.
public class ComisionComercioGastronomicoPolicy implements PoliticaComisionVenta {

    private static final BigDecimal PORCENTAJE = new BigDecimal("0.015"); // 1.5%
    private static final BigDecimal TOPE_MAXIMO = new BigDecimal("5000.00");

    @Override
    public boolean aplicaPara(CategoriaComercio categoria) {
        return categoria == CategoriaComercio.GASTRONOMIA;
    }

    @Override
    public BigDecimal calcularComision(BigDecimal montoBruto, Comercio comercio) {
        BigDecimal calculada = montoBruto.multiply(PORCENTAJE).setScale(2, RoundingMode.HALF_EVEN);
        return calculada.min(TOPE_MAXIMO); // Regla pura del negocio
    }
}

// 3. IMPLEMENTACIÓN DE DOMINIO #2: También vive en la capa de Dominio
public class ComisionComercioEstandarPolicy implements PoliticaComisionVenta {

    private static final BigDecimal PORCENTAJE_BASE = new BigDecimal("0.035"); // 3.5%

    @Override
    public boolean aplicaPara(CategoriaComercio categoria) {
        return categoria == CategoriaComercio.ESTANDAR;
    }

    @Override
    public BigDecimal calcularComision(BigDecimal montoBruto, Comercio comercio) {
        // Si el comercio tiene más de 5 años de antigüedad, tiene medio punto de bonificación
        BigDecimal tasa = comercio.tieneAntiguedadMayorAAnios(5)
                ? new BigDecimal("0.030")
                : PORCENTAJE_BASE;
        return montoBruto.multiply(tasa).setScale(2, RoundingMode.HALF_EVEN);
    }
}
```

---

## 5. CASO 2: Contrato e Implementación en la Capa de APLICACIÓN (Puertos de Entrada / Casos de Uso)

### ¿Qué hace la Capa de Aplicación y qué diferencia tiene con la Capa de Dominio?
- **La Capa de Dominio** sabe **las reglas del juego** (cómo se calcula una comisión, cuándo una factura puede pasar a pagada).
- **La Capa de Aplicación** es el **Director de Orquesta (Caso de Uso)**: no contiene fórmulas matemáticas complejas del negocio, sino los pasos ordenados para cumplir el pedido de un usuario:
  1. Abre la transacción (`@Transactional`).
  2. Pide al Puerto de Salida que busque la entidad en la BD.
  3. Llama a las entidades y políticas de la **Capa de Dominio** para que ejecuten las reglas de negocio.
  4. Pide al Puerto de Salida que guarde los cambios y notifique.

### ¿Cuándo se crea una `interface` en la Capa de Aplicación?
1. **Para definir el Puerto de Entrada (*Input Port / Use Case Interface*):** Es el contrato que expone la Capa de Aplicación hacia los Controladores (`@RestController`), consumidores de Kafka o jobs programados.
2. **Para políticas de flujo de aplicación:** Por ejemplo, estrategias sobre cómo armar un DTO o qué flujo de notificación elegir según el canal de entrada.

### Código del Caso 2 (Contrato e Implementación en la Capa de Aplicación):

```java
// =========================================================================
// 1. CONTRATO EN CAPA DE APLICACIÓN (Input Port):
// Paquete: com.empresa.pagos.application.port.in
// Define QUÉ puede hacer el sistema desde afuera (el Caso de Uso)
// =========================================================================
package com.empresa.pagos.application.port.in;

import java.math.BigDecimal;
import java.util.UUID;

public interface ProcesarCobroComercioUseCase {

    record ComandoCobro(Long comercioId, BigDecimal montoBruto, String tokenTarjeta, String idempotencyKey) {}
    record ResultadoCobroDto(UUID pagoId, BigDecimal montoBruto, BigDecimal comisionCobrada, BigDecimal montoNeto) {}

    ResultadoCobroDto ejecutarCobro(ComandoCobro comando);
}

// =========================================================================
// 2. IMPLEMENTACIÓN EN CAPA DE APLICACIÓN (Application Service):
// Paquete: com.empresa.pagos.application.service
// Orquesta el flujo combinando las Políticas de Dominio y los Puertos de Salida
// =========================================================================
package com.empresa.pagos.application.service;

import com.empresa.pagos.application.port.in.ProcesarCobroComercioUseCase;
import com.empresa.pagos.domain.model.*;
import com.empresa.pagos.domain.policy.PoliticaComisionVenta;
import com.empresa.pagos.domain.port.out.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
public class ProcesarCobroComercioApplicationService implements ProcesarCobroComercioUseCase {

    // Inyecta Políticas de Dominio (Caso 1) y Puertos de Salida (Caso 3)
    private final List<PoliticaComisionVenta> politicasComision;
    private final ComercioRepositoryPort comercioRepositoryPort;
    private final PasarelaExternaPort pasarelaExternaPort;
    private final PagoRepositoryPort pagoRepositoryPort;

    public ProcesarCobroComercioApplicationService(List<PoliticaComisionVenta> politicasComision,
                                                   ComercioRepositoryPort comercioRepositoryPort,
                                                   PasarelaExternaPort pasarelaExternaPort,
                                                   PagoRepositoryPort pagoRepositoryPort) {
        this.politicasComision = politicasComision;
        this.comercioRepositoryPort = comercioRepositoryPort;
        this.pasarelaExternaPort = pasarelaExternaPort;
        this.pagoRepositoryPort = pagoRepositoryPort;
    }

    @Override
    @Transactional
    public ResultadoCobroDto ejecutarCobro(ComandoCobro comando) {
        // Paso 1: Obtener entidad usando el Puerto de Salida (implementado en Infraestructura)
        Comercio comercio = comercioRepositoryPort.buscarPorId(comando.comercioId())
                .orElseThrow(() -> new IllegalArgumentException("Comercio no encontrado"));

        // Paso 2: Ejecutar Regla de Negocio Pura usando la Política del Dominio (Caso 1)
        BigDecimal comision = politicasComision.stream()
                .filter(p -> p.aplicaPara(comercio.getCategoria()))
                .findFirst()
                .orElseThrow()
                .calcularComision(comando.montoBruto(), comercio);

        // Paso 3: Crear entidad de dominio (que valida sus propias invariantes)
        Pago nuevoPago = Pago.crearNuevo(comercio.getId(), comando.montoBruto(), comision);

        // Paso 4: Llamar a la pasarela externa y guardar en BD a través de los Puertos de Salida (Caso 3)
        String idExterno = pasarelaExternaPort.autorizarYCapturar(
                comando.idempotencyKey(), comando.tokenTarjeta(), comando.montoBruto()
        );
        nuevoPago.confirmarCaptura(idExterno);
        pagoRepositoryPort.guardar(nuevoPago);

        return new ResultadoCobroDto(
                nuevoPago.getId(),
                nuevoPago.getMontoBruto(),
                nuevoPago.getComision(),
                nuevoPago.getMontoNetoComercio()
        );
    }
}
```

---

## 6. CASO 3: Contrato en DOMINIO/APLICACIÓN e Implementación (`Adapter`) en INFRAESTRUCTURA (Puertos de Salida)

### ¿Cuándo va el Contrato adentro (Dominio/Aplicación) pero su Implementación (`Adapter`) afuera en la Capa de Infraestructura?
**Siempre que la implementación necesite tocar tecnología, red, disco o librerías externas a tu negocio:**
- Guardar o consultar una Base de Datos (**PostgreSQL con Spring Data JPA, MongoDB, Redis**).
- Llamar por HTTP/REST/gRPC a un proveedor externo (**Stripe, Mercado Pago, AFIP, Twilio, Correo**).
- Publicar un mensaje en una cola (**RabbitMQ, Apache Kafka, AWS SQS**).
- Subir o leer un archivo (**AWS S3, Google Cloud Storage, Sistema de Archivos**).
- Enviar un correo electrónico (**SMTP / SendGrid / AWS SES**).
- Encriptar con una bóveda externa o leer certificados del sistema.

> **¿El Puerto de Salida (la `interface`) va en el paquete `domain` o en el paquete `application`?**
> - Si el contrato habla en términos de **Entidades del Dominio** (ej. `ComercioRepositoryPort` que guarda y devuelve `Comercio`, o `CotizadorDivisaPort` usado por el dominio), la interfaz suele ponerse en **`domain/port/out`** (o `domain/repository`).
> - Si el contrato es para una tarea auxiliar del **Caso de Uso** (ej. `NotificadorEmailPort`, `ExportadorPdfPort`, `PublicadorEventosColaPort`), la interfaz suele ponerse en **`application/port/out`**.
> - En **ambos** casos, la clase concreta que hace el `implements` (**el `Adapter`**) vive **SIEMPRE en `infrastructure/adapter/out`**.

### Código del Caso 3 (Interfaz en Dominio/Aplicación -> Adaptador en Infraestructura):

```java
// =========================================================================
// PARTE A: EL CONTRATO (Output Port) -> Vive en DOMINIO o APLICACIÓN
// Paquete: com.empresa.pagos.domain.port.out
// ¡Observa que habla el idioma de nuestro negocio! No menciona ni a JPA, ni a SQL, ni a Stripe.
// =========================================================================
package com.empresa.pagos.domain.port.out;

import com.empresa.pagos.domain.model.Pago;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface PagoRepositoryPort {
    Pago guardar(Pago pagoDominio);
    Optional<Pago> buscarPorId(UUID id);
}

public interface PasarelaExternaPort {
    String autorizarYCapturar(String idempotencyKey, String tokenTarjeta, BigDecimal monto);
}
```

```java
// =========================================================================
// PARTE B: LAS IMPLEMENTACIONES (Adapters) -> Viven en INFRAESTRUCTURA
// Paquete: com.empresa.pagos.infrastructure.adapter.out
// Aquí SÍ usamos todas las anotaciones técnicas de JPA, Hibernate, RestClient, SDKs, etc.
// =========================================================================
package com.empresa.pagos.infrastructure.adapter.out.persistence;

import com.empresa.pagos.domain.model.Pago;
import com.empresa.pagos.domain.port.out.PagoRepositoryPort;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.UUID;

// 1. ADAPTADOR DE BASE DE DATOS (Implementa el puerto de dominio usando Spring Data JPA)
@Component
public class PagoJpaPersistenceAdapter implements PagoRepositoryPort {

    private final SpringDataPagoJpaRepository jpaRepository; // Interfaz técnica extends JpaRepository<PagoJpaEntity, UUID>
    private final PagoPersistenceMapper mapper;              // Traduce entre Pago (Dominio) y PagoJpaEntity (Tabla SQL)

    public PagoJpaPersistenceAdapter(SpringDataPagoJpaRepository jpaRepository,
                                     PagoPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Pago guardar(Pago pagoDominio) {
        PagoJpaEntity entity = mapper.toJpaEntity(pagoDominio);
        PagoJpaEntity guardado = jpaRepository.save(entity);
        return mapper.toDomainModel(guardado);
    }

    @Override
    public Optional<Pago> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomainModel);
    }
}

// 2. ADAPTADOR DE API EXTERNA (Implementa el puerto usando RestClient hacia Mercado Pago / Stripe)
@Component
public class MercadoPagoRestAdapter implements PasarelaExternaPort {

    private final RestClient restClient;

    public MercadoPagoRestAdapter(RestClient.Builder builder) {
        this.restClient = builder.baseUrl("https://api.mercadopago.com").build();
    }

    @Override
    public String autorizarYCapturar(String idempotencyKey, String tokenTarjeta, BigDecimal monto) {
        // Aquí viven todos los detalles técnicos de headers HTTP, JSON de Mercado Pago y manejo de errores 5xx
        return "mp_tx_99887766";
    }
}
```

---

## 7. Árbol de Decisión Rápido: "¿En qué carpeta pongo mi `interface` y mi `class`?"

Cada vez que estés por crear una funcionalidad nueva, hazte estas **3 preguntas en orden**:

```text
PREGUNTA 1: ¿La clase que implementa la lógica necesita conectarse a una Base de Datos,
            llamar a una API HTTP, leer archivos, usar Redis o enviar mensajes a una cola?
   ├── SÍ ──> Es el CASO 3 (Puerto de Salida + Adaptador):
   │          • La 'interface' (...Port) va en DOMINIO o APLICACIÓN.
   │          • La 'class' (...Adapter) va en INFRAESTRUCTURA.
   │
   └── NO (Es lógica en memoria) ──> Pasa a la PREGUNTA 2:

PREGUNTA 2: ¿La clase coordina los pasos de un Caso de Uso completo (abre @Transactional,
            busca en repositorios, llama al dominio y devuelve un DTO al Controller)?
   ├── SÍ ──> Es el CASO 2 (Caso de Uso / Servicio de Aplicación):
   │          • La 'interface' (...UseCase) va en APLICACIÓN (port/in).
   │          • La 'class' (...ApplicationService) va en APLICACIÓN (service).
   │
   └── NO (Es una fórmula matemática, cálculo de precios/impuestos o regla pura del negocio)
              ──> Es el CASO 1 (Política / Estrategia de Dominio):
                  • La 'interface' (...Policy / ...Strategy) va en DOMINIO.
                  • La 'class' (la implementación de la regla) TAMBIÉN va en DOMINIO.
```

---

## 8. Reglas de Oro para Diseñar Contratos Limpios (Sin Fugas de Infraestructura)

Un error muy común al empezar con Arquitectura Hexagonal es crear la interfaz en el paquete `domain` o `application`, pero **contaminarla con tipos técnicos de la capa de Infraestructura (*Leaky Abstraction*)**.

| ❌ Contrato Contaminado (Mala Práctica) | Por qué está mal | ✅ Contrato Limpio (Buena Práctica) |
|---|---|---|
| `Page<PedidoJpaEntity> buscar(Pageable p);` *(en el paquete `domain`)* | Filtra tipos de Spring Data y la entidad de tabla SQL dentro del dominio puro. | Usar objetos propios del dominio/aplicación o dejar la paginación de Spring solo en el lado Query de Aplicación. |
| `ResponseEntity<StripeChargeResponse> cobrar(HttpServletRequest req);` *(en un Puerto de Salida)* | Obliga a tu caso de uso a conocer objetos de la librería de Stripe y de Servlets web. | `ReciboCobro cobrar(OrdenDeCobro orden);` (El `StripeAdapter` traduce adentro). |
| `void guardar(ResultSet rs) throws SQLException;` | Ata el contrato a JDBC/SQL; si mañana cambias a MongoDB o un Mock en memoria, el contrato no sirve. | `void guardar(Cliente cliente);` (Si falla la BD, lanza una `RuntimeException`). |
| Nombrar al puerto según la tecnología: `interface MercadoPagoPort` o `interface PostgresClienteRepository` | Si el contrato se llama `MercadoPagoPort`, no es una abstracción real (viola DIP). | Nombrar al contrato según **el rol de negocio**: `interface PasarelaPagoPort`, `interface ClienteRepositoryPort`. |

---

## 9. El Antipatión `*Service` + `*ServiceImpl` (Cuándo NO crear una Interfaz)

En muchos proyectos viejos de Java (heredados de la época de J2EE / Spring XML de 2005) verás que por costumbre creaban `UsuarioService` y `UsuarioServiceImpl`, `FacturaService` y `FacturaServiceImpl` para absolutamente todo, en la misma carpeta.

### ¿Cuándo una interfaz es INÚTIL (Ruido innecesario) y cuándo SÍ aporta valor?

| Situación | ¿Crear `interface` o Clase Directa? | Explicación |
|---|---|---|
| Un colaborador interno de la misma capa que **solo tiene 1 implementación**, no cruza ninguna frontera hacia infraestructura y nunca va a tener otra estrategia (ej. `ValidadorFormatoTelefono`, `CalculadoraIvaGeneral`). | **Clase directa (`class`) sin interfaz.** *(En JUnit 5 + Mockito moderno puedes mockear clases concretas sin problema).* | Crear `CalculadoraIva` y `CalculadoraIvaImpl` en el mismo paquete duplica archivos sin aportar polimorfismo ni inversión de dependencias (viola **KISS** y **YAGNI**). |
| Un **Puerto de Salida** hacia BD o API externa (aunque hoy solo uses PostgreSQL o solo uses Stripe). | **SÍ crear `interface` (`...Port`) + `class` (`...Adapter`).** | Porque separa la capa de Negocio de la capa de Infraestructura y te permite testear el negocio en memoria sin levantar bases de datos ni servidores externos. |
| Una **Regla de Negocio que varía** según el tipo de cliente, medio de pago o país. | **SÍ crear `interface` (`...Policy` / `...Strategy`) + múltiples `class`.** | Permite agregar nuevas reglas de negocio sin tocar el código existente (**OCP**). |
| Un **Puerto de Entrada (`...UseCase`)** en Arquitectura Hexagonal estricta. | **SÍ crear `interface` (`...UseCase`) + `class` (`...Service`).** | Separa el contrato que consume el Controller de la orquestación interna de la capa de Aplicación. |

---

## 10. Estructura Completa de Carpetas de un Proyecto Profesional en Spring Boot

Así se ve el árbol de paquetes de un microservicio real aplicando todo lo aprendido en este glosario:

```text
src/main/java/com/miempresa/pagos/
│
├── domain/                                       <── 1. CAPA DE DOMINIO (Pura lógica de negocio)
│   ├── model/
│   │   ├── Pago.java                             (Entidad de Dominio pura, sin @Entity de JPA)
│   │   ├── Comercio.java
│   │   └── Dinero.java                           (Value Object record)
│   ├── exception/
│   │   └── LimiteComercioExcedidoException.java
│   ├── policy/                                   <── CASO 1: Interfaz + Implementaciones de Reglas Puras
│   │   ├── PoliticaComisionVenta.java            (interface)
│   │   ├── ComisionGastronomicaPolicy.java       (class implements PoliticaComisionVenta)
│   │   └── ComisionEstandarPolicy.java           (class implements PoliticaComisionVenta)
│   └── port/
│       └── out/                                  <── CASO 3 (Parte A): Contratos que pide el Dominio
│           ├── PagoRepositoryPort.java           (interface)
│           ├── ComercioRepositoryPort.java       (interface)
│           └── PasarelaExternaPort.java          (interface)
│
├── application/                                  <── 2. CAPA DE APLICACIÓN (Casos de Uso)
│   ├── port/
│   │   ├── in/                                   <── CASO 2 (Parte A): Contratos de Entrada (Use Cases)
│   │   │   └── ProcesarCobroComercioUseCase.java (interface)
│   │   └── out/                                  <── Contratos de salida auxiliares de aplicación
│   │       └── NotificadorComercioPort.java      (interface)
│   └── service/                                  <── CASO 2 (Parte B): Implementación de Casos de Uso
│       └── ProcesarCobroComercioService.java     (class implements ProcesarCobroComercioUseCase)
│
└── infrastructure/                               <── 3. CAPA DE INFRAESTRUCTURA (Adaptadores y Frameworks)
    ├── adapter/
    │   ├── in/
    │   │   └── web/                              <── Adaptadores de Entrada (Llaman a los Input Ports)
    │   │       ├── CobroRestController.java      (@RestController -> inyecta ProcesarCobroComercioUseCase)
    │   │       └── CrearCobroHttpRequest.java
    │   └── out/                                  <── CASO 3 (Parte B): Adaptadores que implementan Output Ports
    │       ├── persistence/
    │       │   ├── PagoJpaEntity.java            (@Entity @Table de Hibernate)
    │       │   ├── SpringDataPagoRepository.java (interface extends JpaRepository)
    │       │   ├── PagoJpaAdapter.java           (@Component implements PagoRepositoryPort)
    │       │   └── PagoPersistenceMapper.java
    │       ├── gateway/
    │       │   └── StripePasarelaAdapter.java    (@Component implements PasarelaExternaPort)
    │       └── notification/
    │           └── SmtpNotificadorAdapter.java   (@Component implements NotificadorComercioPort)
    └── config/
        └── DomainBeansConfig.java                (@Configuration que registra las Policies de Dominio como @Bean)
```

> **Detalle Pro (`DomainBeansConfig.java`):** Para mantener la capa `domain` 100% pura en Java (sin siquiera importar `@Component` de Spring dentro de `domain`), las empresas más estrictas declaran los `@Bean` de las políticas de dominio en una clase `@Configuration` dentro de `infrastructure/config`. Si trabajas en un proyecto pragmático de Spring Boot, poner `@Component` arriba de `ComisionGastronomicaPolicy` también es totalmente aceptado.

---

## 11. Ejemplo Completo de Punta a Punta: Los 3 Casos Funcionando Juntos en un Cobro

Veamos cómo el **`@RestController` (Infraestructura de Entrada)** se conecta con el **Caso de Uso (Aplicación)**, que usa la **Política (Dominio)** y el **Adaptador (Infraestructura de Salida)**:

```java
// 1. ADAPTADOR DE ENTRADA (En infrastructure/adapter/in/web):
// Recibe el JSON HTTP y solo conoce la interfaz del Puerto de Entrada (Caso 2)
@RestController
@RequestMapping("/api/v1/cobros")
public class CobroRestController {

    private final ProcesarCobroComercioUseCase procesarCobroUseCase; // Interfaz de Aplicación (Input Port)

    public CobroRestController(ProcesarCobroComercioUseCase procesarCobroUseCase) {
        this.procesarCobroUseCase = procesarCobroUseCase;
    }

    @PostMapping
    public ResponseEntity<ProcesarCobroComercioUseCase.ResultadoCobroDto> cobrar(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody ProcesarCobroComercioUseCase.ComandoCobro body) {

        var comando = new ProcesarCobroComercioUseCase.ComandoCobro(
                body.comercioId(), body.montoBruto(), body.tokenTarjeta(), idempotencyKey
        );
        var resultado = procesarCobroUseCase.ejecutarCobro(comando);
        return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
    }
}

// 2. CONFIGURACIÓN EN INFRAESTRUCTURA (En infrastructure/config):
// Conecta las implementaciones puras de Dominio (Caso 1) al contenedor de Spring Boot
@Configuration
public class DomainPoliciesConfig {

    @Bean
    public PoliticaComisionVenta comisionGastronomicaPolicy() {
        return new ComisionComercioGastronomicoPolicy();
    }

    @Bean
    public PoliticaComisionVenta comisionEstandarPolicy() {
        return new ComisionComercioEstandarPolicy();
    }
}
```

---

## 12. Glosario de Términos de Contratos y Arquitectura Hexagonal

| Término | Nombre en Inglés | Significado Exacto |
|---|---|---|
| **Puerto (*Port*)** | *Port* | Es una **`interface`** que actúa como puerta de entrada o de salida del núcleo de la aplicación. |
| **Adaptador (*Adapter*)** | *Adapter* | Es la **clase concreta en la capa de Infraestructura** que traduce entre el mundo exterior (HTTP, SQL, RabbitMQ) y un Puerto. |
| **Puerto de Entrada (*Driving / Inbound Port*)** | *Input Port / Use Case* | Interfaz en la capa de **Aplicación** que define qué casos de uso pueden ser invocados por los adaptadores primarios (`Controller`). |
| **Puerto de Salida (*Driven / Outbound Port*)** | *Output Port / SPI* | Interfaz en la capa de **Dominio o Aplicación** que define qué necesita el negocio del mundo exterior (implementada por un adaptador secundario en Infraestructura). |
| **Política de Dominio (*Domain Policy*)** | *Domain Policy / Strategy* | Interfaz + Implementaciones que viven **ambas dentro de la capa de Dominio** porque representan reglas o cálculos puros del negocio sin acceso a infraestructura. |
| **Servicio de Dominio (*Domain Service*)** | *Domain Service* | Lógica pura del negocio que involucra a varias entidades del dominio a la vez (ej. `ConciliadorSaldosDomainService`) sin tocar infraestructura. |
| **Servicio de Aplicación (*Application Service*)** | *Application Service* | Clase en la capa de **Aplicación** que implementa un Caso de Uso: abre la transacción, pide datos a los puertos de salida, invoca al dominio y devuelve resultados. |
| **Abstracción con Fugas (*Leaky Abstraction*)** | *Leaky Abstraction* | Error de diseño donde una interfaz de negocio deja escapar detalles técnicos de su implementación (ej. devolver `ResultSet` o lanzar `MongoException`). |

> **Tip de lectura:** Para resumirlo en una sola frase que nunca te vas a olvidar:
> - **Si cambia cuando cambia una ley o regla comercial de la empresa** ➔ El contrato y la implementación van en **Dominio**.
> - **Si cambia cuando cambia los pasos de una pantalla o caso de uso del usuario** ➔ El contrato y la implementación van en **Aplicación**.
> - **Si cambia cuando cambiamos de PostgreSQL a MongoDB o de Mercado Pago a Stripe** ➔ El contrato se queda en **Dominio/Aplicación** y la implementación (`Adapter`) va en **Infraestructura**.

