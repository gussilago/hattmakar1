package hattmakaren;

/**
 * Representerar ett material som kan användas vid tillverkning av hattar.
 * Klassen innehåller bara materialets data, till exempel namn, typ, pris,
 * lagerantal och leverantör.
 */
public class Material {

    private String materialId;
    private String name;
    private String materialType;
    private String unit;
    private double unitPrice;
    private double quantityInStock;
    private String supplier;
    private String note;
    private boolean active;

    /**
     * Tom konstruktor som gör det möjligt att skapa ett material
     * och fylla på värdena steg för steg med setters.
     */
    public Material() {
    }

    public String getMaterialId() {
        return materialId;
    }

    public void setMaterialId(String materialId) {
        this.materialId = materialId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = Validator.safeName(name);
    }

    public String getMaterialType() {
        return materialType;
    }

    public void setMaterialType(String materialType) {
        this.materialType = Validator.safeText(materialType);
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = Validator.safeText(unit);
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    /**
     * Sätter materialets pris per enhet.
     * Om ett negativt pris skickas in sparas 0 istället,
     * eftersom pris inte bör vara negativt.
     */
    public void setUnitPrice(double unitPrice) {
        if (unitPrice < 0) {
            this.unitPrice = 0;
        } else {
            this.unitPrice = unitPrice;
        }
    }

    public double getQuantityInStock() {
        return quantityInStock;
    }

    /**
     * Sätter hur mycket material som finns i lager.
     * Negativt lagerantal byts till 0 för att undvika orimliga värden.
     */
    public void setQuantityInStock(double quantityInStock) {
        if (quantityInStock < 0) {
            this.quantityInStock = 0;
        } else {
            this.quantityInStock = quantityInStock;
        }
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = Validator.safeText(supplier);
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = Validator.safeText(note);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Bestämmer hur materialet visas i till exempel listor och combo boxes.
     * Här visas materialets namn, eller ett säkert standardvärde om namnet saknas.
     */
    @Override
    public String toString() {
        return Validator.safeName(name);
    }
}