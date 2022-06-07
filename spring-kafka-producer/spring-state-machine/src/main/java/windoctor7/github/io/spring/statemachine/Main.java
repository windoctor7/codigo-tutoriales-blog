package windoctor7.github.io.spring.statemachine;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Creado por  Ascari Q. Romo Pedraza - molder.itp@gmail.com on 12/04/17.
 */
public class Main {
    public static void main(String[] args) {
        new AnnotationConfigApplicationContext(CuestionarioEstados.class);
    }
}
