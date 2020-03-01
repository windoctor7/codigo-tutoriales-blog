package windoctor7.github.io.spring5.reactive.kotlin

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created by Ascari Q. Romo Pedraza - molder.itp@gmail.com on 24/10/2017.
 */
data class User(
    @JsonAlias("uuid","id") val id:String,
    @JsonAlias("username","login") val username: String) {
}
