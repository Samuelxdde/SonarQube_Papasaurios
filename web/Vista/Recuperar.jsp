<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Recuperar Contraseña - Papasaurios</title>
    <link rel="stylesheet" href="${ctx}/Vista/Css/style.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@tabler/icons-webfont@2.44.0/tabler-icons.min.css" integrity="sha384-mY6asDzXlnHeimHHXw8cavPeArhEcAfnCibDFiaDPex3Fudo+edh/hnT7siZz8bg" crossorigin="anonymous">
    <script src="${ctx}/Vista/JavaScript/validarReg.js"></script>
    <script src="${ctx}/Vista/JavaScript/navActivo.js"></script>
</head>
<body>
    <div class="Formulario">
        <h2>Recuperar Contraseña</h2>

        <!-- Paso 1: pedir el código. Se muestra mientras no haya un correo
             guardado en request (lo pone SolicitarRecuperacion al reenviar
             aquí, y también ConfirmarRecuperacion cuando el código falló). -->
        <c:if test="${empty correo}">
            <form action="${ctx}/SolicitarRecuperacion" method="post" onsubmit="return validarSolicitarRecuperacion()">
                <label for="correoRecuperar">Correo registrado:</label>
                <input type="email" name="correo" id="correoRecuperar" placeholder="tu@correo.com" required>

                <button type="submit">Enviar código de recuperación</button>
                <button type="button" onclick="window.location='${ctx}/'">Volver al inicio</button>
            </form>
        </c:if>

        <!-- Paso 2: ingresar el código recibido y la contraseña nueva. -->
        <c:if test="${not empty correo}">
            <form action="${ctx}/ConfirmarRecuperacion" method="post" onsubmit="return validarConfirmarRecuperacion()">
                <input type="hidden" name="correo" value="${correo}">

                <label for="codigo">Código recibido en ${correo}:</label>
                <input type="text" name="codigo" id="codigo" maxlength="6" placeholder="123456" required>

                <label for="claveNueva">Nueva contraseña:</label>
                <input type="password" name="claveNueva" id="claveNueva" minlength="6" required>

                <label for="claveConfirmar">Confirmar contraseña:</label>
                <input type="password" name="claveConfirmar" id="claveConfirmar" minlength="6" required>

                <button type="submit">Cambiar contraseña</button>
                <button type="button" onclick="window.location='${ctx}/Recuperar'">Solicitar otro código</button>
            </form>
        </c:if>
    </div>

    <!-- ===== MODAL DE CARGA ===== -->
    <div class="overlay-carga" id="overlayCarga">
        <div class="modal-carga">
            <div class="spinner-carga"></div>
            <p data-texto-carga>Enviando...</p>
            <span>Un momento, por favor</span>
        </div>
    </div>

    <!-- ===== MODAL DE MENSAJE (info / error) ===== -->
    <div class="overlay-mensaje" id="overlayMensaje">
        <div class="modal-mensaje tipo-info">
            <button type="button" class="modal-mensaje-cerrar" onclick="ocultarModalMensaje()" aria-label="Cerrar">&times;</button>
            <div class="modal-mensaje-icono"><i id="iconoMensaje" class="ti ti-mail-check"></i></div>
            <h3 id="tituloMensaje">Te tenemos noticias</h3>
            <p id="textoMensaje"></p>
            <button type="button" class="modal-mensaje-aceptar" onclick="ocultarModalMensaje()">Entendido</button>
        </div>
    </div>

    <!-- ===== MODAL DE ÉXITO (contraseña actualizada) ===== -->
    <div class="overlay-exito" id="overlayExito">
        <div class="modal-exito">
            <div class="modal-exito-icono"><i class="ti ti-circle-check"></i></div>
            <h3>¡Contraseña actualizada!</h3>
            <p id="textoExito">Ya puedes iniciar sesión con tu nueva contraseña.</p>
            <a id="btnExitoContinuar" class="modal-exito-continuar" href="${ctx}/Iniciar">Ir a iniciar sesión</a>
            <span class="modal-exito-cuenta" id="cuentaExito"></span>
        </div>
    </div>

    <c:if test="${not empty mensaje}">
        <!-- El texto va en un elemento oculto (no incrustado en el <script>)
             para no depender de escapar comillas dentro de un literal JS. -->
        <span id="datoMensajeServidor" hidden data-tipo="${tipoMensaje}">${mensaje}</span>
        <script>
            document.addEventListener("DOMContentLoaded", function () {
                const dato = document.getElementById("datoMensajeServidor");
                const texto = dato.textContent;
                const tipo = dato.dataset.tipo;
                if (tipo === "exito") {
                    mostrarModalExito(texto, "${ctx}/Iniciar", 4);
                } else if (tipo === "error") {
                    mostrarModalMensaje("error", texto);
                } else {
                    mostrarModalMensaje("info", texto);
                }
            });
        </script>
    </c:if>
</body>
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
</html>
