# LEEME - Comedor Solidario: rúbrica y funcionamiento

## Productos: catálogo sencillo desde Necesidades (21/10/2026)

La pantalla **Admin > Productos** es una sola tabla con **Producto, Cantidad total, Presentación y Acciones**. Muestra los nombres registrados en Necesidades y suma sus cantidades por producto y unidad. Si solo se han registrado arroz y aceite, aparecerán esos productos. No usa nombres alternativos del catálogo de referencia.

Se retiraron de esta vista el botón Crear producto de catálogo, Registrar necesidad, el bloque de necesidades activas, las categorías automáticas y la segunda tabla de catálogo. No se borraron productos ni necesidades de la base. Los formularios y controladores anteriores de productos se conservan, pero sus acciones no se presentan en esta pantalla de consulta.

**Acciones > Ver:** abre una página independiente en /admin/productos/distribucion. Se eliminó el detalle desplegable que alargaba las filas del listado. La nueva página muestra el nombre del producto, total y presentación, seguidos de una tabla con comedor destinatario, cantidad y unidad original, prioridad y botón Ver solicitud. El nombre del comedor abre sus datos y Volver a productos regresa al catálogo. Si un comedor tiene varias solicitudes, cada una aparece identificada por su número.

El enlace identifica el producto por nombre y unidad, con parámetros codificados; no usa posiciones de fila ni IDs de un catálogo distinto. El servidor vuelve a consultar las necesidades vigentes y calcula el total y su detalle con las mismas solicitudes. Si el producto ya no tiene solicitudes activas en comedores activos, devuelve 404. No se agregaron botones de creación ni categorías.

El origen es la solicitud registrada en Necesidades y el destino es el comedor que requiere esa cantidad. Esto no identifica a un donante/proveedor ni confirma entregas: esa información no se deduce de las necesidades.

- Arroz y azúcar se suman por separado, aunque ambos estén en kg. Tampoco se mezclan kg con litros o cajas. Se reconocen variantes de escritura como kg/kilos y litro/litros.
- Para líquidos, la presentación muestra la **equivalencia en envases de 20 L**: 100 L equivalen a 5 envases; 105 L equivalen a 5 envases más 5 L restantes. Menos de 20 L muestra cero envases completos y el volumen restante. No se redondea hacia arriba.
- Los envases de 20 L son una referencia de presentación solicitada, no una capacidad real registrada: las necesidades actuales guardan cantidad y unidad, pero no número de botellas ni capacidad de cada una. No se presenta esta equivalencia como inventario.
- Las cantidades son las solicitadas en necesidades ACTIVA de comedores ACTIVO. Quitar el bloque visual de necesidades activas no cambia ese criterio. Para cambiar el producto o la cantidad, se edita la necesidad en su propia sección. Al volver a abrir Productos, JDBC consulta los datos y recalcula el catálogo.

**Para verlo:** detener la aplicación, Clean and Build, Run y Ctrl+F5. Abrir Admin > Productos y pulsar Ver en la columna Acciones junto a Arroz o Aceite. Comprobar que se abre una página independiente, que las cantidades detalladas suman su total y que funcionan Ver solicitud, el enlace al comedor y Volver a productos. No se requieren migraciones SQL ni volver a registrar datos.

Implementación: AdminProductoServlet consulta NecesidadDAO.listar(null, true); ResumenProductosService agrupa por nombre y unidad; ResumenProducto calcula la equivalencia de líquidos. No se guardan totales duplicados en MySQL. El diagrama técnico existente refleja las clases actuales.

La verificación local utiliza datos en memoria y no modifica MySQL. Después de compilar se puede repetir desde la raíz del proyecto:

```powershell
java --class-path target/classes verificacion/VerificarResumenProductos.java
```

## Buscador público: provincia, distrito y zona (21/10/2026)

Desde Inicio > Comedores destacados > Explorar todos, el catálogo público presenta Provincia antes de Distrito, ambos como desplegables. Utiliza el mismo catálogo de ubicaciones que el administrador. La búsqueda inicial muestra todas las provincias; al elegir Lima o Callao se habilitan sus distritos. Se puede dejar Todos los distritos para consultar la provincia completa. Cambiar de provincia limpia el distrito anterior y Limpiar restablece todos los filtros.

Zona es el sector general guardado en cada comedor (por ejemplo, Norte, Sur, Este, Centro u Oeste). Es un filtro opcional de texto y ahora tiene una explicación visible. No se calcula automáticamente a partir del distrito.

Los resultados siguen consultándose por JDBC y solo incluyen comedores activos. Provincia y distrito se validan en el servidor; las consultas parametrizadas comparan la ubicación completa para evitar que buscar Lurigancho también incluya San Juan de Lurigancho. Se reconocen las denominaciones anteriores Lima/Cercado de Lima y Chosica/Lurigancho. Los filtros elegidos se conservan al mostrar los resultados.

Para comprobarlo: Clean and Build, Run y Ctrl+F5. Abrir Comedores, elegir Lima y Comas, buscar y revisar las selecciones; cambiar a Callao para comprobar que se limpia el distrito; pulsar Limpiar para volver al listado completo. No se necesita ejecutar SQL ni cambiar los datos existentes.

## Selección de provincia, distrito y responsable (21/10/2026)

El formulario Crear/Editar comedor ahora presenta **Provincia antes de Distrito**. En registros nuevos se preselecciona Lima. El distrito es un desplegable con los 43 distritos de la provincia de Lima. Callao se mantiene como provincia independiente, con sus siete distritos, porque el proyecto ya incluye el comedor Unidos por el Callao.

Al cambiar la provincia se actualizan los distritos y se limpia la selección anterior. Java vuelve a comprobar la combinación antes de guardar: no acepta, por ejemplo, Miraflores con provincia Callao, aunque se altere el formulario. Las ubicaciones existentes se conservan al editar si pertenecen al catálogo. Se admiten diferencias de mayúsculas/acentos, Lima como alias de Cercado de Lima y Chosica/Lurigancho-Chosica como alias de Lurigancho. Si una ubicación anterior queda fuera del alcance, el formulario solicita elegir una válida; no migra ni sobrescribe registros automáticamente.

**Responsable** es un campo de texto directo donde cada persona puede registrar al responsable del comedor (hasta 120 caracteres). Se conserva el responsable al editar y se almacena en la columna `comedores.responsable`.

Los responsables actuales son contactos de comedores, no cuentas ADMIN/DONADOR. Esta actualización conserva `responsable` como String/VARCHAR y no crea una relación con usuarios ni asigna permisos nuevos.

**Activación:** detener Tomcat, Clean and Build, Run y Ctrl+F5. No ejecutar migraciones SQL. La carga de fotografías sigue funcionando como antes.

**Comprobación manual:** crear un comedor seleccionando Lima y un distrito; escribir el nombre del responsable en el campo de texto; guardar y abrir Editar para comprobar que el valor se mantiene. Cambiar a Callao y verificar que el distrito anterior se limpia. Probar también el comedor existente de Callao.

Archivos afectados: AdminComedorServlet, ComedorDAO, nueva utilidad UbicacionComedor, formulario.jsp de comedores, main.js y versión del script en footer.jspf. La vista técnica existente del diagrama se actualiza; las entidades del negocio y el esquema SQL no cambian.

Fuentes del catálogo geográfico (consultadas el 21/10/2026): [lista de distritos publicada por la Embajada del Perú](https://www.peru-embajada.cz/files/lima-metropolitana.pdf) y [Municipalidad de Ventanilla, Plan de Acción de Seguridad Ciudadana 2023, sección de distritos de Callao](https://www.muniventanilla.gob.pe/contenidos-nfs/files/otrosEnlaces/codisec/PROPUESTA_DEL_PLAN_DE_ACCION_DISTRITAL_DE_SEGURIDAD_CIUDADANA_2023.pdf). El catálogo es local y no requiere Internet para funcionar.

## Últimos ajustes: fotos desde administración y portada (21/10/2026)

### Crear o editar un comedor con fotografía

1. Inicia sesión como ADMIN y entra a **Comedores > Crear comedor** o **Editar**.
2. Completa los datos y busca **Fotografía del comedor**, al final del formulario.
3. Selecciona un **JPG o PNG de hasta 5 MB**. Verás una vista previa antes de guardar.
4. Pulsa **Guardar comedor**. El sistema obtiene el ID asignado por MySQL y asocia la foto automáticamente: no tienes que renombrarla.
5. Consulta el catálogo o el detalle público. Si ese comedor está entre los tres destacados, también aparecerá en el inicio.

Si no eliges archivo, al editar se conserva la foto actual. Un comedor nuevo sin foto utiliza la ilustración predeterminada. Para quitar la foto visible, marca **Usar la imagen predeterminada** y guarda. Para reemplazarla después, selecciona otra fotografía.

Se conserva la protección ADMIN y CSRF. El servidor comprueba el contenido real de la imagen, limita sus dimensiones a 6000 píxeles por lado y 16 millones de píxeles en total, y la vuelve a guardar como PNG. No usa el nombre original como ruta. Si el formulario tiene errores, vuelve a seleccionar el archivo antes de reenviar.

### Carpeta de las imágenes subidas

Las nuevas fotos se guardan fuera del WAR, en **[carpeta del usuario que ejecuta Tomcat]/ComedorSolidarioDatos/comedores**. Si ejecutas NetBeans/Tomcat con tu usuario Diego, la ruta habitual es:

```text
C:\Users\diego\ComedorSolidarioDatos\comedores
```

La carpeta se crea al guardar la primera foto o al elegir explícitamente la predeterminada. El archivo usa el ID: por ejemplo, **7.png**. La variable de entorno opcional **CS_UPLOAD_DIR** permite elegir otra carpeta; debe establecerse antes de iniciar NetBeans/Tomcat. La cuenta que ejecuta Tomcat necesita permiso de escritura en ella.

Guardar una fotografía desde la web tiene efecto al recargar; **no necesita recompilar**. Las fotos sobreviven a Clean and Build porque están fuera de target y del despliegue. Un archivo PNG de cero bytes es la marca interna de elección de imagen predeterminada: no lo interpretes como una carga fallida ni lo reemplaces manualmente.

Las cinco fotos existentes **1.jpg a 5.jpg** de web/assets/img/comedores se siguen usando mientras no exista una elección guardada por el administrador. La prioridad es: fotografía subida o elección de predeterminada, foto incluida en el proyecto y finalmente ilustración de respaldo. El endpoint /foto-comedor?id=ID sirve la imagen y comprueba que el comedor exista; para visitantes exige que esté activo, mientras ADMIN puede consultar también los inactivos.

La aplicación guarda los archivos de imagen, no modifica el código Java ni hace commits. Para trasladar las fotografías subidas a otro equipo, respalda esta carpeta y la base de datos correspondiente; el push de Git no copia esa carpeta externa. Los archivos de un comedor eliminado no se sirven públicamente, aunque permanecen en el directorio de datos. Las fotos incluidas manualmente dentro del proyecto sí pueden versionarse con Git.

### Correcciones del inicio

- El título usa la misma familia tipográfica del sitio, sin la fuente serif ni cursiva anterior.
- El recuadro de comedores activos ocupa una fila debajo de la fotografía, con separación propia; ya no se posiciona encima de ella. El pie descriptivo permanece dentro de la foto.
- Se actualizaron las versiones de CSS y JavaScript para facilitar la recarga de estos cambios.

### Activar y verificar esta entrega

Detén Tomcat, ejecuta **Clean and Build > Run** y recarga con **Ctrl+F5**. Esta primera actualización de código sí requiere desplegar; las cargas de fotos posteriores se aplican directamente. **No se requiere migración SQL** ni cambiar las clases de negocio.

La implementación añade ImagenComedor y FotoComedorServlet, configura multipart en AdminComedorServlet y obtiene claves generadas en ComedorDAO/JdbcDAO. Se actualizaron el formulario, la etiqueta compartida de fotos, las validaciones de tamaño en el filtro, JavaScript y estilos. La vista técnica existente del diagrama se ajustó a los nuevos métodos y clases.

Verificación: Maven package y compilación Jasper de JSP completados correctamente; 21 aserciones aisladas de validación/almacenamiento de fotos y asociación del ID en verificacion/VerificarImagenComedor.java. Esta prueba usa una carpeta temporal y no toca MySQL ni fotografías reales. La prueba HTTP completa con tu Tomcat y la inspección visual siguen pendientes por el bloqueo de conexión local en la sesión de asistencia.

Para demostrarlo: crear un comedor con foto; editar sin adjuntar otra y comprobar que se conserva; reemplazarla; elegir la predeterminada; intentar adjuntar un archivo que no sea imagen o supere el límite. Finalmente, reiniciar/desplegar de nuevo y comprobar que la elección permanece.

## Actualización visual del 21/10/2026

Se mejoraron las plantillas existentes del inicio y de los dashboards administrativo y del donador. Se conserva Bootstrap, la navegación por roles, los formularios CRUD, la conexión JDBC y los datos reales del sistema.

- Inicio: portada con la imagen de comunidad existente, nueva composición, indicadores de MySQL, tarjetas con fotografía, pasos para donar y llamada a la acción.
- Administración: resumen de comedores, donadores y donaciones vigentes; barras proporcionales por estado; acceso a pendientes y a los seis módulos. No se muestran tendencias ni porcentajes inventados.
- Donador: resumen de sus aportes vigentes, seguimiento por estado y accesos a donar, buscar comedores y perfil.
- Fotos: una imagen por ID de comedor, compartida entre inicio, catálogo y detalle público. Sin imagen, se usa una ilustración genérica de respaldo.
- Diseño adaptable: ajustes para móvil y escritorio, foco de teclado, etiquetas de accesibilidad y respeto de la preferencia de movimiento reducido.

### Dónde guardar las fotos

Dentro del proyecto: **web/assets/img/comedores/**.

1. Entra a Administración > Comedores y consulta el ID del registro.
2. Guarda una fotografía horizontal con ese ID: por ejemplo, **3.jpg** para el comedor 3.
3. Se admiten extensiones minúsculas **jpg, jpeg, png y webp**, en ese orden de prioridad. Mantén una sola foto por ID. No basta con renombrar una extensión: exporta la imagen en el formato correcto.
4. Se recomienda 1200 x 750 píxeles y menos de 1 MB. Las tarjetas recortan el encuadre para mantener el mismo tamaño.
5. Detén Tomcat, ejecuta **Clean and Build**, después **Run** y recarga con **Ctrl+F5**.

La misma imagen aparecerá en el catálogo, en el detalle del comedor y en los destacados cuando el registro forme parte de esa selección. No edites imágenes dentro de target ni de la carpeta desplegada de Tomcat; se regeneran al compilar. No hace falta modificar SQL.

La foto principal del inicio es **web/assets/img/comunidad.png**. Puedes reemplazarla por otro PNG y volver a desplegar. Las instrucciones también están junto a las imágenes, en **web/assets/img/comedores/LEEME.md**.

### Archivos del rediseño

- web/index.jsp; web/views/admin/dashboard.jsp; web/views/donante/dashboard.jsp.
- web/views/common/cards-comedores.jspf y web/views/public/detalle-comedor.jsp.
- web/views/common/header.jspf: carga la nueva hoja de estilos.
- web/assets/css/refresh.css: amplía los estilos existentes.
- web/assets/js/main.js: respaldo de imágenes y señalización de la navegación actual; conserva la confirmación de acciones.
- web/WEB-INF/tags/fotoComedor.tag e icono.tag: componentes de presentación reutilizables.
- web/assets/img/iconos.svg y web/assets/img/comedores/sin-foto.svg: recursos locales.

### Verificación del rediseño

Maven package finalizó con BUILD SUCCESS. Las JSP y sus etiquetas se compilaron con Jasper de Tomcat: 0 errores. main.js pasó node --check. Los cambios de presentación no añaden columnas ni modifican las consultas JDBC.

No se pudo realizar la inspección visual en navegador ni ejecutar HTTP contra el Tomcat local desde la sesión de asistencia (acceso a socket bloqueado). Al desplegar, revisar inicio, ambos dashboards, menú móvil, tarjetas con y sin foto y detalle del comedor. Comprobar también el caso de cero donaciones: las barras deben permanecer vacías, sin división por cero.

Los cambios de configuración que ya existían en nbactions.xml y web/META-INF/context.xml no forman parte de este rediseño.

---

Actualizado el 20/10/2026. Esta guía reemplaza las instrucciones anteriores que llamaban "Eliminar" a una desactivación. El archivo leeme.md original se conserva.

## 1. Eliminar, desactivar y reactivar

Los seis módulos administrativos (Comedores, Necesidades, Usuarios, Donaciones, Productos y Campañas) tienen acciones distintas:

| Acción | Operación en MySQL | Resultado |
| --- | --- | --- |
| Eliminar | DELETE de la fila por ID | Desaparece definitivamente; no puede reactivarse. |
| Desactivar | UPDATE del estado o de activo | Conserva la fila y sus relaciones; queda inactiva. |
| Reactivar | UPDATE al estado activo | Recupera la vigencia de una fila conservada. |

En el listado aparecen Ver, Editar, Desactivar/Reactivar y Eliminar según los permisos y estado del registro. Eliminar pide confirmación explícita; Cancelar no envía el formulario. Las acciones se envían por POST, requieren sesión ADMIN y token CSRF.

La base conserva sus claves foráneas, sin borrado en cascada:
- Un comedor con necesidades, donaciones o campañas no puede eliminarse.
- Un producto referenciado por una necesidad no puede eliminarse.
- Un donador con donaciones no puede eliminarse.
- Desactivar un registro relacionado no elimina su relación: todavía puede impedir el borrado del registro padre.
- Las cuentas ADMIN están protegidas: el CRUD de Usuarios gestiona donadores; no elimina administradores.
- Si MySQL rechaza una relación, se muestra un mensaje y el registro permanece. No se transforma el borrado en una baja silenciosa.
- Si la cuenta MySQL carece de permiso DELETE, se informa para que el administrador del servidor revise los permisos sobre esta base.

En Donaciones se añadió la visualización administrativa de activas e inactivas, la columna Vigencia y Reactivar. El proceso PENDIENTE / ACEPTADA / ENTREGADA es independiente de la vigencia. Desactivar y reactivar no cambian la fecha de entrega ni el estado del proceso. Solo pueden editarse datos de donaciones activas y pendientes. Eliminar sí borra la donación, incluyendo su historial; los totales posteriores se calculan sin ella.

## 2. Qué cubren los seis CRUD

| Entidad | Crear y consultar | Actualizar | Eliminar y desactivar |
| --- | --- | --- | --- |
| Comedor | Formulario, listado y detalle | Datos del comedor | Borrado sin referencias; estado reversible |
| Necesidad | Formulario, listado y detalle | Cantidad, prioridad, datos y producto opcional | Borrado de la necesidad; estado reversible |
| Usuario | Crear DONADOR, listar y ver | Datos del donador | Borrado sin donaciones; estado reversible; ADMIN protegido |
| Donación | Crear a nombre de un donador, listar y ver | Datos pendientes; avance de entrega | Borrado físico; vigencia reversible |
| Producto | Crear, listar y ver | Datos del catálogo | Borrado sin necesidades vinculadas; estado reversible |
| Campaña | Crear, listar y ver | Comedor, contenido y fechas | Borrado físico; estado reversible |

Ya existían Comedores y Necesidades; se completaron Usuarios y Donaciones y se añadieron Productos y Campañas. No se agregaron seis módulos nuevos.

Productos permite referenciar recursos solicitados por las necesidades. No es un sistema de stock: cambiar un producto no reescribe el nombre, descripción ni unidad guardados en necesidades anteriores.

Campañas organiza convocatorias de un comedor con fechas de inicio y fin. Una campaña pública debe estar activa, pertenecer a un comedor activo y estar dentro de sus fechas, inclusive. No se vincularon campañas a donaciones ni se implementaron metas de recaudación.

Los seis CRUD administrativos son exclusivos de ADMIN. Se conservan el registro propio, perfil y aportes propios del DONADOR.

## 3. Ejecutar esta actualización en NetBeans

Si tu aplicación ya funciona y aplicaste la ampliación anterior, esta actualización NO requiere migración SQL.

1. Abre ComedorSolidario-yoel, la carpeta que contiene pom.xml.
2. Detén Tomcat desde Servicios > Servidores en NetBeans.
3. Ejecuta Clean and Build (Limpiar y construir), y luego Run (Ejecutar).
4. Usa JDK 21 o superior y Tomcat 10.1. El proyecto compila con release 21 y Jakarta Servlet 6.0.
5. Abre http://localhost:8080/ComedorSolidario/inicio, ajustando puerto/contexto a tu servidor.
6. Inicia sesión con tu cuenta ADMIN de la aplicación y recarga con Ctrl+F5.
7. Abre un listado: ahora deben verse Eliminar y Desactivar como acciones separadas. En una fila inactiva, Desactivar cambia a Reactivar; Eliminar sigue siendo un borrado.

En consola, desde la raíz del proyecto: mvn clean package. El WAR generado es target/ComedorSolidario.war.

### Configuración de una instalación que aún no tiene la ampliación

- Base existente: respaldar y ejecutar database/migracion_rubrica.sql una sola vez. Crea productos y campañas; añade necesidades.producto_id y donaciones.activo.
- Instalación nueva: ejecutar solamente database/comedor_solidario.sql; ya contiene la ampliación. No ejecutar después la migración.
- No repetir los ALTER TABLE ni reinicializar una base con datos para actualizar los botones.
- ConexionBD usa CS_DB_URL, CS_DB_USER y CS_DB_PASSWORD o src/resources/db.properties con db.url, db.user y db.password. Las variables de entorno tienen prioridad. El archivo .example no se carga automáticamente.
- Mantener las credenciales locales fuera de Git. La cuenta MySQL y la cuenta ADMIN de la web son cuentas distintas.

## 4. Integración JDBC de la web no administrativa

Esta parte significa que las páginas del visitante leen información real de MySQL, incluso sin iniciar sesión como administrador.

| Página | Información consultada | Código responsable |
| --- | --- | --- |
| /inicio | Comedores destacados activos, total de comedores activos, total de donadores y resumen de donaciones vigentes | InicioServlet; ComedorDAO, UsuarioDAO y DonacionDAO |
| /comedores | Comedores activos y filtros por zona, distrito y provincia | ComedorServlet; ComedorDAO.listar |
| /comedores/detalle?id=... | Datos de un comedor activo y sus necesidades activas | ComedorServlet; ComedorDAO.buscar y NecesidadDAO.listar |
| Mismo detalle público | Campañas activas, dentro de fechas, de ese comedor activo | CampaniaDAO.listarPublicas |
| Panel y donaciones del DONADOR | Resumen y listado de sus propios aportes vigentes | DonadorDashboardServlet, DonacionServlet y DonacionDAO |

La consulta pública de comedores, necesidades e indicadores ya existía: se conservó y revisó. La ampliación incorporó las campañas al detalle público y el filtrado de donaciones inactivas en listados del donador y resúmenes.

Recorrido real: navegador > servlet > DAO > ConexionBD/MySQL > objetos del modelo > atributos de la petición > JSP. Las JSP recorren resultados con JSTL y muestran textos con c:out; los registros no se escriben a mano en HTML. Los títulos y etiquetas de navegación sí pueden ser estáticos.

### Conexión, parámetros y recursos

- util/ConexionBD.java centraliza la configuración y obtiene conexiones JDBC.
- dao/JdbcDAO.java prepara las consultas con Connection.prepareStatement.
- Los valores del visitante (ID, filtros, etc.) se envían con PreparedStatement.setObject, usando marcadores ?, sin concatenar esos valores al SQL.
- Connection, PreparedStatement y ResultSet se cierran con try-with-resources, incluso ante errores.
- CampaniaDAO.listarPublicas usa un ID parametrizado, JOIN con comedores, estados activos y CURRENT_DATE BETWEEN fecha_inicio AND fecha_fin.
- El total de donadores actual cuenta cuentas DONADOR; no aplica filtro por estado. Los resúmenes de donaciones sí excluyen aportes inactivos.

### Demostración para el profesor

1. Desde ADMIN, crea una campaña de prueba en un comedor activo, con fechas que incluyan hoy.
2. Abre una ventana privada sin sesión y entra al detalle público de ese comedor.
3. Verifica que aparece el título guardado en MySQL.
4. Edita el título desde ADMIN y recarga la página pública: debe reflejar el cambio sin editar JSP.
5. Desactiva la campaña: deja de mostrarse. Reactívala: vuelve a mostrarse si sigue dentro de fechas.
6. Elimínala definitivamente: desaparece del administrador y de MySQL.
7. Prueba los filtros de comedores y el detalle de necesidades. Esto demuestra JDBC también fuera del panel administrativo.

## 5. Modelo y coherencia con SQL

Se mantienen seis clases de negocio encapsuladas: Usuario, Comedor, Necesidad, Donacion, Producto y Campania, organizadas junto a paquetes dao, controller, util y filter.

| Relación | Implementación |
| --- | --- |
| Comedor a necesidades, donaciones y campañas | Un comedor puede tener 0..* de cada una; cada registro hijo tiene un comedor obligatorio |
| Usuario a donaciones | Un donador puede tener 0..* aportes; cada donación tiene un donador obligatorio |
| Producto a necesidades | Un producto puede ser referenciado por 0..* necesidades; cada necesidad tiene 0..1 producto |

Las asociaciones se representan mediante IDs y claves foráneas, no mediante colecciones inexistentes. Los nombres de comedor y donador en Donacion provienen de JOIN. Los atributos privados y accesores del modelo no cambian en esta actualización.

El diagrama de entidades existente sigue describiendo esas clases. No se creó otro archivo PlantUML en esta actualización. Se actualizó únicamente la vista técnica ya existente docs/clases-completas.puml con eliminar(int): void en los seis DAO, eliminarRegistro(...) en JdbcDAO y listarAdministracion(String) / cambiarVigencia(int, boolean) en DonacionDAO, para mantener su correspondencia con Java.

## 6. Responsabilidad individual

Son seis entidades para seis integrantes. El equipo debe acordar y consignar el nombre de quien desarrolla, valida y explica cada CRUD; una distribución propuesta no demuestra autoría ni reemplaza la evaluación individual.

| Integrante propuesto | CRUD |
| --- | --- |
| De Fina Maldonado Flavio Alonso | Comedores |
| Juarez Zapata Diego Humberto | Donaciones |
| Rujel Dios Julio César | Usuarios / donadores |
| Yovera Villegas Franky Yoel | Necesidades |
| Vidal Vega Didier Fabricio | Productos |
| Zevallos Davila Jennifer | Campañas |

La asignación anterior es propuesta, pendiente de confirmación del equipo. Cada integrante debe demostrar crear, listar, consultar un detalle, actualizar y eliminar un registro propio de prueba.

## 7. Archivos de esta actualización

Las rutas Java siguientes están bajo src/java/com/comedorsolidario/:

- dao/JdbcDAO.java: ejecución común de borrado, rechazo por referencias, ID inválido, fila inexistente/protegida y falta de permiso DELETE.
- Los seis DAO: método eliminar(int) con DELETE parametrizado; UsuarioDAO restringe el borrado a DONADOR.
- dao/DonacionDAO.java: listado administrativo de activas/inactivas y cambiarVigencia.
- Los seis controladores administrativos: acción eliminar y mensajes; AdminDonacionServlet también gestiona vigencia y consulta de inactivas.
- web/views/admin/*/listar.jsp: botones separados y confirmación irreversible.
- Detalles de usuarios, productos, campañas y donaciones: acciones diferenciadas; donaciones muestra vigencia.
- web/views/common/header.jspf: mensajes de rechazo de la acción.
- LEEME_RUBRICA.md y docs/VERIFICACION.md: instrucciones actualizadas.
- verificacion/VerificarEliminacion.java: comprobaciones aisladas del SQL de borrado y manejo de rechazos.

No cambia el esquema SQL por esta separación. Los botones siguen utilizando la confirmación compartida ya existente.

## 8. Qué está verificado y qué falta demostrar

- Maven package: BUILD SUCCESS; WAR generado.
- VerificarRubrica.java: 343 aserciones correctas de modelo/encapsulamiento/validación.
- VerificarEliminacion.java: 33 aserciones correctas, interceptando el SQL sin conectar a MySQL.
- Sintaxis JavaScript revisada con node --check.
- Jasper de Tomcat compiló las JSP con 0 errores; detalles en docs/VERIFICACION.md.

No se ejecutaron los DELETE sobre tu base ni se verificaron los seis recorridos HTTP/JDBC desde esta sesión. La conexión real y la demostración en NetBeans/Tomcat deben comprobarse en tu entorno. El usuario informó que resolvió MySQL; eso no sustituye probar esta nueva eliminación.

El código cubre las operaciones y mecanismos solicitados por la rúbrica; la calificación requiere la aplicación funcionando, el diagrama que se entregue coherente y la demostración individual. No se afirma una nota ni cumplimiento máximo sin esas comprobaciones.
