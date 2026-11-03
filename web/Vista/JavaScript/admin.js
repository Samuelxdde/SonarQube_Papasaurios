var MOBILE_BREAKPOINT = 900;

function toggleSidebar() {
    var sidebar = document.getElementById('sidebar');
    if (!sidebar) return;

    if (window.innerWidth <= MOBILE_BREAKPOINT) {
        // En móvil: el sidebar entra/sale como panel deslizante con fondo oscuro
        sidebar.classList.toggle('mobile-open');
        var overlay = document.getElementById('sidebarOverlay');
        if (overlay) overlay.classList.toggle('show');
    } else {
        // En escritorio: se colapsa a solo iconos
        sidebar.classList.toggle('collapsed');
        var main = document.querySelector('.main-content');
        if (main) main.classList.toggle('expanded');
    }
}

function closeSidebarMobile() {
    var sidebar = document.getElementById('sidebar');
    var overlay = document.getElementById('sidebarOverlay');
    if (sidebar) sidebar.classList.remove('mobile-open');
    if (overlay) overlay.classList.remove('show');
}

// Si el usuario gira el celular o agranda la ventana hacia escritorio,
// nos aseguramos de que el sidebar no quede "abierto" en un estado inconsistente
window.addEventListener('resize', function () {
    if (window.innerWidth > MOBILE_BREAKPOINT) {
        closeSidebarMobile();
    }
});

function filtrar(tablaId) {
    var input = document.activeElement;
    var filtro = input && input.value ? input.value.toLowerCase() : '';
    var tabla = document.getElementById(tablaId);
    if (!tabla) return;
    var filas = tabla.querySelectorAll('tbody tr');
    filas.forEach(function (fila) {
        fila.style.display = fila.innerText.toLowerCase().includes(filtro) ? '' : 'none';
    });
}

function confirmar() {
    return confirm('¿Estás seguro de realizar esta acción?');
}
