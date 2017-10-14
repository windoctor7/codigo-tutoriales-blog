package windoctor7.github.io.spring4.sse.Spring4SSE;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Creado por  Ascari Q. Romo Pedraza - molder.itp@gmail.com on 08/10/17.
 */
@Component
public class JobExecutor implements IJobExecutor{

    @Autowired
    private ApplicationContext appContext;

    public final static Map<String,SseEmitter> EMITTERS = new ConcurrentHashMap<>();

    public void executeBean(String beanClass) {
        Object bean = null;
        SseEmitter emitter = JobExecutor.EMITTERS.get(beanClass);
        try {
            //Cargamos la clase
            Class<?> clazz = Class.forName(beanClass);
            bean = getExistingBean(clazz);

            //Si el Job no existe, mandamos un mensaje y completamos el emitter
            if (bean == null) {
                String msg = String.format("No existe ningún Job con el nombre: %s", bean);
                emitter.send(new Mensaje(3, msg));
                emitter.complete();
            }
            for (Method method : AopUtils.getTargetClass(bean).getMethods()) {
                if (method.isAnnotationPresent(Scheduled.class)) {
                    method.invoke(bean);
                    break;
                }
            }
        }
        catch (Exception e) {
            emitter.completeWithError(e);
        }
    }

    private Object getExistingBean(Class<?> beanClass){
        Object bean;
        bean = appContext.getBean(beanClass);

        return bean;

    }

    @EventListener
    public void eventListener(MensajeEvent mensajeEvent) throws IOException {
        String bean = mensajeEvent.getBeanClass();
        SseEmitter emitter = JobExecutor.EMITTERS.get(bean);
        String msg = String.format("El Job %s ha finalizado correctamente", bean);
        emitter.send(new Mensaje(2,msg));
        emitter.complete();
    }
}
