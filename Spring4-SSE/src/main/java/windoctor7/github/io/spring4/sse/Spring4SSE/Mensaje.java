package windoctor7.github.io.spring4.sse.Spring4SSE;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Creado por  Ascari Q. Romo Pedraza - molder.itp@gmail.com on 13/10/17.
 */
public class Mensaje  {

    private int cve;
    private String msg;

    public Mensaje(int cve, String msg) {
        this.cve = cve;
        this.msg = msg;
    }

    public int getCve() {
        return cve;
    }

    public void setCve(int cve) {
        this.cve = cve;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
