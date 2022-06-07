package windoctor7.github.io.spring.kafka.producer;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreditProduct {

    private final long id;
    private final String name;
    private final String description;

}
