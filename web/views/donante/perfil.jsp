<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Mi perfil"/>
<%@ include file="/views/common/header.jspf" %>
<div class="page-heading">
    <p class="eyebrow">Tu cuenta</p>
    <h1>Mi perfil</h1>
    <p class="text-secondary">Actualiza tus datos de contacto.</p>
</div>
<p>Correo: <strong><c:out value="${usuario.email}"/></strong> · Rol: Donador</p>
<form method="post" action="${ctx}/donante/perfil" class="panel row g-3" >
    <input type="hidden" name="csrf" value="${sessionScope.csrf}">
    <div class="col-md-6">
        <label class="form-label" for="nombre">Nombres</label>
        <input class="form-control" id="nombre" name="nombre" type="text" maxlength="80" required value='<c:out value="${pageContext.request.method eq 'POST' ? param.nombre : usuario.nombre}"/>'>
    </div>
    <div class="col-md-6">
        <label class="form-label" for="apellido">Apellidos</label>
        <input class="form-control" id="apellido" name="apellido" type="text" maxlength="80" required value='<c:out value="${pageContext.request.method eq 'POST' ? param.apellido : usuario.apellido}"/>'>
    </div>
    <div class="col-md-6">
        <label class="form-label" for="telefono">Teléfono</label>
        <input class="form-control" id="telefono" name="telefono" type="tel" maxlength="20" value='<c:out value="${pageContext.request.method eq 'POST' ? param.telefono : usuario.telefono}"/>'>
    </div>
    <div class="col-12 d-flex gap-2 mt-4">
        <button class="btn btn-primary" type="submit">Guardar perfil</button>
        <a class="btn btn-outline-secondary" href="${ctx}/donante/dashboard">Volver</a>
    </div>
</form>
<%@ include file="/views/common/footer.jspf" %>
