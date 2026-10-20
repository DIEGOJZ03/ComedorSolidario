package com.comedorsolidario.util;

import java.io.InputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class ConexionBD {
    private static final Properties CONFIG = new Properties();

    static {
        try (InputStream in = ConexionBD.class.getResourceAsStream("/db.properties")) {
            if (in != null) CONFIG.load(in);
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (IOException | ClassNotFoundException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private ConexionBD() {}

    private static String valor(String entorno, String clave, String defecto) {
        String valor = System.getenv(entorno);
        return valor != null ? valor : CONFIG.getProperty(clave, defecto);
    }

    public static Connection getConnection() throws SQLException {
        String url =
                valor(
                        "CS_DB_URL",
                        "db.url",
                        "jdbc:mysql://localhost:3306/comedor_solidario?sslMode=DISABLED&allowPublicKeyRetrieval=true&connectionTimeZone=America/Lima");
        String user = valor("CS_DB_USER", "db.user", "comedor_app");
        String password = valor("CS_DB_PASSWORD", "db.password", "");
        return DriverManager.getConnection(url, user, password);
    }
}
