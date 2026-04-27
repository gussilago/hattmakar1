package hattmakaren;

/**
 * Representerar en orderrad i en order.
 * 
 * En orderrad beskriver en specifik hattmodell i en order,
 * exempelvis vilken storlek kunden vill ha, antal hattar
 * och prisinformation med och utan moms.
 */
public class OrderLine {

    private int orderLineId;
    private String orderId;
    private String hatModelId;
    private String size;
    private int quantity;
    private double priceExclVat;
    private double vatAmount;
    private double priceInclVat;

    /**
     * Tom konstruktor.
     * Används när ett OrderLine-objekt ska skapas först
     * och fyllas med värden senare via setters.
     */
    public OrderLine() {
    }

    /**
     * Konstruktor som skapar en komplett orderrad direkt.
     */
    public OrderLine(int orderLineId, String orderId, String hatModelId,
                     String size, int quantity, double priceExclVat,
                     double vatAmount, double priceInclVat) {

        this.orderLineId = orderLineId;
        this.orderId = orderId;
        this.hatModelId = hatModelId;
        this.size = size;
        this.quantity = quantity;
        this.priceExclVat = priceExclVat;
        this.vatAmount = vatAmount;
        this.priceInclVat = priceInclVat;
    }

    public int getOrderLineId() {
        return orderLineId;
    }

    public void setOrderLineId(int orderLineId) {
        this.orderLineId = orderLineId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getHatModelId() {
        return hatModelId;
    }

    public void setHatModelId(String hatModelId) {
        this.hatModelId = hatModelId;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPriceExclVat() {
        return priceExclVat;
    }

    public void setPriceExclVat(double priceExclVat) {
        this.priceExclVat = priceExclVat;
    }

    public double getVatAmount() {
        return vatAmount;
    }

    public void setVatAmount(double vatAmount) {
        this.vatAmount = vatAmount;
    }

    public double getPriceInclVat() {
        return priceInclVat;
    }

    public void setPriceInclVat(double priceInclVat) {
        this.priceInclVat = priceInclVat;
    }

    /**
     * Returnerar en kort textrepresentation av orderraden.
     * Användbart om objektet visas i exempelvis en lista.
     */
    @Override
    public String toString() {
        return hatModelId + " x " + quantity;
    }
}