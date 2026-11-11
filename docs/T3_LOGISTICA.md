# T3: confianza y logística de Comedor Solidario

## Alcance y arquitectura
Se amplía el proyecto Maven WAR existente. Continúan los seis módulos originales, sus Servlets, JSP y la utilidad ConexionBD. Visitante es acceso sin sesión; las cuentas siguen teniendo solamente ADMIN o DONADOR. Los responsables de acopio y recojo son nombres, no cuentas nuevas.

Spring MVC 6.2.19 se utiliza realmente en LogisticaController, configurado por LogisticaConfig y un DispatcherServlet con rutas /admin/logistica/*, /donante/logistica/* y /acopio/*. El controlador recibe LogisticaService por constructor. Los Servlets conservados invocan el mismo servicio para registrar donaciones y gestionar cuentas. La lógica de estados está en el servicio; la persistencia transaccional está en LogisticaDAO y los DAO de cada entidad. Todas las consultas reciben parámetros. No hay SQL en JSP ni en controladores.

JdbcDAO reutiliza exclusivamente ConexionBD; cada operación compuesta utiliza una conexión, commit y rollback, PreparedStatement y cierre de recursos. No se introduce Spring Boot, JPA ni otra configuración de base. No se modifican las credenciales. Spring 6.2 conserva compatibilidad con Servlet 6 y Tomcat 10.1; no se migró a Spring 7 porque exige una plataforma Servlet más reciente. Esta rama 6.2 ya terminó su soporte comunitario; para un despliegue público futuro habrá que planificar actualización, fuera de este alcance local.

Dependencia Maven directa nueva: org.springframework:spring-webmvc:6.2.19. Spring Context, Beans, Core, Expression, AOP, Web, JCL y Micrometer Observation/Commons llegan transitivamente. No son APIs externas ni servicios de pago. La compilación conserva release 21; el equipo inspeccionado usa JDK 25.0.4.1, Maven 3.9.16, NetBeans 31, Tomcat 10.1.60 y MySQL 8.4.10.

## Uso de los módulos
### Confianza
Registro y perfil requieren nombre, apellido, correo válido y teléfono de 7 a 15 dígitos, con prefijo + opcional. El correo conserva su restricción UNIQUE. Un donador puede tener como máximo dos aportes activos en PENDIENTE, ACEPTADA o PROGRAMADA. La cuenta se bloquea para escritura durante el conteo y alta; dos peticiones simultáneas no eluden el límite. La reactivación y la reasignación de aportes pendientes también comprueban el cupo. Los registros anteriores no se borran ni se corrigen automáticamente; si ya superan el límite deben completarse antes de registrar más.

El donador consulta Historial, incluyendo aportes inactivos y sus motivos. El administrador accede desde Usuarios > Ver > Historial o desde Confianza. Dos donaciones distintas con INCUMPLIDA generan alerta; no se suspende automáticamente a nadie. La desactivación/reactivación exige motivo y registra administrador y fecha. Rechazos e incumplimientos se conservan aunque se desactive el aporte. Los registros con auditoría o logística relacionada no se pueden borrar por las claves foráneas; sigue disponible desactivar.

### Modalidades
DIRECTA mantiene la entrega personal al comedor y el avance PENDIENTE -> ACEPTADA -> ENTREGADA por ADMIN. ACOPIO requiere un punto activo, conserva el comedor destinatario y sigue el mismo avance: dejar algo en el punto no significa recibido por el comedor. RECOJO registra la donación y su solicitud en una sola transacción. El teléfono se propone desde el perfil y puede cambiarse solo para esa solicitud.

### Puntos de acopio
Administración permite alta, lista, detalle, edición, activar/desactivar y borrar si no hay donaciones relacionadas. Se guardan nombre, dirección, provincia, distrito, zona, referencia, horarios, responsable y teléfono. El catálogo público presenta solo puntos activos con sus características. El donador selecciona un punto al registrar el aporte. La vigencia se vuelve a comprobar al guardar.

### Solicitudes de recojo
El donador ve solo sus solicitudes; ADMIN ve todas y filtra por estado/distrito. Aceptar actualiza la donación a ACEPTADA y mantiene SOLICITADO mientras se coordina. Programar exige fecha no pasada, horario y responsable; cambia la solicitud a PROGRAMADO y la donación a PROGRAMADA. Confirmar recogido cambia a RECOGIDO sin marcar entrega. Confirmar recibido en el comedor cambia a COMPLETADO y recién entonces ENTREGADA con fechaEntrega. Rechazar/cancelar/incumplir exige motivo antes de recoger; se puede adjuntar una recomendación de acopio activo. La solicitud y la donación conservan estados separados, actualizados atómicamente. Un donador no puede confirmar ninguna de estas acciones, ni cambiando la URL.

### Rutas
ADMIN crea una ruta con fecha, distrito principal, ventana horaria, responsable y observaciones. Puede elegir recojos programados de esa fecha y responsable; se muestra dirección y disponibilidad. Debe confirmar manualmente la compatibilidad de horarios y autorizar explícitamente otros distritos. La posición de cada parada es un número positivo libre; para intercambiar posiciones se puede usar una posición temporal. Solo se agregan/quitan/reordenan paradas en PLANIFICADA. EN_PROCESO inicia el recorrido; COMPLETADA requiere que cada solicitud haya llegado al comedor o tenga un cierre registrado. Cancelar la ruta libera sus asignaciones, conserva el historial y no cancela por sí solo las solicitudes. No hay optimización geográfica, mapa, GPS, SMS ni verificación de DNI.

## Migración manual y ejecución en NetBeans
El procedimiento detallado de Workbench, NetBeans y los casos de aceptación está en [EJECUCION_Y_PRUEBAS_MANUALES_T3.md](EJECUCION_Y_PRUEBAS_MANUALES_T3.md).

1. Detener la aplicación/Tomcat habitual. Hacer una exportación de respaldo en Workbench.
2. Abrir database/migracion_t3_logistica.sql en Workbench, conectado a la base habitual con una cuenta que pueda modificar su estructura. Ejecutar el archivo completo una vez. Sus guardas permiten repetirlo sin duplicar columnas/tablas. DDL hace commit implícito; si falla, corregir permisos y reanudar, no reinstalar la base.
3. No ejecutar comedor_solidario.sql ni migracion_rubrica.sql sobre la base existente. La migración T3 presupone las seis entidades y donaciones.activo/necesidades.producto_id de la T2, comprobadas en este equipo.
4. La cuenta de la aplicación necesita SELECT, INSERT, UPDATE y DELETE sobre las tablas nuevas; no necesita CREATE ni ALTER. Si sus permisos ya abarcan comedor_solidario.*, no hay que cambiar credenciales.
5. Abrir ComedorSolidario-yoel como proyecto Maven en NetBeans. Conservar el servidor Apache Tomcat 10.1 y seleccionar un JDK 21 o superior. Confirmar el contexto /ComedorSolidario en Propiedades > Run.
6. Ejecutar Clean and Build (Shift+F11), equivalente a mvn clean package. La primera compilación necesita descargar Spring desde Maven Central.
7. Ejecutar Run (F6), abrir /ComedorSolidario/inicio y recargar con Ctrl+F5. Usar las cuentas ya existentes; no se crean administradores automáticamente.
8. Crear un punto de prueba, registrar una donación en cada modalidad y coordinar un recojo/ruta. El donador con teléfono antiguo vacío debe completarlo en Mi perfil.

La migración de la base habitual NO se ejecuta desde el arranque ni desde Java. Antes de la migración, la versión T3 no puede consultar las columnas/tablas nuevas; debe aplicarse antes de poner esta versión en uso.

## Verificación y pruebas aisladas
Consultar VERIFICACION_T3.md para resultados exactos. VerificarLogistica.java contiene una guarda que impide usarlo fuera de 127.0.0.1:3308. La instancia temporal utiliza datos ficticios y no es la base habitual. Las variables CS_DB_* solo se usan en el proceso de prueba, sin cambiar db.properties. verificar_http_t3.py usa exclusivamente localhost:18080.

Los diagramas existentes modelo-clases.puml y clases-completas.puml se ampliaron; no se sustituyeron las entidades previas. El segundo incluye Spring, el servicio, DAO y notas de auditoría.

El esquema final de las doce tablas, claves y relaciones se encuentra en [base-datos-t3.puml](base-datos-t3.puml), con [vista SVG](base-datos-t3.svg). Se obtiene del esquema T2 y la migración definitiva T3; no representa una migración ya aplicada a la base habitual.

Referencias de compatibilidad: https://docs.spring.io/spring-framework/reference/6.2/overview.html y https://github.com/spring-projects/spring-framework/wiki/Spring-Framework-Versions .
