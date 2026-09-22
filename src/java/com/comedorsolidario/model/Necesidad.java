package com.comedorsolidario.model;

import java.math.BigDecimal;

/** JavaBean: representa datos; no ejecuta consultas SQL. */
public class Necesidad {
    private int id;
    private int comedorId;
    private String nombre;
    private String descripcion;
    private BigDecimal cantidadNecesaria;
    private String unidad;
    private String prioridad;
    private String estado;
    private String comedorNombre;

    public Necesidad() {}

    public Necesidad(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getComedorId() {
        return comedorId;
    }

    public void setComedorId(int comedorId) {
        this.comedorId = comedorId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getCantidadNecesaria() {
        return cantidadNecesaria;
    }

    public void setCantidadNecesaria(BigDecimal cantidadNecesaria) {
        this.cantidadNecesaria = cantidadNecesaria;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getComedorNombre() {
        return comedorNombre;
    }

    public void setComedorNombre(String comedorNombre) {
        this.comedorNombre = comedorNombre;
    }
}
