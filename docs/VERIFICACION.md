# Verificación de eliminación y JDBC
Actualización: 20/10/2026.

## Resultados obtenidos

| Comprobación | Resultado | Alcance |
| --- | --- | --- |
| Maven offline package | BUILD SUCCESS | Empaquetado del WAR |
| VerificarRubrica.java | 343 aserciones correctas | Encapsulamiento, modelo y límites de validación |
| VerificarEliminacion.java | 33 aserciones correctas | DELETE de seis DAO, ID parametrizado, protección ADMIN, errores y vigencia |
| node --check web/assets/js/main.js | Sin errores | Sintaxis del JavaScript |
| Jasper de Tomcat: JspC | Generation completed with [0] errors | Traducción y compilación de JSP |

VerificarEliminacion intercepta ejecutar mediante subclases de los DAO: NO conecta a MySQL. Prueba filas inexistentes/protegidas, FK simulada, falta de permiso DELETE, propagación de error SQL y separación UPDATE/DELETE. No prueba realmente el motor MySQL ni la interacción HTTP.

La aplicación sigue compilada con release 21. La comprobación aislada Jasper utiliza source/target 19, compatibles con el compilador JSP disponible, sin cambiar pom.xml.

## Repetir verificadores

Desde la raíz del proyecto después de compilar:

~~~powershell
java --class-path target/classes verificacion/VerificarEliminacion.java
java --class-path "target/classes;$env:USERPROFILE/.m2/repository/jakarta/servlet/jakarta.servlet-api/6.0.0/jakarta.servlet-api-6.0.0.jar" verificacion/VerificarRubrica.java .
node --check web/assets/js/main.js
~~~

## Aceptación pendiente en NetBeans, Tomcat y MySQL

Usar registros propios de prueba. Esta actualización no requiere otra migración si la ampliación anterior ya está instalada.

1. Detener Tomcat, Clean and Build, Run y Ctrl+F5. Entrar como ADMIN.
2. En cada módulo: crear, listar, abrir Ver, editar y comprobar los datos persistidos.
3. Desactivar el registro. Verificar que permanece en MySQL y que el listado ofrece Reactivar. Reactivarlo y comprobar su vigencia.
4. Pulsar Eliminar y cancelar: el registro debe permanecer. Repetir y confirmar: debe desaparecer del listado y de MySQL; consultar su detalle no debe devolver el registro.
5. En Workbench, comprobar la tabla correspondiente con SELECT * FROM tabla WHERE id = ID_DE_PRUEBA; después de desactivar existe; después de eliminar devuelve cero filas.
6. Crear un producto vinculado a una necesidad de prueba. Eliminar el producto: debe rechazarse y conservar ambos registros. Desactivar la necesidad tampoco libera la FK. No borrar registros reales para resolver la prueba.
7. Repetir protección de referencias con comedor y donador. Las cuentas ADMIN no deben admitir edición, desactivación ni eliminación desde el CRUD de donadores.
8. Donaciones: comprobar que administración muestra activas e inactivas y permite Reactivar. El listado del donador y los resúmenes excluyen las inactivas. Reactivar conserva estado de entrega y fecha. Solo una donación activa PENDIENTE admite editar datos.
9. Sin sesión, abrir /admin/productos: redirige al login. Como DONADOR: 403. POST sin CSRF válido: 403. Acceso externo directo a /views/admin/productos/listar.jsp: 404.
10. Validar campos vacíos, cantidades no válidas, correo duplicado, fecha final anterior a inicial e IDs inexistentes.
11. Crear una campaña activa, de comedor activo y dentro de fechas. En ventana privada sin sesión, abrir el detalle público: debe aparecer. Editar título y recargar; desactivar y recargar; reactivar y recargar. Finalmente eliminar la campaña de prueba.
12. Verificar que campañas futuras/vencidas y comedores inactivos no se muestran públicamente. Probar filtros de zona, distrito y provincia con datos reales.
13. Cada integrante debe demostrar su entidad. Registrar resultados y evidencias antes de afirmar cumplimiento completo de la rúbrica.

El usuario informó que resolvió su conexión MySQL. Desde esta sesión no se ejecutaron los flujos HTTP/JDBC ni borrados en su base. Compilar y simular SQL no equivale a la demostración funcional.
