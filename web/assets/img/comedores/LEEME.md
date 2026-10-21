# Fotos de los comedores

Guarda aquí las fotografías reales de cada comedor. El nombre debe ser su ID numérico, tal como aparece en Administración > Comedores.

Ejemplos:
- Comedor con ID 1: 1.jpg
- Comedor con ID 7: 7.png
- Comedor con ID 12: 12.webp

Formatos admitidos (extensión en minúsculas): .jpg, .jpeg, .png y .webp.
Si existen varias imágenes del mismo ID, la prioridad es jpg, jpeg, png y webp. Deja una sola para evitar confusiones.

No basta con cambiar la extensión: guarda o exporta la foto en el formato correcto.
Recomendación: imagen horizontal de aproximadamente 1200 x 750 píxeles, menos de 1 MB.
El encuadre usa object-fit: cover; los bordes pueden recortarse. Mantén lo importante cerca del centro.

Después de agregar o reemplazar una imagen:
1. Detén Tomcat desde NetBeans.
2. Ejecuta Clean and Build y Run.
3. Recarga el navegador con Ctrl+F5.
4. Comprueba la tarjeta del comedor en /comedores y su detalle. Si aparece en destacados, se usará allí la misma foto.

Edita los archivos de web/assets/img/comedores, no los de target ni la carpeta desplegada de Tomcat, porque se regeneran al compilar.

sin-foto.svg es una ilustración genérica de respaldo, no una fotografía de un comedor real.
Se muestra cuando no existe imagen para el ID. Si una foto falla al cargar, JavaScript intenta mostrar también ese respaldo.

La foto grande de la portada se encuentra en web/assets/img/comunidad.png. Para cambiarla, reemplaza ese archivo por una imagen PNG y vuelve a desplegar.

No se necesita modificar MySQL. Los registros, filtros y contadores continúan obteniéndose mediante JDBC.
