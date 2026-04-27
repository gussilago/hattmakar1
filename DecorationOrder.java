package hattmakaren;

/**
 * Representerar en beställning av dekorationer till hattproduktionen.
 *
 * Klassen används som en enkel modellklass för att spara information om
 * vilken dekoration som ska beställas, hur stor mängd som behövs,
 * beställningens status och eventuell anteckning.
 */
public class DecorationOrder {

    private String decorationOrderId;
    private String decorationId;
    private double quantityOrdered;
    private String status;
    private String note;

    public String getDecorationOrderId() {
        return decorationOrderId;
    }

    public void setDecorationOrderId(String decorationOrderId) {
        this.decorationOrderId = decorationOrderId;
    }

    public String getDecorationId() {
        return decorationId;
    }

    public void setDecorationId(String decorationId) {
        this.decorationId = decorationId;
    }

    public double getQuantityOrdered() {
        return quantityOrdered;
    }

    public void setQuantityOrdered(double quantityOrdered) {
        this.quantityOrdered = quantityOrdered;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}