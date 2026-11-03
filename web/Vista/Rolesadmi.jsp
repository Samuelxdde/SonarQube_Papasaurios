<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Roles — Panel Admin</title>
    <link rel="stylesheet" href="${ctx}/Vista/Css/admin.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@tabler/icons-webfont@2.44.0/tabler-icons.min.css" integrity="sha384-mY6asDzXlnHeimHHXw8cavPeArhEcAfnCibDFiaDPex3Fudo+edh/hnT7siZz8bg" crossorigin="anonymous">
    <link href="https://fonts.googleapis.com/css2?family=DM+Sans:wght@400;500;600&family=Syne:wght@600;700&display=swap" rel="stylesheet">
</head>
<body>
<%@ include file="sidebar.jsp" %>
<main class="main-content">
    <%@ include file="topbar.jsp" %>
    <div class="crud-header"><h2 class="page-title"><i class="ti ti-shield"></i> Gestión de Roles</h2></div>
    <c:if test="${not empty mensaje}"><div class="alert alert-ok"><i class="ti ti-circle-check"></i> ${mensaje}</div></c:if>
    <c:if test="${not empty error}"><div class="alert alert-err"><i class="ti ti-alert-circle"></i> ${error}</div></c:if>

    <div class="panel form-panel">
        <div class="panel-header">
            <span class="panel-title"><i class="ti ti-${not empty rolEditar ? 'edit' : 'plus'}"></i> ${not empty rolEditar ? 'Editar Rol' : 'Nuevo Rol'}</span>
        </div>
        <form action="${ctx}/RolesAdmi" method="post" class="crud-form">
            <input type="hidden" name="accion" value="${not empty rolEditar ? 'actualizar' : 'insertar'}">
            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
            <div class="form-row">
                <div class="form-group">
                    <c:if test="${not empty rolEditar}"><input type="hidden" name="idRoles" value="${rolEditar.idRoles}"></c:if>
                    <label for="descripcion_rol">Descripción del Rol</label>
                    <input type="text" name="descripcion_rol" value="${rolEditar.descripcion_rol}" required placeholder="Ej: Administrador" id="descripcion_rol">
                </div>
            </div>
            <div class="form-actions">
                <button type="submit" class="btn-primary"><i class="ti ti-${not empty rolEditar ? 'device-floppy' : 'plus'}"></i> ${not empty rolEditar ? 'Actualizar' : 'Insertar'}</button>
                <c:if test="${not empty rolEditar}"><a href="${ctx}/RolesAdmi" class="btn-sm">Cancelar</a></c:if>
            </div>
        </form>
    </div>

    <div class="panel full-panel">
        <div class="panel-header">
            <span class="panel-title"><i class="ti ti-list"></i> Lista de Roles</span>
            <div class="search-bar"><i class="ti ti-search"></i><input type="text" placeholder="Buscar..." onkeyup="filtrar('tablaRoles')" aria-label="Buscar"></div>
        </div>
        <table id="tablaRoles">
            <thead><tr><th>Descripción</th><th>Acciones</th></tr></thead>
            <tbody>
                <c:forEach var="r" items="${lista}">
                    <tr>
                        <td>${r.descripcion_rol}</td>
                        <td>
                            <form action="${ctx}/RolesAdmi" method="post" class="inline-action-form">
                                <input type="hidden" name="accion" value="editar">
                                <input type="hidden" name="id" value="${r.idRoles}">
                                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                <button type="submit" class="action-btn edit"><i class="ti ti-edit"></i></button>
                            </form>
                            <form action="${ctx}/RolesAdmi" method="post" class="inline-action-form" onsubmit="return confirmar()">
                                <input type="hidden" name="accion" value="eliminar">
                                <input type="hidden" name="id" value="${r.idRoles}">
                                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                <button type="submit" class="action-btn del"><i class="ti ti-trash"></i></button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty lista}"><tr><td colspan="2" class="empty-row">No hay roles registrados</td></tr></c:if>
            </tbody>
        </table>
    </div>
</main>
<script src="${ctx}/Vista/JavaScript/admin.js"></script>
</body></html>
