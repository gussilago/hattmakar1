package hattmakaren;

/**
 * Representerar ett material som är kopplat till en specifik orderrad.
 * Klassen används när en hatt i en order kräver ett visst material,
 * samt för att spara hur mycket material som används och vad det kostar.
 */
public class OrderLineMaterial {

    private int id;
    private int orderLineId;
    private String materialId;
    private double quantityUsed;
    private double materialCost;
    private boolean ordered;

    /**
     * Tom konstruktor.
     * Används när objektet skapas först och värden sätts senare med setters.
     */
    public OrderLineMaterial() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrderLineId() {
        return orderLineId;
    }

    public void setOrderLineId(int orderLineId) {
        this.orderLineId = orderLineId;
    }

    public String getMaterialId() {
        return materialId;
    }

    public void setMaterialId(String materialId) {
        this.materialId = materialId;
    }

    public double getQuantityUsed() {
        return quantityUsed;
    }

    public void setQuantityUsed(double quantityUsed) {
        this.quantityUsed = quantityUsed;
    }

    public double getMaterialCost() {
        return materialCost;
    }

    public void setMaterialCost(double materialCost) {
        this.materialCost = materialCost;
    }

    public boolean isOrdered() {
        return ordered;
    }

    public void setOrdered(boolean ordered) {
        this.ordered = ordered;
    }
}