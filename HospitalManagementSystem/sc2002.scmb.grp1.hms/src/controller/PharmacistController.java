package controller;

import java.io.IOException;
import entity.MedicationInventory;
import repository.MedicationInventoryRepository;
import repository.ReplenishmentRequestRepository;
import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;

/**
 * The {@code PharmacistController} class manages inventory and replenishment requests
 * for medications. It implements both the {@link InventoryManagement} and
 * {@link ReplenishmentRequestService} interfaces, allowing a pharmacist to 
 * check inventory, view low-stock alerts, and submit replenishment requests.
 */
public class PharmacistController implements InventoryManagement, ReplenishmentRequestService {
    private final MedicationInventoryRepository inventoryRepository;
    private final ReplenishmentRequestRepository replenishmentRequestRepository;
    private final Scanner scanner = new Scanner(System.in);

    /**
     * Constructs a new {@code PharmacistController} with specified repositories.
     *
     * @param inventoryRepository the repository for managing medication inventory
     * @param replenishmentRequestRepository the repository for handling replenishment requests
     */
    public PharmacistController(MedicationInventoryRepository inventoryRepository, 
                                ReplenishmentRequestRepository replenishmentRequestRepository) {
        this.inventoryRepository = inventoryRepository;
        this.replenishmentRequestRepository = replenishmentRequestRepository;
    }

    /**
     * Checks the inventory for a specified medication and returns a list of matching inventory items.
     *
     * @param medicationName the name of the medication to check
     * @return a list of {@link MedicationInventory} objects matching the specified name, or {@code null} if an error occurs
     */
    @Override
    public List<MedicationInventory> checkInventory(String medicationName) {
        try {
            return inventoryRepository.getMedicationByName(medicationName);
        } catch (IOException e) {
            System.err.println("Error finding the Medication. Medication does not exist");
            return null;
        }
    }

    /**
     * Retrieves the entire inventory of medications.
     *
     * @return a list of all {@link MedicationInventory} items
     * @throws IOException if an error occurs while loading the inventory
     */
    @Override
    public List<MedicationInventory> getAllInventory() throws IOException {
        return inventoryRepository.loadAllMedications();
    }

    /**
     * Generates a list of medications that are below the stock threshold, 
     * displaying an alert for each low-stock medication.
     *
     * @return a list of low-stock {@link MedicationInventory} items
     * @throws IOException if an error occurs while accessing the inventory
     */
    @Override
    public List<MedicationInventory> lowStockAlert() throws IOException {
        List<MedicationInventory> lowStockMedications = new ArrayList<>();
        List<MedicationInventory> allMedications = inventoryRepository.loadAllMedications();

        for (MedicationInventory medication : allMedications) {
            if (medication.getStockLevel() < 60) {
                lowStockMedications.add(medication);
            }
        }

        if (!lowStockMedications.isEmpty()) {
            System.out.println("+------------------------------------------------+");
            System.out.println("|            Low Stock Medication Alert          |");
            System.out.println("+------------------------------------------------+");
            for (MedicationInventory medication : lowStockMedications) {
                System.out.printf("| Name: %-35s | Stock Level: %3d |\n",
                        medication.getMedicationName(), medication.getStockLevel());
            }
            System.out.println("+------------------------------------------------+");
        } else {
            System.out.println("+------------------------------------------------+");
            System.out.println("|        No medications below stock alert.       |");
            System.out.println("+------------------------------------------------+");
        }

        return lowStockMedications;
    }

    /**
     * Submits a replenishment request for a specified medication, prompting the user to specify
     * the quantity to be replenished. Provides validation for quantity input.
     *
     * @param medicationName the name of the medication for the replenishment request
     * @throws IOException if an error occurs while saving the replenishment request
     */
    @Override
    public void submitReplenishmentRequest(String medicationName) throws IOException {
        System.out.println("+------------------------------------------------+");
        System.out.println("|           Submit Replenishment Request         |");
        System.out.println("+------------------------------------------------+");

        List<MedicationInventory> inventoryList = checkInventory(medicationName);

        if (inventoryList == null || inventoryList.isEmpty()) {
            System.out.println("| Error: The medication '" + medicationName + "' does not exist in the inventory. |");
            System.out.println("+------------------------------------------------+\n");
            return;
        }

        MedicationInventory medication = inventoryList.get(0); // Assuming only one entry per medication
        System.out.println("| Medication found:                              |");
        System.out.printf("| Name: %-40s |\n", medicationName);
        System.out.printf("| Stock Level: %-33d |\n", medication.getStockLevel());
        System.out.println("+------------------------------------------------+");

        System.out.printf("| Enter the quantity to be replenished for '%s': ", medicationName);
        int quantity;
        while (true) {
            try {
                quantity = Integer.parseInt(scanner.nextLine());
                if (quantity <= 0) {
                    System.out.println("| Quantity must be greater than zero. Please try again. |");
                    System.out.print("| Enter the quantity: ");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("| Invalid input. Please enter a valid positive integer. |");
                System.out.print("| Enter the quantity: ");
            }
        }

        replenishmentRequestRepository.saveReplenishmentRequest(medicationName, quantity);
        System.out.println("+------------------------------------------------+");
        System.out.printf("| Replenishment request submitted successfully!  |\n");
        System.out.printf("| Medication: %-35s |\n", medicationName);
        System.out.printf("| Quantity: %-36d |\n", quantity);
        System.out.println("+------------------------------------------------+\n");
    }
}
