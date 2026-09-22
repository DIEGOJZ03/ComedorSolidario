<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Consultar comedor"/>
<%@ include file="/views/common/header.jspf" %>
<a class="text-link" href="${ctx}/admin/comedores">← Volver a comedores</a>
<div class="page-heading">
    <p class="eyebrow"><c:out value="${comedor.distrito}"/> · <c:out value="${comedor.provincia}"/></p>
    <h1><c:out value="${comedor.nombre}"/></h1>
</div>
<div class="row g-4">
    <div class="col-lg-8">
        <section class="panel">
            <h2 class="h4">Descripción del comedor</h2>
            <p class="preserve-lines"><c:out value="${comedor.descripcion}"/></p>
            <h2 class="h4 mt-4">Necesidades registradas</h2>
            <div class="table-responsive">
                <table class="table align-middle">
                    <thead>
                        <tr>
                            <th>Necesidad</th>
                            <th>Cantidad</th>
                            <th>Prioridad</th>
                            <th>Descripción</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="n" items="${necesidades}">
                            <tr>
                                <td><c:out value="${n.nombre}"/></td>
                                <td>${n.cantidadNecesaria} <c:out value="${n.unidad}"/></td>
                                <td><span class="badge ${n.prioridad == 'ALTA' ? 'text-bg-danger' : n.prioridad == 'MEDIA' ? 'text-bg-warning' : 'text-bg-secondary'}"><c:out value="${n.prioridad}"/></span></td>
                                <td><c:out value="${n.descripcion}"/></td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty necesidades}">
                            <tr>
                                <td colspan="4">No hay necesidades publicadas actualmente.</td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </section>
    </div>
    <div class="col-lg-4">
        <aside class="panel">
            <h2 class="h5">Información del comedor</h2>
            <dl>
                <dt>Dirección</dt>
                <dd><c:out value="${comedor.direccion}"/></dd>
                <dt>Zona</dt>
                <dd><c:out value="${comedor.zona}"/></dd>
                <dt>Responsable</dt>
                <dd><c:out value="${comedor.responsable}"/></dd>
                <dt>Teléfono</dt>
                <dd><c:out value="${comedor.telefono}"/></dd>
            </dl>
            <c:if test="${sessionScope.usuarioRol != 'ADMIN'}">
                <a class="btn btn-primary w-100" href="${ctx}/donante/donaciones/nueva?comedorId=${comedor.id}">Donar a este comedor</a>
            </c:if>
        </aside>
    </div>
</div>
<div class="d-flex gap-2 mt-4">
    <a class="btn btn-primary" href="${ctx}/admin/comedores/formulario?id=${comedor.id}">Editar comedor</a>
    <a class="btn btn-outline-primary" href="${ctx}/admin/necesidades?comedorId=${comedor.id}">Gestionar necesidades</a>
</div>
<p class="mt-3">Estado: <c:out value="${comedor.estado}"/></p>
<%@ include file="/views/common/footer.jspf" %>
