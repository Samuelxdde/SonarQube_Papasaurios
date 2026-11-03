<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Variantes — Panel Admin</title>
    <link rel="stylesheet" href="${ctx}/Vista/Css/admin.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@tabler/icons-webfont@2.44.0/tabler-icons.min.css" integrity="sha384-mY6asDzXlnHeimHHXw8cavPeArhEcAfnCibDFiaDPex3Fudo+edh/hnT7siZz8bg" crossorigin="anonymous">
    <link href="https://fonts.googleapis.com/css2?family=DM+Sans:wght@400;500;600&family=Syne:wght@600;700&display=swap" rel="stylesheet">
</head>
<body>
<%@ include file="sidebar.jsp" %>
<main class="main-content">
    <%@ include file="topbar.jsp" %>
    <div class="crud-header"><h2 class="page-title"><i class="ti ti-stack"></i> Variantes (tamaños / combos)</h2></div>
    <c:if test="${not empty mensaje}"><div class="alert alert-ok"><i class="ti ti-circle-check"></i> ${mensaje}</div></c:if>
    <c:if test="${not empty error}"><div class="alert alert-err"><i class="ti ti-alert-circle"></i> ${error}</div></c:if>

    <div class="panel form-panel">
        <div class="panel-header">
            <span class="panel-title"><i class="ti ti-${not empty varianteEditar ? 'edit' : 'plus'}"></i> ${not empty varianteEditar ? 'Editar variante' : 'Nueva variante'}</span>
        </div>
        <form action="${ctx}/VarianteAdmi" method="post" class="crud-form">
            <input type="hidden" name="accion" value="${not empty varianteEditar ? 'actualizar' : 'insertar'}">
            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
            <c:if test="${not empty varianteEditar}"><input type="hidden" name="idVariante" value="${varianteEditar.idVariante}"></c:if>
            <div class="form-row">
                <div class="form-group">
                    <label for="Producto_idProducto">Producto</label>
                    <select name="Producto_idProducto" required id="Producto_idProducto">
                        <option value="">-- Seleccione --</option>
                        <c:forEach var="p" items="${listaProductos}">
                            <option value="${p.idProducto}" ${varianteEditar.producto_idProducto == p.idProducto ? 'selected' : ''}>${p.nombre_producto}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-group">
                    <label for="nombre_variante">Nombre de la variante</label>
                    <input type="text" name="nombre_variante" value="${varianteEditar.nombre_variante}" required placeholder="Ej: Pequeño, Combo Grande, x2" id="nombre_variante">
                </div>
                <div class="form-group">
                    <label for="precio_variante">Precio</label>
                    <input type="number" name="precio_variante" value="${varianteEditar.precio_variante}" required placeholder="Ej: 25900" id="precio_variante">
                </div>
            </div>
            <div class="form-actions">
                <button type="submit" class="btn-primary"><i class="ti ti-${not empty varianteEditar ? 'device-floppy' : 'plus'}"></i> ${not empty varianteEditar ? 'Actualizar' : 'Insertar'}</button>
                <c:if test="${not empty varianteEditar}"><a href="${ctx}/VarianteAdmi" class="btn-sm">Cancelar</a></c:if>
            </div>
        </form>
    </div>

    <div class="panel full-panel">
        <div class="panel-header">
            <span class="panel-title"><i class="ti ti-list"></i> Variantes registradas</span>
            <div class="search-bar"><i class="ti ti-search"></i><input type="text" placeholder="Buscar..." onkeyup="filtrar('tablaVariantes')" aria-label="Buscar"></div>
        </div>
        <table id="tablaVariantes">
            <thead><tr><th>Producto</th><th>Variante</th><th>Precio</th><th>Acciones</th></tr></thead>
            <tbody>
                <c:forEach var="v" items="${lista}">
                    <tr>
                        <td>
                            <c:forEach var="p" items="${listaProductos}">
                                <c:if test="${p.idProducto == v.producto_idProducto}">${p.nombre_producto}</c:if>
                            </c:forEach>
                        </td>
                        <td>${v.nombre_variante}</td>
                        <td>$${v.precio_variante}</td>
                        <td>
                            <form action="${ctx}/VarianteAdmi" method="post" class="inline-action-form">
                                <input type="hidden" name="accion" value="editar">
                                <input type="hidden" name="id" value="${v.idVariante}">
                                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                <button type="submit" class="action-btn edit"><i class="ti ti-edit"></i></button>
                            </form>
                            <form action="${ctx}/VarianteAdmi" method="post" class="inline-action-form" onsubmit="return confirmar()">
                                <input type="hidden" name="accion" value="eliminar">
                                <input type="hidden" name="id" value="${v.idVariante}">
                                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                <button type="submit" class="action-btn del"><i class="ti ti-trash"></i></button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty lista}"><tr><td colspan="4" class="empty-row">No hay variantes registradas</td></tr></c:if>
            </tbody>
        </table>
    </div>
</main>
<script src="${ctx}/Vista/JavaScript/admin.js"></script>
</body></html>
