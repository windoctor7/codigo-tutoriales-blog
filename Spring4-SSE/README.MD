
## Introducción a Server-Sent Event

[Server-Sent Event](https://developer.mozilla.org/es/docs/Server-sent_events/utilizando_server_sent_events_sse) (SSE) es un API de HTML que permite una comunicación unidireccional con el servidor. Anteriormente usabamos técnicas Ajax como [long-polling](https://es.wikipedia.org/wiki/Tecnología_push#Long_polling) para que cada determinados segundos fuera y preguntara al servidor si tenia nuevos datos. Server-Sent Event permite establecer una conexión al servidor y en lugar que el cliente pregunte al servidor cada determinado tiempo si hay datos nuevos, es el servidor mismo quien le informa al cliente el momento en el que existen datos nuevos para actualizar.

El siguiente diagrama muestra la técnica de Long polling 

![long polling](https://github.com/windoctor7/windoctor7.github.io/raw/master/static/img/long-polling.png)

Hoy en día tenemos mejores técnicas como Websockets o Server-Sent Events. 

En este tutorial aprenderemos el uso de SSE con Spring 4. Un diagrama que muestra el flujo de comunicación con SSE es el siguiente: 

![sse](https://github.com/windoctor7/windoctor7.github.io/raw/master/static/img/sse.png)

La diferencia de [Long polling vs SSE](https://stackoverflow.com/questions/9397528/server-sent-events-vs-polling), es que este último establece una conexión con el servidor y es el servidor quien notifica al cliente cada que existe un nuevo evento. La comunicación es en una sola dirección, es decir, una vez que el cliente establece una conexión al servidor, ya no es posible enviar más peticiones al servidor usando la misma conexión.

---

## Server-Sent en Spring
La nueva versión de Spring 5 trae un mejor y más sencillo soporte para SSE, pero desde Spring 4.2 tenemos soporte para trabajar con la clase [SseEmitter](https://docs.spring.io/spring/docs/4.2.0.RC2_to_4.2.0.RC3/Spring%20Framework%204.2.0.RC3/org/springframework/web/servlet/mvc/method/annotation/SseEmitter.html) la cual es la que ocuparemos en este tutorial.

Entre los métodos más importantes de la clase SseEmitter, podemos encontrar los siguientes:

{% highlight java %}
// Permite enviar un mensaje al cliente, normalmente el navegador.
send(java.lang.Object object) 

// Permite registrar el código que se ejecutará una vez que el proceso asíncrono concluya.
onCompletion(java.lang.Runnable callback) 

// Se debe llamar cuando el proceso haya concluido.
complete() 

// Permite registrar un error cuando el proceso haya lanzado una excepción.
completeWithError(java.lang.Throwable ex) 
{% endhighlight %}


Un sencillo ejemplo sería el siguiente controller:

{% highlight java %}
@RestController
public class SseController {
    @GetMapping("/sse")
    public SseEmitter basico() throws IOException {
        SseEmitter emitter = new SseEmitter();
        emitter.send(new Mensaje(1,"Hola Server-Sent Events"));
        return emitter;
    }
}
{% endhighlight %}

Si ejecutamos el proyecto con bootRun y desde el navegador ingresamos a [http://localhost:8080/sse](http://localhost:8080/sse) podemos ver la siguiente salida:

    data:{"cve":1,"msg":"Hola Server-Sent Events"}
    
---

## Un ejemplo más complejo
Hace poco en el trabajo tuve la necesidad de crear un proceso nocturno que permitiera exportar 18 millones de registros desde una tabla Oracle  a un archivo de texto. Por la cantidad de registros, debía ser un proceso nocturno que se ejecutara automáticamente, esto fue realmente muy sencillo hacerlo con la anotación ``@Scheduled``. Puedes leer un tutorial que escribí sobre [Spring Scheduled](https://windoctor7.github.io/Tareas-con-Spring-Scheduler.html).

Sin embargo, debía crear una sencilla página que permitiera lanzar este y algunos otros procesos de forma manual. El proceso que exporta a un archivo de texto los 18 millones de registros tarda aproximadamente 15 minutos y la idea era notificar a la página web cuando el proceso hubiera finalizado. De aquí partió la necesidad de usar Server-Sent Event.

Primero creamos la clase que se va a encargar de crear el archivo de texto. Esta misma clase será la que esté anotada con ``@Scheduled`` y se ejecute automáticamente.

{% highlight java %}
@Component
public class GenerarArchivo {

    // se ejecuta cada 15 segundos
    @Scheduled(cron = "0/15 * * ? * *")
    public void generar() throws IOException {
        System.out.println("Iniciando escritura de archivo");
        File file = new File("/Users/ascariromopedraza/archivo.txt");
        for(int i = 0; i < 100000; i++){
            String cad = "linea "+i;
            FileUtils.writeStringToFile(file, cad, "UTF-8",true);
        }
        System.out.println("Finalizando escritura de archivo");
    }

}
{% endhighlight %}

Para escribir en el archivo de texto, usamos [commons io](https://commons.apache.org/proper/commons-io/), así que se agrega la dependencia al build.gradle

	compile group: 'commons-io', name: 'commons-io', version: '2.5'


Para habilitar la ejecución de Scheduled, debemos agregar la anotación ``@EnableScheduling``

{% highlight java %}
@SpringBootApplication
@EnableScheduling
public class Spring4SseApplication {

	public static void main(String[] args) {
		SpringApplication.run(Spring4SseApplication.class, args);
	}
}
{% endhighlight %}

Al correr el proyecto con la tarea bootRun, podemos ver que nuestra tarea inicia su ejecución algunos segundos después y genera un archivo de texto en la ruta indicada.

Sin embargo, como mencioné, la intensión es tener la posibilidad de ejecutar este proceso de forma manual, por lo que debemos crear un controlador REST que podamos invocar desde una página web.

{% highlight java %}
@RestController
public class SseController {

    @Autowired
    private GenerarArchivo task;

    @GetMapping("/execute")
    public SseEmitter ejecutar() throws IOException {
        SseEmitter emitter = new SseEmitter();
        task.generar();
        emitter.send(new Mensaje(1,"Ejecutanto tarea"));
        return emitter;
    }

}
{% endhighlight %}

Finalmente para ejecutar la tarea desde el navegador, vamos a cambiar la calendarización de nuestro job para que se ejecute cada 5 minutos y no cada 15 segundos como lo teniamos,

    @Scheduled(cron = "0/15 * * ? * *")

Si ejecutamos nuevamente el proyecto con bootRun y accedemos desde el navegador a [http://localhost:8080/execute](http://localhost:8080/execute) nos daremos cuenta que la tarea de escritura del archivo de texto no es asíncrona, es decir, hasta que el método ``task.generar()`` concluya será cuando recibiremos una respuesta.

Para solucionar el problema anterior, deberemos agregar la anotación ``@Async`` a nuestro método

{% highlight java %}
@Component
public class GenerarArchivo {

    // se ejecuta cada 5 minutos
    @Scheduled(cron = "0 5 * ? * *")
    @Async
    public void generar() throws IOException {
        System.out.println("Iniciando escritura de archivo");
        File file = new File("/Users/ascariromopedraza/archivo.txt");
        for(int i = 0; i < 100000; i++){
            String cad = "linea "+i;
            FileUtils.writeStringToFile(file, cad, "UTF-8",true);
        }
        System.out.println("Finalizando escritura de archivo");
    }
}
{% endhighlight %}

Finalmente agregar la anotación ``@EnableAsync``

{% highlight java %}
@SpringBootApplication
@EnableScheduling
@EnableAsync
public class Spring4SseApplication {

	public static void main(String[] args) {
		SpringApplication.run(Spring4SseApplication.class, args);
	}
}
{% endhighlight %}

Esto permitirá ejecutar al método ``generar()`` en un hilo separado. Puedes ver un tutorial sobre procesos asíncronos en este otro [tutorial que escribí](https://windoctor7.github.io/Tareas-asincronas-Spring.html). 

Si corremos de nueva cuenta el proyecto y accedemos desde el navegador al endpoint **/execute** vemos que la respuesta ahora es inmediata, sin embargo después de algunos segundos obtenremos un error de timeout en el log de la aplicación. Para establecer un timeout a los procesos asíncronos de nuestra aplicación, debemos agregar la siguiente clase,

{% highlight java %}
@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        configurer.setDefaultTimeout(1000000);
    }
}
{% endhighlight %}

Ya está!... Bueno, claramente el diseño del controller no es bueno, además tenemos el problema que solo podemos ejecutar un método en específico. Que pasaría si queremos diseñar un front con una lista de Jobs a ejecutar? ¿Tendríamos que crear un endpoint para cada job?... La solución es usar el [API reflection de Java](http://java-white-box.blogspot.mx/2016/02/api-reflect-que-es-reflexion-por-donde.html).

Vamos a crear una clase que se encargue de ejecutar cualquier método que tenga la anotación ``@Scheduled``

{% highlight java %}
@Component
public class JobExecutor implements IJobExecutor{

    @Autowired
    private ApplicationContext appContext;

    public final static Map<String,SseEmitter> EMITTERS = new ConcurrentHashMap<>();

    public void executeBean(String beanClass) {
        Object bean = null;
        SseEmitter emitter = JobExecutor.EMITTERS.get(beanClass);
        try {
        
            //Cargamos la clase
            Class<?> clazz = Class.forName(beanClass);
            bean = getExistingBean(clazz);
            
            //Si el Job no existe, mandamos un mensaje y completamos el emitter
            if (bean == null) {
                String msg = String.format("No existe ningún Job con el nombre: %s", bean);
                emitter.send(new Mensaje(3, msg));
                emitter.complete();
            }
            for (Method method : AopUtils.getTargetClass(bean).getMethods()) {
                if (method.isAnnotationPresent(Scheduled.class)) {
                    method.invoke(bean);
                    break;
                }
            }
        }
        catch (Exception e) {
            emitter.completeWithError(e);
        }
    }

    private Object getExistingBean(Class<?> beanClass){
        Object bean;
        bean = appContext.getBean(beanClass);

        return bean;

    }
}
{% endhighlight %}

``EMITTERS`` será un [ConcurrentHashMap](https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/ConcurrentHashMap.html) donde guardaremos objetos SseEmitter. Dado que nuestro Job escribe en un archivo de texto, es buena idea validar si el Map EMITTERS ya contiene el job ``GenerarArchivo`` y en caso de contenerlo devolver el mensaje.

El código del controller sufre modificaciones y ahora debería quedar más o menos como sigue:

{% highlight java %}
@RestController
public class SseController {

    @Autowired
    private GenerarArchivo task;

    @Autowired
    private IJobExecutor jobExecutor;

    @GetMapping("/execute")
    public SseEmitter ejecutar(@RequestParam String beanClass) throws IOException {

        SseEmitter emitter = new SseEmitter();

        // validamos que el Map NO contenga ya el Job. Si lo contiene, NO permitimos la ejecución del Job.
        if (isTaskExists(beanClass)) {
            String msg = String.format("El Job %s ya está siendo ejecutado. No es posible ejecutarlo en este momento.", beanClass);
            emitter.send(new Mensaje(2, msg));
            emitter.complete();
            return emitter;
        }

        config(emitter,beanClass);

        //Agregamos el emiter al Map y lo ejecutamos
        JobExecutor.EMITTERS.put(beanClass, emitter);
        jobExecutor.executeBean(beanClass);

        // Dado que la ejecución del Job ahora es asíncrona,
        //devolvemos inmediatamente la respuesta al cliente.
        emitter.send(new Mensaje(1,"Ejecutanto tarea..."));
        return emitter;
    }

    private boolean isTaskExists(String beanClass){
        return JobExecutor.EMITTERS.containsKey(beanClass);
    }

    // Establecemos que cuando el SseEmitter concluya, remueva dicho emitter del Map
    private void config(SseEmitter emitter, String beanClass){
        emitter.onCompletion(() -> {
            if (isTaskExists(beanClass))
                System.out.println("ELIMINANDO EMITTER");
                JobExecutor.EMITTERS.remove(beanClass);
            }
        );
    }

}
{% endhighlight %}

## Publicando el Evento
Estamos casi listos para probar nuestro ejemplo, sin embargo, falta algo muy importante y es notificar al navegador cuando la ejecución del método ``generar()`` concluya.

Para ello, utilizaremos el mecanismo de eventos de Spring. Basta con modificar la clase GenerarArchivo y agregar un publicador de eventos ``ApplicationEventPublisher``

{% highlight java %}
@Component
public class GenerarArchivo {
    
    @Autowired
    private ApplicationEventPublisher publisher;
    
    // se ejecuta cada 15 segundos
    @Scheduled(cron = "0 5 * ? * *")
    @Async
    public void generar() throws IOException {
        System.out.println("Iniciando escritura de archivo");
        File file = new File("/Users/ascariromopedraza/archivo.txt");
        for(int i = 0; i < 100000; i++){
            String cad = "linea "+i;
            FileUtils.writeStringToFile(file, cad, "UTF-8",true);
        }
        publisher.publishEvent(new MensajeEvent(this.getClass().getName()));
        System.out.println("Finalizando escritura de archivo");
    }
}
{% endhighlight %}

A partir de la versión 4.2 de Spring, la publicación de eventos resulta muy sencilla, basta con agregar a nuestra clase ``JobExecutor`` un método anotado con ``@EventListener``

{% highlight java %}
    @EventListener
    public void eventListener(MensajeEvent mensajeEvent) throws IOException {
        String bean = mensajeEvent.getBeanClass();
        SseEmitter emitter = JobExecutor.EMITTERS.get(bean);
        String msg = String.format("El Job %s ha finalizado correctamente", bean);
        emitter.send(new Mensaje(2,msg));
        emitter.complete();
    }
{% endhighlight %}

Con lo anterior, cuando el método ``generar()`` concluya publicaremos el evento con la línea 

``publisher.publishEvent(new MensajeEvent(this.getClass().getName()));``

El evento será notificado a los lísteners que Spring encuentre anotados con ``@EventListener`` y el objeto que reciba coincida con el enviado en el método publishEvent

Si quieres ver otro ejemplo del uso de Server-Sent Events usando Spring 5, puedes ver este otro [tutorial en mi blog](https://windoctor7.github.io/Spring-Web-Flux.html)


## Video demostrativo

<iframe width="560" height="315" src="https://www.youtube.com/embed/svfB9Y9O4Ps" frameborder="0" allowfullscreen></iframe>

