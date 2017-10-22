package windoctor7.github.io.kotlin.spring.springkotlin

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class SpringKotlinApplication

fun main(args: Array<String>) {
    SpringApplication.run(SpringKotlinApplication::class.java, *args)
}
