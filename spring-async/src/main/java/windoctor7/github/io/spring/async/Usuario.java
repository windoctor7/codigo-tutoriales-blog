package windoctor7.github.io.spring.async;

/**
 * Creado por  Ascari Q. Romo Pedraza - molder.itp@gmail.com on 09/04/17.
 */
public class Usuario {

    private String nombre;
    private String email;
    private String user;

    public Usuario(String nombre, String email) {
        this.nombre = nombre;
        this.email = email;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    @Override
    public String toString() {
        return "Usuario{" +
                "nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                ", user='" + user + '\'' +
                '}';
    }
}
