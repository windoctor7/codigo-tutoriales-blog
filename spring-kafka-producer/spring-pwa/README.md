# ¿Qué es una Progressive Web Application?

Es una aplicación 100% Web, responsiva y por lo tanto se ve y funciona muy bien en dispositivos móviles, incluso se ve como una  una aplicación nativa **Android/iOS**, pero hay más, dado que la base de las PWA son los [Services Workers](https://developers.google.com/web/fundamentals/primers/service-workers?hl=es), éstas apps pueden trabajar offline.

En este video de 2 minutos, te muestro como se ve e instala en un dispositivo iOS la PWA que vamos a desarrollar en este tutorial.

<center>
<iframe width="560" height="315" src="https://www.youtube.com/embed/VjA2BIMHJ9c" frameborder="0" allow="accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture" allowfullscreen></iframe>
</center>

# ¿Una PWA en Java?

Una PWA no es exclusiva de los nuevos frameworks JavaScript como **Angular, React o VueJS**, aunque si bien es cierto que su implementación es más natural, es muy posible implementar una PWA usando lenguajes de servidor como Java utilizando **SpringBoot + Thyamleaf** y de manera muy rápida y sencilla.

Descarga el código fuente de este ejemplo para que puedas seguir la explicación que a continuación daré.

**Si lo deseas puedes ejecutar el ejemplo sin necesidad de tener ninguna herramienta de desarrollo instalada**, basta
con que descargues el proyecto, desde la terminal o consola de tu sistema operativo te situes en la carpeta raíz del proyecto y ejecutes el siguiente comando en el caso de Linux/MacOS

```bash
 $ ./gradlew bootRun
 ```
 
 o en el caso de Windows creo que es algo así
 
 ```bash
 $ gradlew.bat bootRun
 ```
 
Abre un navegador e ingresa a http://127.0.0.1:8080/login

Para probar el ejemplo desde tu dispositivo móvil, debes estar conectado a la misma red que la computadora donde estás corriendo el ejemplo y en lugar de ingresar al 127.0.0.1 cambiala por la IP que tienes asignada en tu computadora.

# progressify para los ServicesWorkers

Para desarrollar el ejemplo, nos vamos a ayudar de la librería [progressify-spring](https://github.com/navkm/progressify-spring) pero en lugar de utilizar maven como en el ejemplo de su página, usaremos Gradle aunque para ello será necesario agregar a nuestro *build.gradle* la siguiente linea

```groovy
annotationProcessor 'org.progressify:progressify-spring:0.2.0'
```

Esta librería nos ayuda a generar el código JavaScript necesario para trabajar con los ServicesWorkers con simples anotaciones Java y para ello utiliza un annotationProcessor para generar un archivo **sw.js** en tiempo de compilación el cuál contiene el código de nuestro serviceworker.

Después de un rato de no poder correr el ejemplo que viene en su página con Gradle, caí en la cuenta que el desarrollador de la librería ``progressify-spring`` agregó en su clase **PWAProcessor.java** el siguiente método:

```java
private String getResourcesDirectory(String dirLocation) {
        String multiModuleProjectDirectory = System.getProperty("maven.multiModuleProjectDirectory");
        log.info("Found maven.multiModuleProjectDirectory to be :" + multiModuleProjectDirectory);
        return multiModuleProjectDirectory + java.io.File.separator + dirLocation;
    }
```

Ese método es utilizado para obtener la ruta a donde va a escribir el archivo sw.js , sin embargo el path se arma obteniendo el valor de la propiedad ``maven.multiModuleProjectDirectory`` el cual es inexistente en nuestro caso al utilizar Gradle. Por ello será necesario agregar al archivo **gradle.properties** la siguiente línea

```properties
systemProp.maven.multiModuleProjectDirectory=/Users/ascariromopedraza/demo-pwa
```

Lo que resulta obvio es que cambies el valor por la ruta a donde tienes la carpeta del proyecto.

# Las PWA son offline

Una de las características principales de las PWA es que deben funcionar sin conexión a internet y cuando tengan conexión, sincronizar los datos hacia el servidor. ``progressify-spring`` utiliza [Workbox](https://developers.google.com/web/tools/workbox/modules/workbox-strategies), una librería de Google que facilita el trabajo de los ServicesWorkers.

En la clase **LoginController** de nuestro proyecto, observaremos la anotación ``@StaleWhileRevalidate`` la cual permite utilizar la estrategia **Stale-While-Revalidate** de WorkBox. Esta estrategia permite obtener la app tan rápido como sea posible utilizando los elementos almacenados en el caché del navegador y en caso que falle, obtenerlos del servidor.

# Registrar el ServiceWorker

En la página **login.html** encontraremos el registro de nuestro serviceworker

```javascript
if ('serviceWorker' in navigator) {
            window.addEventListener('load', ()=> {
                navigator.serviceWorker.register('/sw.js');
        });
        }
```

Este archivo **sw.js** se genera automáticamente en **resources/static/sw.js** 

Si no tienes este archivo, deberás hacer un ``clean`` y luego un ``assemble`` desde Gradle para que se genere.

# El archivo manifest.json

Este archivo es requerido para que nuestra app sea reconocida como una PWA por los navegadores. En este archivo se configuran diferentes parámetros como el nombre, descripción e iconos de nuestra app que se usarán en un dispositivo móvil.

```json
{
  "short_name": "PWA Ejemplo",
  "name": "Ejemplo de app PWA",
  "display": "standalone",
  "icons": [{
    "src": "./images/logo.jpg",
    "sizes": "192x192",
    "type": "image/jpg"
  }],
  "start_url": "../login",
  "theme_color": "#404549",
  "background_color": "#34373b"
}
```

Observa con detenimiento el video anterior y verás que al momento de agregar la app al Inicio, nos da por default un nombre, url y un ícono, estos valores los hemos configurado en el archivo **manifest.json** (también se puede nombrar **manifest.webmanifest**).

Este archivo **manifest.webmanifest** lo debemos "linkear" en nuestra página **login.html**
    
```html    
    <link rel="manifest"  th:href="@{/assets/manifest.webmanifest}">
```
    
# Oh Apple
Aunque Apple ya tiene soporte para PWA, aun es limitado, no cuenta con varias características interesantes como las notificaciones Push, en iOS no funcionan. De manera similar, hay algunas cosas que apple hace diferentes, como en el caso del ícono de la app, se debe agregar en el **login.html** de la siguiente manera

```html
<link rel="apple-touch-icon" th:href="@{/assets/images/logo.jpg}" />
````

Más adelante continuaré este ejemplo y escribiré sobre como usar [IndexedDB](https://developer.mozilla.org/es/docs/Web/API/IndexedDB_API/Usando_IndexedDB)