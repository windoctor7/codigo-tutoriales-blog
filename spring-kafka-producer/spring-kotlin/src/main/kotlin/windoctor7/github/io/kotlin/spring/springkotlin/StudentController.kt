package windoctor7.github.io.kotlin.spring.springkotlin

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.atomic.AtomicInteger

/**
 * Creado por  Ascari Q. Romo Pedraza - molder.itp@gmail.com on 21/10/17.
 */
@RestController
class StudentController(val repository: StudentRepository) {

    @GetMapping("/dummy-student")
    fun students(@RequestParam name:String) = Student("1", name)

    @GetMapping("/students")
    fun studentByName(@RequestParam name:String) = repository.findByName(name)

}