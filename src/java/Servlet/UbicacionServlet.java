package Servlet;

import Controlador.PedidoDAO;
import Controlador.UbicacionDAO;
import Modelo.Pedido;
import Modelo.Ubicacion;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Puente de ubicación en vivo entre el repartidor y el cliente.
 *
 * POST /Ubicacion  — lo llama el navegador del repartidor (geolocalización
 *                    del celular) cada pocos segundos mientras el pedido
 *                    está en camino. Guarda/actualiza la posición.
 * GET  /Ubicacion?id=N — lo llama (con polling) la página de seguimiento
 *                    del cliente para dibujar al repartidor en el mapa.
 *
 * Todo responde en JSON simple, escrito a mano (el proyecto no trae
 * ninguna librería JSON) porque la respuesta es siempre un objeto
 * plano de 3-4 campos numéricos/booleanos.
 */
@WebServlet("/Ubicacion")
public class UbicacionServlet extends HttpServlet {

    private static final int ROL_ADMIN = 1;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json; charset=UTF-8");
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("idUsuarios") == null) {
            enviarError(response, 401, "No autenticado");
            return;
        }

        try {
            int idPedido = Integer.parseInt(request.getParameter("idPedido"));
            double lat = Double.parseDouble(request.getParameter("lat"));
            double lng = Double.parseDouble(request.getParameter("lng"));
            int idUsuarioSesion = (Integer) session.getAttribute("idUsuarios");

            PedidoDAO pedidoDAO = new PedidoDAO();
            Pedido pedido = pedidoDAO.consultarPorId(idPedido);

            // Solo el repartidor al que se le asignó ESE pedido puede reportar
            // su posición para él — así nadie puede "mover" la ubicación de
            // una entrega que no le corresponde.
            if (pedido == null || pedido.getRepartidor_idUsuarios() == null
                    || pedido.getRepartidor_idUsuarios() != idUsuarioSesion) {
                enviarError(response, 403, "Este pedido no está asignado a tu usuario");
                return;
            }

            new UbicacionDAO().guardarUbicacion(idPedido, lat, lng);

            try (PrintWriter out = response.getWriter()) {
                out.print("{\"ok\":true}");
            }
        } catch (Exception e) {
            enviarError(response, 400, "Datos inválidos: " + e.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json; charset=UTF-8");
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("idUsuarios") == null) {
            enviarError(response, 401, "No autenticado");
            return;
        }

        try {
            int idPedido = Integer.parseInt(request.getParameter("id"));
            int idUsuarioSesion = (Integer) session.getAttribute("idUsuarios");
            Object perfilObj = session.getAttribute("perfil");
            boolean esAdmin = perfilObj != null && (Integer) perfilObj == ROL_ADMIN;

            PedidoDAO pedidoDAO = new PedidoDAO();
            Pedido pedido = pedidoDAO.consultarPorId(idPedido);

            // Solo puede consultar la ubicación: el cliente dueño del pedido,
            // el repartidor asignado, o un admin. Nadie más.
            boolean autorizado = pedido != null && (
                    esAdmin
                    || pedido.getUsuarios_idUsuarios() == idUsuarioSesion
                    || (pedido.getRepartidor_idUsuarios() != null && pedido.getRepartidor_idUsuarios() == idUsuarioSesion)
            );

            if (!autorizado) {
                enviarError(response, 403, "No autorizado");
                return;
            }

            Ubicacion u = new UbicacionDAO().obtenerUbicacion(idPedido);

            try (PrintWriter out = response.getWriter()) {
                if (u == null) {
                    out.print("{\"disponible\":false}");
                } else {
                    out.print(String.format(Locale.US,
                        "{\"disponible\":true,\"lat\":%f,\"lng\":%f,\"actualizado\":\"%s\"}",
                        u.getlatitud(), u.getlongitud(), u.getactualizado().toString()));
                }
            }
        } catch (Exception e) {
            enviarError(response, 400, "Petición inválida: " + e.getMessage());
        }
    }

    private void enviarError(HttpServletResponse response, int codigoHttp, String mensaje) throws IOException {
        response.setStatus(codigoHttp);
        try (PrintWriter out = response.getWriter()) {
            out.print("{\"error\":\"" + mensaje.replace("\"", "'") + "\"}");
        }
    }
}
