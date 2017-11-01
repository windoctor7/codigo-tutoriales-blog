![Reactor](https://github.com/windoctor7/windoctor7.github.io/raw/master/static/img/reactor-project.png)

## Introducción

En la [primer parte](https://windoctor7.github.io/Programacion-Reactiva-Spring5.html) de esta serie de tutoriales sobre Programación Reactiva utilizando el nuevo y flamante Spring 5, aprendimos un poco de teoría de fácil digestión y concluimos con un ejercicio donde utilizamos MongoDB y Thymeleaf.

En este [otro tutorial](https://windoctor7.github.io/intro-kotlin-spring.html) escribí como usar Kotlin en lugar de Java y utilizarlo junto con Spring 5. 

En este nuevo tutorial desarrollaremos un interesante ejercicio de una búsqueda con autocompletado en donde tanto el front es reactivo utilizando [RxJS](https://github.com/Reactive-Extensions/RxJS) y el back también es reactivo utilizando [WebFlux](https://docs.spring.io/spring-framework/docs/5.0.0.BUILD-SNAPSHOT/spring-framework-reference/html/web-reactive.html) de Spring, además de utilizar Kotlin como lenguaje de programación. Veremos lo rápido y sencillo que resulta desarrollar modernas aplicaciones web bajo la plataforma de la JVM y que el estígma que tiene Java sobre un desarrollo lento y aburrido es cosa del pasado.

Como he mencionado en otros tutoriales, la versión 2 de Spring Boot utiliza Spring 5. Vamos a crear un nuevo proyecto Spring Boot 2 que utilice Gradle y Kotlin, lo podemos hacer desde [spring.io](https://start.spring.io). Antes de continuar te recomiendo leer el siguiente [tutorial](https://windoctor7.github.io/intro-kotlin-spring.html) donde muestro como trabajar con Kotlin y Spring 5. En este tutorial utilizaremos WebFlux por lo tanto también es importante que lo elijas como dependencia.

---

## Búsqueda con autocompletado reactivo

El ejemplo consiste en realizar una búsqueda de usuarios en Github y Bitbucket por lo que usaremos sus respectivas API's. Desde un sencillo front utilizaremos RxJS para tener una interfaz reactiva que invocará un servicio REST programado en Kotlin y WebFlux de Spring 5. La búsqueda es reactiva porque veremos que si alguna de las API's de Github o Bitbucket llegara a tardar en responder, no bloquearemos el hilo principal y devolveremos los resultados que ya estén listos como un flujo de elementos. Los devolveremos utilizando [Server-Sent Events](https://windoctor7.github.io/Spring-SSE.html) de tal forma que conforme los elementos lleguen al navegador se irán mostrando.

![Diagrama Búsqueda](https://github.com/windoctor7/windoctor7.github.io/raw/master/static/img/Autocompletado.png)

---

## API Github y Bitbucket

Como lo mencioné anteriormente, la idea es buscar usuarios utilizando el API de Github y Bitbucket. Utilizaremos los siguientes endpoints:

| API         | Endpoint    | Descripción    |      
| :---------- | :--------------------------------|
| Github        | [https://api.github.com/search/users?q=windoctor](https://api.github.com/search/users?q=windoctor) | Devuelve una lista de usuarios que coincida con la búsqueda.|
| Bitbucket     | [https://api.bitbucket.org/2.0/users/windoctor](https://api.bitbucket.org/2.0/users/windoctor) | Devuelve el usuario exacto.|


Como podemos ver, el endpoint de Github nos devuelve una lista de los usuarios que coincidan con la palabra buscada, es decir, es una búsqueda por aproximación, mientras que el endpoint de bitbucket es búsqueda exácta, devuelve 1 solo elemento.


Puedes ver los JSON resultantes en esta [URL](https://gist.github.com/windoctor7/fb946404beddfa75afbaeb892cbed651)

Nosotros deseamos recuperar al menos 2 elementos, que son el id y el username. 

En bitbucket se llaman **uuid** y **username** respectivamente, mientras que en github ámbos atributos están dentro del elemento llamado "items" y se llaman **id** y **login** respectivamente.

---

## Data Class Kotlin
Vamos a crear una [Data Class](https://kotlinlang.org/docs/reference/data-classes.html) en kotlin para contener nuestros datos de id y username.

```kotlin
data class User(
    @JsonAlias("uuid","id") val id:String,
    @JsonAlias("username","login") val username: String) {
}
```

Dado que el id y username tienen nombres diferentes en github y bitbucket, nos valemos de la anotación ``@JsonAlias`` la cual nos permite definir uno o más nombres alternativos para la propiedad durante el proceso de deserialización. De esta forma podemos poner los nombres que tienen en github y bitbucket. 

En el caso de Github vamos a necesitar una clase envoltorio para poder convertir el JSON a un objeto debido a que el id y username se encuentran dentro de la propiedad **items** la cuál es un arreglo.

```kotlin
data class UserResultGh( //clase envoltorio para github
        val total_count:Int,
        val incomplete_results:Boolean,
        val items:ArrayList<User>)
```

En la data class anterior podemos ver la propiedad items que es un ``ArrayLists<User>``, de esta forma podremos convertir de json->object para el caso de github.

---

## Búsqueda reactiva con Mono y Flux

Vamos a crear una interfaz ``IRepositorySearch`` con dos métodos para buscar en github y bitbucket respectivamente:

```kotlin
interface IRepositorySearch {
    fun searchUsersGitHub(username:String) : Mono<UserResultGh>
    fun searchUsersBitBucket(username: String) : Flux<User>
}
```

Recordemos que Mono es una implementación de un Publisher que devuelve un único elemento, mientras que Flux también es un Publisher pero se usa para devolver más de un elemento y sin embargo podemos utilizar Flux para devolver un solo elemento sin problemas.

En la [primer parte](https://windoctor7.github.io/Programacion-Reactiva-Spring5.html) de esta serie de Programación Reactiva vimos como Flux y Mono son implementaciones de un Publisher y en los primeros ejemplos generamos un flujo de elementos numéricos mediante ``Flux.range(1,30)``. 

Sin embargo para emitir elementos personalizados podemos hacer uso del método create que tanto Flux como Mono tienen y sería ahí donde debemos colocar el código que haga la búsqueda en github o bitbucket. 

El código Java quedaría más o menos como sigue:

```java
Mono.create(new Consumer<MonoSink<UserResultGh>>() {
    @Override
    public void accept(MonoSink<UserResultGh> userMonoSink) {
        String url = "https://api.github.com/search/users?q=windoctor";
        RestTemplate restTemplate = new RestTemplate();
        UserResultGh resultGh = restTemplate.getForObject(url, UserResultGh.class);
        userMonoSink.success(resultGh); // agregamos el elemento al Stream.
    }
});
```

Si utilizamos **Java 8**, con la ayuda de los lambdas simplificamos un poco más las cosas:

```java
Mono.create(userMonoSink -> {
    String url = "https://api.github.com/search/users?q=windoctor";
    UserResultGh resultGh = new RestTemplate().getForObject(url, UserResultGh.class);
    userMonoSink.success(resultGh); // agregamos el elemento al Stream.
});
```

Pero con **Kotlin** el asunto es aún más simple

```kotlin
val url = "https://api.github.com/search/users?q=$username"
Mono.create<UserResultGh> { sink ->
    sink.success( RestTemplate().getForObject(url, UserResultGh::class.java) )
}
```

Cabe señalar que estamos utilizando la clase ``RestTemplate`` de Spring para realizar las llamadas a las API's de github y bitbucket. Pero pudimos utilizar [WebClient](http://www.baeldung.com/spring-5-webclient) el cual es un cliente web reactivo para HTTP que se introdujo por primera vez en Spring 5.


Finalmente, el código kotlin de la clase que implementa a nuestra interfaz ``IRepositorySearch`` quedaría como sigue:

```kotlin
@Component
class RepositorySearch() : IRepositorySearch {

    override fun searchUsersGitHub(username: String): Mono<UserResultGh> {
        val url = "https://api.github.com/search/users?q=$username"
        return Mono.create<UserResultGh> { sink ->
            sink.success( RestTemplate().getForObject(url, UserResultGh::class.java) )
        }.delayElement(Duration.ofSeconds(5)) //agregamos un retardo intencional de 5 segundos
    }

    override fun searchUsersBitBucket(username: String): Flux<User> {
        val url = "https://api.bitbucket.org/2.0/users/$username"
        return Flux.create<User> { sink ->
            val user:User? = RestTemplate().getForObject(url, User::class.java)
            sink.next( user)
        }
    }
}
```

## Creando el RestController

Ahora debemos crear un endpoint que lance la búsqueda tanto en Github como en Bitbucket y envíe al cliente el resultado tan pronto como estén disponibles. 

```kotlin
@RestController
class SearchUsersController(val repositorySearch: IRepositorySearch) {

    // text/event-stream para indicar que se trata de Server-Sent Event
    @GetMapping(path = arrayOf("/search/users"), produces = arrayOf("text/event-stream"))
    fun searchUsers(@RequestParam username: String): Flux<User> {

        //iniciamos búsqueda en github. Recordar que existe un retardo de 5 segundos
        val gh = repositorySearch.searchUsersGitHub(username)
            .flatMapIterable { gh -> gh.items }

        //iniciamos búsqueda en bitbucket.
        val bk = repositorySearch.searchUsersBitBucket(username)

        //finalmente hacemos un merge del Mono(github) y Flux (bitbucket)
        return Flux.merge(gh, bk)
    }

    //Cargamos el HTML
    @GetMapping(path = arrayOf("/"), produces = arrayOf(MediaType.TEXT_HTML_VALUE))
    fun index() = ClassPathResource("static/index.html")

}
```
    
El método [flatMapIterable](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#flatMapIterable-java.util.function.Function-) nos permite transformar el objeto ``UserResultGh`` emitido por el Mono a un conjunto de varios elementos que serán devueltos en un Flux.

Estamos transformando un ``UserResultGh`` a ``User``. Recordar que ``UserResultGh``tiene una propiedad llamada **items** que es un ArrayList de tipo ``User`` y el contenido de este ArrayList es precisamente el que estamos devolviendo en un Flux al aplicar la operación **flatMapIterable**.

El método [merge](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#merge-org.reactivestreams.Publisher...-) nos permite unir varios Flux o Monos en uno solo. El siguiente diagrama marble explica el uso de merge:


![marble_merge](https://github.com/windoctor7/windoctor7.github.io/raw/master/static/img/marble_merge.png)

Hasta este punto ya tenemos un back-end reactivo. Si ejecutamos el proyecto con bootRun y accedemos desde nuestro navegador a [localhost:8080/search/users?username=mario](localhost:8080/search/users?username=mario) podemos ver el siguiente resultado:

![animacion](https://github.com/windoctor7/windoctor7.github.io/raw/master/static/img/search_endpoint_reactive.gif)

Observamos que nos arroja un solo resultado y segundos después nos muestra muchos más provenientes de github.

En una aplicación bloqueante tradicional, si alguna de las dos API's tarda unos cuantos segundos en responder, el otro método tiene que esperar a que el primero termine ya que el hilo principal estaría bloqueado por éste.

Dado que nuestra aplicación es reactiva, hemos agregado intencionalmente un retardo de 5 segundos para la búsqueda en github y sin embargo veremos que pese a ello la búsqueda en bitbucket continuará y tan pronto tenga listo el resultado lo enviará al cliente. Una vez que hayan pasado los 5 segundos, se enviarán los resultados de github al cliente y se visualizarán en el navegador. 

## Utilizando RxJS
[RxJS](https://github.com/Reactive-Extensions/RxJS) es un conjunto de librerías para desarrollar aplicaciones asíncronas y basada en eventos, es decir, aplicaciones reactivas en el lado del cliente usando JavaScript. 

Vamos a utilizar 2 librerías, RxJS y [RxJS-DOM](https://github.com/Reactive-Extensions/RxJS-DOM). Esta última nos va a permitir manipular el DOM vinculándo los objetos HTML a eventos de peticiones Ajax, Web Sockets, Server-Sent Events, etc. Para hacer uso de estas librerías debemos agregarlas al **<head>** de nuestra página html:
    
```html
<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/rxjs/4.1.0/rx.all.js"></script>
<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/rxjs-dom/7.0.3/rx.dom.min.js"></script>
```

Recordemos que en la primer parte de esta serie de artículos mencionamos que una aplicación reactiva está compuesta por un Publisher y un Subscriber. Para generar este sencillo front vamos a necesitar también de un Publisher el cuál será el teclado. Debemos capturar el evento **"keyup"**

```javascript
 //Obtenemos el input con id "autocomplete"
const input = document.getElementById("autocomplete"); 

//Creamos el Publisher desde el evento "keyup" que se lance desde el input
const keyup = Rx.Observable.fromEvent(input, "keyup")
```

Teniendo ya listo un flujo de elementos, con la función **map** transformamos el evento keyup al valor que tenga el input y posteriormente filtramos solo cuando el texto tenga una longitud mayor a 3 caracteres.

```javascript
const keyup = Rx.Observable.fromEvent(input, "keyup")
    .map( function(e){ return e.target.value})
    .filter( function(text){ console.log(text)
        return text.length > 3})
 ```
 
También debemos agregar un suscriptor. En el suscriptor será donde construiremos el pequeño html que mostrará los resultados de la búsqueda.

El código completo de **resuorces/static/index.html** es el siguiente:

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>demo-spring-sse</title>
    <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/rxjs/4.1.0/rx.all.js"></script>
    <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/rxjs-dom/7.0.3/rx.dom.min.js"></script>
</head>

<body>

<!--INPUT DE AUTOCOMPLETE-->
<input type="text" id="autocomplete">

<!--ESPACIO RESERVADO PARA LOS ITEMS-->
<div id="items"></div>

</body>

<script>
    //Obtenemos el input con id "autocomplete"
    const input = document.getElementById("autocomplete");

    //Creamos el Publisher desde el evento "keyup"
    const keyup = Rx.Observable.fromEvent(input, "keyup")
        //transformamos el evento al valor del input
        .map( function(e){ return e.target.value})
        //filtramos únicamente cuando el texto tenga más de 3 caracteres
        .filter( function(text){ console.log(text)
            return text.length > 3})
        // agregamos un espacio de tiempo entre cada tecla pulsada
        .debounce(750)
        .distinctUntilChanged()
        // realizamos la busqueda utilizando Server-Sent Events
        .flatMap(busqueda)
        //suscriptor a donde armamos el html para mostrar los resultados
        .subscribe(
            function(data) {
                const items = document.getElementById("items");
                const container = document.createElement("div");
                const a = document.createElement("a");
                a.innerHTML += JSON.parse(data).username
                container.appendChild(a);
                items.appendChild(container);
            }, function (error) {
                console.log(error.message)
            }
    );

    function busqueda(username) {
        const items = document.getElementById("items");
        items.innerHTML = "";
        return Rx.DOM.fromEventSource('search/users?username='+username);
    }


</script>

</html>
```

Finalmente ejecutamos el proyecto con bootRun y al acceder desde nuestro navegador a [http://localhost:8080](http://localhost:8080) veremos una caja de texto a donde podemos realizar la búsqueda. Al escribir una palabra con más de 3 caracteres se comenzará la búsqueda y veremos primero el resultado de bitbucket, 5 segundos después podremos ver como la lista de la búsqueda se actualiza.

Les dejo una muestra de como se ve el ejemplo funcionando.

![front reactivo](https://github.com/windoctor7/windoctor7.github.io/raw/master/static/img/search_front_endpoint_reactive.gif)


Déjame tus comentarios sabiendo que te pareció este tutorial!
