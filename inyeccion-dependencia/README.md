Aunque muchos lo oyen, no todos lo entienden o aplican. El concepto de Inyección de la Dependencia gira en torno a dos cosas:

<ol>
  <li>Uso de Interfaces para desacoplar código</li>
  <li>Dejar de crear objetos concretos mediante "new"</li>
</ol>

## Código con bajo acoplamiento
El uso de interfaces nos ayuda a tener un código desacoplado. Pero curiosamente son varios los desarrolladores que usan interfaces de forma mecánica sin entender realmente sus ventajas.

Vamos a suponer que estamos en un área de cobranza donde nos han pedido el siguiente requerimiento:

<cite>Quiero que les envíes un recordatorio mediante SMS a todos los clientes cuyo día de pago sea hoy y su abono a realizar sea mayor a $300</cite>

Procedemos a crear la clase Cliente

```java
import java.util.Calendar;

public class Cliente {

    private int diaPago;
    private double abono;
    private String phoneNumber;

    public Cliente(int diaPago, double abono, String phoneNumber) {
        this.diaPago = diaPago;
        this.abono = abono;
        this.phoneNumber = phoneNumber;
    }

    public void enviarRecordatorio(){
        Calendar calendar = Calendar.getInstance();
        int dia = calendar.get(Calendar.DAY_OF_WEEK);

        if(dia == diaPago && abono >= 300){
            new SMS().sendMessage(phoneNumber,"Favor de pasar a pagar");
        }
    }
}
```

Con el código anterior, lo primero que sale a relucir es que nuestra clase Cliente solo puede envíar SMS, está casada con una clase concreta. ¿Qué pasa si después nos piden envíar correos electrónicos o incluso mensajes vía Twitter?

Otra de las desventajas de tener clases concretas es que dificulta la escritura de pruebas unitarias. Una prueba unitaria verifica el correcto funcionamiento de un pedazo de código (normalmente un método), es decir, verifica que un método hace lo que tiene que hacer.

Con unos pocos cambios podemos hacer que nuestra clase <code>Cliente</code> ya no dependa de una clase concreta y dejarla preparada para que pueda envíar cualquiera de los 3 tipos de mensajes, SMS, Email y Twitter.

```java
public class Cliente {

    private long id;
    private int diaPago;
    private double abono;

    private String celular;
    private String twitter;
    private String email;

    private MedioContacto medioContacto;

    private IMensaje mensaje;

    public void enviarRecordatorio() {
        Calendar calendar = Calendar.getInstance();
        int dia = calendar.get(Calendar.DAY_OF_WEEK);

        if (dia == diaPago && abono >= 300) {
            if(medioContacto == MedioContacto.EMAIL)
                mensaje.enviar(email, "Favor de pasar a pagar");
            else if (medioContacto == MedioContacto.SMS)
                mensaje.enviar(celular, "Favor de pasar a pagar");
            else if(medioContacto == MedioContacto.TWITTER)
                mensaje.enviar(twitter, "Favor de pasar a pagar");
        }
    }
}
```

```java
public interface IMensaje {
    void enviar(String destinatario, String mensaje);
}
```

```java
public class MensajeTwitter implements IMensaje {

    @Override
    public void enviar(String destinatario, String mensaje) {
        System.out.printf("Enviando mensaje via Twitter a %s \n Mensaje: %s", destinatario, mensaje);
    }

}
```


Nuestro código principal para probar esto quedaría como sigue:

```java
public class Main {
    public static void main(String[] args) {
        Cliente cliente = new Cliente();
        cliente.setAbono(320);
        cliente.setDiaPago(2); //1 Domingo, 2 Lunes, 3 Martes, etc.
        cliente.setMedioContacto(MedioContacto.TWITTER);
        cliente.setEmail("molder.itp@gmail.com");
        cliente.setTwitter("@windoctor");
        cliente.setCelular("0123456789");

        //inyectamos el objeto especifico
        cliente.setMensaje(new MensajeTwitter());

        cliente.enviarRecordatorio();
    }
}
```

En la consola veríamos la siguiente salida:

<code>
Enviando mensaje via Twitter a @windoctor <p>
 Mensaje: Favor de pasar a pagar
</code>

Hasta este punto podemos tener una mayor idea de lo que es la inyección de la dependencia.

<ol>
	<li>Dejamos de crear objetos con new en la clase <code>Cliente</code>. En su lugar, delegamos la creación de objetos a un agente externo (nuestra clase Main).
</li>
	<li>Desacoplamos nuestro código al ya no depender de una clase en específico, en su lugar usamos interfaces.</li>
</ol>

En el ejemplo anterior, nuestra clase <code>Main</code> es la encargada de proveer a <code>Cliente</code> las dependencias que ésta necesita para lograr su cometido, en este caso, <code>Main</code> le inyectó a <code>Cliente</code> un objeto <code>MensajeTwitter</code>

## Inyección mediante Spring Framework
Spring tiene un contenedor de dependencias que se encarga de inyectar los objetos que nuestras clases requieran. Para usar Spring será tan sencillo agregar la dependencia en Gradle,

<code>
compile group: 'org.springframework', name: 'spring-context', version: '4.1.9.RELEASE'
</code>

Ahora modificaremos un poco nuestra clase Main para que luzca de la siguiente forma:

```java
public class Main {

    public static void main(String[] args) {

        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(ApplicationConfig.class);
        ctx.refresh();

        Cliente cliente = (Cliente) ctx.getBean("cliente");

        cliente.setAbono(320);
        cliente.setDiaPago(2); //1 Domingo, 2 Lunes, 3 Martes, etc.
        cliente.setMedioContacto(MedioContacto.TWITTER);
        cliente.setEmail("molder.itp@gmail.com");
        cliente.setTwitter("@windoctor");
        cliente.setCelular("0123456789");

        cliente.enviarRecordatorio();
    }
}
```

Del código anterior nos percatamos que además de tener nuevas líneas de código, eliminamos la línea donde inyectabamos un objeto de tipo <code>MensajeTwitter</code>.

A nuestra clase cliente le agregamos dos anotaciones, <code>@Component</code> y <code>@Autowired</code>

```java
@Component
public class Cliente {

    private long id;
    private int diaPago;
    private double abono;

    private String celular;
    private String twitter;
    private String email;

    private MedioContacto medioContacto;

    @Autowired
    private IMensaje mensaje;
    ...
    ...
```

Si en nuestra clase <code>Cliente</code> tenemos un atributo del tipo <code>IMensaje</code> 

<ol>
	<li>@Component:  Nuestras clases que queramos sean inyectadas por el contenedor de Spring deberán tener esta anotación.</li>
	<li>@Autowired: Con esta anotación le indicamos a Spring que inyecte por nosotros el objeto.</li>
</ol>

Con lo anterior le estamos diciendo a Spring que le inyecte a la clase <code>Cliente</code> una implementación de <code>IMensaje</code>. Puesto que tenemos una sola implementación de <code>IMensaje</code> , Spring es capaz de inferir que la única clase candidata para inyectarse es <code>MensajeTwitter</code>

Dado que queremos que <code>MensajeTwitter</code> sea inyectada por Spring, debemos marcarla con la anotación @Component

```java
@Component
public class MensajeTwitter implements IMensaje {

    @Override
    public void enviar(String destinatario, String mensaje) {
        System.out.printf("Enviando mensaje via Twitter a %s \n Mensaje: %s", destinatario, mensaje);
    }

}
```

Si ejecutamos nuestra clase <code>Main</code> obtendremos el mismo resultado que nuestro primer ejemplo donde no usamos Spring, solo que ahora fue Spring quien le inyectó a <code>Cliente</code> el objeto <code>MensajeTwitter</code>.

Vamos ahora a simular el envío de email y sms, para ello vamos a crear las correspondientes implementaciones.

```java
@Component
public class MensajeEmail implements IMensaje {
    @Override
    public void enviar(String destinatario, String mensaje) {
        System.out.printf("Enviando mensaje por correo electronico a %s \n Mensaje: %s", destinatario, mensaje);
    }
}
```

```java
@Component
public class MensajeSms implements IMensaje {
    @Override
    public void enviar(String destinatario, String mensaje) {
        System.out.printf("Enviando mensaje SMS a %s \n Mensaje: %s", destinatario, mensaje);
    }
}
```

Si a nuestro cliente ahora le cambiamos su medio de contacto por email,

```java
public class Main {
    public static void main(String[] args) {
    .....
    cliente.setMedioContacto(MedioContacto.EMAIL);
    .....
   }
}
```
	
Al ejecutar nuestro ejemplo obtendremos una fea excepción por parte de Spring.

<code>
Caused by: org.springframework.beans.factory.NoUniqueBeanDefinitionException: No qualifying bean of type [basic.IMensaje] is defined: expected single matching bean but found 3: mensajeEmail,mensajeSms,mensajeTwitter
</code>

Spring nos está diciendo que no sabe que tipo de objeto inyectar puesto que existen 3 implementaciones de <code>IMensaje</code>. Spring infiere el tipo de objeto a inyectar cuando solo existe una sola implementación, pero cuando existe más de una, Spring no es capaz de leer nuestros pensamientos razón por la cuál debemos indicarle a Spring el tipo de objeto en específico que deberá inyectar.

```java
	@Autowired
    @Qualifier("email")
    private IMensaje mensaje;
```

Con la anotación <code>@Qualifier</code> le indicamos el nombre del objeto concreto que deseamos inyectar. Este nombre debe corresponder con el que definimos en la anotación @Component.

```java
@Component(value = "email")
public class MensajeEmail implements IMensaje {
    @Override
    public void enviar(String destinatario, String mensaje) {
        System.out.printf("Enviando mensaje por correo electronico a %s \n Mensaje: %s", destinatario, mensaje);
    }
}
```

Nuestro cliente puede llegar y cambiar las reglas del envío de recordatorios y hacerlas un poco más comlejas:


- Si el abono del cliente está entre $200 y $300 y  le faltan

	- de 3 a 7 días para su día de pago, entonces envíale un Tweet.

	- de 1 a 2 días para su día de pago, entonces envíale un Correo.

	- 0 días para su día de pago, entonces envíale un SMS.

Podemos codificar estas reglas en el método <code>enviarRecordatorio()</code> de nuestra clase <code>Cliente</code> pero estas mismas reglas pueden hacerse más complejas con el paso del tiempo. Una posible solución más elegante es utilizar el patrón de diseño Chain of Responsibility.

## Patrón de diseño Chain of Responsibility
Este patrón se puede usar cuando queremos tener una cadena de responsabilidades, es decir, si tenemos una acción "X" a ejecutar se la pasamos a un objeto y este mismo objeto validará si es capaz de ejecutar la acción, en caso que no pueda ejecutarla la delegará a otro objeto. Éste último objeto volverá a verificar si es capaz de ejecutar la acción y en caso negativo, delegará la tarea a un tercer objeto y así sucesivamente.

En nuestro ejemplo, tenemos 3 objetos, Twitter, Email y SMS y cada objeto puede ejecutar el envío del recordatorio siempre y cuando cumpla con las reglas establecidas, en caso contrario, delegará la tarea al siguiente objeto.

Para codificar este patrón de diseño necesitamos de una interfaz ó clase abstracta, usualmente es una interfaz, pero como nosotros vamos a tener un pequeño código que será común a mis 3 objetos entonces usaremos una clase abstracta.

```java
public abstract class Recordatorio {

    Recordatorio recordatorio;

// En este método establecemos quien será el siguiente objeto en
nuestra cadena de responsabilidades en validar si le es posible
ejecutar la tarea en caso que el objeto actual no pueda resolverlo.
    public void nextHandler(Recordatorio recordatorio){
        this.recordatorio = recordatorio;
    }

// Obtenemos el día se la semana actual.
    public int getDay(){
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_WEEK);
    }
    
    public abstract void enviar(Cliente cliente);
}
```

Ahora creamos las implementaciones:

```java
@Component
public class RecordatorioEmail extends Recordatorio{

    @Autowired
    @Qualifier("correo")
    IMensaje email;

    @Override
    public void enviar(Cliente cliente) {
        int dias = cliente.getDiaPago() - getDay();

        if( dias >= 1 && dias <= 2 )
            email.enviar(cliente.getEmail(), "Mensaje por correo electronico");
        else
            recordatorio.enviar(cliente);
    }
}
```

```java
@Component
public class RecordatorioSms extends Recordatorio {

    @Autowired
    @Qualifier("mensaje")
    IMensaje sms;

    @Override
    public void enviar(Cliente cliente) {
        int dias = cliente.getDiaPago() - getDay();
        if(dias == 0)
            sms.enviar(cliente.getCelular(), "Mensaje enviado por SMS");
        else
            recordatorio.enviar(cliente);
    }
}

```

```java
@Component
public class RecordatorioTwitter extends Recordatorio {

    @Autowired
    @Qualifier("tweet")
    IMensaje tweet;

    @Override
    public void enviar(Cliente cliente) {
        int dias = cliente.getDiaPago() - getDay();
        if(dias>=3 && dias<= 7)
            tweet.enviar(cliente.getTwitter(), "Mensaje enviado por Twitter");
    }
}
```

En nuestras 3 nuevas clases están codificadas las reglas para el envio de recordatorios. Nuevamente las vuelvo a poner:

- Si el abono del cliente está entre $200 y $300 y  le faltan

	- de 3 a 7 días para su día de pago, entonces envíale un Tweet.

	- de 1 a 2 días para su día de pago, entonces envíale un Correo.

	- 0 días para su día de pago, entonces envíale un SMS.

Finalmente en nuestra clase <code>Cliente</code> tendremos:

```java
    @Autowired
    private Recordatorio recordatorioEmail;

    @Autowired
    private Recordatorio recordatorioSms;

    @Autowired
    private Recordatorio recordatorioTwitter;

    public void enviarRecordatorio() {
        if (abono >= 200 && abono <= 300) {
            recordatorioEmail.nextHandler(recordatorioSms);
            recordatorioSms.nextHandler(recordatorioTwitter);

            recordatorioEmail.enviar(this);
        }
    }
```

Debemos tener un punto de partida, en este caso es el Email. En caso que el Email no pueda ser manejado, el siguiente será el SMS. En caso que el SMS no pueda ser manejado, será el Twitter.

El código completo lo pueden encontrar en el enlace de abajo.

Bastará con ejecutar la clase Main y jugar con el día de pago del cliente para ver como se ejecutan los diferentes tipos de mensajes.