package com.comedorsolidario.util;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/** javax.crypto pertenece a Java SE; no es javax.servlet. */
public final class PasswordUtil {
    private static final int ITERACIONES = 600_000;

    private PasswordUtil() {}

    public static String hash(String password) {
        byte[] sal = new byte[16];
        new SecureRandom().nextBytes(sal);
        return ITERACIONES
                + ":"
                + Base64.getEncoder().encodeToString(sal)
                + ":"
                + Base64.getEncoder().encodeToString(derivar(password, sal, ITERACIONES));
    }

    public static boolean verificar(String password, String almacenado) {
        try {
            String[] partes = almacenado.split(":");
            int iteraciones = Integer.parseInt(partes[0]);
            if (partes.length != 3 || iteraciones < 100_000 || iteraciones > 2_000_000)
                return false;
            byte[] sal = Base64.getDecoder().decode(partes[1]);
            byte[] esperado = Base64.getDecoder().decode(partes[2]);
            return MessageDigest.isEqual(esperado, derivar(password, sal, iteraciones));
        } catch (RuntimeException e) {
            return false;
        }
    }

    private static byte[] derivar(String password, byte[] sal, int iteraciones) {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), sal, iteraciones, 256);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec)
                    .getEncoded();
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo proteger la contraseña", e);
        } finally {
            spec.clearPassword();
        }
    }
}
