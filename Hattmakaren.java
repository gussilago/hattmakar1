package hattmakaren;

/**
 * Startklass för programmet.
 * 
 * Den här klassen innehåller main-metoden och ansvarar för att starta
 * programmets huvudfönster. Själva programlogiken ligger alltså inte här,
 * utan här skapas bara MainFrame och visas för användaren.
 */
public class Hattmakaren {

    /**
     * Programmets startpunkt.
     * 
     * EventQueue.invokeLater används för att starta Swing-fönstret på rätt tråd.
     * Det är standard när man arbetar med grafiska gränssnitt i Java Swing.
     */
    public static void main(String[] args) {

        java.awt.EventQueue.invokeLater(new Runnable() {

            /**
             * Skapar och visar huvudfönstret.
             */
            @Override
            public void run() {
                new MainFrame().setVisible(true);
            }
        });
    }
}