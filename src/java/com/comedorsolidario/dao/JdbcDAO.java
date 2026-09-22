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

    protected long contar(String sql, Object... parametros) throws SQLException {
        return consultar(sql, rs -> rs.getLong(1), parametros).get(0);
    }

    private void asignar(PreparedStatement ps, Object[] parametros) throws SQLException {
        for (int i = 0; i < parametros.length; i++) ps.setObject(i + 1, parametros[i]);
    }
}
