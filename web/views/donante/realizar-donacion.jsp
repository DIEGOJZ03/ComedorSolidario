<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Realizar donación"/>
<%@ include file="/views/common/header.jspf" %>
<div class="page-heading">
    <p class="eyebrow">Un nuevo aporte</p>
    <h1>Registra tu donación</h1>
    <p class="text-secondary">Selecciona un comedor y cuéntanos cómo deseas ayudar.</p>
</div>
<c:choose>
    <c:when test="${empty comedores}">
        <div class="alert alert-info">No hay comedores activos. <a href="${ctx}/donante/dashboard">Volver a mi panel</a></div>
    </c:when>
    <c:otherwise>
        <form method="post" action="${ctx}/donante/donaciones/nueva" class="panel row g-3" >
            <input type="hidden" name="csrf" value="${sessionScope.csrf}">
            <div class="col-12">
                <label class="form-label" for="comedorId">Comedor</label>
                <select class="form-select" id="comedorId" name="comedorId" required>
                    <option value="">Selecciona un comedor</option>
                    <c:forEach var="c" items="${comedores}">
                        <option value="${c.id}" ${(param.comedorId) == c.id ? 'selected' : ''}><c:out value="${c.nombre}"/> · <c:out value="${c.distrito}"/></option>
                    </c:forEach>
                </select>
            </div>
            <div class="col-md-6">
                <label class="form-label" for="tipo">Tipo de donación</label>
                <select class="form-select" id="tipo" name="tipo" required>
                    <option value="ALIMENTOS" ${(param.tipo) == 'ALIMENTOS' ? 'selected' : ''}>Alimentos</option>
                    <option value="UTENSILIOS" ${(param.tipo) == 'UTENSILIOS' ? 'selected' : ''}>Utensilios</option>
                    <option value="DINERO" ${(param.tipo) == 'DINERO' ? 'selected' : ''}>Dinero</option>
                    <option value="OTROS" ${(param.tipo) == 'OTROS' ? 'selected' : ''}>Otros</option>
                </select>
            </div>
            <div class="col-md-6">
                <label class="form-label" for="cantidad">Cantidad</label>
                <input class="form-control" id="cantidad" name="cantidad" type="number" min="0.01" max="9999999999.99" step="0.01" required value='<c:out value="${param.cantidad}"/>'>
            </div>
            <div class="col-md-6">
                <label class="form-label" for="unidad">Unidad</label>
                <select class="form-select" id="unidad" name="unidad" required>
                    <option value="kg" ${(param.unidad) == 'kg' ? 'selected' : ''}>Kg</option>
                    <option value="litros" ${(param.unidad) == 'litros' ? 'selected' : ''}>Litros</option>
                    <option value="unidades" ${(param.unidad) == 'unidades' ? 'selected' : ''}>Unidades</option>
                    <option value="cajas" ${(param.unidad) == 'cajas' ? 'selected' : ''}>Cajas</option>
                    <option value="soles" ${(param.unidad) == 'soles' ? 'selected' : ''}>Soles</option>
                </select>
            </div>
            <div class="col-12">
                <label class="form-label" for="descripcion">Describe tu aporte</label>
                <textarea class="form-control" id="descripcion" name="descripcion" rows="3" maxlength="1000" required><c:out value="${param.descripcion}"/></textarea>
            </div>
            <p class="small text-secondary">El aporte se registrará como PENDIENTE. Coordina la entrega con el comedor. Si eliges dinero, registra el importe en soles: esta página no procesa pagos.</p>
            <div class="col-12 d-flex gap-2 mt-4">
                <button class="btn btn-primary" type="submit">Registrar donación</button>
                <a class="btn btn-outline-secondary" href="${ctx}/donante/donaciones">Volver</a>
            </div>
        </form>
    </c:otherwise>
</c:choose>
<%@ include file="/views/common/footer.jspf" %>
