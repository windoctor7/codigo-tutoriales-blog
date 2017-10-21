package windoctor7.github.io.spring5.reactive.springwebflux2;

/**
 * Created by Ascari Q. Romo Pedraza - molder.itp@gmail.com on 17/10/2017.
 */
public class Subscriber {

    public static void multiplicar(Integer n)  {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Subscriber2: "+n*n);
    }
}
