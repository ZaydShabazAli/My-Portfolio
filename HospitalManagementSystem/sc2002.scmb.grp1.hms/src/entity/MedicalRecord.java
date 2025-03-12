package entity;

/**
 * Represents a medical record associated with a patient and a doctor.
 */
public class MedicalRecord {
    private String recordId;
    private String patientId;
    private String doctorId;
    private String diagnosis;
    private String treatment;
    private String prescription;

    /**
     * Constructs a MedicalRecord object with the specified attributes.
     *
     * @param recordId     The unique identifier for the medical record.
     * @param patientId    The unique identifier for the patient.
     * @param doctorId     The unique identifier for the doctor.
     * @param diagnosis    The diagnosis made by the doctor.
     * @param treatment    The treatment plan for the patient.
     * @param prescription The medication prescribed to the patient.
     */
    public MedicalRecord(String recordId, String patientId, String doctorId, String diagnosis, String treatment, String prescription) {
        this.recordId = recordId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.diagnosis = diagnosis;
        this.treatment = treatment;
        this.prescription = prescription;
    }

    /**
     * Retrieves the record ID.
     *
     * @return The unique identifier for the medical record.
     */
    public String getRecordId() {
        return recordId;
    }

    /**
     * Retrieves the patient ID associated with this medical record.
     *
     * @return The unique identifier for the patient.
     */
    public String getPatientId() {
        return patientId;
    }

    /**
     * Retrieves the doctor ID associated with this medical record.
     *
     * @return The unique identifier for the doctor.
     */
    public String getDoctorId() {
        return doctorId;
    }

    /**
     * Retrieves the diagnosis for the medical record.
     *
     * @return The diagnosis.
     */
    public String getDiagnosis() {
        return diagnosis;
    }

    /**
     * Updates the diagnosis for the medical record.
     *
     * @param diagnosis The new diagnosis.
     */
    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    /**
     * Retrieves the treatment plan for the medical record.
     *
     * @return The treatment plan.
     */
    public String getTreatment() {
        return treatment;
    }

    /**
     * Updates the treatment plan for the medical record.
     *
     * @param treatment The new treatment plan.
     */
    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    /**
     * Retrieves the prescription for the medical record.
     *
     * @return The prescription.
     */
    public String getPrescription() {
        return prescription;
    }

    /**
     * Updates the prescription for the medical record.
     *
     * @param prescription The new prescription.
     */
    public void setPrescription(String prescription) {
        this.prescription = prescription;
    }

    /**
     * Formats the medical record information as a string for display.
     *
     * @return A string representation of the medical record.
     */
    @Override
    public String toString() {
        return "Record ID:    " + recordId + "\n" +
                "Diagnosis:    " + diagnosis + "\n" +
                "Treatment:    " + treatment + "\n" +
                "Prescription: " + prescription;
    }

    /**
     * Formats the medical record information for patient-specific display.
     *
     * @return A formatted string representing the medical record details.
     */
    public String patientMRToString() {
        return String.format("| %-9s | %-10s | %-12s | %-15s |\n",
                recordId,
                diagnosis,
                treatment,
                prescription);
    }
}