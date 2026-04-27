[README.txt](https://github.com/user-attachments/files/27138184/README.txt)
Hattmakaren - Affärssystem för hattmakare

Projektet är byggt i Java Swing med MySQL-databas.
Systemet är gjort som ett stöd för hattmakaren att hantera kunder, order, hattlager, material, dekorationer och fraktsedlar.

Funktioner som finns:
- Kundhantering
- Skapa, söka, ändra och anonymisera kund
- Visa kundhistorik
- Hattlager
- Söka lagerförda hattmodeller
- Fylla på och minska lager
- Ändra pris och färdig stomme
- Orderhantering
- Skapa order av lagerförda modeller
- Skapa order av anpassade lagerförda modeller
- Flera dekorationer på samma orderrad
- Rabatt
- Expressorder
- Retur och reklamation
- Fraktsedel/följesedel
- Exportanpassad fraktsedel för utländska kunder
- Material och dekoration
- Registrera material och dekorationer
- Visa materialåtgång och dekorationsåtgång
- Markera material/dekoration som beställt
- Exportera beställningsunderlag till textfil

Funktioner som inte ingår i denna version:
- Inloggning/användarkonton
- Specialtillverkade modeller
- Planeringsfunktion
- Slumpat lösenord
- Statistik för individuell hatt
- Statistik

Så körs projektet:

1. Importera databasen
   - Öppna MySQL Workbench
   - Skapa/importera databasen från filen:
     database/hattmakaren1.sql

2. Kontrollera databasanslutningen
   - Öppna filen:
     src/hattmakaren/DB.java
   - Kontrollera databasnamn, användarnamn och lösenord.
   - Standard i projektet är:
     databas: hattmakaren1
     användare: root
     lösenord: ändras vid behov lokalt

3. Öppna projektet i NetBeans
   - File → Open Project
   - Välj projektmappen Hattmakaren

4. Kör programmet
   - Kör MainFrame.java
   - Programmet startar i huvudmenyn.

Kommentar:
Projektet är avgränsat till prioriterad kärnfunktionalitet eftersom implementationen genomförts individuellt och med begränsad tid. Fokus har därför legat på att få något fungerande huvudflöden: kund, order, lager, frakt/export samt material/dekoration.
