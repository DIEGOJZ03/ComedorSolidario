"""Pruebas HTTP exclusivas del Tomcat/MySQL desechable, puertos 18080/3308."""
import urllib.request, urllib.parse, urllib.error, http.cookiejar, re, time
from datetime import date,timedelta
from html import unescape
BASE='http://127.0.0.1:18080/ComedorSolidario'
def client():return urllib.request.build_opener(urllib.request.HTTPCookieProcessor(http.cookiejar.CookieJar()))
def call(c,path,data=None):
 try:
  with c.open(BASE+path,None if data is None else urllib.parse.urlencode(data).encode()) as r:return r.status,r.read().decode('utf-8')
 except urllib.error.HTTPError as e:return e.code,e.read().decode('utf-8')
def token(body):return re.search(r'name="csrf" value="([^"]+)"',body).group(1)
def check(ok,label):
 assert ok,label
 print('OK',label)
public=client()
for p in ['/inicio','/comedores','/login','/registro','/acopio/puntos']:
 status,body=call(public,p);check(status==200,p)
status,body=call(public,'/admin/logistica/rutas');check('Iniciar' in body and status==200,'Anónimo redirigido al login')
admin=client();_,body=call(admin,'/login')
import sys
email=sys.argv[1]
status,body=call(admin,'/login',{'csrf':token(body),'email':email,'password':'PruebaT3_2026!'})
check(status==200 and 'Administración' in body,'Login administrador')
for p in ['/admin/dashboard','/admin/comedores','/admin/necesidades','/admin/donaciones','/admin/usuarios','/admin/productos','/admin/campanias','/admin/logistica/puntos','/admin/logistica/recojos','/admin/logistica/rutas','/admin/logistica/rutas?id=1','/admin/logistica/alertas','/admin/logistica/historial?usuarioId=1']:
 status,body=call(admin,p);check(status==200,p)
status,_=call(admin,'/admin/logistica/puntos',{'accion':'guardar'});check(status==403,'POST sin token rechazado')
donor=client();_,body=call(donor,'/registro');mail='http'+str(time.time_ns())+'@example.test'
data={'csrf':token(body),'nombre':'Prueba','apellido':'HTTP','email':mail,'telefono':'999123456','password':'PruebaT3_2026!','confirmacion':'PruebaT3_2026!'}
status,body=call(donor,'/registro',data);check(status==200,'Registro donador')
_,body=call(donor,'/login');status,body=call(donor,'/login',{'csrf':token(body),'email':mail,'password':'PruebaT3_2026!'})
check(status==200,'Login donador')
for p in ['/donante/dashboard','/donante/perfil','/donante/donaciones','/donante/donaciones/nueva','/donante/logistica/recojos','/donante/logistica/historial']:
 status,body=call(donor,p);check(status==200,p)
status,_=call(donor,'/admin/logistica/rutas');check(status==403,'Donador no accede a rutas administrativas')
_,body=call(donor,'/donante/logistica/recojos')
status,_=call(donor,'/donante/logistica/recojos',{'csrf':token(body),'id':1,'accion':'completar'});check(status==403,'Donador no confirma entrega incluso alterando URL')
status,_=call(donor,'/views/logistica/rutas.jsp');check(status==404,'JSP protegida')
_,body=call(donor,'/donante/donaciones/nueva')
aporte={'csrf':token(body),'comedorId':'1','tipo':'ALIMENTOS','cantidad':'3.00','unidad':'kg','descripcion':'Aporte HTTP de prueba','modalidad':'RECOJO','direccion':'Calle HTTP 123','provincia':'Lima','distrito':'San Isidro','zona':'Centro','referencia':'Puerta azul','fechaPreferida':str(date.today()+timedelta(days=1)),'horarioPreferido':'09:00-12:00','telefono':'999123456'}
status,body=call(donor,'/donante/donaciones/nueva',aporte);check(status==200 and 'pendiente de evaluación' in body,'Formulario registra donación y solicitud')
_,body=call(donor,'/donante/logistica/recojos');sid=re.search(r'Solicitud #(\d+)',body).group(1)
check('SOLICITADO' in body and 'Calle HTTP 123' in body,'Donador ve dirección y estado inicial')
_,body=call(admin,'/admin/logistica/recojos');csrf=token(body)
status,body=call(admin,'/admin/logistica/recojos',{'csrf':csrf,'id':sid,'accion':'programar','fecha':aporte['fechaPreferida'],'horario':'09:00-12:00','responsable':'Responsable HTTP','observaciones':'Coordinado'})
check(status==200 and 'PROGRAMADO' in body,'Programación desde formulario Spring MVC')
_,body=call(admin,'/admin/logistica/rutas');csrf=token(body)
status,body=call(admin,'/admin/logistica/rutas',{'csrf':csrf,'accion':'crear','fecha':aporte['fechaPreferida'],'distrito':'San Isidro','horario':'09:00-12:00','responsable':'Responsable HTTP','observaciones':'Ruta HTTP'})
check(status==200,'Crear ruta desde formulario')
rid=re.search(r'Paradas de la ruta #(\d+)',body).group(1)
status,body=call(admin,'/admin/logistica/rutas',{'csrf':csrf,'accion':'parada','id':rid,'solicitudId':sid,'orden':'1','horariosConfirmados':'on'})
check(status==200 and 'Calle HTTP 123' in body,'Agregar parada mediante formulario')
for accion,label in [('recoger','RECOGIDO'),('completar','COMPLETADO')]:
 status,body=call(admin,'/admin/logistica/recojos',{'csrf':csrf,'id':sid,'accion':accion,'observaciones':'Verificación física HTTP'})
 check(status==200 and label in body,'Confirmación administrativa '+label)
_,body=call(donor,'/donante/logistica/historial');check('ENTREGADA' in body and 'COMPLETADO' in body,'Donador ve recepción final sin poder modificarla')
_,body=call(public,'/registro');data['csrf']=token(body)
status,_=call(public,'/registro',data);check(status==400,'Correo duplicado rechazado por formulario')
data['email']='otro'+str(time.time_ns())+'@example.test';data['telefono']='abc'
status,_=call(public,'/registro',data);check(status==400,'Teléfono inválido rechazado por formulario')
print('Pruebas HTTP completadas')
