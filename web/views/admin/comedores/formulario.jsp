<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Formulario de comedor"/>
<%@ include file="/views/common/header.jspf" %>
<div class="page-heading">
    <p class="eyebrow">Administración</p>
    <h1>${comedor.id == 0 ? "Registrar comedor" : "Editar comedor"}</h1>
    <p class="text-secondary">Completa la información que verán los donadores.</p>
</div>
<form method="post" action="${ctx}/admin/comedores" class="panel row g-3" >
    <input type="hidden" name="csrf" value="${sessionScope.csrf}">
    <input type="hidden" name="accion" value="guardar">
    <input type="hidden" name="id" value="${comedor.id}">
    <div class="col-md-6">
        <label class="form-label" for="nombre">Nombre del comedor</label>
        <input class="form-control" id="nombre" name="nombre" type="text" maxlength="120" required value='<c:out value="${pageContext.request.method eq 'POST' ? param.nombre : comedor.nombre}"/>'>
    </div>
    <div class="col-md-6">
        <label class="form-label" for="direccion">Dirección</label>
        <input class="form-control" id="direccion" name="direccion" type="text" maxlength="200" required value='<c:out value="${pageContext.request.method eq 'POST' ? param.direccion : comedor.direccion}"/>'>
    </div>
    <div class="col-md-6">
        <label class="form-label" for="zona">Zona</label>
        <input class="form-control" id="zona" name="zona" type="text" maxlength="80" required value='<c:out value="${pageContext.request.method eq 'POST' ? param.zona : comedor.zona}"/>'>
    </div>
    <div class="col-md-6">
        <label class="form-label" for="distrito">Distrito</label>
        <input class="form-control" id="distrito" name="distrito" type="text" maxlength="100" required value='<c:out value="${pageContext.request.method eq 'POST' ? param.distrito : comedor.distrito}"/>'>
    </div>
    <div class="col-md-6">
        <label class="form-label" for="provincia">Provincia</label>
        <input class="form-control" id="provincia" name="provincia" type="text" maxlength="100" required value='<c:out value="${pageContext.request.method eq 'POST' ? param.provincia : comedor.provincia}"/>'>
    </div>
    <div class="col-md-6">
        <label class="form-label" for="responsable">Responsable</label>
        <input class="form-control" id="responsable" name="responsable" type="text" maxlength="120" required value='<c:out value="${pageContext.request.method eq 'POST' ? param.responsable : comedor.responsable}"/>'>
    </div>
    <div class="col-md-6">
        <label class="form-label" for="telefono">Teléfono</label>
        <input class="form-control" id="telefono" name="telefono" type="text" maxlength="20" required value='<c:out value="${pageContext.request.method eq 'POST' ? param.telefono : comedor.telefono}"/>'>
    </div>
    <div class="col-md-6">
        <label class="form-label" for="estado">Estado</label>
        <select class="form-select" id="estado" name="estado" required>
            <option value="ACTIVO" ${(pageContext.request.method eq 'POST' ? param.estado : comedor.estado) == 'ACTIVO' ? 'selected' : ''}>Activo</option>
            <option value="INACTIVO" ${(pageContext.request.method eq 'POST' ? param.estado : comedor.estado) == 'INACTIVO' ? 'selected' : ''}>Inactivo</option>
        </select>
    </div>
    <div class="col-12">
        <label class="form-label" for="descripcion">Descripción</label>
        <textarea class="form-control" id="descripcion" name="descripcion" rows="3" maxlength="1500" required><c:out value="${pageContext.request.method eq 'POST' ? param.descripcion : comedor.descripcion}"/></textarea>
    </div>
    <div class="col-12 d-flex gap-2 mt-4">
        <button class="btn btn-primary" type="submit">Guardar comedor</button>
        <a class="btn btn-outline-secondary" href="${ctx}/admin/comedores">Volver</a>
    </div>
</form>
<%@ include file="/views/common/footer.jspf" %>
