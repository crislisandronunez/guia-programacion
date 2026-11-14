# 13 — Backoff y balanceo

## Lectura

Secciones 13 de la [guía](../../guias/Glosario-Tipos-de-Algoritmos-Programadores.md).

## Consigna

Implementá calculadoras de espera sin dormir: exponencial acotado, full jitter y
equal jitter. Usá número de reintento desde cero y fuente aleatoria inyectable.
Full jitter toma entre cero y el tope; equal jitter reserva parte fija y aleatoriza
el resto. Documentá límites inclusivos/exclusivos.

Simulá round robin, weighted round robin y least-active sobre servidores ficticios.
Registrá inicios/finalizaciones y definí desempate, caída y recuperación.

## Experimentos y aceptación

- Intentos muy grandes saturan al máximo sin overflow ni shifts que reinicien el exponente.
- Semilla fija reproduce la simulación; tests verifican rangos sin exigir azar exacto no controlado.
- El ejemplo de la guía con piso en la mitad se clasifica según su fórmula.
- Sin servidores disponibles se devuelve un resultado explícito.
- Los pesos inválidos se rechazan y la distribución se verifica sobre ciclos definidos.
- Least-active libera el contador también si la tarea falla.
- Distinguís balancear cantidad de pedidos de balancear trabajo.

## Entrega

Implementación, pruebas, análisis de tiempo/memoria y respuestas en
`ejercicios/respuestas/13.md`. Incluí precondiciones, casos límite e invariantes.

## Para explicar

¿Por qué agregar jitter no vuelve seguro reintentar una operación con efectos duplicables?

Siguiente: [Partición, Bloom e IDs](14-particion-indices.md).

