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

@WebServlet({"/admin/usuarios","/admin/usuarios/formulario","/admin/usuarios/detalle"})
public class AdminUsuarioServlet extends BaseServlet {
    protected void get(HttpServletRequest r,HttpServletResponse s) throws ServletException,IOException,SQLException {
        UsuarioDAO dao=new UsuarioDAO();
        if(r.getServletPath().equals("/admin/usuarios")){r.setAttribute("items",dao.listar());vista(r,s,"admin/usuarios/listar.jsp");return;}
        Usuario u=new Usuario();u.setEstado("ACTIVO");u.setRol("DONADOR");
        if(r.getParameter("id")!=null)u=dao.buscar(id(r.getParameter("id")));
        if(u==null || (r.getServletPath().endsWith("/detalle") && u.getId()==0)){s.sendError(404);return;}
        if(!"DONADOR".equals(u.getRol()) && r.getServletPath().endsWith("/formulario")){s.sendError(403);return;}
        r.setAttribute("item",u);vista(r,s,"admin/usuarios/"+(r.getServletPath().endsWith("/detalle")?"detalle.jsp":"formulario.jsp"));
    }
    protected void post(HttpServletRequest r,HttpServletResponse s) throws ServletException,IOException,SQLException {
        UsuarioDAO dao=new UsuarioDAO();
        String accion=opcion(r.getParameter("accion"),"guardar","estado", "eliminar");
        if ("eliminar".equals(accion)) {
            try {
                dao.eliminar(id(r.getParameter("id")));
                ir(r, s, "/admin/usuarios", "Registro eliminado definitivamente.");
            } catch (IllegalArgumentException e) {
                r.getSession().setAttribute("errorAccion", e.getMessage());
                ir(r, s, "/admin/usuarios", null);
            }
            return;
        }
        if("estado".equals(accion)){dao.cambiarEstado(id(r.getParameter("id")),opcion(r.getParameter("estado"),"ACTIVO","INACTIVO"));ir(r,s,"/admin/usuarios","Estado actualizado.");return;}
        Usuario u=new Usuario();
        try {
            if(r.getParameter("id")!=null && !"0".equals(r.getParameter("id")))u.setId(id(r.getParameter("id")));
            u.setNombre(texto(r,"nombre",80,true));u.setApellido(texto(r,"apellido",80,true));u.setEmail(email(r));u.setTelefono(texto(r,"telefono",20,false));u.setEstado(opcion(r.getParameter("estado"),"ACTIVO","INACTIVO"));
            if(u.getId()==0)u.setPasswordHash(PasswordUtil.hash(password(r)));
            Usuario existente=dao.buscarEmail(u.getEmail());
            if(existente!=null && existente.getId()!=u.getId())throw new IllegalArgumentException("El correo ya está registrado.");
            dao.guardarAdministrado(u);ir(r,s,"/admin/usuarios","Donador guardado correctamente.");
        }catch(IllegalArgumentException e){s.setStatus(400);r.setAttribute("error",e.getMessage());r.setAttribute("item",u);vista(r,s,"admin/usuarios/formulario.jsp");}
        catch(java.sql.SQLIntegrityConstraintViolationException e){s.setStatus(400);r.setAttribute("error","El correo ya está registrado.");r.setAttribute("item",u);vista(r,s,"admin/usuarios/formulario.jsp");}
    }
}
