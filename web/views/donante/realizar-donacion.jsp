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
        <form method="post" action="${ctx}/donante/donaciones/nueva" class="panel row g-3" id="formRealizarDonacion">
            <input type="hidden" name="csrf" value="${sessionScope.csrf}">
            <div class="col-12" id="contenedor-donaciones">
                <div class="donacion-bloque" data-donacion-item>
                    <div class="d-flex justify-content-between align-items-center mb-3 donacion-bloque-header" hidden>
                        <h2 class="h5 mb-0" data-donacion-titulo>Donación #1</h2>
                        <button type="button" class="btn btn-outline-danger btn-sm" data-quitar-donacion hidden>Quitar</button>
                    </div>
                    <div class="row g-3">
                        <div class="col-12">
                            <label class="form-label" for="comedorId">Comedor</label>
                            <select class="form-select" id="comedorId" name="comedorId" required>
                                <option value="">Selecciona un comedor</option>
                                <c:forEach var="c" items="${comedores}">
                                    <option value="${c.id}" ${(param.comedorId) == c.id ? 'selected' : ''}><c:out value="${c.nombre}"/> · <c:out value="${c.distrito}"/></option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-4">
                            <label class="form-label" for="tipo">Tipo de donación</label>
                            <select class="form-select" id="tipo" name="tipo" data-tipo-cantidad required>
                                <option value="ALIMENTOS" ${(param.tipo) == 'ALIMENTOS' ? 'selected' : ''}>Alimentos</option>
                                <option value="UTENSILIOS" ${(param.tipo) == 'UTENSILIOS' ? 'selected' : ''}>Utensilios</option>
                                <option value="DINERO" ${(param.tipo) == 'DINERO' ? 'selected' : ''}>Dinero</option>
                                <option value="OTROS" ${(param.tipo) == 'OTROS' ? 'selected' : ''}>Otros</option>
                            </select>
                        </div>
                        <div class="col-md-4">
                            <label class="form-label" for="unidad">Unidad</label>
                            <select class="form-select" id="unidad" name="unidad" data-unidad-cantidad required>
                                <option value="kg" ${(param.unidad) == 'kg' ? 'selected' : ''}>Kg</option>
                                <option value="litros" ${(param.unidad) == 'litros' ? 'selected' : ''}>Litros</option>
                                <option value="unidades" ${(param.unidad) == 'unidades' ? 'selected' : ''}>Unidades</option>
                                <option value="cajas" ${(param.unidad) == 'cajas' ? 'selected' : ''}>Cajas</option>
                                <option value="soles" ${(param.unidad) == 'soles' ? 'selected' : ''}>Soles</option>
                            </select>
                        </div>
                        <div class="col-md-4">
                            <label class="form-label" for="cantidad" data-label-cantidad>Cantidad (máx. ${param.unidad == 'soles' ? '5,000' : '500'})</label>
                            <input class="form-control" id="cantidad" name="cantidad" type="number" data-input-cantidad min="0.01" max="${param.unidad == 'soles' ? '5000' : '500'}" step="0.01" required value='<c:out value="${param.cantidad}"/>'>
                        </div>
                        <div class="col-12">
                            <label class="form-label" for="descripcion">Describe tu aporte</label>
                            <textarea class="form-control" id="descripcion" name="descripcion" rows="3" maxlength="1000" required><c:out value="${param.descripcion}"/></textarea>
                        </div>
                    </div>
                </div>
            </div>
            <c:choose>
                <c:when test="${puedeDonarMultiple}">
                    <div class="col-12 d-flex flex-wrap justify-content-between align-items-center gap-2 pt-2 border-top">
                        <span class="small text-secondary">Ya tienes <strong>${totalDonacionesPrevias}</strong> donación(es) registrada(s). Puedes agregar más de 1 donación en esta misma ventana (hasta 5 por envío).</span>
                        <button type="button" class="btn btn-outline-primary btn-sm" id="btnAgregarDonacion">+ Agregar otra donación en esta ventana</button>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="col-12">
                        <p class="small text-secondary mb-0">Después de registrar tu primera donación, se habilitará aquí un botón para hacer más de 1 donación en la misma ventana.</p>
                    </div>
                </c:otherwise>
            </c:choose>
            <div class="col-12"><label class="form-label" for="modalidad">¿Cómo entregarás tu aporte?</label><select class="form-select" name="modalidad" id="modalidad" required><option value="DIRECTA">Entrega directa al comedor</option><option value="ACOPIO" ${param.modalidad == 'ACOPIO' ? 'selected' : ''}>Dejar en un punto de acopio</option><option value="RECOJO" ${param.modalidad == 'RECOJO' ? 'selected' : ''}>Solicitar recojo a domicilio</option></select><p class="small">El administrador confirma la recepción de cada aporte.</p></div>
            <fieldset class="col-12" data-modalidad="ACOPIO"><legend class="h5">Punto de acopio</legend><select name="puntoAcopioId" class="form-select" aria-label="Punto de acopio"><option value="">Selecciona</option><c:forEach var="p" items="${puntos}"><option value="${p.id}" ${param.puntoAcopioId == p.id ? 'selected' : ''}><c:out value="${p.nombre}"/> · <c:out value="${p.direccion}"/> · <c:out value="${p.distrito}"/> · <c:out value="${p.horarios}"/></option></c:forEach></select><a href="${ctx}/acopio/puntos" target="_blank" rel="noopener">Ver dirección, horarios y características</a></fieldset>
            <fieldset class="col-12" data-modalidad="RECOJO"><legend class="h5">Datos para solicitar recojo</legend><p>La disponibilidad será evaluada. Enviar la solicitud no confirma el recojo.</p><div class="row g-3"><div class="col-md-6"><label class="form-label" for="direccion">Dirección</label><input class="form-control" id="direccion" name="direccion" type="text" maxlength="200"  value='<c:out value="${param.direccion}"/>'></div><div class="col-md-6"><label class="form-label" for="provincia">Provincia</label><input class="form-control" id="provincia" name="provincia" type="text" maxlength="100"  value='<c:out value="${param.provincia}"/>'></div><div class="col-md-6"><label class="form-label" for="distrito">Distrito</label><input class="form-control" id="distrito" name="distrito" type="text" maxlength="100"  value='<c:out value="${param.distrito}"/>'></div><div class="col-md-6"><label class="form-label" for="zona">Zona</label><input class="form-control" id="zona" name="zona" type="text" maxlength="80"  value='<c:out value="${param.zona}"/>'></div><div class="col-md-6"><label class="form-label" for="referencia">Referencia</label><input class="form-control" id="referencia" name="referencia" type="text" maxlength="250"  value='<c:out value="${param.referencia}"/>'></div><div class="col-md-6"><label class="form-label" for="horarioPreferido">Horario disponible</label><input class="form-control" id="horarioPreferido" name="horarioPreferido" type="text" maxlength="200"  value='<c:out value="${param.horarioPreferido}"/>'></div><div class="col-md-6"><label class="form-label" for="fechaPreferida">Fecha preferida</label><input class="form-control" id="fechaPreferida" name="fechaPreferida" type="date" maxlength="200"  value='<c:out value="${param.fechaPreferida}"/>'></div><div class="col-md-6"><label class="form-label" for="telefono">Teléfono celular de contacto (9 dígitos)</label><input class="form-control" id="telefono" name="telefono" type="tel" pattern="9[0-9]{8}" minlength="9" maxlength="9" inputmode="numeric" placeholder="987654321" title="Escribe un número celular válido de 9 dígitos que empiece con 9 (ej. 987654321)" value='<c:out value="${empty param.telefono ? perfil.telefono : param.telefono}"/>'></div></div></fieldset>
            <p class="small text-secondary">El aporte se registrará como PENDIENTE. Coordina la entrega con el comedor. Si eliges dinero, registra el importe en soles (máx. 5,000); para unidades físicas el máximo es 500.</p>
            <div class="col-12 d-flex gap-2 mt-4">
                <button class="btn btn-primary" type="submit" id="btnSubmitDonacion">Registrar donación</button>
                <a class="btn btn-outline-secondary" href="${ctx}/donante/donaciones">Volver</a>
            </div>
        </form>
    </c:otherwise>
</c:choose>
<script src="${ctx}/assets/js/logistica.js" defer></script>
<%@ include file="/views/common/footer.jspf" %>
