# Fixtures iniciales

pedido-valido.json representa una creación válida para el catálogo del caso.
pedido-invalido.json prueba validación de contenido: el JSON es sintácticamente válido.
tarifa-valida.json representa el formato del proveedor externo, no tu DTO de dominio.

Usá ClassPathResource o el classloader para leerlos en tests; no dependas de una ruta
absoluta de tu computadora. Cada test prepara catálogo, stock y usuario por separado.
Los archivos no crean datos automáticamente ni resuelven ninguna prueba.
Agregá un cuerpo con sintaxis JSON rota en la etapa web para separar ambos errores.

