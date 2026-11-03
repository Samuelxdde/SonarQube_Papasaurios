<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Usuarios — Panel Admin</title>
    <!-- style.css se incluye solo para reutilizar el sistema de modales
         (overlay-alerta/modal-alerta) y los estilos de campo-error que ya
         existen para el registro público (Registrarse.jsp), en vez de
         duplicarlos aquí. admin.css va después para que sus reglas de
         layout (sidebar, body, etc.) sigan mandando en esta página. -->
    <link rel="stylesheet" href="${ctx}/Vista/Css/style.css">
    <link rel="stylesheet" href="${ctx}/Vista/Css/admin.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@tabler/icons-webfont@2.44.0/tabler-icons.min.css" integrity="sha384-mY6asDzXlnHeimHHXw8cavPeArhEcAfnCibDFiaDPex3Fudo+edh/hnT7siZz8bg" crossorigin="anonymous">
    <link href="https://fonts.googleapis.com/css2?family=DM+Sans:wght@400;500;600&family=Syne:wght@600;700&display=swap" rel="stylesheet">
    <script src="${ctx}/Vista/JavaScript/validarReg.js"></script>
    <style>
        /* El resto del sitio usa la clase "form-field" para el estado de
           error (borde rojo + mensaje debajo); el panel admin usa
           "form-group" para el layout. En vez de duplicar esas reglas en
           admin.css, se agregan aquí puntualmente sobre "form-group". */
        .form-group.campo-invalido input,
        .form-group.campo-invalido select {
            border-color: var(--red) !important;
            background: rgba(224,89,74,0.08);
        }
        .form-group .campo-error {
            display: none;
            align-items: center;
            gap: 5px;
            margin-top: 6px;
            font-size: 0.78rem;
            color: var(--red);
        }
        .form-group .campo-error.visible { display: flex; }
        .form-group .campo-error i { font-size: 13px; }
    </style>
</head>
<body>
<%@ include file="sidebar.jsp" %>
<main class="main-content">
    <%@ include file="topbar.jsp" %>
    <div class="crud-header"><h2 class="page-title"><i class="ti ti-users"></i> Gestión de usuarios</h2></div>
    <c:if test="${not empty resultado}"><div class="alert alert-ok"><i class="ti ti-circle-check"></i> ${resultado}</div></c:if>

    <!-- Formulario de creación -->
    <div class="panel form-panel">
        <div class="panel-header"><span class="panel-title"><i class="ti ti-user-plus"></i> Registrar nuevo usuario</span></div>
        <form action="${ctx}/Usuario" method="post" class="crud-form" onsubmit="return validarFormularioUsuarioAdmin('')">
            <input type="hidden" name="accion" value="insertar">
            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
            <div class="form-row">
                <div class="form-group form-field">
                    <label for="nombre">Nombre</label>
                    <input type="text" name="nombre" id="nombre" placeholder="Nombre" required>
                    <small class="campo-error" id="errorNombre"><i class="ti ti-alert-circle"></i> <span></span></small>
                </div>
                <div class="form-group form-field">
                    <label for="apellido">Apellido</label>
                    <input type="text" name="apellido" id="apellido" placeholder="Apellido" required>
                    <small class="campo-error" id="errorApellido"><i class="ti ti-alert-circle"></i> <span></span></small>
                </div>
                <div class="form-group form-field">
                    <label for="documento">Documento</label>
                    <input type="text" name="documento" id="documento" placeholder="Documento" required>
                    <small class="campo-error" id="errorDocumento"><i class="ti ti-alert-circle"></i> <span></span></small>
                </div>
                <div class="form-group form-field">
                    <label for="telefono">Teléfono</label>
                    <input type="text" name="telefono" id="telefono" placeholder="Teléfono" pattern="\+?\d{7,15}" title="Solo números (puede empezar con '+' para el indicativo de país), entre 7 y 15 dígitos" required>
                    <small class="campo-error" id="errorTelefono"><i class="ti ti-alert-circle"></i> <span></span></small>
                </div>
                <div class="form-group form-field">
                    <label for="correo">Correo</label>
                    <input type="email" name="correo" id="correo" placeholder="Correo" required>
                    <small class="campo-error" id="errorCorreo"><i class="ti ti-alert-circle"></i> <span></span></small>
                </div>
                <div class="form-group form-field">
                    <label for="clave">Contraseña</label>
                    <input type="password" name="clave" id="clave" placeholder="Contraseña" required>
                    <small class="campo-error" id="errorClave"><i class="ti ti-alert-circle"></i> <span></span></small>
                </div>
                <div class="form-group form-field">
                    <label for="fecha_nac">Fecha de nacimiento</label>
                    <input type="date" name="fecha_nac" id="fecha_nac" required>
                    <small class="campo-error" id="errorFechaNac"><i class="ti ti-alert-circle"></i> <span></span></small>
                </div>
                <div class="form-group">
                    <label for="tipodoc">Tipo de documento</label>
                    <select name="Tipo_documento_idTipo_documento" id="tipodoc" required>
                        <c:forEach var="td" items="${listaTipoDocumento}">
                            <option value="${td.idTipo_documento}">${td.descripcion_doc}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-group">
                    <label for="Roles_idRoles">Rol</label>
                    <select name="Roles_idRoles" required id="Roles_idRoles">
                        <c:forEach var="r" items="${listaRoles}">
                            <option value="${r.idRoles}">${r.descripcion_rol}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-group">
                    <label style="text-transform:none;font-size:12px;display:flex;align-items:center;gap:6px;">
                        <input type="checkbox" name="checkbox" style="width:auto;"> Acepta tratamiento de datos
                        (<a href="${ctx}/PoliticaDatos" target="_blank">ver política</a>)
                    </label>
                </div>
            </div>
            <div class="form-actions">
                <button type="submit" class="btn-primary"><i class="ti ti-check"></i> Registrar usuario</button>
            </div>
        </form>
    </div>

    <!-- Tabla de usuarios -->
    <div class="panel full-panel">
        <div class="panel-header">
            <span class="panel-title"><i class="ti ti-list"></i> Usuarios registrados</span>
            <div class="panel-actions">
                <div class="search-bar">
                    <i class="ti ti-search"></i>
                    <input type="text" id="searchUsuarios" placeholder="Buscar usuario..." onkeyup="filtrarTabla()" aria-label="Buscar usuario">
                </div>
            </div>
        </div>
        <table id="tablaUsuarios">
            <thead>
                <tr>
                    <th>Nombre</th>
                    <th>Apellido</th>
                    <th>Documento</th>
                    <th>Teléfono</th>
                    <th>Correo</th>
                    <th>Rol</th>
                    <th>Acciones</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="u" items="${listaUsuarios}">
                    <tr>
                        <td><c:out value="${u.nombre}"/></td>
                        <td><c:out value="${u.apellido}"/></td>
                        <td><c:out value="${u.documento}"/></td>
                        <td><c:out value="${u.telefono}"/></td>
                        <td><c:out value="${u.correo}"/></td>
                        <td>
                            <span class="status ${u.roles_idRoles == 1 ? 'activo' : (u.roles_idRoles == 3 || u.roles_idRoles == 4 ? 'pendiente' : '')}">
                                <c:choose>
                                    <c:when test="${u.roles_idRoles == 1}">Admin</c:when>
                                    <c:when test="${u.roles_idRoles == 3}">Repartidor</c:when>
                                    <c:when test="${u.roles_idRoles == 4}">Cocina</c:when>
                                    <c:otherwise>Usuario</c:otherwise>
                                </c:choose>
                            </span>
                        </td>
                        <td>
                            <button type="button" class="action-btn edit"
                                    data-id="${u.idUsuarios}"
                                    data-nombre="<c:out value='${u.nombre}'/>"
                                    data-apellido="<c:out value='${u.apellido}'/>"
                                    data-documento="<c:out value='${u.documento}'/>"
                                    data-telefono="<c:out value='${u.telefono}'/>"
                                    data-correo="<c:out value='${u.correo}'/>"
                                    data-tipodoc="${u.tipo_documento_idTipo_documento}"
                                    data-rol="${u.roles_idRoles}"
                                    data-fechanac="<fmt:formatDate value="${u.fecha_nac}" pattern="yyyy-MM-dd"/>"
                                    onclick="abrirModalEditar(this)">
                                <i class="ti ti-edit"></i>
                            </button>
                            <form action="${ctx}/Usuario" method="post" style="display:inline;">
                                <input type="hidden" name="accion" value="eliminar">
                                <input type="hidden" name="idUsuario" value="${u.idUsuarios}">
                                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                <button type="submit" class="action-btn del" onclick="return confirm('¿Eliminar usuario?')"><i class="ti ti-trash"></i></button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty listaUsuarios}">
                    <tr><td colspan="7" class="empty-row">No hay usuarios registrados</td></tr>
                </c:if>
            </tbody>
        </table>
    </div>

    <!-- Modal de edición -->
    <div id="modalEditar" class="modal-overlay" style="display:none;position:fixed;inset:0;background:rgba(0,0,0,0.55);align-items:center;justify-content:center;z-index:1000;">
        <div class="panel" style="max-width:520px;width:92%;">
            <div class="panel-header"><span class="panel-title"><i class="ti ti-edit"></i> Editar usuario</span></div>
            <form action="${ctx}/Usuario" method="post" class="crud-form" onsubmit="return validarFormularioUsuarioAdmin('edit-')">
                <input type="hidden" name="accion" value="actualizar">
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                <input type="hidden" name="idUsuario" id="edit-id">
                <div class="form-row">
                    <div class="form-group form-field">
                        <label for="edit-nombre">Nombre</label>
                        <input type="text" name="nombre" id="edit-nombre" required>
                        <small class="campo-error" id="edit-errorNombre"><i class="ti ti-alert-circle"></i> <span></span></small>
                    </div>
                    <div class="form-group form-field">
                        <label for="edit-apellido">Apellido</label>
                        <input type="text" name="apellido" id="edit-apellido" required>
                        <small class="campo-error" id="edit-errorApellido"><i class="ti ti-alert-circle"></i> <span></span></small>
                    </div>
                    <div class="form-group form-field">
                        <label for="edit-documento">Documento</label>
                        <input type="text" name="documento" id="edit-documento" required>
                        <small class="campo-error" id="edit-errorDocumento"><i class="ti ti-alert-circle"></i> <span></span></small>
                    </div>
                    <div class="form-group form-field">
                        <label for="edit-telefono">Teléfono</label>
                        <input type="text" name="telefono" id="edit-telefono" pattern="\+?\d{7,15}" title="Solo números (puede empezar con '+' para el indicativo de país), entre 7 y 15 dígitos" required>
                        <small class="campo-error" id="edit-errorTelefono"><i class="ti ti-alert-circle"></i> <span></span></small>
                    </div>
                    <div class="form-group form-field">
                        <label for="edit-correo">Correo</label>
                        <input type="email" name="correo" id="edit-correo" required>
                        <small class="campo-error" id="edit-errorCorreo"><i class="ti ti-alert-circle"></i> <span></span></small>
                    </div>
                    <div class="form-group form-field">
                        <label for="edit-clave">Contraseña</label>
                        <input type="password" name="clave" id="edit-clave" placeholder="Dejar igual o escribir nueva">
                        <small class="campo-error" id="edit-errorClave"><i class="ti ti-alert-circle"></i> <span></span></small>
                    </div>
                    <div class="form-group form-field">
                        <label for="edit-fecha_nac">Fecha de nacimiento</label>
                        <input type="date" name="fecha_nac" id="edit-fecha_nac" required>
                        <small class="campo-error" id="edit-errorFechaNac"><i class="ti ti-alert-circle"></i> <span></span></small>
                    </div>
                    <div class="form-group">
                        <label for="edit-tipodoc">Tipo de documento</label>
                        <select name="Tipo_documento_idTipo_documento" id="edit-tipodoc" required>
                            <c:forEach var="td" items="${listaTipoDocumento}">
                                <option value="${td.idTipo_documento}">${td.descripcion_doc}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="edit-rol">Rol</label>
                        <select name="Roles_idRoles" id="edit-rol" required>
                            <c:forEach var="r" items="${listaRoles}">
                                <option value="${r.idRoles}">${r.descripcion_rol}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
                <div class="form-actions">
                    <button type="submit" class="btn-primary"><i class="ti ti-check"></i> Guardar cambios</button>
                    <button type="button" class="btn-sm" onclick="cerrarModalEditar()">Cancelar</button>
                </div>
            </form>
        </div>
    </div>

    <!-- ===== MODAL "REVISA ESTE CAMPO" (validación de registro/edición de usuario) =====
         Reutiliza el mismo overlay-alerta/modal-alerta y las funciones
         mostrarModalAlerta()/ocultarModalAlerta() de validarReg.js que ya
         usa el registro público, para que se sienta como el mismo sistema
         de validación en vez de un aviso distinto solo para el admin. -->
    <div class="overlay-alerta" id="overlayAlerta">
        <div class="modal-alerta">
            <button type="button" class="modal-alerta-cerrar" onclick="ocultarModalAlerta()" aria-label="Cerrar">&times;</button>
            <div class="modal-alerta-icono"><i class="ti ti-alert-triangle"></i></div>
            <h3>Revisa este campo</h3>
            <p id="mensajeAlerta"></p>
            <button type="button" class="modal-alerta-aceptar" onclick="ocultarModalAlerta()">Entendido</button>
        </div>
    </div>
</main>

<script>
function filtrarTabla() {
    const input = document.getElementById('searchUsuarios').value.toLowerCase();
    const rows = document.querySelectorAll('#tablaUsuarios tbody tr');
    rows.forEach(row => {
        row.style.display = row.innerText.toLowerCase().includes(input) ? '' : 'none';
    });
}
function abrirModalEditar(btn) {
    document.getElementById('edit-id').value = btn.dataset.id;
    document.getElementById('edit-nombre').value = btn.dataset.nombre;
    document.getElementById('edit-apellido').value = btn.dataset.apellido;
    document.getElementById('edit-documento').value = btn.dataset.documento;
    document.getElementById('edit-telefono').value = btn.dataset.telefono;
    document.getElementById('edit-correo').value = btn.dataset.correo;
    document.getElementById('edit-clave').value = '';
    document.getElementById('edit-tipodoc').value = btn.dataset.tipodoc;
    document.getElementById('edit-rol').value = btn.dataset.rol;
    document.getElementById('edit-fecha_nac').value = btn.dataset.fechanac;
    document.getElementById('modalEditar').style.display = 'flex';
}
function cerrarModalEditar() {
    document.getElementById('modalEditar').style.display = 'none';
}

// ===== Validación de "espacios en blanco o caracteres erróneos" =========
// Antes este formulario (a diferencia del registro público en
// Registrarse.jsp) no validaba nada en el navegador: solo tenía
// "required" y un pattern en teléfono. Todo lo demás dependía de que el
// servidor rechazara el formulario y recargara la página completa con un
// texto de error arriba, sin indicar cuál campo era el problema. Ahora
// reutiliza las mismas reglas y el mismo modal "Revisa este campo" que ya
// usa el registro público (reglaNombre, reglaApellido, reglaTelefono,
// reglaCorreo, reglaFechaNac y mostrarErrorCampo/mostrarModalAlerta vienen
// de validarReg.js).
//
// Documento es la única regla que no se reutiliza tal cual: la de
// validarReg.js busca un <select id="tipodoc"> fijo, pero aquí hay dos
// formularios (crear y editar) con selects distintos ("tipodoc" y
// "edit-tipodoc"), así que se arma la validación de longitud aquí mismo
// según cuál <select aria-label="Campo"> le corresponda a cada formulario.
function reglaDocumentoAdmin(valor, selectTipo) {
    if (valor === "") return "El documento es obligatorio";
    if (isNaN(valor)) return "El documento debe ser numérico";

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

// Arma y valida los campos de un formulario (creación o edición) según
// el prefijo de sus ids: "" para "Registrar nuevo usuario" y "edit-"
// para el modal "Editar usuario".
function validarFormularioUsuarioAdmin(prefijo) {
    function campo(id) { return document.getElementById(prefijo + id); }

    const selectTipo = campo("tipodoc");
    const campoClave = campo("clave");
    // En edición la contraseña es opcional (en blanco = se conserva la
    // actual); en creación es obligatoria. Por eso solo se valida si
    // viene algo escrito, o si es el formulario de creación.
    const claveEsObligatoria = (prefijo === "");

    const campos = [
        { input: campo("nombre"), error: campo("errorNombre"), regla: reglaNombre },
        { input: campo("apellido"), error: campo("errorApellido"), regla: reglaApellido },
        { input: campo("documento"), error: campo("errorDocumento"), regla: (v) => reglaDocumentoAdmin(v, selectTipo) },
        { input: campo("telefono"), error: campo("errorTelefono"), regla: reglaTelefono },
        { input: campo("correo"), error: campo("errorCorreo"), regla: reglaCorreo },
        { input: campo("fecha_nac"), error: campo("errorFechaNac"), regla: reglaFechaNac }
    ];
    if (claveEsObligatoria || (campoClave && campoClave.value.trim() !== "")) {
        campos.push({ input: campoClave, error: campo("errorClave"), regla: reglaClave });
    }

    let primerError = null;
    campos.forEach(function (c) {
        if (!c.input) return;
        const mensaje = c.regla(c.input.value.trim());
        if (mensaje) {
            mostrarErrorCampo(c.input, c.error, mensaje);
            if (!primerError) primerError = { input: c.input, mensaje: mensaje };
        } else {
            limpiarErrorCampo(c.input, c.error);
        }
    });

    if (primerError) {
        primerError.input.focus();
        mostrarModalAlerta(primerError.mensaje);
        return false;
    }
    return true;
}

// Si cambia el tipo de documento, se vuelve a chequear la longitud del
// documento ya escrito (igual que en Registrarse.jsp): elegir "Cédula de
// Ciudadanía" después de escribir un documento largo debe avisar de una
// vez, no solo hasta que se intente enviar el formulario.
[["", ""], ["edit-", "edit-"]].forEach(function (par) {
    const prefijo = par[0];
    const selectTipo = document.getElementById(prefijo + "tipodoc");
    const campoDocumento = document.getElementById(prefijo + "documento");
    const errorDocumento = document.getElementById(prefijo + "errorDocumento");
    if (selectTipo && campoDocumento) {
        selectTipo.addEventListener("change", function () {
            if (campoDocumento.value.trim() === "") return;
            const mensaje = reglaDocumentoAdmin(campoDocumento.value.trim(), selectTipo);
            if (mensaje) {
                mostrarErrorCampo(campoDocumento, errorDocumento, mensaje);
            } else {
                limpiarErrorCampo(campoDocumento, errorDocumento);
            }
        });
    }
});
</script>
</body>
</html>
