<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Mi panel"/>
<%@ include file="/views/common/header.jspf" %>
<div class="page-heading">
    <p class="eyebrow">Tu espacio solidario</p>
    <h1>Hola, <c:out value="${sessionScope.usuarioNombre}"/></h1>
    <p class="text-secondary">Así avanzan tus aportes a la comunidad.</p>
</div>
<div class="row g-3 mb-4">
    <div class="col-sm-6 col-xl-3">
        <article class="stat-card">
            <p>Donaciones realizadas</p>
            <strong>${resumen.TOTAL}</strong>
        </article>
    </div>
    <div class="col-sm-6 col-xl-3">
        <article class="stat-card">
            <p>Pendientes</p>
            <strong>${resumen.PENDIENTE}</strong>
        </article>
    </div>
    <div class="col-sm-6 col-xl-3">
        <article class="stat-card">
            <p>Aceptadas</p>
            <strong>${resumen.ACEPTADA}</strong>
        </article>
    </div>
    <div class="col-sm-6 col-xl-3">
        <article class="stat-card">
            <p>Entregadas</p>
            <strong>${resumen.ENTREGADA}</strong>
        </article>
    </div>
</div>
<section class="panel">
    <h2 class="h4">¿Qué quieres hacer hoy?</h2>
    <div class="d-flex flex-wrap gap-3 mt-3">
        <a class="btn btn-primary" href="${ctx}/donante/donaciones/nueva">Realizar donación</a>
        <a class="btn btn-outline-primary" href="${ctx}/comedores">Buscar comedor</a>
        <a class="btn btn-outline-primary" href="${ctx}/donante/donaciones">Mis donaciones</a>
        <a class="btn btn-outline-secondary" href="${ctx}/donante/perfil">Mi perfil</a>
    </div>
</section>
<%@ include file="/views/common/footer.jspf" %>
