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
<div class="col-12"><label class="form-label" for="modalidad">¿Cómo entregarás tu aporte?</label><select class="form-select" name="modalidad" id="modalidad" required><option value="DIRECTA">Entrega directa al comedor</option><option value="ACOPIO" ${param.modalidad == 'ACOPIO' ? 'selected' : ''}>Dejar en un punto de acopio</option><option value="RECOJO" ${param.modalidad == 'RECOJO' ? 'selected' : ''}>Solicitar recojo a domicilio</option></select><p class="small">Máximo 2 aportes activos pendientes, aceptados o programados. El administrador confirma la recepción.</p></div>
<fieldset class="col-12" data-modalidad="ACOPIO"><legend class="h5">Punto de acopio</legend><select name="puntoAcopioId" class="form-select" aria-label="Punto de acopio"><option value="">Selecciona</option><c:forEach var="p" items="${puntos}"><option value="${p.id}" ${param.puntoAcopioId == p.id ? 'selected' : ''}><c:out value="${p.nombre}"/> · <c:out value="${p.direccion}"/> · <c:out value="${p.distrito}"/> · <c:out value="${p.horarios}"/></option></c:forEach></select><a href="${ctx}/acopio/puntos" target="_blank" rel="noopener">Ver dirección, horarios y características</a></fieldset>
<fieldset class="col-12" data-modalidad="RECOJO"><legend class="h5">Datos para solicitar recojo</legend><p>La disponibilidad será evaluada. Enviar la solicitud no confirma el recojo.</p><div class="row g-3"><div class="col-md-6"><label class="form-label" for="direccion">Dirección</label><input class="form-control" id="direccion" name="direccion" type="text" maxlength="200"  value='<c:out value="${param.direccion}"/>'></div><div class="col-md-6"><label class="form-label" for="provincia">Provincia</label><input class="form-control" id="provincia" name="provincia" type="text" maxlength="100"  value='<c:out value="${param.provincia}"/>'></div><div class="col-md-6"><label class="form-label" for="distrito">Distrito</label><input class="form-control" id="distrito" name="distrito" type="text" maxlength="100"  value='<c:out value="${param.distrito}"/>'></div><div class="col-md-6"><label class="form-label" for="zona">Zona</label><input class="form-control" id="zona" name="zona" type="text" maxlength="80"  value='<c:out value="${param.zona}"/>'></div><div class="col-md-6"><label class="form-label" for="referencia">Referencia</label><input class="form-control" id="referencia" name="referencia" type="text" maxlength="250"  value='<c:out value="${param.referencia}"/>'></div><div class="col-md-6"><label class="form-label" for="horarioPreferido">Horario disponible</label><input class="form-control" id="horarioPreferido" name="horarioPreferido" type="text" maxlength="200"  value='<c:out value="${param.horarioPreferido}"/>'></div><div class="col-md-6"><label class="form-label" for="fechaPreferida">Fecha preferida</label><input class="form-control" id="fechaPreferida" name="fechaPreferida" type="date" maxlength="200"  value='<c:out value="${param.fechaPreferida}"/>'></div><div class="col-md-6"><label class="form-label" for="telefono">Teléfono de contacto</label><input class="form-control" id="telefono" name="telefono" type="text" maxlength="20"  value='<c:out value="${empty param.telefono ? perfil.telefono : param.telefono}"/>'></div></div></fieldset>
            <p class="small text-secondary">El aporte se registrará como PENDIENTE. Coordina la entrega con el comedor. Si eliges dinero, registra el importe en soles: esta página no procesa pagos.</p>
            <div class="col-12 d-flex gap-2 mt-4">
                <button class="btn btn-primary" type="submit">Registrar donación</button>
                <a class="btn btn-outline-secondary" href="${ctx}/donante/donaciones">Volver</a>
            </div>
        </form>
    </c:otherwise>
</c:choose>
<script src="${ctx}/assets/js/logistica.js" defer></script>
<%@ include file="/views/common/footer.jspf" %>
