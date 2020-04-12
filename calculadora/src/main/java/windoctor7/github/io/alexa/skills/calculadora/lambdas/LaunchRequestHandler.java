package windoctor7.github.io.alexa.skills.calculadora.lambdas;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.LaunchRequest;
import com.amazon.ask.model.Response;
import com.amazon.ask.request.Predicates;

import java.util.Optional;

/**
 * Se lanza cuando decimos "Alexa, abre calculadora"
 * */
public class LaunchRequestHandler implements RequestHandler {

    @Override
    public boolean canHandle(HandlerInput handlerInput) {
        return handlerInput.matches(Predicates.requestType(LaunchRequest.class));
    }

    @Override
    public Optional<Response> handle(HandlerInput handlerInput) {
        String speechText = "Hola raton con cola, yo puedo sumar dos números, dime cuales";
        return handlerInput.getResponseBuilder()
                .withSpeech(speechText)
                .withSimpleCard("Bienvenido", speechText)
                .withReprompt(speechText)
                .build();
    }
}