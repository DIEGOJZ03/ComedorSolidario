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
        r.setAttribute("puntos", new PuntoAcopioDAO().listar(true));
        r.setAttribute("perfil",new UsuarioDAO().buscar(usuarioId(r)));
        r.setAttribute("comedores", new ComedorDAO().listar(true, null, null, null));
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
            Donacion d = new Donacion();
            d.setDonanteId(usuarioId(r));
            d.setComedorId(id(r.getParameter("comedorId")));
            d.setTipo(opcion(r.getParameter("tipo"), "ALIMENTOS", "UTENSILIOS", "DINERO", "OTROS"));
            d.setCantidad(cantidad(r.getParameter("cantidad")));
            d.setUnidad(
                    opcion(r.getParameter("unidad"), "kg", "litros", "unidades", "cajas", "soles"));
            if (("DINERO".equals(d.getTipo())) != ("soles".equals(d.getUnidad())))
                throw new IllegalArgumentException(
                        "Para dinero utiliza soles; para otros aportes elige una unidad física.");
            d.setDescripcion(texto(r, "descripcion", 1000, true));
            d.setModalidad(opcion(r.getParameter("modalidad"),"DIRECTA","ACOPIO","RECOJO"));
            if("ACOPIO".equals(d.getModalidad()))d.setPuntoAcopioId(id(r.getParameter("puntoAcopioId")));
            SolicitudRecojo recojo=null;
            if("RECOJO".equals(d.getModalidad())) {
                recojo=new SolicitudRecojo();recojo.setDireccion(texto(r,"direccion",200,true));
                recojo.setProvincia(texto(r,"provincia",100,true));recojo.setDistrito(texto(r,"distrito",100,true));recojo.setZona(texto(r,"zona",80,true));recojo.setReferencia(texto(r,"referencia",250,true));
                recojo.setFechaPreferida(fecha(r.getParameter("fechaPreferida")));com.comedorsolidario.service.logistica.LogisticaService.fechaFutura(recojo.getFechaPreferida());
                recojo.setHorarioPreferido(texto(r,"horarioPreferido",200,true));recojo.setTelefono(telefono(r.getParameter("telefono")));
            }
            new com.comedorsolidario.service.logistica.LogisticaService().registrar(d,recojo);
            ir(r, s, "/donante/donaciones", "RECOJO".equals(d.getModalidad()) ? "Solicitud enviada: pendiente de evaluación. El recojo todavía no está confirmado." : "Donación registrada como PENDIENTE.");
        } catch (IllegalArgumentException e) {
            s.setStatus(400);
            r.setAttribute("error", e.getMessage());
            formulario(r, s);
        }
    }
}
