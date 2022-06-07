package windoctor7.github.io.spring.async;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Creado por  Ascari Q. Romo Pedraza - molder.itp@gmail.com on 09/04/17.
 */
@RestController
@RequestMapping("/registro")
public class RegistroController {

    @Autowired
    RegistroAsync registroAsync;

    @RequestMapping(method = RequestMethod.GET, value = "/usuario")
    public String registrar(){

        Usuario usuario = new Usuario("ascari", ""); //Pon la cuenta de correo a donde quieres enviar el correo
        Usuario registro = registroAsync.registrar(usuario);
        System.out.println("ahora enviamos el correo");
        registroAsync.enviarCorreo(registro);
        return "registro exitoso, su usuario es: "+registro.getUser();
    }
}
