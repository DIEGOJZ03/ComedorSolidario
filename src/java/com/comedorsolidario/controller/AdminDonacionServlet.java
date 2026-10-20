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

@WebServlet({"/admin/donaciones","/admin/donaciones/detalle","/admin/donaciones/formulario"})
public class AdminDonacionServlet extends BaseServlet {
    protected void get(HttpServletRequest r,HttpServletResponse s) throws ServletException,IOException,SQLException {
        DonacionDAO dao=new DonacionDAO();
        String ruta=r.getServletPath();
        if(ruta.endsWith("/detalle") || ruta.endsWith("/formulario")) {
            Donacion d=new Donacion();d.setActivo(true);
            if(r.getParameter("id")!=null)d=dao.buscar(id(r.getParameter("id")));
            if(d==null || (ruta.endsWith("/detalle") && d.getId()==0)){s.sendError(404);return;}
            if (ruta.endsWith("/formulario") && d.getId() != 0
                    && (!d.isActivo() || !"PENDIENTE".equals(d.getEstado()))) {
                s.sendError(403);
                return;
            }
            r.setAttribute("donacion",d);r.setAttribute("item",d);
            if(ruta.endsWith("/detalle"))vista(r,s,"admin/donaciones/detalle.jsp");else formulario(r,s);
        } else {
            String estado=texto(r,"estado",15,false);
            if(!estado.isEmpty())opcion(estado,"PENDIENTE","ACEPTADA","ENTREGADA");
            r.setAttribute("donaciones",dao.listarAdministracion(estado));vista(r,s,"admin/donaciones/listar.jsp");
        }
    }
    private void formulario(HttpServletRequest r,HttpServletResponse s) throws ServletException,IOException,SQLException {
        r.setAttribute("comedores",new ComedorDAO().listar(true,null,null,null));
        r.setAttribute("donadores",new UsuarioDAO().listar().stream().filter(u -> "DONADOR".equals(u.getRol()) && "ACTIVO".equals(u.getEstado())).toList());
        vista(r,s,"admin/donaciones/formulario.jsp");
    }
    protected void post(HttpServletRequest r,HttpServletResponse s) throws ServletException,IOException,SQLException {
        DonacionDAO dao=new DonacionDAO();
        String accion=opcion(r.getParameter("accion"),"guardar","baja","avanzar", "eliminar", "vigencia");
        if ("eliminar".equals(accion)) {
            try {
                dao.eliminar(id(r.getParameter("id")));
                ir(r, s, "/admin/donaciones", "Registro eliminado definitivamente.");
            } catch (IllegalArgumentException e) {
                r.getSession().setAttribute("errorAccion", e.getMessage());
                ir(r, s, "/admin/donaciones", null);
            }
            return;
        }
        if ("vigencia".equals(accion)) {
            String vigencia = opcion(r.getParameter("vigencia"), "ACTIVO", "INACTIVO");
            dao.cambiarVigencia(id(r.getParameter("id")), "ACTIVO".equals(vigencia));
            ir(r, s, "/admin/donaciones", "ACTIVO".equals(vigencia)
                    ? "Donación reactivada." : "Donación desactivada. Su historial se conserva.");
            return;
        }
        if("baja".equals(accion)){dao.darBaja(id(r.getParameter("id")));ir(r,s,"/admin/donaciones","Donación dada de baja; se conserva el historial en la base de datos.");return;}
        if("avanzar".equals(accion)){
            int ident=id(r.getParameter("id"));boolean cambio=dao.avanzar(ident,opcion(r.getParameter("estadoActual"),"PENDIENTE","ACEPTADA"));
            ir(r,s,"/admin/donaciones/detalle?id="+ident,cambio?"Estado actualizado.":"No hubo cambios. Revisa el estado actual.");return;
        }
        Donacion d=new Donacion();
        try {
            if(r.getParameter("id")!=null && !"0".equals(r.getParameter("id")))d.setId(id(r.getParameter("id")));
            d.setDonanteId(id(r.getParameter("donanteId")));d.setComedorId(id(r.getParameter("comedorId")));
            d.setTipo(opcion(r.getParameter("tipo"),"ALIMENTOS","UTENSILIOS","DINERO","OTROS"));
            d.setCantidad(cantidad(r.getParameter("cantidad")));d.setUnidad(opcion(r.getParameter("unidad"),"kg","litros","unidades","cajas","soles"));
            if (("DINERO".equals(d.getTipo())) != ("soles".equals(d.getUnidad())))
                throw new IllegalArgumentException("Para dinero utiliza soles; para otros aportes elige una unidad física.");d.setDescripcion(texto(r,"descripcion",1000,true));
            if(d.getId()==0)dao.registrar(d);else dao.actualizar(d);
            ir(r,s,"/admin/donaciones","Donación guardada a nombre del donador seleccionado.");
        } catch(IllegalArgumentException e){s.setStatus(400);r.setAttribute("error",e.getMessage());r.setAttribute("item",d);formulario(r,s);}
    }
}
