package com.comedorsolidario.model;

import java.time.LocalDateTime;
import java.math.BigDecimal;

/** JavaBean: representa datos; no ejecuta consultas SQL. */
public class Donacion {
    private int id;
    private int donanteId;
    private int comedorId;
    private String tipo;
    private BigDecimal cantidad;
    private String unidad;
    private String descripcion;
    private String estado;
    private LocalDateTime fechaRegistro;
    private LocalDateTime fechaEntrega;
    private String comedorNombre;
    private String donanteNombre;

    public Donacion() {}

    public Donacion(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDonanteId() {
        return donanteId;
    }

    public void setDonanteId(int donanteId) {
        this.donanteId = donanteId;
    }

    public int getComedorId() {
        return comedorId;
    }

    public void setComedorId(int comedorId) {
        this.comedorId = comedorId;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public LocalDateTime getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(LocalDateTime fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public String getComedorNombre() {
        return comedorNombre;
    }

    public void setComedorNombre(String comedorNombre) {
        this.comedorNombre = comedorNombre;
    }

    public String getDonanteNombre() {
        return donanteNombre;
    }

    public void setDonanteNombre(String donanteNombre) {
        this.donanteNombre = donanteNombre;
    }
}
