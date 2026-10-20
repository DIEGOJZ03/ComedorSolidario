# LEEME — Cambios para la rúbrica de Soluciones Web

Fecha: 20/10/2026. Proyecto: Comedor Solidario. Se trabajó sobre esta carpeta local; no había repositorio Git. El archivo `leeme.md` original se conserva.

## 1. Qué se implementó

Se conservan los dos CRUD de **comedores y necesidades**, se completan los módulos de **usuarios y donaciones**, y se incorporan únicamente dos entidades útiles: **productos y campañas**. Resultado: seis CRUD administrativos sobre seis entidades distintas. No se crearon seis módulos adicionales.

| Entidad / tabla | Crear | Leer listado y detalle | Actualizar | Eliminar / dar de baja |
| --- | --- | --- | --- | --- |
| Comedor / comedores | Formulario existente | Listado y detalle existentes | Formulario existente | INACTIVO, reversible |
| Necesidad / necesidades | Formulario, con producto opcional | Listado y nuevo detalle individual | Formulario y producto de referencia | INACTIVA, reversible |
| Usuario / usuarios | ADMIN registra DONADOR | Listado y detalle sin contraseñas | Nombre, apellido, correo, teléfono y estado del donador | INACTIVO, reversible; invalida el acceso de sesiones abiertas |
| Donacion / donaciones | ADMIN registra a nombre de un donador activo | Listado y detalle | Datos mientras está PENDIENTE; avance PENDIENTE → ACEPTADA → ENTREGADA | activo = FALSE; conserva la fila y la fecha de entrega histórica |
| Producto / productos | Nombre, descripción, unidad y estado | Listado y detalle | Formulario | INACTIVO, reversible |
| Campania / campanias | Comedor, nombre, descripción, fechas y estado | Listado y detalle | Formulario | INACTIVO, reversible |

Los productos forman un catálogo reutilizable de recursos solicitados: cada necesidad puede referenciar un producto. La referencia es opcional para conservar las necesidades existentes. El nombre, descripción y unidad de cada necesidad siguen siendo sus propios datos; cambiar el catálogo no reescribe solicitudes históricas. Los productos inactivos permanecen identificables para la administración y las referencias históricas.

Las campañas organizan convocatorias de apoyo de un comedor. Son públicas únicamente cuando están activas, su comedor está activo y la fecha actual está entre inicio y fin, ambos incluidos. No se añadieron pagos, inventarios, roles ni procesos ajenos al alcance.

**Permisos:** todos los CRUD de gestión están bajo `/admin/` y exigen ADMIN. Se conserva la funcionalidad del DONADOR descrita en el PDF: registro propio, perfil, consulta pública y registro/consulta de sus aportes. El administrador no se convierte en donador al registrar un aporte: selecciona la cuenta del donador real. Las cuentas ADMIN existentes se consultan, pero no se modifican ni desactivan desde el CRUD de donadores; se evita perder el acceso administrativo.

Las bajas son lógicas, válidas para el criterio «dar de baja». No se usa DELETE ni se rompen claves foráneas. En donaciones se separa la vigencia (`activo`) del proceso de entrega (`estado`), para mantener los tres estados del PDF. Una donación de baja deja de mostrarse en listados y totales; la fila permanece en SQL. No se implementó su reactivación.

## 2. Antes de ejecutar: base de datos

Usar MySQL 8.0.16 o superior (el esquema utiliza CHECK) y Tomcat 10.1 con JDK 21 o superior. El proyecto compila con `release 21` y usa Jakarta Servlet 6.0; Tomcat 9 no corresponde a estas dependencias.

### Si YA tienes la base comedor_solidario

1. Conserva una copia de seguridad de tu base desde MySQL Workbench.
2. Abre `database/migracion_rubrica.sql` en Workbench, conectado a tu servidor.
3. Ejecuta el archivo completo **una sola vez** antes de desplegar el WAR actualizado.
4. Este script crea `productos` y `campanias`, agrega `necesidades.producto_id` nullable con FK y `donaciones.activo` con valor inicial TRUE. No elimina ni cambia el contenido de los registros anteriores.
5. No ejecutes de nuevo `comedor_solidario.sql` sobre tu base existente. Tampoco repitas la migración: sus ALTER TABLE no son idempotentes.

Si una ejecución del script se interrumpe, revisa qué objetos se crearon antes de continuar: MySQL puede confirmar DDL parcialmente. No borres la base para intentar corregirlo.

### Si es una instalación NUEVA

Ejecuta solamente `database/comedor_solidario.sql`. Incluye las cuatro tablas originales, datos de demostración y la ampliación de la rúbrica. **No ejecutes después la migración**, porque ya está incorporada al final de este archivo.

### Conexión

La clase reutilizable sigue siendo `util/ConexionBD.java`. Usa `CS_DB_URL`, `CS_DB_USER` y `CS_DB_PASSWORD`, o un archivo `src/resources/db.properties` con `db.url`, `db.user` y `db.password`. Las variables de entorno tienen precedencia. Configura tus credenciales reales localmente; el archivo `.example` no se carga automáticamente. No es necesario cambiar el código Java para conectar.

Ejemplo de estructura del archivo, sustituyendo los valores de usuario y contraseña por los de tu instalación:

```properties
db.url=jdbc:mysql://localhost:3306/comedor_solidario
db.user=TU_USUARIO_MYSQL
db.password=TU_CLAVE_MYSQL
```

No se cambiaron contraseñas ni cuentas existentes. Inicia sesión con tu cuenta ADMIN habitual.

## 3. Compilar y ver los cambios

1. Abre en NetBeans la carpeta `ComedorSolidario-yoel`, donde está `pom.xml`.
2. Aplica el SQL que corresponda según la sección anterior y configura la conexión.
3. Selecciona **Clean and Build** y después **Run**, usando Tomcat 10.1. Si ejecutas Maven en consola: `mvn clean package`.
4. Despliega `target/ComedorSolidario.war`. Abre `http://localhost:8080/ComedorSolidario/inicio`, ajustando puerto y contexto si tu Tomcat utiliza otros.
5. Inicia sesión como ADMIN. La barra administrativa contiene Comedores, Necesidades, Donaciones, Usuarios, Productos y Campañas.
6. En Usuarios, Productos y Campañas usa **Registrar**, **Ver**, **Editar** y **Dar de baja**. En Donaciones, **Registrar donación**, luego **Ver detalle** para editar datos pendientes, aceptar, entregar o dar de baja. En Necesidades se agregó **Ver** y el selector opcional de producto.
7. Crea una campaña activa para un comedor activo, con fechas que incluyan hoy. Abre una ventana sin sesión: Comedores → detalle del comedor. Verás la campaña leída de MySQL. Al editarla o darla de baja desde ADMIN, recarga la página pública para ver el cambio.

### Cómo demostrar cada CRUD

Para cada entidad usa un registro de prueba: **crear → listar → ver detalle → editar → volver a consultar → dar de baja → verificar persistencia**. En comedores, necesidades, usuarios, productos y campañas también puedes reactivar. Para donaciones edita antes de aceptar; los datos materiales quedan protegidos cuando deja de estar pendiente. La aceptación y entrega son operaciones adicionales de actualización con control del estado anterior y registro de fecha.

`docs/VERIFICACION.md` contiene la secuencia completa de aceptación, validaciones y comprobación de permisos. No uses registros reales para una demostración de bajas.

## 4. Diagramas PlantUML

- `docs/modelo-clases.puml`: entregable principal; seis entidades con todos sus atributos privados, tipos, constructores explícitos, getters/setters y asociaciones con multiplicidad.
- `docs/clases-completas.puml`: vista técnica ampliada; las 33 clases Java, interfaz Mapper interna, herencia de DAO y servlets, dependencias y separación en paquetes. Los tipos externos de Jakarta están señalados.

Abre el `.puml` en un visor compatible con PlantUML. Si ya tienes el JAR de PlantUML, desde la raíz del proyecto puedes generar un SVG con:

```text
java -jar RUTA/plantuml.jar -tsvg docs/modelo-clases.puml
java -jar RUTA/plantuml.jar -tsvg docs/clases-completas.puml
```

Se usa el motor Smetana para evitar depender de Graphviz. Los archivos se entregan como texto editable; no es necesario convertirlos para revisar el código PlantUML. La compilación Java no valida el renderizado de PlantUML.

### Coherencia entre modelo, Java y SQL

| Clase | Tabla | Claves foráneas y multiplicidades |
| --- | --- | --- |
| Usuario | usuarios | Un usuario puede registrar 0..* donaciones; cada donación tiene 1 usuario donador |
| Comedor | comedores | Un comedor tiene 0..* necesidades, donaciones y campañas |
| Necesidad | necesidades | `comedorId` → `comedor_id` obligatorio; `productoId` → `producto_id` opcional (0..1 producto por necesidad) |
| Donacion | donaciones | `donanteId` → `donante_id`, `comedorId` → `comedor_id`, ambos obligatorios |
| Producto | productos | Puede ser referenciado por 0..* necesidades |
| Campania | campanias | `comedorId` → `comedor_id` obligatorio |

Las asociaciones están implementadas mediante IDs; no se dibujan colecciones ni referencias de objetos que no existan en el código. No corresponde dibujar composición ni eliminación en cascada, porque los registros conservan identidad e historial. La herencia real está en `JdbcDAO` y `BaseServlet`, visible en el diagrama ampliado.

Los atributos `comedorNombre` y `donanteNombre` se obtienen mediante JOIN y no son columnas de las tablas respectivas. `Integer productoId` permite NULL; `int` representa IDs obligatorios. `BigDecimal` corresponde a DECIMAL(12,2), `LocalDateTime` a DATETIME, `LocalDate` a DATE y `boolean activo` a BOOLEAN. Los estados y roles se conservan como String en Java, coherentes con sus ENUM SQL y validados en controladores.

## 5. Integración JDBC pública

La integración existente se conserva: `InicioServlet` consulta indicadores y comedores; `ComedorServlet` filtra comedores y obtiene sus necesidades; las vistas del donador consultan sus propios aportes. Se agregó `CampaniaDAO.listarPublicas(comedorId)` al detalle público del comedor. Los textos de navegación pueden permanecer en JSP; los registros y listados de negocio provienen de SQL.

Los DAO usan `JdbcDAO` y `ConexionBD`: PreparedStatement con parámetros para entradas, y try-with-resources para cerrar Connection, PreparedStatement y ResultSet. Las JSP muestran los valores mediante JSTL y `c:out`. No se incorporó otra tecnología de persistencia.

## 6. Reparto propuesto para seis integrantes

Los nombres provienen del PDF entregado. Esta tabla es una **propuesta de responsabilidad para estudiar, validar y exponer**, no una afirmación de autoría previa. El equipo debe acordarla antes de presentar su reporte individual.

| Integrante | CRUD propuesto | Qué debe explicar |
| --- | --- | --- |
| De Fina Maldonado Flavio Alonso | Comedores | Formulario, filtros públicos, persistencia y baja lógica |
| Juarez Zapata Diego Humberto | Donaciones | Registro administrativo a nombre del donador, edición pendiente, estados y baja |
| Rujel Dios Julio César | Usuarios / donadores | Correo único, hash, edición, baja y permisos |
| Yovera Villegas Franky Yoel | Necesidades | Cantidad, prioridad, relación con comedor y producto opcional |
| Vidal Vega Didier Fabricio | Productos | Catálogo, CRUD y referencias históricas |
| Zevallos Davila Jennifer | Campañas | Relación con comedor, fechas y publicación JDBC |

## 7. Archivos modificados y añadidos

| Archivo o grupo | Cambio |
| --- | --- |
| `database/comedor_solidario.sql` | Ampliación del esquema para instalación nueva |
| `database/migracion_rubrica.sql` | Actualización no destructiva para una base existente |
| `model/Donacion.java`, `model/Necesidad.java` | Vigencia lógica y FK opcional de producto |
| `model/Producto.java`, `model/Campania.java` | Nuevas entidades encapsuladas |
| `dao/DonacionDAO.java` | Editar pendiente, dar de baja y excluir bajas de listados/totales |
| `dao/UsuarioDAO.java` | Crear/editar donadores y cambiar estado desde administración |
| `dao/NecesidadDAO.java` | Guardar y leer producto de referencia |
| `dao/ProductoDAO.java`, `dao/CampaniaDAO.java` | CRUD JDBC; consulta de campañas públicas |
| `controller/AdminDonacionServlet.java`, `controller/AdminUsuarioServlet.java` | Completar operaciones administrativas |
| `controller/AdminProductoServlet.java`, `controller/AdminCampaniaServlet.java` | Controladores de los dos nuevos CRUD |
| `controller/NecesidadServlet.java` | Detalle individual y validación de producto |
| `controller/ComedorServlet.java` | Consulta de campañas públicas |
| `filter/SeguridadFilter.java` | Comprobar vigencia de la cuenta, incluyendo sesiones abiertas |
| `util/ValidacionUtil.java` | Validación reutilizable de fechas |
| `web/views/admin/usuarios/*`, `donaciones/*`, `necesidades/*` | Formularios, detalle y acciones correspondientes |
| `web/views/admin/productos/*`, `campanias/*` | Interfaces nuevas con el estilo existente |
| `web/views/common/header.jspf` | Enlaces a Productos y Campañas |
| `web/views/public/detalle-comedor.jsp` | Campañas vigentes obtenidas de SQL |
| `docs/*.puml`, `docs/VERIFICACION.md`, `LEEME_RUBRICA.md` | Diagramas, guía de pruebas e instrucciones |

Las rutas Java de la tabla están bajo `src/java/com/comedorsolidario/`. El listado exacto con hashes está en `docs/CAMBIOS_ARCHIVOS.md`. Se conservan el diseño CSS, imágenes, autenticación original, configuración Maven y flujos del donador. Los controles de vigencia agregados son necesarios para que la baja de cuentas tenga efecto.

## 8. Verificación y límites reales

Se compiló el proyecto con Maven y Java release 21: **BUILD SUCCESS**, 33 clases fuente y WAR generado. No se pudo conectar a MySQL desde esta sesión (ERROR 2003 / 10013 en localhost:3306), por lo que **no se aplicó la migración ni se verificaron los CRUD de extremo a extremo en una base real**. Tampoco se afirma haber comprobado visualmente las JSP en Tomcat ni renderizado los diagramas. Es necesario ejecutar la guía de aceptación en tu entorno antes de la evaluación.

La migración es aditiva: conserva los datos anteriores. Para revertir una entrega, utiliza tu respaldo original del proyecto. No elimines tablas ni columnas nuevas si ya contienen datos que quieras conservar. El respaldo temporal de esta sesión no se incluye en la entrega.

Además se ejecutó un verificador de encapsulamiento, correspondencia del modelo con PlantUML y límites de validación. Terminó correctamente; el código y la orden para repetirlo están en `verificacion/VerificarRubrica.java` y `docs/VERIFICACION.md`. Esto no sustituye las pruebas JDBC y HTTP pendientes.

## Corrección de conexión del 20/10/2026

Se revisó el log real de Tomcat (último error observado a las 02:01:30): `Access denied for user 'comedor_app'@'localhost' (using password: NO)`. Faltaba `src/resources/db.properties`; solo estaba el archivo de ejemplo, que no se carga. Se creó el archivo correcto con la configuración proporcionada por el usuario y se recompiló correctamente. Las credenciales quedan en el archivo local, excluido por .gitignore; no se reproducen aquí.

Para activar esta corrección: detener Tomcat en NetBeans → Services/Servicios → Servers/Servidores; aplicar la migración SQL una sola vez si aún no se hizo; ejecutar Clean and Build y luego Run. La URL configurada es http://localhost:8080/ComedorSolidario/inicio. La contraseña MySQL no es la contraseña de la cuenta ADMIN de la aplicación.

La conexión desde la sesión de asistencia sigue bloqueada por permisos de red, por lo que no se ejecutó la migración ni se comprobó el login después del nuevo despliegue. El diagnóstico de solo lectura `verificacion/DiagnosticoConexion.java` permite comprobar conexión, seis tablas y las columnas nuevas desde una terminal local.

## Mejora de acciones visibles del 20 de octubre de 2026

Se añadió Eliminar directamente a los listados de Usuarios, Productos, Campañas y Donaciones, junto a Ver y Editar. En Comedores y Necesidades, Desactivar ahora se muestra como Eliminar. La operación sigue siendo una baja lógica y conserva el historial; los registros inactivos muestran Reactivar cuando el módulo lo permite. Los botones superiores ahora indican Crear y la entidad; Usuarios muestra Crear donador. Las cuentas ADMIN mantienen su protección y las donaciones aceptadas o entregadas no permiten editar sus datos materiales.

La confirmación compartida explica el efecto de la acción y diferencia Eliminar de Reactivar. Cancelar no envía la operación. Las acciones mantienen POST y token CSRF. Se armonizó el texto Eliminar en los detalles de Usuarios, Productos, Campañas y Donaciones.

Archivos de esta actualización: los seis `web/views/admin/*/listar.jsp`; los detalles de usuarios, productos, campanias y donaciones; `web/views/common/footer.jspf`; `web/assets/js/main.js`. No cambia el esquema SQL, las entidades Java ni los DAO. No se necesita otra migración por estos botones.

Validación: Maven package completó BUILD SUCCESS; JavaScript pasó node --check; Jasper de Tomcat compiló las JSP con 0 errores. Jasper informó que su compilador utiliza nivel 19 para las JSP cuando se solicita 21; las clases del proyecto mantienen release 21. Esta comprobación de traducción/compilación no sustituye la ejecución HTTP y JDBC de cada CRUD.

Para ver la actualización: detener Tomcat, Clean and Build, Run y recargar con Ctrl+F5. No repetir migracion_rubrica.sql si ya se aplicó. Para demostrar Eliminar usar un registro de prueba, cancelar una vez, confirmar después y comprobar el cambio de estado o su exclusión del listado según el módulo.
