package hattmakaren;

/**
 * Hjälpklass för prisberäkningar.
 * Klassen används för att räkna ut materialkostnad, dekorationskostnad,
 * moms, rabatt, expressavgift och totalpris.
 */
public class PriceCalculator {

    public static final double VAT_RATE = 0.25;
    public static final double EXPRESS_FEE = 250.0;

    /**
     * Privat konstruktor eftersom klassen bara innehåller statiska metoder.
     */
    private PriceCalculator() {
    }

    /**
     * Räknar ut momsbeloppet utifrån ett pris exklusive moms.
     */
    public static double calculateVat(double priceExclVat) {
        return priceExclVat * VAT_RATE;
    }

    /**
     * Räknar ut pris inklusive moms.
     */
    public static double calculatePriceInclVat(double priceExclVat) {
        return priceExclVat + calculateVat(priceExclVat);
    }

    /**
     * Räknar ut priset för en anpassad hatt exklusive moms.
     * Grundpris, materialkostnad och anpassningskostnad läggs ihop.
     */
    public static double calculateCustomizedPriceExclVat(double basePrice,
                                                         double materialCost,
                                                         double adjustmentCost) {
        return basePrice + materialCost + adjustmentCost;
    }

    /**
     * Räknar ut materialkostnaden.
     */
    public static double calculateMaterialCost(double unitPrice, double quantity) {
        return unitPrice * quantity;
    }

    /**
     * Räknar ut dekorationskostnaden.
     */
    public static double calculateDecorationCost(double unitPrice, double quantity) {
        return unitPrice * quantity;
    }

    /**
     * Räknar ut priset för en orderrad exklusive moms.
     * Priset består av grundpris gånger antal samt material, dekoration och anpassning.
     */
    public static double calculateLinePriceExclVat(double basePrice,
                                                   int quantity,
                                                   double materialCost,
                                                   double decorationCost,
                                                   double adjustmentCost) {
        return (basePrice * quantity) + materialCost + decorationCost + adjustmentCost;
    }

    /**
     * Räknar ut rabattbeloppet utifrån pris exklusive moms och rabatt i procent.
     */
    public static double calculateDiscountAmount(double priceExclVat, double discountPct) {
        return priceExclVat * (discountPct / 100);
    }

    /**
     * Räknar ut priset efter rabatt.
     */
    public static double calculatePriceAfterDiscount(double priceExclVat, double discountPct) {
        return priceExclVat - calculateDiscountAmount(priceExclVat, discountPct);
    }

    /**
     * Returnerar fast expressavgift.
     */
    public static double getExpressFee() {
        return EXPRESS_FEE;
    }
}