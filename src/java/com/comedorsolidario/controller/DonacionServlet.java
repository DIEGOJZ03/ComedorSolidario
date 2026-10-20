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

@WebServlet({"/donante/donaciones", "/donante/donaciones/nueva"})
public class DonacionServlet extends BaseServlet {
    @Override
    protected void get(HttpServletRequest r, HttpServletResponse s)
            throws ServletException, IOException, SQLException {
        if (r.getServletPath().endsWith("/nueva")) formulario(r, s);
        else {
            r.setAttribute("donaciones", new DonacionDAO().listar(usuarioId(r), null));
            vista(r, s, "donante/mis-donaciones.jsp");
        }
    }

    private void formulario(HttpServletRequest r, HttpServletResponse s)
            throws ServletException, IOException, SQLException {
        r.setAttribute("comedores", new ComedorDAO().listar(true, null, null, null));
        vista(r, s, "donante/realizar-donacion.jsp");
    }

    @Override
    protected void post(HttpServletRequest r, HttpServletResponse s)
            throws ServletException, IOException, SQLException {
        if (!r.getServletPath().endsWith("/nueva")) {
            s.sendError(405);
            return;
        }
        try {
            Donacion d = new Donacion();
            d.setDonanteId(usuarioId(r));
            d.setComedorId(id(r.getParameter("comedorId")));
            d.setTipo(opcion(r.getParameter("tipo"), "ALIMENTOS", "UTENSILIOS", "DINERO", "OTROS"));
            d.setCantidad(cantidad(r.getParameter("cantidad")));
            d.setUnidad(
                    opcion(r.getParameter("unidad"), "kg", "litros", "unidades", "cajas", "soles"));
            if (("DINERO".equals(d.getTipo())) != ("soles".equals(d.getUnidad())))
                throw new IllegalArgumentException(
                        "Para dinero utiliza soles; para otros aportes elige una unidad física.");
            d.setDescripcion(texto(r, "descripcion", 1000, true));
            new DonacionDAO().registrar(d);
            ir(r, s, "/donante/donaciones", "Donación registrada como PENDIENTE.");
        } catch (IllegalArgumentException e) {
            s.setStatus(400);
            r.setAttribute("error", e.getMessage());
            formulario(r, s);
        }
    }
}
