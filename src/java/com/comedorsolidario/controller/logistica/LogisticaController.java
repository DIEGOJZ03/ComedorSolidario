package com.comedorsolidario.controller.logistica;
import com.comedorsolidario.dao.*;
import com.comedorsolidario.model.*;
import com.comedorsolidario.service.logistica.LogisticaService;
import jakarta.servlet.http.*;
import java.sql.SQLException;
import java.time.LocalDate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import static com.comedorsolidario.util.ValidacionUtil.*;

@Controller
public class LogisticaController {
 private final LogisticaService service;
 public LogisticaController(LogisticaService service) {this.service=service;}
 private boolean admin(HttpServletRequest r) {return "ADMIN".equals(r.getSession().getAttribute("usuarioRol"));}
 private void exigirAdmin(HttpServletRequest r) {if(!admin(r))throw new ResponseStatusException(HttpStatus.FORBIDDEN);}
 private int usuario(HttpServletRequest r) {Object id=r.getSession().getAttribute("usuarioId");if(id==null)throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);return (Integer)id;}
 private String ok(HttpServletRequest r,String path,String mensaje) {r.getSession().setAttribute("exito",mensaje);return "redirect:"+path;}
 @GetMapping("/puntos") public String puntos(HttpServletRequest r,Model m) throws SQLException {
  m.addAttribute("administrador",admin(r));m.addAttribute("puntos",new PuntoAcopioDAO().listar(!admin(r)));
  if(r.getParameter("id")!=null) {PuntoAcopio p=new PuntoAcopioDAO().buscar(id(r.getParameter("id")));if(!admin(r)&&!"ACTIVO".equals(p.getEstado()))throw new ResponseStatusException(HttpStatus.NOT_FOUND);m.addAttribute("punto",p);}
  return "puntos";
 }
 @PostMapping("/puntos") public String guardarPunto(HttpServletRequest r) throws SQLException {
  exigirAdmin(r);PuntoAcopioDAO dao=new PuntoAcopioDAO();String accion=opcion(r.getParameter("accion"),"guardar","estado","eliminar");
  if(!accion.equals("guardar")) {int id=id(r.getParameter("id"));if(accion.equals("eliminar"))dao.eliminar(id);else dao.estado(id,opcion(r.getParameter("estado"),"ACTIVO","INACTIVO"));}
  else {
   PuntoAcopio p=new PuntoAcopio();String ident=r.getParameter("id");if(ident!=null&&!ident.isBlank())p.setId(id(ident));
   p.setNombre(texto(r,"nombre",120,true));p.setDireccion(texto(r,"direccion",200,true));
   p.setProvincia(texto(r,"provincia",100,true));p.setDistrito(texto(r,"distrito",100,true));p.setZona(texto(r,"zona",80,true));
   p.setReferencia(texto(r,"referencia",250,true));p.setHorarios(texto(r,"horarios",200,true));p.setResponsable(texto(r,"responsable",120,true));
   p.setTelefono(telefono(r.getParameter("telefono")));p.setEstado(opcion(r.getParameter("estado"),"ACTIVO","INACTIVO"));dao.guardar(p);
  }return ok(r,"/admin/logistica/puntos","Punto de acopio actualizado.");
 }
 @GetMapping("/recojos") public String recojos(HttpServletRequest r,Model m) throws SQLException {
  int uid=usuario(r);m.addAttribute("administrador",admin(r));
  m.addAttribute("recojos",new SolicitudRecojoDAO().listar(admin(r)?null:uid,texto(r,"estado",20,false),texto(r,"distrito",100,false)));
  m.addAttribute("puntos",new PuntoAcopioDAO().listar(true));return "recojos";
 }
 @PostMapping("/recojos") public String coordinar(HttpServletRequest r) throws SQLException {
  exigirAdmin(r);String accion=texto(r,"accion",20,true);String nota=texto(r,"observaciones",1000,false);
  if(java.util.List.of("rechazar","cancelar","incumplir").contains(accion) && nota.isBlank())throw new IllegalArgumentException("Indica el motivo antes de cerrar la solicitud; la recomendación de un punto es opcional.");
  String recomendado=r.getParameter("recomendado");
  if(recomendado!=null&&!recomendado.isBlank()) {PuntoAcopio p=new PuntoAcopioDAO().buscar(id(recomendado));if(!"ACTIVO".equals(p.getEstado()))throw new IllegalArgumentException("El punto recomendado está inactivo.");nota+=" Punto recomendado: "+p.getNombre()+", "+p.getDireccion()+". Horario: "+p.getHorarios();}
  if(nota.length()>1000)throw new IllegalArgumentException("Acorta las observaciones para incluir la recomendación.");
  service.recojo(id(r.getParameter("id")),accion,accion.equals("programar")?fecha(r.getParameter("fecha")):null,texto(r,"horario",200,false),texto(r,"responsable",120,false),nota,usuario(r));
  return ok(r,"/admin/logistica/recojos","Solicitud actualizada. Solo COMPLETADO confirma la recepción en el comedor.");
 }
 @GetMapping("/rutas") public String rutas(HttpServletRequest r,Model m) throws SQLException {
  exigirAdmin(r);m.addAttribute("rutas",new RutaRecojoDAO().listar());
  m.addAttribute("candidatos",new SolicitudRecojoDAO().listar(null,"PROGRAMADO",texto(r,"distrito",100,false)));
  if(r.getParameter("id")!=null)m.addAttribute("paradas",new DetalleRutaRecojoDAO().listar(id(r.getParameter("id"))));return "rutas";
 }
 @PostMapping("/rutas") public String ruta(HttpServletRequest r) throws SQLException {
  exigirAdmin(r);String accion=opcion(r.getParameter("accion"),"crear","parada","quitar","estado");int ident;
  if(accion.equals("crear")) {RutaRecojo x=new RutaRecojo();x.setFecha(fecha(r.getParameter("fecha")));x.setDistrito(texto(r,"distrito",100,true));x.setHorario(texto(r,"horario",200,true));x.setResponsable(texto(r,"responsable",120,true));x.setObservaciones(texto(r,"observaciones",1000,false));ident=service.crearRuta(x);}
  else {ident=id(r.getParameter("id"));if(accion.equals("estado"))service.estadoRuta(ident,opcion(r.getParameter("estado"),"EN_PROCESO","COMPLETADA","CANCELADA"));else {
   if(accion.equals("parada") && !"on".equals(r.getParameter("horariosConfirmados")))throw new IllegalArgumentException("Confirma que revisaste la disponibilidad horaria de la parada.");
   service.parada(ident,id(r.getParameter("solicitudId")),id(r.getParameter("orden")),accion.equals("quitar"),"on".equals(r.getParameter("mixto")));}}
  return ok(r,"/admin/logistica/rutas?id="+ident,"Ruta actualizada. El orden de las paradas lo decide el administrador.");
 }
 @GetMapping("/historial") public String historial(HttpServletRequest r,Model m) throws SQLException {
  int uid=usuario(r);boolean esAdmin=admin(r);if(esAdmin)uid=id(r.getParameter("usuarioId"));
  m.addAttribute("administrador",esAdmin);m.addAttribute("donaciones",new DonacionDAO().historial(uid));
  m.addAttribute("eventos",service.comportamiento(uid));
  if(esAdmin){m.addAttribute("cuenta",new UsuarioDAO().buscar(uid));m.addAttribute("cambiosCuenta",service.historialCuenta(uid));}
  return "historial";
 }
 @PostMapping("/cuenta") public String cuenta(HttpServletRequest r) throws SQLException {
  exigirAdmin(r);int uid=id(r.getParameter("usuarioId"));service.cuenta(uid,opcion(r.getParameter("estado"),"ACTIVO","INACTIVO"),texto(r,"motivo",1000,true),usuario(r));return ok(r,"/admin/logistica/historial?usuarioId="+uid,"Decisión administrativa registrada.");
 }
 @PostMapping("/cerrar-donacion") public String cerrar(HttpServletRequest r) throws SQLException {
  exigirAdmin(r);service.cerrarDonacion(id(r.getParameter("id")),r.getParameter("estado"),texto(r,"motivo",1000,true),usuario(r));return ok(r,"/admin/donaciones","Resultado y motivo registrados en el historial.");
 }
 @GetMapping("/alertas") public String alertas(HttpServletRequest r,Model m) throws SQLException {exigirAdmin(r);m.addAttribute("alertas",service.alertas());return "alertas";}
 @ExceptionHandler(IllegalArgumentException.class) public String validacion(IllegalArgumentException e,HttpServletResponse s,Model m){s.setStatus(400);m.addAttribute("error",e.getMessage());return "error";}
 @ExceptionHandler(SQLException.class) public String persistencia(SQLException e,HttpServletRequest r,HttpServletResponse s,Model m){r.getServletContext().log("Error de logística",e);s.setStatus(500);m.addAttribute("error","No se pudo completar la operación. Comprueba la migración T3, los permisos y la conexión MySQL.");return "error";}
}
