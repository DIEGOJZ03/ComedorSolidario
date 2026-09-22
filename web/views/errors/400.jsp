<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Revisa los datos"/>
<%@ include file="/views/common/header.jspf" %>
<div class="page-heading">
    <p class="eyebrow">Error 400</p>
    <h1>Revisa los datos</h1>
    <p class="text-secondary">La operación no se realizó. Vuelve al formulario y corrige los campos indicados.</p>
</div>
<a class="btn btn-primary" href="${ctx}/inicio">Volver al inicio</a>
<a class="btn btn-outline-secondary" href="${ctx}/login">Ir al acceso</a>
<%@ include file="/views/common/footer.jspf" %>
