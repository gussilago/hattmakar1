package hattmakaren;

/**
 * Serviceklass som hanterar orderflödet i systemet.
 * Klassen ansvarar för att spara en order, spara orderrader,
 * minska lager och koppla material/dekorationer till varje orderrad.
 */
public class OrderService {

    private OrderLineMaterialRepository lineMaterialRepo = new OrderLineMaterialRepository();
    private OrderLineDecorationRepository lineDecorationRepo = new OrderLineDecorationRepository();
    private MaterialRepository materialRepo = new MaterialRepository();
    private DecorationRepository decorationRepo = new DecorationRepository();

    /**
     * Sparar en komplett order från ett OrderDraft-objekt.
     * Returnerar false om ordern saknar kund eller orderrader.
     */
    public boolean saveOrder(OrderDraft draft) {
        if (draft == null || !draft.hasCustomer() || !draft.hasLines()) {
            return false;
        }

        String orderId = generateOrderId();

        Order order = new Order();
        order.setOrderId(orderId);
        order.setCustomerId(draft.getCustomer().getCustomerId());
        order.setStatus("Ny");

        if (draft.isExpressOrder()) {
            order.setStatusNote("Expressorder");
        } else {
            order.setStatusNote("");
        }

        order.setTotalExclVat(draft.getTotalExclVat());
        order.setTotalVat(draft.getTotalVat());
        order.setTotalInclVat(draft.getTotalInclVat());
        order.setDiscountPct(draft.getDiscountPct());
        order.setDiscountAmount(draft.getDiscountAmount());

        OrderRepository orderRepo = new OrderRepository();
        OrderLineRepository lineRepo = new OrderLineRepository();
        HatInventoryRepository inventoryRepo = new HatInventoryRepository();

        boolean orderSaved = orderRepo.insertOrder(order);

        if (!orderSaved) {
            return false;
        }

        for (OrderLineDraft draftLine : draft.getLines()) {
            OrderLine line = draftLine.getOrderLine();
            line.setOrderId(orderId);

            int orderLineId = lineRepo.insertOrderLineAndReturnId(line);

            if (orderLineId == -1) {
                return false;
            }

            line.setOrderLineId(orderLineId);

            // Minskar lagret för den hattmodell och storlek som beställts.
            inventoryRepo.reduceQuantity(
                    line.getHatModelId(),
                    line.getSize(),
                    line.getQuantity()
            );

            // Om kunden valt ett eget material används det, annars används hattmodellens standardmaterial.
            if (draftLine.getMaterial() != null && draftLine.getMaterialQuantity() > 0) {
                saveCustomMaterial(orderLineId, draftLine);
            } else {
                saveStandardMaterials(orderLineId, draftLine);
            }

            saveDecorations(orderLineId, draftLine);
        }

        return true;
    }

    /**
     * Skapar ett enkelt order-ID baserat på aktuell tid.
     */
    private String generateOrderId() {
        return "o-" + System.currentTimeMillis();
    }

    /**
     * Uppdaterar status och notering för en order.
     */
    public boolean updateOrderStatus(String orderId, String newStatus, String note) {
        if (Validator.isBlank(orderId)) {
            return false;
        }

        if (Validator.isBlank(newStatus)) {
            return false;
        }

        OrderRepository repo = new OrderRepository();
        return repo.updateOrderStatusAndNote(orderId, newStatus, note);
    }

    /**
     * Markerar en order som retur eller reklamation.
     * Kräver order-ID, giltig typ och en notering.
     */
    public boolean markReturnOrComplaint(String orderId, String type, String note) {
        if (Validator.isBlank(orderId)) {
            return false;
        }

        if (Validator.isBlank(type)) {
            return false;
        }

        if (!type.equals("Retur") && !type.equals("Reklamation")) {
            return false;
        }

        if (Validator.isBlank(note)) {
            return false;
        }

        OrderRepository repo = new OrderRepository();
        return repo.updateOrderStatusAndNote(orderId, type, note);
    }
    
    /**
     * Hjälpmetod för kostnadsuträkning.
     */
        private String formatMoney(double value) {
            return String.format(java.util.Locale.US, "%.2f", value);
    }
    
     /**
     * Bygger en textförhandsvisning av en sparad order.
     * Texten visar kund, orderrader, material, dekorationer, moms och totalsummor.
     */
    public String buildSavedOrderPreview(Order order) {
        if (order == null) {
            return "Ingen order att visa.";
        }

        String orderId = order.getOrderId();
        String customerId = order.getCustomerId();
        String status = order.getStatus();
        String statusNote = order.getStatusNote();

        CustomerRepository customerRepo = new CustomerRepository();
        OrderLineRepository lineRepo = new OrderLineRepository();
        HatModelRepository hatRepo = new HatModelRepository();

        Customer customer = customerRepo.getCustomerById(customerId);
        java.util.List<OrderLine> lines = lineRepo.getOrderLinesByOrderId(orderId);

        StringBuilder sb = new StringBuilder();

        sb.append("BESTÄLLNING / ORDER\n");
        sb.append("====================================\n\n");
        sb.append("Order-ID: ").append(Validator.safeText(orderId)).append("\n");
        sb.append("Status: ").append(Validator.safeText(status)).append("\n");

        if (!Validator.isBlank(statusNote) && !statusNote.equals("Nej")) {
            sb.append("Notering: ").append(statusNote).append("\n");
        }

        if (!Validator.isBlank(statusNote) && statusNote.toLowerCase().contains("express")) {
            sb.append("Expressavgift: ").append(formatMoney(PriceCalculator.getExpressFee())).append(" SEK\n");
        }

        sb.append("\n");

        if (customer != null && ShippingLabel.isForeignShipment(customer)) {
            sb.append("Items:\n");
        } else {
            sb.append("Produkter:\n");
        }

        for (OrderLine line : lines) {
            HatModel hat = hatRepo.getHatModelById(line.getHatModelId());
            String hatName = (hat != null) ? Validator.safeName(hat.getName()) : "Okänd hatt";

            sb.append("- ").append(hatName)
                    .append(", storlek ").append(line.getSize())
                    .append(", antal ").append(line.getQuantity())
                    .append("\n");

            double lineMaterialCost = 0;
            double lineDecorationCost = 0;

            java.util.List<OrderLineMaterial> materials =
                    lineMaterialRepo.getMaterialsByOrderLineId(line.getOrderLineId());

            if (!materials.isEmpty()) {
                sb.append("  Materialkostnad / Material cost:\n");
            }

            // Går igenom allt material som är kopplat till orderraden.
            for (OrderLineMaterial olm : materials) {
                Material material = materialRepo.getMaterialById(olm.getMaterialId());
                String materialName = material != null
                        ? Validator.safeName(material.getName())
                        : "Okänt material";

                sb.append("    - ").append(materialName)
                        .append(", åtgång ").append(formatMoney(olm.getQuantityUsed()))
                        .append(", kostnad ").append(formatMoney(olm.getMaterialCost()))
                        .append(" SEK\n");

                lineMaterialCost += olm.getMaterialCost();
            }

            java.util.List<OrderLineDecoration> decorations =
                    lineDecorationRepo.getDecorationsByOrderLineId(line.getOrderLineId());

            if (!decorations.isEmpty()) {
                sb.append("  Dekorationskostnad / Decoration cost:\n");
            }

            // Går igenom alla dekorationer som är kopplade till orderraden.
            for (OrderLineDecoration old : decorations) {
                Decoration decoration = decorationRepo.getDecorationById(old.getDecorationId());
                String decorationName = decoration != null
                        ? Validator.safeName(decoration.getName())
                        : "Okänd dekoration";

                sb.append("    - ").append(decorationName)
                        .append(", åtgång ").append(formatMoney(old.getQuantityUsed()))
                        .append(", kostnad ").append(formatMoney(old.getDecorationCost()))
                        .append(" SEK\n");

                lineDecorationCost += old.getDecorationCost();
            }

            // Räknar fram den del av orderradens pris som inte är material eller dekoration.
            double otherCost = line.getPriceExclVat() - lineMaterialCost - lineDecorationCost;

            sb.append("  Varav material / Of which material: ")
                    .append(formatMoney(lineMaterialCost)).append(" SEK\n");

            sb.append("  Varav dekoration / Of which decoration: ")
                    .append(formatMoney(lineDecorationCost)).append(" SEK\n");

            sb.append("  Övrigt hattpris/arbete / Other hat price/work: ")
                    .append(formatMoney(otherCost)).append(" SEK\n");
        }

        if (order.getDiscountPct() > 0) {
            sb.append("Rabatt: ")
                    .append(formatMoney(order.getDiscountAmount()))
                    .append(" % (-")
                    .append(formatMoney(order.getDiscountAmount()))
                    .append(" SEK)\n");
        }

        sb.append("Totalt exkl. moms / Total excl. VAT: ")
                .append(formatMoney(order.getTotalExclVat()))
                .append(" SEK\n");

        sb.append("Moms / VAT: ")
                .append(formatMoney(order.getTotalVat()))
                .append(" SEK\n");

        sb.append("Totalt inkl. moms / Total incl. VAT: ")
                .append(formatMoney(order.getTotalInclVat()))
                .append(" SEK\n");

        return sb.toString();
    }

    /**
     * Sparar standardmaterial för en orderrad.
     * Standardmaterial hämtas från hattmodellen och lagersaldot minskas.
     */
    private void saveStandardMaterials(int orderLineId, OrderLineDraft draftLine) {
        OrderLine line = draftLine.getOrderLine();

        HatModelMaterialRepository hatMaterialRepo = new HatModelMaterialRepository();
        MaterialRepository materialRepo = new MaterialRepository();
        OrderLineMaterialRepository orderLineMaterialRepo = new OrderLineMaterialRepository();

        java.util.List<HatModelMaterial> standardMaterials =
                hatMaterialRepo.getStandardMaterialsByHatModelId(line.getHatModelId());

        for (HatModelMaterial link : standardMaterials) {
            Material material = materialRepo.getMaterialById(link.getMaterialId());

            if (material == null) {
                continue;
            }

            // Multiplicerar materialåtgång per hatt med antal hattar i orderraden.
            double quantityUsed = link.getQuantityNeeded() * line.getQuantity();
            double materialCost = material.getUnitPrice() * quantityUsed;

            OrderLineMaterial item = new OrderLineMaterial();
            item.setOrderLineId(orderLineId);
            item.setMaterialId(material.getMaterialId());
            item.setQuantityUsed(quantityUsed);
            item.setMaterialCost(materialCost);

            orderLineMaterialRepo.insertOrderLineMaterial(item);
            materialRepo.reduceStock(material.getMaterialId(), quantityUsed);
        }
    }

    /**
     * Sparar ett kundvalt material för en orderrad.
     */
    private void saveCustomMaterial(int orderLineId, OrderLineDraft draftLine) {
        Material material = draftLine.getMaterial();

        if (material == null || draftLine.getMaterialQuantity() <= 0) {
            return;
        }

        double quantityUsed = draftLine.getMaterialQuantity();
        double materialCost = material.getUnitPrice() * quantityUsed;

        OrderLineMaterial item = new OrderLineMaterial();
        item.setOrderLineId(orderLineId);
        item.setMaterialId(material.getMaterialId());
        item.setQuantityUsed(quantityUsed);
        item.setMaterialCost(materialCost);

        OrderLineMaterialRepository repo = new OrderLineMaterialRepository();
        repo.insertOrderLineMaterial(item);

        MaterialRepository materialRepo = new MaterialRepository();
        materialRepo.reduceStock(material.getMaterialId(), quantityUsed);
    }

    /**
     * Sparar dekorationer för en orderrad och minskar dekorationslagret.
     */
    private void saveDecorations(int orderLineId, OrderLineDraft draftLine) {
        OrderLineDecorationRepository repo = new OrderLineDecorationRepository();
        DecorationRepository decorationRepo = new DecorationRepository();

        for (OrderLineDecoration item : draftLine.getDecorations()) {
            item.setOrderLineId(orderLineId);

            repo.insertOrderLineDecoration(item);
            decorationRepo.reduceStock(item.getDecorationId(), item.getQuantityUsed());
        }
    }
}