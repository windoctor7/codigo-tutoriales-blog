package windoctor7.github.io.spring.async;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Creado por  Ascari Q. Romo Pedraza - molder.itp@gmail.com on 09/04/17.
 */
@Service
public class RegistroAsync {

    private MailSender mailSender;

    @Autowired
    public RegistroAsync(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    // Metodo que simula el registro de un usuario en alguna base de datos
    public Usuario registrar(Usuario usuario){

        System.out.printf("Registrando a nuevo usuario: %s", usuario);

        /**
         * Aqui el código requerido para guardar los datos en la base de datos
         */

        usuario.setUser("windoctor");

        System.out.println("Finaliza el registro del usuario.");

        return usuario;
    }

    @Async
    public void enviarCorreo(Usuario usuario){
        for(int i = 1; i < 21; i++) {
            System.out.println("enviando correo "+i);
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(""); //pon tu cuenta de correo gmail u otro texto que sirva como remitente
            mailMessage.setTo(usuario.getEmail());
            mailMessage.setSubject("Registro completado");
            mailMessage.setText("Su registro fue completado con exito");

            mailSender.send(mailMessage);
        }
    }
}
