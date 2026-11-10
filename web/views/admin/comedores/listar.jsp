<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="CRUD de comedores"/>
<%@ include file="/views/common/header.jspf" %>
<div class="page-heading">
    <p class="eyebrow">Administración · CRUD principal</p>
    <h1>Comedores</h1>
    <p class="text-secondary">Crea y administra comedores. Desactivar conserva sus datos; Eliminar los borra definitivamente si no tienen registros relacionados.</p>
</div>
<a class="btn btn-primary mb-4" href="${ctx}/admin/comedores/formulario">+ Crear comedor</a>
<div class="panel table-responsive">
    <table class="table align-middle">
        <thead>
            <tr>
                <th>ID</th>
                <th>Nombre</th>
                <th>Distrito</th>
                <th>Provincia</th>
                <th>Responsable</th>
                <th>Estado</th>
                <th>Acciones</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="c" items="${comedores}">
                <tr>
                    <td>${c.id}</td>
                    <td><c:out value="${c.nombre}"/></td>
                    <td><c:out value="${c.distrito}"/></td>
                    <td><c:out value="${c.provincia}"/></td>
                    <td><c:out value="${c.responsable}"/></td>
                    <td><c:set var="estado" value="${c.estado}"/><%@ include file="/views/common/badge.jspf" %></td>
                    <td>
                        <div class="action-group">
                            <a class="btn btn-sm btn-outline-primary" href="${ctx}/admin/comedores/detalle?id=${c.id}">Ver</a>
                            <a class="btn btn-sm btn-outline-secondary" href="${ctx}/admin/comedores/formulario?id=${c.id}">Editar</a>
                            <form method="post" action="${ctx}/admin/comedores" data-confirm
                                  data-confirm-title="${c.estado == 'ACTIVO' ? 'Desactivar registro' : 'Reactivar registro'}"
                                  data-confirm-label="${c.estado == 'ACTIVO' ? 'Desactivar' : 'Reactivar'}"
                                  data-confirm-message="${c.estado == 'ACTIVO' ? 'El registro quedará inactivo. Se conservarán sus datos y podrás reactivarlo.' : 'El registro volverá a estar activo con sus datos conservados.'}">
                                <input type="hidden" name="csrf" value="${sessionScope.csrf}">
                                <input type="hidden" name="accion" value="estado">
                                <input type="hidden" name="id" value="${c.id}">
                                <input type="hidden" name="estado" value="${c.estado == 'ACTIVO' ? 'INACTIVO' : 'ACTIVO'}">
                                <button class="btn btn-sm btn-outline-secondary" type="submit">${c.estado == 'ACTIVO' ? 'Desactivar' : 'Reactivar'}</button>
                            </form>
                            <form method="post" action="${ctx}/admin/comedores" data-confirm
                                  data-confirm-title="Eliminar definitivamente" data-confirm-label="Eliminar definitivamente"
                                  data-confirm-message="Se borrará de la base de datos el registro #${c.id}. Esta acción no se puede deshacer ni reactivar. Si tiene registros relacionados, el borrado será rechazado. ¿Deseas continuar?">
                                <input type="hidden" name="csrf" value="${sessionScope.csrf}">
                                <input type="hidden" name="accion" value="eliminar">
                                <input type="hidden" name="id" value="${c.id}">
                                <button class="btn btn-sm btn-outline-danger" type="submit">Eliminar</button>
                            </form>
                        </div>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty comedores}">
                <tr>
                    <td colspan="7">No hay comedores registrados.</td>
                </tr>
            </c:if>
        </tbody>
    </table>
</div>
<%@ include file="/views/common/footer.jspf" %>
