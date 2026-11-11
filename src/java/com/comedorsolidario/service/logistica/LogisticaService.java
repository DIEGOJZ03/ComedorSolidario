package com.comedorsolidario.service.logistica;
import com.comedorsolidario.dao.LogisticaDAO;
import com.comedorsolidario.model.*;
import java.sql.SQLException;import java.time.*;import java.util.*;
import org.springframework.stereotype.Service;
/** Servicio inyectado por Spring MVC; también disponible para los Servlets conservados. */
@Service
public class LogisticaService {
    private final LogisticaDAO dao = new LogisticaDAO();
    public static LocalDate hoy() {return LocalDate.now(ZoneId.of("America/Lima"));}
    public static void fechaFutura(LocalDate f) {if(f==null || f.isBefore(hoy()))throw new IllegalArgumentException("La fecha no puede ser anterior a hoy.");}
    public static boolean terminal(Object s) {return List.of("COMPLETADO","RECHAZADO","CANCELADO","INCUMPLIDO").contains(s);}
    public static String siguienteRecojo(String actual,String accion) {
        return switch(accion) {
            case "aceptar" -> {if(!actual.equals("SOLICITADO"))throw new IllegalArgumentException("Solo se acepta una solicitud pendiente.");yield actual;}
            case "programar" -> {if(!actual.equals("SOLICITADO"))throw new IllegalArgumentException("Solo se programa una solicitud pendiente.");yield "PROGRAMADO";}
            case "recoger" -> {if(!actual.equals("PROGRAMADO"))throw new IllegalArgumentException("Primero programa el recojo.");yield "RECOGIDO";}
            case "completar" -> {if(!actual.equals("RECOGIDO"))throw new IllegalArgumentException("Primero confirma el recojo físico.");yield "COMPLETADO";}
            case "rechazar","cancelar","incumplir" -> {
                if(!List.of("SOLICITADO","PROGRAMADO").contains(actual))throw new IllegalArgumentException("No se puede cerrar una solicitud ya recogida o finalizada.");
                yield accion.equals("rechazar")?"RECHAZADO":accion.equals("cancelar")?"CANCELADO":"INCUMPLIDO";
            }
            default -> throw new IllegalArgumentException("Acción inválida.");
        };
    }
    public void registrar(Donacion d,SolicitudRecojo s) throws SQLException { dao.registrar(d,s); }
    public void registrarMultiples(List<Donacion> donaciones, SolicitudRecojo s) throws SQLException { dao.registrarMultiples(donaciones, s); }
    public void recojo(int id,String accion,LocalDate fecha,String horario,String responsable,String nota,int admin) throws SQLException { dao.recojo(id,accion,fecha,horario,responsable,nota,admin); }
    public void cerrarDonacion(int id,String estado,String motivo,int admin) throws SQLException { dao.cerrarDonacion(id,estado,motivo,admin); }
    public List<Map<String,Object>> comportamiento(int usuario) throws SQLException { return dao.comportamiento(usuario); }
    public List<Map<String,Object>> alertas() throws SQLException { return dao.alertas(); }
    public List<Map<String,Object>> historialCuenta(int usuario) throws SQLException { return dao.historialCuenta(usuario); }
    public void cuenta(int usuario,String estado,String motivo,int admin) throws SQLException { dao.cuenta(usuario,estado,motivo,admin); }
    public void guardarDonador(Usuario u,String motivo,int admin) throws SQLException { dao.guardarDonador(u,motivo,admin); }
    public int crearRuta(RutaRecojo r) throws SQLException { return dao.crearRuta(r); }
    public void parada(int ruta,int solicitud,int orden,boolean quitar,boolean mixto) throws SQLException { dao.parada(ruta,solicitud,orden,quitar,mixto); }
    public void estadoRuta(int ruta,String estado) throws SQLException { dao.estadoRuta(ruta,estado); }
}
