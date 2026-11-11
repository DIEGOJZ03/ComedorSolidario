package com.comedorsolidario.model;
import java.time.*;
public class SolicitudRecojo {
    private int id;
    private int donacionId;
    private String direccion;
    private String provincia;
    private String distrito;
    private String zona;
    private String referencia;
    private String horarioPreferido;
    private String telefono;
    private String horarioProgramado;
    private String responsable;
    private String observaciones;
    private String estado;
    private String donador;
    private String email;
    private String descripcion;
    private String comedor;
    private LocalDate fechaPreferida;
    private LocalDate fechaProgramada;
    public SolicitudRecojo() {}
    public int getId() { return id; }
    public void setId(int value) { id=value; }
    public int getDonacionId() { return donacionId; }
    public void setDonacionId(int value) { donacionId=value; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String value) { direccion=value; }
    public String getProvincia() { return provincia; }
    public void setProvincia(String value) { provincia=value; }
    public String getDistrito() { return distrito; }
    public void setDistrito(String value) { distrito=value; }
    public String getZona() { return zona; }
    public void setZona(String value) { zona=value; }
    public String getReferencia() { return referencia; }
    public void setReferencia(String value) { referencia=value; }
    public String getHorarioPreferido() { return horarioPreferido; }
    public void setHorarioPreferido(String value) { horarioPreferido=value; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String value) { telefono=value; }
    public String getHorarioProgramado() { return horarioProgramado; }
    public void setHorarioProgramado(String value) { horarioProgramado=value; }
    public String getResponsable() { return responsable; }
    public void setResponsable(String value) { responsable=value; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String value) { observaciones=value; }
    public String getEstado() { return estado; }
    public void setEstado(String value) { estado=value; }
    public String getDonador() { return donador; }
    public void setDonador(String value) { donador=value; }
    public String getEmail() { return email; }
    public void setEmail(String value) { email=value; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String value) { descripcion=value; }
    public String getComedor() { return comedor; }
    public void setComedor(String value) { comedor=value; }
    public LocalDate getFechaPreferida() { return fechaPreferida; }
    public void setFechaPreferida(LocalDate value) { fechaPreferida=value; }
    public LocalDate getFechaProgramada() { return fechaProgramada; }
    public void setFechaProgramada(LocalDate value) { fechaProgramada=value; }
}
