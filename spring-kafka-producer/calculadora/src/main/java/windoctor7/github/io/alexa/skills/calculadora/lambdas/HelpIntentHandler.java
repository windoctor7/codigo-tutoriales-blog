package windoctor7.github.io.alexa.skills.calculadora.lambdas;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;
import com.amazon.ask.request.Predicates;
import windoctor7.github.io.alexa.skills.calculadora.Intents;

import java.util.Optional;

/**
 * Se lanza cuando decimos "ayuda" o "ayudame"
 * */
public class HelpIntentHandler implements RequestHandler {
    @Override
    public boolean canHandle(HandlerInput handlerInput) {
        return handlerInput.matches(Predicates.intentName(Intents.AMAZON_HELP));
    }

    @Override
    public Optional<Response> handle(HandlerInput handlerInput) {
        String speechText = "Con calculadora solo necesitas decir suma cuarenta y tres mas veinti tres por ejemplo";
        return handlerInput.getResponseBuilder()
                .withSpeech(speechText)
                .withSimpleCard("Calculadora", speechText)
                .withReprompt(speechText)
                .build();
    }
}
