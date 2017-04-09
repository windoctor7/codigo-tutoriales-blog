# Desarrollo ágil de aplicaciones con Java
Frecuentemente se asocia a Java con un lenguaje lento y verboso para desarrollar aplicaciones. 
En mi [blog](https://windoctor7.github.io/) hago un intento nada forzado para cambiar esta perspectiva y dar a conocer 
herramientas y frameworks que nos permiten desarrollar aplicaciones con la plataforma Java de forma ágil y robusta.

## ¿Qué tipo de código voy a encontrar aquí?
Principalmente encontrarás código fuente que usa [Spring Boot](https://projects.spring.io/spring-boot/), [Tapestry](http://tapestry.apache.org).
Todos los ejemplos están construidos con Gradle.

## ¿Como ejecutar los ejemplos?
Mediante el Wrapper de Gradle.

1. Clona el repositorio ó baja el ZIP y descomprimelo en tu computadora.
1. Sitúate en la carpeta específica del ejemplo que deseas ejecutar.
1. Ahí encontrarás 2 archivos, gradlew y gradlew.bat que dependiendo del sistema operativo deberás usar. Asumiendo que la mayoría usa Linux o Mac OS ejecutaríamos esto:

Para ejemplos con Spring Boot

  `$ ./gradlew bootRun`
  
Para ejemplos con Tapestry

  `$ ./gradlew jettyRun`
  
## Índice del código

* spring-scheduler -> 
