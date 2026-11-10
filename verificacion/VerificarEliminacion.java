import com.comedorsolidario.dao.*;
import java.sql.*;
import java.util.*;

/** Pruebas aisladas: intercepta SQL, nunca conecta ni modifica una base real. */
class VerificarEliminacion {
    static int checks;
    static String sql;
    static Object[] params;
    static int rows = 1;
    static SQLException failure;
    interface Action { void run() throws SQLException; }
    static int capture(String query, Object... values) throws SQLException {
        sql = query; params = values;
        if (failure != null) throw failure;
        return rows;
    }
    static void check(boolean ok, String message) {
        if (!ok) throw new AssertionError(message);
        checks++;
    }
    static void reject(Action action, String messagePart) throws SQLException {
        try { action.run(); throw new AssertionError("Se esperaba rechazo"); }
        catch (IllegalArgumentException e) {
            check(e.getMessage().contains(messagePart), "Mensaje incorrecto: " + e.getMessage());
        }
    }
    public static void main(String[] args) throws SQLException {
        ComedorDAO comedor = new ComedorDAO() {
            protected int ejecutar(String s, Object... p) throws SQLException { return capture(s,p); }
        };
        NecesidadDAO necesidad = new NecesidadDAO() {
            protected int ejecutar(String s, Object... p) throws SQLException { return capture(s,p); }
        };
        UsuarioDAO usuario = new UsuarioDAO() {
            protected int ejecutar(String s, Object... p) throws SQLException { return capture(s,p); }
        };
        DonacionDAO donacion = new DonacionDAO() {
            protected int ejecutar(String s, Object... p) throws SQLException { return capture(s,p); }
        };
        ProductoDAO producto = new ProductoDAO() {
            protected int ejecutar(String s, Object... p) throws SQLException { return capture(s,p); }
        };
        CampaniaDAO campania = new CampaniaDAO() {
            protected int ejecutar(String s, Object... p) throws SQLException { return capture(s,p); }
        };
        List<Action> actions = List.of(() -> comedor.eliminar(17), () -> necesidad.eliminar(17),
            () -> usuario.eliminar(17), () -> donacion.eliminar(17),
            () -> producto.eliminar(17), () -> campania.eliminar(17));
        String[] tables = {"comedores", "necesidades", "usuarios", "donaciones", "productos", "campanias"};
        for (int i = 0; i < actions.size(); i++) {
            actions.get(i).run();
            check(sql.startsWith("DELETE FROM " + tables[i] + " WHERE id=?"), "Debe ejecutar DELETE parametrizado");
            check(Arrays.equals(params, new Object[]{17}), "ID separado de la sentencia SQL");
            if (i == 2) check(sql.contains("AND rol='DONADOR'"), "Debe proteger cuentas ADMIN");
            rows = 0;
            reject(actions.get(i), "no existe o está protegido");
            rows = 1;
            failure = new SQLIntegrityConstraintViolationException("foreign key", "23000", 1451);
            reject(actions.get(i), i == 4 ? "vinculado" : "relacionad");
            failure = null;
        }
        sql = null;
        reject(() -> comedor.eliminar(0), "Identificador");
        check(sql == null, "No ejecutar SQL con ID inválido");
        failure = new SQLException("denied", "42000", 1142);
        reject(() -> producto.eliminar(17), "permiso DELETE");
        failure = new SQLException("connection failed", "08001");
        try { producto.eliminar(17); throw new AssertionError("Debe propagar fallo de conexión"); }
        catch (SQLException e) { check(e == failure, "Conservar excepción SQL original"); }
        failure = null;
        donacion.cambiarVigencia(17, false);
        check(sql.equals("UPDATE donaciones SET activo=? WHERE id=?"), "Desactivar no debe borrar");
        check(Arrays.equals(params, new Object[]{false,17}), "Desactivación parametrizada");
        donacion.cambiarVigencia(17, true);
        check(Arrays.equals(params, new Object[]{true,17}), "Reactivación parametrizada");
        rows = 0;
        reject(() -> donacion.cambiarVigencia(17, true), "no existe");
        System.out.println("OK: " + checks + " aserciones; sin conexión ni cambios a MySQL.");
    }
}
