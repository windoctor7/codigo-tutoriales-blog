package windoctor7.github.io.spring.jdbc.springjdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Creado por  Ascari Q. Romo Pedraza - molder.itp@gmail.com on 15/10/17.
 */
@RestController
public class JdbcController {

    @Autowired
    private DepartamentoRepository repository;

    @GetMapping("/departamentos")
    public List<Departamento> getUsers(){
        return repository.findAll();
    }

}
