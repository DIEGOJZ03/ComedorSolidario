<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Acceso no permitido"/>
<%@ include file="/views/common/header.jspf" %>
<div class="page-heading">
    <p class="eyebrow">Error 403</p>
    <h1>Acceso no permitido</h1>
    <p class="text-secondary">Tu cuenta no tiene permiso para realizar esta acción o el formulario ha caducado. Vuelve a abrirlo.</p>
</div>
<a class="btn btn-primary" href="${ctx}/inicio">Volver al inicio</a>
<a class="btn btn-outline-secondary" href="${ctx}/login">Ir al acceso</a>
<%@ include file="/views/common/footer.jspf" %>
