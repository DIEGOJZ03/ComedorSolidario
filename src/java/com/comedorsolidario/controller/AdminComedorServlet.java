package com.comedorsolidario.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.nio.file.Files;
import java.nio.file.Path;
import com.comedorsolidario.dao.*;
import com.comedorsolidario.model.*;
import com.comedorsolidario.util.*;
import static com.comedorsolidario.util.ValidacionUtil.*;

@WebServlet({"/admin/comedores", "/admin/comedores/formulario", "/admin/comedores/detalle"})
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 5 * 1024 * 1024, maxRequestSize = 6 * 1024 * 1024)
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
        String accion = opcion(r.getParameter("accion"), "guardar", "estado", "eliminar");
        if ("eliminar".equals(accion)) {
            try {
                dao.eliminar(id(r.getParameter("id")));
                ir(r, s, "/admin/comedores", "Registro eliminado definitivamente.");
            } catch (IllegalArgumentException e) {
                r.getSession().setAttribute("errorAccion", e.getMessage());
                ir(r, s, "/admin/comedores", null);
            }
            return;
        }
        if ("estado".equals(accion)) {
            dao.cambiarEstado(
                    id(r.getParameter("id")),
                    opcion(r.getParameter("estado"), "ACTIVO", "INACTIVO"));
            ir(r, s, "/admin/comedores", "Estado del comedor actualizado.");
            return;
        }
        Comedor c = new Comedor();
        ImagenComedor imagenes = new ImagenComedor();
        Path temporal = null;
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
            Part foto = r.getContentType() != null && r.getContentType().startsWith("multipart/form-data")
                    ? r.getPart("foto") : null;
            try {
                temporal = imagenes.preparar(foto, "true".equals(r.getParameter("fotoPredeterminada")));
            } catch (IOException e) {
                getServletContext().log("No se pudo preparar la foto del comedor", e);
                throw new IllegalArgumentException("No se pudo guardar la foto. Comprueba que el archivo sea válido y que la carpeta de imágenes tenga permiso de escritura. No se guardaron los datos.");
            }
            dao.guardar(c);
            try {
                imagenes.guardar(temporal, c.getId());
            } catch (IOException e) {
                getServletContext().log("Datos guardados, pero no se pudo publicar la foto del comedor " + c.getId(), e);
                r.getSession().setAttribute("errorAccion", "Los datos del comedor se guardaron, pero la foto no pudo actualizarse. Selecciónala de nuevo y vuelve a guardar.");
                ir(r, s, "/admin/comedores/formulario?id=" + c.getId(), null);
                return;
            }
            ir(r, s, "/admin/comedores", "Comedor guardado correctamente.");
        } catch (IllegalArgumentException e) {
            s.setStatus(400);
            r.setAttribute("error", e.getMessage());
            r.setAttribute("comedor", c);
            vista(r, s, "admin/comedores/formulario.jsp");
        } finally {
            if (temporal != null) {
                try { Files.deleteIfExists(temporal); }
                catch (IOException e) { getServletContext().log("No se pudo limpiar el archivo temporal de imagen", e); }
            }
        }
    }
}
