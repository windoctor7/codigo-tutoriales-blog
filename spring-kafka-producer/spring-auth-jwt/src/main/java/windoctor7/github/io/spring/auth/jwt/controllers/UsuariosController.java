package windoctor7.github.io.spring.auth.jwt.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import windoctor7.github.io.spring.auth.jwt.Usuario;

import java.util.Arrays;
import java.util.List;

/**
 * Creado por  Ascari Q. Romo Pedraza - molder.itp@gmail.com on 28/04/17.
 */
@RestController
public class UsuariosController {

    @GetMapping(path = "/users")
    public List<Usuario> getUsers(){
        return Arrays.asList(new Usuario(1,"Paco"), new Usuario(2,"Pedro"), new Usuario(3, "Juan"));
    }

}
