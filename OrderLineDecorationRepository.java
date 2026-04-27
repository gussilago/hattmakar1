package hattmakaren;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository-klass för kopplingen mellan orderrader och dekorationer.
 * Klassen ansvarar för att spara, hämta och ta bort dekorationer
 * som används på en specifik orderrad.
 */
public class OrderLineDecorationRepository {

    /**
     * Sparar en dekoration som är kopplad till en orderrad.
     *
     * @param orderLineDecoration objektet som innehåller information om dekorationen
     * @return true om posten sparades, annars false
     */
    public boolean insertOrderLineDecoration(OrderLineDecoration orderLineDecoration) {
        String sql = "INSERT INTO order_line_decoration "
                + "(order_line_id, decoration_id, quantity_used, decoration_cost) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Fyller SQL-frågans frågetecken med värden från objektet.
            stmt.setInt(1, orderLineDecoration.getOrderLineId());
            stmt.setString(2, orderLineDecoration.getDecorationId());
            stmt.setDouble(3, orderLineDecoration.getQuantityUsed());
            stmt.setDouble(4, orderLineDecoration.getDecorationCost());

            // executeUpdate returnerar antal ändrade rader.
            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Fel i insertOrderLineDecoration: " + e.getMessage());
        }

        return false;
    }

    /**
     * Hämtar alla dekorationer som hör till en viss orderrad.
     *
     * @param orderLineId id för orderraden
     * @return en lista med dekorationer för orderraden
     */
    public List<OrderLineDecoration> getDecorationsByOrderLineId(int orderLineId) {
        List<OrderLineDecoration> decorations = new ArrayList<>();

        String sql = "SELECT * FROM order_line_decoration WHERE order_line_id = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Anger vilken orderrad som ska användas i WHERE-villkoret.
            stmt.setInt(1, orderLineId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    decorations.add(mapOrderLineDecoration(rs));
                }
            }

        } catch (Exception e) {
            System.out.println("Fel i getDecorationsByOrderLineId: " + e.getMessage());
        }

        return decorations;
    }

    /**
     * Tar bort alla dekorationskopplingar för en viss orderrad.
     *
     * @param orderLineId id för orderraden
     * @return true om borttagningen kunde köras, annars false
     */
    public boolean deleteDecorationsByOrderLineId(int orderLineId) {
        String sql = "DELETE FROM order_line_decoration WHERE order_line_id = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Ser till att bara dekorationer för rätt orderrad tas bort.
            stmt.setInt(1, orderLineId);

            stmt.executeUpdate();
            return true;

        } catch (Exception e) {
            System.out.println("Fel i deleteDecorationsByOrderLineId: " + e.getMessage());
        }

        return false;
    }

    /**
     * Hämtar alla dekorationskopplingar från databasen.
     *
     * @return en lista med alla OrderLineDecoration-objekt
     */
    public List<OrderLineDecoration> getAllOrderLineDecorations() {
        List<OrderLineDecoration> list = new ArrayList<>();

        String sql = "SELECT * FROM order_line_decoration";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapOrderLineDecoration(rs));
            }

        } catch (Exception e) {
            System.out.println("Fel i getAllOrderLineDecorations: " + e.getMessage());
        }

        return list;
    }

    /**
     * Bygger ett OrderLineDecoration-objekt från en rad i ResultSet.
     * Metoden används för att slippa skriva samma kod i flera hämtningsmetoder.
     *
     * @param rs resultat från databasen
     * @return ett ifyllt OrderLineDecoration-objekt
     * @throws Exception om något går fel vid läsning från ResultSet
     */
    private OrderLineDecoration mapOrderLineDecoration(ResultSet rs) throws Exception {
        OrderLineDecoration orderLineDecoration = new OrderLineDecoration();

        // Hämtar värden från databasraden och lägger in dem i objektet.
        orderLineDecoration.setId(rs.getInt("id"));
        orderLineDecoration.setOrderLineId(rs.getInt("order_line_id"));
        orderLineDecoration.setDecorationId(rs.getString("decoration_id"));
        orderLineDecoration.setQuantityUsed(rs.getDouble("quantity_used"));
        orderLineDecoration.setDecorationCost(rs.getDouble("decoration_cost"));

        return orderLineDecoration;
    }
}