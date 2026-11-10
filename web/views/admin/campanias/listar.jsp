<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Campañas"/>
<%@ include file="/views/common/header.jspf" %>
<div class="page-heading">
    <p class="eyebrow">Administración</p>
    <h1>Campañas</h1>
    <p class="text-secondary">Administra convocatorias. Desactivar deja de publicar la campaña; Eliminar la borra definitivamente.</p>
</div>
<a class="btn btn-primary mb-4" href="${ctx}/admin/campanias/formulario">+ Crear campaña</a>
<div class="panel table-responsive">
    <table class="table align-middle">
        <thead><tr><th>ID</th><th>ID del comedor</th><th>Nombre</th><th>Fecha inicial</th><th>Fecha final</th><th>Estado</th><th>Acciones</th></tr></thead>
        <tbody>
            <c:forEach var="item" items="${items}">
                <tr>
                    <td><c:out value="${item.id}"/></td>
                    <td><c:out value="${item.comedorId}"/></td>
                    <td><c:out value="${item.nombre}"/></td>
                    <td><c:out value="${item.fechaInicio}"/></td>
                    <td><c:out value="${item.fechaFin}"/></td>
                    <td><c:out value="${item.estado}"/></td>

                    <td><div class="action-group">
                            <a class="btn btn-sm btn-outline-primary" href="${ctx}/admin/campanias/detalle?id=${item.id}">Ver</a>
                            <a class="btn btn-sm btn-outline-secondary" href="${ctx}/admin/campanias/formulario?id=${item.id}">Editar</a>
                            <form method="post" action="${ctx}/admin/campanias" data-confirm
                                  data-confirm-title="${item.estado == 'ACTIVO' ? 'Desactivar registro' : 'Reactivar registro'}"
                                  data-confirm-label="${item.estado == 'ACTIVO' ? 'Desactivar' : 'Reactivar'}"
                                  data-confirm-message="${item.estado == 'ACTIVO' ? 'El registro quedará inactivo. Se conservarán sus datos y podrás reactivarlo.' : 'El registro volverá a estar activo con sus datos conservados.'}">
                                <input type="hidden" name="csrf" value="${sessionScope.csrf}">
                                <input type="hidden" name="accion" value="estado">
                                <input type="hidden" name="id" value="${item.id}">
                                <input type="hidden" name="estado" value="${item.estado == 'ACTIVO' ? 'INACTIVO' : 'ACTIVO'}">
                                <button class="btn btn-sm btn-outline-secondary" type="submit">${item.estado == 'ACTIVO' ? 'Desactivar' : 'Reactivar'}</button>
                            </form>
                            <form method="post" action="${ctx}/admin/campanias" data-confirm
                                  data-confirm-title="Eliminar definitivamente" data-confirm-label="Eliminar definitivamente"
                                  data-confirm-message="Se borrará de la base de datos el registro #${item.id}. Esta acción no se puede deshacer ni reactivar. Si tiene registros relacionados, el borrado será rechazado. ¿Deseas continuar?">
                                <input type="hidden" name="csrf" value="${sessionScope.csrf}">
                                <input type="hidden" name="accion" value="eliminar">
                                <input type="hidden" name="id" value="${item.id}">
                                <button class="btn btn-sm btn-outline-danger" type="submit">Eliminar</button>
                            </form>
                        </div></td>
                </tr>
            </c:forEach>
            <c:if test="${empty items}"><tr><td colspan="7">No hay registros.</td></tr></c:if>
        </tbody>
    </table>
</div>
<%@ include file="/views/common/footer.jspf" %>
