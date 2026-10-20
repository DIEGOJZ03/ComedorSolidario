# Verificación técnica

La compilación Maven offline completó BUILD SUCCESS con Java release 21 y generó target/ComedorSolidario.war. Maven informó que no había pruebas automatizadas en el proyecto original.

No se pudo conectar a MySQL desde esta sesión: el cliente devolvió ERROR 2003 / 10013 al intentar localhost:3306. No se ejecutó la migración sobre tu base ni se modificaron sus registros. La prueba funcional de extremo a extremo en Tomcat y MySQL queda pendiente; compilar el WAR no demuestra que los formularios y SQL se hayan ejecutado.

## Prueba de aceptación en tu entorno

1. Aplicar la migración una sola vez, compilar y desplegar como explica LEEME_RUBRICA.md.
2. Iniciar sesión como ADMIN. En cada uno de los seis módulos: registrar un dato de prueba, encontrarlo en el listado, abrir Ver, editarlo, volver a consultar y darlo de baja. Reiniciar Tomcat y verificar que los cambios permanecen.
3. Productos: registrar Arroz de prueba, unidad kg; seleccionarlo como referencia de una necesidad. Desactivar el producto y verificar que la necesidad conserva su referencia histórica.
4. Campañas: elegir un comedor activo y fechas que incluyan el día actual. Verificar su aparición pública en Comedores → detalle. Editar el título y recargar; dar de baja y comprobar que deja de aparecer. Una campaña futura, vencida o de un comedor inactivo no se publica.
5. Usuarios: crear un DONADOR con correo único; editar sus datos; darlo de baja. Comprobar que no inicia sesión y que una sesión previamente abierta deja de tener acceso. Reactivarlo. Intentar editar una cuenta ADMIN mediante URL: debe devolver 403; mediante POST tampoco puede modificarla.
6. Donaciones: registrar a nombre del donador activo, consultar detalle, editar mientras PENDIENTE, aceptar y entregar. Comprobar fecha_entrega y que no retrocede. Dar de baja y comprobar que desaparece de listados y totales, conservando su fila SQL. Un aporte aceptado o entregado no permite cambiar sus datos materiales.
7. Validaciones: cantidad cero/negativa, más de dos decimales, correo duplicado, campos obligatorios vacíos, fecha final anterior a inicial, dinero con unidad distinta de soles e identificador inexistente. Deben rechazarse con mensaje y sin insertar/actualizar.
8. Sin sesión, abrir /admin/productos: debe dirigir al login. Como DONADOR, debe responder 403. POST sin token csrf válido: 403. Consultar directamente /views/admin/productos/listar.jsp: 404.
9. Consultar /inicio, filtrar /comedores y abrir detalle sin sesión. Desactivar un comedor desde ADMIN: desaparece de resultados públicos. Las necesidades inactivas y las donaciones de otros usuarios no deben aparecer al donador.

Usar datos de prueba propios y no eliminar registros reales para la demostración. Las bajas de entidades de negocio son lógicas.

## Comprobaciones ejecutadas adicionales

Se ejecutó `verificacion/VerificarRubrica.java` contra las clases compiladas. Resultado: 343 aserciones correctas de encapsulamiento, correspondencia de atributos/accesores con el PlantUML y límites de validación de cantidades, fechas, identificadores y opciones. Son aserciones automáticas de un verificador, no 343 casos de prueba ni pruebas HTTP/JDBC. No sustituyen la aceptación funcional anterior.

Para repetir en PowerShell desde la raíz del proyecto, después de compilar:

```powershell
java --class-path "target/classes;$env:USERPROFILE/.m2/repository/jakarta/servlet/jakarta.servlet-api/6.0.0/jakarta.servlet-api-6.0.0.jar" verificacion/VerificarRubrica.java .
```

## Revisión de interfaces del 20 de octubre de 2026

La compilación del WAR volvió a finalizar correctamente. Se verificó la sintaxis de main.js y se precompilaron las vistas mediante org.apache.jasper.JspC de Tomcat: Generation completed with [0] errors. Jasper usó su nivel soportado 19 para las JSP (warning al solicitar 21); no se cambió el release 21 del proyecto. Se mantienen pendientes las pruebas funcionales HTTP/JDBC de los seis flujos y la demostración individual. El usuario informó que resolvió su conexión MySQL.
