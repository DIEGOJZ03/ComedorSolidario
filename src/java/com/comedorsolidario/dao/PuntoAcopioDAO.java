package com.comedorsolidario.dao;
import com.comedorsolidario.model.PuntoAcopio;
import java.sql.*; import java.util.*;
public class PuntoAcopioDAO extends JdbcDAO {
 public List<PuntoAcopio> listar(boolean activos) throws SQLException { return consultar("SELECT * FROM puntos_acopio WHERE (?=FALSE OR estado='ACTIVO') ORDER BY distrito,nombre",this::map,activos); }
 public PuntoAcopio buscar(int id) throws SQLException { return consultar("SELECT * FROM puntos_acopio WHERE id=?",this::map,id).stream().findFirst().orElseThrow(()->new IllegalArgumentException("Punto inexistente.")); }
 public void guardar(PuntoAcopio p) throws SQLException {
  if(p.getId()==0) insertar("INSERT INTO puntos_acopio(nombre,direccion,provincia,distrito,zona,referencia,horarios,responsable,telefono,estado) VALUES(?,?,?,?,?,?,?,?,?,?)",p.getNombre(),p.getDireccion(),p.getProvincia(),p.getDistrito(),p.getZona(),p.getReferencia(),p.getHorarios(),p.getResponsable(),p.getTelefono(),p.getEstado());
  else if(ejecutar("UPDATE puntos_acopio SET nombre=?,direccion=?,provincia=?,distrito=?,zona=?,referencia=?,horarios=?,responsable=?,telefono=?,estado=? WHERE id=?",p.getNombre(),p.getDireccion(),p.getProvincia(),p.getDistrito(),p.getZona(),p.getReferencia(),p.getHorarios(),p.getResponsable(),p.getTelefono(),p.getEstado(),p.getId())!=1) throw new IllegalArgumentException("Punto inexistente.");
 }
 public void estado(int id,String estado) throws SQLException {if(ejecutar("UPDATE puntos_acopio SET estado=? WHERE id=?",estado,id)!=1)throw new IllegalArgumentException("Punto inexistente.");}
 public void eliminar(int id) throws SQLException {eliminarRegistro("DELETE FROM puntos_acopio WHERE id=?",id,"El punto tiene donaciones relacionadas. Puedes desactivarlo.");}
 private PuntoAcopio map(ResultSet r) throws SQLException {
  PuntoAcopio p=new PuntoAcopio(); p.setId(r.getInt("id"));
  p.setNombre(r.getString("nombre"));
  p.setDireccion(r.getString("direccion"));
  p.setProvincia(r.getString("provincia"));
  p.setDistrito(r.getString("distrito"));
  p.setZona(r.getString("zona"));
  p.setReferencia(r.getString("referencia"));
  p.setHorarios(r.getString("horarios"));
  p.setResponsable(r.getString("responsable"));
  p.setTelefono(r.getString("telefono"));
  p.setEstado(r.getString("estado"));
  return p;
 }
}
