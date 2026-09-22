<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Formulario de necesidad"/>
<%@ include file="/views/common/header.jspf" %>
<div class="page-heading">
    <p class="eyebrow">Administración</p>
    <h1>${necesidad.id == 0 ? "Registrar necesidad" : "Editar necesidad"}</h1>
    <p class="text-secondary">Indica el producto, la unidad y su prioridad.</p>
</div>
<form method="post" action="${ctx}/admin/necesidades" class="panel row g-3" >
    <input type="hidden" name="csrf" value="${sessionScope.csrf}">
    <input type="hidden" name="accion" value="guardar">
    <input type="hidden" name="id" value="${necesidad.id}">
    <div class="col-12">
        <label class="form-label" for="comedorId">Comedor</label>
        <select class="form-select" id="comedorId" name="comedorId" required>
            <option value="">Selecciona un comedor</option>
            <c:forEach var="c" items="${comedores}">
                <option value="${c.id}" ${(pageContext.request.method eq 'POST' ? param.comedorId : necesidad.comedorId) == c.id ? 'selected' : ''}><c:out value="${c.nombre}"/> · <c:out value="${c.distrito}"/></option>
            </c:forEach>
        </select>
    </div>
    <div class="col-md-6">
        <label class="form-label" for="nombre">Nombre de la necesidad</label>
        <input class="form-control" id="nombre" name="nombre" type="text" maxlength="100" required value='<c:out value="${pageContext.request.method eq 'POST' ? param.nombre : necesidad.nombre}"/>'>
    </div>
    <div class="col-md-6">
        <label class="form-label" for="cantidadNecesaria">Cantidad necesaria</label>
        <input class="form-control" id="cantidadNecesaria" name="cantidadNecesaria" type="number" min="0.01" max="9999999999.99" step="0.01" required value='<c:out value="${pageContext.request.method eq 'POST' ? param.cantidadNecesaria : necesidad.cantidadNecesaria}"/>'>
    </div>
    <div class="col-md-6">
        <label class="form-label" for="unidad">Unidad</label>
        <select class="form-select" id="unidad" name="unidad" required>
            <option value="kg" ${(pageContext.request.method eq 'POST' ? param.unidad : necesidad.unidad) == 'kg' ? 'selected' : ''}>Kg</option>
            <option value="litros" ${(pageContext.request.method eq 'POST' ? param.unidad : necesidad.unidad) == 'litros' ? 'selected' : ''}>Litros</option>
            <option value="unidades" ${(pageContext.request.method eq 'POST' ? param.unidad : necesidad.unidad) == 'unidades' ? 'selected' : ''}>Unidades</option>
            <option value="cajas" ${(pageContext.request.method eq 'POST' ? param.unidad : necesidad.unidad) == 'cajas' ? 'selected' : ''}>Cajas</option>
            <option value="soles" ${(pageContext.request.method eq 'POST' ? param.unidad : necesidad.unidad) == 'soles' ? 'selected' : ''}>Soles</option>
        </select>
    </div>
    <div class="col-md-6">
        <label class="form-label" for="prioridad">Prioridad</label>
        <select class="form-select" id="prioridad" name="prioridad" required>
            <option value="ALTA" ${(pageContext.request.method eq 'POST' ? param.prioridad : necesidad.prioridad) == 'ALTA' ? 'selected' : ''}>Alta</option>
            <option value="MEDIA" ${(pageContext.request.method eq 'POST' ? param.prioridad : necesidad.prioridad) == 'MEDIA' ? 'selected' : ''}>Media</option>
            <option value="BAJA" ${(pageContext.request.method eq 'POST' ? param.prioridad : necesidad.prioridad) == 'BAJA' ? 'selected' : ''}>Baja</option>
        </select>
    </div>
    <div class="col-md-6">
        <label class="form-label" for="estado">Estado</label>
        <select class="form-select" id="estado" name="estado" required>
            <option value="ACTIVA" ${(pageContext.request.method eq 'POST' ? param.estado : necesidad.estado) == 'ACTIVA' ? 'selected' : ''}>Activa</option>
            <option value="INACTIVA" ${(pageContext.request.method eq 'POST' ? param.estado : necesidad.estado) == 'INACTIVA' ? 'selected' : ''}>Inactiva</option>
        </select>
    </div>
    <div class="col-12">
        <label class="form-label" for="descripcion">Descripción</label>
        <textarea class="form-control" id="descripcion" name="descripcion" rows="3" maxlength="1000" required><c:out value="${pageContext.request.method eq 'POST' ? param.descripcion : necesidad.descripcion}"/></textarea>
    </div>
    <div class="col-12 d-flex gap-2 mt-4">
        <button class="btn btn-primary" type="submit">Guardar necesidad</button>
        <a class="btn btn-outline-secondary" href="${ctx}/admin/necesidades">Volver</a>
    </div>
</form>
<%@ include file="/views/common/footer.jspf" %>
