<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Detalle de necesidad"/>
<%@ include file="/views/common/header.jspf" %>
<h1>Detalle de necesidad</h1><section class="panel"><dl class="row">
<dt class="col-sm-4">ID</dt><dd class="col-sm-8"><c:out value="${necesidad.id}" default="Sin referencia"/></dd>
<dt class="col-sm-4">Comedor</dt><dd class="col-sm-8"><c:out value="${necesidad.comedorNombre}" default="Sin referencia"/></dd>
<dt class="col-sm-4">ID del producto de referencia (opcional)</dt><dd class="col-sm-8"><c:out value="${necesidad.productoId}" default="Sin referencia"/></dd>
<dt class="col-sm-4">Nombre</dt><dd class="col-sm-8"><c:out value="${necesidad.nombre}" default="Sin referencia"/></dd>
<dt class="col-sm-4">Descripción</dt><dd class="col-sm-8"><c:out value="${necesidad.descripcion}" default="Sin referencia"/></dd>
<dt class="col-sm-4">Cantidad</dt><dd class="col-sm-8"><c:out value="${necesidad.cantidadNecesaria}" default="Sin referencia"/></dd>
<dt class="col-sm-4">Unidad</dt><dd class="col-sm-8"><c:out value="${necesidad.unidad}" default="Sin referencia"/></dd>
<dt class="col-sm-4">Prioridad</dt><dd class="col-sm-8"><c:out value="${necesidad.prioridad}" default="Sin referencia"/></dd>
<dt class="col-sm-4">Estado</dt><dd class="col-sm-8"><c:out value="${necesidad.estado}" default="Sin referencia"/></dd>
</dl><a class="btn btn-primary" href="${ctx}/admin/necesidades/formulario?id=${necesidad.id}">Editar</a> <a class="btn btn-outline-secondary" href="${ctx}/admin/necesidades">Volver</a></section>
<%@ include file="/views/common/footer.jspf" %>
