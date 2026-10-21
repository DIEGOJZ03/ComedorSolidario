package com.comedorsolidario.util;

import jakarta.servlet.http.Part;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

/** Fotos fuera del WAR: sobreviven a Clean and Build y a nuevos despliegues. */
public class ImagenComedor {
    public static final long MAX_BYTES = 5L * 1024 * 1024;
    private final Path directorio;

    public ImagenComedor() {
        String configurado = System.getenv("CS_UPLOAD_DIR");
        directorio = (configurado == null || configurado.isBlank()
                ? Path.of(System.getProperty("user.home"), "ComedorSolidarioDatos", "comedores")
                : Path.of(configurado)).toAbsolutePath().normalize();
    }

    public Path buscar(int id) {
        if (id <= 0) throw new IllegalArgumentException("Identificador inválido.");
        Path archivo = directorio.resolve(id + ".png");
        return Files.isRegularFile(archivo) ? archivo : null;
    }

    /** Null conserva la foto; un archivo vacío representa la opción predeterminada. */
    public Path preparar(Part foto, boolean predeterminada) throws IOException {
        boolean adjunta = foto != null && foto.getSize() > 0;
        if (predeterminada && adjunta)
            throw new IllegalArgumentException("Elige una foto o la imagen predeterminada, no ambas.");
        if (!predeterminada && !adjunta) return null;
        BufferedImage imagen = adjunta ? validar(foto) : null;
        Files.createDirectories(directorio);
        Path temporal = Files.createTempFile(directorio, "foto-", ".tmp");
        try {
            if (imagen != null && !ImageIO.write(imagen, "png", temporal.toFile()))
                throw new IOException("No se pudo convertir la imagen.");
            return temporal;
        } catch (IOException | RuntimeException e) {
            Files.deleteIfExists(temporal);
            throw e;
        } finally {
            if (imagen != null) imagen.flush();
        }
    }

    private BufferedImage validar(Part foto) throws IOException {
        if (foto.getSize() > MAX_BYTES)
            throw new IllegalArgumentException("La foto no puede superar 5 MB.");
        try (InputStream entrada = foto.getInputStream();
                ImageInputStream imagen = ImageIO.createImageInputStream(entrada)) {
            if (imagen == null) throw new IllegalArgumentException("No se pudo leer la foto.");
            Iterator<ImageReader> lectores = ImageIO.getImageReaders(imagen);
            if (!lectores.hasNext()) throw new IllegalArgumentException("Selecciona una imagen JPG o PNG válida.");
            ImageReader lector = lectores.next();
            try {
                String formato = lector.getFormatName();
                if (!"JPEG".equalsIgnoreCase(formato) && !"PNG".equalsIgnoreCase(formato))
                    throw new IllegalArgumentException("Solo se admiten fotos JPG y PNG.");
                lector.setInput(imagen, true, true);
                int ancho = lector.getWidth(0), alto = lector.getHeight(0);
                if (ancho <= 0 || alto <= 0 || ancho > 6000 || alto > 6000 || (long) ancho * alto > 16000000)
                    throw new IllegalArgumentException("La foto debe tener como máximo 6000 píxeles por lado y 16 millones de píxeles.");
                return lector.read(0);
            } catch (javax.imageio.IIOException e) {
                throw new IllegalArgumentException("La imagen está dañada. Selecciona otro JPG o PNG.", e);
            } finally {
                lector.dispose();
            }
        }
    }

    /** El ID es generado por MySQL. El nombre original del archivo nunca se usa como ruta. */
    public void guardar(Path temporal, int id) throws IOException {
        if (temporal == null) return;
        if (id <= 0 || !temporal.toAbsolutePath().normalize().getParent().equals(directorio))
            throw new IllegalArgumentException("Destino de imagen inválido.");
        Path destino = directorio.resolve(id + ".png");
        try {
            Files.move(temporal, destino, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(temporal, destino, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
