<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Administración"/>
<%@ include file="/views/common/header.jspf" %>
<div class="page-heading">
    <p class="eyebrow">Administración</p>
    <h1>La solidaridad, organizada</h1>
    <p class="text-secondary">Supervisa los comedores y el recorrido de cada aporte.</p>
</div>
<div class="row g-3 mb-4">
    <div class="col-sm-6 col-xl-4">
        <article class="stat-card">
            <p>Comedores registrados</p>
            <strong>${totalComedores}</strong>
        </article>
    </div>
    <div class="col-sm-6 col-xl-4">
        <article class="stat-card">
            <p>Donadores registrados</p>
            <strong>${totalDonadores}</strong>
        </article>
    </div>
    <div class="col-sm-6 col-xl-4">
        <article class="stat-card">
            <p>Donaciones realizadas</p>
            <strong>${resumen.TOTAL}</strong>
        </article>
    </div>
    <div class="col-sm-6 col-xl-4">
        <article class="stat-card">
            <p>Pendientes</p>
            <strong>${resumen.PENDIENTE}</strong>
        </article>
    </div>
    <div class="col-sm-6 col-xl-4">
        <article class="stat-card">
            <p>Aceptadas</p>
            <strong>${resumen.ACEPTADA}</strong>
        </article>
    </div>
    <div class="col-sm-6 col-xl-4">
        <article class="stat-card">
            <p>Entregadas</p>
            <strong>${resumen.ENTREGADA}</strong>
        </article>
    </div>
</div>
<section class="panel">
    <h2 class="h4">Gestión del sistema</h2>
    <div class="d-flex flex-wrap gap-3 mt-3">
        <a class="btn btn-primary" href="${ctx}/admin/comedores">Gestionar comedores · CRUD</a>
        <a class="btn btn-outline-primary" href="${ctx}/admin/necesidades">Gestionar necesidades</a>
        <a class="btn btn-outline-primary" href="${ctx}/admin/donaciones">Supervisar donaciones</a>
        <a class="btn btn-outline-secondary" href="${ctx}/admin/usuarios">Consultar usuarios</a>
    </div>
</section>
<%@ include file="/views/common/footer.jspf" %>
