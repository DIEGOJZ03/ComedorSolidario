# Comedor Solidario

Aplicación universitaria para **Soluciones Web y Aplicaciones Distribuidas**.
Java Web MVC: JSP + Servlets + JavaBeans + DAO + JDBC + MySQL. Dos roles: ADMIN y DONADOR.

## Verificación de esta entrega

Compilación correcta, 3 pruebas unitarias, 29 comprobaciones funcionales con Tomcat/MySQL y 18 comprobaciones de interfaz. Consulta `docs/pruebas/VERIFICACION.md` para la evidencia y sus límites. La interfaz gráfica de NetBeans no se ejecutó en el entorno de pruebas.

Se incluye `dist/ComedorSolidario.war`, construido con el código entregado. Para empezar, sigue la compilación desde NetBeans y configura `db.properties` como se indica abajo. El WAR precompilado no contiene tus credenciales: si lo usas directamente, configura `CS_DB_URL`, `CS_DB_USER` y `CS_DB_PASSWORD` en el entorno de Tomcat antes de iniciarlo.

## 1. Qué contiene y qué hace

- Inicio responsive, búsqueda combinada por zona/distrito/provincia y necesidades por comedor.
- Registro, login, logout POST, sesiones y autorización en Java.
- Dashboard del donador, perfil editable, registro y seguimiento de sus donaciones.
- Dashboard administrativo; CRUD de comedores con desactivación/reactivación; gestión de necesidades; consulta de usuarios.
- Donaciones: PENDIENTE → ACEPTADA → ENTREGADA, sin saltos ni retrocesos.
- Contraseñas PBKDF2, SQL parametrizado, validación backend, escape HTML y tokens CSRF.
- Bootstrap local: la interfaz no depende de Internet al ejecutarse.
- `docs/`: arquitectura, diagramas, casos de uso, contenido de 12 diapositivas y verificación.

Registrar DINERO solo registra el compromiso en soles. No hay pasarela ni cobro. Las entregas se coordinan físicamente y las confirma el administrador. Las cantidades de necesidades se actualizan manualmente; no se descuentan automáticamente al entregar una donación.
Todos los comedores, responsables, direcciones y teléfonos del SQL son ficticios.

## 2. Programas que debes instalar (Windows)

1. **JDK 21**, no solo JRE: [Microsoft Build of OpenJDK](https://learn.microsoft.com/java/openjdk/download) u otra distribución de JDK 21.
2. **Apache NetBeans** con soporte Java Web y Maven: [descargas oficiales](https://netbeans.apache.org/front/main/download/). Si la versión del IDE solicita un JDK más reciente para arrancar, úsalo para el IDE y registra JDK 21 como plataforma del proyecto.
3. **Apache Tomcat 10.1.x**, distribución Core ZIP: [descarga oficial](https://tomcat.apache.org/download-10.cgi). No Tomcat 9, 10.0 ni 11 para esta configuración.
4. **MySQL Community Server 8.4 LTS** y opcionalmente **MySQL Workbench**: [MySQL](https://dev.mysql.com/downloads/). Workbench es un cliente: por sí solo no instala el servidor.
5. **VS Code**, opcional, para editar CSS, JavaScript y revisar HTML.

Maven integrado en NetBeans descarga Servlet API, JSTL y Connector/J según `pom.xml`. Para compilar desde una terminal puedes instalar Maven 3.9.x. Se requiere Internet en la primera compilación.

## 3. Configurar Java/JDK

1. Instala JDK 21 en una ruta como `C:\Program Files\Microsoft\jdk-21...`.
2. En Windows busca **Editar las variables de entorno del sistema** → **Variables de entorno**.
3. Crea `JAVA_HOME` con la carpeta del JDK (sin `\bin`).
4. Agrega `%JAVA_HOME%\bin` a `Path`.
5. Cierra y abre una terminal. Ejecuta `java -version` y `javac -version`. Ambos deben indicar 21 para este proyecto.
6. En NetBeans: **Tools → Java Platforms → Add Platform → Java Standard Edition**. Selecciona la carpeta de JDK 21.

El `pom.xml` compila con `release=21`. El servidor Tomcat también debe ejecutarse con JDK 21.

## 4. Descomprimir y abrir el proyecto

1. Extrae `ComedorSolidario.zip`, por ejemplo en `C:\Proyectos`.
2. Debe existir `C:\Proyectos\ComedorSolidario\pom.xml`.
3. En NetBeans: **File → Open Project**. Selecciona la carpeta que contiene ese `pom.xml` y pulsa **Open Project**.
4. Espera a que Maven descargue dependencias. No crees un segundo proyecto ni pegues los archivos dentro de otro.
5. Clic derecho al proyecto → **Properties**: selecciona JDK 21 cuando la opción de plataforma esté disponible.
6. Las carpetas físicas son `src/java` y `web`. Maven las configura expresamente; NetBeans puede mostrarlas como **Source Packages** y **Web Pages**. En la pestaña **Files** puedes ver las rutas reales.

## 5. Instalar y registrar Tomcat en NetBeans

1. Extrae el Core ZIP de Tomcat en `C:\Servidores\apache-tomcat-10.1.x`.
2. Comprueba que existan `bin`, `conf`, `lib` y `webapps`.
3. En NetBeans, activa **Tools → Plugins → Installed → Java Web and EE** si el soporte web no aparece. Según la distribución puede ser necesario instalarlo desde **Available Plugins**.
4. Abre **Window → Services**. En **Servers**, clic derecho → **Add Server**.
5. Selecciona **Apache Tomcat or TomEE** y coloca un nombre, por ejemplo `Tomcat 10.1`.
6. Elige la carpeta de Tomcat. Selecciona JDK 21 en sus propiedades de plataforma Java.
7. Si el asistente pide usuario y contraseña para administrar el servidor, crea un usuario LOCAL de despliegue. No es el usuario administrador del sistema Comedor Solidario.
8. Para configuración manual, agrega dentro de `<tomcat-users>` de `conf/tomcat-users.xml`:

```xml
<role rolename="manager-script"/>
<user username="netbeans" password="ELIGE_UNA_CLAVE_LOCAL" roles="manager-script"/>
```

9. Usa esas credenciales en las propiedades del servidor de NetBeans y mantenlo accesible solo localmente. No es necesario abrir puertos de Internet.
10. Clic derecho al proyecto → **Properties → Run**: servidor Tomcat 10.1, contexto `/ComedorSolidario`, URL relativa `/`.
11. Si tu NetBeans no ofrece Tomcat, comprueba el módulo Java Web y actualiza a una distribución compatible. También puedes desplegar el WAR manualmente como se explica más abajo.

## 6. Instalar/configurar MySQL

1. Ejecuta el instalador de MySQL Community Server 8.4 para Windows.
2. Configura una instalación de desarrollo local, puerto **3306** y servicio Windows iniciado automáticamente.
3. Establece y guarda la contraseña de `root` de MySQL. Es distinta de cualquier cuenta de la aplicación.
4. Abre MySQL Workbench → crea conexión a `127.0.0.1`, puerto 3306, usuario `root`.
5. Pulsa **Test Connection**, introduce la contraseña y abre la conexión.
6. Si ya tienes un servidor en 3307, usa ese puerto tanto en Workbench como en JDBC. No cambies el puerto de otro proyecto sin necesidad.
7. Esta entrega se orienta a **MySQL 8.4**. XAMPP puede incluir MariaDB; no asumas que es la misma versión del servidor.

## 7. Crear la base e importar el SQL

1. En Workbench: **File → Open SQL Script**.
2. Abre `database/comedor_solidario.sql`.
3. Ejecuta TODO el archivo con el botón del rayo (sin limitarte a una selección).
4. El propio script crea `comedor_solidario`, selecciona la base, crea las cuatro tablas e inserta datos de prueba.
5. En **Schemas**, pulsa actualizar. Abre `comedor_solidario → Tables`.
6. Verifica que existan `usuarios`, `comedores`, `necesidades` y `donaciones`.
7. Ejecuta `SELECT COUNT(*) FROM comedor_solidario.comedores;`: inicialmente devuelve 5.
8. Importa una sola vez. El script no hace DROP ni sobrescribe una base existente. Si aparece “Table already exists”, verifica que ya se importó; no elimines datos de trabajos previos.

Alternativa con cliente de línea de comandos: abre `mysql -u root -p`; dentro del cliente ejecuta `SOURCE C:/Proyectos/ComedorSolidario/database/comedor_solidario.sql;`.

Crea un usuario de base de datos específico, después de importar:

```sql
CREATE USER 'comedor_app'@'localhost' IDENTIFIED BY 'ELIGE_UNA_CLAVE_MYSQL';
GRANT SELECT, INSERT, UPDATE, DELETE ON comedor_solidario.* TO 'comedor_app'@'localhost';
```

Este usuario se utiliza por JDBC. **No sirve para iniciar sesión en la página web.** Si ya existe, usa su contraseña conocida o actualízala conscientemente con `ALTER USER`; no vuelvas a ejecutar `CREATE USER` sobre una cuenta existente.

## 8. Configurar JDBC y Connector/J

1. Copia `src/resources/db.properties.example` como `src/resources/db.properties`.
2. Edita la copia:

```properties
db.url=jdbc:mysql://localhost:3306/comedor_solidario?sslMode=DISABLED&allowPublicKeyRetrieval=true&connectionTimeZone=America/Lima
db.user=comedor_app
db.password=ELIGE_UNA_CLAVE_MYSQL
```

3. Cambia puerto, usuario y contraseña por los configurados en tu MySQL. No incluyas comillas alrededor de los valores.
4. Si la contraseña contiene `\`, representa esa barra como `\\` en el archivo `.properties`.
5. `ConexionBD.java` carga ese archivo y utiliza `DriverManager.getConnection`. No es necesario modificar todos los DAO ni poner contraseñas en los Servlets.
6. Alternativa: define `CS_DB_URL`, `CS_DB_USER` y `CS_DB_PASSWORD` en el entorno del proceso Tomcat. Tienen prioridad sobre `db.properties`.
7. Después de cambiar `db.properties`, ejecuta **Clean and Build** y vuelve a desplegar. Si cambias variables del entorno, reinicia NetBeans/Tomcat para que las hereden.
8. Connector/J ya está declarado en `pom.xml`: `com.mysql:mysql-connector-j:8.4.0`. NetBeans/Maven lo descarga y lo empaqueta en `WEB-INF/lib` del WAR.
9. En **Dependencies** debe verse `mysql-connector-j`. No agregues otra copia manual al servidor: produciría duplicados innecesarios.
10. Servlet API tiene alcance `provided`: Tomcat la suministra. JSTL compatible con Jakarta sí se incluye en el WAR.

`sslMode=DISABLED` y `allowPublicKeyRetrieval=true` se usan aquí para MySQL en tu equipo. Si se despliega en otro servidor, configura TLS y una URL adecuada. No publiques `db.properties` con credenciales reales.

## 9. Clean and Build y ejecución

1. Guarda los cambios.
2. En NetBeans, clic derecho al proyecto → **Clean and Build**.
3. Espera `BUILD SUCCESS`. Las pruebas unitarias se ejecutan durante la compilación.
4. Se genera `target/ComedorSolidario.war`.
5. Comprueba que el servicio de MySQL esté iniciado.
6. Clic derecho al proyecto → **Run**. Selecciona Tomcat 10.1 si NetBeans lo pregunta.
7. Abre **http://localhost:8080/ComedorSolidario/**.
8. Debes ver el inicio con estadísticas y comedores destacados.
9. Si Tomcat está en otro puerto, sustituye 8080. El contexto distingue mayúsculas.

En terminal con Maven instalado: `mvn clean package` desde la carpeta del proyecto.

### Despliegue manual del WAR

Si prefieres ejecutar sin la integración del IDE: detén Tomcat, copia `target/ComedorSolidario.war` a su carpeta `webapps`, inicia `bin/startup.bat` y abre la misma URL. Para volver a desplegar, usa el mecanismo de despliegue de Tomcat o sustituye el WAR con Tomcat detenido. No edites directamente los archivos de la carpeta expandida: el siguiente despliegue los reemplazará.

## 10. Cuentas web de prueba

| Rol | Correo | Contraseña |
|---|---|---|
| Administrador | admin@comedorsolidario.pe | Admin2026! |
| Donador principal | donador@comedorsolidario.pe | Donador2026! |
| Donadora | lucia@example.com | Donador2026! |
| Donador | carlos@example.com | Donador2026! |

Son cuentas de demostración. El SQL contiene hashes PBKDF2 con sal distinta para cada usuario, no contraseñas en texto plano. No uses estas claves en un despliegue público.

## 11. Probar el administrador y el CRUD

1. Abre **Iniciar sesión**. Ingresa la cuenta administradora.
2. Comprueba que veas el dashboard con seis indicadores y el menú administrativo.
3. En **Comedores → Registrar comedor**, completa todos los campos y guarda. Aparece en el listado: CREATE.
4. Pulsa **Ver** y comprueba su información: READ.
5. Pulsa **Editar**, cambia el teléfono o distrito y guarda: UPDATE.
6. Pulsa **Desactivar**, confirma el modal: DELETE lógico. El registro permanece en administración, pero desaparece del catálogo público.
7. Pulsa **Reactivar**: vuelve al catálogo.
8. En **Necesidades → Registrar necesidad**, selecciona el comedor, agrega arroz, 20 kg y prioridad ALTA.
9. Comprueba que aparezca en el detalle público; edita la cantidad y desactiva/reactiva la necesidad.
10. En **Usuarios**, verifica las cuentas registradas. No se muestran hashes ni contraseñas.

## 12. Probar al donador y el ciclo completo

1. Cierra sesión con el botón correspondiente.
2. Ingresa con `donador@comedorsolidario.pe`.
3. Revisa sus indicadores iniciales y **Mis donaciones**.
4. Busca `San Juan de Lurigancho`; prueba además zona `Este` y provincia `Lima`.
5. Abre **Olla Común Santa Rosa** y revisa sus necesidades.
6. Pulsa **Donar a este comedor**.
7. Selecciona ALIMENTOS, cantidad 10, unidad kg, descripción “Arroz en bolsas cerradas”.
8. Guarda. En **Mis donaciones** debe aparecer PENDIENTE.
9. Cierra sesión e ingresa como administrador.
10. Abre **Donaciones**, filtra PENDIENTE y entra al detalle del aporte recién creado.
11. Pulsa **Aceptar donación** y confirma. Debe cambiar a ACEPTADA.
12. Regresa al donador y comprueba el nuevo estado.
13. Como administrador, vuelve al detalle y pulsa **Confirmar entrega**; confirma la acción.
14. Como donador, verifica ENTREGADA y la fecha de entrega.
15. En **Mi perfil**, actualiza nombre o teléfono. El correo es de consulta.
16. Prueba **Registro** con otro correo; vuelve a intentar ese correo y verifica el mensaje de duplicado.
17. Intenta entrar a `/admin/comedores` siendo donador: debe devolver 403.
18. Cierra sesión e intenta `/donante/donaciones`: debe dirigir al login.
19. Inicia sesión con Lucía: no debe ver las donaciones del donador principal.

## 13. Estructura y funcionamiento

- `src/java/com/comedorsolidario/model`: JavaBeans.
- `dao`: SQL parametrizado; `JdbcDAO` cierra todos los recursos con try-with-resources.
- `controller`: un Servlet por responsabilidad. `BaseServlet` centraliza mensajes de error y navegación.
- `filter/SeguridadFilter`: UTF-8, sesión, rol, CSRF, bloqueo del acceso externo a `/views/`.
- `util`: conexión, hash de contraseñas y validación.
- `web/index.jsp`: contenido de la página inicial; `InicioServlet` prepara los datos.
- `web/views`: JSP separadas por auth, public, donante y admin. Las peticiones directas se bloquean; los Servlets hacen forward interno.
- `web/assets`: estilos, JS mínimo, Bootstrap e ilustraciones originales SVG.
- `web/WEB-INF/web.xml`: filtro, errores, sesiones y página de bienvenida.
- `database`: SQL de instalación y datos de demostración.
- `test/java`: pruebas unitarias de reglas y contraseñas.
- `docs/pruebas`: informe y prueba HTTP reproducible.

Se respeta tu estructura `src/java` y `web` mediante configuración Maven. La arquitectura aprobada sugería `WEB-INF/views`; esta implementación conserva tu ruta `web/views` y la protege con un filtro. Ninguna JSP ejecuta SQL. Todas las JSP dinámicas se visitan mediante sus Servlets.

JDK 21, Tomcat 10.1 y Servlet 6.0 usan **jakarta.servlet**. Las clases `javax.crypto` son parte de Java SE y no significan mezcla de Servlet API.

La aplicación es una única unidad WAR. Navegador, servidor Tomcat y servidor MySQL son los componentes que se comunican; no se introducen microservicios ni un framework adicional.

## 14. Trabajar las vistas desde VS Code

1. Abre la carpeta `ComedorSolidario` con **File → Open Folder**.
2. Edita `web/assets/css/styles.css`, `web/assets/js/main.js` y las vistas JSP.
3. Para revisar la versión funcional abre la URL de Tomcat en el navegador.
4. Live Server no ejecuta JSP, Java ni JDBC. Si haces una maqueta HTML por tu cuenta, solo te servirá para revisar la presentación.
5. Las JSP comparten `views/common/header.jspf` y `footer.jspf` para evitar duplicación de navbar y footer.

## 15. Errores frecuentes

| Síntoma | Solución |
|---|---|
| `release version 21 not supported` | Maven está usando un JDK anterior. Selecciona JDK 21 y verifica `mvn -version`. |
| `UnsupportedClassVersionError` | Tomcat se está ejecutando con Java anterior a 21. Cambia su plataforma y reinicia. |
| `ClassNotFoundException: jakarta.servlet...` | Se usó Tomcat 9 o una biblioteca incorrecta. Ejecuta con Tomcat 10.1. |
| `ClassNotFoundException: com.mysql.cj.jdbc.Driver` | Revisa la descarga Maven de Connector/J y vuelve a generar/desplegar el WAR. |
| `Access denied for user` | Corrige usuario, contraseña y permisos MySQL en `db.properties`. |
| `Communications link failure` | Inicia MySQL y revisa host/puerto de la URL JDBC. |
| `Unknown database` | Importa primero el SQL completo. |
| `Table already exists` | La base ya fue importada. Verifica los datos antes de hacer cualquier cambio. |
| Puerto 8080 ocupado | Detén la otra instancia o cambia el Connector de Tomcat a 8081 y usa ese puerto. |
| Error 404 en `/views/...jsp` | Es intencional: accede mediante `/login`, `/comedores` o los controladores. |
| Error 404 en todo el proyecto | Revisa que el WAR se desplegó, contexto exacto, puerto y log de Tomcat. |
| JSTL no se encuentra | Revisa dependencias Jakarta JSTL del POM; no agregues JSTL antigua `javax`. |
| 403 al enviar un formulario | La sesión/token puede haber expirado. Recarga el formulario e inicia sesión nuevamente. |
| No descarga dependencias Maven | Comprueba Internet y el proxy de tu institución. No desactives TLS. |
| Los cambios no aparecen | Guarda, Clean and Build y vuelve a desplegar. Revisa que editas el proyecto original. |
| Hay una página 500 | Consulta Output/Tomcat y `logs/catalina...`; la página no expone detalles técnicos al visitante. |

## 16. Verificación y alcance

Consulta `docs/pruebas/VERIFICACION.md` para conocer las comprobaciones realmente ejecutadas. Las pruebas HTTP están en `docs/pruebas/prueba_http.py`: ejecutarlas requiere Python 3, una instancia local y una base de pruebas con el SQL de ejemplo. Crea registros de prueba; no usar contra datos de producción.

No se implementan envío de emails, recuperación de contraseña, carga de fotografías, pagos electrónicos ni cancelación de donaciones. No forman parte del alcance funcional acordado. No se usan Spring, React, Angular, Vue, Node.js, PHP, Laravel ni Django.

Fuentes de compatibilidad: [Tomcat 10.1](https://tomcat.apache.org/whichversion.html), [Connector/J](https://dev.mysql.com/doc/connector-j/en/), [NetBeans](https://netbeans.apache.org/).
