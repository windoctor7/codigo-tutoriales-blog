package windoctor7.github.io.alexa.skills.calculadora.lambdas;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;
import windoctor7.github.io.alexa.skills.calculadora.Intents;

import java.util.Optional;

import static com.amazon.ask.request.Predicates.intentName;

/**
 * Se lanza cuando decimos "Alexa detente" o "Alexa para"
 * */
public class CancelAndStopIntent implements RequestHandler {
    @Override
    public boolean canHandle(HandlerInput handlerInput) {
        return handlerInput.matches(intentName(Intents.AMAZON_STOP).or(intentName(Intents.AMAZON_CANCEL)));
    }

    @Override
    public Optional<Response> handle(HandlerInput handlerInput) {
        return handlerInput.getResponseBuilder()
                .withSpeech("Muy bien, adiosin")
                .withSimpleCard("Adiosin", "Muy bien, hasta pronto")
                .build();
    }
}
