# 13 — Maven, cobertura y calidad de pruebas

## Lectura

Secciones 19 de la [guía](../../guias/Glosario-Testing.md).

## Consigna

Configurá Failsafe con integration-test y verify. Adoptá *Test para unitarios/slices
y *IT para integraciones: renombrá el smoke HTTP y separá las pruebas con Docker.
Inspeccioná reportes para demostrar que cada test corre una vez. No asumas que
*IntegrationTest lo descubre Failsafe por defecto.

Agregá JaCoCo compatible con el JDK elegido y preservá su agente si configurás otro
argLine. Generá cobertura de unitarios e integraciones con archivos o append controlados.
Elegí un umbral razonado para reglas de negocio, no exclusiones masivas para aprobar.

## Experimentos y aceptación

- mvn test ejecuta la selección rápida; mvn verify agrega integraciones y falla si alguna falla.
- Reportes Surefire/Failsafe confirman cantidad de tests y omisiones.
- El informe incluye realmente las ejecuciones esperadas.
- Una rama ejecutada sin aserción no se considera suficientemente verificada.
- Cambiar >= por > en el umbral del descuento hace fallar el test de frontera.
- La mutación manual se revierte; PIT queda como extensión con versión compatible.
- Preparás instrucciones de CI con Java 21 y Docker; no publicás un workflow en la raíz sin decidir el alcance del repositorio.

## Entrega

Pruebas y código mínimo del comportamiento, más evidencia y respuestas en
`ejercicios/respuestas/13.md`. Indicá qué falla si rompés la regla que estás verificando.

## Para explicar

¿Cómo puede subir la cobertura sin mejorar la capacidad de detectar errores?

Siguiente: [Diagnóstico, regresión y recorrido final](14-regresion-integracion.md).

