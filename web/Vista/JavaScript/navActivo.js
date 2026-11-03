/**
 * Marca como "activo" el enlace de la navbar que corresponde a la página
 * actual, tanto en el menú de escritorio como en el menú móvil.
 *
 * Antes la barra de navegación superior no tenía ningún indicador visual
 * de en qué sección estaba parado el usuario: el CSS ya definía el
 * estilo (.nav-links a.active), pero nada en el sitio le agregaba esa
 * clase a ningún enlace.
 */
document.addEventListener('DOMContentLoaded', function () {
    var rutaActual = window.location.pathname;

    document.querySelectorAll('.nav-links a, .mobile-menu a').forEach(function (enlace) {
        var rutaEnlace;
        try {
            rutaEnlace = new URL(enlace.href, window.location.origin).pathname;
        } catch (e) {
            return; // enlace con formato inesperado: se ignora
        }
        if (rutaEnlace === rutaActual) {
            enlace.classList.add('active');
        }
    });
});
