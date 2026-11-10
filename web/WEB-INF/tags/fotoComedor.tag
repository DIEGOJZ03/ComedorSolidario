<%@ tag pageEncoding="UTF-8" %>
<%@ attribute name="id" required="true" type="java.lang.Integer" %>
<%@ attribute name="nombre" required="true" type="java.lang.String" %>
<%@ attribute name="clase" required="false" type="java.lang.String" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="foto" value="${pageContext.request.contextPath}/assets/img/comedores/sin-foto.svg"/>
<c:if test="${id > 0}"><c:set var="foto" value="${pageContext.request.contextPath}/foto-comedor?id=${id}"/></c:if>
<img class="comedor-photo <c:out value='${clase}'/>" src="${foto}"
     alt="Imagen del comedor: <c:out value='${nombre}'/>" width="800" height="500"
     loading="lazy" decoding="async"
     data-image-fallback="${pageContext.request.contextPath}/assets/img/comedores/sin-foto.svg">
