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

@WebServlet({"/registro"})
public class RegistroServlet extends BaseServlet {
    @Override
    protected void get(HttpServletRequest r, HttpServletResponse s)
            throws ServletException, IOException, SQLException {
        vista(r, s, "auth/registro.jsp");
    }

    @Override
    protected void post(HttpServletRequest r, HttpServletResponse s)
            throws ServletException, IOException, SQLException {
        try {
            Usuario u = new Usuario();
            u.setNombre(texto(r, "nombre", 80, true));
            u.setApellido(texto(r, "apellido", 80, true));
            u.setTelefono(texto(r, "telefono", 20, false));
            u.setEmail(email(r));
            String clave = password(r);
            if (!clave.equals(r.getParameter("confirmacion")))
                throw new IllegalArgumentException("Las contraseñas no coinciden.");
            UsuarioDAO dao = new UsuarioDAO();
            if (dao.buscarEmail(u.getEmail()) != null)
                throw new IllegalArgumentException("Este correo ya está registrado.");
            u.setPasswordHash(PasswordUtil.hash(clave));
            dao.registrar(u);
            ir(r, s, "/login", "Cuenta creada. Ya puedes iniciar sesión.");
        } catch (IllegalArgumentException e) {
            s.setStatus(400);
            r.setAttribute("error", e.getMessage());
            vista(r, s, "auth/registro.jsp");
        } catch (SQLException e) {
            if (e.getErrorCode() != 1062) throw e;
            s.setStatus(400);
            r.setAttribute("error", "Este correo ya está registrado.");
            vista(r, s, "auth/registro.jsp");
        }
    }
}
