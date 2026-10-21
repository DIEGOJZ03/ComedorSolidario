package com.comedorsolidario.model;

import java.math.BigDecimal;
import java.util.List;

/** Fila calculada del catálogo de necesidades. No representa inventario. */
public final class ResumenProducto {
    private static final BigDecimal LITROS_POR_ENVASE = new BigDecimal("20");
    private final String nombre;
    private final String unidad;
    private final BigDecimal cantidadTotal;
    private final List<Necesidad> necesidades;

    public ResumenProducto(String nombre, String unidad, List<Necesidad> necesidades) {
        this.nombre = nombre;
        this.unidad = unidad;
        this.necesidades = List.copyOf(necesidades);
        this.cantidadTotal = necesidades.stream().map(Necesidad::getCantidadNecesaria)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public String getNombre() { return nombre; }
    public String getUnidad() { return unidad; }
    public BigDecimal getCantidadTotal() { return cantidadTotal; }
    public List<Necesidad> getNecesidades() { return necesidades; }
    public BigDecimal getEnvases20Litros() {
        return "litros".equals(unidad) ? cantidadTotal.divideToIntegralValue(LITROS_POR_ENVASE) : BigDecimal.ZERO;
    }
    public BigDecimal getLitrosRestantes() {
        return "litros".equals(unidad) ? cantidadTotal.remainder(LITROS_POR_ENVASE) : BigDecimal.ZERO;
    }
}
