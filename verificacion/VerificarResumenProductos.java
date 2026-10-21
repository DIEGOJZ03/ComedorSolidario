import com.comedorsolidario.model.*;
import com.comedorsolidario.service.ResumenProductosService;
import java.math.BigDecimal;
import java.util.*;

/** Comprueba la lógica del catálogo sin conectar ni modificar MySQL. */
class VerificarResumenProductos {
    private static Necesidad necesidad(String nombre, String cantidad, String unidad) {
        Necesidad n = new Necesidad();
        n.setNombre(nombre);
        n.setCantidadNecesaria(new BigDecimal(cantidad));
        n.setUnidad(unidad);
        n.setEstado("ACTIVA");
        return n;
    }
    private static void comprobar(boolean cumple, String mensaje) {
        if (!cumple) throw new AssertionError(mensaje);
    }
    private static boolean igual(BigDecimal valor, String esperado) {
        return valor.compareTo(new BigDecimal(esperado)) == 0;
    }
    public static void main(String[] args) {
        ResumenProductosService servicio = new ResumenProductosService();
        Necesidad arroz = necesidad("Arroz", "50.00", "kg");
        Necesidad otro = necesidad(" arroz ", "30.10", "Kilos");
        arroz.setId(101); arroz.setComedorId(5); arroz.setComedorNombre("Unidos por el Callao");
        otro.setId(102); otro.setComedorId(1); otro.setComedorNombre("Otro comedor");
        List<Necesidad> datos = new ArrayList<>(List.of(arroz, otro));
        comprobar(igual(servicio.resumir(datos).getFirst().getCantidadTotal(), "80.10"),
                "Debe agrupar nombres equivalentes y sumar decimales sin producto_id.");

        datos.add(necesidad("Azúcar", "10", "kg"));
        datos.add(necesidad("Arroz", "2", "cajas"));
        comprobar(servicio.resumir(datos).size() == 3,
                "Productos diferentes y unidades diferentes deben permanecer separados.");
        ResumenProducto detalle = servicio.resumir(datos).stream()
                .filter(r -> r.getNombre().equals("Arroz") && r.getUnidad().equals("kg"))
                .findFirst().orElseThrow();
        comprobar(detalle.getNecesidades().size() == 2
                && detalle.getNecesidades().containsAll(List.of(arroz, otro))
                && detalle.getNecesidades().stream().anyMatch(n -> n.getId() == 101
                    && n.getComedorId() == 5 && n.getComedorNombre().equals("Unidos por el Callao"))
                && detalle.getNecesidades().stream().map(Necesidad::getCantidadNecesaria)
                    .reduce(BigDecimal.ZERO, BigDecimal::add).compareTo(detalle.getCantidadTotal()) == 0,
                "El detalle debe conservar solicitudes y destinos exactos y sumar el total sin incluir otro producto o unidad.");

        otro.setEstado("INACTIVA");
        arroz.setCantidadNecesaria(new BigDecimal("25"));
        comprobar(servicio.resumir(datos).stream().anyMatch(r -> r.getNombre().equals("Arroz")
                && r.getUnidad().equals("kg") && igual(r.getCantidadTotal(), "25")
                && r.getNecesidades().equals(List.of(arroz))),
                "La siguiente consulta debe reflejar la edición y desactivación.");
        ResumenProducto seleccionado = servicio.buscar(datos, " ARROZ ", "kilogramos");
        comprobar(seleccionado != null && igual(seleccionado.getCantidadTotal(), "25")
                && seleccionado.getNecesidades().equals(List.of(arroz))
                && igual(servicio.buscar(datos, "Arroz", "cajas").getCantidadTotal(), "2")
                && servicio.buscar(datos, "Arroz", "litros") == null
                && servicio.buscar(datos, "Producto inexistente", "kg") == null,
                "Ver debe seleccionar por nombre y unidad, recalcular los datos y no elegir otro producto si no existe.");

        ResumenProducto aceite = servicio.resumir(List.of(necesidad("Aceite", "100", "litros"))).getFirst();
        comprobar(igual(aceite.getEnvases20Litros(), "5") && igual(aceite.getLitrosRestantes(), "0"),
                "100 L deben equivaler a cinco envases completos de 20 L.");
        aceite = servicio.resumir(List.of(necesidad("Aceite", "105.50", "l"))).getFirst();
        comprobar(igual(aceite.getEnvases20Litros(), "5") && igual(aceite.getLitrosRestantes(), "5.50"),
                "El volumen sobrante debe conservarse sin redondear envases hacia arriba.");
        aceite = servicio.resumir(List.of(necesidad("Aceite", "0.50", "litros"))).getFirst();
        comprobar(igual(aceite.getEnvases20Litros(), "0") && igual(aceite.getLitrosRestantes(), "0.50")
                && servicio.resumir(List.of()).isEmpty(),
                "Un volumen pequeño no debe inventar envases completos; un catálogo vacío no inventa productos.");
        System.out.println("OK: 8 escenarios del catálogo, selección del detalle y equivalencia de envases. Sin usar MySQL.");
    }
}
