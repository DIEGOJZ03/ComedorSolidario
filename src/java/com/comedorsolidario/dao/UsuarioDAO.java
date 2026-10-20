package com.comedorsolidario.dao;

import com.comedorsolidario.model.Usuario;
import java.sql.*;
import java.util.*;

public class UsuarioDAO extends JdbcDAO {
    /** Borrado físico; las claves foráneas impiden eliminar registros referenciados. */
    public void eliminar(int id) throws SQLException {
        eliminarRegistro("DELETE FROM usuarios WHERE id=? AND rol='DONADOR'", id,
                "El donador tiene donaciones relacionadas. No se eliminó la cuenta. Puedes desactivarla para conservar su historial.");
    }

    public Usuario buscarEmail(String email) throws SQLException {
        List<Usuario> lista = consultar("SELECT * FROM usuarios WHERE email=?", this::map, email);
        return lista.isEmpty() ? null : lista.get(0);
    }

    public Usuario buscar(int id) throws SQLException {
        List<Usuario> lista = consultar("SELECT * FROM usuarios WHERE id=?", this::map, id);
        return lista.isEmpty() ? null : lista.get(0);
    }

    public List<Usuario> listar() throws SQLException {
        return consultar("SELECT * FROM usuarios ORDER BY id DESC", this::map);
    }

    public void registrar(Usuario u) throws SQLException {
        ejecutar(
                "INSERT INTO usuarios(nombre,apellido,email,password_hash,telefono,rol) VALUES (?,?,?,?,?,'DONADOR')",
                u.getNombre(),
                u.getApellido(),
                u.getEmail(),
                u.getPasswordHash(),
                u.getTelefono());
    }

    public void actualizarPerfil(Usuario u) throws SQLException {
        ejecutar(
                "UPDATE usuarios SET nombre=?,apellido=?,telefono=? WHERE id=? AND rol='DONADOR'",
                u.getNombre(),
                u.getApellido(),
                u.getTelefono(),
                u.getId());
    }

    public void guardarAdministrado(Usuario u) throws SQLException {
        if(u.getId()==0) ejecutar("INSERT INTO usuarios(nombre,apellido,email,password_hash,telefono,rol,estado) VALUES(?,?,?,?,?,'DONADOR',?)",u.getNombre(),u.getApellido(),u.getEmail(),u.getPasswordHash(),u.getTelefono(),u.getEstado());
        else if(ejecutar("UPDATE usuarios SET nombre=?,apellido=?,email=?,telefono=?,estado=? WHERE id=? AND rol='DONADOR'",u.getNombre(),u.getApellido(),u.getEmail(),u.getTelefono(),u.getEstado(),u.getId())!=1)
            throw new IllegalArgumentException("El donador no existe. Las cuentas ADMIN están protegidas.");
    }
    public void cambiarEstado(int id,String estado) throws SQLException {
        if(ejecutar("UPDATE usuarios SET estado=? WHERE id=? AND rol='DONADOR'",estado,id)!=1)
            throw new IllegalArgumentException("El donador no existe. Las cuentas ADMIN están protegidas.");
    }
    public long totalDonadores() throws SQLException {
        return contar("SELECT COUNT(*) FROM usuarios WHERE rol='DONADOR'");
    }

    private Usuario map(ResultSet rs) throws SQLException {
        Usuario o = new Usuario();
        o.setId(rs.getInt("id"));
        o.setNombre(rs.getString("nombre"));
        o.setApellido(rs.getString("apellido"));
        o.setEmail(rs.getString("email"));
        o.setPasswordHash(rs.getString("password_hash"));
        o.setTelefono(rs.getString("telefono"));
        o.setRol(rs.getString("rol"));
        o.setEstado(rs.getString("estado"));
        o.setFechaRegistro(
                rs.getTimestamp("fecha_registro") == null
                        ? null
                        : rs.getTimestamp("fecha_registro").toLocalDateTime());
        return o;
    }
}
