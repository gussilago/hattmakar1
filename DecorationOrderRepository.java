package hattmakaren;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository-klass för materialbeställningar av dekorationer.
 * Klassen ansvarar för att spara och hämta decoration orders från databasen.
 */
public class DecorationOrderRepository {

    /**
     * Sparar en ny dekorationsbeställning i tabellen decoration_order.
     *
     * @param order objektet som innehåller beställningens information
     * @return true om beställningen sparades, annars false
     */
    public boolean insertDecorationOrder(DecorationOrder order) {

        // Enkel kontroll så att programmet inte försöker spara ett tomt objekt.
        if (order == null || Validator.isBlank(order.getDecorationOrderId())) {
            return false;
        }

        String sql = "INSERT INTO decoration_order (decoration_order_id, status, note) VALUES (?, ?, ?)";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Värdena sätts in säkert med PreparedStatement.
            stmt.setString(1, order.getDecorationOrderId());
            stmt.setString(2, Validator.safeText(order.getStatus()));
            stmt.setString(3, Validator.safeText(order.getNote()));

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Fel i insertDecorationOrder: " + e.getMessage());
        }

        return false;
    }

    /**
     * Sparar en rad i en dekorationsbeställning.
     * En orderrad kopplar ihop en beställning med en viss dekoration och mängd.
     *
     * @param decorationOrderId id för dekorationsbeställningen
     * @param decorationId id för dekorationen som beställs
     * @param quantityOrdered antal som beställs
     * @return true om orderraden sparades, annars false
     */
    public boolean insertDecorationOrderLine(String decorationOrderId, String decorationId, double quantityOrdered) {

        // Kontrollerar att viktiga värden finns innan databasen används.
        if (Validator.isBlank(decorationOrderId) || Validator.isBlank(decorationId) || quantityOrdered <= 0) {
            return false;
        }

        String sql = "INSERT INTO decoration_order_line "
                + "(decoration_order_id, decoration_id, quantity_ordered) "
                + "VALUES (?, ?, ?)";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, decorationOrderId);
            stmt.setString(2, decorationId);
            stmt.setDouble(3, quantityOrdered);

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Fel i insertDecorationOrderLine: " + e.getMessage());
        }

        return false;
    }

    /**
     * Hämtar alla dekorationsbeställningar och deras orderrader från databasen.
     *
     * @return en lista med DecorationOrder-objekt
     */
    public List<DecorationOrder> getAllDecorationOrders() {
        List<DecorationOrder> orders = new ArrayList<>();

        String sql = "SELECT dor.decoration_order_id, dor.status, dor.note, "
                + "dol.decoration_id, dol.quantity_ordered "
                + "FROM decoration_order dor "
                + "JOIN decoration_order_line dol "
                + "ON dor.decoration_order_id = dol.decoration_order_id "
                + "ORDER BY dor.decoration_order_id DESC";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            // Läser en rad i taget från resultatet från databasen.
            while (rs.next()) {
                DecorationOrder order = new DecorationOrder();

                order.setDecorationOrderId(rs.getString("decoration_order_id"));
                order.setDecorationId(rs.getString("decoration_id"));
                order.setQuantityOrdered(rs.getDouble("quantity_ordered"));
                order.setStatus(rs.getString("status"));
                order.setNote(rs.getString("note"));

                orders.add(order);
            }

        } catch (Exception e) {
            System.out.println("Fel i getAllDecorationOrders: " + e.getMessage());
        }

        return orders;
    }
}