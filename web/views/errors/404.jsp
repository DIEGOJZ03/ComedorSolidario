<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Página no encontrada"/>
<%@ include file="/views/common/header.jspf" %>
<div class="page-heading">
    <p class="eyebrow">Error 404</p>
    <h1>Página no encontrada</h1>
    <p class="text-secondary">La página o el registro solicitado no están disponibles.</p>
</div>
<a class="btn btn-primary" href="${ctx}/inicio">Volver al inicio</a>
<a class="btn btn-outline-secondary" href="${ctx}/login">Ir al acceso</a>
<%@ include file="/views/common/footer.jspf" %>
