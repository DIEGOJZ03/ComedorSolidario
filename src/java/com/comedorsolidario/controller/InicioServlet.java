package com.comedorsolidario.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import com.comedorsolidario.dao.*;
import com.comedorsolidario.model.*;
import com.comedorsolidario.util.*;
import static com.comedorsolidario.util.ValidacionUtil.*;

@WebServlet({"/inicio"})
public class InicioServlet extends BaseServlet {
    @Override
    protected void get(HttpServletRequest r, HttpServletResponse s)
            throws ServletException, IOException, SQLException {
        ComedorDAO dao = new ComedorDAO();
        r.setAttribute("destacados", dao.listar(true, null, null, null).stream().limit(3).toList());
        r.setAttribute("totalComedores", dao.total(true));
        r.setAttribute("totalDonadores", new UsuarioDAO().totalDonadores());
        r.setAttribute("resumen", new DonacionDAO().resumen(null));
        r.setAttribute("inicioCargado", true);
        r.getRequestDispatcher("/index.jsp").forward(r, s);
    }
}
