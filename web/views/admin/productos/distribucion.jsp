<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="es_PE"/>
<c:set var="titulo" value="Detalle del producto"/>
<%@ include file="/views/common/header.jspf" %>
<a class="btn btn-outline-secondary mb-3" href="${ctx}/admin/productos">Volver a productos</a>
<div class="page-heading">
    <p class="eyebrow">Detalle del producto</p>
    <h1><c:out value="${producto.nombre}"/></h1>
    <p class="text-secondary">Consulta cuánto solicita cada comedor y la necesidad donde se registró.</p>
</div>
<section class="panel mb-4" aria-label="Cantidad y presentación del producto">
    <dl class="row mb-0">
        <div class="col-md-6">
            <dt class="text-secondary">Cantidad total solicitada</dt>
            <dd class="h3 mt-2"><fmt:formatNumber value="${producto.cantidadTotal}" minFractionDigits="2" maxFractionDigits="2"/> <c:out value="${producto.unidad}"/></dd>
        </div>
        <div class="col-md-6">
            <dt class="text-secondary">Presentación</dt>
            <dd class="mt-2 mb-0">
                <c:choose>
                    <c:when test="${producto.unidad == 'litros'}">
                        <strong><fmt:formatNumber value="${producto.envases20Litros}" maxFractionDigits="0"/> envases de 20 L<c:if test="${producto.litrosRestantes > 0}"> + <fmt:formatNumber value="${producto.litrosRestantes}" minFractionDigits="0" maxFractionDigits="2"/> L restantes</c:if></strong>
                        <span class="d-block small text-secondary">Equivalencia por volumen; no indica botellas registradas.</span>
                    </c:when>
                    <c:otherwise><c:out value="${producto.unidad}"/></c:otherwise>
                </c:choose>
            </dd>
        </div>
    </dl>
</section>
<section class="panel" aria-labelledby="destinos-producto">
    <h2 class="h4" id="destinos-producto">Cantidades por comedor</h2>
    <p class="text-secondary">Cada fila corresponde a una solicitud activa. Pulsa el nombre del comedor para consultar sus datos.</p>
    <div class="table-responsive">
        <table class="table align-middle">
            <thead><tr><th scope="col">Comedor destinatario</th><th scope="col" class="text-end">Cantidad solicitada</th><th scope="col">Prioridad</th><th scope="col">Solicitud de origen</th></tr></thead>
            <tbody>
                <c:forEach var="n" items="${producto.necesidades}">
                    <tr>
                        <td><a href="${ctx}/admin/comedores/detalle?id=${n.comedorId}"><c:out value="${n.comedorNombre}"/></a></td>
                        <td class="text-end text-nowrap"><strong><fmt:formatNumber value="${n.cantidadNecesaria}" minFractionDigits="2" maxFractionDigits="2"/></strong> <c:out value="${n.unidad}"/></td>
                        <td><span class="badge ${n.prioridad == 'ALTA' ? 'text-bg-danger' : n.prioridad == 'MEDIA' ? 'text-bg-warning' : 'text-bg-secondary'}"><c:out value="${n.prioridad}"/></span></td>
                        <td><a class="btn btn-sm btn-outline-primary" href="${ctx}/admin/necesidades/detalle?id=${n.id}">Ver solicitud #${n.id}</a></td>
                    </tr>
                </c:forEach>
            </tbody>
            <tfoot><tr><th scope="row">Total solicitado</th><td class="text-end text-nowrap"><strong><fmt:formatNumber value="${producto.cantidadTotal}" minFractionDigits="2" maxFractionDigits="2"/> <c:out value="${producto.unidad}"/></strong></td><td colspan="2"></td></tr></tfoot>
        </table>
    </div>
    <p class="small text-secondary mb-0">El origen es la necesidad registrada y el destino es el comedor solicitante. Estas cantidades no confirman donaciones recibidas ni entregas realizadas.</p>
</section>
<%@ include file="/views/common/footer.jspf" %>
