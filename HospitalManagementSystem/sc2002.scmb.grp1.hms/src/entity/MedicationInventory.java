package entity;

/**
 * Represents the inventory information of a specific medication.
 */
public class MedicationInventory {
    private String medicationName;
    private int stockLevel;
    private int stockAlertLevel;

    /**
     * Constructs a MedicationInventory object with the specified attributes.
     *
     * @param medicationName  The name of the medication.
     * @param stockLevel      The current stock level of the medication.
     * @param stockAlertLevel The alert level for the stock, indicating when to reorder.
     */
    public MedicationInventory(String medicationName, int stockLevel, int stockAlertLevel) {
        this.medicationName = medicationName;
        this.stockLevel = stockLevel;
        this.stockAlertLevel = stockAlertLevel;
    }

    /**
     * Retrieves the name of the medication.
     *
     * @return The medication name.
     */
    public String getMedicationName() {
        return medicationName;
    }

    /**
     * Retrieves the current stock level of the medication.
     *
     * @return The stock level.
     */
    public int getStockLevel() {
        return stockLevel;
    }

    /**
     * Retrieves the stock alert level of the medication.
     *
     * @return The stock alert level.
     */
    public int getStockAlertLevel() {
        return stockAlertLevel;
    }

    /**
     * Updates the stock level of the medication.
     *
     * @param stockLevel The new stock level.
     */
    public void setStockLevel(int stockLevel) {
        this.stockLevel = stockLevel;
    }

    /**
     * Updates the stock alert level of the medication.
     *
     * @param stockAlertLevel The new stock alert level.
     */
    public void setStockAlertLevel(int stockAlertLevel) {
        this.stockAlertLevel = stockAlertLevel;
    }
}