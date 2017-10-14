# Desarrollo ágil de aplicaciones con Java
Frecuentemente se asocia a Java con un lenguaje lento y verboso para desarrollar aplicaciones. 
En mi [blog](https://windoctor7.github.io/) hago un intento nada forzado para cambiar esta perspectiva y dar a conocer 
herramientas y frameworks que nos permiten desarrollar aplicaciones con la plataforma Java de forma ágil y robusta.

## ¿Qué ejemplos voy a encontrar aquí?
Principalmente encontrarás código fuente que usa [Spring Boot](https://projects.spring.io/spring-boot/), [Tapestry](http://tapestry.apache.org), [MongoDB](https://www.mongodb.com/es) y de algunos lenguajes que soporta la JVM como por ejemplo Groovy, Scala y Kotlin. 

Todos los ejemplos están construidos con Gradle por lo que clonar el repositorio y abrirlo con tu IDE favorito será muy fácil.



## ¿Como ejecutar los ejemplos?
Mediante el Wrapper de Gradle.

1. Clona el repositorio ó baja el ZIP y descomprimelo en tu computadora.
1. Sitúate en la carpeta específica del ejemplo que deseas ejecutar.
1. Ahí encontrarás 2 archivos, gradlew y gradlew.bat que dependiendo del sistema operativo deberás usar. Asumiendo que la mayoría usa Linux o Mac OS ejecutaríamos esto:

Revisa en la carpeta del código fuente del proyecto, si no existen otras indicaciones en el archivo README.md entonces puedes ejecutar el proyecto generalmente de la siguiente forma: 

Para ejemplos con Spring Boot

  `$ ./gradlew bootRun`
  
Para ejemplos que usen Tapestry

  `$ ./gradlew jettyRun`
  
Si el archivo README.md del proyecto contiene indicaciones adicionales, entonces deberás seguir tales.
  
## Índice de código
Se enlista a continuación una pequeña descripción de cada una de las carpetas de código existente y el enlace al tutorial de mi blog que explica su funcionamiento.


| Código | Descripción
|:-:|---|
| [spring-scheduler](https://windoctor7.github.io/Tareas-con-Spring-Scheduler.html)| Se explica como programar tareas que se ejecuten automáticamente usando la anotación `@Scheduled`de Spring. Una excelente alternativa a Quartz. |
| [spring-set-profile](https://github.com/windoctor7/codigo-tutoriales-blog/tree/master/spring-set-profile)| Frecuentemente necesitamos obtener recursos o urls que varian dependiendo si estamos ejecutando la aplicación en desarrollo, QA o producción. Este tutorial explica como hacerlo de forma muy fácil usando los perfiles de Spring.  |
| [spring-async](https://windoctor7.github.io/Tareas-asincronas-Spring.html)  | Este Cookbook simula el registro de un usuario en una base de datos mientras envia correos electronicos reales en segundo plano usando el servidor SMTP de Google  |
| [spring-rest-1](https://github.com/windoctor/SpringBoot-Ejemplos)  | Video que muestra como desarrollar un sencillo servicio web tipo REST usando Spring Boot. |
|[spring-retry](https://github.com/windoctor7/codigo-tutoriales-blog/tree/master/spring-retry) | Cuando el envío de un correo electrónico falla ó un servicio web no responde por intermitencias en la red o porque el servidor está caído, es importante tener un sistema preparado para reintentar la operación. En este cookbook veremos como hacer esto con spring-retry. |
| [spring-statemachine](https://github.com/windoctor7/codigo-tutoriales-blog/tree/master/spring-state-machine)  | En este cookbook modelaremos una sencilla encuesta con máquinas de estados usando el proyecto oficial spring-statemachine. |
| [spring-webflux](https://github.com/windoctor7/codigo-tutoriales-blog/tree/master/spring-web-flux)   | En este tutorial se muestra un sencillo ejemplo usando programación reactiva de Spring 5 (Spring Boot 2)  |
| [spring-auth-jwt](https://github.com/windoctor7/codigo-tutoriales-blog/tree/master/spring-auth-jwt)  | En este tutorial aprenderás como implementar seguridad basada en token a tus servicios REST mediante JSON Web Token.  |
| [spring4-sse](https://github.com/windoctor7/codigo-tutoriales-blog/tree/master/Spring4-SSE)  | Muestra código de ejemplo del uso de Server-Sent Events usando Spring 4.  |