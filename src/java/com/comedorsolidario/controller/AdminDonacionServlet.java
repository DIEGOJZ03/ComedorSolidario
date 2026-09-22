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

@WebServlet({"/admin/donaciones", "/admin/donaciones/detalle"})
public class AdminDonacionServlet extends BaseServlet {
    @Override
    protected void get(HttpServletRequest r, HttpServletResponse s)
            throws ServletException, IOException, SQLException {
        DonacionDAO dao = new DonacionDAO();
        if (r.getServletPath().endsWith("/detalle")) {
            Donacion d = dao.buscar(id(r.getParameter("id")));
            if (d == null) {
                s.sendError(404);
                return;
            }
            r.setAttribute("donacion", d);
            vista(r, s, "admin/donaciones/detalle.jsp");
        } else {
            String estado = texto(r, "estado", 15, false);
            if (!estado.isEmpty()) opcion(estado, "PENDIENTE", "ACEPTADA", "ENTREGADA");
            r.setAttribute("donaciones", dao.listar(null, estado));
            vista(r, s, "admin/donaciones/listar.jsp");
        }
    }

    @Override
    protected void post(HttpServletRequest r, HttpServletResponse s)
            throws ServletException, IOException, SQLException {
        int ident = id(r.getParameter("id"));
        String actual = opcion(r.getParameter("estadoActual"), "PENDIENTE", "ACEPTADA");
        boolean cambio = new DonacionDAO().avanzar(ident, actual);
        ir(
                r,
                s,
                "/admin/donaciones/detalle?id=" + ident,
                cambio
                        ? "Estado actualizado."
                        : "La donación cambió previamente. Revisa el estado actual.");
    }
}
