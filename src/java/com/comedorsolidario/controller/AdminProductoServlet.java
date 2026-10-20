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

@WebServlet({"/admin/productos","/admin/productos/formulario","/admin/productos/detalle"})
public class AdminProductoServlet extends BaseServlet {
    protected void get(HttpServletRequest r,HttpServletResponse s) throws ServletException,IOException,SQLException {
        ProductoDAO dao=new ProductoDAO();
        if(r.getServletPath().equals("/admin/productos")) {r.setAttribute("items",dao.listar());vista(r,s,"admin/productos/listar.jsp");return;}
        Producto o=new Producto();o.setEstado("ACTIVO");
        if(r.getParameter("id")!=null)o=dao.buscar(id(r.getParameter("id")));
        if(o==null || (r.getServletPath().endsWith("/detalle") && o.getId()==0)){s.sendError(404);return;}
        r.setAttribute("item",o);
        if(r.getServletPath().endsWith("/detalle"))vista(r,s,"admin/productos/detalle.jsp");else formulario(r,s);
    }
    private void formulario(HttpServletRequest r,HttpServletResponse s) throws ServletException,IOException,SQLException {
        
        vista(r,s,"admin/productos/formulario.jsp");
    }
    protected void post(HttpServletRequest r,HttpServletResponse s) throws ServletException,IOException,SQLException {
        ProductoDAO dao=new ProductoDAO();
        String accion=opcion(r.getParameter("accion"),"guardar","estado", "eliminar");
        if ("eliminar".equals(accion)) {
            try {
                dao.eliminar(id(r.getParameter("id")));
                ir(r, s, "/admin/productos", "Registro eliminado definitivamente.");
            } catch (IllegalArgumentException e) {
                r.getSession().setAttribute("errorAccion", e.getMessage());
                ir(r, s, "/admin/productos", null);
            }
            return;
        }
        if("estado".equals(accion)){dao.cambiarEstado(id(r.getParameter("id")),opcion(r.getParameter("estado"),"ACTIVO","INACTIVO"));ir(r,s,"/admin/productos","Estado actualizado.");return;}
        Producto o=new Producto();
        try {
            if(r.getParameter("id")!=null && !"0".equals(r.getParameter("id")))o.setId(id(r.getParameter("id")));
            o.setNombre(texto(r,"nombre",100,true));
            o.setDescripcion(texto(r,"descripcion",1000,true));
            o.setUnidad(texto(r,"unidad",30,true));
            o.setEstado(opcion(r.getParameter("estado"),"ACTIVO","INACTIVO"));
            
            dao.guardar(o);ir(r,s,"/admin/productos","Registro guardado correctamente.");
        }catch(IllegalArgumentException e){s.setStatus(400);r.setAttribute("error",e.getMessage());r.setAttribute("item",o);formulario(r,s);}
    }
}
