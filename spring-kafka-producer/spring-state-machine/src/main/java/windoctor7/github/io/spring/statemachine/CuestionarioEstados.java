package windoctor7.github.io.spring.statemachine;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import javax.swing.*;
import java.util.EnumSet;

/**
 * Creado por  Ascari Q. Romo Pedraza - molder.itp@gmail.com on 12/04/17.
 */
@Configuration
@ComponentScan("windoctor7")
@EnableStateMachine
public class CuestionarioEstados extends EnumStateMachineConfigurerAdapter<Estados, Eventos>{

    @Override
    public void configure(StateMachineConfigurationConfigurer<Estados, Eventos> config) throws Exception {
        config
            .withConfiguration()
            .autoStartup(true) //la máquina de estados se iniciará automáticamente al correr la aplicación
            .listener(listener());
    }

    @Override
    public void configure(StateMachineStateConfigurer<Estados, Eventos> states) throws Exception {
        states
            .withStates()
                .initial(Estados.INICIO)
                .state(Estados.INICIO,new PreguntaMayoriaEdad(),null)
                .states(EnumSet.allOf(Estados.class))

        ;
    }

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

    @Bean
    public StateMachineListener<Estados, Eventos> listener() {
        return new StateMachineListenerAdapter<Estados, Eventos>() {
            @Override
            public void stateChanged(State<Estados, Eventos> from, State<Estados, Eventos> to) {
                System.out.println(to.getId());
            }
        };
    }

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
}
