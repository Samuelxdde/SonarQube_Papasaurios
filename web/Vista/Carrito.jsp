<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mi carrito - Papasaurios</title>
    <link rel="stylesheet" href="${ctx}/Vista/Css/style.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@tabler/icons-webfont@2.44.0/tabler-icons.min.css" integrity="sha384-mY6asDzXlnHeimHHXw8cavPeArhEcAfnCibDFiaDPex3Fudo+edh/hnT7siZz8bg" crossorigin="anonymous">
    <script src="${ctx}/Vista/JavaScript/validarReg.js"></script>
    <script src="${ctx}/Vista/JavaScript/navActivo.js"></script>
</head>
<body>

<nav class="navbar scrolled" id="navbar">
    <div class="navbar-inner">
        <ul class="nav-links nav-left">
            <li><a href="${ctx}/">Inicio</a></li>
            <li><a href="${ctx}/Menu">Menú</a></li>
        </ul>
        <a href="${ctx}/" class="nav-logo">
            <img src="${ctx}/Vista/Imagenes/loguito.png" alt="Logo">
            <span>Papasaurios</span>
        </a>
        <ul class="nav-links nav-right">
            <c:choose>
                <c:when test="${not empty sessionScope.nombreUsuario}">
                    <li><a href="${ctx}/PanelUsuario">Mis pedidos</a></li>
                </c:when>
                <c:otherwise>
                    <li><a href="${ctx}/Iniciar" class="nav-btn">Ingresar</a></li>
                </c:otherwise>
            </c:choose>
        </ul>
        <button class="hamburger" id="hamburger" aria-label="Menú"><span></span><span></span><span></span></button>
    </div>
    <div class="mobile-menu" id="mobileMenu">
        <a href="${ctx}/">Inicio</a>
        <a href="${ctx}/Menu">Menú</a>
        <c:if test="${not empty sessionScope.nombreUsuario}"><a href="${ctx}/PanelUsuario">Mis pedidos</a></c:if>
        <c:if test="${empty sessionScope.nombreUsuario}"><a href="${ctx}/Iniciar">Ingresar</a></c:if>
    </div>
</nav>

<section class="seccion" style="padding-top:140px; min-height:60vh;">
    <div class="seccion-header reveal">
        <span class="label-seccion">Tu pedido</span>
        <h2>Mi carrito</h2>
    </div>

    <c:if test="${not empty sessionScope.errorCarrito}">
        <div class="alert alert-err" style="max-width:600px;margin:0 auto 24px;"><i class="ti ti-alert-circle"></i> ${sessionScope.errorCarrito}</div>
        <c:remove var="errorCarrito" scope="session"/>
    </c:if>
    <c:if test="${not empty error}">
        <div class="alert alert-err" style="max-width:600px;margin:0 auto 24px;"><i class="ti ti-alert-circle"></i> ${error}</div>
    </c:if>

    <div style="max-width:700px;margin:0 auto;padding:0 24px;">
        <c:set var="total" value="${0}"/>
        <c:forEach var="item" items="${sessionScope.carrito}">
            <c:set var="total" value="${total + (item.value.precioUnitario * item.value.cantidad)}"/>
            <div class="carrito-item" data-clave="${item.key}" data-precio-unitario="${item.value.precioUnitario}">
                <div class="carrito-item-info">
                    <h4>${item.value.nombreProducto}</h4>
                    <c:if test="${not empty item.value.nombreVariante}">
                        <span>${item.value.nombreVariante}</span>
                    </c:if>
                    <div class="producto-stepper">
                        <button type="button" class="stepper-btn" onclick="cambiarCantidad('${item.key}',-1)" aria-label="Quitar una unidad">−</button>
                        <input type="number" class="producto-cantidad carrito-cantidad-input" value="${item.value.cantidad}" min="1" max="20" readonly aria-label="Cantidad">
                        <button type="button" class="stepper-btn" onclick="cambiarCantidad('${item.key}',1)" aria-label="Agregar una unidad">+</button>
                    </div>
                </div>
                <div class="carrito-item-precio" id="precio-${item.key}">$${item.value.precioUnitario * item.value.cantidad}</div>
                <button type="button" class="carrito-item-del" title="Quitar del carrito" onclick="quitarItem('${item.key}')">
                    <i class="ti ti-trash"></i>
                </button>
            </div>
        </c:forEach>

        <div id="carritoVacioMsg" style="${empty sessionScope.carrito ? '' : 'display:none;'}">
            <div class="reserva-card-vacio">
                <i class="ti ti-shopping-cart-off"></i>
                <p>Tu carrito está vacío. ¡Ve al menú y arma tu pedido!</p>
                <a href="${ctx}/Menu" class="btn-hero-primary">Ver el menú</a>
            </div>
        </div>

        <div id="carritoResumenWrap" style="${empty sessionScope.carrito ? 'display:none;' : ''}">
            <div class="carrito-resumen reveal">
                <div class="carrito-total">Total: $<span id="carritoTotalTexto">${total}</span></div>

                <c:choose>
                    <c:when test="${not empty sessionScope.nombreUsuario}">
                        <form action="${ctx}/Pedido" method="post" id="formCheckout">
                            <div class="form-field" style="margin-bottom:14px;">
                                <span class="form-field-label" id="lblTipoEntrega">Tipo de entrega</span>
                                <div class="opciones-entrega" role="radiogroup" aria-labelledby="lblTipoEntrega">
                                    <label class="opcion-entrega">
                                        <input type="radio" name="tipo_entrega" value="Recoger en tienda" checked onchange="toggleDireccion()">
                                        <span class="opcion-entrega-circulo"></span>
                                        <span class="opcion-entrega-texto">
                                            <i class="ti ti-building-store"></i> Recoger en tienda
                                        </span>
                                    </label>
                                    <label class="opcion-entrega">
                                        <input type="radio" name="tipo_entrega" value="Domicilio" onchange="toggleDireccion()">
                                        <span class="opcion-entrega-circulo"></span>
                                        <span class="opcion-entrega-texto">
                                            <i class="ti ti-moped"></i> Domicilio
                                        </span>
                                    </label>
                                </div>
                            </div>
                            <div class="form-field" id="campoDireccion" style="display:none; margin-bottom:14px;">
                                <label for="direccionEntrega">Dirección de entrega</label>
                                <input type="text" name="direccion_entrega" id="direccionEntrega" placeholder="Dirección completa, barrio, indicaciones">
                            </div>
                            <button type="submit" class="btn-hero-primary" style="width:100%;">Confirmar pedido</button>
                        </form>
                    </c:when>
                    <c:otherwise>
                        <button type="button" class="btn-hero-primary" style="width:100%;" onclick="mostrarModalLogin()">Confirmar pedido</button>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</section>

<!-- ===== MODAL "DEBES INICIAR SESIÓN" ===== -->
<div class="overlay-login" id="overlayLogin">
    <div class="modal-login">
        <button type="button" class="modal-login-cerrar" onclick="ocultarModalLogin()" aria-label="Cerrar">✕</button>
        <div class="modal-login-icono"><i class="ti ti-lock"></i></div>
        <h3>Inicia sesión</h3>
        <p data-texto-login>Necesitas una cuenta para confirmar tu pedido. Inicia sesión o crea una cuenta en segundos.</p>
        <div class="modal-login-botones">
            <a href="${ctx}/Iniciar" class="btn-login">Iniciar sesión</a>
            <a href="${ctx}/CargarRegistro" class="btn-login btn-secundario">Crear cuenta</a>
        </div>
        <button type="button" class="modal-login-cancelar" onclick="ocultarModalLogin()">Ahora no</button>
    </div>
</div>

    <!-- ===== MODAL "REVISA ESTE CAMPO" (validación de checkout) ===== -->
    <div class="overlay-alerta" id="overlayAlerta">
        <div class="modal-alerta">
            <button type="button" class="modal-alerta-cerrar" onclick="ocultarModalAlerta()" aria-label="Cerrar">&times;</button>
            <div class="modal-alerta-icono"><i class="ti ti-alert-triangle"></i></div>
            <h3>Revisa este campo</h3>
            <p id="mensajeAlerta"></p>
            <button type="button" class="modal-alerta-aceptar" onclick="ocultarModalAlerta()">Entendido</button>
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
const hamburger = document.getElementById('hamburger');
const mobileMenu = document.getElementById('mobileMenu');
hamburger.addEventListener('click', () => { hamburger.classList.toggle('active'); mobileMenu.classList.toggle('open'); });

// Esto es lo que le faltaba a esta página: sin este observador, todo lo
// que tiene la clase "reveal" (el título y el bloque de "Confirmar
// pedido") se queda con opacity:0 para siempre — sigue estando ahí y
// ocupando su espacio, pero invisible. Por eso después de agregar algo
// al carrito no se veía ningún botón para continuar.
const reveals = document.querySelectorAll('.reveal');
const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => { if (entry.isIntersecting) { entry.target.classList.add('visible'); observer.unobserve(entry.target); } });
}, { threshold: 0.1 });
reveals.forEach(el => observer.observe(el));

function toggleDireccion() {
    var seleccionado = document.querySelector('input[name="tipo_entrega"]:checked');
    var tipo = seleccionado ? seleccionado.value : '';
    var campo = document.getElementById('campoDireccion');
    var input = document.getElementById('direccionEntrega');
    var esDomicilio = (tipo === 'Domicilio');
    campo.style.display = esDomicilio ? 'block' : 'none';
    input.required = esDomicilio;
    if (!esDomicilio) { input.value = ''; }
}

// Mismo criterio que Controlador.DireccionValidator en el servidor:
// ciudades grandes de Colombia que NO son Soacha ni Bogotá. Comparación
// sin tildes para que "Medellin" y "Medellín" se traten igual.
var CIUDADES_FUERA_DE_COBERTURA = [
    'medellin', 'cali', 'barranquilla', 'cartagena', 'cucuta',
    'bucaramanga', 'pereira', 'manizales', 'ibague', 'santa marta',
    'villavicencio', 'pasto', 'monteria', 'neiva', 'armenia',
    'popayan', 'valledupar', 'sincelejo', 'tunja', 'florencia',
    'riohacha', 'quibdo', 'yopal', 'mocoa', 'leticia', 'san andres'
];
function quitarTildes(texto) {
    return texto.normalize('NFD').replace(/[\u0300-\u036f]/g, '');
}
function direccionFueraDeCobertura(direccion) {
    var sinTildes = quitarTildes(direccion).toLowerCase();
    return CIUDADES_FUERA_DE_COBERTURA.some(function (ciudad) {
        return new RegExp('\\b' + ciudad + '\\b').test(sinTildes);
    });
}

document.getElementById('formCheckout') && document.getElementById('formCheckout').addEventListener('submit', function(e) {
    var seleccionado = document.querySelector('input[name="tipo_entrega"]:checked');
    var tipo = seleccionado ? seleccionado.value : '';
    var direccion = document.getElementById('direccionEntrega').value.trim();

    // Mismo criterio que Controlador.DireccionValidator en el servidor:
    // esto es solo para avisar rápido sin esperar el viaje al servidor.
    // La validación que de verdad protege es la del servidor, porque
    // esta se puede saltar fácilmente (herramientas de desarrollador,
    // formularios armados a mano, etc.).
    if (tipo === 'Domicilio') {
        if (direccion === '') {
            e.preventDefault();
            mostrarModalAlerta('Para un pedido a domicilio necesitamos la dirección de entrega.');
            return;
        }
        if (direccion.length < 10) {
            e.preventDefault();
            mostrarModalAlerta('La dirección parece incompleta. Incluye calle/carrera, número y barrio (ej: Cra 12 #34-56, Barrio Centro).');
            return;
        }
        if (!/\d/.test(direccion)) {
            e.preventDefault();
            mostrarModalAlerta('La dirección debe incluir un número (de la calle/carrera y de la casa o apartamento), para que el repartidor pueda ubicarte.');
            return;
        }
        if (/^(.)\1*$/i.test(direccion.replace(/\s+/g, ''))) {
            e.preventDefault();
            mostrarModalAlerta('Esa dirección no parece real. Escribe la dirección completa de entrega.');
            return;
        }
        if (direccionFueraDeCobertura(direccion)) {
            e.preventDefault();
            mostrarModalAlerta('Por ahora solo hacemos domicilios en Soacha y Bogotá. Si tu dirección queda en otra ciudad, escríbenos por WhatsApp para ver otras opciones.');
            return;
        }
    }

    // Evita pedidos duplicados/triplicados: si el usuario ya envió el
    // formulario una vez, no dejamos que se dispare otro submit aunque
    // vuelva a hacer clic (o doble clic) en "Confirmar pedido" mientras
    // el navegador procesa el POST anterior.
    if (this.dataset.enviado === 'true') {
        e.preventDefault();
        return;
    }
    this.dataset.enviado = 'true';

    var btnConfirmar = this.querySelector('button[type="submit"]');
    if (btnConfirmar) {
        btnConfirmar.disabled = true;
        btnConfirmar.textContent = 'Enviando pedido...';
    }
});

// --- Cantidad y quitar, sin recargar la página --------------------------
// Antes la única forma de cambiar de idea sobre una cantidad era borrar
// la línea entera y volver al menú a agregarla de nuevo. El servidor ya
// soportaba la acción "actualizar"; solo faltaba usarla desde aquí.
const ctxJs = "${ctx}";

function recalcularTotal() {
    let total = 0;
    document.querySelectorAll('.carrito-item').forEach(fila => {
        const precioUnitario = parseFloat(fila.dataset.precioUnitario);
        const cantidad = parseInt(fila.querySelector('.carrito-cantidad-input').value);
        const subtotal = precioUnitario * cantidad;
        fila.querySelector('.carrito-item-precio').textContent = '$' + subtotal;
        total += subtotal;
    });
    const totalTexto = document.getElementById('carritoTotalTexto');
    if (totalTexto) totalTexto.textContent = total;
}

function enviarCarrito(datos) {
    return fetch(ctxJs + '/Carrito', {
        method: 'POST',
        headers: { 'X-Requested-With': 'XMLHttpRequest', 'Content-Type': 'application/x-www-form-urlencoded' },
        body: datos
    }).then(r => r.json());
}

function cambiarCantidad(clave, delta) {
    const fila = document.querySelector('.carrito-item[data-clave="' + clave + '"]');
    if (!fila) return;
    const input = fila.querySelector('.carrito-cantidad-input');
    let nueva = parseInt(input.value) + delta;
    if (nueva < 1) { quitarItem(clave); return; }
    if (nueva > 20) nueva = 20;
    input.value = nueva;
    recalcularTotal();

    enviarCarrito(new URLSearchParams({ accion: 'actualizar', clave: clave, cantidad: nueva }))
        .catch(() => { /* si falla, la próxima recarga de la página vuelve a mostrar el valor real */ });
}

function quitarItem(clave) {
    const fila = document.querySelector('.carrito-item[data-clave="' + clave + '"]');
    if (!fila) return;
    fila.classList.add('saliendo');

    setTimeout(() => {
        fila.remove();
        recalcularTotal();
        if (!document.querySelector('.carrito-item')) {
            document.getElementById('carritoVacioMsg').style.display = '';
            document.getElementById('carritoResumenWrap').style.display = 'none';
        }
    }, 200);

    enviarCarrito(new URLSearchParams({ accion: 'quitar', clave: clave }))
        .catch(() => { /* si falla, la próxima recarga de la página vuelve a mostrar el estado real */ });
}
</script>
</body>
</html>
