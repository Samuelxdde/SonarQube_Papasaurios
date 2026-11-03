window.addEventListener("pageshow", function (evento) {
    if (evento.persisted) {
        ocultarCarga();
        ocultarModalLogin();
    }
});

function mostrarCarga(mensaje) {
    const overlay = document.getElementById("overlayCarga");
    if (!overlay) return;
    const texto = overlay.querySelector("[data-texto-carga]");
    if (texto && mensaje) texto.textContent = mensaje;
    overlay.classList.add("activo");
}

function ocultarCarga() {
    const overlay = document.getElementById("overlayCarga");
    if (overlay) overlay.classList.remove("activo");
}

function mostrarModalLogin(mensaje) {
    const overlay = document.getElementById("overlayLogin");
    if (!overlay) return;
    const texto = overlay.querySelector("[data-texto-login]");
    if (texto && mensaje) texto.textContent = mensaje;
    overlay.classList.add("activo");
}

function ocultarModalLogin() {
    const overlay = document.getElementById("overlayLogin");
    if (overlay) overlay.classList.remove("activo");
}

document.addEventListener("click", function (evento) {
    if (evento.target.id === "overlayLogin") {
        ocultarModalLogin();
    }
    if (evento.target.id === "overlayCodigo") {
        ocultarModalCodigo();
    }
    if (evento.target.id === "overlayAlerta") {
        ocultarModalAlerta();
    }
    if (evento.target.id === "overlayMensaje") {
        ocultarModalMensaje();
    }
    if (evento.target.id === "overlayExito") {
        ocultarModalExito();
    }
});

function mostrarModalCodigo(correo) {
    const overlay = document.getElementById("overlayCodigo");
    if (!overlay) return;
    const spanCorreo = document.getElementById("modalCodigoCorreo");
    if (spanCorreo) spanCorreo.textContent = correo;
    const campoCodigo = document.getElementById("codigoVerificacion");
    if (campoCodigo) {
        campoCodigo.value = "";
        campoCodigo.disabled = false;
    }
    const estado = document.getElementById("estadoVerificacion");
    if (estado) estado.textContent = "";
    overlay.classList.add("activo");
    if (campoCodigo) campoCodigo.focus();
}

function ocultarModalCodigo() {
    const overlay = document.getElementById("overlayCodigo");
    if (overlay) overlay.classList.remove("activo");
}

function mostrarModalAlerta(mensaje) {
    const overlay = document.getElementById("overlayAlerta");
    if (!overlay) {
        // Si la página no tiene este modal (por ejemplo, otro formulario
        // que reutilice validarRegistro en el futuro), no se rompe nada:
        // simplemente cae de vuelta al alert nativo.
        alert(mensaje);
        return;
    }
    const texto = document.getElementById("mensajeAlerta");
    if (texto) texto.textContent = mensaje;
    overlay.classList.add("activo");
}

function ocultarModalAlerta() {
    const overlay = document.getElementById("overlayAlerta");
    if (overlay) overlay.classList.remove("activo");
}

document.addEventListener("keydown", function (evento) {
    if (evento.key === "Escape") {
        ocultarModalCodigo();
        ocultarModalLogin();
        ocultarModalAlerta();
        ocultarModalMensaje();
        ocultarModalExito();
    }
});

// ===== Modal genérico de mensaje (info / error) — Recuperar contraseña =====
// Un solo modal que cambia de color e icono según el tipo, en vez de tener
// un modal distinto por cada caso. "info" se usa cuando el formulario
// simplemente informa algo (p. ej. "si el correo existe, te llegó un
// código"); "error" cuando algo salió mal (código inválido, contraseñas
// que no coinciden, etc).
function mostrarModalMensaje(tipo, mensaje) {
    const overlay = document.getElementById("overlayMensaje");
    if (!overlay) {
        alert(mensaje);
        return;
    }
    const tarjeta = overlay.querySelector(".modal-mensaje");
    const icono = document.getElementById("iconoMensaje");
    const titulo = document.getElementById("tituloMensaje");
    const texto = document.getElementById("textoMensaje");

    tarjeta.classList.remove("tipo-info", "tipo-error");
    tarjeta.classList.add(tipo === "error" ? "tipo-error" : "tipo-info");
    if (icono) icono.className = tipo === "error" ? "ti ti-alert-triangle" : "ti ti-mail-check";
    if (titulo) titulo.textContent = tipo === "error" ? "Revisa este dato" : "Te tenemos noticias";
    if (texto) texto.textContent = mensaje;

    overlay.classList.add("activo");
}

function ocultarModalMensaje() {
    const overlay = document.getElementById("overlayMensaje");
    if (overlay) overlay.classList.remove("activo");
}

// ===== Modal de éxito con redirección (contraseña actualizada) =====
// Reemplaza el simple texto plano "ya puedes iniciar sesión": ahora se ve
// como el resto de modales del sitio, y lleva directo al login en vez de
// dejar a la persona parada en la misma página sin saber qué hacer.
let temporizadorExito = null;

function mostrarModalExito(mensaje, urlDestino, segundos) {
    const overlay = document.getElementById("overlayExito");
    if (!overlay) {
        if (confirm(mensaje + "\n\n¿Ir a iniciar sesión ahora?")) {
            window.location = urlDestino;
        }
        return;
    }
    const texto = document.getElementById("textoExito");
    const cuenta = document.getElementById("cuentaExito");
    const boton = document.getElementById("btnExitoContinuar");
    if (texto) texto.textContent = mensaje;
    if (boton) boton.setAttribute("href", urlDestino);

    overlay.classList.add("activo");

    let restante = segundos || 4;
    if (cuenta) cuenta.textContent = "Te llevamos a iniciar sesión en " + restante + "s...";
    clearInterval(temporizadorExito);
    temporizadorExito = setInterval(function () {
        restante -= 1;
        if (restante <= 0) {
            clearInterval(temporizadorExito);
            window.location = urlDestino;
            return;
        }
        if (cuenta) cuenta.textContent = "Te llevamos a iniciar sesión en " + restante + "s...";
    }, 1000);
}

function ocultarModalExito() {
    const overlay = document.getElementById("overlayExito");
    if (overlay) overlay.classList.remove("activo");
    clearInterval(temporizadorExito);
}

// ===== Validación en el flujo de "Recuperar contraseña" =====
function validarSolicitarRecuperacion() {
    const correo = document.getElementById("correoRecuperar");
    if (!correo) return true;
    if (!patronCorreo.test(correo.value.trim())) {
        mostrarModalMensaje("error", "Ingresa un correo con un formato válido (ejemplo: nombre@dominio.com)");
        return false;
    }
    mostrarCarga("Enviando código...");
    return true;
}

function validarConfirmarRecuperacion() {
    const codigo = document.getElementById("codigo");
    const claveNueva = document.getElementById("claveNueva");
    const claveConfirmar = document.getElementById("claveConfirmar");

    if (codigo && codigo.value.trim() === "") {
        mostrarModalMensaje("error", "Ingresa el código de 6 dígitos que te llegó al correo");
        return false;
    }
    if (claveNueva && claveNueva.value.length < 6) {
        mostrarModalMensaje("error", "La nueva contraseña debe tener al menos 6 caracteres");
        return false;
    }
    if (claveNueva && claveConfirmar && claveNueva.value !== claveConfirmar.value) {
        mostrarModalMensaje("error", "Las contraseñas no coinciden");
        return false;
    }
    mostrarCarga("Actualizando contraseña...");
    return true;
}

// ===== Reglas de validación por campo =====================
// Cada regla recibe el valor ya "trimeado" y devuelve null si es válido,
// o el mensaje de error si no lo es. Se usan tanto para la validación en
// tiempo real (mientras la persona escribe) como para el chequeo final
// al enviar el formulario, para no mantener la misma lógica dos veces.
const patronNombre = /^[A-Za-zÀ-ÖØ-öø-ÿ]+([ '-][A-Za-zÀ-ÖØ-öø-ÿ]+)*$/;
const patronTelefono = /^\+?\d{7,15}$/;
const patronCorreo = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;

function reglaNombre(valor) {
    if (valor === "") return "El nombre es obligatorio";
    if (!patronNombre.test(valor)) return "El nombre solo debe contener letras";
    return null;
}

function reglaApellido(valor) {
    if (valor === "") return "El apellido es obligatorio";
    if (!patronNombre.test(valor)) return "El apellido solo debe contener letras";
    return null;
}

function reglaDocumento(valor) {
    if (valor === "") return "El documento es obligatorio";
    if (Number.isNaN(Number(valor))) return "El documento debe ser numérico";

    // La longitud exigida depende del tipo de documento seleccionado.
    const selectTipo = document.getElementById("tipodoc");
    const tipoTexto = selectTipo && selectTipo.selectedOptions.length
        ? selectTipo.selectedOptions[0].text.toLowerCase()
        : "";
    if (tipoTexto.includes("cédula de ciudadanía") || tipoTexto.includes("cedula de ciudadania")) {
        if (valor.length < 6 || valor.length > 10) return "La cédula de ciudadanía debe tener entre 6 y 10 dígitos";
    } else if (tipoTexto.includes("tarjeta de identidad")) {
        if (valor.length < 8 || valor.length > 11) return "La tarjeta de identidad debe tener entre 8 y 11 dígitos";
    }
    return null;
}

function reglaTelefono(valor) {
    if (valor === "") return "El teléfono es obligatorio";
    if (!patronTelefono.test(valor)) return "Solo números (puede empezar con '+' para el indicativo de país), entre 7 y 15 dígitos";
    return null;
}

function reglaCorreo(valor) {
    if (valor === "") return "El correo es obligatorio";
    if (!patronCorreo.test(valor)) return "Ingresa un correo con un formato válido (ejemplo: nombre@dominio.com)";
    return null;
}

function reglaClave(valor) {
    if (valor === "") return "La contraseña es obligatoria";
    return null;
}

function reglaFechaNac(valor) {
    if (valor === "") return "La fecha de nacimiento es obligatoria";
    return null;
}

// ===== Mostrar/ocultar el error de un campo puntual =====================
function mostrarErrorCampo(input, errorEl, mensaje) {
    const contenedor = input.closest(".form-field");
    if (contenedor) contenedor.classList.add("campo-invalido");
    if (errorEl) {
        const span = errorEl.querySelector("span");
        if (span) span.textContent = mensaje;
        errorEl.classList.add("visible");
    }
}

function limpiarErrorCampo(input, errorEl) {
    const contenedor = input.closest(".form-field");
    if (contenedor) contenedor.classList.remove("campo-invalido");
    if (errorEl) errorEl.classList.remove("visible");
}

// Conecta un campo a su regla: valida en cada tecla que la persona
// escribe (no solo cuando sale del campo), para que un carácter inválido
// o un espacio se marquen en rojo de inmediato, con el mensaje debajo.
// Antes se esperaba a que la persona saliera del campo una vez (blur)
// para recién empezar a validar mientras escribía; ahora se valida desde
// la primera tecla.
function configurarValidacionEnVivo(input, errorEl, regla) {
    if (!input) return;

    function evaluar() {
        const mensaje = regla(input.value.trim());
        if (mensaje) {
            mostrarErrorCampo(input, errorEl, mensaje);
        } else {
            limpiarErrorCampo(input, errorEl);
        }
        return mensaje;
    }

    input.addEventListener("input", evaluar);
    input.addEventListener("blur", evaluar);

    // Se guarda la función de evaluación en el propio elemento para poder
    // volver a llamarla desde afuera (al enviar el formulario, o cuando
    // cambia el tipo de documento y hay que re-chequear su longitud).
    input._evaluarCampo = evaluar;
}

document.addEventListener("DOMContentLoaded", function () {
    configurarValidacionEnVivo(document.getElementById("nombre"), document.getElementById("errorNombre"), reglaNombre);
    configurarValidacionEnVivo(document.getElementById("apellido"), document.getElementById("errorApellido"), reglaApellido);
    configurarValidacionEnVivo(document.getElementById("documento"), document.getElementById("errorDocumento"), reglaDocumento);
    configurarValidacionEnVivo(document.getElementById("telefono"), document.getElementById("errorTelefono"), reglaTelefono);
    configurarValidacionEnVivo(document.getElementById("correo"), document.getElementById("errorCorreo"), reglaCorreo);
    configurarValidacionEnVivo(document.getElementById("clave"), document.getElementById("errorClave"), reglaClave);
    configurarValidacionEnVivo(document.getElementById("fecha_nac"), document.getElementById("errorFechaNac"), reglaFechaNac);

    // El documento se valida distinto según el tipo (cédula, tarjeta de
    // identidad...), así que si cambia el tipo hay que re-chequearlo.
    const selectTipo = document.getElementById("tipodoc");
    const campoDocumento = document.getElementById("documento");
    if (selectTipo && campoDocumento) {
        selectTipo.addEventListener("change", function () {
            if (campoDocumento.value.trim() !== "" && campoDocumento._evaluarCampo) {
                campoDocumento._evaluarCampo();
            }
        });
    }

    // Al salir del campo de correo, si el formato ya es válido, se
    // pregunta al servidor si ese correo ya tiene cuenta. No tiene sentido
    // preguntar si el formato está mal: ese error ya lo muestra reglaCorreo.
    const campoCorreoRegistro = document.getElementById("correo");
    if (campoCorreoRegistro) {
        campoCorreoRegistro.addEventListener("blur", function () {
            if (patronCorreo.test(campoCorreoRegistro.value.trim())) {
                verificarCorreoRegistrado();
            }
        });
    }
});

// Comprueba si el correo ya está registrado y, si es así, lo muestra como
// error de campo (mismo estilo rojo que el resto de validaciones) sin
// esperar a que la persona haga clic en "Enviar código".
function verificarCorreoRegistrado() {
    const campoCorreo = document.getElementById("correo");
    const errorEl = document.getElementById("errorCorreo");
    if (!campoCorreo) return;
    const correo = campoCorreo.value.trim();

    fetch("VerificarCorreoDisponible", {
        method: "POST",
        headers: {"Content-Type": "application/x-www-form-urlencoded"},
        body: "correo=" + encodeURIComponent(correo)
    })
        .then(function (r) { return r.json(); })
        .then(function (datos) {
            // Si mientras esperábamos la respuesta la persona ya cambió el
            // correo, esta respuesta quedó vieja y no aplica.
            if (campoCorreo.value.trim() !== correo) return;
            if (!datos.disponible) {
                mostrarErrorCampo(campoCorreo, errorEl, "Ese correo ya está registrado. ¿Ya tienes cuenta? Inicia sesión.");
            }
        })
        .catch(function () {
            // Si falla la comprobación no bloqueamos nada: el servidor
            // vuelve a chequearlo de todas formas al pedir el código.
        });
}

function validarRegistro() {
    const campos = [
        { input: document.getElementById("nombre"), error: document.getElementById("errorNombre"), regla: reglaNombre },
        { input: document.getElementById("apellido"), error: document.getElementById("errorApellido"), regla: reglaApellido },
        { input: document.getElementById("documento"), error: document.getElementById("errorDocumento"), regla: reglaDocumento },
        { input: document.getElementById("telefono"), error: document.getElementById("errorTelefono"), regla: reglaTelefono },
        { input: document.getElementById("correo"), error: document.getElementById("errorCorreo"), regla: reglaCorreo },
        { input: document.getElementById("clave"), error: document.getElementById("errorClave"), regla: reglaClave },
        { input: document.getElementById("fecha_nac"), error: document.getElementById("errorFechaNac"), regla: reglaFechaNac }
    ];

    // Se revisan todos los campos (no solo hasta el primer error) para que
    // la persona vea de una vez, en rojo, todo lo que falta por corregir —
    // no solo lo primero que encontramos.
    let primerError = null;
    campos.forEach(function (campo) {
        const mensaje = campo.regla(campo.input.value.trim());
        if (mensaje) {
            mostrarErrorCampo(campo.input, campo.error, mensaje);
            if (!primerError) primerError = { input: campo.input, mensaje: mensaje };
        } else {
            limpiarErrorCampo(campo.input, campo.error);
        }
    });

    if (primerError) {
        primerError.input.focus();
        mostrarModalAlerta(primerError.mensaje);
        return false;
    }

    if (!correoVerificadoOk) {
        mostrarModalAlerta("Primero debes verificar tu correo con el código que te enviamos");
        return false;
    }

    mostrarCarga("Creando tu cuenta...");
    return true;
}
// ===== Verificación de correo en el registro (envío y validación de código) =====
let correoVerificadoOk = false;

function marcarCorreoSinVerificar() {
    correoVerificadoOk = false;
    const btnRegistrarse = document.getElementById("btnRegistrarse");
    if (btnRegistrarse) {
        // No se deshabilita de verdad: un botón disabled no dispara el
        // submit, así que un clic ahí no hacía nada ni avisaba por qué.
        // Se deja clicable pero atenuado; validarRegistro() es quien
        // realmente bloquea el envío y muestra el modal de aviso.
        btnRegistrarse.classList.add("btn-pendiente-verificacion");
    }
    const estado = document.getElementById("estadoVerificacion");
    if (estado) {
        estado.textContent = "";
    }
    // Si la persona cambia el correo, el código anterior ya no sirve:
    // se oculta el badge y, si el modal estaba abierto, se cierra.
    const estadoCorreo = document.getElementById("estadoCorreo");
    if (estadoCorreo) {
        estadoCorreo.className = "estado-correo";
        estadoCorreo.innerHTML = "";
    }
    ocultarModalCodigo();
}

document.addEventListener("DOMContentLoaded", function () {
    const campoCorreo = document.getElementById("correo");
    if (campoCorreo) {
        // Si la persona cambia el correo después de haberlo verificado,
        // hay que pedirle que lo verifique de nuevo: el código anterior
        // corresponde al correo viejo.
        campoCorreo.addEventListener("input", marcarCorreoSinVerificar);
    }
});

function enviarCodigoRegistro() {
    const campoCorreo = document.getElementById("correo");
    const correo = campoCorreo.value.trim();

    if (!patronCorreo.test(correo)) {
        mostrarModalAlerta("Ingresa un correo con un formato válido antes de pedir el código");
        return;
    }

    const btnEnviar = document.getElementById("btnEnviarCodigo");
    btnEnviar.disabled = true;
    btnEnviar.textContent = "Enviando...";

    fetch("EnviarCodigoRegistro", {
        method: "POST",
        headers: {"Content-Type": "application/x-www-form-urlencoded"},
        body: "correo=" + encodeURIComponent(correo)
    })
        .then(function (respuesta) { return respuesta.json(); })
        .then(function (datos) {
            if (datos.ok) {
                const estadoCorreo = document.getElementById("estadoCorreo");
                if (estadoCorreo) {
                    estadoCorreo.className = "estado-correo visible pendiente";
                    estadoCorreo.innerHTML = "<i class=\"ti ti-clock\"></i> Código enviado, falta verificarlo";
                }
                mostrarModalCodigo(correo);
            } else {
                // Mismo estilo que el resto de errores de campo (borde rojo +
                // mensaje debajo), en vez del alert() nativo del navegador.
                // Esto cubre, entre otros casos, un correo ya registrado.
                mostrarErrorCampo(campoCorreo, document.getElementById("errorCorreo"), datos.mensaje);
                mostrarModalAlerta(datos.mensaje);
            }
        })
        .catch(function () {
            mostrarModalAlerta("No se pudo contactar al servidor. Intenta de nuevo.");
        })
        .finally(function () {
            btnEnviar.disabled = false;
            btnEnviar.textContent = "Enviar código";
        });
}

function verificarCodigoRegistro() {
    const correo = document.getElementById("correo").value.trim();
    const codigo = document.getElementById("codigoVerificacion").value.trim();
    const estado = document.getElementById("estadoVerificacion");

    if (codigo === "") {
        alert("Ingresa el código que te llegó al correo");
        return;
    }

    const btnVerificar = document.getElementById("btnVerificarCodigo");
    btnVerificar.disabled = true;

    fetch("VerificarCodigoRegistro", {
        method: "POST",
        headers: {"Content-Type": "application/x-www-form-urlencoded"},
        body: "correo=" + encodeURIComponent(correo) + "&codigo=" + encodeURIComponent(codigo)
    })
        .then(function (respuesta) { return respuesta.json(); })
        .then(function (datos) {
            estado.textContent = datos.mensaje;
            const estadoCorreo = document.getElementById("estadoCorreo");
            if (datos.ok) {
                correoVerificadoOk = true;
                document.getElementById("btnRegistrarse").classList.remove("btn-pendiente-verificacion");
                if (estadoCorreo) {
                    estadoCorreo.className = "estado-correo visible verificado";
                    estadoCorreo.innerHTML = "<i class=\"ti ti-circle-check\"></i> Correo verificado";
                }
                // Pequeña pausa para que la persona alcance a ver el mensaje
                // de éxito dentro del modal antes de que se cierre solo.
                setTimeout(ocultarModalCodigo, 900);
            } else {
                correoVerificadoOk = false;
                document.getElementById("btnRegistrarse").classList.add("btn-pendiente-verificacion");
                if (estadoCorreo) {
                    estadoCorreo.className = "estado-correo visible pendiente";
                    estadoCorreo.innerHTML = "<i class=\"ti ti-clock\"></i> Código enviado, falta verificarlo";
                }
            }
        })
        .catch(function () {
            estado.textContent = "No se pudo contactar al servidor. Intenta de nuevo.";
        })
        .finally(function () {
            btnVerificar.disabled = false;
        });
}

function validarLogin() {
    const usuario = document.getElementById("usuario").value.trim();
    const pass = document.getElementById("pass").value.trim();

    if (usuario === "") {
        alert("El documento es obligatorio");
        return false;
    }
    if (Number.isNaN(Number(usuario))) {
        alert("El documento debe ser numérico");
        return false;
    }
    if (pass === "") {
        alert("La clave es obligatoria");
        return false;
    }
    mostrarCarga("Iniciando sesión...");
    return true;
}