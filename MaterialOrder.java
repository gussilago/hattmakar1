package hattmakaren;

/**
 * Representerar en materialbeställning i systemet.
 * Klassen används för att lagra information om vilket material
 * som har beställts, hur stor mängd som beställts, status på beställningen
 * samt eventuell anteckning.
 */
public class MaterialOrder {

    private String materialOrderId;
    private String materialId;
    private double quantityOrdered;
    private String status;
    private String note;

    public String getMaterialOrderId() {
        return materialOrderId;
    }

    public void setMaterialOrderId(String materialOrderId) {
        this.materialOrderId = materialOrderId;
    }

    public String getMaterialId() {
        return materialId;
    }

    public void setMaterialId(String materialId) {
        this.materialId = materialId;
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