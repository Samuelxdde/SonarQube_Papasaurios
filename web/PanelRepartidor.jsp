<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Panel repartidor — Papasaurios</title>
    <link rel="icon" type="image/png" href="${ctx}/Vista/Imagenes/loguito.png">
    <link rel="stylesheet" href="${ctx}/Vista/Css/style.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@tabler/icons-webfont@2.44.0/tabler-icons.min.css" integrity="sha384-mY6asDzXlnHeimHHXw8cavPeArhEcAfnCibDFiaDPex3Fudo+edh/hnT7siZz8bg" crossorigin="anonymous">
    <script src="${ctx}/Vista/JavaScript/mapsConfig.js"></script>
    <style>
        .btn-compartir.compartiendo-activo { background: var(--dorado); color: var(--morado); }
        .badge-asignacion { font-size:0.78rem; opacity:.85; display:flex; align-items:center; gap:6px; }
    </style>
</head>
<body>

<nav class="navbar scrolled" id="navbar">
    <div class="navbar-inner">
        <ul class="nav-links nav-left">
            <li><a href="${ctx}/">Inicio</a></li>
        </ul>
        <a href="${ctx}/PanelRepartidor" class="nav-logo">
            <img src="${ctx}/Vista/Imagenes/loguito.png" alt="Logo">
            <span>Papasaurios</span>
        </a>
        <ul class="nav-links nav-right">
            <li class="nav-user"><i class="ti ti-moped"></i> Hola, <c:out value="${sessionScope.nombreUsuario}"/></li>
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
        <span class="label-seccion">Panel de repartidor</span>
        <h2>Hola, <c:out value="${sessionScope.nombreUsuario}"/> 🛵</h2>
        <p>Estos son los pedidos a domicilio que están en curso.</p>
    </div>

    <div class="perfil-grid reveal">
        <div class="perfil-card">
            <div class="perfil-card-icon"><i class="ti ti-truck-delivery"></i></div>
            <div>
                <div class="perfil-card-label">Pendientes por entregar</div>
                <div class="perfil-card-valor">${pendientes.size()}</div>
            </div>
        </div>
        <div class="perfil-card">
            <div class="perfil-card-icon"><i class="ti ti-checks"></i></div>
            <div>
                <div class="perfil-card-label">Entregas completadas (últimas)</div>
                <div class="perfil-card-valor">${completados.size()}</div>
            </div>
        </div>
    </div>
</section>

<section class="seccion seccion-dark">
    <div class="seccion-header reveal">
        <span class="label-seccion">En ruta</span>
        <h2>Pedidos por entregar</h2>
    </div>

    <c:if test="${not empty error}">
        <div class="alert alert-err" style="max-width:600px;margin:0 auto 24px;"><i class="ti ti-alert-circle"></i> ${error}</div>
    </c:if>
    <c:if test="${not empty mensaje}">
        <div class="alert alert-ok" style="max-width:600px;margin:0 auto 24px;"><i class="ti ti-circle-check"></i> ${mensaje}</div>
    </c:if>

    <div class="reservas-usuario-grid reveal">
        <c:forEach var="pedido" items="${pendientes}">
            <c:set var="estado" value="${pedido.estado_pedido_idEstado_pedido}"/>
            <c:set var="cliente" value="${clientesPorId[pedido.usuarios_idUsuarios]}"/>
            <c:set var="sinAsignar" value="${empty pedido.repartidor_idUsuarios}"/>
            <c:set var="esMio" value="${!sinAsignar && pedido.repartidor_idUsuarios == idRepartidorSesion}"/>
            <div class="reserva-card" data-direccion="${fn:escapeXml(pedido.direccion_entrega)}">
                <div class="pedido-card-top">
                    <div class="reserva-card-fecha">
                        <i class="ti ti-hash"></i> Pedido ${pedido.idPedido}
                    </div>
                    <span class="status ${estado == 3 ? 'activo' : 'pendiente'}">
                        <c:choose>
                            <c:when test="${estado == 1}">Recibido</c:when>
                            <c:when test="${estado == 2}">En preparación</c:when>
                            <c:when test="${estado == 3}">Listo para entregar</c:when>
                            <c:otherwise>Pendiente</c:otherwise>
                        </c:choose>
                    </span>
                </div>

                <div class="reserva-card-hora"><i class="ti ti-clock"></i> <fmt:formatDate value="${pedido.hora}" pattern="hh:mm a"/></div>
                <c:if test="${not empty pedido.hora_estimada}">
                    <div class="reserva-card-hora"><i class="ti ti-hourglass"></i> Estimado: <fmt:formatDate value="${pedido.hora_estimada}" pattern="hh:mm a"/></div>
                </c:if>

                <div class="reserva-card-personas">
                    <i class="ti ti-map-pin"></i> <c:out value="${pedido.direccion_entrega}"/>
                </div>

                <div class="badge-asignacion">
                    <c:choose>
                        <c:when test="${esMio}"><i class="ti ti-user-check"></i> <strong>Tú la llevas</strong></c:when>
                        <c:when test="${sinAsignar}"><i class="ti ti-circle-dashed"></i> Sin asignar todavía</c:when>
                        <c:otherwise><i class="ti ti-moped"></i> La lleva otro repartidor</c:otherwise>
                    </c:choose>
                </div>

                <c:if test="${esMio && not empty cliente}">
                    <div class="reserva-card-personas">
                        <i class="ti ti-user"></i> ${cliente.nombre} ${cliente.apellido}
                        &nbsp;·&nbsp;
                        <a href="tel:${cliente.telefono}" style="color:inherit;text-decoration:underline;">
                            <i class="ti ti-phone"></i> ${cliente.telefono}
                        </a>
                    </div>
                </c:if>

                <c:set var="detalles" value="${detallesPorPedido[pedido.idPedido]}"/>
                <c:if test="${not empty detalles}">
                    <ul class="pedido-detalle-lista">
                        <c:forEach var="d" items="${detalles}">
                            <li><span class="pedido-detalle-cant">${d.cantidad}x</span> ${nombresProducto[d.producto_idProducto]}</li>
                        </c:forEach>
                    </ul>
                </c:if>

                <div class="pedido-card-total">Total: $${pedido.total}</div>

                <div style="display:flex; gap:10px; margin-top:14px; flex-wrap:wrap;">
                    <c:if test="${sinAsignar && estado == 3}">
                        <form action="${ctx}/PanelRepartidor" method="post">
                            <input type="hidden" name="accion" value="tomar">
                            <input type="hidden" name="id" value="${pedido.idPedido}">
                            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                            <button type="submit" class="btn-hero-primary" style="border:none;cursor:pointer;font-family:inherit;">
                                <i class="ti ti-hand-stop"></i> Tomar este pedido
                            </button>
                        </form>
                    </c:if>

                    <c:if test="${esMio}">
                        <button type="button" class="btn-outline btn-compartir" data-pedido="${pedido.idPedido}"
                                onclick="toggleCompartir(${pedido.idPedido}, this)" style="cursor:pointer;font-family:inherit;">
                            <i class="ti ti-broadcast"></i> Compartir mi ubicación
                        </button>
                        <a href="#" class="btn-outline btn-como-llegar" target="_blank" rel="noopener">
                            <i class="ti ti-route"></i> Cómo llegar
                        </a>

                        <form action="${ctx}/PanelRepartidor" method="post">
                            <input type="hidden" name="accion" value="entregar">
                            <input type="hidden" name="id" value="${pedido.idPedido}">
                            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                            <c:choose>
                                <c:when test="${estado == 3}">
                                    <button type="submit" class="btn-hero-primary" style="border:none;cursor:pointer;font-family:inherit;">
                                        <i class="ti ti-check"></i> Marcar como entregado
                                    </button>
                                </c:when>
                                <c:otherwise>
                                    <button type="submit" class="btn-hero-primary" disabled title="Todavía no está listo en cocina" style="opacity:.5;cursor:not-allowed;border:none;font-family:inherit;">
                                        <i class="ti ti-check"></i> Marcar como entregado
                                    </button>
                                </c:otherwise>
                            </c:choose>
                        </form>
                        <form action="${ctx}/PanelRepartidor" method="post" onsubmit="return confirm('¿Marcar este pedido como no entregado?');">
                            <input type="hidden" name="accion" value="cancelar">
                            <input type="hidden" name="id" value="${pedido.idPedido}">
                            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                            <button type="submit" class="btn-outline" style="background:transparent;cursor:pointer;font-family:inherit;">
                                <i class="ti ti-x"></i> No se pudo entregar
                            </button>
                        </form>
                    </c:if>
                </div>
            </div>
        </c:forEach>
        <c:if test="${empty pendientes}">
            <div class="reserva-card-vacio">
                <i class="ti ti-moped"></i>
                <p>No hay pedidos a domicilio pendientes por entregar en este momento.</p>
            </div>
        </c:if>
    </div>
</section>

<section class="seccion">
    <div class="seccion-header reveal">
        <span class="label-seccion">Historial</span>
        <h2>Entregas recientes</h2>
    </div>

    <div class="reservas-usuario-grid reveal">
        <c:forEach var="pedido" items="${completados}">
            <c:set var="estado" value="${pedido.estado_pedido_idEstado_pedido}"/>
            <div class="reserva-card">
                <div class="pedido-card-top">
                    <div class="reserva-card-fecha">
                        <i class="ti ti-calendar-event"></i>
                        <fmt:formatDate value="${pedido.fecha}" pattern="dd 'de' MMMM, yyyy"/>
                    </div>
                    <span class="status ${estado == 4 ? 'activo' : 'cancelado'}">
                        ${estado == 4 ? 'Entregado' : 'Cancelado'}
                    </span>
                </div>
                <div class="reserva-card-personas"><i class="ti ti-map-pin"></i> <c:out value="${pedido.direccion_entrega}"/></div>
                <div class="pedido-card-total">Total: $${pedido.total}</div>
            </div>
        </c:forEach>
        <c:if test="${empty completados}">
            <div class="reserva-card-vacio">
                <i class="ti ti-history"></i>
                <p>Todavía no has completado entregas.</p>
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
const ctx = "${ctx}";
const hamburger = document.getElementById('hamburger');
const mobileMenu = document.getElementById('mobileMenu');
hamburger.addEventListener('click', () => { hamburger.classList.toggle('active'); mobileMenu.classList.toggle('open'); });
const reveals = document.querySelectorAll('.reveal');
const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => { if (entry.isIntersecting) { entry.target.classList.add('visible'); observer.unobserve(entry.target); } });
}, { threshold: 0.1 });
reveals.forEach(el => observer.observe(el));

// "Cómo llegar": deep link directo a Google Maps con la ruta hacia la
// dirección del cliente. No necesita API key — Maps lo abre con
// navegación turn-by-turn en el celular del repartidor.
document.querySelectorAll('.btn-como-llegar').forEach(a => {
    const card = a.closest('.reserva-card');
    const direccion = card ? card.dataset.direccion : '';
    // direccionParaMapa() completa en silencio con ", Bogotá, Colombia"
    // si hace falta (ver mapsConfig.js), para que Google ubique bien la
    // dirección del cliente aunque la haya escrito sin ciudad.
    a.href = 'https://www.google.com/maps/dir/?api=1&destination=' + encodeURIComponent(direccionParaMapa(direccion)) + '&travelmode=driving';
});

// Compartir ubicación en vivo: usa la geolocalización del navegador
// (funciona en el celular del repartidor) y manda la posición al
// servidor cada ~8 segundos mientras esté activado. El cliente la ve
// en su página de seguimiento del pedido.
const compartiendo = {};

function toggleCompartir(idPedido, btn) {
    if (compartiendo[idPedido]) {
        navigator.geolocation.clearWatch(compartiendo[idPedido]);
        delete compartiendo[idPedido];
        btn.innerHTML = '<i class="ti ti-broadcast"></i> Compartir mi ubicación';
        btn.classList.remove('compartiendo-activo');
        return;
    }
    if (!navigator.geolocation) {
        alert('Tu navegador no soporta geolocalización.');
        return;
    }
    btn.innerHTML = '<i class="ti ti-broadcast-off"></i> Dejar de compartir';
    btn.classList.add('compartiendo-activo');

    let ultimoEnvio = 0;
    const watchId = navigator.geolocation.watchPosition(function (pos) {
        const ahora = Date.now();
        if (ahora - ultimoEnvio < 8000) return; // no saturar el servidor
        ultimoEnvio = ahora;
        const params = new URLSearchParams();
        params.set('idPedido', idPedido);
        params.set('lat', pos.coords.latitude);
        params.set('lng', pos.coords.longitude);
        fetch(ctx + '/Ubicacion', { method: 'POST', body: params }).catch(() => {});
    }, function (err) {
        alert('No se pudo obtener tu ubicación: ' + err.message);
        btn.innerHTML = '<i class="ti ti-broadcast"></i> Compartir mi ubicación';
        btn.classList.remove('compartiendo-activo');
        delete compartiendo[idPedido];
    }, { enableHighAccuracy: true, maximumAge: 5000, timeout: 15000 });

    compartiendo[idPedido] = watchId;
}

// Auto-refresco: cada 8 segundos pregunta si hay pedidos nuevos "Listo"
// para tomar, o si alguno cambió (por ejemplo, otro repartidor se
// adelantó a tomar uno). Si nada cambió, no hace nada. Y si en ese
// momento estás compartiendo tu ubicación en vivo para una entrega, se
// salta la recarga por completo — recargar cortaría el GPS a mitad de
// camino, así que en ese caso prefiere quedarse "desactualizada" un
// rato en vez de interrumpir el rastreo.
(function () {
    let ultimaFirma = "${firmaEstado}";
    setInterval(async () => {
        if (Object.keys(compartiendo).length > 0) return;
        try {
            const res = await fetch(ctx + '/PanelRepartidor?check=estado', { cache: 'no-store' });
            const datos = await res.json();
            if (datos.firma !== ultimaFirma) {
                location.reload();
            }
        } catch (e) {
            // Sin conexión momentánea: se reintenta en el siguiente ciclo.
        }
    }, 8000);
})();
</script>
</body>
</html>
