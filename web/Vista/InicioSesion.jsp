<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Iniciar Sesión — Papasaurios</title>
    <link rel="stylesheet" href="${ctx}/Vista/Css/style.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@tabler/icons-webfont@2.44.0/tabler-icons.min.css" integrity="sha384-mY6asDzXlnHeimHHXw8cavPeArhEcAfnCibDFiaDPex3Fudo+edh/hnT7siZz8bg" crossorigin="anonymous">
    <script src="${ctx}/Vista/JavaScript/validarReg.js"></script>
    <script src="${ctx}/Vista/JavaScript/navActivo.js"></script>
</head>
<body>

<!-- ===== NAVBAR ===== -->
<nav class="navbar scrolled" id="navbar">
    <div class="navbar-inner">
        <ul class="nav-links nav-left">
            <li><a href="${ctx}/Menu">Menú</a></li>
        </ul>

        <a href="${ctx}/" class="nav-logo">
            <img src="${ctx}/Vista/Imagenes/loguito.png" alt="Logo">
            <span>Papasaurios</span>
        </a>

        <ul class="nav-links nav-right">
            <li><a href="${ctx}/Carrito">Mi carrito</a></li>
            <li><a href="${ctx}/CargarRegistro" class="nav-btn">Registrarse</a></li>
        </ul>

        <button class="hamburger" id="hamburger" aria-label="Menú">
            <span></span><span></span><span></span>
        </button>
    </div>
    <div class="mobile-menu" id="mobileMenu">
        <a href="${ctx}/">Inicio</a>
        <a href="${ctx}/Menu">Menú</a>
        <a href="${ctx}/Carrito">Mi carrito</a>
        <a href="${ctx}/CargarRegistro">Registrarse</a>
    </div>
</nav>

<!-- ===== FONDO ===== -->
<div class="login-page">
    <div class="login-overlay"></div>

    <div class="Formulario">
        <div class="login-brand">
            <img src="${ctx}/Vista/Imagenes/loguito.png" alt="Logo">
            <span>Papasaurios</span>
        </div>
        <hr class="login-divider">

        <h2>Inicia sesión</h2>
        <p class="login-sub">Ingresa tu documento y contraseña para continuar</p>

        <c:if test="${not empty mensaje}">
            <p class="mensaje">${mensaje}</p>
        </c:if>

        <c:if test="${not empty sessionScope.mensajeRegistro}">
            <span id="datoMensajeRegistro" hidden>${sessionScope.mensajeRegistro}</span>
            <c:remove var="mensajeRegistro" scope="session"/>
        </c:if>

        <form action="${ctx}/Iniciar" method="post" onsubmit="return validarLogin()">
            <div class="form-field">
                <label for="usuario"><i class="ti ti-id-badge-2"></i> Documento</label>
                <input type="text" name="usuario" id="usuario" placeholder="Ingresa tu documento" required>
            </div>
            <div class="form-field">
                <label for="pass"><i class="ti ti-lock"></i> Contraseña</label>
                <input type="password" name="pass" id="pass" placeholder="Ingresa tu clave" required>
            </div>

            <div class="login-links login-links-top">
                <a href="${ctx}/Recuperar">¿Olvidaste tu contraseña?</a>
            </div>

            <button type="submit" class="btn-login">Iniciar sesión</button>
        </form>

        <div class="login-separador"><span>o</span></div>

        <p class="login-crear-cuenta">
            ¿Todavía no tienes cuenta?
            <a href="${ctx}/CargarRegistro">Regístrate aquí</a>
        </p>
    </div>
</div>

<!-- ===== MODAL DE CARGA ===== -->
<div class="overlay-carga" id="overlayCarga">
    <div class="modal-carga">
        <div class="spinner-carga"></div>
        <p data-texto-carga>Iniciando sesión...</p>
        <span>Un momento, por favor</span>
    </div>
</div>

<!-- ===== MODAL "CUENTA CREADA" (tras registrarse con éxito) ===== -->
<div class="overlay-exito" id="overlayExitoRegistro">
    <div class="modal-exito">
        <div class="modal-exito-icono"><i class="ti ti-circle-check"></i></div>
        <h3>¡Cuenta creada!</h3>
        <p id="textoExitoRegistro"></p>
        <button type="button" class="modal-exito-continuar" onclick="document.getElementById('overlayExitoRegistro').classList.remove('activo')">Entendido, ya voy a iniciar sesión</button>
    </div>
</div>

<script>
const hamburger = document.getElementById('hamburger');
const mobileMenu = document.getElementById('mobileMenu');
hamburger.addEventListener('click', () => {
    hamburger.classList.toggle('active');
    mobileMenu.classList.toggle('open');
});

document.addEventListener('DOMContentLoaded', function () {
    const dato = document.getElementById('datoMensajeRegistro');
    if (dato) {
        document.getElementById('textoExitoRegistro').textContent = dato.textContent;
        document.getElementById('overlayExitoRegistro').classList.add('activo');
    }
});

document.addEventListener('click', function (evento) {
    if (evento.target.id === 'overlayExitoRegistro') {
        evento.target.classList.remove('activo');
    }
});

document.addEventListener('keydown', function (evento) {
    if (evento.key === 'Escape') {
        document.getElementById('overlayExitoRegistro').classList.remove('activo');
    }
});
</script>

<!-- FOOTER -->
<!-- Antes esta página no tenía footer: no había forma de llegar a la
     Política de tratamiento de datos ni al contacto por WhatsApp desde
     aquí, a diferencia de las demás páginas públicas del sitio. -->
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
    <div class="footer-copy">
        <p>&copy; 2026 Papasaurios - Salchipapería. Todos los derechos reservados.</p>
        <p><a href="${ctx}/PoliticaDatos">Política de tratamiento de datos y términos</a></p>
    </div>
</footer>

</body>
</html>
