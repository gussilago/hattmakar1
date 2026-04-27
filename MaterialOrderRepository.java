package hattmakaren;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository-klass för materialbeställningar.
 * Klassen sköter databaskopplingen för att skapa och hämta materialordrar.
 */
public class MaterialOrderRepository {

    /**
     * Lägger till en ny materialbeställning i databasen.
     */
    public boolean insertMaterialOrder(MaterialOrder order) {

        if (order == null) {
            System.out.println("Fel i insertMaterialOrder: order saknas.");
            return false;
        }

        String sql = "INSERT INTO material_order (material_order_id, status, note) VALUES (?, ?, ?)";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Fyller SQL-frågans frågetecken med värden från order-objektet.
            stmt.setString(1, order.getMaterialOrderId());
            stmt.setString(2, order.getStatus());
            stmt.setString(3, order.getNote());

            // executeUpdate returnerar antal rader som påverkades.
            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Fel i insertMaterialOrder: " + e.getMessage());
        }

        return false;
    }

    /**
     * Lägger till en materialrad till en materialbeställning.
     * En materialorder kan alltså ha en eller flera rader med material.
     */
    public boolean insertMaterialOrderLine(String materialOrderId, String materialId, double quantityOrdered) {

        if (Validator.isBlank(materialOrderId)) {
            System.out.println("Fel i insertMaterialOrderLine: materialOrderId saknas.");
            return false;
        }

        if (Validator.isBlank(materialId)) {
            System.out.println("Fel i insertMaterialOrderLine: materialId saknas.");
            return false;
        }

        if (quantityOrdered <= 0) {
            System.out.println("Fel i insertMaterialOrderLine: quantityOrdered måste vara större än 0.");
            return false;
        }

        String sql = "INSERT INTO material_order_line "
                + "(material_order_id, material_id, quantity_ordered) "
                + "VALUES (?, ?, ?)";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Kopplar materialraden till rätt materialorder och rätt material.
            stmt.setString(1, materialOrderId);
            stmt.setString(2, materialId);
            stmt.setDouble(3, quantityOrdered);

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Fel i insertMaterialOrderLine: " + e.getMessage());
        }

        return false;
    }

    /**
     * Hämtar alla materialbeställningar tillsammans med deras materialrader.
     */
    public List<MaterialOrder> getAllMaterialOrders() {
        List<MaterialOrder> orders = new ArrayList<>();

        String sql = "SELECT mo.material_order_id, mo.status, mo.note, "
                + "mol.material_id, mol.quantity_ordered "
                + "FROM material_order mo "
                + "JOIN material_order_line mol "
                + "ON mo.material_order_id = mol.material_order_id "
                + "ORDER BY mo.material_order_id DESC";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            // Läser en rad i taget från resultatet och gör om raden till ett MaterialOrder-objekt.
            while (rs.next()) {
                MaterialOrder order = new MaterialOrder();

                order.setMaterialOrderId(rs.getString("material_order_id"));
                order.setMaterialId(rs.getString("material_id"));
                order.setQuantityOrdered(rs.getDouble("quantity_ordered"));
                order.setStatus(rs.getString("status"));
                order.setNote(rs.getString("note"));

                orders.add(order);
            }

        } catch (Exception e) {
            System.out.println("Fel i getAllMaterialOrders: " + e.getMessage());
        }

        return orders;
    }

    /**
     * Hämtar materialrader för en viss materialbeställning.
     * Returnerar ResultSet direkt eftersom anropande kod själv vill läsa resultatet.
     */
    public ResultSet getMaterialOrderLinesRaw(String materialOrderId, Connection conn) throws Exception {

        if (Validator.isBlank(materialOrderId)) {
            throw new IllegalArgumentException("materialOrderId saknas.");
        }

        String sql = "SELECT * FROM material_order_line WHERE material_order_id = ?";

        PreparedStatement stmt = conn.prepareStatement(sql);

        // Skyddar mot SQL-injection och sätter vilket order-id som ska hämtas.
        stmt.setString(1, materialOrderId);

        return stmt.executeQuery();
    }
}