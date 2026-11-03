package Servlet;

import Controlador.UsuariosDAO;
import Controlador.JsonUtil;
import Controlador.PasswordUtil;
import Controlador.CategoriaDAO;
import Controlador.ProductoDAO;
import Controlador.Producto_varianteDAO;
import Controlador.PedidoDAO;
import Controlador.Detalle_pedidoDAO;
import Controlador.Estado_pedidoDAO;
import Controlador.UbicacionDAO;
import Modelo.Usuarios;
import Modelo.Categoria;
import Modelo.Producto;
import Modelo.Producto_variante;
import Modelo.Pedido;
import Modelo.Detalle_pedido;
import Modelo.Estado_pedido;
import Modelo.Ubicacion;
import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Único servlet ("Front Controller") para toda la API que consume la
 * app Flutter (web y APK). Reemplaza a los 5 servlets que existían
 * antes (LoginApiServlet, RegistroApiServlet, ProductosApiServlet,
 * CategoriasApiServlet, PedidosApiServlet): la lógica de cada uno
 * ahora vive en un método privado de esta clase, y el enrutamiento
 * que antes hacía el contenedor (Tomcat) a partir de 5 anotaciones
 * @WebServlet distintas, ahora lo hace este servlet a mano leyendo
 * req.getPathInfo() (lo que viene después de "/api").
 *
 * Rutas soportadas:
 *   POST /api/login      -> igual que LoginApiServlet
 *   POST /api/registro   -> igual que RegistroApiServlet
 *   GET  /api/productos  -> igual que ProductosApiServlet
 *   GET  /api/categorias -> igual que CategoriasApiServlet
 *   GET  /api/pedidos?idUsuario=123 -> igual que PedidosApiServlet (listar)
 *   POST /api/pedidos    -> igual que PedidosApiServlet (confirmar pedido)
 *   GET  /api/pedidos-repartidor?idRepartidor=N -> pool de pedidos "Listo" sin
 *        asignar + los que ya tomó ese repartidor (para la app del repartidor)
 *   POST /api/pedidos-tomar     -> Body: idRepartidor, idPedido (tomar un pedido)
 *   POST /api/pedidos-entregar  -> Body: idRepartidor, idPedido (marcar entregado)
 *   POST /api/pedidos-cancelar  -> Body: idRepartidor, idPedido (marcar cancelado)
 *   POST /api/ubicacion  -> igual que UbicacionServlet.doPost (reportar posición del repartidor)
 *   GET  /api/ubicacion?idPedido=N&idUsuario=N -> igual que UbicacionServlet.doGet (consultar posición)
 *
 * El Filtro.java ya deja pasar todo lo que empiece por /api/ sin exigir
 * sesión de navegador y agrega los headers CORS, así que este servlet
 * no necesita preocuparse por ninguna de esas dos cosas. Por eso, a
 * diferencia del UbicacionServlet original (que validaba con
 * HttpSession), aquí la identidad de quien llama se recibe explícita
 * como parámetro "idUsuario" en cada petición, igual que hace
 * postPedidos()/getPedidos() más abajo.
 *
 * NOTA: los demás servlets de esta carpeta (CarritoServlet, PanelAdmin,
 * PanelCocina, PanelRepartidor, PanelUsuario, CategoriaAdmi, etc.) son
 * parte del panel web administrativo: dependen de HttpSession y hacen
 * forward a JSP, así que intencionalmente NO se fusionaron aquí — esta
 * clase solo agrupa la lógica de negocio que consume la app móvil.
 */
@WebServlet("/api/*")
public class MobileApiServlet extends HttpServlet {

    // ---- Reglas de negocio (idénticas a los servlets originales) ----
    private static final int ROL_USUARIO = 2;
    private static final int TIPO_DOC_POR_DEFECTO = 1; // Cédula de Ciudadanía
    private static final int ESTADO_RECIBIDO = 1;
    private static final int PAGO_PENDIENTE = 1;
    private static final int TOTAL_MINIMO = 15000;
    private static final java.util.Set<Integer> CATEGORIAS_PLATO_PRINCIPAL =
            java.util.Set.of(1, 5, 6, 7, 8, 9, 10, 11, 12, 13);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        String ruta = request.getPathInfo(); // ej: "/productos", "/pedidos"

        if (ruta == null) {
            responderError(response, HttpServletResponse.SC_NOT_FOUND, "Recurso no encontrado.");
            return;
        }

        switch (ruta) {
            case "/productos":
                getProductos(request, response);
                break;
            case "/categorias":
                getCategorias(request, response);
                break;
            case "/pedidos":
                getPedidos(request, response);
                break;
            case "/pedidos-repartidor":
                getPedidosRepartidor(request, response);
                break;
            case "/ubicacion":
                getUbicacion(request, response);
                break;
            default:
                responderError(response, HttpServletResponse.SC_NOT_FOUND, "Recurso no encontrado.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        String ruta = request.getPathInfo();

        if (ruta == null) {
            responderError(response, HttpServletResponse.SC_NOT_FOUND, "Recurso no encontrado.");
            return;
        }

        switch (ruta) {
            case "/login":
                postLogin(request, response);
                break;
            case "/registro":
                postRegistro(request, response);
                break;
            case "/pedidos":
                postPedidos(request, response);
                break;
            case "/pedidos-tomar":
                postPedidosTomar(request, response);
                break;
            case "/pedidos-entregar":
                postPedidosEntregar(request, response);
                break;
            case "/pedidos-cancelar":
                postPedidosCancelar(request, response);
                break;
            case "/ubicacion":
                postUbicacion(request, response);
                break;
            default:
                responderError(response, HttpServletResponse.SC_NOT_FOUND, "Recurso no encontrado.");
        }
    }

    // =========================================================
    // POST /api/login  (antes: LoginApiServlet)
    // Body (application/x-www-form-urlencoded): documento=...&clave=...
    //
    // 200 -> {"ok":true,"usuario":{...}}
    // 401 -> {"ok":false,"mensaje":"El documento no existe"} | "Clave incorrecta"
    // =========================================================
    private void postLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String documento = request.getParameter("documento");
        String clave = request.getParameter("clave");

        if (documento == null || documento.trim().isEmpty() || clave == null || clave.isEmpty()) {
            responderError(response, HttpServletResponse.SC_BAD_REQUEST, "Documento y clave son obligatorios.");
            return;
        }

        try {
            UsuariosDAO dao = new UsuariosDAO();
            Usuarios usuarioBD = dao.ConsultaUsuarios(documento);

            if (usuarioBD == null) {
                responderError(response, HttpServletResponse.SC_UNAUTHORIZED, "El documento no existe");
                return;
            }

            if (!PasswordUtil.verificar(clave, usuarioBD.getclave())) {
                responderError(response, HttpServletResponse.SC_UNAUTHORIZED, "Clave incorrecta");
                return;
            }

            // Misma migración silenciosa de claves viejas en texto plano
            // que hace InicioSesion.java.
            if (!PasswordUtil.esHash(usuarioBD.getclave())) {
                usuarioBD.setclave(PasswordUtil.hash(clave));
                try {
                    dao.actualizarClave(usuarioBD);
                } catch (java.sql.SQLException ex) {
                    System.err.println("No se pudo migrar la contraseña a hash: " + ex.getMessage());
                }
            }

            String usuarioJson = JsonUtil.objeto(
                    "idUsuarios", usuarioBD.getidUsuarios(),
                    "nombre", usuarioBD.getnombre(),
                    "apellido", usuarioBD.getapellido(),
                    "correo", usuarioBD.getcorreo(),
                    "rolId", usuarioBD.getRoles_idRoles()
            );
            response.getWriter().write(JsonUtil.objeto("ok", true, "usuario", "@raw:" + usuarioJson));

        } catch (Exception e) {
            responderError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error del servidor: " + e.getMessage());
        }
    }

    // =========================================================
    // POST /api/registro  (antes: RegistroApiServlet)
    // Body: nombre, apellido, documento, telefono, correo, clave,
    // fechaNacimiento (yyyy-MM-dd), tipoDocumentoId (opcional).
    //
    // El rol se fija en el servidor (ROL_USUARIO) y jamás se lee de la
    // petición — no hay forma de registrarse como administrador desde
    // este endpoint.
    //
    // 201 -> {"ok":true}
    // 409 -> {"ok":false,"mensaje":"El documento ya está registrado"} | "Ese correo ya está registrado"
    // =========================================================
    private void postRegistro(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String nombre = request.getParameter("nombre");
        String apellido = request.getParameter("apellido");
        String documento = request.getParameter("documento");
        String telefono = request.getParameter("telefono");
        String correo = request.getParameter("correo");
        String clave = request.getParameter("clave");
        String fechaNacimiento = request.getParameter("fechaNacimiento");
        String tipoDocParam = request.getParameter("tipoDocumentoId");

        if (esVacio(nombre) || esVacio(apellido) || esVacio(documento) || esVacio(telefono)
                || esVacio(correo) || esVacio(clave) || esVacio(fechaNacimiento)) {
            responderError(response, HttpServletResponse.SC_BAD_REQUEST, "Todos los campos son obligatorios.");
            return;
        }

        LocalDate fechaNacValidada;
        try {
            fechaNacValidada = parseFechaNacimiento(fechaNacimiento);
        } catch (IllegalArgumentException e) {
            responderError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            return;
        }

        int tipoDocumentoId;
        try {
            tipoDocumentoId = (tipoDocParam != null && !tipoDocParam.isEmpty())
                    ? Integer.parseInt(tipoDocParam)
                    : TIPO_DOC_POR_DEFECTO;
        } catch (NumberFormatException e) {
            tipoDocumentoId = TIPO_DOC_POR_DEFECTO;
        }

        try {
            UsuariosDAO dao = new UsuariosDAO();

            // Antes este endpoint no controlaba la longitud del documento
            // según el tipo (se podía mandar "tipoDocumentoId" de Cédula de
            // Ciudadanía con cualquier cantidad de dígitos).
            Controlador.Tipo_documentoDAO tipoDocDao = new Controlador.Tipo_documentoDAO();
            Modelo.Tipo_documento tipoSeleccionado = tipoDocDao.ConsultarTipo_documento(tipoDocumentoId);
            String errorLongitud = Controlador.DocumentoValidator.validar(
                    tipoSeleccionado != null ? tipoSeleccionado.getdescripcion_doc() : null, documento);
            if (errorLongitud != null) {
                responderError(response, HttpServletResponse.SC_BAD_REQUEST, errorLongitud);
                return;
            }

            // Este endpoint solo comprobaba que el teléfono no viniera
            // vacío (ver esVacio(...) arriba): no validaba el formato,
            // igual que le pasaba al formulario de registro web.
            String errorTelefono = Controlador.TelefonoValidator.validar(telefono);
            if (errorTelefono != null) {
                responderError(response, HttpServletResponse.SC_BAD_REQUEST, errorTelefono);
                return;
            }

            if (dao.existeUsuario(documento)) {
                responderError(response, HttpServletResponse.SC_CONFLICT, "El documento ya está registrado");
                return;
            }
            if (dao.existeCorreo(correo, null)) {
                responderError(response, HttpServletResponse.SC_CONFLICT, "Ese correo ya está registrado");
                return;
            }

            Usuarios usuario = new Usuarios();
            usuario.setnombre(nombre);
            usuario.setapellido(apellido);
            usuario.setdocumento(documento);
            usuario.settelefono(telefono);
            usuario.setcorreo(correo);
            usuario.setfecha_nac(Date.valueOf(fechaNacValidada));
            usuario.setclave(PasswordUtil.hash(clave));
            usuario.setTipo_documento_idTipo_documento(tipoDocumentoId);
            usuario.setRoles_idRoles(ROL_USUARIO);
            usuario.setfecha_cad(Date.valueOf(LocalDate.now().plusYears(1)));
            usuario.setcheckbox(true);

            boolean creado = dao.insertarUsuarios(usuario);
            if (creado) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                response.getWriter().write(JsonUtil.objeto("ok", true));
            } else {
                responderError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "No se pudo registrar el usuario.");
            }
        } catch (Exception e) {
            responderError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error del servidor: " + e.getMessage());
        }
    }

    // =========================================================
    // GET /api/productos  (antes: ProductosApiServlet)
    // Lista de productos, cada uno con sus variantes anidadas (si tiene).
    // =========================================================
    private void getProductos(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            List<Producto> productos = new ProductoDAO().listarProductos();

            // Se traen TODAS las variantes de una sola vez y se agrupan en
            // memoria por producto, en vez de hacer una consulta por cada
            // producto (evita ~70 idas y vueltas a la base de datos).
            List<Producto_variante> todasLasVariantes = new Producto_varianteDAO().listarVariantes();
            Map<Integer, List<Producto_variante>> variantesPorProducto = new HashMap<>();
            for (Producto_variante v : todasLasVariantes) {
                variantesPorProducto
                        .computeIfAbsent(v.getProducto_idProducto(), k -> new ArrayList<>())
                        .add(v);
            }

            List<String> objetos = new ArrayList<>();
            for (Producto p : productos) {
                List<Producto_variante> variantes = variantesPorProducto.getOrDefault(p.getidProducto(), List.of());
                List<String> variantesJson = new ArrayList<>();
                for (Producto_variante v : variantes) {
                    variantesJson.add(JsonUtil.objeto(
                            "idVariante", v.getidVariante(),
                            "nombre", v.getnombre_variante(),
                            "precio", v.getprecio_variante()
                    ));
                }

                objetos.add(JsonUtil.objeto(
                        "idProducto", p.getidProducto(),
                        "nombre", p.getnombre_producto(),
                        "descripcion", p.getdescripcion_producto(),
                        "precioBase", p.getprecio_base(),
                        "disponible", p.isdisponible(),
                        "categoriaId", p.getCategoria_idCategoria(),
                        "imagenUrl", p.getimagen_url(),
                        "variantes", "@raw:" + JsonUtil.arregloDeObjetos(variantesJson)
                ));
            }
            response.getWriter().write(JsonUtil.arregloDeObjetos(objetos));
        } catch (Exception e) {
            responderError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "No se pudo cargar el menú.");
        }
    }

    // =========================================================
    // GET /api/categorias  (antes: CategoriasApiServlet)
    // =========================================================
    private void getCategorias(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            List<Categoria> categorias = new CategoriaDAO().listarCategorias();
            List<String> objetos = new ArrayList<>();
            for (Categoria c : categorias) {
                objetos.add(JsonUtil.objeto(
                        "idCategoria", c.getidCategoria(),
                        "nombre", c.getnombre_categoria(),
                        "orden", c.getorden()
                ));
            }
            response.getWriter().write(JsonUtil.arregloDeObjetos(objetos));
        } catch (Exception e) {
            responderError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "No se pudieron cargar las categorías.");
        }
    }

    // =========================================================
    // GET /api/pedidos?idUsuario=123  (antes: PedidosApiServlet.doGet)
    // =========================================================
    private void getPedidos(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idUsuarioParam = request.getParameter("idUsuario");
        if (idUsuarioParam == null) {
            responderError(response, HttpServletResponse.SC_BAD_REQUEST, "Falta el parámetro idUsuario.");
            return;
        }

        try {
            int idUsuario = Integer.parseInt(idUsuarioParam);
            List<Pedido> pedidos = new PedidoDAO().listarPorUsuario(idUsuario);

            Map<Integer, String> nombresEstado = new HashMap<>();
            for (Estado_pedido e : new Estado_pedidoDAO().listarEstados()) {
                nombresEstado.put(e.getidEstado_pedido(), e.getdescripcion_esta());
            }

            SimpleDateFormat fmtFecha = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat fmtHora = new SimpleDateFormat("HH:mm");

            List<String> objetos = new ArrayList<>();
            // Más reciente primero, para que la app no tenga que ordenarlos.
            for (int i = pedidos.size() - 1; i >= 0; i--) {
                Pedido p = pedidos.get(i);
                objetos.add(JsonUtil.objeto(
                        "idPedido", p.getidPedido(),
                        "fecha", fmtFecha.format(p.getfecha()),
                        "hora", fmtHora.format(p.gethora()),
                        "tipoEntrega", p.gettipo_entrega(),
                        "direccionEntrega", p.getdireccion_entrega(),
                        "total", p.gettotal(),
                        "estadoId", p.getEstado_pedido_idEstado_pedido(),
                        "estadoDescripcion", nombresEstado.getOrDefault(p.getEstado_pedido_idEstado_pedido(), "Desconocido")
                ));
            }
            response.getWriter().write(JsonUtil.arregloDeObjetos(objetos));
        } catch (NumberFormatException e) {
            responderError(response, HttpServletResponse.SC_BAD_REQUEST, "idUsuario inválido.");
        } catch (Exception e) {
            responderError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "No se pudieron cargar tus pedidos.");
        }
    }

    // =========================================================
    // GET /api/pedidos-repartidor?idRepartidor=N
    //
    // Para la app del repartidor. Devuelve dos listas en un solo viaje:
    //  - "disponibles": pedidos a domicilio ya "Listo" en cocina y sin
    //    repartidor asignado todavía (el pool para "tomar").
    //  - "mios": los que este repartidor ya tomó, con los más recientes
    //    primero (incluye tanto los que sigue llevando como su
    //    historial de entregados/cancelados, hasta 30).
    //
    // Igual que en PanelRepartidor.jsp, se incluye el nombre y teléfono
    // del cliente en cada pedido: sin eso el repartidor no tiene cómo
    // ubicarlo ni llamarlo si no encuentra la dirección.
    // =========================================================
    private void getPedidosRepartidor(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idRepartidorParam = request.getParameter("idRepartidor");
        if (idRepartidorParam == null) {
            responderError(response, HttpServletResponse.SC_BAD_REQUEST, "Falta el parámetro idRepartidor.");
            return;
        }

        try {
            int idRepartidor = Integer.parseInt(idRepartidorParam);

            // Igual que hace PanelRepartidorServlet con el panel web: cada
            // vez que la app consulta sus pedidos (esto pasa cada 15s, ver
            // repartidor_screen.dart), se marca al repartidor como activo
            // para que el panel de cocina lo vea trabajando "ahora mismo".
            // Antes esto solo pasaba desde el panel web, así que un
            // repartidor que solo usara la app nunca aparecía ahí.
            Controlador.RepartidoresActivosRegistro.marcarActivo(idRepartidor);

            PedidoDAO pedidoDAO = new PedidoDAO();
            UsuariosDAO usuariosDAO = new UsuariosDAO();
            Map<Integer, Usuarios> cacheClientes = new HashMap<>();

            List<Pedido> disponibles = new ArrayList<>();
            for (Pedido p : pedidoDAO.listarPorTipoEntrega("Domicilio")) {
                if (p.getEstado_pedido_idEstado_pedido() == 3 && p.getRepartidor_idUsuarios() == null) {
                    disponibles.add(p);
                }
            }

            List<Pedido> mios = pedidoDAO.listarPorRepartidor(idRepartidor);
            if (mios.size() > 30) {
                mios = mios.subList(0, 30);
            }

            Map<Integer, String> nombresEstado = new HashMap<>();
            for (Estado_pedido e : new Estado_pedidoDAO().listarEstados()) {
                nombresEstado.put(e.getidEstado_pedido(), e.getdescripcion_esta());
            }

            SimpleDateFormat fmtFecha = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat fmtHora = new SimpleDateFormat("HH:mm");

            response.getWriter().write(JsonUtil.objeto(
                    "disponibles", "@raw:" + JsonUtil.arregloDeObjetos(
                            objetosPedidoRepartidor(disponibles, cacheClientes, usuariosDAO, nombresEstado, fmtFecha, fmtHora)),
                    "mios", "@raw:" + JsonUtil.arregloDeObjetos(
                            objetosPedidoRepartidor(mios, cacheClientes, usuariosDAO, nombresEstado, fmtFecha, fmtHora))
            ));
        } catch (NumberFormatException e) {
            responderError(response, HttpServletResponse.SC_BAD_REQUEST, "idRepartidor inválido.");
        } catch (Exception e) {
            responderError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "No se pudieron cargar los pedidos.");
        }
    }

    private List<String> objetosPedidoRepartidor(List<Pedido> pedidos, Map<Integer, Usuarios> cacheClientes,
            UsuariosDAO usuariosDAO, Map<Integer, String> nombresEstado,
            SimpleDateFormat fmtFecha, SimpleDateFormat fmtHora) {

        List<String> objetos = new ArrayList<>();
        for (Pedido p : pedidos) {
            Usuarios cliente = cacheClientes.computeIfAbsent(p.getUsuarios_idUsuarios(), usuariosDAO::consultarPorId);
            objetos.add(JsonUtil.objeto(
                    "idPedido", p.getidPedido(),
                    "fecha", fmtFecha.format(p.getfecha()),
                    "hora", fmtHora.format(p.gethora()),
                    "direccionEntrega", p.getdireccion_entrega(),
                    "total", p.gettotal(),
                    "estadoId", p.getEstado_pedido_idEstado_pedido(),
                    "estadoDescripcion", nombresEstado.getOrDefault(p.getEstado_pedido_idEstado_pedido(), "Desconocido"),
                    "clienteNombre", cliente != null ? (cliente.getnombre() + " " + cliente.getapellido()).trim() : "Cliente",
                    "clienteTelefono", cliente != null ? cliente.gettelefono() : ""
            ));
        }
        return objetos;
    }

    // =========================================================
    // POST /api/pedidos-tomar   Body: idRepartidor, idPedido
    //
    // Mismas reglas que la acción "tomar" de PanelRepartidorServlet:
    // solo se puede tomar un pedido a domicilio que ya esté "Listo" y
    // sin repartidor asignado. asignarRepartidor() ya protege contra
    // que dos repartidores tomen el mismo pedido a la vez.
    // =========================================================
    private void postPedidosTomar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int idRepartidor = Integer.parseInt(request.getParameter("idRepartidor"));
            int idPedido = Integer.parseInt(request.getParameter("idPedido"));

            PedidoDAO pedidoDAO = new PedidoDAO();
            Pedido pedido = pedidoDAO.consultarPorId(idPedido);

            if (pedido == null || !"Domicilio".equalsIgnoreCase(pedido.gettipo_entrega())) {
                responderError(response, HttpServletResponse.SC_BAD_REQUEST, "Ese pedido no existe o no es a domicilio.");
            } else if (pedido.getEstado_pedido_idEstado_pedido() != 3) {
                responderError(response, HttpServletResponse.SC_CONFLICT, "Solo puedes tomar un pedido que ya esté \"Listo\" en cocina.");
            } else if (pedido.getRepartidor_idUsuarios() != null) {
                responderError(response, HttpServletResponse.SC_CONFLICT, "Ese pedido ya lo tomó otro repartidor.");
            } else if (pedidoDAO.asignarRepartidor(idPedido, idRepartidor)) {
                response.getWriter().write(JsonUtil.objeto("ok", true));
            } else {
                responderError(response, HttpServletResponse.SC_CONFLICT, "No se pudo tomar el pedido (puede que alguien más se te haya adelantado).");
            }
        } catch (NumberFormatException e) {
            responderError(response, HttpServletResponse.SC_BAD_REQUEST, "Datos inválidos.");
        } catch (Exception e) {
            responderError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error del servidor: " + e.getMessage());
        }
    }

    // =========================================================
    // POST /api/pedidos-entregar   Body: idRepartidor, idPedido
    // =========================================================
    private void postPedidosEntregar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        marcarPedidoRepartidor(request, response, 4); // 4 = Entregado
    }

    // =========================================================
    // POST /api/pedidos-cancelar   Body: idRepartidor, idPedido
    // =========================================================
    private void postPedidosCancelar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        marcarPedidoRepartidor(request, response, 5); // 5 = Cancelado
    }

    private void marcarPedidoRepartidor(HttpServletRequest request, HttpServletResponse response, int nuevoEstado) throws IOException {
        try {
            int idRepartidor = Integer.parseInt(request.getParameter("idRepartidor"));
            int idPedido = Integer.parseInt(request.getParameter("idPedido"));

            PedidoDAO pedidoDAO = new PedidoDAO();
            Pedido pedido = pedidoDAO.consultarPorId(idPedido);

            if (pedido == null || pedido.getRepartidor_idUsuarios() == null || pedido.getRepartidor_idUsuarios() != idRepartidor) {
                responderError(response, HttpServletResponse.SC_FORBIDDEN, "Este pedido no está asignado a tu usuario.");
                return;
            }
            if (nuevoEstado == 4 && pedido.getEstado_pedido_idEstado_pedido() != 3) {
                responderError(response, HttpServletResponse.SC_CONFLICT, "Solo puedes marcar como entregado un pedido que ya esté \"Listo\".");
                return;
            }

            pedidoDAO.actualizarEstado(idPedido, nuevoEstado);
            new UbicacionDAO().eliminarUbicacion(idPedido); // ya no hay nada que rastrear
            response.getWriter().write(JsonUtil.objeto("ok", true));
        } catch (NumberFormatException e) {
            responderError(response, HttpServletResponse.SC_BAD_REQUEST, "Datos inválidos.");
        } catch (Exception e) {
            responderError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error del servidor: " + e.getMessage());
        }
    }

    // =========================================================
    // POST /api/pedidos  (antes: PedidosApiServlet.doPost)
    //
    // Reutiliza EXACTAMENTE las mismas reglas de negocio que
    // PedidoServlet.java (pedido mínimo $15.000, al menos un plato
    // principal, dirección obligatoria para domicilio).
    //
    // IMPORTANTE — límite conocido: a diferencia de la web (que valida
    // contra la sesión del servidor), aquí se confía en el idUsuario
    // que manda la app porque todavía no hay un token de autenticación
    // por petición. Mejora pendiente: agregar un token de sesión (JWT
    // o similar) que se valide en cada endpoint, en vez de solo enviar
    // el id.
    // =========================================================
    private void postPedidos(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int idUsuario = Integer.parseInt(request.getParameter("idUsuario"));
            String tipoEntrega = request.getParameter("tipoEntrega");
            String direccion = request.getParameter("direccionEntrega");
            int cantidadItemsForm = Integer.parseInt(request.getParameter("cantidadItems"));

            if ("Domicilio".equalsIgnoreCase(tipoEntrega) && (direccion == null || direccion.trim().isEmpty())) {
                responderError(response, HttpServletResponse.SC_BAD_REQUEST,
                        "Para un pedido a domicilio necesitamos la dirección de entrega.");
                return;
            }

            // Se reconstruyen las líneas del pedido a partir de los campos
            // indexados item_producto_N / item_variante_N / item_cantidad_N
            // / item_nota_N, y se vuelve a consultar cada producto en la
            // base de datos: nunca se confía en un precio que mande la
            // app, el precio real siempre sale de Producto/Producto_variante
            // en este momento.
            ProductoDAO productoDAO = new ProductoDAO();
            Producto_varianteDAO varianteDAO = new Producto_varianteDAO();

            class LineaPedido {
                int idProducto;
                Integer idVariante;
                int cantidad;
                String nota;
                int precioUnitario;
                int categoriaId;
            }

            List<LineaPedido> lineas = new ArrayList<>();
            int total = 0;
            boolean tienePlatoPrincipal = false;

            for (int i = 0; i < cantidadItemsForm; i++) {
                String idProductoStr = request.getParameter("item_producto_" + i);
                String idVarianteStr = request.getParameter("item_variante_" + i);
                String cantidadStr = request.getParameter("item_cantidad_" + i);
                String nota = request.getParameter("item_nota_" + i);

                if (idProductoStr == null || cantidadStr == null) continue;

                LineaPedido linea = new LineaPedido();
                linea.idProducto = Integer.parseInt(idProductoStr);
                linea.idVariante = (idVarianteStr != null && !idVarianteStr.isEmpty()) ? Integer.parseInt(idVarianteStr) : null;
                linea.cantidad = Integer.parseInt(cantidadStr);
                linea.nota = nota == null ? "" : nota;

                Producto producto = productoDAO.consultarPorId(linea.idProducto);
                if (producto == null) {
                    responderError(response, HttpServletResponse.SC_BAD_REQUEST, "Uno de los productos ya no existe.");
                    return;
                }
                linea.categoriaId = producto.getCategoria_idCategoria();

                if (linea.idVariante != null) {
                    var variante = varianteDAO.consultarPorId(linea.idVariante);
                    linea.precioUnitario = variante != null ? variante.getprecio_variante() : producto.getprecio_base();
                } else {
                    linea.precioUnitario = producto.getprecio_base();
                }

                total += linea.precioUnitario * linea.cantidad;
                if (CATEGORIAS_PLATO_PRINCIPAL.contains(linea.categoriaId)) {
                    tienePlatoPrincipal = true;
                }
                lineas.add(linea);
            }

            if (lineas.isEmpty()) {
                responderError(response, HttpServletResponse.SC_BAD_REQUEST, "Tu carrito está vacío.");
                return;
            }
            if (!tienePlatoPrincipal) {
                responderError(response, HttpServletResponse.SC_BAD_REQUEST,
                        "Tu pedido necesita al menos un plato armado (no solo adicionales sueltos).");
                return;
            }
            if (total < TOTAL_MINIMO) {
                responderError(response, HttpServletResponse.SC_BAD_REQUEST,
                        "El pedido mínimo es de $" + TOTAL_MINIMO + ". Te faltan $" + (TOTAL_MINIMO - total) + ".");
                return;
            }

            int cantidadTotalUnidades = lineas.stream().mapToInt(l -> l.cantidad).sum();
            long ahoraMillis = System.currentTimeMillis();
            int minutosPreparacion = Math.min(15 + (cantidadTotalUnidades * 2), 45);
            int minutosTransporte = "Domicilio".equalsIgnoreCase(tipoEntrega) ? 20 : 0;
            long horaEstimadaMillis = ahoraMillis + (minutosPreparacion + minutosTransporte) * 60_000L;
            Time horaEstimada = new Time(horaEstimadaMillis);

            Pedido pedido = new Pedido();
            pedido.setfecha(new Date(ahoraMillis));
            pedido.sethora(new Time(ahoraMillis));
            pedido.sethora_estimada(horaEstimada);
            pedido.settipo_entrega(tipoEntrega);
            pedido.setdireccion_entrega("Domicilio".equalsIgnoreCase(tipoEntrega) ? direccion : null);
            pedido.settotal(total);
            pedido.setUsuarios_idUsuarios(idUsuario);
            pedido.setEstado_pedido_idEstado_pedido(ESTADO_RECIBIDO);
            pedido.setPagos_idPagos(PAGO_PENDIENTE);

            PedidoDAO pedidoDAO = new PedidoDAO();
            int idPedidoNuevo = pedidoDAO.insertarPedido(pedido);

            if (idPedidoNuevo == -1) {
                responderError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "No se pudo registrar el pedido. Intenta de nuevo.");
                return;
            }

            Detalle_pedidoDAO detalleDAO = new Detalle_pedidoDAO();
            for (LineaPedido linea : lineas) {
                Detalle_pedido detalle = new Detalle_pedido();
                detalle.setcantidad(linea.cantidad);
                detalle.setprecio_unitario(linea.precioUnitario);
                detalle.setnota(linea.nota);
                detalle.setPedido_idPedido(idPedidoNuevo);
                detalle.setProducto_idProducto(linea.idProducto);
                detalle.setProducto_variante_idVariante(linea.idVariante);
                detalleDAO.insertarDetalle(detalle);
            }

            SimpleDateFormat fmtHora = new SimpleDateFormat("HH:mm");
            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().write(JsonUtil.objeto(
                    "ok", true,
                    "idPedido", idPedidoNuevo,
                    "total", total,
                    "horaEstimada", fmtHora.format(horaEstimada)
            ));

        } catch (NumberFormatException e) {
            responderError(response, HttpServletResponse.SC_BAD_REQUEST, "Datos del pedido incompletos o inválidos.");
        } catch (Exception e) {
            responderError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al confirmar el pedido: " + e.getMessage());
        }
    }

    // =========================================================
    // POST /api/ubicacion  (antes: UbicacionServlet.doPost)
    // Body: idUsuario, idPedido, lat, lng
    //
    // Lo llama la app del repartidor cada pocos segundos mientras el
    // pedido va en camino, para guardar/actualizar su posición.
    //
    // 200 -> {"ok":true}
    // 403 -> {"ok":false,"mensaje":"Este pedido no está asignado a tu usuario"}
    // =========================================================
    private void postUbicacion(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int idUsuario = Integer.parseInt(request.getParameter("idUsuario"));
            int idPedido = Integer.parseInt(request.getParameter("idPedido"));
            double lat = Double.parseDouble(request.getParameter("lat"));
            double lng = Double.parseDouble(request.getParameter("lng"));

            PedidoDAO pedidoDAO = new PedidoDAO();
            Pedido pedido = pedidoDAO.consultarPorId(idPedido);

            // Solo el repartidor al que se le asignó ESE pedido puede reportar
            // su posición para él — así nadie puede "mover" la ubicación de
            // una entrega que no le corresponde.
            if (pedido == null || pedido.getRepartidor_idUsuarios() == null
                    || pedido.getRepartidor_idUsuarios() != idUsuario) {
                responderError(response, HttpServletResponse.SC_FORBIDDEN, "Este pedido no está asignado a tu usuario");
                return;
            }

            new UbicacionDAO().guardarUbicacion(idPedido, lat, lng);
            response.getWriter().write(JsonUtil.objeto("ok", true));

        } catch (NumberFormatException e) {
            responderError(response, HttpServletResponse.SC_BAD_REQUEST, "Datos inválidos.");
        } catch (Exception e) {
            responderError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error del servidor: " + e.getMessage());
        }
    }

    // =========================================================
    // GET /api/ubicacion?idPedido=N&idUsuario=N  (antes: UbicacionServlet.doGet)
    //
    // Lo llama (con polling) la app del cliente para dibujar al
    // repartidor en el mapa mientras el pedido está en camino.
    //
    // 200 -> {"disponible":false} | {"disponible":true,"lat":..,"lng":..,"actualizado":".."}
    // 403 -> {"ok":false,"mensaje":"No autorizado"}
    // =========================================================
    private void getUbicacion(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int idPedido = Integer.parseInt(request.getParameter("idPedido"));
            int idUsuario = Integer.parseInt(request.getParameter("idUsuario"));

            PedidoDAO pedidoDAO = new PedidoDAO();
            Pedido pedido = pedidoDAO.consultarPorId(idPedido);

            // Solo puede consultar la ubicación: el cliente dueño del pedido
            // o el repartidor asignado. (El panel admin, que sí puede ver
            // cualquier pedido, sigue usando el UbicacionServlet original
            // con sesión de navegador, no esta ruta de la app móvil.)
            boolean autorizado = pedido != null && (
                    pedido.getUsuarios_idUsuarios() == idUsuario
                    || (pedido.getRepartidor_idUsuarios() != null && pedido.getRepartidor_idUsuarios() == idUsuario)
            );

            if (!autorizado) {
                responderError(response, HttpServletResponse.SC_FORBIDDEN, "No autorizado");
                return;
            }

            Ubicacion u = new UbicacionDAO().obtenerUbicacion(idPedido);
            if (u == null) {
                response.getWriter().write(JsonUtil.objeto("disponible", false));
            } else {
                response.getWriter().write(String.format(Locale.US,
                        "{\"disponible\":true,\"lat\":%f,\"lng\":%f,\"actualizado\":\"%s\"}",
                        u.getlatitud(), u.getlongitud(), u.getactualizado().toString()));
            }

        } catch (NumberFormatException e) {
            responderError(response, HttpServletResponse.SC_BAD_REQUEST, "Petición inválida.");
        } catch (Exception e) {
            responderError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error del servidor: " + e.getMessage());
        }
    }

    // ---- utilidades comunes ----

    private void responderError(HttpServletResponse response, int status, String mensaje) throws IOException {
        response.setStatus(status);
        response.getWriter().write(JsonUtil.objeto("ok", false, "mensaje", mensaje));
    }

    private boolean esVacio(String s) {
        return s == null || s.trim().isEmpty();
    }

    // Valida "yyyy-MM-dd" con año de exactamente 4 dígitos (0001-9999).
    // Antes, un año de 5+ dígitos (ej: 10000-01-01) hacía que
    // Date.valueOf/LocalDate.parse lanzara una excepción no controlada
    // que terminaba como un 500 "Error del servidor". Ahora se detecta
    // aquí y se responde con un 400 y un mensaje claro.
    private static final java.util.regex.Pattern PATRON_FECHA
            = java.util.regex.Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");

    private LocalDate parseFechaNacimiento(String fechaNacimiento) {
        if (!PATRON_FECHA.matcher(fechaNacimiento).matches()) {
            throw new IllegalArgumentException(
                    "Fecha de nacimiento inválida. Usa el formato AAAA-MM-DD con un año de 4 dígitos.");
        }
        try {
            return LocalDate.parse(fechaNacimiento);
        } catch (java.time.format.DateTimeParseException e) {
            throw new IllegalArgumentException("Fecha de nacimiento inválida.");
        }
    }
}
