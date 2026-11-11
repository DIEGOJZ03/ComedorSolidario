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
            <label class="form-label" for="telefono">Teléfono celular (9 dígitos)</label>
            <input class="form-control" id="telefono" name="telefono" required pattern="9[0-9]{8}" minlength="9" maxlength="9" inputmode="numeric" placeholder="987654321" title="Escribe un número celular válido de 9 dígitos que empiece con 9 (ej. 987654321)" type="tel" value='<c:out value="${param.telefono}"/>'>
        </div>
        <div class="col-md-6">
            <label class="form-label" for="password">Contraseña</label>
            <div class="password-wrapper">
                <input type="password" class="form-control" id="password" name="password" minlength="8" maxlength="128" autocomplete="new-password" required>
                <button type="button" class="btn-password-toggle" data-toggle-password aria-label="Mostrar contraseña" title="Mostrar contraseña">
                    <svg class="icon-eye" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M2 12s3.5-7 10-7 10 7 10 7-3.5 7-10 7-10-7-10-7Z"/><circle cx="12" cy="12" r="3"/></svg>
                    <svg class="icon-eye-off" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M17.94 17.94A10.07 10.07 0 0 1 12 19c-6.5 0-10-7-10-7a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c6.5 0 10 7 10 7a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"/><line x1="2" y1="2" x2="22" y2="22"/></svg>
                </button>
            </div>
        </div>
        <div class="col-md-6">
            <label class="form-label" for="confirmacion">Repite la contraseña</label>
            <div class="password-wrapper">
                <input type="password" class="form-control" id="confirmacion" name="confirmacion" minlength="8" maxlength="128" autocomplete="new-password" required>
                <button type="button" class="btn-password-toggle" data-toggle-password aria-label="Mostrar contraseña" title="Mostrar contraseña">
                    <svg class="icon-eye" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M2 12s3.5-7 10-7 10 7 10 7-3.5 7-10 7-10-7-10-7Z"/><circle cx="12" cy="12" r="3"/></svg>
                    <svg class="icon-eye-off" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M17.94 17.94A10.07 10.07 0 0 1 12 19c-6.5 0-10-7-10-7a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c6.5 0 10 7 10 7a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"/><line x1="2" y1="2" x2="22" y2="22"/></svg>
                </button>
            </div>
        </div>
        <p class="small text-secondary">Usa mas de 8 caracteres. Tu cuenta tendrá el rol Donador.</p>
        <div class="col-12 d-flex gap-2 mt-4">
            <button class="btn btn-primary" type="submit">Crear mi cuenta</button>
            <a class="btn btn-outline-secondary" href="${ctx}/login">Volver</a>
        </div>
    </form>
</div>
<%@ include file="/views/common/footer.jspf" %>
