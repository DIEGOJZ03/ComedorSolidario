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

@WebServlet({"/comedores", "/comedores/detalle"})
public class ComedorServlet extends BaseServlet {
    @Override
    protected void get(HttpServletRequest r, HttpServletResponse s)
            throws ServletException, IOException, SQLException {
        ComedorDAO dao = new ComedorDAO();
        if (r.getServletPath().endsWith("/detalle")) {
            Comedor c = dao.buscar(id(r.getParameter("id")));
            if (c == null || !"ACTIVO".equals(c.getEstado())) {
                s.sendError(404);
                return;
            }
            r.setAttribute("comedor", c);
            r.setAttribute("necesidades", new NecesidadDAO().listar(c.getId(), true));
            vista(r, s, "public/detalle-comedor.jsp");
        } else {
            r.setAttribute(
                    "comedores",
                    dao.listar(
                            true,
                            texto(r, "zona", 80, false),
                            texto(r, "distrito", 100, false),
                            texto(r, "provincia", 100, false)));
            vista(r, s, "public/comedores.jsp");
        }
    }
}
