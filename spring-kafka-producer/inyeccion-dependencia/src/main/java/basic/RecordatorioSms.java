package basic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Creado por  Ascari Q. Romo Pedraza - molder.itp@gmail.com on 20/03/17.
 */
@Component
public class RecordatorioSms extends Recordatorio {

    @Autowired
    @Qualifier("mensaje")
    IMensaje sms;

    @Override
    public void enviar(Cliente cliente) {
        int dias = cliente.getDiaPago() - getDay();
        if(dias == 0)
            sms.enviar(cliente.getCelular(), "Mensaje enviado por SMS");
        else
            recordatorio.enviar(cliente);
    }
}
