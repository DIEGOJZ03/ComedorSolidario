# Diagramas

Abre los SVG en un navegador para presentarlos o insertarlos en diapositivas. Los archivos `.puml` son las fuentes editables PlantUML.

## Clases y cardinalidades

Usuario 1 — 0..* Donacion: cada donación tiene exactamente un donador; un usuario puede no haber donado.
Comedor 1 — 0..* Donacion: cada donación tiene exactamente un comedor; un comedor puede no haber recibido aportes.
Comedor 1 — 0..* Necesidad: cada necesidad corresponde a un comedor; un comedor puede no haber publicado necesidades.

Los cuatro JavaBeans contienen constructor vacío, constructor por id y getters/setters. En Java se guardan los identificadores de asociación para mantener el modelo simple. Los campos de nombres obtenidos por JOIN facilitan la vista; no duplican esos nombres en la tabla donaciones.

Los DAO dependen de los JavaBeans y de ConexionBD; los Servlets dependen de los DAO y preparan los atributos de las JSP. Se omiten del diagrama principal para que las entidades sean legibles en la exposición.

## Modelo relacional

PK identifica cada fila. FK garantiza que comedor y usuario referenciados existan. `email` tiene UNIQUE, cantidades tienen CHECK positivo y `fecha_entrega` solo está presente en ENTREGADA. La regla de rol DONADOR se valida en el servidor y en el INSERT SELECT; una FK a usuarios por sí sola no impone el rol.

El SQL completo es la fuente de verdad para tamaños, restricciones y datos de prueba. Las tablas usan InnoDB y utf8mb4. Desactivar preserva relaciones: no se aplica ON DELETE CASCADE.
