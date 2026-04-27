package hattmakaren;

/**
 * Representerar en hattmodell som kan säljas eller lagerföras.
 * Klassen används för att samla all grundinformation om en viss typ av hatt.
 */
public class HatModel {

    private String hatModelId;
    private String name;
    private String hatType;
    private String category;
    private String baseSizes;
    private double basePrice;
    private boolean hasPremadeFrame;
    private String copyrightNote;
    private boolean active;

    /**
     * Tom konstruktor.
     * Används när ett HatModel-objekt skapas först och fylls med värden senare.
     */
    public HatModel() {
    }

    /**
     * Konstruktor som fyller i alla fält direkt när objektet skapas.
     */
    public HatModel(String hatModelId, String name, String hatType, String category,
                    String baseSizes, double basePrice, boolean hasPremadeFrame,
                    String copyrightNote, boolean active) {

        this.hatModelId = hatModelId;
        this.name = name;
        this.hatType = hatType;
        this.category = category;
        this.baseSizes = baseSizes;
        this.basePrice = basePrice;
        this.hasPremadeFrame = hasPremadeFrame;
        this.copyrightNote = copyrightNote;
        this.active = active;
    }

    public String getHatModelId() {
        return hatModelId;
    }

    public void setHatModelId(String hatModelId) {
        this.hatModelId = hatModelId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHatType() {
        return hatType;
    }

    public void setHatType(String hatType) {
        this.hatType = hatType;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBaseSizes() {
        return baseSizes;
    }

    public void setBaseSizes(String baseSizes) {
        this.baseSizes = baseSizes;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public boolean isHasPremadeFrame() {
        return hasPremadeFrame;
    }

    public void setHasPremadeFrame(boolean hasPremadeFrame) {
        this.hasPremadeFrame = hasPremadeFrame;
    }

    public String getCopyrightNote() {
        return copyrightNote;
    }

    public void setCopyrightNote(String copyrightNote) {
        this.copyrightNote = copyrightNote;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Returnerar hattmodellens namn när objektet visas i till exempel en ComboBox eller lista.
     * Validator.safeName används för att undvika att null eller tom text visas i gränssnittet.
     */
    @Override
    public String toString() {
        return Validator.safeName(name);
    }
}