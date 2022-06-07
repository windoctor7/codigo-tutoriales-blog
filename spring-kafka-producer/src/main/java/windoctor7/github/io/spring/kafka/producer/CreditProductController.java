package windoctor7.github.io.spring.kafka.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/credit/products")
public class CreditProductController {

    private final CreditProductService service;

    @Autowired
    public CreditProductController(CreditProductService service) {
        this.service = service;
    }

    @PostMapping
    public String createNewProduct(@RequestBody CreditProduct product){
        return service.newProduct(product);
    }

}
