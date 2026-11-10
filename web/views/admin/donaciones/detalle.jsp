<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Detalle de donación"/>
<%@ include file="/views/common/header.jspf" %>
<div class="page-heading">
    <p class="eyebrow">Administración</p>
    <h1>Donación #${donacion.id}</h1>
    <p class="text-secondary">Verifica la coordinación con el comedor antes de confirmar el avance.</p>
</div>
<section class="panel">
    <dl class="row">
        <dt class="col-sm-3">Donador</dt>
        <dd class="col-sm-9"><c:out value="${donacion.donanteNombre}"/></dd>
        <dt class="col-sm-3">Comedor</dt>
        <dd class="col-sm-9"><c:out value="${donacion.comedorNombre}"/></dd>
        <dt class="col-sm-3">Aporte</dt>
        <dd class="col-sm-9"><c:out value="${donacion.tipo}"/> · ${donacion.cantidad} <c:out value="${donacion.unidad}"/></dd>
        <dt class="col-sm-3">Descripción</dt>
        <dd class="col-sm-9 preserve-lines"><c:out value="${donacion.descripcion}"/></dd>
        <dt class="col-sm-3">Registro</dt>
        <dd class="col-sm-9"><c:out value="${donacion.fechaRegistro}"/></dd>
        <dt class="col-sm-3">Entrega</dt>
        <dd class="col-sm-9"><c:out value="${donacion.fechaEntrega}" default="Por confirmar"/></dd>
        <dt class="col-sm-3">Estado</dt>
        <dd class="col-sm-9"><c:set var="estado" value="${donacion.estado}"/><%@ include file="/views/common/badge.jspf" %></dd>
    <dt class="col-sm-3">Vigencia</dt><dd class="col-sm-9">${donacion.activo ? 'ACTIVA' : 'INACTIVA'}</dd>
    </dl>
    <c:if test="${donacion.activo and donacion.estado == 'PENDIENTE'}"><a class="btn btn-outline-primary mb-3" href="${ctx}/admin/donaciones/formulario?id=${donacion.id}">Editar datos</a></c:if>
    <div class="d-flex flex-wrap gap-2 my-3"><form method="post" action="${ctx}/admin/donaciones" data-confirm
                                  data-confirm-title="${donacion.activo ? 'Desactivar registro' : 'Reactivar registro'}"
                                  data-confirm-label="${donacion.activo ? 'Desactivar' : 'Reactivar'}"
                                  data-confirm-message="${donacion.activo ? 'El registro quedará inactivo. Se conservarán sus datos y podrás reactivarlo.' : 'El registro volverá a estar activo con sus datos conservados.'}">
                                <input type="hidden" name="csrf" value="${sessionScope.csrf}">
                                <input type="hidden" name="accion" value="vigencia">
                                <input type="hidden" name="id" value="${donacion.id}">
                                <input type="hidden" name="vigencia" value="${donacion.activo ? 'INACTIVO' : 'ACTIVO'}">
                                <button class="btn btn-sm btn-outline-secondary" type="submit">${donacion.activo ? 'Desactivar' : 'Reactivar'}</button>
                            </form><form method="post" action="${ctx}/admin/donaciones" data-confirm
                                  data-confirm-title="Eliminar definitivamente" data-confirm-label="Eliminar definitivamente"
                                  data-confirm-message="Se borrará de la base de datos el registro #${donacion.id}. Esta acción no se puede deshacer ni reactivar. Si tiene registros relacionados, el borrado será rechazado. ¿Deseas continuar?">
                                <input type="hidden" name="csrf" value="${sessionScope.csrf}">
                                <input type="hidden" name="accion" value="eliminar">
                                <input type="hidden" name="id" value="${donacion.id}">
                                <button class="btn btn-sm btn-outline-danger" type="submit">Eliminar</button>
                            </form></div>
    <ol class="status-steps">
        <li class="complete">Pendiente</li>
        <li class="${donacion.estado != 'PENDIENTE' ? 'complete' : ''}">Aceptada</li>
        <li class="${donacion.estado == 'ENTREGADA' ? 'complete' : ''}">Entregada</li>
    </ol>
    <c:if test="${donacion.activo and donacion.estado != 'ENTREGADA'}">
        <form method="post" action="${ctx}/admin/donaciones" data-confirm>
            <input type="hidden" name="csrf" value="${sessionScope.csrf}">
            <input type="hidden" name="id" value="${donacion.id}">
            <input type="hidden" name="accion" value="avanzar">
            <input type="hidden" name="estadoActual" value="${donacion.estado}">
            <button class="btn btn-primary" type="submit">${donacion.estado == 'PENDIENTE' ? 'Aceptar donación' : 'Confirmar entrega'}</button>
        </form>
    </c:if>
    <a class="btn btn-outline-secondary mt-3" href="${ctx}/admin/donaciones">Volver a donaciones</a>
</section>
<%@ include file="/views/common/footer.jspf" %>
