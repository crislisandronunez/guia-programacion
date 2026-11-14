# Guía y Glosario de Resolución de Problemas de Empresas con Sistemas

Este documento reúne la metodología, el vocabulario, los patrones de arquitectura y los ejemplos prácticos (en **Java y
Spring Boot**) necesarios para **traducir un problema real de una empresa en una solución de software robusta, escalable
y mantenible**.

---

## Índice

1. [Mentalidad de Ingeniería: Del Problema de Negocio al Sistema](#1-mentalidad-de-ingeniería-del-problema-de-negocio-al-sistema)
2. [Glosario de Levantamiento y Análisis de Requerimientos](#2-glosario-de-levantamiento-y-análisis-de-requerimientos)
3. [Metodología Paso a Paso para Resolver un Problema Empresarial](#3-metodología-paso-a-paso-para-resolver-un-problema-empresarial)
4. [Modelado del Dominio (Domain-Driven Design - DDD Práctico)](#4-modelado-del-dominio-domain-driven-design---ddd-práctico)
5. [Catálogo de Problemas Clásicos de Empresas y su Solución Técnica](#5-catálogo-de-problemas-clásicos-de-empresas-y-su-solución-técnica)
6. [Problema 1: Doble Cobro y Condiciones de Carrera (Concurrencia e Idempotencia)](#6-problema-1-doble-cobro-y-condiciones-de-carrera-concurrencia-e-idempotencia)
7. [Problema 2: Procesos Lentos que Bloquean al Usuario (Asincronía y Colas)](#7-problema-2-procesos-lentos-que-bloquean-al-usuario-asincronía-y-colas)
8. [Problema 3: Caída de Proveedores Externos (Resiliencia, Reintentos y Circuit Breaker)](#8-problema-3-caída-de-proveedores-externos-resiliencia-reintentos-y-circuit-breaker)
9. [Problema 4: Cuellos de Botella en Lecturas y Reportes Pesados (Caché, Paginación y CQRS)](#9-problema-4-cuellos-de-botella-en-lecturas-y-reportes-pesados-caché-paginación-y-cqrs)
10. [Problema 5: Tareas Programadas, Cierres de Lote y Conciliación (Batch & Scheduling)](#10-problema-5-tareas-programadas-cierres-de-lote-y-conciliación-batch--scheduling)
11. [Problema 6: "¿Quién cambió este dato?" (Auditoría, Trazabilidad y Máquina de Estados)](#11-problema-6-quién-cambió-este-dato-auditoría-trazabilidad-y-máquina-de-estados)
12. [Problema 7: Datos Inconsistentes entre Sistemas (Transacciones, Outbox Pattern y Sagas)](#12-problema-7-datos-inconsistentes-entre-sistemas-transacciones-outbox-pattern-y-sagas)
13. [Problema 8: Modernización de Sistemas Viejos / Excel (Integración y Strangler Fig)](#13-problema-8-modernización-de-sistemas-viejos--excel-integración-y-strangler-fig)
14. [Diagnóstico y Resolución de Incidentes en Producción (Troubleshooting)](#14-diagnóstico-y-resolución-de-incidentes-en-producción-troubleshooting)
15. [Trade-offs: Cómo Elegir la Arquitectura Correcta sin Sobre-Ingeniería](#15-trade-offs-cómo-elegir-la-arquitectura-correcta-sin-sobre-ingeniería)
16. [Caso Completo de Punta a Punta: De Planilla Excel a Sistema Empresarial](#16-caso-completo-de-punta-a-punta-de-planilla-excel-a-sistema-empresarial)
17. [Checklist Rápido para Analizar Cualquier Problema de Negocio](#17-checklist-rápido-para-analizar-cualquier-problema-de-negocio)

---

## 1. Mentalidad de Ingeniería: Del Problema de Negocio al Sistema

Las empresas **no compran ni construyen software por tener código**; construyen sistemas por **cuatro motivos
fundamentales**:

| Objetivo de Negocio                                    | Qué significa en la realidad de la empresa                                                     | Cómo lo resuelve un sistema                                                                   | Ejemplo Real                                                                                                  |
|--------------------------------------------------------|------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------|
| **1. Aumentar Ingresos (*Revenue*)**                   | Vender más, llegar a más clientes o cobrar más rápido sin depender de horarios de oficina.     | Autoservicio 24/7, carritos de compra rápidos, pasarelas de pago automatizadas.               | Pasar de tomar pedidos por teléfono de 9 a 18 hs a una API de e-commerce que vende a las 3 AM.                |
| **2. Reducir Costos Operativos (*Efficiency*)**        | Eliminar tareas manuales repetitivas donde empleados copian y pegan datos entre planillas.     | Automatización de flujos (*workflows*), integraciones entre APIs, procesos *batch*.           | Un proceso automático que concilia 10.000 pagos bancarios en 5 segundos en vez de 3 personas tardando 2 días. |
| **3. Reducir Riesgos y Errores (*Risk & Compliance*)** | Evitar pérdidas de dinero por errores humanos, fraudes, multas legales o fugas de información. | Validaciones estrictas, transacciones ACID, control de permisos (RBAC) y pistas de auditoría. | Impedir por sistema que se despache mercadería si la factura no figura pagada o aprobada por Créditos.        |
| **4. Tomar Mejores Decisiones (*Visibility*)**         | Saber en tiempo real cuánto stock hay, cuánto se vendió hoy o dónde se traban las entregas.    | Dashboards operativos, métricas en tiempo real, alertas automáticas de desvíos.               | Alertar por Slack/Email cuando el stock de un insumo crítico baja del mínimo de seguridad.                    |

**Ejemplo de cómo traducir el pedido de un cliente a diseño técnico:**

```text
🗣️ Lo que dice el dueño de la empresa:
"Los vendedores venden por WhatsApp productos que ya no tenemos en el galpón y después tenemos que devolver la plata."

🧠 Lo que analiza el ingeniero de sistemas:
1. Causa raíz: No hay una única fuente de verdad (Single Source of Truth) del stock ni reserva atómica al iniciar la venta.
2. Solución de sistema:
   - Base de datos centralizada con bloqueo optimista (@Version) o descuento atómico.
   - Estado de "Stock Reservado Temporalmente" con expiración automática (TTL de 15 min) mientras el cliente paga.
```

---

## 2. Glosario de Levantamiento y Análisis de Requerimientos

Cuando hablas con gerentes, analistas funcionales o líderes técnicos para entender un problema, aparecen estos términos
clave:

| Término                                                      | Significado                                                                                                           | Ejemplo Práctico                                                                                                            |
|--------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------|
| **Dolor del Negocio (*Pain Point*)**                         | El problema real que le cuesta tiempo, dinero o clientes a la empresa hoy.                                            | *"Tardamos 4 días a fin de mes en calcular las comisiones de los vendedores a mano en Excel"*.                              |
| **Causa Raíz (*Root Cause*)**                                | El origen profundo del problema (diferente del síntoma superficial). Se descubre con la técnica de los **5 Porqués**. | Síntoma: *"El servidor se cae los lunes"*. Causa raíz: *"El reporte semanal carga 2 millones de filas en RAM sin paginar"*. |
| **Stakeholder (Interesado)**                                 | Cualquier persona afectada por el sistema (dueño, operador de depósito, contador, cliente final, auditor).            | El operador del depósito necesita que la pantalla funcione con lector de código de barras sin usar el mouse.                |
| **Requerimiento Funcional (RF)**                             | **Qué** debe hacer el sistema (reglas de negocio, cálculos, pantallas, validaciones).                                 | *"Si un cliente supera los $500.000 de deuda vencida, el sistema debe bloquear nuevos pedidos"*.                            |
| **Requerimiento No Funcional (RNF)**                         | **Cómo** debe comportarse el sistema en calidad (rendimiento, seguridad, disponibilidad, escalabilidad).              | *"El endpoint de cobro debe responder en menos de 300ms y soportar 500 peticiones por segundo"*.                            |
| **Regla de Negocio (*Business Rule*)**                       | Condición o política propia de la empresa que el software debe hacer cumplir siempre.                                 | *"Los pedidos mayoristas tienen 10% de descuento solo si incluyen más de 20 unidades del mismo SKU"*.                       |
| **Caso Borde (*Edge Case*)**                                 | Situación poco frecuente pero posible que rompe el sistema si no se contempla.                                        | ¿Qué pasa si el cliente hace doble clic rápido en "Pagar"? ¿Qué pasa si pide reembolso parcial en año bisiesto?             |
| **Camino Feliz (*Happy Path*)**                              | El flujo ideal donde todo sale bien (el usuario tiene saldo, la BD responde, el stock alcanza).                       | Usuario elige producto -> paga -> tarjeta aprueba -> se descuenta stock -> recibe email.                                    |
| **Fuente Única de Verdad (*Single Source of Truth - SSOT*)** | Definir qué sistema o base de datos manda sobre un dato específico para evitar contradicciones.                       | *"El precio oficial de los productos vive en el ERP; la web solo lo consulta y cachea, nunca lo inventa"*.                  |
| **MVP (*Minimum Viable Product*)**                           | La versión más pequeña del sistema que ya resuelve el dolor principal y entrega valor real en producción.             | Primero lanzar el cálculo automático de comisiones y exportación CSV; dejar los gráficos 3D para la fase 2.                 |
| **SLA / SLO (*Service Level Agreement / Objective*)**        | Compromiso de disponibilidad o tiempo de respuesta del sistema (ej. 99.9% del tiempo arriba).                         | *"Las transferencias inmediatas deben procesarse en < 5 segundos el 99% de las veces"*.                                     |

**Ejemplo de transformación de Requerimiento en Código (Regla de Negocio + Edge Cases):**

```java
// Requerimiento Funcional: "Aprobar crédito automático si el monto no supera 3 sueldos del cliente y no tiene deudas".
// Edge Cases considerados: monto negativo/cero, sueldo nulo, cliente inactivo.
@Service
public class EvaluadorCreditoService {

  private static final int MULTIPLICADOR_MAXIMO_SUELDO = 3;

  public ResultadoEvaluacion evaluarSolicitud(Cliente cliente, BigDecimal montoSolicitado) {
    // 1. Validación de Casos Borde (Fail-Fast)
    if (montoSolicitado == null || montoSolicitado.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("El monto solicitado debe ser mayor a cero");
    }
    if (!cliente.isActivo() || cliente.getSueldoMensualDeclarado() == null) {
      return ResultadoEvaluacion.rechazado("El cliente no está activo o no registra ingresos");
    }

    // 2. Regla de Negocio 1: Sin mora vigente
    if (cliente.tieneDeudaVencida()) {
      return ResultadoEvaluacion.rechazado("El cliente posee deuda vencida impaga");
    }

    // 3. Regla de Negocio 2: Tope de 3 sueldos
    BigDecimal limiteMaximo = cliente.getSueldoMensualDeclarado()
        .multiply(BigDecimal.valueOf(MULTIPLICADOR_MAXIMO_SUELDO));

    if (montoSolicitado.compareTo(limiteMaximo) > 0) {
      return ResultadoEvaluacion.rechazado("El monto supera el límite de 3 sueldos ($" + limiteMaximo + ")");
    }

    // 4. Happy Path
    return ResultadoEvaluacion.aprobado(montoSolicitado);
  }
}
```

---

## 3. Metodología Paso a Paso para Resolver un Problema Empresarial

Nunca empieces escribiendo código ni creando tablas en la base de datos sin seguir estos **6 pasos**:

| Paso                                                         | Pregunta Clave                                                                     | Qué se produce                                                | Ejemplo Concreto                                                                                                         |
|--------------------------------------------------------------|------------------------------------------------------------------------------------|---------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------|
| **1. Entender el Dolor y la Causa Raíz**                     | *¿Qué problema estamos resolviendo y cuánto le cuesta hoy a la empresa?*           | Definición del problema (Técnica de los *5 Porqués*).         | Se pierden 15 ventas diarias porque la aprobación manual de transferencias demora 6 horas.                               |
| **2. Mapear el Flujo Actual (*As-Is*) vs Deseado (*To-Be*)** | *¿Cómo viaja la información hoy paso a paso y dónde se traba?*                     | Diagrama de flujo o secuencia de punta a punta.               | *As-Is*: Cliente manda foto de comprobante por WhatsApp. *To-Be*: Webhook del banco notifica a nuestra API en 1 segundo. |
| **3. Definir el Modelo de Datos y Estados**                  | *¿Cuáles son las entidades centrales y por qué estados pasan?*                     | Entidades, relaciones y máquina de estados.                   | Entidad `OrdenPago` con estados: `PENDIENTE -> APROBADA / RECHAZADA / EXPIRADA`.                                         |
| **4. Identificar Integraciones y Fallos (*Unhappy Paths*)**  | *¿Qué pasa si la API externa falla, el archivo viene corrupto o hay concurrencia?* | Estrategia de errores, reintentos, idempotencia y alertas.    | Si el webhook llega dos veces con el mismo `idTransaccionBanco`, ignorar el duplicado.                                   |
| **5. Diseñar Contratos (APIs y Eventos)**                    | *¿Qué endpoints, DTOs y eventos necesitamos?*                                      | Especificación de endpoints REST, payloads JSON y colas.      | `POST /api/v1/webhooks/pagos` con validación de firma HMAC.                                                              |
| **6. Implementar, Medir y Monitorear**                       | *¿Cómo sabemos en producción que el problema realmente se resolvió?*               | Código + Tests + Logs con trazabilidad + Métricas de negocio. | Métrica Prometheus: `pagos_aprobados_automaticamente_total` y tiempo medio de acreditación.                              |

**Diagrama del flujo de análisis:**

```text
[1. Dolor del Negocio] ➔ [2. Flujo Actual vs Futuro] ➔ [3. Modelo de Datos y Estados]
                                                                 │
[6. Monitoreo en Prod] ⬅ [5. Código + Tests + Logs] ⬅ [4. Casos Borde y Fallos]
```

---

## 4. Modelado del Dominio (Domain-Driven Design - DDD Práctico)

Para resolver problemas complejos de empresas, necesitas que tu código hable **el mismo idioma que los expertos del
negocio**. Eso es la base de **DDD (Diseño Guiado por el Dominio)**:

| Concepto DDD                                  | Significado Práctico                                                                                                                                                                   | Ejemplo en una Empresa de Logística                                                                                                                           |
|-----------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Lenguaje Ubicuo (*Ubiquitous Language*)**   | Usar exactamente las mismas palabras que usa la empresa tanto en las reuniones como en los nombres de clases y métodos (nunca traducir ni inventar nombres genéricos como `DataItem`). | Si en la empresa le llaman `HojaDeRuta` y `RemitoConformado`, tus clases se llaman `HojaDeRuta` y `RemitoConformado` (no `TableData1`).                       |
| **Contexto Delimitado (*Bounded Context*)**   | La misma palabra significa cosas distintas según el área de la empresa. Cada área debe tener su propio modelo separado.                                                                | Para **Ventas**, un `Producto` tiene precio, fotos y descripción comercial. Para **Depósito**, un `Producto` tiene peso, volumen en cm³ y pasillo/estantería. |
| **Entidad (*Entity*)**                        | Objeto que tiene identidad única (`id`) y ciclo de vida a lo largo del tiempo, aunque cambien sus atributos.                                                                           | `Cliente`, `Pedido`, `Factura`, `Camion`.                                                                                                                     |
| **Objeto de Valor (*Value Object*)**          | Objeto que se define solo por sus valores, no tiene ID propio y es **inmutable** (en Java se modela idealmente con `record`).                                                          | `Dinero(BigDecimal monto, Moneda moneda)`, `Cuit(String numero)`, `RangoFechas(LocalDate desde, LocalDate hasta)`.                                            |
| **Agregado (*Aggregate* y *Aggregate Root*)** | Grupo de entidades que se tratan como una sola unidad para mantener la consistencia de los datos. Solo se modifica a través de la entidad raíz (*Root*).                               | `Pedido` (Raíz) + `List<LineaPedido>`. Nadie modifica una `LineaPedido` suelta sin pasar por `pedido.modificarCantidad(...)`.                                 |
| **Evento de Dominio (*Domain Event*)**        | Algo importante que **ya ocurrió** en el negocio (siempre se nombra en pasado) y ante lo cual otras áreas deben reaccionar.                                                            | `PedidoPagadoEvent`, `StockAgotadoEvent`, `FacturaVencidaEvent`.                                                                                              |

**Ejemplo de Value Object (`record`) + Aggregate Root protegiendo reglas de negocio:**

```java
// 1. Value Object inmutable con su propia validación: nunca existirá un Dinero inválido en memoria
public record Dinero(BigDecimal monto, String moneda) {
  public Dinero {
    Objects.requireNonNull(monto, "El monto es obligatorio");
    Objects.requireNonNull(moneda, "La moneda es obligatoria");
    if (monto.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("El monto no puede ser negativo: " + monto);
    }
  }

  public Dinero sumar(Dinero otro) {
    if (!this.moneda.equals(otro.moneda)) {
      throw new IllegalArgumentException("No se pueden sumar monedas distintas: " + this.moneda + " y " + otro.moneda);
    }
    return new Dinero(this.monto.add(otro.monto), this.moneda);
  }
}

// 2. Aggregate Root (OrdenDeCompra): Protege las invariantes del negocio
public class OrdenDeCompra {
  private Long id;
  private EstadoOrden estado = EstadoOrden.BORRADOR;
  private final List<ItemOrden> items = new ArrayList<>();
  private Dinero total = new Dinero(BigDecimal.ZERO, "ARS");

  public void agregarItem(String sku, Dinero precioUnitario, int cantidad) {
    if (this.estado != EstadoOrden.BORRADOR) {
      throw new IllegalStateException("No se pueden agregar ítems a una orden que ya fue emitida");
    }
    ItemOrden nuevoItem = new ItemOrden(sku, precioUnitario, cantidad);
    this.items.add(nuevoItem);
    this.total = this.total.sumar(nuevoItem.calcularSubtotal());
  }
}
```

---

## 5. Catálogo de Problemas Clásicos de Empresas y su Solución Técnica

El 90% de los problemas en sistemas empresariales caen en uno de estos **8 patrones universales**. Usa esta tabla como
mapa de referencia:

| #     | Problema Real en la Empresa                                                                                          | Qué está pasando técnicamente                                                                            | Patrón / Herramienta de Solución                                                                              |
|-------|----------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------|
| **1** | *"A un cliente se le cobró dos veces o se vendió el último producto a dos personas a la vez"*.                       | Condición de carrera (*Race Condition*) y falta de idempotencia.                                         | **Clave de Idempotencia (`Idempotency-Key`) + Bloqueo Optimista (`@Version`) o Pesimista (`FOR UPDATE`)**.    |
| **2** | *"La pantalla se queda cargando 25 segundos al confirmar la compra y da Timeout"*.                                   | Trabajo pesado (PDF, emails, AFIP/impuestos) ejecutándose en el mismo hilo HTTP del usuario.             | **Procesamiento Asíncrono (`@Async`, Eventos de Spring o Colas con RabbitMQ / Kafka)**.                       |
| **3** | *"Cuando el servicio del banco o correo anda lento, toda nuestra plataforma se cuelga"*.                             | Fallo en cascada por falta de *Timeouts* y saturación de hilos esperando respuesta externa.              | **Timeouts estrictos + Reintentos con Backoff + Circuit Breaker (Resilience4j)**.                             |
| **4** | *"El sistema anda bien a la mañana pero al mediodía cuando todos sacan reportes se congela la BD"*.                  | Consultas pesadas sin índice, problema `N+1`, falta de paginación o lecturas compitiendo con escrituras. | **Índices SQL + Paginación (`Pageable`) + Caché (Redis) + Réplica de lectura / CQRS**.                        |
| **5** | *"Todos los días a las 00:00 hs hay que vencer facturas, cobrar suscripciones y cruzar pagos con el banco"*.         | Necesidad de procesamiento por lotes (*Batch*) confiable y auditable.                                    | **Tareas programadas (`@Scheduled` + ShedLock) o Spring Batch por bloques (*Chunks*)**.                       |
| **6** | *"Apareció un pedido cancelado o un precio cambiado a $1 y nadie sabe quién fue ni cuándo"*.                         | Falta de trazabilidad, borrado físico (`DELETE`) y cambios de estado sin control.                        | **Borrado Lógico (*Soft Delete*) + Auditoría (`@EntityListeners`) + Máquina de Estados**.                     |
| **7** | *"Se guardó el pedido en la BD pero falló el envío del evento a Logística y el paquete nunca salió"*.                | Problema de doble escritura (*Dual Write*) entre base de datos y sistema de mensajería.                  | **Transactional Outbox Pattern** (guardar el evento en tabla en la misma transacción SQL y publicarlo luego). |
| **8** | *"Toda la empresa depende de un sistema viejo de hace 15 años o de 40 planillas Excel que nadie se anima a apagar"*. | Sistema legado crítico sin tests y alto riesgo de migración "Big Bang".                                  | **Patrón Strangler Fig (Estrangulador) + Capa Anticorrupción (ACL) + Importadores validados**.                |

---

## 6. Problema 1: Doble Cobro y Condiciones de Carrera (Concurrencia e Idempotencia)

### El Problema de Negocio

Un cliente aprieta el botón "Pagar" en el celular, el ascensor pierde señal, vuelve a apretar "Pagar" y se le descuenta
dos veces el dinero. O quedan 1 unidad en stock y dos usuarios compran en el mismo milisegundo.

### Conceptos Clave de la Solución

| Término                                     | Significado                                                                                                                                                                        | Ejemplo en Código / BD                                                                                                      |
|---------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------|
| **Idempotencia**                            | Propiedad por la cual ejecutar una misma operación 1 vez o 10 veces seguidas produce exactamente el mismo resultado, sin duplicar efectos secundarios.                             | Enviar un header `Idempotency-Key: uuid-unico` desde el frontend y guardarlo con restricción `UNIQUE` en la tabla de pagos. |
| **Condición de Carrera (*Race Condition*)** | Dos hilos leen el mismo dato al mismo tiempo (`stock = 1`), ambos creen que alcanza, y ambos restan 1 dejando el stock inconsistente.                                              | Hilo A lee `saldo = 100`, Hilo B lee `saldo = 100`. Ambos descuentan 80.                                                    |
| **Bloqueo Optimista (`@Version`)**          | No bloquea la fila al leer, pero al hacer `UPDATE` verifica que la columna `version` no haya cambiado. Si otro hilo ganó de mano, lanza `ObjectOptimisticLockingFailureException`. | `@Version private Long version;` en la entidad JPA. Ideal cuando las colisiones son ocasionales.                            |
| **Bloqueo Pesimista (`PESSIMISTIC_WRITE`)** | Ejecuta `SELECT ... FOR UPDATE` en SQL, bloqueando la fila en la base de datos hasta que termine la transacción actual. Los demás hilos esperan en fila.                           | `@Lock(LockModeType.PESSIMISTIC_WRITE)` en el repositorio. Ideal para saldos bancarios o billeteras de alta contención.     |

### Ejemplo en Spring Boot (Idempotencia + Bloqueo de Fila)

```java

@Service
public class CobroSeguroService {

  private final PagoRepository pagoRepository;
  private final CuentaRepository cuentaRepository;

  public CobroSeguroService(PagoRepository pagoRepository, CuentaRepository cuentaRepository) {
    this.pagoRepository = pagoRepository;
    this.cuentaRepository = cuentaRepository;
  }

  @Transactional
  public RespuestaPagoDto procesarCobroIdempotente(String idempotencyKey, Long cuentaId, BigDecimal monto) {
    // 1. IDEMPOTENCIA: Si ya procesamos esta clave antes, devolvemos el resultado anterior sin volver a cobrar
    Optional<Pago> pagoExistente = pagoRepository.findByIdempotencyKey(idempotencyKey);
    if (pagoExistente.isPresent()) {
      return RespuestaPagoDto.desde(pagoExistente.get(), true); // true = respuesta recuperada sin recobrar
    }

    // 2. BLOQUEO PESIMISTA (SELECT ... FOR UPDATE): Nadie más puede modificar el saldo de esta cuenta hasta el commit
    Cuenta cuenta = cuentaRepository.findByIdConBloqueo(cuentaId)
        .orElseThrow(() -> new EntidadNoEncontradaException("Cuenta no encontrada"));

    // 3. Regla de negocio protegida dentro de la entidad
    cuenta.debitar(monto);

    // 4. Guardamos el pago con su idempotencyKey (la columna tiene índice UNIQUE en la BD por seguridad extra)
    Pago nuevoPago = new Pago(idempotencyKey, cuentaId, monto, EstadoPago.APROBADO);
    pagoRepository.save(nuevoPago);

    return RespuestaPagoDto.desde(nuevoPago, false);
  }
}

public interface CuentaRepository extends JpaRepository<Cuenta, Long> {
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT c FROM Cuenta c WHERE c.id = :id")
  Optional<Cuenta> findByIdConBloqueo(@Param("id") Long id);
}
```

---

## 7. Problema 2: Procesos Lentos que Bloquean al Usuario (Asincronía y Colas)

### El Problema de Negocio

Cuando un usuario finaliza una compra, el sistema: 1) Guarda el pedido, 2) Llama al ente fiscal para generar factura
electrónica, 3) Arma un PDF con código QR, 4) Envía un email por SMTP, 5) Notifica al sistema del depósito. Todo junto
tarda 18 segundos. El usuario cierra la página pensando que se colgó.

### La Regla de Oro

- **Síncrono (en el request HTTP)**: Solo lo estrictamente indispensable para confirmar la operación (validar, reservar
  stock y registrar el pedido en estado `RECIBIDO`). Debe tardar **menos de 300 ms**.
- **Asíncrono (en segundo plano)**: Todo lo demás (facturación electrónica, generación de PDF, envío de email/WhatsApp,
  analítica).

| Herramienta en Spring Boot                                         | Cuándo usarla                                                                                                                                                                      | Ejemplo                                                                                      |
|--------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------|
| **`@TransactionalEventListener(phase = AFTER_COMMIT)` + `@Async`** | Cuando quieres desacoplar acciones secundarias dentro del mismo servicio, asegurándote de que solo se disparen **después** de que la transacción en BD se haya guardado con éxito. | Enviar el correo de bienvenida solo si el `INSERT` del usuario hizo `COMMIT` real.           |
| **Message Broker (RabbitMQ / Kafka / AWS SQS)**                    | Cuando el trabajo secundario es crítico y **no puede perderse** aunque el servidor de la aplicación se reinicie a mitad de camino.                                                 | Procesar la emisión de la factura fiscal y el envío del paquete en microservicios separados. |

### Ejemplo en Spring Boot (Desacoplamiento con Eventos Post-Commit + `@Async`)

```java
// 1. El evento que representa el hecho de negocio ocurrido
public record PedidoConfirmadoEvent(Long pedidoId, String emailCliente, BigDecimal total) {
}

// 2. Servicio principal: Responde en 50 milisegundos al cliente
@Service
public class CheckoutRapidoService {

  private final PedidoRepository pedidoRepository;
  private final ApplicationEventPublisher eventPublisher;

  public CheckoutRapidoService(PedidoRepository pedidoRepository, ApplicationEventPublisher eventPublisher) {
    this.pedidoRepository = pedidoRepository;
    this.eventPublisher = eventPublisher;
  }

  @Transactional
  public Pedido confirmarCompra(Pedido nuevoPedido) {
    nuevoPedido.setEstado(EstadoPedido.CONFIRMADO);
    Pedido guardado = pedidoRepository.save(nuevoPedido);

    // Publicamos el evento en memoria (no bloquea; los listeners correrán tras el commit)
    eventPublisher.publishEvent(new PedidoConfirmadoEvent(
        guardado.getId(), guardado.getEmailCliente(), guardado.getTotal()
    ));

    return guardado; // El usuario ya recibe su 201 Created en pantalla inmediatamente
  }
}

// 3. Listener asíncrono: Corre en otro hilo SOLO después de que el pedido se guardó firme en la BD
@Component
public class PostVentaEventListener {

  private static final Logger log = LoggerFactory.getLogger(PostVentaEventListener.class);
  private final FacturacionElectronicaService facturacionService;
  private final EmailService emailService;

  public PostVentaEventListener(FacturacionElectronicaService facturacionService, EmailService emailService) {
    this.facturacionService = facturacionService;
    this.emailService = emailService;
  }

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void gestionarFacturacionYEnvioEmail(PedidoConfirmadoEvent event) {
    log.info("Iniciando proceso post-venta en segundo plano para pedido {}", event.pedidoId());
    byte[] pdfFactura = facturacionService.emitirFacturaLegal(event.pedidoId(), event.total());
    emailService.enviarConfirmacionConAdjunto(event.emailCliente(), pdfFactura);
  }
}
```

---

## 8. Problema 3: Caída de Proveedores Externos (Resiliencia, Reintentos y Circuit Breaker)

### El Problema de Negocio

Tu sistema consulta el costo de envío a la API de un correo privado o valida identidades contra un organismo estatal. Un
viernes a la tarde el servidor de ese proveedor externo empieza a tardar 60 segundos en responder. Todos los hilos de tu
Tomcat se quedan esperando esa respuesta y **toda tu empresa queda fuera de línea**, incluso las pantallas que no usan
ese proveedor.

### Patrones de Resiliencia (Resilience4j / Spring Cloud)

| Patrón                                                     | Qué problema evita                                                                  | Cómo funciona                                                                                                                              | Ejemplo                                                                             |
|------------------------------------------------------------|-------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------|
| **Timeout (Tiempo límite)**                                | Evita que un hilo espere eternamente a un servidor colgado.                         | Si la API externa no conecta en 2 seg o no responde en 3 seg, corta la llamada de inmediato.                                               | `connectTimeout(Duration.ofSeconds(2))`                                             |
| **Retry con Exponential Backoff**                          | Resuelve micro-cortes de red de 1 segundo sin molestar al usuario.                  | Si falla por error de red (503/Timeout), reintenta hasta 3 veces esperando cada vez más tiempo (ej. 500ms, 1s, 2s).                        | `@Retry(name = "correoApi", fallbackMethod = "...")`                                |
| **Circuit Breaker (Disyuntor)**                            | Evita bombardear a un proveedor caído y protege los recursos de tu propio servidor. | Si el 50% de las últimas llamadas fallaron, **abre el circuito**: durante 30 seg rechaza o desvía al instante sin siquiera llamar por red. | `@CircuitBreaker(name = "correoApi", fallbackMethod = "calcularCostoContingencia")` |
| **Fallback (Plan de Contingencia / Degradación Elegante)** | Evita perder una venta cuando falla un servicio no crítico.                         | Si la API de cotización de envío está caída, devuelve una tarifa plana estimada por código postal para no frenar la compra.                | Método `fallback` que consulta una tabla local de precios de emergencia.            |

### Ejemplo en Spring Boot con Resilience4j

```java

@Service
public class CotizadorEnvioResilienteService {

  private static final Logger log = LoggerFactory.getLogger(CotizadorEnvioResilienteService.class);
  private final CorreoExternoClient correoClient;
  private final TarifaContingenciaRepository contingenciaRepository;

  public CotizadorEnvioResilienteService(CorreoExternoClient correoClient,
                                         TarifaContingenciaRepository contingenciaRepository) {
    this.correoClient = correoClient;
    this.contingenciaRepository = contingenciaRepository;
  }

  // Si falla temporalmente reintenta; si el proveedor se cae del todo, abre el circuito y ejecuta el fallback
  @Retry(name = "apiCorreoExterno")
  @CircuitBreaker(name = "apiCorreoExterno", fallbackMethod = "cotizarConTablaDeContingencia")
  public BigDecimal cotizarEnvio(String codigoPostal, double pesoKg) {
    return correoClient.obtenerTarifaEnVivo(codigoPostal, pesoKg);
  }

  // PLAN B (Degradación Elegante): La empresa sigue vendiendo aunque el proveedor externo esté caído
  public BigDecimal cotizarConTablaDeContingencia(String codigoPostal, double pesoKg, Throwable causa) {
    log.warn("Proveedor de correo caído ({}). Usando tarifa de contingencia local para CP {}",
        causa.getMessage(), codigoPostal);
    return contingenciaRepository.obtenerTarifaPromedioPorZona(codigoPostal, pesoKg);
  }
}
```

---

## 9. Problema 4: Cuellos de Botella en Lecturas y Reportes Pesados (Caché, Paginación y CQRS)

### El Problema de Negocio

La empresa creció: antes tenía 500 pedidos en la base de datos y ahora tiene 8 millones. Cada vez que un supervisor abre
el listado de pedidos o descarga un reporte mensual, la CPU de la base de datos sube al 100% y los clientes no pueden
comprar.

### Soluciones Técnicas por Nivel de Escala

| Técnica                                              | Cuándo aplicarla                                                                                                                                        | Impacto                                                                                                                                                                     | Ejemplo                                                              |
|------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------|
| **1. Índices Compuestos en BD**                      | Siempre que filtres u ordenes por columnas (`WHERE estado = ? AND fecha >= ? ORDER BY fecha DESC`).                                                     | Pasa de tardar 12 segundos (*Full Table Scan*) a **2 milisegundos** (*Index Seek*).                                                                                         | `@Table(indexes = @Index(columnList = "estado, fecha_creacion"))`    |
| **2. Paginación Obligatoria (`Pageable` / `Slice`)** | Jamás hacer `findAll()` sin límite en tablas transaccionales. Si no necesitas el total de páginas (`COUNT(*)`), usa **`Slice<T>`** en vez de `Page<T>`. | Evita traer millones de objetos a la memoria RAM (evita `OutOfMemoryError`).                                                                                                | `Slice<PedidoResumenDto> findByEstado(Estado e, Pageable p);`        |
| **3. Proyecciones DTO directas**                     | Cuando muestras una tabla o exportas datos y solo necesitas 5 columnas de una entidad que tiene 40 columnas y 4 relaciones.                             | Reduce el tráfico de red entre BD y aplicación en un 80% y elimina el problema `N+1`.                                                                                       | `SELECT new com.empresa.PedidoFilaDto(p.id, p.total) FROM Pedido p`  |
| **4. Caché en Memoria (`@Cacheable` / Redis)**       | Datos que se leen miles de veces por minuto pero cambian poco (catálogo de productos, cotización del dólar, configuración de sucursales).               | Reduce la carga de la base de datos a cero para esas consultas.                                                                                                             | `@Cacheable(value = "cotizacionMoneda", key = "#moneda")`            |
| **5. CQRS / Réplica de Lectura**                     | Cuando los reportes analíticos complejos compiten con las ventas en vivo.                                                                               | Separar el modelo de **Escritura (Commands)** del modelo de **Lectura (Queries)** o apuntar los reportes a una réplica de solo lectura (`@Transactional(readOnly = true)`). | Tabla precalculada `resumen_ventas_diarias` actualizada por eventos. |

### Ejemplo en Spring Boot (Consulta Optimizada con `Slice` y Proyección vs Consulta Lenta)

```java
// ❌ MALO: Trae todas las columnas, ejecuta un COUNT(*) costosísimo en millones de filas y carga entidades completas
Page<Pedido> pedidos = pedidoRepository.findAll(PageRequest.of(0, 50));

// ✅ BUENO: Proyección a Record (solo trae 3 columnas de la BD) + Slice (no ejecuta COUNT(*) sobre los 8 millones de filas)
public record PedidoListadoDto(Long id, String nombreCliente, BigDecimal total, LocalDateTime fecha) {
}

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

  @Query("""
      SELECT new com.empresa.dto.PedidoListadoDto(p.id, c.nombre, p.total, p.fechaCreacion)
      FROM Pedido p
      JOIN p.cliente c
      WHERE p.estado = :estado
      ORDER BY p.fechaCreacion DESC
      """)
  Slice<PedidoListadoDto> buscarListadoRapidoPorEstado(@Param("estado") EstadoPedido estado, Pageable pageable);
}
```

---

## 10. Problema 5: Tareas Programadas, Cierres de Lote y Conciliación (Batch & Scheduling)

### El Problema de Negocio

En las empresas hay procesos que ocurren por reloj sin que ningún humano apriete un botón:

- A las 00:00 hs: Vencer presupuestos que llevan más de 7 días sin pagarse.
- A las 03:00 hs: Procesar el archivo de rendición del banco con 200.000 cobros por débito automático.
- Cada 5 minutos: Liberar el stock reservado de carritos abandonados.

### Herramientas y Cuidados Críticos

| Concepto / Herramienta                   | Qué resuelve                                                                                                                                                                                | Ejemplo                                                                                  |
|------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------|
| **`@Scheduled(cron = "...")`**           | Ejecuta un método automáticamente según una expresión Cron o intervalo fijo.                                                                                                                | `@Scheduled(cron = "0 0 3 * * *", zone = "America/Argentina/Buenos_Aires")`              |
| **ShedLock (`@SchedulerLock`)**          | **Crítico en producción:** Si tienes 3 instancias de tu microservicio corriendo en Kubernetes, evita que las 3 ejecuten el mismo Cron a las 03:00 hs y cobren 3 veces a todos los clientes. | `@SchedulerLock(name = "cierreLoteDiario", lockAtMostFor = "30m")`                       |
| **Procesamiento por Lotes (*Chunking*)** | Si tienes que procesar 100.000 registros en un job nocturno, **nunca** los proceses en una sola transacción gigante. Hazlo en lotes de a 500 o 1.000.                                       | Si falla el registro 99.999, no pierdes las 2 horas de trabajo de los 99.000 anteriores. |

### Ejemplo de Job Nocturno Seguro por Lotes en Spring Boot

```java

@Component
public class ExpiracionReservasJob {

  private static final Logger log = LoggerFactory.getLogger(ExpiracionReservasJob.class);
  private final ReservaStockRepository reservaRepository;
  private final LiberadorReservaService liberadorService;

  public ExpiracionReservasJob(ReservaStockRepository reservaRepository,
                               LiberadorReservaService liberadorService) {
    this.reservaRepository = reservaRepository;
    this.liberadorService = liberadorService;
  }

  // Corre cada 5 minutos.procesando de a lotes de 200 para no saturar la memoria ni bloquear la base de datos
  @Scheduled(fixedDelayString = "PT5M")
  public void liberarReservasVencidas() {
    Instant limiteVencimiento = Instant.now().minus(Duration.ofMinutes(15));
    Slice<Long> loteIds;
    int totalLiberadas = 0;

    do {
      // Buscamos solo los primeros 200 IDs vencidos
      loteIds = reservaRepository.buscarIdsVencidosAntesDe(limiteVencimiento, PageRequest.of(0, 200));
      for (Long reservaId : loteIds.getContent()) {
        try {
          // Cada reserva se libera en su propia transacción independiente y aislada
          liberadorService.liberarReservaIndividual(reservaId);
          totalLiberadas++;
        } catch (Exception e) {
          // Si 1 registro tiene un problema de datos, lo logueamos y seguimos con los demás sin abortar todo el lote
          log.error("Error al liberar reserva vencida ID {}: {}", reservaId, e.getMessage());
        }
      }
    } while (loteIds.hasNext());

    if (totalLiberadas > 0) {
      log.info("Job finalizado: se liberaron {} reservas de stock vencidas", totalLiberadas);
    }
  }
}
```

---

## 11. Problema 6: "¿Quién cambió este dato?" (Auditoría, Trazabilidad y Máquina de Estados)

### El Problema de Negocio

En un sistema empresarial, un pedido pasa de `ENTREGADO` de vuelta a `PENDIENTE`, o una deuda de $500.000 desaparece
porque alguien ejecutó un `DELETE`, o un operador modifica el CBU de pago de un proveedor y nadie sabe quién lo hizo ni
qué valor tenía antes.

### Las 3 Reglas de Oro de los Datos Empresariales

1. **En sistemas de negocio el dinero y los documentos comerciales NUNCA se borran con `DELETE` físico**: se usa *
   *Borrado Lógico (*Soft Delete*)** o se emite un **Contra-asiento / Nota de Crédito**.
2. **Los estados no se cambian con un simple `setEstado(cualquierCosa)`**: deben seguir una **Máquina de
   Estados (*State Machine*)** que valide qué transiciones están permitidas.
3. **Todo cambio sensible deja rastro de auditoría**: quién (`usuario`), cuándo (`timestamp`), desde dónde (`IP`) y qué
   cambió (`valorAnterior -> valorNuevo`).

### Ejemplo de Máquina de Estados en Java (Evitando estados ilegales)

```java
public enum EstadoFactura {
  BORRADOR {
    @Override
    public Set<EstadoFactura> siguientesEstadosPermitidos() {
      return EnumSet.of(EMITIDA, ANULADA);
    }
  },
  EMITIDA {
    @Override
    public Set<EstadoFactura> siguientesEstadosPermitidos() {
      return EnumSet.of(PAGADA, VENCIDA, ANULADA);
    }
  },
  VENCIDA {
    @Override
    public Set<EstadoFactura> siguientesEstadosPermitidos() {
      return EnumSet.of(PAGADA, ANULADA);
    }
  },
  PAGADA {
    @Override
    public Set<EstadoFactura> siguientesEstadosPermitidos() {
      // Una factura pagada es un estado final: no puede volver a BORRADOR ni EMITIDA
      return EnumSet.noneOf(EstadoFactura.class);
    }
  },
  ANULADA {
    @Override
    public Set<EstadoFactura> siguientesEstadosPermitidos() {
      return EnumSet.noneOf(EstadoFactura.class);
    }
  };

  public abstract Set<EstadoFactura> siguientesEstadosPermitidos();

  public void validarTransicionHacia(EstadoFactura nuevoEstado) {
    if (!siguientesEstadosPermitidos().contains(nuevoEstado)) {
      throw new TransicionEstadoInvalidaException(
          "Transición ilegal de factura: no se puede pasar de " + this + " a " + nuevoEstado
      );
    }
  }
}

// Uso dentro de la entidad Factura:
@Entity
@SQLDelete(sql = "UPDATE facturas SET eliminado = true WHERE id = ?") // Soft Delete automático en Hibernate
@SQLRestriction("eliminado = false")
public class Factura {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  private EstadoFactura estado = EstadoFactura.BORRADOR;

  private boolean eliminado = false;

  public void cambiarEstado(EstadoFactura nuevoEstado, String usuarioResponsable) {
    // 1. Verifica que la transición sea legal según las reglas de la empresa
    this.estado.validarTransicionHacia(nuevoEstado);
    this.estado = nuevoEstado;
  }
}
```

---

## 12. Problema 7: Datos Inconsistentes entre Sistemas (Transacciones, Outbox Pattern y Sagas)

### El Problema de Negocio (El peligro del *Dual Write*)

Tu servicio de Ventas guarda el pedido en PostgreSQL y en la línea siguiente envía un mensaje a RabbitMQ/Kafka o llama
por HTTP al microservicio de Depósito para que preparen la caja:

```java
// ❌ PELIGRO DE INCONSISTENCIA (Dual Write):
pedidoRepository.save(pedido); // 1. Se guarda en PostgreSQL
kafkaTemplate.

send("pedidos-nuevos",evento); // 2. ¿Qué pasa si justo aquí se corta la red con Kafka?
// Resultado: El pedido quedó cobrado y guardado en la BD, pero el mensaje jamás llegó a Depósito.
```

### Soluciones para Consistencia Entre Sistemas

| Patrón                                                             | Cómo funciona                                                                                                                                                                                                                                        | Cuándo usarlo                                                                                                    |
|--------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------|
| **Transactional Outbox Pattern (Bandeja de Salida Transaccional)** | En la **misma transacción SQL** donde guardas el `Pedido`, insertas también una fila en una tabla `outbox_eventos` de la misma base de datos. Un proceso en segundo plano lee `outbox_eventos`, lo envía a Kafka/RabbitMQ y lo marca como `ENVIADO`. | Siempre que necesites garantizar al 100% que si se guarda en BD se publique el evento (entrega *At-Least-Once*). |
| **Patrón Saga (Transacciones Compensatorias)**                     | En microservicios donde cada uno tiene su propia base de datos (Ventas, Pagos, Stock), si el paso 3 (Cobro) falla, se dispara un **evento de compensación** para deshacer el paso 2 (liberar el stock reservado) y cancelar el paso 1.               | Flujos de negocio distribuidos entre múltiples microservicios o APIs de terceros.                                |

### Ejemplo de Implementación del *Outbox Pattern* en Spring Boot

```java
// 1. En una única transacción ACID en PostgreSQL se guarda el Pedido y el registro en la tabla Outbox
@Service
public class CrearPedidoConOutboxService {

  private final PedidoRepository pedidoRepository;
  private final OutboxEventoRepository outboxRepository;
  private final ObjectMapper objectMapper;

  public CrearPedidoConOutboxService(PedidoRepository pedidoRepository,
                                     OutboxEventoRepository outboxRepository,
                                     ObjectMapper objectMapper) {
    this.pedidoRepository = pedidoRepository;
    this.outboxRepository = outboxRepository;
    this.objectMapper = objectMapper;
  }

  @Transactional // O se guardan AMBOS en la base de datos, o no se guarda NINGUNO
  public Pedido crearPedido(Pedido pedido) throws JsonProcessingException {
    Pedido guardado = pedidoRepository.save(pedido);

    String payloadJson = objectMapper.writeValueAsString(
        new PedidoCreadoPayload(guardado.getId(), guardado.getClienteId(), guardado.getTotal())
    );

    OutboxEvento outbox = new OutboxEvento("PEDIDO_CREADO", guardado.getId().toString(), payloadJson);
    outboxRepository.save(outbox);

    return guardado;
  }
}

// 2. Relay en segundo plano que vacía la tabla Outbox hacia el broker de mensajería con reintentos seguros
@Component
public class OutboxPublisherRelay {

  private final OutboxEventoRepository outboxRepository;
  private final RabbitTemplate rabbitTemplate;

  public OutboxPublisherRelay(OutboxEventoRepository outboxRepository, RabbitTemplate rabbitTemplate) {
    this.outboxRepository = outboxRepository;
    this.rabbitTemplate = rabbitTemplate;
  }

  @Scheduled(fixedDelay = 2000)
  @Transactional
  public void publicarEventosPendientes() {
    List<OutboxEvento> pendientes = outboxRepository.findTop100ByEnviadoFalseOrderByFechaCreacionAsc();
    for (OutboxEvento evento : pendientes) {
      rabbitTemplate.convertAndSend("exchange.negocio", evento.getTipoEvento(), evento.getPayloadJson());
      evento.marcarComoEnviado(Instant.now());
    }
  }
}
```

---

## 13. Problema 8: Modernización de Sistemas Viejos / Excel (Integración y Strangler Fig)

### El Problema de Negocio

Muchas empresas funcionan sobre un sistema antiguo (AS/400, Cobol, Visual Basic, un ERP viejo sin documentación) o
decenas de planillas Excel que se pasan por correo. **Reescribir todo el sistema desde cero en un proyecto de 2 años ("
Big Bang Rewrite") casi siempre fracasa** porque el negocio sigue cambiando mientras programas.

### Patrones de Migración e Integración Segura

| Patrón                                                 | Significado                                                                                                                                                                                                                                                            | Ejemplo Práctico                                                                                                |
|--------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------|
| **Strangler Fig Pattern (Higuera Estranguladora)**     | Ir reemplazando el sistema viejo **módulo por módulo** (primero el módulo de precios, luego el de reclamos, luego el de facturación) poniendo una fachada o API Gateway adelante, hasta que el sistema viejo queda vacío y se apaga sin trauma.                        | Las rutas `/api/reclamos/**` van al nuevo microservicio en Spring Boot; el resto sigue yendo al sistema legado. |
| **Anti-Corruption Layer (ACL - Capa Anticorrupción)**  | Cuando tu sistema nuevo moderno debe hablar con el sistema viejo que tiene nombres horribles (`CLI_FEC_ULT_MOV_99`), creas un **Adaptador/Traductor** aislado para que esa suciedad no contamine tu dominio limpio.                                                    | `LegacyErpClienteAdapter` traduce `TBL_CLI_01` a tu clase limpia `Cliente`.                                     |
| **Importador Tolerante a Fallos con Reporte de Filas** | Al migrar desde Excel/CSV al sistema nuevo, si la fila 432 tiene un CUIT mal escrito, no debes tirar un `500 Internal Server Error` sin explicación: debes validar fila por fila y devolver exactamente qué fila y columna tiene error para que el usuario lo corrija. | Devolver `List<ErrorImportacionFilaDto>` indicando `Fila 432, Columna Email: Formato inválido`.                 |

**Ejemplo de Capa Anticorrupción (ACL) en Spring Boot:**

```java
// El JSON horrible que devuelve el sistema viejo de la empresa de hace 20 años:
public record LegacyErpResponse(String COD_CLI_X, String NOM_RAZ_SOC, int EST_HAB_01, String SAL_CTA_CTE) {
}

// Tu Adaptador Anticorrupción (ACL) que protege al resto de tu aplicación:
@Component
public class ErpLegadoAnticorruptionAdapter implements DirectorioClientesPort {

  private final LegacyErpHttpClient legacyClient;

  public ErpLegadoAnticorruptionAdapter(LegacyErpHttpClient legacyClient) {
    this.legacyClient = legacyClient;
  }

  @Override
  public ClienteDominio obtenerCliente(String documento) {
    LegacyErpResponse raw = legacyClient.buscarPorDoc(documento);
    // Traducimos los códigos crípticos del sistema viejo a objetos limpios de nuestro negocio:
    boolean activo = (raw.EST_HAB_01() == 1);
    BigDecimal saldo = new BigDecimal(raw.SAL_CTA_CTE().trim().replace(",", "."));
    return new ClienteDominio(raw.COD_CLI_X(), raw.NOM_RAZ_SOC().trim(), activo, saldo);
  }
}
```

---

## 14. Diagnóstico y Resolución de Incidentes en Producción (Troubleshooting)

Cuando un sistema empresarial falla en vivo en producción, un ingeniero profesional **no adivina ni reinicia servidores
a ciegas**. Sigue un método clínico apoyado en **Observabilidad**:

| Herramienta / Concepto                              | Para qué sirve en una urgencia                                                                                                                                                                                             | Ejemplo en Spring Boot                                                                     |
|-----------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------|
| **Correlation ID / Trace ID (`MDC`)**               | Identificador único que acompaña a una petición desde que entra al Controller, pasa por los Services y se envía a otros microservicios. Permite filtrar en millones de líneas de log exactamente qué le pasó a un usuario. | `MDC.put("traceId", UUID.randomUUID().toString());` (o automático con Micrometer Tracing). |
| **Logs Estructurados con Contexto de Negocio**      | No loguear `"Error al procesar"`. Loguear siempre **qué entidad, qué ID y por qué falló** (cuidando de **nunca loguear contraseñas, tarjetas ni tokens**).                                                                 | `log.error("Fallo cobro para pedidoId={} clienteId={} monto={}", id, cliId, monto, ex);`   |
| **RFC 7807 (`ProblemDetail`)**                      | Devolver errores HTTP claros con un código único de negocio que el soporte técnico pueda buscar rápidamente.                                                                                                               | `ProblemDetail` con `type`, `title`, `detail` y `codigoErrorInterno`.                      |
| **Feature Flags (Interruptores de Funcionalidad)**  | Variables de configuración que permiten **apagar una funcionalidad nueva en 1 segundo** si da problemas en producción, sin tener que recompilar ni hacer rollback del despliegue.                                          | `if (featureFlags.isNuevoMotorDescuentosActivo()) { ... }`                                 |
| **Post-Mortem sin Culpa (*Blameless Post-Mortem*)** | Documento breve que se escribe después de resolver un incidente grave: qué pasó, línea de tiempo, causa raíz y qué test o alerta se agregó para que no pueda volver a ocurrir nunca más.                                   | Agregar un test de integración y un índice tras descubrir una consulta lenta.              |

**Ejemplo de Filtro de `Correlation-ID` (MDC) para rastrear problemas de punta a punta:**

```java

@Component
public class CorrelationIdFilter extends OncePerRequestFilter {

  private static final String HEADER_CORRELATION_ID = "X-Correlation-Id";
  private static final String MDC_KEY = "correlationId";

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {
    String correlationId = Optional.ofNullable(request.getHeader(HEADER_CORRELATION_ID))
        .filter(id -> !id.isBlank())
        .orElseGet(() -> UUID.randomUUID().toString());

    MDC.put(MDC_KEY, correlationId);
    response.setHeader(HEADER_CORRELATION_ID, correlationId);
    try {
      filterChain.doFilter(request, response);
    } finally {
      // Siempre limpiar el MDC al terminar el hilo para no mezclar IDs entre peticiones
      MDC.remove(MDC_KEY);
    }
  }
}
```

---

## 15. Trade-offs: Cómo Elegir la Arquitectura Correcta sin Sobre-Ingeniería

En ingeniería de sistemas **no existe la solución perfecta universal; todo es un *trade-off* (compromiso o balance entre
ventajas y desventajas)**. Saber elegir la opción adecuada según el tamaño real de la empresa es lo que distingue a un
perfil Senior:

| Decisión Arquitectónica                                       | Cuándo elegir la Opción Simple (Recomendado para el 80% de empresas)                                                                                                        | Cuándo elegir la Opción Compleja (Alta Escala)                                                                                                              |
|---------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Monolito Modular vs Microservicios**                        | **Monolito Modular en Spring Boot:** Equipo de 1 a 15 desarrolladores, dominio en evolución, necesitas velocidad de desarrollo y transacciones ACID simples en una sola BD. | **Microservicios:** Decenas de equipos independientes que necesitan desplegar por separado o módulos con requerimientos de hardware radicalmente distintos. |
| **SQL (PostgreSQL/MySQL) vs NoSQL (MongoDB/Redis)**           | **SQL Relacional:** Datos financieros, facturación, inventario, pedidos y relaciones claras donde la integridad referencial y transacciones ACID son sagradas.              | **NoSQL:** Catálogos con atributos dinámicos variables por producto (MongoDB) o caché/sesiones/contadores de ultra baja latencia (Redis).                   |
| **Comunicación Síncrona (REST) vs Asíncrona (Colas/Eventos)** | **REST Síncrono:** El usuario necesita la respuesta inmediata en pantalla para continuar su flujo (ej. consultar saldo, iniciar sesión).                                    | **Colas/Eventos:** Procesos largos, notificaciones, desacoplamiento de áreas o absorción de picos de tráfico (*Load Leveling*).                             |
| **Consistencia Fuerte (ACID) vs Consistencia Eventual**       | **Consistencia Fuerte:** Descontar dinero de una cuenta o asignar el último asiento de un avión.                                                                            | **Consistencia Eventual:** Actualizar el contador de "Productos más vendidos de la semana" o enviar el email con el resumen.                                |

---

## 16. Caso Completo de Punta a Punta: De Planilla Excel a Sistema Empresarial

Veamos cómo se aplica toda esta guía a un caso real completo de una empresa distribuidora:

### 1. El Problema de la Empresa (Situación Inicial)

Una distribuidora recibe 300 pedidos diarios de comercios minoristas.

- **Cómo trabajan hoy:** Los vendedores anotan los pedidos en una planilla de Google Sheets compartida. Administración
  mira la planilla, revisa en otro sistema si el cliente debe facturas viejas, descuenta a mano el stock en otra hoja y
  manda un mail al depósito.
- **Dolores del negocio:**
    1. Dos vendedores pisan la misma fila del Excel y borran pedidos.
    2. Se despachan pedidos a clientes morosos porque Administración se olvidó de revisar la deuda.
    3. Se vende mercadería sin stock real y el camión sale medio vacío.

### 2. Diseño de la Solución de Sistema

1. **Regla de Negocio 1 (Riesgo Crediticio):** Todo pedido nuevo verifica automáticamente la deuda vencida del cliente
   antes de reservar stock.
2. **Regla de Negocio 2 (Concurrencia de Stock):** El descuento de stock es atómico en base de datos para que dos
   vendedores nunca puedan vender la misma última caja.
3. **Regla de Negocio 3 (Idempotencia y Auditoría):** Cada pedido lleva un código único de operación, una máquina de
   estados estricta y dispara un evento asíncrono al depósito tras el `COMMIT`.

### 3. Implementación Limpia en Spring Boot

```java
// 1. Entidad de Dominio con sus invariantes protegidas
@Entity
@Table(name = "pedidos_mayoristas")
public class PedidoMayorista {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String codigoOperacion; // Clave de idempotencia

  private Long clienteId;
  private BigDecimal total;

  @Enumerated(EnumType.STRING)
  private EstadoPedido estado;

  @CreatedDate
  private Instant creadoEn;

  protected PedidoMayorista() {
  }

  public PedidoMayorista(String codigoOperacion, Long clienteId, BigDecimal total) {
    this.codigoOperacion = Objects.requireNonNull(codigoOperacion);
    this.clienteId = Objects.requireNonNull(clienteId);
    this.total = Objects.requireNonNull(total);
    this.estado = EstadoPedido.APROBADO_PARA_PREPARACION;
    this.creadoEn = Instant.now();
  }

  // Getters...
}

// 2. Caso de Uso que resuelve los 3 dolores del negocio en una transacción segura
@Service
public class TomarPedidoMayoristaUseCase {

  private final PedidoMayoristaRepository pedidoRepository;
  private final RiesgoCrediticioService riesgoService;
  private final InventarioService inventarioService;
  private final ApplicationEventPublisher eventPublisher;

  public TomarPedidoMayoristaUseCase(PedidoMayoristaRepository pedidoRepository,
                                     RiesgoCrediticioService riesgoService,
                                     InventarioService inventarioService,
                                     ApplicationEventPublisher eventPublisher) {
    this.pedidoRepository = pedidoRepository;
    this.riesgoService = riesgoService;
    this.inventarioService = inventarioService;
    this.eventPublisher = eventPublisher;
  }

  @Transactional
  public PedidoMayorista ejecutar(SolicitudPedidoDto solicitud) {
    // Dolor 1 resuelto: Idempotencia (si el vendedor mandó dos veces el mismo pedido por mala señal, no se duplica)
    Optional<PedidoMayorista> existente = pedidoRepository.findByCodigoOperacion(solicitud.codigoOperacion());
    if (existente.isPresent()) {
      return existente.get();
    }

    // Dolor 2 resuelto: Control automático e incorruptible de deuda vencida
    riesgoService.validarClienteAptoParaComprar(solicitud.clienteId(), solicitud.calcularTotal());

    // Dolor 3 resuelto: Reserva atómica de stock por cada ítem (lanza StockInsuficienteException si no alcanza)
    for (ItemSolicitudDto item : solicitud.items()) {
      inventarioService.reservarStockAtomico(item.productoId(), item.cantidad());
    }

    // Guardado firme del pedido aprobado
    PedidoMayorista nuevoPedido = new PedidoMayorista(
        solicitud.codigoOperacion(),
        solicitud.clienteId(),
        solicitud.calcularTotal()
    );
    PedidoMayorista guardado = pedidoRepository.save(nuevoPedido);

    // Notificación asíncrona post-commit al depósito para que impriman la orden de preparación
    eventPublisher.publishEvent(new PedidoListoParaDepositoEvent(guardado.getId()));

    return guardado;
  }
}
```

---

## 17. Checklist Rápido para Analizar Cualquier Problema de Negocio

Cada vez que te enfrentes a un nuevo requerimiento o problema empresarial en el trabajo o en una entrevista técnica,
hazte estas **10 preguntas de control**:

| #      | Pregunta de Control                                                                                    | Qué previene                                                                                                |
|--------|--------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------|
| **1**  | **¿Cuál es el dolor real y cómo se mide el éxito?**                                                    | Evita construir pantallas complejas que no resuelven el cuello de botella real.                             |
| **2**  | **¿Quién es la Fuente Única de Verdad (*SSOT*) de este dato?**                                         | Evita tener dos bases de datos contradiciéndose sobre el precio o el saldo.                                 |
| **3**  | **¿Qué pasa si el usuario o sistema externo envía la misma petición dos veces?**                       | Te recuerda implementar **Idempotencia** (`Idempotency-Key` / restricciones `UNIQUE`).                      |
| **4**  | **¿Qué pasa si dos usuarios modifican el mismo registro en el mismo milisegundo?**                     | Te recuerda usar `@Version` (Bloqueo Optimista) o `SELECT FOR UPDATE` (Bloqueo Pesimista).                  |
| **5**  | **¿Algún paso de este flujo llama a un servicio externo lento o inestable?**                           | Te recuerda configurar **Timeouts**, **Retries**, **Circuit Breaker** o sacarlo a un proceso **Asíncrono**. |
| **6**  | **¿Cuántos registros tendrá esta tabla dentro de 2 años?**                                             | Te obliga a poner **Índices**, **Paginación (`Pageable`/`Slice`)** y evitar `findAll()` en memoria.         |
| **7**  | **¿Qué estados válidos tiene esta entidad y qué transiciones están prohibidas?**                       | Evita que una factura `ANULADA` pase a `PAGADA` por un bug en el código.                                    |
| **8**  | **¿Qué rastro de auditoría necesitamos si mañana hay un reclamo legal o contable?**                    | Te recuerda usar **Soft Delete**, `@CreatedBy`, `@LastModifiedDate` y un historial de eventos.              |
| **9**  | **¿Cómo nos vamos a enterar en producción si esto empieza a fallar?**                                  | Te obliga a agregar **Logs con `Correlation-ID`**, métricas y alertas claras.                               |
| **10** | **¿Cuál es la solución más simple (KISS / MVP) que resuelve el problema hoy sin hipotecar el futuro?** | Evita la sobre-ingeniería y permite entregar valor rápido a la empresa.                                     |

> **Tip de lectura:** Cuando analices el código de un sistema empresarial existente, busca primero **las máquinas de
estados (Enums de estado)**, **los métodos anotados con `@Transactional`** y **los clientes de integración externa**.
> Esos tres puntos concentran el 90% de las reglas críticas del negocio y de los posibles puntos de fallo del sistema.

