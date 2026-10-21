<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="ui" tagdir="/WEB-INF/tags" %>
<c:if test="${empty inicioCargado}"><c:redirect url="/inicio"/></c:if>
<c:set var="titulo" value="Inicio"/>
<%@ include file="/views/common/header.jspf" %>
<div class="home-page">
    <section class="welcome-hero" aria-labelledby="inicio-titulo">
        <div class="hero-copy">
            <p class="eyebrow"><span class="live-dot"></span> Una comunidad que comparte</p>
            <h1 id="inicio-titulo">Una mesa más grande.<br><em>Un futuro más solidario.</em></h1>
            <p class="hero-intro">Detrás de cada comedor hay personas que cuidan de su comunidad. Encuentra uno cerca de ti y transforma tu ayuda en un aporte concreto.</p>
            <div class="hero-actions">
                <a class="btn btn-primary btn-lg" href="${ctx}/comedores">Encuentra dónde ayudar <ui:icono nombre="arrow"/></a>
                <a class="text-link" href="#como-ayudar">Así funciona</a>
            </div>
            <div class="hero-footnote"><ui:icono nombre="heart"/><span>Conoce sus necesidades. Comparte lo que puedas.</span></div>
        </div>
        <div class="hero-visual">
            <div class="hero-photo-frame">
            <img src="${ctx}/assets/img/comunidad.png" class="community-cover" alt="Personas reunidas frente a un comedor comunitario" width="1235" height="755" fetchpriority="high">
            <div class="photo-caption"><span class="caption-label">El corazón de la comunidad</span><strong>Juntos, alimentamos<br>nuevas posibilidades.</strong></div>
            </div>
            <a class="floating-note" href="${ctx}/comedores"><span class="icon-tile"><ui:icono nombre="home"/></span><span><strong>${totalComedores} comedores activos</strong><small>Encuentra tu próxima causa</small></span><ui:icono nombre="arrow"/></a>
        </div>
    </section>
    <section class="community-stats" aria-label="Actividad de la plataforma">
        <div class="stats-intro"><span class="eyebrow">Cada aporte cuenta</span><h2>Una red que se construye contigo.</h2></div>
        <div class="community-stat"><strong>${totalComedores}</strong><span>Comedores activos</span></div>
        <div class="community-stat"><strong>${totalDonadores}</strong><span>Donadores registrados</span></div>
        <div class="community-stat"><strong>${resumen.ENTREGADA}</strong><span>Donaciones entregadas</span></div>
    </section>
    <section class="section-space" aria-labelledby="destacados-titulo">
        <div class="section-heading"><div><p class="eyebrow">Encuentra tu próxima causa</p><h2 id="destacados-titulo">Comedores destacados</h2><p>Conoce sus historias y descubre cómo puedes sumar.</p></div><a href="${ctx}/comedores" class="text-link">Explorar todos <ui:icono nombre="arrow"/></a></div>
        <c:set var="comedores" value="${destacados}"/>
        <%@ include file="/views/common/cards-comedores.jspf" %>
    </section>
    <section id="como-ayudar" class="section-space help-section" aria-labelledby="ayuda-titulo">
        <div class="section-heading"><div><p class="eyebrow">De la intención a la acción</p><h2 id="ayuda-titulo">Ayudar empieza con un paso.</h2></div><p>Elige una comunidad y acompaña<br class="d-none d-md-block"> el recorrido de tu aporte.</p></div>
        <div class="help-grid">
            <article class="help-step"><span class="step-number">01</span><ui:icono nombre="pin"/><h3>Encuentra un comedor</h3><p>Explora por zona, distrito o provincia y conoce a la comunidad.</p></article>
            <article class="help-step"><span class="step-number">02</span><ui:icono nombre="list"/><h3>Conoce lo que necesita</h3><p>Revisa los productos, cantidades y prioridades publicados.</p></article>
            <article class="help-step"><span class="step-number">03</span><ui:icono nombre="heart"/><h3>Comparte tu aporte</h3><p>Registra qué vas a donar y a qué comedor quieres apoyar.</p></article>
            <article class="help-step"><span class="step-number">04</span><ui:icono nombre="check"/><h3>Sigue su recorrido</h3><p>Consulta cuándo tu donación es aceptada y entregada.</p></article>
        </div>
    </section>
    <section class="join-banner section-space"><div><p class="eyebrow">Siempre hay una forma de sumar</p><h2>Tu próximo gesto puede<br>empezar aquí.</h2><p>Una comunidad que comparte, una mesa que crece.</p></div><a class="btn btn-lime btn-lg" href="${ctx}/comedores">Quiero ayudar <ui:icono nombre="arrow"/></a></section>
</div>
<%@ include file="/views/common/footer.jspf" %>
