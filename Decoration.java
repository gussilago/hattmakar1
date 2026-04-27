package hattmakaren;

/**
 * Representerar en dekoration som kan användas på en hatt.
 * Klassen innehåller grundinformation om dekorationen, till exempel namn,
 * typ, pris, lagerantal och leverantör.
 */
public class Decoration {

    private String decorationId;
    private String name;
    private String decorationType;
    private String unit;
    private double unitPrice;
    private double quantityInStock;
    private String supplier;
    private String note;
    private boolean active;

    /**
     * Tom konstruktor.
     * Används när ett Decoration-objekt skapas först
     * och värden sätts in senare med set-metoderna.
     */
    public Decoration() {
    }

    public String getDecorationId() {
        return decorationId;
    }

    public void setDecorationId(String decorationId) {
        this.decorationId = decorationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDecorationType() {
        return decorationType;
    }

    public void setDecorationType(String decorationType) {
        this.decorationType = decorationType;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getQuantityInStock() {
        return quantityInStock;
    }

    public void setQuantityInStock(double quantityInStock) {
        this.quantityInStock = quantityInStock;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Returnerar en kort och säker textrepresentation av dekorationen.
     * Validator används för att undvika att null eller tomma värden visas fult.
     */
    @Override
    public String toString() {
        return Validator.safeName(name) + " (" + Validator.safeText(unit) + ")";
    }
}