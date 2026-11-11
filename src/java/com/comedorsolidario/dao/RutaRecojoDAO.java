package com.comedorsolidario.dao;
import com.comedorsolidario.model.RutaRecojo; import java.sql.*;import java.util.*;
public class RutaRecojoDAO extends JdbcDAO {
 public List<RutaRecojo> listar() throws SQLException { return consultar("SELECT * FROM rutas_recojo ORDER BY fecha DESC,id DESC",r->{RutaRecojo x=new RutaRecojo();x.setId(r.getInt("id"));x.setFecha(r.getDate("fecha").toLocalDate());x.setDistrito(r.getString("distrito"));x.setHorario(r.getString("horario"));x.setResponsable(r.getString("responsable"));x.setObservaciones(r.getString("observaciones"));x.setEstado(r.getString("estado"));return x;}); }
 public int crear(RutaRecojo r) throws SQLException {return insertar("INSERT INTO rutas_recojo(fecha,distrito,horario,responsable,observaciones) VALUES(?,?,?,?,?)",r.getFecha(),r.getDistrito(),r.getHorario(),r.getResponsable(),r.getObservaciones());}
}
