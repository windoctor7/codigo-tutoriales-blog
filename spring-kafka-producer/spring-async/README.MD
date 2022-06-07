## Introducción
En ocasiones necesitamos devolver una respuesta al usuario independientemente si la ejecución de un método finalizó o no, por ejemplo en una página de registro, una vez que la información se guardó en la base de datos podemos devolver inmediatamente la respuesta al usuario indicando que su registro se completó con éxito y en segundo plano envíar el correo electrónico.

___

## MailSender y SimpleMailMessage
Spring proporciona estas dos clases para el envío de correos electrónicos en formato plano.

[``MailSender``](http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/mail/MailSender.html). Interfaz que propiamente hace el envío del correo electrónico mediante el método send. Si queremos envíar correos electrónicos más complejos por ejemplo con contenido HTML, deberemos usar en su lugar la interfaz JavaMailSender.

[``SimpleMailMessage``](http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/mail/SimpleMailMessage.html). Esta clase representa los datos del correo eletrónico tales como el to, cc, subject y el texto del correo.

Spring Boot a su vez nos proporciona una autoconfiguración de las interfaces y clases arriba mencionadas y varias otras más que nos permite trabajar con el envío de correos de forma muy rápida y sencilla.

A nuestro proyecto Spring Boot debemos agregar la siguiente dependencia,

    compile group: 'org.springframework.boot', name: 'spring-boot-starter-mail', version: '1.5.2.RELEASE'

---

## Habilitando el soporte para tareas asíncronas
Un método anotado con [``@Async``](http://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/scheduling/annotation/Async.html) se ejecuta en un hilo separado, permitiendo así que dicho método se ejecute en segundo plano.

Para habilitar el soporte de tareas asíncronas necesitamos agregar la anotación [``@EnableAsync``](http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/scheduling/annotation/EnableAsync.html) a la clase principal de Spring Boot.

```java
    @SpringBootApplication
    @EnableAsync
    public class SpringAsyncApplication
```

Ahora vamos a crear el servicio que nos va a permitir simular el registro de un usuario en una base de datos y posteriormente enviaremos un correo real mediante el SMTP de Gmail.

El servicio quedaría de la siguiente forma,

```java
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
            mailMessage.setFrom("aqui el correo electronico del emisor");
            mailMessage.setTo(usuario.getEmail());
            mailMessage.setSubject("Registro completado");
            mailMessage.setText("Su registro fue completado con exito");

            mailSender.send(mailMessage);
        }
    }
}
```

Podemos ver que el método _enviarCorreo_ está marcado con la anotación [``@Async``](http://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/scheduling/annotation/Async.html), esto le permitirá ejecutarse de manera asíncrona. Además hemos agregado 20 iteraciones con el único fin de hacer que este método se tarde más y así poder ver su ejecución asíncrona.

Ahora debemos configurar los datos del servidor de correos. Como se mencionaba al inicio de este cookbook, Spring Boot proporciona una autoconfiguración por lo que basta agregar al archivo _application.properties_ los parámetros del servidor SMTP.

```properties
# El servidor SMTP de gmail. El puerto 465 es el puerto seguro de gmail.
spring.mail.host=smtp.gmail.com
spring.mail.port= 465

# Indicamos que se habilite el soporte SSL
spring.mail.properties.mail.smtp.ssl.enable = true

# aqui pon tu direccion de correo electronico de gmail
spring.mail.username= molder.itp@gmail.com

# aqui pon tu contraseña de tu cuenta de correo gmail
spring.mail.password= F0h731183virg@
```

___

## Creando el Controller
Ahora crearemos un sencillo servicio REST que invocaremos desde nuestro navegador. 

```java
@RestController
@RequestMapping("/registro")
public class RegistroController {

    @Autowired
    RegistroAsync registroAsync;

    @RequestMapping(method = RequestMethod.GET, value = "/usuario")
    public String registrar(){

        Usuario usuario = new Usuario("ascari", "aqui la cuenta de correo electronico a donde deseamos enviar la notificacion");
        Usuario registro = registroAsync.registrar(usuario);
        System.out.println("ahora enviamos el correo");
        registroAsync.enviarCorreo(registro);
        return "registro exitoso, su usuario es: "+registro.getUser();
    }
}

```

Debemos agregar también una dependencia al _build.gradle_

        compile group: 'org.springframework.boot', name: 'spring-boot-starter-web', version: '1.5.2.RELEASE'

    
Podemos observar que el método http usado es GET. Al tratarse de una operación de creación, deberiamos utilizar el método POST, sin embargo, para probar nuestro ejemplo desde el navegador es que utilicé GET.

___

## Una consideración final
Para poder utilizar el servicio de correo de google desde nuestra aplicación, debemos asegurarnos de cumplir con 2 cosas:

1. Debes tener deshabilitada la autenticación en 2 pasos. Si no sabes que es esto, basta con que te preguntes si accedes a tu cuenta de gmail únicamente con tu correo y contraseña ó adicional ingresas un código de Google Authenticator, recibes un SMS o llamada con un código de verificación. Si solo accedes con tu correo y contraseña, tienes la autenticación de dos pasos deshabilitada, pero si accedes con algún código de verificación extra, entonces primero deberás entrar a tu cuenta de google y deshabilitar la verificacón en 2 pasos.

1. Debes permitir el acceso a aplicaciones menos seguras. Esto lo haces ingresando [aquí](https://myaccount.google.com/lesssecureapps) 

## Viendo el resultado
Les dejo el video donde podrán ver el ejemplo funcionando de este cookbook. 

<iframe width="560" height="315" src="https://www.youtube.com/embed/0pkARIDeIXE?ecver=1" frameborder="0" allowfullscreen></iframe>
