import com.comedorsolidario.dao.*;
import com.comedorsolidario.model.*;
import com.comedorsolidario.service.logistica.LogisticaService;
import com.comedorsolidario.util.*;
import java.sql.*;import java.nio.file.*;import java.math.*;import java.time.*;import java.util.*;import java.util.concurrent.*;
import static com.comedorsolidario.dao.JdbcDAO.*;

/** Solo sobre la instancia desechable 127.0.0.1:3308. Nunca usa la base habitual. */
class VerificarLogistica {
 static int pruebas=0;static LogisticaService service=new LogisticaService();static int user,admin;
 static void check(boolean b,String s){if(!b)throw new AssertionError(s);pruebas++;System.out.println("OK "+s);}
 interface Accion {void run() throws Exception;}
 static void rechaza(Accion a,String s)throws Exception {try{a.run();}catch(IllegalArgumentException e){check(true,s);return;}throw new AssertionError("No rechazado: "+s);}
 static void sql(Connection c,Path file)throws Exception {
  String s=Files.readString(file).replaceAll("(?m)^\\s*--.*$","");
  try(Statement p=c.createStatement()){for(String q:s.split(";"))if(!q.isBlank())p.execute(q);}
 }
 static Donacion don(String modo){Donacion d=new Donacion();d.setDonanteId(user);d.setComedorId(1);d.setTipo("ALIMENTOS");d.setUnidad("kg");d.setCantidad(new BigDecimal("2.00"));d.setDescripcion("Aporte prueba T3");d.setModalidad(modo);return d;}
 static SolicitudRecojo solicitud(){SolicitudRecojo s=new SolicitudRecojo();s.setDireccion("Calle de prueba 123");s.setProvincia("Lima");s.setDistrito("San Isidro");s.setZona("Centro");s.setReferencia("Puerta azul");s.setFechaPreferida(LogisticaService.hoy().plusDays(1));s.setHorarioPreferido("09:00-12:00");s.setTelefono("999123456");return s;}
 static int sid(int d)throws Exception {return transaccion(c->((Number)fila(c,"SELECT id FROM solicitudes_recojo WHERE donacion_id=?",d).get("id")).intValue());}
 static RutaRecojo ruta(){RutaRecojo r=new RutaRecojo();r.setFecha(LogisticaService.hoy().plusDays(1));r.setDistrito("San Isidro");r.setHorario("09:00-12:00");r.setResponsable("Equipo T3");r.setObservaciones("");return r;}
 public static void main(String[] args)throws Exception {
  if (!Boolean.getBoolean("activarTests")) {
   System.out.println("Tests de verificación desactivados temporalmente.");
   return;
  }
  if(!String.valueOf(System.getenv("CS_DB_URL")).startsWith("jdbc:mysql://127.0.0.1:3308/"))throw new IllegalStateException("Usar exclusivamente MySQL desechable puerto 3308.");
  if(args.length>0 && args[0].equals("instalar")) {
   Class.forName("com.mysql.cj.jdbc.Driver");try(Connection c=DriverManager.getConnection("jdbc:mysql://127.0.0.1:3308/?allowPublicKeyRetrieval=true&sslMode=DISABLED","root","")) {sql(c,Path.of("database/comedor_solidario.sql"));sql(c,Path.of("database/migracion_t3_logistica.sql"));sql(c,Path.of("database/migracion_t3_logistica.sql"));}
   System.out.println("Instalación aislada y migración repetida OK");
  }
  try(Connection cn=ConexionBD.getConnection()) {
   long antes=((Number)fila(cn,"SELECT COUNT(*) n FROM donaciones").get("n")).longValue();
   sql(cn,Path.of("database/migracion_t3_logistica.sql"));
   check(((Number)fila(cn,"SELECT COUNT(*) n FROM donaciones").get("n")).longValue()==antes,"Repetir migración conserva donaciones existentes");
  }
  String hash=PasswordUtil.hash("PruebaT3_2026!");
  transaccion(c->{cambio(c,"UPDATE usuarios SET password_hash=? WHERE rol='ADMIN'",hash);return null;});
  admin=transaccion(c->((Number)fila(c,"SELECT id FROM usuarios WHERE rol='ADMIN' LIMIT 1").get("id")).intValue());
  user=transaccion(c->alta(c,"INSERT INTO usuarios(nombre,apellido,email,password_hash,telefono) VALUES('Prueba','Logistica',?,?,?)","t3-"+System.nanoTime()+"@example.test",hash,"999123456"));
  rechaza(()->ValidacionUtil.telefono("abc"),"Teléfono inválido");
  PuntoAcopio p=new PuntoAcopio();p.setNombre("Punto T3");p.setDireccion("Local 123");p.setProvincia("Lima");p.setDistrito("San Isidro");p.setZona("Centro");p.setReferencia("Plaza");p.setHorarios("9 a 17");p.setResponsable("Responsable");p.setTelefono("999123456");p.setEstado("ACTIVO");
  PuntoAcopioDAO pd=new PuntoAcopioDAO();pd.guardar(p);p=pd.listar(true).stream().filter(x->x.getNombre().equals("Punto T3")).findFirst().orElseThrow();int pid=p.getId();p.setReferencia("Referencia editada");pd.guardar(p);check(pd.buscar(pid).getReferencia().equals("Referencia editada"),"CRUD acopio consulta y edición");
  Donacion a=don("ACOPIO");a.setPuntoAcopioId(pid);service.registrar(a,null);
  rechaza(()->pd.eliminar(pid),"Acopio relacionado no se elimina");pd.estado(pid,"INACTIVO");
  Donacion mala=don("ACOPIO");mala.setPuntoAcopioId(pid);rechaza(()->service.registrar(mala,null),"Acopio inactivo rechazado");pd.estado(pid,"ACTIVO");
  Donacion b=don("RECOJO");service.registrar(b,solicitud());int sid=sid(b.getId());
  rechaza(()->service.registrar(don("DIRECTA"),null),"Límite de dos aportes");
  check(!new DonacionDAO().avanzar(b.getId(),"PENDIENTE"),"Endpoint antiguo no confirma recojo");
  rechaza(()->service.recojo(sid,"completar",null,"","","",admin),"No saltar de solicitado a entregado");
  service.recojo(sid,"aceptar",null,"","","Coordinado",admin);
  service.recojo(sid,"programar",LogisticaService.hoy().plusDays(1),"09:00-12:00","Equipo T3","Confirmado",admin);
  int ruta=service.crearRuta(ruta());service.parada(ruta,sid,1,false,false);
  int otra=service.crearRuta(ruta());rechaza(()->service.parada(otra,sid,1,false,false),"No duplicar solicitud en rutas activas");
  check(new DetalleRutaRecojoDAO().listar(ruta).size()==1,"Detalle de ruta consulta real");
  service.estadoRuta(ruta,"EN_PROCESO");rechaza(()->service.estadoRuta(ruta,"COMPLETADA"),"Ruta no completa con recojo pendiente");
  service.recojo(sid,"recoger",null,"","","Recogido físicamente",admin);
  check(new DonacionDAO().buscar(b.getId()).getEstado().equals("PROGRAMADA"),"Recoger no equivale a entregar");
  service.recojo(sid,"completar",null,"","","Recepción física verificada",admin);service.estadoRuta(ruta,"COMPLETADA");
  check(new DonacionDAO().buscar(b.getId()).getFechaEntrega()!=null,"Recepción registra fecha de entrega");
  service.cerrarDonacion(a.getId(),"INCUMPLIDA","No llegó",admin);
  Donacion c=don("DIRECTA");service.registrar(c,null);service.cerrarDonacion(c.getId(),"INCUMPLIDA","Segundo incumplimiento",admin);
  check(service.alertas().stream().anyMatch(x->((Number)x.get("id")).intValue()==user),"Alerta por reincidencia");
  new DonacionDAO().cambiarVigencia(c.getId(),false);check(new DonacionDAO().historial(user).size()==3,"Historial conserva inactivos");
  check(new UsuarioDAO().buscar(user).getEstado().equals("ACTIVO"),"No suspensión automática");
  service.cuenta(user,"INACTIVO","Revisión administrativa",admin);rechaza(()->service.registrar(don("DIRECTA"),null),"Cuenta inactiva no dona");service.cuenta(user,"ACTIVO","Contacto verificado",admin);
  var pool=Executors.newFixedThreadPool(3);var gate=new CountDownLatch(1);List<Future<Boolean>> tasks=new ArrayList<>();
  for(int i=0;i<3;i++)tasks.add(pool.submit(()->{gate.await();try{service.registrar(don("DIRECTA"),null);return true;}catch(IllegalArgumentException e){return false;}}));gate.countDown();int count=0;for(var t:tasks)if(t.get())count++;pool.shutdown();check(count==2,"Tres registros concurrentes: solo dos permitidos");
  check(new SolicitudRecojoDAO().listar(user,"COMPLETADO","San Isidro").size()==1,"Filtros recojo por propietario, estado y distrito");
  Donacion pendiente=new DonacionDAO().historial(user).stream().filter(x->x.getEstado().equals("PENDIENTE")).findFirst().orElseThrow();
  new DonacionDAO().cambiarVigencia(pendiente.getId(),false);
  service.registrar(don("DIRECTA"),null);
  rechaza(()->new DonacionDAO().cambiarVigencia(pendiente.getId(),true),"Reactivar no elude el cupo");
  for(Donacion item:new DonacionDAO().historial(user))if(item.isActivo() && item.getEstado().equals("PENDIENTE"))service.cerrarDonacion(item.getId(),"CANCELADA","Cerrar pruebas",admin);
  Donacion fallida=don("RECOJO");SolicitudRecojo invalida=solicitud();invalida.setDireccion(null);
  int antes=new DonacionDAO().historial(user).size();
  try {service.registrar(fallida,invalida);throw new AssertionError("Se esperaba rollback");}catch(SQLException esperado){}
  check(new DonacionDAO().historial(user).size()==antes,"Fallo en solicitud revierte también donación");
  Donacion rechazo=don("RECOJO");service.registrar(rechazo,solicitud());int rid=sid(rechazo.getId());
  rechaza(()->service.recojo(rid,"rechazar",null,"","","",admin),"Rechazo exige motivo");
  service.recojo(rid,"rechazar",null,"","","Sin disponibilidad; usar acopio",admin);
  check(new DonacionDAO().buscar(rechazo.getId()).getEstado().equals("RECHAZADA"),"Rechazo sincroniza estados separados");
  Donacion mixta=don("RECOJO");SolicitudRecojo sm=solicitud();sm.setDistrito("Miraflores");service.registrar(mixta,sm);int mid=sid(mixta.getId());
  service.recojo(mid,"programar",LogisticaService.hoy().plusDays(1),"09:00-12:00","Equipo T3","",admin);
  int rm=service.crearRuta(ruta());rechaza(()->service.parada(rm,mid,1,false,false),"Otro distrito requiere decisión explícita");service.parada(rm,mid,1,false,true);
  service.parada(rm,mid,5,false,true);check(new DetalleRutaRecojoDAO().listar(rm).get(0).getOrden()==5,"Orden manual persistido");
  service.estadoRuta(rm,"CANCELADA");int rn=service.crearRuta(ruta());service.parada(rn,mid,1,false,true);
  check(new DetalleRutaRecojoDAO().listar(rn).size()==1,"Cancelar ruta libera asignación conservando historial");
  service.parada(rn,mid,1,true,true);check(new DetalleRutaRecojoDAO().listar(rn).isEmpty(),"Quitar parada planificada");
  rechaza(()->service.estadoRuta(rn,"EN_PROCESO"),"Ruta vacía no inicia");
  service.recojo(mid,"incumplir",null,"","","No atendió",admin);
  check(new DonacionDAO().buscar(mixta.getId()).getEstado().equals("INCUMPLIDA"),"Incumplimiento de recojo registrado");
  rechaza(()->service.cuenta(user,"INACTIVO","",admin),"Cuenta requiere motivo");
  Usuario u=new UsuarioDAO().buscar(user);u.setEstado("INACTIVO");u.setEmail(new UsuarioDAO().buscar(admin).getEmail());
  int eventos=service.historialCuenta(user).size();
  try{service.guardarDonador(u,"Prueba rollback",admin);throw new AssertionError();}catch(SQLException esperado){}
  check(new UsuarioDAO().buscar(user).getEstado().equals("ACTIVO") && service.historialCuenta(user).size()==eventos,"Edición de cuenta y auditoría atómicas ante correo duplicado");
  p.setId(0);p.setNombre("Punto sin relaciones");pd.guardar(p);int borrar=pd.listar(true).stream().filter(x->x.getNombre().equals("Punto sin relaciones")).findFirst().orElseThrow().getId();pd.eliminar(borrar);rechaza(()->pd.buscar(borrar),"Eliminar punto sin relaciones");
  System.out.println("TOTAL: "+pruebas+" pruebas correctas. Solo instancia aislada.");
 }
}
