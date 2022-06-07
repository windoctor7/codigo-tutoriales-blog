package windoctor7.github.io.spring4.sse.Spring4SSE;

/**
 * Creado por  Ascari Q. Romo Pedraza - molder.itp@gmail.com on 13/10/17.
 */
public class MensajeEvent {

    private String beanClass;

    public MensajeEvent(String beanClass) {
        this.beanClass = beanClass;
    }

    public String getBeanClass() {
        return beanClass;
    }
}
