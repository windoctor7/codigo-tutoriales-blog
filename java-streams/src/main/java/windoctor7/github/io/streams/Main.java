package windoctor7.github.io.streams;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Creado por  Ascari Q. Romo Pedraza - molder.itp@gmail.com on 17/04/17.
 * Este es solo el codigo base usado en el workshop "Conociendo al API Stream de Java 8"
 * que puedes encontrar en mi blog
 * https://windoctor7.github.io/API-Stream-Java8.html
 *
 * Ahi se encuentran toda una serie de ejercicios y explicaciones.
 * Este codigo es solo la base, no contiene los ejercicios descritos en el blog.
 */
public class Main {

    public List<String> sorted(){
        List<Product> products = Util.getProducts();
        Stream<String> streams = products.stream()
                .map(Product::getName)
                ;

        return streams.collect(Collectors.toList());
    }

    public static void main(String[] args) {
        Main main = new Main();
        main.sorted().forEach(System.out::println);
    }

}
