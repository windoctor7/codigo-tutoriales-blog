package windoctor7.github.io.spring.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import windoctor7.github.io.spring.kafka.producer.CreditProduct;

@Component
@Slf4j
public class CreditProductConsumer {

    @KafkaListener(topics = "${cloudkarafka.topic}")
    public void consumerMessage(CreditProduct product){
        log.info("Nuevo Producto de Credito recibido: {}", product);
    }
}
