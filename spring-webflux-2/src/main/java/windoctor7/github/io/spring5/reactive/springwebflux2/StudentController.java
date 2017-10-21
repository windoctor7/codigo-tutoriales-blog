package windoctor7.github.io.spring5.reactive.springwebflux2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Created by Ascari Q. Romo Pedraza - molder.itp@gmail.com on 19/10/2017.
 */
@RestController
@RequestMapping("/api/students")
public class StudentController {

    @Autowired
    private StudentReactiveRepository repository;

    @GetMapping
    public Flux<Student> allStudents(){
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Mono<Student> getUser(@PathVariable String id) {
        return repository.findById(id);
    }




}
