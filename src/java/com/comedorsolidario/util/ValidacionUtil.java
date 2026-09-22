package com.comedorsolidario.util;

import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Locale;

public final class ValidacionUtil {
    private ValidacionUtil() {}

    public static String texto(HttpServletRequest r, String campo, int max, boolean obligatorio) {
        String valor = r.getParameter(campo);
        valor = valor == null ? "" : valor.trim();
        if ((obligatorio && valor.isEmpty()) || valor.length() > max)
            throw new IllegalArgumentException(
                    "Revisa el campo " + campo + " (máximo " + max + " caracteres).");
        return valor;
    }

    public static int id(String valor) {
        try {
            int id = Integer.parseInt(valor);
            if (id > 0) return id;
        } catch (RuntimeException ignored) {
        }
        throw new IllegalArgumentException("Identificador inválido.");
    }

    public static String email(HttpServletRequest r) {
        String email = texto(r, "email", 150, true).toLowerCase(Locale.ROOT);
        if (!email.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+"))
            throw new IllegalArgumentException("Escribe un correo válido.");
        return email;
    }

    public static String password(HttpServletRequest r) {
        String valor = r.getParameter("password");
        if (valor == null || valor.length() < 8 || valor.length() > 128)
            throw new IllegalArgumentException(
                    "La contraseña debe tener entre 8 y 128 caracteres.");
        return valor;
    }

    public static BigDecimal cantidad(String valor) {
        try {
            BigDecimal n = new BigDecimal(valor);
            if (n.signum() > 0
                    && n.compareTo(new BigDecimal("9999999999.99")) <= 0
                    && n.scale() <= 2) return n;
        } catch (RuntimeException ignored) {
        }
        throw new IllegalArgumentException(
                "La cantidad debe ser mayor que cero y tener como máximo dos decimales.");
    }

    public static String opcion(String valor, String... permitidas) {
        if (!Arrays.asList(permitidas).contains(valor))
            throw new IllegalArgumentException("Selecciona una opción válida.");
        return valor;
    }
}
