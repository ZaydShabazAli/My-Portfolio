package entity;

/**
 * Represents the availability of a doctor on a specific date and time.
 */
public class Availability {
    private String availabilityId;
    private String doctorId;
    private String date;
    private String startTime;
    private String endTime;

    /**
     * Constructs an Availability object with the specified details.
     *
     * @param availabilityId The unique identifier for the availability.
     * @param doctorId       The unique identifier for the doctor.
     * @param date           The date of availability.
     * @param startTime      The start time of availability.
     * @param endTime        The end time of availability.
     */
    public Availability(String availabilityId, String doctorId, String date, String startTime, String endTime) {
        this.availabilityId = availabilityId;
        this.doctorId = doctorId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * Retrieves the availability ID.
     *
     * @return The unique identifier for the availability.
     */
    public String getAvailabilityId() {
        return availabilityId;
    }

    /**
     * Updates the availability ID.
     *
     * @param availabilityId The new availability ID.
     */
    public void setAvailabilityId(String availabilityId) {
        this.availabilityId = availabilityId;
    }

    /**
     * Retrieves the doctor ID associated with this availability.
     *
     * @return The unique identifier for the doctor.
     */
    public String getDoctorId() {
        return doctorId;
    }

    /**
     * Updates the doctor ID associated with this availability.
     *
     * @param doctorId The new doctor ID.
     */
    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    /**
     * Retrieves the date of the availability.
     *
     * @return The date of the availability.
     */
    public String getDate() {
        return date;
    }

    /**
     * Updates the date of the availability.
     *
     * @param date The new date of availability.
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Retrieves the start time of the availability.
     *
     * @return The start time of the availability.
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * Updates the start time of the availability.
     *
     * @param startTime The new start time of availability.
     */
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    /**
     * Retrieves the end time of the availability.
     *
     * @return The end time of the availability.
     */
    public String getEndTime() {
        return endTime;
    }

    /**
     * Updates the end time of the availability.
     *
     * @param endTime The new end time of availability.
     */
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}