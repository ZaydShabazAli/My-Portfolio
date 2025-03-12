package controller;

import entity.MedicationInventory;
import repository.MedicationInventoryRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@code MedicationInventoryController} class manages medication inventory operations.
 * It allows for adding, removing, updating, and retrieving medication information 
 * from the inventory, as well as setting alerts for low stock levels.
 */
public class MedicationInventoryController {
    private MedicationInventoryRepository medicationinventoryRepository = new MedicationInventoryRepository();

    /**
     * Retrieves a list of all unique medication names from the inventory.
     *
     * @return a list of all unique medication names
     * @throws IOException if an error occurs while loading the medication data
     */
    public List<String> getAllMedicationNames() throws IOException {
        List<MedicationInventory> medications = medicationinventoryRepository.loadAllMedications();
        List<String> medicationNames = new ArrayList<>();

        for (MedicationInventory medication : medications) {
            medicationNames.add(medication.getMedicationName());
        }

        return medicationNames;
    }

    /**
     * Adds a new medication to the inventory with the specified name, stock quantity, and alert level.
     *
     * @param medicineName the name of the medication to add
     * @param stockQuantity the initial quantity of the medication
     * @param stockLevel the alert level for low stock
     * @throws IOException if an error occurs while adding the medication to the inventory
     */
    public void addMedicine(String medicineName, int stockQuantity, int stockLevel) throws IOException {
        medicationinventoryRepository.addMedication(medicineName, stockLevel, stockLevel);
    }

    /**
     * Removes a medication from the inventory based on its name.
     *
     * @param medicineName the name of the medication to remove
     * @throws IOException if an error occurs while removing the medication from the inventory
     */
    public void removeMedicine(String medicineName) throws IOException {
        medicationinventoryRepository.removeMedication(medicineName);
    }

    /**
     * Replenishes the stock level of a specified medication.
     *
     * @param medication the name of the medication to replenish
     * @param quantity the amount to add to the current stock level
     * @throws IOException if an error occurs while updating the stock level
     */
    public void replenishInventory(String medication, int quantity) throws IOException {
        medicationinventoryRepository.updateStockLevel(medication, quantity);
    }

    /**
     * Updates the alert level for a specified medication.
     *
     * @param medication the name of the medication for which to set the alert level
     * @param level the stock level at which the alert should trigger
     * @throws IOException if an error occurs while updating the alert level
     */
    public void updateAlert(String medication, int level) throws IOException {
        medicationinventoryRepository.updateStockAlert(medication, level);
    }

    /**
     * Retrieves a list of all medications currently in the inventory.
     *
     * @return a list of all {@link MedicationInventory} items in the inventory
     * @throws IOException if an error occurs while loading the medication data
     */
    public List<MedicationInventory> inventory() throws IOException {
        return medicationinventoryRepository.loadAllMedications();
    }
}
