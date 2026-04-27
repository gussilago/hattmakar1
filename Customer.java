package hattmakaren;

/**
 * Representerar en kund i Hattmakarens system.
 *
 * Klassen innehåller grunduppgifter om kunden, till exempel namn,
 * adress, kontaktuppgifter, rabatt och om kunden är anonymiserad.
 */
public class Customer {

    private String customerId;
    private String customerType;
    private String name;
    private String orgNumber;

    private String deliveryAddress;
    private String streetAddress;
    private String postalCode;
    private String city;
    private String country;

    private String email;
    private String phone;

    private double discountPct;
    private String paymentNotes;
    private boolean anonymized;

    /**
     * Tom konstruktor.
     *
     * Används när ett Customer-objekt skapas först och fylls med värden senare,
     * till exempel via setters eller från databasen.
     */
    public Customer() {
    }

    /**
     * Konstruktor som skapar en kund med alla uppgifter direkt.
     *
     * Används när all kundinformation redan finns tillgänglig.
     */
    public Customer(String customerId, String customerType, String name, String orgNumber,
                    String deliveryAddress, String streetAddress, String postalCode,
                    String city, String country, String email, String phone,
                    double discountPct, String paymentNotes, boolean anonymized) {

        this.customerId = customerId;
        this.customerType = customerType;
        this.name = name;
        this.orgNumber = orgNumber;

        this.deliveryAddress = deliveryAddress;
        this.streetAddress = streetAddress;
        this.postalCode = postalCode;
        this.city = city;
        this.country = country;

        this.email = email;
        this.phone = phone;

        this.discountPct = discountPct;
        this.paymentNotes = paymentNotes;
        this.anonymized = anonymized;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrgNumber() {
        return orgNumber;
    }

    public void setOrgNumber(String orgNumber) {
        this.orgNumber = orgNumber;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public double getDiscountPct() {
        return discountPct;
    }

    public void setDiscountPct(double discountPct) {
        this.discountPct = discountPct;
    }

    public String getPaymentNotes() {
        return paymentNotes;
    }

    public void setPaymentNotes(String paymentNotes) {
        this.paymentNotes = paymentNotes;
    }

    public boolean isAnonymized() {
        return anonymized;
    }

    public void setAnonymized(boolean anonymized) {
        this.anonymized = anonymized;
    }

    /**
     * Bestämmer hur kunden ska visas som text i programmet.
     *
     * Detta används till exempel om Customer-objekt visas i en lista
     * eller i en combobox. Validator.safeName används för att undvika
     * att ett tomt eller null-värde visas på ett dåligt sätt.
     */
    @Override
    public String toString() {
        return Validator.safeName(name);
    }
}