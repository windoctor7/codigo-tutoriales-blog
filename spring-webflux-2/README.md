Código del tutorial de mi blog http://https://windoctor7.github.io/Programacion-Reactiva-Spring5.html

---

Anteriormente escribí un sencillo [tutorial](https://windoctor7.github.io/Spring-Web-Flux.html) sobre el nuevo módulo de Spring 5, llamado [Spring Web Flux](https://docs.spring.io/spring-framework/docs/5.0.0.M5/spring-framework-reference/html/web-reactive.html) el cual nos permite hacer programación reactiva. Fue un tutorial sencillo y corto con un video demostrativo donde se aprecia un pequeño ejemplo.

En este tutorial, quiero ampliar más el concepto de programación reactiva y clarificar los conceptos con código fuente. Para ello es necesario crear un proyecto con Spring Boot 2 que al momento de escribir este tutorial se encuentra en la versión M5. Además es necesario contar con Java 8.

La creación del proyecto la puedes hacer desde [https://start.spring.io](https://start.spring.io). 


## ¿Qué es la Programación Reactiva?

La programación reactiva es un paradigma de programación orientado al flujo de datos (streams) y la propagación del cambio, todo de forma asíncrona. 

Esto quiere decir que la programación reactiva se sustenta en el patrón de diseño Observer, donde se tiene un Publisher y uno o más Suscribers que reciben notificaciones cuando el Publisher emite nuevos datos.

En la programación reactiva, el Publisher es el que se encarga de emitir el flujo de datos y propaga el cambio (notifica) a los Suscribers.

Por lo tanto, podemos decir que la programación reactiva se basa en 3 conceptos clave:

1. **Publisher**: También llamados Observables. Estos objetos son los que emiten el flujo de datos. 
1. **Suscriber**: También llamados Observers. Estos objetos son a los que se les notifican los cambios en el flujo de datos que emite el Publisher
1. **Schedulers**: Es el componente que administra la concurrencia. Se encarga de indicarle a los Publishers y Suscribers  en que thread deben ejecutarse.

En librerías como [RxJava](http://reactivex.io) o [Reactor](https://projectreactor.io) (esta última es la base de Spring Web Flux), son las mismas librerías quienes se encargan de crear un Pool de Hilos y manejarlos, de esta forma se oculta la complejidad del manejo de concurrencia.

---

## Spring 5 y el nuevo módulo Web Flux

La nueva versión de Spring 5 trae soporte para la programación reactiva mediante el nuevo módulo llamado Web Flux.

En Spring Web Flux, la clase Flux es la implementación de un Publisher. El Subscriber es cualquier objeto que necesite ser notificado y el Scheduler es algo que Spring maneja internamente facilitándonos más las cosas.

La clase Flux es un publicador de Streams. Un Stream es un flujo de datos. Podemos imaginar una tuberia por la cual fluyen los datos. 

Vamos a hacer un pequeño ejemplo para reforzar los conceptos aprendidos hasta este momento.

Publicaremos un rango de números del 1 al 30.

```java
Flux<Integer> flux = Flux.range(1,30)
```

Lo anterior es nuestro Publisher que emitira un flujo de números. Podemos ver a este flujo de elementos numéricos de la siguiente manera:

---

![flujo](https://github.com/windoctor7/windoctor7.github.io/raw/master/static/img/flujo_streams.png)

---

La flecha sobre la que se encuentran los números es la línea de tiempo sobre la que se emiten los elementos.

Para poder visualizar el cambio de elementos, le indicaremos al Publisher que emita un número cada segundo:    

```java
Flux<Integer> flux = Flux.range(1,30).delayElements(Duration.ofSeconds(1));
```
    
En este punto, ya tenemos un Publisher que es nuestra clase Flux que emite objetos Integer. Veamos ahora la parte de los Subscribers creando la siguiente clase:

```java
public class Subscriber {

    public static void multiplicar(Integer n)  {
        System.out.println("Subscriber2: "+n*n);
    }
}
```

Esta clase es muy sencilla, simplemente tenemos un método ``multiplicar`` que espera recibir un entero y multiplicarlo por si mismo e imprimir el resultado en consola.

Finalmente creamos un RestController a donde crearemos el Publisher visto anteriormente y le indicaremos sus suscriptores a quien les deberá notificar cuando haya nuevos elementos:

```java
@RestController
public class NumerosController {

    @GetMapping(path = "/numeros", produces = "text/event-stream")
    public Flux<Integer> all () {
        Flux<Integer> flux = Flux.range(1,30)
                .delayElements(Duration.ofSeconds(1));

        flux.subscribe(System.out::println); // suscriptor 1
        flux.subscribe(Subscriber::multiplicar); // suscriptor 2
        return flux; // retornamos el elemento. Sería como el suscriptor 3
    }
}
```

Si ejecutamos el proyecto con bootRun y accedemos a la dirección [http://localhost:8080/numeros](http://localhost:8080/numeros) veremos en el navegador la siguiente salida:

    data:1

    data:2

    data:3

    .....

    .....
      

Al mismo tiempo, en la consola veremos algo como esto:

    1
    Subscriber2: 1
    2
    Subscriber2: 4
    Subscriber2: 9
    3
    

Una de las características de las aplicaciones reactivas es que son no bloqueantes, son asíncronas. Para entender a lo que me refiero, modifiquemos el método ``multiplicar()`` y agreguemos un retardo de 5 segundos.

```java
    public static void multiplicar(Integer n)  {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Subscriber2: "+n*n);
    }
```


Al ejecutar de nueva cuenta el proyecto, la salida en la consola es similar a esto:

    1
    2
    3
    4
    Subscriber2: 1
    5
    6
    7
    8
    9
    10
    Subscriber2: 4


Podemos ver que pese a que hay un ``Thread.sleep(5000)`` el resto de los suscriptores no se bloquean, tal es el caso que el suscriptor 1 continua imprimiendo en consola la secuencia de números. 

En una aplicación tradicional, podríamos verlo como la ejecución de estas 2 líneas de código:

```java
Subscriber.multiplicar(numero);
System.out.println(numero);
```

El método multiplicar bloquearía el hilo principal cada 5 segundos, dando como resultado que el ``println`` de abajo no se ejecute hasta que el hilo principal que está usando el método multiplicar sea liberado.

## Streams (Flujo de datos)

La programación reactiva se basa en Streams. Podemos entender a un Stream como un flujo de datos. A cada elemento del flujo podemos aplicarle una operación. 

Para entender de mejor forma este concepto, nos ayudaremos de los [Diagramas Marble](https://dzone.com/articles/marble-diagrams-rxjava-operators).

Considere la secuencia de números: 1,60,5,22,30,2. A este flujo de elementos, deseamos aplicarle un filtro para solo devolver aquellos números mayores a 10. El diagrama marble que lo modelaría sería el siguiente:

---

![Operación Filter](https://github.com/windoctor7/windoctor7.github.io/raw/master/static/img/marble_filter.png)

---

El diagrama anterior es muy explicativo y nos hace comprender la programación en streams. Los elementos fluyen precisamente como un flujo y conforme los elementos fluyen, se les aplica una transformación a cada elemento del flujo.


Veamos ahora un ejemplo en código. 

A nuestra secuencia de números del 1 al 30, deseamos aplicarle un filtro para solo tener aquellos números que son divisibles entre 2. Finalmente a estos números divisibles entre 2, los multiplicaremos por 2.

El código reactivo quedaría de la siguiente forma:

```java
Flux<Integer> flux = Flux.range(1,30)
        .delayElements(Duration.ofSeconds(1))
        .filter(n -> n % 2 == 0) // solo números divisibles entre 2
        .map(n -> n*2); // a cada elemento que ha sido filtrado, lo multiplicamos por 2
```

Si ejecutamos el proyecto con este cambio, al ingresar a [http://localhost:8080/numeros](http://localhost:8080/numeros) obtendremos la siguiente secuencia de números: 4,8,12,16,20... que corresponden con los números 2,4,6,8,10... que fueron filtrados y después multiplicados por 2.

Podemos decir entonces que a un flujo de elementos podemos aplicarle diferentes operaciones anidadas, en el código anterior aplicamos las operaciones delayElements, filter y map.

A estas alturas seguramente has comprendido los ejemplos realizados, pero quizá te estés preguntando ¿Cuál es la ventaja de usar programación reactiva? ¿Acaso no se pudo resolver con la programación tradicional? ¿En que casos podemos aplicar programación reactiva?

La respuesta a estas preguntas está en el método ``multiplicar()`` que vimos anteriormente. Recordemos que agregamos un retardo de 5 segundos para simular por ejemplo una consulta pesada a una base de datos o la invocación a un servicio remoto. Vimos que pese al retardo de 5 segundos, la aplicación siguió con su ejecución. El Publisher siguió emitiendo elementos y el consumidor 1 siguió procesando estos elementos sin esperar a que el consumidor 2 terminara. Las librerías que proveen soporte para programación reactiva se encargan de la concurrencia y las invocaciones asíncronas. Por lo tanto, si podemos resolver cualquier problema con la programación tradicional, pero seríamos nosotros quienes manejaríamos los threads y la concurrencia.

---

## Soporte reactivo para Thymeleaf y MongoDB

Utilizaremos [Spring Data Mongo](https://docs.spring.io/spring-data/mongodb/docs/current/reference/html/) para conectarnos a una base de datos MongoDB, pero lo haremos de forma reactiva. Por lo tanto, el starter a utilizar será:

	compile('org.springframework.boot:spring-boot-starter-data-mongodb-reactive')

También usaremos el starter para trabajar con [Thymeleaf](http://www.thymeleaf.org) como motor de plantillas, el cuál en su última versión trae soporte para programación reactiva.

	compile('org.springframework.boot:spring-boot-starter-thymeleaf')

En aplicaciones Spring WebFlux, Thymeleaf tiene 3 modos de operación, y su ejecución depende si un límite se ha configurado para el tamaño máximo del **chunk** de salida ó si una variable de contexto data-driver ha sido especificada. Un chunk es un fragmento de información.

Los 3 modos y cuando se ejecutan se explican a continuación:

1. **FULL**. Cuando no hay establecido un límite para el tamaño máximo del chunk y ninguna variable de contexto data-driver fue especificada, entonces toda la salida de la plantilla se generará en la memoria como un solo fragmento (chunk). Este modo debe ser utilizado cuando tengamos pocos datos que renderizar en el template, asumiendo un mayor consumo de memoria.

1. **CHUNKED**. Cuando un limite para el tamaño máximo del chunk es establecido pero una variable de contexto data-driver no fue especificada. La salida de la plantilla se generará en fragmentos (chunks) de un tamaño igual o menor al límite especificado (en bytes). Después de que un chunk es emitido, el motor de plantillas se detendrá y esperará a que el servidor solicite más chunks por medio de un **backpressure** reactivo.

1. **DATA-DRIVEN**. Cuando una variable data-driven a sido especificada en el contexto. Esta variable de contexto deberá ser del tipo [IReactiveDataDriverContextVariable](http://www.thymeleaf.org/apidocs/thymeleaf-spring5/3.0.5.M3/org/thymeleaf/spring5/context/webflux/IReactiveDataDriverContextVariable.html). En esta variable es donde pondremos el flujo de datos que el Publisher emita.


Antes de empezar con el ejemplo, debes cargar a mongo algunos datos. Puedes obtener un data JSON desde este repositorio de [github](https://github.com/ozlerhakan/mongodb-json-files/blob/master/datasets/students.json) e importarlos a la base de datos **test** de mongodb.

Creamos el POJO a donde mapearemos los campos con los de la colección "students" de mongo.

```java
@Document(collection = "students")
public class Student {

    private String id;
    private String name;
    private List<Score> scores;

    //SETTERS y GETTERS
}
```

---

## Conexion a Mongo DB

Mientras no indiquemos lo contrario, Spring Boot nos conectará por default a la base de datos mongo localhost:27017/test

Por lo tanto, será en la base de datos **test** donde deberás cargar la información del JSON mencionado arriba. Este JSON trae poco más de 100,000 registros (en el mundo de las bases de datos nosql se le llaman "documentos"). Esta cantidad de información es interesante para ver el modo reactivo y no reactivo que estaremos viendo en estos ejemplos.

También debemos creamos un repositorio Spring Data, pero lo haremos con soporte reactivo mediante la interfaz [ReactiveCrudRepository](https://docs.spring.io/spring-data/data-commons/docs/2.0.0.M3/api//org/springframework/data/repository/reactive/ReactiveCrudRepository.html)

```java
public interface StudentReactiveRepository extends ReactiveCrudRepository<Student,String>{
}
```

[Si no conoces de Spring Data, puedes leer [la documentación oficial](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories) para que comprendas como una interfaz hereda de otra interfaz y sin mayor código obtendremos datos desde mongo :D]

---

## Thymeleaf: Modo FULL

Primero veamos el Modo full el cuál se ejecutará si no hay definido un límite del tamaño del chunk y tampoco alguna variable de contexto data-driver. Teniendo en mente lo anterior, el siguiente Controller mandará al template los datos de un solo golpe.

```java
@Controller
public class StudentListController {

    // Esta es una interfaz, no hay código! Esta es la magia de Spring Data!
    @Autowired
    private StudentReactiveRepository repository;

    @GetMapping("/list-students")
    public String listStudents(Model model){
        Flux<Student> flux = repository.findAll(); // recuperamos todos los registros de forma reactiva
        model.addAttribute("students", flux); 
        return "students"; // direccionamos al students.html
    }
}
```

Finalmente bajo la ruta **/resources/templates** crearemos el siguiente html,

**students.html**

```java
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Students</title> 
</head>
<body>
<table>
    <thead>
    <tr>
        <th>Name</th>
    </tr>
    </thead>
    <tbody>
    <tr th:each="s : ${students}">
        <td th:text="${s.name}">...</td>
    </tr>
    </tbody>
</table>
</body>
</html>
```


Si ejecutamos el proyecto y accedemos desde nuestro navegador a [http://localhost:8080/list-students](http://localhost:8080/list-students) veremos que la página tarda varios segundos en responder. Esto es porque aunque la obtención de datos desde MongoDB se hace de forma reactiva, el envío que hacemos al cliente no lo es tanto. El modo Full toma todo el flujo de información y  lo envía al template en un solo fragmento.

Mediante las herramientas de desarrollo del navegador, quise medir el tiempo que tarda en llegar el primer dato al navegador y la siguiente gráfica nos arroja un tiempo de 5.53 segundos.

![tiempo en modo full](https://github.com/windoctor7/windoctor7.github.io/raw/master/static/img/time_full.png)



Esto nos deja como conclusión que podemos tener un back-end reactivo, pero si nuestro front-end no lo es, nuestra aplicación no será completamente reactiva.

## Thymeleaf: Modo CHUNKED
El modo chunked se ejecutará si la variable maxChunkSize está establecida. En Spring Boot la podemos establecer en el **application.properties** de esta manera:

    spring.thymeleaf.reactive.max-chunk-size=1024
    
El valor es el número de bytes del buffer.

Con solo agregar esta propiedad, al invocar a [http://localhost:8080/list-students](http://localhost:8080/list-students) se ejecutará en modo CHUNKED.

Podemos ver la diferencia en los tiempos de ejecución respecto al modo FULL.

![modo CHUNKED](https://github.com/windoctor7/windoctor7.github.io/raw/master/static/img/time_chunked.png)


## Thymeleaf: Modo DATA-DRIVEN
Como mencionamos anteriormente, el modo DATA-DRIVEN se ejecutará si se agrega una variable de contexto IReactiveDataDriverContextVariable en el modelo.La clase ReactiveDataDriverContextVariable nos permite agregar esta variable:

```java
@GetMapping("/list-students-reactive")
public String listUsersReactive(Model model)
{
    Flux<Student> userFlux = repository.findAll();
    model.addAttribute("students", new ReactiveDataDriverContextVariable(userFlux, 50));
    return "students";
}
```

ReactiveDataDriverContextVariable internamente utilizará Server-Sent Event para enviar los datos al cliente. Puedes ver un tutorial de Server-Sent Event con Spring [aquí](https://windoctor7.github.io/Spring-SSE.html) y con Spring 5 sin Thymeleaf [aquí](https://windoctor7.github.io/Spring-Web-Flux.html)


El segundo parámetro de ReactiveDataDriverContextVariable corresponde al número de elementos que serán enviados con cada fragmento de datos. En este caso, se estarán enviando de 50 en 50 elementos.


Podemos observar que al ingresar a [http://localhost:8080/list-students-reactive](http://localhost:8080/list-students-reactive) tenemos una respuesta más reactiva. Además de notarlo a simple vista, podemos ver que solo tarda 7.65 milisegundos en llegar el primer dato.

![modo DATA-DRIVEN](https://github.com/windoctor7/windoctor7.github.io/raw/master/static/img/time_reactive.png)

Si queremos jugar un poco y ver como van apareciendo en la página elemento por elemento, podemos poner un retardo de 1 segundo en la generación que el Flux (Publisher) emitirá y cambiar el buffer de elementos de ReactiveDataDriverContextVariable.

```java
@GetMapping("/list-students-reactive")
public String listUsersReactive(Model model)
{
    Flux<Student> userFlux = repository.findAll().delayElements(Duration.ofSeconds(1));
    model.addAttribute("students", new ReactiveDataDriverContextVariable(userFlux, 1));
    return "students";
}
```

De esta forma podremos ver como se muestra en la página uno por uno cada elemento. Finalmente aclarar que cambiamos el valor de 50 a 1 debido a que los elementos no serán enviados al cliente hasta que el buffer se llene, es decir que si tuvieramos el siguiente código,

```java
@GetMapping("/list-students-reactive")
public String listUsersReactive(Model model)
{
    Flux<Student> userFlux = repository.findAll().delayElements(Duration.ofSeconds(1));
    model.addAttribute("students", new ReactiveDataDriverContextVariable(userFlux, 50));
    return "students";
}
```
    
Tendriamos que esperar 50 segundos para poder ver datos en la pantalla.
