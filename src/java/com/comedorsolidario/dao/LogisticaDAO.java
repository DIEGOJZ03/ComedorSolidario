package com.comedorsolidario.dao;

import com.comedorsolidario.dao.*;
import com.comedorsolidario.model.*;
import java.sql.*;
import java.time.*;
import java.util.*;
import static com.comedorsolidario.service.logistica.LogisticaService.*;
import static com.comedorsolidario.dao.JdbcDAO.*;

/** Persistencia transaccional de logística; todas las sentencias usan parámetros y la conexión existente. */
public class LogisticaDAO {
    public void registrar(Donacion d,SolicitudRecojo s) throws SQLException {
        registrarMultiples(List.of(d), s);
    }
    public void registrarMultiples(List<Donacion> donaciones, SolicitudRecojo s) throws SQLException {
        transaccion(c->{
            if (donaciones == null || donaciones.isEmpty()) throw new IllegalArgumentException("Registra al menos una donación.");
            DonacionDAO dao = new DonacionDAO();
            SolicitudRecojoDAO recojoDao = new SolicitudRecojoDAO();
            for (Donacion d : donaciones) {
                if ("RECOJO".equals(d.getModalidad()) != (s != null)) throw new IllegalArgumentException("Completa los datos del recojo.");
                dao.registrar(c, d);
                if (s != null) {
                    s.setDonacionId(d.getId());
                    recojoDao.registrar(c, s);
                }
            }
            return null;
        });
    }
    private static void auditar(Connection c,int donacion,int admin,String estado,String motivo) throws SQLException {
        alta(c,"INSERT INTO historial_donacion(donacion_id,administrador_id,estado,motivo) VALUES(?,?,?,?)",donacion,admin,estado,motivo);
    }
    public void recojo(int id,String accion,LocalDate fecha,String horario,String responsable,String nota,int admin) throws SQLException {
        transaccion(c->{
            var s=fila(c,"SELECT * FROM solicitudes_recojo WHERE id=? FOR UPDATE",id);
            int did=((Number)s.get("donacion_id")).intValue();
            var d=fila(c,"SELECT * FROM donaciones WHERE id=? FOR UPDATE",did);
            if(!Boolean.TRUE.equals(d.get("activo")) && !"1".equals(String.valueOf(d.get("activo"))))throw new IllegalArgumentException("Reactiva la donación antes de coordinar el recojo.");
            String next=siguienteRecojo((String)s.get("estado"),accion);
            if(terminal(next) && !"COMPLETADO".equals(next) && nota.isBlank())throw new IllegalArgumentException("Indica el motivo del cierre.");
            String general=switch(next) {case "PROGRAMADO","RECOGIDO" -> "PROGRAMADA";case "COMPLETADO"->"ENTREGADA";case "RECHAZADO"->"RECHAZADA";case "CANCELADO"->"CANCELADA";case "INCUMPLIDO"->"INCUMPLIDA";default->"ACEPTADA";};
            if("aceptar".equals(accion) && !"PENDIENTE".equals(d.get("estado")))throw new IllegalArgumentException("Esta solicitud ya fue aceptada.");
            if("programar".equals(accion)) {
                fechaFutura(fecha);if(horario.isBlank()||responsable.isBlank())throw new IllegalArgumentException("Indica horario y responsable definitivos.");
                cambio(c,"UPDATE solicitudes_recojo SET fecha_programada=?,horario_programado=?,responsable=? WHERE id=?",fecha,horario,responsable,id);
            }
            cambio(c,"UPDATE solicitudes_recojo SET estado=?,observaciones=? WHERE id=?",next,nota,id);
            cambio(c,"UPDATE donaciones SET estado=?,motivo=?,fecha_entrega=CASE WHEN ?='ENTREGADA' THEN CURRENT_TIMESTAMP ELSE NULL END WHERE id=?",general,nota,general,did);
            auditar(c,did,admin,general,nota);return null;
        });
    }
    public void cerrarDonacion(int id,String estado,String motivo,int admin) throws SQLException {
        if(motivo.isBlank())throw new IllegalArgumentException("Indica un motivo.");
        com.comedorsolidario.util.ValidacionUtil.opcion(estado,"RECHAZADA","CANCELADA","INCUMPLIDA");
        transaccion(c->{
            var d=fila(c,"SELECT * FROM donaciones WHERE id=? FOR UPDATE",id);
            if("RECOJO".equals(d.get("modalidad")))throw new IllegalArgumentException("Gestiona este aporte desde Solicitudes de Recojo.");
            if(!List.of("PENDIENTE","ACEPTADA","PROGRAMADA").contains(d.get("estado")))throw new IllegalArgumentException("El aporte ya está finalizado.");
            cambio(c,"UPDATE donaciones SET estado=?,motivo=? WHERE id=?",estado,motivo,id);
            auditar(c,id,admin,estado,motivo);return null;
        });
    }
    public List<Map<String,Object>> comportamiento(int usuario) throws SQLException {
        return transaccion(c->filas(c,"SELECT h.donacion_id,h.estado,h.motivo,h.fecha FROM historial_donacion h JOIN donaciones d ON d.id=h.donacion_id WHERE d.donante_id=? ORDER BY h.fecha DESC,h.id DESC",usuario));
    }
    public List<Map<String,Object>> alertas() throws SQLException {
        return transaccion(c->filas(c,"SELECT u.id,CONCAT(u.nombre,' ',u.apellido) nombre,COUNT(DISTINCT h.donacion_id) incumplimientos FROM usuarios u JOIN donaciones d ON d.donante_id=u.id JOIN historial_donacion h ON h.donacion_id=d.id AND h.estado='INCUMPLIDA' GROUP BY u.id,u.nombre,u.apellido HAVING COUNT(DISTINCT h.donacion_id)>=2"));
    }
    public List<Map<String,Object>> historialCuenta(int usuario) throws SQLException {return transaccion(c->filas(c,"SELECT estado,motivo,fecha FROM historial_usuario WHERE usuario_id=? ORDER BY id DESC",usuario));}
    public void cuenta(int usuario,String estado,String motivo,int admin) throws SQLException {
        if(motivo.isBlank())throw new IllegalArgumentException("Justifica la desactivación o reactivación de la cuenta.");
        transaccion(c->{
            if(cambio(c,"UPDATE usuarios SET estado=? WHERE id=? AND rol='DONADOR'",estado,usuario)!=1)throw new IllegalArgumentException("Cuenta no disponible o protegida.");
            alta(c,"INSERT INTO historial_usuario(usuario_id,administrador_id,estado,motivo) VALUES(?,?,?,?)",usuario,admin,estado,motivo);return null;
        });
    }
    public void guardarDonador(Usuario u,String motivo,int admin) throws SQLException {
        if(u.getId()==0) {new UsuarioDAO().guardarAdministrado(u);return;}
        transaccion(c->{
            var anterior=fila(c,"SELECT estado,rol FROM usuarios WHERE id=? FOR UPDATE",u.getId());
            if(!"DONADOR".equals(anterior.get("rol")))throw new IllegalArgumentException("Las cuentas ADMIN están protegidas.");
            if(!u.getEstado().equals(anterior.get("estado"))) {
                if(motivo.isBlank())throw new IllegalArgumentException("Justifica el cambio de estado de la cuenta.");
                alta(c,"INSERT INTO historial_usuario(usuario_id,administrador_id,estado,motivo) VALUES(?,?,?,?)",u.getId(),admin,u.getEstado(),motivo);
            }
            cambio(c,"UPDATE usuarios SET nombre=?,apellido=?,email=?,telefono=?,estado=? WHERE id=?",u.getNombre(),u.getApellido(),u.getEmail(),u.getTelefono(),u.getEstado(),u.getId());
            return null;
        });
    }
    public int crearRuta(RutaRecojo r) throws SQLException {fechaFutura(r.getFecha());return new RutaRecojoDAO().crear(r);}
    public void parada(int ruta,int solicitud,int orden,boolean quitar,boolean mixto) throws SQLException {
        if(orden<=0)throw new IllegalArgumentException("El orden debe ser positivo.");
        transaccion(c->{
            var r=fila(c,"SELECT * FROM rutas_recojo WHERE id=? FOR UPDATE",ruta);
            if(!"PLANIFICADA".equals(r.get("estado")))throw new IllegalArgumentException("Solo se modifican paradas de rutas planificadas.");
            var s=fila(c,"SELECT s.*,d.activo FROM solicitudes_recojo s JOIN donaciones d ON d.id=s.donacion_id WHERE s.id=? FOR UPDATE",solicitud);
            if(quitar) {cambio(c,"DELETE FROM detalle_ruta_recojo WHERE ruta_id=? AND solicitud_id=?",ruta,solicitud);return null;}
            if(!"PROGRAMADO".equals(s.get("estado")) || !Boolean.TRUE.equals(s.get("activo")))throw new IllegalArgumentException("Selecciona un recojo programado con donación activa.");
            if(!Objects.equals(r.get("fecha"),s.get("fecha_programada")))throw new IllegalArgumentException("La fecha de la ruta debe coincidir con la del recojo.");
            if(!mixto && !r.get("distrito").toString().equalsIgnoreCase(s.get("distrito").toString()))throw new IllegalArgumentException("Marca la autorización para incluir otro distrito.");
            if(!Objects.equals(r.get("responsable"),s.get("responsable")))throw new IllegalArgumentException("El responsable de la ruta y del recojo debe coincidir.");
            if(!filas(c,"SELECT solicitud_id FROM detalle_ruta_recojo WHERE ruta_id=? AND orden=? AND solicitud_id<>?",ruta,orden,solicitud).isEmpty())throw new IllegalArgumentException("Ese orden ya está ocupado; utiliza una posición libre.");
            var otras=filas(c,"SELECT ruta_id FROM detalle_ruta_recojo WHERE solicitud_id=? AND activa=TRUE AND ruta_id<>?",solicitud,ruta);
            if(!otras.isEmpty())throw new IllegalArgumentException("La solicitud ya pertenece a otra ruta activa.");
            if(cambio(c,"UPDATE detalle_ruta_recojo SET orden=? WHERE ruta_id=? AND solicitud_id=?",orden,ruta,solicitud)==0)
                cambio(c,"INSERT INTO detalle_ruta_recojo(ruta_id,solicitud_id,orden) VALUES(?,?,?)",ruta,solicitud,orden);
            return null;
        });
    }
    public void estadoRuta(int ruta,String estado) throws SQLException {
        transaccion(c->{
            var r=fila(c,"SELECT estado FROM rutas_recojo WHERE id=? FOR UPDATE",ruta);
            String actual=(String)r.get("estado");
            boolean valido=(actual.equals("PLANIFICADA") && List.of("EN_PROCESO","CANCELADA").contains(estado)) || (actual.equals("EN_PROCESO") && List.of("COMPLETADA","CANCELADA").contains(estado));
            if(!valido)throw new IllegalArgumentException("Transición de ruta no válida.");
            var paradas=filas(c,"SELECT s.estado,d.activo FROM detalle_ruta_recojo p JOIN solicitudes_recojo s ON s.id=p.solicitud_id JOIN donaciones d ON d.id=s.donacion_id WHERE p.ruta_id=? FOR UPDATE",ruta);
            if(!estado.equals("CANCELADA") && paradas.isEmpty())throw new IllegalArgumentException("Agrega paradas antes de iniciar una ruta.");
            for(var s:paradas) {
                if(estado.equals("EN_PROCESO") && (!"PROGRAMADO".equals(s.get("estado")) || !Boolean.TRUE.equals(s.get("activo"))))throw new IllegalArgumentException("Revisa las paradas: deben estar programadas y activas.");
                if(estado.equals("COMPLETADA") && !terminal(s.get("estado")))throw new IllegalArgumentException("Confirma la llegada al comedor o el cierre de cada solicitud antes de completar la ruta.");
            }
            cambio(c,"UPDATE rutas_recojo SET estado=? WHERE id=?",estado,ruta);
            if(List.of("COMPLETADA","CANCELADA").contains(estado))cambio(c,"UPDATE detalle_ruta_recojo SET activa=FALSE WHERE ruta_id=?",ruta);
            return null;
        });
    }
}
