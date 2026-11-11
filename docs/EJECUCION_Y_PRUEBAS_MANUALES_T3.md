# Activación y pruebas manuales T3

Revisión final: 11 de noviembre de 2026. Proyecto existente: `ComedorSolidario-yoel`. No se necesita crear otro proyecto ni importar otra base.

## 1. Migración en MySQL Workbench

1. Detener Tomcat desde NetBeans y mantener MySQL iniciado.
2. Abrir la conexión habitual en Workbench. Respaldar el esquema `comedor_solidario` desde **Server > Data Export**, incluyendo estructura y datos, y guardar el archivo de respaldo.
3. Abrir **File > Open SQL Script** y seleccionar `database/migracion_t3_logistica.sql` dentro del proyecto. Confirmar que el inicio del archivo dice `USE comedor_solidario;` y que la conexión seleccionada es la habitual correcta.
4. Utilizar una cuenta con permisos de creación/alteración de tablas e índices. Esto no requiere cambiar las credenciales que usa Java. Activar **Query > Stop Script Execution On Errors** para detectar el primer fallo si hubiera uno.
5. Seleccionar todo el contenido del archivo y elegir **Query > Execute (All or Selection)**. Ejecutar el script completo, no únicamente la instrucción donde está el cursor. Consultar el panel **Action Output** y comprobar que no aparezcan errores.
6. Refrescar **Schemas**. Deben aparecer las seis tablas nuevas: `puntos_acopio`, `solicitudes_recojo`, `rutas_recojo`, `detalle_ruta_recojo`, `historial_donacion` e `historial_usuario`. Las tablas anteriores siguen existiendo.
7. En una pestaña nueva se pueden ejecutar las consultas de comprobación de abajo. Son únicamente de lectura.

```sql
USE comedor_solidario;
SHOW TABLES;
SHOW COLUMNS FROM donaciones;
SHOW CREATE TABLE detalle_ruta_recojo;
SELECT COUNT(*) AS usuarios_conservados FROM usuarios;
SELECT COUNT(*) AS donaciones_conservadas FROM donaciones;
```

Comparar los conteos con los obtenidos antes de la migración o con el respaldo. En donaciones deben verse `modalidad`, `punto_acopio_id`, `motivo` y los siete estados; las donaciones previas reciben modalidad DIRECTA. El script no borra tablas ni filas, ni reinicia identificadores ni crea cuentas. Se comprobó su repetición en la instancia de pruebas. El DDL de MySQL hace commit implícito: un error no significa que todo lo anterior se haya revertido; corregir la causa y repetir el script con la aplicación detenida.

No ejecutar `comedor_solidario.sql` ni `migracion_rubrica.sql` sobre la base habitual: esta ya contiene la T2. Tampoco ejecutar `VerificarLogistica instalar` ni los verificadores automáticos con escritura sobre tus datos habituales.

Los permisos SELECT, INSERT, UPDATE y DELETE de la cuenta Java deben incluir las tablas nuevas. Si ya abarcan `comedor_solidario.*`, basta conservarlos. No se requieren permisos DDL para ejecutar la aplicación.

Referencia de los comandos de ejecución: [menú Query de Workbench](https://dev.mysql.com/doc/workbench/en/wb-sql-editor-main-menu.html).

## 2. Ejecución desde NetBeans

1. Abrir **File > Open Project** y seleccionar la carpeta `ComedorSolidario-yoel`, donde está `pom.xml`.
2. En propiedades del proyecto, conservar el JDK 21 o superior y **Apache Tomcat 10.1**. El contexto es `/ComedorSolidario`.
3. Conservar la configuración habitual de `src/resources/db.properties`. Los puertos 3308 y 18080 se usaron únicamente durante las pruebas aisladas; no deben copiarse a esa configuración. Los procesos temporales se cerraron al terminar las pruebas.
4. Ejecutar **Clean and Build (Shift+F11)** y verificar `BUILD SUCCESS`. Es equivalente a compilar y empaquetar con `mvn clean package`; la acción del IDE puede añadir la instalación del artefacto en el repositorio Maven local.
5. Ejecutar **Run (F6)**. El navegador debe abrir `http://localhost:8080/ComedorSolidario/inicio` si se conserva el puerto 8080 de Tomcat. Recargar con Ctrl+F5.
6. Ingresar con la cuenta ADMIN existente. Deben aparecer Puntos de Acopio, Solicitudes de Recojo, Rutas de Recojo y Confianza, además de los módulos anteriores.

El artefacto generado es `target/ComedorSolidario.war`. No debe copiarse además manualmente si NetBeans ya despliega el proyecto: F6 utiliza su configuración de despliegue existente.

## 3. Prueba de aceptación después de migrar

Usar una cuenta DONADOR dedicada a pruebas y nombres claramente marcados PRUEBA T3. Estas acciones manuales sí guardarán datos en tu base ya migrada; no utilizar a un donador real para simular incumplimientos. Se puede usar una ventana privada para el donador y otra ventana para ADMIN, evitando mezclar sesiones.

| Paso | Acción | Resultado esperado |
| --- | --- | --- |
| Acopio | ADMIN registra PRUEBA T3 con todos sus datos, lo edita y consulta el catálogo público | El punto activo se ve con dirección, horario, responsable y teléfono |
| Estado de acopio | ADMIN lo desactiva y vuelve a activar | Desaparece de las opciones públicas mientras está inactivo; vuelve al reactivarlo |
| Contacto | Registrar el donador de prueba con correo único y teléfono; probar teléfono vacío/incorrecto | El formulario exige contacto válido; un correo repetido se rechaza |
| Directa | El donador registra entrega DIRECTA; ADMIN acepta y confirma entrega | PENDIENTE → ACEPTADA → ENTREGADA; la entrega queda en historial |
| Acopio | Registrar ACOPIO seleccionando PRUEBA T3; ADMIN acepta y confirma recepción final | Se conserva el punto elegido y el comedor destinatario; ADMIN confirma la recepción en el comedor |
| Recojo | Registrar RECOJO con dirección, fecha preferida, horario y teléfono | Se crean juntos la donación y la solicitud SOLICITADO; el donador ve sus datos |
| Programación | ADMIN programa la solicitud con fecha actual/futura, horario y responsable | Solicitud PROGRAMADO y donación PROGRAMADA |
| Ruta | ADMIN crea ruta con la misma fecha, distrito y responsable, agrega el recojo con orden 1 y confirma compatibilidad horaria | La parada muestra su dirección; una segunda ruta activa no puede incluir la misma solicitud |
| Recorrido | ADMIN inicia la ruta y confirma la recogida en Solicitudes de Recojo | Ruta EN_PROCESO, solicitud RECOGIDO; la donación todavía no está ENTREGADA |
| Recepción | ADMIN confirma recibido en el comedor y después completa la ruta | Solicitud COMPLETADO, donación ENTREGADA con fecha y ruta COMPLETADA |
| Permisos | Con la sesión DONADOR abrir `/ComedorSolidario/admin/logistica/rutas` | Acceso denegado; el donador no tiene botones de confirmación de entrega |
| Cupo | Crear dos donaciones pendientes con la cuenta de prueba e intentar una tercera | La tercera se rechaza; finalizar/cerrar una libera un cupo |
| Confianza | ADMIN marca dos aportes diferentes de esa cuenta como INCUMPLIDA, con motivo de prueba | La cuenta aparece en Confianza; permanece ACTIVA hasta que ADMIN decida |
| Cuenta | ADMIN desactiva y reactiva esa cuenta con motivo | Ambas decisiones quedan en el historial; la cuenta inactiva no puede seguir donando |
| Conservación | Consultar las donaciones y comedores previos; probar búsqueda, necesidades, productos y campañas | Los registros anteriores y sus pantallas permanecen disponibles |

Completar primero las pruebas DIRECTA y ACOPIO antes de iniciar RECOJO evita agotar el cupo de dos aportes. Los pasos de confianza se realizan solo sobre la cuenta de pruebas. No es necesario borrar registros para terminar: desactivar los puntos y la cuenta de pruebas conserva la auditoría.

## 4. Evidencia de Spring MVC

- `pom.xml`: dependencia directa `org.springframework:spring-webmvc:6.2.19`, empaquetada dentro del WAR junto con sus dependencias transitivas. Servlet API 6.0.0 es `provided`, la aporta Tomcat.
- `web/WEB-INF/web.xml`: registra `org.springframework.web.servlet.DispatcherServlet` con nombre `logistica`, carga al arrancar y mapea `/admin/logistica/*`, `/donante/logistica/*` y `/acopio/*`.
- `src/java/com/comedorsolidario/config/LogisticaConfig.java`: `@Configuration`, `@EnableWebMvc`, `@ComponentScan` y `InternalResourceViewResolver` para los JSP de logística.
- `src/java/com/comedorsolidario/controller/logistica/LogisticaController.java`: `@Controller`, `@GetMapping`, `@PostMapping` y `@ExceptionHandler`; resuelve puntos, solicitudes, rutas, historial, alertas, cuentas y cierres. Recibe `LogisticaService` por constructor.
- `src/java/com/comedorsolidario/service/logistica/LogisticaService.java`: `@Service` detectado e inyectado por Spring; comparte reglas con los Servlets conservados. La persistencia usa los DAO y `ConexionBD` existentes.
- En las pruebas de ejecución, Tomcat inicializó el servlet `logistica`; los formularios HTTP de alta de ruta, programación, parada, recogida y recepción respondieron correctamente y persistieron en MySQL aislado. No se deduce el funcionamiento solo de la presencia de la dependencia.

## 5. Archivos de entrega

- [Migración definitiva](../database/migracion_t3_logistica.sql).
- [Clases del modelo](modelo-clases.puml), [diagrama técnico completo editable](clases-completas.puml) y [diagrama completo en SVG](ClasesCompletas.svg).
- [Base de datos editable PlantUML](base-datos-t3.puml) y [versión SVG para abrir en navegador](base-datos-t3.svg).
- [Documentación de módulos y arquitectura](T3_LOGISTICA.md).
- [Informe de pruebas](VERIFICACION_T3.md).
- [Inventario de archivos](ARCHIVOS_T3.md).

No hay errores conocidos pendientes en los casos comprobados. La ejecución con la base habitual queda condicionada a aplicar manualmente la migración y conservar los permisos/configuración correctos. No se afirma que esa migración ya haya sido ejecutada ni que se hayan probado automáticamente todos los datos reales.
