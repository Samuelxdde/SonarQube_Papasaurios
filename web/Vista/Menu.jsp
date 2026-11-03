<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setLocale value="es_CO"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="totalCarrito" value="0"/>
<c:forEach var="item" items="${sessionScope.carrito}">
    <c:set var="totalCarrito" value="${totalCarrito + item.value.cantidad}"/>
</c:forEach>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Menú - Papasaurios</title>
    <link rel="stylesheet" href="${ctx}/Vista/Css/style.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@tabler/icons-webfont@2.44.0/tabler-icons.min.css" integrity="sha384-mY6asDzXlnHeimHHXw8cavPeArhEcAfnCibDFiaDPex3Fudo+edh/hnT7siZz8bg" crossorigin="anonymous">
    <script src="${ctx}/Vista/JavaScript/validarReg.js"></script>
    <style>
        /* Aviso flotante al agregar algo al carrito: nace justo encima del
           botón flotante del carrito (abajo a la derecha) y "sale" de ahí,
           en vez de bajar desde arriba de la pantalla — así queda claro
           qué es lo que se actualizó. */
        #toastCarrito {
            position: fixed; bottom: 92px; right: 24px; left: auto; top: auto;
            transform: translateY(14px) scale(0.82);
            transform-origin: bottom right;
            z-index: 999; opacity: 0; pointer-events: none;
            transition: opacity .25s ease, transform .3s cubic-bezier(.34,1.56,.64,1);
            max-width: min(320px, 86vw);
        }
        #toastCarrito.mostrar { opacity: 1; transform: translateY(0) scale(1); pointer-events: auto; }
        #toastCarrito .alert {
            font-size: 0.95rem; font-weight: 700; padding: 14px 20px;
            box-shadow: 0 14px 36px rgba(20,32,26,0.35), 0 4px 12px rgba(20,32,26,0.15);
            border-width: 2px;
        }
        #toastCarrito .alert-ok { border-color: var(--verde2); }
        #toastCarrito .alert i { font-size: 1.15rem; }
        .btn-agregar-carrito.agregado { background: var(--verde); }
    </style>
    <script src="${ctx}/Vista/JavaScript/navActivo.js"></script>
</head>
<body>

<div id="toastCarrito"></div>

<!-- NAVBAR -->
<nav class="navbar scrolled" id="navbar">
    <div class="navbar-inner">
        <ul class="nav-links nav-left">
            <li><a href="${ctx}/">Inicio</a></li>
        </ul>
        <a href="${ctx}/" class="nav-logo">
            <img src="${ctx}/Vista/Imagenes/loguito.png" alt="Logo">
            <span>Papasaurios</span>
        </a>
        <ul class="nav-links nav-right">
            <li><a href="${ctx}/Carrito" class="nav-btn" id="btnAbrirCarrito"><i class="ti ti-shopping-cart"></i> Mi carrito</a></li>
            <c:choose>
                <c:when test="${not empty sessionScope.nombreUsuario}">
                    <li><a href="${ctx}/PanelUsuario">Mis pedidos</a></li>
                </c:when>
                <c:otherwise>
                    <li><a href="${ctx}/Iniciar">Ingresar</a></li>
                </c:otherwise>
            </c:choose>
        </ul>
        <button class="hamburger" id="hamburger" aria-label="Menú"><span></span><span></span><span></span></button>
    </div>
    <div class="mobile-menu" id="mobileMenu">
        <a href="${ctx}/">Inicio</a>
        <a href="${ctx}/Carrito" id="btnAbrirCarritoMobile">Mi carrito</a>
        <c:if test="${not empty sessionScope.nombreUsuario}"><a href="${ctx}/PanelUsuario">Mis pedidos</a></c:if>
        <c:if test="${empty sessionScope.nombreUsuario}"><a href="${ctx}/Iniciar">Ingresar</a></c:if>
    </div>
</nav>

<section class="menu-hero">
    <h1>Nuestro menú</h1>
    <p>Elige tu categoría, arma tu pedido y agrégalo al carrito</p>
</section>

<c:if test="${not empty sessionScope.mensajeCarrito}">
    <div class="alert alert-ok menu-alert"><i class="ti ti-circle-check"></i> ${sessionScope.mensajeCarrito} <a href="${ctx}/Carrito">Ver carrito</a></div>
    <c:remove var="mensajeCarrito" scope="session"/>
</c:if>
<c:if test="${not empty sessionScope.errorCarrito}">
    <div class="alert alert-err menu-alert"><i class="ti ti-alert-circle"></i> ${sessionScope.errorCarrito}</div>
    <c:remove var="errorCarrito" scope="session"/>
</c:if>
<c:if test="${not empty error}">
    <div class="alert alert-err menu-alert"><i class="ti ti-alert-circle"></i> ${error}</div>
</c:if>

<div class="menu-layout">

    <!-- Pestañas de categorías (sticky) -->
    <nav class="menu-tabs" id="menuTabs">
        <%-- La pestaña abierta es la primera categoría que tenga productos
             (no la primera del mapa, que podría venir vacía). --%>
        <c:set var="tabActivaPuesta" value="false"/>
        <c:forEach var="entry" items="${menuPorCategoria}">
            <c:if test="${not empty entry.value}">
                <button class="menu-tab ${not tabActivaPuesta ? 'active' : ''}" data-target="panel-${entry.key.idCategoria}" type="button">
                    ${entry.key.nombre_categoria}
                </button>
                <c:set var="tabActivaPuesta" value="true"/>
            </c:if>
        </c:forEach>
    </nav>

    <!-- Paneles de cada categoría -->
    <div class="menu-panels">
        <c:set var="panelActivoPuesto" value="false"/>
        <c:forEach var="entry" items="${menuPorCategoria}">
            <c:if test="${not empty entry.value}">
                <c:set var="sinImagen" value="${entry.key.nombre_categoria == 'Papas, Salchichas y Quesos' or entry.key.nombre_categoria == 'Proteínas y Toppings' or entry.key.nombre_categoria == 'Salsas'}"/>
                <c:set var="totalProductos" value="${fn:length(entry.value)}"/>

                <div class="menu-panel ${not panelActivoPuesto ? 'active' : ''}" id="panel-${entry.key.idCategoria}">
                    <c:set var="panelActivoPuesto" value="true"/>
                    <div class="menu-panel-header">
                        <h2>${entry.key.nombre_categoria}</h2>
                        <span class="menu-panel-count">${totalProductos} productos</span>
                    </div>

                    <c:choose>
                        <%-- ===== Categorías sin imágenes: listado simple en filas ===== --%>
                        <c:when test="${sinImagen}">
                            <div class="producto-row-list">
                                <c:forEach var="p" items="${entry.value}">
                                    <article class="producto-row">
                                        <div class="producto-row-info">
                                            <h3>${p.nombre_producto}</h3>
                                            <c:if test="${not empty p.descripcion_producto}">
                                                <p>${p.descripcion_producto}</p>
                                            </c:if>
                                        </div>

                                        <form action="${ctx}/Carrito" method="post" class="producto-card-form producto-row-form">
                                            <input type="hidden" name="accion" value="agregar">
                                            <input type="hidden" name="idProducto" value="${p.idProducto}">
                                            <input type="hidden" name="redirect" value="/Menu">

                                            <c:set var="variantes" value="${variantesPorProducto[p.idProducto]}"/>
                                            <div class="producto-row-actions">
                                                <c:choose>
                                                    <c:when test="${not empty variantes}">
                                                        <select name="idVariante" class="producto-select-variante" required aria-label="Variante">
                                                            <c:forEach var="v" items="${variantes}">
                                                                <option value="${v.idVariante}">${v.nombre_variante} — $<fmt:formatNumber value="${v.precio_variante}" pattern="#,##0"/></option>
                                                            </c:forEach>
                                                        </select>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <div class="producto-precio">$<fmt:formatNumber value="${p.precio_base}" pattern="#,##0"/></div>
                                                    </c:otherwise>
                                                </c:choose>

                                                <div class="producto-stepper">
                                                    <button type="button" class="stepper-btn" onclick="step(this,-1)">−</button>
                                                    <input type="number" name="cantidad" value="1" min="1" max="20" class="producto-cantidad" readonly aria-label="Cantidad">
                                                    <button type="button" class="stepper-btn" onclick="step(this,1)">+</button>
                                                </div>
                                                <button type="submit" class="btn-agregar-carrito"><i class="ti ti-shopping-cart-plus"></i></button>
                                            </div>
                                        </form>
                                    </article>
                                </c:forEach>
                            </div>
                        </c:when>

                        <%-- ===== Categorías normales: destacados + grilla completa ===== --%>
                        <c:otherwise>
                            <c:if test="${totalProductos > 0}">
                                <div class="menu-featured-label">Lo más pedido</div>
                                <div class="menu-featured-grid">
                                    <c:forEach var="p" items="${entry.value}" begin="0" end="1" varStatus="fst">
                                        <c:set var="variantesF" value="${variantesPorProducto[p.idProducto]}"/>
                                        <article class="featured-card">
                                            <div class="featured-card-img">
                                                <c:if test="${not empty p.imagen_url}">
                                                    <c:choose>
                                                        <c:when test="${fn:startsWith(p.imagen_url, 'http')}">
                                                            <img src="${p.imagen_url}" alt="${p.nombre_producto}" loading="lazy">
                                                        </c:when>
                                                        <c:otherwise>
                                                            <img src="${ctx}${p.imagen_url}" alt="${p.nombre_producto}" loading="lazy">
                                                        </c:otherwise>
                                                    </c:choose>
                                                </c:if>
                                                <span class="featured-badge ${fst.index == 1 ? 'badge-nuevo' : ''}">${fst.index == 1 ? 'Nuevo' : 'Más pedido'}</span>
                                            </div>
                                            <div class="featured-card-info">
                                                <h3>${p.nombre_producto}</h3>
                                                <c:if test="${empty variantesF}">
                                                    <div class="featured-price">$<fmt:formatNumber value="${p.precio_base}" pattern="#,##0"/></div>
                                                </c:if>
                                            </div>

                                            <form action="${ctx}/Carrito" method="post" class="producto-card-form featured-card-controls">
                                                <input type="hidden" name="accion" value="agregar">
                                                <input type="hidden" name="idProducto" value="${p.idProducto}">
                                                <input type="hidden" name="redirect" value="/Menu">

                                                <c:if test="${not empty variantesF}">
                                                    <select name="idVariante" class="producto-select-variante" required aria-label="Variante">
                                                        <c:forEach var="v" items="${variantesF}">
                                                            <option value="${v.idVariante}">${v.nombre_variante} — $<fmt:formatNumber value="${v.precio_variante}" pattern="#,##0"/></option>
                                                        </c:forEach>
                                                    </select>
                                                </c:if>

                                                <div class="featured-card-controls-row">
                                                    <div class="producto-stepper">
                                                        <button type="button" class="stepper-btn" onclick="step(this,-1)">−</button>
                                                        <input type="number" name="cantidad" value="1" min="1" max="20" class="producto-cantidad" readonly aria-label="Cantidad">
                                                        <button type="button" class="stepper-btn" onclick="step(this,1)">+</button>
                                                    </div>
                                                    <button type="submit" class="btn-agregar-carrito"><i class="ti ti-shopping-cart-plus"></i></button>
                                                </div>
                                            </form>
                                        </article>
                                    </c:forEach>
                                </div>
                            </c:if>

                            <c:if test="${totalProductos > 2}">
                                <div class="menu-subheader">Todo el menú</div>
                                <div class="menu-productos-list">
                                    <c:forEach var="p" items="${entry.value}" begin="2">
                                        <article class="producto-card">
                                            <div class="producto-card-img">
                                                <c:choose>
                                                    <c:when test="${not empty p.imagen_url}">
                                                        <c:choose>
                                                            <c:when test="${fn:startsWith(p.imagen_url, 'http')}">
                                                                <img src="${p.imagen_url}" alt="${p.nombre_producto}" loading="lazy">
                                                            </c:when>
                                                            <c:otherwise>
                                                                <img src="${ctx}${p.imagen_url}" alt="${p.nombre_producto}" loading="lazy">
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="producto-card-placeholder">🦖</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>

                                            <div class="producto-card-body">
                                                <h3>${p.nombre_producto}</h3>
                                                <c:if test="${not empty p.descripcion_producto}">
                                                    <p class="producto-desc">${p.descripcion_producto}</p>
                                                </c:if>

                                                <form action="${ctx}/Carrito" method="post" class="producto-card-form">
                                                    <input type="hidden" name="accion" value="agregar">
                                                    <input type="hidden" name="idProducto" value="${p.idProducto}">
                                                    <input type="hidden" name="redirect" value="/Menu">

                                                    <c:set var="variantes" value="${variantesPorProducto[p.idProducto]}"/>
                                                    <c:choose>
                                                        <c:when test="${not empty variantes}">
                                                            <select name="idVariante" class="producto-select-variante" required aria-label="Variante">
                                                                <c:forEach var="v" items="${variantes}">
                                                                    <option value="${v.idVariante}">${v.nombre_variante} — $<fmt:formatNumber value="${v.precio_variante}" pattern="#,##0"/></option>
                                                                </c:forEach>
                                                            </select>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <div class="producto-precio">$<fmt:formatNumber value="${p.precio_base}" pattern="#,##0"/></div>
                                                        </c:otherwise>
                                                    </c:choose>

                                                    <div class="producto-card-footer">
                                                        <div class="producto-stepper">
                                                            <button type="button" class="stepper-btn" onclick="step(this,-1)">−</button>
                                                            <input type="number" name="cantidad" value="1" min="1" max="20" class="producto-cantidad" readonly aria-label="Cantidad">
                                                            <button type="button" class="stepper-btn" onclick="step(this,1)">+</button>
                                                        </div>
                                                        <button type="submit" class="btn-agregar-carrito"><i class="ti ti-shopping-cart-plus"></i></button>
                                                    </div>
                                                </form>
                                            </div>
                                        </article>
                                    </c:forEach>
                                </div>
                            </c:if>
                        </c:otherwise>
                    </c:choose>
                </div>
            </c:if>
        </c:forEach>
    </div>
</div>

<a href="${ctx}/Carrito" class="carrito-flotante" id="btnAbrirCarritoFlotante">
    <i class="ti ti-shopping-cart"></i>
    <c:if test="${totalCarrito > 0}">
        <span class="carrito-flotante-badge" id="carritoBadge">${totalCarrito}</span>
    </c:if>
</a>

<!-- ===== PANEL LATERAL DEL CARRITO (drawer) ===== -->
<div class="carrito-drawer-overlay" id="carritoDrawerOverlay"></div>
<aside class="carrito-drawer" id="carritoDrawer" aria-hidden="true">
    <div class="carrito-drawer-header">
        <h3><i class="ti ti-shopping-cart"></i> Mi carrito</h3>
        <button type="button" class="carrito-drawer-cerrar" id="btnCerrarCarrito" aria-label="Cerrar">
            <i class="ti ti-x"></i>
        </button>
    </div>
    <div class="carrito-drawer-body" id="carritoDrawerBody">
        <!-- Se llena por JS con fetch al abrir el panel -->
    </div>
    <div class="carrito-drawer-footer" id="carritoDrawerFooter" style="display:none;">
        <div class="carrito-total">Total: $<span id="carritoDrawerTotal">0</span></div>
        <a href="${ctx}/Carrito" class="btn-hero-primary" style="width:100%; display:block; text-align:center;">Ver carrito y pagar</a>
    </div>
</aside>

<!-- ===== MODAL "DEBES INICIAR SESIÓN" (al intentar agregar al carrito) ===== -->
<div class="overlay-login" id="overlayLogin">
    <div class="modal-login">
        <button type="button" class="modal-login-cerrar" onclick="ocultarModalLogin()" aria-label="Cerrar">✕</button>
        <div class="modal-login-icono"><i class="ti ti-lock"></i></div>
        <h3>Inicia sesión</h3>
        <p data-texto-login>Necesitas una cuenta para agregar productos al carrito. Inicia sesión o crea una cuenta en segundos.</p>
        <div class="modal-login-botones">
            <a href="${ctx}/Iniciar" class="btn-login">Iniciar sesión</a>
            <a href="${ctx}/CargarRegistro" class="btn-login btn-secundario">Crear cuenta</a>
        </div>
        <button type="button" class="modal-login-cancelar" onclick="ocultarModalLogin()">Ahora no</button>
    </div>
</div>

<footer class="footer">
    <div class="footer-contenedor">
        <div class="footer-info">
            <h3>Papasaurios - Salchipapería</h3>
            <p>Dirección: DG 34 #13A-40, Soacha</p>
            <p>Teléfono: +57 314 300 7413</p>
        </div>
        <div class="logo-footer">
            <img src="${ctx}/Vista/Imagenes/loguito.png" alt="Logo Papasaurios">
        </div>
        <div class="footer-redes">
            <h3>Síguenos</h3>
            <a href="https://www.instagram.com/papasaurios_soacha/" target="_blank">Instagram</a> |
            <a href="https://wa.me/573143007413" target="_blank">WhatsApp</a>
        </div>
    </div>
    <div class="footer-copy"><p>&copy; 2026 Papasaurios. Todos los derechos reservados.</p> <a href="${ctx}/PoliticaDatos">Política de datos y términos</a></div>
</footer>

<script>
const ctxJs = "${ctx}";
const hamburger = document.getElementById('hamburger');
const mobileMenu = document.getElementById('mobileMenu');
hamburger.addEventListener('click', () => { hamburger.classList.toggle('active'); mobileMenu.classList.toggle('open'); });

// Tabs del menú: sin recargar la página, solo muestra/oculta paneles.
document.querySelectorAll('.menu-tab').forEach(tab => {
    tab.addEventListener('click', () => {
        document.querySelectorAll('.menu-tab').forEach(t => t.classList.remove('active'));
        document.querySelectorAll('.menu-panel').forEach(p => p.classList.remove('active'));
        tab.classList.add('active');
        document.getElementById(tab.dataset.target).classList.add('active');
        document.querySelector('.menu-panels').scrollIntoView({ behavior: 'smooth', block: 'start' });
    });
});

// Stepper de cantidad +/-
function step(btn, delta) {
    const input = btn.parentElement.querySelector('.producto-cantidad');
    let val = parseInt(input.value) + delta;
    if (val < 1) val = 1;
    if (val > 20) val = 20;
    input.value = val;
}

// Agregar al carrito sin recargar la página. Antes cada clic mandaba a
// Carrito.jsp y volvía, reiniciando la pestaña activa a la primera
// categoría — molesto si querías seguir agregando cosas de otras
// secciones. Ahora se queda exactamente donde estabas.
let toastTimeout;
function mostrarToast(mensaje, tipo, conLinkCarrito) {
    const toast = document.getElementById('toastCarrito');
    const link = (tipo !== 'error' && conLinkCarrito)
        ? ' <a href="' + ctxJs + '/Carrito" style="color:inherit;text-decoration:underline;margin-left:6px;">Ver carrito</a>' : '';
    toast.innerHTML = '<div class="alert ' + (tipo === 'error' ? 'alert-err' : 'alert-ok') + '">'
        + '<i class="ti ti-' + (tipo === 'error' ? 'alert-circle' : 'circle-check') + '"></i> ' + mensaje + link + '</div>';
    toast.classList.add('mostrar');
    clearTimeout(toastTimeout);
    toastTimeout = setTimeout(() => toast.classList.remove('mostrar'), 3400);
}

document.querySelectorAll('.producto-card-form').forEach(form => {
    form.addEventListener('submit', function (e) {
        e.preventDefault();
        const boton = form.querySelector('.btn-agregar-carrito');
        const datos = new URLSearchParams(new FormData(form));

        fetch(ctxJs + '/Carrito', {
            method: 'POST',
            headers: { 'X-Requested-With': 'XMLHttpRequest' },
            body: datos
        })
        .then(r => r.json())
        .then(data => {
            if (!data.ok) {
                if (data.requiereLogin) {
                    mostrarModalLogin('Necesitas una cuenta para agregar productos al carrito. Inicia sesión o crea una cuenta en segundos.');
                } else {
                    mostrarToast(data.error || 'No se pudo agregar el producto.', 'error');
                }
                return;
            }
            // Feedback visual en el propio botón: un check que dura un instante.
            boton.classList.add('agregado');
            const iconoOriginal = boton.innerHTML;
            boton.innerHTML = '<i class="ti ti-check"></i>';
            setTimeout(() => { boton.classList.remove('agregado'); boton.innerHTML = iconoOriginal; }, 900);

            // Actualiza el contador del carrito flotante.
            let badge = document.getElementById('carritoBadge');
            if (!badge) {
                badge = document.createElement('span');
                badge.className = 'carrito-flotante-badge';
                badge.id = 'carritoBadge';
                document.querySelector('.carrito-flotante').appendChild(badge);
            }
            badge.textContent = data.totalItems;

            // Vuelve la cantidad del stepper a 1, listo para seguir agregando.
            form.querySelector('.producto-cantidad').value = 1;

            mostrarToast('Producto agregado al carrito.', 'ok', true);

            // Si el panel del carrito está abierto mientras se agrega algo
            // desde el menú, se refresca para que se vea al instante.
            if (drawerCarrito.classList.contains('abierto')) {
                cargarCarritoDrawer();
            }
        })
        .catch(() => mostrarToast('No se pudo conectar con el servidor.', 'error'));
    });
});

// ===== Panel lateral del carrito (drawer) =====================
const drawerCarrito = document.getElementById('carritoDrawer');
const overlayCarrito = document.getElementById('carritoDrawerOverlay');
const cuerpoDrawerCarrito = document.getElementById('carritoDrawerBody');
const footerDrawerCarrito = document.getElementById('carritoDrawerFooter');
const totalDrawerCarrito = document.getElementById('carritoDrawerTotal');

function abrirCarritoDrawer(e) {
    if (e) e.preventDefault();
    drawerCarrito.classList.add('abierto');
    overlayCarrito.classList.add('abierto');
    drawerCarrito.setAttribute('aria-hidden', 'false');
    document.body.style.overflow = 'hidden';
    cargarCarritoDrawer();
}

function cerrarCarritoDrawer() {
    drawerCarrito.classList.remove('abierto');
    overlayCarrito.classList.remove('abierto');
    drawerCarrito.setAttribute('aria-hidden', 'true');
    document.body.style.overflow = '';
}

document.getElementById('btnAbrirCarrito').addEventListener('click', abrirCarritoDrawer);
document.getElementById('btnAbrirCarritoMobile').addEventListener('click', abrirCarritoDrawer);
document.getElementById('btnAbrirCarritoFlotante').addEventListener('click', abrirCarritoDrawer);
document.getElementById('btnCerrarCarrito').addEventListener('click', cerrarCarritoDrawer);
overlayCarrito.addEventListener('click', cerrarCarritoDrawer);
document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape' && drawerCarrito.classList.contains('abierto')) cerrarCarritoDrawer();
});

function formatearPrecio(valor) {
    return new Intl.NumberFormat('es-CO').format(valor);
}

function cargarCarritoDrawer() {
    cuerpoDrawerCarrito.innerHTML = '<div style="text-align:center; padding:40px 0; color: var(--texto-medio);">Cargando...</div>';

    fetch(ctxJs + '/Carrito', { headers: { 'X-Requested-With': 'XMLHttpRequest' } })
        .then(r => r.json())
        .then(data => pintarCarritoDrawer(data))
        .catch(() => {
            cuerpoDrawerCarrito.innerHTML = '<div class="carrito-drawer-vacio"><p>No se pudo cargar el carrito.</p></div>';
            footerDrawerCarrito.style.display = 'none';
        });
}

function pintarCarritoDrawer(data) {
    if (!data.items || data.items.length === 0) {
        cuerpoDrawerCarrito.innerHTML =
            '<div class="carrito-drawer-vacio">' +
                '<i class="ti ti-shopping-cart-off"></i>' +
                '<p>Tu carrito está vacío. ¡Ve al menú y arma tu pedido!</p>' +
            '</div>';
        footerDrawerCarrito.style.display = 'none';
        return;
    }

    let html = '';
    data.items.forEach(item => {
        html += '<div class="carrito-item" data-clave="' + item.clave + '">' +
            '<div class="carrito-item-info">' +
                '<h4>' + item.nombreProducto + '</h4>' +
                (item.nombreVariante ? '<span>' + item.nombreVariante + '</span>' : '') +
                '<div class="producto-stepper">' +
                    '<button type="button" class="stepper-btn" onclick="cambiarCantidadDrawer(\'' + item.clave + '\',-1)" aria-label="Quitar una unidad">−</button>' +
                    '<input type="number" class="producto-cantidad" value="' + item.cantidad + '" min="1" max="20" readonly aria-label="Cantidad">' +
                    '<button type="button" class="stepper-btn" onclick="cambiarCantidadDrawer(\'' + item.clave + '\',1)" aria-label="Agregar una unidad">+</button>' +
                '</div>' +
            '</div>' +
            '<div class="carrito-item-precio">$' + formatearPrecio(item.subtotal) + '</div>' +
            '<button type="button" class="carrito-item-del" title="Quitar del carrito" onclick="quitarItemDrawer(\'' + item.clave + '\')">' +
                '<i class="ti ti-trash"></i>' +
            '</button>' +
        '</div>';
    });
    cuerpoDrawerCarrito.innerHTML = html;

    totalDrawerCarrito.textContent = formatearPrecio(data.total);
    footerDrawerCarrito.style.display = '';

    actualizarBadgeCarrito(data.totalItems);
}

function actualizarBadgeCarrito(totalItems) {
    let badge = document.getElementById('carritoBadge');
    if (totalItems > 0) {
        if (!badge) {
            badge = document.createElement('span');
            badge.className = 'carrito-flotante-badge';
            badge.id = 'carritoBadge';
            document.querySelector('.carrito-flotante').appendChild(badge);
        }
        badge.textContent = totalItems;
    } else if (badge) {
        badge.remove();
    }
}

function enviarAccionCarrito(datos) {
    return fetch(ctxJs + '/Carrito', {
        method: 'POST',
        headers: { 'X-Requested-With': 'XMLHttpRequest', 'Content-Type': 'application/x-www-form-urlencoded' },
        body: datos
    }).then(r => r.json());
}

function cambiarCantidadDrawer(clave, delta) {
    const fila = drawerCarrito.querySelector('.carrito-item[data-clave="' + clave + '"]');
    if (!fila) return;
    const input = fila.querySelector('.producto-cantidad');
    let nueva = parseInt(input.value) + delta;
    if (nueva < 1) { quitarItemDrawer(clave); return; }
    if (nueva > 20) nueva = 20;

    enviarAccionCarrito(new URLSearchParams({ accion: 'actualizar', clave: clave, cantidad: nueva }))
        .then(() => cargarCarritoDrawer())
        .catch(() => mostrarToast('No se pudo actualizar el carrito.', 'error'));
}

function quitarItemDrawer(clave) {
    enviarAccionCarrito(new URLSearchParams({ accion: 'quitar', clave: clave }))
        .then(() => cargarCarritoDrawer())
        .catch(() => mostrarToast('No se pudo quitar el producto.', 'error'));
}
</script>
</body>
</html>
