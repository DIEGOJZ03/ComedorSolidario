<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Revisión de confianza"/>
<%@ include file="/views/common/header.jspf" %>
<div class="page-heading">
<h1>Revisión de confianza</h1></div>
<p>Se muestran donadores con dos o más donaciones incumplidas. Esta alerta requiere revisión humana y no suspende cuentas.</p>
<div class="panel">
<c:forEach var="a" items="${alertas}">
<p><a href="${ctx}/admin/logistica/historial?usuarioId=${a.id}"><c:out value="${a.nombre}"/></a>: ${a.incumplimientos} incumplimientos</p></c:forEach>
<c:if test="${empty alertas}">
<p>No hay alertas con ese criterio.</p></c:if></div><%@ include file="/views/common/footer.jspf" %>
