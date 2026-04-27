package hattmakaren;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository för orderrader.
 * Klassen ansvarar för all databaskommunikation som gäller order_line-tabellen.
 */
public class OrderLineRepository {

    /**
     * Sparar en orderrad i databasen.
     * Returnerar true om minst en rad sparades.
     */
    public boolean insertOrderLine(OrderLine orderLine) {
        String sql = "INSERT INTO order_line (order_id, hat_model_id, size, quantity, price_excl_vat, vat_amount, price_incl_vat) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Fyller frågetecknen i SQL-satsen med värden från orderraden.
            stmt.setString(1, orderLine.getOrderId());
            stmt.setString(2, orderLine.getHatModelId());
            stmt.setString(3, orderLine.getSize());
            stmt.setInt(4, orderLine.getQuantity());
            stmt.setDouble(5, orderLine.getPriceExclVat());
            stmt.setDouble(6, orderLine.getVatAmount());
            stmt.setDouble(7, orderLine.getPriceInclVat());

            // executeUpdate returnerar hur många rader som påverkades.
            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Fel i insertOrderLine: " + e.getMessage());
        }

        return false;
    }

    /**
     * Sparar en orderrad i databasen och returnerar det skapade order_line_id.
     * Returnerar -1 om orderraden inte kunde sparas.
     */
    public int insertOrderLineAndReturnId(OrderLine orderLine) {
        String sql = "INSERT INTO order_line (order_id, hat_model_id, size, quantity, price_excl_vat, vat_amount, price_incl_vat) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Fyller SQL-satsen med orderradens värden.
            stmt.setString(1, orderLine.getOrderId());
            stmt.setString(2, orderLine.getHatModelId());
            stmt.setString(3, orderLine.getSize());
            stmt.setInt(4, orderLine.getQuantity());
            stmt.setDouble(5, orderLine.getPriceExclVat());
            stmt.setDouble(6, orderLine.getVatAmount());
            stmt.setDouble(7, orderLine.getPriceInclVat());

            int affectedRows = stmt.executeUpdate();

            // Om en rad skapades hämtas det automatiskt skapade id:t.
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("Fel i insertOrderLineAndReturnId: " + e.getMessage());
        }

        return -1;
    }

    /**
     * Hämtar alla orderrader som tillhör en viss order.
     */
    public List<OrderLine> getOrderLinesByOrderId(String orderId) {
        List<OrderLine> lines = new ArrayList<>();
        String sql = "SELECT * FROM order_line WHERE order_id = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Anger vilken order som orderraderna ska hämtas för.
            stmt.setString(1, orderId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lines.add(mapOrderLine(rs));
                }
            }

        } catch (Exception e) {
            System.out.println("Fel i getOrderLinesByOrderId: " + e.getMessage());
        }

        return lines;
    }

    /**
     * Tar bort alla orderrader som hör till en viss order.
     * Returnerar true om SQL-frågan kunde köras utan fel.
     */
    public boolean deleteOrderLinesByOrderId(String orderId) {
        String sql = "DELETE FROM order_line WHERE order_id = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Anger vilken orders orderrader som ska tas bort.
            stmt.setString(1, orderId);
            stmt.executeUpdate();

            return true;

        } catch (Exception e) {
            System.out.println("Fel i deleteOrderLinesByOrderId: " + e.getMessage());
        }

        return false;
    }

    /**
     * Skapar ett OrderLine-objekt från den aktuella raden i ResultSet.
     * Detta gör att samma kod kan återanvändas när orderrader hämtas från databasen.
     */
    private OrderLine mapOrderLine(ResultSet rs) throws Exception {
        OrderLine line = new OrderLine();

        line.setOrderLineId(rs.getInt("order_line_id"));
        line.setOrderId(rs.getString("order_id"));
        line.setHatModelId(rs.getString("hat_model_id"));
        line.setSize(rs.getString("size"));
        line.setQuantity(rs.getInt("quantity"));
        line.setPriceExclVat(rs.getDouble("price_excl_vat"));
        line.setVatAmount(rs.getDouble("vat_amount"));
        line.setPriceInclVat(rs.getDouble("price_incl_vat"));

        return line;
    }
}