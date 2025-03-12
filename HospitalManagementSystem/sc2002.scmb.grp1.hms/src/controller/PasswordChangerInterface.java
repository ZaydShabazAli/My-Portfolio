package controller;

/**
 * The {@code PasswordChangerInterface} interface defines a method for changing 
 * the password of a user based on their hospital ID.
 * <p>
 * Implementing classes should provide the mechanism to update the password in 
 * the corresponding data repository or storage.
 * </p>
 */
public interface PasswordChangerInterface {

    /**
     * Changes the password for the specified hospital ID.
     *
     * @param hospitalID the ID of the hospital user whose password is to be changed
     * @param hashedPassword the new hashed password to set for the user
     * @return {@code true} if the password was changed successfully, {@code false} otherwise
     */
    public boolean changePassword(String hospitalID, String hashedPassword);
}
