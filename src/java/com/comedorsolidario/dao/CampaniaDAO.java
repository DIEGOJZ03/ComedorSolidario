package com.comedorsolidario.dao;
import com.comedorsolidario.model.Campania;
import java.sql.*;
import java.util.*;
public class CampaniaDAO extends JdbcDAO {
    /** Borrado físico; las claves foráneas impiden eliminar registros referenciados. */
    public void eliminar(int id) throws SQLException {
        eliminarRegistro("DELETE FROM campanias WHERE id=?", id,
                "La campaña tiene registros relacionados y no se puede eliminar. Puedes desactivarla.");
    }

    public List<Campania> listar() throws SQLException { return consultar("SELECT * FROM campanias ORDER BY id DESC", this::map); }
    public Campania buscar(int id) throws SQLException {
        List<Campania> lista=consultar("SELECT * FROM campanias WHERE id=?",this::map,id);
        return lista.isEmpty()?null:lista.get(0);
    }
    public void guardar(Campania o) throws SQLException {
        if(o.getId()==0) ejecutar("INSERT INTO campanias(comedor_id,nombre,descripcion,fecha_inicio,fecha_fin,estado) VALUES (?,?,?,?,?,?)",o.getComedorId(),o.getNombre(),o.getDescripcion(),o.getFechaInicio(),o.getFechaFin(),o.getEstado());
        else if(ejecutar("UPDATE campanias SET comedor_id=?,nombre=?,descripcion=?,fecha_inicio=?,fecha_fin=?,estado=? WHERE id=?",o.getComedorId(),o.getNombre(),o.getDescripcion(),o.getFechaInicio(),o.getFechaFin(),o.getEstado(),o.getId())!=1) throw new IllegalArgumentException("El registro no existe.");
    }
    public void cambiarEstado(int id,String estado) throws SQLException {
        if(ejecutar("UPDATE campanias SET estado=? WHERE id=?",estado,id)!=1) throw new IllegalArgumentException("El registro no existe.");
    }
    public List<Campania> listarPublicas(int comedorId) throws SQLException {
        return consultar("SELECT ca.* FROM campanias ca JOIN comedores c ON c.id=ca.comedor_id WHERE ca.comedor_id=? AND ca.estado='ACTIVO' AND c.estado='ACTIVO' AND CURRENT_DATE BETWEEN ca.fecha_inicio AND ca.fecha_fin ORDER BY ca.fecha_fin",this::map,comedorId);
    }
    private Campania map(ResultSet rs) throws SQLException {
        Campania o=new Campania();
        o.setId(rs.getInt("id"));
        o.setComedorId(rs.getInt("comedor_id"));
        o.setNombre(rs.getString("nombre"));
        o.setDescripcion(rs.getString("descripcion"));
        o.setFechaInicio(rs.getDate("fecha_inicio").toLocalDate());
        o.setFechaFin(rs.getDate("fecha_fin").toLocalDate());
        o.setEstado(rs.getString("estado"));
        return o;
    }
}
