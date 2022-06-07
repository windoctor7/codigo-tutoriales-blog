package windoctor7.github.io.kotlin.spring.springkotlin

import org.springframework.data.mongodb.core.mapping.Document

/**
 * Creado por  Ascari Q. Romo Pedraza - molder.itp@gmail.com on 21/10/17.
 */
@Document(collection = "students")
data class Student(val id:String, val name:String) {
}