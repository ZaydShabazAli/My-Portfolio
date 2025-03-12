package entity;

/**
 * Represents a pharmacist in the hospital system. Extends the User class.
 */
public class Pharmacist extends User {
    private String staffemail;
    private String staffcontact;

    /**
     * Constructs a Pharmacist object with the specified attributes.
     *
     * @param userid       The unique identifier for the pharmacist.
     * @param name         The name of the pharmacist.
     * @param Role         The role of the user (fixed as "Pharmacist").
     * @param password     The password of the pharmacist.
     * @param gender       The gender of the pharmacist.
     * @param age          The age of the pharmacist.
     * @param staffemail   The email address of the pharmacist.
     * @param staffcontact The contact number of the pharmacist.
     */
    public Pharmacist(String userid, String name, String Role, String password, String gender, String age, String staffemail, String staffcontact) {
        super(userid, name, "Pharmacist", password, gender, age);
        this.staffemail = staffemail;
        this.staffcontact = staffcontact;
    }

    /**
     * Retrieves the email address of the pharmacist.
     *
     * @return The pharmacist's email address.
     */
    public String getStaffEmail() {
        return staffemail;
    }

    /**
     * Updates the email address of the pharmacist.
     *
     * @param staffemail The new email address of the pharmacist.
     */
    public void setStaffEmail(String staffemail) {
        this.staffemail = staffemail;
    }

    /**
     * Retrieves the contact number of the pharmacist.
     *
     * @return The pharmacist's contact number.
     */
    public String getStaffContact() {
        return staffcontact;
    }

    /**
     * Updates the contact number of the pharmacist.
     *
     * @param staffcontact The new contact number of the pharmacist.
     */
    public void setStaffContact(String staffcontact) {
        this.staffcontact = staffcontact;
    }
}