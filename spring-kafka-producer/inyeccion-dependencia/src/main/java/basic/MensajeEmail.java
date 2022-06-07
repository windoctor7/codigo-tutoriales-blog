package basic;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Creado por  Ascari Q. Romo Pedraza - molder.itp@gmail.com on 20/03/17.
 */
@Component(value = "correo")
public class MensajeEmail implements IMensaje {
    @Override
    public void enviar(String destinatario, String mensaje) {
        System.out.printf("Enviando mensaje por correo electronico a %s \n Mensaje: %s", destinatario, mensaje);
    }
}
