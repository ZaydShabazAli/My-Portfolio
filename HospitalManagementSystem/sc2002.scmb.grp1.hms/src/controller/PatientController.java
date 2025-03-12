package controller;

import entity.Patient;
import repository.PatientRepository;
import java.util.Scanner;
import java.io.IOException;

/**
 * The {@code PatientController} class provides methods to manage and update patient information.
 * It allows retrieving patient details by ID and updating a patient's contact information.
 */
public class PatientController {
    private final PatientRepository patientRepository = new PatientRepository();
    private final Scanner scanner = new Scanner(System.in);

    /**
     * Retrieves a patient by their patient ID.
     *
     * @param patientId the ID of the patient to retrieve
     * @return the {@link Patient} object associated with the specified ID, or {@code null} if not found
     * @throws IOException if an error occurs while accessing the repository
     */
    public Patient getPatientById(String patientId) throws IOException {
        return patientRepository.findPatientById(patientId);
    }

    /**
     * Updates the contact information (email and phone number) for a specified patient.
     * <p>
     * Prompts the user to enter new contact details, allowing them to keep the current 
     * information by leaving the input blank.
     * </p>
     *
     * @param patientId the ID of the patient whose contact information will be updated
     * @throws IOException if an error occurs while accessing the repository
     */
    public void updatePatientInfo(String patientId) throws IOException {
        try {
            Patient patient = getPatientById(patientId);

            if (patient == null) {
                System.out.println("Patient not found!");
                return;
            }

            System.out.println("Current Email: " + patient.getEmail());
            System.out.println("Current Phone Number: " + patient.getPhoneNumber());

            System.out.println("Enter new email (leave blank to keep current email): ");
            String newEmail = scanner.nextLine();
            if (!newEmail.isEmpty()) {
                patient.setEmail(newEmail);
            }

            System.out.println("Enter new phone number (leave blank to keep current number): ");
            String newPhoneNumber = scanner.nextLine();
            if (!newPhoneNumber.isEmpty()) {
                patient.setPhoneNumber(newPhoneNumber);
            }

            boolean success = patientRepository.updatePatient(patient);
            if (success) {
                System.out.println("Contact information updated successfully.");
            } else {
                System.out.println("Failed to update contact information.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
