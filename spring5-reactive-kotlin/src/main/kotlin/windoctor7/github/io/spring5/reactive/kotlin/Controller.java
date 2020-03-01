package windoctor7.github.io.spring5.reactive.kotlin;

import org.springframework.web.client.RestTemplate;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;
import reactor.util.context.Context;

import java.util.function.Consumer;
import java.util.function.LongConsumer;

/**
 * Created by Ascari Q. Romo Pedraza - molder.itp@gmail.com on 30/10/2017.
 */
public class Controller {

    Flux<User> metodo(){

       return Flux.create(new Consumer<FluxSink<? extends Object>>() {
           @Override
           public void accept(FluxSink<?> fluxSink) {

           }
       });

    }

    Mono<UserResultGh> metodo2(){
        return Mono.create(userMonoSink -> {
            String url = "https://api.github.com/search/users?q=windoctor";
            UserResultGh resultGh = new RestTemplate().getForObject(url, UserResultGh.class);
            userMonoSink.success(resultGh); // agregamos el elemento al Stream.
        });
    }

}
