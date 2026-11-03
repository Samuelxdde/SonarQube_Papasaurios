package Servlet;

import Controlador.Tipo_documentoDAO;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import Modelo.Tipo_documento;

@WebServlet("/CargarRegistro")
public class CargarRegistro extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        Tipo_documentoDAO tipoDAO = new Tipo_documentoDAO();
        
        List<Tipo_documento> tiposDoc = tipoDAO.listarTipoDocumento();
        
        request.setAttribute("tiposDoc", tiposDoc);
        
        request.getRequestDispatcher("/Vista/Registrarse.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}