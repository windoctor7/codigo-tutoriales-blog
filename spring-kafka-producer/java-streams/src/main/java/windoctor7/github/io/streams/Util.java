package windoctor7.github.io.streams;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Creado por  Ascari Q. Romo Pedraza - molder.itp@gmail.com on 17/04/17.
 * Este es solo el codigo base usado en el workshop "Conociendo al API Stream de Java 8"
 * que puedes encontrar en mi blog
 * https://windoctor7.github.io/API-Stream-Java8.html
 *
 * Ahi se encuentran toda una serie de ejercicios y explicaciones.
 * Este codigo es solo la base, no contiene los ejercicios descritos en el blog.
 */
public class Util {

    public static List<Product> getProducts(){
        // Cargamos el archivo ubicado en la carpeta resources.
        ClassLoader classLoader = Util.class.getClassLoader();
        Scanner sc = new Scanner(classLoader.getResourceAsStream("products.csv"));

        sc.nextLine(); // comenzamos a leer a partir de la segunda linea (la primera tiene solo los titulos)
        sc.useDelimiter(","); // separamos por comas para obtener las columnas

        List<Product>  products = new ArrayList<>();

        while(sc.hasNextLine()){
            //con los metodos nextXX() obtenemos las columnas en el orden en el que se encuentran en el archivo
            Product product = new Product();
            product.setId(sc.nextInt());
            product.setName(sc.next());
            product.setSupplier(sc.nextInt());
            product.setCategory(sc.nextInt());
            sc.next(); //saltamos la columna quantityPerUnit
            product.setUnitPrice(sc.nextDouble());
            product.setUnitsInStock(sc.nextInt());

            products.add(product); // agregamos el producto a la lista.

            sc.nextLine(); // pasamos a la siguiente linea
        }

        return products;
    }
}
