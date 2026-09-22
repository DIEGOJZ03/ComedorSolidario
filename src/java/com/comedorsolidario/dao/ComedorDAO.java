package com.comedorsolidario.dao;

import com.comedorsolidario.model.Comedor;
import java.sql.*;
import java.util.*;

public class ComedorDAO extends JdbcDAO {
    public List<Comedor> listar(boolean soloActivos, String zona, String distrito, String provincia)
            throws SQLException {
        String sql = "SELECT * FROM comedores WHERE 1=1";
        List<Object> parametros = new ArrayList<>();
        if (soloActivos) sql += " AND estado='ACTIVO'";
        if (zona != null && !zona.isBlank()) {
            sql += " AND LOCATE(?,zona)>0";
            parametros.add(zona);
        }
        if (distrito != null && !distrito.isBlank()) {
            sql += " AND LOCATE(?,distrito)>0";
            parametros.add(distrito);
        }
        if (provincia != null && !provincia.isBlank()) {
            sql += " AND LOCATE(?,provincia)>0";
            parametros.add(provincia);
        }
        return consultar(sql + " ORDER BY nombre", this::map, parametros.toArray());
    }

    public Comedor buscar(int id) throws SQLException {
        List<Comedor> lista = consultar("SELECT * FROM comedores WHERE id=?", this::map, id);
        return lista.isEmpty() ? null : lista.get(0);
    }

    public void guardar(Comedor c) throws SQLException {
        if (c.getId() == 0) {
            ejecutar(
                    "INSERT INTO comedores(nombre,descripcion,direccion,zona,distrito,provincia,responsable,telefono,estado) VALUES (?,?,?,?,?,?,?,?,?)",
                    c.getNombre(),
                    c.getDescripcion(),
                    c.getDireccion(),
                    c.getZona(),
                    c.getDistrito(),
                    c.getProvincia(),
                    c.getResponsable(),
                    c.getTelefono(),
                    c.getEstado());
        } else {
            if (ejecutar(
                            "UPDATE comedores SET nombre=?,descripcion=?,direccion=?,zona=?,distrito=?,provincia=?,responsable=?,telefono=?,estado=? WHERE id=?",
                            c.getNombre(),
                            c.getDescripcion(),
                            c.getDireccion(),
                            c.getZona(),
                            c.getDistrito(),
                            c.getProvincia(),
                            c.getResponsable(),
                            c.getTelefono(),
                            c.getEstado(),
                            c.getId())
                    == 0) throw new IllegalArgumentException("El comedor ya no existe.");
        }
    }

    public void cambiarEstado(int id, String estado) throws SQLException {
        if (ejecutar("UPDATE comedores SET estado=? WHERE id=?", estado, id) == 0)
            throw new IllegalArgumentException("El comedor no existe.");
    }

    public long total(boolean activos) throws SQLException {
        return contar("SELECT COUNT(*) FROM comedores" + (activos ? " WHERE estado='ACTIVO'" : ""));
    }

    private Comedor map(ResultSet rs) throws SQLException {
        Comedor o = new Comedor();
        o.setId(rs.getInt("id"));
        o.setNombre(rs.getString("nombre"));
        o.setDescripcion(rs.getString("descripcion"));
        o.setDireccion(rs.getString("direccion"));
        o.setZona(rs.getString("zona"));
        o.setDistrito(rs.getString("distrito"));
        o.setProvincia(rs.getString("provincia"));
        o.setResponsable(rs.getString("responsable"));
        o.setTelefono(rs.getString("telefono"));
        o.setEstado(rs.getString("estado"));
        o.setFechaRegistro(
                rs.getTimestamp("fecha_registro") == null
                        ? null
                        : rs.getTimestamp("fecha_registro").toLocalDateTime());
        return o;
    }
}
