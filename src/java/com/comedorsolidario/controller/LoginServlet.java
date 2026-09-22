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

@WebServlet({"/login"})
public class LoginServlet extends BaseServlet {
    @Override
    protected void get(HttpServletRequest r, HttpServletResponse s)
            throws ServletException, IOException, SQLException {
        if (r.getSession().getAttribute("usuarioId") != null) {
            destino(r, s);
            return;
        }
        vista(r, s, "auth/login.jsp");
    }

    @Override
    protected void post(HttpServletRequest r, HttpServletResponse s)
            throws ServletException, IOException, SQLException {
        try {
            String correo = email(r);
            String clave = password(r);
            Usuario u = new UsuarioDAO().buscarEmail(correo);
            if (u == null
                    || !"ACTIVO".equals(u.getEstado())
                    || !PasswordUtil.verificar(clave, u.getPasswordHash()))
                throw new IllegalArgumentException(
                        "Correo o contraseña incorrectos, o cuenta inactiva.");
            r.changeSessionId();
            HttpSession session = r.getSession();
            session.setAttribute("usuarioId", u.getId());
            session.setAttribute("usuarioNombre", u.getNombre());
            session.setAttribute("usuarioRol", u.getRol());
            session.setAttribute("csrf", java.util.UUID.randomUUID().toString());
            destino(r, s);
        } catch (IllegalArgumentException e) {
            s.setStatus(400);
            r.setAttribute("error", e.getMessage());
            vista(r, s, "auth/login.jsp");
        }
    }

    private void destino(HttpServletRequest r, HttpServletResponse s) throws IOException {
        String rol = (String) r.getSession().getAttribute("usuarioRol");
        Object retorno = r.getSession().getAttribute("retornoDonacion");
        r.getSession().removeAttribute("retornoDonacion");
        if ("DONADOR".equals(rol) && retorno instanceof Integer)
            ir(r, s, "/donante/donaciones/nueva?comedorId=" + retorno, null);
        else ir(r, s, "ADMIN".equals(rol) ? "/admin/dashboard" : "/donante/dashboard", null);
    }
}
