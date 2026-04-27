package hattmakaren;

/**
 * Fönster för orderhantering.
 * Här kan användaren skapa nya ordrar, visa orderunderlag,
 * exportera fraktsedel och hantera befintliga ordrar.
 */
public class OrderFrame extends javax.swing.JFrame {

    private OrderDraft orderDraft = new OrderDraft();

    // Tillfälliga val för anpassning av en hatt innan den läggs till i ordern.
    private Material pendingMaterial = null;
    private double pendingMaterialQuantity = 0;
    private java.util.List<OrderLineDecoration> pendingDecorations = new java.util.ArrayList<>();
    private double pendingAdjustmentCost = 0;
    private String pendingAdjustmentText = "";

    // Lista med ordrar som är laddade från databasen och visas i tabellen.
    private java.util.List<Order> loadedOrders = new java.util.ArrayList<>();

    /**
     * Skapar orderfönstret och laddar all grunddata som behövs.
     */
    public OrderFrame() {
        initComponents();

        setSize(1920, 1080);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        txtQuantity.setText("1");

        loadCustomers();
        loadHatModels();
        setupOrdersTable();
        loadOrdersTable();
        setupTableDoubleClick();
    }

    /**
     * Laddar alla kunder från databasen och lägger in dem i kundlistan.
     */
    private void loadCustomers() {
        CustomerRepository repo = new CustomerRepository();
        java.util.List<Customer> customers = repo.getAllCustomers();

        cmbCustomer.removeAllItems();

        for (Customer c : customers) {
            if (!c.isAnonymized()) {
                cmbCustomer.addItem(c);
            }
        }
    }

    /**
     * Ställer in ordertabellen så att den får rätt kolumner
     * och inte går att redigera direkt i tabellen.
     */
    private void setupOrdersTable() {
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(
                new Object[]{"Order-ID", "Kund", "Status", "Notering", "Datum", "Totalpris"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblOrders.setModel(model);
        tblOrders.setRowSelectionAllowed(true);
        tblOrders.setColumnSelectionAllowed(false);
        tblOrders.setCellSelectionEnabled(false);
    }

    /**
     * Laddar alla hattmodeller från databasen och lägger in dem i listan.
     */
    private void loadHatModels() {
        HatModelRepository repo = new HatModelRepository();
        java.util.List<HatModel> hatModels = repo.getAllHatModels();

        cmbHatModel.removeAllItems();

        for (HatModel h : hatModels) {
            cmbHatModel.addItem(h);
        }
    }

    /**
     * Laddar tillgängliga storlekar för den hattmodell som är vald.
     */
    private void loadSizesForSelectedHat() {
        cmbSize.removeAllItems();

        HatModel selectedHat = (HatModel) cmbHatModel.getSelectedItem();

        if (selectedHat == null) {
            return;
        }

        HatInventoryRepository repo = new HatInventoryRepository();
        java.util.List<HatInventory> inventoryList = repo.getAllInventory();

        for (HatInventory item : inventoryList) {
            if (item.getHatModelId().equals(selectedHat.getHatModelId())) {
                cmbSize.addItem(item.getSize());
            }
        }
    }

    /**
     * Visar lagersaldo för vald hattmodell och vald storlek.
     */
    private void showInventoryStatus() {
        HatModel selectedHat = (HatModel) cmbHatModel.getSelectedItem();
        String selectedSize = (String) cmbSize.getSelectedItem();

        if (selectedHat == null || selectedSize == null) {
            lblInventoryStatus.setText("Lagersaldo: -");
            return;
        }

        HatInventoryRepository repo = new HatInventoryRepository();
        HatInventory inventory = repo.getInventoryByModelAndSize(
                selectedHat.getHatModelId(),
                selectedSize
        );

        if (inventory == null) {
            lblInventoryStatus.setText("Lagersaldo: 0");
        } else {
            lblInventoryStatus.setText("Lagersaldo: " + inventory.getQuantity());
        }
    }

    /**
     * Uppdaterar orderunderlaget som visas i textrutan.
     */
    private void updateOrderPreview() {
        txtOrderPreview.setText(orderDraft.buildPreviewText());
    }

    /**
     * Lägger till vald hattmodell, storlek och eventuella anpassningar
     * i den pågående ordern.
     */
    private void addSelectedHatToOrder() {
        HatModel selectedHat = (HatModel) cmbHatModel.getSelectedItem();
        String selectedSize = (String) cmbSize.getSelectedItem();
        String quantityText = txtQuantity.getText().trim();

        if (selectedHat == null || selectedSize == null) {
            javax.swing.JOptionPane.showMessageDialog(this, "Välj hatt och storlek först.");
            return;
        }

        if (!Validator.isPositiveInt(quantityText)) {
            javax.swing.JOptionPane.showMessageDialog(this, Validator.invalidIntegerMessage("Antal"));
            return;
        }

        int quantity = Validator.parsePositiveInt(quantityText);

        HatInventoryRepository inventoryRepo = new HatInventoryRepository();
        HatInventory inventory = inventoryRepo.getInventoryByModelAndSize(
                selectedHat.getHatModelId(),
                selectedSize
        );

        if (inventory == null || inventory.getQuantity() < quantity) {
            javax.swing.JOptionPane.showMessageDialog(this, "Det finns inte tillräckligt många i lager.");
            return;
        }

        double materialCost = 0;
        double decorationCost = 0;

        // Räknar ihop kostnaden för alla dekorationer som lagts till tillfälligt.
        for (OrderLineDecoration d : pendingDecorations) {
            decorationCost += d.getDecorationCost();
        }

        // Räknar materialkostnad endast om användaren faktiskt har valt material.
        if (pendingMaterial != null && pendingMaterialQuantity > 0) {
            materialCost = pendingMaterial.getUnitPrice() * pendingMaterialQuantity;
        }

        double priceExclVat = (selectedHat.getBasePrice() * quantity)
                + materialCost
                + decorationCost
                + pendingAdjustmentCost;

        double vat = PriceCalculator.calculateVat(priceExclVat);
        double total = PriceCalculator.calculatePriceInclVat(priceExclVat);

        OrderLine line = new OrderLine();
        line.setHatModelId(selectedHat.getHatModelId());
        line.setSize(selectedSize);
        line.setQuantity(quantity);
        line.setPriceExclVat(priceExclVat);
        line.setVatAmount(vat);
        line.setPriceInclVat(total);

        OrderLineDraft draftLine = new OrderLineDraft();
        draftLine.setOrderLine(line);
        draftLine.setHatName(selectedHat.getName());
        draftLine.setAdjustmentText(pendingAdjustmentText);
        draftLine.setMaterial(pendingMaterial);
        draftLine.setMaterialQuantity(pendingMaterialQuantity);

        for (OrderLineDecoration d : pendingDecorations) {
            draftLine.addDecoration(d);
        }

        orderDraft.addLine(draftLine);

        clearPendingCustomization();
        updateOrderPreview();
    }

    /**
     * Väljer kund för den pågående ordern.
     */
    private void selectCustomerForOrder() {
        Customer customer = (Customer) cmbCustomer.getSelectedItem();

        if (customer == null) {
            javax.swing.JOptionPane.showMessageDialog(this, "Välj en kund först.");
            return;
        }

        orderDraft.setCustomer(customer);
        updateOrderPreview();
    }

    /**
     * Sparar ordern och alla orderrader i databasen.
     */
    private void placeOrder() {
        if (!orderDraft.hasCustomer()) {
            javax.swing.JOptionPane.showMessageDialog(this, "Välj kund först.");
            return;
        }

        if (!orderDraft.hasLines()) {
            javax.swing.JOptionPane.showMessageDialog(this, "Lägg till minst en hatt i ordern först.");
            return;
        }

        OrderService service = new OrderService();
        boolean success = service.saveOrder(orderDraft);

        if (success) {
            javax.swing.JOptionPane.showMessageDialog(this, "Beställning skapad.");

            orderDraft.clear();
            clearPendingCustomization();

            txtOrderPreview.setText("");
            txtQuantity.setText("1");
            lblInventoryStatus.setText("Lagersaldo: -");

            loadOrdersTable();
        } else {
            javax.swing.JOptionPane.showMessageDialog(this, "Beställningen kunde inte sparas korrekt.");
        }
    }

    /**
     * Exporterar aktuellt orderunderlag som en textfil.
     */
    private void exportShippingLabel() {
        if (Validator.isBlank(txtOrderPreview.getText())) {
            javax.swing.JOptionPane.showMessageDialog(this, "Det finns inget orderunderlag att exportera.");
            return;
        }

        javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
        fileChooser.setDialogTitle("Spara fraktsedel");

        String fileName;

        int selectedRow = tblOrders.getSelectedRow();

        // Om en order är vald används order-id i filnamnet.
        // Annars används aktuell tid för att undvika samma filnamn.
        if (selectedRow != -1) {
            String orderId = tblOrders.getValueAt(selectedRow, 0).toString();
            fileName = "fraktsedel_" + orderId;
        } else {
            fileName = "fraktsedel_" + System.currentTimeMillis();
        }

        fileChooser.setSelectedFile(new java.io.File(fileName + ".txt"));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == javax.swing.JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();

            try (java.io.PrintWriter writer = new java.io.PrintWriter(fileToSave, "UTF-8")) {
                writer.print(txtOrderPreview.getText());
                javax.swing.JOptionPane.showMessageDialog(this, "Fraktsedel exporterad.");
            } catch (Exception e) {
                javax.swing.JOptionPane.showMessageDialog(this, "Fel vid export: " + e.getMessage());
            }
        }
    }

    /**
     * Laddar alla ordrar till ordertabellen.
     * Metoden tar även hänsyn till söktext och statusfilter.
     */
    private void loadOrdersTable() {
        OrderRepository orderRepo = new OrderRepository();
        CustomerRepository customerRepo = new CustomerRepository();

        loadedOrders = orderRepo.getAllOrders();

        javax.swing.table.DefaultTableModel model =
                (javax.swing.table.DefaultTableModel) tblOrders.getModel();

        model.setRowCount(0);

        String searchText = txtOrderSearch.getText().trim().toLowerCase();
        String statusFilter = cmbOrderStatusFilter.getSelectedItem().toString();

        for (Order order : loadedOrders) {
            Customer customer = customerRepo.getCustomerById(order.getCustomerId());

            String customerName = "Okänd kund";

            if (customer != null) {
                customerName = Validator.safeName(customer.getName());
            }

            String note = Validator.isBlank(order.getStatusNote()) ? "Nej" : order.getStatusNote();
            String date = Validator.safeText(order.getOrderDate());
            String totalPrice = String.valueOf(order.getTotalInclVat());

            // Kontrollerar om ordern matchar texten som användaren söker på.
            boolean matchesSearch = Validator.isBlank(searchText)
                    || order.getOrderId().toLowerCase().contains(searchText)
                    || order.getCustomerId().toLowerCase().contains(searchText)
                    || customerName.toLowerCase().contains(searchText)
                    || order.getStatus().toLowerCase().contains(searchText)
                    || note.toLowerCase().contains(searchText)
                    || date.toLowerCase().contains(searchText)
                    || totalPrice.toLowerCase().contains(searchText);

            // Kontrollerar om ordern matchar valt statusfilter.
            boolean matchesStatus = statusFilter.equals("Alla")
                    || order.getStatus().equalsIgnoreCase(statusFilter);

            if (matchesSearch && matchesStatus) {
                model.addRow(new Object[]{
                    order.getOrderId(),
                    customerName,
                    order.getStatus(),
                    note,
                    date,
                    order.getTotalInclVat()
                });
            }
        }
    }

    /**
     * Gör så att dubbelklick på en order visar orderunderlaget.
     */
    private void setupTableDoubleClick() {
        tblOrders.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    showSelectedOrderInPreview();
                }
            }
        });
    }

    /**
     * Visar vald order i orderunderlaget.
     */
    private void showSelectedOrderInPreview() {
        int selectedRow = tblOrders.getSelectedRow();

        if (selectedRow == -1) {
            return;
        }

        String orderId = tblOrders.getValueAt(selectedRow, 0).toString();

        Order foundOrder = null;

        // Letar upp orderobjektet som motsvarar raden användaren markerat.
        for (Order order : loadedOrders) {
            if (order.getOrderId().equals(orderId)) {
                foundOrder = order;
                break;
            }
        }

        if (foundOrder == null) {
            javax.swing.JOptionPane.showMessageDialog(this, "Ordern hittades inte.");
            return;
        }

        String customerId = foundOrder.getCustomerId();

        OrderService service = new OrderService();
        String previewText = service.buildSavedOrderPreview(foundOrder);

        CustomerRepository customerRepo = new CustomerRepository();
        Customer customer = customerRepo.getCustomerById(customerId);

        String shippingPreview = ShippingLabel.buildShippingText(previewText, customer);
        txtOrderPreview.setText(shippingPreview);
    }

    /**
     * Markerar vald order som retur eller reklamation.
     */
    private void markReturnOrComplaint() {
        int selectedRow = tblOrders.getSelectedRow();

        if (selectedRow == -1) {
            javax.swing.JOptionPane.showMessageDialog(this, "Välj en order först.");
            return;
        }

        String orderId = tblOrders.getValueAt(selectedRow, 0).toString();

        javax.swing.JComboBox<String> cmbType = new javax.swing.JComboBox<>(
                new String[]{"Retur", "Reklamation"}
        );

        javax.swing.JTextField txtNote = new javax.swing.JTextField();

        Object[] message = {
            "Typ:", cmbType,
            "Kort orsak/notering:", txtNote
        };

        int option = javax.swing.JOptionPane.showConfirmDialog(
                this,
                message,
                "Retur/Reklamation",
                javax.swing.JOptionPane.OK_CANCEL_OPTION
        );

        if (option != javax.swing.JOptionPane.OK_OPTION) {
            return;
        }

        String newStatus = cmbType.getSelectedItem().toString();
        String note = txtNote.getText().trim();

        if (Validator.isBlank(note)) {
            javax.swing.JOptionPane.showMessageDialog(this, "Skriv en kort notering.");
            return;
        }

        OrderService service = new OrderService();
        boolean success = service.markReturnOrComplaint(orderId, newStatus, note);

        if (success) {
            javax.swing.JOptionPane.showMessageDialog(this, "Ordern markerades som " + newStatus + ".");
            loadOrdersTable();
        } else {
            javax.swing.JOptionPane.showMessageDialog(this, "Status kunde inte uppdateras.");
        }
    }

    /**
     * Hämtar enhetstext för standardmaterialet som är kopplat till hattmodellen.
     */
    private String getStandardMaterialUnitText(HatModel hat) {
        if (hat == null) {
            return "Enhet: okänd";
        }

        HatModelMaterialRepository linkRepo = new HatModelMaterialRepository();
        MaterialRepository materialRepo = new MaterialRepository();

        java.util.List<HatModelMaterial> materials =
                linkRepo.getStandardMaterialsByHatModelId(hat.getHatModelId());

        if (materials.isEmpty()) {
            return "Enhet: ingen standard kopplad";
        }

        HatModelMaterial firstLink = materials.get(0);
        Material material = materialRepo.getMaterialById(firstLink.getMaterialId());

        if (material == null) {
            return "Enhet: okänd";
        }

        return "Enhet: " + Validator.safeText(material.getUnit());
    }

    /**
     * Räknar ihop standardåtgången för material som är kopplat till hattmodellen.
     */
    private String getStandardMaterialQuantity(HatModel hat) {
        if (hat == null) {
            return "0";
        }

        HatModelMaterialRepository repo = new HatModelMaterialRepository();

        java.util.List<HatModelMaterial> materials =
                repo.getStandardMaterialsByHatModelId(hat.getHatModelId());

        if (materials.isEmpty()) {
            return "0";
        }

        double total = 0;

        for (HatModelMaterial item : materials) {
            total += item.getQuantityNeeded();
        }

        return String.valueOf(total);
    }

    /**
     * Låter användaren anpassa vald hatt med material,
     * extra arbetskostnad och en eller flera dekorationer.
     */
    private void customizeSelectedHat() {
        HatModel selectedHat = (HatModel) cmbHatModel.getSelectedItem();

        if (selectedHat == null) {
            javax.swing.JOptionPane.showMessageDialog(this, "Välj hattmodell först.");
            return;
        }

        if (!selectedHat.isHasPremadeFrame()) {
            javax.swing.JOptionPane.showMessageDialog(
                    this,
                    "Den valda hattmodellen saknar färdig stomme och kan därför inte anpassas som lagerförd modell."
            );
            return;
        }

        MaterialRepository materialRepo = new MaterialRepository();
        DecorationRepository decorationRepo = new DecorationRepository();

        java.util.List<Material> materials = materialRepo.getAllMaterials();
        java.util.List<Decoration> decorations = decorationRepo.getAllDecorations();

        javax.swing.JComboBox<Material> cmbMaterial = new javax.swing.JComboBox<>();
        cmbMaterial.addItem(null);

        for (Material m : materials) {
            cmbMaterial.addItem(m);
        }

        javax.swing.JTextField txtMaterialQuantity =
                new javax.swing.JTextField(getStandardMaterialQuantity(selectedHat));

        javax.swing.JLabel lblMaterialUnit =
                new javax.swing.JLabel(getStandardMaterialUnitText(selectedHat));

        javax.swing.JTextField txtAdjustmentCost = new javax.swing.JTextField("0");
        javax.swing.JTextField txtNote = new javax.swing.JTextField();

        Object[] message = {
            "Material:", cmbMaterial,
            "Materialåtgång:", txtMaterialQuantity,
            lblMaterialUnit,
            "Extra arbetskostnad/pristillägg:", txtAdjustmentCost,
            "Kommentar:", txtNote
        };

        int option = javax.swing.JOptionPane.showConfirmDialog(
                this,
                message,
                "Anpassa hatt",
                javax.swing.JOptionPane.OK_CANCEL_OPTION
        );

        if (option != javax.swing.JOptionPane.OK_OPTION) {
            return;
        }

        Material selectedMaterial = (Material) cmbMaterial.getSelectedItem();

        if (!Validator.isValidDouble(txtMaterialQuantity.getText())) {
            javax.swing.JOptionPane.showMessageDialog(this, Validator.invalidDoubleMessage("Materialåtgång"));
            return;
        }

        if (!Validator.isValidDouble(txtAdjustmentCost.getText())) {
            javax.swing.JOptionPane.showMessageDialog(this, Validator.invalidDoubleMessage("Pristillägg"));
            return;
        }

        double materialQuantity = Validator.parseDouble(txtMaterialQuantity.getText());
        double extraCost = Validator.parseDouble(txtAdjustmentCost.getText());

        if (materialQuantity < 0 || extraCost < 0) {
            javax.swing.JOptionPane.showMessageDialog(this, "Värden får inte vara negativa.");
            return;
        }

        pendingDecorations.clear();

        String text = "";

        if (selectedMaterial != null && materialQuantity > 0) {
            double materialCost = selectedMaterial.getUnitPrice() * materialQuantity;

            text += "Material: " + selectedMaterial.getName()
                    + ", åtgång " + materialQuantity + " " + selectedMaterial.getUnit()
                    + ", kostnad " + materialCost + " SEK\n";
        }

        if (extraCost > 0) {
            text += "Pristillägg/arbete: " + extraCost + " SEK\n";
        }

        if (!Validator.isBlank(txtNote.getText())) {
            text += "Kommentar: " + txtNote.getText().trim() + "\n";
        }

        boolean addMoreDecorations = true;

        // Användaren får lägga till flera dekorationer tills den väljer nej.
        while (addMoreDecorations) {
            int wantDecoration = javax.swing.JOptionPane.showConfirmDialog(
                    this,
                    "Vill du lägga till dekoration?",
                    "Dekoration",
                    javax.swing.JOptionPane.YES_NO_OPTION
            );

            if (wantDecoration != javax.swing.JOptionPane.YES_OPTION) {
                break;
            }

            javax.swing.JComboBox<Decoration> cmbDecoration = new javax.swing.JComboBox<>();

            for (Decoration d : decorations) {
                cmbDecoration.addItem(d);
            }

            javax.swing.JLabel lblDecorationUnit = new javax.swing.JLabel("Enhet: se vald dekoration");
            javax.swing.JTextField txtDecorationQuantity = new javax.swing.JTextField("1");

            Object[] decorationMessage = {
                "Dekoration:", cmbDecoration,
                lblDecorationUnit,
                "Dekorationsåtgång:", txtDecorationQuantity
            };

            int decorationOption = javax.swing.JOptionPane.showConfirmDialog(
                    this,
                    decorationMessage,
                    "Lägg till dekoration",
                    javax.swing.JOptionPane.OK_CANCEL_OPTION
            );

            if (decorationOption != javax.swing.JOptionPane.OK_OPTION) {
                break;
            }

            Decoration selectedDecoration = (Decoration) cmbDecoration.getSelectedItem();

            if (selectedDecoration == null) {
                javax.swing.JOptionPane.showMessageDialog(this, "Välj en dekoration.");
                break;
            }

            if (!Validator.isValidDouble(txtDecorationQuantity.getText())) {
                javax.swing.JOptionPane.showMessageDialog(
                        this,
                        Validator.invalidDoubleMessage("Dekorationsåtgång")
                );
                break;
            }

            double decorationQuantity = Validator.parseDouble(txtDecorationQuantity.getText());

            if (decorationQuantity <= 0) {
                javax.swing.JOptionPane.showMessageDialog(this, "Dekorationsåtgång måste vara större än 0.");
                break;
            }

            double decorationCost = selectedDecoration.getUnitPrice() * decorationQuantity;

            OrderLineDecoration item = new OrderLineDecoration();
            item.setDecorationId(selectedDecoration.getDecorationId());
            item.setQuantityUsed(decorationQuantity);
            item.setDecorationCost(decorationCost);

            pendingDecorations.add(item);

            text += "Dekoration: " + selectedDecoration.getName()
                    + ", åtgång " + decorationQuantity + " " + selectedDecoration.getUnit()
                    + ", kostnad " + decorationCost + " SEK\n";

            int more = javax.swing.JOptionPane.showConfirmDialog(
                    this,
                    "Vill du lägga till fler dekorationer?",
                    "Fler dekorationer",
                    javax.swing.JOptionPane.YES_NO_OPTION
            );

            addMoreDecorations = more == javax.swing.JOptionPane.YES_OPTION;
        }

        pendingMaterial = selectedMaterial;
        pendingMaterialQuantity = materialQuantity;
        pendingAdjustmentCost = extraCost;
        pendingAdjustmentText = text;

        javax.swing.JOptionPane.showMessageDialog(this, "Anpassning tillagd. Lägg nu till hatten i ordern.");
    }

    /**
     * Rensar alla tillfälliga anpassningar efter att hatten lagts till i ordern.
     */
    private void clearPendingCustomization() {
        pendingMaterial = null;
        pendingMaterialQuantity = 0;
        pendingAdjustmentCost = 0;
        pendingAdjustmentText = "";
        pendingDecorations.clear();
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnBack = new javax.swing.JButton();
        lblTitle = new javax.swing.JLabel();
        lblHatModel = new javax.swing.JLabel();
        cmbHatModel = new javax.swing.JComboBox();
        lblSize = new javax.swing.JLabel();
        cmbSize = new javax.swing.JComboBox();
        lblInventoryStatus = new javax.swing.JLabel();
        lblQuantity = new javax.swing.JLabel();
        txtQuantity = new javax.swing.JTextField();
        btnAddToOrder = new javax.swing.JButton();
        lblCustomer = new javax.swing.JLabel();
        cmbCustomer = new javax.swing.JComboBox();
        btnSelectCustomer = new javax.swing.JButton();
        btnPlaceOrder = new javax.swing.JButton();
        btnExportShippingLabel = new javax.swing.JButton();
        lblPreview = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtOrderPreview = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtOrderSearch = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        cmbOrderStatusFilter = new javax.swing.JComboBox<>();
        btnSearchOrders = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblOrders = new javax.swing.JTable();
        tblOrders.setDefaultEditor(Object.class, null); tblOrders.setRowSelectionAllowed(true); tblOrders.setColumnSelectionAllowed(false); tblOrders.setCellSelectionEnabled(false);
        btnUpdateOrderStatus = new javax.swing.JButton();
        btnDeleteOrder = new javax.swing.JButton();
        btnReturnComplaint = new javax.swing.JButton();
        btnCustomizeHat = new javax.swing.JButton();
        btnExpressOrder = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setSize(new java.awt.Dimension(1920, 1000));

        btnBack.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        btnBack.setText("Tillbaka");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        lblTitle.setText("ORDERHANTERING");

        lblHatModel.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        lblHatModel.setText("Välj hattmodell");

        cmbHatModel.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        cmbHatModel.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbHatModel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbHatModelActionPerformed(evt);
            }
        });

        lblSize.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        lblSize.setText("Välj storlek");

        cmbSize.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        cmbSize.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbSizeActionPerformed(evt);
            }
        });

        lblInventoryStatus.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        lblInventoryStatus.setText("Lagersaldo");

        lblQuantity.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        lblQuantity.setText("Antal");

        txtQuantity.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        txtQuantity.setText("1");
        txtQuantity.setName(""); // NOI18N
        txtQuantity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtQuantityActionPerformed(evt);
            }
        });

        btnAddToOrder.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnAddToOrder.setText("Lägg till i order");
        btnAddToOrder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddToOrderActionPerformed(evt);
            }
        });

        lblCustomer.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        lblCustomer.setText("Välj kund");

        cmbCustomer.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        cmbCustomer.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbCustomer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbCustomerActionPerformed(evt);
            }
        });

        btnSelectCustomer.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnSelectCustomer.setText("Välj kund");
        btnSelectCustomer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectCustomerActionPerformed(evt);
            }
        });

        btnPlaceOrder.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnPlaceOrder.setText("Lägg beställning");
        btnPlaceOrder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPlaceOrderActionPerformed(evt);
            }
        });

        btnExportShippingLabel.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnExportShippingLabel.setText("Skapa fraktsedel");
        btnExportShippingLabel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportShippingLabelActionPerformed(evt);
            }
        });

        lblPreview.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        lblPreview.setText("Orderunderlag");

        txtOrderPreview.setEditable(false);
        txtOrderPreview.setColumns(20);
        txtOrderPreview.setRows(5);
        jScrollPane1.setViewportView(txtOrderPreview);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel1.setText("Orderöversikt");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel2.setText("Sök Order");

        txtOrderSearch.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        txtOrderSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtOrderSearchActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel3.setText("Status");

        cmbOrderStatusFilter.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        cmbOrderStatusFilter.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Alla", "Ny", "Pågående", "Packad", "Skickad", "Färdig", "Retur", "Reklamation", "Avbruten" }));
        cmbOrderStatusFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbOrderStatusFilterActionPerformed(evt);
            }
        });

        btnSearchOrders.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnSearchOrders.setText("Sök ordrar");
        btnSearchOrders.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchOrdersActionPerformed(evt);
            }
        });

        tblOrders.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tblOrders.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Order-ID", "Kund", "Status", "Notering", "Orderdatum", "Totalpris"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tblOrders);
        if (tblOrders.getColumnModel().getColumnCount() > 0) {
            tblOrders.getColumnModel().getColumn(4).setPreferredWidth(100);
        }
        tblOrders.getAccessibleContext().setAccessibleName("");

        btnUpdateOrderStatus.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnUpdateOrderStatus.setText("Ändra orderstatus");
        btnUpdateOrderStatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateOrderStatusActionPerformed(evt);
            }
        });

        btnDeleteOrder.setBackground(new java.awt.Color(255, 102, 102));
        btnDeleteOrder.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnDeleteOrder.setText("Ta bort order");
        btnDeleteOrder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteOrderActionPerformed(evt);
            }
        });

        btnReturnComplaint.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnReturnComplaint.setText("Retur/Reklamation");
        btnReturnComplaint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReturnComplaintActionPerformed(evt);
            }
        });

        btnCustomizeHat.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnCustomizeHat.setText("Anpassa hatt");
        btnCustomizeHat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCustomizeHatActionPerformed(evt);
            }
        });

        btnExpressOrder.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnExpressOrder.setText("Express");
        btnExpressOrder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExpressOrderActionPerformed(evt);
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
                        .addComponent(btnBack, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(645, 645, 645)
                        .addComponent(lblTitle))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblHatModel)
                            .addComponent(cmbHatModel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblInventoryStatus)
                            .addComponent(lblCustomer)
                            .addComponent(cmbCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnSelectCustomer)
                            .addComponent(btnPlaceOrder)
                            .addComponent(btnExportShippingLabel)
                            .addComponent(txtQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblQuantity)
                            .addComponent(btnAddToOrder)
                            .addComponent(lblSize)
                            .addComponent(btnCustomizeHat)
                            .addComponent(btnExpressOrder))
                        .addGap(100, 100, 100)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblPreview)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 555, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 840, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 68, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtOrderSearch, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbOrderStatusFilter, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSearchOrders, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnUpdateOrderStatus, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnDeleteOrder, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnReturnComplaint, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnBack, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(34, 34, 34)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblPreview)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(lblCustomer)))
                    .addComponent(lblTitle))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtOrderSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmbOrderStatusFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnSearchOrders)
                                .addGap(67, 67, 67)
                                .addComponent(btnUpdateOrderStatus)
                                .addGap(18, 18, 18)
                                .addComponent(btnReturnComplaint)
                                .addGap(88, 88, 88)
                                .addComponent(btnDeleteOrder))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(cmbCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnSelectCustomer)
                                .addGap(50, 50, 50)
                                .addComponent(lblHatModel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmbHatModel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblSize)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmbSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblInventoryStatus)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblQuantity)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnCustomizeHat)))
                        .addGap(18, 18, 18)
                        .addComponent(btnAddToOrder)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 202, Short.MAX_VALUE)
                        .addComponent(btnExpressOrder)
                        .addGap(74, 74, 74)
                        .addComponent(btnPlaceOrder)
                        .addGap(18, 18, 18)
                        .addComponent(btnExportShippingLabel))
                    .addComponent(jScrollPane2))
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
     * Körs när användaren trycker Enter i antalsfältet.
     * Ingen särskild funktion behövs här just nu.
     */
    private void txtQuantityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtQuantityActionPerformed
        // Lämnas tom eftersom antal hanteras när användaren klickar på "Lägg till".
    }//GEN-LAST:event_txtQuantityActionPerformed
    /**
     * Laddar storlekar och uppdaterar lagerstatus när användaren byter hattmodell.
     */
    private void cmbHatModelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbHatModelActionPerformed
        loadSizesForSelectedHat();
        showInventoryStatus();
    }//GEN-LAST:event_cmbHatModelActionPerformed
    /**
     * Uppdaterar lagerstatus när storleken ändras.
     */
    private void cmbSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbSizeActionPerformed
        showInventoryStatus();
    }//GEN-LAST:event_cmbSizeActionPerformed
    /**
     * Lägger till vald hatt i ordern.
     */
    private void btnAddToOrderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddToOrderActionPerformed
        addSelectedHatToOrder();
    }//GEN-LAST:event_btnAddToOrderActionPerformed
    /**
     * Körs när vald kund ändras i listan.
     * Ingen särskild funktion behövs här eftersom kunden väljs med separat knapp.
     */
    private void cmbCustomerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbCustomerActionPerformed
            // Lämnas tom eftersom kundvalet bekräftas via knappen "Välj kund".
    }//GEN-LAST:event_cmbCustomerActionPerformed
    /**
     * Väljer kund för aktuell order.
     */
    private void btnSelectCustomerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelectCustomerActionPerformed
        selectCustomerForOrder();
    }//GEN-LAST:event_btnSelectCustomerActionPerformed
    /**
     * Sparar aktuell order.
     */
    private void btnPlaceOrderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPlaceOrderActionPerformed
        placeOrder();
    }//GEN-LAST:event_btnPlaceOrderActionPerformed
    /**
     * Exporterar fraktsedel.
     */
    private void btnExportShippingLabelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportShippingLabelActionPerformed
        exportShippingLabel();
    }//GEN-LAST:event_btnExportShippingLabelActionPerformed
    /**
     * Körs när användaren trycker Enter i sökfältet.
     */
    private void txtOrderSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtOrderSearchActionPerformed
        loadOrdersTable();
    }//GEN-LAST:event_txtOrderSearchActionPerformed
    /**
     * Tar bort vald order och återställer lagret.
     */
    private void btnDeleteOrderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteOrderActionPerformed
    int selectedRow = tblOrders.getSelectedRow();

        if (selectedRow == -1) {
            javax.swing.JOptionPane.showMessageDialog(this, "Välj en order först.");
            return;
        }

        String orderId = tblOrders.getValueAt(selectedRow, 0).toString();

        int confirm = javax.swing.JOptionPane.showConfirmDialog(
                this,
                "Är du säker på att du vill ta bort ordern?\nLagret återställs för hattarna.",
                "Bekräfta borttagning",
                javax.swing.JOptionPane.YES_NO_OPTION
        );

        if (confirm != javax.swing.JOptionPane.YES_OPTION) {
            return;
        }

        OrderLineRepository lineRepo = new OrderLineRepository();
        OrderRepository orderRepo = new OrderRepository();
        HatInventoryRepository inventoryRepo = new HatInventoryRepository();
        OrderLineMaterialRepository materialRepo = new OrderLineMaterialRepository();
        OrderLineDecorationRepository decorationRepo = new OrderLineDecorationRepository();

        java.util.List<OrderLine> lines = lineRepo.getOrderLinesByOrderId(orderId);

        // Återställer lagret och tar bort kopplade material/dekorationer för varje orderrad.
        for (OrderLine line : lines) {
            inventoryRepo.increaseQuantity(
                    line.getHatModelId(),
                    line.getSize(),
                    line.getQuantity()
            );

            materialRepo.deleteMaterialsByOrderLineId(line.getOrderLineId());
            decorationRepo.deleteDecorationsByOrderLineId(line.getOrderLineId());
        }

        boolean linesDeleted = lineRepo.deleteOrderLinesByOrderId(orderId);
        boolean orderDeleted = orderRepo.deleteOrder(orderId);

        if (linesDeleted && orderDeleted) {
            javax.swing.JOptionPane.showMessageDialog(this, "Order borttagen och lager återställt.");
            loadOrdersTable();
            txtOrderPreview.setText("");
        } else {
            javax.swing.JOptionPane.showMessageDialog(this, "Order kunde inte tas bort korrekt.");
        }
    }//GEN-LAST:event_btnDeleteOrderActionPerformed
    /**
     * Uppdaterar status för vald order.
     */
    private void btnUpdateOrderStatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateOrderStatusActionPerformed
    int selectedRow = tblOrders.getSelectedRow();

        if (selectedRow == -1) {
            javax.swing.JOptionPane.showMessageDialog(this, "Välj en order först.");
            return;
        }

        String orderId = tblOrders.getValueAt(selectedRow, 0).toString();

        String[] statuses = {
            "Ny", "Pågående", "Packad", "Skickad", "Färdig", "Retur", "Reklamation", "Avbruten"
        };

        javax.swing.JComboBox<String> cmbStatus = new javax.swing.JComboBox<>(statuses);
        javax.swing.JTextField txtNote = new javax.swing.JTextField();

        Object[] message = {
            "Ny status:", cmbStatus,
            "Notering:", txtNote
        };

        int option = javax.swing.JOptionPane.showConfirmDialog(
                this,
                message,
                "Ändra orderstatus",
                javax.swing.JOptionPane.OK_CANCEL_OPTION
        );

        if (option != javax.swing.JOptionPane.OK_OPTION) {
            return;
        }

        String newStatus = cmbStatus.getSelectedItem().toString();
        String note = txtNote.getText().trim();

        OrderService service = new OrderService();
        boolean success = service.updateOrderStatus(orderId, newStatus, note);

        if (success) {
            javax.swing.JOptionPane.showMessageDialog(this, "Orderstatus uppdaterad.");
            loadOrdersTable();
        } else {
            javax.swing.JOptionPane.showMessageDialog(this, "Orderstatus kunde inte uppdateras.");
        }
    }//GEN-LAST:event_btnUpdateOrderStatusActionPerformed
    /**
     * Söker fram ordrar i orderöversikten.
     */
    private void btnSearchOrdersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchOrdersActionPerformed
        loadOrdersTable();
    }//GEN-LAST:event_btnSearchOrdersActionPerformed
    /**
     * Filtrerar ordrar på status.
     */
    private void cmbOrderStatusFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbOrderStatusFilterActionPerformed
        loadOrdersTable();
    }//GEN-LAST:event_cmbOrderStatusFilterActionPerformed
    /**
     * Markerar vald order som retur eller reklamation.
     */
    private void btnReturnComplaintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReturnComplaintActionPerformed
    markReturnOrComplaint();
    }//GEN-LAST:event_btnReturnComplaintActionPerformed
    /**
     * Öppnar dialog för att anpassa vald hatt.
     */
    private void btnCustomizeHatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCustomizeHatActionPerformed
        customizeSelectedHat();
    }//GEN-LAST:event_btnCustomizeHatActionPerformed
    /**
     * Markerar den pågående ordern som expressorder.
     */
    private void btnExpressOrderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExpressOrderActionPerformed
    orderDraft.setExpressOrder(true);
        updateOrderPreview();
        javax.swing.JOptionPane.showMessageDialog(this, "Ordern markerades som expressorder.");
    }//GEN-LAST:event_btnExpressOrderActionPerformed
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
            java.util.logging.Logger.getLogger(OrderFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(OrderFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(OrderFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(OrderFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new OrderFrame().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddToOrder;
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnCustomizeHat;
    private javax.swing.JButton btnDeleteOrder;
    private javax.swing.JButton btnExportShippingLabel;
    private javax.swing.JButton btnExpressOrder;
    private javax.swing.JButton btnPlaceOrder;
    private javax.swing.JButton btnReturnComplaint;
    private javax.swing.JButton btnSearchOrders;
    private javax.swing.JButton btnSelectCustomer;
    private javax.swing.JButton btnUpdateOrderStatus;
    private javax.swing.JComboBox cmbCustomer;
    private javax.swing.JComboBox cmbHatModel;
    private javax.swing.JComboBox<String> cmbOrderStatusFilter;
    private javax.swing.JComboBox cmbSize;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblCustomer;
    private javax.swing.JLabel lblHatModel;
    private javax.swing.JLabel lblInventoryStatus;
    private javax.swing.JLabel lblPreview;
    private javax.swing.JLabel lblQuantity;
    private javax.swing.JLabel lblSize;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JTable tblOrders;
    private javax.swing.JTextArea txtOrderPreview;
    private javax.swing.JTextField txtOrderSearch;
    private javax.swing.JTextField txtQuantity;
    // End of variables declaration//GEN-END:variables
}