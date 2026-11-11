<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Revisa la operación"/>
<%@ include file="/views/common/header.jspf" %>
<div class="page-heading">
<h1>Revisa la operación</h1></div>
<p>La operación no se completó. Vuelve al formulario para corregir los datos.</p><button class="btn btn-outline-secondary" type="button" onclick="history.back()">Volver</button><%@ include file="/views/common/footer.jspf" %>
