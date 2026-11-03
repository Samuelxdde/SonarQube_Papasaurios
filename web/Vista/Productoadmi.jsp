<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Productos — Panel Admin</title>
    <link rel="stylesheet" href="${ctx}/Vista/Css/admin.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@tabler/icons-webfont@2.44.0/tabler-icons.min.css" integrity="sha384-mY6asDzXlnHeimHHXw8cavPeArhEcAfnCibDFiaDPex3Fudo+edh/hnT7siZz8bg" crossorigin="anonymous">
    <link href="https://fonts.googleapis.com/css2?family=DM+Sans:wght@400;500;600&family=Syne:wght@600;700&display=swap" rel="stylesheet">
</head>
<body>
<%@ include file="sidebar.jsp" %>
<main class="main-content">
    <%@ include file="topbar.jsp" %>
    <div class="crud-header"><h2 class="page-title"><i class="ti ti-bone"></i> Productos del menú</h2></div>
    <c:if test="${not empty mensaje}"><div class="alert alert-ok"><i class="ti ti-circle-check"></i> ${mensaje}</div></c:if>
    <c:if test="${not empty error}"><div class="alert alert-err"><i class="ti ti-alert-circle"></i> ${error}</div></c:if>

    <div class="panel form-panel">
        <div class="panel-header">
            <span class="panel-title"><i class="ti ti-${not empty productoEditar ? 'edit' : 'plus'}"></i> ${not empty productoEditar ? 'Editar producto' : 'Nuevo producto'}</span>
        </div>
        <form action="${ctx}/ProductoAdmi" method="post" class="crud-form">
            <input type="hidden" name="accion" value="${not empty productoEditar ? 'actualizar' : 'insertar'}">
            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
            <c:if test="${not empty productoEditar}"><input type="hidden" name="idProducto" value="${productoEditar.idProducto}"></c:if>
            <div class="form-row">
                <div class="form-group">
                    <label for="nombre_producto">Nombre del producto</label>
                    <input type="text" name="nombre_producto" value="${productoEditar.nombre_producto}" required placeholder="Ej: Dino Burguer" id="nombre_producto">
                </div>
                <div class="form-group">
                    <label for="Categoria_idCategoria">Categoría</label>
                    <select name="Categoria_idCategoria" required id="Categoria_idCategoria">
                        <option value="">-- Seleccione --</option>
                        <c:forEach var="cat" items="${listaCategorias}">
                            <option value="${cat.idCategoria}" ${productoEditar.categoria_idCategoria == cat.idCategoria ? 'selected' : ''}>${cat.nombre_categoria}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-group">
                    <label for="precio_base">Precio base</label>
                    <input type="number" name="precio_base" value="${productoEditar.precio_base}" required placeholder="Ej: 32000" id="precio_base">
                </div>
                <div class="form-group" style="display:flex; align-items:center; gap:8px;">
                    <label for="descripcion_producto" style="text-transform:none; font-size:13px; display:flex; align-items:center; gap:6px;">
                        <input type="checkbox" name="disponible" style="width:auto;" ${empty productoEditar or productoEditar.disponible ? 'checked' : ''}>
                        Disponible en el menú
                    </label>
                </div>
                <div class="form-group" style="grid-column: 1 / -1;">
                    <label for="descripcion_producto">Descripción</label>
                    <input type="text" name="descripcion_producto" value="${productoEditar.descripcion_producto}" placeholder="Ingredientes o detalle del producto" id="descripcion_producto">
                </div>
                <div class="form-group" style="grid-column: 1 / -1;">
                    <label for="imagen_url">URL de la foto</label>
                    <input type="text" name="imagen_url" value="${productoEditar.imagen_url}" placeholder="https://... (enlace directo a una imagen .jpg o .png)" id="imagen_url">
                    <c:if test="${not empty productoEditar.imagen_url}">
                        <c:choose>
                            <c:when test="${fn:startsWith(productoEditar.imagen_url, 'http')}">
                                <img src="${productoEditar.imagen_url}" alt="" style="margin-top:8px;height:70px;width:70px;object-fit:cover;border-radius:8px;border:1px solid var(--border);">
                            </c:when>
                            <c:otherwise>
                                <img src="${ctx}${productoEditar.imagen_url}" alt="" style="margin-top:8px;height:70px;width:70px;object-fit:cover;border-radius:8px;border:1px solid var(--border);">
                            </c:otherwise>
                        </c:choose>
                    </c:if>
                </div>
            </div>
            <div class="form-actions">
                <button type="submit" class="btn-primary"><i class="ti ti-${not empty productoEditar ? 'device-floppy' : 'plus'}"></i> ${not empty productoEditar ? 'Actualizar' : 'Insertar'}</button>
                <c:if test="${not empty productoEditar}"><a href="${ctx}/ProductoAdmi" class="btn-sm">Cancelar</a></c:if>
            </div>
        </form>

        <c:if test="${not empty variantesProducto}">
            <div style="margin-top:18px;">
                <p style="color:var(--text2); font-size:13px; margin-bottom:8px;"><i class="ti ti-stack"></i> Variantes de este producto:</p>
                <c:forEach var="v" items="${variantesProducto}">
                    <span class="status pendiente" style="margin-right:6px;">${v.nombre_variante}: $${v.precio_variante}</span>
                </c:forEach>
                <p style="margin-top:8px;"><a href="${ctx}/VarianteAdmi" class="btn-sm">Gestionar variantes</a></p>
            </div>
        </c:if>
    </div>

    <div class="panel full-panel">
        <div class="panel-header">
            <span class="panel-title"><i class="ti ti-list"></i> Productos registrados</span>
            <div class="search-bar"><i class="ti ti-search"></i><input type="text" placeholder="Buscar..." onkeyup="filtrar('tablaProductos')" aria-label="Buscar"></div>
        </div>
        <table id="tablaProductos">
            <thead><tr><th>Foto</th><th>Nombre</th><th>Categoría</th><th>Precio base</th><th>Disponible</th><th>Acciones</th></tr></thead>
            <tbody>
                <c:forEach var="p" items="${lista}">
                    <tr>
                        <td>
                            <c:choose>
                                <c:when test="${not empty p.imagen_url and fn:startsWith(p.imagen_url, 'http')}">
                                    <img src="${p.imagen_url}" alt="" style="height:42px;width:42px;object-fit:cover;border-radius:6px;">
                                </c:when>
                                <c:when test="${not empty p.imagen_url}">
                                    <img src="${ctx}${p.imagen_url}" alt="" style="height:42px;width:42px;object-fit:cover;border-radius:6px;">
                                </c:when>
                                <c:otherwise>
                                    <span style="color:var(--text2);font-size:12px;">— sin foto —</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>${p.nombre_producto}</td>
                        <td>
                            <c:forEach var="cat" items="${listaCategorias}">
                                <c:if test="${cat.idCategoria == p.categoria_idCategoria}">${cat.nombre_categoria}</c:if>
                            </c:forEach>
                        </td>
                        <td>$${p.precio_base}</td>
                        <td><span class="status ${p.disponible ? 'activo' : 'inactivo'}">${p.disponible ? 'Sí' : 'No'}</span></td>
                        <td>
                            <form action="${ctx}/ProductoAdmi" method="post" class="inline-action-form">
                                <input type="hidden" name="accion" value="editar">
                                <input type="hidden" name="id" value="${p.idProducto}">
                                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                <button type="submit" class="action-btn edit"><i class="ti ti-edit"></i></button>
                            </form>
                            <form action="${ctx}/ProductoAdmi" method="post" class="inline-action-form" onsubmit="return confirmar()">
                                <input type="hidden" name="accion" value="eliminar">
                                <input type="hidden" name="id" value="${p.idProducto}">
                                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                <button type="submit" class="action-btn del"><i class="ti ti-trash"></i></button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty lista}"><tr><td colspan="6" class="empty-row">No hay productos registrados</td></tr></c:if>
            </tbody>
        </table>
    </div>
</main>
<script src="${ctx}/Vista/JavaScript/admin.js"></script>
</body></html>
