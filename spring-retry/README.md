
## Introducción
En este cookbook veremos como crear una política de reintentos sencilla que permita a nuestra aplicación volver a ejecutar un servicio web REST cuando el servidor está caído. Para lograrlo usaremos el proyecto [spring-retry](https://github.com/spring-projects/spring-retry).

En este cookbook crearemos 2 proyectos:

1. **ws-rest**. Este será un sencilo servicio REST
1. **cliente-ws**. Este será el cliente que consumirá el servicio REST.


___

## Creando el servicio web
Vamos a crear un sencillo servicio web tipo REST que solo devuelva un mensaje de texto, aunque si lo deseas puedes utilizar el desarrollado en el cookbook [Tareas asíncronas con spring](https://windoctor7.github.io/Tareas-asincronas-Spring.html) cuyo código fuente puedes obtener desde [github](https://github.com/windoctor7/codigo-tutoriales-blog/tree/master/spring-async) y  correrlo muy fácilmente.

En Linux o MAC OS

    ./gradlew bootRun 
    

O en Windows

    gradlew.bat bootRun
    
Sin embargo para este cookbook crearemos un servicio nuevo y será muy sencillo,

```java
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
```

Como podemos ver se trata de un servicio GET que devuelve la información en formato JSON de un empleado determinado.

La clase ``Empleado``consta solo de dos atributos.

```java
public class Empleado {

    public int id;
    public String nombre;

    public Empleado() {
    }

    public Empleado(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return "Empleado{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                '}';
    }
}

```

## Consumiendo el servicio web

Vamos a necesitar crear otro proyecto spring boot para el cliente que va a consumir el servicio web. Este proyecto deberá tener las dependencias de ``spring-retry`` y ``spring-boot-starter-aop``

	compile group: 'org.springframework.boot', name: 'spring-boot-starter-web', version: '1.5.2.RELEASE'
    compile group: 'org.springframework.boot', name: 'spring-boot-starter-aop', version: '1.5.2.RELEASE'
	compile group: 'org.springframework.retry', name: 'spring-retry', version: '1.2.0.RELEASE'


Creamos una clase que implemente la interfaz [``CommandLineRunner``](http://nixmash.com/post/using-spring-boot-commandlinerunner) 

```java
@Service
public class ClienteRest implements CommandLineRunner {

    //Si falla, se realizarán 5 intentos con 5 segundos de espera entre cada uno de ellos
    @Retryable(backoff = @Backoff(5000), maxAttempts = 5)
    public void run(String... args) throws Exception {
        System.out.println("Iniciando el llamado al WS - "+ System.currentTimeMillis() / 1000);
        String url = "http://localhost:8080/empleados/{id}";
        int id = 1;

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Empleado> entity = restTemplate.getForEntity(url, Empleado.class, id);
        Empleado empleado = entity.getBody();
        System.out.println(empleado);
    }
}
```

Si no sabes que es CommandLineRunner no te preocupes, no tiene nada que ver con este ejemplo. CommandLineRunner nos permite ejecutar el código automáticamente al correr la aplicación.

Lo que si nos importa aquí es observar la anotación [@Retryable](http://docs.spring.io/spring-retry/docs/1.1.2.RELEASE/apidocs/org/springframework/retry/annotation/Retryable.html). Esta anotación permite que si el método lanza una excepción entonces vuelva a reintentar su ejecución. Por default, @Retryable realiza un máximo de 3 intentos en intervalos de 1 segundo. Esta anotación tiene algunos atributos que vale la pena mencionar,


``maxAttempts`` . El número de intentos máximos que intentará ejecutar el método. Por default 3 intentos.

``backoff`` . El intervalo de espera entre cada intento. Por default 1 segundo entre cada intento.

``include`` . La(s) clase(s) de excepción que si son lanzadas deseamos que el método vuelva a reintentar su ejecución.

En el código anterior también podemos notar el uso de la clase [RestTemplate](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/client/RestTemplate.html) de Spring que nos permite invocar servicios REST de forma rápida y fácil. El método ``getForEntity``permite invocar servicios GET. En este ejemplo hemos utilizado la siguiente el siguiente, 


``getForEntity(String url, Class<T> responseType, Object... uriVariables)``


donde

    url             es propiamente la URL de nuestro servicio
    responseType    la clase del objeto a donde se mapeara el JSON devuelto por el servicio
    uriVariables    las variables que en la url definimos entre llaves {}, en este caso el id del empleado


Notamos que también usamos la clase Empleado, por lo que en este proyecto también debemos colocar esta clase que creamos en el proyecto del servicio.

## Habilitando el soporte para @Retryable
En nuestra clase ClienteRest que definimos arriba, hemos usado la anotación @Retryable. Para que Spring la pueda tomar en cuenta, debemos indicarlo agregando @EnableRetry en la clase de configuración principal de Spring Boot.

```java
@SpringBootApplication
@EnableRetry
public class SpringRetryTestApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(SpringRetryTestApplication.class, args);
	}

}
```

## Ejecutando el ejemplo
Ya tenemos todo para ver el funcionamiento y lo primero es ejecutar el proyecto del servicio web REST .

Posteriormente debemos ejecutar el segundo proyecto que consumirá el servicio REST. Dado que por default las aplicaciones spring boot corren sobre el puerto 8080 y ya tenemos a nuestro web service corriendo en ese puerto, vamos a cambiar el puerto por el que correrá nuestro cliente, para ello basta agrega la siguiente línea al _application.properties_ del proyecto cliente,

    server.port=8001
    
Ahora si procedemos a ejecutarlo con bootRun.

En este punto tenemos los siguientes proyectos ejecutándose.


1. **_ws-rest_**. Es el servicio web y está corriendo sobre el puerto 8080.
1. **_cliente-ws_**. Es el cliente que consumirá al servicio REST y está corriendo sobre el puerto 8081.


Al ejecutar el _cliente-ws_ nada raro pasará, todo correrá con normalidad y en la consola veremos lo siguiente:

    Iniciando el llamado al WS - 1491875972
    Empleado{id=1, nombre='David Perez'}

Vamos a detener ámbos proyectos y volveremos a ejecutar únicamente el _cliente-ws_

En la consola veremos lo siguiente,

    Iniciando el llamado al WS - 1491876414
    Iniciando el llamado al WS - 1491876419
    Iniciando el llamado al WS - 1491876424
    Iniciando el llamado al WS - 1491876429
    Iniciando el llamado al WS - 1491876434
    org.springframework.web.client.ResourceAccessException: I/O error on GET request for "http://localhost:8080/empleados/1": Connection refused; nested exception is java.net.ConnectException: Connection refused
    
Dado que el servicio web no está corriendo, al invocarlo se obtiene una excepción, y según nuestra política definida, se reintentará un máximo de 5 veces. Si después de los 5 intentos no se tuvo éxito entonces se lanzará la excepción correspondiente.

## Consideración final
Podemos definir una política de recuperación, es decir, si después de los reintentos sigue siendo imposible ejecutar el servicio. Basta con definir un método y anotarlo con ``@Recover```

```java
    @Recover
    public void recover(){
        System.out.println("Aqui el código que deseamos ejecutar en caso que la política de reintentos falle");
    }
```

Al método anotado con recover podemos agregar como parámetro la excepción que causó los reintentos y si nuestro método de reintento tiene parámetros también podemos agregarlos al método de recuperacion. El siguiente fragmento de la página de github del proyecto [spring-retry](https://github.com/spring-projects/spring-retry) es muy clarificador.

```java
@Service
class Service {
    @Retryable(RemoteAccessException.class)
    public void service(String str1, String str2) {
        // ... do something
    }
    @Recover
    public void recover(RemoteAccessException e, String str1, String str2) {
       // ... Manejo de errores, haciendo uso de los parametros originales si son necesarios.
    }
}
```

Lamentablemente nosotros no podemos ver esto pues al utilizar la interfaz CommandLineRunner, si en el método run(String… args) se lanza una excepción, causará que el [contexto de spring se cierre](http://howtodoinjava.com/spring/spring-boot/command-line-runner-interface-example/) y nuestra aplicación se detendrá, ocasionando que el método recover no se ejecute. Para que una aplicación que usa CommandLineRunner no se termine en el caso de lanzar una excepción en el método run(..) será necesario agregar un bloque try/catch sin embargo si lo hacemos la excepción no se lanzará un nuestra política de reintentos no se ejecutará sencillamente porque no habrá detectado ninguna excepción. Sin embargo esto no es ningun problema, en una aplicación real no usarás CommandLineRunner, aquí se uso solo para fines demostrativos y rápidos.

En definitiva es recomendable darse una vuelta por la documentación oficial para un mayor detalle del uso.

[Documentación oficial](https://github.com/spring-projects/spring-retry)

## Video demostrativo

<iframe width="560" height="315" src="https://www.youtube.com/embed/aTZIM1cI-DQ?rel=0?ecver=1" frameborder="0" allowfullscreen></iframe>