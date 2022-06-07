package basic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Creado por  Ascari Q. Romo Pedraza - molder.itp@gmail.com on 20/03/17.
 */
@Component
public class RecordatorioTwitter extends Recordatorio {

    @Autowired
    @Qualifier("tweet")
    IMensaje tweet;

    @Override
    public void enviar(Cliente cliente) {
        int dias = cliente.getDiaPago() - getDay();
        if(dias>=3 && dias<= 7)
            tweet.enviar(cliente.getTwitter(), "Mensaje enviado por Twitter");
    }
}
