package windoctor7.github.io.alexa.skills.calculadora;

import com.amazon.ask.Skill;
import com.amazon.ask.SkillStreamHandler;
import com.amazon.ask.Skills;
import windoctor7.github.io.alexa.skills.calculadora.lambdas.*;

/**
 * Esta es la clase principal para cuando se suba a Amazon Lambda, debemos indicar esta clase como la clase principal
 * */
public class MainStreamHandler extends SkillStreamHandler {

    private static Skill getSkill() {
        return Skills.standard()
                .addRequestHandlers(
                        new SumaIntent(),
                        new CancelAndStopIntent(),
                        new HelpIntentHandler(),
                        new LaunchRequestHandler(),
                        new SessionEndedRequestHandler(),
                        new RepeatIntentHandler(),
                        new NoIntentHandler())
                .build();
    }
    public MainStreamHandler() {
        super(getSkill());

    }
}
