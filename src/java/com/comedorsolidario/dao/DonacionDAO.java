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
            "SELECT d.*,c.nombre AS comedor_nombre,CONCAT(u.nombre,' ',u.apellido) AS donante_nombre FROM donaciones d JOIN comedores c ON c.id=d.comedor_id JOIN usuarios u ON u.id=d.donante_id";

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
        if (ejecutar("UPDATE donaciones SET activo=? WHERE id=?", activo, id) != 1)
            throw new IllegalArgumentException("La donación no existe.");
    }

    public Donacion buscar(int id) throws SQLException {
        List<Donacion> lista = consultar(SELECT + " WHERE d.id=?", this::map, id);
        return lista.isEmpty() ? null : lista.get(0);
    }

    public void registrar(Donacion d) throws SQLException {
        // INSERT SELECT vuelve a comprobar comedor y donador activos al escribir.
        int filas =
                ejecutar(
                        "INSERT INTO donaciones(donante_id,comedor_id,tipo,cantidad,unidad,descripcion,estado) "
                                + "SELECT u.id,c.id,?,?,?,?,'PENDIENTE' FROM usuarios u CROSS JOIN comedores c "
                                + "WHERE u.id=? AND u.rol='DONADOR' AND u.estado='ACTIVO' AND c.id=? AND c.estado='ACTIVO'",
                        d.getTipo(),
                        d.getCantidad(),
                        d.getUnidad(),
                        d.getDescripcion(),
                        d.getDonanteId(),
                        d.getComedorId());
        if (filas != 1)
            throw new IllegalArgumentException("El comedor o la cuenta ya no están activos.");
    }

    public void actualizar(Donacion d) throws SQLException {
        int filas=ejecutar("UPDATE donaciones SET donante_id=?,comedor_id=?,tipo=?,cantidad=?,unidad=?,descripcion=? WHERE id=? AND estado='PENDIENTE' AND activo=TRUE AND EXISTS(SELECT 1 FROM usuarios WHERE id=? AND rol='DONADOR' AND estado='ACTIVO') AND EXISTS(SELECT 1 FROM comedores WHERE id=? AND estado='ACTIVO')",
            d.getDonanteId(),d.getComedorId(),d.getTipo(),d.getCantidad(),d.getUnidad(),d.getDescripcion(),d.getId(),d.getDonanteId(),d.getComedorId());
        if(filas!=1)throw new IllegalArgumentException("Solo se pueden editar donaciones pendientes y vigentes con donador y comedor activos.");
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
                        "UPDATE donaciones SET estado=?,fecha_entrega=CASE WHEN ?='ENTREGADA' THEN CURRENT_TIMESTAMP ELSE NULL END WHERE id=? AND estado=? AND activo=TRUE",
                        siguiente,
                        siguiente,
                        id,
                        estadoActual)
                == 1;
    }

    public Map<String, Long> resumen(Integer donanteId) throws SQLException {
        Map<String, Long> datos = new LinkedHashMap<>();
        for (String e : List.of("PENDIENTE", "ACEPTADA", "ENTREGADA")) datos.put(e, 0L);
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
