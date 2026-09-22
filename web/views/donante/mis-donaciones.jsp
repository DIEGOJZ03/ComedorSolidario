<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Mis donaciones"/>
<%@ include file="/views/common/header.jspf" %>
<div class="page-heading">
    <p class="eyebrow">El recorrido de tu ayuda</p>
    <h1>Mis donaciones</h1>
    <p class="text-secondary">Pendiente → Aceptada → Entregada. El administrador confirma cada avance.</p>
</div>
<a class="btn btn-primary mb-4" href="${ctx}/donante/donaciones/nueva">Registrar otra donación</a>
<div class="panel table-responsive">
    <table class="table align-middle">
        <thead>
            <tr>
                <th>ID</th>
                <th>Comedor</th>
                <th>Tipo / aporte</th>
                <th>Registro</th>
                <th>Estado</th>
                <th>Entrega</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="d" items="${donaciones}">
                <tr>
                    <td>#${d.id}</td>
                    <td><c:out value="${d.comedorNombre}"/></td>
                    <td><c:out value="${d.tipo}"/><br><span class="small">${d.cantidad} <c:out value="${d.unidad}"/></span></td>
                    <td><c:out value="${d.fechaRegistro}"/></td>
                    <td><c:set var="estado" value="${d.estado}"/><%@ include file="/views/common/badge.jspf" %></td>
                    <td><c:out value="${d.fechaEntrega}" default="Por confirmar"/></td>
                </tr>
            </c:forEach>
            <c:if test="${empty donaciones}">
                <tr>
                    <td colspan="7" class="text-center py-4">Todavía no hay donaciones para mostrar.</td>
                </tr>
            </c:if>
        </tbody>
    </table>
</div>
<%@ include file="/views/common/footer.jspf" %>
