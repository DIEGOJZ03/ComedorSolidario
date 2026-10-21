package com.comedorsolidario.util;

import java.text.Normalizer;
import java.util.*;

/** Catálogo compartido por administración y búsqueda pública. Fuentes y alcance en README. */
public final class UbicacionComedor {
    private static final Map<String, List<String>> DISTRITOS;
    static {
        Map<String, List<String>> datos = new LinkedHashMap<>();
        datos.put("Lima", List.of(
                "Ancón", "Ate", "Barranco", "Breña", "Carabayllo", "Cercado de Lima",
                "Chaclacayo", "Chorrillos", "Cieneguilla", "Comas", "El Agustino",
                "Independencia", "Jesús María", "La Molina", "La Victoria", "Lince",
                "Los Olivos", "Lurigancho", "Lurín", "Magdalena del Mar", "Miraflores",
                "Pachacámac", "Pucusana", "Pueblo Libre", "Puente Piedra", "Punta Hermosa",
                "Punta Negra", "Rímac", "San Bartolo", "San Borja", "San Isidro",
                "San Juan de Lurigancho", "San Juan de Miraflores", "San Luis",
                "San Martín de Porres", "San Miguel", "Santa Anita", "Santa María del Mar",
                "Santa Rosa", "Santiago de Surco", "Surquillo", "Villa El Salvador",
                "Villa María del Triunfo"));
        // El proyecto ya contiene un comedor de Callao; no se mezcla con la provincia de Lima.
        datos.put("Callao", List.of("Bellavista", "Callao", "Carmen de la Legua Reynoso",
                "La Perla", "La Punta", "Mi Perú", "Ventanilla"));
        DISTRITOS = Collections.unmodifiableMap(datos);
    }

    private UbicacionComedor() {}

    public static Map<String, List<String>> distritos() { return DISTRITOS; }

    public static String provincia(String valor) {
        for (String provincia : DISTRITOS.keySet())
            if (clave(provincia).equals(clave(valor))) return provincia;
        throw new IllegalArgumentException("Selecciona una provincia válida: Lima o Callao.");
    }

    public static String distrito(String provincia, String valor) {
        String buscado = clave(valor);
        if ("Lima".equals(provincia)) {
            if ("lima".equals(buscado)) buscado = clave("Cercado de Lima");
            if ("chosica".equals(buscado) || "lurigancho-chosica".equals(buscado)
                    || "lurigancho (chosica)".equals(buscado)) buscado = "lurigancho";
        }
        for (String distrito : DISTRITOS.getOrDefault(provincia, List.of()))
            if (clave(distrito).equals(buscado)) return distrito;
        throw new IllegalArgumentException("Selecciona un distrito que pertenezca a la provincia elegida.");
    }

    private static String clave(String valor) {
        return Normalizer.normalize(valor == null ? "" : valor.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT);
    }
}
