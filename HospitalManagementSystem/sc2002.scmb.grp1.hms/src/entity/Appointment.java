package entity;

/**
 * Represents an appointment in the system.
 */
public class Appointment {
    private String appointmentId;
    private String patientId;
    private String doctorId;
    private String appointmentDate;
    private String startTime;
    private String endTime;
    private String status;

    /**
     * Constructs an Appointment object with the specified details.
     *
     * @param appointmentId    The unique identifier for the appointment.
     * @param patientId        The unique identifier of the patient.
     * @param doctorId         The unique identifier of the doctor.
     * @param appointmentDate  The date of the appointment.
     * @param startTime        The start time of the appointment.
     * @param endTime          The end time of the appointment.
     * @param status           The status of the appointment (e.g., scheduled, completed, canceled).
     */
    public Appointment(String appointmentId, String patientId, String doctorId,
                       String appointmentDate, String startTime, String endTime, String status) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.appointmentDate = appointmentDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }

    /**
     * Retrieves the appointment ID.
     *
     * @return The appointment ID.
     */
    public String getAppointmentId() {
        return appointmentId;
    }

    /**
     * Updates the appointment ID.
     *
     * @param appointmentId The new appointment ID.
     */
    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    /**
     * Retrieves the patient ID.
     *
     * @return The patient ID.
     */
    public String getPatientId() {
        return patientId;
    }

    /**
     * Updates the patient ID.
     *
     * @param patientId The new patient ID.
     */
    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    /**
     * Retrieves the doctor ID.
     *
     * @return The doctor ID.
     */
    public String getDoctorId() {
        return doctorId;
    }

    /**
     * Updates the doctor ID.
     *
     * @param doctorId The new doctor ID.
     */
    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    /**
     * Retrieves the appointment date.
     *
     * @return The date of the appointment.
     */
    public String getAppointmentDate() {
        return appointmentDate;
    }

    /**
     * Updates the appointment date.
     *
     * @param appointmentDate The new date for the appointment.
     */
    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    /**
     * Retrieves the start time of the appointment.
     *
     * @return The start time of the appointment.
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * Updates the start time of the appointment.
     *
     * @param startTime The new start time of the appointment.
     */
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    /**
     * Retrieves the end time of the appointment.
     *
     * @return The end time of the appointment.
     */
    public String getEndTime() {
        return endTime;
    }

    /**
     * Updates the end time of the appointment.
     *
     * @param endTime The new end time of the appointment.
     */
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    /**
     * Retrieves the status of the appointment.
     *
     * @return The status of the appointment.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Updates the status of the appointment.
     *
     * @param status The new status of the appointment.
     */
    public void setStatus(String status) {
        this.status = status;
    }
}