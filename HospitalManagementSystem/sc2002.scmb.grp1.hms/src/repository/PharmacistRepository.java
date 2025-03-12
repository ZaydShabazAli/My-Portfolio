/**
 * Repository class for managing Pharmacist data from a CSV file.
 * This class handles CRUD operations and additional functionalities such as
 * password validation, security question management, and pharmacist lookup.
 */
package repository;

import controller.ChangeSecurityQuestionInterface;
import controller.PasswordChangerInterface;
import controller.PasswordController;
import controller.ValidationInterface;
import controller.checkHaveQuestionsInterface;
import entity.Pharmacist;
import entity.User;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PharmacistRepository implements ValidationInterface, checkHaveQuestionsInterface, PasswordChangerInterface,
        ChangeSecurityQuestionInterface {

    private static final String FILE_PATH_PHARMACISTS = "sc2002.scmb.grp1.hms//resource//Pharmacist.csv";

     /**
     * Creates a Pharmacist object from a CSV line.
     *
     * @param parts An array of strings representing a CSV line split by commas.
     * @return A Pharmacist object created from the CSV data.
     */
    private Pharmacist createPharmacistFromCSV(String[] parts) {
        // Create a Pharmacist using the CSV parts in the exact order of columns
        return new Pharmacist(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], parts[6], parts[7]);
    }

    /**
     * Validates pharmacist credentials against the CSV data.
     *
     * @param id       The pharmacist's ID.
     * @param password The password to validate.
     * @return A User object if credentials are valid, null otherwise.
     */
    public User validateCredentials(String id, String password) {
        PasswordController pc = new PasswordController();
        String df = "Password";
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH_PHARMACISTS))) {
            reader.readLine(); // Skip header
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(id) && parts[3].equals(df) && parts[3].equals(password)) {
                    return createPharmacistFromCSV(parts);
                } else if (parts[0].equals(id) && parts[3].equals(pc.hashPassword(password))) { // UserID and Password
                    return createPharmacistFromCSV(parts);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Checks if a security question exists for a given pharmacist ID.
     *
     * @param hospitalID The ID of the pharmacist.
     * @return True if a security question exists, false otherwise.
     */
    public boolean checkHaveQuestions(String hospitalID) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH_PHARMACISTS))) {
            reader.readLine(); // Skip header
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length > 8 && parts[0].equals(hospitalID) && !parts[8].isEmpty()) { // UserID and Password
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    /**
     * Returns the security question for a given pharmacist ID.
     *
     * @param hospitalID The ID of the pharmacist.
     * @return The security question if found, or "Error" otherwise.
     */
    public String returnQuestion(String hospitalID) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH_PHARMACISTS))) {
            reader.readLine(); // Skip header
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(hospitalID) && !parts[8].isEmpty()) { // UserID and Password
                    return parts[8];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Error";
    }
    /**
     * Verifies the answer to a security question for a given pharmacist ID.
     *
     * @param hospitalID The ID of the pharmacist.
     * @param answer     The answer to validate.
     * @return True if the answer is correct, false otherwise.
     */
    public boolean questionVerification(String hospitalID, String answer) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH_PHARMACISTS))) {
            reader.readLine(); // Skip header
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(hospitalID) && parts[9].equals(answer.toLowerCase())) { // Match ID and Answer
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    /**
     * Changes the password for a given pharmacist ID.
     *
     * @param hospitalID       The ID of the pharmacist.
     * @param newHashedPassword The new hashed password.
     * @return True if the password was updated, false otherwise.
     */
    public boolean changePassword(String hospitalID, String newHashedPassword) {
        List<String[]> allRecords = new ArrayList<>();
        boolean passwordUpdated = false;

        // Load all records from the file
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH_PHARMACISTS))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(hospitalID)) {
                    parts[3] = newHashedPassword; // Update password
                    passwordUpdated = true;
                }
                allRecords.add(parts);
            }
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
            return false; // Indicate failure
        }

        // Rewrite the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH_PHARMACISTS))) {
            for (String[] record : allRecords) {
                writer.write(String.join(",", record));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing to the file: " + e.getMessage());
            return false; // Indicate failure
        }

        return passwordUpdated;
    }
    /**
     * Changes the security question and answer for a given pharmacist ID.
     *
     * @param hospitalID The ID of the pharmacist.
     * @param question   The new security question.
     * @param answer     The new answer to the security question.
     * @return True if the security question was updated, false otherwise.
     */
    public boolean changeSecurityQuestion(String hospitalID, String question, String answer) {
        List<String[]> allRecords = new ArrayList<>();
        boolean questionUpdated = false;

        // Load all records from the file
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH_PHARMACISTS))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");

                // Check if the record matches the hospitalID
                if (parts[0].equals(hospitalID)) {
                    // Ensure the CSV has enough columns for Question and Answer
                    if (parts.length <= 8) {
                        // Add blank placeholders if Question and Answer columns are missing
                        parts = Arrays.copyOf(parts, 10);
                        parts[8] = ""; // Question placeholder
                        parts[9] = ""; // Answer placeholder
                    }
                    // Update Question and Answer
                    parts[8] = question;
                    parts[9] = answer;
                    questionUpdated = true;
                }
                allRecords.add(parts); // Add the record to the list
            }
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
            return false; // Indicate failure
        }

        // Rewrite the file with updated records
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH_PHARMACISTS))) {
            for (String[] record : allRecords) {
                writer.write(String.join(",", record));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing to the file: " + e.getMessage());
            return false; // Indicate failure
        }

        return questionUpdated; // Return true if the question was updated
    }

    /**
     * Loads all pharmacists from the CSV file.
     *
     * @return A list of Pharmacist objects.
     * @throws IOException If an error occurs while reading the file.
     */
    public List<Pharmacist> loadPharmacists() throws IOException {  
        List<Pharmacist> pharmacists = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH_PHARMACISTS))) {
            br.readLine(); // Skip header row
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 8) { // Ensure minimum required fields to avoid errors
                    pharmacists.add(createPharmacistFromCSV(data));
                } else {
                    System.err.println("Skipped invalid line: " + line);
                }
            }
        }
        return pharmacists;
    }
    /**
     * Adds a new pharmacist to the CSV file.
     *
     * @param newPharmacist The Pharmacist object to add.
     * @throws IOException If an error occurs while writing to the file.
     */
    public void writePharmacist(Pharmacist newPharmacist) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH_PHARMACISTS, true))) {
            String csvLine = String.join(",",
                    newPharmacist.getUserId(),
                    newPharmacist.getName(),
                    newPharmacist.getRole(),
                    newPharmacist.getPassword(),
                    newPharmacist.getGender(),
                    newPharmacist.getAge(),
                    newPharmacist.getStaffEmail(),
                    newPharmacist.getStaffContact()
            );
            writer.write(csvLine);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error writing to the file: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Removes a pharmacist by their ID from the CSV file.
     *
     * @param pharmacistID The ID of the pharmacist to remove.
     * @throws IOException If an error occurs while writing to the file.
     */
    public void removePharmacistById(String pharmacistID) throws IOException {
        List<Pharmacist> pharmacists = loadPharmacists(); // Load all pharmacists

        // Remove the pharmacist with the specified ID
        pharmacists.removeIf(pharmacist -> pharmacist.getUserId().equals(pharmacistID));

        // Rewrite the CSV file with the updated list of pharmacists
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH_PHARMACISTS))) {
            writer.write("UserID,Name,Role,Password,Gender,Age,StaffEmail,StaffContact\n"); // Header
            for (Pharmacist pharmacist : pharmacists) {
                String csvLine = String.join(",",
                        pharmacist.getUserId(),
                        pharmacist.getName(),
                        pharmacist.getRole(),
                        pharmacist.getPassword(),
                        pharmacist.getGender(),
                        pharmacist.getAge(),
                        pharmacist.getStaffEmail(),
                        pharmacist.getStaffContact()
                );
                writer.write(csvLine);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing to the file: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Finds a pharmacist by their ID.
     *
     * @param pharmacistId The ID of the pharmacist to find.
     * @return The Pharmacist object if found, null otherwise.
     * @throws IOException If an error occurs while reading the file.
     */
    public Pharmacist findPharmacistById(String pharmacistId) throws IOException {
        List<Pharmacist> pharmacists = loadPharmacists();
        // Search for the pharmacist with the given ID
        return pharmacists.stream()
                .filter(pharmacist -> pharmacist.getUserId().equals(pharmacistId))
                .findFirst()
                .orElse(null); // Return null if no pharmacist is found
    }

    /**
     * Updates a pharmacist's details in the CSV file.
     *
     * @param updatedPharmacist The updated Pharmacist object.
     * @return True if the update was successful, false otherwise.
     * @throws IOException If an error occurs while writing to the file.
     */
    public boolean updatePharmacist(Pharmacist updatedPharmacist) throws IOException {
        List<String[]> allRecords = new ArrayList<>();
        boolean isUpdated = false;
    
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH_PHARMACISTS))) {
            String line;
            boolean firstLine = true;
    
            // Read the CSV file line by line
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    allRecords.add(line.split(","));
                    firstLine = false;
                    continue;
                }
    
                String[] parts = line.split(",");
                // Check if this line corresponds to the pharmacist we want to update
                if (parts[0].equals(updatedPharmacist.getUserId())) {
                    parts[6] = updatedPharmacist.getStaffEmail(); // Update email
                    parts[7] = updatedPharmacist.getStaffContact(); // Update phone number
                    isUpdated = true;
                }
                allRecords.add(parts);
            }
        }
    
        // If we made an update, rewrite the CSV
        if (isUpdated) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH_PHARMACISTS))) {
                for (String[] record : allRecords) {
                    writer.write(String.join(",", record));
                    writer.newLine();
                }
            }
        }
    
        return isUpdated;
    }
    /**
     * Checks if a pharmacist exists by their ID.
     *
     * @param userId The ID of the pharmacist to check.
     * @return True if the pharmacist exists, false otherwise.
     * @throws IOException If an error occurs while reading the file.
     */
    public boolean hasPharmacist(String userId) throws IOException {
        List<Pharmacist> pharmacists = loadPharmacists();
        return pharmacists.stream()
                .anyMatch(pharmacist -> pharmacist.getUserId().equals(userId));
    }
}

