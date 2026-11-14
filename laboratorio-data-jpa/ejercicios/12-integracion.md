# 12 — Migraciones, pool y desafío integrador

## Lectura
Secciones 19–21 de la [guía](../../guias/Glosario-Spring-Data-JPA.md).

## Consigna
Agregá un campo obligatorio a una tabla con datos mediante migración en etapas:
columna compatible, backfill y restricción final. Probá arranque desde base vacía
y actualización desde una versión anterior. No edites migraciones ya aplicadas.

Prepará @DataJpaTest con Testcontainers y AutoConfigureTestDatabase(replace=NONE).
Probá repositorios, mappings, restricciones y consultas tras flush/clear.
Para pruebas de commit, rollback y concurrencia abrí transacciones explícitas separadas;
no dependas únicamente del rollback automático del test.

Compará una importación por JPA con inserciones por lotes y una variante JdbcTemplate.
Medí viajes/tiempo, acotá el persistence context con flush/clear y documentá cómo las
estrategias de ID afectan batching. No prometas que saveAll es una sola sentencia.

En un perfil de pruebas reducís Hikari a dos conexiones y generás contención controlada.
Observá timeout y recuperación sin filtrar secretos. Dimensionar el pool requiere
mediciones y límites de PostgreSQL, no aumentarlo sin analizar.

## Desafío
Creá clientes y productos físicos/digitales, armá un pedido, confirmalo con stock correcto,
consultá una página de resúmenes sin N+1, cancelalo una vez y verificá auditoría.
Intentá confirmar dos pedidos por la última unidad y validá un único ganador.
Desactivá un producto y comprobá que el historial continúa visible.

## Entrega y aceptación
- mvn verify con Docker activo y pruebas de integración no omitidas.
- Colección Postman con recorrido repetible y variables para IDs.
- Consultas DBeaver para verificar stock, líneas, precios y versiones.
- Pruebas de migración desde cero y desde esquema anterior.
- Informe en ejercicios/respuestas/12-informe.md con mediciones y SQL observado.
- Matriz de cobertura de las 21 secciones, distinguiendo implementado y conceptual.
- Comparación razonada de JPA frente a JDBC para la importación.

Segundo nivel de caché, @Formula, @Immutable y @Subselect quedan como extensiones:
explicá cuándo podrían aportar valor y qué riesgos introducirían antes de incorporarlos.
