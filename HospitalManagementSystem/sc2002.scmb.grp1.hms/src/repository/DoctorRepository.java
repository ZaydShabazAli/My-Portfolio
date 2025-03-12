package repository;

import entity.Doctor;
import entity.User;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import controller.PasswordChangerInterface;
import controller.PasswordController;
import controller.ValidationInterface;
import controller.checkHaveQuestionsInterface;
import controller.ChangeSecurityQuestionInterface;

/**
 * Repository class responsible for handling CRUD operations on Doctor data
 * stored in a CSV file.
 * This class implements several interfaces to support validation, password
 * management,
 * and security questions for doctors.
 */
public class DoctorRepository implements ValidationInterface, checkHaveQuestionsInterface, PasswordChangerInterface,
        ChangeSecurityQuestionInterface {

    private static final String FILE_PATH_DOCTORS = "sc2002.scmb.grp1.hms//resource//Doctor.csv";

    /**
     * Creates a Doctor object from a CSV line.
     *
     * @param parts The array of strings representing the fields from a CSV line.
     * @return A Doctor object.
     */
    // Create Doctor object from CSV line
    private Doctor createDoctorFromCSV(String[] parts) {
        // Create a Doctor using the CSV parts in the exact order of columns
        return new Doctor(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], parts[6], parts[7], parts[8]);
    }

    /**
     * Validates doctor credentials using the given ID and password.
     *
     * @param id       The doctor's ID.
     * @param password The doctor's password.
     * @return The authenticated User object, or null if validation fails.
     */
    // Validate doctor credentials
    public User validateCredentials(String id, String password) {
        PasswordController pc = new PasswordController();
        String df = "Password";
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH_DOCTORS))) {
            reader.readLine(); // Skip header
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(id) && parts[3].equals(df) && parts[3].equals(password)) {
                    return createDoctorFromCSV(parts);
                } else if (parts[0].equals(id) && parts[3].equals(pc.hashPassword(password))) { // UserID and Password
                    return createDoctorFromCSV(parts);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Loads all doctors from the CSV file.
     *
     * @return A list of Doctor objects.
     * @throws IOException if an error occurs while reading the file.
     */
    public List<Doctor> loadDoctors() throws IOException {
        List<Doctor> doctors = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH_DOCTORS))) {
            br.readLine(); // Skip header row
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 9) { // Ensure minimum required fields to avoid errors
                    doctors.add(createDoctorFromCSV(data));
                } else {
                    System.err.println("Skipped invalid line: " + line);
                }
            }
        }
        return doctors;
    }

    /**
     * Finds a doctor by their ID.
     *
     * @param doctorId The doctor's ID.
     * @return The Doctor object or null if not found.
     * @throws IOException if an error occurs while reading the file.
     */
    // Find a doctor by their DoctorID
    public Doctor findDoctorById(String doctorId) throws IOException {
        List<Doctor> doctors = loadDoctors();
        // Search for the doctor with the given ID
        return doctors.stream()
                .filter(doctor -> doctor.getUserId().equals(doctorId))
                .findFirst()
                .orElse(null); // Return null if no doctor is found
    }

    /**
     * Checks if a doctor has set security questions.
     *
     * @param hospitalID The hospital ID.
     * @return True if questions are set, otherwise false.
     */

    public boolean checkHaveQuestions(String hospitalID) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH_DOCTORS))) {
            reader.readLine(); // Skip header
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length > 9 && parts[0].equals(hospitalID) && !parts[9].isEmpty()) { // UserID and Password
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Returns the security question for a given doctor.
     *
     * @param hospitalID The hospital ID.
     * @return The security question or "Error" if not found.
     */
    public String returnQuestion(String hospitalID) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH_DOCTORS))) {
            reader.readLine(); // Skip header
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(hospitalID) && !parts[9].isEmpty()) { // UserID and Password
                    return parts[9];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Error";
    }

    /**
     * Verifies the answer to the security question.
     *
     * @param hospitalID The hospital ID.
     * @param answer     The provided answer.
     * @return True if the answer matches, otherwise false.
     */
    public boolean questionVerification(String hospitalID, String answer) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH_DOCTORS))) {
            reader.readLine(); // Skip header
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(hospitalID) && parts[10].equals(answer.toLowerCase())) { // Match ID and Answer
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Updates the password for a doctor.
     *
     * @param hospitalID        The doctor's hospital ID.
     * @param newHashedPassword The new hashed password.
     * @return True if successful, otherwise false.
     */
    public boolean changePassword(String hospitalID, String newHashedPassword) {
        List<String[]> allRecords = new ArrayList<>();
        boolean passwordUpdated = false;

        // Load all records from the file
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH_DOCTORS))) {
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
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH_DOCTORS))) {
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
     * Changes the security question and answer for a doctor.
     *
     * @param hospitalID The hospital ID of the doctor.
     * @param question   The new security question.
     * @param answer     The answer to the security question.
     * @return True if the security question was successfully updated, otherwise
     *         false.
     */
    public boolean changeSecurityQuestion(String hospitalID, String question, String answer) {
        List<String[]> allRecords = new ArrayList<>();
        boolean questionUpdated = false;

        // Load all records from the file
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH_DOCTORS))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");

                // Check if the record matches the hospitalID
                if (parts[0].equals(hospitalID)) {
                    // Ensure the CSV has enough columns for Question and Answer
                    if (parts.length <= 9) {
                        // Add blank placeholders if Question and Answer columns are missing
                        parts = Arrays.copyOf(parts, 11);
                        parts[9] = ""; // Question placeholder
                        parts[10] = ""; // Answer placeholder
                    }
                    // Update Question and Answer
                    parts[9] = question;
                    parts[10] = answer;
                    questionUpdated = true;
                }
                allRecords.add(parts); // Add the record to the list
            }
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
            return false; // Indicate failure
        }

        // Rewrite the file with updated records
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH_DOCTORS))) {
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
     * Writes a new doctor to the CSV file.
     *
     * @param newDoctor The Doctor object to add.
     * @throws IOException if an error occurs while writing.
     */
    public void writeDoctor(Doctor newDoctor) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH_DOCTORS, true))) {
            String csvLine = String.join(",",
                    newDoctor.getUserId(),
                    newDoctor.getName(),
                    newDoctor.getRole(),
                    newDoctor.getPassword(),
                    newDoctor.getGender(),
                    newDoctor.getAge(),
                    newDoctor.getSpecialization(),
                    newDoctor.getStaffEmail(),
                    newDoctor.getStaffContact());
            writer.write(csvLine);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error writing to the file: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Removes a doctor by their ID.
     *
     * @param doctorID The doctor's ID.
     * @throws IOException if an error occurs while updating the file.
     */
    public void removeDoctorById(String doctorID) throws IOException {
        List<Doctor> doctors = loadDoctors(); // Load all doctors

        // Remove the doctor with the specified ID
        doctors.removeIf(doctor -> doctor.getUserId().equals(doctorID));

        // Rewrite the CSV file with the updated list of doctors
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH_DOCTORS))) {
            writer.write("UserID,Name,Role,Password,Gender,Age,Specialization,StaffEmail,StaffContact\n"); // Header
            for (Doctor doctor : doctors) {
                String csvLine = String.join(",",
                        doctor.getUserId(),
                        doctor.getName(),
                        doctor.getRole(),
                        doctor.getPassword(),
                        doctor.getGender(),
                        doctor.getAge(),
                        doctor.getSpecialization(),
                        doctor.getStaffEmail(),
                        doctor.getStaffContact());
                writer.write(csvLine);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing to the file: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Updates an existing doctor's contact information in the CSV file.
     *
     * @param updatedDoctor The Doctor object containing the updated information.
     * @return True if the doctor was successfully updated, otherwise false.
     * @throws IOException if an error occurs while reading or writing the file.
     */
    public boolean updateDoctor(Doctor updatedDoctor) throws IOException {
        List<String[]> allRecords = new ArrayList<>();
        boolean isUpdated = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH_DOCTORS))) {
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
                // Check if this line corresponds to the doctor we want to update
                if (parts[0].equals(updatedDoctor.getUserId())) {
                    parts[7] = updatedDoctor.getStaffEmail(); // Update email
                    parts[8] = updatedDoctor.getStaffContact(); // Update phone number
                    isUpdated = true;
                }
                allRecords.add(parts);
            }
        }

        // If we made an update, rewrite the CSV
        if (isUpdated) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH_DOCTORS))) {
                for (String[] record : allRecords) {
                    writer.write(String.join(",", record));
                    writer.newLine();
                }
            }
        }

        return isUpdated;
    }

    /**
     * Checks if a doctor exists by their User ID.
     *
     * @param userId The User ID to check.
     * @return True if the doctor exists, otherwise false.
     * @throws IOException if an error occurs while reading the file.
     */
    public boolean hasDoctor(String userId) throws IOException {
        List<Doctor> doctors = loadDoctors();
        return doctors.stream()
                .anyMatch(doctor -> doctor.getUserId().equals(userId));
    }

}
