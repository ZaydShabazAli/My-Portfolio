package controller;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * The {@code PasswordController} class provides methods for hashing passwords and 
 * changing passwords for users based on their hospital ID. It supports SHA-256 hashing
 * and allows role-based password management.
 */
public class PasswordController {

    /**
     * Hashes a password using SHA-256 and returns the result as a hexadecimal string.
     *
     * @param password the plain text password to hash
     * @return the hashed password as a hexadecimal string, or {@code null} if an error occurs
     */
    public String hashPassword(String password) {
        try {
            // Create a MessageDigest instance for SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            
            // Convert the password to a byte array and hash it
            byte[] hashBytes = digest.digest(password.getBytes());
            
            // Convert the hashed bytes to a hexadecimal string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b); // Convert each byte to a two-digit hex
                if (hex.length() == 1) {
                    hexString.append('0'); // Pad single-digit hex with a leading 0
                }
                hexString.append(hex);
            }
            
            return hexString.toString(); // Return the hashed password as a hex string
            
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null; // Return null if an error occurs
        }
    }

    /**
     * Changes the password for a specified hospital ID by hashing the new password and 
     * updating it in the corresponding repository.
     *
     * @param hospitalID the ID of the hospital user whose password is to be changed
     * @param newPassword the new plain text password to set
     * @return {@code true} if the password was changed successfully, {@code false} if 
     *         the user ID is invalid or an error occurs
     */
    public boolean changePassword(String hospitalID, String newPassword) {
        String hashedPassword = hashPassword(newPassword);
        String role = extractPrefix(hospitalID);
        RepositoryController repositoryController = new RepositoryController();
        PasswordChangerInterface repository = (PasswordChangerInterface) repositoryController.getRepository(role);

        if (repository == null) {
            System.out.println("Invalid ID or password. Returning to main menu.");
            System.out.println();
            return false;
        }

        return repository.changePassword(hospitalID, hashedPassword);
    }

    /**
     * Extracts the prefix from the hospital ID to determine the user role associated with it.
     * <p>
     * The role is determined based on the prefix of the ID:
     * <ul>
     *     <li>"PH" for Pharmacist</li>
     *     <li>"P" for Patient</li>
     *     <li>"A" for Administrator</li>
     *     <li>"D" for Doctor</li>
     * </ul>
     * </p>
     *
     * @param hospitalId the hospital ID from which to extract the prefix
     * @return a string representing the role prefix or "NULL" if the prefix does not match a known role
     */
    private static String extractPrefix(String hospitalId) {
        if (hospitalId.startsWith("PH")) {
            return "PH";
        } else if (hospitalId.startsWith("P")) {
            return "P";
        } else if (hospitalId.startsWith("A")) {
            return "A";
        } else if (hospitalId.startsWith("D")) {
            return "D";
        }
        return "NULL"; // Return "NULL" if there's no matching prefix
    }
}
