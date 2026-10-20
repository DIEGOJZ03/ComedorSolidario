<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="No se pudo completar la operación"/>
<%@ include file="/views/common/header.jspf" %>
<div class="page-heading">
    <p class="eyebrow">Error 500</p>
    <h1>No se pudo completar la operación</h1>
    <p class="text-secondary">Revisa que MySQL esté iniciado y la configuración JDBC sea correcta. El detalle técnico queda en el registro de Tomcat.</p>
</div>
<a class="btn btn-primary" href="${ctx}/inicio">Volver al inicio</a>
<a class="btn btn-outline-secondary" href="${ctx}/login">Ir al acceso</a>
<%@ include file="/views/common/footer.jspf" %>
