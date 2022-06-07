Ya está a la vuelta de la esquina la versión 5 del framework más popular en el mundo Java, hablo por supuesto de Spring.

Entre las novedades se encuentra el proyecto [Spring Web Flux](http://docs.spring.io/spring-framework/docs/5.0.0.M1/spring-framework-reference/html/web-reactive.html) el cuál nos proporcionará capacidades para la programación reactiva en aplicaciones web.

## ¿Qué es la programación reactiva?
Se trata de un paradigma que consiste en ver a casi todo como un flujo de elementos (Stream). Si conocen Java 8 y el [API Stream](https://windoctor7.github.io/API-Stream-Java8.html), podemos decir que es algo parecido a ello con importantes diferencias, pero de estilo muy similar.

Pero ¿que resuelve la programación reactiva? Bueno, actualmente las aplicaciones web requieren de mucho dinamismo, con operaciones en tiempo real. Quienes hayan tratado con AJAX, seguramente recordaran aquel típico ejemplo con el que nos iniciamos con esta tecnología, se trataba de actualizar la hora cada cierto tiempo. Esto daba la apariencia que ocurría todo en línea, no era necesario recargar la página entera, solo la sección donde se mostraba la hora. La gran diferencia es que el navegador web realizaba una petición al servidor cada cierto tiempo. 

Con la programación reactiva no es necesario que el cliente realice una petición al servidor cada cierto tiempo, en lugar de ello se sigue un esquema de ``Publisher``/ ``Suscriber``, algo así como el patrón de diseño [Observer](https://es.wikipedia.org/wiki/Observer_(patrón_de_diseño))

Spring Web Flux se basa en el proyecto Reactor que es un framework para Java que proporciona soporte para la programaciópn reactiva y que fue creado por la gente de PIVOTAL, la empresa detrás del desarrollo de Spring.

## Ejemplo
La  mejor forma de comprender un poco es haciendo un sencillo pero muy clarificador ejemplo. Para ello usaremos Spring Boot 2, que de igual manera se trata de una versión beta, aún no es definitiva. Spring Boot 2 usa Spring Framework 5.

Pueden crear el proyecto en [https://start.spring.io](https://start.spring.io) ó por supuesto descargarlo de mi repositorio github, el enlace lo encuentran al inicio o al final de este artículo.

Lo único que necesitamos es crear un controller con la ya conocida anotación ``@RestController``

```java
@RestController
public class NumerosController {

    @GetMapping(path = "numeros", produces = "text/event-stream")
    public Flux<Integer> all () {
        return Flux.range(1, 30)
                .delayElements(Duration.ofSeconds(1)).map(n->n);
    }
}
```

Del código anterior podemos comentar,

- ``@GetMapping`` es una abreviatura a @RequestMapping(method = RequestMethod.GET) y está disponible a partir de la versión 4.3 de spring.

- ``text/event-stream`` es el Content-Type necesario para poder transmitir el resultado como un flujo de elementos.

- ``Flux`` es una clase que tiene las tareas del Publisher, es decir, del que se encargará de informar al Susriber los cambios.

Con el código anterior, crearemos una secuencia de elementos de tipo Integer. Esta secuencia serán números del 1 al 30 que se transmitirán al servidor de forma asíncrona con lapsos de tiempo de 1 segundo entre cada uno de ellos.

Lo único que necesitamos es crear una página **index.html**

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Numeros</title>
</head>
<body>
<div id="resultado"></div>
<script type="text/javascript">
    var source = new EventSource("numeros") // este es el endpoint de nuestro controller
    source.addEventListener('message', function(e) {
        document.getElementById("resultado").innerHTML += event.data + "<br>"
    }, false);

    source.addEventListener('open', function(e) {
        console.log("INICIO");
    }, false);

    source.addEventListener('error', function(e) {
        if (e.readyState == EventSource.CLOSED) {
            console.log("close");
        }
    }, false);
</script>
</body>
</html>
```

Del código anterior podemos comentar que,

- ``EventSource`` es una clase del API **server-sent event** que aunque no es un estándar, muy pronto lo será y permite abrir una conexión al servidor y recibir eventos de él. Esto cambia un poco a lo que estamos acostumbrados con AJAX donde el cliente es el que periódicamente consulta al servidor. Aquí es el servidor el que informa al cliente cuando existen nuevos flujos que transmitir.

Como comenté al inicio, la programación reactiva consiste en ver a todo como un flujo de elementos, permitiendo realizar operaciones sobre ese flujo y pudiendo transmitirlo a un cliente de tal forma que se tiene una experiencia asíncrona. Esto es como los servicios de streaming como Netflix. No necesitamos esperar a que la película cargue completamente para poder empezar a verla. En cuanto tenemos un cierto flujo de bytes, comienza la reproducción y mientras se está reproduciendo, los bytes siguen fluyendo.

En cuanto al ejemplo eso es todo lo que necesitamos. Dado que está sobre Spring Boot, lo lanzaremos con el conocido bootRun.

Puedes bajar el código fuente del proyecto que está sobre Gradle e importarlo a tu IDE favorito y ejecutarlo accediendo a tu navegador: http://127.0.0.1:8080/index.html

## Video demostrativo

<iframe width="560" height="315" src="https://www.youtube.com/embed/zm6IB6KoPIw?rel=0?ecver=1" frameborder="0" allowfullscreen></iframe>