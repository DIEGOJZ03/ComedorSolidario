<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Comedores"/>
<%@ include file="/views/common/header.jspf" %>
<div class="page-heading">
    <p class="eyebrow">Encuentra dónde ayudar</p>
    <h1>Comedores comunitarios</h1>
    <p class="text-secondary">Combina los filtros de ubicación para encontrar una comunidad cercana.</p>
</div>
<form method="get" action="${ctx}/comedores" class="panel row g-3 mb-4">
    <div class="col-md-6">
        <label class="form-label" for="zona">Zona</label>
        <input class="form-control" id="zona" name="zona" type="text" maxlength="100" value='<c:out value="${param.zona}"/>'>
    </div>
    <div class="col-md-6">
        <label class="form-label" for="distrito">Distrito</label>
        <input class="form-control" id="distrito" name="distrito" type="text" maxlength="100" value='<c:out value="${param.distrito}"/>'>
    </div>
    <div class="col-md-6">
        <label class="form-label" for="provincia">Provincia</label>
        <input class="form-control" id="provincia" name="provincia" type="text" maxlength="100" value='<c:out value="${param.provincia}"/>'>
    </div>
    <div class="col-md-6 d-flex align-items-end gap-2">
        <button class="btn btn-primary" type="submit">Buscar comedores</button>
        <a class="btn btn-outline-secondary" href="${ctx}/comedores">Limpiar</a>
    </div>
</form>
<%@ include file="/views/common/cards-comedores.jspf" %>
<%@ include file="/views/common/footer.jspf" %>
