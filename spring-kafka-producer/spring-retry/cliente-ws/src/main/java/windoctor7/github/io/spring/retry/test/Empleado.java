package windoctor7.github.io.spring.retry.test;

/**
 * Creado por  Ascari Q. Romo Pedraza - molder.itp@gmail.com on 09/04/17.
 */
public class Empleado {

    public int id;
    public String nombre;

    public Empleado() {
    }

    public Empleado(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return "Empleado{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                '}';
    }
}

