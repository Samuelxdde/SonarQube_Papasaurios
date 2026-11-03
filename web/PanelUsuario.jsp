<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mi panel — Papasaurios</title>
    <link rel="icon" type="image/png" href="${ctx}/Vista/Imagenes/loguito.png">
    <link rel="stylesheet" href="${ctx}/Vista/Css/style.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@tabler/icons-webfont@2.44.0/tabler-icons.min.css" integrity="sha384-mY6asDzXlnHeimHHXw8cavPeArhEcAfnCibDFiaDPex3Fudo+edh/hnT7siZz8bg" crossorigin="anonymous">
</head>
<body>

<nav class="navbar scrolled" id="navbar">
    <div class="navbar-inner">
        <ul class="nav-links nav-left">
            <li><a href="${ctx}/">Inicio</a></li>
            <li><a href="${ctx}/Menu">Menú</a></li>
        </ul>
        <a href="${ctx}/PanelUsuario" class="nav-logo">
            <img src="${ctx}/Vista/Imagenes/loguito.png" alt="Logo">
            <span>Papasaurios</span>
        </a>
        <ul class="nav-links nav-right">
            <li><a href="${ctx}/Menu">Nuevo pedido</a></li>
            <li class="nav-user">Hola, <c:out value="${sessionScope.nombreUsuario}"/></li>
            <li><a href="${ctx}/CerrarSesion" class="nav-btn">Salir</a></li>
        </ul>
        <button class="hamburger" id="hamburger" aria-label="Menú"><span></span><span></span><span></span></button>
    </div>
    <div class="mobile-menu" id="mobileMenu">
        <a href="${ctx}/">Inicio</a>
        <a href="${ctx}/Menu">Menú</a>
        <a href="${ctx}/CerrarSesion">Cerrar Sesión</a>
    </div>
</nav>

<section class="seccion seccion-perfil" style="padding-top:140px;">
    <div class="seccion-header reveal">
        <span class="label-seccion">Tu espacio</span>
        <h2>Hola, <c:out value="${sessionScope.nombreUsuario}"/> 🦖</h2>
        <p>Aquí puedes ver el estado de tus pedidos en Papasaurios.</p>
        <a href="${ctx}/Menu" class="btn-hero-primary" style="display:inline-block;margin-top:22px;font-size:1.05rem;padding:14px 32px;box-shadow:0 10px 28px rgba(227,112,43,0.35);">
            <i class="ti ti-shopping-bag-plus"></i> Hacer un nuevo pedido
        </a>
    </div>

    <c:if test="${not empty confPedidoId}">
        <div class="overlay-exito activo" id="overlayPedidoConfirmado">
            <div class="modal-exito">
                <button type="button" class="modal-exito-cerrar" onclick="document.getElementById('overlayPedidoConfirmado').classList.remove('activo')" aria-label="Cerrar">✕</button>
                <div class="modal-exito-icono"><i class="ti ti-circle-check"></i></div>
                <h3>¡Pedido #${confPedidoId} confirmado!</h3>

                <div style="text-align:left;margin:14px 0 20px;display:flex;flex-direction:column;gap:8px;font-size:0.9rem;color:var(--texto-medio);">
                    <c:choose>
                        <c:when test="${confTipoEntrega == 'Domicilio'}">
                            <div><i class="ti ti-map-pin"></i> Entrega a domicilio: <strong>${confDireccion}</strong></div>
                            <div><i class="ti ti-hourglass"></i> Hora estimada de entrega: <strong><fmt:formatDate value="${confHoraEstimada}" pattern="hh:mm a"/></strong></div>
                        </c:when>
                        <c:otherwise>
                            <div><i class="ti ti-building-store"></i> Para recoger en tienda</div>
                            <div><i class="ti ti-hourglass"></i> Hora estimada para recoger: <strong><fmt:formatDate value="${confHoraEstimada}" pattern="hh:mm a"/></strong></div>
                        </c:otherwise>
                    </c:choose>
                    <div><i class="ti ti-coin"></i> Total: <strong>$${confTotal}</strong></div>
                </div>

                <a href="${ctx}/PedidoDetalle?id=${confPedidoId}" class="modal-exito-continuar">Ver el seguimiento de este pedido</a>
            </div>
        </div>
    </c:if>

    <div class="perfil-grid reveal">
        <div class="perfil-card">
            <div class="perfil-card-icon"><i class="ti ti-user"></i></div>
            <div>
                <div class="perfil-card-label">Correo</div>
                <div class="perfil-card-valor"><c:out value="${sessionScope.correoUsuario}"/></div>
            </div>
        </div>
        <div class="perfil-card">
            <div class="perfil-card-icon"><i class="ti ti-phone"></i></div>
            <div>
                <div class="perfil-card-label">Teléfono</div>
                <div class="perfil-card-valor"><c:out value="${sessionScope.telefonoUsuario}"/></div>
            </div>
        </div>
        <div class="perfil-card">
            <div class="perfil-card-icon"><i class="ti ti-shopping-cart"></i></div>
            <div>
                <div class="perfil-card-label">Pedidos totales</div>
                <div class="perfil-card-valor">${misPedidos.size()}</div>
            </div>
        </div>
    </div>
</section>

<section class="seccion seccion-dark">
    <div class="seccion-header reveal">
        <span class="label-seccion">Tu actividad</span>
        <h2>Mis pedidos</h2>
    </div>

    <c:if test="${not empty error}">
        <div class="alert alert-err" style="max-width:600px;margin:0 auto 24px;"><i class="ti ti-alert-circle"></i> ${error}</div>
    </c:if>

    <div class="reservas-usuario-grid reveal">
        <c:forEach var="pedido" items="${misPedidos}">
            <div class="reserva-card">
                <div class="pedido-card-top">
                    <div class="reserva-card-fecha">
                        <i class="ti ti-calendar-event"></i>
                        <fmt:formatDate value="${pedido.fecha}" pattern="dd 'de' MMMM, yyyy"/>
                    </div>
                    <c:set var="estado" value="${pedido.estado_pedido_idEstado_pedido}"/>
                    <span class="status ${estado == 4 ? 'activo' : (estado == 5 ? 'cancelado' : 'pendiente')}">
                        <c:choose>
                            <c:when test="${estado == 1}">Recibido</c:when>
                            <c:when test="${estado == 2}">En preparación</c:when>
                            <c:when test="${estado == 3}">Listo</c:when>
                            <c:when test="${estado == 4}">Entregado</c:when>
                            <c:when test="${estado == 5}">Cancelado</c:when>
                            <c:otherwise>Pendiente</c:otherwise>
                        </c:choose>
                    </span>
                </div>

                <div class="reserva-card-hora"><i class="ti ti-clock"></i> <fmt:formatDate value="${pedido.hora}" pattern="hh:mm a"/></div>
                <div class="reserva-card-personas">
                    <i class="ti ti-truck-delivery"></i> ${pedido.tipo_entrega}
                    <c:if test="${not empty pedido.direccion_entrega}"> — <c:out value="${pedido.direccion_entrega}"/></c:if>
                </div>
                <c:if test="${estado != 4 && estado != 5 && not empty pedido.hora_estimada}">
                    <div class="reserva-card-personas">
                        <i class="ti ti-hourglass"></i> Estimado: <fmt:formatDate value="${pedido.hora_estimada}" pattern="hh:mm a"/>
                    </div>
                </c:if>

                <c:set var="detalles" value="${detallesPorPedido[pedido.idPedido]}"/>
                <c:if test="${not empty detalles}">
                    <ul class="pedido-detalle-lista">
                        <c:forEach var="d" items="${detalles}">
                            <li><span class="pedido-detalle-cant">${d.cantidad}x</span> ${nombresProducto[d.producto_idProducto]} <span class="pedido-detalle-precio">$${d.subtotal}</span></li>
                        </c:forEach>
                    </ul>
                </c:if>

                <div class="pedido-card-total">Total: $${pedido.total}</div>
                <a href="${ctx}/PedidoDetalle?id=${pedido.idPedido}" class="btn-outline" style="align-self:flex-start;margin-top:6px;">
                    <i class="ti ti-map-2"></i> Ver seguimiento
                </a>
            </div>
        </c:forEach>
        <c:if test="${empty misPedidos}">
            <div class="reserva-card-vacio">
                <i class="ti ti-bone"></i>
                <p>Todavía no tienes pedidos. ¡Ve al menú y pide tu primera Papa Saurios!</p>
                <a href="${ctx}/Menu" class="btn-hero-primary">Ver el menú</a>
            </div>
        </c:if>
    </div>
</section>

<a href="${ctx}/Carrito" class="carrito-flotante">
    <i class="ti ti-shopping-cart"></i>
    <c:if test="${not empty sessionScope.carrito}">
        <span class="carrito-flotante-badge">${sessionScope.carrito.size()}</span>
    </c:if>
</a>

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

// Auto-refresco: cada 8 segundos pregunta si el estado de tus pedidos
// cambió (por ejemplo, cocina lo marcó "Listo" o el repartidor lo
// marcó "Entregado"), para que veas el avance sin tener que recargar
// tú mismo la página.
(function () {
    let ultimaFirma = "${firmaEstado}";
    setInterval(async () => {
        try {
            const res = await fetch('${ctx}/PanelUsuario?check=estado', { cache: 'no-store' });
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
