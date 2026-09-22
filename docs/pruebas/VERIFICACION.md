# Verificación final de Comedor Solidario

## Entorno utilizado

- JDK 21 (Microsoft OpenJDK 21.0.12.1).
- Maven 3.9.9; empaquetado WAR; Servlet API Jakarta 6.0.
- Apache Tomcat 10.1.60.
- MySQL Community Server 8.4.11, instancia local de pruebas.
- MySQL Connector/J 8.4.0.
- Chromium 134 para las comprobaciones de navegador.

## Resultados ejecutados

- **Clean and Build: BUILD SUCCESS.** Se compilaron las clases Java y los imports/dependencias.
- **3 pruebas JUnit aprobadas**, sin fallos ni errores: hashes y sal, cantidades e identificadores/opciones.
- **29 comprobaciones HTTP funcionales aprobadas** contra Tomcat y MySQL reales; detalle en `funcional.json`.
- **18 comprobaciones de interfaz aprobadas**: anchos 1440, 768 y 390; sin desbordamiento horizontal del documento; tablas con desplazamiento interno; Bootstrap, imágenes, menú móvil y modal. Sin errores JavaScript. Detalle en `visual.json`.
- Script SQL ejecutado con creación de tablas, relaciones y datos. El SQL de entrega conserva únicamente los datos de demostración originales; no incorpora las modificaciones de las pruebas.
- Se revisaron capturas de inicio y CRUD, y se incluyeron las pantallas de la ejecución en `docs/capturas/`.

## Lista de comprobación

| Elemento | Resultado y evidencia |
|---|---|
| index.jsp | HTTP 200 a inicio y acceso directo con redirección al controlador; datos reales |
| Login | Cuentas ADMIN y DONADOR verificadas; credenciales incorrectas rechazadas |
| Registro | Usuario nuevo creado; correo repetido rechazado; rol ADMIN enviado por cliente ignorado |
| Logout y sesión | Sesión invalidada; ruta protegida vuelve al login |
| Roles | DONADOR recibe 403 al leer o modificar administración |
| CRUD comedores | Crear, ver, editar, desactivar y reactivar ejecutados |
| Necesidades | Registrar, editar, desactivar y reactivar; visibilidad en detalle público |
| Donaciones | Inserción, propietario de sesión, cantidad y comedor válidos |
| Estados | PENDIENTE → ACEPTADA → ENTREGADA y fecha; actualización antigua no avanza otro paso |
| Mis donaciones | Cambios visibles; cuenta diferente no puede consultar aportes ajenos |
| Buscador | Zona, distrito y provincia combinados |
| JDBC y MySQL | SQL de DAO ejecutado y persistencia comprobada durante los flujos |
| JavaBeans | Compilación y acceso a propiedades desde JSP mediante EL |
| Servlets y JSP | Rutas públicas, donador y administrador renderizadas en Tomcat |
| GET/POST | Lecturas y formularios de escritura probados; CSRF ausente rechazado |
| Validaciones | Cantidad cero, comedor inexistente, correo repetido, estados inválidos |
| Escape HTML | Texto con etiquetas en perfil mostrado escapado, no ejecutado |
| Bootstrap | Archivos locales cargados; menú responsive y modal operativos |
| Responsive | Inicio, paneles, formularios y tablas revisados en tamaños seleccionados |
| Enlaces | Enlaces principales emitidos por las páginas recorridos sin errores HTTP |
| Rutas JSP directas | Acceso externo a /views bloqueado; forward de Servlet permitido |
| README | Instalación, JDBC, NetBeans, Tomcat, MySQL, cuentas y prueba completa documentados |

## Correcciones realizadas durante la revisión

1. Se corrigieron comillas de expresiones EL en los formularios de comedor, necesidad y perfil que ocasionaban un error de compilación JSP.
2. Se declaró UTF-8 en los fragmentos `.jspf` para corregir tildes y textos de accesibilidad de navbar/footer.
3. Se ajustó la prueba de destacados para comprobar un comedor realmente incluido entre los tres primeros del orden alfabético.

## Alcance de la evidencia

Se probó el despliegue del WAR en Tomcat. **No se ejecutó la interfaz gráfica de NetBeans ni el instalador de Windows** en este entorno; la apertura como proyecto Maven y los pasos de configuración están documentados. La compatibilidad de compilación y servidor sí se comprobó.

Las pruebas responsive corresponden a Chromium en los tamaños indicados, no a todos los dispositivos ni a una certificación WCAG. La prueba del modal verificó apertura/cancelación; las escrituras se verificaron por HTTP.

Las capturas pueden mostrar registros creados durante las pruebas. La instalación nueva utiliza las cuatro cuentas, cinco comedores, diez necesidades y cinco donaciones del SQL original.

## Reproducción

1. Importa el SQL en una base de pruebas y configura `db.properties`.
2. Ejecuta `mvn clean package` con JDK 21.
3. Despliega en Tomcat 10.1.
4. Ejecuta `python docs/pruebas/prueba_http.py http://localhost:8080/ComedorSolidario` (requiere Python 3 solo para esta prueba opcional).
5. La prueba crea datos en esa instancia: usar solamente la base de demostración.
6. El runtime de la aplicación no necesita Python ni herramientas de navegador automatizado.
