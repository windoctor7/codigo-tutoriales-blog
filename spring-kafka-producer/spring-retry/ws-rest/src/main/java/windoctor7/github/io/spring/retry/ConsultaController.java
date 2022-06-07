package windoctor7.github.io.spring.retry;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Creado por  Ascari Q. Romo Pedraza - molder.itp@gmail.com on 09/04/17.
 */
@RestController
@RequestMapping("/empleados")
public class ConsultaController {

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, value = "/{id}")
    public Empleado buscarPorId(@PathVariable int id){
        if(id == 1)
            return new Empleado(id,"David Perez");
        else if(id == 2)
            return new Empleado(id, "Juan Rodriguez");
        else
            return new Empleado(-1, "Empleado no existente");

    }

}
