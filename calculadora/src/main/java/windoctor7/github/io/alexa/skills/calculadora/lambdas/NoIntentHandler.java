package windoctor7.github.io.alexa.skills.calculadora.lambdas;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;
import com.amazon.ask.request.Predicates;
import windoctor7.github.io.alexa.skills.calculadora.Intents;

import java.util.Optional;

/**
 * A built-in intent that triggers when the user says: "no".
 * This will most likely be called when the conversation goes:
 * Alexa: "Would you like to hear the next message?"
 * User: "No."
 * Alexa: "Alright."
 * */
public class NoIntentHandler implements RequestHandler {
    @Override
    public boolean canHandle(HandlerInput handlerInput) {
        return handlerInput.matches(Predicates.intentName(Intents.AMAZON_NO));
    }

    @Override
    public Optional<Response> handle(HandlerInput handlerInput) {
        return handlerInput.getResponseBuilder()
                .withSpeech("OK")
                .build();
    }
}
