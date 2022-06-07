package windoctor7.github.io.alexa.skills.calculadora.lambdas;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;
import com.amazon.ask.request.Predicates;
import windoctor7.github.io.alexa.skills.calculadora.Intents;

import java.util.Map;
import java.util.Optional;


public class RepeatIntentHandler implements RequestHandler {

    @Override
    public boolean canHandle(HandlerInput handlerInput) {
        return handlerInput.matches(Predicates.intentName(Intents.AMAZON_REPEAT));
    }

    @Override
    public Optional<Response> handle(HandlerInput handlerInput) {
        Map<String, Object> sessionAttributes = handlerInput.getAttributesManager().getSessionAttributes();
        String response = "Ups, aun no tengo programada esta funcionalidad, disculpame";

        return handlerInput.getResponseBuilder()
                .withShouldEndSession(false)
                .withSpeech(response)
                .withSimpleCard("Ups", response)
                .build();
    }
}
