
## Introducción
Casi siempre necesitamos personalizar la configuración de nuestra aplicación según el entorno en el que se ejecuta, por ejemplo la url de la base de datos, el servidor de correos ó la url de los servicios web cambian si nuestra aplicación está corriendo en un ambiente de desarrollo, QA, producción, etc. Cambiar estas URL's o recursos manualmente puede ocasionarnos graves problemas, ¿Les ha pasado que han subido a producción apuntando alguna URL de desarrollo?... Bueno, a mi si.

## Primer ejemplo
Teniendo nuestra aplicación Spring Boot, vamos a crear una clase que imprima un simple texto dependiendo del ambiente de ejecución.

```java
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
```


Según la [documentación de Spring](http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/core/env/Environment.html),``Environment`` es una interfaz que representa el entorno en el cuál está corriendo la aplicación actual y modela dos aspectos claves del entorno de la aplicación: profiles y properties. 

Por lo anterior es que podemos usar el método _getProperty_ para obtener el valor de la propiedad _url.smtp_ definida en el archivo principal de configuración de spring boot.

**application.properties**
```properties
url.smtp="200.123.45.65"
```

Para ejecutar este primer ejemplo basta agregar a la clase principal de spring boot un ``CommandLineRunner``.

```java
@SpringBootApplication
public class DemoApplication {

    @Autowired
    PrimerEjemplo ejemplo;

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(){
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                ejemplo.imprimir();
            }
        };
    }
}
```

Al ejecutarlo veremos en la consola lo siguiente:

    Enviando un correo electronico usando el servidor SMTP 200.123.45.65
    

## Agregando el archivo application-dev.properties
Hasta ahora solo hemos leído una propiedad del archivo _application.properties_ usando la interfaz ``Environment``pero no hemos visto nada sobre los ambientes de ejecución.

Vamos a crear un archivo de propiedades adicional agregando la misma clave _url.smtp_ pero con diferente valor.

**application-dev.properties**
```properties
url.smtp=10.60.34.123
```

Para que Spring pueda obtener el valor de una propiedad mediante la interfaz ``Environment`` se utiliza la convención de nombres ``application-{profile}.properties`` 

En el archivo _build.gradle_ agregamos lo siguiente,

```java
bootRun {
    args = ["--spring.profiles.active=dev"]
}
```

Al ejecutar nuevamente la aplicación, ahora obtendremos el siguiente texto en la consola,

    Enviando un correo electronico usando el servidor SMTP 10.60.34.123
    
¿Todas las propiedades que existen en _application.properties_ debo tenerlas en _application-dev.properties_? La respuesta es no. En este ejemplo, spring buscará la propiedad _url.smtp_ primero en el archivo _application-dev.properties_ y de no encontrarla buscará en el archivo por default _application.properties_

Lo anterior es una excelente forma de sobre escribir propiedades y no tener propiedades duplicadas, en su lugar solo tendremos aquellas cuyo valor si dependa del ambiente de ejecución.

## Conclusiones
Mientras desarrollamos alguna aplicación spring boot como por ejemplos servicios REST, podemos correr la aplicación con la tarea ``bootRun`` y agregar al archivo _build.gradle_ la variable de entorno _spring.profiles.active_ estableciendo el perfil dev. 

Si tenemos un ambiente de QA o pre producción seguramente desplegaremos un archivo WAR. Podemos agregar la variable de entorno al momento de levantar el servidor. En JBoss EAP o Wildfly sería algo así

    ./standalone.sh -Dspring.profiles.active=qa

Por lo tanto también deberemos tener un archivo _application-qa.properties_

Finalmente cuando se trate de desplegar la aplicación en producción, podemos **no** establecer ningún valor para ``spring.profiles.active``y de esta forma spring buscará todas las propiedades en el archivo por default _application.properties_