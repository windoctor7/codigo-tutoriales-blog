package basic;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Creado por  Ascari Q. Romo Pedraza - molder.itp@gmail.com on 20/03/17.
 */
public class Main {

    public static void main(String[] args) {

        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(ApplicationConfig.class);
        ctx.refresh();

        Cliente cliente = (Cliente) ctx.getBean("cliente");

        cliente.setAbono(280);
        cliente.setDiaPago(1); //1 Domingo, 2 Lunes, 3 Martes, etc.
        cliente.setEmail("molder.itp@gmail.com");
        cliente.setTwitter("@windoctor");
        cliente.setCelular("0123456789");

        cliente.enviarRecordatorio();

    }
}
