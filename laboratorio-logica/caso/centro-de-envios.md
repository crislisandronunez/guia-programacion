# Caso: turnos de un centro de envíos

Un centro de envíos registra cuántos paquetes recibe en cada turno de atención de un día. La entrada es un arreglo `int[] cargas`; cada posición representa un turno, empezando en la posición `0`. El orden de los turnos importa cuando se busca el primero que cumple una condición.

En este laboratorio, una carga válida está entre **0 y 50 paquetes**, incluidos ambos límites. `0` representa un turno real sin paquetes. Un arreglo vacío representa un día sin turnos registrados. Un arreglo `null` significa que falta la entrada y se rechaza. Una carga negativa o mayor que `50` invalida el arreglo completo, incluso si aparece después de un dato que permitiría terminar una búsqueda.

Estos límites son reglas del caso ficticio. No representan la capacidad de un centro real.

| Entrada | Interpretación |
|---|---|
| `{0, 12, 45}` | Tres turnos: el primero vacío, el segundo con 12 paquetes y el tercero con 45. |
| `{}` | Ningún turno registrado. |
| `{0}` | Un turno registrado, sin paquetes. |
| `null` | Entrada ausente; se rechaza. |
| `{12, 51}` | Entrada inválida; se rechaza por completo. |

## Reglas comunes

- Los métodos que reciben `int[] cargas` rechazan `null` con `IllegalArgumentException`.
- Cualquier valor fuera de `0..50` provoca `IllegalArgumentException`; ningún método devuelve un resultado parcial.
- Una búsqueda devuelve la **posición**, no el número de turno contado desde uno.
- Una secuencia vacía es válida, salvo que una consigna exija explícitamente un elemento para producir su resultado.
- Las funciones no modifican el arreglo recibido.
- Las pruebas deben incluir límites, vacío, un elemento, varios elementos y entradas inválidas pertinentes.

Las consignas que agregan un parámetro, como `minimo` o `cupo`, definen por separado su rango admitido. Las operaciones de suma usan `long` para el resultado. Este caso contiene datos pequeños para poder razonar y trazar resultados a mano.
