package windoctor7.github.io.alexa.skills.calculadora.lambdas;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.SessionEndedRequest;

import java.util.Optional;

import static com.amazon.ask.request.Predicates.requestType;

/**
 * Used to handle logic for when a session ends, i.e., when
 * the user has finished a conversation with Alexa thus ending
 * this skill's session.
 * */
public class SessionEndedRequestHandler implements RequestHandler {
    @Override
    public boolean canHandle(HandlerInput handlerInput) {
        return handlerInput.matches(requestType(SessionEndedRequest.class));
    }

    @Override
    public Optional<Response> handle(HandlerInput handlerInput) {
        //Any cleanup logic goes here
        return handlerInput.getResponseBuilder().build();
    }
}
