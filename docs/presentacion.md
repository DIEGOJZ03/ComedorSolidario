# Contenido de presentación — 12 diapositivas

Texto breve listo para trasladar a PowerPoint o Canva. Las capturas de la ejecución están en `capturas/` cuando se pudieron generar; también puedes obtenerlas en tu instalación.

## Diapositiva 1: Portada

- Comedor Solidario
- Gestión de donaciones para comedores comunitarios
- Soluciones Web y Aplicaciones Distribuidas · Ingeniería de Sistemas
- Integrantes: completar · Docente: completar

## Diapositiva 2: Problema identificado

- Información repartida entre mensajes, llamadas y redes.
- Dificultad para conocer necesidades y seguir la entrega.
- Registros de donadores y aportes poco organizados.

## Diapositiva 3: Solución propuesta

- Un solo sistema para comedores, necesidades y donaciones.
- Búsqueda por ubicación.
- Seguimiento: Pendiente → Aceptada → Entregada.

## Diapositiva 4: Roles y funciones

- Donador: buscar, registrar aportes y consultar sus estados.
- Administrador: CRUD de comedores, necesidades y seguimiento.
- Visitante: consulta pública y registro.

## Diapositiva 5: Arquitectura y tecnologías

- Vista: JSP + HTML + CSS + Bootstrap.
- Controlador: Servlets. Modelo: JavaBeans.
- Datos: DAO + JDBC + MySQL. Tomcat 10.1, JDK 21.

## Diapositiva 6: Casos de uso

- Insertar diagramas/casos-de-uso.svg.
- Resaltar: registrar donación y actualizar su estado.
- Explicar precondiciones y alternativa de datos inválidos.

## Diapositiva 7: Diagrama de clases

- Insertar diagramas/clases.svg.
- Usuario y Comedor se relacionan con Donacion.
- Comedor publica muchas Necesidades.

## Diapositiva 8: Modelo de datos

- Insertar diagramas/modelo-relacional.svg.
- Cuatro tablas con PK/FK. Correo único.
- Borrado lógico para conservar historial.

## Diapositiva 9: UX del donador

- Mostrar inicio, búsqueda y Mis donaciones.
- Formulario simple y estados con texto y color.
- Navegación responsive.

## Diapositiva 10: UX del administrador

- Mostrar dashboard y CRUD de comedores.
- Crear → Ver → Editar → Desactivar.
- Necesidades y donaciones organizadas en tablas.

## Diapositiva 11: Funcionamiento

- Formulario POST → Servlet → DAO/JDBC → MySQL.
- Servlet prepara datos → JSP muestra resultado.
- Demostrar aceptación y entrega de una donación.

## Diapositiva 12: Conclusiones

- Información centralizada y seguimiento claro.
- MVC separa responsabilidades y facilita mantenimiento.
- Mejoras futuras: notificaciones y paginación de alto volumen.

## Guion de demostración (4–5 minutos)

1. Inicio y búsqueda de San Juan de Lurigancho.
2. Login como donador y registro de arroz.
3. Login administrativo y vista de CRUD.
4. Aceptar la donación; luego confirmar entrega.
5. Volver al donador y mostrar estado actualizado.
6. Explicar un Servlet, su DAO y su JSP.
