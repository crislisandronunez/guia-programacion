# 04 — Repositorios, operadores y proyecciones

## Lectura
Secciones 7–9, 13 de la [guía](../../guias/Glosario-Spring-MongoDB.md).

## Consigna
Implementá búsquedas derivadas y @Query parametrizada por stock, rango de precio,
etiquetas y nombre. Agregá variantes [{color, stock}] a productos y compará filtros
independientes de array con $elemMatch para exigir coincidencia en un mismo elemento.

Devolvé ProductoResumenDto y una proyección de interfaz. Verificá los campos realmente
solicitados al servidor, no solo la forma final de la respuesta.
Limitá longitud de búsqueda y escapá metacaracteres si el usuario busca texto literal.

## Experimentos y aceptación
- Rango inclusivo y rango inválido tienen resultados definidos.
- $in y $all se prueban con etiquetas distintas.
- El caso variante roja sin stock y azul con stock no satisface “roja con stock” con $elemMatch.
- No se concatenan fragmentos JSON arbitrarios proporcionados por el usuario.
- Proyecciones omiten atributos grandes y campos internos.
- Demostrá diferencia entre campo inexistente, null y valor explícito.

## Para explicar
¿Una búsqueda regex sin ancla aprovecha siempre un índice normal? Comprobalo.

Siguiente: [05](05-template.md).
