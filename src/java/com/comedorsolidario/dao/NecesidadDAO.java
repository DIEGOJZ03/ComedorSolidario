package com.comedorsolidario.dao;

import com.comedorsolidario.model.Necesidad;
import java.sql.*;
import java.util.*;

public class NecesidadDAO extends JdbcDAO {
    private static final String SELECT =
            "SELECT n.*,c.nombre AS comedor_nombre FROM necesidades n JOIN comedores c ON c.id=n.comedor_id";

    public List<Necesidad> listar(Integer comedorId, boolean activas) throws SQLException {
        String sql = SELECT + " WHERE 1=1";
        List<Object> parametros = new ArrayList<>();
        if (comedorId != null) {
            sql += " AND n.comedor_id=?";
            parametros.add(comedorId);
        }
        if (activas) sql += " AND n.estado='ACTIVA' AND c.estado='ACTIVO'";
        return consultar(
                sql + " ORDER BY FIELD(n.prioridad,'ALTA','MEDIA','BAJA'),n.id DESC",
                this::map,
                parametros.toArray());
    }

    public Necesidad buscar(int id) throws SQLException {
        List<Necesidad> lista = consultar(SELECT + " WHERE n.id=?", this::map, id);
        return lista.isEmpty() ? null : lista.get(0);
    }

    public void guardar(Necesidad n) throws SQLException {
        if (n.getId() == 0) {
            ejecutar(
                    "INSERT INTO necesidades(comedor_id,nombre,descripcion,cantidad_necesaria,unidad,prioridad,estado) VALUES (?,?,?,?,?,?,?)",
                    n.getComedorId(),
                    n.getNombre(),
                    n.getDescripcion(),
                    n.getCantidadNecesaria(),
                    n.getUnidad(),
                    n.getPrioridad(),
                    n.getEstado());
        } else {
            if (ejecutar(
                            "UPDATE necesidades SET comedor_id=?,nombre=?,descripcion=?,cantidad_necesaria=?,unidad=?,prioridad=?,estado=? WHERE id=?",
                            n.getComedorId(),
                            n.getNombre(),
                            n.getDescripcion(),
                            n.getCantidadNecesaria(),
                            n.getUnidad(),
                            n.getPrioridad(),
                            n.getEstado(),
                            n.getId())
                    == 0) throw new IllegalArgumentException("La necesidad no existe.");
        }
    }

    public void cambiarEstado(int id, String estado) throws SQLException {
        if (ejecutar("UPDATE necesidades SET estado=? WHERE id=?", estado, id) == 0)
            throw new IllegalArgumentException("La necesidad no existe.");
    }

    private Necesidad map(ResultSet rs) throws SQLException {
        Necesidad o = new Necesidad();
        o.setId(rs.getInt("id"));
        o.setComedorId(rs.getInt("comedor_id"));
        o.setNombre(rs.getString("nombre"));
        o.setDescripcion(rs.getString("descripcion"));
        o.setCantidadNecesaria(rs.getBigDecimal("cantidad_necesaria"));
        o.setUnidad(rs.getString("unidad"));
        o.setPrioridad(rs.getString("prioridad"));
        o.setEstado(rs.getString("estado"));
        o.setComedorNombre(rs.getString("comedor_nombre"));
        return o;
    }
}
