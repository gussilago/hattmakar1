package hattmakaren;

/**
 * Hjälpklass för enkel validering och standardtexter.
 * Klassen används för att minska upprepning i GUI-klasserna.
 */
public class Validator {

    /**
     * Privat konstruktor eftersom klassen bara innehåller statiska metoder.
     */
    private Validator() {
    }

    /**
     * Kontrollerar om en text är null eller tom.
     */
    public static boolean isBlank(String text) {
        return text == null || text.trim().isEmpty();
    }

    /**
     * Returnerar en säker text att visa.
     * Om texten saknas returneras "Ej Tillgänglig".
     */
    public static String safeText(String text) {
        if (isBlank(text)) {
            return "-";
        }
        return text.trim();
    }

    /**
     * Returnerar ett säkert namn att visa.
     * Om namnet saknas returneras "Okänt namn".
     */
    public static String safeName(String name) {
        if (isBlank(name)) {
            return "Okänt namn";
        }
        return name.trim();
    }

    /**
     * Kontrollerar om texten kan tolkas som ett heltal större än 0.
     */
    public static boolean isPositiveInt(String text) {
        try {
            int value = Integer.parseInt(text.trim());
            return value > 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Tolkar text som ett positivt heltal.
     * Metoden förutsätter att texten redan validerats.
     */
    public static int parsePositiveInt(String text) {
        return Integer.parseInt(text.trim());
    }

    /**
     * Kontrollerar om texten kan tolkas som ett giltigt decimaltal.
     */
    public static boolean isValidDouble(String text) {
        try {
            Double.parseDouble(text.trim());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Tolkar text som ett decimaltal.
     * Metoden förutsätter att texten redan validerats.
     */
    public static double parseDouble(String text) {
        return Double.parseDouble(text.trim());
    }

    /**
     * Standardmeddelande för obligatoriskt fält.
     */
    public static String requiredFieldMessage(String fieldName) {
        return fieldName + " måste fyllas i.";
    }

    /**
     * Standardmeddelande för ogiltigt heltal.
     */
    public static String invalidIntegerMessage(String fieldName) {
        return fieldName + " måste vara ett heltal.";
    }

    /**
     * Standardmeddelande för ogiltigt decimaltal.
     */
    public static String invalidDoubleMessage(String fieldName) {
        return fieldName + " måste vara ett tal.";
    }

    /**
     * Standardmeddelande för minsta tillåtna värde.
     */
    public static String minValueMessage(String fieldName, int minValue) {
        return fieldName + " måste vara minst " + minValue + ".";
    }
}