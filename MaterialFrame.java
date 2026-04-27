package hattmakaren;

/**
 * Fönster för att hantera material och dekorationer.
 *
 * Klassen visar material/dekorationer i en tabell och låter användaren:
 * - lägga till material och dekorationer
 * - ändra lagersaldo
 * - koppla material till hattmodeller
 * - visa materialåtgång
 * - skapa beställningsunderlag
 * - markera material/dekorationer som beställda
 */
public class MaterialFrame extends javax.swing.JFrame {

    private String currentView = "REGISTER";

    /**
     * Skapar materialfönstret, ställer in storlek och laddar tabellen.
     */
    public MaterialFrame() {
        initComponents();

        setPreferredSize(new java.awt.Dimension(1920, 1080));
        setMinimumSize(new java.awt.Dimension(1200, 800));
        setSize(1920, 1080);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jScrollPane1.setMaximumSize(null);
        jScrollPane1.setPreferredSize(new java.awt.Dimension(1400, 750));

        setupMaterialTable();
        loadAll();
    }

    /**
     * Skapar ett enkelt material-ID baserat på aktuell tid.
     */
    private String generateMaterialId() {
        return "mat-" + System.currentTimeMillis();
    }

    /**
     * Skapar ett enkelt dekorations-ID baserat på aktuell tid.
     */
    private String generateDecorationId() {
        return "dec-" + System.currentTimeMillis();
    }

    /**
     * Skapar ett enkelt beställnings-ID för material.
     */
    private String generateMaterialOrderId() {
        return "mo-" + System.currentTimeMillis();
    }

    /**
     * Skapar ett enkelt beställnings-ID för dekorationer.
     */
    private String generateDecorationOrderId() {
        return "do-" + System.currentTimeMillis();
    }

    /**
     * Skapar grundtabellen som används för material och dekorationer.
     */
    private void setupMaterialTable() {
        currentView = "REGISTER";

        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(
                new Object[]{
                    "Typ", "ID", "Namn", "Kategori/Typ", "Enhet",
                    "Pris/enhet", "Lagersaldo", "Leverantör", "Notering"
                },
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblMaterial.setModel(model);
        tblMaterial.setRowSelectionAllowed(true);
        tblMaterial.setColumnSelectionAllowed(false);
        tblMaterial.setCellSelectionEnabled(false);
    }

    /**
     * Laddar både material och dekorationer till tabellen.
     */
    private void loadAll() {
        javax.swing.table.DefaultTableModel model =
                (javax.swing.table.DefaultTableModel) tblMaterial.getModel();

        model.setRowCount(0);

        loadMaterialsIntoTable(model);
        loadDecorationsIntoTable(model);
    }

    /**
     * Laddar endast material till tabellen.
     */
    private void loadMaterials() {
        javax.swing.table.DefaultTableModel model =
                (javax.swing.table.DefaultTableModel) tblMaterial.getModel();

        model.setRowCount(0);
        loadMaterialsIntoTable(model);
    }

    /**
     * Laddar endast dekorationer till tabellen.
     */
    private void loadDecorations() {
        javax.swing.table.DefaultTableModel model =
                (javax.swing.table.DefaultTableModel) tblMaterial.getModel();

        model.setRowCount(0);
        loadDecorationsIntoTable(model);
    }

    /**
     * Hämtar alla material från databasen och lägger in dem i tabellen.
     */
    private void loadMaterialsIntoTable(javax.swing.table.DefaultTableModel model) {
        MaterialRepository repo = new MaterialRepository();
        java.util.List<Material> materials = repo.getAllMaterials();

        for (Material m : materials) {
            model.addRow(new Object[]{
                "Material",
                m.getMaterialId(),
                Validator.safeName(m.getName()),
                Validator.safeText(m.getMaterialType()),
                Validator.safeText(m.getUnit()),
                m.getUnitPrice(),
                m.getQuantityInStock(),
                Validator.safeText(m.getSupplier()),
                Validator.safeText(m.getNote())
            });
        }
    }

    /**
     * Hämtar alla dekorationer från databasen och lägger in dem i tabellen.
     */
    private void loadDecorationsIntoTable(javax.swing.table.DefaultTableModel model) {
        DecorationRepository repo = new DecorationRepository();
        java.util.List<Decoration> decorations = repo.getAllDecorations();

        for (Decoration d : decorations) {
            model.addRow(new Object[]{
                "Dekoration",
                d.getDecorationId(),
                Validator.safeName(d.getName()),
                Validator.safeText(d.getDecorationType()),
                Validator.safeText(d.getUnit()),
                d.getUnitPrice(),
                d.getQuantityInStock(),
                Validator.safeText(d.getSupplier()),
                Validator.safeText(d.getNote())
            });
        }
    }

    /**
     * Söker i både material och dekorationer.
     *
     * Sökningen görs genom att flera fält slås ihop till en text,
     * som sedan jämförs med användarens sökord.
     */
    private void searchAll() {
        setupMaterialTable();

        String searchText = txtSearch.getText().trim().toLowerCase();

        javax.swing.table.DefaultTableModel model =
                (javax.swing.table.DefaultTableModel) tblMaterial.getModel();

        model.setRowCount(0);

        MaterialRepository materialRepo = new MaterialRepository();
        DecorationRepository decorationRepo = new DecorationRepository();

        for (Material m : materialRepo.getAllMaterials()) {
            String searchableText =
                    Validator.safeText(m.getMaterialId()) + " "
                    + Validator.safeName(m.getName()) + " "
                    + Validator.safeText(m.getMaterialType()) + " "
                    + Validator.safeText(m.getUnit()) + " "
                    + m.getUnitPrice() + " "
                    + m.getQuantityInStock() + " "
                    + Validator.safeText(m.getSupplier()) + " "
                    + Validator.safeText(m.getNote());

            if (searchableText.toLowerCase().contains(searchText)) {
                model.addRow(new Object[]{
                    "Material",
                    m.getMaterialId(),
                    Validator.safeName(m.getName()),
                    Validator.safeText(m.getMaterialType()),
                    Validator.safeText(m.getUnit()),
                    formatNumber(m.getUnitPrice()),
                    formatNumber(m.getQuantityInStock()),
                    Validator.safeText(m.getSupplier()),
                    Validator.safeText(m.getNote())
                });
            }
        }

        for (Decoration d : decorationRepo.getAllDecorations()) {
            String searchableText =
                    Validator.safeText(d.getDecorationId()) + " "
                    + Validator.safeName(d.getName()) + " "
                    + Validator.safeText(d.getDecorationType()) + " "
                    + Validator.safeText(d.getUnit()) + " "
                    + d.getUnitPrice() + " "
                    + d.getQuantityInStock() + " "
                    + Validator.safeText(d.getSupplier()) + " "
                    + Validator.safeText(d.getNote());

            if (searchableText.toLowerCase().contains(searchText)) {
                model.addRow(new Object[]{
                    "Dekoration",
                    d.getDecorationId(),
                    Validator.safeName(d.getName()),
                    Validator.safeText(d.getDecorationType()),
                    Validator.safeText(d.getUnit()),
                    formatNumber(d.getUnitPrice()),
                    formatNumber(d.getQuantityInStock()),
                    Validator.safeText(d.getSupplier()),
                    Validator.safeText(d.getNote())
                });
            }
        }
    }

    /**
     * Öppnar en dialogruta där användaren kan lägga till nytt material.
     */
    private void addMaterial() {
        javax.swing.JTextField txtName = new javax.swing.JTextField();
        javax.swing.JTextField txtType = new javax.swing.JTextField();
        javax.swing.JTextField txtUnit = new javax.swing.JTextField();
        javax.swing.JTextField txtUnitPrice = new javax.swing.JTextField();
        javax.swing.JTextField txtQuantity = new javax.swing.JTextField();
        javax.swing.JTextField txtSupplier = new javax.swing.JTextField();
        javax.swing.JTextField txtNote = new javax.swing.JTextField();

        Object[] message = {
            "Namn:", txtName,
            "Materialtyp:", txtType,
            "Enhet (st, m, m2):", txtUnit,
            "Pris per enhet:", txtUnitPrice,
            "Lagersaldo:", txtQuantity,
            "Leverantör:", txtSupplier,
            "Notering:", txtNote
        };

        int option = javax.swing.JOptionPane.showConfirmDialog(
                this,
                message,
                "Lägg till material",
                javax.swing.JOptionPane.OK_CANCEL_OPTION
        );

        if (option != javax.swing.JOptionPane.OK_OPTION) {
            return;
        }

        if (Validator.isBlank(txtName.getText())) {
            javax.swing.JOptionPane.showMessageDialog(this, Validator.requiredFieldMessage("Namn"));
            return;
        }

        if (Validator.isBlank(txtType.getText())) {
            javax.swing.JOptionPane.showMessageDialog(this, Validator.requiredFieldMessage("Materialtyp"));
            return;
        }

        if (Validator.isBlank(txtUnit.getText())) {
            javax.swing.JOptionPane.showMessageDialog(this, Validator.requiredFieldMessage("Enhet"));
            return;
        }

        if (!Validator.isValidDouble(txtUnitPrice.getText())) {
            javax.swing.JOptionPane.showMessageDialog(this, Validator.invalidDoubleMessage("Pris per enhet"));
            return;
        }

        if (!Validator.isValidDouble(txtQuantity.getText())) {
            javax.swing.JOptionPane.showMessageDialog(this, Validator.invalidDoubleMessage("Lagersaldo"));
            return;
        }

        Material material = new Material();
        material.setMaterialId(generateMaterialId());
        material.setName(txtName.getText().trim());
        material.setMaterialType(txtType.getText().trim());
        material.setUnit(txtUnit.getText().trim());
        material.setUnitPrice(Validator.parseDouble(txtUnitPrice.getText()));
        material.setQuantityInStock(Validator.parseDouble(txtQuantity.getText()));
        material.setSupplier(txtSupplier.getText().trim());
        material.setNote(txtNote.getText().trim());
        material.setActive(true);

        MaterialRepository repo = new MaterialRepository();
        boolean success = repo.insertMaterial(material);

        if (success) {
            javax.swing.JOptionPane.showMessageDialog(this, "Material sparat.");
            loadAll();
        } else {
            javax.swing.JOptionPane.showMessageDialog(this, "Material kunde inte sparas.");
        }
    }

    /**
     * Öppnar en dialogruta där användaren kan lägga till ny dekoration.
     */
    private void addDecoration() {
        javax.swing.JTextField txtName = new javax.swing.JTextField();
        javax.swing.JTextField txtType = new javax.swing.JTextField();
        javax.swing.JTextField txtUnit = new javax.swing.JTextField();
        javax.swing.JTextField txtUnitPrice = new javax.swing.JTextField();
        javax.swing.JTextField txtQuantity = new javax.swing.JTextField();
        javax.swing.JTextField txtSupplier = new javax.swing.JTextField();
        javax.swing.JTextField txtNote = new javax.swing.JTextField();

        Object[] message = {
            "Namn:", txtName,
            "Dekorationstyp:", txtType,
            "Enhet (st, m):", txtUnit,
            "Pris per enhet:", txtUnitPrice,
            "Lagersaldo:", txtQuantity,
            "Leverantör:", txtSupplier,
            "Notering:", txtNote
        };

        int option = javax.swing.JOptionPane.showConfirmDialog(
                this,
                message,
                "Lägg till dekoration",
                javax.swing.JOptionPane.OK_CANCEL_OPTION
        );

        if (option != javax.swing.JOptionPane.OK_OPTION) {
            return;
        }

        if (Validator.isBlank(txtName.getText())) {
            javax.swing.JOptionPane.showMessageDialog(this, Validator.requiredFieldMessage("Namn"));
            return;
        }

        if (Validator.isBlank(txtType.getText())) {
            javax.swing.JOptionPane.showMessageDialog(this, Validator.requiredFieldMessage("Dekorationstyp"));
            return;
        }

        if (Validator.isBlank(txtUnit.getText())) {
            javax.swing.JOptionPane.showMessageDialog(this, Validator.requiredFieldMessage("Enhet"));
            return;
        }

        if (!Validator.isValidDouble(txtUnitPrice.getText())) {
            javax.swing.JOptionPane.showMessageDialog(this, Validator.invalidDoubleMessage("Pris per enhet"));
            return;
        }

        if (!Validator.isValidDouble(txtQuantity.getText())) {
            javax.swing.JOptionPane.showMessageDialog(this, Validator.invalidDoubleMessage("Lagersaldo"));
            return;
        }

        Decoration decoration = new Decoration();
        decoration.setDecorationId(generateDecorationId());
        decoration.setName(txtName.getText().trim());
        decoration.setDecorationType(txtType.getText().trim());
        decoration.setUnit(txtUnit.getText().trim());
        decoration.setUnitPrice(Validator.parseDouble(txtUnitPrice.getText()));
        decoration.setQuantityInStock(Validator.parseDouble(txtQuantity.getText()));
        decoration.setSupplier(txtSupplier.getText().trim());
        decoration.setNote(txtNote.getText().trim());
        decoration.setActive(true);

        DecorationRepository repo = new DecorationRepository();
        boolean success = repo.insertDecoration(decoration);

        if (success) {
            javax.swing.JOptionPane.showMessageDialog(this, "Dekoration sparad.");
            loadAll();
        } else {
            javax.swing.JOptionPane.showMessageDialog(this, "Dekoration kunde inte sparas.");
        }
    }

    /**
     * Ändrar lagersaldo för vald material- eller dekorationsrad.
     */
    private void editSelected() {
        int selectedRow = tblMaterial.getSelectedRow();

        if (selectedRow == -1) {
            javax.swing.JOptionPane.showMessageDialog(this, "Välj en rad först.");
            return;
        }

        String type = tblMaterial.getValueAt(selectedRow, 0).toString();
        String id = tblMaterial.getValueAt(selectedRow, 1).toString();
        String currentQuantity = tblMaterial.getValueAt(selectedRow, 6).toString();

        javax.swing.JTextField txtQuantity = new javax.swing.JTextField(currentQuantity);

        Object[] message = {
            "Nytt lagersaldo:", txtQuantity
        };

        int option = javax.swing.JOptionPane.showConfirmDialog(
                this,
                message,
                "Ändra lagersaldo",
                javax.swing.JOptionPane.OK_CANCEL_OPTION
        );

        if (option != javax.swing.JOptionPane.OK_OPTION) {
            return;
        }

        if (!Validator.isValidDouble(txtQuantity.getText())) {
            javax.swing.JOptionPane.showMessageDialog(this, Validator.invalidDoubleMessage("Lagersaldo"));
            return;
        }

        double newQuantity = Validator.parseDouble(txtQuantity.getText());

        if (newQuantity < 0) {
            javax.swing.JOptionPane.showMessageDialog(this, "Lagersaldo får inte vara negativt.");
            return;
        }

        boolean success = false;

        if (type.equals("Material")) {
            MaterialRepository repo = new MaterialRepository();
            success = repo.updateQuantity(id, newQuantity);
        } else if (type.equals("Dekoration")) {
            DecorationRepository repo = new DecorationRepository();
            success = repo.updateQuantity(id, newQuantity);
        }

        if (success) {
            javax.swing.JOptionPane.showMessageDialog(this, "Lagersaldo uppdaterat.");
            loadAll();
        } else {
            javax.swing.JOptionPane.showMessageDialog(this, "Lagersaldo kunde inte uppdateras.");
        }
    }

    /**
     * Kopplar ett material till en hattmodell.
     */
    private void linkMaterialToHatModel() {
        HatModelRepository hatRepo = new HatModelRepository();
        MaterialRepository materialRepo = new MaterialRepository();

        java.util.List<HatModel> hatModels = hatRepo.getAllHatModels();
        java.util.List<Material> materials = materialRepo.getAllMaterials();

        javax.swing.JComboBox<HatModel> cmbHatModel = new javax.swing.JComboBox<>();
        javax.swing.JComboBox<Material> cmbMaterial = new javax.swing.JComboBox<>();
        javax.swing.JTextField txtQuantityNeeded = new javax.swing.JTextField();
        javax.swing.JTextField txtNote = new javax.swing.JTextField();
        javax.swing.JCheckBox chkStandard = new javax.swing.JCheckBox("Standardmaterial", true);

        for (HatModel h : hatModels) {
            cmbHatModel.addItem(h);
        }

        for (Material m : materials) {
            cmbMaterial.addItem(m);
        }

        Object[] message = {
            "Hattmodell:", cmbHatModel,
            "Material:", cmbMaterial,
            "Materialåtgång:", txtQuantityNeeded,
            chkStandard,
            "Notering:", txtNote
        };

        int option = javax.swing.JOptionPane.showConfirmDialog(
                this,
                message,
                "Koppla material till hattmodell",
                javax.swing.JOptionPane.OK_CANCEL_OPTION
        );

        if (option != javax.swing.JOptionPane.OK_OPTION) {
            return;
        }

        HatModel selectedHat = (HatModel) cmbHatModel.getSelectedItem();
        Material selectedMaterial = (Material) cmbMaterial.getSelectedItem();

        if (selectedHat == null || selectedMaterial == null) {
            javax.swing.JOptionPane.showMessageDialog(this, "Välj hattmodell och material.");
            return;
        }

        if (!Validator.isValidDouble(txtQuantityNeeded.getText())) {
            javax.swing.JOptionPane.showMessageDialog(this, Validator.invalidDoubleMessage("Materialåtgång"));
            return;
        }

        double quantityNeeded = Validator.parseDouble(txtQuantityNeeded.getText());

        if (quantityNeeded <= 0) {
            javax.swing.JOptionPane.showMessageDialog(this, "Materialåtgång måste vara större än 0.");
            return;
        }

        HatModelMaterial item = new HatModelMaterial();
        item.setHatModelId(selectedHat.getHatModelId());
        item.setMaterialId(selectedMaterial.getMaterialId());
        item.setQuantityNeeded(quantityNeeded);
        item.setStandard(chkStandard.isSelected());
        item.setNote(txtNote.getText().trim());

        HatModelMaterialRepository repo = new HatModelMaterialRepository();
        boolean success = repo.insertHatModelMaterial(item);

        if (success) {
            javax.swing.JOptionPane.showMessageDialog(this, "Material kopplat till hattmodell.");
        } else {
            javax.swing.JOptionPane.showMessageDialog(this, "Kopplingen kunde inte sparas.");
        }
    }

    /**
     * Visar vilka material som är kopplade till en vald hattmodell.
     */
    private void showMaterialsForHatModel() {
        setupMaterialTable();

        HatModelRepository hatRepo = new HatModelRepository();
        MaterialRepository materialRepo = new MaterialRepository();
        HatModelMaterialRepository linkRepo = new HatModelMaterialRepository();

        java.util.List<HatModel> hatModels = hatRepo.getAllHatModels();
        javax.swing.JComboBox<HatModel> cmbHatModel = new javax.swing.JComboBox<>();

        for (HatModel h : hatModels) {
            cmbHatModel.addItem(h);
        }

        Object[] message = {
            "Välj hattmodell:", cmbHatModel
        };

        int option = javax.swing.JOptionPane.showConfirmDialog(
                this,
                message,
                "Visa material för hattmodell",
                javax.swing.JOptionPane.OK_CANCEL_OPTION
        );

        if (option != javax.swing.JOptionPane.OK_OPTION) {
            return;
        }

        HatModel selectedHat = (HatModel) cmbHatModel.getSelectedItem();

        if (selectedHat == null) {
            javax.swing.JOptionPane.showMessageDialog(this, "Välj en hattmodell.");
            return;
        }

        java.util.List<HatModelMaterial> links =
                linkRepo.getMaterialsByHatModelId(selectedHat.getHatModelId());

        javax.swing.table.DefaultTableModel model =
                (javax.swing.table.DefaultTableModel) tblMaterial.getModel();

        model.setRowCount(0);

        for (HatModelMaterial link : links) {
            Material material = materialRepo.getMaterialById(link.getMaterialId());

            String materialName = "Okänt material";
            String materialType = "";
            String unit = "";
            double unitPrice = 0;

            if (material != null) {
                materialName = Validator.safeName(material.getName());
                materialType = Validator.safeText(material.getMaterialType());
                unit = Validator.safeText(material.getUnit());
                unitPrice = material.getUnitPrice();
            }

            model.addRow(new Object[]{
                "Hattmaterial",
                link.getMaterialId(),
                materialName,
                materialType,
                unit,
                unitPrice,
                link.getQuantityNeeded(),
                link.isStandard() ? "Standard" : "Alternativ",
                Validator.safeText(link.getNote())
            });
        }

        if (links.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, "Ingen materialkoppling finns för vald hattmodell.");
        }
    }

    /**
     * Laddar faktisk materialåtgång till tabellen.
     */
    private void loadMaterialUsage(java.util.List<OrderLineMaterial> usageList) {
        setupMaterialUsageTable();

        javax.swing.table.DefaultTableModel model =
                (javax.swing.table.DefaultTableModel) tblMaterial.getModel();

        MaterialRepository materialRepo = new MaterialRepository();

        for (OrderLineMaterial item : usageList) {
            Material material = materialRepo.getMaterialById(item.getMaterialId());

            String materialName = "Okänt material";

            if (material != null) {
                materialName = Validator.safeName(material.getName());
            }

            model.addRow(new Object[]{
                item.getId(),
                item.getMaterialId(),
                materialName,
                item.getQuantityUsed(),
                item.getMaterialCost(),
                item.isOrdered() ? "Ja" : "Nej"
            });
        }
    }

    /**
     * Skapar tabellen som används för att visa total åtgång.
     */
    private void setupUsageTable() {
        currentView = "USAGE";

        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(
                new Object[]{
                    "Typ", "ID", "Namn", "Åtgång", "Enhet", "Lager", "Behöver beställas"
                },
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblMaterial.setModel(model);
    }

    /**
     * Skapar tabellen som visar materialåtgång per orderrad.
     */
    private void setupMaterialUsageTable() {
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(
                new Object[]{
                    "ID", "Material-ID", "Material", "Åtgång", "Kostnad", "Beställt"
                },
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblMaterial.setModel(model);
    }

    /**
     * Visar total materialåtgång och räknar ut om något behöver beställas.
     */
    private void showMaterialUsage() {
        setupUsageTable();

        OrderLineMaterialRepository usageRepo = new OrderLineMaterialRepository();
        MaterialRepository materialRepo = new MaterialRepository();

        java.util.List<OrderLineMaterial> usageList = usageRepo.getAllOrderLineMaterials();

        javax.swing.table.DefaultTableModel model =
                (javax.swing.table.DefaultTableModel) tblMaterial.getModel();

        java.util.HashMap<String, Double> totals = new java.util.HashMap<>();

        // Samlar ihop total åtgång per material-ID.
        for (OrderLineMaterial usage : usageList) {
            String materialId = usage.getMaterialId();
            double oldValue = totals.getOrDefault(materialId, 0.0);

            totals.put(materialId, oldValue + usage.getQuantityUsed());
        }

        for (String materialId : totals.keySet()) {
            Material material = materialRepo.getMaterialById(materialId);

            if (material == null) {
                continue;
            }

            double used = totals.get(materialId);
            double stock = material.getQuantityInStock();
            double needToOrder = MaterialNeedCalculator.calculateNeedToOrder(used, stock);

            model.addRow(new Object[]{
                "Material",
                material.getMaterialId(),
                Validator.safeName(material.getName()),
                formatNumber(used),
                Validator.safeText(material.getUnit()),
                formatNumber(stock),
                formatNumber(needToOrder)
            });
        }
    }

    /**
     * Visar total dekorationsåtgång och räknar ut om något behöver beställas.
     */
    private void showDecorationUsage() {
        setupUsageTable();

        OrderLineDecorationRepository usageRepo = new OrderLineDecorationRepository();
        DecorationRepository decorationRepo = new DecorationRepository();

        java.util.List<OrderLineDecoration> usageList = usageRepo.getAllOrderLineDecorations();

        javax.swing.table.DefaultTableModel model =
                (javax.swing.table.DefaultTableModel) tblMaterial.getModel();

        java.util.HashMap<String, Double> totals = new java.util.HashMap<>();

        // Samlar ihop total åtgång per dekorations-ID.
        for (OrderLineDecoration usage : usageList) {
            String decorationId = usage.getDecorationId();
            double oldValue = totals.getOrDefault(decorationId, 0.0);

            totals.put(decorationId, oldValue + usage.getQuantityUsed());
        }

        for (String decorationId : totals.keySet()) {
            Decoration decoration = decorationRepo.getDecorationById(decorationId);

            if (decoration == null) {
                continue;
            }

            double used = totals.get(decorationId);
            double stock = decoration.getQuantityInStock();
            double needToOrder = MaterialNeedCalculator.calculateNeedToOrder(used, stock);

            model.addRow(new Object[]{
                "Dekoration",
                decoration.getDecorationId(),
                Validator.safeName(decoration.getName()),
                formatNumber(used),
                Validator.safeText(decoration.getUnit()),
                formatNumber(stock),
                formatNumber(needToOrder)
            });
        }
    }

    /**
     * Exporterar nuvarande tabell som ett enkelt textunderlag.
     */
    private void exportMaterialReport() {
        if (tblMaterial.getRowCount() == 0) {
            javax.swing.JOptionPane.showMessageDialog(this, "Det finns inget underlag att exportera.");
            return;
        }

        javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
        fileChooser.setDialogTitle("Spara beställningsunderlag");

        String fileName = "materialunderlag_" + System.currentTimeMillis() + ".txt";
        fileChooser.setSelectedFile(new java.io.File(fileName));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection != javax.swing.JFileChooser.APPROVE_OPTION) {
            return;
        }

        java.io.File fileToSave = fileChooser.getSelectedFile();

        try (java.io.PrintWriter writer = new java.io.PrintWriter(fileToSave, "UTF-8")) {
            writer.println("MATERIAL- OCH DEKORATIONSUNDERLAG");
            writer.println("========================================");
            writer.println();

            javax.swing.table.TableModel model = tblMaterial.getModel();

            for (int row = 0; row < model.getRowCount(); row++) {
                writer.println("Rad " + (row + 1));
                writer.println("----------------------------------------");

                for (int col = 0; col < model.getColumnCount(); col++) {
                    writer.println(model.getColumnName(col) + ": " + model.getValueAt(row, col));
                }

                writer.println();
            }

            writer.println("========================================");
            writer.println("Export skapad: " + java.time.LocalDate.now());

            javax.swing.JOptionPane.showMessageDialog(this, "Beställningsunderlag exporterades.");

        } catch (Exception e) {
            javax.swing.JOptionPane.showMessageDialog(this, "Fel vid export: " + e.getMessage());
        }
    }

    /**
     * Markerar valt material som beställt.
     */
    private void markMaterialOrdered() {
        int selectedRow = tblMaterial.getSelectedRow();

        if (selectedRow == -1) {
            javax.swing.JOptionPane.showMessageDialog(this, "Välj en rad först.");
            return;
        }

        String type = tblMaterial.getValueAt(selectedRow, 0).toString();

        if (!type.equals("Material")) {
            javax.swing.JOptionPane.showMessageDialog(this, "Välj en materialrad från materialåtgång.");
            return;
        }

        String materialId = tblMaterial.getValueAt(selectedRow, 1).toString();
        double needToOrder = Double.parseDouble(tblMaterial.getValueAt(selectedRow, 6).toString());

        if (needToOrder <= 0) {
            int confirm = javax.swing.JOptionPane.showConfirmDialog(
                    this,
                    "Mer än 50% finns kvar i lager. Vill du beställa ändå?",
                    "Beställa ändå?",
                    javax.swing.JOptionPane.YES_NO_OPTION
            );

            if (confirm != javax.swing.JOptionPane.YES_OPTION) {
                return;
            }
        }

        String startQuantity = String.valueOf(needToOrder);

        if (needToOrder <= 0) {
            startQuantity = "";
        }

        javax.swing.JTextField txtQuantity = new javax.swing.JTextField(startQuantity);
        javax.swing.JTextField txtNote = new javax.swing.JTextField();

        Object[] message = {
            "Antal att beställa:", txtQuantity,
            "Notering:", txtNote
        };

        int option = javax.swing.JOptionPane.showConfirmDialog(
                this,
                message,
                "Markera material som beställt",
                javax.swing.JOptionPane.OK_CANCEL_OPTION
        );

        if (option != javax.swing.JOptionPane.OK_OPTION) {
            return;
        }

        if (!Validator.isValidDouble(txtQuantity.getText())) {
            javax.swing.JOptionPane.showMessageDialog(this, Validator.invalidDoubleMessage("Antal"));
            return;
        }

        double quantity = Validator.parseDouble(txtQuantity.getText());

        if (quantity <= 0) {
            javax.swing.JOptionPane.showMessageDialog(this, "Antal måste vara större än 0.");
            return;
        }

        MaterialOrder order = new MaterialOrder();
        String orderId = generateMaterialOrderId();

        order.setMaterialOrderId(orderId);
        order.setStatus("Beställd");
        order.setNote(txtNote.getText().trim());

        MaterialOrderRepository repo = new MaterialOrderRepository();

        boolean orderSaved = repo.insertMaterialOrder(order);
        boolean lineSaved = false;

        // Orderraden sparas bara om själva beställningen först kunde sparas.
        if (orderSaved) {
            lineSaved = repo.insertMaterialOrderLine(orderId, materialId, quantity);
        }

        if (orderSaved && lineSaved) {
            javax.swing.JOptionPane.showMessageDialog(this, "Material markerat som beställt.");
            showOrderedMaterials();
        } else {
            javax.swing.JOptionPane.showMessageDialog(this, "Materialbeställningen kunde inte sparas.");
        }
    }

    /**
     * Markerar vald dekoration som beställd.
     */
    private void markDecorationOrdered() {
        int selectedRow = tblMaterial.getSelectedRow();

        if (selectedRow == -1) {
            javax.swing.JOptionPane.showMessageDialog(this, "Välj en rad först.");
            return;
        }

        String type = tblMaterial.getValueAt(selectedRow, 0).toString();

        if (!type.equals("Dekoration")) {
            javax.swing.JOptionPane.showMessageDialog(this, "Välj en dekorationsrad från dekorationsåtgång.");
            return;
        }

        String decorationId = tblMaterial.getValueAt(selectedRow, 1).toString();
        double needToOrder = Double.parseDouble(tblMaterial.getValueAt(selectedRow, 6).toString());

        if (needToOrder <= 0) {
            int confirm = javax.swing.JOptionPane.showConfirmDialog(
                    this,
                    "Mer än 50% finns kvar i lager. Vill du beställa ändå?",
                    "Beställa ändå?",
                    javax.swing.JOptionPane.YES_NO_OPTION
            );

            if (confirm != javax.swing.JOptionPane.YES_OPTION) {
                return;
            }
        }

        String startQuantity = String.valueOf(needToOrder);

        if (needToOrder <= 0) {
            startQuantity = "";
        }

        javax.swing.JTextField txtQuantity = new javax.swing.JTextField(startQuantity);
        javax.swing.JTextField txtNote = new javax.swing.JTextField();

        Object[] message = {
            "Antal att beställa:", txtQuantity,
            "Notering:", txtNote
        };

        int option = javax.swing.JOptionPane.showConfirmDialog(
                this,
                message,
                "Markera dekoration som beställd",
                javax.swing.JOptionPane.OK_CANCEL_OPTION
        );

        if (option != javax.swing.JOptionPane.OK_OPTION) {
            return;
        }

        if (!Validator.isValidDouble(txtQuantity.getText())) {
            javax.swing.JOptionPane.showMessageDialog(this, Validator.invalidDoubleMessage("Antal"));
            return;
        }

        double quantity = Validator.parseDouble(txtQuantity.getText());

        if (quantity <= 0) {
            javax.swing.JOptionPane.showMessageDialog(this, "Antal måste vara större än 0.");
            return;
        }

        DecorationOrder order = new DecorationOrder();
        String orderId = generateDecorationOrderId();

        order.setDecorationOrderId(orderId);
        order.setStatus("Beställd");
        order.setNote(txtNote.getText().trim());

        DecorationOrderRepository repo = new DecorationOrderRepository();

        boolean orderSaved = repo.insertDecorationOrder(order);
        boolean lineSaved = false;

        if (orderSaved) {
            lineSaved = repo.insertDecorationOrderLine(orderId, decorationId, quantity);
        }

        if (orderSaved && lineSaved) {
            javax.swing.JOptionPane.showMessageDialog(this, "Dekoration markerad som beställd.");
            showOrderedDecorations();
        } else {
            javax.swing.JOptionPane.showMessageDialog(this, "Dekorationsbeställningen kunde inte sparas.");
        }
    }

    /**
     * Skapar tabellen som visar sparade material- och dekorationsbeställningar.
     */
    private void setupOrderedTable() {
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(
                new Object[]{
                    "Typ", "Beställnings-ID", "ID", "Namn", "Antal", "Enhet", "Status", "Notering"
                },
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblMaterial.setModel(model);
    }

    /**
     * Visar alla materialbeställningar.
     */
    private void showOrderedMaterials() {
        setupOrderedTable();

        MaterialOrderRepository orderRepo = new MaterialOrderRepository();
        MaterialRepository materialRepo = new MaterialRepository();

        javax.swing.table.DefaultTableModel model =
                (javax.swing.table.DefaultTableModel) tblMaterial.getModel();

        for (MaterialOrder order : orderRepo.getAllMaterialOrders()) {
            Material material = materialRepo.getMaterialById(order.getMaterialId());

            String name = "Okänt material";
            String unit = "";

            if (material != null) {
                name = Validator.safeName(material.getName());
                unit = Validator.safeText(material.getUnit());
            }

            model.addRow(new Object[]{
                "Material",
                order.getMaterialOrderId(),
                order.getMaterialId(),
                name,
                formatNumber(order.getQuantityOrdered()),
                unit,
                order.getStatus(),
                Validator.safeText(order.getNote())
            });
        }
    }

    /**
     * Visar alla dekorationsbeställningar.
     */
    private void showOrderedDecorations() {
        setupOrderedTable();

        DecorationOrderRepository orderRepo = new DecorationOrderRepository();
        DecorationRepository decorationRepo = new DecorationRepository();

        javax.swing.table.DefaultTableModel model =
                (javax.swing.table.DefaultTableModel) tblMaterial.getModel();

        for (DecorationOrder order : orderRepo.getAllDecorationOrders()) {
            Decoration decoration = decorationRepo.getDecorationById(order.getDecorationId());

            String name = "Okänd dekoration";
            String unit = "";

            if (decoration != null) {
                name = Validator.safeName(decoration.getName());
                unit = Validator.safeText(decoration.getUnit());
            }

            model.addRow(new Object[]{
                "Dekoration",
                order.getDecorationOrderId(),
                order.getDecorationId(),
                name,
                formatNumber(order.getQuantityOrdered()),
                unit,
                order.getStatus(),
                Validator.safeText(order.getNote())
            });
        }
    }

    /**
     * Formaterar decimaltal till två decimaler.
     */
    private String formatNumber(double value) {
        return String.format(java.util.Locale.US, "%.2f", value);
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        btnShowAll = new javax.swing.JButton();
        btnShowMaterials = new javax.swing.JButton();
        btnShowDecorations = new javax.swing.JButton();
        btnSearch = new javax.swing.JButton();
        lblSearch = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        btnBack = new javax.swing.JButton();
        btnAddMaterial = new javax.swing.JButton();
        btnAddDecoration = new javax.swing.JButton();
        btnEditSelected = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblMaterial = new javax.swing.JTable();
        btnLinkMaterialToHat = new javax.swing.JButton();
        btnShowHatMaterials = new javax.swing.JButton();
        btnShowMaterialUsage = new javax.swing.JButton();
        btnShowDecorationUsage = new javax.swing.JButton();
        btnShowOrderedMaterials = new javax.swing.JButton();
        btnShowOrderedDecorations = new javax.swing.JButton();
        btnMarkMaterialOrdered = new javax.swing.JButton();
        btnMarkDecorationOrdered = new javax.swing.JButton();
        btnExportMaterialReport = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMaximumSize(null);
        setMinimumSize(null);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel1.setText("Material & Dekoration");
        jLabel1.setMaximumSize(null);
        jLabel1.setMinimumSize(null);
        jLabel1.setPreferredSize(null);

        btnShowAll.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnShowAll.setText("Visa allt");
        btnShowAll.setMargin(null);
        btnShowAll.setMaximumSize(null);
        btnShowAll.setMinimumSize(null);
        btnShowAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnShowAllActionPerformed(evt);
            }
        });

        btnShowMaterials.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnShowMaterials.setText("Visa material");
        btnShowMaterials.setMargin(null);
        btnShowMaterials.setMaximumSize(null);
        btnShowMaterials.setMinimumSize(null);
        btnShowMaterials.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnShowMaterialsActionPerformed(evt);
            }
        });

        btnShowDecorations.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnShowDecorations.setText("Visa dekoration");
        btnShowDecorations.setInheritsPopupMenu(true);
        btnShowDecorations.setMargin(null);
        btnShowDecorations.setMaximumSize(null);
        btnShowDecorations.setMinimumSize(null);
        btnShowDecorations.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnShowDecorationsActionPerformed(evt);
            }
        });

        btnSearch.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnSearch.setText("Sök");
        btnSearch.setMargin(null);
        btnSearch.setMaximumSize(null);
        btnSearch.setMinimumSize(null);
        btnSearch.setPreferredSize(null);
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        lblSearch.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblSearch.setText("Sök på material eller dekoration");
        lblSearch.setPreferredSize(null);

        txtSearch.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        txtSearch.setMargin(null);
        txtSearch.setMaximumSize(null);
        txtSearch.setMinimumSize(null);

        btnBack.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        btnBack.setText("Tillbaka");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });

        btnAddMaterial.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnAddMaterial.setText("Lägg till material");
        btnAddMaterial.setMargin(null);
        btnAddMaterial.setMaximumSize(null);
        btnAddMaterial.setMinimumSize(null);
        btnAddMaterial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddMaterialActionPerformed(evt);
            }
        });

        btnAddDecoration.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnAddDecoration.setText("Lägg till dekoration");
        btnAddDecoration.setMargin(null);
        btnAddDecoration.setMaximumSize(null);
        btnAddDecoration.setMinimumSize(null);
        btnAddDecoration.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddDecorationActionPerformed(evt);
            }
        });

        btnEditSelected.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnEditSelected.setText("Ändra lager vald");
        btnEditSelected.setMargin(null);
        btnEditSelected.setMaximumSize(null);
        btnEditSelected.setMinimumSize(null);
        btnEditSelected.setPreferredSize(null);
        btnEditSelected.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditSelectedActionPerformed(evt);
            }
        });

        jScrollPane1.setMaximumSize(null);
        jScrollPane1.setPreferredSize(new java.awt.Dimension(1920, 750));

        tblMaterial.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Typ", "ID", "Namn", "Kategori/Typ", "Enhet", "Pris/enhet", "Lagersaldo", "Leverantör", "Notering"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblMaterial.setMaximumSize(null);
        jScrollPane1.setViewportView(tblMaterial);

        btnLinkMaterialToHat.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnLinkMaterialToHat.setText("Välj material till hattmodell");
        btnLinkMaterialToHat.setMargin(null);
        btnLinkMaterialToHat.setMaximumSize(null);
        btnLinkMaterialToHat.setMinimumSize(null);
        btnLinkMaterialToHat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLinkMaterialToHatActionPerformed(evt);
            }
        });

        btnShowHatMaterials.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnShowHatMaterials.setText("Visa material för hattmodell");
        btnShowHatMaterials.setMargin(null);
        btnShowHatMaterials.setMaximumSize(null);
        btnShowHatMaterials.setMinimumSize(null);
        btnShowHatMaterials.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnShowHatMaterialsActionPerformed(evt);
            }
        });

        btnShowMaterialUsage.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnShowMaterialUsage.setText("Visa materialåtgång");
        btnShowMaterialUsage.setMargin(null);
        btnShowMaterialUsage.setMaximumSize(null);
        btnShowMaterialUsage.setMinimumSize(null);
        btnShowMaterialUsage.setPreferredSize(null);
        btnShowMaterialUsage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnShowMaterialUsageActionPerformed(evt);
            }
        });

        btnShowDecorationUsage.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnShowDecorationUsage.setText("Visa dekorationsåtgång");
        btnShowDecorationUsage.setMargin(null);
        btnShowDecorationUsage.setMaximumSize(null);
        btnShowDecorationUsage.setMinimumSize(null);
        btnShowDecorationUsage.setPreferredSize(null);
        btnShowDecorationUsage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnShowDecorationUsageActionPerformed(evt);
            }
        });

        btnShowOrderedMaterials.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnShowOrderedMaterials.setText("Visa beställt material");
        btnShowOrderedMaterials.setMargin(null);
        btnShowOrderedMaterials.setMaximumSize(null);
        btnShowOrderedMaterials.setMinimumSize(null);
        btnShowOrderedMaterials.setPreferredSize(null);
        btnShowOrderedMaterials.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnShowOrderedMaterialsActionPerformed(evt);
            }
        });

        btnShowOrderedDecorations.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnShowOrderedDecorations.setText("Visa beställda dekorationer");
        btnShowOrderedDecorations.setMargin(null);
        btnShowOrderedDecorations.setMaximumSize(null);
        btnShowOrderedDecorations.setMinimumSize(null);
        btnShowOrderedDecorations.setPreferredSize(null);
        btnShowOrderedDecorations.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnShowOrderedDecorationsActionPerformed(evt);
            }
        });

        btnMarkMaterialOrdered.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnMarkMaterialOrdered.setText("Markera material beställt");
        btnMarkMaterialOrdered.setMargin(null);
        btnMarkMaterialOrdered.setMaximumSize(null);
        btnMarkMaterialOrdered.setMinimumSize(null);
        btnMarkMaterialOrdered.setName(""); // NOI18N
        btnMarkMaterialOrdered.setPreferredSize(null);
        btnMarkMaterialOrdered.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMarkMaterialOrderedActionPerformed(evt);
            }
        });

        btnMarkDecorationOrdered.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnMarkDecorationOrdered.setText("Markera dekoration beställd");
        btnMarkDecorationOrdered.setMargin(null);
        btnMarkDecorationOrdered.setMaximumSize(null);
        btnMarkDecorationOrdered.setMinimumSize(null);
        btnMarkDecorationOrdered.setPreferredSize(null);
        btnMarkDecorationOrdered.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMarkDecorationOrderedActionPerformed(evt);
            }
        });

        btnExportMaterialReport.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnExportMaterialReport.setText("Exportera beställningsunderlag");
        btnExportMaterialReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportMaterialReportActionPerformed(evt);
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
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnBack, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnShowAll, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnShowMaterials, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnShowDecorations, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                    .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btnEditSelected, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(txtSearch, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblSearch, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnAddMaterial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnAddDecoration, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnMarkMaterialOrdered, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnLinkMaterialToHat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnShowHatMaterials, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnShowOrderedMaterials, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(231, 231, 231)
                                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(btnShowDecorationUsage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnMarkDecorationOrdered, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnShowOrderedDecorations, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnExportMaterialReport)))
                            .addComponent(btnShowMaterialUsage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1908, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnBack, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnShowAll, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnShowMaterialUsage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnShowDecorationUsage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnShowMaterials, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnShowDecorations, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnMarkMaterialOrdered, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnMarkDecorationOrdered, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnShowOrderedDecorations, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(10, 10, 10)
                                .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(btnShowOrderedMaterials, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnEditSelected, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnShowHatMaterials, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btnAddDecoration, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnLinkMaterialToHat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnExportMaterialReport))
                            .addComponent(btnAddMaterial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 700, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    /**
     * Går tillbaka till huvudmenyn och stänger detta fönster.
     */
    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        new MainFrame().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnBackActionPerformed
    /**
     * Söker bland material och dekorationer.
     */
    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        searchAll();
    }//GEN-LAST:event_btnSearchActionPerformed
    /**
     * Ändrar lagersaldo för vald rad i tabellen.
     */
    private void btnEditSelectedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditSelectedActionPerformed
        editSelected();
    }//GEN-LAST:event_btnEditSelectedActionPerformed
    /**
     * Visar både material och dekorationer.
     */
    private void btnShowAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnShowAllActionPerformed
        loadAll();
    }//GEN-LAST:event_btnShowAllActionPerformed
    /**
     * Visar endast material.
     */
    private void btnShowMaterialsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnShowMaterialsActionPerformed
        loadMaterials();
    }//GEN-LAST:event_btnShowMaterialsActionPerformed
    /**
     * Öppnar dialogruta för att lägga till material.
     */
    private void btnAddMaterialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddMaterialActionPerformed
        addMaterial();
    }//GEN-LAST:event_btnAddMaterialActionPerformed
    /**
     * Visar endast dekorationer.
     */
    private void btnShowDecorationsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnShowDecorationsActionPerformed
        loadDecorations();
    }//GEN-LAST:event_btnShowDecorationsActionPerformed
    /**
     * Öppnar dialogruta för att lägga till dekoration.
     */
    private void btnAddDecorationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddDecorationActionPerformed
        addDecoration();
    }//GEN-LAST:event_btnAddDecorationActionPerformed
     /**
     * Kopplar ett material till en hattmodell.
     */
    private void btnLinkMaterialToHatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLinkMaterialToHatActionPerformed
        linkMaterialToHatModel();
    }//GEN-LAST:event_btnLinkMaterialToHatActionPerformed
    /**
     * Visar material som är kopplade till en vald hattmodell.
     */
    private void btnShowHatMaterialsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnShowHatMaterialsActionPerformed
        showMaterialsForHatModel();
    }//GEN-LAST:event_btnShowHatMaterialsActionPerformed
    /**
     * Visar total åtgång av dekorationer.
     */
    private void btnShowDecorationUsageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnShowDecorationUsageActionPerformed
        showDecorationUsage();
    }//GEN-LAST:event_btnShowDecorationUsageActionPerformed
    /**
     * Visar dekorationer som har markerats som beställda.
     */
    private void btnShowOrderedDecorationsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnShowOrderedDecorationsActionPerformed
        showOrderedDecorations();
    }//GEN-LAST:event_btnShowOrderedDecorationsActionPerformed
    /**
     * Markerar vald dekoration som beställd.
     */
    private void btnMarkDecorationOrderedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMarkDecorationOrderedActionPerformed
        markDecorationOrdered();
    }//GEN-LAST:event_btnMarkDecorationOrderedActionPerformed
    /**
     * Exporterar aktuellt material- eller dekorationsunderlag.
     */
    private void btnExportMaterialReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportMaterialReportActionPerformed
        exportMaterialReport();
    }//GEN-LAST:event_btnExportMaterialReportActionPerformed
    /**
     * Visar total åtgång av material.
     */
    private void btnShowMaterialUsageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnShowMaterialUsageActionPerformed
        showMaterialUsage();
    }//GEN-LAST:event_btnShowMaterialUsageActionPerformed
    /**
     * Visar material som har markerats som beställt.
     */
    private void btnShowOrderedMaterialsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnShowOrderedMaterialsActionPerformed
        showOrderedMaterials();
    }//GEN-LAST:event_btnShowOrderedMaterialsActionPerformed
    /**
     * Markerar valt material som beställt.
     */
    private void btnMarkMaterialOrderedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMarkMaterialOrderedActionPerformed
        markMaterialOrdered();
    }//GEN-LAST:event_btnMarkMaterialOrderedActionPerformed
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
            java.util.logging.Logger.getLogger(MaterialFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MaterialFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MaterialFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MaterialFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MaterialFrame().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddDecoration;
    private javax.swing.JButton btnAddMaterial;
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnEditSelected;
    private javax.swing.JButton btnExportMaterialReport;
    private javax.swing.JButton btnLinkMaterialToHat;
    private javax.swing.JButton btnMarkDecorationOrdered;
    private javax.swing.JButton btnMarkMaterialOrdered;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnShowAll;
    private javax.swing.JButton btnShowDecorationUsage;
    private javax.swing.JButton btnShowDecorations;
    private javax.swing.JButton btnShowHatMaterials;
    private javax.swing.JButton btnShowMaterialUsage;
    private javax.swing.JButton btnShowMaterials;
    private javax.swing.JButton btnShowOrderedDecorations;
    private javax.swing.JButton btnShowOrderedMaterials;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblSearch;
    private javax.swing.JTable tblMaterial;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}