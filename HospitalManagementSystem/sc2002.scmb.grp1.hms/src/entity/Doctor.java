package entity;

/**
 * Represents a doctor in the system. Extends the User class.
 */
public class Doctor extends User {
    private String specialization;
    private String staffcontact;
    private String staffemail;

    /**
     * Constructs a Doctor object with the specified attributes.
     *
     * @param userid         The unique identifier of the doctor.
     * @param name           The name of the doctor.
     * @param role           The role of the user (fixed as "Doctor").
     * @param password       The password of the doctor.
     * @param gender         The gender of the doctor.
     * @param age            The age of the doctor.
     * @param specialization The specialization of the doctor (e.g., "Cardiologist").
     * @param staffemail     The email address of the doctor.
     * @param staffcontact   The contact number of the doctor.
     */
    public Doctor(String userid, String name, String role, String password, String gender, String age, String specialization, String staffemail, String staffcontact) {
        super(userid, name, "Doctor", password, gender, age);
        this.specialization = specialization;
        this.staffemail = staffemail;
        this.staffcontact = staffcontact;
    }

    /**
     * Retrieves the specialization of the doctor.
     *
     * @return The doctor's specialization.
     */
    public String getSpecialization() {
        return specialization;
    }

    /**
     * Updates the specialization of the doctor.
     *
     * @param specialization The new specialization of the doctor.
     */
    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    /**
     * Retrieves the email address of the doctor.
     *
     * @return The doctor's email address.
     */
    public String getStaffEmail() {
        return staffemail;
    }

    /**
     * Updates the email address of the doctor.
     *
     * @param staffemail The new email address of the doctor.
     */
    public void setStaffEmail(String staffemail) {
        this.staffemail = staffemail;
    }

    /**
     * Retrieves the contact number of the doctor.
     *
     * @return The doctor's contact number.
     */
    public String getStaffContact() {
        return staffcontact;
    }

    /**
     * Updates the contact number of the doctor.
     *
     * @param staffcontact The new contact number of the doctor.
     */
    public void setStaffContact(String staffcontact) {
        this.staffcontact = staffcontact;
    }
}