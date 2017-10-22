package basic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Creado por  Ascari Q. Romo Pedraza - molder.itp@gmail.com on 20/03/17.
 */
@Component
public class RecordatorioEmail extends Recordatorio{

    @Autowired
    @Qualifier("correo")
    IMensaje email;

    @Override
    public void enviar(Cliente cliente) {
        int dias = cliente.getDiaPago() - getDay();

        if( dias >= 1 && dias <= 2 )
            email.enviar(cliente.getEmail(), "Mensaje por correo electronico");
        else
            recordatorio.enviar(cliente);
    }
}
