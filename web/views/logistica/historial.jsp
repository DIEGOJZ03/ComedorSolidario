<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Historial de donaciones"/>
<%@ include file="/views/common/header.jspf" %>
<div class="page-heading">
<h1>Historial de donaciones</h1></div>
<p>Incluye aportes activos e inactivos, rechazos e incumplimientos. Los cambios de estado corresponden a la administración.</p>
<div class="panel table-responsive">
<table class="table">
<thead>
<tr><th>Aporte</th><th>Comedor</th><th>Modalidad / punto</th><th>Estado / vigencia</th><th>Recojo</th><th>Motivo</th></tr></thead>
<tbody>
<c:forEach var="d" items="${donaciones}">
<tr><td>#${d.id} · <c:out value="${d.descripcion}"/></td><td><c:out value="${d.comedorNombre}"/></td><td><c:out value="${d.modalidad}"/> <c:out value="${d.puntoAcopioNombre}"/></td><td><c:out value="${d.estado}"/> / ${d.activo ? 'ACTIVA' : 'INACTIVA'}</td><td><c:out value="${d.estadoRecojo}"/></td><td><c:out value="${d.motivo}"/></td></tr></c:forEach></tbody></table></div>
<section class="panel mt-3">
<h2 class="h4">Seguimiento registrado</h2>
<c:forEach var="e" items="${eventos}">
<p><c:out value="${e.fecha}"/> · #${e.donacion_id} · <c:out value="${e.estado}"/> · <c:out value="${e.motivo}"/></p></c:forEach></section>
<c:if test="${administrador && cuenta.rol == 'DONADOR'}">
<section class="panel mt-3">
<h2 class="h4">Revisión de cuenta: <c:out value="${cuenta.nombre}"/></h2>
<p>No hay suspensión automática. Revisa el comportamiento antes de tomar una decisión.</p>
<form method="post" action="${ctx}/admin/logistica/cuenta" class="row g-3" data-confirm><input type="hidden" name="csrf" value="${sessionScope.csrf}"><input type="hidden" name="usuarioId" value="${cuenta.id}">
<div class="col-md-6"><label>Decisión</label><select class="form-select" name="estado"><option value="INACTIVO">Desactivar</option><option value="ACTIVO">Reactivar</option></select></div>
<div class="col-md-6"><label class="form-label" for="motivo">Motivo de la decisión</label><input class="form-control" id="motivo" name="motivo" type="text" maxlength="1000" required value=''></div>
<div><button class="btn btn-primary">Registrar decisión</button></div></form>
<c:forEach var="e" items="${cambiosCuenta}">
<p><c:out value="${e.fecha}"/> · <c:out value="${e.estado}"/> · <c:out value="${e.motivo}"/></p></c:forEach></section></c:if><%@ include file="/views/common/footer.jspf" %>
