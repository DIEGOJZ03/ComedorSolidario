package com.comedorsolidario.dao;
import com.comedorsolidario.model.Producto;
import java.sql.*;
import java.util.*;
public class ProductoDAO extends JdbcDAO {
    private static volatile boolean catalogoVerificado = false;

    /** Inicializa el catálogo base si la tabla productos está vacía. */
    private void asegurarCatalogoBase() throws SQLException {
        if (catalogoVerificado) return;
        synchronized (ProductoDAO.class) {
            if (catalogoVerificado) return;
            if (contar("SELECT COUNT(*) FROM productos") == 0) {
                String sql = "INSERT INTO productos(nombre,descripcion,unidad,estado) VALUES (?,?,?,'ACTIVO')";
                ejecutar(sql, "Arroz", "Arroz blanco o superior en sacos o bolsas selladas", "kg");
                ejecutar(sql, "Aceite vegetal", "Aceite vegetal comestible en botellas o bidones", "litros");
                ejecutar(sql, "Azúcar", "Azúcar rubia o blanca en bolsas selladas", "kg");
                ejecutar(sql, "Menestras (lentejas / frejoles)", "Menestras secas seleccionadas y selladas", "kg");
                ejecutar(sql, "Fideos", "Fideos largos o cortos en paquetes sellados", "kg");
                ejecutar(sql, "Avena", "Hojuelas de avena en bolsas o paquetes", "kg");
                ejecutar(sql, "Conservas de pescado", "Latas de atún,urel o caballa en buen estado", "unidades");
                ejecutar(sql, "Leche evaporada", "Latas o cajas de leche evaporada", "unidades");
                ejecutar(sql, "Agua potable envasada", "Bidones o botellas de agua para consumo", "litros");
                ejecutar(sql, "Ollas y utensilios de cocina", "Ollas, cucharones, platos o cubiertos en buen estado", "unidades");
            }
            catalogoVerificado = true;
        }
    }

    /** Borrado físico; las claves foráneas impiden eliminar registros referenciados. */
    public void eliminar(int id) throws SQLException {
        eliminarRegistro("DELETE FROM productos WHERE id=?", id,
                "El producto está vinculado a necesidades. No se eliminó. Puedes desactivarlo o corregir primero las referencias que correspondan.");
    }

    public List<Producto> listar() throws SQLException {
        asegurarCatalogoBase();
        return consultar("SELECT * FROM productos ORDER BY id DESC", this::map);
    }

    public List<Producto> listarActivos() throws SQLException {
        asegurarCatalogoBase();
        return consultar("SELECT * FROM productos WHERE estado='ACTIVO' ORDER BY nombre ASC", this::map);
    }
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
