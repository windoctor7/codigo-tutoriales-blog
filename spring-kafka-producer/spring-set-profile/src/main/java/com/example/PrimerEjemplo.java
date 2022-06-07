package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Creado por  Ascari Q. Romo Pedraza - molder.itp@gmail.com on 08/04/17.
 */
@Component
public class PrimerEjemplo {

    private Environment env;

    @Autowired
    public PrimerEjemplo(Environment env) {
        this.env = env;
    }

    public void imprimir(){
        System.out.printf("Enviando un correo electronico usando el servidor SMTP %s",
                env.getProperty("url.smtp"));
    }
}
