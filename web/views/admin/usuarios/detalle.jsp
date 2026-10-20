<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Detalle de usuario"/>
<%@ include file="/views/common/header.jspf" %>
<div class="page-heading"><p class="eyebrow">Administración</p><h1>Detalle de usuario</h1></div>
<section class="panel"><dl class="row"><dt class="col-sm-3">ID</dt><dd class="col-sm-9"><c:out value="${item.id}"/></dd><dt class="col-sm-3">Nombre</dt><dd class="col-sm-9"><c:out value="${item.nombre}"/></dd><dt class="col-sm-3">Apellido</dt><dd class="col-sm-9"><c:out value="${item.apellido}"/></dd><dt class="col-sm-3">Correo</dt><dd class="col-sm-9"><c:out value="${item.email}"/></dd><dt class="col-sm-3">Teléfono</dt><dd class="col-sm-9"><c:out value="${item.telefono}"/></dd><dt class="col-sm-3">Rol</dt><dd class="col-sm-9"><c:out value="${item.rol}"/></dd><dt class="col-sm-3">Estado</dt><dd class="col-sm-9"><c:out value="${item.estado}"/></dd></dl><c:if test="${item.rol == 'DONADOR'}"><a class="btn btn-primary" href="${ctx}/admin/usuarios/formulario?id=${item.id}">Editar</a> <a class="btn btn-outline-secondary" href="${ctx}/admin/usuarios">Volver</a><div class="d-flex flex-wrap gap-2 my-3"><form method="post" action="${ctx}/admin/usuarios" data-confirm
                                  data-confirm-title="${item.estado == 'ACTIVO' ? 'Desactivar registro' : 'Reactivar registro'}"
                                  data-confirm-label="${item.estado == 'ACTIVO' ? 'Desactivar' : 'Reactivar'}"
                                  data-confirm-message="${item.estado == 'ACTIVO' ? 'El registro quedará inactivo. Se conservarán sus datos y podrás reactivarlo.' : 'El registro volverá a estar activo con sus datos conservados.'}">
                                <input type="hidden" name="csrf" value="${sessionScope.csrf}">
                                <input type="hidden" name="accion" value="estado">
                                <input type="hidden" name="id" value="${item.id}">
                                <input type="hidden" name="estado" value="${item.estado == 'ACTIVO' ? 'INACTIVO' : 'ACTIVO'}">
                                <button class="btn btn-sm btn-outline-secondary" type="submit">${item.estado == 'ACTIVO' ? 'Desactivar' : 'Reactivar'}</button>
                            </form><form method="post" action="${ctx}/admin/usuarios" data-confirm
                                  data-confirm-title="Eliminar definitivamente" data-confirm-label="Eliminar definitivamente"
                                  data-confirm-message="Se borrará de la base de datos el registro #${item.id}. Esta acción no se puede deshacer ni reactivar. Si tiene registros relacionados, el borrado será rechazado. ¿Deseas continuar?">
                                <input type="hidden" name="csrf" value="${sessionScope.csrf}">
                                <input type="hidden" name="accion" value="eliminar">
                                <input type="hidden" name="id" value="${item.id}">
                                <button class="btn btn-sm btn-outline-danger" type="submit">Eliminar</button>
                            </form></div></c:if><a class="btn btn-outline-secondary mt-3" href="${ctx}/admin/usuarios">Volver al listado</a></section><%@ include file="/views/common/footer.jspf" %>
