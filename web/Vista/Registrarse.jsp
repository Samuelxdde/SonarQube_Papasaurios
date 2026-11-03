<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Registrarse - Papasaurios</title>
    <link rel="stylesheet" href="${ctx}/Vista/Css/style.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@tabler/icons-webfont@2.44.0/tabler-icons.min.css" integrity="sha384-mY6asDzXlnHeimHHXw8cavPeArhEcAfnCibDFiaDPex3Fudo+edh/hnT7siZz8bg" crossorigin="anonymous">
    <script src="${ctx}/Vista/JavaScript/validarReg.js"></script>
    <script src="${ctx}/Vista/JavaScript/navActivo.js"></script>
</head>
<body>

    <!-- NAVBAR -->
    <header class="navbar scrolled">
        <div class="navbar-inner">
            <a href="${ctx}/" class="nav-logo">
                <img src="${ctx}/Vista/Imagenes/logo.png" alt="Logo">
                <span>Papasaurios</span>
            </a>
            <ul class="nav-links">
                <li><a href="${ctx}/Menu">Menú</a></li>
                <li><a href="${ctx}/Carrito">Mi carrito</a></li>
                <li><a href="${ctx}/Iniciar" class="nav-btn">Iniciar Sesión</a></li>
            </ul>
        </div>
    </header>

    <!-- FORMULARIO REGISTRO -->
    <section class="seccion seccion-historia seccion-formulario">
        <div class="Formulario Formulario-ancho">
            <h2>Registrarse</h2>
            <form action="${ctx}/Registrarse" method="post" onsubmit="return validarRegistro()">
                <c:if test="${not empty resultado}">
                    <p class="mensaje">${resultado}</p>
                </c:if>

                <div class="campos-grid">
                    <div class="form-field">
                        <label for="nombre">Nombre</label>
                        <input type="text" name="nombrep" id="nombre" required>
                        <small class="campo-error" id="errorNombre"><i class="ti ti-alert-circle"></i> <span></span></small>
                    </div>

                    <div class="form-field">
                        <label for="apellido">Apellido</label>
                        <input type="text" name="apellidoa" id="apellido" required>
                        <small class="campo-error" id="errorApellido"><i class="ti ti-alert-circle"></i> <span></span></small>
                    </div>

                    <div class="form-field">
                        <label for="tipodoc">Tipo de documento</label>
                        <select id="tipodoc" name="tipodocs">
                            <c:forEach var="tipo" items="${tiposDoc}">
                                <option value="${tipo.idTipo_documento}">${tipo.descripcion_doc}</option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="form-field">
                        <label for="documento">Número de documento</label>
                        <input type="text" name="documentoa" id="documento" required>
                        <small class="campo-error" id="errorDocumento"><i class="ti ti-alert-circle"></i> <span></span></small>
                    </div>

                    <div class="form-field">
                        <label for="telefono">Teléfono</label>
                        <input type="text" name="telefonoi" id="telefono" pattern="\+?\d{7,15}" title="Solo números (puede empezar con '+' para el indicativo de país), entre 7 y 15 dígitos" required>
                        <small class="campo-error" id="errorTelefono"><i class="ti ti-alert-circle"></i> <span></span></small>
                    </div>

                    <div class="form-field">
                        <label for="correo">Correo</label>
                        <div class="correo-verificacion">
                            <input type="email" name="correoz" id="correo" required>
                            <button type="button" id="btnEnviarCodigo" onclick="enviarCodigoRegistro()">Enviar código</button>
                        </div>
                        <small class="campo-error" id="errorCorreo"><i class="ti ti-alert-circle"></i> <span></span></small>
                        <small id="estadoCorreo" class="estado-correo"></small>
                    </div>

                    <div class="form-field">
                        <label for="clave">Contraseña</label>
                        <input type="password" name="clavev" id="clave" required>
                        <small class="campo-error" id="errorClave"><i class="ti ti-alert-circle"></i> <span></span></small>
                    </div>

                    <div class="form-field">
                        <label for="fecha_nac">Fecha de nacimiento</label>
                        <input type="date" name="fecha_nac" id="fecha_nac" required>
                        <small class="campo-error" id="errorFechaNac"><i class="ti ti-alert-circle"></i> <span></span></small>
                    </div>

                    <div class="form-nota col-full">
                        Este formulario crea una cuenta de <strong>cliente</strong>.
                        Si necesitas una cuenta de administrador o de repartidor, pídesela a un administrador
                        desde el panel de usuarios.
                    </div>

                    <div class="form-check col-full">
                        <input type="checkbox" name="checkbox" id="checkbox" required>
                        <label for="checkbox">Acepto el tratamiento de mis datos personales y los
                        <a href="${ctx}/PoliticaDatos" target="_blank">términos y la política de tratamiento de datos</a></label>
                    </div>

                    <div class="form-botones col-full">
                        <button type="submit" id="btnRegistrarse" class="btn-pendiente-verificacion" title="Primero verifica tu correo">Registrarse</button>
                        <button type="button" onclick="window.location='${ctx}/Iniciar'">Iniciar Sesión</button>
                    </div>
                </div>
            </form>
        </div>
    </section>

    <!-- ===== MODAL DE CARGA ===== -->
    <div class="overlay-carga" id="overlayCarga">
        <div class="modal-carga">
            <div class="spinner-carga"></div>
            <p data-texto-carga>Creando tu cuenta...</p>
            <span>Un momento, por favor</span>
        </div>
    </div>

    <!-- ===== MODAL "VERIFICA TU CORREO" ===== -->
    <div class="overlay-codigo" id="overlayCodigo">
        <div class="modal-codigo">
            <button type="button" class="modal-codigo-cerrar" onclick="ocultarModalCodigo()" aria-label="Cerrar">&times;</button>
            <div class="modal-codigo-icono"><i class="ti ti-mail-check"></i></div>
            <h3>Verifica tu correo</h3>
            <p>Te enviamos un código de 6 dígitos a <strong id="modalCodigoCorreo"></strong>.
               Revisa también la carpeta de spam. Ingrésalo aquí para poder continuar con tu registro.</p>

            <input type="text" id="codigoVerificacion" maxlength="6" placeholder="123456"
                   inputmode="numeric" autocomplete="one-time-code" aria-label="Código de verificación">

            <button type="button" id="btnVerificarCodigo" class="modal-codigo-verificar" onclick="verificarCodigoRegistro()">
                Verificar código
            </button>

            <small id="estadoVerificacion"></small>

            <button type="button" class="modal-codigo-reenviar" id="btnReenviarCodigo" onclick="enviarCodigoRegistro()">
                ¿No te llegó? Reenviar código
            </button>
        </div>
    </div>

    <!-- ===== MODAL "REVISA ESTE CAMPO" (validación de registro) ===== -->
    <div class="overlay-alerta" id="overlayAlerta">
        <div class="modal-alerta">
            <button type="button" class="modal-alerta-cerrar" onclick="ocultarModalAlerta()" aria-label="Cerrar">&times;</button>
            <div class="modal-alerta-icono"><i class="ti ti-alert-triangle"></i></div>
            <h3>Revisa este campo</h3>
            <p id="mensajeAlerta"></p>
            <button type="button" class="modal-alerta-aceptar" onclick="ocultarModalAlerta()">Entendido</button>
        </div>
    </div>

    <!-- FOOTER -->
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
