# Datos iniciales

CSV UTF-8, separador coma, cabecera obligatoria y valores ficticios sin comas ni comillas
internas. El lector a implementar puede limitarse a este formato y debe rechazar
columnas faltantes, tipos inválidos y valores fuera del contrato.

| Archivo | Uso |
|---|---|
| pedidos.csv | Cruce, ordenamiento y ranking |
| comprobantes.csv | Duplicados, pagos parciales y huérfanos |
| eventos.csv | Ventana temporal y bordes exactos |
| rutas.csv | Caminos dirigidos con pesos |
| dependencias.csv | Orden de tareas |

Los archivos son entradas, no soluciones. Prepará los resultados esperados a mano
antes de escribir el algoritmo. Agregá casos vacíos, inválidos y extremos en tests.

Leé desde una ruta explícita, sin asumir siempre el directorio de IntelliJ.
Si usás estos archivos en tests, configurá testResources o indicá su ubicación de
forma reproducible. No son recursos automáticamente empaquetados en el JAR.

Generá datasets grandes durante pruebas/benchmarks con semilla fija y límites;
no guardes millones de filas en Git.

