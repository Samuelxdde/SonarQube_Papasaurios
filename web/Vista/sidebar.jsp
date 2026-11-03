<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<aside class="sidebar" id="sidebar">
    <div class="sidebar-logo">
        <img src="${ctx}/Vista/Imagenes/loguito.png" alt="Papasaurios" style="width:36px;height:36px;border-radius:50%;object-fit:cover;flex-shrink:0;">
        <div>
            <div class="logo-title">Papasaurios</div>
            <div class="logo-sub">Panel administrativo</div>
        </div>
    </div>
    <nav class="sidebar-nav">
        <div class="nav-section">General</div>
        <a href="${ctx}/PanelAdmin" class="nav-item"><i class="ti ti-layout-dashboard"></i> Inicio</a>
        <a href="${ctx}/Usuario" class="nav-item"><i class="ti ti-users"></i> Usuarios</a>

        <div class="nav-section">Menú</div>
        <a href="${ctx}/CategoriaAdmi" class="nav-item"><i class="ti ti-category"></i> Categorías</a>
        <a href="${ctx}/ProductoAdmi" class="nav-item"><i class="ti ti-bone"></i> Productos</a>
        <a href="${ctx}/VarianteAdmi" class="nav-item" title="Variantes (tamaños/combos)"><i class="ti ti-stack"></i> Variantes</a>

        <div class="nav-section">Pedidos</div>
        <a href="${ctx}/PedidoAdmi" class="nav-item"><i class="ti ti-shopping-cart"></i> Pedidos</a>
        <a href="${ctx}/PagosAdmi" class="nav-item"><i class="ti ti-credit-card"></i> Pagos</a>

        <div class="nav-section">Configuración</div>
        <a href="${ctx}/Tipodoc" class="nav-item"><i class="ti ti-file-description"></i> Tipo documento</a>
        <a href="${ctx}/RolesAdmi" class="nav-item"><i class="ti ti-shield"></i> Roles</a>
        <a href="${ctx}/CerrarSesion" class="nav-item nav-logout"><i class="ti ti-logout"></i> Cerrar sesión</a>
    </nav>
</aside>
<div class="sidebar-overlay" id="sidebarOverlay" role="button" tabindex="0" aria-label="Cerrar menú" onclick="closeSidebarMobile()" onkeydown="if (event.key === 'Enter' || event.key === ' ') { event.preventDefault(); closeSidebarMobile(); }"></div>

<script>
// Un solo botón hamburguesa (topbar.jsp) sirve para dos comportamientos
// distintos según el ancho de pantalla:
//  - Escritorio (>900px): colapsa el sidebar a solo íconos.
//  - Móvil (<=900px): lo desliza como panel flotante con overlay oscuro.
// Antes esta función no existía en ninguna página admin (o solo hacía la
// mitad del trabajo en Usuarios), por eso el botón no abría nada en móvil.
function toggleSidebar() {
    var sidebar = document.getElementById('sidebar');
    var overlay = document.getElementById('sidebarOverlay');
    if (!sidebar) return;
    if (window.innerWidth <= 900) {
        sidebar.classList.toggle('mobile-open');
        if (overlay) overlay.classList.toggle('show');
    } else {
        sidebar.classList.toggle('collapsed');
    }
}

function closeSidebarMobile() {
    var sidebar = document.getElementById('sidebar');
    var overlay = document.getElementById('sidebarOverlay');
    if (sidebar) sidebar.classList.remove('mobile-open');
    if (overlay) overlay.classList.remove('show');
}
</script>
