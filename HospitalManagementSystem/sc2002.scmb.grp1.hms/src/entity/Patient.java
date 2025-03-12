package entity;

/**
 * Represents a patient in the hospital system. Extends the User class.
 */
public class Patient extends User {
    private String phoneNumber;
    private String email;
    private String dob;
    private String bloodtype;

    /**
     * Constructs a Patient object with the specified attributes.
     *
     * @param userid      The unique identifier for the patient.
     * @param name        The name of the patient.
     * @param Role        The role of the user (fixed as "Patient").
     * @param password    The password of the patient.
     * @param gender      The gender of the patient.
     * @param age         The age of the patient.
     * @param phoneNumber The phone number of the patient.
     * @param email       The email address of the patient.
     * @param dob         The date of birth of the patient.
     * @param bloodtype   The blood type of the patient.
     */
    public Patient(String userid, String name, String Role, String password, String gender, String age, String phoneNumber, String email, String dob, String bloodtype) {
        super(userid, name, "Patient", password, gender, age);
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.dob = dob;
        this.bloodtype = bloodtype;
    }

    /**
     * Retrieves the phone number of the patient.
     *
     * @return The phone number of the patient.
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Updates the phone number of the patient.
     *
     * @param phoneNumber The new phone number of the patient.
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Retrieves the email address of the patient.
     *
     * @return The email address of the patient.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Updates the email address of the patient.
     *
     * @param address The new email address of the patient.
     */
    public void setEmail(String address) {
        this.email = address;
    }

    /**
     * Retrieves the date of birth of the patient.
     *
     * @return The date of birth of the patient.
     */
    public String getDob() {
        return dob;
    }

    /**
     * Updates the date of birth of the patient.
     *
     * @param dob The new date of birth of the patient.
     */
    public void setDob(String dob) {
        this.dob = dob;
    }

    /**
     * Retrieves the blood type of the patient.
     *
     * @return The blood type of the patient.
     */
    public String getBloodtype() {
        return bloodtype;
    }

    /**
     * Updates the blood type of the patient.
     *
     * @param bloodtype The new blood type of the patient.
     */
    public void setBloodtype(String bloodtype) {
        this.bloodtype = bloodtype;
    }
}