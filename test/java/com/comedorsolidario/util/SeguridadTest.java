package com.comedorsolidario.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;

class SeguridadTest {
    @Test
    void contrasenasTienenSalYSeVerifican() {
        String a = PasswordUtil.hash("Prueba2026!");
        String b = PasswordUtil.hash("Prueba2026!");
        assertNotEquals(a, b);
        assertTrue(PasswordUtil.verificar("Prueba2026!", a));
        assertFalse(PasswordUtil.verificar("Incorrecta", a));
        assertFalse(PasswordUtil.verificar("Prueba2026!", "hash-invalido"));
    }

    @Test
    void cantidadesInvalidasNoSeAceptan() {
        for (String v : new String[] {"0", "-1", "NaN", "1.001", "10000000000.00", "texto"})
            assertThrows(IllegalArgumentException.class, () -> ValidacionUtil.cantidad(v));
        assertEquals(new BigDecimal("12.50"), ValidacionUtil.cantidad("12.50"));
    }

    @Test
    void identificadoresYOpcionesSeValidan() {
        assertThrows(IllegalArgumentException.class, () -> ValidacionUtil.id("-1"));
        assertThrows(IllegalArgumentException.class, () -> ValidacionUtil.id("1 OR 1=1"));
        assertThrows(
                IllegalArgumentException.class, () -> ValidacionUtil.opcion("ADMIN", "DONADOR"));
        assertEquals(2, ValidacionUtil.id("2"));
    }
}
