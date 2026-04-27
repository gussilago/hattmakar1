package hattmakaren;

/**
 * Hjälpklass för att räkna ut om material eller dekorationer behöver beställas.
 *
 * Klassen används när systemet ska jämföra hur mycket som har använts
 * med hur mycket som finns kvar i lager.
 */
public class MaterialNeedCalculator {

    /**
     * Kontrollerar om nytt material/dekoration bör beställas.
     *
     * Regeln är:
     * Om lagret är mindre än eller lika med hälften av det som har använts,
     * bör mer beställas.
     *
     * @param used  mängden som har använts
     * @param stock mängden som finns kvar i lager
     * @return true om mer bör beställas, annars false
     */
    public static boolean shouldOrder(double used, double stock) {
        if (used <= 0) {
            return false;
        }

        double halfOfUsed = used * 0.5;

        return stock <= halfOfUsed;
    }

    /**
     * Räknar ut hur mycket som behöver beställas.
     *
     * Om inget behöver beställas returneras 0.
     * Annars returneras skillnaden mellan använd mängd och kvarvarande lager.
     *
     * @param used  mängden som har använts
     * @param stock mängden som finns kvar i lager
     * @return mängden som behöver beställas
     */
    public static double calculateNeedToOrder(double used, double stock) {
        if (!shouldOrder(used, stock)) {
            return 0;
        }

        return used - stock;
    }

    /**
     * Kontrollerar om det finns mer än hälften kvar i lager.
     *
     * Om inget har använts returneras true, eftersom det då inte finns något
     * faktiskt behov att jämföra mot.
     *
     * @param used  mängden som har använts
     * @param stock mängden som finns kvar i lager
     * @return true om mer än hälften finns kvar, annars false
     */
    public static boolean hasMoreThanHalfLeft(double used, double stock) {
        if (used <= 0) {
            return true;
        }

        return stock > (used * 0.5);
    }
}