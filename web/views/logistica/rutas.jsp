<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Rutas de recojo"/>
<%@ include file="/views/common/header.jspf" %>
<div class="page-heading">
<h1>Rutas de recojo</h1></div>
<p>Agrupación por distrito y fecha, con horario y responsable. Revisa la disponibilidad de cada parada. El orden es manual; no se calcula una ruta geográfica óptima.</p>
<div class="panel table-responsive">
<table class="table">
<thead>
<tr><th>Ruta</th><th>Fecha / horario</th><th>Distrito</th><th>Responsable</th><th>Estado</th><th>Observaciones</th><th>Acciones</th></tr></thead>
<tbody>
<c:forEach var="r" items="${rutas}">
<tr><td><a href="${ctx}/admin/logistica/rutas?id=${r.id}">#${r.id}</a></td><td><c:out value="${r.fecha}"/> / <c:out value="${r.horario}"/></td><td><c:out value="${r.distrito}"/></td><td><c:out value="${r.responsable}"/></td><td><c:out value="${r.estado}"/></td><td><c:out value="${r.observaciones}"/></td><td>
<c:if test="${r.estado == 'PLANIFICADA' || r.estado == 'EN_PROCESO'}">
<form method="post" action="${ctx}/admin/logistica/rutas" data-confirm><input type="hidden" name="csrf" value="${sessionScope.csrf}"><input type="hidden" name="accion" value="estado"><input type="hidden" name="id" value="${r.id}"><button class="btn btn-sm btn-primary" name="estado" value="${r.estado == 'PLANIFICADA' ? 'EN_PROCESO' : 'COMPLETADA'}">${r.estado == 'PLANIFICADA' ? 'Iniciar' : 'Completar'}</button><button class="btn btn-sm btn-outline-danger" name="estado" value="CANCELADA">Cancelar ruta</button></form></c:if></td></tr></c:forEach></tbody></table></div>
<c:if test="${not empty param.id}">
<section class="panel my-3">
<h2 class="h4">Paradas de la ruta #<c:out value="${param.id}"/></h2>
<c:forEach var="p" items="${paradas}">
<div class="border-bottom py-3">
<p>${p.orden}. <c:out value="${p.donador}"/> — <c:out value="${p.direccion}"/> · <c:out value="${p.distrito}"/> · <c:out value="${p.horario}"/> · <c:out value="${p.estado}"/></p>
<form method="post" action="${ctx}/admin/logistica/rutas" class="d-flex gap-2"><input type="hidden" name="csrf" value="${sessionScope.csrf}"><input type="hidden" name="id" value="${param.id}"><input type="hidden" name="solicitudId" value="${p.solicitudId}"><input type="hidden" name="mixto" value="on"><input type="hidden" name="horariosConfirmados" value="on"><input aria-label="Orden" class="form-control w-auto" type="number" min="1" name="orden" value="${p.orden}" required><button class="btn btn-outline-primary" name="accion" value="parada">Cambiar orden</button><button class="btn btn-outline-danger" name="accion" value="quitar">Quitar de la ruta</button></form></div></c:forEach>
<h3 class="h5 mt-3">Agregar solicitud programada</h3>
<form method="get" class="row g-2"><input type="hidden" name="id" value="${param.id}">
<div class="col-md-6"><label class="form-label" for="distrito">Filtrar candidatos por distrito</label><input class="form-control" id="distrito" name="distrito" type="text" maxlength="100"  value='<c:out value="${param.distrito}"/>'></div>
<div><button class="btn btn-outline-secondary">Filtrar</button></div></form>
<form method="post" action="${ctx}/admin/logistica/rutas" class="row g-3 mt-2"><input type="hidden" name="csrf" value="${sessionScope.csrf}"><input type="hidden" name="id" value="${param.id}"><input type="hidden" name="accion" value="parada">
<div class="col-12"><label>Solicitud / fecha / horario / dirección / responsable</label><select class="form-select" name="solicitudId" required><option value="">Selecciona</option>
<c:forEach var="s" items="${candidatos}"><option value="${s.id}">#${s.id} · <c:out value="${s.fechaProgramada}"/> · <c:out value="${s.horarioProgramado}"/> · <c:out value="${s.distrito}"/> · <c:out value="${s.direccion}"/> · <c:out value="${s.responsable}"/></option></c:forEach></select></div>
<div class="col-md-6"><label class="form-label" for="orden">Orden de parada</label><input class="form-control" id="orden" name="orden" type="number" min="1" required value=''></div>
<div><label><input type="checkbox" name="mixto"> Autorizar inclusión de otro distrito</label></div>
<div><label><input type="checkbox" name="horariosConfirmados" required> Revisé la compatibilidad del horario de la parada con esta ruta</label></div><div><button class="btn btn-primary">Agregar parada</button></div></form></section></c:if>
<section class="panel mt-4">
<h2 class="h4">Crear ruta</h2>
<form method="post" action="${ctx}/admin/logistica/rutas" class="row g-3"><input type="hidden" name="csrf" value="${sessionScope.csrf}"><input type="hidden" name="accion" value="crear">
<div class="col-md-6"><label class="form-label" for="fecha">Fecha</label><input class="form-control" id="fecha" name="fecha" type="date" maxlength="200" required value=''></div>
<div class="col-md-6"><label class="form-label" for="distritoRuta">Distrito principal</label><input class="form-control" id="distritoRuta" name="distrito" type="text" maxlength="100" required value=''></div>
<div class="col-md-6"><label class="form-label" for="horario">Ventana horaria</label><input class="form-control" id="horario" name="horario" type="text" maxlength="200" required value=''></div>
<div class="col-md-6"><label class="form-label" for="responsable">Responsable</label><input class="form-control" id="responsable" name="responsable" type="text" maxlength="120" required value=''></div>
<div class="col-md-6"><label class="form-label" for="observaciones">Observaciones</label><input class="form-control" id="observaciones" name="observaciones" type="text" maxlength="1000"  value=''></div>
<div><button class="btn btn-primary">Crear ruta</button></div></form></section><%@ include file="/views/common/footer.jspf" %>
