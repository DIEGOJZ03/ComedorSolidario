package com.comedorsolidario.model;
import java.time.*;
public class RutaRecojo {
    private int id;
    private LocalDate fecha;
    private String distrito;
    private String horario;
    private String responsable;
    private String observaciones;
    private String estado;
    public RutaRecojo() {}
    public int getId() { return id; }
    public void setId(int value) { id=value; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate value) { fecha=value; }
    public String getDistrito() { return distrito; }
    public void setDistrito(String value) { distrito=value; }
    public String getHorario() { return horario; }
    public void setHorario(String value) { horario=value; }
    public String getResponsable() { return responsable; }
    public void setResponsable(String value) { responsable=value; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String value) { observaciones=value; }
    public String getEstado() { return estado; }
    public void setEstado(String value) { estado=value; }
}
