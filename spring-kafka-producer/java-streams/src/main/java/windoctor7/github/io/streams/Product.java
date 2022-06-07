package windoctor7.github.io.streams;

/**
 * Creado por  Ascari Q. Romo Pedraza - molder.itp@gmail.com on 17/04/17.
 * Este es solo el codigo base usado en el workshop "Conociendo al API Stream de Java 8"
 * que puedes encontrar en mi blog
 * https://windoctor7.github.io/API-Stream-Java8.html
 *
 * Ahi se encuentran toda una serie de ejercicios y explicaciones.
 * Este codigo es solo la base, no contiene los ejercicios descritos en el blog.
 */
public class Product implements Comparable<Product>{
    private int id;
    private String name;
    private int supplier;
    private int category;
    private double unitPrice;
    private int unitsInStock;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSupplier() {
        return supplier;
    }

    public void setSupplier(int supplier) {
        this.supplier = supplier;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getUnitsInStock() {
        return unitsInStock;
    }

    public void setUnitsInStock(int unitsInStock) {
        this.unitsInStock = unitsInStock;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", supplier=" + supplier +
                ", category=" + category +
                ", unitPrice=" + unitPrice +
                ", unitsInStock=" + unitsInStock +
                '}';
    }


    @Override
    public int compareTo(Product p) {
        if(this.getUnitsInStock() < p.getUnitsInStock())
            return -1;
        else if(this.getUnitsInStock() > p.getUnitsInStock())
            return 1;
        else
            return 0;
    }
}
