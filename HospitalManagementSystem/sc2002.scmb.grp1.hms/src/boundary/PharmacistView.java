package boundary;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.List;

import controller.AppointmentOutcomeController;
import controller.PharmacistController;
import controller.SecurityQuestionsController;
import controller.MenuInterface;
import entity.User;
import entity.MedicationInventory;
import repository.MedicationInventoryRepository;
import repository.ReplenishmentRequestRepository;


/**
 * The PharmacistView class provides the user interface for pharmacists.
 * It allows pharmacists to manage appointment outcomes, update prescriptions,
 * view medication inventory, and handle replenishment requests.
 */
public class PharmacistView implements MenuInterface {
	private final Scanner scanner = new Scanner(System.in);
    private final AppointmentOutcomeController outcomecontroller = new AppointmentOutcomeController();
    private final MedicationInventoryRepository inventoryRepository = new MedicationInventoryRepository();
    private final ReplenishmentRequestRepository replenishmentRequestRepository = new ReplenishmentRequestRepository();
    private final PharmacistController pharmacistController =  new PharmacistController(inventoryRepository, replenishmentRequestRepository);// Create an instance
    
    /**
     * Displays the Pharmacist Menu and processes user input.
     *
     * @param user The logged-in pharmacist user.
     */
    public void Menu(User user) {
        while (true) {
            System.out.println();
            System.out.println("+------------------------------------------------+");
            System.out.println("|                Pharmacist Menu                 |");
            System.out.println("+------------------------------------------------+");
            System.out.println("| 1. View Appointment Outcome Record             |");
            System.out.println("| 2. Update Prescription Status                  |");
            System.out.println("| 3. View Medication Inventory                   |");
            System.out.println("| 4. Submit Replenishment Request                |");
            System.out.println("| 5. Set Security Questions for Recovery         |");
            System.out.println("| 6. Logout                                      |");
            System.out.println("+------------------------------------------------+");
            System.out.println();

            int choice  = -1;

            while (true) {
                try {
                    System.out.print("Please enter your choice: ");
                    choice = scanner.nextInt();
                    scanner.nextLine(); // Consume newline left-over
                    break; // Exit loop if a valid integer is entered
                } catch (InputMismatchException e) {
                    System.err.println("Invalid input. Please enter a valid integer.");
                    scanner.nextLine(); // Consume the invalid input
                }
            }

            if (choice == 6) {
                System.out.println("Logging out...");
                break;
            }
            // Add functionality for each menu option here

            if (choice == 1){
                try {
                    outcomecontroller.viewAppointmentOutcomes();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            if (choice == 2){
                try {
                    displayAndUpdatePendingOutcomes();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            if (choice == 3) {
                viewMedicationInventory(); // Call method to view inventory
            }

            if(choice == 4){
                submitReplenishmentRequest();
            }
            if (choice == 5) {
                SecurityQuestionsController sqc = new SecurityQuestionsController();

                System.out.println("+------------------------------------------------+");
                System.out.println("|        Setting Security Questions              |");
                System.out.println("+------------------------------------------------+");

                System.out.print("| Enter a security question: ");
                String question = scanner.nextLine();
                System.out.print("| Enter the answer: ");
                String answer = scanner.nextLine();

                boolean isSet = sqc.changeSecurityQuestionControl(user.getUserId(), question, answer);

                if (!isSet) {
                    System.out.println("+------------------------------------------------+");
                    System.out.println("| Sorry, your security questions could not be set.|");
                    System.out.println("| Please contact an administrator for assistance.|");
                    System.out.println("+------------------------------------------------+\n");
                } else {
                    System.out.println("+------------------------------------------------+");
                    System.out.println("| Security questions successfully set.           |");
                    System.out.println("+------------------------------------------------+\n");
                }
            }
            
            
        }
    }

    /**
     * Displays pending appointment outcomes and updates prescription status.
     *
     * @throws IOException if an error occurs during I/O operations.
     */
    private void displayAndUpdatePendingOutcomes() throws IOException {
        System.out.println("+------------------------------------------------+");
        System.out.println("|       Pending Prescription Updates             |");
        System.out.println("+------------------------------------------------+");

        // Display pending appointment outcomes
        outcomecontroller.displayPendingAppointmentOutcomes();

        System.out.println("+------------------------------------------------+");
        System.out.print("| Enter the Outcome ID to update status: ");
        String outcomeId = scanner.nextLine();
        System.out.println("+------------------------------------------------+");

        // Update prescription status
        outcomecontroller.changePrescriptionStatusToDispensed(outcomeId);

        System.out.println("+------------------------------------------------+");
        System.out.println("|   Prescription status updated successfully!    |");
        System.out.println("+------------------------------------------------+\n");
    }

    /**
     * Retrieves and returns the list of all medication inventory items.
     *
     * @return A list of MedicationInventory items.
     * @throws IOException if an error occurs during I/O operations.
     */
    public List<MedicationInventory> getAllInventory() throws IOException {
        return inventoryRepository.loadAllMedications();
    }

    /**
     * Displays the current medication inventory with stock levels.
     */
    private void viewMedicationInventory() {
        System.out.println("+------------------------------------------------+");
        System.out.println("|               Medication Inventory             |");
        System.out.println("+------------------------------------------------+");

        try {
            // Load the full inventory
            List<MedicationInventory> medications = pharmacistController.getAllInventory();

            if (medications.isEmpty()) {
                System.out.println("|         No medications found in inventory.     |");
            } else {
                System.out.println("| Name                              | Stock  | Alert |");
                System.out.println("+------------------------------------------------+");

                // Iterate through the medications
                for (MedicationInventory medication : medications) {
                    String stockAlertLabel = medication.getStockLevel() < 60 ? "LOW" : "OK";
                    System.out.printf("| %-32s | %6d | %-5s |\n",
                            medication.getMedicationName(),
                            medication.getStockLevel(),
                            stockAlertLabel);
                }
            }
            System.out.println("+------------------------------------------------+");
        } catch (IOException e) {
            System.out.println("+------------------------------------------------+");
            System.err.printf("| Failed to load inventory: %-23s |\n", e.getMessage());
            System.out.println("+------------------------------------------------+");
        }
    }

    /**
     * Submits a replenishment request for a specified medication.
     */
    private void submitReplenishmentRequest() {
        System.out.println("+------------------------------------------------+");
        System.out.println("|           Submit Replenishment Request         |");
        System.out.println("+------------------------------------------------+");
        System.out.print("| Enter the medication name: ");
        String medicationName = scanner.nextLine();

        try {
            pharmacistController.submitReplenishmentRequest(medicationName);
            System.out.println("+------------------------------------------------+");
            System.out.println("|   Replenishment request submitted successfully!  |");
            System.out.println("+------------------------------------------------+\n");
        } catch (IOException e) {
            System.out.println("+------------------------------------------------+");
            System.err.printf("| Error: %-40s |\n", e.getMessage());
            System.out.println("+------------------------------------------------+\n");
        }
    }



    
}
