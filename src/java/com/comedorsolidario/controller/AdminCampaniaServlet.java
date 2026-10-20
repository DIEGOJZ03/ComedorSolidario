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

@WebServlet({"/admin/campanias","/admin/campanias/formulario","/admin/campanias/detalle"})
public class AdminCampaniaServlet extends BaseServlet {
    protected void get(HttpServletRequest r,HttpServletResponse s) throws ServletException,IOException,SQLException {
        CampaniaDAO dao=new CampaniaDAO();
        if(r.getServletPath().equals("/admin/campanias")) {r.setAttribute("items",dao.listar());vista(r,s,"admin/campanias/listar.jsp");return;}
        Campania o=new Campania();o.setEstado("ACTIVO");
        if(r.getParameter("id")!=null)o=dao.buscar(id(r.getParameter("id")));
        if(o==null || (r.getServletPath().endsWith("/detalle") && o.getId()==0)){s.sendError(404);return;}
        r.setAttribute("item",o);
        if(r.getServletPath().endsWith("/detalle"))vista(r,s,"admin/campanias/detalle.jsp");else formulario(r,s);
    }
    private void formulario(HttpServletRequest r,HttpServletResponse s) throws ServletException,IOException,SQLException {
        r.setAttribute("comedores",new ComedorDAO().listar(false,null,null,null));
        vista(r,s,"admin/campanias/formulario.jsp");
    }
    protected void post(HttpServletRequest r,HttpServletResponse s) throws ServletException,IOException,SQLException {
        CampaniaDAO dao=new CampaniaDAO();
        String accion=opcion(r.getParameter("accion"),"guardar","estado", "eliminar");
        if ("eliminar".equals(accion)) {
            try {
                dao.eliminar(id(r.getParameter("id")));
                ir(r, s, "/admin/campanias", "Registro eliminado definitivamente.");
            } catch (IllegalArgumentException e) {
                r.getSession().setAttribute("errorAccion", e.getMessage());
                ir(r, s, "/admin/campanias", null);
            }
            return;
        }
        if("estado".equals(accion)){dao.cambiarEstado(id(r.getParameter("id")),opcion(r.getParameter("estado"),"ACTIVO","INACTIVO"));ir(r,s,"/admin/campanias","Estado actualizado.");return;}
        Campania o=new Campania();
        try {
            if(r.getParameter("id")!=null && !"0".equals(r.getParameter("id")))o.setId(id(r.getParameter("id")));
            o.setComedorId(id(r.getParameter("comedorId")));
            o.setNombre(texto(r,"nombre",100,true));
            o.setDescripcion(texto(r,"descripcion",1000,true));
            o.setFechaInicio(fecha(r.getParameter("fechaInicio")));
            o.setFechaFin(fecha(r.getParameter("fechaFin")));
            o.setEstado(opcion(r.getParameter("estado"),"ACTIVO","INACTIVO"));
            if(new ComedorDAO().buscar(o.getComedorId())==null)throw new IllegalArgumentException("El comedor no existe.");
            if(o.getFechaFin().isBefore(o.getFechaInicio()))throw new IllegalArgumentException("La fecha final debe ser igual o posterior a la inicial.");
            dao.guardar(o);ir(r,s,"/admin/campanias","Registro guardado correctamente.");
        }catch(IllegalArgumentException e){s.setStatus(400);r.setAttribute("error",e.getMessage());r.setAttribute("item",o);formulario(r,s);}
    }
}
