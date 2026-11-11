<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="es_PE"/>
<c:set var="titulo" value="Productos"/>
<%@ include file="/views/common/header.jspf" %>
<div class="page-heading d-flex flex-wrap justify-content-between align-items-end gap-3">
    <div>
        <p class="eyebrow">Administración</p>
        <h1>Productos</h1>
        <p class="text-secondary mb-0">Administra el catálogo de productos disponibles para donar (con su unidad asignada) y consulta el resumen de necesidades.</p>
    </div>
    <a class="btn btn-primary" href="${ctx}/admin/productos/formulario">+ Nuevo producto en ventana</a>
</div>

<section id="gestion-producto" class="panel mb-4">
    <h2 class="h4 mb-3">${not empty item and item.id != 0 ? 'Editar producto del catálogo' : 'Agregar producto al catálogo'}</h2>
    <form method="post" action="${ctx}/admin/productos" class="row g-3">
        <input type="hidden" name="csrf" value="${sessionScope.csrf}">
        <input type="hidden" name="accion" value="guardar">
        <input type="hidden" name="origen" value="listar">
        <input type="hidden" name="id" value="${empty item ? 0 : item.id}">
        <div class="col-md-4">
            <label class="form-label" for="nombre">Nombre del producto</label>
            <input class="form-control" id="nombre" name="nombre" type="text" maxlength="100" placeholder="Ej. Arroz, Aceite vegetal, Leche..." required value='<c:out value="${not empty item ? item.nombre : param.nombre}"/>'>
        </div>
        <div class="col-md-3">
            <label class="form-label" for="unidad">Unidad asignada</label>
            <c:set var="unidadSel" value="${not empty item ? item.unidad : param.unidad}"/>
            <select class="form-select" id="unidad" name="unidad" required>
                <option value="kg" ${unidadSel == 'kg' ? 'selected' : ''}>Kg (kilogramos)</option>
                <option value="litros" ${unidadSel == 'litros' ? 'selected' : ''}>Litros</option>
                <option value="unidades" ${unidadSel == 'unidades' ? 'selected' : ''}>Unidades</option>
                <option value="cajas" ${unidadSel == 'cajas' ? 'selected' : ''}>Cajas</option>
            </select>
        </div>
        <div class="col-md-2">
            <label class="form-label" for="estado">Estado</label>
            <c:set var="estadoSel" value="${not empty item and not empty item.estado ? item.estado : (not empty param.estado ? param.estado : 'ACTIVO')}"/>
            <select class="form-select" id="estado" name="estado" required>
                <option value="ACTIVO" ${estadoSel == 'ACTIVO' ? 'selected' : ''}>ACTIVO</option>
                <option value="INACTIVO" ${estadoSel == 'INACTIVO' ? 'selected' : ''}>INACTIVO</option>
            </select>
        </div>
        <div class="col-md-3 d-flex align-items-end gap-2">
            <button class="btn btn-primary w-100" type="submit">${not empty item and item.id != 0 ? 'Actualizar' : 'Guardar producto'}</button>
            <c:if test="${not empty item and item.id != 0}">
                <a class="btn btn-outline-secondary" href="${ctx}/admin/productos">Cancelar</a>
            </c:if>
        </div>
        <div class="col-12">
            <label class="form-label" for="descripcion">Descripción / presentación recomendada</label>
            <input class="form-control" id="descripcion" name="descripcion" type="text" maxlength="1000" placeholder="Ej. Bolsas o sacos sellados dentro de su fecha de vencimiento" required value='<c:out value="${not empty item ? item.descripcion : param.descripcion}"/>'>
        </div>
    </form>
</section>

<div class="panel table-responsive mb-4">
    <h2 class="h4 mb-3">Catálogo de productos administrables</h2>
    <p class="small text-secondary">Los productos en estado <strong>ACTIVO</strong> aparecen automáticamente en la vista del donador con su unidad asignada para evitar entradas inválidas.</p>
    <table class="table align-middle">
        <thead>
            <tr>
                <th scope="col">ID</th>
                <th scope="col">Producto</th>
                <th scope="col">Unidad asignada</th>
                <th scope="col">Descripción</th>
                <th scope="col">Estado</th>
                <th scope="col">Acciones</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="prod" items="${items}">
                <tr>
                    <td>#${prod.id}</td>
                    <td><strong><c:out value="${prod.nombre}"/></strong></td>
                    <td><span class="badge text-bg-light border"><c:out value="${prod.unidad}"/></span></td>
                    <td><c:out value="${prod.descripcion}"/></td>
                    <td><c:set var="estado" value="${prod.estado}"/><%@ include file="/views/common/badge.jspf" %></td>
                    <td class="action-group">
                        <a class="btn btn-sm btn-outline-secondary" href="${ctx}/admin/productos/detalle?id=${prod.id}">Ver</a>
                        <a class="btn btn-sm btn-outline-primary" href="${ctx}/admin/productos?editarId=${prod.id}#gestion-producto">Editar</a>
                        <form method="post" action="${ctx}/admin/productos" data-confirm
                              data-confirm-title="${prod.estado == 'ACTIVO' ? 'Desactivar producto' : 'Reactivar producto'}"
                              data-confirm-label="${prod.estado == 'ACTIVO' ? 'Desactivar' : 'Reactivar'}"
                              data-confirm-message="${prod.estado == 'ACTIVO' ? 'El producto dejará de mostrarse en el selector de donaciones, pero conservará su historial.' : 'El producto volverá a mostrarse en el selector de donaciones.'}">
                            <input type="hidden" name="csrf" value="${sessionScope.csrf}">
                            <input type="hidden" name="accion" value="estado">
                            <input type="hidden" name="id" value="${prod.id}">
                            <input type="hidden" name="estado" value="${prod.estado == 'ACTIVO' ? 'INACTIVO' : 'ACTIVO'}">
                            <button class="btn btn-sm btn-outline-secondary" type="submit">${prod.estado == 'ACTIVO' ? 'Desactivar' : 'Reactivar'}</button>
                        </form>
                        <form method="post" action="${ctx}/admin/productos" data-confirm
                              data-confirm-title="Eliminar definitivamente"
                              data-confirm-label="Eliminar definitivamente"
                              data-confirm-message="Se borrará de la base de datos el producto #${prod.id}. Si está vinculado a necesidades, no podrá eliminarse. ¿Deseas continuar?">
                            <input type="hidden" name="csrf" value="${sessionScope.csrf}">
                            <input type="hidden" name="accion" value="eliminar">
                            <input type="hidden" name="id" value="${prod.id}">
                            <button class="btn btn-sm btn-outline-danger" type="submit">Eliminar</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty items}">
                <tr><td colspan="6" class="text-center py-3">No hay productos registrados en el catálogo.</td></tr>
            </c:if>
        </tbody>
    </table>
</div>

<div class="panel table-responsive">
    <h2 class="h4 mb-3">Resumen de productos solicitados en Necesidades</h2>
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
