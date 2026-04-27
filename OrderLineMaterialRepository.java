package hattmakaren;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository-klass för material som är kopplade till orderrader.
 * Klassen ansvarar för att spara, hämta och ta bort materialrader
 * i tabellen order_line_material.
 */
public class OrderLineMaterialRepository {

    /**
     * Sparar en materialkoppling för en orderrad i databasen.
     */
    public boolean insertOrderLineMaterial(OrderLineMaterial orderLineMaterial) {
        String sql = "INSERT INTO order_line_material (order_line_id, material_id, quantity_used, material_cost, is_ordered) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Sätter värdena i samma ordning som frågetecknen i SQL-satsen.
            stmt.setInt(1, orderLineMaterial.getOrderLineId());
            stmt.setString(2, orderLineMaterial.getMaterialId());
            stmt.setDouble(3, orderLineMaterial.getQuantityUsed());
            stmt.setDouble(4, orderLineMaterial.getMaterialCost());
            stmt.setBoolean(5, orderLineMaterial.isOrdered());

            // Returnerar true om minst en rad lades till.
            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Fel i insertOrderLineMaterial: " + e.getMessage());
        }

        return false;
    }

    /**
     * Hämtar alla material som hör till en viss orderrad.
     */
    public List<OrderLineMaterial> getMaterialsByOrderLineId(int orderLineId) {
        List<OrderLineMaterial> materials = new ArrayList<>();
        String sql = "SELECT * FROM order_line_material WHERE order_line_id = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Anger vilken orderrad som ska hämtas från databasen.
            stmt.setInt(1, orderLineId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    materials.add(mapOrderLineMaterial(rs));
                }
            }

        } catch (Exception e) {
            System.out.println("Fel i getMaterialsByOrderLineId: " + e.getMessage());
        }

        return materials;
    }

    /**
     * Tar bort alla materialkopplingar som hör till en viss orderrad.
     */
    public boolean deleteMaterialsByOrderLineId(int orderLineId) {
        String sql = "DELETE FROM order_line_material WHERE order_line_id = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Anger vilken orderrads materialkopplingar som ska tas bort.
            stmt.setInt(1, orderLineId);
            stmt.executeUpdate();

            return true;

        } catch (Exception e) {
            System.out.println("Fel i deleteMaterialsByOrderLineId: " + e.getMessage());
        }

        return false;
    }

    /**
     * Hämtar alla materialkopplingar från databasen.
     */
    public List<OrderLineMaterial> getAllOrderLineMaterials() {
        List<OrderLineMaterial> list = new ArrayList<>();
        String sql = "SELECT * FROM order_line_material";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapOrderLineMaterial(rs));
            }

        } catch (Exception e) {
            System.out.println("Fel i getAllOrderLineMaterials: " + e.getMessage());
        }

        return list;
    }

    /**
     * Hämtar materialkopplingar baserat på om materialet är beställt eller inte.
     */
    public List<OrderLineMaterial> getOrderLineMaterialsByOrdered(boolean ordered) {
        List<OrderLineMaterial> list = new ArrayList<>();
        String sql = "SELECT * FROM order_line_material WHERE is_ordered = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Filtrerar resultatet på kolumnen is_ordered.
            stmt.setBoolean(1, ordered);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapOrderLineMaterial(rs));
                }
            }

        } catch (Exception e) {
            System.out.println("Fel i getOrderLineMaterialsByOrdered: " + e.getMessage());
        }

        return list;
    }

    /**
     * Bygger ett OrderLineMaterial-objekt från den aktuella raden i ResultSet.
     * Den här metoden används för att slippa upprepa samma kod i flera hämtningsmetoder.
     */
    private OrderLineMaterial mapOrderLineMaterial(ResultSet rs) throws Exception {
        OrderLineMaterial orderLineMaterial = new OrderLineMaterial();

        // Hämtar värden från databasen och lägger in dem i objektet.
        orderLineMaterial.setId(rs.getInt("id"));
        orderLineMaterial.setOrderLineId(rs.getInt("order_line_id"));
        orderLineMaterial.setMaterialId(rs.getString("material_id"));
        orderLineMaterial.setQuantityUsed(rs.getDouble("quantity_used"));
        orderLineMaterial.setMaterialCost(rs.getDouble("material_cost"));
        orderLineMaterial.setOrdered(rs.getBoolean("is_ordered"));

        return orderLineMaterial;
    }
}