package repository;


import controller.ChangeSecurityQuestionInterface;
import controller.PasswordChangerInterface;
import controller.PasswordController;
import controller.ValidationInterface;
import controller.checkHaveQuestionsInterface;
import entity.Patient;
import entity.User;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/**
 * The PatientRepository class handles all operations related to the storage and retrieval
 * of patient data in a CSV file. It provides functionality for authentication, 
 * security question handling, password management, and updating patient information.
 * 
 * Implements interfaces:
 * - ValidationInterface
 * - checkHaveQuestionsInterface
 * - PasswordChangerInterface
 * - ChangeSecurityQuestionInterface
 * 
 * The class ensures that patient data can be effectively managed while adhering to 
 * proper file handling practices.
 */
public class PatientRepository implements ValidationInterface, checkHaveQuestionsInterface, PasswordChangerInterface, ChangeSecurityQuestionInterface{

	private static final String FILE_PATH_PATIENT = "sc2002.scmb.grp1.hms//resource//Patient.csv";

    /**
     * Creates a Patient object from a CSV line split into parts.
     * @param parts Array of strings representing columns of a CSV row.
     * @return A Patient object populated with the provided data.
     */
    private Patient createPatientFromCSV(String[] parts) {
        // Create a Patient using the CSV parts in the exact order of columns
        return new Patient(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], parts[6], parts[7], parts[8], parts[9]);
    }

    /**
     * Validates user credentials by checking the ID and password against the CSV data.
     * @param id The user's hospital ID.
     * @param password The provided password to validate.
     * @return A User object if credentials are valid, or null if invalid.
     */
    public User validateCredentials(String id, String password) {
        PasswordController pc = new PasswordController();
        String df = "Password";
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH_PATIENT))) {
            reader.readLine(); // Skip header
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if(parts[0].equals(id) && parts[3].equals(df) && parts[3].equals(password)){
                    return createPatientFromCSV(parts);
                }
                else if (parts[0].equals(id) && parts[3].equals(pc.hashPassword(password))) { // UserID and Password
                    return createPatientFromCSV(parts);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
	}

    /**
     * Checks if the user with the given hospital ID has set a security question.
     * @param hospitalID The hospital ID of the user.
     * @return true if a security question is set, false otherwise.
     */
    public boolean checkHaveQuestions(String hospitalID) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH_PATIENT))) {
            reader.readLine(); // Skip header
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length > 10 && parts[0].equals(hospitalID) && !parts[10].isEmpty()) { // UserID and Password
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Retrieves the security question for a user based on their hospital ID.
     * @param hospitalID The hospital ID of the user.
     * @return The security question as a String, or "Error" if not found.
     */
    public String returnQuestion(String hospitalID) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH_PATIENT))) {
            reader.readLine(); // Skip header
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(hospitalID) && !parts[10].isEmpty()) { // UserID and Password
                    return parts[10];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Error";
    }

    /**
     * Verifies the user's answer to their security question.
     * @param hospitalID The hospital ID of the user.
     * @param answer The answer provided by the user.
     * @return true if the answer matches, false otherwise.
     */
    public boolean questionVerification(String hospitalID, String answer) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH_PATIENT))) {
            reader.readLine(); // Skip header
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(hospitalID) && parts[11].equals(answer.toLowerCase())) { // Match ID and Answer
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Changes the user's password in the CSV file.
     * @param hospitalID The hospital ID of the user.
     * @param newHashedPassword The new hashed password.
     * @return true if the password was successfully updated, false otherwise.
     */
    public boolean changePassword(String hospitalID, String newHashedPassword) {
        List<String[]> allRecords = new ArrayList<>();
        boolean passwordUpdated = false;
    
        // Load all records from the file
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH_PATIENT))) {
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
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH_PATIENT))) {
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
     * Loads all patient records from the CSV file.
     * @return A list of Patient objects.
     * @throws IOException If an error occurs during file reading.
     */
	public List<Patient> loadPatients() throws IOException {
        List<Patient> patients = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(FILE_PATH_PATIENT));
        String line;
        boolean isFirstLine = true;
        while ((line = br.readLine()) != null) {
            if (isFirstLine){
                isFirstLine = false; //skips the first line
                continue;
            }
            String[] data = line.split(",");
            
            patients.add(createPatientFromCSV(data));
        }
        br.close();
        return patients;
    }

    /**
     * Finds a patient by their hospital ID.
     * @param patientId The hospital ID of the patient.
     * @return A Patient object if found, or null if not found.
     * @throws IOException If an error occurs during file reading.
     */
    public Patient findPatientById(String patientId) throws IOException {
        List<Patient> patients = loadPatients();    
        return patients.stream()
                .filter(patient -> patient.getUserId().equals(patientId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Updates a patient's email and phone number in the CSV file.
     * @param updatedPatient The Patient object containing updated information.
     * @return true if the patient's information was successfully updated, false otherwise.
     * @throws IOException If an error occurs during file reading or writing.
     */
    public boolean updatePatient(Patient updatedPatient) throws IOException {
        List<String[]> allRecords = new ArrayList<>();
        boolean isUpdated = false;
    
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH_PATIENT))) {
            String line;
            boolean firstLine = true;
    
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    allRecords.add(line.split(","));
                    firstLine = false; 
                    continue;
                }
    
                String[] parts = line.split(",");
                if (parts[0].equals(updatedPatient.getUserId())) {
                    parts[7] = updatedPatient.getEmail();  //update email
                    parts[6] = updatedPatient.getPhoneNumber();  //update phone number 
                    isUpdated = true;
                }
                allRecords.add(parts);
            }
        }
    
        if (isUpdated) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH_PATIENT))) {
                for (String[] record : allRecords) {
                    writer.write(String.join(",", record));
                    writer.newLine();
                }
            }
        }
    
        return isUpdated;
    }

    /**
     * Updates the user's security question and answer in the CSV file.
     * @param hospitalID The hospital ID of the user.
     * @param question The new security question.
     * @param answer The new answer for the security question.
     * @return true if the security question was successfully updated, false otherwise.
     */
    public boolean changeSecurityQuestion(String hospitalID, String question, String answer) {
    List<String[]> allRecords = new ArrayList<>();
    boolean questionUpdated = false;

    // Load all records from the file
    try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH_PATIENT))) {
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");

            // Check if the record matches the hospitalID
            if (parts[0].equals(hospitalID)) {
                // Ensure the CSV has enough columns for Question and Answer
                if (parts.length <= 10) {
                    // Add blank placeholders if Question and Answer columns are missing
                    parts = Arrays.copyOf(parts, 12);
                    parts[10] = ""; // Question placeholder
                    parts[11] = ""; // Answer placeholder
                }
                // Update Question and Answer
                parts[10] = question;
                parts[11] = answer;
                questionUpdated = true;
            }
            allRecords.add(parts); // Add the record to the list
        }
    } catch (IOException e) {
        System.err.println("Error reading the file: " + e.getMessage());
        return false; // Indicate failure
    }

    // Rewrite the file with updated records
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH_PATIENT))) {
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
}
