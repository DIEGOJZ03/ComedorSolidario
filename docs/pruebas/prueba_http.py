#!/usr/bin/env python3
"""Prueba real contra Tomcat y MySQL. CREA registros: utilizar una base de pruebas.
Uso: python prueba_http.py http://localhost:8080/ComedorSolidario
Solo biblioteca estándar Python; no forma parte del runtime de la aplicación.
"""
import urllib.request, urllib.parse, urllib.error, http.cookiejar, re, sys, time, json
BASE=(sys.argv[1] if len(sys.argv)>1 else 'http://localhost:8080/ComedorSolidario').rstrip('/')
results=[]
def check(name,condition):
    if not condition: raise AssertionError(name)
    results.append(name); print('OK:',name,flush=True)
class Client:
    def __init__(self):
        self.jar=http.cookiejar.CookieJar()
        self.op=urllib.request.build_opener(urllib.request.ProxyHandler({}),urllib.request.HTTPCookieProcessor(self.jar))
        self.token=''
    def req(self,path,data=None,expected=200,token=True):
        if data is not None:
            data=dict(data)
            if token:data['csrf']=self.token
            data=urllib.parse.urlencode(data).encode()
        try:r=self.op.open(BASE+path,data,timeout=30)
        except urllib.error.HTTPError as e:r=e
        body=r.read().decode('utf-8');code=r.code
        if code!=expected:
            open('http-error.html','w').write(body)
            raise AssertionError(f'{path}: HTTP {code}, esperado {expected}. Véase http-error.html')
        match=re.search(r'name="csrf" value="([^"]+)"',body)
        if match:self.token=match.group(1)
        return body,r.url
    def login(self,email,password):
        self.req('/login');return self.req('/login',{'email':email,'password':password})
def row_for(body,text):return next(x for x in re.findall(r'<tr>.*?</tr>',body,re.S) if text in x)
pub=Client();body,_=pub.req('/')
check('Inicio, estadísticas y comedores destacados', 'Tu ayuda puede llegar' in body and 'Comedor Nuevo Amanecer' in body)
check('UTF-8 en navbar y fragmentos JSP', 'Iniciar sesión' in body and 'Proyecto académico' in body)
pub.req('/index.jsp');pub.req('/assets/css/styles.css');pub.req('/assets/vendor/bootstrap/bootstrap.min.css');pub.req('/assets/vendor/bootstrap/bootstrap.bundle.min.js')
body,_=pub.req('/comedores?zona=Este&distrito=San+Juan+de+Lurigancho&provincia=Lima')
check('Filtros combinados', 'Olla Común Santa Rosa' in body and 'Comedor Nuevo Amanecer' not in body)
pub.req('/comedores/detalle?id=1');pub.req('/comedores/detalle?id=999999',expected=404)
pub.req('/views/admin/dashboard.jsp',expected=404)
body,url=pub.req('/admin/comedores');check('Visitante redirigido al login',url.endswith('/login'))
pub.req('/login',{'email':'admin@comedorsolidario.pe','password':'Incorrecta2026!'},expected=400)
pub.req('/registro',{'nombre':'X'},expected=403,token=False)
check('Login inválido y CSRF bloqueados',True)
admin=Client();_,url=admin.login('admin@comedorsolidario.pe','Admin2026!');check('Login ADMIN y dashboard',url.endswith('/admin/dashboard'))
for path in ['/admin/comedores','/admin/comedores/formulario','/admin/comedores/formulario?id=1','/admin/comedores/detalle?id=1','/admin/necesidades','/admin/necesidades/formulario','/admin/necesidades/formulario?id=1','/admin/donaciones','/admin/donaciones?estado=ACEPTADA','/admin/donaciones/detalle?id=1','/admin/usuarios']:
    admin.req(path)
check('Todas las vistas administrativas renderizan',True)
marker='Prueba'+str(time.time_ns())
comedor={'accion':'guardar','id':'0','nombre':marker,'descripcion':'Comedor de prueba automatizada','direccion':'Av. Prueba 10','zona':'Este','distrito':'Pruebas','provincia':'Lima','responsable':'Responsable Prueba','telefono':'900123456','estado':'ACTIVO'}
body,_=admin.req('/admin/comedores',comedor)
row=row_for(body,marker);cid=re.search(r'detalle\?id=(\d+)',row).group(1)
check('CRUD CREATE comedor',bool(cid))
body,_=admin.req('/admin/comedores/detalle?id='+cid);check('CRUD READ comedor',marker in body)
comedor.update(id=cid,telefono='900654321');admin.req('/admin/comedores',comedor)
body,_=admin.req('/admin/comedores/detalle?id='+cid);check('CRUD UPDATE comedor','900654321' in body)
admin.req('/admin/comedores',{'accion':'estado','id':cid,'estado':'INACTIVO'});pub.req('/comedores/detalle?id='+cid,expected=404)
check('CRUD DELETE lógico oculta comedor',True)
admin.req('/admin/comedores',{'accion':'estado','id':cid,'estado':'ACTIVO'});pub.req('/comedores/detalle?id='+cid)
check('Reactivar comedor',True)
need={'accion':'guardar','id':'0','comedorId':cid,'nombre':marker+' arroz','descripcion':'Bolsas selladas','cantidadNecesaria':'20','unidad':'kg','prioridad':'ALTA','estado':'ACTIVA'}
body,_=admin.req('/admin/necesidades',need);nid=re.search(r'formulario\?id=(\d+)',row_for(body,marker+' arroz')).group(1)
need.update(id=nid,cantidadNecesaria='25');admin.req('/admin/necesidades',need)
body,_=pub.req('/comedores/detalle?id='+cid);check('Crear y editar necesidades','25.00' in body and marker+' arroz' in body)
admin.req('/admin/necesidades',{'accion':'estado','id':nid,'estado':'INACTIVA'})
body,_=pub.req('/comedores/detalle?id='+cid);check('Desactivar necesidad',marker+' arroz' not in body)
admin.req('/admin/necesidades',{'accion':'estado','id':nid,'estado':'ACTIVA'})
# Test registration and a new isolated donor.
u=Client();u.req('/registro');email=marker.lower()+'@example.com'
registration={'nombre':'Prueba','apellido':'Donador','email':email,'telefono':'900123456','password':'Prueba2026!','confirmacion':'Prueba2026!','rol':'ADMIN'}
u.req('/registro',registration);u.req('/registro',registration,expected=400)
check('Registro y correo duplicado',True)
_,url=u.login(email,'Prueba2026!');check('Registro no admite elevar rol',url.endswith('/donante/dashboard'))
u.req('/admin/comedores',expected=403);u.req('/admin/donaciones',{'id':1,'estadoActual':'PENDIENTE'},expected=403)
check('DONADOR no puede leer ni escribir administración',True)
for path in ['/donante/dashboard','/donante/perfil','/donante/donaciones','/donante/donaciones/nueva?comedorId='+cid]:u.req(path)
check('Vistas de donador renderizan',True)
invalid={'comedorId':cid,'tipo':'ALIMENTOS','cantidad':'0','unidad':'kg','descripcion':'Prueba'}
u.req('/donante/donaciones/nueva',invalid,expected=400)
invalid.update(cantidad='10',comedorId='999999');u.req('/donante/donaciones/nueva',invalid,expected=400)
check('Cantidad positiva y comedor válido',True)
donation={'comedorId':cid,'tipo':'ALIMENTOS','cantidad':'12.50','unidad':'kg','descripcion':marker,'donanteId':'2','estado':'ENTREGADA'}
body,_=u.req('/donante/donaciones/nueva',donation)
row=row_for(body,marker);did=re.search(r'<td>#(\d+)</td>',row).group(1)
check('Donación toma propietario y estado del servidor','PENDIENTE' in row)
admin.req('/admin/donaciones',{'id':did,'estadoActual':'PENDIENTE'})
body,_=u.req('/donante/donaciones');check('Aceptación visible para donador','ACEPTADA' in row_for(body,marker))
admin.req('/admin/donaciones',{'id':did,'estadoActual':'PENDIENTE'})
body,_=u.req('/donante/donaciones');check('Actualización antigua no salta de estado','ACEPTADA' in row_for(body,marker))
admin.req('/admin/donaciones',{'id':did,'estadoActual':'ACEPTADA'})
body,_=u.req('/donante/donaciones');row=row_for(body,marker)
check('Entrega visible con fecha','ENTREGADA' in row and 'Por confirmar' not in row)
admin.req('/admin/donaciones',{'id':did,'estadoActual':'ENTREGADA'},expected=400)
check('Estado final no admite avance',True)
other=Client();other.login('lucia@example.com','Donador2026!')
body,_=other.req('/donante/donaciones?donanteId=2&id='+did);check('Aislamiento de donaciones entre cuentas',marker not in body)
u.req('/donante/perfil',{'nombre':'<script>alert(1)</script>','apellido':'Prueba','telefono':'900000000','id':'2'})
body,_=u.req('/donante/perfil');check('Perfil propio y escape XSS','&lt;script&gt;' in body and '<script>alert(1)</script>' not in body)
u.req('/donante/perfil',{'nombre':'Prueba','apellido':'Donador','telefono':'900000000'})
# Check admin changes preserve historic donation even if comedor becomes inactive.
admin.req('/admin/comedores',{'accion':'estado','id':cid,'estado':'INACTIVO'})
u.req('/donante/donaciones/nueva',donation,expected=400)
body,_=u.req('/donante/donaciones');check('Desactivar conserva historial e impide nuevos aportes',marker in body)
u.req('/logout',{})
_,url=u.req('/donante/donaciones');check('Logout invalida sesión',url.endswith('/login'))
# Follow every main link emitted by public, donor and admin layouts.
for client,paths in [(pub,['/inicio','/comedores']), (other,['/donante/dashboard']), (admin,['/admin/dashboard','/admin/comedores','/admin/necesidades','/admin/donaciones'])]:
    for path in paths:
        html,_=client.req(path)
        for href in set(re.findall(r'href="([^"]+)"',html)):
            prefix=urllib.parse.urlsplit(BASE).path
            if href.startswith(prefix+'/'):
                target=href[len(prefix):].split('#')[0].replace('&amp;','&')
                client.req(target)
check('Enlaces principales sin errores HTTP',True)
print(json.dumps({'resultado':'APROBADO','comprobaciones':len(results),'detalle':results},ensure_ascii=False,indent=2))
