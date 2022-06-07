package windoctor7.github.io.spring4.sse.Spring4SSE;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

/**
 * Creado por  Ascari Q. Romo Pedraza - molder.itp@gmail.com on 08/10/17.
 */
@Component
public class GenerarArchivo {

    @Autowired
    private ApplicationEventPublisher publisher;

    // se ejecuta cada 5 minutos
    @Scheduled(cron = "0 5 * ? * *")
    @Async
    public void generar() throws IOException {
        System.out.println("Iniciando escritura de archivo");
        File file = new File("/Users/ascariromopedraza/archivo.txt");
        for(int i = 0; i < 50000; i++){
            String cad = "linea "+i;
            FileUtils.writeStringToFile(file, cad, "UTF-8",true);
        }
        publisher.publishEvent(new MensajeEvent(this.getClass().getName()));
        System.out.println("Finalizando escritura de archivo");
    }
}
