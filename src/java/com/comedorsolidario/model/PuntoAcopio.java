package com.comedorsolidario.model;
import java.time.*;
public class PuntoAcopio {
    private int id;
    private String nombre;
    private String direccion;
    private String provincia;
    private String distrito;
    private String zona;
    private String referencia;
    private String horarios;
    private String responsable;
    private String telefono;
    private String estado;
    public PuntoAcopio() {}
    public int getId() { return id; }
    public void setId(int value) { id=value; }
    public String getNombre() { return nombre; }
    public void setNombre(String value) { nombre=value; }
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
    public String getHorarios() { return horarios; }
    public void setHorarios(String value) { horarios=value; }
    public String getResponsable() { return responsable; }
    public void setResponsable(String value) { responsable=value; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String value) { telefono=value; }
    public String getEstado() { return estado; }
    public void setEstado(String value) { estado=value; }
}
