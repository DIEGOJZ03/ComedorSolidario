<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Solicitudes de recojo"/>
<%@ include file="/views/common/header.jspf" %>
<div class="page-heading">
<h1>Solicitudes de recojo</h1></div>
<p>Solicitado no significa confirmado. Solo el administrador verifica el recojo y la recepción en el comedor.</p>
<form method="get" class="panel row g-3">
<div class="col-md-6"><label class="form-label" for="distrito">Filtrar distrito</label><input class="form-control" id="distrito" name="distrito" type="text" maxlength="100"  value='<c:out value="${param.distrito}"/>'></div>
<div class="col-md-6"><label for="estado" class="form-label">Estado</label><select class="form-select" name="estado" id="estado"><option value="">Todos</option><c:forTokens var="e" items="SOLICITADO,PROGRAMADO,RECOGIDO,COMPLETADO,RECHAZADO,CANCELADO,INCUMPLIDO" delims=","><option ${param.estado == e ? 'selected' : ''}>${e}</option></c:forTokens></select></div>
<div><button class="btn btn-primary">Filtrar</button></div></form>
<c:forEach var="s" items="${recojos}">
<section class="panel my-3">
<h2 class="h4">Solicitud #${s.id} · Donación #${s.donacionId}</h2>
<span class="badge text-bg-secondary"><c:out value="${s.estado}"/></span>
<p>Donador: <c:out value="${s.donador}"/> · <c:out value="${s.email}"/></p>
<p>Aporte: <c:out value="${s.descripcion}"/> → <c:out value="${s.comedor}"/></p>
<p>Dirección: <c:out value="${s.direccion}"/> · <c:out value="${s.distrito}"/> · <c:out value="${s.provincia}"/> · Zona <c:out value="${s.zona}"/></p>
<p>Referencia: <c:out value="${s.referencia}"/> · Teléfono <c:out value="${s.telefono}"/></p>
<p>Preferencia: <c:out value="${s.fechaPreferida}"/> / <c:out value="${s.horarioPreferido}"/></p>
<p>Programación: <c:out value="${s.fechaProgramada}"/> / <c:out value="${s.horarioProgramado}"/> · Responsable <c:out value="${s.responsable}"/></p>
<p>Observaciones: <c:out value="${s.observaciones}"/></p>
<c:if test="${administrador && (s.estado == 'SOLICITADO' || s.estado == 'PROGRAMADO' || s.estado == 'RECOGIDO')}">
<form method="post" action="${ctx}/admin/logistica/recojos" class="row g-3" data-confirm><input type="hidden" name="csrf" value="${sessionScope.csrf}"><input type="hidden" name="id" value="${s.id}">
<div class="col-md-6"><label class="form-label" for="fecha-${s.id}">Fecha definitiva</label><input class="form-control" id="fecha-${s.id}" name="fecha" type="date" maxlength="200"  value='<c:out value="${s.fechaProgramada}"/>'></div>
<div class="col-md-6"><label class="form-label" for="horario-${s.id}">Horario definitivo</label><input class="form-control" id="horario-${s.id}" name="horario" type="text" maxlength="200"  value='<c:out value="${s.horarioProgramado}"/>'></div>
<div class="col-md-6"><label class="form-label" for="responsable-${s.id}">Responsable del recojo</label><input class="form-control" id="responsable-${s.id}" name="responsable" type="text" maxlength="120"  value='<c:out value="${s.responsable}"/>'></div>
<div class="col-md-6"><label class="form-label">Recomendar un punto de acopio (opcional)</label><select class="form-select" name="recomendado"><option value="">Ninguno</option>
<c:forEach var="p" items="${puntos}"><option value="${p.id}"><c:out value="${p.nombre}"/> · <c:out value="${p.distrito}"/></option></c:forEach></select></div>
<div class="col-12"><label class="form-label">Observaciones / motivo de rechazo, cancelación o incumplimiento</label><textarea class="form-control" name="observaciones" maxlength="1000"><c:out value="${s.observaciones}"/></textarea></div>
<div class="d-flex flex-wrap gap-2">
<c:if test="${s.estado == 'SOLICITADO'}"><button class="btn btn-outline-primary" name="accion" value="aceptar">Aceptar solicitud</button><button class="btn btn-primary" name="accion" value="programar">Programar recojo</button></c:if>
<c:if test="${s.estado == 'PROGRAMADO'}"><button class="btn btn-primary" name="accion" value="recoger">Confirmar recogido físicamente</button></c:if>
<c:if test="${s.estado == 'RECOGIDO'}"><button class="btn btn-success" name="accion" value="completar">Confirmar recibido en el comedor</button></c:if>
<c:if test="${s.estado != 'RECOGIDO'}"><button class="btn btn-outline-danger" name="accion" value="rechazar">Rechazar</button><button class="btn btn-outline-secondary" name="accion" value="cancelar">Cancelar</button><button class="btn btn-outline-danger" name="accion" value="incumplir">Registrar incumplimiento</button></c:if></div></form></c:if></section></c:forEach>
<c:if test="${empty recojos}">
<p>No hay solicitudes con estos filtros.</p></c:if><%@ include file="/views/common/footer.jspf" %>
