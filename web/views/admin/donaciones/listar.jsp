<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Donaciones"/>
<%@ include file="/views/common/header.jspf" %>
<div class="page-heading">
    <p class="eyebrow">Administración</p>
    <h1>Todas las donaciones</h1>
    <p class="text-secondary">Consulta los aportes y confirma cada paso de su entrega.</p>
</div>
<form method="get" action="${ctx}/admin/donaciones" class="panel row g-3 mb-4">
    <div class="col-md-6">
        <label class="form-label" for="estado">Estado</label>
        <select class="form-select" id="estado" name="estado">
            <option value="" ${(param.estado) == '' ? 'selected' : ''}>Todos</option>
            <option value="PENDIENTE" ${(param.estado) == 'PENDIENTE' ? 'selected' : ''}>Pendiente</option>
            <option value="ACEPTADA" ${(param.estado) == 'ACEPTADA' ? 'selected' : ''}>Aceptada</option>
            <option value="ENTREGADA" ${(param.estado) == 'ENTREGADA' ? 'selected' : ''}>Entregada</option>
        </select>
    </div>
    <div class="col-md-6 d-flex align-items-end">
        <button class="btn btn-primary">Filtrar donaciones</button>
    </div>
</form>
<div class="panel table-responsive">
    <table class="table align-middle">
        <thead>
            <tr>
                <th>ID</th>
                <th>Comedor</th>
                <th>Donador</th>
                <th>Tipo / aporte</th>
                <th>Registro</th>
                <th>Estado</th>
                <th>Entrega</th>
                <th>Acción</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="d" items="${donaciones}">
                <tr>
                    <td>#${d.id}</td>
                    <td><c:out value="${d.comedorNombre}"/></td>
                    <td><c:out value="${d.donanteNombre}"/></td>
                    <td><c:out value="${d.tipo}"/><br><span class="small">${d.cantidad} <c:out value="${d.unidad}"/></span></td>
                    <td><c:out value="${d.fechaRegistro}"/></td>
                    <td><c:set var="estado" value="${d.estado}"/><%@ include file="/views/common/badge.jspf" %></td>
                    <td><c:out value="${d.fechaEntrega}" default="Por confirmar"/></td>
                    <td><a class="btn btn-sm btn-outline-primary" href="${ctx}/admin/donaciones/detalle?id=${d.id}">Ver detalle</a></td>
                </tr>
            </c:forEach>
            <c:if test="${empty donaciones}">
                <tr>
                    <td colspan="9" class="text-center py-4">Todavía no hay donaciones para mostrar.</td>
                </tr>
            </c:if>
        </tbody>
    </table>
</div>
<%@ include file="/views/common/footer.jspf" %>
