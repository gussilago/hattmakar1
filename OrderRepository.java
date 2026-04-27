package hattmakaren;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository för ordrar.
 * 
 * Klassen ansvarar för all databaskommunikation som gäller ordrar.
 * Här finns metoder för att skapa, hämta, uppdatera och ta bort ordrar.
 */
public class OrderRepository {

    /**
     * Sparar en ny order i databasen.
     *
     * @param order ordern som ska sparas
     * @return true om ordern sparades, annars false
     */
    public boolean insertOrder(Order order) {
        String sql = "INSERT INTO customer_order "
                + "(order_id, customer_id, status, status_note, total_excl_vat, "
                + "total_vat, total_incl_vat, discount_pct, discount_amount) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Sätter in värdena i samma ordning som frågetecknen i SQL-satsen.
            stmt.setString(1, order.getOrderId());
            stmt.setString(2, order.getCustomerId());
            stmt.setString(3, order.getStatus());
            stmt.setString(4, order.getStatusNote());
            stmt.setDouble(5, order.getTotalExclVat());
            stmt.setDouble(6, order.getTotalVat());
            stmt.setDouble(7, order.getTotalInclVat());
            stmt.setDouble(8, order.getDiscountPct());
            stmt.setDouble(9, order.getDiscountAmount());

            // executeUpdate returnerar antal påverkade rader.
            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Fel i insertOrder: " + e.getMessage());
        }

        return false;
    }

    /**
     * Hämtar alla ordrar från databasen.
     *
     * @return en lista med alla ordrar
     */
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();

        String sql = "SELECT * FROM customer_order ORDER BY order_id DESC";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            // Går igenom varje rad från databasen och gör om den till ett Order-objekt.
            while (rs.next()) {
                orders.add(mapOrder(rs));
            }

        } catch (Exception e) {
            System.out.println("Fel i getAllOrders: " + e.getMessage());
        }

        return orders;
    }

    /**
     * Uppdaterar status för en viss order.
     *
     * @param orderId orderns id
     * @param newStatus den nya statusen
     * @return true om statusen uppdaterades, annars false
     */
    public boolean updateOrderStatus(String orderId, String newStatus) {
        String sql = "UPDATE customer_order SET status = ? WHERE order_id = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newStatus);
            stmt.setString(2, orderId);

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Fel i updateOrderStatus: " + e.getMessage());
        }

        return false;
    }

    /**
     * Uppdaterar både status och statuskommentar för en viss order.
     *
     * @param orderId orderns id
     * @param newStatus den nya statusen
     * @param note kommentar eller förklaring till statusen
     * @return true om ordern uppdaterades, annars false
     */
    public boolean updateOrderStatusAndNote(String orderId, String newStatus, String note) {
        String sql = "UPDATE customer_order SET status = ?, status_note = ? WHERE order_id = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newStatus);
            stmt.setString(2, note);
            stmt.setString(3, orderId);

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Fel i updateOrderStatusAndNote: " + e.getMessage());
        }

        return false;
    }

    /**
     * Tar bort en order från databasen.
     *
     * @param orderId id för ordern som ska tas bort
     * @return true om ordern togs bort, annars false
     */
    public boolean deleteOrder(String orderId) {
        String sql = "DELETE FROM customer_order WHERE order_id = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, orderId);

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Fel i deleteOrder: " + e.getMessage());
        }

        return false;
    }

    /**
     * Bygger ett Order-objekt från en rad i ResultSet.
     *
     * Metoden används internt i repositoryt för att slippa upprepa samma
     * kod varje gång en order hämtas från databasen.
     *
     * @param rs resultatet från databasen, placerat på aktuell rad
     * @return ett Order-objekt med värden från databasen
     * @throws Exception om något går fel vid läsning från ResultSet
     */
    private Order mapOrder(ResultSet rs) throws Exception {
        Order order = new Order();

        // Läser kolumner från databasen och fyller Order-objektet.
        order.setOrderId(rs.getString("order_id"));
        order.setCustomerId(rs.getString("customer_id"));
        order.setStatus(rs.getString("status"));
        order.setStatusNote(rs.getString("status_note"));
        order.setOrderDate(rs.getString("order_date"));
        order.setTotalExclVat(rs.getDouble("total_excl_vat"));
        order.setTotalVat(rs.getDouble("total_vat"));
        order.setTotalInclVat(rs.getDouble("total_incl_vat"));
        order.setDiscountPct(rs.getDouble("discount_pct"));
        order.setDiscountAmount(rs.getDouble("discount_amount"));

        return order;
    }
}