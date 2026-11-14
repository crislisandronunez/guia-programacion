# Guía y Glosario del Mundo de Pagos y Fintech (Arquitectura en Java y Spring Boot)

Este documento reúne todos los conceptos, actores, flujos financieros, patrones de ingeniería y escenarios reales que
necesitas dominar para **diseñar, construir o mantener sistemas de pagos, billeteras virtuales (*wallets*), pasarelas,
cobros de e-commerce y conciliaciones bancarias** en **Java y Spring Boot**.

---

## Índice

1. [Actores y Ecosistema del Mundo de Pagos](#1-actores-y-ecosistema-del-mundo-de-pagos)
2. [Glosario Fundamental de Términos de Pagos](#2-glosario-fundamental-de-términos-de-pagos)
3. [Reglas de Oro al Programar Dinero en Java (
   `BigDecimal`, Centavos y Monedas)](#3-reglas-de-oro-al-programar-dinero-en-java-bigdecimal-centavos-y-monedas)
4. [Ciclo de Vida de un Pago y Máquina de Estados](#4-ciclo-de-vida-de-un-pago-y-máquina-de-estados)
5. [Escenario 1: Cobro Directo vs Autorización y Captura en 2 Pasos (*Auth &
   Capture*)](#5-escenario-1-cobro-directo-vs-autorización-y-captura-en-2-pasos-auth--capture)
6. [Escenario 2: Pagos Asíncronos y Webhooks Seguros (Firma HMAC + Idempotencia)](#6-escenario-2-pagos-asíncronos-y-webhooks-seguros-firma-hmac--idempotencia)
7. [Escenario 3: Prevención del Doble Cobro (
   `Idempotency-Key` y Bloqueos)](#7-escenario-3-prevención-del-doble-cobro-idempotency-key-y-bloqueos)
8. [Escenario 4: Libro Mayor Contable de Partida Doble (*Double-Entry
   Ledger*)](#8-escenario-4-libro-mayor-contable-de-partida-doble-double-entry-ledger)
9. [Escenario 5: Transferencias entre Billeteras (*P2P*) y Prevención de
   *Deadlocks*](#9-escenario-5-transferencias-entre-billeteras-p2p-y-prevención-de-deadlocks)
10. [Escenario 6: Reembolsos (*Refunds*), Anulaciones (*Voids*) y Contracargos (
    *Chargebacks*)](#10-escenario-6-reembolsos-refunds-anulaciones-voids-y-contracargos-chargebacks)
11. [Escenario 7: Marketplace y División de Pagos (*Split Payments* y
    *Escrow*)](#11-escenario-7-marketplace-y-división-de-pagos-split-payments-y-escrow)
12. [Escenario 8: Suscripciones, Cobros Recurrentes y
    *Dunning* (Reintentos Inteligentes)](#12-escenario-8-suscripciones-cobros-recurrentes-y-dunning-reintentos-inteligentes)
13. [Escenario 9: Estado Desconocido / Timeout de Red y Sonda de Consulta (
    *Polling*)](#13-escenario-9-estado-desconocido--timeout-de-red-y-sonda-de-consulta-polling)
14. [Escenario 10: Conciliación Financiera (*Reconciliation* y
    *Settlement*)](#14-escenario-10-conciliación-financiera-reconciliation-y-settlement)
15. [Seguridad en Pagos: PCI-DSS, Tokenización, 3DS y Antifraude](#15-seguridad-en-pagos-pci-dss-tokenización-3ds-y-antifraude)
16. [Arquitectura Multi-Pasarela (*Payment
    Orchestrator* con Patrón Strategy/Adapter)](#16-arquitectura-multi-pasarela-payment-orchestrator-con-patrón-strategyadapter)
17. [Checklist de Producción para Sistemas de Pagos](#17-checklist-de-producción-para-sistemas-de-pagos)

---

## 1. Actores y Ecosistema del Mundo de Pagos

Cuando un usuario desliza una tarjeta, escanea un QR o paga en una web, el dinero no viaja mágicamente de un lado a
otro. Intervienen estos **6 actores principales**:

```text
[1. Pagador (Cardholder)] ──(Checkout / Token)──> [2. Comercio (Merchant - Tu Sistema)]
                                                           │
                                                           ▼
[6. Banco Emisor (Issuer)] <── [5. Marca / Red (Visa/Master)] <── [4. Adquirente (Acquirer)] <── [3. Pasarela / PSP (Stripe/MP)]
```

| Actor                            | Nombre en Inglés                             | Qué rol cumple                                                                                                                | Ejemplo Real                                                                                                                                 |
|----------------------------------|----------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------|
| **1. Pagador / Tarjetahabiente** | *Payer / Cardholder*                         | El cliente dueño de la tarjeta, cuenta bancaria o billetera virtual que inicia la compra.                                     | El usuario que compra en tu app.                                                                                                             |
| **2. Comercio**                  | *Merchant*                                   | La empresa que vende el producto o servicio (el sistema que tú programas).                                                    | Tu e-commerce, SaaS o app de delivery.                                                                                                       |
| **3. Pasarela de Pagos / PSP**   | *Payment Gateway / Payment Service Provider* | Proveedor tecnológico que expone la API para cobrar sin que tengas que conectarte a cada banco por separado.                  | **Stripe**, **Mercado Pago**, **PayPal**, **Adyen**, **dLocal**, **Payway**.                                                                 |
| **4. Adquirente**                | *Acquirer / Merchant Bank*                   | Entidad financiera que procesa los pagos en nombre del comercio y le deposita el dinero en su cuenta.                         | Fiserv, Prisma (Payway), Getnet, Worldpay.                                                                                                   |
| **5. Red / Marca de Tarjeta**    | *Card Network / Scheme*                      | Red global que conecta adquirentes con bancos emisores y define las reglas de intercambio.                                    | **Visa**, **Mastercard**, **American Express**, redes de transferencias inmediatas (Coelsa/PCT en Argentina, Pix en Brasil, SPEI en México). |
| **6. Banco Emisor**              | *Issuing Bank / Issuer*                      | El banco o fintech que le dio la tarjeta o cuenta al cliente y decide si tiene saldo/límite para aprobar o rechazar el cobro. | Santander, Galicia, BBVA, Brubank, Nubank.                                                                                                   |

**Ejemplo de cómo se modela el rastro de los actores en tu Entidad de Pago:**

```java

@Entity
@Table(name = "transacciones_pago")
public class TransaccionPago {
    @Id
    private UUID id;                       // ID interno de tu Comercio (Merchant)
    private String pspProvider;            // Ej: "MERCADO_PAGO", "STRIPE" (PSP)
    private String pspTransactionId;       // ID externo devuelto por la pasarela
    private String cardNetwork;            // Ej: "VISA", "MASTERCARD" (Red)
    private String issuerBankName;         // Ej: "BANCO_GALICIA" (Emisor)
    private String authorizationCode;      // Código de autorización de 6 dígitos del emisor
}
```

---

## 2. Glosario Fundamental de Términos de Pagos

| Término                                      | Significado en la Industria                                                                                                                                                                                | Ejemplo en tu Sistema                                                                            |
|----------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------|
| **Pay-in (Cobro)**                           | Dinero que **entra** desde los clientes hacia la cuenta de la empresa.                                                                                                                                     | Cobrar una suscripción o un carrito de compras.                                                  |
| **Pay-out (Dispersión / Retiro)**            | Dinero que **sale** desde la empresa hacia cuentas bancarias de terceros (proveedores, vendedores, choferes).                                                                                              | Transferirle las ganancias de la semana al restaurante o repartidor.                             |
| **Payment Intent (Intención de Pago)**       | Objeto que rastrea todo el ciclo de vida de un intento de cobro desde que el usuario abre el checkout hasta que termina (puede incluir varios intentos con distintas tarjetas).                            | `PaymentIntent` con estado `REQUIRES_PAYMENT_METHOD` -> `SUCCEEDED`.                             |
| **Autorización (*Authorization / Hold*)**    | El banco emisor verifica que el cliente tiene saldo y **congela/reserva** el monto en su tarjeta, pero el dinero aún no se transfiere.                                                                     | Reservar $50.000 en la tarjeta al pedir un viaje en Uber o reservar un hotel.                    |
| **Captura (*Capture*)**                      | Confirmar una autorización previa para que el dinero realmente se cobre. Puede ser por el total o por un monto menor (*Partial Capture*).                                                                  | Al terminar el viaje en Uber, capturar el costo final real ($48.500).                            |
| **Anulación (*Void / Reversal*)**            | Cancelar una autorización **antes** de que haya sido capturada/liquidada. Libera el límite de la tarjeta del cliente en el acto sin costo.                                                                 | El usuario cancela el pedido a los 2 minutos de comprar; se hace un *Void*.                      |
| **Reembolso (*Refund*)**                     | Devolver el dinero de un pago que **ya fue capturado**. Puede ser total (*Full Refund*) o parcial (*Partial Refund*).                                                                                      | El cliente devuelve 1 de las 3 remeras que compró la semana pasada; se hace un *Partial Refund*. |
| **Contracargo (*Chargeback*)**               | Disputa iniciada por el cliente directamente ante su banco ("No reconozco esta compra" o "No me entregaron el producto"). El banco retira el dinero al comercio y cobra una multa si se pierde la disputa. | Recibir un webhook `chargeback.created` y congelar la cuenta del usuario sospechoso.             |
| **Liquidación (*Settlement*)**               | Proceso mediante el cual el Adquirente/PSP transfiere finalmente el dinero real a la cuenta bancaria del comercio (suele tardar `T+1`, `T+2` o `T+14` días).                                               | Cobraste el lunes (`T`), pero el dinero líquido entra al banco el miércoles (`T+2`).             |
| **Monto Bruto vs Neto (*Gross vs Net*)**     | **Bruto (*Gross*)**: lo que pagó el cliente ($10.000). **Comisión (*Fee*)**: lo que cobra la pasarela ($500) + Impuestos ($200). **Neto (*Net*)**: lo que realmente recibe la empresa ($9.300).            | Guardar siempre `montoBruto`, `comisionPsp`, `impuestosRetenidos` y `montoNeto`.                 |
| **MDR (*Merchant Discount Rate*)**           | El porcentaje de comisión que cobra la pasarela o procesador por cada venta (ej. 3.5% + IVA).                                                                                                              | `BigDecimal fee = gross.multiply(new BigDecimal("0.035"));`                                      |
| **PAN (*Primary Account Number*)**           | Los 16 dígitos de la tarjeta de crédito/débito. **Dato ultra sensible regulado por PCI-DSS.**                                                                                                              | Jamás guardarlo en texto plano; a lo sumo guardar `ultimos4Digitos = "4242"` y `bin = "450995"`. |
| **BIN / IIN (*Bank Identification Number*)** | Los primeros 6 a 8 dígitos de la tarjeta. Permiten saber qué banco la emitió, de qué país es y si es crédito, débito o prepaga.                                                                            | Consultar el BIN `"450799"` para aplicar automáticamente 20% de descuento con Banco X.           |

**Ejemplo de registro de Monto Bruto, Comisiones y Neto:**

```java
public record DesgloseFinancieroPago(
        BigDecimal montoBruto,       // Ej: 10000.00 (Lo que pagó el cliente)
        BigDecimal comisionPasarela, // Ej:   350.00 (3.5% de Stripe/MercadoPago)
        BigDecimal impuestos,        // Ej:   150.00 (Retenciones / IVA sobre comisión)
        BigDecimal montoNeto         // Ej:  9500.00 (Lo que ingresa real a la caja de la empresa)
) {
    public DesgloseFinancieroPago {
        BigDecimal calculado = montoBruto.subtract(comisionPasarela).subtract(impuestos);
        if (calculado.compareTo(montoNeto) != 0) {
            throw new IllegalArgumentException("El desglose financiero no cuadra: Bruto - Comisiones != Neto");
        }
    }
}
```

---

## 3. Reglas de Oro al Programar Dinero en Java (`BigDecimal`, Centavos y Monedas)

El error más grave y famoso de los programadores que entran al mundo fintech es usar `double` o `float` para representar
dinero. En punto flotante binario (`IEEE 754`), `0.1 + 0.2` da `0.30000000000000004`, haciendo que desaparezcan o
aparezcan centavos de la nada.

| Regla Inquebrantable                                                                     | Por qué                                                                                                                                                                                                  | Ejemplo Correcto vs Incorrecto                                               |
|------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------|
| **1. PROHIBIDO usar `double` o `float`**                                                 | Pierden precisión decimal en operaciones básicas.                                                                                                                                                        | ❌ `double saldo = 10.20;`<br>✅ `BigDecimal saldo = new BigDecimal("10.20");` |
| **2. Instanciar `BigDecimal` siempre con `String` o `valueOf`**                          | `new BigDecimal(0.1)` arrastra la imprecisión del `double` antes de convertirse.                                                                                                                         | ❌ `new BigDecimal(0.1)`<br>✅ `new BigDecimal("0.10")`                        |
| **3. Comparar montos con `compareTo()`, NUNCA con `equals()`**                           | En `BigDecimal`, `new BigDecimal("10.0").equals(new BigDecimal("10.00"))` da **`false`** porque tienen distinta escala. Con `compareTo()` da `0` (iguales).                                              | ❌ `if (monto1.equals(monto2))`<br>✅ `if (monto1.compareTo(monto2) == 0)`     |
| **4. Definir siempre escala y `RoundingMode` al dividir**                                | `100 / 3` en `BigDecimal.divide()` sin modo de redondeo lanza `ArithmeticException: Non-terminating decimal expansion`. En finanzas se usa **`RoundingMode.HALF_EVEN`** (redondeo bancario) o `HALF_UP`. | `total.divide(BigDecimal.valueOf(3), 2, RoundingMode.HALF_EVEN)`             |
| **5. Almacenar en la menor unidad entera (*Minor Units / Cents*) para APIs como Stripe** | Muchas pasarelas internacionales (Stripe, Adyen) piden los montos como `long` en **centavos** para evitar decimales en el JSON (`$15.50 USD` se envía como `1550`).                                      | `long centavos = monto.movePointRight(2).longValueExact();`                  |
| **6. Nunca guardar un monto sin su código de moneda ISO 4217**                           | `1000` sin saber si son `ARS`, `USD`, `BRL` o `EUR` es un dato peligroso. En BD van dos columnas: `monto NUMERIC(19,4)` y `moneda VARCHAR(3)`.                                                           | `Currency.getInstance("ARS")`, `"USD"`, `"BRL"`.                             |

**Ejemplo de Clase `Dinero` (Patrón *Money* de Martin Fowler en Java):**

```java
public record Dinero(BigDecimal monto, Currency moneda) {

    private static final RoundingMode REDONDEO_BANCARIO = RoundingMode.HALF_EVEN;

    public Dinero {
        Objects.requireNonNull(monto, "El monto no puede ser nulo");
        Objects.requireNonNull(moneda, "La moneda no puede ser nula");
        // Ajustamos automáticamente la cantidad de decimales según la moneda (ej: USD=2, JPY=0)
        int decimales = Math.max(moneda.getDefaultFractionDigits(), 2);
        monto = monto.setScale(decimales, REDONDEO_BANCARIO);
    }

    public static Dinero de(String monto, String codigoIsoMoneda) {
        return new Dinero(new BigDecimal(monto), Currency.getInstance(codigoIsoMoneda));
    }

    public Dinero sumar(Dinero otro) {
        validarMismaMoneda(otro);
        return new Dinero(this.monto.add(otro.monto), this.moneda);
    }

    public Dinero restar(Dinero otro) {
        validarMismaMoneda(otro);
        return new Dinero(this.monto.subtract(otro.monto), this.moneda);
    }

    public boolean esMayorOIgualQue(Dinero otro) {
        validarMismaMoneda(otro);
        return this.monto.compareTo(otro.monto) >= 0; // Uso correcto de compareTo
    }

    public long aCentavos() {
        return this.monto.movePointRight(this.moneda.getDefaultFractionDigits()).longValueExact();
    }

    private void validarMismaMoneda(Dinero otro) {
        if (!this.moneda.equals(otro.moneda)) {
            throw new IllegalArgumentException(
                    "Operación inválida entre monedas distintas: " + this.moneda + " vs " + otro.moneda
            );
        }
    }
}
```

---

## 4. Ciclo de Vida de un Pago y Máquina de Estados

Un pago nunca pasa mágicamente de "no existe" a "pagado". Tiene una **Máquina de Estados estricta** que impide errores
gravísimos (como reembolsar un pago que fue rechazado, o volver a marcar como pendiente un pago ya reembolsado).

```text
                       ┌──> [REJECTED / FAILED] (Estado Final)
                       │
[CREATED] ──> [PENDING / PROCESSING] ──> [AUTHORIZED] ──┬──> [VOIDED / CANCELLED] (Estado Final)
                       │                                │
                       └───────────────> [CAPTURED / APPROVED] ──┬──> [PARTIALLY_REFUNDED]
                                                                 ├──> [REFUNDED] (Estado Final)
                                                                 └──> [CHARGED_BACK] (En Disputa)
```

| Estado                       | Significado                                                                                                   | ¿Qué acciones permite?                                                   |
|------------------------------|---------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------|
| **`CREATED`**                | Se creó la intención de pago en tu BD, aún no se llamó a la pasarela.                                         | Enviar a procesar o expirar.                                             |
| **`PENDING` / `IN_PROCESS`** | Esperando acción del usuario (ej. pagar en Rapipago/Pix/Transferencia o completar 3DS) o respuesta de la red. | Aprobar, rechazar o expirar por tiempo.                                  |
| **`AUTHORIZED`**             | El dinero está reservado en la tarjeta del cliente, esperando que confirmes el envío del producto.            | **Capturar (`CAPTURE`)** o **Anular (`VOID`)**.                          |
| **`CAPTURED` / `APPROVED`**  | ¡Dinero cobrado con éxito! Aquí recién se entrega el producto o se acredita el saldo.                         | **Reembolsar (`REFUND`)** o recibir un **Contracargo (`CHARGEBACK`)**.   |
| **`REJECTED` / `FAILED`**    | El banco o el antifraude rechazó el pago (fondos insuficientes, tarjeta vencida, sospecha de fraude).         | Ninguna sobre esta transacción (el usuario debe crear un nuevo intento). |
| **`VOIDED`**                 | La reserva fue liberada antes de cobrarse.                                                                    | Ninguna (Estado terminal).                                               |
| **`REFUNDED`**               | Se devolvió el 100% del dinero al cliente.                                                                    | Ninguna (Estado terminal).                                               |

**Ejemplo de Máquina de Estados de Pago en Java:**

```java
public enum EstadoPago {
    CREATED,
    PENDING,
    AUTHORIZED,
    CAPTURED,
    REJECTED,
    VOIDED,
    REFUNDED,
    CHARGED_BACK;

    public boolean puedeTransicionarA(EstadoPago nuevo) {
        return switch (this) {
            case CREATED -> EnumSet.of(PENDING, AUTHORIZED, CAPTURED, REJECTED).contains(nuevo);
            case PENDING -> EnumSet.of(AUTHORIZED, CAPTURED, REJECTED, VOIDED).contains(nuevo);
            case AUTHORIZED -> EnumSet.of(CAPTURED, VOIDED).contains(nuevo);
            case CAPTURED -> EnumSet.of(REFUNDED, CHARGED_BACK).contains(nuevo);
            // Estados terminales: jamás pueden volver atrás
            case REJECTED, VOIDED, REFUNDED, CHARGED_BACK -> false;
        };
    }

    public void validarTransicion(EstadoPago nuevo) {
        if (!puedeTransicionarA(nuevo)) {
            throw new IllegalStateException("Transición de pago prohibida: " + this + " -> " + nuevo);
        }
    }
}
```

---

## 5. Escenario 1: Cobro Directo vs Autorización y Captura en 2 Pasos (*Auth & Capture*)

En el mundo de pagos existen dos formas de cobrar con tarjeta:

| Modalidad                                          | Cómo funciona                                                                                                                                                                                                                                                                                 | En qué negocios se usa                                                                                                               | Ejemplo                                                                                                            |
|----------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------|
| **Cobro en 1 Paso (*Direct Sale / Auto-Capture*)** | Autoriza y captura el dinero en la misma llamada HTTP (`capture: true`).                                                                                                                                                                                                                      | Suscripciones digitales (Netflix, Spotify), recargas de saldo, entradas de cine.                                                     | El producto es digital e instantáneo; sabemos que hay stock infinito.                                              |
| **Cobro en 2 Pasos (*Auth & Capture*)**            | **Paso 1 (`Auth`):** Reserva el monto en la tarjeta (`capture: false`).<br>**Paso 2 (`Capture` o `Void`):** Cuando el supermercado pesa la carne y arma la caja real, captura el monto exacto. Si no había stock del producto, hace `Void` y al cliente nunca se le cobró nada en su resumen. | Supermercados online, apps de movilidad (Uber/Cabify), hoteles, alquiler de autos, e-commerce con stock físico en múltiples tiendas. | El cliente pide 1 kg de queso ($10.000), la balanza marca 920 gramos ($9.200): reservas $10.000 y capturas $9.200. |

**Ejemplo de Servicio con Autorización y Captura en 2 Pasos en Spring Boot:**

```java

@Service
public class PagoDosPasosService {

    private final PasarelaPagoPort pasarela;
    private final PagoRepository pagoRepository;

    public PagoDosPasosService(PasarelaPagoPort pasarela, PagoRepository pagoRepository) {
        this.pasarela = pasarela;
        this.pagoRepository = pagoRepository;
    }

    // PASO 1: Al confirmar el carrito, solo AUTORIZAMOS (congelamos el cupo de la tarjeta)
    @Transactional
    public Pago reservarFondosParaPedido(Long pedidoId, Dinero montoEstimado, String cardToken) {
        ResultadoPasarela res = pasarela.autorizarSinCapturar(pedidoId, montoEstimado, cardToken);

        Pago pago = new Pago(pedidoId, montoEstimado, res.externalPaymentId());
        pago.cambiarEstado(res.aprobado() ? EstadoPago.AUTHORIZED : EstadoPago.REJECTED);
        return pagoRepository.save(pago);
    }

    // PASO 2 A: El depósito armó la caja con éxito -> CAPTURAMOS el monto final (igual o menor al autorizado)
    @Transactional
    public Pago capturarFondosAlDespachar(Long pagoId, Dinero montoRealFinal) {
        Pago pago = pagoRepository.findByIdConBloqueo(pagoId).orElseThrow();
        pago.getEstado().validarTransicion(EstadoPago.CAPTURED);

        if (montoRealFinal.monto().compareTo(pago.getMontoAutorizado().monto()) > 0) {
            throw new IllegalArgumentException("No se puede capturar un monto mayor al autorizado originalmente");
        }

        pasarela.capturarAutorizacion(pago.getExternalPaymentId(), montoRealFinal);
        pago.registrarCaptura(montoRealFinal);
        return pagoRepository.save(pago);
    }

    // PASO 2 B: No había stock en el depósito -> ANULAMOS (VOID) la reserva sin costo ni cargo para el cliente
    @Transactional
    public Pago anularReservaPorFaltaDeStock(Long pagoId) {
        Pago pago = pagoRepository.findByIdConBloqueo(pagoId).orElseThrow();
        pago.getEstado().validarTransicion(EstadoPago.VOIDED);

        pasarela.anularAutorizacion(pago.getExternalPaymentId());
        pago.cambiarEstado(EstadoPago.VOIDED);
        return pagoRepository.save(pago);
    }
}
```

---

## 6. Escenario 2: Pagos Asíncronos y Webhooks Seguros (Firma HMAC + Idempotencia)

Cuando un cliente paga con **QR, Pix, Transferencia Bancaria, Cripto, PagoFácil/Rapipago** o **Checkout Redireccionado
**, el resultado no está en el momento: el cliente puede tardar 5 minutos o 2 horas en pagar.
Cuando paga, la pasarela (Mercado Pago, Stripe, dLocal) hace una petición `POST` hacia tu servidor llamada **Webhook**.

### Las 5 Reglas de Seguridad de un Webhook de Pagos

1. **Verificar siempre la Firma Criptográfica (`HMAC-SHA256`)**: Cualquier hacker puede hacer un `POST` a tu URL
   `/api/webhooks/pagos` diciendo `"estado": "APPROVED"`. Debes validar la firma con tu `Webhook Secret` para comprobar
   que el mensaje realmente vino de la pasarela.
2. **Idempotencia en la Recepción**: Las pasarelas envían el mismo webhook **varias veces** si hubo un microcorte.
   Guardar el `eventId` del webhook en una tabla con restricción `UNIQUE` para no acreditar el saldo dos veces.
3. **No confiar a ciegas en el body (*Check-the-Checker*)**: Opcionalmente (muy común en Mercado Pago), recibir el `id`
   del pago en el webhook e ir a consultar con un `GET` a la API oficial de la pasarela el estado real actual.
4. **Tolerancia a Webhooks Desordenados**: El webhook `payment.captured` puede llegar 100ms *antes* que
   `payment.created`. Tu código debe manejarlo sin romperse.
5. **Responder `200 OK` rápido (< 2 segundos)**: Si tardas mucho, la pasarela cree que tu servidor falló y te vuelve a
   disparar el webhook en bucle.

**Ejemplo Completo de Receptor de Webhooks de Pagos en Spring Boot:**

```java

@RestController
@RequestMapping("/api/v1/webhooks/pagos")
public class WebhookPagosController {

    private final VerificadorFirmaWebhook verificadorFirma;
    private final ProcesadorWebhookPagoService procesadorService;

    public WebhookPagosController(VerificadorFirmaWebhook verificadorFirma,
                                  ProcesadorWebhookPagoService procesadorService) {
        this.verificadorFirma = verificadorFirma;
        this.procesadorService = procesadorService;
    }

    @PostMapping
    public ResponseEntity<Void> recibirNotificacion(
            @RequestHeader("X-Signature") String firmaRecibida,
            @RequestBody String payloadCrudo) { // Recibimos el JSON crudo exacto para calcular el HMAC

        // 1. SEGURIDAD: Si la firma criptográfica no coincide con nuestro secreto, rechazamos con 401
        if (!verificadorFirma.esFirmaValida(payloadCrudo, firmaRecibida)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 2. Procesar de forma idempotente
        procesadorService.procesarEventoWebhook(payloadCrudo);

        // 3. Devolver 200 OK rápido para que la pasarela sepa que lo recibimos
        return ResponseEntity.ok().build();
    }
}

@Component
public class VerificadorFirmaWebhook {

    @Value("${pagos.webhook.secret}")
    private String webhookSecret;

    public boolean esFirmaValida(String payload, String firmaHexRecibida) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(webhookSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            hmac.init(keySpec);
            byte[] hashCalculado = hmac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String hashHexCalculado = HexFormat.of().formatHex(hashCalculado);

            // MessageDigest.isEqual compara en tiempo constante para evitar Timing Attacks
            return MessageDigest.isEqual(
                    hashHexCalculado.getBytes(StandardCharsets.UTF_8),
                    firmaHexRecibida.getBytes(StandardCharsets.UTF_8)
            );
        } catch (Exception e) {
            return false;
        }
    }
}
```

---

## 7. Escenario 3: Prevención del Doble Cobro (`Idempotency-Key` y Bloqueos)

### El Problema

El usuario hace clic en "
Pagar $50.000". Su conexión 4G parpadea, no ve respuesta y vuelve a tocar el botón. O tu microservicio llama a Stripe, hay un *Read Timeout*, y tu librería de reintentos vuelve a llamar a Stripe. Sin idempotencia, **le cobras $
100.000 al cliente**.

### Cómo funciona la Idempotencia de Punta a Punta

1. **Del Frontend a tu Backend**: La app móvil/web genera un `UUID` único cuando el usuario entra a la pantalla de
   checkout y lo envía en el header `Idempotency-Key`.
2. **En tu Base de Datos**: Guardas ese `Idempotency-Key` en una tabla con restricción `UNIQUE` antes de hablar con la
   pasarela.
3. **De tu Backend a la Pasarela (Stripe / Mercado Pago / Adyen)**: Le reenvías ese mismo header `Idempotency-Key` (o
   `X-Idempotency-Key`) a la API de la pasarela. Así, aunque reintentes la llamada HTTP 5 veces por cortes de red, la
   pasarela reconoce la clave y **cobra una sola vez**.

**Ejemplo de Cliente de Pasarela enviando `X-Idempotency-Key` en Spring Boot (`RestClient`):**

```java

@Component
public class MercadoPagoClientAdapter {

    private final RestClient restClient;

    public MercadoPagoClientAdapter(RestClient.Builder builder,
                                    @Value("${mercadopago.access-token}") String accessToken) {
        this.restClient = builder
                .baseUrl("https://api.mercadopago.com")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .build();
    }

    public RespuestaMpDto crearPagoSeguro(String idempotencyKey, SolicitudMpDto request) {
        return restClient.post()
                .uri("/v1/payments")
                // CRÍTICO: Si reintentamos por error de red, Mercado Pago no vuelve a cobrar la tarjeta
                .header("X-Idempotency-Key", idempotencyKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(RespuestaMpDto.class);
    }
}
```

---

## 8. Escenario 4: Libro Mayor Contable de Partida Doble (*Double-Entry Ledger*)

### La Regla Secreta de los Bancos y Billeteras Virtuales

En un sistema de pagos profesional **NUNCA se diseña una simple tabla `usuarios` con una columna `saldo` que se
sobreescribe con `UPDATE usuarios SET saldo = 500`** sin más. Si alguien hace un `UPDATE` erróneo o hay un bug, es
imposible saber de dónde salió o adónde fue la plata.

Los bancos, fintechs (Mercado Pago, Ualá, Revolut, Stripe) usan un **Libro Mayor de Partida
Doble (*Double-Entry Ledger*) Inmutable**:

1. **El dinero no se crea ni se destruye, se mueve**: Por cada movimiento de dinero existen al menos **dos asientos
   contables (*Ledger Entries*)**: un **Débito (sale de la Cuenta A)** y un **Crédito (entra a la Cuenta B)** cuya suma
   algebraica da **exactamente CERO (`0.00`)**.
2. **Las filas del Ledger son inmutables (*Append-Only*)**: Jamás se hace `UPDATE` ni `DELETE` de un movimiento pasado.
   Si hubo un error o devolución, se inserta un **nuevo movimiento inverso (Contra-asiento)**.
3. **El saldo es la suma de sus movimientos**: El campo `saldo_actual` en la tabla `cuentas` es solo una caché
   optimizada que siempre debe coincidir con `SUM(creditos) - SUM(debitos)`.

| Tabla en la Base de Datos                              | Qué representa                                                                                                                            | Ejemplo                                                                                                                            |
|--------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------|
| **`cuentas_contables` (*Accounts*)**                   | Cuentas de usuarios y cuentas internas del sistema (ej. `CUENTA_RECAUDADORA_BANCO`, `CUENTA_COMISIONES_EMPRESA`, `BILLETERA_USUARIO_45`). | Cuenta #100 (Usuario Juan), Cuenta #1 (Caja Recaudadora).                                                                          |
| **`transacciones_contables` (*Journal Transactions*)** | Agrupador de la operación (fecha, motivo, `idempotency_key`, referencia al pago).                                                         | Transacción `"Cobro Pedido #889"`.                                                                                                 |
| **`asientos_contables` (*Ledger Entries*)**            | Cada línea individual de débito (`-`) o crédito (`+`). **La suma de todas las líneas de una misma transacción siempre debe ser `0.00`**.  | Línea 1: Cuenta Cliente `-1000.00`<br>Línea 2: Cuenta Vendedor `+950.00`<br>Línea 3: Cuenta Comisiones `+50.00`<br>*(Suma = 0.00)* |

**Ejemplo de Motor Contable de Partida Doble en Spring Boot:**

```java
public record LineaAsientoRequest(Long cuentaId, BigDecimal monto) {
} // Negativo = Débito, Positivo = Crédito

@Service
public class LibroMayorContableService {

    private final CuentaContableRepository cuentaRepository;
    private final TransaccionContableRepository transaccionRepository;

    public LibroMayorContableService(CuentaContableRepository cuentaRepository,
                                     TransaccionContableRepository transaccionRepository) {
        this.cuentaRepository = cuentaRepository;
        this.transaccionRepository = transaccionRepository;
    }

    @Transactional
    public TransaccionContable registrarAsientoDoblePartida(String claveIdempotencia,
                                                            String descripcion,
                                                            List<LineaAsientoRequest> lineas) {
        // 1. REGLA SAGRADA DE PARTIDA DOBLE: La suma de todos los movimientos DEBE dar exactamente 0.00
        BigDecimal sumaTotal = lineas.stream()
                .map(LineaAsientoRequest::monto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (sumaTotal.compareTo(BigDecimal.ZERO) != 0) {
            throw new DesbalanceContableException(
                    "Violación de partida doble: los asientos no suman cero. Desbalance: " + sumaTotal
            );
        }

        // 2. Bloquear las cuentas en orden ascendente de ID (previene Deadlocks)
        List<Long> idsOrdenados = lineas.stream()
                .map(LineaAsientoRequest::cuentaId)
                .distinct()
                .sorted()
                .toList();
        Map<Long, CuentaContable> cuentas = cuentaRepository.findAllByIdConBloqueoPesimista(idsOrdenados)
                .stream()
                .collect(Collectors.toMap(CuentaContable::getId, Function.identity()));

        // 3. Crear la transacción inmutable y actualizar los saldos cacheados
        TransaccionContable transaccion = new TransaccionContable(claveIdempotencia, descripcion);

        for (LineaAsientoRequest linea : lineas) {
            CuentaContable cuenta = cuentas.get(linea.cuentaId());
            cuenta.aplicarMovimiento(linea.monto()); // Valida que no quede en negativo si no permite descubierto
            transaccion.agregarAsiento(new AsientoContable(cuenta.getId(), linea.monto(), cuenta.getSaldoActual()));
        }

        return transaccionRepository.save(transaccion);
    }
}
```

---

## 9. Escenario 5: Transferencias entre Billeteras (*P2P*) y Prevención de *Deadlocks*

### El Problema Clásico de Concurrencia en Billeteras

Imagina que en el mismo milisegundo:

- **Hilo 1**: Juan (Cuenta `ID=10`) le transfiere $100 a María (Cuenta `ID=20`). El Hilo 1 bloquea la Cuenta `10` y
  espera bloquear la `20`.
- **Hilo 2**: María (Cuenta `ID=20`) le transfiere $50 a Juan (Cuenta `ID=10`). El Hilo 2 bloquea la Cuenta `20` y
  espera bloquear la `10`.
- **Resultado**: **abrazo mortal (*Deadlock*)**. Ambos hilos quedan congelados esperando al otro hasta que la base de
  datos mata una de las transacciones.

### La Solución de Ingeniería: Ordenamiento Determinístico de Bloqueos (*Lock Ordering*)

Siempre que una operación involucre dos o más cuentas, **ordena los IDs de menor a mayor antes de
hacer `SELECT ... FOR UPDATE`**. Así, tanto el Hilo 1 como el Hilo 2 intentarán bloquear **primero la Cuenta `10` y
segundo la Cuenta `20`**, haciendo imposible un *Deadlock*.

```java

@Service
public class TransferenciaBilleteraService {

    private final CuentaBilleteraRepository cuentaRepository;

    public TransferenciaBilleteraService(CuentaBilleteraRepository cuentaRepository) {
        this.cuentaRepository = cuentaRepository;
    }

    @Transactional
    public void transferirEntreUsuarios(Long cuentaOrigenId, Long cuentaDestinoId, BigDecimal monto) {
        if (cuentaOrigenId.equals(cuentaDestinoId)) {
            throw new IllegalArgumentException("No se puede transferir a la misma cuenta");
        }

        // PREVENCIÓN DE DEADLOCK: Siempre bloqueamos primero el ID menor y segundo el ID mayor
        Long primeroEnBloquear = Math.min(cuentaOrigenId, cuentaDestinoId);
        Long segundoEnBloquear = Math.max(cuentaOrigenId, cuentaDestinoId);

        CuentaBilletera primera = cuentaRepository.findByIdForUpdate(primeroEnBloquear).orElseThrow();
        CuentaBilletera segunda = cuentaRepository.findByIdForUpdate(segundoEnBloquear).orElseThrow();

        CuentaBilletera origen = cuentaOrigenId.equals(primeroEnBloquear) ? primera : segunda;
        CuentaBilletera destino = cuentaDestinoId.equals(primeroEnBloquear) ? primera : segunda;

        origen.debitar(monto);   // Lanza SaldoInsuficienteException si saldo < monto
        destino.acreditar(monto);
    }
}
```

---

## 10. Escenario 6: Reembolsos (*Refunds*), Anulaciones (*Voids*) y Contracargos (*Chargebacks*)

En el mundo real, los pagos vuelven hacia atrás. Tu sistema debe soportar tres mecanismos distintos:

| Escenario                                | Quién lo inicia                                 | Regla Crítica de Sistema                                                                                                                                                                                                                  | Ejemplo de Validación en Código                                                                                 |
|------------------------------------------|-------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------|
| **Reembolso Parcial o Total (*Refund*)** | El comercio o el cliente desde tu app.          | **La suma de todos los reembolsos parciales jamás puede superar el monto original capturado.** (`montoYaReembolsado + nuevoReembolso <= montoCapturado`).                                                                                 | Un pago de $10.000 puede tener 2 reembolsos de $3.000 y uno de $4.000, pero rechazar un cuarto reembolso de $1. |
| **Anulación (*Void*)**                   | El comercio antes del cierre de lote / captura. | Solo aplica si el pago está en `AUTHORIZED` o no liquidado. No admite montos parciales (se libera el 100%).                                                                                                                               | `if (pago.getEstado() != AUTHORIZED) throw ...`                                                                 |
| **Contracargo (*Chargeback*)**           | El banco del cliente (forzoso).                 | Cuando llega el webhook `chargeback.created`, debes: 1) Cambiar el estado, 2) Descontar/congelar el saldo del vendedor, 3) Subir evidencia (remito de entrega, IP, factura) a la API de disputas de la pasarela antes de la fecha límite. | Marcar al usuario con `alertaFraude = true` y registrar el contra-asiento contable.                             |

**Ejemplo de Control de Reembolsos Parciales Acumulados:**

```java

@Entity
public class PagoCapturado {
    @Id
    private Long id;
    private BigDecimal montoCapturado;
    private BigDecimal montoTotalReembolsado = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    private EstadoPago estado = EstadoPago.CAPTURED;

    public void registrarReembolso(BigDecimal montoAReembolsar) {
        if (montoAReembolsar.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto a reembolsar debe ser positivo");
        }
        BigDecimal nuevoAcumulado = this.montoTotalReembolsado.add(montoAReembolsar);

        // Regla crítica: Nunca devolver más plata de la que originalmente entró
        if (nuevoAcumulado.compareTo(this.montoCapturado) > 0) {
            BigDecimal disponible = this.montoCapturado.subtract(this.montoTotalReembolsado);
            throw new MontoReembolsoExcedidoException(
                    "No se puede reembolsar $" + montoAReembolsar + ". Saldo reembolsable restante: $" + disponible
            );
        }

        this.montoTotalReembolsado = nuevoAcumulado;
        if (this.montoTotalReembolsado.compareTo(this.montoCapturado) == 0) {
            this.estado = EstadoPago.REFUNDED;
        }
    }
}
```

---

## 11. Escenario 7: Marketplace y División de Pagos (*Split Payments* y *Escrow*)

### El Problema de Negocio

Imagina que construyes una plataforma estilo **Mercado Libre, PedidosYa, Airbnb o una ticketera de eventos**:

- El comprador paga **$100.000**.
- Tu plataforma se queda con una comisión del **10% ($10.000)**.
- El vendedor/restaurante recibe el **90% ($90.000)**, pero **recién cuando el paquete llega al comprador** (cuenta de
  garantía o *Escrow*).

| Concepto                                                     | Significado                                                                                                                                                                                                                                                                                           | Cómo se resuelve                                                                                                             |
|--------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------|
| **Split Payment (División de Pago)**                         | Dividir un único cobro en múltiples destinatarios (Vendedor A, Vendedor B, Comisión de Plataforma) automáticamente.                                                                                                                                                                                   | Soportado nativamente por Stripe Connect / Mercado Pago Marketplace o mediante el Libro Mayor interno.                       |
| **Escrow (Cuenta en Garantía / Saldo a Liquidar)**           | El dinero cobrado no va a `saldo_disponible` del vendedor de inmediato: entra a `saldo_en_liquidacion` (*Pending Balance*). Cuando el correo confirma la entrega (o pasan X días sin reclamos), un proceso libera el dinero hacia `saldo_disponible` para que el vendedor pueda retirarlo a su banco. | Dos cuentas contables por vendedor: `VENDEDOR_EN_CUSTODIA` y `VENDEDOR_DISPONIBLE`.                                          |
| **Problema del Centavo Sobrante (*Penny Rounding Problem*)** | Si divides `$100.00` en 3 partes iguales (`33.33 + 33.33 + 33.33 = 99.99`), **falta `$0.01`**.                                                                                                                                                                                                        | La última parte siempre se calcula por diferencia (`total - sumaDeLasPartesAnteriores`) para no perder ni inventar centavos. |

**Ejemplo de Cálculo de Split y Resolución del Centavo Sobrante en Java:**

```java
public record ResultadoSplit(BigDecimal montoVendedor, BigDecimal comisionPlataforma, BigDecimal costoPasarela) {
}

@Component
public class CalculadoraSplitMarketplace {

    public ResultadoSplit calcularDivision(BigDecimal montoTotalVenta,
                                           BigDecimal porcentajeComisionPlataforma,
                                           BigDecimal costoRealPasarela) {
        // 1. Calculamos nuestra comisión redondeando con HALF_EVEN a 2 decimales
        BigDecimal comisionPlataforma = montoTotalVenta
                .multiply(porcentajeComisionPlataforma)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_EVEN);

        // 2. El monto del vendedor se obtiene por RESTA EXACTA para garantizar que:
        // montoVendedor + comisionPlataforma + costoPasarela == montoTotalVenta (al centavo exacto)
        BigDecimal montoVendedor = montoTotalVenta
                .subtract(comisionPlataforma)
                .subtract(costoRealPasarela);

        return new ResultadoSplit(montoVendedor, comisionPlataforma, costoRealPasarela);
    }
}
```

---

## 12. Escenario 8: Suscripciones, Cobros Recurrentes y *Dunning* (Reintentos Inteligentes)

### El Problema de Negocio

En un negocio de suscripciones (SaaS, gimnasio, colegio, seguro, streaming), cobras todos los meses automáticamente
usando una **tarjeta tokenizada (*Card on File*)**.
¿Qué pasa cuando el día 1 del mes el cliente aún no cobró el sueldo y la tarjeta da `insufficient_funds`? Si le cancelas
la suscripción en el acto, pierdes al cliente (*Involuntary Churn*).

### Conceptos Clave de Cobros Recurrentes

| Concepto                                                 | Significado                                                                                                                                                                                                                                                                                                                                                           | Regla Técnica                                                                        |
|----------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------|
| **Códigos de Rechazo: *Soft Decline* vs *Hard Decline*** | - **Soft Decline (Rechazo Suave):** Temporal (`insufficient_funds`, `issuer_unavailable`, `try_again_later`). **Sí se debe reintentar.**<br>- **Hard Decline (Rechazo Duro):** Definitivo (`stolen_card`, `invalid_account`, `expired_card`). **PROHIBIDO reintentar** (las marcas Visa/Mastercard multan al comercio si reintenta tarjetas robadas o dadas de baja). | Clasificar el `decline_code` de la pasarela antes de programar el siguiente intento. |
| **Dunning (Gestión de Reintentos y Avisos)**             | Estrategia escalonada de reintentos (ej. Día 1, Día 3, Día 7 y Día 10) combinada con emails amistosos pidiendo actualizar el medio de pago.                                                                                                                                                                                                                           | Estado `PAST_DUE` (En mora con período de gracia) antes de pasar a `CANCELED`.       |
| **Período de Gracia (*Grace Period*)**                   | Seguir dándole acceso al servicio durante 5 o 7 días mientras reintentas el cobro en segundo plano.                                                                                                                                                                                                                                                                   | `suscripcion.PermiteAcceso()` devuelve `true` tanto en `ACTIVE` como en `PAST_DUE`.  |

**Ejemplo de Motor de Cobro de Suscripciones (Diferenciando *Soft* vs *Hard Decline*):**

```java

@Service
public class CobroSuscripcionService {

    private static final Set<String> HARD_DECLINE_CODES = Set.of(
            "stolen_card", "lost_card", "invalid_card_number", "card_not_supported"
    );

    private final PasarelaPagoPort pasarela;
    private final SuscripcionRepository suscripcionRepository;

    public CobroSuscripcionService(PasarelaPagoPort pasarela, SuscripcionRepository suscripcionRepository) {
        this.pasarela = pasarela;
        this.suscripcionRepository = suscripcionRepository;
    }

    @Transactional
    public void ejecutarCobroMensual(Long suscripcionId) {
        Suscripcion sub = suscripcionRepository.findById(suscripcionId).orElseThrow();
        String idempotencyKey = "SUB-" + sub.getId() + "-PERIODO-" + sub.getPeriodoActual();

        ResultadoCobroDto resultado = pasarela.cobrarTarjetaGuardada(
                idempotencyKey, sub.getCustomerToken(), sub.getCardToken(), sub.getPrecioMensual()
        );

        if (resultado.aprobado()) {
            sub.renovarPorUnMesMas();
        } else if (HARD_DECLINE_CODES.contains(resultado.codigoRechazo())) {
            // HARD DECLINE: Prohibido reintentar esta tarjeta. Pedimos al usuario que cargue otra.
            sub.suspenderPorTarjetaInvalida(resultado.codigoRechazo());
        } else {
            // SOFT DECLINE (ej. sin saldo hoy): Programamos reintento para dentro de 3 días (Dunning)
            sub.programarSiguienteReintentoDunning(LocalDate.now().plusDays(3));
        }
    }
}
```

---

## 13. Escenario 9: Estado Desconocido / Timeout de Red y Sonda de Consulta (*Polling*)

### El Escenario Más Peligroso en Pagos

Tu servidor envía `POST /payments` a la pasarela para cobrar $200.000. Pasan 5 segundos y salta
`SocketTimeoutException` (se cortó la conexión esperando la respuesta).
**Pregunta crítica: ¿Se cobró o no se cobró en la pasarela?**

- **Error fatal de principiante**: Asumir que como dio excepción "falló el pago", marcarlo como `REJECTED` en tu base de
  datos y decirle al usuario "Intenta de nuevo". (Si en realidad sí había llegado a la pasarela, al usuario le cobraste
  pero tu sistema dice que no pagó).

### La Solución: Estado `UNKNOWN / PENDING_CONFIRMATION` + Sonda de Reconciliación

1. Cuando una llamada de pago sufre un *Timeout* de lectura o error `502/504`, el pago queda en estado *
   *`PENDING_CONFIRMATION`**.
2. Un job en segundo plano (*Polling*) o el Webhook consulta a la pasarela buscando por el `externalReference` o
   `idempotencyKey` para descubrir qué pasó realmente y recién ahí lo pasa a `CAPTURED` o `REJECTED`.

```java

@Service
public class SondaPagosInciertoJob {

    private final PagoRepository pagoRepository;
    private final PasarelaPagoPort pasarela;

    public SondaPagosInciertoJob(PagoRepository pagoRepository, PasarelaPagoPort pasarela) {
        this.pagoRepository = pagoRepository;
        this.pasarela = pasarela;
    }

    // Cada 2 minutos busca pagos que quedaron en estado incierto por Timeout de red hace más de 1 minuto
    @Scheduled(fixedDelay = 120_000)
    public void resolverPagosEnEstadoIncierto() {
        Instant haceUnMinuto = Instant.now().minusSeconds(60);
        List<Pago> inciertos = pagoRepository.findByEstadoAndCreadoAntesDe(
                EstadoPago.PENDING_CONFIRMATION, haceUnMinuto
        );

        for (Pago pago : inciertos) {
            Optional<EstadoRemotoDto> estadoRealEnPasarela = pasarela.buscarPagoPorReferenciaExterna(
                    pago.getIdempotencyKey()
            );

            if (estadoRealEnPasarela.isPresent()) {
                // La pasarela sí lo había procesado: actualizamos nuestro sistema con la verdad oficial
                pago.sincronizarConEstadoPasarela(estadoRealEnPasarela.get().estado());
            } else if (pago.llevaMasDeMinutosEnIncierto(30)) {
                // Pasaron 30 minutos y la pasarela confirma que jamás recibió ese pago -> Lo damos por fallido
                pago.cambiarEstado(EstadoPago.REJECTED);
            }
            pagoRepository.save(pago);
        }
    }
}
```

---

## 14. Escenario 10: Conciliación Financiera (*Reconciliation* y *Settlement*)

### ¿Por qué todas las empresas de pagos tienen un proceso de Conciliación?

Aunque tengas webhooks e idempotencia perfectos, en el mundo real siempre pueden ocurrir desfasajes entre lo que dice tu
base de datos y lo que dice el reporte oficial de liquidación del banco/pasarela:

- Un webhook nunca llegó porque tu servidor estuvo caído 3 horas.
- La pasarela cobró una comisión distinta a la pactada.
- Hubo un contracargo o ajuste manual en el panel de la pasarela.

### Cómo funciona la Conciliación Automática (*3-Way Reconciliation*)

Todas las madrugadas, tu sistema descarga el archivo de liquidación (*Settlement Report* en CSV/JSON/SFTP) de la
pasarela y cruza registro por registro contra tu base de datos detectando las **4 posibles discrepancias**:

| Tipo de Discrepancia                                      | Qué significa                                                                              | Acción del Sistema                                                                  |
|-----------------------------------------------------------|--------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------|
| **1. Falta en Nuestro Sistema (*Missing Internally*)**    | La pasarela cobró el pago, pero en nuestra BD figura pendiente o no existe.                | Acreditar/confirmar el pago faltante y alertar a Operaciones.                       |
| **2. Falta en la Pasarela (*Missing in PSP*)**            | Nuestra BD dice `APPROVED`, pero en el reporte oficial de la pasarela no existe ese cobro. | **Alerta Crítica (Severidad 1):** Posible bug o fraude; congelar entrega o auditar. |
| **3. Diferencia de Estado (*Status Mismatch*)**           | En nuestra BD dice `APPROVED` pero en la pasarela figura `REFUNDED`.                       | Sincronizar el estado y generar el asiento contable de ajuste.                      |
| **4. Diferencia de Monto o Comisión (*Amount Mismatch*)** | Esperábamos recibir $9.500 netos pero la pasarela liquidó $9.200.                          | Registrar desvío de comisión para reclamo comercial al PSP.                         |

**Ejemplo de Conciliador Automático en Java:**

```java
public record RegistroLiquidacionPsp(String referenciaInterna, BigDecimal montoBruto, String estadoPsp) {
}

@Service
public class ConciliadorFinancieroService {

    public List<IncidenciaConciliacion> conciliarDia(LocalDate fecha,
                                                     List<RegistroLiquidacionPsp> reportePsp,
                                                     List<Pago> pagosEnNuestraBd) {
        List<IncidenciaConciliacion> incidencias = new ArrayList<>();

        Map<String, Pago> mapaInterno = pagosEnNuestraBd.stream()
                .collect(Collectors.toMap(Pago::getReferenciaUnica, Function.identity()));

        Set<String> referenciasVistasEnPsp = new HashSet<>();

        // 1. Revisar cada cobro reportado por la pasarela
        for (RegistroLiquidacionPsp itemPsp : reportePsp) {
            referenciasVistasEnPsp.add(itemPsp.referenciaInterna());
            Pago pagoLocal = mapaInterno.get(itemPsp.referenciaInterna());

            if (pagoLocal == null) {
                incidencias.add(new IncidenciaConciliacion(
                        itemPsp.referenciaInterna(), "COBRO_EN_PSP_NO_EXISTE_EN_BD_LOCAL"
                ));
            } else if (pagoLocal.getMontoBruto().compareTo(itemPsp.montoBruto()) != 0) {
                incidencias.add(new IncidenciaConciliacion(
                        itemPsp.referenciaInterna(),
                        "DIFERENCIA_MONTO: Local=" + pagoLocal.getMontoBruto() + " vs PSP=" + itemPsp.montoBruto()
                ));
            }
        }

        // 2. Revisar pagos que nuestra BD cree que fueron cobrados pero no están en el reporte del PSP
        for (Pago pagoLocal : pagosEnNuestraBd) {
            if (pagoLocal.getEstado() == EstadoPago.CAPTURED &&
                    !referenciasVistasEnPsp.contains(pagoLocal.getReferenciaUnica())) {
                incidencias.add(new IncidenciaConciliacion(
                        pagoLocal.getReferenciaUnica(), "FIGURA_COBRADO_EN_BD_PERO_NO_EXISTE_EN_PSP"
                ));
            }
        }

        return incidencias;
    }
}
```

---

## 15. Seguridad en Pagos: PCI-DSS, Tokenización, 3DS y Antifraude

Cuando manejas pagos con tarjeta, rige la norma de seguridad internacional *
*PCI-DSS (*Payment Card Industry Data Security Standard*)**:

| Concepto de Seguridad                         | Qué es y Qué debes hacer como Desarrollador                                                                                                                                                                                                                                                                                         | Ejemplo                                                                                                                  |
|-----------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------|
| **Tokenización (Cómo no tocar datos PCI)**    | **Nunca envíes los 16 números de la tarjeta ni el código CVV a tu propio backend en Spring Boot.** El frontend usa el SDK JS de la pasarela (Stripe Elements / MercadoPago.js) para enviar la tarjeta directo a la pasarela, recibe un `cardToken` de un solo uso (ej. `"tok_1N3a..."`) y te envía **solo ese token** a tu backend. | Tu DTO en Spring Boot recibe `String paymentMethodToken`, jamás `String numeroTarjeta` ni `String cvv`.                  |
| **Prohibición absoluta de almacenar CVV/CVC** | Ni siquiera las empresas certificadas PCI Nivel 1 tienen permitido guardar el código de seguridad de 3 o 4 dígitos del dorso de la tarjeta después de autorizar el pago.                                                                                                                                                            | Jamás crear una columna `cvv` en la base de datos ni imprimir el request en los logs.                                    |
| **3D Secure (3DS 2.0)**                       | Protocolo de autenticación adicional donde el banco le pide un segundo factor al usuario en compras sospechosas. Produce el ***Liability Shift*** (Cambio de Responsabilidad): si un pago pasa por 3DS y resulta ser fraude, **el costo lo absorbe el banco emisor y no tu empresa**.                                               | Estado `REQUIRES_ACTION` devolviendo la `redirectUrl` del desafío 3DS al frontend.                                       |
| **Motor Antifraude y *Velocity Checks***      | Reglas previas para frenar ataques de *Carding* (bots que prueban miles de tarjetas robadas en tu web para ver cuáles pasan).                                                                                                                                                                                                       | Bloquear si una misma IP o usuario intenta probar más de 3 tarjetas distintas en 10 minutos (*Rate Limiting* con Redis). |

**Ejemplo de Control Antifraude (*Velocity Check*) antes de llamar a la Pasarela:**

```java

@Service
public class ControlAntifraudePrevioService {

    private final PagoRepository pagoRepository;

    public ControlAntifraudePrevioService(PagoRepository pagoRepository) {
        this.pagoRepository = pagoRepository;
    }

    public void validarRiesgoOperacion(Long usuarioId, String ipCliente, BigDecimal monto) {
        Instant haceDiezMinutos = Instant.now().minus(Duration.ofMinutes(10));

        // Velocity Check: Si el usuario tuvo 3 o más rechazos en los últimos 10 minutos, bloqueamos (evita Card Testing)
        long rechazosRecientes = pagoRepository.countByUsuarioIdAndEstadoAndFechaCreacionAfter(
                usuarioId, EstadoPago.REJECTED, haceDiezMinutos
        );
        if (rechazosRecientes >= 3) {
            throw new OperacionSospechosaBloqueadaException(
                    "Demasiados intentos de pago fallidos recientes. Por seguridad, aguarde 15 minutos."
            );
        }
    }
}
```

---

## 16. Arquitectura Multi-Pasarela (*Payment Orchestrator* con Patrón Strategy/Adapter)

Las empresas medianas y grandes nunca dependen de una única pasarela de pagos:

- Usan **Mercado Pago** en Argentina, **Stripe** en EE.UU./Europa y **Pix/PagSeguro** en Brasil.
- O si **Pasarela A** se cae un domingo de Día de la Madre, desvían automáticamente el tráfico a **Pasarela B** (
  *Failover Routing*).

Para lograr esto sin llenar tu código de `if ("STRIPE".equals(pasarela))`, aplicamos **SOLID (OCP + DIP)** con un *
*Orquestador de Pagos**:

```java
// 1. Contrato Único de nuestro Dominio (Puerto de Salida)
public interface PasarelaPagoProvider {
    NombrePasarela getNombrePasarela();

    RespuestaCobro procesarCobro(OrdenCobro orden);
}

// 2. Adaptador para Stripe
@Component
public class StripePagoAdapter implements PasarelaPagoProvider {
    @Override
    public NombrePasarela getNombrePasarela() {
        return NombrePasarela.STRIPE;
    }

    @Override
    public RespuestaCobro procesarCobro(OrdenCobro orden) {
        // Traduce OrdenCobro al SDK de Stripe (en centavos) y devuelve RespuestaCobro estándar
        return new RespuestaCobro(true, "ch_3N9x...", NombrePasarela.STRIPE);
    }
}

// 3. Adaptador para Mercado Pago
@Component
public class MercadoPagoAdapter implements PasarelaPagoProvider {
    @Override
    public NombrePasarela getNombrePasarela() {
        return NombrePasarela.MERCADO_PAGO;
    }

    @Override
    public RespuestaCobro procesarCobro(OrdenCobro orden) {
        // Traduce OrdenCobro a la API de Mercado Pago y devuelve RespuestaCobro estándar
        return new RespuestaCobro(true, "mp_99812...", NombrePasarela.MERCADO_PAGO);
    }
}

// 4. Orquestador de Pagos con Failover Automático (Si la pasarela primaria se cae, cobra por la secundaria)
@Service
public class OrquestadorPagosService {

    private final Map<NombrePasarela, PasarelaPagoProvider> proveedores;

    public OrquestadorPagosService(List<PasarelaPagoProvider> listaProveedores) {
        this.proveedores = listaProveedores.stream()
                .collect(Collectors.toMap(PasarelaPagoProvider::getNombrePasarela, Function.identity()));
    }

    public RespuestaCobro cobrarConRespaldo(OrdenCobro orden,
                                            NombrePasarela pasarelaPrimaria,
                                            NombrePasarela pasarelaSecundaria) {
        try {
            return proveedores.get(pasarelaPrimaria).procesarCobro(orden);
        } catch (PasarelaCaidaException e) {
            // FAILOVER: Si la pasarela principal sufre una caída técnica (5xx/Timeout),
            // redirigimos automáticamente el cobro a la pasarela de respaldo sin perder la venta
            return proveedores.get(pasarelaSecundaria).procesarCobro(orden);
        }
    }
}
```

---

## 17. Checklist de Producción para Sistemas de Pagos

Antes de salir a producción con cualquier módulo que toque dinero real, verifica que cumples estos **12 puntos**:

| #      | Punto de Verificación                                            | ¿Cómo se implementa en Java / Spring Boot?                                                                                   |
|--------|------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------|
| **1**  | **Cero `double` / `float` en todo el proyecto**                  | Todos los montos usan `BigDecimal` con `RoundingMode.HALF_EVEN` y se comparan con `.compareTo() == 0`.                       |
| **2**  | **Idempotencia en todos los endpoints de cobro y transferencia** | Header `Idempotency-Key` validado con índice `UNIQUE` en base de datos y reenviado a la pasarela externa.                    |
| **3**  | **Bloqueo de fila al tocar saldos o estados de pago**            | Uso de `@Lock(LockModeType.PESSIMISTIC_WRITE)` (`SELECT FOR UPDATE`) ordenando IDs de menor a mayor para evitar *Deadlocks*. |
| **4**  | **Máquina de estados inquebrantable**                            | Un pago en estado terminal (`REFUNDED`, `VOIDED`, `REJECTED`) jamás puede volver a cambiar de estado.                        |
| **5**  | **Control de tope en reembolsos parciales**                      | Validación estricta: `sumaReembolsosPrevios + nuevoReembolso <= montoCapturado`.                                             |
| **6**  | **Webhooks verificados criptográficamente**                      | Validación de firma `HMAC-SHA256` sobre el body crudo e idempotencia por `event_id`.                                         |
| **7**  | **Manejo de estado `UNKNOWN / PENDING_CONFIRMATION`**            | Si hay *Read Timeout* al llamar a la pasarela, jamás asumir rechazo inmediato; consultar con una sonda antes de reintentar.  |
| **8**  | **Libro Mayor de Partida Doble (*Double-Entry Ledger*)**         | Todo movimiento de saldo deja asientos contables inmutables (`Append-Only`) donde `SUM(débitos + créditos) == 0`.            |
| **9**  | **Cumplimiento PCI-DSS (Tokenización)**                          | El backend solo recibe tokens (`cardToken`), nunca números de tarjeta completos ni códigos CVV.                              |
| **10** | **Enmascaramiento en Logs**                                      | Jamás imprimir tokens de pago, CBU completos ni datos sensibles en los archivos de log.                                      |
| **11** | **Distinción entre *Soft Decline* y *Hard Decline***             | En cobros recurrentes, nunca reintentar tarjetas rechazadas por robo, pérdida o cuenta inválida.                             |
| **12** | **Job de Conciliación Diaria (*Reconciliation*)**                | Proceso automático que cruza cada noche los pagos `CAPTURED` en tu BD contra el reporte de liquidación del PSP/Banco.        |

> **Tip de lectura:** Cuando revises el código de cualquier pasarela, billetera o módulo de cobros, busca primero **cómo
maneja los Timeouts de red al cobrar**, **cómo procesa los Webhooks** y **cómo actualiza los saldos en la base de datos
**. Si ves `BigDecimal`, `Idempotency-Key`, validación de firma HMAC, máquina de estados y asientos contables en
> bloqueos `FOR UPDATE`, estás frente a una arquitectura de pagos de nivel bancario.

