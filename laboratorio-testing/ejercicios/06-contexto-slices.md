# 06 — Contexto, JSON y configuración

## Lectura

Secciones 9–10 de la [guía](../../guias/Glosario-Testing.md).

## Consigna

Conectá los beans en la aplicación y compará una prueba unitaria con @SpringBootTest.
Usá @TestConfiguration para Clock y un colaborador de práctica; compará @Mock con
@MockitoBean en el contexto. No importes configuraciones de test desde producción.

Probá DTO con @JsonTest y una propiedad condicional sencilla con ApplicationContextRunner.
Inspeccioná dependency:tree antes de agregar herramientas que el starter ya traiga.

## Experimentos y aceptación

- El test de JSON comprueba nombres, importes y fechas, no solo round-trip del mismo mapper.
- Un DTO inválido en JSON se distingue de un valor de dominio inválido.
- La prueba de configuración comprueba bean presente/ausente según propiedad.
- @Mock no se confunde con un reemplazo del bean de Spring.
- No se combinan slices incompatibles en una clase.
- No usás @DirtiesContext por defecto para limpiar datos de negocio.

## Entrega

Pruebas y código mínimo del comportamiento, más evidencia y respuestas en
`ejercicios/respuestas/06.md`. Indicá qué falla si rompés la regla que estás verificando.

## Para explicar

¿Qué parte de la aplicación queda fuera de un slice y por qué eso puede ser útil?

Siguiente: [Contrato web con MockMvc](07-mockmvc.md).

