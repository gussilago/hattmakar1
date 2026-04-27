package hattmakaren;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository-klass för lagerhantering av hattar.
 * 
 * Klassen sköter all kontakt med databastabellen hat_inventory.
 * Den används för att hämta, skapa, uppdatera och ta bort lagerposter.
 */
public class HatInventoryRepository {

    /**
     * Hämtar alla lagerposter från databasen.
     *
     * @return en lista med alla lagerposter
     */
    public List<HatInventory> getAllInventory() {
        List<HatInventory> inventoryList = new ArrayList<>();

        String sql = "SELECT * FROM hat_inventory";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            // Går igenom varje rad från databasen och gör om den till ett objekt.
            while (rs.next()) {
                inventoryList.add(mapInventory(rs));
            }

        } catch (Exception e) {
            System.out.println("Fel i getAllInventory: " + e.getMessage());
        }

        return inventoryList;
    }

    /**
     * Hämtar en lagerpost baserat på hattmodell och storlek.
     *
     * @param hatModelId id för hattmodellen
     * @param size storleken på hatten
     * @return lagerposten om den finns, annars null
     */
    public HatInventory getInventoryByModelAndSize(String hatModelId, String size) {
        if (Validator.isBlank(hatModelId) || Validator.isBlank(size)) {
            return null;
        }

        String sql = "SELECT * FROM hat_inventory WHERE hat_model_id = ? AND size = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // PreparedStatement används för att säkert stoppa in värden i SQL-frågan.
            stmt.setString(1, hatModelId.trim());
            stmt.setString(2, size.trim());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapInventory(rs);
                }
            }

        } catch (Exception e) {
            System.out.println("Fel i getInventoryByModelAndSize: " + e.getMessage());
        }

        return null;
    }

    /**
     * Uppdaterar antal för en befintlig lagerpost.
     *
     * @param hatModelId id för hattmodellen
     * @param size storleken på hatten
     * @param newQuantity det nya antalet i lager
     * @return true om uppdateringen lyckades, annars false
     */
    public boolean updateQuantity(String hatModelId, String size, int newQuantity) {
        if (Validator.isBlank(hatModelId) || Validator.isBlank(size) || newQuantity < 0) {
            return false;
        }

        String sql = "UPDATE hat_inventory SET quantity = ? WHERE hat_model_id = ? AND size = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, newQuantity);
            stmt.setString(2, hatModelId.trim());
            stmt.setString(3, size.trim());

            // executeUpdate returnerar antal rader som ändrades.
            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Fel i updateQuantity: " + e.getMessage());
        }

        return false;
    }

    /**
     * Minskar lagersaldot för en viss hattmodell och storlek.
     *
     * Metoden kontrollerar först att lagerposten finns och att det finns
     * tillräckligt många hattar i lager innan antalet minskas.
     *
     * @param hatModelId id för hattmodellen
     * @param size storleken på hatten
     * @param amountToReduce antal som ska tas bort från lagret
     * @return true om lagret kunde minskas, annars false
     */
    public boolean reduceQuantity(String hatModelId, String size, int amountToReduce) {
        if (Validator.isBlank(hatModelId) || Validator.isBlank(size) || amountToReduce <= 0) {
            return false;
        }

        HatInventory inventory = getInventoryByModelAndSize(hatModelId, size);

        if (inventory == null) {
            return false;
        }

        int currentQty = inventory.getQuantity();

        // Det ska inte gå att minska lagret mer än vad som finns.
        if (currentQty < amountToReduce) {
            return false;
        }

        int newQty = currentQty - amountToReduce;

        return updateQuantity(hatModelId, size, newQty);
    }

    /**
     * Ökar lagersaldot för en viss hattmodell och storlek.
     *
     * @param hatModelId id för hattmodellen
     * @param size storleken på hatten
     * @param amountToAdd antal som ska läggas till
     * @return true om lagret kunde ökas, annars false
     */
    public boolean increaseQuantity(String hatModelId, String size, int amountToAdd) {
        if (Validator.isBlank(hatModelId) || Validator.isBlank(size) || amountToAdd <= 0) {
            return false;
        }

        HatInventory inventory = getInventoryByModelAndSize(hatModelId, size);

        if (inventory == null) {
            return false;
        }

        int currentQty = inventory.getQuantity();
        int newQty = currentQty + amountToAdd;

        return updateQuantity(hatModelId, size, newQty);
    }

    /**
     * Lägger till antal i lagret.
     *
     * Om lagerposten redan finns uppdateras antalet.
     * Om lagerposten inte finns skapas en ny rad i databasen.
     *
     * @param hatModelId id för hattmodellen
     * @param size storleken på hatten
     * @param amountToAdd antal som ska läggas till
     * @return true om operationen lyckades, annars false
     */
    public boolean addToInventory(String hatModelId, String size, int amountToAdd) {
        if (Validator.isBlank(hatModelId) || Validator.isBlank(size) || amountToAdd <= 0) {
            return false;
        }

        HatInventory existing = getInventoryByModelAndSize(hatModelId, size);

        if (existing == null) {
            HatInventory newInventory = new HatInventory();
            newInventory.setHatModelId(hatModelId.trim());
            newInventory.setSize(size.trim());
            newInventory.setQuantity(amountToAdd);

            return insertInventory(newInventory);
        }

        int newQty = existing.getQuantity() + amountToAdd;

        return updateQuantity(hatModelId, size, newQty);
    }

    /**
     * Skapar en ny lagerpost i databasen.
     *
     * @param inventory objektet som innehåller hattmodell, storlek och antal
     * @return true om raden skapades, annars false
     */
    public boolean insertInventory(HatInventory inventory) {
        if (inventory == null) {
            return false;
        }

        if (Validator.isBlank(inventory.getHatModelId())
                || Validator.isBlank(inventory.getSize())
                || inventory.getQuantity() < 0) {
            return false;
        }

        String sql = "INSERT INTO hat_inventory (hat_model_id, size, quantity) VALUES (?, ?, ?)";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, inventory.getHatModelId().trim());
            stmt.setString(2, inventory.getSize().trim());
            stmt.setInt(3, inventory.getQuantity());

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Fel i insertInventory: " + e.getMessage());
        }

        return false;
    }

    /**
     * Tar bort en lagerpost helt från databasen.
     *
     * Denna metod tar bort själva raden. Om man bara vill visa att lagret är slut
     * är det bättre att sätta quantity till 0 istället.
     *
     * @param hatModelId id för hattmodellen
     * @param size storleken på hatten
     * @return true om raden togs bort, annars false
     */
    public boolean deleteInventoryRow(String hatModelId, String size) {
        if (Validator.isBlank(hatModelId) || Validator.isBlank(size)) {
            return false;
        }

        String sql = "DELETE FROM hat_inventory WHERE hat_model_id = ? AND size = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, hatModelId.trim());
            stmt.setString(2, size.trim());

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Fel i deleteInventoryRow: " + e.getMessage());
        }

        return false;
    }

    /**
     * Skapar ett HatInventory-objekt från en rad i databasen.
     *
     * Den här metoden används för att slippa upprepa samma kod varje gång
     * en lagerpost ska byggas från ett ResultSet.
     *
     * @param rs ResultSet från SQL-frågan
     * @return ett HatInventory-objekt med värden från databasen
     * @throws Exception om något går fel när värden hämtas från ResultSet
     */
    private HatInventory mapInventory(ResultSet rs) throws Exception {
        HatInventory inventory = new HatInventory();

        inventory.setHatModelId(rs.getString("hat_model_id"));
        inventory.setSize(rs.getString("size"));
        inventory.setQuantity(rs.getInt("quantity"));

        return inventory;
    }
}