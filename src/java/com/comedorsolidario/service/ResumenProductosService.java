package com.comedorsolidario.service;

import com.comedorsolidario.model.*;
import java.text.Normalizer;
import java.util.*;

/** Calcula una fila por nombre y unidad usando las necesidades como origen. */
public final class ResumenProductosService {
    /** Recalcula el producto seleccionado con datos vigentes; no acepta totales de la URL. */
    public ResumenProducto buscar(List<Necesidad> necesidades, String nombre, String unidad) {
        String nombreBuscado = normalizar(nombre);
        String unidadBuscada = unidad(unidad);
        return resumir(necesidades).stream()
                .filter(p -> normalizar(p.getNombre()).equals(nombreBuscado)
                        && p.getUnidad().equals(unidadBuscada))
                .findFirst().orElse(null);
    }

    public List<ResumenProducto> resumir(List<Necesidad> necesidades) {
        Map<String, List<Necesidad>> solicitudes = new TreeMap<>();
        Map<String, String> nombres = new HashMap<>();
        Map<String, String> unidades = new HashMap<>();
        for (Necesidad n : necesidades) {
            if (!"ACTIVA".equals(n.getEstado())) continue;
            String nombre = n.getNombre().trim().replaceAll("\\s+", " ");
            String unidad = unidad(n.getUnidad());
            String clave = normalizar(nombre) + "\u0000" + unidad;
            solicitudes.computeIfAbsent(clave, k -> new ArrayList<>()).add(n);
            nombres.putIfAbsent(clave, nombre);
            unidades.putIfAbsent(clave, unidad);
        }
        List<ResumenProducto> catalogo = new ArrayList<>();
        solicitudes.forEach((clave, detalle) -> {
            detalle.sort(Comparator.comparing((Necesidad n) -> normalizar(n.getComedorNombre()))
                    .thenComparingInt(Necesidad::getId));
            catalogo.add(new ResumenProducto(nombres.get(clave), unidades.get(clave), detalle));
        });
        return catalogo;
    }

    private String normalizar(String valor) {
        return Normalizer.normalize(valor == null ? "" : valor.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "").replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }

    private String unidad(String valor) {
        return switch (normalizar(valor)) {
            case "kg", "kilo", "kilos", "kilogramo", "kilogramos" -> "kg";
            case "l", "lt", "lts", "litro", "litros" -> "litros";
            case "unidad", "unidades", "und", "uds" -> "unidades";
            case "caja", "cajas" -> "cajas";
            case "sol", "soles", "pen", "s/", "s/." -> "soles";
            default -> normalizar(valor);
        };
    }
}
