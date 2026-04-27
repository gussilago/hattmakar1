package hattmakaren;

/**
 * Skapar text för fraktsedel.
 * Klassen avgör också om försändelsen är svensk eller internationell.
 */
public class ShippingLabel {

    /**
     * Kontrollerar om kunden finns utanför Sverige.
     * Returnerar false om kunden eller landet saknas.
     */
    public static boolean isForeignShipment(Customer customer) {
        if (customer == null || Validator.isBlank(customer.getCountry())) {
            return false;
        }

        String country = customer.getCountry().trim().toLowerCase();

        return !country.equals("sverige")
                && !country.equals("sweden");
    }

    /**
     * Bygger rätt typ av frakttext beroende på om kunden finns i Sverige eller utomlands.
     */
    public static String buildShippingText(String orderPreviewText, Customer customer) {
        if (customer == null) {
            return "Ingen kund vald.";
        }

        if (isForeignShipment(customer)) {
            return buildInternationalShippingText(orderPreviewText, customer);
        }

        return buildSwedishShippingText(orderPreviewText, customer);
    }

    /**
     * Bygger en svensk fraktsedel.
     */
    private static String buildSwedishShippingText(String orderPreviewText, Customer customer) {
        StringBuilder sb = new StringBuilder();

        sb.append("FRAKTSEDEL\n");
        sb.append("========================================\n\n");

        sb.append("MOTTAGARE\n");
        sb.append("----------------------------------------\n");
        sb.append("Namn: ").append(Validator.safeName(customer.getName())).append("\n");
        sb.append("Gata/adress: ").append(getBestDeliveryAddress(customer)).append("\n");
        sb.append("Postnummer: ").append(Validator.safeText(customer.getPostalCode())).append("\n");
        sb.append("Stad: ").append(Validator.safeText(customer.getCity())).append("\n");
        sb.append("Land: ").append(Validator.safeText(customer.getCountry())).append("\n");

        if (!Validator.isBlank(customer.getPhone())) {
            sb.append("Telefon: ").append(customer.getPhone()).append("\n");
        }

        if (!Validator.isBlank(customer.getEmail())) {
            sb.append("E-post: ").append(customer.getEmail()).append("\n");
        }

        sb.append("\n\n");

        sb.append("AVSÄNDARE\n");
        sb.append("----------------------------------------\n");
        sb.append("Namn: Otto Hattmakaren\n");
        sb.append("Företag: Hattmakarens Verkstad\n");
        sb.append("Gata/adress: Storgatan 1\n");
        sb.append("Postnummer: 702 10\n");
        sb.append("Stad: Örebro\n");
        sb.append("Land: Sverige\n");
        sb.append("Notering: F-skatt godkänd\n");

        sb.append("\n\n");

        sb.append("ORDERUNDERLAG\n");
        sb.append("========================================\n\n");
        sb.append(Validator.safeText(orderPreviewText));

        return sb.toString();
    }

    /**
     * Bygger en internationell fraktsedel med exportinformation.
     */
    private static String buildInternationalShippingText(String orderPreviewText, Customer customer) {
        StringBuilder sb = new StringBuilder();

        sb.append("SHIPPING LABEL / EXPORT\n");
        sb.append("========================================\n\n");

        sb.append("RECIPIENT\n");
        sb.append("----------------------------------------\n");
        sb.append("Name: ").append(Validator.safeName(customer.getName())).append("\n");
        sb.append("Street/address: ").append(getBestDeliveryAddress(customer)).append("\n");
        sb.append("ZIP-code: ").append(Validator.safeText(customer.getPostalCode())).append("\n");
        sb.append("City: ").append(Validator.safeText(customer.getCity())).append("\n");
        sb.append("Country: ").append(Validator.safeText(customer.getCountry())).append("\n");

        if (!Validator.isBlank(customer.getPhone())) {
            sb.append("Phone: ").append(customer.getPhone()).append("\n");
        }

        if (!Validator.isBlank(customer.getEmail())) {
            sb.append("Email: ").append(customer.getEmail()).append("\n");
        }

        sb.append("\n\n");

        sb.append("SENDER\n");
        sb.append("----------------------------------------\n");
        sb.append("Name: Otto Hattmakaren\n");
        sb.append("Company: Hattmakarens Verkstad\n");
        sb.append("Street/address: Storgatan 1\n");
        sb.append("ZIP-code: 702 10\n");
        sb.append("City: Örebro\n");
        sb.append("Country: Sweden\n");
        sb.append("Note: Approved for Swedish F-tax\n");

        sb.append("\n\n");

        sb.append("CUSTOMS INFORMATION\n");
        sb.append("----------------------------------------\n");
        sb.append("Shipment from: Sweden\n");
        sb.append("Destination country: ").append(Validator.safeText(customer.getCountry())).append("\n");
        sb.append("Contents: Hat / headwear\n");
        sb.append("Customs code / HS code: 6505\n");
        sb.append("Purpose: Sale of goods\n");

        sb.append("\n\n");

        sb.append("ORDER INFORMATION\n");
        sb.append("========================================\n\n");
        sb.append(Validator.safeText(orderPreviewText));

        return sb.toString();
    }

    /**
     * Hämtar bästa adressen för leverans.
     * Om leveransadress finns används den, annars används kundens vanliga adress.
     */
    private static String getBestDeliveryAddress(Customer customer) {
        if (customer == null) {
            return "Okänt";
        }

        if (!Validator.isBlank(customer.getDeliveryAddress())) {
            return customer.getDeliveryAddress();
        }

        return Validator.safeText(customer.getStreetAddress());
    }
}