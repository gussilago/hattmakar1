package hattmakaren;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository-klass för dekorationer.
 * 
 * Klassen ansvarar för all databaskommunikation som gäller dekorationer.
 * Här hämtas, sparas och uppdateras dekorationer i databasen.
 */
public class DecorationRepository {

    /**
     * Hämtar alla aktiva dekorationer från databasen.
     * 
     * @return en lista med aktiva dekorationer
     */
    public List<Decoration> getAllDecorations() {
        List<Decoration> decorations = new ArrayList<>();

        String sql = "SELECT * FROM decoration WHERE is_active = true";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            // Går igenom alla rader som databasen skickar tillbaka.
            while (rs.next()) {
                decorations.add(mapDecoration(rs));
            }

        } catch (Exception e) {
            System.out.println("Fel i getAllDecorations: " + e.getMessage());
        }

        return decorations;
    }

    /**
     * Hämtar en dekoration baserat på dekorationens ID.
     * 
     * @param decorationId ID för dekorationen som ska hämtas
     * @return dekorationen om den finns, annars null
     */
    public Decoration getDecorationById(String decorationId) {
        if (Validator.isBlank(decorationId)) {
            return null;
        }

        String sql = "SELECT * FROM decoration WHERE decoration_id = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Sätter värdet på frågetecknet i SQL-frågan.
            stmt.setString(1, decorationId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapDecoration(rs);
                }
            }

        } catch (Exception e) {
            System.out.println("Fel i getDecorationById: " + e.getMessage());
        }

        return null;
    }

    /**
     * Sparar en ny dekoration i databasen.
     * 
     * @param decoration dekorationen som ska sparas
     * @return true om dekorationen sparades, annars false
     */
    public boolean insertDecoration(Decoration decoration) {
        if (decoration == null || Validator.isBlank(decoration.getDecorationId())) {
            return false;
        }

        String sql = "INSERT INTO decoration "
                + "(decoration_id, name, decoration_type, unit, unit_price, quantity_in_stock, supplier, note, is_active) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Kopplar objektets värden till SQL-frågans frågetecken.
            stmt.setString(1, decoration.getDecorationId());
            stmt.setString(2, decoration.getName());
            stmt.setString(3, decoration.getDecorationType());
            stmt.setString(4, decoration.getUnit());
            stmt.setDouble(5, decoration.getUnitPrice());
            stmt.setDouble(6, decoration.getQuantityInStock());
            stmt.setString(7, decoration.getSupplier());
            stmt.setString(8, decoration.getNote());
            stmt.setBoolean(9, decoration.isActive());

            // executeUpdate returnerar antal rader som ändrades.
            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Fel i insertDecoration: " + e.getMessage());
        }

        return false;
    }

    /**
     * Uppdaterar antal i lager för en dekoration.
     * 
     * @param decorationId ID för dekorationen
     * @param newQuantity nytt antal i lager
     * @return true om lagersaldot uppdaterades, annars false
     */
    public boolean updateQuantity(String decorationId, double newQuantity) {
        if (Validator.isBlank(decorationId)) {
            return false;
        }

        String sql = "UPDATE decoration SET quantity_in_stock = ? WHERE decoration_id = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, newQuantity);
            stmt.setString(2, decorationId);

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Fel i updateQuantity: " + e.getMessage());
        }

        return false;
    }

    /**
     * Lägger till ett antal i lagersaldot för en dekoration.
     * 
     * @param decorationId ID för dekorationen
     * @param amountToAdd antal som ska läggas till
     * @return true om lagret uppdaterades, annars false
     */
    public boolean addToStock(String decorationId, double amountToAdd) {
        Decoration decoration = getDecorationById(decorationId);

        if (decoration == null) {
            return false;
        }

        double newQuantity = decoration.getQuantityInStock() + amountToAdd;

        return updateQuantity(decorationId, newQuantity);
    }

    /**
     * Minskar lagersaldot för en dekoration.
     * 
     * Om resultatet skulle bli mindre än 0 sätts saldot till 0.
     * Detta gör att lagret inte kan visa ett negativt antal.
     * 
     * @param decorationId ID för dekorationen
     * @param amountToReduce antal som ska tas bort från lagret
     * @return true om lagret uppdaterades, annars false
     */
    public boolean reduceStock(String decorationId, double amountToReduce) {
        Decoration decoration = getDecorationById(decorationId);

        if (decoration == null) {
            return false;
        }

        double newQuantity = decoration.getQuantityInStock() - amountToReduce;

        // Hindrar att lagersaldot blir negativt.
        if (newQuantity < 0) {
            newQuantity = 0;
        }

        return updateQuantity(decorationId, newQuantity);
    }

    /**
     * Skapar ett Decoration-objekt från en rad i databasen.
     * 
     * Metoden används internt i repository-klassen för att slippa upprepa
     * samma kod varje gång en dekoration hämtas från databasen.
     * 
     * @param rs resultatet från databasen
     * @return ett Decoration-objekt med värden från databasen
     * @throws Exception om något går fel vid läsning från ResultSet
     */
    private Decoration mapDecoration(ResultSet rs) throws Exception {
        Decoration decoration = new Decoration();

        decoration.setDecorationId(rs.getString("decoration_id"));
        decoration.setName(rs.getString("name"));
        decoration.setDecorationType(rs.getString("decoration_type"));
        decoration.setUnit(rs.getString("unit"));
        decoration.setUnitPrice(rs.getDouble("unit_price"));
        decoration.setQuantityInStock(rs.getDouble("quantity_in_stock"));
        decoration.setSupplier(rs.getString("supplier"));
        decoration.setNote(rs.getString("note"));
        decoration.setActive(rs.getBoolean("is_active"));

        return decoration;
    }
}