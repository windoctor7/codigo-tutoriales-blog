package windoctor7.github.io.spring.auth.jwt;

/**
 * Creado por  Ascari Q. Romo Pedraza - molder.itp@gmail.com on 28/04/17.
 */
public class Usuario {
    private int id;
    private String name;

    public Usuario(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
