package controller;

import entity.MedicationInventory;
import java.io.IOException;
import java.util.List;
/**
 * Interface for managing medication inventory.
 * Provides methods for checking inventory, retrieving all inventory, and handling low-stock alerts.
 */
public interface InventoryManagement {
    /**
     * Checks the inventory for a specific medication by name.
     *
     * @param medicationName the name of the medication to search for.
     * @return a list of {@link MedicationInventory} objects matching the given medication name.
     *         If no matches are found, the list will be empty.
     * @throws IOException if an error occurs while accessing the inventory data.
     */
    List<MedicationInventory> checkInventory(String medicationName) throws IOException;
     /**
     * Retrieves all medication inventory.
     *
     * @return a list of all {@link MedicationInventory} objects.
     * @throws IOException if an error occurs while accessing the inventory data.
     */
    List<MedicationInventory> getAllInventory() throws IOException;
    /**
     * Retrieves a list of medications that are below the low-stock threshold.
     *
     * @return a list of {@link MedicationInventory} objects that are low in stock.
     * @throws IOException if an error occurs while accessing the inventory data.
     */
    List<MedicationInventory> lowStockAlert() throws IOException;
}
