# Consignas de algoritmos y estructuras de datos

| Etapa | Consigna | Secciones de la guía |
|---|---|---|
| 01 | [Complejidad y medición](01-complejidad.md) | 1, 16 |
| 02 | [Colecciones y contratos de igualdad](02-estructuras.md) | 2 |
| 03 | [Cruce de pedidos y comprobantes](03-cruces.md) | 3 |
| 04 | [Búsqueda binaria y rangos](04-busqueda.md) | 4 |
| 05 | [Ordenamiento y Comparator](05-ordenamiento.md) | 5 |
| 06 | [Dos punteros y ventanas](06-punteros-ventanas.md) | 6 |
| 07 | [Recursión, división y backtracking](07-recursion.md) | 7 |
| 08 | [Greedy, memoización y programación dinámica](08-greedy-dp.md) | 8 |
| 09 | [BFS, DFS, Dijkstra y dependencias](09-grafos.md) | 9 |
| 10 | [Top K y planificación por prioridad](10-heaps.md) | 10 |
| 11 | [LRU, LFU y vencimiento](11-cache.md) | 11 |
| 12 | [Token Bucket y ventanas de tasa](12-rate-limiting.md) | 12 |
| 13 | [Backoff y balanceo](13-jitter-balanceo.md) | 13 |
| 14 | [Partición, Bloom e IDs](14-particion-indices.md) | 14 |
| 15 | [Trie, checksums e integridad](15-texto-integridad.md) | 15 |
| 16 | [Integración y elección fundamentada](16-integracion.md) | 1–16 |

## Forma de trabajo

Leé el [caso](../caso/distribuidora.md) y los [datasets](../datos/README.md).
Las soluciones, modelos y pruebas algorítmicas son entregables del estudiante.
La base solo comprueba el arranque; no contiene algoritmos resueltos.

1. Definí entrada, salida, invariantes y comportamiento ante errores.
2. Resolvé a mano un ejemplo pequeño.
3. Implementá una variante clara y verificá corrección.
4. Compará con un oráculo simple sobre entradas pequeñas.
5. Analizá tiempo y memoria incluyendo preparación y tamaño de salida.
6. Medí aparte cuando tenga sentido y registrá evidencia en ejercicios/respuestas/NN.md.

## Convenciones

- Java 21; paquete com.estudio.algoritmos, organizado por tema.
- Funciones puras cuando sea posible; reloj/fuente monótona y azar inyectables donde se requieran.
- Centavos long con overflow detectado, no double para importes.
- Semilla fija, entradas pequeñas y límites explícitos en tests.
- JUnit para algoritmos; SpringBootTest solo para integración.
- No reemplazar el algoritmo pedido por una llamada JDK sin explicarlo.
  Las utilidades JDK sí sirven como comparación u oráculo donde corresponda.
- No asumir que las colecciones recibidas son mutables.
- Comparar con fuerza bruta solo en tamaños seguros.
- Concurrencia con barreras y timeouts; no mediante requests secuenciales de Postman.
- No incluir millones de registros ni benchmarks de varios minutos en mvn verify.

## Cobertura y lectura crítica

Las 16 secciones tienen una etapa asociada. Las implementaciones didácticas locales
no sustituyen bibliotecas de caché, sistemas distribuidos o criptografía de producción.

Big-O describe crecimiento, no tiempos exactos. Operaciones hash dependen de las
hipótesis de distribución; dos entradas distintas necesitan variables n y m.
La salida, ordenación previa y reconstrucción también cuestan tiempo y memoria.
Una mejora promedio no elimina casos adversos.

B-Tree/LSM, Snowflake y variantes avanzadas se estudian con el alcance delimitado
en 14; algoritmos criptográficos se usan mediante APIs, sin implementarlos desde cero.
La [documentación de PriorityQueue](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/PriorityQueue.html)
y las [variantes de jitter](https://aws.amazon.com/blogs/architecture/exponential-backoff-and-jitter/)
permiten contrastar ejemplos concretos de la guía.

