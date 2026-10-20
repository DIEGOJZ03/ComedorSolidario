import java.nio.file.*;
import java.lang.reflect.*;
import java.util.*;
import com.comedorsolidario.util.ValidacionUtil;

/** Verifica el entregable contra las clases compiladas; no sustituye pruebas JDBC/HTTP. */
class VerificarRubrica {
    static int checks;

    static void check(boolean ok, String label) {
        if (!ok) throw new AssertionError(label);
        checks++;
    }

    static void rejects(Runnable r, String label) {
        try {
            r.run();
            throw new AssertionError(label);
        } catch (IllegalArgumentException expected) {
            checks++;
        }
    }

    public static void main(String[] args) throws Exception {
        String uml = Files.readString(Path.of(args[0], "docs/modelo-clases.puml"));
        for (String name : List.of("Usuario", "Comedor", "Necesidad", "Donacion", "Producto", "Campania")) {
            Class<?> c = Class.forName("com.comedorsolidario.model." + name);
            String section = uml.split("class " + name + " \\{")[1].split("\\n}")[0];
            for (Field f : c.getDeclaredFields()) {
                check(Modifier.isPrivate(f.getModifiers()), name + " encapsulamiento " + f.getName());
                check(section.contains("- " + f.getName() + " : " + f.getType().getSimpleName()), name + " atributo UML " + f.getName());
                String suffix = Character.toUpperCase(f.getName().charAt(0)) + f.getName().substring(1);
                check(c.getMethod((f.getType() == boolean.class ? "is" : "get") + suffix).getReturnType() == f.getType(), "getter");
                check(c.getMethod("set" + suffix, f.getType()).getReturnType() == void.class, "setter");
            }
            for (Method m : c.getDeclaredMethods())
                check(section.contains("+ " + m.getName() + "("), name + " metodo UML " + m.getName());
        }
        check(ValidacionUtil.cantidad("0.01").toPlainString().equals("0.01"), "cantidad minima");
        check(ValidacionUtil.cantidad("9999999999.99").toPlainString().equals("9999999999.99"), "cantidad maxima");
        rejects(() -> ValidacionUtil.cantidad("0"), "cantidad cero");
        rejects(() -> ValidacionUtil.cantidad("-1"), "cantidad negativa");
        rejects(() -> ValidacionUtil.cantidad("1.001"), "precision");
        rejects(() -> ValidacionUtil.cantidad("10000000000"), "overflow decimal");
        rejects(() -> ValidacionUtil.cantidad("no"), "texto cantidad");
        rejects(() -> ValidacionUtil.fecha("2026-02-30"), "fecha inexistente");
        rejects(() -> ValidacionUtil.fecha("0999-01-01"), "rango mysql");
        check(ValidacionUtil.fecha("2028-02-29").getDayOfMonth() == 29, "bisiesto");
        rejects(() -> ValidacionUtil.id("0"), "id cero");
        rejects(() -> ValidacionUtil.id("1 OR 1=1"), "id invalido");
        rejects(() -> ValidacionUtil.opcion("ADMIN", "DONADOR"), "opcion fuera de lista");
        System.out.println("OK: " + checks + " comprobaciones de encapsulamiento, correspondencia UML y limites de validacion. No son pruebas JDBC ni HTTP.");
    }
}
