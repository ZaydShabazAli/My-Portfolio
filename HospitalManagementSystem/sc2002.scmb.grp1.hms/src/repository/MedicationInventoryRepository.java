package repository;

import entity.MedicationInventory;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository class for managing medication inventory and replenishment requests.
 * Handles loading, updating, and saving medication data in CSV files.
 */
public class MedicationInventoryRepository {
    private static final String FILE_PATH_MEDICATION_INVENTORY = "sc2002.scmb.grp1.hms//resource//MedicationInventory.csv";
    private static final String FILE_PATH_REPLENISHMENT_REQUESTS = "sc2002.scmb.grp1.hms//resource//ReplenishmentRequests.csv";
    // private static final CSVUtil csvUtil = new CSVUtil();

    /**
     * Repository class for managing medication inventory and replenishment requests.
     * Handles loading, updating, and saving medication data in CSV files.
     */
    public List<MedicationInventory> loadAllMedications() throws IOException {
        List<MedicationInventory> medications = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH_MEDICATION_INVENTORY))) {
            String line;
            reader.readLine(); // Skip the header row
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length == 3) {
                    String medicationName = fields[0];
                    int stockLevel = Integer.parseInt(fields[1]);
                    int stockAlertLevel = Integer.parseInt(fields[2]);
                    medications.add(new MedicationInventory(medicationName, stockLevel, stockAlertLevel));
                }
            }
        } catch (IOException e) {
            System.out.printf("| Error: %-40s |\n", e.getMessage());
            System.out.println("+------------------------------------------------+");
            throw new IOException("Error reading medication inventory data: " + e.getMessage());
        }
        return medications;
    }

    /**
     * Retrieves a list of medications filtered by the given medication name.
     * @param medicationName the name of the medication to search for.
     * @return a list of matching {@link MedicationInventory} objects.
     * @throws IOException if the file cannot be read.
     */
    public List<MedicationInventory> getMedicationByName(String medicationName) throws IOException {
        System.out.println("+------------------------------------------------+");
        System.out.println("|            Searching Medication by Name        |");
        System.out.println("+------------------------------------------------+");

        List<MedicationInventory> allMedications = loadAllMedications();
        List<MedicationInventory> filteredMedications = new ArrayList<>();

        for (MedicationInventory medication : allMedications) {
            if (medication.getMedicationName().equalsIgnoreCase(medicationName)) {
                filteredMedications.add(medication);
            }
        }

        if (filteredMedications.isEmpty()) {
            System.out.println("| Error: Medication not found in the inventory.  |");
        } else {
            System.out.println("| Medication found:                              |");
            for (MedicationInventory medication : filteredMedications) {
                System.out.printf("| Name: %-35s | Stock Level: %3d |\n",
                        medication.getMedicationName(), medication.getStockLevel());
            }
        }
        System.out.println("+------------------------------------------------+\n");
        return filteredMedications;
    }

    /**
     * Adds a new medication to the inventory CSV file.
     * @param name the name of the medication.
     * @param stockLevel the initial stock level of the medication.
     * @param alertLevel the stock alert level of the medication.
     * @throws IOException if the file cannot be written to.
     */
    public void addMedication(String name, int stockLevel, int alertLevel) throws IOException {
        System.out.println("+------------------------------------------------+");
        System.out.println("|               Adding New Medication            |");
        System.out.println("+------------------------------------------------+");

        MedicationInventory newMedicine = new MedicationInventory(name, stockLevel, alertLevel);
        try (FileWriter writer = new FileWriter(FILE_PATH_MEDICATION_INVENTORY, true)) {
            writer.append(newMedicine.getMedicationName()).append(",");
            writer.append(String.valueOf(newMedicine.getStockLevel())).append(",");
            writer.append(String.valueOf(newMedicine.getStockAlertLevel())).append("\n");
            System.out.println("| New medication added successfully!             |");
        } catch (IOException e) {
            System.out.printf("| Error: %-40s |\n", e.getMessage());
            throw new IOException("Error writing new medication to file: " + e.getMessage());
        }
        System.out.println("+------------------------------------------------+\n");
    }

    /**
     * Removes a medication from the inventory based on its name.
     * @param name the name of the medication to be removed.
     * @throws IOException if the file cannot be read or written to.
     */
    public void removeMedication(String name) throws IOException {
        System.out.println("+------------------------------------------------+");
        System.out.println("|               Removing Medication              |");
        System.out.println("+------------------------------------------------+");

        List<MedicationInventory> medications = loadAllMedications();
        medications.removeIf(medication -> medication.getMedicationName().equalsIgnoreCase(name));

        saveAllMedication(medications);
        System.out.println("| Medication removed successfully!               |");
        System.out.println("+------------------------------------------------+\n");
    }

    /**
     * Updates the stock level of a medication.
     * @param name the name of the medication.
     * @param level the amount to add to the current stock level.
     * @throws IOException if the file cannot be read or written to.
     */
    public void updateStockLevel(String name, int level) throws IOException {
        System.out.println("+------------------------------------------------+");
        System.out.println("|             Updating Stock Level               |");
        System.out.println("+------------------------------------------------+");

        List<MedicationInventory> medications = loadAllMedications();
        for (MedicationInventory medication : medications) {
            if (medication.getMedicationName().equalsIgnoreCase(name)) {
                int increase = medication.getStockLevel() + level;
                medication.setStockLevel(increase);
                break;
            }
        }
        saveAllMedication(medications);
        System.out.println("| Stock level updated successfully!              |");
        System.out.println("+------------------------------------------------+\n");
    }

    /**
     * Updates the stock alert level of a medication.
     * @param name the name of the medication.
     * @param level the new stock alert level.
     * @throws IOException if the file cannot be read or written to.
     */
    public void updateStockAlert(String name, int level) throws IOException {
        System.out.println("+------------------------------------------------+");
        System.out.println("|           Updating Stock Alert Level           |");
        System.out.println("+------------------------------------------------+");

        List<MedicationInventory> medications = loadAllMedications();
        for (MedicationInventory medication : medications) {
            if (medication.getMedicationName().equalsIgnoreCase(name)) {
                medication.setStockAlertLevel(level);
                break;
            }
        }
        saveAllMedication(medications);
        System.out.println("| Stock alert level updated successfully!        |");
        System.out.println("+------------------------------------------------+\n");
    }

    /**
     * Saves the current medication inventory to the CSV file.
     * @param medications a list of {@link MedicationInventory} objects to save.
     * @throws IOException if the file cannot be written to.
     */
    private void saveAllMedication(List<MedicationInventory> medications) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH_MEDICATION_INVENTORY))) {
            writer.write("MedicationName,StockLevel,StockAlertLevel\n");
            for (MedicationInventory medication : medications) {
                writer.write(medication.getMedicationName() + ","
                        + medication.getStockLevel() + ","
                        + medication.getStockAlertLevel() + "\n");
            }
        }
    }

    /**
     * Saves a replenishment request to the replenishment requests CSV file.
     * @param medicationName the name of the medication for the request.
     * @param requestId the ID of the replenishment request.
     * @param status the status of the replenishment request.
     */
    public void saveReplenishmentRequest(String medicationName, String requestId, String status) {
        System.out.println("+------------------------------------------------+");
        System.out.println("|           Saving Replenishment Request         |");
        System.out.println("+------------------------------------------------+");
        try (FileWriter writer = new FileWriter(FILE_PATH_REPLENISHMENT_REQUESTS, true)) {
            writer.append(medicationName).append(",");
            writer.append(requestId).append(",");
            writer.append(status).append("\n");
            System.out.println("| Replenishment request saved successfully!      |");
        } catch (IOException e) {
            System.out.printf("| Error: %-40s |\n", e.getMessage());
        }
        System.out.println("+------------------------------------------------+\n");
    }
    
    /**
     * Checks if a medication exists in the inventory.
     * @param medicationName the name of the medication to check.
     * @return {@code true} if the medication exists, {@code false} otherwise.
     * @throws IOException if the file cannot be read.
     */
    public boolean medicationExists(String medicationName) throws IOException {
        System.out.println("+------------------------------------------------+");
        System.out.println("|          Checking Medication Existence         |");
        System.out.println("+------------------------------------------------+");

        List<MedicationInventory> medications = loadAllMedications();
        for (MedicationInventory medication : medications) {
            if (medication.getMedicationName().equalsIgnoreCase(medicationName)) {
                System.out.println("| Medication exists in inventory.                |");
                System.out.println("+------------------------------------------------+\n");
                return true;
            }
        }
        System.out.println("| Medication does not exist in inventory.        |");
        System.out.println("+------------------------------------------------+\n");
        return false;
    }
}