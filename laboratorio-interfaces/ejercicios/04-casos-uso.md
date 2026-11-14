# 04 — Puertos de entrada y orquestación

## Lectura

Secciones 2, 5, 11 de la [guía](../../guias/Glosario-Interface-Contratos.md).

## Consigna

Definí EmitirPresupuestoUseCase, ConsultarPresupuestoUseCase y ListarPresupuestosUseCase
con comandos y resultados propios de aplicación. Implementá los servicios con
inyección por constructor: puertos de salida, políticas y Clock.

La aplicación valida el comando, obtiene datos, pide el cálculo al dominio, construye
la instantánea y guarda. Las fórmulas no se duplican en el servicio.
Construí el flujo en tests usando adaptadores en memoria o dobles simples, sin Spring.

## Experimentos y aceptación

- Emitir guarda un presupuesto completo y devuelve su identificador.
- Cliente o producto ausente impide guardar cualquier presupuesto parcial.
- Cantidad inválida o SKU repetido se rechaza también al invocar directamente el caso de uso.
- Clock fijo produce fechas y vencimiento deterministas.
- Cambiar precios después no altera el presupuesto ya emitido.
- Fallo de persistencia no devuelve un resultado exitoso.
- El caso de uso desconoce de dónde vino el comando y qué tecnología guarda los datos.

## Entrega

Código, pruebas y respuestas en `ejercicios/respuestas/04.md`.
Agregá documentación del contrato y solicitudes Postman cuando corresponda.

## Para explicar

¿Por qué las anotaciones de validación del controller no bastan para proteger el puerto de entrada?

Siguiente: [HTTP, lote y composición con Spring](05-entradas-spring.md).

