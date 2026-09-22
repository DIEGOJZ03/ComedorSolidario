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

@WebServlet({"/admin/comedores", "/admin/comedores/formulario", "/admin/comedores/detalle"})
public class AdminComedorServlet extends BaseServlet {
    @Override
    protected void get(HttpServletRequest r, HttpServletResponse s)
            throws ServletException, IOException, SQLException {
        ComedorDAO dao = new ComedorDAO();
        String ruta = r.getServletPath();
        if (ruta.endsWith("/formulario") || ruta.endsWith("/detalle")) {
            Comedor c = new Comedor();
            c.setEstado("ACTIVO");
            if (r.getParameter("id") != null) c = dao.buscar(id(r.getParameter("id")));
            if (c == null || (ruta.endsWith("/detalle") && c.getId() == 0)) {
                s.sendError(404);
                return;
            }
            r.setAttribute("comedor", c);
            if (ruta.endsWith("/detalle"))
                r.setAttribute("necesidades", new NecesidadDAO().listar(c.getId(), false));
            vista(
                    r,
                    s,
                    "admin/comedores/"
                            + (ruta.endsWith("/detalle") ? "detalle.jsp" : "formulario.jsp"));
        } else {
            r.setAttribute("comedores", dao.listar(false, null, null, null));
            vista(r, s, "admin/comedores/listar.jsp");
        }
    }

    @Override
    protected void post(HttpServletRequest r, HttpServletResponse s)
            throws ServletException, IOException, SQLException {
        ComedorDAO dao = new ComedorDAO();
        String accion = opcion(r.getParameter("accion"), "guardar", "estado");
        if ("estado".equals(accion)) {
            dao.cambiarEstado(
                    id(r.getParameter("id")),
                    opcion(r.getParameter("estado"), "ACTIVO", "INACTIVO"));
            ir(r, s, "/admin/comedores", "Estado del comedor actualizado.");
            return;
        }
        Comedor c = new Comedor();
        try {
            String ident = r.getParameter("id");
            if (ident != null && !ident.isBlank() && !"0".equals(ident)) c.setId(id(ident));
            c.setNombre(texto(r, "nombre", 120, true));
            c.setDescripcion(texto(r, "descripcion", 1500, true));
            c.setDireccion(texto(r, "direccion", 200, true));
            c.setZona(texto(r, "zona", 80, true));
            c.setDistrito(texto(r, "distrito", 100, true));
            c.setProvincia(texto(r, "provincia", 100, true));
            c.setResponsable(texto(r, "responsable", 120, true));
            c.setTelefono(texto(r, "telefono", 20, true));
            c.setEstado(opcion(r.getParameter("estado"), "ACTIVO", "INACTIVO"));
            dao.guardar(c);
            ir(r, s, "/admin/comedores", "Comedor guardado correctamente.");
        } catch (IllegalArgumentException e) {
            s.setStatus(400);
            r.setAttribute("error", e.getMessage());
            r.setAttribute("comedor", c);
            vista(r, s, "admin/comedores/formulario.jsp");
        }
    }
}
