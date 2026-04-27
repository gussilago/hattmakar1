package hattmakaren;

import java.util.ArrayList;
import java.util.List;

/**
 * Tillfällig order innan den sparas i databasen.
 * Klassen håller reda på vald kund, orderrader och om ordern är expressorder.
 */
public class OrderDraft {

    private Customer customer;
    private List<OrderLineDraft> lines = new ArrayList<>();
    private boolean expressOrder = false;

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<OrderLineDraft> getLines() {
        return lines;
    }

    /**
     * Lägger till en orderrad i den tillfälliga ordern.
     */
    public void addLine(OrderLineDraft line) {
        lines.add(line);
    }

    /**
     * Tömmer ordern så att en ny order kan börja byggas.
     */
    public void clear() {
        customer = null;
        lines.clear();
        expressOrder = false;
    }

    /**
     * Kontrollerar om en kund har valts för ordern.
     */
    public boolean hasCustomer() {
        return customer != null;
    }

    /**
     * Kontrollerar om ordern innehåller minst en orderrad.
     */
    public boolean hasLines() {
        return !lines.isEmpty();
    }

    /**
     * Returnerar totalpriset exklusive moms efter eventuell rabatt.
     */
    public double getTotalExclVat() {
        return PriceCalculator.calculatePriceAfterDiscount(
                getTotalExclVatBeforeDiscount(),
                getDiscountPct()
        );
    }

    /**
     * Returnerar total moms för ordern.
     */
    public double getTotalVat() {
        return PriceCalculator.calculateVat(getTotalExclVat());
    }

    /**
     * Returnerar totalpriset inklusive moms.
     */
    public double getTotalInclVat() {
        return PriceCalculator.calculatePriceInclVat(getTotalExclVat());
    }

    /**
     * Bygger en textförhandsvisning av ordern.
     * Texten kan användas för att visa ordern innan den sparas eller skrivs ut.
     */
    public String buildPreviewText() {
        StringBuilder sb = new StringBuilder();

        sb.append("BESTÄLLNING / ORDER\n");
        sb.append("====================================\n\n");

        // Lägger till kundinformation om en kund finns vald.
        if (customer != null) {
            sb.append("Kund / Customer:\n");
            sb.append(Validator.safeName(customer.getName())).append("\n");

            if (!Validator.isBlank(customer.getStreetAddress())) {
                sb.append(customer.getStreetAddress()).append("\n");
            }

            if (!Validator.isBlank(customer.getPostalCode())) {
                sb.append(customer.getPostalCode()).append(" ");
            }

            if (!Validator.isBlank(customer.getCity())) {
                sb.append(customer.getCity());
            }

            sb.append("\n");

            if (!Validator.isBlank(customer.getCountry())) {
                sb.append(customer.getCountry()).append("\n");
            }

            if (!Validator.isBlank(customer.getDeliveryAddress())) {
                sb.append("Leveransadress / Delivery address:\n");
                sb.append(customer.getDeliveryAddress()).append("\n");
            }

            sb.append("\n");
        }

        // Lägger till alla produkter i ordern.
        if (!lines.isEmpty()) {
            sb.append("Produkter / Items:\n");

            for (OrderLineDraft draftLine : lines) {
                OrderLine line = draftLine.getOrderLine();

                sb.append("- ").append(Validator.safeName(draftLine.getHatName()))
                        .append(", storlek ").append(line.getSize())
                        .append(", antal ").append(line.getQuantity())
                        .append("\n");

                if (!Validator.isBlank(draftLine.getAdjustmentText())) {
                    sb.append("  Anpassning:\n");
                    sb.append(draftLine.getAdjustmentText()).append("\n");
                }
            }

            sb.append("\n");
        }

        // Lägger till prisinformation sist i förhandsvisningen.
        sb.append("Summa före rabatt / Total before discount: ")
                .append(getTotalExclVatBeforeDiscount()).append(" SEK\n");

        if (getDiscountPct() > 0) {
            sb.append("Rabatt / Discount: ")
                    .append(getDiscountPct()).append(" % (-")
                    .append(getDiscountAmount()).append(" SEK)\n");
        }

        if (expressOrder) {
            sb.append("Expressorder: Ja\n");
            sb.append("Expressavgift: ")
                    .append(PriceCalculator.getExpressFee()).append(" SEK\n");
        }

        sb.append("Totalt exkl. moms / Total excl. VAT: ")
                .append(getTotalExclVat()).append(" SEK\n");

        sb.append("Moms / VAT: ")
                .append(getTotalVat()).append(" SEK\n");

        sb.append("Totalt inkl. moms / Total incl. VAT: ")
                .append(getTotalInclVat()).append(" SEK\n");

        return sb.toString();
    }

    /**
     * Hämtar kundens rabattprocent.
     * Om ingen kund är vald returneras 0.
     */
    public double getDiscountPct() {
        if (customer == null) {
            return 0;
        }

        return customer.getDiscountPct();
    }

    /**
     * Räknar ut rabattbeloppet i SEK.
     */
    public double getDiscountAmount() {
        return PriceCalculator.calculateDiscountAmount(
                getTotalExclVatBeforeDiscount(),
                getDiscountPct()
        );
    }

    /**
     * Räknar ut orderns totalsumma före rabatt och före moms.
     * Expressavgift läggs till här om ordern är markerad som expressorder.
     */
    public double getTotalExclVatBeforeDiscount() {
        double total = 0;

        for (OrderLineDraft draftLine : lines) {
            total += draftLine.getOrderLine().getPriceExclVat();
        }

        if (expressOrder) {
            total += PriceCalculator.getExpressFee();
        }

        return total;
    }

    public boolean isExpressOrder() {
        return expressOrder;
    }

    public void setExpressOrder(boolean expressOrder) {
        this.expressOrder = expressOrder;
    }
}