package com.comedorsolidario.controller;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

/** Manejo común de errores. Los controladores concretos mantienen cada función separada. */
public abstract class BaseServlet extends HttpServlet {
    @Override
    protected final void doGet(HttpServletRequest r, HttpServletResponse s)
            throws ServletException, IOException {
        ejecutar(r, s, false);
    }

    @Override
    protected final void doPost(HttpServletRequest r, HttpServletResponse s)
            throws ServletException, IOException {
        ejecutar(r, s, true);
    }

    private void ejecutar(HttpServletRequest r, HttpServletResponse s, boolean post)
            throws ServletException, IOException {
        try {
            if (post) post(r, s);
            else get(r, s);
        } catch (IllegalArgumentException e) {
            s.setStatus(400);
            r.setAttribute("error", e.getMessage());
            vista(r, s, "errors/400.jsp");
        } catch (SQLException e) {
            getServletContext().log("Error de persistencia", e);
            s.setStatus(500);
            r.setAttribute(
                    "error",
                    "No se pudo completar la operación. Revisa la conexión MySQL o vuelve a intentarlo.");
            vista(r, s, "errors/500.jsp");
        }
    }

    protected void get(HttpServletRequest r, HttpServletResponse s)
            throws ServletException, IOException, SQLException {
        s.sendError(405);
    }

    protected void post(HttpServletRequest r, HttpServletResponse s)
            throws ServletException, IOException, SQLException {
        s.sendError(405);
    }

    protected void vista(HttpServletRequest r, HttpServletResponse s, String ruta)
            throws ServletException, IOException {
        r.getRequestDispatcher("/views/" + ruta).forward(r, s);
    }

    protected void ir(HttpServletRequest r, HttpServletResponse s, String ruta, String mensaje)
            throws IOException {
        if (mensaje != null) r.getSession().setAttribute("exito", mensaje);
        s.sendRedirect(r.getContextPath() + ruta);
    }

    protected int usuarioId(HttpServletRequest r) {
        return (Integer) r.getSession().getAttribute("usuarioId");
    }
}
