package windoctor7.github.io.spring5.reactive.kotlin

import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import reactor.core.publisher.Flux
import java.net.HttpURLConnection
import java.net.InetSocketAddress
import java.net.Proxy
import java.net.URL
import kotlin.text.Charsets
import java.io.IOException
import java.io.InputStream
import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets
import java.util.*
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import reactor.core.publisher.Mono
import java.time.Duration


/**
 * Created by Ascari Q. Romo Pedraza - molder.itp@gmail.com on 23/10/2017.
 */
@Component
class RepositorySearch() : IRepositorySearch {

    override fun searchUsersGitHub(username: String): Mono<UserResultGh> {
        val url = "https://api.github.com/search/users?q=$username"
        return Mono.create<UserResultGh> { sink ->
            sink.success( RestTemplate().getForObject(url, UserResultGh::class.java) )
        }.delayElement(Duration.ofSeconds(5)) //agregamos un retardo intencional de 5 segundos
    }

    override fun searchUsersBitBucket(username: String): Flux<User> {
        val url = "https://api.bitbucket.org/2.0/users/$username"
        return Flux.create<User> { sink ->
            val user:User? = RestTemplate().getForObject(url, User::class.java)
            sink.next( user)
        }
    }
}