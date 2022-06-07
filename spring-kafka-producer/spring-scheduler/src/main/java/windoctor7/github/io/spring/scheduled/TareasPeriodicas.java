package windoctor7.github.io.spring.scheduled;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Creado por  Ascari Q. Romo Pedraza - molder.itp@gmail.com on 07/04/17.
 */
@Component
public class TareasPeriodicas {


    // Se ejecuta cada 3 segundos
    @Scheduled(fixedRate = 3000)
    public void tarea1() {
        System.out.println("Tarea usando fixedRate cada 3 segundos - " + System.currentTimeMillis() / 1000);
    }

    @Scheduled(fixedRateString = "${imprime.tarea}")
    public void tarea2() {
        System.out.println("Tarea usando fixedRateString cada 5 segundos - " + System.currentTimeMillis() / 1000);
    }

    @Scheduled(fixedRate = 3000, initialDelay = 10000)
    public void tarea3() {
        System.out.println("Tarea con retraso inicial de 10 segundos - " + System.currentTimeMillis() / 1000);
    }


    @Scheduled(cron = "0/15 * 0 ? * 6,7 ")
    public void tarea4() {
        System.out.println("Tarea usando expresiones cron - " + new Date());
    }
}
