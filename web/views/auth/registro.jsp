<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Registrarse"/>
<%@ include file="/views/common/header.jspf" %>
<div class="auth-shell">
    <div class="page-heading">
        <p class="eyebrow">Súmate a la comunidad</p>
        <h1>Cada aporte cuenta</h1>
        <p class="text-secondary">Crea tu cuenta de donador.</p>
    </div>
    <form method="post" action="${ctx}/registro" class="panel row g-3" >
        <input type="hidden" name="csrf" value="${sessionScope.csrf}">
        <div class="col-md-6">
            <label class="form-label" for="nombre">Nombres</label>
            <input class="form-control" id="nombre" name="nombre" type="text" maxlength="80" required value='<c:out value="${param.nombre}"/>'>
        </div>
        <div class="col-md-6">
            <label class="form-label" for="apellido">Apellidos</label>
            <input class="form-control" id="apellido" name="apellido" type="text" maxlength="80" required value='<c:out value="${param.apellido}"/>'>
        </div>
        <div class="col-md-6">
            <label class="form-label" for="email">Correo electrónico</label>
            <input class="form-control" id="email" name="email" type="email" maxlength="150" required value='<c:out value="${param.email}"/>'>
        </div>
        <div class="col-md-6">
            <label class="form-label" for="telefono">Teléfono</label>
            <input class="form-control" id="telefono" name="telefono" type="tel" maxlength="20" value='<c:out value="${param.telefono}"/>'>
        </div>
        <div class="col-md-6">
            <label class="form-label" for="password">Contraseña</label>
            <input type="password" class="form-control" id="password" name="password" minlength="8" maxlength="128" autocomplete="new-password" required>
        </div>
        <div class="col-md-6">
            <label class="form-label" for="confirmacion">Repite la contraseña</label>
            <input type="password" class="form-control" id="confirmacion" name="confirmacion" minlength="8" maxlength="128" autocomplete="new-password" required>
        </div>
        <p class="small text-secondary">Usa entre 8 y 128 caracteres. Tu cuenta tendrá el rol Donador.</p>
        <div class="col-12 d-flex gap-2 mt-4">
            <button class="btn btn-primary" type="submit">Crear mi cuenta</button>
            <a class="btn btn-outline-secondary" href="${ctx}/login">Volver</a>
        </div>
    </form>
</div>
<%@ include file="/views/common/footer.jspf" %>
