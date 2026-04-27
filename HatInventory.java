package hattmakaren;

/**
 * Representerar lagerstatus för en hattmodell i en viss storlek.
 *
 * Klassen används för att hålla reda på hur många exemplar
 * som finns i lager av en viss hattmodell och storlek.
 */
public class HatInventory {

    private String hatModelId;
    private String size;
    private int quantity;

    /**
     * Tom konstruktor.
     * Behövs när objektet skapas först och får värden senare via setters.
     */
    public HatInventory() {
    }

    /**
     * Skapar ett lagerobjekt med hattmodell, storlek och antal.
     */
    public HatInventory(String hatModelId, String size, int quantity) {
        this.hatModelId = hatModelId;
        this.size = size;
        this.quantity = quantity;
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

    /**
     * Returnerar en enkel textbeskrivning av lagerobjektet.
     * Kan användas vid utskrift, felsökning eller i enklare listor.
     */
    @Override
    public String toString() {
        return "Modell: " + hatModelId
                + ", Storlek: " + size
                + ", Antal: " + quantity;
    }
}