package hattmakaren;

import java.util.ArrayList;
import java.util.List;

/**
 * Klassen används som ett tillfälligt utkast för en orderrad.
 * Den samlar orderrad, hattnamn, justeringar, material och dekorationer
 * innan informationen används vidare i systemet.
 */
public class OrderLineDraft {

    private OrderLine orderLine;
    private String hatName;
    private String adjustmentText;

    private Material material;
    private double materialQuantity;

    private List<OrderLineDecoration> decorations = new ArrayList<>();

    public OrderLine getOrderLine() {
        return orderLine;
    }

    public void setOrderLine(OrderLine orderLine) {
        this.orderLine = orderLine;
    }

    public String getHatName() {
        return hatName;
    }

    public void setHatName(String hatName) {
        this.hatName = hatName;
    }

    public String getAdjustmentText() {
        return adjustmentText;
    }

    public void setAdjustmentText(String adjustmentText) {
        this.adjustmentText = adjustmentText;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public double getMaterialQuantity() {
        return materialQuantity;
    }

    public void setMaterialQuantity(double materialQuantity) {
        this.materialQuantity = materialQuantity;
    }

    public List<OrderLineDecoration> getDecorations() {
        return decorations;
    }

    /**
     * Lägger till en dekoration i utkastets lista.
     * Varje dekoration kan senare användas för att beräkna totalkostnaden.
     */
    public void addDecoration(OrderLineDecoration decoration) {
        decorations.add(decoration);
    }

    /**
     * Räknar ihop den totala kostnaden för alla dekorationer
     * som har lagts till på orderraden.
     */
    public double getDecorationCostTotal() {
        double total = 0;

        // Går igenom varje dekoration och lägger till dess kostnad i totalen.
        for (OrderLineDecoration decoration : decorations) {
            total += decoration.getDecorationCost();
        }

        return total;
    }
}