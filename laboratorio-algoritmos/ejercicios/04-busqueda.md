# 04 — Búsqueda binaria y rangos

## Lectura

Secciones 4 de la [guía](../../guias/Glosario-Tipos-de-Algoritmos-Programadores.md).

## Consigna

Implementá búsqueda lineal, binaria y límites inferior/superior en un arreglo ordenado.
El contrato devuelve primera posición de un valor o ausencia; lower/upper bound permiten
contar duplicados. Compará con utilidades JDK sin atribuirles garantías que no prometen.

Separá costo de ordenar y costo de buscar. Para validar precondición de orden,
usá una operación separada y contabilizá su costo.

## Experimentos y aceptación

- Cubrís vacío, un elemento, extremos, ausente y duplicados.
- El cálculo del índice medio evita overflow.
- La búsqueda principal no recorre todo el arreglo para validar orden en cada consulta.
- Contás comparaciones y justificás O(log n) sobre acceso aleatorio.
- No asumís igual costo al usar get(i) sobre LinkedList.
- La variante que ordena primero informa O(n log n) adicional.

## Entrega

Implementación, pruebas, análisis de tiempo/memoria y respuestas en
`ejercicios/respuestas/04.md`. Incluí precondiciones, casos límite e invariantes.

## Para explicar

¿En cuántas consultas empieza a convenir preparar una estructura ordenada?

Siguiente: [Ordenamiento y Comparator](05-ordenamiento.md).

