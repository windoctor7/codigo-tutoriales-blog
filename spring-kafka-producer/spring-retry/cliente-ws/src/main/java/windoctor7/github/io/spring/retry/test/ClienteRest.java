package windoctor7.github.io.spring.retry.test;

import org.springframework.boot.CommandLineRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Creado por  Ascari Q. Romo Pedraza - molder.itp@gmail.com on 09/04/17.
 */
@Component("clienteRest")
public class ClienteRest implements CommandLineRunner {

    @Retryable(backoff = @Backoff(7000), maxAttempts = 5)
    public void run(String... args)  {
        System.out.println("Iniciando el llamado al WS - " + System.currentTimeMillis() / 1000);
        String url = "http://localhost:8080/empleados/{id}";
        int id = 1;

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Empleado> entity = restTemplate.getForEntity(url, Empleado.class, id);
        Empleado empleado = entity.getBody();
        System.out.println(empleado);

    }

    /**
     * Lamentablemente nosotros no podemos ver esto
     * pues al utilizar la interfaz CommandLineRunner, si en el método run(String… args) se lanza una excepción,
     * causará que el contexto de spring se cierre y nuestra aplicación se detendrá,
     * ocasionando que el método recover no se ejecute.
     *
     * Para que una aplicación que usa CommandLineRunner no se termine en el caso de lanzar una excepción
     * en el método run(..) será necesario agregar un bloque try/catch sin embargo si lo hacemos
     * la excepción no se lanzará un nuestra política de reintentos no se ejecutará sencillamente porque
     * no habrá detectado ninguna excepción.
     *
     * Sin embargo esto no es ningun problema, en una aplicación real no usarás CommandLineRunner,
     * aquí se uso solo para fines demostrativos y rápidos.
     */
    @Recover
    public void recover(){
        System.out.println("Aqui el código que deseamos ejecutar en caso que la política de reintentos falle");
    }
}
