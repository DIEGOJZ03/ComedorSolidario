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

    public static java.time.LocalDate fecha(String valor) {
        try { java.time.LocalDate f=java.time.LocalDate.parse(valor);
            if(f.getYear()>=1000 && f.getYear()<=9999)return f;
        } catch (RuntimeException ignored) {}
        throw new IllegalArgumentException("Escribe una fecha válida entre los años 1000 y 9999.");
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

    public static String telefono(String valor) {
        String t = valor == null ? "" : valor.trim().replaceAll("[ ()-]", "");
        if (!t.matches("9[0-9]{8}"))
            throw new IllegalArgumentException(
                    "Escribe un teléfono celular válido de 9 dígitos que empiece con 9 (ej. 987654321).");
        return t;
    }

    public static String password(HttpServletRequest r) {
        String valor = r.getParameter("password");
        if (valor == null || valor.length() < 8 || valor.length() > 128)
            throw new IllegalArgumentException(
                    "La contraseña debe tener entre 8 y 128 caracteres.");
        return valor;
    }

    public static BigDecimal cantidad(String valor) {
        return cantidad(valor, "unidades");
    }

    public static BigDecimal cantidad(String valor, String unidad) {
        boolean esDinero = "soles".equalsIgnoreCase(unidad) || "DINERO".equalsIgnoreCase(unidad);
        BigDecimal max = esDinero ? new BigDecimal("5000.00") : new BigDecimal("500.00");
        try {
            BigDecimal n = new BigDecimal(valor);
            if (n.signum() > 0
                    && n.compareTo(max) <= 0
                    && n.scale() <= 2) return n;
        } catch (RuntimeException ignored) {
        }
        throw new IllegalArgumentException(
                esDinero
                        ? "Para dinero (soles), la cantidad debe ser mayor que cero, máximo 5,000 y con hasta dos decimales."
                        : "La cantidad para unidades físicas debe ser mayor que cero, máximo 500 y con hasta dos decimales.");
    }

    public static String opcion(String valor, String... permitidas) {
        if (!Arrays.asList(permitidas).contains(valor))
            throw new IllegalArgumentException("Selecciona una opción válida.");
        return valor;
    }
}
