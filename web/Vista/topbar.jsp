<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<header class="topbar">
    <div class="topbar-left">
        <button class="toggle-btn" onclick="toggleSidebar()"><i class="ti ti-menu-2"></i></button>
    </div>
    <div class="topbar-right">
        <c:if test="${not empty sessionScope.nombreUsuario}">
            <div class="topbar-user">
                <div class="user-avatar-sm"><c:out value="${sessionScope.nombreUsuario.substring(0,1)}"/></div>
                <span><c:out value="${sessionScope.nombreUsuario}"/></span>
            </div>
        </c:if>
    </div>
</header>
