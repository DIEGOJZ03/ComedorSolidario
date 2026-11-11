<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Puntos de acopio"/>
<%@ include file="/views/common/header.jspf" %>
<div class="page-heading">
<h1>Puntos de acopio</h1></div>

<p>Consulta un lugar donde dejar tu aporte. La entrega se confirma cuando llega al comedor.</p>
<div class="row g-3">
<c:forEach var="p" items="${puntos}">
<div class="col-lg-6"><article class="panel h-100">
<h2 class="h4"><c:out value="${p.nombre}"/></h2>
<p><c:out value="${p.direccion}"/> · <c:out value="${p.distrito}"/> · <c:out value="${p.provincia}"/></p>
<p>Zona: <c:out value="${p.zona}"/> · Referencia: <c:out value="${p.referencia}"/></p>
<p>Horario: <c:out value="${p.horarios}"/></p>
<p>Responsable: <c:out value="${p.responsable}"/> · <c:out value="${p.telefono}"/></p>
<span class="badge text-bg-secondary"><c:out value="${p.estado}"/></span>
<c:if test="${administrador}"><a class="btn btn-outline-primary" href="${ctx}/admin/logistica/puntos?id=${p.id}#editar">Editar / ver</a>
<form method="post" action="${ctx}/admin/logistica/puntos" class="d-flex gap-2 mt-2" data-confirm><input type="hidden" name="csrf" value="${sessionScope.csrf}"><input type="hidden" name="id" value="${p.id}"><input type="hidden" name="estado" value="${p.estado == 'ACTIVO' ? 'INACTIVO' : 'ACTIVO'}"><button class="btn btn-outline-secondary" name="accion" value="estado">${p.estado == 'ACTIVO' ? 'Desactivar' : 'Reactivar'}</button><button class="btn btn-outline-danger" name="accion" value="eliminar">Eliminar</button></form></c:if>
</article></div></c:forEach></div>
<c:if test="${empty puntos}">
<p>No hay puntos disponibles.</p></c:if>
<c:if test="${administrador}">
<section id="editar" class="panel mt-4">
<h2 class="h4">${empty punto ? 'Registrar punto' : 'Editar punto'}</h2>
<form method="post" action="${ctx}/admin/logistica/puntos" class="row g-3"><input type="hidden" name="csrf" value="${sessionScope.csrf}"><input type="hidden" name="accion" value="guardar"><input type="hidden" name="id" value="${punto.id}">
<div class="col-md-6"><label class="form-label" for="nombre">Nombre</label><input class="form-control" id="nombre" name="nombre" type="text" maxlength="120" required value='<c:out value="${punto.nombre}"/>'></div>
<div class="col-md-6"><label class="form-label" for="direccion">Dirección</label><input class="form-control" id="direccion" name="direccion" type="text" maxlength="200" required value='<c:out value="${punto.direccion}"/>'></div>
<div class="col-md-6"><label class="form-label" for="provincia">Provincia</label><input class="form-control" id="provincia" name="provincia" type="text" maxlength="100" required value='<c:out value="${punto.provincia}"/>'></div>
<div class="col-md-6"><label class="form-label" for="distrito">Distrito</label><input class="form-control" id="distrito" name="distrito" type="text" maxlength="100" required value='<c:out value="${punto.distrito}"/>'></div>
<div class="col-md-6"><label class="form-label" for="zona">Zona</label><input class="form-control" id="zona" name="zona" type="text" maxlength="80" required value='<c:out value="${punto.zona}"/>'></div>
<div class="col-md-6"><label class="form-label" for="referencia">Referencia</label><input class="form-control" id="referencia" name="referencia" type="text" maxlength="250" required value='<c:out value="${punto.referencia}"/>'></div>
<div class="col-md-6"><label class="form-label" for="horarios">Horarios de atención</label><input class="form-control" id="horarios" name="horarios" type="text" maxlength="200" required value='<c:out value="${punto.horarios}"/>'></div>
<div class="col-md-6"><label class="form-label" for="responsable">Responsable</label><input class="form-control" id="responsable" name="responsable" type="text" maxlength="120" required value='<c:out value="${punto.responsable}"/>'></div>
<div class="col-md-6"><label class="form-label" for="telefono">Teléfono celular (9 dígitos)</label><input class="form-control" id="telefono" name="telefono" type="tel" pattern="9[0-9]{8}" minlength="9" maxlength="9" inputmode="numeric" placeholder="987654321" title="Escribe un número celular válido de 9 dígitos que empiece con 9 (ej. 987654321)" required value='<c:out value="${punto.telefono}"/>'></div>
<div class="col-md-6"><label class="form-label" for="estado">Estado</label><select id="estado" name="estado" class="form-select"><option>ACTIVO</option><option ${punto.estado == 'INACTIVO' ? 'selected' : ''}>INACTIVO</option></select></div>
<div><button class="btn btn-primary">Guardar punto</button> <a href="${ctx}/admin/logistica/puntos">Nuevo / cancelar edición</a></div></form></section></c:if>
<%@ include file="/views/common/footer.jspf" %>
