# 02 — Políticas puras de dominio

## Lectura

Secciones 2, 4, 7 de la [guía](../../guias/Glosario-Interface-Contratos.md).

## Consigna

Construí modelos de dominio y PoliticaDescuento con implementaciones para las tres
categorías. Contrato e implementaciones viven en domain/policy y no usan Spring, JPA
ni HTTP. Elegí una selección explícita que detecte cero o varias políticas aplicables.

Implementá el cálculo y las invariantes del caso. Para pruebas de redondeo podés usar
importes construidos directamente aunque el catálogo inicial tenga precios enteros.
El redondeo se realiza una vez sobre el descuento total.

## Experimentos y aceptación

- Las cinco filas de ejemplos del caso producen los importes indicados.
- Probás el umbral mayorista y el tope VIP, incluyendo justo antes y después.
- Un subtotal negativo se rechaza; el contrato define si acepta cero.
- Redondeo HALF_EVEN funciona en empates y no usa double.
- Falta de política o dos políticas para una categoría produce error visible.
- La selección no depende accidentalmente de findFirst ni del orden de inyección.
- Los tests son unitarios, sin @SpringBootTest.

## Entrega

Código, pruebas y respuestas en `ejercicios/respuestas/02.md`.
Agregá documentación del contrato y solicitudes Postman cuando corresponda.

## Para explicar

¿Por qué una implementación de política puede vivir en dominio y no en infraestructura?

Siguiente: [Puertos de salida y adaptadores en memoria](03-puertos-salida.md).

