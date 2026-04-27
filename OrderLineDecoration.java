package hattmakaren;

/**
 * Representerar en dekoration som hör till en specifik orderrad.
 *
 * Klassen används när en hatt i en order har extra dekorationer,
 * till exempel band, fjädrar eller andra tillval.
 */
public class OrderLineDecoration {

    private int id;
    private int orderLineId;
    private String decorationId;
    private double quantityUsed;
    private double decorationCost;

    /**
     * Tom konstruktor.
     * Används när objektet skapas först och värden sätts senare med setters.
     */
    public OrderLineDecoration() {
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

    public String getDecorationId() {
        return decorationId;
    }

    public void setDecorationId(String decorationId) {
        this.decorationId = decorationId;
    }

    public double getQuantityUsed() {
        return quantityUsed;
    }

    public void setQuantityUsed(double quantityUsed) {
        this.quantityUsed = quantityUsed;
    }

    public double getDecorationCost() {
        return decorationCost;
    }

    public void setDecorationCost(double decorationCost) {
        this.decorationCost = decorationCost;
    }
}