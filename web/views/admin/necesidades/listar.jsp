<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Necesidades"/>
<%@ include file="/views/common/header.jspf" %>
<div class="page-heading">
    <p class="eyebrow">Administración</p>
    <h1>Necesidades de los comedores</h1>
    <p class="text-secondary">Mantén las prioridades y cantidades actualizadas.</p>
</div>
<a class="btn btn-primary mb-4" href="${ctx}/admin/necesidades/formulario">+ Registrar necesidad</a>
<form method="get" action="${ctx}/admin/necesidades" class="panel row g-3 mb-4">
    <div class="col-md-8">
        <label for="comedorId" class="form-label">Filtrar por comedor</label>
        <select name="comedorId" id="comedorId" class="form-select">
            <option value="">Todos</option>
            <c:forEach var="c" items="${comedores}">
                <option value="${c.id}" ${param.comedorId == c.id ? "selected" : ""}><c:out value="${c.nombre}"/></option>
            </c:forEach>
        </select>
    </div>
    <div class="col-md-4 d-flex align-items-end">
        <button class="btn btn-primary">Filtrar</button>
    </div>
</form>
<div class="panel table-responsive">
    <table class="table align-middle">
        <thead>
            <tr>
                <th>Comedor</th>
                <th>Necesidad</th>
                <th>Cantidad</th>
                <th>Prioridad</th>
                <th>Estado</th>
                <th>Acciones</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="n" items="${necesidades}">
                <tr>
                    <td><c:out value="${n.comedorNombre}"/></td>
                    <td><c:out value="${n.nombre}"/></td>
                    <td>${n.cantidadNecesaria} <c:out value="${n.unidad}"/></td>
                    <td><c:out value="${n.prioridad}"/></td>
                    <td><c:set var="estado" value="${n.estado}"/><%@ include file="/views/common/badge.jspf" %></td>
                    <td>
                        <div class="action-group">
                            <a class="btn btn-sm btn-outline-primary" href="${ctx}/admin/necesidades/formulario?id=${n.id}">Editar</a>
                            <form method="post" action="${ctx}/admin/necesidades" data-confirm>
                                <input type="hidden" name="csrf" value="${sessionScope.csrf}">
                                <input type="hidden" name="accion" value="estado">
                                <input type="hidden" name="id" value="${n.id}">
                                <input type="hidden" name="estado" value="${n.estado == 'ACTIVA' ? 'INACTIVA' : 'ACTIVA'}">
                                <button class="btn btn-sm btn-outline-danger">${n.estado == 'ACTIVA' ? 'Desactivar' : 'Reactivar'}</button>
                            </form>
                        </div>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty necesidades}">
                <tr>
                    <td colspan="6">No hay necesidades registradas.</td>
                </tr>
            </c:if>
        </tbody>
    </table>
</div>
<%@ include file="/views/common/footer.jspf" %>
