import com.comedorsolidario.util.ImagenComedor;
import com.comedorsolidario.dao.ComedorDAO;
import com.comedorsolidario.model.Comedor;
import jakarta.servlet.http.Part;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import javax.imageio.ImageIO;

/** Ejecutar con CS_UPLOAD_DIR apuntando a una carpeta TEMPORAL de pruebas. No usa MySQL. */
class VerificarImagenComedor {
    static int checks;
    interface Action { void run() throws Exception; }
    static void check(boolean value) { if (!value) throw new AssertionError(); checks++; }
    static void rejects(Action action) throws Exception {
        try { action.run(); throw new AssertionError("Se esperaba rechazo"); }
        catch (IllegalArgumentException expected) { checks++; }
    }
    record Archivo(byte[] bytes, long size) implements Part {
        public InputStream getInputStream() { return new ByteArrayInputStream(bytes); }
        public String getContentType() { return "image/jpeg"; }
        public String getName() { return "foto"; }
        public String getSubmittedFileName() { return "../../fuera.jsp"; }
        public long getSize() { return size; }
        public void write(String path) { throw new AssertionError("No usar el nombre del usuario"); }
        public void delete() {}
        public String getHeader(String name) { return null; }
        public Collection<String> getHeaders(String name) { return List.of(); }
        public Collection<String> getHeaderNames() { return List.of(); }
    }
    static Archivo foto(String formato) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(new BufferedImage(20, 10, BufferedImage.TYPE_INT_RGB), formato, output);
        return new Archivo(output.toByteArray(), output.size());
    }
    public static void main(String[] args) throws Exception {
        Path dir = Path.of(Objects.requireNonNull(System.getenv("CS_UPLOAD_DIR"))).toAbsolutePath().normalize();
        check(dir.toString().contains("prueba-fotos-"));
        ImagenComedor servicio = new ImagenComedor();
        check(servicio.preparar(null, false) == null);
        check(servicio.buscar(901) == null);
        for (String formato : List.of("png", "jpeg")) {
            Path preparado = servicio.preparar(foto(formato), false);
            servicio.guardar(preparado, 901);
            check(!Files.exists(preparado));
            Path guardado = servicio.buscar(901);
            check(guardado.equals(dir.resolve("901.png")));
            check(ImageIO.read(guardado.toFile()).getWidth() == 20);
            check(Files.readAllBytes(guardado)[0] == (byte) 0x89); // Siempre PNG, sin contenido ejecutable.
        }
        rejects(() -> servicio.preparar(new Archivo("<svg/>".getBytes(), 6), false));
        rejects(() -> servicio.preparar(new Archivo(new byte[0], ImagenComedor.MAX_BYTES + 1), false));
        rejects(() -> servicio.preparar(foto("png"), true));
        rejects(() -> servicio.buscar(-1));
        Path defecto = servicio.preparar(null, true);
        servicio.guardar(defecto, 901);
        check(Files.size(servicio.buscar(901)) == 0);
        // Otra instancia reproduce la elección después de un nuevo despliegue.
        check(Files.size(new ImagenComedor().buscar(901)) == 0);
        servicio.guardar(servicio.preparar(foto("png"), false), 901);
        check(Files.size(servicio.buscar(901)) > 0);
        // La clave devuelta por INSERT se asigna al modelo para asociar la foto al nuevo comedor.
        ComedorDAO dao = new ComedorDAO() {
            protected int insertar(String sql, Object... valores) {
                check(sql.startsWith("INSERT INTO comedores"));
                check(valores.length == 9);
                return 123;
            }
        };
        Comedor comedor = new Comedor();
        dao.guardar(comedor);
        check(comedor.getId() == 123);
        Files.delete(servicio.buscar(901));
        Files.delete(dir);
        System.out.println("OK: " + checks + " aserciones de fotos y asociación de ID; sin tocar MySQL ni fotos reales.");
    }
}
