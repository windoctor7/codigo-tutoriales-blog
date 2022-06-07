package windoctor7.github.io.spring.kafka.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CreditProductService {

    private final CreditProductProducer producer;

    @Autowired
    public CreditProductService(CreditProductProducer producer) {
        this.producer = producer;
    }

    public String newProduct(CreditProduct product){
        //aqui logica para guardar en BD
        log.debug("Producto guardado en BD!");

        //aqui notificamos al bus que el nuevo producto ha sido creado
        producer.sendMessage(product);

        return "Producto creado exitosamente!";
    }

}
