package windoctor7.github.io.spring.kafka.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Slf4j
@Component
public class CreditProductProducer {

    private final KafkaTemplate<String, CreditProduct> kafkaTemplate;

    @Value(value = "${cloudkarafka.topic}")
    private String topicName;

    @Autowired
    public CreditProductProducer(KafkaTemplate<String, CreditProduct> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(CreditProduct creditProduct){
        ListenableFuture<SendResult<String, CreditProduct>> future = kafkaTemplate.send(topicName, "key", creditProduct);
        future.addCallback(new ListenableFutureCallback<SendResult<String, CreditProduct>>() {
            @Override
            public void onFailure(Throwable ex) {
                log.error("Ups, ocurrio un error al enviar el mensaje.", ex.getCause());
            }

            @Override
            public void onSuccess(SendResult<String, CreditProduct> result) {
                log.info("El mensaje fue enviado con éxito {}", creditProduct);
            }
        });
    }


}
