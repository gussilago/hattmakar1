package hattmakaren;

/**
 * Fönster för kundhantering.
 * Här kan användaren söka fram kunder, ändra kunduppgifter,
 * anonymisera kunder och visa kundhistorik.
 */
public class CustomerFrame extends javax.swing.JFrame {

    /**
     * Skapar kundfönstret, placerar det mitt på skärmen
     * och laddar in kundtabellen direkt när fönstret öppnas.
     */
    public CustomerFrame() {
        initComponents();
        setSize(1920, 1080);
        setLocationRelativeTo(null);

        setupCustomerTable();
        searchCustomers();
    }

    /**
     * Skapar tabellens kolumner och gör så att användaren
     * kan markera rader men inte ändra direkt i tabellen.
     */
    private void setupCustomerTable() {
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(
                new Object[]{
                    "Kund-ID",
                    "Typ",
                    "Namn",
                    "Epost",
                    "Telefon",
                    "Gata",
                    "Postkod",
                    "Stad",
                    "Land",
                    "Rabatt",
                    "Anonym"
                },
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        jTable1.setModel(model);

        // Gör att användaren väljer hela rader, inte enskilda celler.
        jTable1.setRowSelectionAllowed(true);
        jTable1.setColumnSelectionAllowed(false);
        jTable1.setCellSelectionEnabled(false);
    }

    /**
     * Laddar in en lista med kunder i tabellen.
     * Tabellen töms först så att gamla sökresultat inte ligger kvar.
     */
    private void loadCustomersToTable(java.util.List<Customer> customers) {
        javax.swing.table.DefaultTableModel model =
                (javax.swing.table.DefaultTableModel) jTable1.getModel();

        // Tömmer tabellen innan nya kundrader läggs in.
        model.setRowCount(0);

        for (Customer c : customers) {
            model.addRow(new Object[]{
                c.getCustomerId(),
                Validator.safeText(c.getCustomerType()),
                Validator.safeName(c.getName()),
                Validator.safeText(c.getEmail()),
                Validator.safeText(c.getPhone()),
                Validator.safeText(c.getStreetAddress()),
                Validator.safeText(c.getPostalCode()),
                Validator.safeText(c.getCity()),
                Validator.safeText(c.getCountry()),
                c.getDiscountPct(),
                c.isAnonymized() ? "Ja" : "Nej"
            });
        }
    }

    /**
     * Söker efter kunder.
     * Om sökfältet är tomt visas alla kunder.
     * Om användaren har skrivit något söks kunder fram via namn.
     */
    private void searchCustomers() {
        CustomerRepository repo = new CustomerRepository();
        String searchText = txtSearch.getText().trim();

        java.util.List<Customer> customers;

        if (Validator.isBlank(searchText)) {
            customers = repo.getAllCustomers();
        } else {
            customers = repo.searchCustomersByName(searchText);
        }

        loadCustomersToTable(customers);
    }

    /**
     * Öppnar ett formulär för att ändra den kund som är markerad i tabellen.
     * Metoden hämtar först kunden från databasen och uppdaterar sedan objektet
     * om användaren fyller i giltiga värden.
     */
    private void editSelectedCustomer() {
        int selectedRow = jTable1.getSelectedRow();

        if (selectedRow == -1) {
            javax.swing.JOptionPane.showMessageDialog(this, "Välj en kund först.");
            return;
        }

        String customerId = jTable1.getValueAt(selectedRow, 0).toString();

        CustomerRepository repo = new CustomerRepository();
        Customer customer = repo.getCustomerById(customerId);

        if (customer == null) {
            javax.swing.JOptionPane.showMessageDialog(this, "Kunden hittades inte.");
            return;
        }

        // Skapar textfält med kundens nuvarande uppgifter.
        javax.swing.JTextField txtCustomerType = new javax.swing.JTextField(customer.getCustomerType());
        javax.swing.JTextField txtName = new javax.swing.JTextField(customer.getName());
        javax.swing.JTextField txtOrgNumber = new javax.swing.JTextField(customer.getOrgNumber());
        javax.swing.JTextField txtStreetAddress = new javax.swing.JTextField(customer.getStreetAddress());
        javax.swing.JTextField txtPostalCode = new javax.swing.JTextField(customer.getPostalCode());
        javax.swing.JTextField txtCity = new javax.swing.JTextField(customer.getCity());
        javax.swing.JTextField txtCountry = new javax.swing.JTextField(customer.getCountry());
        javax.swing.JTextField txtDeliveryAddress = new javax.swing.JTextField(customer.getDeliveryAddress());
        javax.swing.JTextField txtEmail = new javax.swing.JTextField(customer.getEmail());
        javax.swing.JTextField txtPhone = new javax.swing.JTextField(customer.getPhone());
        javax.swing.JTextField txtDiscount = new javax.swing.JTextField(String.valueOf(customer.getDiscountPct()));
        javax.swing.JTextField txtPaymentNotes = new javax.swing.JTextField(customer.getPaymentNotes());

        Object[] message = {
            "Kundtyp:", txtCustomerType,
            "Namn:", txtName,
            "Organisationsnummer:", txtOrgNumber,
            "Gatuadress:", txtStreetAddress,
            "Postnummer:", txtPostalCode,
            "Stad:", txtCity,
            "Land:", txtCountry,
            "Leveransadress:", txtDeliveryAddress,
            "E-post:", txtEmail,
            "Telefon:", txtPhone,
            "Rabatt %:", txtDiscount,
            "Betalningsnotering:", txtPaymentNotes
        };

        int option = javax.swing.JOptionPane.showConfirmDialog(
                this,
                message,
                "Ändra kund",
                javax.swing.JOptionPane.OK_CANCEL_OPTION
        );

        if (option != javax.swing.JOptionPane.OK_OPTION) {
            return;
        }

        // Kundtyp och namn måste finnas eftersom de är viktiga grunduppgifter.
        if (Validator.isBlank(txtCustomerType.getText())) {
            javax.swing.JOptionPane.showMessageDialog(
                    this,
                    Validator.requiredFieldMessage("Kundtyp")
            );
            return;
        }

        if (Validator.isBlank(txtName.getText())) {
            javax.swing.JOptionPane.showMessageDialog(
                    this,
                    Validator.requiredFieldMessage("Namn")
            );
            return;
        }

        // Rabatt måste vara ett tal eftersom det sparas som decimalvärde.
        if (!Validator.isValidDouble(txtDiscount.getText())) {
            javax.swing.JOptionPane.showMessageDialog(
                    this,
                    Validator.invalidDoubleMessage("Rabatt")
            );
            return;
        }

        // Uppdaterar kundobjektet med värden från formuläret.
        customer.setCustomerType(txtCustomerType.getText().trim());
        customer.setName(txtName.getText().trim());
        customer.setOrgNumber(txtOrgNumber.getText().trim());
        customer.setStreetAddress(txtStreetAddress.getText().trim());
        customer.setPostalCode(txtPostalCode.getText().trim());
        customer.setCity(txtCity.getText().trim());
        customer.setCountry(txtCountry.getText().trim());
        customer.setDeliveryAddress(txtDeliveryAddress.getText().trim());
        customer.setEmail(txtEmail.getText().trim());
        customer.setPhone(txtPhone.getText().trim());
        customer.setDiscountPct(Validator.parseDouble(txtDiscount.getText()));
        customer.setPaymentNotes(txtPaymentNotes.getText().trim());

        boolean success = repo.updateCustomer(customer);

        if (success) {
            javax.swing.JOptionPane.showMessageDialog(this, "Kunden uppdaterades.");
            searchCustomers();
        } else {
            javax.swing.JOptionPane.showMessageDialog(this, "Kunden kunde inte uppdateras.");
        }
    }

    /**
     * Anonymiserar den markerade kunden.
     * Kundens personuppgifter tas bort men kund-ID sparas
     * så att gamla ordrar fortfarande kan finnas kvar i systemet.
     */
    private void anonymizeSelectedCustomer() {
        int selectedRow = jTable1.getSelectedRow();

        if (selectedRow == -1) {
            javax.swing.JOptionPane.showMessageDialog(this, "Välj en kund först.");
            return;
        }

        String customerId = jTable1.getValueAt(selectedRow, 0).toString();

        int confirm = javax.swing.JOptionPane.showConfirmDialog(
                this,
                "Vill du anonymisera kunden? Kundens personuppgifter tas bort, men ID finns kvar för orderhistorik.",
                "Anonymisera kund",
                javax.swing.JOptionPane.YES_NO_OPTION
        );

        if (confirm != javax.swing.JOptionPane.YES_OPTION) {
            return;
        }

        CustomerRepository repo = new CustomerRepository();
        boolean success = repo.anonymizeCustomer(customerId);

        if (success) {
            javax.swing.JOptionPane.showMessageDialog(this, "Kunden anonymiserades.");
            searchCustomers();
        } else {
            javax.swing.JOptionPane.showMessageDialog(this, "Kunden kunde inte anonymiseras.");
        }
    }

    /**
     * Visar historik för den markerade kunden.
     * Historiken innehåller kundens namn, eventuell betalningsnotering
     * och alla ordrar som är kopplade till kunden.
     */
    private void showCustomerHistory() {
        int selectedRow = jTable1.getSelectedRow();

        if (selectedRow == -1) {
            javax.swing.JOptionPane.showMessageDialog(this, "Välj en kund först.");
            return;
        }

        String customerId = jTable1.getValueAt(selectedRow, 0).toString();

        CustomerRepository repo = new CustomerRepository();
        Customer customer = repo.getCustomerById(customerId);
        java.util.List<Order> orders = repo.getOrdersByCustomerId(customerId);

        StringBuilder sb = new StringBuilder();

        sb.append("KUNDHISTORIK\n");
        sb.append("====================================\n\n");

        if (customer != null) {
            sb.append("Kund: ")
                    .append(Validator.safeName(customer.getName()))
                    .append("\n");

            if (!Validator.isBlank(customer.getPaymentNotes())) {
                sb.append("Betalningsnotering:\n");
                sb.append(customer.getPaymentNotes()).append("\n");
            }

            sb.append("\n");
        }

        // Lägger till varje order i historiktexten.
        for (Order o : orders) {
            sb.append("Order-ID: ").append(o.getOrderId()).append("\n");
            sb.append("Status: ").append(o.getStatus()).append("\n");
            sb.append("Total: ").append(o.getTotalInclVat()).append(" SEK\n");

            if (!Validator.isBlank(o.getStatusNote())) {
                sb.append("Ordernotering:\n");
                sb.append(o.getStatusNote()).append("\n");
            }

            sb.append("------------------------------------\n");
        }

        javax.swing.JTextArea txtArea = new javax.swing.JTextArea(sb.toString());
        txtArea.setEditable(false);
        txtArea.setLineWrap(true);
        txtArea.setWrapStyleWord(true);

        javax.swing.JScrollPane scroll = new javax.swing.JScrollPane(txtArea);
        scroll.setPreferredSize(new java.awt.Dimension(700, 500));

        javax.swing.JOptionPane.showMessageDialog(
                this,
                scroll,
                "Kundhistorik",
                javax.swing.JOptionPane.INFORMATION_MESSAGE
        );
    }
    /**
     * Den här metoden skapas automatiskt av NetBeans GUI Builder.
     * Ändringar här kan skrivas över av form editorn.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnBack = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        btnSearch = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        btnAddCustomer = new javax.swing.JButton();
        btnEditCustomer = new javax.swing.JButton();
        btnAnonymizeCustomer = new javax.swing.JButton();
        btnCustomerHistory = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(1000, 700));

        btnBack.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        btnBack.setText("Tillbaka");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 30)); // NOI18N
        jLabel1.setText("KUNDHANTERING");

        txtSearch.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel2.setText("Sök Kundnamn");

        btnSearch.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnSearch.setText("Sök kund");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Kund-ID", "Typ", "Namn", "Epost", "Telefon", "Gata", "Postkod", "Stad", "Land", "Rabatt", "Anonym"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(jTable1);

        btnAddCustomer.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnAddCustomer.setText("Lägg till kund");
        btnAddCustomer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddCustomerActionPerformed(evt);
            }
        });

        btnEditCustomer.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnEditCustomer.setText("Ändra kundinfo");
        btnEditCustomer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditCustomerActionPerformed(evt);
            }
        });

        btnAnonymizeCustomer.setBackground(new java.awt.Color(255, 153, 153));
        btnAnonymizeCustomer.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnAnonymizeCustomer.setText("Anonymisera kund");
        btnAnonymizeCustomer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAnonymizeCustomerActionPerformed(evt);
            }
        });

        btnCustomerHistory.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnCustomerHistory.setText("Visa kundhistorik");
        btnCustomerHistory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCustomerHistoryActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(194, 194, 194)
                        .addComponent(btnCustomerHistory)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnEditCustomer))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 307, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnAddCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnBack, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(636, 636, 636)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 679, Short.MAX_VALUE)
                        .addComponent(btnAnonymizeCustomer)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnBack, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(btnAnonymizeCustomer)))
                .addGap(44, 44, 44)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAddCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEditCustomer)
                    .addComponent(btnCustomerHistory))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 748, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    /**
     * Går tillbaka till startsidan.
     */
    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        new MainFrame().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnBackActionPerformed
    /**
    * Kör kundsökningen när användaren klickar på sökknappen.
    * Själva söklogiken ligger i metoden searchCustomers().
    */
    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
    searchCustomers();
    }//GEN-LAST:event_btnSearchActionPerformed
    /**
    * Öppnar ett formulär där användaren kan lägga till en ny kund.
    * Metoden hämtar värden från formuläret, validerar obligatoriska fält
    * och sparar sedan kunden via CustomerRepository.
    */
    private void btnAddCustomerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddCustomerActionPerformed
    javax.swing.JTextField txtCustomerType = new javax.swing.JTextField();
    javax.swing.JTextField txtName = new javax.swing.JTextField();
    javax.swing.JTextField txtOrgNumber = new javax.swing.JTextField();
    javax.swing.JTextField txtStreetAddress = new javax.swing.JTextField();
    javax.swing.JTextField txtPostalCode = new javax.swing.JTextField();
    javax.swing.JTextField txtCity = new javax.swing.JTextField();
    javax.swing.JTextField txtCountry = new javax.swing.JTextField();
    javax.swing.JTextField txtDeliveryAddress = new javax.swing.JTextField();
    javax.swing.JTextField txtEmail = new javax.swing.JTextField();
    javax.swing.JTextField txtPhone = new javax.swing.JTextField();

    Object[] message = {
        "Kundtyp (private/company):", txtCustomerType,
        "Namn:", txtName,
        "Organisationsnummer:", txtOrgNumber,
        "Gatuadress:", txtStreetAddress,
        "Postnummer:", txtPostalCode,
        "Stad:", txtCity,
        "Land:", txtCountry,
        "Leveransadress:", txtDeliveryAddress,
        "E-post:", txtEmail,
        "Telefon:", txtPhone
    };

    int option = javax.swing.JOptionPane.showConfirmDialog(
            this,
            message,
            "Lägg till kund",
            javax.swing.JOptionPane.OK_CANCEL_OPTION
    );

    // Avbryter om användaren inte klickade på OK.
    if (option != javax.swing.JOptionPane.OK_OPTION) {
        return;
    }

    // Hämtar och rensar text från formuläret innan kunden skapas.
    String customerType = txtCustomerType.getText().trim();
    String name = txtName.getText().trim();
    String orgNumber = txtOrgNumber.getText().trim();
    String streetAddress = txtStreetAddress.getText().trim();
    String postalCode = txtPostalCode.getText().trim();
    String city = txtCity.getText().trim();
    String country = txtCountry.getText().trim();
    String deliveryAddress = txtDeliveryAddress.getText().trim();
    String email = txtEmail.getText().trim();
    String phone = txtPhone.getText().trim();

    // Kundtyp måste anges eftersom den avgör vilken typ av kund det är.
    if (Validator.isBlank(customerType)) {
        javax.swing.JOptionPane.showMessageDialog(
                this,
                Validator.requiredFieldMessage("Kundtyp")
        );
        return;
    }

    // Namn måste anges för att kunden ska kunna identifieras i systemet.
    if (Validator.isBlank(name)) {
        javax.swing.JOptionPane.showMessageDialog(
                this,
                Validator.requiredFieldMessage("Namn")
        );
        return;
    }

    // Skapar ett nytt kundobjekt och fyller det med formulärvärden.
    Customer customer = new Customer();
    customer.setCustomerId(generateCustomerId());
    customer.setCustomerType(customerType);
    customer.setName(name);
    customer.setOrgNumber(orgNumber);
    customer.setStreetAddress(streetAddress);
    customer.setPostalCode(postalCode);
    customer.setCity(city);
    customer.setCountry(country);
    customer.setDeliveryAddress(deliveryAddress);
    customer.setEmail(email);
    customer.setPhone(phone);

    // Standardvärden för nya kunder.
    customer.setDiscountPct(0.0);
    customer.setPaymentNotes("");
    customer.setAnonymized(false);

    CustomerRepository repo = new CustomerRepository();
    boolean success = repo.insertCustomer(customer);

    if (success) {
        javax.swing.JOptionPane.showMessageDialog(this, "Kunden sparades.");
        searchCustomers();
    } else {
        javax.swing.JOptionPane.showMessageDialog(this, "Kunden kunde inte sparas.");
    }
    }//GEN-LAST:event_btnAddCustomerActionPerformed
    /**
    * Öppnar ändringsformuläret för den markerade kunden.
    */
    private void btnEditCustomerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditCustomerActionPerformed
        editSelectedCustomer();
    }//GEN-LAST:event_btnEditCustomerActionPerformed
    /**
    * Anonymiserar den markerade kunden.
    */
    private void btnAnonymizeCustomerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAnonymizeCustomerActionPerformed
        anonymizeSelectedCustomer();
    }//GEN-LAST:event_btnAnonymizeCustomerActionPerformed
    /**
    * Visar orderhistorik och kundinformation för den markerade kunden.
    */
    private void btnCustomerHistoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCustomerHistoryActionPerformed
        showCustomerHistory();
    }//GEN-LAST:event_btnCustomerHistoryActionPerformed
    /**
    * Skapar ett enkelt kund-ID baserat på aktuell tid.
    * Detta minskar risken att två kunder får samma ID.
    */
    private String generateCustomerId() {
        return "c-" + System.currentTimeMillis();
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(CustomerFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CustomerFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CustomerFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CustomerFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CustomerFrame().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddCustomer;
    private javax.swing.JButton btnAnonymizeCustomer;
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnCustomerHistory;
    private javax.swing.JButton btnEditCustomer;
    private javax.swing.JButton btnSearch;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
