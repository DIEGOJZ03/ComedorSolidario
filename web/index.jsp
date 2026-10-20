<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:if test="${empty inicioCargado}">
    <c:redirect url="/inicio"/>
</c:if>
<c:set var="titulo" value="Inicio"/>
<%@ include file="/views/common/header.jspf" %>
<section class="hero">
    <div class="row align-items-center g-4">
        <div class="col-lg-7">
            <p class="eyebrow">Pequeños aportes. Grandes cambios.</p>
            <h1>Tu ayuda puede llegar a quienes más la necesitan</h1>
            <p class="lead">Conecta con comedores comunitarios, conoce sus necesidades y acompaña el recorrido de tu donación.</p>
            <div class="d-flex flex-wrap gap-3">
                <a class="btn btn-primary btn-lg" href="${ctx}/comedores">Ver comedores</a>
                <a class="btn btn-outline-primary btn-lg" href="${ctx}/donante/donaciones/nueva">Quiero donar</a>
            </div>
            <p class="small text-secondary mt-4">Una comunidad que comparte, una mesa que crece.</p>
        </div>
        <div class="col-lg-5">
            <img class="hero-image" src="${ctx}/assets/img/comunidad.png" alt="Ilustración de una mesa con alimentos compartidos">
        </div>
    </div>
</section>
<section class="stats-strip row g-0 text-center" aria-label="Actividad de la plataforma">
    <div class="col-sm-4">
        <strong>${totalComedores}</strong>
        <span>Comedores activos</span>
    </div>
    <div class="col-sm-4">
        <strong>${totalDonadores}</strong>
        <span>Donadores registrados</span>
    </div>
    <div class="col-sm-4">
        <strong>${resumen.ENTREGADA}</strong>
        <span>Donaciones entregadas</span>
    </div>
</section>
<section id="como-ayudar" class="section-space">
    <p class="eyebrow">Ayudar es sencillo</p>
    <h2>¿Cómo funciona?</h2>
    <div class="row g-4 mt-1">
        <div class="col-md-6 col-xl-3">
            <div class="step-card">
                <span class="step-number">01</span>
                <h3 class="h5">Busca un comedor</h3>
                <p>Encuentra una comunidad por zona, distrito o provincia.</p>
            </div>
        </div>
        <div class="col-md-6 col-xl-3">
            <div class="step-card">
                <span class="step-number">02</span>
                <h3 class="h5">Consulta sus necesidades</h3>
                <p>Conoce qué productos y cantidades se necesitan.</p>
            </div>
        </div>
        <div class="col-md-6 col-xl-3">
            <div class="step-card">
                <span class="step-number">03</span>
                <h3 class="h5">Registra tu donación</h3>
                <p>Elige el comedor y describe lo que vas a aportar.</p>
            </div>
        </div>
        <div class="col-md-6 col-xl-3">
            <div class="step-card">
                <span class="step-number">04</span>
                <h3 class="h5">Realiza seguimiento</h3>
                <p>Consulta si tu aporte fue aceptado y entregado.</p>
            </div>
        </div>
    </div>
</section>
<section class="section-space">
    <div class="d-flex flex-wrap justify-content-between align-items-end gap-3 mb-4">
        <div>
            <p class="eyebrow">Cerca de ti</p>
            <h2 class="mb-0">Comedores destacados</h2>
        </div>
        <a href="${ctx}/comedores" class="text-link">Explorar todos →</a>
    </div>
    <c:set var="comedores" value="${destacados}"/>
    <%@ include file="/views/common/cards-comedores.jspf" %>
</section>
<section class="why-section section-space">
    <p class="eyebrow">Solidaridad con seguimiento</p>
    <h2>¿Por qué Comedor Solidario?</h2>
    <div class="row g-4 mt-2">
        <div class="col-md-4">
            <h3 class="h5">Información en un lugar</h3>
            <p>Comedores, necesidades y aportes organizados para facilitar la coordinación.</p>
        </div>
        <div class="col-md-4">
            <h3 class="h5">Ayuda que responde</h3>
            <p>Conoce las prioridades de cada comedor antes de registrar tu donación.</p>
        </div>
        <div class="col-md-4">
            <h3 class="h5">Estados claros</h3>
            <p>Sigue tu aporte desde el registro hasta la confirmación de entrega.</p>
        </div>
    </div>
</section>
<%@ include file="/views/common/footer.jspf" %>
