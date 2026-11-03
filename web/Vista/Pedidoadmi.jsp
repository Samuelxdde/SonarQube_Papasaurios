<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Pedidos — Panel Admin</title>
    <link rel="stylesheet" href="${ctx}/Vista/Css/admin.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@tabler/icons-webfont@2.44.0/tabler-icons.min.css" integrity="sha384-mY6asDzXlnHeimHHXw8cavPeArhEcAfnCibDFiaDPex3Fudo+edh/hnT7siZz8bg" crossorigin="anonymous">
    <link href="https://fonts.googleapis.com/css2?family=DM+Sans:wght@400;500;600&family=Syne:wght@600;700&display=swap" rel="stylesheet">
</head>
<body>
<%@ include file="sidebar.jsp" %>
<main class="main-content">
    <%@ include file="topbar.jsp" %>
    <div class="crud-header"><h2 class="page-title"><i class="ti ti-shopping-cart"></i> Pedidos</h2></div>
    <c:if test="${not empty mensaje}"><div class="alert alert-ok"><i class="ti ti-circle-check"></i> ${mensaje}</div></c:if>
    <c:if test="${not empty error}"><div class="alert alert-err"><i class="ti ti-alert-circle"></i> ${error}</div></c:if>

    <div class="panel full-panel">
        <div class="panel-header">
            <span class="panel-title"><i class="ti ti-list"></i> Todos los pedidos</span>
            <div class="search-bar"><i class="ti ti-search"></i><input type="text" placeholder="Buscar..." onkeyup="filtrar('tablaPedidos')" aria-label="Buscar"></div>
        </div>
        <table id="tablaPedidos">
            <thead><tr><th>#</th><th>Usuario</th><th>Entrega</th><th>Fecha</th><th>Total</th><th>Productos</th><th>Estado</th><th>Acciones</th></tr></thead>
            <tbody>
                <c:forEach var="p" items="${lista}">
                    <tr>
                        <td>${p.idPedido}</td>
                        <td>
                            <c:forEach var="u" items="${listaUsuarios}">
                                <c:if test="${u.idUsuarios == p.usuarios_idUsuarios}"><c:out value="${u.nombre}"/> <c:out value="${u.apellido}"/></c:if>
                            </c:forEach>
                        </td>
                        <td>
                            ${p.tipo_entrega}
                            <c:if test="${not empty p.direccion_entrega}"><br><span style="font-size:11px;color:var(--text3);"><c:out value="${p.direccion_entrega}"/></span></c:if>
                            <c:if test="${not empty p.hora_estimada && p.estado_pedido_idEstado_pedido != 4 && p.estado_pedido_idEstado_pedido != 5}">
                                <br><span style="font-size:11px;color:var(--text3);"><i class="ti ti-hourglass"></i> Est: ${p.hora_estimada}</span>
                            </c:if>
                        </td>
                        <td>${p.fecha} ${p.hora}</td>
                        <td>$${p.total}</td>
                        <td>
                            <c:forEach var="d" items="${detallesPorPedido[p.idPedido]}">
                                <div style="font-size:11.5px;color:var(--text2);">
                                    ${d.cantidad}x
                                    <c:forEach var="prod" items="${listaProductos}">
                                        <c:if test="${prod.idProducto == d.producto_idProducto}">${prod.nombre_producto}</c:if>
                                    </c:forEach>
                                </div>
                            </c:forEach>
                        </td>
                        <td>
                            <form action="${ctx}/PedidoAdmi" method="post" style="display:flex; gap:4px; align-items:center;">
                                <input type="hidden" name="idPedido" value="${p.idPedido}">
                                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                <select name="Estado_pedido_idEstado_pedido" onchange="this.form.submit()" style="padding:4px 6px; font-size:12px;" aria-label="Estado del pedido">
                                    <c:forEach var="es" items="${listaEstados}">
                                        <option value="${es.idEstado_pedido}" ${p.estado_pedido_idEstado_pedido == es.idEstado_pedido ? 'selected' : ''}>${es.descripcion_esta}</option>
                                    </c:forEach>
                                </select>
                            </form>
                        </td>
                        <td>
                            <form action="${ctx}/PedidoAdmi" method="post" class="inline-action-form" onsubmit="return confirmar()">
                                <input type="hidden" name="accion" value="eliminar">
                                <input type="hidden" name="id" value="${p.idPedido}">
                                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                <button type="submit" class="action-btn del"><i class="ti ti-trash"></i></button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty lista}"><tr><td colspan="8" class="empty-row">No hay pedidos registrados</td></tr></c:if>
            </tbody>
        </table>
    </div>
</main>
<script src="${ctx}/Vista/JavaScript/admin.js"></script>
</body></html>
