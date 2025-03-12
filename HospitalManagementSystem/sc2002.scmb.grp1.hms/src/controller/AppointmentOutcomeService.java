package controller;


import java.io.IOException;

/**
 * Interface for managing appointment outcomes.
 * Defines the contract for operations related to appointment outcomes, 
 * including creation, retrieval, and updating of appointment outcomes.
 */
public interface AppointmentOutcomeService {
/**
     * Creates a new appointment outcome for a specific appointment.
     *
     * @param appointmentId the ID of the appointment for which the outcome is being created.
     * @throws IOException if an I/O error occurs during the operation.
     */
    void createAppointmentOutcome(String appointmentId) throws IOException;

/**
     * Retrieves and displays all appointment outcomes.
     *
     * @throws IOException if an I/O error occurs during the operation.
     */
    void viewAppointmentOutcomes() throws IOException;

/**
     * Retrieves and displays all appointment outcomes for a specific patient.
     *
     * @param patientId the ID of the patient whose appointment outcomes are to be retrieved.
     * @throws IOException if an I/O error occurs during the operation.
     */
    void getAllAppointmentOutcomesForPatient(String patientId) throws IOException;

/**
     * Displays all appointment outcomes with a "Pending" medication status.
     *
     * @throws IOException if an I/O error occurs during the operation.
     */
    void displayPendingAppointmentOutcomes() throws IOException;

 /**
     * Updates the prescription status of a specific appointment outcome to "Dispensed".
     *
     * @param outcomeId the ID of the appointment outcome to update.
     * @throws IOException if an I/O error occurs during the operation.
     */
    void changePrescriptionStatusToDispensed(String outcomeId) throws IOException;
 /**
     * Validates a medication name against the medication inventory.
     *
     * @param medicationName the name of the medication to validate.
     * @return the exact medication name if it exists in the inventory, or {@code null} otherwise.
     * @throws IOException if an I/O error occurs during the operation.
     */
    String isValidMedication(String medicationName) throws IOException;

    /**
     * Generates the next appointment outcome ID in sequence.
     *
     * @return the generated appointment outcome ID.
     * @throws IOException if an I/O error occurs during the operation.
     */
    String generateNextAppointmentOutcomeId() throws IOException;
}
