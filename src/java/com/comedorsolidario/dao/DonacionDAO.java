package com.comedorsolidario.dao;

import com.comedorsolidario.model.Donacion;
import java.sql.*;
import java.util.*;

public class DonacionDAO extends JdbcDAO {
    private static final String SELECT =
            "SELECT d.*,c.nombre AS comedor_nombre,CONCAT(u.nombre,' ',u.apellido) AS donante_nombre FROM donaciones d JOIN comedores c ON c.id=d.comedor_id JOIN usuarios u ON u.id=d.donante_id";

    public List<Donacion> listar(Integer donanteId, String estado) throws SQLException {
        String sql = SELECT + " WHERE 1=1";
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

    public boolean avanzar(int id, String estadoActual) throws SQLException {
        String siguiente;
        if ("PENDIENTE".equals(estadoActual)) siguiente = "ACEPTADA";
        else if ("ACEPTADA".equals(estadoActual)) siguiente = "ENTREGADA";
        else throw new IllegalArgumentException("Esta donación no admite otro cambio de estado.");
        // Actualización condicional: dos administradores no pueden avanzar el mismo paso dos veces.
        return ejecutar(
                        "UPDATE donaciones SET estado=?,fecha_entrega=CASE WHEN ?='ENTREGADA' THEN CURRENT_TIMESTAMP ELSE NULL END WHERE id=? AND estado=?",
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
                "SELECT estado,COUNT(*) AS cantidad FROM donaciones"
                        + (donanteId == null ? "" : " WHERE donante_id=?")
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
