package hattmakaren;

/**
 * Representerar en order i systemet.
 * Klassen används för att spara information om en kundorder,
 * till exempel kund, status, totalsummor, moms, rabatt och orderdatum.
 */
public class Order {

    private String orderId;
    private String customerId;
    private String status;
    private double totalExclVat;
    private double totalVat;
    private double totalInclVat;
    private String orderDate;
    private String statusNote;
    private double discountPct;
    private double discountAmount;

    /**
     * Tom konstruktor.
     * Används när ett Order-objekt skapas först och får sina värden senare
     * via set-metoderna.
     */
    public Order() {
    }

    /**
     * Konstruktor som skapar en order med alla viktiga värden direkt.
     */
    public Order(String orderId, String customerId, String status,
                 double totalExclVat, double totalVat, double totalInclVat,
                 String orderDate, String statusNote,
                 double discountPct, double discountAmount) {

        this.orderId = orderId;
        this.customerId = customerId;
        this.status = status;
        this.totalExclVat = totalExclVat;
        this.totalVat = totalVat;
        this.totalInclVat = totalInclVat;
        this.orderDate = orderDate;
        this.statusNote = statusNote;
        this.discountPct = discountPct;
        this.discountAmount = discountAmount;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getStatus() {
        return status;
    }

    /**
     * Sätter aktuell status för ordern.
     * Exempel kan vara ny, pågående, klar eller skickad.
     */
    public void setStatus(String status) {
        this.status = status;
    }

    public double getTotalExclVat() {
        return totalExclVat;
    }

    /**
     * Sätter totalsumman exklusive moms.
     */
    public void setTotalExclVat(double totalExclVat) {
        this.totalExclVat = totalExclVat;
    }

    public double getTotalVat() {
        return totalVat;
    }

    /**
     * Sätter den beräknade momsen för ordern.
     */
    public void setTotalVat(double totalVat) {
        this.totalVat = totalVat;
    }

    public double getTotalInclVat() {
        return totalInclVat;
    }

    /**
     * Sätter totalsumman inklusive moms.
     */
    public void setTotalInclVat(double totalInclVat) {
        this.totalInclVat = totalInclVat;
    }

    public String getStatusNote() {
        return statusNote;
    }

    /**
     * Sätter en eventuell anteckning kopplad till orderns status.
     */
    public void setStatusNote(String statusNote) {
        this.statusNote = statusNote;
    }

    public String getOrderDate() {
        return orderDate;
    }

    /**
     * Sätter datumet då ordern skapades.
     */
    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public double getDiscountPct() {
        return discountPct;
    }

    /**
     * Sätter rabatt i procent.
     */
    public void setDiscountPct(double discountPct) {
        this.discountPct = discountPct;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    /**
     * Sätter rabattbeloppet i kronor.
     */
    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    /**
     * Returnerar orderns id som text.
     * Detta gör att ordern visas på ett enkelt sätt i till exempel listor.
     */
    @Override
    public String toString() {
        return orderId;
    }
}