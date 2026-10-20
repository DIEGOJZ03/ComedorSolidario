<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Donaciones"/>
<%@ include file="/views/common/header.jspf" %>
<a class="btn btn-primary my-3" href="${ctx}/admin/donaciones/formulario">+ Crear donación</a>
<div class="page-heading">
    <p class="eyebrow">Administración</p>
    <h1>Todas las donaciones</h1>
    <p class="text-secondary">Incluye donaciones activas e inactivas. Desactivar conserva el aporte; Eliminar lo borra definitivamente. Solo se editan datos de aportes activos y pendientes.</p>
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
                <th>Vigencia</th>
                <th>Acciones</th>
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
                    <td>${d.activo ? 'ACTIVA' : 'INACTIVA'}</td>
                    <td>
                        <div class="action-group">
                            <a class="btn btn-sm btn-outline-primary" href="${ctx}/admin/donaciones/detalle?id=${d.id}">Ver</a>
                            <c:choose>
                                <c:when test="${d.activo and d.estado == 'PENDIENTE'}"><a class="btn btn-sm btn-outline-secondary" href="${ctx}/admin/donaciones/formulario?id=${d.id}">Editar</a></c:when>
                                <c:otherwise><button type="button" class="btn btn-sm btn-outline-secondary" disabled title="Solo se editan datos de donaciones pendientes">Editar</button></c:otherwise>
                            </c:choose>
                            <form method="post" action="${ctx}/admin/donaciones" data-confirm
                                  data-confirm-title="${d.activo ? 'Desactivar registro' : 'Reactivar registro'}"
                                  data-confirm-label="${d.activo ? 'Desactivar' : 'Reactivar'}"
                                  data-confirm-message="${d.activo ? 'El registro quedará inactivo. Se conservarán sus datos y podrás reactivarlo.' : 'El registro volverá a estar activo con sus datos conservados.'}">
                                <input type="hidden" name="csrf" value="${sessionScope.csrf}">
                                <input type="hidden" name="accion" value="vigencia">
                                <input type="hidden" name="id" value="${d.id}">
                                <input type="hidden" name="vigencia" value="${d.activo ? 'INACTIVO' : 'ACTIVO'}">
                                <button class="btn btn-sm btn-outline-secondary" type="submit">${d.activo ? 'Desactivar' : 'Reactivar'}</button>
                            </form>
                            <form method="post" action="${ctx}/admin/donaciones" data-confirm
                                  data-confirm-title="Eliminar definitivamente" data-confirm-label="Eliminar definitivamente"
                                  data-confirm-message="Se borrará de la base de datos el registro #${d.id}. Esta acción no se puede deshacer ni reactivar. Si tiene registros relacionados, el borrado será rechazado. ¿Deseas continuar?">
                                <input type="hidden" name="csrf" value="${sessionScope.csrf}">
                                <input type="hidden" name="accion" value="eliminar">
                                <input type="hidden" name="id" value="${d.id}">
                                <button class="btn btn-sm btn-outline-danger" type="submit">Eliminar</button>
                            </form>
                        </div>
                    </td>
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
