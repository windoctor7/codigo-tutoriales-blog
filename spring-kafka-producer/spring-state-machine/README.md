
## Introducción
Una máquina de estados es un modelo de comportamiento que permite seguir el ciclo de vida de un objeto en un tiempo determinado. Ejemplos de máquinas de estados son una lavadora, un reproductor de música, etc. Una lavadora se encuentra en varios estados en tiempos diferentes, pesando la ropa, suministrando agua, lavando, enjuagando, exprimiendo, etc.

Para entender mejor el ejemplo que realizaremos a continuación, es muy importante tener en mente que una máquina de estados consta principalmente de:

- **Estados**. Son propiamente los estados en los que se encuentra un objeto.
- **Transiciones**. Son los cambios de un estado a otro.
- **Eventos**. Son aquellos que ocurren y disparán una transición, es decir, cuando ocurre un evento generalmente se hace una transición de un estado a otro.
- **Acciones**. Son aquellas que ocurren generalmente cuando se entra a un estado, se sale de un estado o cuando ocurre una transición.

La teoría de máquinas de estados por su puesto que es algo más complejo que lo expuesto aquí. Si quieres saber  más sobre máquinas de estados desde un punto de vista de modelos de negocio y UML puedes visitar el [siguiente tutorial](http://www.sparxsystems.com.ar/resources/tutorial/uml2_statediagram.html).

## Modelando una encuesta
Quizá no sea el ejemplo más adecuado, pero con el fin de evitar los clásicos ejemplos de una lavadora, reproductor de música o una máquina de refrescos que dificilmente implementaremos en la realidad, vamos a hacer un sencillo ejemplo de un cuestionario que inicialmente constará de solo 2 preguntas para determinar si el cliente es candidato a un préstamo.

![Image of Yaktocat](https://github.com/windoctor7/windoctor7.github.io/raw/master/static/img/diagrama_estados3.png)

Según la imagen anterior, podemos distinguir los siguientes elementos:

- Los rectángulos son los **estados**
- Las flechas son las **transiciones** e indican el cambio de un estado a otro
- Las etiquetas SI / NO resaltadas en gris son los **eventos** que deben ocurrir para llevar a cabo la transición de un estado a otro

El diagrama indica que existe en estado INICIO en donde se ejecutará la _acción_ de preguntarle al usuario si es mayor de edad. Si ocurre el _evento_ NO, habrá una _transición_ del estado INICIO al estado FIN. Si el usuario si es mayor de edad, se lanzará el _evento_ SI y habrá una _transición_ del estado INICIO al estado CANDIDATO en donde se ejecutará una _acción_ que será preguntarle al cliente si está interesado en un préstamo. Finalmente habrá una _transición_ del estado CANDIDATO al estado FIN siempre y cuando ocurra cualquiera de los dos eventos, SI o NO.


## Iniciando el proyecto
Necesitamos las siguientes dependencias,
    
    compile 'org.springframework:spring-context:4.3.7.RELEASE'
    compile 'org.springframework.statemachine:spring-statemachine-core:1.2.3.RELEASE'
    
_spring-statemachine-core_ es el módulo de spring que nos va a permitir trabajar con máquinas de estados.

Creamos una clase llamada CuestionarioEstados que herede de la clase ``EnumStateMachineConfigurerAdapter``, una clase de Spring. Este ejemplo no utiliza Spring Boot con el fin de facilitar el uso de JOptionPane. Al no usar Spring Boot necesitamos indicarle a nuestra clase de configuración los paquetes a donde spring buscará componentes, esto lo hacemos con la anotación ``@ComponentScan``. En este caso indicamos que busque en todo el paquete raíz llamado windoctor7.

```java
@Configuration
@ComponentScan("windoctor7") 
@EnableStateMachine
public class CuestionarioEstados extends EnumStateMachineConfigurerAdapter<Estados, Eventos>{
    
    @Override
    public void configure(StateMachineConfigurationConfigurer<Estados, Eventos> config) throws Exception {
        // Aquí se configura la máquina de estados
    }

    @Override
    public void configure(StateMachineStateConfigurer<Estados, Eventos> states) throws Exception {
        // Aquí se configuran los estados de la máquina
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<Estados, Eventos> transitions) throws Exception {
        // Aquí se configuran las transiciones entre estados
    }
    
}
```

Por los comentarios podemos entender la función de cada uno de los 3 métodos que proporciona ``EnumStateMachineConfigurerAdapter``. Tanto ``Estados``como ``Eventos``se tratan de simples Enums.

```java
public enum Estados {
    INICIO, CANDIDATO, FIN
}
```

```java
public enum Eventos {
    SI, NO
}
```

Lo primero que haremos es configurar la máquina de estados,

```java
@Override
public void configure(StateMachineConfigurationConfigurer<Estados, Eventos> config) throws Exception {
    config
        .withConfiguration()
        .autoStartup(true) //la máquina de estados se iniciará automáticamente al correr la aplicación
        .listener(listener()); //Un listener (escuchador) que ocurrirá en cada cambio de estado.
}
```

El listener será un método dentro de la misma clase y será el siguiente,

```java
@Bean
public StateMachineListener<Estados, Eventos> listener() {
    return new StateMachineListenerAdapter<Estados, Eventos>() {
        @Override
        public void stateChanged(State<Estados, Eventos> from, State<Estados, Eventos> to) {
            System.out.println(to.getId());
        }
    };
}
```

El método _stateChanged_ tiene dos argumentos, "from" y "to" que son el estado origen y estado destino respectivamente y el método se ejecutará siempre que ocurra un cambio de estado. En este caso solo imprimiremos el nombre del estado destino.

Lo segundo será indicar a nuestra máquina por cuantos estados estará compuesta,

```java
@Override
public void configure(StateMachineStateConfigurer<Estados, Eventos> states) throws Exception {
    states
        .withStates()
            .initial(Estados.INICIO)
            .state(Estados.INICIO,new PreguntaMayoriaEdad(),null)
            .states(EnumSet.allOf(Estados.class))

    ;
}
```

El estado INICIO es el estado inicial y propiamente un estado. Por ello aparece dos veces. Al definir a INICIO como un estado, observamos que se pasan dos argumentos adicionales al método ``state`` que corresponden a la acción entrante y a la acción de salida, esto quiere decir que es la acción que se ejecutará cada vez que entre al estado INICIO o cada vez que se salga de él. En este caso, se define como null la acción de salida ya que no deseamos ejecutar ninguna acción. Con el método ``states`` que recibe como argumento un ``Set`` de elementos podemos declarar el resto de estados para no tener que hacerlo uno por uno.

El tercer punto es definir las transiciones entre estados,

```java
@Override
public void configure(StateMachineTransitionConfigurer<Estados, Eventos> transitions) throws Exception {
    transitions
        .withExternal()
        //La transición de INICIO a CANDIDATO se lleva solo cuando ocurre el evento SI.
        //De ocurrir la transición se ejecuta la acción new CandidatoPrestamo()
            .source(Estados.INICIO).target(Estados.CANDIDATO).event(Eventos.SI).action(new CandidatoPrestamo())
            .and()
        .withExternal()
        // La transición de INICIO a FIN se lleva cuando ocurre el evento NO.
        // De ocurrir la transición se ejecuta la acción new FinEncuesta()
            .source(Estados.INICIO).target(Estados.FIN).event(Eventos.NO).action(new FinEncuesta())
            .and()
        .withExternal()
        //La transición de CANDIDATO a FIN se lleva cuando ocurre el evento SI
            .source(Estados.CANDIDATO).target(Estados.FIN).event(Eventos.SI).action(new FinEncuesta())
            .and()
        .withExternal()
            //La transición de CANDIDATO a FIN se lleva cuando ocurre el evento NO
            .source(Estados.CANDIDATO).target(Estados.FIN).event(Eventos.NO).action(new FinEncuesta())
    ;
}
```

Los comentarios en el código tratan de explicar lo que se está codificando. Estamos definiendo como serán las transiciones entre cada estado junto con el evento que las dispara y una acción a realizar cuando ocurra el cambio. Vale la pena detenerse un momento, volver a mirar el diagrama de estados expuesto arriba y compaginarlo con este código para comprender lo que estamos haciendo.

Finalmente solo nos resta definir las acciones. Por simplicidad, las acciones se han definido como clases internas de nuestra clase principal CuestionarioEstados. En ejemplos más complejos, las acciones podrían encontrarse en clases separadas.


```java
private class PreguntaMayoriaEdad implements Action<Estados, Eventos>{
    @Override
    public void execute(StateContext<Estados, Eventos> context) {
        int op = JOptionPane.showConfirmDialog(null, "Usted es MAYOR de edad?");
        context.getStateMachine().sendEvent(Eventos.values()[op]);
    }
}

private class CandidatoPrestamo implements Action<Estados,Eventos>{
    @Override
    public void execute(StateContext<Estados, Eventos> context) {
        int op = JOptionPane.showConfirmDialog(null, "Usted es candidato a un prestamo por 10,000, ¿desea aceptarlo?");
        context.getStateMachine().sendEvent(Eventos.values()[op]);
    }
}

private class FinEncuesta implements Action<Estados, Eventos>{
    @Override
    public void execute(StateContext<Estados, Eventos> context) {

        if(Estados.INICIO == context.getSource().getId())
            JOptionPane.showMessageDialog(null, "Usted no es candidato");
        else if(Estados.CANDIDATO == context.getSource().getId()){
            if (context.getEvent() == Eventos.SI)
                JOptionPane.showMessageDialog(null, "Usted SI aceptó el prestamo");
            else
                JOptionPane.showMessageDialog(null, "Usted NO aceptó el prestamo");
        }
    }
}
```

El código es muy simple y se explica casi por si solo, pero vale la pena mencionar la acción FinEncuesta. Esta acción tiene lugar cuando se pasa al estado FIN y aquí se puede procesar la petición del usuario, por ejemplo se podrían llamar a los componentes que se encargan de guardar alguna transacción en la base de datos.

El único estado donde se ejecuta la acción FinEncuesta es en el estado FIN y si ocurrió una transición directa entre el estado INICIO a FIN quiere decir que el usuario no es mayor de edad.

## Ejecutando el ejemplo

Para ejecutar el ejemplo solo definimos una clase Main en donde lanzamos el contexto de Spring indicando la clase de configuración, que en este caso es nuestra clase CuestionarioEstados que es en donde configuramos a nuestra máquina de estados.

```java
public class Main {
    public static void main(String[] args) {
        new AnnotationConfigApplicationContext(CuestionarioEstados.class);
    }
}
```

Sin duda este ejemplo pudo resolverse con unos cuantos if's de manera más simple, sin embargo lo que intentamos aquí es dar una introducción a este proyecto que sin duda alguna para ejemplos más complejos que dos preguntas resulta de gran ayuda.

## Video demostrativo

Como siempre, un video demostrando la funcionalidad de este cookbook. No olvides descargar el código fuente y dejar tus comentarios.

<iframe width="560" height="315" src="https://www.youtube.com/embed/z5wle70FXRs?rel=0?ecver=1" frameborder="0" allowfullscreen></iframe>