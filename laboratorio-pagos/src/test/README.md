# Pruebas del laboratorio de pagos

Las pruebas unitarias usan JUnit 5 y Mockito. No levantan Spring, PostgreSQL ni Docker.
Los paquetes siguen la estructura de `src/main/java`:

- `dinero/domain/objetcValue`: Money, PaymentBreakdown y PaymentGatewayResult.
- `dinero/domain/model`: creacion, reconstitucion y transiciones de Pay.
- `dinero/application/command`: validacion de referencias, IDs, tokens, importes y monedas.
- `dinero/application/service`: un test por servicio; persistencia y pasarela simuladas con Mockito.
- `dinero/infra/adapter`: decisiones del simulador para tokens ficticios.

El cobro aprobado y rechazado se prueba en `ChargePayTest`, de acuerdo con la API actual.
Las pruebas existentes de Money y PaymentBreakdown se trasladaron a sus paquetes de dominio.

## Ejecutar solo las pruebas unitarias

Con Maven y un JDK compatible con Java 21:

```shell
mvn clean test "-Dtest=com.estudio.pagos.dinero.**.*Test"
```

En IntelliJ tambien se puede ejecutar el paquete `com.estudio.pagos.dinero` dentro de
`src/test/java`. La limpieza de Maven evita ejecutar clases compiladas de los paquetes anteriores.

Los informes por clase quedan en `target/surefire-reports`.

## Prueba de integracion existente

`LaboratorioPagosApplicationTest` sigue separado de las pruebas unitarias.
Usa Spring Boot y PostgreSQL mediante Testcontainers; se omite si Docker no esta disponible.
Para ejecutar toda la suite:

```shell
mvn clean test
```

## Resultado de la implementacion de estas pruebas

Las pruebas respetan el comportamiento actual: `Pay.create` acepta un importe cero
y `Pay.capturePayment` acepta cero y pasa a CAPTURED. Los comandos CreatePayCommand
y CapturePayCommand rechazan cero; sus pruebas verifican esa validacion propia.
No se modifico codigo de produccion.
