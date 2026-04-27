package hattmakaren;

/**
 * Representerar kopplingen mellan en hattmodell och ett material.
 *
 * Klassen används för att spara vilket material som behövs till en viss hattmodell,
 * hur mycket material som behövs och om materialet är ett standardmaterial för modellen.
 */
public class HatModelMaterial {

    private int id;
    private String hatModelId;
    private String materialId;
    private double quantityNeeded;
    private boolean standard;
    private String note;

    /**
     * Tom konstruktor.
     * Används när ett objekt skapas först och fylls med data senare.
     */
    public HatModelMaterial() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHatModelId() {
        return hatModelId;
    }

    public void setHatModelId(String hatModelId) {
        this.hatModelId = hatModelId;
    }

    public String getMaterialId() {
        return materialId;
    }

    public void setMaterialId(String materialId) {
        this.materialId = materialId;
    }

    public double getQuantityNeeded() {
        return quantityNeeded;
    }

    public void setQuantityNeeded(double quantityNeeded) {
        this.quantityNeeded = quantityNeeded;
    }

    public boolean isStandard() {
        return standard;
    }

    public void setStandard(boolean standard) {
        this.standard = standard;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}