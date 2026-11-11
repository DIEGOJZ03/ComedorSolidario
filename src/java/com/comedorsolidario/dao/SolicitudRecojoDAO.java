package com.comedorsolidario.dao;
import com.comedorsolidario.model.SolicitudRecojo;
import java.sql.*; import java.util.*;
public class SolicitudRecojoDAO extends JdbcDAO {
 public List<SolicitudRecojo> listar(Integer usuario,String estado,String distrito) throws SQLException {
  return consultar("SELECT s.*,CONCAT(u.nombre,' ',u.apellido) donador,u.email,d.descripcion,c.nombre comedor FROM solicitudes_recojo s JOIN donaciones d ON d.id=s.donacion_id JOIN usuarios u ON u.id=d.donante_id JOIN comedores c ON c.id=d.comedor_id WHERE (? IS NULL OR u.id=?) AND (?='' OR s.estado=?) AND (?='' OR s.distrito=?) ORDER BY s.fecha_programada,s.horario_programado,s.distrito,s.id",this::map,usuario,usuario,estado,estado,distrito,distrito);
 }
 public void registrar(Connection c,SolicitudRecojo s) throws SQLException {
  alta(c,"INSERT INTO solicitudes_recojo(donacion_id,direccion,provincia,distrito,zona,referencia,fecha_preferida,horario_preferido,telefono) VALUES(?,?,?,?,?,?,?,?,?)",s.getDonacionId(),s.getDireccion(),s.getProvincia(),s.getDistrito(),s.getZona(),s.getReferencia(),s.getFechaPreferida(),s.getHorarioPreferido(),s.getTelefono());
 }
 private SolicitudRecojo map(ResultSet r) throws SQLException {
  SolicitudRecojo s=new SolicitudRecojo(); s.setId(r.getInt("id"));s.setDonacionId(r.getInt("donacion_id"));
  s.setDireccion(r.getString("direccion"));
  s.setProvincia(r.getString("provincia"));
  s.setDistrito(r.getString("distrito"));
  s.setZona(r.getString("zona"));
  s.setReferencia(r.getString("referencia"));
  s.setTelefono(r.getString("telefono"));
  s.setResponsable(r.getString("responsable"));
  s.setObservaciones(r.getString("observaciones"));
  s.setEstado(r.getString("estado"));
  s.setDonador(r.getString("donador"));
  s.setEmail(r.getString("email"));
  s.setDescripcion(r.getString("descripcion"));
  s.setComedor(r.getString("comedor"));
  s.setHorarioPreferido(r.getString("horario_preferido"));
  s.setHorarioProgramado(r.getString("horario_programado"));
  s.setFechaPreferida(r.getDate("fecha_preferida").toLocalDate());
  if(r.getDate("fecha_programada")!=null)s.setFechaProgramada(r.getDate("fecha_programada").toLocalDate());return s;
 }
}
