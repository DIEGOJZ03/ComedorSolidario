<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Guardar producto"/>
<%@ include file="/views/common/header.jspf" %>
<div class="page-heading"><p class="eyebrow">Administración</p><h1>${item.id == 0 ? 'Registrar producto' : 'Editar producto'}</h1></div>
<form class="panel row g-3" method="post" action="${ctx}/admin/productos">
    <input type="hidden" name="csrf" value="${sessionScope.csrf}">
    <input type="hidden" name="accion" value="guardar">
    <input type="hidden" name="id" value="${item.id}">
    <div class="col-md-6">
        <label class="form-label" for="nombre">Nombre del producto</label>
        <input class="form-control" id="nombre" name="nombre" type="text" maxlength="100" required value='<c:out value="${pageContext.request.method eq 'POST' ? param.nombre : item.nombre}"/>'>
    </div>
    <div class="col-md-3">
        <label class="form-label" for="unidad">Unidad asignada</label>
        <c:set var="unidadActual" value="${pageContext.request.method eq 'POST' ? param.unidad : item.unidad}"/>
        <select class="form-select" id="unidad" name="unidad" required>
            <option value="kg" ${unidadActual == 'kg' ? 'selected' : ''}>Kg (kilogramos)</option>
            <option value="litros" ${unidadActual == 'litros' ? 'selected' : ''}>Litros</option>
            <option value="unidades" ${unidadActual == 'unidades' ? 'selected' : ''}>Unidades</option>
            <option value="cajas" ${unidadActual == 'cajas' ? 'selected' : ''}>Cajas</option>
        </select>
    </div>
    <div class="col-md-3">
        <label class="form-label" for="estado">Estado</label>
        <select class="form-select" id="estado" name="estado" required>
            <option value="ACTIVO" ${(pageContext.request.method eq 'POST' ? param.estado : item.estado) == 'ACTIVO' ? 'selected' : ''}>ACTIVO</option>
            <option value="INACTIVO" ${(pageContext.request.method eq 'POST' ? param.estado : item.estado) == 'INACTIVO' ? 'selected' : ''}>INACTIVO</option>
        </select>
    </div>
    <div class="col-12">
        <label class="form-label" for="descripcion">Descripción</label>
        <input class="form-control" id="descripcion" name="descripcion" type="text" maxlength="1000" required value='<c:out value="${pageContext.request.method eq 'POST' ? param.descripcion : item.descripcion}"/>'>
    </div>
    <div class="col-12">
        <button class="btn btn-primary" type="submit">Guardar</button>
        <a class="btn btn-outline-secondary" href="${ctx}/admin/productos">Volver</a>
    </div>
</form><%@ include file="/views/common/footer.jspf" %>
