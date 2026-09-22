package com.comedorsolidario.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.UUID;

/** Registrado en web.xml: protege peticiones externas, no forwards internos a vistas. */
public class SeguridadFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest r = (HttpServletRequest) request;
        HttpServletResponse s = (HttpServletResponse) response;
        r.setCharacterEncoding("UTF-8");
        s.setCharacterEncoding("UTF-8");
        s.setHeader("X-Content-Type-Options", "nosniff");
        s.setHeader("X-Frame-Options", "DENY");
        s.setHeader("Referrer-Policy", "same-origin");
        String ruta = r.getServletPath();
        if (ruta.startsWith("/assets/")) {
            chain.doFilter(r, s);
            return;
        }
        s.setHeader("Cache-Control", "no-store");
        if (ruta.startsWith("/views/")) {
            s.sendError(404);
            return;
        }
        HttpSession session = r.getSession();
        if (session.getAttribute("csrf") == null)
            session.setAttribute("csrf", UUID.randomUUID().toString());
        boolean admin = ruta.startsWith("/admin/");
        boolean donador = ruta.startsWith("/donante/");
        if ((admin || donador) && session.getAttribute("usuarioId") == null) {
            if (ruta.equals("/donante/donaciones/nueva")) {
                try {
                    int id = Integer.parseInt(r.getParameter("comedorId"));
                    if (id > 0) session.setAttribute("retornoDonacion", id);
                } catch (RuntimeException ignored) {
                }
            }
            s.sendRedirect(r.getContextPath() + "/login");
            return;
        }
        String rol = (String) session.getAttribute("usuarioRol");
        if ((admin && !"ADMIN".equals(rol)) || (donador && !"DONADOR".equals(rol))) {
            s.sendError(403);
            return;
        }
        if ("POST".equals(r.getMethod())
                && !session.getAttribute("csrf").equals(r.getParameter("csrf"))) {
            s.sendError(403);
            return;
        }
        chain.doFilter(r, s);
    }
}
