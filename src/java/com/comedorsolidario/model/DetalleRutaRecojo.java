package com.comedorsolidario.model;
import java.time.*;
public class DetalleRutaRecojo {
    private int rutaId;
    private int solicitudId;
    private int orden;
    private String direccion;
    private String distrito;
    private String horario;
    private String responsable;
    private String estado;
    private String donador;
    public DetalleRutaRecojo() {}
    public int getRutaId() { return rutaId; }
    public void setRutaId(int value) { rutaId=value; }
    public int getSolicitudId() { return solicitudId; }
    public void setSolicitudId(int value) { solicitudId=value; }
    public int getOrden() { return orden; }
    public void setOrden(int value) { orden=value; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String value) { direccion=value; }
    public String getDistrito() { return distrito; }
    public void setDistrito(String value) { distrito=value; }
    public String getHorario() { return horario; }
    public void setHorario(String value) { horario=value; }
    public String getResponsable() { return responsable; }
    public void setResponsable(String value) { responsable=value; }
    public String getEstado() { return estado; }
    public void setEstado(String value) { estado=value; }
    public String getDonador() { return donador; }
    public void setDonador(String value) { donador=value; }
}
