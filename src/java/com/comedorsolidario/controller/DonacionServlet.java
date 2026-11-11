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
        int uid = usuarioId(r);
        r.setAttribute("puntos", new PuntoAcopioDAO().listar(true));
        r.setAttribute("perfil", new UsuarioDAO().buscar(uid));
        r.setAttribute("comedores", new ComedorDAO().listar(true, null, null, null));
        int previas = new DonacionDAO().historial(uid).size();
        r.setAttribute("totalDonacionesPrevias", previas);
        r.setAttribute("puedeDonarMultiple", previas >= 1);
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
            int uid = usuarioId(r);
            String[] comedoresIds = r.getParameterValues("comedorId");
            String[] tipos = r.getParameterValues("tipo");
            String[] cantidades = r.getParameterValues("cantidad");
            String[] unidades = r.getParameterValues("unidad");
            String[] descripciones = r.getParameterValues("descripcion");

            if (comedoresIds == null || tipos == null || cantidades == null || unidades == null || descripciones == null
                    || comedoresIds.length == 0
                    || comedoresIds.length != tipos.length
                    || comedoresIds.length != cantidades.length
                    || comedoresIds.length != unidades.length
                    || comedoresIds.length != descripciones.length) {
                throw new IllegalArgumentException("Completa todos los campos de la donación.");
            }

            if (comedoresIds.length > 1) {
                int previas = new DonacionDAO().historial(uid).size();
                if (previas < 1) {
                    throw new IllegalArgumentException(
                            "Debes haber realizado al menos 1 donación previa para registrar múltiples donaciones a la vez.");
                }
                if (comedoresIds.length > 5) {
                    throw new IllegalArgumentException("Puedes registrar hasta 5 donaciones a la vez en una misma solicitud.");
                }
            }

            String modalidad = opcion(r.getParameter("modalidad"), "DIRECTA", "ACOPIO", "RECOJO");
            Integer puntoAcopioId = "ACOPIO".equals(modalidad) ? id(r.getParameter("puntoAcopioId")) : null;

            SolicitudRecojo recojo = null;
            if ("RECOJO".equals(modalidad)) {
                recojo = new SolicitudRecojo();
                recojo.setDireccion(texto(r, "direccion", 200, true));
                recojo.setProvincia(texto(r, "provincia", 100, true));
                recojo.setDistrito(texto(r, "distrito", 100, true));
                recojo.setZona(texto(r, "zona", 80, true));
                recojo.setReferencia(texto(r, "referencia", 250, true));
                recojo.setFechaPreferida(fecha(r.getParameter("fechaPreferida")));
                com.comedorsolidario.service.logistica.LogisticaService.fechaFutura(recojo.getFechaPreferida());
                recojo.setHorarioPreferido(texto(r, "horarioPreferido", 200, true));
                recojo.setTelefono(telefono(r.getParameter("telefono")));
            }

            java.util.List<Donacion> listaDonaciones = new java.util.ArrayList<>();
            for (int i = 0; i < comedoresIds.length; i++) {
                Donacion d = new Donacion();
                d.setDonanteId(uid);
                d.setComedorId(id(comedoresIds[i]));
                d.setTipo(opcion(tipos[i], "ALIMENTOS", "UTENSILIOS", "DINERO", "OTROS"));
                d.setUnidad(opcion(unidades[i], "kg", "litros", "unidades", "cajas", "soles"));
                if (("DINERO".equals(d.getTipo())) != ("soles".equals(d.getUnidad()))) {
                    throw new IllegalArgumentException(
                            "Para dinero utiliza soles; para otros aportes elige una unidad física.");
                }
                d.setCantidad(cantidad(cantidades[i], d.getUnidad()));
                String desc = descripciones[i] == null ? "" : descripciones[i].trim();
                if (desc.isEmpty() || desc.length() > 1000) {
                    throw new IllegalArgumentException("Revisa el campo descripcion (máximo 1000 caracteres).");
                }
                d.setDescripcion(desc);
                d.setModalidad(modalidad);
                d.setPuntoAcopioId(puntoAcopioId);
                listaDonaciones.add(d);
            }

            new com.comedorsolidario.service.logistica.LogisticaService().registrarMultiples(listaDonaciones, recojo);
            String msg;
            if (listaDonaciones.size() > 1) {
                msg = "RECOJO".equals(modalidad)
                        ? "Se registraron " + listaDonaciones.size() + " donaciones con solicitud de recojo pendiente de evaluación."
                        : "Se registraron " + listaDonaciones.size() + " donaciones como PENDIENTE.";
            } else {
                msg = "RECOJO".equals(modalidad)
                        ? "Solicitud enviada: pendiente de evaluación. El recojo todavía no está confirmado."
                        : "Donación registrada como PENDIENTE.";
            }
            ir(r, s, "/donante/donaciones", msg);
        } catch (IllegalArgumentException e) {
            s.setStatus(400);
            r.setAttribute("error", e.getMessage());
            formulario(r, s);
        }
    }
}
