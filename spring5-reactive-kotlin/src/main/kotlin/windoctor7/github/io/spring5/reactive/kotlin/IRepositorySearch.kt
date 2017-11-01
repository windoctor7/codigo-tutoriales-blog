package windoctor7.github.io.spring5.reactive.kotlin

import com.fasterxml.jackson.annotation.JsonProperty
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Created by Ascari Q. Romo Pedraza - molder.itp@gmail.com on 23/10/2017.
 */
interface IRepositorySearch {
    fun searchUsersGitHub(username:String) : Mono<UserResultGh>
    fun searchUsersBitBucket(username: String) : Flux<User>
}

