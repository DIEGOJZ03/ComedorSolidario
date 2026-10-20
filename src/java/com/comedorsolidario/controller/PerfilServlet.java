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

@WebServlet({"/donante/perfil"})
public class PerfilServlet extends BaseServlet {
    @Override
    protected void get(HttpServletRequest r, HttpServletResponse s)
            throws ServletException, IOException, SQLException {
        r.setAttribute("usuario", new UsuarioDAO().buscar(usuarioId(r)));
        vista(r, s, "donante/perfil.jsp");
    }

    @Override
    protected void post(HttpServletRequest r, HttpServletResponse s)
            throws ServletException, IOException, SQLException {
        try {
            Usuario u = new Usuario(usuarioId(r));
            u.setNombre(texto(r, "nombre", 80, true));
            u.setApellido(texto(r, "apellido", 80, true));
            u.setTelefono(texto(r, "telefono", 20, false));
            new UsuarioDAO().actualizarPerfil(u);
            r.getSession().setAttribute("usuarioNombre", u.getNombre());
            ir(r, s, "/donante/perfil", "Perfil actualizado.");
        } catch (IllegalArgumentException e) {
            s.setStatus(400);
            r.setAttribute("error", e.getMessage());
            get(r, s);
        }
    }
}
