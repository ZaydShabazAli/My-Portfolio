package repository;

import controller.ChangeSecurityQuestionInterface;
import controller.PasswordChangerInterface;
import controller.PasswordController;
import controller.ValidationInterface;
import controller.checkHaveQuestionsInterface;
import entity.Administrator;
import entity.User;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The AdministratorRepository class provides methods to manage administrators
 * in the Hospital Management System, including functionalities for validation,
 * password management, and handling security questions.
 * It interacts with the "Administrator.csv" file for data persistence.
 */
public class AdministratorRepository implements ValidationInterface, checkHaveQuestionsInterface,
        PasswordChangerInterface, ChangeSecurityQuestionInterface {
    private static final String FILE_PATH_ADMINISTRATOR = "sc2002.scmb.grp1.hms//resource//Administrator.csv";

    /**
     * Creates an Administrator object from a CSV line.
     *
     * @param parts Array of strings representing the columns in the CSV line.
     * @return An Administrator object.
     */
    // Create Doctor object from CSV line
    private Administrator createAdministratorFromCSV(String[] parts) {
        // Create a Doctor using the CSV parts in the exact order of columns
        return new Administrator(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], parts[6], parts[7]);
    }

    /**
     * Validates the credentials of an administrator using their ID and password.
     *
     * @param id       The administrator's ID.
     * @param password The administrator's password.
     * @return The validated User object, or null if validation fails.
     */
    // Validate doctor credentials
    public User validateCredentials(String id, String password) {
        PasswordController pc = new PasswordController();
        String df = "Password";
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH_ADMINISTRATOR))) {
            reader.readLine(); // Skip header
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(id) && parts[3].equals(df) && parts[3].equals(password)) {
                    return createAdministratorFromCSV(parts);
                } else if (parts[0].equals(id) && parts[3].equals(pc.hashPassword(password))) { // UserID and Password
                    return createAdministratorFromCSV(parts);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Checks if a security question exists for a given hospital ID.
     *
     * @param hospitalID The hospital ID.
     * @return true if a security question exists, false otherwise.
     */
    public boolean checkHaveQuestions(String hospitalID) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH_ADMINISTRATOR))) {
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
     * Returns the security question for a given hospital ID.
     *
     * @param hospitalID The hospital ID.
     * @return The security question or "Error" if not found.
     */
    public String returnQuestion(String hospitalID) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH_ADMINISTRATOR))) {
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
     * Verifies the answer to a security question for a given hospital ID.
     *
     * @param hospitalID The hospital ID.
     * @param answer     The provided answer.
     * @return true if the answer is correct, false otherwise.
     */
    public boolean questionVerification(String hospitalID, String answer) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH_ADMINISTRATOR))) {
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
     * Changes the password of an administrator.
     *
     * @param hospitalID        The hospital ID.
     * @param newHashedPassword The new hashed password.
     * @return true if the password was updated successfully, false otherwise.
     */
    public boolean changePassword(String hospitalID, String newHashedPassword) {
        List<String[]> allRecords = new ArrayList<>();
        boolean passwordUpdated = false;

        // Load all records from the file
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH_ADMINISTRATOR))) {
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
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH_ADMINISTRATOR))) {
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
     * Changes the security question and answer for a given hospital ID.
     *
     * @param hospitalID The hospital ID.
     * @param question   The new security question.
     * @param answer     The new answer.
     * @return true if updated successfully, false otherwise.
     */
    public boolean changeSecurityQuestion(String hospitalID, String question, String answer) {
        List<String[]> allRecords = new ArrayList<>();
        boolean questionUpdated = false;

        // Load all records from the file
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH_ADMINISTRATOR))) {
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
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH_ADMINISTRATOR))) {
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
     * Loads all administrators from the CSV file.
     *
     * @return A list of Administrator objects.
     * @throws IOException if an error occurs during file reading.
     */
    public List<Administrator> loadAdministrators() throws IOException {
        List<Administrator> administrators = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH_ADMINISTRATOR))) {
            br.readLine(); // Skip header row
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 8) { // Ensure minimum required fields
                    administrators.add(createAdministratorFromCSV(data));
                } else {
                    System.err.println("Skipped invalid line: " + line);
                }
            }
        }
        return administrators;
    }
    
    /**
     * Writes a new administrator to the CSV file.
     *
     * @param newAdmin The Administrator object to be written to the file.
     * @throws IOException if an error occurs while writing to the file.
     */
    public void writeAdmin(Administrator newAdmin) throws IOException
    {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH_ADMINISTRATOR, true))) {
            // Convert the Administrator object to a CSV line
            String csvLine = String.join(",",
                    newAdmin.getUserId(), // Assuming getUserId() returns the administrator's user ID
                    newAdmin.getName(), // Assuming getName() returns the administrator's name
                    newAdmin.getRole(), // Assuming getRole() returns the administrator's role
                    newAdmin.getPassword(), // Assuming getPassword() returns the administrator's password
                    newAdmin.getGender(), // Assuming getGender() returns the administrator's gender
                    newAdmin.getAge(), // Assuming getAge() returns the administrator's age as a String
                    newAdmin.getStaffEmail(), // Assuming getStaffEmail() returns the administrator's email
                    newAdmin.getStaffContact() // Assuming getStaffContact() returns the administrator's contact number
            );

            // Write the new administrator's CSV line to the file and add a newline
            writer.write(csvLine);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error writing to the file: " + e.getMessage());
            throw e; // Re-throw exception to indicate failure
        }
    }

    /**
     * Removes an administrator by their ID.
     *
     * @param adminID The ID of the administrator to be removed.
     * @throws IOException if an error occurs while reading or writing to the file.
     */

    public void removeAdministratorById(String adminID) throws IOException {
        List<Administrator> administrators = loadAdministrators(); // Load all administrators
    
        // Remove the administrator with the specified ID
        administrators.removeIf(admin -> admin.getUserId().equals(adminID));
    
        // Rewrite the CSV file with the updated list of administrators
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH_ADMINISTRATOR))) {
            writer.write("UserID,Name,Role,Password,Gender,Age,StaffEmail,StaffContact,Question,Answer\n"); // Header
            for (Administrator admin : administrators) {
                String csvLine = String.join(",",
                        admin.getUserId(),
                        admin.getName(),
                        admin.getRole(),
                        admin.getPassword(),
                        admin.getGender(),
                        admin.getAge(),
                        admin.getStaffEmail(),
                        admin.getStaffContact()
                );
                writer.write(csvLine);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing to the file: " + e.getMessage());
            throw e; // Rethrow the exception for further handling if necessary
        }
    }
    

    /**
     * Finds an administrator by their UserID.
     *
     * @param adminId The ID of the administrator to find.
     * @return The Administrator object if found, otherwise null.
     * @throws IOException if an error occurs while reading the file.
     */
    // Find an administrator by their UserID
    public Administrator findAdminById(String adminId) throws IOException {
        List<Administrator> administrators = loadAdministrators();
        // Search for the administrator with the given ID
        return administrators.stream()
                .filter(admin -> admin.getUserId().equals(adminId))
                .findFirst()
                .orElse(null); // Return null if no administrator is found
    }

    /**
     * Updates the email and contact information of an existing administrator.
     *
     * @param updatedAdmin The updated Administrator object.
     * @return true if the update was successful, false otherwise.
     * @throws IOException if an error occurs while reading or writing to the file.
     */

    public boolean updateAdministrator(Administrator updatedAdmin) throws IOException {
        List<String[]> allRecords = new ArrayList<>();
        boolean isUpdated = false;
    
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH_ADMINISTRATOR))) {
            String line;
            boolean firstLine = true;
    
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    allRecords.add(line.split(","));
                    firstLine = false;
                    continue;
                }
    
                String[] parts = line.split(",");
                // Check if this line corresponds to the administrator we want to update
                if (parts[0].equals(updatedAdmin.getUserId())) {
                    parts[6] = updatedAdmin.getStaffEmail(); // Update email
                    parts[7] = updatedAdmin.getStaffContact(); // Update phone number
                    isUpdated = true;
                }
                allRecords.add(parts);
            }
        }
    
        if (isUpdated) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH_ADMINISTRATOR))) {
                for (String[] record : allRecords) {
                    writer.write(String.join(",", record));
                    writer.newLine();
                }
            }
        }
    
        return isUpdated;
    }
    
    /**
     * Checks if an administrator exists with the given UserID.
     *
     * @param userId The ID of the administrator to check.
     * @return true if the administrator exists, false otherwise.
     * @throws IOException if an error occurs while reading the file.
     */
    public boolean hasAdministrator(String userId) throws IOException {
        List<Administrator> administrators = loadAdministrators();
        return administrators.stream()
                .anyMatch(admin -> admin.getUserId().equals(userId));
    }

}
