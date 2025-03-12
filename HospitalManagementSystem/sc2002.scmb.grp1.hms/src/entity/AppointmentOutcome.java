package entity;

/**
 * Represents the outcome of an appointment in the system.
 */
public class AppointmentOutcome {
    private String outcomeId;
    private String appointmentId;
    private String date;
    private String serviceType;
    private String prescribedMedication;
    private String medicationStatus; // e.g., "Pending", "Dispensed"
    private String consultationNotes;

    /**
     * Constructs an AppointmentOutcome object with the specified details.
     *
     * @param outcomeId            The unique identifier for the outcome.
     * @param appointmentId        The unique identifier for the related appointment.
     * @param date                 The date of the outcome.
     * @param serviceType          The type of service provided during the appointment (e.g., "Consultation", "Treatment").
     * @param prescribedMedication The medication prescribed during the appointment.
     * @param medicationStatus     The status of the prescribed medication (e.g., "Pending", "Dispensed").
     * @param consultationNotes    Additional notes from the consultation or appointment.
     */
    public AppointmentOutcome(String outcomeId, String appointmentId, String date, String serviceType, String prescribedMedication, String medicationStatus, String consultationNotes) {
        this.outcomeId = outcomeId;
        this.appointmentId = appointmentId;
        this.date = date;
        this.serviceType = serviceType;
        this.prescribedMedication = prescribedMedication;
        this.medicationStatus = medicationStatus;
        this.consultationNotes = consultationNotes;
    }

    /**
     * Retrieves the outcome ID.
     *
     * @return The unique identifier for the outcome.
     */
    public String getOutcomeId() {
        return outcomeId;
    }

    /**
     * Retrieves the appointment ID associated with this outcome.
     *
     * @return The unique identifier for the related appointment.
     */
    public String getAppointmentId() {
        return appointmentId;
    }

    /**
     * Retrieves the date of the outcome.
     *
     * @return The date of the outcome.
     */
    public String getDate() {
        return date;
    }

    /**
     * Retrieves the type of service provided during the appointment.
     *
     * @return The service type (e.g., "Consultation", "Treatment").
     */
    public String getServiceType() {
        return serviceType;
    }

    /**
     * Retrieves the prescribed medication.
     *
     * @return The medication prescribed during the appointment.
     */
    public String getPrescribedMedication() {
        return prescribedMedication;
    }

    /**
     * Retrieves the status of the prescribed medication.
     *
     * @return The status of the medication (e.g., "Pending", "Dispensed").
     */
    public String getMedicationStatus() {
        return medicationStatus;
    }

    /**
     * Updates the status of the prescribed medication.
     *
     * @param medicationStatus The new status of the medication.
     */
    public void setMedicationStatus(String medicationStatus) {
        this.medicationStatus = medicationStatus;
    }

    /**
     * Retrieves the consultation notes.
     *
     * @return The notes from the consultation or appointment.
     */
    public String getConsultationNotes() {
        return consultationNotes;
    }

    /**
     * Updates the consultation notes.
     *
     * @param consultationNotes The new notes for the consultation.
     */
    public void setConsultationNotes(String consultationNotes) {
        this.consultationNotes = consultationNotes;
    }
}