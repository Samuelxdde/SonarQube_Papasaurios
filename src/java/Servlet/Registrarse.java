package Servlet;
import Controlador.Tipo_documentoDAO;
import Controlador.UsuariosDAO;
import Modelo.Tipo_documento;
import Modelo.Usuarios;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "Registrarse", urlPatterns = {"/Registrarse"})
public class Registrarse extends HttpServlet {

    // El formulario público de registro SOLO puede crear clientes.
    // Antes se leía el rol directamente de un parámetro del formulario
    // (Roles_idRoles = Integer.parseInt(request.getParameter("rola"))),
    // lo que dejaba que cualquier visitante se registrara como
    // Administrador o Repartidor con solo cambiar el <option> en el
    // HTML. Ahora el rol se fija en el servidor y no depende de nada
    // que venga del navegador. Las cuentas de Admin/Repartidor solo
    // las puede crear un administrador ya logueado desde /Usuario
    // (UsuariosAdmi), que está protegido por el filtro de roles.
    private static final int ROL_USUARIO = 2;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // formulario
        String nombre = request.getParameter("nombrep");
        String apellido = request.getParameter("apellidoa");
        String documento = request.getParameter("documentoa");
        System.out.println("Documento recibido: " + documento);
        String telefono = request.getParameter("telefonoi");
        String correo = request.getParameter("correoz");
        String clave = request.getParameter("clavev");
        int Tipo_documento_idTipo_documento = Integer.parseInt(request.getParameter("tipodocs"));
        String fechaNacParam = request.getParameter("fecha_nac");

        // Valida la fecha de nacimiento antes de convertirla (antes se
        // llamaba directo a Date.valueOf con lo que llegara del
        // formulario: un año de 5+ dígitos, ej. "10000-01-01", hacía que
        // esa conversión lanzara una excepción sin controlar y el
        // usuario veía un error 500 crudo del servidor).
        String errorFechaNac = Controlador.FechaNacimientoValidator.validar(fechaNacParam);

        // Crear objeto 
        Usuarios usuario = new Usuarios();
        usuario.setnombre(nombre);
        usuario.setapellido(apellido);
        usuario.setdocumento(documento);
        usuario.settelefono(telefono);
        usuario.setcorreo(correo);
        if (errorFechaNac == null) {
            usuario.setfecha_nac(Date.valueOf(fechaNacParam));
        }
        usuario.setclave(Controlador.PasswordUtil.hash(clave));
        usuario.setTipo_documento_idTipo_documento(Tipo_documento_idTipo_documento);
        usuario.setRoles_idRoles(ROL_USUARIO);
        
        LocalDate fechaCad = LocalDate.now().plusYears(1);
        usuario.setfecha_cad(Date.valueOf(fechaCad));
        
        usuario.setcheckbox(request.getParameter("checkbox") != null);
        
        // DAO
        UsuariosDAO usuariosDao = new UsuariosDAO();
        Tipo_documentoDAO tipoDocDaoValidacion = new Tipo_documentoDAO();

        // Valida que el documento tenga una longitud válida para el tipo
        // seleccionado (antes se podía elegir "Cédula de Ciudadanía" e
        // ingresar cualquier cantidad de dígitos sin ningún control).
        String errorLongitud;
        try {
            Modelo.Tipo_documento tipoSeleccionado = tipoDocDaoValidacion.ConsultarTipo_documento(Tipo_documento_idTipo_documento);
            errorLongitud = Controlador.DocumentoValidator.validar(
                    tipoSeleccionado != null ? tipoSeleccionado.getdescripcion_doc() : null, documento);
        } catch (java.sql.SQLException e) {
            errorLongitud = "Error al validar el tipo de documento: " + e.getMessage();
        }

        // Valida la estructura del correo (antes solo se comprobaba que no
        // viniera vacío; el navegador podía validar el formato, pero el
        // servidor lo aceptaba tal cual llegara si esa validación se
        // saltaba).
        String errorCorreo = Controlador.EmailValidator.validar(correo);

        // Valida que nombre y apellido contengan solo letras (antes no se
        // comprobaba el contenido: se podía registrar un usuario con
        // nombre "12345" sin ningún aviso, ni en el navegador ni aquí).
        String errorNombre = Controlador.NombreValidator.validar(nombre, "nombre");
        String errorApellido = Controlador.NombreValidator.validar(apellido, "apellido");

        // Valida que el teléfono solo contenga números (antes se aceptaba
        // cualquier texto: el formulario y el servidor solo comprobaban
        // que el campo no viniera vacío).
        String errorTelefono = Controlador.TelefonoValidator.validar(telefono);

        // Exige que el correo haya pasado por el paso de verificación con
        // código (EnviarCodigoRegistro / VerificarCodigoRegistro) antes de
        // crear la cuenta. Esto evita que alguien complete el formulario
        // saltándose el JS del front y registre una cuenta con un correo
        // que no controla.
        String correoVerificado = (String) request.getSession().getAttribute("correoVerificado");
        boolean correoNoVerificado = correo == null
                || correoVerificado == null
                || !correoVerificado.equalsIgnoreCase(correo);

        // Verifica si ya existe el documento o el correo
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
        } else if (usuariosDao.existeUsuario(documento)) {
            request.setAttribute("resultado", "El documento ya está registrado");
        } else if (usuariosDao.existeCorreo(correo, null)) {
            request.setAttribute("resultado", "Ese correo ya está registrado");
        } else if (correoNoVerificado) {
            request.setAttribute("resultado", "Debes verificar tu correo con el código antes de registrarte.");
        } else {
            // Registra el usuario
            boolean resultado = false;
            try {
                resultado = usuariosDao.insertarUsuarios(usuario);
                if (resultado) {
                    // Antes esto solo recargaba el mismo formulario con un
                    // mensaje ("Usuario registrado exitosamente") y la
                    // persona se quedaba ahí sin saber qué hacer. Ahora se
                    // manda directo a iniciar sesión, con el mensaje viajando
                    // en la sesión (no en la URL) para mostrarlo una sola vez
                    // en la pantalla de login.
                    request.getSession().setAttribute("mensajeRegistro",
                            "Cuenta creada correctamente. Ahora inicia sesión.");
                    request.getSession().removeAttribute("correoVerificado");
                    response.sendRedirect(request.getContextPath() + "/Iniciar");
                    return;
                } else {
                    request.setAttribute("resultado", "Error al registrar usuario");
                }
            } catch (Exception e) {
                request.setAttribute("resultado", "Error: " + e.getMessage());
            }
        }
        
        // Carga combos y va al JSP
        Tipo_documentoDAO tipoDAO = new Tipo_documentoDAO();
        request.setAttribute("tiposDoc", tipoDAO.listarTipoDocumento());
        request.getRequestDispatcher("/Vista/Registrarse.jsp").forward(request, response);
    }
}
