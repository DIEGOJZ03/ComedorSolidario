package com.comedorsolidario.dao;
import com.comedorsolidario.model.Producto;
import java.sql.*;
import java.util.*;
public class ProductoDAO extends JdbcDAO {
    /** Borrado físico; las claves foráneas impiden eliminar registros referenciados. */
    public void eliminar(int id) throws SQLException {
        eliminarRegistro("DELETE FROM productos WHERE id=?", id,
                "El producto está vinculado a necesidades. No se eliminó. Puedes desactivarlo o corregir primero las referencias que correspondan.");
    }

    public List<Producto> listar() throws SQLException { return consultar("SELECT * FROM productos ORDER BY id DESC", this::map); }
    public Producto buscar(int id) throws SQLException {
        List<Producto> lista=consultar("SELECT * FROM productos WHERE id=?",this::map,id);
        return lista.isEmpty()?null:lista.get(0);
    }
    public void guardar(Producto o) throws SQLException {
        if(o.getId()==0) ejecutar("INSERT INTO productos(nombre,descripcion,unidad,estado) VALUES (?,?,?,?)",o.getNombre(),o.getDescripcion(),o.getUnidad(),o.getEstado());
        else if(ejecutar("UPDATE productos SET nombre=?,descripcion=?,unidad=?,estado=? WHERE id=?",o.getNombre(),o.getDescripcion(),o.getUnidad(),o.getEstado(),o.getId())!=1) throw new IllegalArgumentException("El registro no existe.");
    }
    public void cambiarEstado(int id,String estado) throws SQLException {
        if(ejecutar("UPDATE productos SET estado=? WHERE id=?",estado,id)!=1) throw new IllegalArgumentException("El registro no existe.");
    }
    
    private Producto map(ResultSet rs) throws SQLException {
        Producto o=new Producto();
        o.setId(rs.getInt("id"));
        o.setNombre(rs.getString("nombre"));
        o.setDescripcion(rs.getString("descripcion"));
        o.setUnidad(rs.getString("unidad"));
        o.setEstado(rs.getString("estado"));
        return o;
    }
}
