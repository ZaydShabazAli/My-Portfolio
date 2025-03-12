package controller;

import entity.User;

/**
 * The {@code ValidationInterface} interface defines a method for validating user credentials.
 * <p>
 * Implementing classes should provide a mechanism for validating a user's ID and password,
 * typically to allow authentication of users within the system.
 * </p>
 */
public interface ValidationInterface {

    /**
     * Validates the credentials of a user based on their ID and password.
     *
     * @param id       the user ID to validate
     * @param password the password associated with the user ID
     * @return a {@link User} object if the credentials are valid; 
     *         {@code null} if the credentials are invalid
     */
    public User validateCredentials(String id, String password);
}
