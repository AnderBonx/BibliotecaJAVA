import java.util.*;
import java.time.*;

// ---------------------- Clase Libro ----------------------
class Libro {
    private String titulo;
    private String autor;
    private String codigo;
    private boolean disponible;

    public Libro(String titulo, String autor, String codigo) {
        this.titulo = titulo;
        this.autor = autor;
        this.codigo = codigo;
        this.disponible = true;
    }

    public String getCodigo() { return codigo; }
    public boolean isDisponible() { return disponible; }
    public void marcarPrestado() { disponible = false; }
    public void marcarDisponible() { disponible = true; }
    public String getTitulo() { return titulo; }

    public void mostrarDatos() {
        System.out.println("[" + codigo + "] " + titulo + " - " + autor + 
                           (disponible ? " (Disponible)" : " (Prestado)"));
    }
}

// ---------------------- Clase Usuario ----------------------
class Usuario {
    private String nombre;
    private String idUsuario;
    private List<Libro> librosPrestados;

    public Usuario(String nombre, String idUsuario) {
        this.nombre = nombre;
        this.idUsuario = idUsuario;
        this.librosPrestados = new ArrayList<>();
    }

    public String getIdUsuario() { return idUsuario; }
    public List<Libro> getLibrosPrestados() { return librosPrestados; }

    public boolean puedePrestar() {
        return librosPrestados.size() < 3;
    }

    public void agregarPrestamo(Libro libro) {
        if (puedePrestar()) {
            librosPrestados.add(libro);
        } else {
            System.out.println("⚠ El usuario ya tiene el máximo de libros prestados.");
        }
    }

    public void devolverLibro(Libro libro) {
        librosPrestados.remove(libro);
    }

    public void mostrarDatos() {
        System.out.println("Usuario: " + nombre + " (ID: " + idUsuario + ")");
        System.out.println("Libros prestados:");
        for (Libro l : librosPrestados) {
            System.out.println(" - " + l.getTitulo());
        }
    }
}

// ---------------------- Clase Prestamo ----------------------
class Prestamo {
    private Libro libro;
    private Usuario usuario;
    private LocalDate fechaInicio;
    private LocalDate fechaLimite;

    public Prestamo(Libro libro, Usuario usuario, LocalDate fechaInicio, LocalDate fechaLimite) {
        this.libro = libro;
        this.usuario = usuario;
        this.fechaInicio = fechaInicio;
        this.fechaLimite = fechaLimite;
    }

    public Libro getLibro() { return libro; }
    public Usuario getUsuario() { return usuario; }
    public LocalDate getFechaLimite() { return fechaLimite; }

    public void mostrarDatos() {
        System.out.println("Libro: " + libro.getTitulo() + " | Usuario: " + usuario.getIdUsuario() +
                           " | Desde: " + fechaInicio + " | Límite: " + fechaLimite);
    }
}

// ---------------------- Clase Biblioteca ----------------------
class Biblioteca {
    private List<Libro> libros;
    private List<Usuario> usuarios;
    private List<Prestamo> prestamos;

    public Biblioteca() {
        libros = new ArrayList<>();
        usuarios = new ArrayList<>();
        prestamos = new ArrayList<>();
    }

    public void registrarLibro(String titulo, String autor, String codigo) {
        libros.add(new Libro(titulo, autor, codigo));
        System.out.println("✅ Libro registrado: " + titulo);
    }

    public void registrarUsuario(String nombre, String idUsuario) {
        usuarios.add(new Usuario(nombre, idUsuario));
        System.out.println("✅ Usuario registrado: " + nombre);
    }

    private Libro buscarLibro(String codigo) {
        for (Libro l : libros) {
            if (l.getCodigo().equals(codigo)) return l;
        }
        return null;
    }

    private Usuario buscarUsuario(String id) {
        for (Usuario u : usuarios) {
            if (u.getIdUsuario().equals(id)) return u;
        }
        return null;
    }

    public void prestarLibro(String codigo, String idUsuario) {
        Libro libro = buscarLibro(codigo);
        Usuario usuario = buscarUsuario(idUsuario);

        if (libro == null || usuario == null) {
            System.out.println("⚠ Libro o usuario no encontrado.");
            return;
        }

        if (!libro.isDisponible()) {
            System.out.println("⚠ El libro no está disponible.");
            return;
        }

        if (!usuario.puedePrestar()) {
            System.out.println("⚠ El usuario ya alcanzó el límite de préstamos.");
            return;
        }

        libro.marcarPrestado();
        usuario.agregarPrestamo(libro);

        LocalDate hoy = LocalDate.now();
        LocalDate fechaLimite = hoy.plusDays(7); // 7 días de préstamo
        Prestamo p = new Prestamo(libro, usuario, hoy, fechaLimite);
        prestamos.add(p);

        System.out.println("✅ Libro prestado hasta " + fechaLimite);
    }

    public void devolverLibro(String codigo, String idUsuario) {
        Libro libro = buscarLibro(codigo);
        Usuario usuario = buscarUsuario(idUsuario);

        if (libro == null || usuario == null) {
            System.out.println("⚠ Libro o usuario no encontrado.");
            return;
        }

        Prestamo prestamoEncontrado = null;
        for (Prestamo p : prestamos) {
            if (p.getLibro() == libro && p.getUsuario() == usuario) {
                prestamoEncontrado = p;
                break;
            }
        }

        if (prestamoEncontrado == null) {
            System.out.println("⚠ No existe un préstamo de este libro para este usuario.");
            return;
        }

        // Calcular multa
        LocalDate hoy = LocalDate.now();
        long diasRetraso = 0;
        if (hoy.isAfter(prestamoEncontrado.getFechaLimite())) {
            diasRetraso = Duration.between(prestamoEncontrado.getFechaLimite().atStartOfDay(),
                                           hoy.atStartOfDay()).toDays();
        }

        if (diasRetraso > 0) {
            long multa = diasRetraso * 500;
            System.out.println("⚠ Libro devuelto con retraso de " + diasRetraso + " días.");
            System.out.println("💰 Multa a pagar: $" + multa);
        } else {
            System.out.println("✅ Libro devuelto a tiempo, sin multa.");
        }

        libro.marcarDisponible();
        usuario.devolverLibro(libro);
        prestamos.remove(prestamoEncontrado);
    }

    public void mostrarLibrosDisponibles() {
        System.out.println("📚 Libros disponibles:");
        for (Libro l : libros) {
            if (l.isDisponible()) {
                l.mostrarDatos();
            }
        }
    }

    public void mostrarUsuarios() {
        System.out.println("👥 Usuarios registrados:");
        for (Usuario u : usuarios) {
            u.mostrarDatos();
        }
    }

    public void mostrarHistorialPrestamos() {
        System.out.println("📖 Historial de préstamos:");
        for (Prestamo p : prestamos) {
            p.mostrarDatos();
        }
    }
}

// ---------------------- Clase Main ----------------------
public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Biblioteca biblioteca = new Biblioteca();
        int opcion;

        do {
            System.out.println("\n===== Sistema de Biblioteca =====");
            System.out.println("1. Registrar libro");
            System.out.println("2. Registrar usuario");
            System.out.println("3. Prestar libro");
            System.out.println("4. Devolver libro");
            System.out.println("5. Mostrar libros disponibles");
            System.out.println("6. Mostrar usuarios");
            System.out.println("7. Mostrar historial de préstamos");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");
            opcion = sc.nextInt();
            sc.nextLine();

            switch (opcion) {
                case 1:
                    System.out.print("Título: ");
                    String titulo = sc.nextLine();
                    System.out.print("Autor: ");
                    String autor = sc.nextLine();
                    System.out.print("Código: ");
                    String codigo = sc.nextLine();
                    biblioteca.registrarLibro(titulo, autor, codigo);
                    break;
                case 2:
                    System.out.print("Nombre: ");
                    String nombre = sc.nextLine();
                    System.out.print("ID Usuario: ");
                    String id = sc.nextLine();
                    biblioteca.registrarUsuario(nombre, id);
                    break;
                case 3:
                    System.out.print("Código del libro: ");
                    String codPrestar = sc.nextLine();
                    System.out.print("ID Usuario: ");
                    String idPrestar = sc.nextLine();
                    biblioteca.prestarLibro(codPrestar, idPrestar);
                    break;
                case 4:
                    System.out.print("Código del libro: ");
                    String codDevolver = sc.nextLine();
                    System.out.print("ID Usuario: ");
                    String idDevolver = sc.nextLine();
                    biblioteca.devolverLibro(codDevolver, idDevolver);
                    break;
                case 5:
                    biblioteca.mostrarLibrosDisponibles();
                    break;
                case 6:
                    biblioteca.mostrarUsuarios();
                    break;
                case 7:
                    biblioteca.mostrarHistorialPrestamos();
                    break;
                case 0:
                    System.out.println("👋 Saliendo del sistema...");
                    break;
                default:
                    System.out.println("⚠ Opción no válida.");
            }
        } while (opcion != 0);

        sc.close();
    }
}