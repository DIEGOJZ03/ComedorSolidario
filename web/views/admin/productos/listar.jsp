<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="es_PE"/>
<c:set var="titulo" value="Productos"/>
<%@ include file="/views/common/header.jspf" %>
<div class="page-heading">
    <p class="eyebrow">Administración</p>
    <h1>Productos</h1>
    <p class="text-secondary">Catálogo de productos registrados en Necesidades, con su cantidad total por unidad.</p>
</div>
<div class="panel table-responsive">
    <table class="table align-middle">
        <thead><tr><th scope="col">Producto</th><th scope="col">Cantidad total</th><th scope="col">Presentación</th><th scope="col">Acciones</th></tr></thead>
        <tbody>
            <c:forEach var="producto" items="${catalogo}">
                <tr>
                    <td><strong><c:out value="${producto.nombre}"/></strong></td>
                    <td class="text-nowrap"><fmt:formatNumber value="${producto.cantidadTotal}" minFractionDigits="2" maxFractionDigits="2"/> <c:out value="${producto.unidad}"/></td>
                    <td>
                        <c:choose>
                            <c:when test="${producto.unidad == 'litros'}">
                                <fmt:formatNumber value="${producto.envases20Litros}" maxFractionDigits="0"/> envases de 20 L
                                <c:if test="${producto.litrosRestantes > 0}"> + <fmt:formatNumber value="${producto.litrosRestantes}" minFractionDigits="0" maxFractionDigits="2"/> L restantes</c:if>
                                <span class="d-block small text-secondary">Equivalencia por volumen</span>
                            </c:when>
                            <c:otherwise><c:out value="${producto.unidad}"/></c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        <c:url var="detalleProductoUrl" value="/admin/productos/distribucion">
                            <c:param name="nombre" value="${producto.nombre}"/>
                            <c:param name="unidad" value="${producto.unidad}"/>
                        </c:url>
                        <a class="btn btn-sm btn-outline-primary" href="<c:out value='${detalleProductoUrl}'/>">Ver<span class="visually-hidden"> detalle de <c:out value="${producto.nombre}"/> en <c:out value="${producto.unidad}"/></span></a>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty catalogo}"><tr><td colspan="4">No hay productos para mostrar.</td></tr></c:if>
        </tbody>
    </table>
    <p class="small text-secondary mb-0">Los totales se actualizan desde las necesidades vigentes de los comedores activos. La presentación de líquidos usa envases de 20 L como referencia; no indica botellas registradas ni existencias.</p>
</div>
<%@ include file="/views/common/footer.jspf" %>
