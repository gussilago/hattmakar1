package hattmakaren;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository-klass för material.
 *
 * Klassen ansvarar för all databaskommunikation som gäller material.
 * Den kan hämta material, lägga till nytt material och uppdatera lagersaldo.
 */
public class MaterialRepository {

    /**
     * Hämtar alla aktiva material från databasen.
     *
     * Endast material där is_active är true hämtas.
     */
    public List<Material> getAllMaterials() {
        List<Material> materials = new ArrayList<>();

        String sql = "SELECT * FROM material WHERE is_active = true";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            // Går igenom varje rad från databasen och gör om den till ett Material-objekt.
            while (rs.next()) {
                materials.add(mapMaterial(rs));
            }

        } catch (Exception e) {
            System.out.println("Fel i getAllMaterials: " + e.getMessage());
        }

        return materials;
    }

    /**
     * Hämtar ett material från databasen med hjälp av materialets ID.
     *
     * Returnerar null om materialet inte hittas eller om ID saknas.
     */
    public Material getMaterialById(String materialId) {
        if (Validator.isBlank(materialId)) {
            return null;
        }

        String sql = "SELECT * FROM material WHERE material_id = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Ersätter frågetecknet i SQL-satsen med materialets ID.
            stmt.setString(1, materialId.trim());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapMaterial(rs);
                }
            }

        } catch (Exception e) {
            System.out.println("Fel i getMaterialById: " + e.getMessage());
        }

        return null;
    }

    /**
     * Sparar ett nytt material i databasen.
     *
     * Returnerar true om materialet sparades korrekt.
     * Returnerar false om något gick fel.
     */
    public boolean insertMaterial(Material material) {
        if (material == null || Validator.isBlank(material.getMaterialId())) {
            return false;
        }

        String sql = "INSERT INTO material "
                + "(material_id, name, material_type, unit, unit_price, quantity_in_stock, supplier, note, is_active) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Fyller SQL-satsens frågetecken med värden från Material-objektet.
            stmt.setString(1, material.getMaterialId().trim());
            stmt.setString(2, material.getName());
            stmt.setString(3, material.getMaterialType());
            stmt.setString(4, material.getUnit());
            stmt.setDouble(5, material.getUnitPrice());
            stmt.setDouble(6, material.getQuantityInStock());
            stmt.setString(7, material.getSupplier());
            stmt.setString(8, material.getNote());
            stmt.setBoolean(9, material.isActive());

            // executeUpdate returnerar antal ändrade rader. Mer än 0 betyder att insert lyckades.
            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Fel i insertMaterial: " + e.getMessage());
        }

        return false;
    }

    /**
     * Uppdaterar lagersaldot för ett material.
     *
     * Metoden skriver över det gamla saldot med det nya värdet.
     */
    public boolean updateQuantity(String materialId, double newQuantity) {
        if (Validator.isBlank(materialId)) {
            return false;
        }

        String sql = "UPDATE material SET quantity_in_stock = ? WHERE material_id = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, newQuantity);
            stmt.setString(2, materialId.trim());

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Fel i updateQuantity: " + e.getMessage());
        }

        return false;
    }

    /**
     * Lägger till ett antal i materialets nuvarande lagersaldo.
     *
     * Först hämtas materialet från databasen.
     * Sedan räknas det nya saldot ut och sparas.
     */
    public boolean addToStock(String materialId, double amountToAdd) {
        if (Validator.isBlank(materialId)) {
            return false;
        }

        Material material = getMaterialById(materialId);

        if (material == null) {
            return false;
        }

        double newQuantity = material.getQuantityInStock() + amountToAdd;

        return updateQuantity(materialId, newQuantity);
    }

    /**
     * Minskar ett antal från materialets nuvarande lagersaldo.
     *
     * Om saldot skulle bli mindre än 0 sätts saldot till 0.
     * Detta gör att lagret inte kan få ett negativt värde.
     */
    public boolean reduceStock(String materialId, double amountToReduce) {
        if (Validator.isBlank(materialId)) {
            return false;
        }

        Material material = getMaterialById(materialId);

        if (material == null) {
            return false;
        }

        double newQuantity = material.getQuantityInStock() - amountToReduce;

        // Lager ska inte kunna bli negativt.
        if (newQuantity < 0) {
            newQuantity = 0;
        }

        return updateQuantity(materialId, newQuantity);
    }

    /**
     * Skapar ett Material-objekt från en rad i databasen.
     *
     * ResultSet innehåller databasens svar.
     * Varje kolumn läses ut och läggs in i ett Material-objekt.
     */
    private Material mapMaterial(ResultSet rs) throws Exception {
        Material material = new Material();

        material.setMaterialId(rs.getString("material_id"));
        material.setName(rs.getString("name"));
        material.setMaterialType(rs.getString("material_type"));
        material.setUnit(rs.getString("unit"));
        material.setUnitPrice(rs.getDouble("unit_price"));
        material.setQuantityInStock(rs.getDouble("quantity_in_stock"));
        material.setSupplier(rs.getString("supplier"));
        material.setNote(rs.getString("note"));
        material.setActive(rs.getBoolean("is_active"));

        return material;
    }
}