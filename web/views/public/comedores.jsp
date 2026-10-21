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
        <label class="form-label" for="provincia">Provincia</label>
        <select class="form-select" id="provincia" name="provincia" data-provincia-comedor>
            <option value="">Todas las provincias</option>
            <c:forEach var="ubicacion" items="${ubicaciones}">
                <option value="${ubicacion.key}" ${provinciaSeleccionada == ubicacion.key ? 'selected' : ''}><c:out value="${ubicacion.key}"/></option>
            </c:forEach>
        </select>
    </div>
    <div class="col-md-6">
        <label class="form-label" for="distrito">Distrito</label>
        <select class="form-select" id="distrito" name="distrito" data-distrito-comedor data-placeholder="Todos los distritos" aria-describedby="distrito-ayuda">
            <option value="">Todos los distritos</option>
            <c:forEach var="ubicacion" items="${ubicaciones}">
                <optgroup label="${ubicacion.key}" data-provincia="${ubicacion.key}">
                    <c:forEach var="d" items="${ubicacion.value}">
                        <option value="<c:out value='${d}'/>" ${provinciaSeleccionada == ubicacion.key and distritoSeleccionado == d ? 'selected' : ''}><c:out value="${d}"/></option>
                    </c:forEach>
                </optgroup>
            </c:forEach>
        </select>
        <p class="form-text" id="distrito-ayuda">Selecciona una provincia para filtrar por distrito. Puedes consultar todos sus distritos.</p>
    </div>
    <div class="col-md-6">
        <label class="form-label" for="zona">Zona (opcional)</label>
        <input class="form-control" id="zona" name="zona" type="text" maxlength="80" placeholder="Ej.: Norte" aria-describedby="zona-ayuda" value='<c:out value="${param.zona}"/>'>
        <p class="form-text" id="zona-ayuda">Sector general registrado para el comedor: Norte, Sur, Este, Centro u Oeste.</p>
    </div>
    <div class="col-md-6 d-flex align-items-end gap-2">
        <button class="btn btn-primary" type="submit">Buscar comedores</button>
        <a class="btn btn-outline-secondary" href="${ctx}/comedores">Limpiar</a>
    </div>
</form>
<%@ include file="/views/common/cards-comedores.jspf" %>
<%@ include file="/views/common/footer.jspf" %>
