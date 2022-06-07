package windoctor7.github.io.spring4.sse.Spring4SSE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

/**
 * Creado por  Ascari Q. Romo Pedraza - molder.itp@gmail.com on 13/10/17.
 */
@RestController
public class SseController {

    @Autowired
    private GenerarArchivo task;

    @Autowired
    private IJobExecutor jobExecutor;

    @GetMapping("/execute")
    public SseEmitter ejecutar(@RequestParam String beanClass) throws IOException {

        SseEmitter emitter = new SseEmitter();

        // validamos que el Map NO contenga ya el Job. Si lo contiene, NO permitimos la ejecución del Job.
        if (isTaskExists(beanClass)) {
            String msg = String.format("El Job %s ya está siendo ejecutado. No es posible ejecutarlo en este momento.", beanClass);
            emitter.send(new Mensaje(2, msg));
            emitter.complete();
            return emitter;
        }

        config(emitter,beanClass);

        //Agregamos el emiter al Map y lo ejecutamos
        JobExecutor.EMITTERS.put(beanClass, emitter);
        jobExecutor.executeBean(beanClass);

        // Dado que la ejecución del Job ahora es asíncrona,
        //devolvemos inmediatamente la respuesta al cliente.
        emitter.send(new Mensaje(1,"Ejecutanto tarea..."));
        return emitter;
    }

    private boolean isTaskExists(String beanClass){
        return JobExecutor.EMITTERS.containsKey(beanClass);
    }

    // Establecemos que cuando el SseEmitter concluya, remueva dicho emitter del Map
    private void config(SseEmitter emitter, String beanClass){
        emitter.onCompletion(() -> {
            if (isTaskExists(beanClass))
                System.out.println("ELIMINANDO EMITTER");
                JobExecutor.EMITTERS.remove(beanClass);
            }
        );
    }

}
