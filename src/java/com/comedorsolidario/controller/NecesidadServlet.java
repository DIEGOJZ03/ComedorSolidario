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

@WebServlet({"/admin/necesidades", "/admin/necesidades/formulario"})
public class NecesidadServlet extends BaseServlet {
    @Override
    protected void get(HttpServletRequest r, HttpServletResponse s)
            throws ServletException, IOException, SQLException {
        NecesidadDAO dao = new NecesidadDAO();
        if (r.getServletPath().endsWith("/formulario")) {
            Necesidad n = new Necesidad();
            n.setEstado("ACTIVA");
            n.setPrioridad("MEDIA");
            if (r.getParameter("id") != null) n = dao.buscar(id(r.getParameter("id")));
            if (n == null) {
                s.sendError(404);
                return;
            }
            r.setAttribute("necesidad", n);
            formulario(r, s);
        } else {
            String comedorId = r.getParameter("comedorId");
            r.setAttribute(
                    "necesidades",
                    dao.listar(
                            comedorId == null || comedorId.isBlank() ? null : id(comedorId),
                            false));
            r.setAttribute("comedores", new ComedorDAO().listar(false, null, null, null));
            vista(r, s, "admin/necesidades/listar.jsp");
        }
    }

    private void formulario(HttpServletRequest r, HttpServletResponse s)
            throws ServletException, IOException, SQLException {
        r.setAttribute("comedores", new ComedorDAO().listar(false, null, null, null));
        vista(r, s, "admin/necesidades/formulario.jsp");
    }

    @Override
    protected void post(HttpServletRequest r, HttpServletResponse s)
            throws ServletException, IOException, SQLException {
        NecesidadDAO dao = new NecesidadDAO();
        String accion = opcion(r.getParameter("accion"), "guardar", "estado");
        if ("estado".equals(accion)) {
            dao.cambiarEstado(
                    id(r.getParameter("id")),
                    opcion(r.getParameter("estado"), "ACTIVA", "INACTIVA"));
            ir(r, s, "/admin/necesidades", "Estado de la necesidad actualizado.");
            return;
        }
        Necesidad n = new Necesidad();
        try {
            String ident = r.getParameter("id");
            if (ident != null && !ident.isBlank() && !"0".equals(ident)) n.setId(id(ident));
            n.setComedorId(id(r.getParameter("comedorId")));
            if (new ComedorDAO().buscar(n.getComedorId()) == null)
                throw new IllegalArgumentException("El comedor no existe.");
            n.setNombre(texto(r, "nombre", 100, true));
            n.setDescripcion(texto(r, "descripcion", 1000, true));
            n.setCantidadNecesaria(cantidad(r.getParameter("cantidadNecesaria")));
            n.setUnidad(
                    opcion(r.getParameter("unidad"), "kg", "litros", "unidades", "cajas", "soles"));
            n.setPrioridad(opcion(r.getParameter("prioridad"), "ALTA", "MEDIA", "BAJA"));
            n.setEstado(opcion(r.getParameter("estado"), "ACTIVA", "INACTIVA"));
            dao.guardar(n);
            ir(r, s, "/admin/necesidades", "Necesidad guardada.");
        } catch (IllegalArgumentException e) {
            s.setStatus(400);
            r.setAttribute("error", e.getMessage());
            r.setAttribute("necesidad", n);
            formulario(r, s);
        }
    }
}
