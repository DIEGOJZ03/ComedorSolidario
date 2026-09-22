<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Usuarios"/>
<%@ include file="/views/common/header.jspf" %>
<div class="page-heading">
    <p class="eyebrow">Administración</p>
    <h1>Usuarios registrados</h1>
    <p class="text-secondary">Consulta las cuentas del sistema. Las contraseñas nunca se muestran.</p>
</div>
<div class="panel table-responsive">
    <table class="table align-middle">
        <thead>
            <tr>
                <th>ID</th>
                <th>Nombre</th>
                <th>Correo</th>
                <th>Teléfono</th>
                <th>Rol</th>
                <th>Estado</th>
                <th>Registro</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="u" items="${usuarios}">
                <tr>
                    <td>${u.id}</td>
                    <td><c:out value="${u.nombre}"/> <c:out value="${u.apellido}"/></td>
                    <td><c:out value="${u.email}"/></td>
                    <td><c:out value="${u.telefono}"/></td>
                    <td><c:out value="${u.rol}"/></td>
                    <td><c:out value="${u.estado}"/></td>
                    <td><c:out value="${u.fechaRegistro}"/></td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</div>
<%@ include file="/views/common/footer.jspf" %>
