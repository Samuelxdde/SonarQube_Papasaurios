package Servlet;

import Controlador.CsrfUtil;
import Controlador.UsuariosDAO;
import Controlador.RolesDAO;
import Controlador.Tipo_documentoDAO;
import Modelo.Usuarios;
import java.io.IOException;
import java.sql.Date;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// Ver CategoriaAdmiServlet para la explicación completa del token CSRF.
// Este servlet ya usaba POST para todo (insertar/actualizar/eliminar) y
// "editar" ya se hacía sin navegar (modal por JavaScript), así que solo
// hizo falta sumar la validación del token — no cambió el flujo de URLs.
@WebServlet(name = "Usuario", urlPatterns = {"/Usuario"})
public class UsuarioAdmi extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        UsuariosDAO usuariosDao = new UsuariosDAO();

        if (!CsrfUtil.esValido(request)) {
            request.setAttribute("resultado", "Tu sesión de formulario expiró o no es válida. Intenta de nuevo.");
            cargarListasYMostrar(request, response, usuariosDao);
            return;
        }

        String accion = request.getParameter("accion");
        Usuarios usuario = new Usuarios();
        boolean resultado = false;

        try {
            if ("insertar".equalsIgnoreCase(accion)) {
                usuario.setnombre(request.getParameter("nombre"));
                usuario.setapellido(request.getParameter("apellido"));
                usuario.setdocumento(request.getParameter("documento"));
                usuario.settelefono(request.getParameter("telefono"));
                usuario.setcorreo(request.getParameter("correo"));
                usuario.setclave(Controlador.PasswordUtil.hash(request.getParameter("clave")));
                usuario.setcheckbox(request.getParameter("checkbox") != null);
                usuario.setTipo_documento_idTipo_documento(Integer.parseInt(request.getParameter("Tipo_documento_idTipo_documento")));
                usuario.setRoles_idRoles(Integer.parseInt(request.getParameter("Roles_idRoles")));

                // Igual que en el registro público: antes se llamaba directo
                // a Date.valueOf con lo que llegara del formulario, así que
                // un año de 5+ dígitos terminaba como una excepción sin
                // controlar (error 500) en vez de un mensaje claro.
                String fechaNacParam = request.getParameter("fecha_nac");
                String errorFechaNac = Controlador.FechaNacimientoValidator.validar(fechaNacParam);

                // Antes esta pantalla (a diferencia del registro público) no
                // validaba nada: se podían crear dos usuarios con el mismo
                // documento o el mismo correo sin ningún aviso, ni tampoco
                // se controlaba la longitud del documento según el tipo
                // (se podía elegir "Cédula de Ciudadanía" e ingresar
                // cualquier cantidad de dígitos).
                String errorLongitud = validarLongitudDocumento(usuario);
                // Al igual que en el registro público, esta pantalla solo
                // exigía type="email" en el HTML: el servidor no comprobaba
                // el formato del correo, así que se podía guardar cualquier
                // texto que se colara sin pasar por esa validación del navegador.
                String errorCorreo = Controlador.EmailValidator.validar(usuario.getcorreo());
                // Antes tampoco se validaba que nombre/apellido contuvieran
                // solo letras: se podía crear un usuario con nombre "123"
                // sin ningún aviso.
                String errorNombre = Controlador.NombreValidator.validar(usuario.getnombre(), "nombre");
                String errorApellido = Controlador.NombreValidator.validar(usuario.getapellido(), "apellido");
                // A diferencia del registro público, esta pantalla tampoco
                // validaba el teléfono: se podía guardar cualquier texto.
                String errorTelefono = Controlador.TelefonoValidator.validar(usuario.gettelefono());
                if (errorFechaNac != null) {
                    request.setAttribute("resultado", errorFechaNac);
                } else if (errorLongitud != null) {
                    request.setAttribute("resultado", errorLongitud);
                } else if (errorCorreo != null) {
                    request.setAttribute("resultado", errorCorreo);
                } else if (errorNombre != null) {
                    request.setAttribute("resultado", errorNombre);
                } else if (errorApellido != null) {
                    request.setAttribute("resultado", errorApellido);
                } else if (errorTelefono != null) {
                    request.setAttribute("resultado", errorTelefono);
                } else if (usuariosDao.existeUsuario(usuario.getdocumento(), null)) {
                    request.setAttribute("resultado", "Ya existe un usuario con el documento " + usuario.getdocumento() + ".");
                } else if (usuariosDao.existeCorreo(usuario.getcorreo(), null)) {
                    request.setAttribute("resultado", "Ya existe un usuario con el correo " + usuario.getcorreo() + ".");
                } else {
                    usuario.setfecha_nac(Date.valueOf(fechaNacParam));
                    usuario.setfecha_cad(Date.valueOf(java.time.LocalDate.now().plusYears(1)));
                    resultado = usuariosDao.insertarUsuarios(usuario);
                    request.setAttribute("resultado", resultado ? "Usuario registrado correctamente" : "Error al registrar usuario");
                }

            } else if ("actualizar".equalsIgnoreCase(accion)) {
                int idUsuario = Integer.parseInt(request.getParameter("idUsuario"));
                usuario.setidUsuarios(idUsuario);
                usuario.setnombre(request.getParameter("nombre"));
                usuario.setapellido(request.getParameter("apellido"));
                usuario.setdocumento(request.getParameter("documento"));
                usuario.settelefono(request.getParameter("telefono"));
                usuario.setcorreo(request.getParameter("correo"));
                usuario.setTipo_documento_idTipo_documento(Integer.parseInt(request.getParameter("Tipo_documento_idTipo_documento")));
                String fechaNacParamEdicion = request.getParameter("fecha_nac");

                String errorFechaNacEdicion = Controlador.FechaNacimientoValidator.validar(fechaNacParamEdicion);
                String errorLongitudEdicion = validarLongitudDocumento(usuario);
                String errorCorreoEdicion = Controlador.EmailValidator.validar(usuario.getcorreo());
                String errorNombreEdicion = Controlador.NombreValidator.validar(usuario.getnombre(), "nombre");
                String errorApellidoEdicion = Controlador.NombreValidator.validar(usuario.getapellido(), "apellido");
                String errorTelefonoEdicion = Controlador.TelefonoValidator.validar(usuario.gettelefono());
                if (errorFechaNacEdicion != null) {
                    request.setAttribute("resultado", errorFechaNacEdicion);
                } else if (errorLongitudEdicion != null) {
                    request.setAttribute("resultado", errorLongitudEdicion);
                } else if (errorCorreoEdicion != null) {
                    request.setAttribute("resultado", errorCorreoEdicion);
                } else if (errorNombreEdicion != null) {
                    request.setAttribute("resultado", errorNombreEdicion);
                } else if (errorApellidoEdicion != null) {
                    request.setAttribute("resultado", errorApellidoEdicion);
                } else if (errorTelefonoEdicion != null) {
                    request.setAttribute("resultado", errorTelefonoEdicion);
                } else if (usuariosDao.existeUsuario(usuario.getdocumento(), idUsuario)) {
                    request.setAttribute("resultado", "Ya existe otro usuario con el documento " + usuario.getdocumento() + ".");
                } else if (usuariosDao.existeCorreo(usuario.getcorreo(), idUsuario)) {
                    request.setAttribute("resultado", "Ya existe otro usuario con el correo " + usuario.getcorreo() + ".");
                } else {
                    // Si dejan la clave en blanco al editar, se conserva la actual en vez de borrarla.
                    String claveForm = request.getParameter("clave");
                    Usuarios actual = usuariosDao.consultarPorId(idUsuario);
                    if (claveForm == null || claveForm.isEmpty()) {
                        usuario.setclave(actual != null ? actual.getclave() : claveForm);
                    } else {
                        usuario.setclave(Controlador.PasswordUtil.hash(claveForm));
                    }

                    usuario.setfecha_nac(Date.valueOf(fechaNacParamEdicion));
                    // La fecha de caducidad se mantiene automáticamente (no se edita a mano);
                    // solo se renueva un año más si ya venció.
                    if (actual != null && actual.getfecha_cad() != null
                            && !new java.sql.Date(actual.getfecha_cad().getTime()).toLocalDate().isBefore(java.time.LocalDate.now())) {
                        usuario.setfecha_cad(actual.getfecha_cad());
                    } else {
                        usuario.setfecha_cad(Date.valueOf(java.time.LocalDate.now().plusYears(1)));
                    }
                    usuario.setcheckbox(request.getParameter("checkbox") != null);
                    usuario.setTipo_documento_idTipo_documento(Integer.parseInt(request.getParameter("Tipo_documento_idTipo_documento")));
                    usuario.setRoles_idRoles(Integer.parseInt(request.getParameter("Roles_idRoles")));

                    resultado = usuariosDao.actualizarUsuario(usuario);
                    request.setAttribute("resultado", resultado ? "Usuario actualizado correctamente" : "Error al actualizar usuario");
                }

            } else if ("eliminar".equalsIgnoreCase(accion)) {
                int idUsuario = Integer.parseInt(request.getParameter("idUsuario"));
                resultado = usuariosDao.eliminarUsuario(idUsuario);
                request.setAttribute("resultado", resultado ? "Usuario eliminado correctamente" : "Error al eliminar usuario");
            }
        } catch (Exception e) {
            request.setAttribute("resultado", "Error: " + e.getMessage());
        }

        cargarListasYMostrar(request, response, usuariosDao);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        cargarListasYMostrar(request, response, new UsuariosDAO());
    }

    // Consulta el tipo de documento elegido y valida que el número
    // de documento tenga una longitud válida para ese tipo (p. ej.
    // Cédula de Ciudadanía: 6 a 10 dígitos). Devuelve null si es válido.
    private String validarLongitudDocumento(Usuarios usuario) throws java.sql.SQLException {
        Tipo_documentoDAO tipoDocDao = new Tipo_documentoDAO();
        Modelo.Tipo_documento tipo = tipoDocDao.ConsultarTipo_documento(usuario.getTipo_documento_idTipo_documento());
        return Controlador.DocumentoValidator.validar(
                tipo != null ? tipo.getdescripcion_doc() : null, usuario.getdocumento());
    }

    private void cargarListasYMostrar(HttpServletRequest request, HttpServletResponse response, UsuariosDAO usuariosDao)
            throws ServletException, IOException {
        RolesDAO rolesDao = new RolesDAO();
        Tipo_documentoDAO tipoDocDao = new Tipo_documentoDAO();
        request.setAttribute("listaRoles", rolesDao.listarRoles());
        request.setAttribute("listaTipoDocumento", tipoDocDao.listarTipoDocumento());
        request.setAttribute("listaUsuarios", usuariosDao.listarUsuarios());
        request.getRequestDispatcher("/Vista/UsuariosAdmi.jsp").forward(request, response);
    }
}
