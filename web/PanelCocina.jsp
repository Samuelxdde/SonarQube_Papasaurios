<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Panel de cocina — Papasaurios</title>
    <link rel="icon" type="image/png" href="${ctx}/Vista/Imagenes/loguito.png">
    <link rel="stylesheet" href="${ctx}/Vista/Css/style.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@tabler/icons-webfont@2.44.0/tabler-icons.min.css" integrity="sha384-mY6asDzXlnHeimHHXw8cavPeArhEcAfnCibDFiaDPex3Fudo+edh/hnT7siZz8bg" crossorigin="anonymous">
    <style>
        .pedido-nota { font-size: 0.78rem; color: var(--dorado2); margin-top: 2px; font-style: italic; }
        .cocina-card-listo { opacity: 0.7; }
        .online-dot { width: 10px; height: 10px; border-radius: 50%; flex-shrink: 0; background: #3fa34d; box-shadow: 0 0 8px rgba(63,163,77,0.6); display: inline-block; }
        .live-dot { width: 9px; height: 9px; border-radius: 50%; background: #3fa34d; box-shadow: 0 0 0 3px rgba(63,163,77,0.25); animation: pulso-vivo 1.6s ease-in-out infinite; display:inline-block; margin-left:10px; vertical-align:middle; }
        @keyframes pulso-vivo { 0%, 100% { opacity: 1; } 50% { opacity: 0.35; } }
        .repartidor-activo-card { display:flex; align-items:center; gap:12px; }
        .repartidor-activo-nombre { font-weight: 600; }
        .repartidor-activo-tel { font-size: 0.85rem; opacity: 0.8; margin-top: 2px; }

        /* ===== Ventanita flotante "Repartidores activos" ===== */
        .repartidores-fab {
            position: fixed;
            right: 22px;
            bottom: 22px;
            z-index: 500;
            width: 56px;
            height: 56px;
            border-radius: 50%;
            border: none;
            cursor: pointer;
            background: var(--morado);
            color: var(--crema);
            font-size: 1.4rem;
            box-shadow: 0 10px 26px rgba(28,46,35,0.35);
            display: flex;
            align-items: center;
            justify-content: center;
            transition: transform 0.15s, background 0.2s;
        }
        .repartidores-fab:hover { background: var(--morado2); transform: translateY(-2px); }
        .repartidores-fab-badge {
            position: absolute;
            top: -4px; right: -4px;
            min-width: 20px;
            height: 20px;
            padding: 0 5px;
            border-radius: 10px;
            background: #3fa34d;
            color: #fff;
            font-size: 0.72rem;
            font-weight: 700;
            display: flex;
            align-items: center;
            justify-content: center;
            box-shadow: 0 0 0 2px var(--crema);
        }
        .repartidores-fab-badge.vacio { background: var(--texto-medio); }

        .repartidores-popover {
            position: fixed;
            right: 22px;
            bottom: 90px;
            z-index: 500;
            width: 300px;
            max-width: calc(100vw - 44px);
            max-height: 60vh;
            display: flex;
            flex-direction: column;
            background: #fff;
            border-radius: 16px;
            border-top: 4px solid var(--dorado);
            box-shadow: 0 20px 50px rgba(0,0,0,0.28);
            opacity: 0;
            transform: translateY(12px);
            pointer-events: none;
            transition: opacity 0.18s ease, transform 0.18s ease;
        }
        .repartidores-popover.abierto {
            opacity: 1;
            transform: translateY(0);
            pointer-events: auto;
        }
        .repartidores-popover-header {
            display: flex;
            align-items: center;
            justify-content: space-between;
            gap: 8px;
            padding: 14px 16px;
            font-weight: 700;
            color: var(--morado);
            border-bottom: 1px solid var(--crema2);
        }
        .repartidores-popover-cerrar {
            width: 26px; height: 26px;
            border: none;
            border-radius: 50%;
            background: var(--crema);
            color: var(--morado);
            cursor: pointer;
            line-height: 1;
            font-size: 1rem;
            flex-shrink: 0;
        }
        .repartidores-popover-cerrar:hover { background: var(--crema2); }
        .repartidores-popover-body {
            padding: 12px 14px 16px;
            overflow-y: auto;
        }
        .repartidores-popover-body .repartidor-activo-card { margin-bottom: 10px; }
        .repartidores-popover-body .repartidor-activo-card:last-child { margin-bottom: 0; }
    </style>
</head>
<body>

<nav class="navbar scrolled" id="navbar">
    <div class="navbar-inner">
        <ul class="nav-links nav-left">
            <li><a href="${ctx}/">Inicio</a></li>
        </ul>
        <a href="${ctx}/PanelCocina" class="nav-logo">
            <img src="${ctx}/Vista/Imagenes/loguito.png" alt="Logo">
            <span>Papasaurios</span>
        </a>
        <ul class="nav-links nav-right">
            <li class="nav-user"><i class="ti ti-chef-hat"></i> Hola, <c:out value="${sessionScope.nombreUsuario}"/></li>
            <li><a href="${ctx}/CerrarSesion" class="nav-btn">Salir</a></li>
        </ul>
        <button class="hamburger" id="hamburger" aria-label="Menú"><span></span><span></span><span></span></button>
    </div>
    <div class="mobile-menu" id="mobileMenu">
        <a href="${ctx}/">Inicio</a>
        <a href="${ctx}/CerrarSesion">Cerrar Sesión</a>
    </div>
</nav>

<section class="seccion seccion-perfil" style="padding-top:140px;">
    <div class="seccion-header reveal">
        <span class="label-seccion">Panel de cocina</span>
        <h2>Hola, <c:out value="${sessionScope.nombreUsuario}"/> 🍳</h2>
        <p>Cola de pedidos, del más viejo al más nuevo.</p>
    </div>

    <div class="perfil-grid reveal">
        <div class="perfil-card">
            <div class="perfil-card-icon"><i class="ti ti-receipt"></i></div>
            <div>
                <div class="perfil-card-label">Por empezar</div>
                <div class="perfil-card-valor">${recibidos.size()}</div>
            </div>
        </div>
        <div class="perfil-card">
            <div class="perfil-card-icon"><i class="ti ti-tools-kitchen-2"></i></div>
            <div>
                <div class="perfil-card-label">En preparación</div>
                <div class="perfil-card-valor">${enPreparacion.size()}</div>
            </div>
        </div>
    </div>
</section>

<c:if test="${not empty error}">
    <div class="alert alert-err" style="max-width:600px;margin:0 auto 24px;"><i class="ti ti-alert-circle"></i> ${error}</div>
</c:if>
<c:if test="${not empty mensaje}">
    <div class="alert alert-ok" style="max-width:600px;margin:0 auto 24px;"><i class="ti ti-circle-check"></i> ${mensaje}</div>
</c:if>

<button type="button" class="repartidores-fab" id="repartidoresFab" aria-label="Ver repartidores trabajando ahora">
    <i class="ti ti-moped"></i>
    <span class="repartidores-fab-badge${empty repartidoresActivos ? ' vacio' : ''}" id="repartidoresBadge">${repartidoresActivos.size()}</span>
</button>

<div class="repartidores-popover" id="repartidoresPopover">
    <div class="repartidores-popover-header">
        <span><i class="ti ti-moped"></i> Repartidores activos <span class="live-dot" title="Se actualiza solo"></span></span>
        <button type="button" class="repartidores-popover-cerrar" id="repartidoresCerrar" aria-label="Cerrar">&times;</button>
    </div>
    <div class="repartidores-popover-body" id="listaRepartidoresActivos">
        <c:forEach var="r" items="${repartidoresActivos}">
            <div class="reserva-card repartidor-activo-card">
                <span class="online-dot"></span>
                <div>
                    <div class="repartidor-activo-nombre"><c:out value="${r.nombre} ${r.apellido}"/></div>
                    <div class="repartidor-activo-tel"><i class="ti ti-phone"></i> <c:out value="${r.telefono}"/></div>
                </div>
            </div>
        </c:forEach>
        <c:if test="${empty repartidoresActivos}">
            <div class="reserva-card-vacio">
                <i class="ti ti-moped"></i>
                <p>Ningún repartidor tiene la sesión activa en este momento.</p>
            </div>
        </c:if>
    </div>
</div>

<section class="seccion seccion-dark">
    <div class="seccion-header reveal">
        <span class="label-seccion">Paso 1</span>
        <h2>Por empezar</h2>
    </div>

    <div class="reservas-usuario-grid reveal">
        <c:forEach var="pedido" items="${recibidos}">
            <div class="reserva-card">
                <div class="pedido-card-top">
                    <div class="reserva-card-fecha"><i class="ti ti-hash"></i> Pedido ${pedido.idPedido}</div>
                    <span class="status pendiente">Recibido</span>
                </div>
                <div class="reserva-card-hora"><i class="ti ti-clock"></i> <fmt:formatDate value="${pedido.hora}" pattern="hh:mm a"/></div>
                <div class="reserva-card-personas">
                    <i class="ti ti-truck-delivery"></i> ${pedido.tipo_entrega}
                    <c:if test="${not empty pedido.direccion_entrega}"> — <c:out value="${pedido.direccion_entrega}"/></c:if>
                </div>

                <c:set var="detalles" value="${detallesPorPedido[pedido.idPedido]}"/>
                <c:if test="${not empty detalles}">
                    <ul class="pedido-detalle-lista">
                        <c:forEach var="d" items="${detalles}">
                            <li>
                                <span class="pedido-detalle-cant">${d.cantidad}x</span> ${nombresProducto[d.producto_idProducto]}
                                <c:if test="${not empty d.nota}"><div class="pedido-nota"><i class="ti ti-note"></i> ${d.nota}</div></c:if>
                            </li>
                        </c:forEach>
                    </ul>
                </c:if>

                <form action="${ctx}/PanelCocina" method="post" style="margin-top:14px;">
                    <input type="hidden" name="accion" value="empezar">
                    <input type="hidden" name="id" value="${pedido.idPedido}">
                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                    <button type="submit" class="btn-hero-primary" style="border:none;cursor:pointer;font-family:inherit;width:100%;">
                        <i class="ti ti-player-play"></i> Empezar preparación
                    </button>
                </form>
            </div>
        </c:forEach>
        <c:if test="${empty recibidos}">
            <div class="reserva-card-vacio">
                <i class="ti ti-checkbox"></i>
                <p>No hay pedidos nuevos por empezar.</p>
            </div>
        </c:if>
    </div>
</section>

<section class="seccion">
    <div class="seccion-header reveal">
        <span class="label-seccion">Paso 2</span>
        <h2>En preparación</h2>
    </div>

    <div class="reservas-usuario-grid reveal">
        <c:forEach var="pedido" items="${enPreparacion}">
            <div class="reserva-card">
                <div class="pedido-card-top">
                    <div class="reserva-card-fecha"><i class="ti ti-hash"></i> Pedido ${pedido.idPedido}</div>
                    <span class="status activo">En preparación</span>
                </div>
                <div class="reserva-card-hora"><i class="ti ti-clock"></i> <fmt:formatDate value="${pedido.hora}" pattern="hh:mm a"/></div>
                <c:if test="${not empty pedido.hora_estimada}">
                    <div class="reserva-card-hora"><i class="ti ti-hourglass"></i> Estimado: <fmt:formatDate value="${pedido.hora_estimada}" pattern="hh:mm a"/></div>
                </c:if>
                <div class="reserva-card-personas">
                    <i class="ti ti-truck-delivery"></i> ${pedido.tipo_entrega}
                    <c:if test="${not empty pedido.direccion_entrega}"> — <c:out value="${pedido.direccion_entrega}"/></c:if>
                </div>

                <c:set var="detalles" value="${detallesPorPedido[pedido.idPedido]}"/>
                <c:if test="${not empty detalles}">
                    <ul class="pedido-detalle-lista">
                        <c:forEach var="d" items="${detalles}">
                            <li>
                                <span class="pedido-detalle-cant">${d.cantidad}x</span> ${nombresProducto[d.producto_idProducto]}
                                <c:if test="${not empty d.nota}"><div class="pedido-nota"><i class="ti ti-note"></i> ${d.nota}</div></c:if>
                            </li>
                        </c:forEach>
                    </ul>
                </c:if>

                <form action="${ctx}/PanelCocina" method="post" style="margin-top:14px;">
                    <input type="hidden" name="accion" value="listo">
                    <input type="hidden" name="id" value="${pedido.idPedido}">
                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                    <button type="submit" class="btn-hero-primary" style="border:none;cursor:pointer;font-family:inherit;width:100%;">
                        <i class="ti ti-check"></i> Marcar como listo
                    </button>
                </form>
            </div>
        </c:forEach>
        <c:if test="${empty enPreparacion}">
            <div class="reserva-card-vacio">
                <i class="ti ti-tools-kitchen-2"></i>
                <p>No hay nada en preparación en este momento.</p>
            </div>
        </c:if>
    </div>
</section>

<section class="seccion seccion-dark">
    <div class="seccion-header reveal">
        <span class="label-seccion">Historial</span>
        <h2>Listos recientemente</h2>
    </div>

    <div class="reservas-usuario-grid reveal">
        <c:forEach var="pedido" items="${listosRecientes}">
            <div class="reserva-card cocina-card-listo">
                <div class="pedido-card-top">
                    <div class="reserva-card-fecha"><i class="ti ti-hash"></i> Pedido ${pedido.idPedido}</div>
                    <span class="status activo"><i class="ti ti-check"></i> Listo</span>
                </div>
                <div class="reserva-card-personas">
                    <i class="ti ti-truck-delivery"></i> ${pedido.tipo_entrega}
                    <c:if test="${not empty pedido.direccion_entrega}"> — <c:out value="${pedido.direccion_entrega}"/></c:if>
                </div>
            </div>
        </c:forEach>
        <c:if test="${empty listosRecientes}">
            <div class="reserva-card-vacio">
                <i class="ti ti-history"></i>
                <p>Todavía no has marcado ningún pedido como listo.</p>
            </div>
        </c:if>
    </div>
</section>

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
const hamburger = document.getElementById('hamburger');
const mobileMenu = document.getElementById('mobileMenu');
hamburger.addEventListener('click', () => { hamburger.classList.toggle('active'); mobileMenu.classList.toggle('open'); });
const reveals = document.querySelectorAll('.reveal');
const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => { if (entry.isIntersecting) { entry.target.classList.add('visible'); observer.unobserve(entry.target); } });
}, { threshold: 0.1 });
reveals.forEach(el => observer.observe(el));

// Auto-refresco de la cocina: cada 8 segundos pregunta al servidor si
// hay pedidos nuevos o si alguno cambió de estado (por ejemplo, si otra
// persona en otro dispositivo ya lo marcó). Si nada cambió, no hace
// nada — la pantalla se queda quieta, sin parpadear ni perder el
// scroll. Solo recarga cuando de verdad hay algo nuevo que ver.
(function () {
    let ultimaFirma = "${firmaEstado}";
    setInterval(async () => {
        try {
            const res = await fetch('${ctx}/PanelCocina?check=estado', { cache: 'no-store' });
            const datos = await res.json();
            if (datos.firma !== ultimaFirma) {
                location.reload();
            }
        } catch (e) {
            // Sin internet momentáneamente o el servidor no respondió:
            // no pasa nada, se vuelve a intentar en el siguiente ciclo.
        }
    }, 8000);
})();

// Ventanita flotante "Repartidores trabajando ahora": se abre/cierra con el
// botón, y cada 10 segundos vuelve a preguntar quién está activo para
// redibujar la lista y el numerito del botón.
(function () {
    const fab = document.getElementById('repartidoresFab');
    const popover = document.getElementById('repartidoresPopover');
    const cerrar = document.getElementById('repartidoresCerrar');
    const badge = document.getElementById('repartidoresBadge');
    const contenedor = document.getElementById('listaRepartidoresActivos');
    if (!fab || !popover || !contenedor) return;

    function abrirCerrar() { popover.classList.toggle('abierto'); }
    fab.addEventListener('click', abrirCerrar);
    cerrar.addEventListener('click', () => popover.classList.remove('abierto'));
    document.addEventListener('click', (e) => {
        if (!popover.classList.contains('abierto')) return;
        if (popover.contains(e.target) || fab.contains(e.target)) return;
        popover.classList.remove('abierto');
    });

    function escaparHtml(texto) {
        const div = document.createElement('div');
        div.textContent = texto;
        return div.innerHTML;
    }

    function redibujar(repartidores) {
        const cantidad = repartidores ? repartidores.length : 0;
        if (badge) {
            badge.textContent = cantidad;
            badge.classList.toggle('vacio', cantidad === 0);
        }
        if (!repartidores || repartidores.length === 0) {
            contenedor.innerHTML = '<div class="reserva-card-vacio"><i class="ti ti-moped"></i><p>Ningún repartidor tiene la sesión activa en este momento.</p></div>';
            return;
        }
        let html = '';
        repartidores.forEach((r) => {
            html += '<div class="reserva-card repartidor-activo-card">'
                + '<span class="online-dot"></span>'
                + '<div>'
                + '<div class="repartidor-activo-nombre">' + escaparHtml(r.nombre) + '</div>'
                + '<div class="repartidor-activo-tel"><i class="ti ti-phone"></i> ' + escaparHtml(r.telefono || '') + '</div>'
                + '</div></div>';
        });
        contenedor.innerHTML = html;
    }

    setInterval(async () => {
        try {
            const res = await fetch('${ctx}/PanelCocina?check=repartidores', { cache: 'no-store' });
            const datos = await res.json();
            redibujar(datos.repartidores);
        } catch (e) {
            // Sin conexión momentánea: se reintenta en el siguiente ciclo.
        }
    }, 10000);
})();
</script>
</body>
</html>
