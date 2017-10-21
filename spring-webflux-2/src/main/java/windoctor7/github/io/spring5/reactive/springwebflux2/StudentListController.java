package windoctor7.github.io.spring5.reactive.springwebflux2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;

import java.time.Duration;

/**
 * Created by Ascari Q. Romo Pedraza - molder.itp@gmail.com on 19/10/2017.
 */
@Controller
public class StudentListController {

    @Autowired
    private StudentReactiveRepository repository;

    @GetMapping("/list-students")
    public String listStudents(Model model){
        Flux<Student> flux = repository.findAll(); // recuperamos todos los registros de forma reactiva
        model.addAttribute("students", flux);
        return "students"; // direccionamos al students.html
    }


    @GetMapping("/list-students-reactive")
    public String listUsersReactive(Model model)
    {
        Flux<Student> userFlux = repository.findAll();
        model.addAttribute("students", new ReactiveDataDriverContextVariable(userFlux, 50));
        return "students";
    }

}
