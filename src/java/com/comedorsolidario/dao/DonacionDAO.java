package com.comedorsolidario.dao;

import com.comedorsolidario.model.Donacion;
import java.sql.*;
import java.util.*;

public class DonacionDAO extends JdbcDAO {
    /** Borrado físico; las claves foráneas impiden eliminar registros referenciados. */
    public void eliminar(int id) throws SQLException {
        eliminarRegistro("DELETE FROM donaciones WHERE id=?", id,
                "La donación tiene registros relacionados y no se puede eliminar. Puedes desactivarla.");
    }

    private static final String SELECT =
            "SELECT d.*,p.nombre punto_acopio_nombre,s.estado estado_recojo,c.nombre AS comedor_nombre,CONCAT(u.nombre,' ',u.apellido) AS donante_nombre FROM donaciones d JOIN comedores c ON c.id=d.comedor_id JOIN usuarios u ON u.id=d.donante_id LEFT JOIN puntos_acopio p ON p.id=d.punto_acopio_id LEFT JOIN solicitudes_recojo s ON s.donacion_id=d.id";

    public List<Donacion> listar(Integer donanteId, String estado) throws SQLException {
        String sql = SELECT + " WHERE d.activo=TRUE";
        List<Object> parametros = new ArrayList<>();
        if (donanteId != null) {
            sql += " AND d.donante_id=?";
            parametros.add(donanteId);
        }
        if (estado != null && !estado.isBlank()) {
            sql += " AND d.estado=?";
            parametros.add(estado);
        }
        return consultar(sql + " ORDER BY d.id DESC", this::map, parametros.toArray());
    }

    public List<Donacion> listarAdministracion(String estado) throws SQLException {
        String sql = SELECT;
        if (estado != null && !estado.isBlank())
            return consultar(sql + " WHERE d.estado=? ORDER BY d.id DESC", this::map, estado);
        return consultar(sql + " ORDER BY d.id DESC", this::map);
    }

    public void cambiarVigencia(int id, boolean activo) throws SQLException {
        transaccion(c->{
            var d=fila(c,"SELECT donante_id,estado FROM donaciones WHERE id=?",id);
            if(activo && List.of("PENDIENTE","ACEPTADA","PROGRAMADA").contains(d.get("estado")))comprobarCupo(c,((Number)d.get("donante_id")).intValue(),id);
            cambio(c,"UPDATE donaciones SET activo=? WHERE id=?",activo,id);return null;
        });
    }

    public Donacion buscar(int id) throws SQLException {
        List<Donacion> lista = consultar(SELECT + " WHERE d.id=?", this::map, id);
        return lista.isEmpty() ? null : lista.get(0);
    }

    public void registrar(Donacion d) throws SQLException { transaccion(c->{registrar(c,d);return null;}); }
    public void registrar(Connection c,Donacion d) throws SQLException {
        comprobarCupo(c,d.getDonanteId(),0);
        String modalidad=d.getModalidad()==null?"DIRECTA":d.getModalidad();
        com.comedorsolidario.util.ValidacionUtil.opcion(modalidad,"DIRECTA","ACOPIO","RECOJO");
        if("ACOPIO".equals(modalidad)) {
            var p=fila(c,"SELECT estado FROM puntos_acopio WHERE id=? FOR UPDATE",d.getPuntoAcopioId());
            if(!"ACTIVO".equals(p.get("estado")))throw new IllegalArgumentException("Selecciona un punto de acopio activo.");
        } else d.setPuntoAcopioId(null);
        d.setId(alta(c,"INSERT INTO donaciones(donante_id,comedor_id,tipo,cantidad,unidad,descripcion,estado,modalidad,punto_acopio_id) SELECT ?,id,?,?,?,?,'PENDIENTE',?,? FROM comedores WHERE id=? AND estado='ACTIVO'",d.getDonanteId(),d.getTipo(),d.getCantidad(),d.getUnidad(),d.getDescripcion(),modalidad,d.getPuntoAcopioId(),d.getComedorId()));
    }
    public static void comprobarCupo(Connection c,int usuario,int excluir) throws SQLException {
        var u=fila(c,"SELECT nombre,apellido,email,telefono,estado,rol FROM usuarios WHERE id=? FOR UPDATE",usuario);
        if(!"ACTIVO".equals(u.get("estado")) || !"DONADOR".equals(u.get("rol")))throw new IllegalArgumentException("La cuenta del donador debe estar activa.");
        for(String k:List.of("nombre","apellido","email"))if(u.get(k)==null || u.get(k).toString().isBlank())throw new IllegalArgumentException("Completa los datos del perfil antes de donar.");
        com.comedorsolidario.util.ValidacionUtil.telefono((String)u.get("telefono"));
        int n=filas(c,"SELECT id FROM donaciones WHERE donante_id=? AND id<>? AND activo=TRUE AND estado IN ('PENDIENTE','ACEPTADA','PROGRAMADA') FOR UPDATE",usuario,excluir).size();
        if(n>=2)throw new IllegalArgumentException("Ya tienes 2 donaciones activas. Completa tus donaciones pendientes antes de registrar otra.");
    }
    public List<Donacion> historial(int usuario) throws SQLException {return consultar(SELECT+" WHERE d.donante_id=? ORDER BY d.id DESC",this::map,usuario);}
    public void actualizar(Donacion d) throws SQLException {
        transaccion(c->{
            comprobarCupo(c,d.getDonanteId(),d.getId());
            var anterior=fila(c,"SELECT donante_id,modalidad FROM donaciones WHERE id=? FOR UPDATE",d.getId());
            if("RECOJO".equals(anterior.get("modalidad")) && ((Number)anterior.get("donante_id")).intValue()!=d.getDonanteId())
                throw new IllegalArgumentException("Un recojo conserva su donador porque la dirección pertenece a su solicitud.");
            int filas=cambio(c,"UPDATE donaciones SET donante_id=?,comedor_id=?,tipo=?,cantidad=?,unidad=?,descripcion=? WHERE id=? AND estado='PENDIENTE' AND activo=TRUE AND EXISTS(SELECT 1 FROM comedores WHERE id=? AND estado='ACTIVO')",
                d.getDonanteId(),d.getComedorId(),d.getTipo(),d.getCantidad(),d.getUnidad(),d.getDescripcion(),d.getId(),d.getComedorId());
            if(filas!=1)throw new IllegalArgumentException("Solo se pueden editar donaciones pendientes y vigentes con donador y comedor activos.");
            return null;
        });
    }
    public void darBaja(int id) throws SQLException {
        if(ejecutar("UPDATE donaciones SET activo=FALSE WHERE id=? AND activo=TRUE",id)!=1)
            throw new IllegalArgumentException("La donación no existe o ya está de baja.");
    }
    public boolean avanzar(int id, String estadoActual) throws SQLException {
        String siguiente;
        if ("PENDIENTE".equals(estadoActual)) siguiente = "ACEPTADA";
        else if ("ACEPTADA".equals(estadoActual)) siguiente = "ENTREGADA";
        else throw new IllegalArgumentException("Esta donación no admite otro cambio de estado.");
        // Actualización condicional: dos administradores no pueden avanzar el mismo paso dos veces.
        return ejecutar(
                        "UPDATE donaciones SET estado=?,fecha_entrega=CASE WHEN ?='ENTREGADA' THEN CURRENT_TIMESTAMP ELSE NULL END WHERE id=? AND estado=? AND activo=TRUE AND modalidad<>'RECOJO'",
                        siguiente,
                        siguiente,
                        id,
                        estadoActual)
                == 1;
    }

    public Map<String, Long> resumen(Integer donanteId) throws SQLException {
        Map<String, Long> datos = new LinkedHashMap<>();
        for (String e : List.of("PENDIENTE", "ACEPTADA", "PROGRAMADA", "ENTREGADA", "RECHAZADA", "CANCELADA", "INCUMPLIDA")) datos.put(e, 0L);
        String sql =
                "SELECT estado,COUNT(*) AS cantidad FROM donaciones WHERE activo=TRUE"
                        + (donanteId == null ? "" : " AND donante_id=?")
                        + " GROUP BY estado";
        Object[] params = donanteId == null ? new Object[0] : new Object[] {donanteId};
        for (Map.Entry<String, Long> fila :
                consultar(sql, rs -> Map.entry(rs.getString(1), rs.getLong(2)), params))
            datos.put(fila.getKey(), fila.getValue());
        datos.put("TOTAL", datos.values().stream().mapToLong(Long::longValue).sum());
        return datos;
    }

    private Donacion map(ResultSet rs) throws SQLException {
        Donacion o = new Donacion();
        o.setModalidad(rs.getString("modalidad"));o.setPuntoAcopioId((Integer)rs.getObject("punto_acopio_id"));o.setPuntoAcopioNombre(rs.getString("punto_acopio_nombre"));o.setMotivo(rs.getString("motivo"));o.setEstadoRecojo(rs.getString("estado_recojo"));
        o.setId(rs.getInt("id"));
        o.setActivo(rs.getBoolean("activo"));
        o.setDonanteId(rs.getInt("donante_id"));
        o.setComedorId(rs.getInt("comedor_id"));
        o.setTipo(rs.getString("tipo"));
        o.setCantidad(rs.getBigDecimal("cantidad"));
        o.setUnidad(rs.getString("unidad"));
        o.setDescripcion(rs.getString("descripcion"));
        o.setEstado(rs.getString("estado"));
        o.setFechaRegistro(
                rs.getTimestamp("fecha_registro") == null
                        ? null
                        : rs.getTimestamp("fecha_registro").toLocalDateTime());
        o.setFechaEntrega(
                rs.getTimestamp("fecha_entrega") == null
                        ? null
                        : rs.getTimestamp("fecha_entrega").toLocalDateTime());
        o.setComedorNombre(rs.getString("comedor_nombre"));
        o.setDonanteNombre(rs.getString("donante_nombre"));
        return o;
    }
}
