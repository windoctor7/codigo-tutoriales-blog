
## Habilitando soporte para scheduling

Si necesitamos ejecutar tareas automáticamente y de forma periódica, usar la anotación ``@Scheduled`` de Spring es la opción rápida y perfecta.

Para habilitar el soporte de scheduling y poder usar la anotación ``@Scheduled`` de Spring  solo agregamos ``@EnableScheduling`` en nuestra clase principal de Spring Boot.

```java
    @SpringBootApplication
    @EnableScheduling
    public class SpringSchedulerApplication
```

## Programando tareas

Podemos programar una tarea para que se ejecute cada cierto tiempo.

```java
    // Se ejecuta cada 3 segundos
    @Scheduled(fixedRate = 3000)
    public void tarea1() {
        System.out.println("Tarea usando fixedRate cada 3 segundos - " + System.currentTimeMillis() / 1000);
    }
```

Usando ``fixedRateString``podemos tener el tiempo de ejecución en el archivo de configuración de spring boot.

```java
    @Scheduled(fixedRateString = "${imprime.tarea}")
    public void tarea2() {
        System.out.println("Tarea usando fixedRateString cada 5 segundos - " + System.currentTimeMillis() / 1000);
    }
```


y en el archivo **application.properties** ponemos esto.

    imprime.tarea=5000

Si deseamos que nuestra tarea inicie su ejecución con un retardo inicial, entonces usaremos la propiedad ``initialDelay``

```java
    @Scheduled(fixedRate = 3000, initialDelay = 10000)
    public void tarea3() {
        System.out.println("Tarea con retraso inicial de 10 segundos - " + System.currentTimeMillis() / 1000);
    }
```

Lo anterior causará que pasen 10 segundos antes que la tarea se ejecute por primera vez, posteriormente las siguientes ejecuciones se realizarán con normalidad, cada 3 segundos.

## Usando expresiones Cron

Si lo que deseamos es configurar con mayor flexibilidad la ejecución de una tarea, entonces debemos usar expresiones cron.

```java
    @Scheduled(cron = "0 9 23 ? * 5 ")
    public void tarea4() {
        System.out.println("Tarea usando expresiones cron");
    }
```

La tarea anterior se ejecutará a las 23 horas con 9 minutos y 0 segundos, todos los meses, los días 5 (viernes).

Las expresiones cron tienen 6 valores obligatorios.

1. **Segundos**. En nuestro ejemplo tiene el valor 0. Acepta valores del 0-59 y caracteres especiales como , - * / 
1. **Minutos**. En nuestro ejemplo tiene el valor 9. Acepta valores del 0-59 y caracteres especiales como , - * / 
1. **Hora**. En nuestro ejemplo tiene el valor 23. Acepta valores del 0-23 y caracteres especiales como , - * / 
1. **Día del mes**. En nuestro ejemplo tiene el caracter especial "?" el cual significa **_no definido_** ya que no deseamos que se ejecute un determinado día del mes, en su lugar deseamos que se ejecute un determinado día de la semana. Acepta valores del 1-31 y caracteres especiales como , - * ? /
1. **Mes**. En nuestro ejemplo tiene el caracter especial "*" el cuál significa **_todos_** , es decir, deseamos se ejecute todos los meses. Acepta valores del 1-12 o abreviaturas JAN-DEC y caracteres especiales como , - * /
1. **Día de la semana**. En nuestro ejemplo tiene el valor 5, es decir, deseamos se ejecute el quinto día (Viernes). Acepta valores del 1-7 o abreviaturas SUN-SAT y caracteres especiales como , - * ? /

El **día del mes** y el **día de la semana** son excluyentes, es decir que podemos definir solo uno de los dos, no ámbos. En nuestro ejemplo queremos que se ejecute siempre un día de la semana por lo tanto en la posición de día del mes asignaremos un "?" para indicar que no está definido.

El caracter especial "/" se usa para especificar incrementos. Por ejemplo en el campo de minutos, un valor como 0/1 indica que la tarea se ejecutará cada minuto, en el campo de segundos un valor como 0/15 indica una ejecución cada 15 segundos.

    Se ejecuta cada minuto de todos los dias sábados a media noche.
    @Scheduled(cron = "0 0/1 0 ? * 6 ")


El caracter especial "," se usa para especificar un conjunto de valores. Por ejemplo en el campo de día de la semana, un valor como "6,7" indica que la tarea se ejecutará todos los sábados y domingos.


    Se ejecuta cada 15 segundos los días sábados y domingos a media noche.
    @Scheduled(cron = "0/15 * 0 ? * 6,7 ")


Para una explicación con mayor detalle sobre las expresiones cron pueden consultar la [siguiente documentación](http://www.quartz-scheduler.org/documentation/quartz-2.x/tutorials/crontrigger.html) del framework Quartz. 

**Quartz** es un framework de scheduling que puede ser usado con Spring, sin embargo, en la mayoría de los casos el scheduling de spring será suficiente para nuestras tareas. Si deseamos una mayor potencia y flexibilidad como el soporte a tareas persistentes, transaccionales y distribuidas, entonces podemos considerar usar Quartz, aunque debo decir que usarlo es algo engorroso comparado con la sencillez de usar la anotación @Scheduling de Spring.




