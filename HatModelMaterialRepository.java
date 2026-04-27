package hattmakaren;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository-klass för kopplingen mellan hattmodeller och material.
 * Klassen ansvarar för att spara, hämta, uppdatera och ta bort data
 * från databastabeller som rör material till hattmodeller.
 */
public class HatModelMaterialRepository {

    /**
     * Sparar en materialkoppling för en hattmodell i databasen.
     *
     * @param item objektet som innehåller information om hattmodell, material och mängd
     * @return true om raden sparades, annars false
     */
    public boolean insertHatModelMaterial(HatModelMaterial item) {
        String sql = "INSERT INTO hat_model_material "
                + "(hat_model_id, material_id, quantity_needed, is_standard, note) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Sätter värdena i SQL-frågan i samma ordning som frågetecknen.
            stmt.setString(1, item.getHatModelId());
            stmt.setString(2, item.getMaterialId());
            stmt.setDouble(3, item.getQuantityNeeded());
            stmt.setBoolean(4, item.isStandard());
            stmt.setString(5, item.getNote());

            // executeUpdate returnerar antal rader som påverkats.
            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Fel i insertHatModelMaterial: " + e.getMessage());
        }

        return false;
    }

    /**
     * Hämtar alla materialkopplingar som tillhör en viss hattmodell.
     *
     * @param hatModelId id för hattmodellen
     * @return lista med materialkopplingar för hattmodellen
     */
    public List<HatModelMaterial> getMaterialsByHatModelId(String hatModelId) {
        List<HatModelMaterial> list = new ArrayList<>();
        String sql = "SELECT * FROM hat_model_material WHERE hat_model_id = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, hatModelId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapHatModelMaterial(rs));
                }
            }

        } catch (Exception e) {
            System.out.println("Fel i getMaterialsByHatModelId: " + e.getMessage());
        }

        return list;
    }

    /**
     * Hämtar endast standardmaterial för en viss hattmodell.
     *
     * @param hatModelId id för hattmodellen
     * @return lista med standardmaterial för hattmodellen
     */
    public List<HatModelMaterial> getStandardMaterialsByHatModelId(String hatModelId) {
        List<HatModelMaterial> list = new ArrayList<>();
        String sql = "SELECT * FROM hat_model_material "
                + "WHERE hat_model_id = ? AND is_standard = true";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, hatModelId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapHatModelMaterial(rs));
                }
            }

        } catch (Exception e) {
            System.out.println("Fel i getStandardMaterialsByHatModelId: " + e.getMessage());
        }

        return list;
    }

    /**
     * Tar bort en materialkoppling från databasen.
     *
     * @param id id för materialkopplingen som ska tas bort
     * @return true om raden togs bort, annars false
     */
    public boolean deleteHatModelMaterial(int id) {
        String sql = "DELETE FROM hat_model_material WHERE id = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Fel i deleteHatModelMaterial: " + e.getMessage());
        }

        return false;
    }

    /**
     * Uppdaterar om ett material i en orderrad är beställt eller inte.
     *
     * @param id id för orderradens material
     * @param ordered true om materialet är beställt, annars false
     * @return true om raden uppdaterades, annars false
     */
    public boolean updateOrderedStatus(int id, boolean ordered) {
        String sql = "UPDATE order_line_material SET is_ordered = ? WHERE id = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, ordered);
            stmt.setInt(2, id);

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Fel i updateOrderedStatus: " + e.getMessage());
        }

        return false;
    }

    /**
     * Skapar ett HatModelMaterial-objekt från en rad i databasen.
     * Detta gör att samma kod inte behöver upprepas i alla SELECT-metoder.
     *
     * @param rs resultatet från databasen
     * @return ett färdigt HatModelMaterial-objekt
     * @throws Exception om något går fel när värden hämtas från ResultSet
     */
    private HatModelMaterial mapHatModelMaterial(ResultSet rs) throws Exception {
        HatModelMaterial item = new HatModelMaterial();

        item.setId(rs.getInt("id"));
        item.setHatModelId(rs.getString("hat_model_id"));
        item.setMaterialId(rs.getString("material_id"));
        item.setQuantityNeeded(rs.getDouble("quantity_needed"));
        item.setStandard(rs.getBoolean("is_standard"));
        item.setNote(rs.getString("note"));

        return item;
    }
}