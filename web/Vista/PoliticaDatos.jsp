<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Política de tratamiento de datos y términos - Papasaurios</title>
    <link rel="stylesheet" href="${ctx}/Vista/Css/style.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@tabler/icons-webfont@2.44.0/tabler-icons.min.css" integrity="sha384-mY6asDzXlnHeimHHXw8cavPeArhEcAfnCibDFiaDPex3Fudo+edh/hnT7siZz8bg" crossorigin="anonymous">
    <style>
        /* Estilos mínimos propios de esta página: el resto del sitio no
           tenía ninguna vista de solo texto largo (títulos, listas), así
           que se agregan aquí en vez de tocar style.css globalmente. */
        .legal-contenido { max-width: 820px; margin: 0 auto; padding: calc(var(--nav-h) + 40px) 20px 80px; line-height: 1.6; }
        .legal-contenido h1 { margin-bottom: 8px; font-size: 1.7rem; }
        .legal-contenido .legal-fecha { color: var(--texto-medio, #666); margin-bottom: 32px; font-size: 14px; }
        .legal-contenido h2 { margin-top: 40px; margin-bottom: 12px; font-size: 1.25rem; }
        .legal-contenido h3 { margin-top: 24px; margin-bottom: 8px; font-size: 1.05rem; }
        .legal-contenido p, .legal-contenido li { font-size: 0.95rem; }
        .legal-contenido ul { padding-left: 22px; margin-bottom: 16px; }
        .legal-contenido li { margin-bottom: 6px; }

        @media (max-width: 640px) {
            .legal-contenido { padding: calc(var(--nav-h) + 24px) 16px 56px; }
            .legal-contenido h1 { font-size: 1.4rem; }
            .legal-contenido h2 { font-size: 1.1rem; margin-top: 30px; }
            .legal-contenido h3 { font-size: 1rem; }
            .legal-contenido p, .legal-contenido li { font-size: 0.9rem; }
        }
    </style>
</head>
<body>

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
                <li><a href="${ctx}/Menu">Menú</a></li>
                <li><a href="${ctx}/Carrito" class="nav-btn"><i class="ti ti-shopping-cart"></i> Mi carrito</a></li>
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
            <a href="${ctx}/Menu">Menú</a>
            <a href="${ctx}/Carrito">Mi carrito</a>
            <c:if test="${not empty sessionScope.nombreUsuario}"><a href="${ctx}/PanelUsuario">Mis pedidos</a></c:if>
            <c:if test="${empty sessionScope.nombreUsuario}"><a href="${ctx}/Iniciar">Ingresar</a></c:if>
        </div>
    </nav>

    <div class="legal-contenido">
        <h1>Política de tratamiento de datos personales y Términos y condiciones</h1>
        <p class="legal-fecha">Última actualización: agosto de 2026</p>

        <h2>1. Responsable del tratamiento</h2>
        <p>
            <strong>Papasaurios – Salchipapería</strong> (en adelante, "Papasaurios"), con domicilio en
            DG 34 #13A-40, Soacha, Cundinamarca, Colombia, es responsable del tratamiento de los
            datos personales que los usuarios suministran a través de este sitio web y de la
            aplicación móvil asociada.
        </p>
        <p>
            Canal de contacto para temas de datos personales:
            WhatsApp: <a href="https://wa.me/573143007413" target="_blank">+57 314 300 7413</a>.
        </p>

        <h2>2. Datos que se recolectan</h2>
        <p>Al registrarte, hacer un pedido o usar el seguimiento de entrega, Papasaurios recolecta:</p>
        <ul>
            <li>Datos de identificación: nombre, apellido, tipo y número de documento.</li>
            <li>Datos de contacto: teléfono y correo electrónico.</li>
            <li>Fecha de nacimiento (para el registro de la cuenta).</li>
            <li>Contraseña (almacenada siempre cifrada, nunca en texto plano).</li>
            <li>Historial de pedidos, productos elegidos y método de pago asociado a cada pedido.</li>
            <li>Ubicación GPS en tiempo real, únicamente durante el trayecto de un pedido asignado a
                un repartidor, para que el cliente pueda ver el avance de su entrega.</li>
        </ul>

        <h2>3. Finalidad del tratamiento</h2>
        <ul>
            <li>Crear y administrar tu cuenta de usuario.</li>
            <li>Procesar, preparar y entregar los pedidos realizados.</li>
            <li>Contactarte para confirmar, aclarar o dar seguimiento a un pedido.</li>
            <li>Enviar la contraseña temporal cuando se solicita recuperación de cuenta.</li>
            <li>Cumplir obligaciones legales y responder ante autoridades competentes cuando aplique.</li>
        </ul>
        <p>
            Papasaurios no vende ni comparte tus datos personales con terceros con fines comerciales
            ajenos a la operación del pedido.
        </p>

        <h2>4. Derechos del titular de los datos</h2>
        <p>Como titular de tus datos personales, tienes derecho a:</p>
        <ul>
            <li>Conocer, actualizar y rectificar tus datos.</li>
            <li>Solicitar prueba de la autorización otorgada para el tratamiento de tus datos.</li>
            <li>Ser informado sobre el uso que se le ha dado a tus datos.</li>
            <li>Presentar quejas ante la Superintendencia de Industria y Comercio por infracciones a la ley.</li>
            <li>Revocar la autorización y/o solicitar la supresión de tus datos, cuando no exista un
                deber legal o contractual que obligue a conservarlos (por ejemplo, historial de pedidos
                con fines contables).</li>
            <li>Acceder de forma gratuita a tus datos que hayan sido objeto de tratamiento.</li>
        </ul>
        <p>
            Estas solicitudes se pueden presentar por el canal de contacto indicado en la sección 1.
        </p>

        <h2>5. Conservación de los datos</h2>
        <p>
            Los datos se conservarán mientras la cuenta permanezca activa y, después de eso, durante
            el tiempo necesario para cumplir obligaciones legales, contables o fiscales. La ubicación
            GPS de un pedido no se conserva más allá de lo necesario para dar seguimiento a esa
            entrega puntual.
        </p>

        <h2>6. Seguridad de la información</h2>
        <p>
            Papasaurios implementa medidas técnicas razonables para proteger los datos personales
            contra pérdida, uso indebido, acceso no autorizado o alteración (por ejemplo, contraseñas
            almacenadas siempre cifradas y controles de acceso por rol dentro del sistema).
        </p>

        <hr style="margin: 40px 0; border: none; border-top: 1px solid #eee;">

        <h2>7. Términos y condiciones de uso</h2>

        <h3>7.1 Aceptación</h3>
        <p>
            Al registrarte o realizar un pedido a través de este sitio, aceptas estos términos y la
            política de tratamiento de datos descrita arriba.
        </p>

        <h3>7.2 Pedidos y pagos</h3>
        <p>
            Los precios y la disponibilidad de los productos mostrados en el menú pueden cambiar sin
            previo aviso. Un pedido se considera confirmado una vez pagado o aceptado según el método
            de pago elegido. Papasaurios se reserva el derecho de cancelar un pedido ante
            indisponibilidad de algún producto, notificando al cliente por los canales de contacto
            registrados.
        </p>

        <h3>7.3 Cuentas de usuario</h3>
        <p>
            Eres responsable de mantener la confidencialidad de tu contraseña y de la actividad
            realizada desde tu cuenta. Debes notificar de inmediato cualquier uso no autorizado.
        </p>

        <h3>7.4 Entregas</h3>
        <p>
            Los tiempos de entrega son estimados y pueden variar según la demanda, el clima o
            condiciones de tránsito. El seguimiento en tiempo real solo está disponible mientras el
            pedido está en camino con un repartidor asignado.
        </p>

        <h3>7.5 Modificaciones</h3>
        <p>
            Papasaurios puede actualizar estos términos y la política de tratamiento de datos en
            cualquier momento. Los cambios se publicarán en esta misma página con la fecha de
            actualización correspondiente.
        </p>
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
            <p>&copy; 2026 Papasaurios. Todos los derechos reservados.</p>
            <p><a href="${ctx}/PoliticaDatos">Política de tratamiento de datos y términos</a></p>
        </div>
    </footer>

    <a href="${ctx}/Carrito" class="carrito-flotante">
        <i class="ti ti-shopping-cart"></i>
        <c:if test="${not empty sessionScope.carrito}">
            <span class="carrito-flotante-badge">${sessionScope.carrito.size()}</span>
        </c:if>
    </a>

    <script>
        const hamburger = document.getElementById('hamburger');
        const mobileMenu = document.getElementById('mobileMenu');
        hamburger.addEventListener('click', () => {
            hamburger.classList.toggle('active');
            mobileMenu.classList.toggle('open');
        });
    </script>
</body>
</html>
