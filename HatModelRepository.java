package hattmakaren;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository för hattmodeller.
 * Klassen ansvarar för all databaskontakt som gäller tabellen hat_model.
 */
public class HatModelRepository {

    /**
     * Hämtar alla hattmodeller från databasen.
     */
    public List<HatModel> getAllHatModels() {
        List<HatModel> hatModels = new ArrayList<>();

        String sql = "SELECT * FROM hat_model";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            // Går igenom varje rad från databasen och gör om den till ett HatModel-objekt.
            while (rs.next()) {
                hatModels.add(mapHatModel(rs));
            }

        } catch (Exception e) {
            System.out.println("Fel i getAllHatModels: " + e.getMessage());
        }

        return hatModels;
    }

    /**
     * Hämtar en hattmodell utifrån modellens ID.
     */
    public HatModel getHatModelById(String hatModelId) {
        if (Validator.isBlank(hatModelId)) {
            System.out.println("Fel i getHatModelById: hattmodellens ID saknas.");
            return null;
        }

        String sql = "SELECT * FROM hat_model WHERE hat_model_id = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Frågetecknet i SQL-satsen ersätts med valt ID.
            stmt.setString(1, hatModelId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapHatModel(rs);
                }
            }

        } catch (Exception e) {
            System.out.println("Fel i getHatModelById: " + e.getMessage());
        }

        return null;
    }

    /**
     * Sparar en ny hattmodell i databasen.
     */
    public boolean insertHatModel(HatModel hatModel) {
        if (hatModel == null) {
            System.out.println("Fel i insertHatModel: hattmodell saknas.");
            return false;
        }

        if (Validator.isBlank(hatModel.getHatModelId())) {
            System.out.println("Fel i insertHatModel: hattmodellens ID saknas.");
            return false;
        }

        if (Validator.isBlank(hatModel.getName())) {
            System.out.println("Fel i insertHatModel: hattmodellens namn saknas.");
            return false;
        }

        String sql = "INSERT INTO hat_model "
                + "(hat_model_id, name, hat_type, category, base_sizes, base_price, "
                + "has_premade_frame, copyright_note, is_active) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Värdena sätts i samma ordning som frågetecknen i SQL-satsen.
            stmt.setString(1, hatModel.getHatModelId());
            stmt.setString(2, hatModel.getName());
            stmt.setString(3, hatModel.getHatType());
            stmt.setString(4, hatModel.getCategory());
            stmt.setString(5, hatModel.getBaseSizes());
            stmt.setDouble(6, hatModel.getBasePrice());
            stmt.setBoolean(7, hatModel.isHasPremadeFrame());
            stmt.setString(8, hatModel.getCopyrightNote());
            stmt.setBoolean(9, hatModel.isActive());

            // executeUpdate returnerar antal rader som påverkades.
            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Fel i insertHatModel: " + e.getMessage());
        }

        return false;
    }

    /**
     * Uppdaterar grundpris och information om färdig stomme för en hattmodell.
     */
    public boolean updateHatModelPriceAndFrame(String hatModelId, double basePrice, boolean hasPremadeFrame) {
        if (Validator.isBlank(hatModelId)) {
            System.out.println("Fel i updateHatModelPriceAndFrame: hattmodellens ID saknas.");
            return false;
        }

        if (basePrice < 0) {
            System.out.println("Fel i updateHatModelPriceAndFrame: priset får inte vara negativt.");
            return false;
        }

        String sql = "UPDATE hat_model "
                + "SET base_price = ?, has_premade_frame = ? "
                + "WHERE hat_model_id = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, basePrice);
            stmt.setBoolean(2, hasPremadeFrame);
            stmt.setString(3, hatModelId);

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Fel i updateHatModelPriceAndFrame: " + e.getMessage());
        }

        return false;
    }

    /**
     * Bygger ett HatModel-objekt från en rad i ResultSet.
     * Denna metod används för att slippa upprepa samma kod i flera hämtmetoder.
     */
    private HatModel mapHatModel(ResultSet rs) throws Exception {
        HatModel hatModel = new HatModel();

        hatModel.setHatModelId(rs.getString("hat_model_id"));
        hatModel.setName(rs.getString("name"));
        hatModel.setHatType(rs.getString("hat_type"));
        hatModel.setCategory(rs.getString("category"));
        hatModel.setBaseSizes(rs.getString("base_sizes"));
        hatModel.setBasePrice(rs.getDouble("base_price"));
        hatModel.setHasPremadeFrame(rs.getBoolean("has_premade_frame"));
        hatModel.setCopyrightNote(rs.getString("copyright_note"));
        hatModel.setActive(rs.getBoolean("is_active"));

        return hatModel;
    }
}