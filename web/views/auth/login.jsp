<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Iniciar sesión"/>
<%@ include file="/views/common/header.jspf" %>
<div class="auth-shell">
    <div class="page-heading">
        <p class="eyebrow">Bienvenido de nuevo</p>
        <h1>Iniciar sesión</h1>
        <p class="text-secondary">Consulta y acompaña tus aportes.</p>
    </div>
    <form method="post" action="${ctx}/login" class="panel row g-3" >
        <input type="hidden" name="csrf" value="${sessionScope.csrf}">
        <div class="col-md-6">
            <label class="form-label" for="email">Correo electrónico</label>
            <input class="form-control" id="email" name="email" type="email" maxlength="150" required value='<c:out value="${param.email}"/>'>
        </div>
        <div class="col-md-6">
            <label class="form-label" for="password">Contraseña</label>
            <input class="form-control" id="password" name="password" type="password" maxlength="128" required autocomplete="current-password">
        </div>
        <div class="col-12 d-flex gap-2 mt-4">
            <button class="btn btn-primary" type="submit">Ingresar</button>
            <a class="btn btn-outline-secondary" href="${ctx}/inicio">Volver</a>
        </div>
    </form>
    <p class="mt-4">¿Todavía no tienes cuenta? <a href="${ctx}/registro">Regístrate como donador</a></p>
</div>
<%@ include file="/views/common/footer.jspf" %>
