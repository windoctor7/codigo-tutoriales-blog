package windoctor7.github.io.spring5.reactive.kotlin

import org.springframework.core.io.ClassPathResource
import org.springframework.http.MediaType
import org.springframework.http.codec.ServerSentEvent
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Created by Ascari Q. Romo Pedraza - molder.itp@gmail.com on 23/10/2017.
 */
@RestController
class SearchUsersController(val repositorySearch: IRepositorySearch) {

    // text/event-stream para indicar que se trata de Server-Sent Event
    @GetMapping(path = arrayOf("/search/users"), produces = arrayOf("text/event-stream"))
    fun searchUsers(@RequestParam username: String): Flux<User> {

        //iniciamos búsqueda en github. Recordar que existe un retardo de 5 segundos
        val gh = repositorySearch.searchUsersGitHub(username)
            .flatMapIterable { gh -> gh.items }

        //iniciamos búsqueda en bitbucket.
        val bk = repositorySearch.searchUsersBitBucket(username)

        //finalmente hacemos un merge del Mono(github) y Flux (bitbucket)
        return Flux.merge(gh, bk)
    }

    //Cargamos el HTML
    @GetMapping(path = arrayOf("/"), produces = arrayOf(MediaType.TEXT_HTML_VALUE))
    fun index() = ClassPathResource("static/index.html")

}