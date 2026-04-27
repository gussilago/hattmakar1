package hattmakaren;

/**
 * Fönster för lagerhantering.
 *
 * Den här klassen hanterar visning och ändring av lager.
 * Användaren kan bland annat:
 * - visa lagerposter
 * - söka i lager
 * - lägga till nya hattmodeller
 * - fylla på lager
 * - minska lager
 * - ändra vissa uppgifter för hattmodeller
 * - ta bort felregistrerade lagerposter
 */
public class InventoryFrame extends javax.swing.JFrame {

    /**
     * Skapar lagersfönstret.
     * Tabellen sätts upp direkt och fylls sedan med lagerdata.
     */
    public InventoryFrame() {
        initComponents();

        setSize(1920, 1080);
        setLocationRelativeTo(null);

        setupInventoryTable();
        loadInventoryTable();
    }

    /**
     * Skapar ett enkelt unikt ID för en ny hattmodell.
     * System.currentTimeMillis används för att minska risken att två ID blir lika.
     */
    private String generateHatModelId() {
        return "hm-" + System.currentTimeMillis();
    }

    /**
     * Sätter upp kolumnerna i lagertabellen.
     */
    private void setupInventoryTable() {
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(
                new Object[]{
                    "Hattmodell-ID",
                    "Hattnamn",
                    "Typ",
                    "Kategori",
                    "Storlek",
                    "Antal",
                    "Färdig stomme",
                    "Baspris",
                    "Aktiv"
                },
                0
        );

        tblInventory.setModel(model);
    }

    /**
     * Laddar alla lagerposter från databasen och visar dem i tabellen.
     * Lagerposter med antal 0 eller mindre visas inte.
     */
    private void loadInventoryTable() {
        HatInventoryRepository inventoryRepo = new HatInventoryRepository();
        HatModelRepository hatRepo = new HatModelRepository();

        java.util.List<HatInventory> inventoryList = inventoryRepo.getAllInventory();

        javax.swing.table.DefaultTableModel model =
                (javax.swing.table.DefaultTableModel) tblInventory.getModel();

        // Rensar tabellen innan ny data läggs in.
        model.setRowCount(0);

        for (HatInventory item : inventoryList) {

            // Hoppar över lagerposter som inte finns i lager.
            if (item.getQuantity() <= 0) {
                continue;
            }

            HatModel hat = hatRepo.getHatModelById(item.getHatModelId());

            String hatName = "Okänd hatt";
            String hatType = "Okänt";
            String category = "Okänt";
            String hasFrame = "Nej";
            double basePrice = 0;
            String active = "Nej";

            // Om hattmodellen hittas används dess riktiga information.
            if (hat != null) {
                hatName = Validator.safeName(hat.getName());
                hatType = Validator.safeText(hat.getHatType());
                category = Validator.safeText(hat.getCategory());
                hasFrame = hat.isHasPremadeFrame() ? "Ja" : "Nej";
                basePrice = hat.getBasePrice();
                active = hat.isActive() ? "Ja" : "Nej";
            }

            model.addRow(new Object[]{
                item.getHatModelId(),
                hatName,
                hatType,
                category,
                item.getSize(),
                item.getQuantity(),
                hasFrame,
                basePrice,
                active
            });
        }
    }

    /**
     * Söker i lagertabellen.
     * Sökningen jämför texten med flera synliga kolumner, till exempel namn,
     * ID, typ, kategori och storlek.
     */
    private void searchInventoryTable() {
        HatInventoryRepository inventoryRepo = new HatInventoryRepository();
        HatModelRepository hatRepo = new HatModelRepository();

        String searchText = txtInventorySearch.getText().trim().toLowerCase();

        java.util.List<HatInventory> inventoryList = inventoryRepo.getAllInventory();

        javax.swing.table.DefaultTableModel model =
                (javax.swing.table.DefaultTableModel) tblInventory.getModel();

        // Rensar tabellen så bara sökresultatet visas.
        model.setRowCount(0);

        for (HatInventory item : inventoryList) {

            if (item.getQuantity() <= 0) {
                continue;
            }

            HatModel hat = hatRepo.getHatModelById(item.getHatModelId());

            String hatName = "Okänd hatt";
            String hatType = "Okänt";
            String category = "Okänt";
            String hasFrame = "Nej";
            double basePrice = 0;
            String active = "Nej";

            if (hat != null) {
                hatName = Validator.safeName(hat.getName());
                hatType = Validator.safeText(hat.getHatType());
                category = Validator.safeText(hat.getCategory());
                hasFrame = hat.isHasPremadeFrame() ? "Ja" : "Nej";
                basePrice = hat.getBasePrice();
                active = hat.isActive() ? "Ja" : "Nej";
            }

            // Kontrollerar om söktexten matchar någon av de viktigare uppgifterna.
            boolean matchesSearch = Validator.isBlank(searchText)
                    || item.getHatModelId().toLowerCase().contains(searchText)
                    || hatName.toLowerCase().contains(searchText)
                    || hatType.toLowerCase().contains(searchText)
                    || category.toLowerCase().contains(searchText)
                    || item.getSize().toLowerCase().contains(searchText)
                    || hasFrame.toLowerCase().contains(searchText)
                    || active.toLowerCase().contains(searchText);

            if (matchesSearch) {
                model.addRow(new Object[]{
                    item.getHatModelId(),
                    hatName,
                    hatType,
                    category,
                    item.getSize(),
                    item.getQuantity(),
                    hasFrame,
                    basePrice,
                    active
                });
            }
        }
    }
    /**
     * Sätter grunden till storlek till en ny hattmodell.
     */
    private String formatBaseSizes(String input) {
        if (Validator.isBlank(input)) {
            return null;
        }

        input = input.trim();

        if (input.startsWith("[") && input.endsWith("]")) {
            return input;
        }

        String[] parts = input.split(",");
        String result = "[";

        for (int i = 0; i < parts.length; i++) {
            String size = parts[i].trim();

            result += "\"" + size + "\"";

            if (i < parts.length - 1) {
                result += ",";
            }
        }

        result += "]";

        return result;
    }
    /**
     * Öppnar ett formulär där användaren kan lägga till en ny hattmodell.
     */
    private void addHatModel() {
        javax.swing.JTextField txtName = new javax.swing.JTextField();
        javax.swing.JTextField txtBasePrice = new javax.swing.JTextField();
        javax.swing.JTextField txtBaseSizes = new javax.swing.JTextField();
        javax.swing.JTextField txtCopyrightNote = new javax.swing.JTextField();

        javax.swing.JComboBox<String> cmbHatType = new javax.swing.JComboBox<>(
                new String[]{"stock", "custom_base"}
        );

        javax.swing.JComboBox<String> cmbCategory = new javax.swing.JComboBox<>(
                new String[]{"felt", "panama", "straw", "fabric", "leather"}
        );

        javax.swing.JCheckBox chkPremadeFrame = new javax.swing.JCheckBox("Har färdig stomme");
        javax.swing.JCheckBox chkActive = new javax.swing.JCheckBox("Aktiv", true);

        Object[] message = {
            "Namn:", txtName,
            "Hattyp:", cmbHatType,
            "Kategori:", cmbCategory,
            "Baspris:", txtBasePrice,
            "Basstorlekar (ex: [\"56\", \"57\", \"58\"]):", txtBaseSizes,
            "Copyright-notering:", txtCopyrightNote,
            chkPremadeFrame,
            chkActive
        };

        int option = javax.swing.JOptionPane.showConfirmDialog(
                this,
                message,
                "Lägg till hattmodell",
                javax.swing.JOptionPane.OK_CANCEL_OPTION
        );

        if (option != javax.swing.JOptionPane.OK_OPTION) {
            return;
        }

        String name = txtName.getText().trim();
        String hatType = cmbHatType.getSelectedItem().toString();
        String category = cmbCategory.getSelectedItem().toString();
        String basePriceText = txtBasePrice.getText().trim();
        String baseSizes = txtBaseSizes.getText().trim();
        String copyrightNote = txtCopyrightNote.getText().trim();

        if (Validator.isBlank(name)) {
            javax.swing.JOptionPane.showMessageDialog(this, Validator.requiredFieldMessage("Namn"));
            return;
        }

        if (Validator.isBlank(basePriceText)) {
            javax.swing.JOptionPane.showMessageDialog(this, Validator.requiredFieldMessage("Baspris"));
            return;
        }

        if (!Validator.isValidDouble(basePriceText)) {
            javax.swing.JOptionPane.showMessageDialog(this, Validator.invalidDoubleMessage("Baspris"));
            return;
        }

        double basePrice = Validator.parseDouble(basePriceText);

        if (basePrice < 0) {
            javax.swing.JOptionPane.showMessageDialog(this, "Baspris får inte vara negativt.");
            return;
        }

        HatModel hatModel = new HatModel();

        // Fyller objektet med informationen från formuläret.
        hatModel.setHatModelId(generateHatModelId());
        hatModel.setName(name);
        hatModel.setHatType(hatType);
        hatModel.setCategory(category);
        hatModel.setBasePrice(basePrice);
        hatModel.setBaseSizes(formatBaseSizes(baseSizes));
        hatModel.setHasPremadeFrame(chkPremadeFrame.isSelected());
        hatModel.setCopyrightNote(Validator.isBlank(copyrightNote) ? null : copyrightNote);
        hatModel.setActive(chkActive.isSelected());

        HatModelRepository repo = new HatModelRepository();
        boolean success = repo.insertHatModel(hatModel);

        if (success) {
            javax.swing.JOptionPane.showMessageDialog(this, "Hattmodell sparad.");
            loadInventoryTable();
        } else {
            javax.swing.JOptionPane.showMessageDialog(this, "Hattmodell kunde inte sparas.");
        }
    }

    /**
     * Öppnar ett formulär där användaren kan fylla på lagret
     * för en befintlig hattmodell.
     */
    private void addInventory() {
        HatModelRepository hatRepo = new HatModelRepository();
        java.util.List<HatModel> hatModels = hatRepo.getAllHatModels();

        javax.swing.JComboBox<HatModel> cmbHatModelPopup = new javax.swing.JComboBox<>();

        for (HatModel h : hatModels) {
            cmbHatModelPopup.addItem(h);
        }

        javax.swing.JTextField txtSize = new javax.swing.JTextField();
        javax.swing.JTextField txtQuantity = new javax.swing.JTextField();

        Object[] message = {
            "Välj hattmodell:", cmbHatModelPopup,
            "Storlek:", txtSize,
            "Antal att lägga till:", txtQuantity
        };

        int option = javax.swing.JOptionPane.showConfirmDialog(
                this,
                message,
                "Fyll på lager",
                javax.swing.JOptionPane.OK_CANCEL_OPTION
        );

        if (option != javax.swing.JOptionPane.OK_OPTION) {
            return;
        }

        HatModel selectedHat = (HatModel) cmbHatModelPopup.getSelectedItem();
        String size = txtSize.getText().trim();
        String quantityText = txtQuantity.getText().trim();

        if (selectedHat == null) {
            javax.swing.JOptionPane.showMessageDialog(this, "Välj en hattmodell.");
            return;
        }

        if (Validator.isBlank(size)) {
            javax.swing.JOptionPane.showMessageDialog(this, Validator.requiredFieldMessage("Storlek"));
            return;
        }

        if (!Validator.isPositiveInt(quantityText)) {
            javax.swing.JOptionPane.showMessageDialog(this, Validator.invalidIntegerMessage("Antal"));
            return;
        }

        int quantity = Validator.parsePositiveInt(quantityText);

        HatInventoryRepository inventoryRepo = new HatInventoryRepository();

        // Lägger till valt antal i lagret för vald hattmodell och storlek.
        boolean success = inventoryRepo.addToInventory(
                selectedHat.getHatModelId(),
                size,
                quantity
        );

        if (success) {
            javax.swing.JOptionPane.showMessageDialog(this, "Lagret uppdaterades.");
            loadInventoryTable();
        } else {
            javax.swing.JOptionPane.showMessageDialog(this, "Lagret kunde inte uppdateras.");
        }
    }

    /**
     * Minskar lagersaldot för den rad som är markerad i tabellen.
     * Om användaren försöker dra bort mer än vad som finns sätts lagret till 0.
     */
    private void reduceSelectedInventoryRow() {
        int selectedRow = tblInventory.getSelectedRow();

        if (selectedRow == -1) {
            javax.swing.JOptionPane.showMessageDialog(this, "Välj en lagerpost först.");
            return;
        }

        String hatModelId = tblInventory.getValueAt(selectedRow, 0).toString();
        String size = tblInventory.getValueAt(selectedRow, 4).toString();

        String quantityText = javax.swing.JOptionPane.showInputDialog(
                this,
                "Hur många vill du dra bort?"
        );

        if (Validator.isBlank(quantityText)) {
            return;
        }

        if (!Validator.isPositiveInt(quantityText)) {
            javax.swing.JOptionPane.showMessageDialog(this, Validator.invalidIntegerMessage("Antal"));
            return;
        }

        int amountToReduce = Validator.parsePositiveInt(quantityText);

        HatInventoryRepository repo = new HatInventoryRepository();
        HatInventory inventory = repo.getInventoryByModelAndSize(hatModelId, size);

        if (inventory == null) {
            javax.swing.JOptionPane.showMessageDialog(this, "Lagerposten hittades inte.");
            return;
        }

        int newQuantity = inventory.getQuantity() - amountToReduce;

        // Lagerantal ska inte kunna bli negativt.
        if (newQuantity < 0) {
            newQuantity = 0;
        }

        boolean success = repo.updateQuantity(hatModelId, size, newQuantity);

        if (success) {
            javax.swing.JOptionPane.showMessageDialog(this, "Lagret uppdaterades.");
            loadInventoryTable();
        } else {
            javax.swing.JOptionPane.showMessageDialog(this, "Lagret kunde inte uppdateras.");
        }
    }

    /**
     * Ändrar baspris och om vald hattmodell har färdig stomme.
     */
    private void editHatModel() {
        HatModelRepository hatRepo = new HatModelRepository();
        java.util.List<HatModel> hatModels = hatRepo.getAllHatModels();

        javax.swing.JComboBox<HatModel> cmbHatModelPopup = new javax.swing.JComboBox<>();

        for (HatModel h : hatModels) {
            cmbHatModelPopup.addItem(h);
        }

        Object[] firstMessage = {
            "Välj hattmodell:", cmbHatModelPopup
        };

        int firstOption = javax.swing.JOptionPane.showConfirmDialog(
                this,
                firstMessage,
                "Välj hattmodell",
                javax.swing.JOptionPane.OK_CANCEL_OPTION
        );

        if (firstOption != javax.swing.JOptionPane.OK_OPTION) {
            return;
        }

        HatModel selectedHat = (HatModel) cmbHatModelPopup.getSelectedItem();

        if (selectedHat == null) {
            javax.swing.JOptionPane.showMessageDialog(this, "Välj en hattmodell.");
            return;
        }

        javax.swing.JTextField txtBasePrice = new javax.swing.JTextField();
        javax.swing.JCheckBox chkPremadeFrame = new javax.swing.JCheckBox("Har färdig stomme");

        // Fyller formuläret med nuvarande värden.
        txtBasePrice.setText(String.valueOf(selectedHat.getBasePrice()));
        chkPremadeFrame.setSelected(selectedHat.isHasPremadeFrame());

        Object[] editMessage = {
            "Hattmodell:", selectedHat.getName(),
            "Baspris:", txtBasePrice,
            chkPremadeFrame
        };

        int editOption = javax.swing.JOptionPane.showConfirmDialog(
                this,
                editMessage,
                "Ändra modell/pris",
                javax.swing.JOptionPane.OK_CANCEL_OPTION
        );

        if (editOption != javax.swing.JOptionPane.OK_OPTION) {
            return;
        }

        String priceText = txtBasePrice.getText().trim();

        if (Validator.isBlank(priceText)) {
            javax.swing.JOptionPane.showMessageDialog(this, Validator.requiredFieldMessage("Baspris"));
            return;
        }

        if (!Validator.isValidDouble(priceText)) {
            javax.swing.JOptionPane.showMessageDialog(this, Validator.invalidDoubleMessage("Baspris"));
            return;
        }

        double basePrice = Validator.parseDouble(priceText);

        if (basePrice < 0) {
            javax.swing.JOptionPane.showMessageDialog(this, "Baspris får inte vara negativt.");
            return;
        }

        boolean success = hatRepo.updateHatModelPriceAndFrame(
                selectedHat.getHatModelId(),
                basePrice,
                chkPremadeFrame.isSelected()
        );

        if (success) {
            javax.swing.JOptionPane.showMessageDialog(this, "Hattmodellen uppdaterades.");
            loadInventoryTable();
        } else {
            javax.swing.JOptionPane.showMessageDialog(this, "Hattmodellen kunde inte uppdateras.");
        }
    }

    /**
     * Tar bort vald lagerpost från tabellen och databasen.
     * Detta är tänkt att användas vid felregistrering, inte vid vanlig lagerförbrukning.
     */
    private void deleteSelectedInventoryRow() {
        int selectedRow = tblInventory.getSelectedRow();

        if (selectedRow == -1) {
            javax.swing.JOptionPane.showMessageDialog(this, "Välj en lagerpost först.");
            return;
        }

        String hatModelId = tblInventory.getValueAt(selectedRow, 0).toString();
        String hatName = tblInventory.getValueAt(selectedRow, 1).toString();
        String size = tblInventory.getValueAt(selectedRow, 4).toString();

        int confirm = javax.swing.JOptionPane.showConfirmDialog(
                this,
                "Vill du ta bort lagerposten?\n\n"
                + "Hatt: " + hatName + "\n"
                + "Storlek: " + size + "\n\n"
                + "Detta ska bara användas vid felregistrering.",
                "Ta bort lagerpost",
                javax.swing.JOptionPane.YES_NO_OPTION
        );

        if (confirm != javax.swing.JOptionPane.YES_OPTION) {
            return;
        }

        HatInventoryRepository repo = new HatInventoryRepository();

        // Tar bort exakt den lagerpost som matchar hattmodell och storlek.
        boolean success = repo.deleteInventoryRow(hatModelId, size);

        if (success) {
            javax.swing.JOptionPane.showMessageDialog(this, "Lagerposten togs bort.");
            loadInventoryTable();
        } else {
            javax.swing.JOptionPane.showMessageDialog(this, "Lagerposten kunde inte tas bort.");
        }
    }
    /**
     * Den här metoden skapas automatiskt av NetBeans GUI Builder.
     * Ändringar här kan skrivas över av form editorn.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnBack = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        btnLoadInventory = new javax.swing.JButton();
        btnAddHatModel = new javax.swing.JButton();
        btnAddInventory = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblInventory = new javax.swing.JTable();
        lblSearchInventory = new javax.swing.JLabel();
        txtInventorySearch = new javax.swing.JTextField();
        btnSearchInventory = new javax.swing.JButton();
        btnReduceInventory = new javax.swing.JButton();
        btnEditHatModel = new javax.swing.JButton();
        btnDeleteInventoryRow = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        btnBack.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        btnBack.setText("Tillbaka");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel1.setText("LAGER");

        btnLoadInventory.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnLoadInventory.setText("Visa allt i lager");
        btnLoadInventory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoadInventoryActionPerformed(evt);
            }
        });

        btnAddHatModel.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnAddHatModel.setText("Lägg till hattmodell");
        btnAddHatModel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddHatModelActionPerformed(evt);
            }
        });

        btnAddInventory.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnAddInventory.setText("Fyll på lager");
        btnAddInventory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddInventoryActionPerformed(evt);
            }
        });

        tblInventory.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblInventory.setToolTipText("");
        jScrollPane1.setViewportView(tblInventory);

        lblSearchInventory.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        lblSearchInventory.setText("Sök hattmodell");

        txtInventorySearch.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        txtInventorySearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtInventorySearchActionPerformed(evt);
            }
        });

        btnSearchInventory.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnSearchInventory.setText("Sök");
        btnSearchInventory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchInventoryActionPerformed(evt);
            }
        });

        btnReduceInventory.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnReduceInventory.setText("Minska lager");
        btnReduceInventory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReduceInventoryActionPerformed(evt);
            }
        });

        btnEditHatModel.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnEditHatModel.setText("Ändra modell/pris");
        btnEditHatModel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditHatModelActionPerformed(evt);
            }
        });

        btnDeleteInventoryRow.setBackground(new java.awt.Color(255, 153, 153));
        btnDeleteInventoryRow.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnDeleteInventoryRow.setText("Ta bort lagerpost");
        btnDeleteInventoryRow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteInventoryRowActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnBack, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 811, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addGap(841, 841, 841))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtInventorySearch, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(48, 48, 48)
                                .addComponent(btnLoadInventory)
                                .addGap(62, 62, 62)
                                .addComponent(btnAddInventory)
                                .addGap(18, 18, 18)
                                .addComponent(btnAddHatModel)
                                .addGap(18, 18, 18)
                                .addComponent(btnReduceInventory)
                                .addGap(18, 18, 18)
                                .addComponent(btnEditHatModel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnDeleteInventoryRow))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblSearchInventory)
                                    .addComponent(btnSearchInventory))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(btnBack, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                .addComponent(lblSearchInventory)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtInventorySearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnLoadInventory)
                        .addComponent(btnAddInventory)
                        .addComponent(btnAddHatModel)
                        .addComponent(btnReduceInventory)
                        .addComponent(btnEditHatModel)
                        .addComponent(btnDeleteInventoryRow)))
                .addGap(18, 18, 18)
                .addComponent(btnSearchInventory)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 784, javax.swing.GroupLayout.PREFERRED_SIZE)
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
     * Visar alla lagerposter i tabellen.
     */
    private void btnLoadInventoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoadInventoryActionPerformed
        loadInventoryTable();

    }//GEN-LAST:event_btnLoadInventoryActionPerformed
     /**
     * Öppnar formuläret för att lägga till en ny hattmodell.
     */
    private void btnAddHatModelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddHatModelActionPerformed
        addHatModel();
    }//GEN-LAST:event_btnAddHatModelActionPerformed
    /**
     * Öppnar formuläret för att fylla på lager.
     */
    private void btnAddInventoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddInventoryActionPerformed
        addInventory();
    }//GEN-LAST:event_btnAddInventoryActionPerformed
    /**
     * Söker i tabellen efter hattmodell.
     */
    private void txtInventorySearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtInventorySearchActionPerformed
        
    }//GEN-LAST:event_txtInventorySearchActionPerformed
    /**
     * Minskar lagersaldot för vald lagerpost.
     */
    private void btnSearchInventoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchInventoryActionPerformed
        searchInventoryTable();
    }//GEN-LAST:event_btnSearchInventoryActionPerformed

    private void btnReduceInventoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReduceInventoryActionPerformed
        reduceSelectedInventoryRow();
    }//GEN-LAST:event_btnReduceInventoryActionPerformed
    /**
     * Öppnar formulär att ändra modellen inklusive priset.
     */
    private void btnEditHatModelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditHatModelActionPerformed
        editHatModel();
    }//GEN-LAST:event_btnEditHatModelActionPerformed
    /**
     * Raderar vald row i från tabellen.
     */
    private void btnDeleteInventoryRowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteInventoryRowActionPerformed
        deleteSelectedInventoryRow();
    }//GEN-LAST:event_btnDeleteInventoryRowActionPerformed

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
            java.util.logging.Logger.getLogger(InventoryFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(InventoryFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(InventoryFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(InventoryFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new InventoryFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddHatModel;
    private javax.swing.JButton btnAddInventory;
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnDeleteInventoryRow;
    private javax.swing.JButton btnEditHatModel;
    private javax.swing.JButton btnLoadInventory;
    private javax.swing.JButton btnReduceInventory;
    private javax.swing.JButton btnSearchInventory;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblSearchInventory;
    private javax.swing.JTable tblInventory;
    private javax.swing.JTextField txtInventorySearch;
    // End of variables declaration//GEN-END:variables
}
