package basic;

import java.util.Calendar;

/**
 * Creado por  Ascari Q. Romo Pedraza - molder.itp@gmail.com on 20/03/17.
 */
public abstract class Recordatorio {

    Recordatorio recordatorio;

    public void nextHandler(Recordatorio recordatorio){
        this.recordatorio = recordatorio;
    }

    public int getDay(){
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    public abstract void enviar(Cliente cliente);

}
