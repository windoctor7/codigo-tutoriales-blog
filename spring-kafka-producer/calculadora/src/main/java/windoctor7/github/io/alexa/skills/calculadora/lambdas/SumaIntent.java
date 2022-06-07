package windoctor7.github.io.alexa.skills.calculadora.lambdas;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.*;
import com.amazon.ask.request.Predicates;
import windoctor7.github.io.alexa.skills.calculadora.Intents;

import java.util.Map;
import java.util.Optional;

public class SumaIntent implements RequestHandler {
    @Override
    public boolean canHandle(HandlerInput handlerInput) {
        return handlerInput.matches(Predicates.intentName(Intents.SUMA_INTENT));
    }

    @Override
    public Optional<Response> handle(HandlerInput handlerInput) {
        String speechText = "Con calculadora solo necesitas decir suma cuarenta y tres mas veinti tres por ejemplo";

        Request request = handlerInput.getRequestEnvelope().getRequest();
        IntentRequest intentRequest = (IntentRequest) request;
        Intent intent = intentRequest.getIntent();
        Map<String, Slot> slots = intent.getSlots();

        //recuperamos el valor de nuestros slots y los convertimos a enteros para sumarlos
        int uno = Integer.parseInt(slots.get("uno").getValue());
        int dos = Integer.parseInt(slots.get("dos").getValue());
        int suma = uno + dos;

        return handlerInput.getResponseBuilder()
                .withSpeech("el resultado es "+suma)
                .withSimpleCard("Calculadora", "el resultado es "+suma)
                .withReprompt(speechText)
                .build();
    }
}
