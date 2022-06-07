package windoctor7.github.io.spring5.reactive;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;

/**
 * Creado por  Ascari Q. Romo Pedraza - molder.itp@gmail.com on 20/04/17.
 */
@RestController
public class NumerosController {

    @GetMapping(path = "numeros", produces = "text/event-stream")
    public Flux<Integer> all () {
        return Flux.range(1, 30)
                .delayElements(Duration.ofSeconds(1)).repeat().map(n->n);
    }
}
