package com.comedorsolidario.controller;

import com.comedorsolidario.dao.ComedorDAO;
import com.comedorsolidario.model.Comedor;
import com.comedorsolidario.util.ImagenComedor;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.nio.file.*;
import java.sql.SQLException;
import static com.comedorsolidario.util.ValidacionUtil.id;

@WebServlet("/foto-comedor")
public class FotoComedorServlet extends BaseServlet {
    @Override
    protected void get(HttpServletRequest r, HttpServletResponse s) throws IOException, SQLException {
        int ident = id(r.getParameter("id"));
        Comedor comedor = new ComedorDAO().buscar(ident);
        HttpSession sesion = r.getSession(false);
        boolean admin = sesion != null && "ADMIN".equals(sesion.getAttribute("usuarioRol"));
        if (comedor == null || (!"ACTIVO".equals(comedor.getEstado()) && !admin)) {
            s.sendError(404);
            return;
        }
        s.setHeader("Cache-Control", "no-store");
        Path subida = new ImagenComedor().buscar(ident);
        if (subida != null && Files.size(subida) > 0) {
            s.setContentType("image/png");
            try (InputStream entrada = Files.newInputStream(subida)) {
                entrada.transferTo(s.getOutputStream());
            }
            return;
        }
        // Si no hay elección del administrador, conserva las fotos incluidas en el proyecto.
        if (subida == null) {
            for (String extension : new String[]{"jpg", "jpeg", "png", "webp"}) {
                String ruta = "/assets/img/comedores/" + ident + "." + extension;
                try (InputStream entrada = getServletContext().getResourceAsStream(ruta)) {
                    if (entrada == null) continue;
                    s.setContentType("jpg".equals(extension) || "jpeg".equals(extension)
                            ? "image/jpeg" : "image/" + extension);
                    entrada.transferTo(s.getOutputStream());
                    return;
                }
            }
        }
        try (InputStream entrada = getServletContext().getResourceAsStream("/assets/img/comedores/sin-foto.svg")) {
            if (entrada == null) { s.sendError(404); return; }
            s.setContentType("image/svg+xml");
            entrada.transferTo(s.getOutputStream());
        }
    }
}
