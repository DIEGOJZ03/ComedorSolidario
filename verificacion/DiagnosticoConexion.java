import com.comedorsolidario.util.ConexionBD;
import java.sql.*;

/** Diagnóstico de solo lectura. No muestra contraseñas ni datos personales. */
class DiagnosticoConexion {
    public static void main(String[] args) {
        try (Connection cn = ConexionBD.getConnection()) {
            System.out.println("CONEXION OK: " + cn.getCatalog());
            for (String table : new String[] {"usuarios", "comedores", "necesidades", "donaciones", "productos", "campanias"}) {
                try (ResultSet rs = cn.getMetaData().getTables(cn.getCatalog(), null, table, new String[] {"TABLE"})) {
                    System.out.println(table + ": " + (rs.next() ? "OK" : "FALTA"));
                }
            }
            for (String[] col : new String[][] {{"necesidades", "producto_id"}, {"donaciones", "activo"}}) {
                try (ResultSet rs = cn.getMetaData().getColumns(cn.getCatalog(), null, col[0], col[1])) {
                    System.out.println(col[0] + "." + col[1] + ": " + (rs.next() ? "OK" : "FALTA"));
                }
            }
        } catch (SQLException e) {
            System.out.println("CONEXION FALLIDA: SQLState=" + e.getSQLState() + "; codigo=" + e.getErrorCode());
            System.out.println(e.getMessage());
            Throwable causa = e.getCause();
            while (causa != null) {
                System.out.println("Causa: " + causa.getClass().getSimpleName() + ": " + causa.getMessage());
                causa = causa.getCause();
            }
            System.exit(1);
        }
    }
}
