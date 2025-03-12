package repository;

import entity.ReplenishmentRequests;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
/**
 * Repository class for managing replenishment requests.
 * Provides functionality to save, load, and update replenishment requests stored in a CSV file.
 */
public class ReplenishmentRequestRepository {
    private static final String FILE_PATH_REPLENISHMENT_REQUESTS = "sc2002.scmb.grp1.hms/resource/ReplenishmentRequests.csv";
    private static int nextRequestId = -1; // Uninitialized marker
    /**
     * Initializes the request ID by reading the highest existing ID from the CSV file.
     * If the file does not exist or is empty, starts the ID at 1.
     *
     * @throws IOException if an error occurs while reading the file.
     */
    private void initializeRequestId() throws IOException {
        int highestId = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH_REPLENISHMENT_REQUESTS))) {
            String line;
            reader.readLine(); // Skip the header row if present

            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length > 0) {
                    try {
                        int currentId = Integer.parseInt(fields[0]); // Read the first field as ID
                        highestId = Math.max(highestId, currentId);
                    } catch (NumberFormatException e) {
                        // Ignore invalid IDs
                    }
                }
            }
        } catch (FileNotFoundException e) {
            // File not found means this is the first request
        }

        nextRequestId = highestId + 1; // Start with the next ID
    }

    // Save a replenishment request to the CSV file
    /**
     * Saves a new replenishment request to the CSV file.
     *
     * @param medicationName the name of the medication to replenish.
     * @param quantity the quantity of the medication.
     * @throws IOException if an error occurs while saving the request.
     */
    public void saveReplenishmentRequest(String medicationName, int quantity) throws IOException {
        if (nextRequestId == -1) { // Initialize only if not already initialized
            initializeRequestId();
        }

        String status = "Pending";

        try (FileWriter writer = new FileWriter(FILE_PATH_REPLENISHMENT_REQUESTS, true)) {
            // Convert int `nextRequestId` to String when writing to the file
            writer.append(String.valueOf(nextRequestId)).append(",") // Use String.valueOf for conversion
                    .append(medicationName).append(",")
                    .append(String.valueOf(quantity)).append(",")
                    .append(status).append("\n");

            System.out.println("+------------------------------------------------+");
            System.out.printf("| Replenishment request ID %-20d saved. |\n", nextRequestId);
            System.out.println("+------------------------------------------------+\n");

            nextRequestId++; // Increment the static counter after saving
        } catch (IOException e) {
            throw new IOException("Error saving replenishment request: " + e.getMessage(), e);
        }
    }

    // Load all replenishment requests from the CSV file
    /**
     * Loads all replenishment requests from the CSV file.
     *
     * @return a list of all replenishment requests.
     * @throws IOException if an error occurs while reading the file.
     */
    public List<ReplenishmentRequests> loadAllRequests() throws IOException {
        List<ReplenishmentRequests> requests = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH_REPLENISHMENT_REQUESTS))) {
            String line;
            reader.readLine(); // Skip the header row if there is one

            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");

                if (fields.length == 4) { // Ensure all fields are present
                    try {
                        int requestId = Integer.parseInt(fields[0]); // Parse ID as int
                        String medicationName = fields[1];
                        int quantity = Integer.parseInt(fields[2]); // Parse quantity as int
                        String status = fields[3].trim();

                        requests.add(new ReplenishmentRequests(requestId, medicationName, quantity, status));
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing row: " + line);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            // If the file does not exist, return an empty list
            System.err.println("Replenishment requests file not found. Returning an empty list.");
        } catch (IOException e) {
            throw new IOException("Error reading replenishment requests: " + e.getMessage(), e);
        }

        return requests;
    }

    /**
     * Retrieves all pending replenishment requests.
     *
     * @return a list of pending replenishment requests.
     * @throws IOException if an error occurs while loading the requests.
     */
    public List<ReplenishmentRequests> pendingRequests() throws IOException {
        List<ReplenishmentRequests> allRequests = loadAllRequests();
        List<ReplenishmentRequests> pending = new ArrayList<>();

        for (ReplenishmentRequests request : allRequests) {
            if ("Pending".equalsIgnoreCase(request.getStatus())) {
                pending.add(request);
            }
        }

        return pending;
    }
    /**
     * Retrieves a specific replenishment request by its ID.
     *
     * @param requestId the ID of the request to retrieve.
     * @return the replenishment request with the given ID, or null if not found.
     * @throws IOException if an error occurs while reading the file.
     */
    public ReplenishmentRequests getRequestById(int requestId) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH_REPLENISHMENT_REQUESTS))) {
            String line;
            reader.readLine(); // Skip the header row if there is one

            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");

                if (fields.length == 4) { // Ensure all fields are present
                    try {
                        int currentRequestId = Integer.parseInt(fields[0]); // Parse ID as int
                        if (currentRequestId == requestId) {
                            String medicationName = fields[1];
                            int quantity = Integer.parseInt(fields[2]); // Parse quantity as int
                            String status = fields[3].trim();

                            // Return the matching request
                            return new ReplenishmentRequests(currentRequestId, medicationName, quantity, status);
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing row: " + line);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Replenishment requests file not found.");
            return null; // Return null if the file does not exist
        } catch (IOException e) {
            throw new IOException("Error reading replenishment requests: " + e.getMessage(), e);
        }

        // Return null if no matching request was found
        return null;
    }
    /**
     * Updates the status of a replenishment request by its ID.
     *
     * @param requestId the ID of the request to update.
     * @param newStatus the new status to set.
     * @throws IOException if an error occurs while updating the request.
     */
    public void updateRequestStatus(int requestId, String newStatus) throws IOException {
        // Load all requests from the CSV file
        List<ReplenishmentRequests> allRequests = loadAllRequests();
    
        // Flag to check if a request with the given ID was found
        boolean found = false;
    
        // Find the request by requestId and update its status
        for (ReplenishmentRequests request : allRequests) {
            if (request.getRequestId() == requestId) {
                request.setStatus(newStatus); // Update the status using a setter method
                found = true;
                break; // Exit loop once the correct request is found and updated
            }
        }
    
        // If no matching request was found, log and exit the method
        if (!found) {
            System.err.println("Request with ID " + requestId + " not found. Status not updated.");
            return;
        }
    
        // Rewrite the entire list of requests back to the CSV file, including the updated status
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH_REPLENISHMENT_REQUESTS))) {
            // Write the header row if necessary
            writer.write("RequestId,MedicationName,Quantity,Status\n");
    
            // Write each request back to the file
            for (ReplenishmentRequests request : allRequests) {
                writer.write(request.getRequestId() + "," +
                             request.getMedicationName() + "," +
                             request.getQuantity() + "," +
                             request.getStatus() + "\n");
            }
        } catch (IOException e) {
            throw new IOException("Error updating request status: " + e.getMessage(), e);
        }
    
        System.out.println("Request ID " + requestId + " status updated to " + newStatus);
    }
    
}