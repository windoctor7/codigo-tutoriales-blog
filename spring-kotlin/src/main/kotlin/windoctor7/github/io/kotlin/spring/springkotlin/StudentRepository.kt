package windoctor7.github.io.kotlin.spring.springkotlin

import org.springframework.data.repository.CrudRepository
import reactor.core.publisher.Flux

/**
 * Creado por  Ascari Q. Romo Pedraza - molder.itp@gmail.com on 22/10/17.
 */
interface StudentRepository : CrudRepository<Student,String>{
    fun findByName(name:String): Flux<Student>
}