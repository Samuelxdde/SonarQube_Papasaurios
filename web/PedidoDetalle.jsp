<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="estado" value="${pedido.estado_pedido_idEstado_pedido}"/>
<c:set var="esDomicilio" value="${pedido.tipo_entrega == 'Domicilio'}"/>
<c:set var="enRuta" value="${esDomicilio && estado == 3 && not empty repartidor}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Pedido #${pedido.idPedido} — Papasaurios</title>
    <link rel="icon" type="image/png" href="${ctx}/Vista/Imagenes/loguito.png">
    <link rel="stylesheet" href="${ctx}/Vista/Css/style.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@tabler/icons-webfont@2.44.0/tabler-icons.min.css" integrity="sha384-mY6asDzXlnHeimHHXw8cavPeArhEcAfnCibDFiaDPex3Fudo+edh/hnT7siZz8bg" crossorigin="anonymous">
    <script src="${ctx}/Vista/JavaScript/mapsConfig.js"></script>
    <style>
        .tracker { max-width: 640px; margin: 0 auto; padding: 0 24px; }
        .tracker-pasos { display:flex; justify-content:space-between; align-items:flex-start; margin: 36px 0 8px; position:relative; }
        .tracker-pasos::before {
            content:''; position:absolute; top:17px; left:6%; right:6%; height:3px;
            background: rgba(247,236,216,0.2); z-index:0;
        }
        .tracker-paso { position:relative; z-index:1; display:flex; flex-direction:column; align-items:center; flex:1; text-align:center; }
        .tracker-bola {
            width:36px; height:36px; border-radius:50%; display:flex; align-items:center; justify-content:center;
            background: rgba(247,236,216,0.15); border:2px solid rgba(247,236,216,0.3); color: var(--texto-claro);
            font-size:1rem; margin-bottom:8px; transition: all .3s;
        }
        .tracker-paso.hecho .tracker-bola { background: var(--dorado); border-color: var(--dorado); color: var(--morado); }
        .tracker-paso.actual .tracker-bola { background: var(--dorado2); border-color: var(--dorado2); color: var(--morado); box-shadow: 0 0 0 5px rgba(240,140,63,0.25); }
        .tracker-paso span { font-size: 0.78rem; color: var(--texto-claro); opacity:.85; max-width:90px; }
        .tracker-cancelado { text-align:center; padding: 20px; }
        .info-pedido-card {
            background: rgba(255,255,255,0.06); border:1px solid rgba(201,145,58,0.25); border-radius:16px;
            padding:24px; margin: 28px auto 0; color: var(--texto-claro);
        }
        .info-pedido-fila { display:flex; justify-content:space-between; gap:16px; padding:10px 0; border-bottom:1px solid rgba(247,236,216,0.12); }
        .info-pedido-fila:last-child { border-bottom:none; }
        .info-pedido-fila .label { opacity:.75; }
        .info-pedido-fila .valor { font-weight:600; text-align:right; }
        .mapa-card {
            background: rgba(255,255,255,0.06); border:1px solid rgba(201,145,58,0.25); border-radius:16px;
            padding:20px; margin: 28px auto 0; color: var(--texto-claro); max-width:640px;
        }
        .mapa-repartidor-header { display:flex; align-items:center; gap:12px; margin-bottom:14px; }
        .mapa-repartidor-icon {
            width:44px; height:44px; border-radius:50%; background: var(--dorado); color: var(--morado);
            display:flex; align-items:center; justify-content:center; font-size:1.3rem; flex-shrink:0;
        }
        #mapaSeguimiento { width:100%; height:300px; border-radius:12px; background: rgba(0,0,0,0.15); }
        .mapa-actualizado { font-size:0.78rem; opacity:.7; margin-top:8px; }
        .mapa-fallback { text-align:center; padding: 24px 10px; }
    </style>
    <script src="${ctx}/Vista/JavaScript/navActivo.js"></script>
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
            <li><a href="${ctx}/PanelUsuario">Mi panel</a></li>
            <li><a href="${ctx}/CerrarSesion" class="nav-btn">Salir</a></li>
        </ul>
        <button class="hamburger" id="hamburger" aria-label="Menú"><span></span><span></span><span></span></button>
    </div>
    <div class="mobile-menu" id="mobileMenu">
        <a href="${ctx}/">Inicio</a>
        <a href="${ctx}/Menu">Menú</a>
        <a href="${ctx}/PanelUsuario">Mi panel</a>
        <a href="${ctx}/CerrarSesion">Cerrar Sesión</a>
    </div>
</nav>

<section class="seccion seccion-dark" style="padding-top:140px;">
    <div class="seccion-header reveal">
        <span class="label-seccion">Seguimiento</span>
        <h2>Pedido #${pedido.idPedido}</h2>
        <p>
            <c:choose>
                <c:when test="${esDomicilio}">A domicilio</c:when>
                <c:otherwise>Para recoger en tienda</c:otherwise>
            </c:choose>
        </p>
    </div>

    <div class="tracker reveal">
        <c:choose>
            <c:when test="${estado == 5}">
                <div class="tracker-cancelado">
                    <span class="status cancelado" style="font-size:1rem;padding:10px 20px;">
                        <i class="ti ti-x"></i> Este pedido fue cancelado
                    </span>
                </div>
            </c:when>
            <c:otherwise>
                <div class="tracker-pasos">
                    <div class="tracker-paso ${estado >= 1 ? 'hecho' : ''} ${estado == 1 ? 'actual' : ''}">
                        <div class="tracker-bola"><i class="ti ti-receipt"></i></div>
                        <span>Recibido</span>
                    </div>
                    <div class="tracker-paso ${estado >= 2 ? 'hecho' : ''} ${estado == 2 ? 'actual' : ''}">
                        <div class="tracker-bola"><i class="ti ti-tools-kitchen-2"></i></div>
                        <span>En preparación</span>
                    </div>
                    <div class="tracker-paso ${estado >= 3 ? 'hecho' : ''} ${estado == 3 ? 'actual' : ''}">
                        <div class="tracker-bola"><i class="ti ti-package"></i></div>
                        <span><c:if test="${esDomicilio}">Listo, en camino</c:if><c:if test="${!esDomicilio}">Listo para recoger</c:if></span>
                    </div>
                    <div class="tracker-paso ${estado >= 4 ? 'hecho' : ''} ${estado == 4 ? 'actual' : ''}">
                        <div class="tracker-bola"><i class="ti ti-check"></i></div>
                        <span><c:if test="${esDomicilio}">Entregado</c:if><c:if test="${!esDomicilio}">Recogido</c:if></span>
                    </div>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

    <c:if test="${enRuta}">
        <div class="mapa-card reveal">
            <div class="mapa-repartidor-header">
                <div class="mapa-repartidor-icon"><i class="ti ti-moped"></i></div>
                <div>
                    <div style="font-weight:700;">${repartidor.nombre} ${repartidor.apellido} va en camino</div>
                    <a href="tel:${repartidor.telefono}" style="color:inherit;text-decoration:underline;font-size:0.85rem;">
                        <i class="ti ti-phone"></i> ${repartidor.telefono}
                    </a>
                </div>
            </div>

            <p id="mapaTextoRuta" style="font-size:0.85rem; opacity:.85; margin-bottom:10px;">Cargando la ruta...</p>

            <div id="mapaSeguimiento"></div>
            <div id="mapaFallback" class="mapa-fallback" style="display:none;">
                <i class="ti ti-map-2" style="font-size:1.8rem;"></i>
                <p style="margin:10px 0;" id="mapaFallbackTexto">Sigue la ruta de tu pedido en Google Maps:</p>
                <a href="#" id="linkVerEnMaps" target="_blank" rel="noopener" class="btn-hero-primary">
                    <i class="ti ti-route"></i> Ver ruta en Google Maps
                </a>
            </div>

            <!-- Se muestra solo si el mapa embebido ya cargó (hay marcador
                 del repartidor) pero Directions no pudo trazar la línea de
                 la ruta (dirección que no se pudo ubicar bien, o la
                 Directions API no respondió "OK"). Antes esto fallaba en
                 silencio: el cliente veía el punto del repartidor solo, sin
                 ruta y sin ningún aviso de qué pasó ni una alternativa. -->
            <div id="mapaRutaFallback" style="display:none; margin-top:10px; padding:10px 12px; background:rgba(255,255,255,0.06); border:1px solid rgba(201,145,58,0.25); border-radius:10px; font-size:0.82rem;">
                <i class="ti ti-alert-triangle" style="color:var(--dorado2);"></i>
                No pudimos trazar la línea de la ruta en el mapa, pero puedes verla en Google Maps:
                <a href="#" id="linkVerRutaFallback" target="_blank" rel="noopener" style="color:var(--dorado2); font-weight:600; text-decoration:underline;">abrir ruta</a>
            </div>

            <div class="mapa-actualizado" id="mapaActualizado"></div>
        </div>
    </c:if>

    <div class="info-pedido-card reveal">
        <div class="info-pedido-fila">
            <span class="label"><i class="ti ti-calendar-event"></i> Fecha</span>
            <span class="valor"><fmt:formatDate value="${pedido.fecha}" pattern="dd 'de' MMMM, yyyy"/></span>
        </div>
        <div class="info-pedido-fila">
            <span class="label"><i class="ti ti-clock"></i> Pedido hecho a las</span>
            <span class="valor"><fmt:formatDate value="${pedido.hora}" pattern="hh:mm a"/></span>
        </div>
        <c:if test="${estado != 4 && estado != 5}">
            <div class="info-pedido-fila">
                <span class="label"><i class="ti ti-hourglass"></i>
                    <c:choose>
                        <c:when test="${esDomicilio}">Hora estimada de entrega</c:when>
                        <c:otherwise>Hora estimada para recoger</c:otherwise>
                    </c:choose>
                </span>
                <span class="valor"><fmt:formatDate value="${pedido.hora_estimada}" pattern="hh:mm a"/></span>
            </div>
        </c:if>
        <c:choose>
            <c:when test="${esDomicilio}">
                <div class="info-pedido-fila">
                    <span class="label"><i class="ti ti-map-pin"></i> Dirección de entrega</span>
                    <span class="valor"><c:out value="${pedido.direccion_entrega}"/></span>
                </div>
            </c:when>
            <c:otherwise>
                <div class="info-pedido-fila">
                    <span class="label"><i class="ti ti-building-store"></i> Recoger en</span>
                    <span class="valor">Papasaurios — DG 34 #13A-40, Soacha</span>
                </div>
            </c:otherwise>
        </c:choose>
        <div class="info-pedido-fila">
            <span class="label"><i class="ti ti-list"></i> Productos</span>
            <span class="valor" style="text-align:right;">
                <c:forEach var="d" items="${detalles}">
                    <div>${d.cantidad}x ${nombresProducto[d.producto_idProducto]} — $${d.subtotal}</div>
                </c:forEach>
            </span>
        </div>
        <div class="info-pedido-fila">
            <span class="label"><i class="ti ti-coin"></i> Total</span>
            <span class="valor" style="font-size:1.15rem;">$${pedido.total}</span>
        </div>
    </div>

    <div style="text-align:center; margin: 32px 0 10px;">
        <a href="${ctx}/PanelUsuario" class="btn-outline"><i class="ti ti-arrow-left"></i> Volver a mi panel</a>
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

<c:if test="${enRuta}">
// --- Seguimiento en vivo del repartidor ------------------------------
const ctx = "${ctx}";
const idPedidoSeguimiento = ${pedido.idPedido};
// Dirección tal como la escribió el cliente en el checkout. Se usa tal
// cual para MOSTRARLA en la tarjeta de info del pedido, pero para
// pedírsela a Directions/Maps se completa con ciudad y país (ver
// direccionDestino más abajo): una dirección suelta como "Cra 12 #34-56,
// Barrio Centro" sin ciudad es la causa más común de que Google no logre
// ubicarla bien y la ruta no se pueda trazar, aunque el marcador del
// repartidor sí se vea (ese no depende de geocodificar nada).
const direccionEntregaCruda = "${fn:escapeXml(pedido.direccion_entrega)}";
// direccionParaMapa() completa en silencio con ", Bogotá, Colombia" si
// hace falta (ver mapsConfig.js) — el cliente nunca ve ese agregado, se
// usa solo para pedirle la ruta a Directions.
const direccionDestino = direccionParaMapa(direccionEntregaCruda);
// Dirección del local (Papasaurios Soacha): es el "punto de partida" por
// defecto — se usa como origen de la ruta "planeada" mientras el
// repartidor todavía no ha activado su ubicación en vivo, para que el
// cliente vea desde ya por dónde le va a llegar el pedido.
// Dirección del local, solo para mostrarla en texto si hiciera falta.
// Como ORIGEN de la ruta se usan las coordenadas exactas (UBICACION_TIENDA),
// no este texto: pedirle a Google que geocodifique "DG 34 #13A-40,
// Soacha, Colombia" como texto libre a veces la resuelve al lugar
// equivocado más parecido (p. ej. "Avenida Terreros" o "Campestre etapa
// 6"), porque esa dirección no está bien indexada en su buscador. Las
// coordenadas no dependen de que Google adivine nada.
const direccionTienda = DIRECCION_TIENDA;
const origenTienda = UBICACION_TIENDA.lat + ',' + UBICACION_TIENDA.lng;

let map, marcadorRepartidor, marcadorDestino, directionsRenderer, directionsService;
let mapaListo = false;
let mapaCentrado = false;

function segundosDesde(fechaTexto) {
    const diff = (Date.now() - new Date(fechaTexto.replace(' ', 'T')).getTime()) / 1000;
    return Math.max(0, Math.round(diff));
}

function actualizarTexto(fechaTexto) {
    const el = document.getElementById('mapaActualizado');
    if (el) el.textContent = 'Última actualización: hace ' + segundosDesde(fechaTexto) + ' s';
}

function inicializarMapaConKey() {
    cargarGoogleMaps(function () {
        map = new google.maps.Map(document.getElementById('mapaSeguimiento'), {
            zoom: 13,
            center: UBICACION_TIENDA, // Papasaurios Soacha: punto de partida por defecto hasta tener la 1ra ruta
            disableDefaultUI: true,
            zoomControl: true
        });
        directionsService = new google.maps.DirectionsService();
        directionsRenderer = new google.maps.DirectionsRenderer({ suppressMarkers: true, map: map });
        mapaListo = true;
    });
}

// Anima el marcador del repartidor deslizándose suavemente desde su
// posición actual hasta la nueva, en vez de saltar de golpe cada vez
// que llega una actualización GPS (~cada 8s). Es lo que hace que el
// recorrido se sienta continuo, como en Uber, en lugar de a "teletransportes".
let animacionMarcadorId = null;
function moverMarcadorSuave(destinoLat, destinoLng, duracionMs) {
    if (!marcadorRepartidor) return;
    const posActual = marcadorRepartidor.getPosition();
    const origenLat = posActual.lat();
    const origenLng = posActual.lng();

    if (animacionMarcadorId) cancelAnimationFrame(animacionMarcadorId);
    const inicio = performance.now();

    function frame(ahora) {
        const t = Math.min((ahora - inicio) / duracionMs, 1);
        // Suavizado ease-in-out: arranca y frena, no se mueve a velocidad
        // mecánica constante — se ve más natural, como un vehículo real.
        const s = t < 0.5 ? 2 * t * t : 1 - Math.pow(-2 * t + 2, 2) / 2;
        marcadorRepartidor.setPosition({
            lat: origenLat + (destinoLat - origenLat) * s,
            lng: origenLng + (destinoLng - origenLng) * s
        });
        if (t < 1) {
            animacionMarcadorId = requestAnimationFrame(frame);
        }
    }
    animacionMarcadorId = requestAnimationFrame(frame);
}

// Dibuja/actualiza la ruta hacia la dirección del cliente. "origen" puede
// ser una posición GPS real ({lat,lng}) o, mientras no haya una, la
// dirección de la tienda — Directions API acepta ambas formas.
function actualizarRuta(origen) {
    directionsService.route({
        origin: origen,
        destination: direccionDestino,
        travelMode: google.maps.TravelMode.DRIVING
    }, function (result, status) {
        if (status === 'OK') {
            directionsRenderer.setDirections(result);
            ocultarAvisoRutaFallback();
            if (!marcadorDestino) {
                const destino = result.routes[0].legs[0].end_location;
                marcadorDestino = new google.maps.Marker({ position: destino, map: map, title: 'Tu dirección' });
            }
            if (!mapaCentrado) {
                map.fitBounds(result.routes[0].bounds);
                mapaCentrado = true;
            }
        } else {
            // No tapamos el error: lo dejamos en consola para poder
            // diagnosticarlo (motivos típicos: la Directions API no está
            // habilitada en Google Cloud para esta key, o la dirección de
            // entrega no se puede ubicar en el mapa tal como está escrita).
            console.warn('No se pudo calcular la ruta. Estado de Directions API:', status, '- destino:', direccionDestino);

            // Antes esto se quedaba así, en silencio: el cliente veía el
            // marcador del repartidor pero ninguna línea de ruta, sin
            // ningún aviso ni alternativa. Ahora, sin ocultar el mapa ni
            // el marcador (eso sigue funcionando), se muestra un aviso con
            // un link directo a Google Maps para que igual pueda ver la
            // ruta, aunque no se pueda dibujar dentro del mapa embebido.
            const origenTexto = (origen && typeof origen === 'object')
                ? (origen.lat + ',' + origen.lng)
                : origen;
            mostrarAvisoRutaFallback(origenTexto);
        }
    });
}

function mostrarAvisoRutaFallback(origenTexto) {
    const aviso = document.getElementById('mapaRutaFallback');
    const link = document.getElementById('linkVerRutaFallback');
    if (link) {
        link.href = 'https://www.google.com/maps/dir/?api=1&origin=' + encodeURIComponent(origenTexto)
            + '&destination=' + encodeURIComponent(direccionDestino) + '&travelmode=driving';
    }
    if (aviso) aviso.style.display = 'block';
}

function ocultarAvisoRutaFallback() {
    const aviso = document.getElementById('mapaRutaFallback');
    if (aviso) aviso.style.display = 'none';
}

// Calcula el rumbo (en grados) entre dos puntos, para rotar el ícono
// del repartidor hacia donde se está moviendo — el mismo efecto que
// el "autito" de Uber girando según hacia dónde avanza.
function calcularRumbo(lat1, lng1, lat2, lng2) {
    const rad = Math.PI / 180;
    const y = Math.sin((lng2 - lng1) * rad) * Math.cos(lat2 * rad);
    const x = Math.cos(lat1 * rad) * Math.sin(lat2 * rad) - Math.sin(lat1 * rad) * Math.cos(lat2 * rad) * Math.cos((lng2 - lng1) * rad);
    return (Math.atan2(y, x) * 180 / Math.PI + 360) % 360;
}

function iconoRepartidor(rotacion) {
    return {
        path: google.maps.SymbolPath.FORWARD_CLOSED_ARROW,
        scale: 5,
        rotation: rotacion || 0,
        fillColor: '#f08c3f', fillOpacity: 1,
        strokeColor: '#1c2e23', strokeWeight: 2
    };
}

function actualizarPosicionRepartidor(lat, lng) {
    const posicion = { lat: lat, lng: lng };
    if (!marcadorRepartidor) {
        // Primera vez que sabemos dónde está: aparece directo, sin animar.
        marcadorRepartidor = new google.maps.Marker({
            position: posicion, map: map, title: 'Repartidor',
            icon: iconoRepartidor(0)
        });
        // Antes el mapa se quedaba centrado en el punto por defecto
        // (Colombia entera) hasta que Directions lograra calcular la ruta
        // — si eso fallaba (p. ej. sin facturación habilitada en Google
        // Cloud), el cliente veía el marcador perdido en un mapa alejado.
        // Ahora, apenas se sabe dónde está el repartidor, el mapa se
        // centra ahí de una vez, sin depender de que la ruta se calcule.
        map.setCenter(posicion);
        map.setZoom(15);
    } else {
        // Ya lo veníamos siguiendo: gira el ícono hacia el nuevo punto y se
        // desliza hasta ahí en vez de saltar. La duración es casi la misma
        // que el intervalo entre actualizaciones (8s) para que el
        // movimiento se vea continuo, como el mapa de Uber.
        const posActual = marcadorRepartidor.getPosition();
        const rumbo = calcularRumbo(posActual.lat(), posActual.lng(), lat, lng);
        marcadorRepartidor.setIcon(iconoRepartidor(rumbo));
        moverMarcadorSuave(lat, lng, 7000);
    }
    actualizarRuta(posicion);
}

function mostrarFallback(origenTexto, conUbicacionViva) {
    document.getElementById('mapaSeguimiento').style.display = 'none';
    // El texto "Cargando la ruta..." es solo el estado inicial: si vamos a
    // mostrar el bloque de respaldo (sin mapa embebido), lo ocultamos para
    // que no se quede pegado ahí para siempre pareciendo que nunca termina
    // de cargar — la explicación ya la da el bloque de respaldo.
    const textoRuta = document.getElementById('mapaTextoRuta');
    if (textoRuta) textoRuta.style.display = 'none';
    const fallback = document.getElementById('mapaFallback');
    fallback.style.display = 'block';
    document.getElementById('mapaFallbackTexto').textContent = conUbicacionViva
        ? 'Sigue la ruta en vivo de tu repartidor en Google Maps:'
        : 'Esta es la ruta planeada desde la tienda hasta tu dirección (se actualizará con la posición real en cuanto tu repartidor la active):';
    document.getElementById('linkVerEnMaps').href =
        'https://www.google.com/maps/dir/?api=1&origin=' + encodeURIComponent(origenTexto)
        + '&destination=' + encodeURIComponent(direccionDestino) + '&travelmode=driving';
}

function consultarUbicacion() {
    fetch(ctx + '/Ubicacion?id=' + idPedidoSeguimiento)
        .then(r => r.json())
        .then(data => {
            const hayUbicacionViva = !!data.disponible;
            const textoRuta = document.getElementById('mapaTextoRuta');

            if (mapsApiKeyConfigurada()) {
                document.getElementById('mapaFallback').style.display = 'none';
                document.getElementById('mapaSeguimiento').style.display = 'block';
                if (!mapaListo) { inicializarMapaConKey(); }
                // Da un instante a que el script de Maps termine de cargar
                // la primera vez antes de intentar dibujar la ruta.
                const intentar = setInterval(function () {
                    if (mapaListo) {
                        clearInterval(intentar);
                        if (hayUbicacionViva) {
                            textoRuta.textContent = 'Ruta en vivo hasta tu dirección.';
                            actualizarTexto(data.actualizado);
                            actualizarPosicionRepartidor(data.lat, data.lng);
                        } else {
                            textoRuta.textContent = 'Ruta planeada desde la tienda — se actualizará con la posición real en cuanto tu repartidor la active.';
                            actualizarRuta(UBICACION_TIENDA);
                        }
                    }
                }, 200);
            } else {
                const origenTexto = hayUbicacionViva ? (data.lat + ',' + data.lng) : origenTienda;
                mostrarFallback(origenTexto, hayUbicacionViva);
                if (hayUbicacionViva) { actualizarTexto(data.actualizado); }
            }
        })
        .catch(() => {});
}

consultarUbicacion();
setInterval(consultarUbicacion, 8000);
</c:if>
</script>
</body>
</html>
