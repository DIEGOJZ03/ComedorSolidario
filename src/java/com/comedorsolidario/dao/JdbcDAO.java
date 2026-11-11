package com.comedorsolidario.dao;

import com.comedorsolidario.util.ConexionBD;
import java.sql.*;
import java.util.*;

/** Cierra Connection, PreparedStatement y ResultSet incluso si ocurre un error. */
public abstract class JdbcDAO {
    @FunctionalInterface
    protected interface Mapper<T> {
        T map(ResultSet rs) throws SQLException;
    }

    protected <T> List<T> consultar(String sql, Mapper<T> mapper, Object... parametros)
            throws SQLException {
        try (Connection cn = ConexionBD.getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            asignar(ps, parametros);
            try (ResultSet rs = ps.executeQuery()) {
                List<T> lista = new ArrayList<>();
                while (rs.next()) lista.add(mapper.map(rs));
                return lista;
            }
        }
    }

    protected int ejecutar(String sql, Object... parametros) throws SQLException {
        try (Connection cn = ConexionBD.getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            asignar(ps, parametros);
            return ps.executeUpdate();
        }
    }

    protected int insertar(String sql, Object... parametros) throws SQLException {
        try (Connection cn = ConexionBD.getConnection();
                PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            asignar(ps, parametros);
            if (ps.executeUpdate() != 1) throw new SQLException("No se pudo crear el registro.");
            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (!claves.next()) throw new SQLException("No se obtuvo el ID del registro.");
                return claves.getInt(1);
            }
        }
    }

    protected void eliminarRegistro(String sql, int id, String mensajeRelacionado)
            throws SQLException {
        if (id <= 0) throw new IllegalArgumentException("Identificador inválido.");
        try {
            if (ejecutar(sql, id) != 1)
                throw new IllegalArgumentException("El registro no existe o está protegido.");
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new IllegalArgumentException(mensajeRelacionado, e);
        } catch (SQLException e) {
            if (e.getErrorCode() == 1142)
                throw new IllegalArgumentException("La cuenta de conexión no tiene permiso DELETE en MySQL. Solicita al administrador de la base que revise sus permisos.", e);
            throw e;
        }
    }

    protected long contar(String sql, Object... parametros) throws SQLException {
        return consultar(sql, rs -> rs.getLong(1), parametros).get(0);
    }

    @FunctionalInterface public interface Trabajo<T> { T ejecutar(Connection cn) throws SQLException; }
    public static <T> T transaccion(Trabajo<T> trabajo) throws SQLException {
        try(Connection cn=ConexionBD.getConnection()) {
            cn.setAutoCommit(false);
            try { T result=trabajo.ejecutar(cn); cn.commit(); return result; }
            catch(SQLException | RuntimeException e) { cn.rollback(); throw e; }
        }
    }
    public static int cambio(Connection cn,String sql,Object... args) throws SQLException {
        try(PreparedStatement ps=cn.prepareStatement(sql)) { asignar(ps,args); return ps.executeUpdate(); }
    }
    public static int alta(Connection cn,String sql,Object... args) throws SQLException {
        try(PreparedStatement ps=cn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)) {
            asignar(ps,args); if(ps.executeUpdate()!=1) throw new IllegalArgumentException("No se pudo registrar: revisa los datos y su vigencia.");
            try(ResultSet rs=ps.getGeneratedKeys()) { if(!rs.next()) throw new SQLException("Sin ID generado"); return rs.getInt(1); }
        }
    }
    public static List<Map<String,Object>> filas(Connection cn,String sql,Object... args) throws SQLException {
        try(PreparedStatement ps=cn.prepareStatement(sql)) {
            asignar(ps,args); try(ResultSet rs=ps.executeQuery()) {
                List<Map<String,Object>> out=new ArrayList<>();
                while(rs.next()) { Map<String,Object> row=new LinkedHashMap<>();
                    for(int i=1;i<=rs.getMetaData().getColumnCount();i++) row.put(rs.getMetaData().getColumnLabel(i),rs.getObject(i)); out.add(row); }
                return out;
            }
        }
    }
    public static Map<String,Object> fila(Connection cn,String sql,Object... args) throws SQLException {
        var rows=filas(cn,sql,args); if(rows.isEmpty()) throw new IllegalArgumentException("El registro no existe."); return rows.get(0);
    }
    private static void asignar(PreparedStatement ps, Object[] parametros) throws SQLException {
        for (int i = 0; i < parametros.length; i++) ps.setObject(i + 1, parametros[i]);
    }
}
