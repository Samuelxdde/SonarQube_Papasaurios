<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Panel Admin — Papasaurios</title>
    <link rel="icon" type="image/png" href="${ctx}/Vista/Imagenes/loguito.png">
    <link rel="stylesheet" href="${ctx}/Vista/Css/admin.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@tabler/icons-webfont@2.44.0/tabler-icons.min.css" integrity="sha384-mY6asDzXlnHeimHHXw8cavPeArhEcAfnCibDFiaDPex3Fudo+edh/hnT7siZz8bg" crossorigin="anonymous">
    <link href="https://fonts.googleapis.com/css2?family=DM+Sans:wght@400;500;600&family=Syne:wght@600;700&display=swap" rel="stylesheet">
</head>
<body>

<%@ include file="Vista/sidebar.jsp" %>

<!-- ===== MAIN ===== -->
<main class="main-content">

    <%@ include file="Vista/topbar.jsp" %>

    <div class="crud-header">
        <h1 class="page-title">Dashboard</h1>
        <p class="page-sub">Resumen general de Papasaurios</p>
    </div>

    <c:if test="${not empty error}">
        <div class="alert alert-err"><i class="ti ti-alert-circle"></i> ${error}</div>
    </c:if>

    <!-- Tarjetas resumen -->
    <section class="cards-grid">
        <div class="stat-card green">
            <div class="stat-icon"><i class="ti ti-users"></i></div>
            <div class="stat-label">Usuarios</div>
            <div class="stat-num">${totalUsuarios}</div>
            <div class="stat-sub">registrados en total</div>
        </div>
        <div class="stat-card blue">
            <div class="stat-icon"><i class="ti ti-shopping-cart"></i></div>
            <div class="stat-label">Pedidos</div>
            <div class="stat-num">${totalPedidos}</div>
            <div class="stat-sub">en el sistema</div>
        </div>
        <div class="stat-card amber">
            <div class="stat-icon"><i class="ti ti-bone"></i></div>
            <div class="stat-label">Productos</div>
            <div class="stat-num">${totalProductos}</div>
            <div class="stat-sub">en el menú</div>
        </div>
        <div class="stat-card teal">
            <div class="stat-icon"><i class="ti ti-credit-card"></i></div>
            <div class="stat-label">Pagos registrados</div>
            <div class="stat-num">${totalPagos}</div>
            <div class="stat-sub">en total</div>
        </div>
        <div class="stat-card blue">
            <div class="stat-icon"><i class="ti ti-moped"></i></div>
            <div class="stat-label">Entregas pendientes</div>
            <div class="stat-num">${totalEntregasPendientes}</div>
            <div class="stat-sub"><a href="${pageContext.request.contextPath}/PedidoAdmi" style="color:inherit;text-decoration:underline;">ver en pedidos</a></div>
        </div>
    </section>

    <!-- Repartidores con sesión activa: botón flotante + ventanita -->
    <button type="button" class="repartidores-fab" id="repartidoresFab" aria-label="Ver repartidores trabajando ahora">
        <i class="ti ti-moped"></i>
        <span class="repartidores-fab-badge${empty repartidoresActivos ? ' vacio' : ''}" id="repartidoresBadge">${repartidoresActivos.size()}</span>
    </button>

    <div class="repartidores-popover" id="repartidoresPopover">
        <div class="repartidores-popover-header">
            <span class="panel-title"><i class="ti ti-moped"></i> Repartidores activos <span class="live-dot" title="Se actualiza solo"></span></span>
            <button type="button" class="repartidores-popover-cerrar" id="repartidoresCerrar" aria-label="Cerrar">&times;</button>
        </div>
        <div class="repartidores-popover-body" id="listaRepartidoresActivos">
            <c:forEach var="r" items="${repartidoresActivos}">
                <div class="repartidor-activo-card">
                    <span class="online-dot"></span>
                    <div class="repartidor-activo-info">
                        <div class="repartidor-activo-nombre"><c:out value="${r.nombre} ${r.apellido}"/></div>
                        <div class="repartidor-activo-tel"><i class="ti ti-phone"></i> <c:out value="${r.telefono}"/></div>
                    </div>
                </div>
            </c:forEach>
            <c:if test="${empty repartidoresActivos}">
                <p class="empty-row">Ningún repartidor tiene la sesión activa en este momento.</p>
            </c:if>
        </div>
    </div>

    <!-- Fila de tablas -->
    <section class="tables-row">

        <!-- Tabla pedidos recientes -->
        <div class="panel">
            <div class="panel-header">
                <span class="panel-title"><i class="ti ti-shopping-cart"></i> Pedidos recientes</span>
                <a href="${ctx}/PedidoAdmi" class="btn-sm">Ver todos</a>
            </div>
            <div class="table-scroll">
            <table>
                <thead>
                    <tr>
                        <th>#</th>
                        <th>Entrega</th>
                        <th>Fecha</th>
                        <th>Total</th>
                        <th>Estado</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="p" items="${pedidos}" begin="0" end="4">
                        <tr>
                            <td>${p.idPedido}</td>
                            <td>${p.tipo_entrega}</td>
                            <td>${p.fecha}</td>
                            <td>$${p.total}</td>
                            <td>
                                <span class="status
                                    <c:choose>
                                        <c:when test="${p.estado_pedido_idEstado_pedido == 4}">activo</c:when>
                                        <c:when test="${p.estado_pedido_idEstado_pedido == 5}">inactivo</c:when>
                                        <c:otherwise>pendiente</c:otherwise>
                                    </c:choose>">
                                    <c:choose>
                                        <c:when test="${p.estado_pedido_idEstado_pedido == 1}">Recibido</c:when>
                                        <c:when test="${p.estado_pedido_idEstado_pedido == 2}">En preparación</c:when>
                                        <c:when test="${p.estado_pedido_idEstado_pedido == 3}">Listo</c:when>
                                        <c:when test="${p.estado_pedido_idEstado_pedido == 4}">Entregado</c:when>
                                        <c:otherwise>Cancelado</c:otherwise>
                                    </c:choose>
                                </span>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty pedidos}">
                        <tr><td colspan="5" class="empty-row">No hay pedidos registrados</td></tr>
                    </c:if>
                </tbody>
            </table>
            </div>
        </div>

        <!-- Tabla productos -->
        <div class="panel">
            <div class="panel-header">
                <span class="panel-title"><i class="ti ti-bone"></i> Productos</span>
                <a href="${ctx}/ProductoAdmi" class="btn-sm">Gestionar</a>
            </div>
            <div class="table-scroll">
            <table>
                <thead>
                    <tr>
                        <th>#</th>
                        <th>Nombre</th>
                        <th>Precio base</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="prod" items="${productos}" begin="0" end="4">
                        <tr>
                            <td>${prod.idProducto}</td>
                            <td>${prod.nombre_producto}</td>
                            <td>$${prod.precio_base}</td>
                            <td>
                                <a href="${ctx}/ProductoAdmi?accion=editar&id=${prod.idProducto}" class="action-btn edit"><i class="ti ti-edit"></i></a>
                                <a href="${ctx}/ProductoAdmi?accion=eliminar&id=${prod.idProducto}" class="action-btn del" onclick="return confirm('¿Eliminar producto?')"><i class="ti ti-trash"></i></a>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty productos}">
                        <tr><td colspan="4" class="empty-row">No hay productos registrados</td></tr>
                    </c:if>
                </tbody>
            </table>
            </div>
        </div>

    </section>

    <!-- Tabla usuarios completa -->
    <section class="panel full-panel">
        <div class="panel-header">
            <span class="panel-title"><i class="ti ti-users"></i> Gestión de usuarios</span>
            <div class="panel-actions">
                <div class="search-bar">
                    <i class="ti ti-search"></i>
                    <input type="text" id="searchUsuarios" placeholder="Buscar usuario..." onkeyup="filtrar('tablaUsuarios')" aria-label="Buscar usuario">
                </div>
                <a href="${ctx}/Usuario" class="btn-primary">
                    <i class="ti ti-plus"></i> Nuevo usuario
                </a>
            </div>
        </div>
        <div class="table-scroll">
        <table id="tablaUsuarios">
            <thead>
                <tr>
                    <th>Nombre</th>
                    <th>Apellido</th>
                    <th>Documento</th>
                    <th>Correo</th>
                    <th>Teléfono</th>
                    <th>Rol</th>
                    <th>Acciones</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="u" items="${usuarios}">
                    <tr>
                        <td><c:out value="${u.nombre}"/></td>
                        <td><c:out value="${u.apellido}"/></td>
                        <td><c:out value="${u.documento}"/></td>
                        <td><c:out value="${u.correo}"/></td>
                        <td><c:out value="${u.telefono}"/></td>
                        <td>
                            <span class="status ${u.roles_idRoles == 1 ? 'activo' : (u.roles_idRoles == 3 || u.roles_idRoles == 4 ? 'pendiente' : '')}">
                                <c:choose>
                                    <c:when test="${u.roles_idRoles == 1}">Admin</c:when>
                                    <c:when test="${u.roles_idRoles == 3}">Repartidor</c:when>
                                    <c:when test="${u.roles_idRoles == 4}">Cocina</c:when>
                                    <c:otherwise>Usuario</c:otherwise>
                                </c:choose>
                            </span>
                        </td>
                        <td>
                            <a href="${ctx}/Usuario" class="action-btn edit" title="Editar y eliminar en Gestión de usuarios"><i class="ti ti-edit"></i></a>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty usuarios}">
                    <tr><td colspan="7" class="empty-row">No hay usuarios registrados</td></tr>
                </c:if>
            </tbody>
        </table>
        </div>
        <div class="text-center" style="margin-top:14px">
            <a href="${ctx}/Usuario" class="btn-sm"><i class="ti ti-arrow-right"></i> Ir a Gestión de usuarios (editar / eliminar)</a>
        </div>
    </section>

</main>

<script src="${ctx}/Vista/JavaScript/admin.js"></script>
<script>
// Ventanita flotante "Repartidores trabajando ahora": se abre/cierra con el
// botón, y cada 10 segundos vuelve a preguntar quién está activo para
// redibujar la lista y el numerito del botón, sin recargar el dashboard.
(function () {
    var ctx = "${ctx}";
    var fab = document.getElementById('repartidoresFab');
    var popover = document.getElementById('repartidoresPopover');
    var cerrar = document.getElementById('repartidoresCerrar');
    var badge = document.getElementById('repartidoresBadge');
    var contenedor = document.getElementById('listaRepartidoresActivos');
    if (!fab || !popover || !contenedor) return;

    fab.addEventListener('click', function () { popover.classList.toggle('abierto'); });
    cerrar.addEventListener('click', function () { popover.classList.remove('abierto'); });
    document.addEventListener('click', function (e) {
        if (!popover.classList.contains('abierto')) return;
        if (popover.contains(e.target) || fab.contains(e.target)) return;
        popover.classList.remove('abierto');
    });

    function escaparHtml(texto) {
        var div = document.createElement('div');
        div.textContent = texto;
        return div.innerHTML;
    }

    function redibujar(repartidores) {
        var cantidad = repartidores ? repartidores.length : 0;
        if (badge) {
            badge.textContent = cantidad;
            badge.classList.toggle('vacio', cantidad === 0);
        }
        if (!repartidores || repartidores.length === 0) {
            contenedor.innerHTML = '<p class="empty-row">Ningún repartidor tiene la sesión activa en este momento.</p>';
            return;
        }
        var html = '';
        repartidores.forEach(function (r) {
            html += '<div class="repartidor-activo-card">'
                + '<span class="online-dot"></span>'
                + '<div class="repartidor-activo-info">'
                + '<div class="repartidor-activo-nombre">' + escaparHtml(r.nombre) + '</div>'
                + '<div class="repartidor-activo-tel"><i class="ti ti-phone"></i> ' + escaparHtml(r.telefono || '') + '</div>'
                + '</div></div>';
        });
        contenedor.innerHTML = html;
    }

    setInterval(async function () {
        try {
            var res = await fetch(ctx + '/PanelAdmin?check=repartidores', { cache: 'no-store' });
            var datos = await res.json();
            redibujar(datos.repartidores);
        } catch (e) {
            // Sin conexión momentánea: se reintenta en el siguiente ciclo.
        }
    }, 10000);
})();
</script>
</body>
</html>
