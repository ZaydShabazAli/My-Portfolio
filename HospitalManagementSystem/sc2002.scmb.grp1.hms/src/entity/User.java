package entity;

/**
 * Abstract class representing a generic user in the system.
 * This class serves as the base class for all user roles in the system.
 */
public abstract class User {
    protected String userid;
    protected String name;
    protected String role;
    protected String password;
    protected String gender;
    protected String age;

    /**
     * Constructs a User object with the specified attributes.
     *
     * @param userid   The unique identifier of the user.
     * @param name     The name of the user.
     * @param role     The role of the user (e.g., Administrator, Pharmacist, Patient).
     * @param password The password of the user.
     * @param gender   The gender of the user.
     * @param age      The age of the user.
     */
    public User(String userid, String name, String role, String password, String gender, String age) {
        this.userid = userid;
        this.name = name;
        this.password = password;
        this.role = role;
        this.gender = gender;
        this.age = age;
    }

    /**
     * Retrieves the user ID.
     *
     * @return The user ID.
     */
    public String getUserId() {
        return userid;
    }

    /**
     * Retrieves the name of the user.
     *
     * @return The user's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the role of the user.
     *
     * @return The user's role.
     */
    public String getRole() {
        return role;
    }

    /**
     * Retrieves the gender of the user.
     *
     * @return The user's gender.
     */
    public String getGender() {
        return gender;
    }

    /**
     * Retrieves the age of the user.
     *
     * @return The user's age.
     */
    public String getAge() {
        return age;
    }

    /**
     * Retrieves the password of the user.
     *
     * @return The user's password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Updates the name of the user.
     *
     * @param name The new name of the user.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Updates the age of the user.
     *
     * @param age The new age of the user.
     */
    public void setAge(String age) {
        this.age = age;
    }

    /**
     * Updates the gender of the user.
     *
     * @param gender The new gender of the user.
     */
    public void setGender(String gender) {
        this.gender = gender;
    }
}