package hattmakaren;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository-klass för kunder.
 *
 * Klassen ansvarar för all databaskommunikation som gäller kunder.
 * Här finns metoder för att hämta, söka, lägga till, uppdatera
 * och anonymisera kunder.
 */
public class CustomerRepository {

    /**
     * Hämtar alla kunder från databasen.
     *
     * @return en lista med alla kunder som finns i tabellen customer
     */
    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();

        String sql = "SELECT * FROM customer";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            // Går igenom varje rad från databasen och gör om den till ett Customer-objekt.
            while (rs.next()) {
                customers.add(mapCustomer(rs));
            }

        } catch (Exception e) {
            System.out.println("Fel i getAllCustomers: " + e.getMessage());
        }

        return customers;
    }

    /**
     * Hämtar en specifik kund utifrån kundens ID.
     *
     * @param customerId kundens ID
     * @return kunden om den finns, annars null
     */
    public Customer getCustomerById(String customerId) {
        if (Validator.isBlank(customerId)) {
            return null;
        }

        String sql = "SELECT * FROM customer WHERE customer_id = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Frågetecknet i SQL-frågan ersätts med kundens ID.
            stmt.setString(1, customerId.trim());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapCustomer(rs);
                }
            }

        } catch (Exception e) {
            System.out.println("Fel i getCustomerById: " + e.getMessage());
        }

        return null;
    }

    /**
     * Söker efter kunder vars namn matchar söktexten.
     *
     * @param name namnet eller del av namnet som ska sökas efter
     * @return en lista med kunder som matchar sökningen
     */
    public List<Customer> searchCustomersByName(String name) {
        List<Customer> customers = new ArrayList<>();

        if (Validator.isBlank(name)) {
            return customers;
        }

        String sql = "SELECT * FROM customer WHERE name LIKE ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Procenttecknen gör att sökningen matchar även delar av namnet.
            stmt.setString(1, "%" + name.trim() + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    customers.add(mapCustomer(rs));
                }
            }

        } catch (Exception e) {
            System.out.println("Fel i searchCustomersByName: " + e.getMessage());
        }

        return customers;
    }

    /**
     * Sparar en ny kund i databasen.
     *
     * @param customer kunden som ska sparas
     * @return true om kunden sparades, annars false
     */
    public boolean insertCustomer(Customer customer) {
        if (customer == null || Validator.isBlank(customer.getCustomerId())) {
            return false;
        }

        String sql = "INSERT INTO customer "
                + "(customer_id, customer_type, name, org_number, delivery_address, "
                + "street_address, postal_code, city, country, email, phone, "
                + "discount_pct, payment_notes, is_anonymized) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Varje set-metod fyller ett frågetecken i SQL-satsen, i rätt ordning.
            stmt.setString(1, customer.getCustomerId());
            stmt.setString(2, customer.getCustomerType());
            stmt.setString(3, customer.getName());
            stmt.setString(4, customer.getOrgNumber());
            stmt.setString(5, customer.getDeliveryAddress());
            stmt.setString(6, customer.getStreetAddress());
            stmt.setString(7, customer.getPostalCode());
            stmt.setString(8, customer.getCity());
            stmt.setString(9, customer.getCountry());
            stmt.setString(10, customer.getEmail());
            stmt.setString(11, customer.getPhone());
            stmt.setDouble(12, customer.getDiscountPct());
            stmt.setString(13, customer.getPaymentNotes());
            stmt.setBoolean(14, customer.isAnonymized());

            // executeUpdate returnerar antal rader som påverkades.
            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Fel i insertCustomer: " + e.getMessage());
        }

        return false;
    }

    /**
     * Uppdaterar en befintlig kund i databasen.
     *
     * @param customer kunden med nya värden
     * @return true om kunden uppdaterades, annars false
     */
    public boolean updateCustomer(Customer customer) {
        if (customer == null || Validator.isBlank(customer.getCustomerId())) {
            return false;
        }

        String sql = "UPDATE customer SET "
                + "customer_type = ?, name = ?, org_number = ?, "
                + "delivery_address = ?, street_address = ?, postal_code = ?, "
                + "city = ?, country = ?, email = ?, phone = ?, "
                + "discount_pct = ?, payment_notes = ?, is_anonymized = ? "
                + "WHERE customer_id = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customer.getCustomerType());
            stmt.setString(2, customer.getName());
            stmt.setString(3, customer.getOrgNumber());
            stmt.setString(4, customer.getDeliveryAddress());
            stmt.setString(5, customer.getStreetAddress());
            stmt.setString(6, customer.getPostalCode());
            stmt.setString(7, customer.getCity());
            stmt.setString(8, customer.getCountry());
            stmt.setString(9, customer.getEmail());
            stmt.setString(10, customer.getPhone());
            stmt.setDouble(11, customer.getDiscountPct());
            stmt.setString(12, customer.getPaymentNotes());
            stmt.setBoolean(13, customer.isAnonymized());

            // Sista parametern används i WHERE-delen för att hitta rätt kund.
            stmt.setString(14, customer.getCustomerId());

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Fel i updateCustomer: " + e.getMessage());
        }

        return false;
    }

    /**
     * Anonymiserar en kund.
     *
     * Metoden tar bort personliga uppgifter men behåller kundraden,
     * vilket kan vara användbart om orderhistorik fortfarande måste finnas kvar.
     *
     * @param customerId ID för kunden som ska anonymiseras
     * @return true om kunden anonymiserades, annars false
     */
    public boolean anonymizeCustomer(String customerId) {
        if (Validator.isBlank(customerId)) {
            return false;
        }

        String sql = "UPDATE customer SET "
                + "customer_type = 'private', "
                + "name = 'Anonymiserad kund', "
                + "org_number = NULL, "
                + "delivery_address = NULL, "
                + "street_address = NULL, "
                + "postal_code = NULL, "
                + "city = NULL, "
                + "country = NULL, "
                + "email = NULL, "
                + "phone = NULL, "
                + "discount_pct = 0, "
                + "payment_notes = NULL, "
                + "is_anonymized = true "
                + "WHERE customer_id = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customerId.trim());

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Fel i anonymizeCustomer: " + e.getMessage());
        }

        return false;
    }

    /**
     * Hämtar alla ordrar som hör till en viss kund.
     *
     * @param customerId kundens ID
     * @return en lista med kundens ordrar
     */
    public List<Order> getOrdersByCustomerId(String customerId) {
        List<Order> orders = new ArrayList<>();

        if (Validator.isBlank(customerId)) {
            return orders;
        }

        String sql = "SELECT * FROM customer_order "
                + "WHERE customer_id = ? "
                + "ORDER BY order_date DESC";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customerId.trim());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Order order = new Order();

                    // Här byggs ett Order-objekt upp från databasen.
                    order.setOrderId(rs.getString("order_id"));
                    order.setCustomerId(rs.getString("customer_id"));
                    order.setStatus(rs.getString("status"));
                    order.setTotalExclVat(rs.getDouble("total_excl_vat"));
                    order.setTotalVat(rs.getDouble("total_vat"));
                    order.setTotalInclVat(rs.getDouble("total_incl_vat"));
                    order.setStatusNote(rs.getString("status_note"));

                    orders.add(order);
                }
            }

        } catch (Exception e) {
            System.out.println("Fel i getOrdersByCustomerId: " + e.getMessage());
        }

        return orders;
    }

    /**
     * Skapar ett Customer-objekt från en rad i databasen.
     *
     * Metoden används internt i repository-klassen för att undvika
     * att samma kod behöver upprepas i flera olika hämtningsmetoder.
     *
     * @param rs resultat från databasen
     * @return ett Customer-objekt med värden från aktuell rad
     * @throws Exception om något går fel vid läsning från ResultSet
     */
    private Customer mapCustomer(ResultSet rs) throws Exception {
        Customer customer = new Customer();

        customer.setCustomerId(rs.getString("customer_id"));
        customer.setCustomerType(rs.getString("customer_type"));
        customer.setName(rs.getString("name"));
        customer.setOrgNumber(rs.getString("org_number"));
        customer.setDeliveryAddress(rs.getString("delivery_address"));
        customer.setStreetAddress(rs.getString("street_address"));
        customer.setPostalCode(rs.getString("postal_code"));
        customer.setCity(rs.getString("city"));
        customer.setCountry(rs.getString("country"));
        customer.setEmail(rs.getString("email"));
        customer.setPhone(rs.getString("phone"));
        customer.setDiscountPct(rs.getDouble("discount_pct"));
        customer.setPaymentNotes(rs.getString("payment_notes"));
        customer.setAnonymized(rs.getBoolean("is_anonymized"));

        return customer;
    }
}